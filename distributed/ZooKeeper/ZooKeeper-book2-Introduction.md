
- [Document & Code](#document--code)
- [I. The ZooKeeper Mission](#i-the-zookeeper-mission)
    - [1.1 How the World Survived without ZooKeeper](#11-how-the-world-survived-without-zookeeper)
    - [1.2 What ZooKeeper Doesn’t Do](#12-what-zookeeper-doesnt-do)
    - [1.3 The Apache Project](#13-the-apache-project)
    - [1.4 Building Distributed Systems with ZooKeeper](#14-building-distributed-systems-with-zookeeper)
- [II. Example: Master-Worker Application](#ii-example-master-worker-application)
    - [2.1 Master Failures](#21-master-failures)
    - [2.2 Worker Failures](#22-worker-failures)
    - [2.3 Communication Failures](#23-communication-failures)
    - [2.4 Summary of Tasks](#24-summary-of-tasks)
- [III. Why Is Distributed Coordination Hard?](#iii-why-is-distributed-coordination-hard)
- [IV. ZooKeeper Is a Success, with Caveats](#iv-zookeeper-is-a-success-with-caveats)

---

# Document & Code

- page
  - [../Zookeeper-book2](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-book2.md)

---

In the past, each application was a single program running on a single computer with a single CPU. Today, things have changed. In the Big Data and Cloud Computing world, applications are made up of many independent programs running on an ever-changing set of computers.

Coordinating the actions of these independent programs is far more difficult than writing a single program to run on a single computer.

ZooKeeper was designed to be a robust service that enables application developers to focus mainly on their application logic rather than coordination.

When designing an application with ZooKeeper, one ideally separates application data from control or coordination data.

# I. The ZooKeeper Mission

## 1.1 How the World Survived without ZooKeeper

## 1.2 What ZooKeeper Doesn’t Do

## 1.3 The Apache Project

## 1.4 Building Distributed Systems with ZooKeeper

# II. Example: Master-Worker Application

## 2.1 Master Failures

## 2.2 Worker Failures

## 2.3 Communication Failures

## 2.4 Summary of Tasks

# III. Why Is Distributed Coordination Hard?

# IV. ZooKeeper Is a Success, with Caveats
