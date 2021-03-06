
# link

- __Java__
  - org
    - apache
      - jute
        - [BinaryInputArchive.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/jute/BinaryInputArchive.java)
        - [BinaryOutputArchive.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/jute/BinaryOutputArchive.java)
        - [InputArchive.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/jute/InputArchive.java)
        - [OutputArchive.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/jute/OutputArchive.java)
        - [Record.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/jute/Record.java)
      - zookeeper
        - server
          - [DataNode.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/zookeeper/server/DataNode.java)
          - [DataTree.java](https://github.com/zozospider/note/blob/master/open-source/Java/org/apache/zookeeper/server/DataTree.java)


- [【Zookeeper】源码分析目录](https://www.cnblogs.com/leesf456/p/6518040.html)
- [zookeeper-数据同步源码分析](https://juejin.im/post/5cdd57966fb9a03212507c7b#heading-0)
- [Zookeeper源码](https://blog.reactor.top/categories/Zookeeper%E6%BA%90%E7%A0%81/)

---

# Core Source

* __Archive__, __Recode__
  * InputArchive `I`, BinaryInputArchive `C`
  * OutputArchive `I`, BinaryOutputArchive `C`
  * Record `I`, ConnectRequest `C`, CreateRequest `C`, ACL `C`, Datatree `C`
* __SnapShot__, __TxnLog__
  * SnapShot `I`, FileSnap `C`
  * TxnLog `I`, FileTxnLog `C`
  * FileTxnSnapLog `C`
* __DataNode__, __Datatree__, __ZKDatabase__
  * DataNode `C`, WatchManager `C`
  * Datatree `C`
  * ZKDatabase `C`
* __ZooKeeper__, __ClientCnxn__
  * ZooKeeper _ZKWatchManager `C`
  * ClientCnxn _SendThread _EventThread _Packet `C`
  * ClientCnxnSocket `I`, ClientCnxnSocketNIO `C`
* __QuorumPeerMain__, __ServerCnxnFactory__, __ServerCnxn__, __ZooKeeperServer__, __RequestProcessor__
  * QuorumPeerMain `C`, QuorumPeer `C`, QuorumPeerConfig `C`
  * ServerCnxnFactory `AC`, NIOServerCnxnFactory `C`
  * ServerCnxn `AC`, NIOServerCnxn `C`
  * ZooKeeperServer `C`, ReadOnlyZooKeeperServer `C`
  * RequestProcessor `I`, PrepRequestProcessor `C`, SyncRequestProcessor `C`, FinalRequestProcessor `C`
* __SessionTracker__
  * SessionTracker `I`, SessionTrackerImpl `C`
* __Election__, __QuorumCnxManager__
  * Election `I`, FastLeaderElection _Notification _ToSend _Messenger _WorkerSender _WorkerReceiver `C`
  * QuorumCnxManager _Message _Listener _SendWorker _RecvWorker `C`
* __Learner__, __LearnerHandler__, __QuorumZooKeeperServer__
  * Learner `C`, Follower `C`, Observer `C`
  * LearnerHandler `C`
  * QuorumZooKeeperServer `C`, LeaderZooKeeperServer `C`, LearnerZooKeeperServer `C`, FollowerZooKeeperServer `C`, ObserverZooKeeperServer `C`

---

---

---

---

---

---

![image](https://github.com/zozospider/note/blob/master/open-source/open-source-ZooKeeper/%E9%80%89%E4%B8%BE-1.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/open-source/open-source-ZooKeeper/%E9%80%89%E4%B8%BE-2.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/open-source/open-source-ZooKeeper/%E6%9C%8D%E5%8A%A1%E5%99%A8%E9%97%B4%E6%95%B0%E6%8D%AE%E5%90%8C%E6%AD%A5%E8%BF%87%E7%A8%8B-1.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/open-source/open-source-ZooKeeper/%E6%9C%8D%E5%8A%A1%E5%99%A8%E9%97%B4%E6%95%B0%E6%8D%AE%E5%90%8C%E6%AD%A5%E8%BF%87%E7%A8%8B-2.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/open-source/open-source-ZooKeeper/%E4%BA%8B%E5%8A%A1%E8%AF%B7%E6%B1%82-Leader.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/open-source/open-source-ZooKeeper/%E4%BA%8B%E5%8A%A1%E8%AF%B7%E6%B1%82-Follower.png?raw=true)

---
