
- [一 序列化概述](#一-序列化概述)
    - [1.1 什么是序列化和反序列化](#11-什么是序列化和反序列化)
    - [1.2 为什么要序列化](#12-为什么要序列化)
    - [1.3 为什么不用 Java 的序列化](#13-为什么不用-java-的序列化)
    - [1.4 Hadoop 序列化特点:](#14-hadoop-序列化特点)
- [二 自定义 bean 对象实现序列化接口 (Writable)](#二-自定义-bean-对象实现序列化接口-writable)
- [三 序列化案例实操](#三-序列化案例实操)
    - [3.1 代码和本地运行](#31-代码和本地运行)

---

# 一 序列化概述

## 1.1 什么是序列化和反序列化

- 序列化就是把内存中的对象转换成字节序列 (或其他传输协议) 以便存储到磁盘或网络传输.
- 反序列化就是将从磁盘或网络传输收到的字节序列 (或其他传输协议) 转换成内存中到对象.

## 1.2 为什么要序列化

一般来说, _活的_ 对象只生存在内存中, 无法持久化. 且 _活的_ 对象只能由本地的进程使用, 不能被发送到网络上到另一台计算机. 而序列化可以存储 _活的_ 对象, 可以将 _活的_ 对象发送到远程计算机.

## 1.3 为什么不用 Java 的序列化

Java 的序列化是一个重量级序列化框架 (Serializable), 一个对象被序列化后, 会附带很多额外的信息 (校验信息, Header, 继承体系等), 不便于在网络中高效的传输. 所以 Hadoop 开发了一套序列化机制 (Writable).

## 1.4 Hadoop 序列化特点:

- 紧凑: 高效使用存储空间.
- 快速: 读写数据的额外开销小.
- 可扩展: 随着通信协议的升级而升级.
- 互操作: 支持多语言的交互.

---

# 二 自定义 bean 对象实现序列化接口 (Writable)

- 必须实现 `Writable` 接口

```java
/**
 * 实现 Writable 接口
 */
public class FlowWritable implements Writable {

}
```

- 必须有空参构造方法 (因为反序列化时, 需要反射调用空参构造方法)

```java
    /**
     * 空构造方法, 必须实现
     */
    public FlowWritable() {
        super();
    }
```

- 重写序列化方法和反序列化方法, 且反序列化的顺序和序列化的顺序一致.

```java
    /**
     * 序列化
     */
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(upFlow);
        out.writeLong(downFlow);
        out.writeLong(sumFlow);
    }

    /**
     * 反序列化 (和序列化顺序一致)
     */
    @Override
    public void readFields(DataInput in) throws IOException {
        upFlow = in.readLong();
        downFlow = in.readLong();
        sumFlow = in.readLong();
    }
```

- 要想把最终结果输出到文件中, 需要重写 `toString()` 方法.

```java
    /**
     * 最终结果输出到文件中的格式
     */
    @Override
    public String toString() {
        return "FlowWritable{" +
                "upFlow=" + upFlow +
                ", downFlow=" + downFlow +
                ", sumFlow=" + sumFlow +
                '}';
    }
```

- 如果需要将自定义的 bean 放在 key 中传输, 则还需要实现 `Comparable` 接口, 因为 MapReduce 框架中的 Shuffle 过程要求 key 必须能被排序

```java
    /**
     * 如果需要将自定义的 bean 放在 key 中传输, 需要实现 `Comparable` 接口
     * @param o 被比较的对象
     * @return 比较结果
     */
    @Override
    public int compareTo(FlowWritable o) {
        return this.sumFlow > o.getSumFlow() ? -1 : 1;
    }
```

---

# 三 序列化案例实操

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E5%BA%8F%E5%88%97%E5%8C%96/%E5%BA%8F%E5%88%97%E5%8C%96%E6%A1%88%E4%BE%8B%E5%88%86%E6%9E%90.png?raw=true)

## 3.1 代码和本地运行

参考以下项目:

- code
  - [zozospider/note-hadoop-video1](https://github.com/zozospider/note-hadoop-video1)

---
