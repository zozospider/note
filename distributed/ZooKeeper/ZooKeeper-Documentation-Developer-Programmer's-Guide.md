
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

### 2.1.2 Data Access

### 2.1.3 Ephemeral Nodes

### 2.1.4 Sequence Nodes -- Unique Naming

## 2.2 Time in ZooKeeper

## 2.3 ZooKeeper Stat Structure

---

# 三. ZooKeeper Sessions

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
