
- [Document & Code](#document--code)
- [一. Introduction](#一-introduction)
- [二. The ZooKeeper Data Model](#二-the-zookeeper-data-model)
    - [2.1 ZNodes](#21-znodes)
        - [2.1.1 Watches](#211-watches)
        - [2.1.2 Data Access](#212-data-access)
        - [2.1.3 Ephemeral Nodes](#213-ephemeral-nodes)
        - [2.1.4 Sequence Nodes -- Unique Naming](#214-sequence-nodes----unique-naming)
    - [2.2 Time in ZooKeeper](#22-time-in-zookeeper)
    - [2.3 ZooKeeper Stat Structure](#23-zookeeper-stat-structure)
- [三. ZooKeeper Sessions](#三-zookeeper-sessions)
- [四. ZooKeeper Watches](#四-zookeeper-watches)
    - [4.1 Semantics of Watches](#41-semantics-of-watches)
    - [4.2 What ZooKeeper Guarantees about Watches](#42-what-zookeeper-guarantees-about-watches)
    - [4.3 Things to Remember about Watches](#43-things-to-remember-about-watches)
- [五. ZooKeeper access control using ACLs](#五-zookeeper-access-control-using-acls)
    - [5.1 ACL Permissions](#51-acl-permissions)
        - [5.1.1 Builtin ACL Schemes](#511-builtin-acl-schemes)
        - [5.1.2 ZooKeeper C client API](#512-zookeeper-c-client-api)
- [六. Pluggable ZooKeeper authentication](#六-pluggable-zookeeper-authentication)
- [七. Consistency Guarantees](#七-consistency-guarantees)
- [八. Bindings](#八-bindings)
    - [8.1 Java Binding](#81-java-binding)
    - [8.2 C Binding](#82-c-binding)
        - [8.2.1 Installation](#821-installation)
        - [8.2.2 Building Your Own C Client](#822-building-your-own-c-client)
- [九. Building Blocks: A Guide to ZooKeeper Operations](#九-building-blocks-a-guide-to-zookeeper-operations)
    - [9.1 Handling Errors](#91-handling-errors)
- [十. Gotchas: Common Problems and Troubleshooting](#十-gotchas-common-problems-and-troubleshooting)

---

# Document & Code

* [../Zookeeper-Documentation](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-Documentation.md)

---

# 一. Introduction

---

# 二. The ZooKeeper Data Model

__ZooKeeper has a hierarchal name space, much like a distributed file system. The only diffrence is that each node in the namespace can have data associated with it as well as children. It is like having a file system that allows a file to also be a directory.__ 

## 2.1 ZNodes

Every node in a ZooKeeper tree is refered to as a `znode`. Znodes maintain a stat structure that includes version numbers for data changes, acl changes. The stat structure also has timestamps. __Each time a znode's data changes, the version number increases. For instance, whenever a client retrieves data, it also receives the version of the data. And when a client performs an update or a delete, it must supply the version of the data of the znode it is changing. If the version it supplies doesn't match the actual version of the data, the update will fail.__

### 2.1.1 Watches

Clients can set watches on znodes. __Changes to that znode trigger the watch and then clear the watch. When a watch triggers, ZooKeeper sends the client a notification.__

### 2.1.2 Data Access

__The data stored at each znode in a namespace is read and writen atomically. Reads get all the data bytes associated with a znode and writes replaces all the data. Each znode has an Access Control List (ACL) that restricts who can do what.__

ZooKeeper was not designed to be a general database or large object store. Instead, it manages coordination data. A common property of the various forms of coordination data is that they are relatively small: measured in kilobytes. __If large data storage is needed, the usually pattern of dealing with such data is to store it on a bulk storage syste, such as NFS or HDFS, and store pointers to the storage locations in ZooKeeper.__

### 2.1.3 Ephemeral Nodes

ZooKeeper also has the notion of `ephemeral nodes`. __These nodes exists as long as the session that created the znode is active. When the session ends the znode is deleted. Because of this behavior ephemeral znodes are not allowed to have children.__

### 2.1.4 Sequence Nodes -- Unique Naming

__When creating a znode you can also request that ZooKeeper append a monotonically increasing counter to the end of the path. This counter is unique to the parent znode. The counter used to store the next sequence number is signed int (4 bytes) maintained by the parent node, the counter will overflow when incremented beyond 2147483647.__

## 2.2 Time in ZooKeeper

- __`Zxid`__: __Every change to the ZooKeeper state receives a stamp in the form of a `zxid` (ZooKeeper Transaction Id). This exposes the total ordering of all changes to ZooKeeper. Each change will have a unique zxid and if zxid1 is smaller than zxid2 then zxid1 is happened before zxid2.__
- __`Version numbers`__: Every change to a node will cause an increase to one of the version numbers of that node. The three version numbers are version (number of changes to the data of a znode), cversion (number of changes to the children of a znode), aversion (number of changes to the ACL of a znode).
- __`Ticks`__
- __`Real time`__

## 2.3 ZooKeeper Stat Structure

The Stat structure for each znode in ZooKeeper is made up of the following fields:
- __`czxid`__: The zxid of the change that caused this znode to be created.
- __`mzxid`__: The zxid of the change that last modified this znode.
- __`pzxid`__: The zxid of the change that last modified children of this znode.
- __`ctime`__: The time in milliseconds from epoch when this znode was created.
- __`mtime`__: The time in milliseconds from epoch when this znode was last modified.
- __`version`__: The number of changes to the data of this znode.
- __`cversion`__: The number of changes to the children of this znode.
- __`aversion`__: The number of changes to the ACL of this znode.
- __`ephemeralOwner`__: The session if the the owner of this znode if the znode is an ephemeral node. If it is not an ephemeral node, it will be zero.
- __`dataLength`__: The length of the data field of this znode.
- __`numChildren`__: The number of children of this znode.

---

# 三. ZooKeeper Sessions

A ZooKeeper client establishes a session with the ZooKeeper service by creating a handle to the service using a language binding. Once created, the handle starts of in the CONNECTING state and the client library tries to connect to the one of the servers that make up the ZooKeeper service at which point it switches to the CONNECTED state. During normal operation will be in one of these two states. If an unrecoverable error occurs, such as session expiration or authentication failure, or if the application explicitly closes the handle, the handle will move to the CLOSED state. The following figure shows the possible state transitions of a ZooKeeper client:

![image](https://raw.githubusercontent.com/zozospider/note/master/distributed/ZooKeeper/ZooKeeper-Documentation-Developer-Programmer's-Guide/state_dia.jpg)

To create a client session the application code must provide a connection string containing a comma separated list of host:port pairs, each corresponding to a ZooKeeper server. __The ZooKeeper client library will pick an arbitrary server and try to connect to it. If this connection fails, or if the client becomes disconnected from the server or any reason, the client will automatically try the next server in the list, until a connection is (re-)established.__

__When a client gets a handle to the ZooKeeper service, ZooKeeper creates a ZooKeeper session, represented as a 64-bit number, that it assigns to the client. If the client connects to a diffrent ZooKeeper server, it will send the session id as a part of the connection handshake. As a security measure, the server creates a password for the session id that any ZooKeeper server can validate. The password is sent to the client with the session id when the client establishes the session. The client sends this password with the session id whenever it reestablishes the session with a new server.__

When connectivity between the client and at least one of the servers is re-established, the session will either again transition to the `connected` state (if reconnected within timeout value) or it will transition to the `expired` state (if reconnected after the session timeout). __It is not advisable to create a new session object (a new ZooKeeper.class or zookeeper handle in the c binding) for disconnection. The ZK client library will handle reconnect for you. Only create a new session when you are notified of session expiration (mandatory).__

__Session expiration is managed by the ZooKeeper cluster itself, not by the client. When the ZK client establishes a session with the cluster it provides a `timeout` value detailed above. This value is used by the cluster to determine when the client's session expires. Expirations happens when the cluster does not hear from the client with the specified session timeout period (i.e. no heartbeat). At session expiration the cluster will delete any/all emphameral nodes owned by that session and immediately notify any/all connected clients of the change (anyone watching those znodes).__ At this point the client of the expired session is still disconnected from the cluster, it will not be nofified of the session expiration until/unless it is able to re-establish a connection to the cluster. __The client will stay in disconnected state until TCP connection is re-established with the cluster, at which point the watcher of the expired session will receive the `session expired` notification.__

__The session is kept alive by requests sent by the client. If the session is idle for a period of time that would timeout the session, the client will send a PING request to keep the session alive.__

---

# 四. ZooKeeper Watches

## 4.1 Semantics of Watches

## 4.2 What ZooKeeper Guarantees about Watches

## 4.3 Things to Remember about Watches

---

# 五. ZooKeeper access control using ACLs

## 5.1 ACL Permissions

### 5.1.1 Builtin ACL Schemes

### 5.1.2 ZooKeeper C client API

---

# 六. Pluggable ZooKeeper authentication

---

# 七. Consistency Guarantees

---

# 八. Bindings

## 8.1 Java Binding

## 8.2 C Binding

### 8.2.1 Installation

### 8.2.2 Building Your Own C Client

---

# 九. Building Blocks: A Guide to ZooKeeper Operations

## 9.1 Handling Errors

---

# 十. Gotchas: Common Problems and Troubleshooting

---
