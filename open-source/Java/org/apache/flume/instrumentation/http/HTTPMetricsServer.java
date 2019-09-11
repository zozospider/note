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

package org.apache.flume.instrumentation.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.flume.Context;
import org.apache.flume.instrumentation.MonitorService;
import org.apache.flume.instrumentation.util.JMXPollUtil;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Monitor service implementation that runs a web server on a configurable
 * port and returns the metrics for components in JSON format. <p> Optional
 * parameters: <p> <tt>port</tt> : The port on which the server should listen
 * to.<p> Returns metrics in the following format: <p>
 * Monitor 服务实现, 在可配置端口上运行 Web 服务器, 并以 JSON 格式返回组件的度量标准.
 * <p>可选参数: <p> <tt>port</tt> : 服务器应侦听的端口.<p>以下列格式返回 metrics: <p>
 *
 * {<p> "componentName1":{"metric1" : "metricValue1","metric2":"metricValue2"}
 * <p> "componentName1":{"metric3" : "metricValue3","metric4":"metricValue4"}
 * <p> }
 */
public class HTTPMetricsServer implements MonitorService {

  private Server jettyServer;
  private int port;
  private static Logger LOG = LoggerFactory.getLogger(HTTPMetricsServer.class);
  public static int DEFAULT_PORT = 41414;
  public static String CONFIG_PORT = "port";

  /**
   * 实现 MonitorService 接口的 start() 方法
   * 使用 Jetty 启动一个 HTTP 服务, 端口默认为 41414
   */
  @Override
  public void start() {
    jettyServer = new Server();
    //We can use Contexts etc if we have many urls to handle. For one url,
    //specifying a handler directly is the most efficient.
    // 如果我们有许多网址要处理, 我们可以使用上下文等. 对于一个 URL, 直接指定处理程序是最有效的.
    HttpConfiguration httpConfiguration = new HttpConfiguration();
    ServerConnector connector = new ServerConnector(jettyServer,
        new HttpConnectionFactory(httpConfiguration));
    connector.setReuseAddress(true);
    connector.setPort(port);
    jettyServer.addConnector(connector);
    jettyServer.setHandler(new HTTPMetricsHandler());
    try {
      jettyServer.start();
      while (!jettyServer.isStarted()) {
        Thread.sleep(500);
      }
    } catch (Exception ex) {
      LOG.error("Error starting Jetty. JSON Metrics may not be available.", ex);
    }

  }

  /**
   * 实现 MonitorService 接口的 stop() 方法
   * 停止当前的 Jetty HTTP 服务
   */
  @Override
  public void stop() {
    try {
      jettyServer.stop();
      jettyServer.join();
    } catch (Exception ex) {
      LOG.error("Error stopping Jetty. JSON Metrics may not be available.", ex);
    }

  }

  /**
   * 实现 Configurable 接口的 configure(c) 方法
   * 获取上下文中配置的端口, 默认为 41414
   */
  @Override
  public void configure(Context context) {
    port = context.getInteger(CONFIG_PORT, DEFAULT_PORT);
  }

  /**
   * Http 接收请求逻辑
   */
  private class HTTPMetricsHandler extends AbstractHandler {

    Type mapType = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
    Gson gson = new Gson();

    /**
     * 获取所有实现了 MBeans 接口的实现对象的相关数据, 将其转换为 JSON 格式并返回.
     */
    @Override
    public void handle(String target, Request r1,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
      // /metrics is the only place to pull metrics.
      //If we want to use any other url for something else, we should make sure
      //that for metrics only /metrics is used to prevent backward
      //compatibility issues.
      // /metrics 是唯一可以提取 metrics 的地方.
      // 如果我们想要将任何其他 URL 用于其他内容, 则应确保仅对 metrics 使用 /metrics 来防止向后兼容性问题.
      if (request.getMethod().equalsIgnoreCase("TRACE") ||
          request.getMethod().equalsIgnoreCase("OPTIONS")) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        response.flushBuffer();
        ((Request) request).setHandled(true);
        return;
      }
      if (target.equals("/")) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("For Flume metrics please click"
                + " <a href = \"./metrics\"> here</a>.");
        response.flushBuffer();
        ((Request) request).setHandled(true);
        return;
      // 请求路径必须为 /metrics
      } else if (target.equalsIgnoreCase("/metrics")) {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        // 获取所有实现了 MBeans 接口的实现对象的相关数据
        Map<String, Map<String, String>> metricsMap = JMXPollUtil.getAllMBeans();
        // 转换为 JSON 格式
        String json = gson.toJson(metricsMap, mapType);
        // 返回
        response.getWriter().write(json);
        response.flushBuffer();
        ((Request) request).setHandled(true);
        return;
      }
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      response.flushBuffer();
      //Not handling the request returns a Not found error page.
      // 不处理请求会返回 Not found 错误页面.
    }

  }
}
