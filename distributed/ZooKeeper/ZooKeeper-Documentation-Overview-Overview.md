
- [Document & Code](#document--code)
- [ZooKeeper](#zookeeper)
- [Design Goals](#design-goals)
    - [ZooKeeper is simple](#zookeeper-is-simple)
    - [ZooKeeper is replicated](#zookeeper-is-replicated)
    - [ZooKeeper is ordered](#zookeeper-is-ordered)
    - [ZooKeeper is fast](#zookeeper-is-fast)
- [Data model and the hierarchical namespace](#data-model-and-the-hierarchical-namespace)
- [Nodes and ephemeral nodes](#nodes-and-ephemeral-nodes)
- [Conditional updates and watches](#conditional-updates-and-watches)
- [Guarantees](#guarantees)
- [Simple API](#simple-api)
- [Implementation](#implementation)
- [Uses](#uses)
- [Performance](#performance)
- [Reliability](#reliability)
- [The ZooKeeper Project](#the-zookeeper-project)

---

# Document & Code

* [../Zookeeper-Documentation](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-Documentation.md)

---

# ZooKeeper

__ZooKeeper is a distributed, open-source coordination service for distributed applications.__

Coordination services are notoriously hard to get right. They are especially prone to errors such as race conditions and deadlock. __The motivation behind ZooKeeper is to relieve distributed applications the responsibility of implementing coordination services from scratch.__

---

# Design Goals

## ZooKeeper is simple

Unlike a typical file system, which is designed for storage, __ZooKeeper data is kept in-memory, which means ZooKeeper can acheive high throughput and low latency numbers.__

The ZooKeeper implementation puts a premium on high performance, highly avaliable, strictly ordered access. The performance aspects of ZooKeeper means it can be used in large, distributed systems. The reliability aspects keep it from being a single point of failure. The strict ordering means that sophisticated synchronization primitives can ben implemented at the client.

## ZooKeeper is replicated

![image](https://raw.githubusercontent.com/zozospider/note/master/distributed/ZooKeeper/ZooKeeper-Documentation-Overview-Welcome/ZooKeeper-Service.jpg)

The servers that make up the ZooKeeper service must all know about each other. They maintain an in-memory image of state, along with a transaction logs and snapshots in a persistent store. As long as the majority of the servers are avaliable, the ZooKeeper service will be avaliable.

Client connect to a single ZooKeper server. __The client maintains a TCP connection through which it sends requests, gets responses, gets watch events, and sends hart beats.__ If the TCP connection to server breaks, the client will connect to a diffrent server.

## ZooKeeper is ordered

__ZooKeeper stamps each update with a number that reflects the order of all ZooKeeper transactions.__

## ZooKeeper is fast

__It is especially fast in `read-dominant` workloads.__ ZooKeeper applications run on thousands of machines, and it performs best where reads are more common than writes, at radios of around 10:1.

---

# Data model and the hierarchical namespace

The namespace provided by ZooKeeper is much like that of a standard file system. __Evey node in ZooKeeper's namespace is identified by a path.__

![image](https://raw.githubusercontent.com/zozospider/note/master/distributed/ZooKeeper/ZooKeeper-Documentation-Overview-Welcome/ZooKeepers-Hierarchical-Namespace.jpg)

---

# Nodes and ephemeral nodes

Unlike is standard file systems, __each node in a ZooKeeper namespace can have data associated with it as well as children.__ It is like a file-system that allows a file to also be a directory. (__ZooKeeper was designed to store coordination data: status information, configuration, local infomation, etc., so the data stored at each node is usually small, in the byte to kilobyte range.__)

__Znodes maintain a stat structure that includes version numbers for data changes, ACL changes, and timestamps, to allow cache validations and coordinated updates. Each time a znode's data changes, the version number increases.__

__The data stored at each znode in a namespace is read and written atomically.__ Reads get all the data bytes associated with a znode and a write replaces all the data. __Each znode has an Access Control List (ACL) that restricts who can do what.__

__ZooKeeper also has the notion of ephemeral nodes. These znodes exists as long as the session that created the znode is active. When sessin ends the znode is deleted.__

---

# Conditional updates and watches

Clients can set a watch on a znodes. A watch will be triggered and removed when the znode changes. When a watch is triggered the client receives a packet saying that the znode has changed. And if the connection between the client and one of the Zookeeper servers broken, the client will recevie a local notification.

---

# Guarantees

ZooKeeper is very fast and very simple. Since its goal, though, is to be a basis of the constraction of more complicated services, such as synchronization, it provides a set of guarantees. These are:
- __Sequential Consistency__: Updates from a client will be applied in the order that they were sent.
- __Atomicity__: Updates either succeed or fail. No partial results.
- __Single System Image__: A client will see the same view of the service regardless of the server that it connects to.
- __Reliability__: Once an update has been applied, it will persist from that time forward until a client overwrites the update.
- __Timeliness__: the clients view of the system is guaranteed to be up-to-date within a certain time bound.

---

# Simple API

One of the design goals of ZooKeeper is provide a very simple programming interface. As a result, it supports only these operations:

- __create__: create a node at a location in the tree.
- __delete__: delete a node.
- __exists__: test if a node exists at a location.
- __get data__: reads the data from a node.
- __set data__: writes data to a node.
- __get children__: retrieves a list of children of a node.
- __sync__: waits for data to be propagated.

---

# Implementation

---

# Uses

---

# Performance

---

# Reliability

---

# The ZooKeeper Project

---
