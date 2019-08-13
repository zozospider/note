
- link
  - [日志系统实践](http://www.yoonper.com/index.php)
  - [flume架构与核心组件源代码分析](https://blog.csdn.net/HarderXin/article/details/74191460)
  - [修改Flume源码使taildir source支持递归（可配置）](https://segmentfault.com/a/1190000019551664)
  - [修改Flume taildir source源码,使其支持递归读取指定目录下的文件变化](https://github.com/yx1319250478/Flume-taildir-source)
  - [中国民生银行大数据团队的Flume实践](https://juejin.im/post/5a22b1c76fb9a045167d00f0)


* 源码
  * __LifecycleAware__, __Configurable__
    * LifecycleAware `I`, Configurable `I`
  * __Source__, __SourceRunner__
    * Source `I`, EventDrivenSource `I`, PollableSource `I`, NetcatSource `C`, AvroSource `C`, TaildirSource `C`
    * SourceRunner `C`, EventDrivenSourceRunner `C`, PollableSourceRunner `C`
  * __Channel__, __Transaction__, __Interceptor__, __ChannelSelector__, __ChannelProcessor__
    * Channel `I`, MemoryChannel `C`, FileChannel `C`
    * Transaction `I`, MemoryTransaction `C`, FileBackedTransaction `C`
    * Interceptor `I`, StaticInterceptor `C`, RegexFilteringInterceptor `C`, RegexExtractorInterceptor `C`
    * ChannelSelector `I`, MultiplexingChannelSelector `C`, ReplicatingChannelSelector `C`
    * ChannelProcessor `C` _processEvent(e)_
  * __Sink__, __SinkRunner__, __SinkProcessor__
    * Sink `I`, LoggerSink `C`, AvroSink `C`, RollingFileSInk `C`
    * SinkRunner `C`
    * SinkProcessor `I` _process()_, DefaultSinkProcessor `C`, LoadBalancingSinkProcessor `C`, FailoverSinkProcessor `C`
  * __Counter__
    * MonitoredCounterGroup `C`, SourceCounter `C`, ChannelCounter `C`, SinkCounter `C`
  * __Application__
    * Application `C`
  * __Client__
    * RpcClient `I`, NettyAvroRpcClient `C`, LoadBalancingRpcClient `C`, FailoverRpcClient `C`


- 基本介绍
- 三大组件, 其他
  - 源码 __LifecycleAware__, __Configurable__
- 拓扑结构
- 核心原理
- Channel
  - 源码 __Channel__, __Transaction__
  - 自定义 Channel
- Source
  - 详细原理
  - 源码 __Source__, __SourceRunner__, __Interceptor__, __ChannelSelector__, __ChannelProcessor__
  - 自定义 Source, Interceptor
- Sink
  - 详细原理
  - 源码 __Sink__, __SinkRunner__, __SinkProcessor__
  - 自定义 Sink
- 客户端 (可选)
- 启动
  - 介绍
  - 源码 __Application__
- 常见问题处理, 规划
  - 常见问题处理, 监控源码 __Counter__
  - 规划
