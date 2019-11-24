/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jute;

import java.io.IOException;

/**
 * Interface that all the Deserializers have to implement.
 * 所有 Deserializers (反序列化) 都必须实现的接口: 内存数据 <- 磁盘 / 网络 (通常为二进制数据).
 *
 */
public interface InputArchive {
    // 从磁盘 / 网络 (通常为二进制数据) 中反序列化 (输入 / 读) 1 个 byte
    public byte readByte(String tag) throws IOException;
    // 从磁盘 / 网络 (通常为二进制数据) 中反序列化 (输入 / 读) 1 个 boolean
    public boolean readBool(String tag) throws IOException;
    // 从磁盘 / 网络 (通常为二进制数据) 中反序列化 (输入 / 读) 1 个 int
    public int readInt(String tag) throws IOException;
    // 从磁盘 / 网络 (通常为二进制数据) 中反序列化 (输入 / 读) 1 个 long
    public long readLong(String tag) throws IOException;
    // 从磁盘 / 网络 (通常为二进制数据) 中反序列化 (输入 / 读) 1 个 float
    public float readFloat(String tag) throws IOException;
    // 从磁盘 / 网络 (通常为二进制数据) 中反序列化 (输入 / 读) 1 个 double
    public double readDouble(String tag) throws IOException;
    // 从磁盘 / 网络 (通常为二进制数据) 中反序列化 (输入 / 读) 1 个 String
    public String readString(String tag) throws IOException;
    // 从磁盘 / 网络 (通常为二进制数据) 中反序列化 (输入 / 读) 1 个 byte[]
    public byte[] readBuffer(String tag) throws IOException;
    // 从磁盘 / 网络 (通常为二进制数据) 中反序列化 (输入 / 读) 1 个 Record
    public void readRecord(Record r, String tag) throws IOException;
    // 开始反序列化 (输入 / 读) Record
    public void startRecord(String tag) throws IOException;
    // 结束反序列化 (输入 / 读) Record
    public void endRecord(String tag) throws IOException;
    // 开始反序列化 (输入 / 读) Vector (List)
    public Index startVector(String tag) throws IOException;
    // 结束反序列化 (输入 / 读) Vector (List)
    public void endVector(String tag) throws IOException;
    // 开始反序列化 (输入 / 读) Map (TreeMap)
    public Index startMap(String tag) throws IOException;
    // 结束反序列化 (输入 / 读) Map (TreeMap)
    public void endMap(String tag) throws IOException;
}
