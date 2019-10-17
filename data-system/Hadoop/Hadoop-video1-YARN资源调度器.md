


---

# 一 YARN 基本架构

YARN 是一个资源调度平台, 负责为运算程序提供服务器运算资源, 相当于一个分布式的操作系统平台, MapReduce 等运算程序则相当于运行于操作系统之上的应用程序.

YARN 主要由 ResourceManager, NodeManager, ApplicationMaster, Container 等组件构成.

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-YARN%E8%B5%84%E6%BA%90%E8%B0%83%E5%BA%A6%E5%99%A8/YARN%E6%9E%B6%E6%9E%84.png?raw=true)

---

# 二 YARN 工作机制 / 作业提交

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-YARN%E8%B5%84%E6%BA%90%E8%B0%83%E5%BA%A6%E5%99%A8/YARN%E5%B7%A5%E4%BD%9C%E6%9C%BA%E5%88%B6.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A1%86%E6%9E%B6%E5%8E%9F%E7%90%86-MapReduce%E5%B7%A5%E4%BD%9C%E6%9C%BA%E5%88%B6/Map1.png?raw=true)

- 一. 作业提交
  - 1. Client 调用 job.waitForCompletion() 方法, 向整个集群提交 MapReduce 作业.
  - 2. Client 向 ResourceManager 申请一个作业 ID.
  - 3. ResourceManager 给 Client 返回该 Job 资源的提交路径和作业 ID.
  - 4. Client 提交 jar 包, splits 信息, 配置文件到指定到资源提交路径.
  - 5. Client 提交完成后, 向 ResourceManager 申请运行 MrAppMaster.
- 二. 作业初始化
  - 6. 当 ResourceManager 收到 Client 请求后, 将该 Job 添加到容量调度器中.
  - 7. 某一个空闲的 NodeManager 领取到该 Job.
  - 8. 该 NodeManager 创建 Container, 并产生 MrAppMaster.
  - 9. 下载 Client 提交的资源到本地.
- 三. 任务分配
  - 10. MrAppMaster 向 ResourceManager 申请运行多个 MapTask 任务资源.
  - 11. ResourceManager 将运行 MapTask 任务分配给另外两个 NodeManager, 另外两个 NodeManager 分别领取任务并创建 Container.
- 四. 任务运行
  - 12. ResourceManager 向两个接收到任务的 NodeManager 发送启动脚本, 这两个 NodeManager 分别启动 MapTask, MapTask 对数据进行分区排序.
  - 13. MrAppMaster 等待所有 MapTask 运行完毕后, 向 ResourceManager 申请 Container, 运行 ReduceTask.
  - 14. ReduceTask 向 MapTask 获取相应分区对数据.
- 五. 作业完成
  - 15. 除了向应用管理器请求作业进度外, Client 每 5 分钟都会通过调用 waitForCompletion() 来检查作业是否完成 (通过 `mapreduce.client.completion.pollinterval` 设置). 作业完成之后, 应用管理器和 Container 会清理工作状态. 作业的信息会被作业历史服务器存储以备之后用户核查.

- 进度和状态更新
  - YARN 中的任务将其进度和状态 (包括 Counter) 返回给应用管理器, Client 每秒 (通过 `mapreduce.client.progressmonitor.pollinterval` 设置) 向应用管理器请求进度更新, 展示给用户.

---

# 三 资源调度器

---

# 四 任务的推测执行

---
