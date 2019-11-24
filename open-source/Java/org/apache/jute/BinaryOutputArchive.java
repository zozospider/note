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

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.TreeMap;

/**
 * OutputArchive 输出流 serializers (序列化) 实现类: 内存数据 -> 磁盘 / 网络 (二进制数据等).
 */
public class BinaryOutputArchive implements OutputArchive {
    private ByteBuffer bb = ByteBuffer.allocate(1024);

    private DataOutput out;
    
    public static BinaryOutputArchive getArchive(OutputStream strm) {
        return new BinaryOutputArchive(new DataOutputStream(strm));
    }
    
    /** Creates a new instance of BinaryOutputArchive */
    public BinaryOutputArchive(DataOutput out) {
        this.out = out;
    }
    
    public void writeByte(byte b, String tag) throws IOException {
        out.writeByte(b);
    }
    
    public void writeBool(boolean b, String tag) throws IOException {
        out.writeBoolean(b);
    }
    
    public void writeInt(int i, String tag) throws IOException {
        out.writeInt(i);
    }
    
    public void writeLong(long l, String tag) throws IOException {
        out.writeLong(l);
    }
    
    public void writeFloat(float f, String tag) throws IOException {
        out.writeFloat(f);
    }
    
    public void writeDouble(double d, String tag) throws IOException {
        out.writeDouble(d);
    }
    
    /**
     * create our own char encoder to utf8. This is faster 
     * then string.getbytes(UTF8).
     * 为 utf8 创建我们自己的 char encoder (编码器). 这比 string.getbytes (UTF8) 更快.
     * @param s the string to encode into utf8
     * @param s encode (编码) 为 utf8 的字符串
     * @return utf8 byte sequence.
     * @return utf8 字节序列.
     */
    final private ByteBuffer stringToByteBuffer(CharSequence s) {
        bb.clear();
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            if (bb.remaining() < 3) {
                ByteBuffer n = ByteBuffer.allocate(bb.capacity() << 1);
                bb.flip();
                n.put(bb);
                bb = n;
            }
            char c = s.charAt(i);
            if (c < 0x80) {
                bb.put((byte) c);
            } else if (c < 0x800) {
                bb.put((byte) (0xc0 | (c >> 6)));
                bb.put((byte) (0x80 | (c & 0x3f)));
            } else {
                bb.put((byte) (0xe0 | (c >> 12)));
                bb.put((byte) (0x80 | ((c >> 6) & 0x3f)));
                bb.put((byte) (0x80 | (c & 0x3f)));
            }
        }
        bb.flip();
        return bb;
    }

    /**
     * 往磁盘 / 网络 (二进制数据等) 中序列化 (输出 / 写) 1 个 String
     */
    public void writeString(String s, String tag) throws IOException {
        // 如果字符串为 null, 则往输出流中写入 -1 长度
        if (s == null) {
            writeInt(-1, "len");
            return;
        }
        // 调用自身编码器 (而非 string.getbytes), 将字符串转换成 ByteBuffer
        ByteBuffer bb = stringToByteBuffer(s);
        // 先往输出流中写入该字符串长度
        writeInt(bb.remaining(), "len");
        // 再往输出流中写入字符串转换后的字节数组
        out.write(bb.array(), bb.position(), bb.limit());
    }

    /**
     * 往磁盘 / 网络 (二进制数据等) 中序列化 (输出 / 写) 1 个 byte[]
     */
    public void writeBuffer(byte barr[], String tag)
    throws IOException {
        // 如果 byte[] 为 null, 则往输出流中写入 -1 长度
    	if (barr == null) {
    		out.writeInt(-1);
    		return;
    	}
        // 先往输出流中写入该 byte[] 长度
    	out.writeInt(barr.length);
        // 再往输出流中写入 byte[]
        out.write(barr);
    }
    
    /**
     * 往磁盘 / 网络 (二进制数据等) 中序列化 (输出 / 写) 1 个 Record
     */
    public void writeRecord(Record r, String tag) throws IOException {
        // 调用此 Record 的 serialize 序列化: 内存 -> 磁盘 / 网络 (二进制数据等)
        r.serialize(this, tag);
    }
    
    public void startRecord(Record r, String tag) throws IOException {}
    
    public void endRecord(Record r, String tag) throws IOException {}
    
    /**
     * 开始序列化 (输出 / 写) Vector (List)
     */
    public void startVector(List v, String tag) throws IOException {
        // 如果字符串为空, 则往输出流中写入 -1 长度
    	if (v == null) {
    		writeInt(-1, tag);
    		return;
    	}
        // 先往输出流中写入该 Vector (List) 大小
        writeInt(v.size(), tag);
    }
    
    public void endVector(List v, String tag) throws IOException {}
    
    /**
     * 开始序列化 (输出 / 写) Map (TreeMap)
     */
    public void startMap(TreeMap v, String tag) throws IOException {
        // 先往输出流中写入该 Map (TreeMap) 大小
        writeInt(v.size(), tag);
    }
    
    public void endMap(TreeMap v, String tag) throws IOException {}
    
}
