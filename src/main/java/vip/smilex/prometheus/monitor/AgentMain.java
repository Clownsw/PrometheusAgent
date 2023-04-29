package vip.smilex.prometheus.monitor;

import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @author smilex
 * @date 2023/4/29/11:57
 */
public class AgentMain {
    private static final int MONITOR_PORT = Integer.parseInt(System.getProperty("monitor.port", "1111"));
    private static final int MONITOR_BACK_LOG = Integer.parseInt(System.getProperty("monitor.backlog", "1111"));
    private static final String MONITOR_URI = System.getProperty("monitor.uri", "/prometheus");

    private static void createMonitorServer() {
        try {
            final HttpServer httpServer = HttpServer.create(new InetSocketAddress(MONITOR_PORT), MONITOR_BACK_LOG);
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

    public static void premain(String agentArgs, Instrumentation inst) {
        new Thread(AgentMain::createMonitorServer, "prometheus-monitor-thread").start();
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        premain(agentArgs, inst);
    }
}
