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
import java.util.List;
import java.util.TreeMap;

/**
 * Interface that alll the serializers have to implement.
 * 所有 serializers (序列化) 都必须实现的接口: 内存数据 -> 磁盘 / 网络 (通常为二进制数据).
 *
 */
public interface OutputArchive {
    // 往磁盘 / 网络 (通常为二进制数据) 中序列化 (输出 / 写) 1 个 byte
    public void writeByte(byte b, String tag) throws IOException;
    // 往磁盘 / 网络 (通常为二进制数据) 中序列化 (输出 / 写) 1 个 boolean
    public void writeBool(boolean b, String tag) throws IOException;
    // 往磁盘 / 网络 (通常为二进制数据) 中序列化 (输出 / 写) 1 个 int
    public void writeInt(int i, String tag) throws IOException;
    // 往磁盘 / 网络 (通常为二进制数据) 中序列化 (输出 / 写) 1 个 bylongte
    public void writeLong(long l, String tag) throws IOException;
    // 往磁盘 / 网络 (通常为二进制数据) 中序列化 (输出 / 写) 1 个 float
    public void writeFloat(float f, String tag) throws IOException;
    // 往磁盘 / 网络 (通常为二进制数据) 中序列化 (输出 / 写) 1 个 double
    public void writeDouble(double d, String tag) throws IOException;
    // 往磁盘 / 网络 (通常为二进制数据) 中序列化 (输出 / 写) 1 个 String
    public void writeString(String s, String tag) throws IOException;
    // 往磁盘 / 网络 (通常为二进制数据) 中序列化 (输出 / 写) 1 个 byte[]
    public void writeBuffer(byte buf[], String tag)
        throws IOException;
    // 往磁盘 / 网络 (通常为二进制数据) 中序列化 (输出 / 写) 1 个 Record
    public void writeRecord(Record r, String tag) throws IOException;
    // 开始序列化 (输出 / 写) Record
    public void startRecord(Record r, String tag) throws IOException;
    // 结束序列化 (输出 / 写) Record
    public void endRecord(Record r, String tag) throws IOException;
    // 开始序列化 (输出 / 写) Vector (List)
    public void startVector(List v, String tag) throws IOException;
    // 结束序列化 (输出 / 写) Vector (List)
    public void endVector(List v, String tag) throws IOException;
    // 开始序列化 (输出 / 写) Map (TreeMap)
    public void startMap(TreeMap v, String tag) throws IOException;
    // 开始序列化 (输出 / 写) Map (TreeMap)
    public void endMap(TreeMap v, String tag) throws IOException;

}
