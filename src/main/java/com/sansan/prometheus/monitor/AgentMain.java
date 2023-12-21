package com.sansan.prometheus.monitor;

import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

/**
 * Agent入口
 *
 * @author yanglujia
 * @date 2023/12/20 18:30:08
 */
public class AgentMain {
    private static final int MONITOR_PORT = Integer.parseInt(System.getProperty("monitor.port", "1111"));
    private static final int MONITOR_BACK_LOG = Integer.parseInt(System.getProperty("monitor.backlog", "1111"));
    private static final String MONITOR_URI = System.getProperty("monitor.uri", "/prometheus");
    private static final int MONITOR_WORKER_SIZE = Integer.parseInt(System.getProperty("monitor.worker.size", "1"));

    /**
     * 创建监控服务器
     *
     * @author yanglujia
     * @date 2023/12/20 18:38:33
     */
    private static void createMonitorServer() {
        try {
            final HttpServer httpServer = HttpServer.create(new InetSocketAddress(MONITOR_PORT), MONITOR_BACK_LOG);
            httpServer.setExecutor(Executors.newFixedThreadPool(MONITOR_WORKER_SIZE));
            httpServer.createContext(MONITOR_URI, context -> {
                final String scrape = PrometheusMonitor.scrape();
                final byte[] scrapeBytes = scrape.getBytes(StandardCharsets.UTF_8);

                context.sendResponseHeaders(200, scrapeBytes.length);

                try (OutputStream responseBody = context.getResponseBody()) {
                    responseBody.write(scrapeBytes);
                }
            });

            httpServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Agent入口
     *
     * @param agentArgs agentArgs
     * @param inst      inst
     * @author yanglujia
     * @date 2023/12/20 18:38:46
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        new Thread(AgentMain::createMonitorServer, "prometheus-monitor-thread").start();
    }
}
