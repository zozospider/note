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

import org.apache.yetus.audience.InterfaceAudience;

import java.io.IOException;

/**
 * Interface that is implemented by generated classes.
 * 
 */
@InterfaceAudience.Public
public interface Record {
    /**
     * 序列化: 内存 -> 磁盘 / 网络 (通常为二进制数据)
     */
    public void serialize(OutputArchive archive, String tag)
        throws IOException;
    /**
     * 反序列化: 磁盘 / 网络 (通常为二进制数据) -> 内存
     */
    public void deserialize(InputArchive archive, String tag)
        throws IOException;
}
