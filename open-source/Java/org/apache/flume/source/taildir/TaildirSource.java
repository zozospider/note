/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.flume.source.taildir;

import static org.apache.flume.source.taildir.TaildirSourceConfigurationConstants.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.BatchSizeSupported;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.AbstractSource;
import org.apache.flume.source.PollableSourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;

public class TaildirSource extends AbstractSource implements
    PollableSource, Configurable, BatchSizeSupported {

  private static final Logger logger = LoggerFactory.getLogger(TaildirSource.class);

  // 监控的文件路径列表
  private Map<String, String> filePaths;
  private Table<String, String, String> headerTable;
  // 往 Channel 中发送 Event 的批量大小
  private int batchSize;
  // 记录所有监控文件 pos 偏移量的文件路径
  private String positionFilePath;
  // 每次程序启动, 对文件进行读取时, 是否从文件尾部开始读取数据, 或者从文件最开始读取
  private boolean skipToEnd;
  private boolean byteOffsetHeader;

  private SourceCounter sourceCounter;
  private ReliableTaildirEventReader reader;
  // 用于监控日志文件的线程池
  private ScheduledExecutorService idleFileChecker;
  // 用于记录日志文件读取的偏移量的线程池
  private ScheduledExecutorService positionWriter;

  private int retryInterval = 1000;
  private int maxRetryInterval = 5000;
  // 日志文件在 idleTimeout 间隔时间没有被修改, 文件将被关闭
  private int idleTimeout;
  private int checkIdleInterval = 5000;
  private int writePosInitDelay = 5000;
  // TaildirSource 读取每个监控文件都在位置文件中记录监控文件的已经读取的偏移量, writePosInterval 则是定义了更新位置文件的间隔
  private int writePosInterval;
  private boolean cachePatternMatching;

  // 用于存放每次 process 获取所有 Taildir 匹配器匹配的文件列表对应的 inode 列表
  private List<Long> existingInodes = new CopyOnWriteArrayList<Long>();
  // 空闲 inodes 列表
  private List<Long> idleInodes = new CopyOnWriteArrayList<Long>();
  private Long backoffSleepIncrement;
  private Long maxBackOffSleepInterval;
  private boolean fileHeader;
  private String fileHeaderKey;
  // 单个文件处理的最大次数, 即文件行数不能超过 maxBatchCount * batchSize
  private Long maxBatchCount;

  /**
   * Source 启动时调用
   */
  @Override
  public synchronized void start() {
    // r1 TaildirSource source starting with directory: {f1=/var/log/test1/example.log, f2=/var/log/test2/.*log.*}
    logger.info("{} TaildirSource source starting with directory: {}", getName(), filePaths);
    try {
      // 通过建造者模式构建 reader 对象, 参数为 configure(x) 方法初始化的变量
      reader = new ReliableTaildirEventReader.Builder()
          .filePaths(filePaths)
          .headerTable(headerTable)
          .positionFilePath(positionFilePath)
          .skipToEnd(skipToEnd)
          .addByteOffset(byteOffsetHeader)
          .cachePatternMatching(cachePatternMatching)
          .annotateFileName(fileHeader)
          .fileNameHeader(fileHeaderKey)
          .build();
    } catch (IOException e) {
      throw new FlumeException("Error instantiating ReliableTaildirEventReader", e);
    }
    // 创建线程池 idleFileChecker, 用于监控日志文件
    idleFileChecker = Executors.newSingleThreadScheduledExecutor(
        new ThreadFactoryBuilder().setNameFormat("idleFileChecker").build());
    // 启动运行 idleFileCheckerRunnable
    idleFileChecker.scheduleWithFixedDelay(new idleFileCheckerRunnable(),
        idleTimeout, checkIdleInterval, TimeUnit.MILLISECONDS);

    // 创建线程池 positionWriter, 用于记录日志文件读取的偏移量
    positionWriter = Executors.newSingleThreadScheduledExecutor(
        new ThreadFactoryBuilder().setNameFormat("positionWriter").build());
    // 启动运行 PositionWriterRunnable
    positionWriter.scheduleWithFixedDelay(new PositionWriterRunnable(),
        writePosInitDelay, writePosInterval, TimeUnit.MILLISECONDS);

    // 调用父类 AbstractSource 的启动逻辑
    super.start();
    logger.debug("TaildirSource started");
    // 启动 sourceCounter
    sourceCounter.start();
  }

  @Override
  public synchronized void stop() {
    try {
      super.stop();
      ExecutorService[] services = {idleFileChecker, positionWriter};
      for (ExecutorService service : services) {
        service.shutdown();
        if (!service.awaitTermination(1, TimeUnit.SECONDS)) {
          service.shutdownNow();
        }
      }
      // write the last position
      writePosition();
      reader.close();
    } catch (InterruptedException e) {
      logger.info("Interrupted while awaiting termination", e);
    } catch (IOException e) {
      logger.info("Failed: " + e.getMessage(), e);
    }
    sourceCounter.stop();
    logger.info("Taildir source {} stopped. Metrics: {}", getName(), sourceCounter);
  }

  @Override
  public String toString() {
    return String.format("Taildir source: { positionFile: %s, skipToEnd: %s, "
        + "byteOffsetHeader: %s, idleTimeout: %s, writePosInterval: %s }",
        positionFilePath, skipToEnd, byteOffsetHeader, idleTimeout, writePosInterval);
  }

  /**
   * 获取配置, 初始化参数
   */
  @Override
  public synchronized void configure(Context context) {
    // a1.sources.r1.filegroups = f1 f2
    String fileGroups = context.getString(FILE_GROUPS);
    // 为空则抛异常
    Preconditions.checkState(fileGroups != null, "Missing param: " + FILE_GROUPS);

    // 文件路径列表: {f1 = /var/log/test1/example.log, f2 = /var/log/test2/.*log.*}
    filePaths = selectByKeys(context.getSubProperties(FILE_GROUPS_PREFIX),
                             fileGroups.split("\\s+"));
    // 为空则抛异常
    Preconditions.checkState(!filePaths.isEmpty(),
        "Mapping for tailing files is empty or invalid: '" + FILE_GROUPS_PREFIX + "'");

    // /user/zozo
    String homePath = System.getProperty("user.home").replace('\\', '/');
    // positionFile 路径默认为 /user/zozo/.flume/taildir_position.json
    positionFilePath = context.getString(POSITION_FILE, homePath + DEFAULT_POSITION_FILE);
    // 创建 positionFile 父文件夹
    Path positionFile = Paths.get(positionFilePath);
    try {
      Files.createDirectories(positionFile.getParent());
    } catch (IOException e) {
      throw new FlumeException("Error creating positionFile parent directories", e);
    }
    // {f1.headerKey1 = value1, f2.headerKey1 = value2, f2.headerKey2 = value2-2}
    headerTable = getTable(context, HEADERS_PREFIX);
    // batchSize 默认 100
    batchSize = context.getInteger(BATCH_SIZE, DEFAULT_BATCH_SIZE);
    // skipToEnd 默认 false
    skipToEnd = context.getBoolean(SKIP_TO_END, DEFAULT_SKIP_TO_END);
    // byteOffsetHeader 默认 false
    byteOffsetHeader = context.getBoolean(BYTE_OFFSET_HEADER, DEFAULT_BYTE_OFFSET_HEADER);
    // idleTimeout 默认 120000
    idleTimeout = context.getInteger(IDLE_TIMEOUT, DEFAULT_IDLE_TIMEOUT);
    // writePosInterval 默认 3000
    writePosInterval = context.getInteger(WRITE_POS_INTERVAL, DEFAULT_WRITE_POS_INTERVAL);
    // cachePatternMatching 默认 true
    cachePatternMatching = context.getBoolean(CACHE_PATTERN_MATCHING,
        DEFAULT_CACHE_PATTERN_MATCHING);
    // backoffSleepIncrement 默认 1000
    backoffSleepIncrement = context.getLong(PollableSourceConstants.BACKOFF_SLEEP_INCREMENT,
        PollableSourceConstants.DEFAULT_BACKOFF_SLEEP_INCREMENT);
    // maxBackoffSleep 默认 5000
    maxBackOffSleepInterval = context.getLong(PollableSourceConstants.MAX_BACKOFF_SLEEP,
        PollableSourceConstants.DEFAULT_MAX_BACKOFF_SLEEP);
    // fileHeader 默认 false
    fileHeader = context.getBoolean(FILENAME_HEADER,
            DEFAULT_FILE_HEADER);
    // fileHeaderKey 默认 file
    fileHeaderKey = context.getString(FILENAME_HEADER_KEY,
            DEFAULT_FILENAME_HEADER_KEY);
    // maxBatchCount 默认 Long.MAX_VALUE
    maxBatchCount = context.getLong(MAX_BATCH_COUNT, DEFAULT_MAX_BATCH_COUNT);
    // maxBatchCount 如果小于 0 则使用默认值
    if (maxBatchCount <= 0) {
      maxBatchCount = DEFAULT_MAX_BATCH_COUNT;
      logger.warn("Invalid maxBatchCount specified, initializing source "
          + "default maxBatchCount of {}", maxBatchCount);
    }
    // 创建 SourceCounter(r1)
    if (sourceCounter == null) {
      sourceCounter = new SourceCounter(getName());
    }
  }

  @Override
  public long getBatchSize() {
    return batchSize;
  }

  private Map<String, String> selectByKeys(Map<String, String> map, String[] keys) {
    Map<String, String> result = Maps.newHashMap();
    for (String key : keys) {
      if (map.containsKey(key)) {
        result.put(key, map.get(key));
      }
    }
    return result;
  }

  private Table<String, String, String> getTable(Context context, String prefix) {
    Table<String, String, String> table = HashBasedTable.create();
    for (Entry<String, String> e : context.getSubProperties(prefix).entrySet()) {
      String[] parts = e.getKey().split("\\.", 2);
      table.put(parts[0], parts[1], e.getValue());
    }
    return table;
  }

  @VisibleForTesting
  protected SourceCounter getSourceCounter() {
    return sourceCounter;
  }

  /**
   * 上级 PollableSourceRunner 会启动线程, 并通过 PollingRunner 的 run() 方法不断调用该 process() 方法进行轮询拉取, 然后判断返回状态是否成功.
   * 如果成功, 会继续下一轮循环调用; 如果失败, 等待超时时间后会进行重试.
   */
  @Override
  public Status process() {
    // 默认失败
    Status status = Status.BACKOFF;
    try {
      // 清空 existingInodes
      existingInodes.clear();
      // 从 reader.updateTailFiles() 获取所有 Taildir 匹配器匹配的文件列表对应的 inode 列表, 赋值给 existingInodes
      existingInodes.addAll(reader.updateTailFiles());
      // 遍历 existingInodes
      for (long inode : existingInodes) {
        // 获取 TailFile 对象
        TailFile tf = reader.getTailFiles().get(inode);
        // 如果当前 TailFile 对象需要 tail, 则调用 tailFileProcess(x) 方法, 将当前文件中需要读取的行转换为 event 并发送到 channel
        if (tf.needTail()) {
          boolean hasMoreLines = tailFileProcess(tf, true);
          if (hasMoreLines) {
            status = Status.READY;
          }
        }
      }
      closeTailFiles();
    } catch (Throwable t) {
      logger.error("Unable to tail files", t);
      sourceCounter.incrementEventReadFail();
      status = Status.BACKOFF;
    }
    return status;
  }

  @Override
  public long getBackOffSleepIncrement() {
    return backoffSleepIncrement;
  }

  @Override
  public long getMaxBackOffSleepInterval() {
    return maxBackOffSleepInterval;
  }

  /**
   * 当前文件中需要读取的行转换为 event 并发送到 channel, 返回是否还有行未读取
   */
  private boolean tailFileProcess(TailFile tf, boolean backoffWithoutNL)
      throws IOException, InterruptedException {
    // 循环次数
    long batchCount = 0;
    while (true) {
      reader.setCurrentFile(tf);
      // 将日志文件每行转换为 Flume 的消息对象 event 列表, 并循环将每个 event 添加 header 信息 (每次读 batchSize 行, 即 batchSize 个 event)
      List<Event> events = reader.readEvents(batchSize, backoffWithoutNL);
      // 如果本次获取到的 event 列表为空, 则认为当前 TailFile 全部读完, 返回 (当前 TailFile 的所有行都已经读取)
      if (events.isEmpty()) {
        return false;
      }
      // 如果本次获取到的 event 列表不为空, 继续下面的逻辑处理 events

      // 记录 EventReceivedCount 指标
      sourceCounter.addToEventReceivedCount(events.size());
      // 记录 AppendBatchReceivedCount 指标
      sourceCounter.incrementAppendBatchReceivedCount();
      try {
        // 调用 channelProcessor, 将 events 批量发送到 channel 进行处理
        getChannelProcessor().processEventBatch(events);
        // 发送到 channel 成功后, 提交最近读取的行偏移量 pos
        reader.commit();
      } catch (ChannelException ex) {
        // 发送到 channel 异常, retryInterval 时间后重试 (如果连续异常, retryInterval 时间会一直增加, 从 1000 开始增加到 5000, 之后不再变化) (时间从 1000 开始, 然后以指数形式增长直到达到上限值 5000)
        logger.warn("The channel is full or unexpected failure. " +
            "The source will try again after " + retryInterval + " ms");
        // 记录 ChannelWriteFail 指标
        sourceCounter.incrementChannelWriteFail();
        TimeUnit.MILLISECONDS.sleep(retryInterval);
        retryInterval = retryInterval << 1;
        retryInterval = Math.min(retryInterval, maxRetryInterval);
        continue;
      }
      // 如果没有连续异常, retryInterval 时间重置为 10000
      retryInterval = 1000;
      // 记录 EventAcceptedCount 指标
      sourceCounter.addToEventAcceptedCount(events.size());
      // 记录 AppendBatchAcceptedCount 指标
      sourceCounter.incrementAppendBatchAcceptedCount();
      // 如果本次获取到的 event 列表, 小于本次批量大小, 则认为当前 TailFile 已经全部读完, 返回 (当前 TailFile 的所有行都已经读取)
      if (events.size() < batchSize) {
        logger.debug("The events taken from " + tf.getPath() + " is less than " + batchSize);
        return false;
      }
      // 如果循环次数大于了最大次数, 说明该文件超大, (循环了 maxBatchCount 次, 每次批量大小为 batchSize, 即已经读了 maxBatchCount * batchSize 行还未读完), 返回 (当前 TailFile 还有行未读取)
      if (++batchCount >= maxBatchCount) {
        logger.debug("The batches read from the same file is larger than " + maxBatchCount );
        return true;
      }
    }
  }

  private void closeTailFiles() throws IOException, InterruptedException {
    for (long inode : idleInodes) {
      TailFile tf = reader.getTailFiles().get(inode);
      if (tf.getRaf() != null) { // when file has not closed yet
        tailFileProcess(tf, false);
        tf.close();
        logger.info("Closed file: " + tf.getPath() + ", inode: " + inode + ", pos: " + tf.getPos());
      }
    }
    idleInodes.clear();
  }

  /**
   * Runnable class that checks whether there are files which should be closed.
   * 遍历 reader 所有监控的文件, 检查文件最后修改时间 + idleTimeout 是否小于当前时间, 如果小于则说明日志文件在 idleTimeout 时间内没有被修改, 该文件将会被关闭
   */
  private class idleFileCheckerRunnable implements Runnable {
    @Override
    public void run() {
      try {
        // 当前时间
        long now = System.currentTimeMillis();
        // 遍历 reader 所有监控的文件
        for (TailFile tf : reader.getTailFiles().values()) {
          // 如果文件最后修改时间 + idleTimeout 小于当前时间
          if (tf.getLastUpdated() + idleTimeout < now && tf.getRaf() != null) {
            // 加入到空闲 inodes 列表
            idleInodes.add(tf.getInode());
          }
        }
      } catch (Throwable t) {
        logger.error("Uncaught exception in IdleFileChecker thread", t);
        sourceCounter.incrementGenericProcessingFail();
      }
    }
  }

  /**
   * Runnable class that writes a position file which has the last read position
   * of each file.
   * 记录日志文件的偏移量, 以 json 格式: [{"inode", inode, "pos", tf.getPos(), "file", tf.getPath()}, {}, ...]
   * inode: Linux 系统的特有属性, 在其他应用系统如 Windows 使用时需要修改 ReliableTaildirEventReader.getInode()
   * pos: 日志读取的偏移量
   * file: 日志文件的路径
   */
  private class PositionWriterRunnable implements Runnable {
    @Override
    public void run() {
      writePosition();
    }
  }

  private void writePosition() {
    // 新建文件: /user/zozo/.flume/taildir_position.json 用于保存偏移量
    File file = new File(positionFilePath);
    // 文件写入流
    FileWriter writer = null;
    try {
      writer = new FileWriter(file);
      // 如果 existingInodes 不为空, 则获取文件 json 内容, 并写入文件流中
      if (!existingInodes.isEmpty()) {
        String json = toPosInfoJson();
        writer.write(json);
      }
    } catch (Throwable t) {
      logger.error("Failed writing positionFile", t);
      sourceCounter.incrementGenericProcessingFail();
    } finally {
      try {
        if (writer != null) writer.close();
      } catch (IOException e) {
        logger.error("Error: " + e.getMessage(), e);
        sourceCounter.incrementGenericProcessingFail();
      }
    }
  }

  private String toPosInfoJson() {
    @SuppressWarnings("rawtypes")
    List<Map> posInfos = Lists.newArrayList();
    // 遍历 existingInodes, 将每个 inode 在 reader 的 tailFiles 列表属性中对应的 TailFile 对象信息存入 json 字符串中
    for (Long inode : existingInodes) {
      TailFile tf = reader.getTailFiles().get(inode);
      posInfos.add(ImmutableMap.of("inode", inode, "pos", tf.getPos(), "file", tf.getPath()));
    }
    return new Gson().toJson(posInfos);
  }
}
