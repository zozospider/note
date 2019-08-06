/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.flume.source.taildir;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.gson.stream.JsonReader;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.annotations.InterfaceAudience;
import org.apache.flume.annotations.InterfaceStability;
import org.apache.flume.client.avro.ReliableEventReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 可靠的 Taildir Event 读取器
 */
@InterfaceAudience.Private
@InterfaceStability.Evolving
public class ReliableTaildirEventReader implements ReliableEventReader {
  private static final Logger logger = LoggerFactory.getLogger(ReliableTaildirEventReader.class);

  // FileGroup 对应的 Taildir 匹配器列表 {new TaildirMatcher(f1, /var/log/test1/example.log, true), new TaildirMatcher(f2, /var/log/test2/.*log.*, true)}
  private final List<TaildirMatcher> taildirCache;
  // {f1.headerKey1 = value1, f2.headerKey1 = value2, f2.headerKey2 = value2-2}
  private final Table<String, String, String> headerTable;

  // 当前处理的 TailFile
  private TailFile currentFile = null;
  // key 为 inode 的 TailFile 集合
  private Map<Long, TailFile> tailFiles = Maps.newHashMap();
  private long updateTime;
  private boolean addByteOffset;
  private boolean cachePatternMatching;
  private boolean committed = true;
  private final boolean annotateFileName;
  private final String fileNameHeader;

  /**
   * Create a ReliableTaildirEventReader to watch the given directory.
   */
  private ReliableTaildirEventReader(Map<String, String> filePaths,
      Table<String, String, String> headerTable, String positionFilePath,
      boolean skipToEnd, boolean addByteOffset, boolean cachePatternMatching,
      boolean annotateFileName, String fileNameHeader) throws IOException {
    // Sanity checks
    // filePaths 和 positionFilePath 不能为空
    Preconditions.checkNotNull(filePaths);
    Preconditions.checkNotNull(positionFilePath);

    if (logger.isDebugEnabled()) {
      logger.debug("Initializing {} with directory={}, metaDir={}",
          new Object[] { ReliableTaildirEventReader.class.getSimpleName(), filePaths });
    }

    // 新建 Taildir 匹配对象列表
    List<TaildirMatcher> taildirCache = Lists.newArrayList();
    // filePaths: {f1 = /var/log/test1/example.log, f2 = /var/log/test2/.*log.*}
    // 通过 filePaths 内容构建多个 Taildir 匹配对象, 并加入 Taildir 匹配对象列表
    for (Entry<String, String> e : filePaths.entrySet()) {
      // taildirCache.add(new TaildirMatcher(f1, /var/log/test1/example.log, true));
      taildirCache.add(new TaildirMatcher(e.getKey(), e.getValue(), cachePatternMatching));
    }
    // taildirCache: [{filegroup='f1', filePattern='/var/log/test1/example.log', cached=true}, {filegroup='f2', filePattern='/var/log/test2/.*log.*', cached=true}]
    logger.info("taildirCache: " + taildirCache.toString());
    // headerTable: {f1.headerKey1 = value1, f2.headerKey1 = value2, f2.headerKey2 = value2-2}
    logger.info("headerTable: " + headerTable.toString());

    this.taildirCache = taildirCache;
    this.headerTable = headerTable;
    this.addByteOffset = addByteOffset;
    this.cachePatternMatching = cachePatternMatching;
    this.annotateFileName = annotateFileName;
    this.fileNameHeader = fileNameHeader;
    // 更新 key 为 inode 的 TailFile 集合
    updateTailFiles(skipToEnd);

    // 从 /user/zozo/.flume/taildir_position.json 文件加载数据, 重新恢复各个文件的偏移量到内存中.
    logger.info("Updating position from position file: " + positionFilePath);
    loadPositionFile(positionFilePath);
  }

  /**
   * Load a position file which has the last read position of each file.
   * If the position file exists, update tailFiles mapping.
   * 从 /user/zozo/.flume/taildir_position.json 文件加载数据, 重新恢复各个文件的偏移量到内存中.
   */
  public void loadPositionFile(String filePath) {
    Long inode, pos;
    String path;
    FileReader fr = null;
    JsonReader jr = null;
    try {
      // 文件读取工具
      fr = new FileReader(filePath);
      // json 读取工具
      jr = new JsonReader(fr);
      // 开始读取 json
      jr.beginArray();
      while (jr.hasNext()) {
        inode = null;
        pos = null;
        path = null;
        // 开始读取 json 中的 1 个对象
        jr.beginObject();
        // 解析本对象中的 3 个属性值, 分别赋值到临时变量 inode, pos, file
        while (jr.hasNext()) {
          switch (jr.nextName()) {
            case "inode":
              inode = jr.nextLong();
              break;
            case "pos":
              pos = jr.nextLong();
              break;
            case "file":
              path = jr.nextString();
              break;
          }
        }
        // 结束读取 json 中的 1 个对象
        jr.endObject();

        // inode, pos, file 这 3 个属性值都不能为空
        for (Object v : Arrays.asList(inode, pos, path)) {
          Preconditions.checkNotNull(v, "Detected missing value in position file. "
              + "inode: " + inode + ", pos: " + pos + ", path: " + path);
        }
        // 通过 inode 从 TailFile 集合中拿到当前 TailFile
        TailFile tf = tailFiles.get(inode);
        // 如果集合中存在, 且集合中的 TailFile 的 inode, path 属性和当前临时变量相等, 则认为是同一个文件, 即更新内存中的 TailFile 的 pos 偏移量.
        // 即恢复 taildir_position.json 中 inode 对应的 pos 到内存中的 TailFile 集合中, 否则为新文件, 在本逻辑中不做处理.
        if (tf != null && tf.updatePos(path, inode, pos)) {
          // 更新 TailFile 集合中的 TailFile
          tailFiles.put(inode, tf);
        } else {
          // taildir_position.json 文件对应的 inode 在 TailFile 集合不存在, 表示丢失了该文件.
          logger.info("Missing file: " + path + ", inode: " + inode + ", pos: " + pos);
        }
      }
      // 结束读取 json
      jr.endArray();
    } catch (FileNotFoundException e) {
      logger.info("File not found: " + filePath + ", not updating position");
    } catch (IOException e) {
      logger.error("Failed loading positionFile: " + filePath, e);
    } finally {
      // 关闭流
      try {
        if (fr != null) fr.close();
        if (jr != null) jr.close();
      } catch (IOException e) {
        logger.error("Error: " + e.getMessage(), e);
      }
    }
  }

  public Map<Long, TailFile> getTailFiles() {
    return tailFiles;
  }

  public void setCurrentFile(TailFile currentFile) {
    this.currentFile = currentFile;
  }

  @Override
  public Event readEvent() throws IOException {
    List<Event> events = readEvents(1);
    if (events.isEmpty()) {
      return null;
    }
    return events.get(0);
  }

  @Override
  public List<Event> readEvents(int numEvents) throws IOException {
    return readEvents(numEvents, false);
  }

  @VisibleForTesting
  public List<Event> readEvents(TailFile tf, int numEvents) throws IOException {
    setCurrentFile(tf);
    return readEvents(numEvents, true);
  }

  /**
   * 将日志文件每行转换为 Flume 的消息对象 event 列表, 并循环将每个 event 添加 header 信息
   */
  public List<Event> readEvents(int numEvents, boolean backoffWithoutNL)
      throws IOException {
    if (!committed) {
      if (currentFile == null) {
        throw new IllegalStateException("current file does not exist. " + currentFile.getPath());
      }
      logger.info("Last read was never committed - resetting position");
      long lastPos = currentFile.getPos();
      currentFile.updateFilePos(lastPos);
    }
    List<Event> events = currentFile.readEvents(numEvents, backoffWithoutNL, addByteOffset);
    if (events.isEmpty()) {
      return events;
    }

    Map<String, String> headers = currentFile.getHeaders();
    if (annotateFileName || (headers != null && !headers.isEmpty())) {
      for (Event event : events) {
        if (headers != null && !headers.isEmpty()) {
          event.getHeaders().putAll(headers);
        }
        if (annotateFileName) {
          event.getHeaders().put(fileNameHeader, currentFile.getPath());
        }
      }
    }
    committed = false;
    return events;
  }

  @Override
  public void close() throws IOException {
    for (TailFile tf : tailFiles.values()) {
      if (tf.getRaf() != null) tf.getRaf().close();
    }
  }

  /** Commit the last lines which were read. */
  @Override
  public void commit() throws IOException {
    if (!committed && currentFile != null) {
      long pos = currentFile.getLineReadPos();
      currentFile.setPos(pos);
      currentFile.setLastUpdated(updateTime);
      committed = true;
    }
  }

  /**
   * Update tailFiles mapping if a new file is created or appends are detected
   * to the existing file.
   * 获取所有 Taildir 匹配器匹配的文件列表对应的 inode 列表
   */
  public List<Long> updateTailFiles(boolean skipToEnd) throws IOException {
    // 本次更新时间
    updateTime = System.currentTimeMillis();
    // 更新的 inode 集合
    List<Long> updatedInodes = Lists.newArrayList();

    // taildirCache: [{filegroup='f1', filePattern='/var/log/test1/example.log', cached=true}, {filegroup='f2', filePattern='/var/log/test2/.*log.*', cached=true}]
    // headerTable: {f1.headerKey1 = value1, f2.headerKey1 = value2, f2.headerKey2 = value2-2}
    // 遍历 taildirCache (FileGroup 对应的 Taildir 匹配器列表)
    for (TaildirMatcher taildir : taildirCache) {
      // headers: [{headerKey1=value1}]
      Map<String, String> headers = headerTable.row(taildir.getFileGroup());

      // 获取当前 FileGroup 对应的 Taildir 匹配器匹配的文件列表, 然后遍历
      for (File f : taildir.getMatchingFiles()) {
        long inode;
        try {
          // 获取当前匹配器中文件的 inode
          inode = getInode(f);
        } catch (NoSuchFileException e) {
          logger.info("File has been deleted in the meantime: " + e.getMessage());
          continue;
        }
        // 从内存中的 key 为 inode 的 TailFile 集合中取出 TailFile, 判断是否存在
        TailFile tf = tailFiles.get(inode);
        // 如果不存在, 或者全路径不相等, 则认为是新文件.
        if (tf == null || !tf.getPath().equals(f.getAbsolutePath())) {
          // 从 0 开始读
          long startPos = skipToEnd ? f.length() : 0;
          // 新建一个 TailFile 对象 (使用匹配器匹配的文件对象, 从 0 开始), 并将该对象指向内存集合中 key 为 inode 的 TailFile
          tf = openFile(f, headers, inode, startPos);

        // 如果存在, 且全路径相等, 则认为是旧文件.
        } else {
          // 如果内存中的 TailFile 上次更新时间小于匹配器中文件的修改时间, 或者内存中的 TailFile 的偏移量不等于匹配器中文件的长度, 则需要 tail
          // 如果内存中的 TailFile 上次更新时间大于等于匹配器中文件的修改时间, 且内存中的 TailFile 的偏移量等于匹配器中文件的长度, 则不需要 Tail
          boolean updated = tf.getLastUpdated() < f.lastModified() || tf.getPos() != f.length();
          if (updated) {
            // 如果内存中的 TailFile 对应的 RandomAccessFile 属性为空, 则新建一个 TailFile 对象 (使用匹配器匹配的文件对象, 从 TailFile 的 pos 偏移量开始)
            if (tf.getRaf() == null) {
              tf = openFile(f, headers, inode, tf.getPos());
            }
            // 如果匹配器中文件的长度小于内存中的 TailFile 的 pos 偏移量, 则从 0 开始重新读.
            if (f.length() < tf.getPos()) {
              logger.info("Pos " + tf.getPos() + " is larger than file size! "
                  + "Restarting from pos 0, file: " + tf.getPath() + ", inode: " + inode);
              tf.updatePos(tf.getPath(), inode, 0);
            }
          }
          // 更新内存中的 TailFile 是否需要 tail
          tf.setNeedTail(updated);
        }
        // 将修改后的 TailFile 重新放入内存中的 key 为 inode 的 TailFile 集合中
        tailFiles.put(inode, tf);
        // 将所有 Taildir 匹配器匹配的文件列表对应的 inode 加入到 updatedInodes 列表
        updatedInodes.add(inode);
      }
    }
    // 返回所有 Taildir 匹配器匹配的文件集合对应的 updatedInodes 列表
    return updatedInodes;
  }

  public List<Long> updateTailFiles() throws IOException {
    return updateTailFiles(false);
  }


  /**
   * 获取文件的 inode, 仅适用于 Linux
   */
  private long getInode(File file) throws IOException {
    long inode = (long) Files.getAttribute(file.toPath(), "unix:ino");
    return inode;
  }

  /**
   * 新建一个 TailFile 对象
   */
  private TailFile openFile(File file, Map<String, String> headers, long inode, long pos) {
    try {
      logger.info("Opening file: " + file + ", inode: " + inode + ", pos: " + pos);
      return new TailFile(file, headers, inode, pos);
    } catch (IOException e) {
      throw new FlumeException("Failed opening file: " + file, e);
    }
  }

  /**
   * Special builder class for ReliableTaildirEventReader
   */
  public static class Builder {
    private Map<String, String> filePaths;
    private Table<String, String, String> headerTable;
    private String positionFilePath;
    private boolean skipToEnd;
    private boolean addByteOffset;
    private boolean cachePatternMatching;
    private Boolean annotateFileName =
            TaildirSourceConfigurationConstants.DEFAULT_FILE_HEADER;
    private String fileNameHeader =
            TaildirSourceConfigurationConstants.DEFAULT_FILENAME_HEADER_KEY;

    public Builder filePaths(Map<String, String> filePaths) {
      this.filePaths = filePaths;
      return this;
    }

    public Builder headerTable(Table<String, String, String> headerTable) {
      this.headerTable = headerTable;
      return this;
    }

    public Builder positionFilePath(String positionFilePath) {
      this.positionFilePath = positionFilePath;
      return this;
    }

    public Builder skipToEnd(boolean skipToEnd) {
      this.skipToEnd = skipToEnd;
      return this;
    }

    public Builder addByteOffset(boolean addByteOffset) {
      this.addByteOffset = addByteOffset;
      return this;
    }

    public Builder cachePatternMatching(boolean cachePatternMatching) {
      this.cachePatternMatching = cachePatternMatching;
      return this;
    }

    public Builder annotateFileName(boolean annotateFileName) {
      this.annotateFileName = annotateFileName;
      return this;
    }

    public Builder fileNameHeader(String fileNameHeader) {
      this.fileNameHeader = fileNameHeader;
      return this;
    }

    public ReliableTaildirEventReader build() throws IOException {
      return new ReliableTaildirEventReader(filePaths, headerTable, positionFilePath, skipToEnd,
                                            addByteOffset, cachePatternMatching,
                                            annotateFileName, fileNameHeader);
    }
  }

}
