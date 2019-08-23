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

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

/**
 * Interceptor that extracts matches using a specified regular expression and
 * appends the matches to the event headers using the specified serializers</p>
 * Interceptor: 使用指定的正则表达式提取匹配项, 并使用指定的序列化程序将匹配项附加到 event headers</p>
 * Note that all regular expression matching occurs through Java's built in
 * java.util.regex package</p>. Properties:
 * <p>
 * 请注意, 所有正则表达式匹配都是通过 Java 内置 java.util.regex 包进行的</p>. Properties:
 * <p>
 * regex: The regex to use
 * <p>
 * regex: 要使用的正则表达式
 * <p>
 * serializers: Specifies the group the serializer will be applied to, and the
 * name of the header that will be added. If no serializer is specified for a
 * group the default {@link RegexExtractorInterceptorPassThroughSerializer} will
 * be used
 * <p>
 * serializers: 指定将应用序列化程序的组以及将添加的 header 的名称.
 * 如果没有为组指定序列化程序, 将使用默认的 {@link RegexExtractorInterceptorPassThroughSerializer}
 * <p>
 * Sample config:
 * <p>
 * agent.sources.r1.channels = c1
 * <p>
 * agent.sources.r1.type = SEQ
 * <p>
 * agent.sources.r1.interceptors = i1
 * <p>
 * agent.sources.r1.interceptors.i1.type = REGEX_EXTRACTOR
 * <p>
 * agent.sources.r1.interceptors.i1.regex = (WARNING)|(ERROR)|(FATAL)
 * <p>
 * agent.sources.r1.interceptors.i1.serializers = s1 s2
 * agent.sources.r1.interceptors.i1.serializers.s1.type = com.blah.SomeSerializer
 * agent.sources.r1.interceptors.i1.serializers.s1.name = warning
 * agent.sources.r1.interceptors.i1.serializers.s2.type =
 *     org.apache.flume.interceptor.RegexExtractorInterceptorTimestampSerializer
 * agent.sources.r1.interceptors.i1.serializers.s2.name = error
 * agent.sources.r1.interceptors.i1.serializers.s2.dateFormat = yyyy-MM-dd
 * </p>
 * <pre>
 * Example 1:
 * </p>
 * EventBody: 1:2:3.4foobar5</p> Configuration:
 * agent.sources.r1.interceptors.i1.regex = (\\d):(\\d):(\\d)
 * </p>
 * agent.sources.r1.interceptors.i1.serializers = s1 s2 s3
 * agent.sources.r1.interceptors.i1.serializers.s1.name = one
 * agent.sources.r1.interceptors.i1.serializers.s2.name = two
 * agent.sources.r1.interceptors.i1.serializers.s3.name = three
 * </p>
 * results in an event with the the following
 * 具有以下内容的 event 结果
 *
 * body: 1:2:3.4foobar5 headers: one=>1, two=>2, three=3
 *
 * Example 2:
 *
 * EventBody: 1:2:3.4foobar5
 *
 * Configuration: agent.sources.r1.interceptors.i1.regex = (\\d):(\\d):(\\d)
 * <p>
 * agent.sources.r1.interceptors.i1.serializers = s1 s2
 * agent.sources.r1.interceptors.i1.serializers.s1.name = one
 * agent.sources.r1.interceptors.i1.serializers.s2.name = two
 * <p>
 *
 * results in an event with the the following
 * 具有以下内容的 event 结果
 *
 * body: 1:2:3.4foobar5 headers: one=>1, two=>2
 * </pre>
 */
public class RegexExtractorInterceptor implements Interceptor {

  static final String REGEX = "regex";
  static final String SERIALIZERS = "serializers";

  private static final Logger logger = LoggerFactory
      .getLogger(RegexExtractorInterceptor.class);

  private final Pattern regex;
  private final List<NameAndSerializer> serializers;

  private RegexExtractorInterceptor(Pattern regex,
      List<NameAndSerializer> serializers) {
    this.regex = regex;
    this.serializers = serializers;
  }

  @Override
  public void initialize() {
    // NO-OP...
  }

  @Override
  public void close() {
    // NO-OP...
  }

  @Override
  public Event intercept(Event event) {
    Matcher matcher = regex.matcher(
        new String(event.getBody(), Charsets.UTF_8));
    // 获取 event 的 headers 属性
    Map<String, String> headers = event.getHeaders();
    // 通过正则表达式匹配 event, 将匹配到的多个匹配字段进行遍历
    // 然后将当前匹配索引对应配置的 NameAndSerializer (包含 name 和 Serializer 实现) 对应的 name 作为该 event header 的 key, 匹配到的字段内容作为该 event header 的 value, 设置到该 event 的 header
    if (matcher.find()) {
      for (int group = 0, count = matcher.groupCount(); group < count; group++) {
        int groupIndex = group + 1;
        if (groupIndex > serializers.size()) {
          if (logger.isDebugEnabled()) {
            logger.debug("Skipping group {} to {} due to missing serializer",
                group, count);
          }
          break;
        }
        NameAndSerializer serializer = serializers.get(group);
        if (logger.isDebugEnabled()) {
          logger.debug("Serializing {} using {}", serializer.headerName,
              serializer.serializer);
        }
        // Serializer 的 headerName 作为该 event header 的 key
        // 匹配到的字段内容作为该 event header 的 value (默认实现类的 serialize(v) 方法只是返回传入的 value 参数, 即匹配到的字段内容)
        headers.put(serializer.headerName,
            serializer.serializer.serialize(matcher.group(groupIndex)));
      }
    }
    return event;
  }

  @Override
  public List<Event> intercept(List<Event> events) {
    List<Event> intercepted = Lists.newArrayListWithCapacity(events.size());
    for (Event event : events) {
      Event interceptedEvent = intercept(event);
      if (interceptedEvent != null) {
        intercepted.add(interceptedEvent);
      }
    }
    return intercepted;
  }

  /**
   * 生成 RegexExtractorInterceptor 新实例的 Builder.
   */
  public static class Builder implements Interceptor.Builder {

    private Pattern regex;
    // NameAndSerializer (包含 name 和 Serializer 实现) 列表
    private List<NameAndSerializer> serializerList;
    private final RegexExtractorInterceptorSerializer defaultSerializer =
        new RegexExtractorInterceptorPassThroughSerializer();

    @Override
    public void configure(Context context) {
      String regexString = context.getString(REGEX);
      Preconditions.checkArgument(!StringUtils.isEmpty(regexString),
          "Must supply a valid regex string");
      regex = Pattern.compile(regexString);
      regex.pattern();
      regex.matcher("").groupCount();
      configureSerializers(context);
    }

    private void configureSerializers(Context context) {
      String serializerListStr = context.getString(SERIALIZERS);
      Preconditions.checkArgument(!StringUtils.isEmpty(serializerListStr),
          "Must supply at least one name and serializer");

      String[] serializerNames = serializerListStr.split("\\s+");

      Context serializerContexts =
          new Context(context.getSubProperties(SERIALIZERS + "."));

      serializerList = Lists.newArrayListWithCapacity(serializerNames.length);
      for (String serializerName : serializerNames) {
        Context serializerContext = new Context(
            serializerContexts.getSubProperties(serializerName + "."));
        // Must be default (org.apache.flume.interceptor.RegexExtractorInterceptorPassThroughSerializer), 
        // org.apache.flume.interceptor.RegexExtractorInterceptorMillisSerializer, or the FQCN of a custom class that implements org.apache.flume.interceptor.RegexExtractorInterceptorSerializer
        // 必须是 default (org.apache.flume.interceptor.RegexExtractorInterceptorPassThroughSerializer), 
        // org.apache.flume.interceptor.RegexExtractorInterceptorMillisSerializer, 或实现 org.apache.flume.interceptor.RegexExtractorInterceptorSerializer 的自定义类的 FQCN
        String type = serializerContext.getString("type", "DEFAULT");
        String name = serializerContext.getString("name");
        Preconditions.checkArgument(!StringUtils.isEmpty(name),
            "Supplied name cannot be empty.");

        // 默认使用 RegexExtractorInterceptorPassThroughSerializer 作为 RegexExtractorInterceptorSerializer 接口实现
        if ("DEFAULT".equals(type)) {
          serializerList.add(new NameAndSerializer(name, defaultSerializer));
        // 否则使用配置的自定义 Serializer, 通过反射创建自定义 Serializer 的实例
        } else {
          serializerList.add(new NameAndSerializer(name, getCustomSerializer(
              type, serializerContext)));
        }
      }
    }

    /**
     * 通过反射创建自定义 Serializer 的实例
     */
    private RegexExtractorInterceptorSerializer getCustomSerializer(
        String clazzName, Context context) {
      try {
        RegexExtractorInterceptorSerializer serializer = (RegexExtractorInterceptorSerializer) Class
            .forName(clazzName).newInstance();
        // 调用该自定义 Serializer 的 configure(e) 方法
        serializer.configure(context);
        return serializer;
      } catch (Exception e) {
        logger.error("Could not instantiate event serializer.", e);
        Throwables.propagate(e);
      }
      // 异常情况下返回默认实现
      return defaultSerializer;
    }

    @Override
    public Interceptor build() {
      Preconditions.checkArgument(regex != null,
          "Regex pattern was misconfigured");
      Preconditions.checkArgument(serializerList.size() > 0,
          "Must supply a valid group match id list");
      return new RegexExtractorInterceptor(regex, serializerList);
    }
  }

  static class NameAndSerializer {
    // headerName: 对应 event header 的 key
    private final String headerName;
    // serializer: 具体的 Serializer 实现, 用于序列化 event
    // 默认实现类的 serialize(v) 方法只是返回传入的 value 参数
    private final RegexExtractorInterceptorSerializer serializer;

    public NameAndSerializer(String headerName,
        RegexExtractorInterceptorSerializer serializer) {
      this.headerName = headerName;
      this.serializer = serializer;
    }
  }
}
