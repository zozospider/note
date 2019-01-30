
# ZooKeeper

`ZooKeeper is a distributed, open-source coordination service for distributed applications.`

Coordination services are notoriously hard to get right. They are especially prone to errors such as race conditions and deadlock. `The motivation behind ZooKeeper is to relieve distributed applications the responsibility of implementing coordination services from scratch.`

# Design Goals

## ZooKeeper is simple

Unlike a typical file system, which is designed for storage, `ZooKeeper data is kept in-memory, which means ZooKeeper can acheive high throughput and low latency numbers.`

The ZooKeeper implementation puts a premium on high performance, highly avaliable, strictly ordered access. The performance aspects of ZooKeeper means it can be used in large, distributed systems. The reliability aspects keep it from being a single point of failure. The strict ordering means that sophisticated synchronization primitives can ben implemented at the client.

## ZooKeeper is replicated

![image](https://raw.githubusercontent.com/zozospider/note/master/distributed/ZooKeeper/ZooKeeper-Documentation-Overview-Welcome/ZooKeeper-is-replicated.jpg)

The servers that make up the ZooKeeper service must all know about each other. They maintain an in-memory image of state, along with a transaction logs and snaopshots in a persistent store. As long as the majority of the servers are avaliable, the ZooKeeper service will be avaliable.

Client connect to a single ZooKeper server. `The client maintains a TCP connection through which it sends requests, gets responses, gets watch events, and sends hart beats.` If the TCP connection to server breaks, the client will connect to a diffrent server.

## ZooKeeper is ordered

`ZooKeeper stamps each update with a number that reflects the order of all ZooKeeper transactions.`

## ZooKeeper is fast

`It is especially fast in read-dominant wokloads.` ZooKeeper applications run on thousands of machines, and it performs best where reads are more common than writes, at radios of around 10:1.

# Data model and the hierarchical namespace

# Nodes and ephemeral nodes

# Conditional updates and watches

# Guarantees

# Simple API

# Implementation

# Uses

# Performance

# Reliability

# The ZooKeeper Project
