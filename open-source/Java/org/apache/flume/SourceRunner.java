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

package org.apache.flume;

import org.apache.flume.lifecycle.LifecycleAware;
import org.apache.flume.source.EventDrivenSourceRunner;
import org.apache.flume.source.PollableSourceRunner;

/**
 * A source runner controls how a source is driven.
 * Source Runner 控制 Source 的驱动方式.
 *
 * This is an abstract class used for instantiating derived classes.
 * 这是一个抽象类, 用于实例化派生类.
 */
public abstract class SourceRunner implements LifecycleAware {

  private Source source;

  /**
   * Static factory method to instantiate a source runner implementation that
   * corresponds to the type of {@link Source} specified.
   * 静态工厂方法, 用于实例化与指定的 {@link Source} 类型对应的 Source Runner 实现.
   *
   * @param source The source to run
   * @param source 要运行的 Source
   * @return A runner that can run the specified source
   * @return 可以运行指定 Source 的 Runner
   * @throws IllegalArgumentException if the specified source does not implement
   * a supported derived interface of {@link SourceRunner}.
   * @throws IllegalArgumentException 如果指定的 Source 未实现 {@link SourceRunner} 支持的派生接口.
   */
  public static SourceRunner forSource(Source source) {
    SourceRunner runner = null;

    // 如果 source 为 PollableSource, 则新建 PollableSourceRunner 实例并设置它的 source 变量.
    // 如果 source 为 EventDrivenSource, 则新建 EventDrivenSourceRunner 实例并设置它的 source 变量.
    // 如果 source 不属于以上两者, 则抛出异常
    if (source instanceof PollableSource) {
      runner = new PollableSourceRunner();
      ((PollableSourceRunner) runner).setSource((PollableSource) source);
    } else if (source instanceof EventDrivenSource) {
      runner = new EventDrivenSourceRunner();
      ((EventDrivenSourceRunner) runner).setSource((EventDrivenSource) source);
    } else {
      throw new IllegalArgumentException("No known runner type for source "
          + source);
    }

    return runner;
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }

}
