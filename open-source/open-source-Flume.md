
# Document & Code

* 核心源码
  * __LifecycleAware__, __Configurable__
    * LifecycleAware `I`, Configurable `I`
  * __Channel__, __Transaction__
    * Channel `I`, MemoryChannel `C`, FileChannel `C`
    * Transaction `I`, MemoryTransaction `C`, FileBackedTransaction `C`
  * __Source__, __SourceRunner__, __Interceptor__, __ChannelSelector__, __ChannelProcessor__
    * Source `I`, EventDrivenSource `I`, PollableSource `I`, NetcatSource `C`, AvroSource `C`, TaildirSource `C`
    * SourceRunner `C`, EventDrivenSourceRunner `C`, PollableSourceRunner `C`
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

- Java
  - org
    - apache
      - flume
        - conf
          - [Configurable.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/flume/conf/Configurable.java)
        - lifecycle
          - [LifecycleAware.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/flume/lifecycle/LifecycleAware.java)
        - source
          - taildir
            - [TaildirSource.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/flume/source/taildir/TaildirSource.java)
            - [ReliableTaildirEventReader.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/flume/source/taildir/ReliableTaildirEventReader.java)
            - [TailFile.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/flume/source/taildir/TailFile.java)
            - [TaildirMatcher.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/flume/source/taildir/TaildirMatcher.java)
        - [Channel.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/flume/Channel.java)
        - [Sink.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/flume/Sink.java)
        - [Source.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/flume/Source.java)
        - [Transaction.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/flume/Transaction.java)

- code
  - [zozospider/open-source](https://github.com/zozospider/open-source)

- link
  - [日志系统实践](http://www.yoonper.com/index.php)
  - [flume架构与核心组件源代码分析](https://blog.csdn.net/HarderXin/article/details/74191460)
  - [【Flume】TailDirSource源碼理解](https://www.twblogs.net/a/5b957a7c2b717750bda476b6)
  - [【Flume】TailDirSource源码理解](https://blog.51cto.com/10120275/2050827)
  - [修改Flume源码使taildir source支持递归（可配置）](https://segmentfault.com/a/1190000019551664)
  - [修改Flume taildir source源码,使其支持递归读取指定目录下的文件变化](https://github.com/yx1319250478/Flume-taildir-source)
  - [flume1.7.0－taildirSource 支持多文件监控和断点续传](https://unordered.org/timelines/59cd596c3c001000)
  - [中国民生银行大数据团队的Flume实践](https://juejin.im/post/5a22b1c76fb9a045167d00f0)
  - [中国民生银行大数据团队的Flume实践（Github 地址）](https://github.com/tinawenqiao/flume/tree/trunk-cmbc)
  - [flume使用（五）：taildirSource重复获取数据和不释放资源解决办法](https://blog.csdn.net/maoyuanming0806/article/details/79391657)
  - [flume监控inode变化的文件（自定义source不影响原来的TAILDIR）](https://www.jianshu.com/p/7f74dbd45fd2)
