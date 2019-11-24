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

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * InputArchive 输入流 Deserializers (反序列化) 实现类: 二进制数据 -> 内存数据.
 */
public class BinaryInputArchive implements InputArchive {
    static public final String UNREASONBLE_LENGTH= "Unreasonable length = ";
    private DataInput in;
    
    static public BinaryInputArchive getArchive(InputStream strm) {
        return new BinaryInputArchive(new DataInputStream(strm));
    }
    
    static private class BinaryIndex implements Index {
        private int nelems;
        BinaryIndex(int nelems) {
            this.nelems = nelems;
        }
        public boolean done() {
            return (nelems <= 0);
        }
        public void incr() {
            nelems--;
        }
    }
    /** Creates a new instance of BinaryInputArchive */
    public BinaryInputArchive(DataInput in) {
        this.in = in;
    }
    
    public byte readByte(String tag) throws IOException {
        return in.readByte();
    }
    
    public boolean readBool(String tag) throws IOException {
        return in.readBoolean();
    }
    
    public int readInt(String tag) throws IOException {
        return in.readInt();
    }
    
    public long readLong(String tag) throws IOException {
        return in.readLong();
    }
    
    public float readFloat(String tag) throws IOException {
        return in.readFloat();
    }
    
    public double readDouble(String tag) throws IOException {
        return in.readDouble();
    }
    
    /**
     * 从输入流 - 磁盘 / 网络 (二进制数据等) 中反序列化 (输入 / 读) 1 个 String
     */
    public String readString(String tag) throws IOException {
        // 先从输入流读取该字符串长度
    	int len = in.readInt();
        // 如果长度为 -1, 则返回 null
    	if (len == -1) return null;
        // 检查长度
        checkLength(len);
        // 新建 1 个此长度的 byte[] 缓冲区
    	byte b[] = new byte[len];
        // 再从输入流读取此长度的数据到 byte[] 中
    	in.readFully(b);
        // 返回读取到的 byte[] 转换成的字符串
    	return new String(b, "UTF8");
    }
    
    static public final int maxBuffer = Integer.getInteger("jute.maxbuffer", 0xfffff);

    /**
     * 从磁盘 / 网络 (二进制数据等) 中反序列化 (输入 / 读) 1 个 byte[]
     */
    public byte[] readBuffer(String tag) throws IOException {
        // 先从输入流读取该 byte[] 长度
        int len = readInt(tag);
        // 如果长度为 -1, 则返回 null
        if (len == -1) return null;
        // 检查长度
        checkLength(len);
        // 新建 1 个此长度的 byte[] 缓冲区
        byte[] arr = new byte[len];
        // 再从输入流读取此长度的数据到 byte[] 中
        in.readFully(arr);
        // 返回读取到到 byte[]
        return arr;
    }
    
    /**
     * 从磁盘 / 网络 (二进制数据等) 中反序列化 (输入 / 读) 1 个 Record
     */
    public void readRecord(Record r, String tag) throws IOException {
        // 调用此 Record 的 deserialize 反序列化: 磁盘 / 网络 (二进制数据等) -> 内存
        r.deserialize(this, tag);
    }
    
    public void startRecord(String tag) throws IOException {}
    
    public void endRecord(String tag) throws IOException {}
    
    /**
     * 开始反序列化 (输入 / 读) Vector (List)
     */
    public Index startVector(String tag) throws IOException {
        // 先从输入流读取该 Vector (List) 大小
        int len = readInt(tag);
        // 如果长度为 -1, 则返回 null
        if (len == -1) {
        	return null;
        }
        // 返回此长度构造的 BinaryIndex 对象
		return new BinaryIndex(len);
    }
    
    public void endVector(String tag) throws IOException {}
    
    /**
     * 开始反序列化 (输入 / 读) Map (TreeMap)
     */
    public Index startMap(String tag) throws IOException {
        // 先从输入流读取该 Map (TreeMap) 大小
        // 并返回此长度构造的 BinaryIndex 对象
        return new BinaryIndex(readInt(tag));
    }
    
    public void endMap(String tag) throws IOException {}

    // Since this is a rough sanity check, add some padding to maxBuffer to
    // make up for extra fields, etc. (otherwise e.g. clients may be able to
    // write buffers larger than we can read from disk!)
    // 由于这是一项粗略的健全性检查, 因此请在 maxBuffer 中添加一些填充以弥补额外的字段等.
    // (否则, 例如, 客户端可能能够写入比我们从磁盘读取的缓冲区大的缓冲区!)
    private void checkLength(int len) throws IOException {
        if (len < 0 || len > maxBuffer + 1024) {
            throw new IOException(UNREASONBLE_LENGTH + len);
        }
    }
}
