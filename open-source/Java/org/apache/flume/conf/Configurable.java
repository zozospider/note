/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.flume.conf;

import org.apache.flume.Context;
import org.apache.flume.annotations.InterfaceAudience;
import org.apache.flume.annotations.InterfaceStability;

/**
 * <p>
 * Any class marked as Configurable may have a context including its
 * sub-configuration passed to it, requesting it configure itself.
 * </p>
 * <p>
 * 任何标记为 Configurable 的类都可能具有上下文, 包括传递给它的子配置, 请求它自己配置.
 * </P>
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public interface Configurable {
  /**
   * <p>
   * Request the implementing class to (re)configure itself.
   * </p>
   * <p>
   * 请求实现类重新配置自己.
   * </p>
   * <p>
   * When configuration parameters are changed, they must be
   * reflected by the component asap.
   * </p>
   * <p>
   * 当配置参数更改时, 组件必须尽快反映这些更改的参数.
   * </p>
   * <p>
   * There are no thread safety guarantees on when configure might be called.
   * </p>
   * <p>
   * 没有线程安全保证何时调用该方法.
   * </p>
   * @param context
   */
  public void configure(Context context);

}
