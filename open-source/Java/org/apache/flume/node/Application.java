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

package org.apache.flume.node;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.flume.Channel;
import org.apache.flume.Constants;
import org.apache.flume.Context;
import org.apache.flume.SinkRunner;
import org.apache.flume.SourceRunner;
import org.apache.flume.instrumentation.MonitorService;
import org.apache.flume.instrumentation.MonitoringType;
import org.apache.flume.lifecycle.LifecycleAware;
import org.apache.flume.lifecycle.LifecycleState;
import org.apache.flume.lifecycle.LifecycleSupervisor;
import org.apache.flume.lifecycle.LifecycleSupervisor.SupervisorPolicy;
import org.apache.flume.util.SSLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class Application {

  private static final Logger logger = LoggerFactory
      .getLogger(Application.class);

  public static final String CONF_MONITOR_CLASS = "flume.monitoring.type";
  public static final String CONF_MONITOR_PREFIX = "flume.monitoring.";

  // reload 时用于存储所有 components
  private final List<LifecycleAware> components;
  private final LifecycleSupervisor supervisor;
  private MaterializedConfiguration materializedConfiguration;
  private MonitorService monitorServer;
  private final ReentrantLock lifecycleLock = new ReentrantLock();

  public Application() {
    this(new ArrayList<LifecycleAware>(0));
  }

  public Application(List<LifecycleAware> components) {
    this.components = components;
    supervisor = new LifecycleSupervisor();
  }

  /**
   * 启动所有 components (reload 时用到)
   */
  public void start() {
    // 锁定
    lifecycleLock.lock();
    try {
      // 启动所有 components (reload 时该 components 不为空)
      for (LifecycleAware component : components) {
        supervisor.supervise(component,
            new SupervisorPolicy.AlwaysRestartPolicy(), LifecycleState.START);
      }
    } finally {
      // 解锁
      lifecycleLock.unlock();
    }
  }

  /**
   * 停止所有 components (如果已存在), 启动所有 components, 加载并启动监控服务
   */
  @Subscribe
  public void handleConfigurationEvent(MaterializedConfiguration conf) {
    try {
      lifecycleLock.lockInterruptibly();
      // 停止所有 components (如果已存在)
      stopAllComponents();
      // 启动所有 components, 加载并启动监控服务
      startAllComponents(conf);
    } catch (InterruptedException e) {
      logger.info("Interrupted while trying to handle configuration event");
      return;
    } finally {
      // If interrupted while trying to lock, we don't own the lock, so must not attempt to unlock
      // 如果在尝试锁定时中断, 我们不拥有锁, 因此不得尝试解锁
      if (lifecycleLock.isHeldByCurrentThread()) {
        lifecycleLock.unlock();
      }
    }
  }

  public void stop() {
    lifecycleLock.lock();
    stopAllComponents();
    try {
      supervisor.stop();
      if (monitorServer != null) {
        monitorServer.stop();
      }
    } finally {
      lifecycleLock.unlock();
    }
  }

  /**
   * 停止所有 components (如果已存在)
   */
  private void stopAllComponents() {
    // 在当前对象的成员变量 materializedConfiguration 不为 null 的情况下 (即重新加载配置而非重新启动), 需要停止所有 components.
    if (this.materializedConfiguration != null) {
      logger.info("Shutting down configuration: {}", this.materializedConfiguration);
      // 停止所有 sourceRunners
      for (Entry<String, SourceRunner> entry :
           this.materializedConfiguration.getSourceRunners().entrySet()) {
        try {
          logger.info("Stopping Source " + entry.getKey());
          supervisor.unsupervise(entry.getValue());
        } catch (Exception e) {
          logger.error("Error while stopping {}", entry.getValue(), e);
        }
      }

      // 停止所有 sinkRunners
      for (Entry<String, SinkRunner> entry :
           this.materializedConfiguration.getSinkRunners().entrySet()) {
        try {
          logger.info("Stopping Sink " + entry.getKey());
          supervisor.unsupervise(entry.getValue());
        } catch (Exception e) {
          logger.error("Error while stopping {}", entry.getValue(), e);
        }
      }

      // 停止所有 channels
      for (Entry<String, Channel> entry :
           this.materializedConfiguration.getChannels().entrySet()) {
        try {
          logger.info("Stopping Channel " + entry.getKey());
          supervisor.unsupervise(entry.getValue());
        } catch (Exception e) {
          logger.error("Error while stopping {}", entry.getValue(), e);
        }
      }
    }
    // 停止监控服务
    if (monitorServer != null) {
      monitorServer.stop();
    }
  }

  /**
   * 启动所有 components, 加载并启动监控服务
   */
  private void startAllComponents(MaterializedConfiguration materializedConfiguration) {
    logger.info("Starting new configuration:{}", materializedConfiguration);

    // 设置到当前对象的成员变量 materializedConfiguration
    this.materializedConfiguration = materializedConfiguration;

    // 启动所有 channels (异步)
    for (Entry<String, Channel> entry :
        materializedConfiguration.getChannels().entrySet()) {
      try {
        logger.info("Starting Channel " + entry.getKey());
        supervisor.supervise(entry.getValue(),
            new SupervisorPolicy.AlwaysRestartPolicy(), LifecycleState.START);
      } catch (Exception e) {
        logger.error("Error while starting {}", entry.getValue(), e);
      }
    }

    /*
     * Wait for all channels to start.
     * 等待所有 channels 开始.
     * 由于所有 channels 通过异步启动, 在此需要等待所有 channels 状态都变为 START 才进入下面的逻辑.
     */
    for (Channel ch : materializedConfiguration.getChannels().values()) {
      while (ch.getLifecycleState() != LifecycleState.START
          && !supervisor.isComponentInErrorState(ch)) {
        try {
          // Waiting for channel: c1 to start. Sleeping for 500 ms
          // Waiting for channel: c2 to start. Sleeping for 500 ms
          // ...
          logger.info("Waiting for channel: " + ch.getName() +
              " to start. Sleeping for 500 ms");
          Thread.sleep(500);
        } catch (InterruptedException e) {
          logger.error("Interrupted while waiting for channel to start.", e);
          Throwables.propagate(e);
        }
      }
    }

    // 启动所有 sinkRunners (异步)
    for (Entry<String, SinkRunner> entry : materializedConfiguration.getSinkRunners().entrySet()) {
      try {
        logger.info("Starting Sink " + entry.getKey());
        supervisor.supervise(entry.getValue(),
            new SupervisorPolicy.AlwaysRestartPolicy(), LifecycleState.START);
      } catch (Exception e) {
        logger.error("Error while starting {}", entry.getValue(), e);
      }
    }

    // 启动所有 sourceRunners (异步)
    for (Entry<String, SourceRunner> entry :
         materializedConfiguration.getSourceRunners().entrySet()) {
      try {
        logger.info("Starting Source " + entry.getKey());
        supervisor.supervise(entry.getValue(),
            new SupervisorPolicy.AlwaysRestartPolicy(), LifecycleState.START);
      } catch (Exception e) {
        logger.error("Error while starting {}", entry.getValue(), e);
      }
    }

    // 加载并启动监控服务
    this.loadMonitoring();
  }

  /**
   * 加载并启动监控服务
   */
  @SuppressWarnings("unchecked")
  private void loadMonitoring() {
    Properties systemProps = System.getProperties();
    Set<String> keys = systemProps.stringPropertyNames();
    try {
      /**
       * 如果系统参数中有 flume.monitoring.type, 则初始化对应的监控服务
       * a. 判断系统参数中是否有 flume.monitoring.type
       * b. 如果有则获取传入参数值
       * c. 根据传入参数值, 判断是否是已知类型
       * d. 如果是已知类型, 则创建已知的 MonitorService 实例对象
       * e. 如果不是已知类型, 则创建配置的 MonitorService 实例对象
       * f. 将传入的监控参数加入到上下文对象
       * g. 调用当前 MonitorService 的 configure(c) 方法
       * h. 启动当前 MonitorService
       */
      // a
      if (keys.contains(CONF_MONITOR_CLASS)) {
        // b
        String monitorType = systemProps.getProperty(CONF_MONITOR_CLASS);
        Class<? extends MonitorService> klass;
        try {
          // c
          // d
          //Is it a known type?
          // 它是一种已知类型吗?
          klass = MonitoringType.valueOf(
              monitorType.toUpperCase(Locale.ENGLISH)).getMonitorClass();
        } catch (Exception e) {
          // e
          //Not a known type, use FQCN
          // 不是已知类型, 使用 FQCN
          klass = (Class<? extends MonitorService>) Class.forName(monitorType);
        }
        this.monitorServer = klass.newInstance();
        // f
        Context context = new Context();
        for (String key : keys) {
          if (key.startsWith(CONF_MONITOR_PREFIX)) {
            context.put(key.substring(CONF_MONITOR_PREFIX.length()),
                systemProps.getProperty(key));
          }
        }
        // g
        monitorServer.configure(context);
        // h
        monitorServer.start();
      }
    } catch (Exception e) {
      logger.warn("Error starting monitoring. "
          + "Monitoring might not be available.", e);
    }

  }

  public static void main(String[] args) {

    try {
      SSLUtil.initGlobalSSLParameters();

      // 处理启动参数
      Options options = new Options();

      Option option = new Option("n", "name", true, "the name of this agent");
      option.setRequired(true);
      options.addOption(option);

      option = new Option("f", "conf-file", true,
          "specify a config file (required if -z missing)");
      option.setRequired(false);
      options.addOption(option);

      option = new Option(null, "no-reload-conf", false,
          "do not reload config file if changed");
      options.addOption(option);

      // Options for Zookeeper
      // Zookeeper 的选项
      option = new Option("z", "zkConnString", true,
          "specify the ZooKeeper connection to use (required if -f missing)");
      option.setRequired(false);
      options.addOption(option);

      option = new Option("p", "zkBasePath", true,
          "specify the base path in ZooKeeper for agent configs");
      option.setRequired(false);
      options.addOption(option);

      option = new Option("h", "help", false, "display help text");
      options.addOption(option);

      // 启动相关转为 CommandLine 对象
      CommandLineParser parser = new GnuParser();
      CommandLine commandLine = parser.parse(options, args);

      // 打印帮助
      if (commandLine.hasOption('h')) {
        new HelpFormatter().printHelp("flume-ng agent", options, true);
        return;
      }

      // 获取 agent 名称
      String agentName = commandLine.getOptionValue('n');
      // 获取是否需要 reload
      boolean reload = !commandLine.hasOption("no-reload-conf");

      // 是否使用 ZooKeeper 配置
      boolean isZkConfigured = false;
      if (commandLine.hasOption('z') || commandLine.hasOption("zkConnString")) {
        isZkConfigured = true;
      }

      Application application;
      // 以下为使用 ZooKeeper 配置逻辑 (即不使用配置文件)
      if (isZkConfigured) {
        // get options
        // 得到选项
        String zkConnectionStr = commandLine.getOptionValue('z');
        String baseZkPath = commandLine.getOptionValue('p');

        // 以下为 reload 逻辑
        if (reload) {
          EventBus eventBus = new EventBus(agentName + "-event-bus");
          List<LifecycleAware> components = Lists.newArrayList();
          PollingZooKeeperConfigurationProvider zookeeperConfigurationProvider =
              new PollingZooKeeperConfigurationProvider(
                  agentName, zkConnectionStr, baseZkPath, eventBus);
          components.add(zookeeperConfigurationProvider);
          application = new Application(components);
          eventBus.register(application);
        // 以下为非 reload 逻辑
        } else {
          StaticZooKeeperConfigurationProvider zookeeperConfigurationProvider =
              new StaticZooKeeperConfigurationProvider(
                  agentName, zkConnectionStr, baseZkPath);
          application = new Application();
          application.handleConfigurationEvent(zookeeperConfigurationProvider.getConfiguration());
        }
      // 以下为不使用 ZooKeeper 配置逻辑 (即使用配置文件)
      } else {
        // 配置文件 File 对象
        File configurationFile = new File(commandLine.getOptionValue('f'));

        /*
         * The following is to ensure that by default the agent will fail on
         * startup if the file does not exist.
         * 以下是为了确保默认情况下, 如果文件不存在, agent 将在启动时失败.
         */
        if (!configurationFile.exists()) {
          // If command line invocation, then need to fail fast
          // 如果命令行调用, 则需要快速失败
          if (System.getProperty(Constants.SYSPROP_CALLED_FROM_SERVICE) ==
              null) {
            String path = configurationFile.getPath();
            try {
              path = configurationFile.getCanonicalPath();
            } catch (IOException ex) {
              logger.error("Failed to read canonical path for file: " + path,
                  ex);
            }
            throw new ParseException(
                "The specified configuration file does not exist: " + path);
          }
        }
        List<LifecycleAware> components = Lists.newArrayList();

        // 以下为 reload 逻辑
        if (reload) {
          EventBus eventBus = new EventBus(agentName + "-event-bus");
          PollingPropertiesFileConfigurationProvider configurationProvider =
              new PollingPropertiesFileConfigurationProvider(
                  agentName, configurationFile, eventBus, 30);
          components.add(configurationProvider);
          application = new Application(components);
          eventBus.register(application);
        // 以下为非 reload 逻辑
        } else {
          // 新建 ConfigurationProvider 的具体实现对象 PropertiesFileConfigurationProvider, 传入参数: agent 名称, 配置文件 File 对象
          PropertiesFileConfigurationProvider configurationProvider =
              new PropertiesFileConfigurationProvider(agentName, configurationFile);
          application = new Application();
          // 通过 ConfigurationProvider 接口的 getConfiguration() 接口获取 MaterializedConfiguration (即 Flume 配置文件的具体化)
          // 停止所有 components (如果已存在), 启动所有 components, 加载并启动监控服务
          application.handleConfigurationEvent(configurationProvider.getConfiguration());
        }
      }
      // 启动所有 components (reload 时用到)
      application.start();

      final Application appReference = application;
      Runtime.getRuntime().addShutdownHook(new Thread("agent-shutdown-hook") {
        @Override
        public void run() {
          appReference.stop();
        }
      });

    } catch (Exception e) {
      logger.error("A fatal error occurred while running. Exception follows.", e);
    }
  }
}