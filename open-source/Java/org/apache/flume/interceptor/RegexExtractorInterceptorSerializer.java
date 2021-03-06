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
package org.apache.flume.interceptor;

import org.apache.flume.conf.Configurable;
import org.apache.flume.conf.ConfigurableComponent;

/**
 * Serializer for serializing groups matched by the
 * {@link RegexExtractorInterceptor}
 * 用于序列化组的 Serializer, 由 {@link RegexExtractorInterceptor} 匹配
 */
public interface RegexExtractorInterceptorSerializer extends Configurable,
    ConfigurableComponent {

  /**
   * @param value
   *          The value extracted by the {@link RegexExtractorInterceptor}
   * @param value
   *          {@link RegexExtractorInterceptor} 提取的值
   * @return The serialized version of the specified value
   * @return 指定值的 Serializer 版本
   */
  String serialize(String value);

}
