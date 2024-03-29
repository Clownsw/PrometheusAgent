package com.sansan.prometheus.monitor;

import io.github.mweirauch.micrometer.jvm.extras.ProcessMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.*;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

/**
 * 普罗米修斯监控
 *
 * @author yanglujia
 * @date 2023/12/20 18:30:08
 */
@SuppressWarnings("unused")
public final class PrometheusMonitor {
    private PrometheusMonitor() {
    }

    private static final PrometheusMeterRegistry PROMETHEUS_METER_REGISTRY = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    private static final UptimeMetrics UPTIME_METRICS = new UptimeMetrics();
    private static final FileDescriptorMetrics FILE_DESCRIPTOR_METRICS = new FileDescriptorMetrics();
    private static final ProcessorMetrics PROCESSOR_METRICS = new ProcessorMetrics();
    private static final ClassLoaderMetrics CLASS_LOADER_METRICS = new ClassLoaderMetrics();
    private static final JvmMemoryMetrics JVM_MEMORY_METRICS = new JvmMemoryMetrics();
    private static final JvmHeapPressureMetrics JVM_HEAP_PRESSURE_METRICS = new JvmHeapPressureMetrics();
    private static final JvmCompilationMetrics JVM_COMPILATION_METRICS = new JvmCompilationMetrics();
    private static final JvmGcMetrics JVM_GC_METRICS = new JvmGcMetrics();
    private static final JvmInfoMetrics JVM_INFO_METRICS = new JvmInfoMetrics();
    private static final JvmThreadMetrics JVM_THREAD_METRICS = new JvmThreadMetrics();
    private static final ProcessMemoryMetrics PROCESS_MEMORY_METRICS = new ProcessMemoryMetrics();

    static {
        UPTIME_METRICS.bindTo(PROMETHEUS_METER_REGISTRY);
        FILE_DESCRIPTOR_METRICS.bindTo(PROMETHEUS_METER_REGISTRY);
        PROCESSOR_METRICS.bindTo(PROMETHEUS_METER_REGISTRY);
        CLASS_LOADER_METRICS.bindTo(PROMETHEUS_METER_REGISTRY);
        JVM_MEMORY_METRICS.bindTo(PROMETHEUS_METER_REGISTRY);
        JVM_HEAP_PRESSURE_METRICS.bindTo(PROMETHEUS_METER_REGISTRY);
        JVM_COMPILATION_METRICS.bindTo(PROMETHEUS_METER_REGISTRY);
        JVM_GC_METRICS.bindTo(PROMETHEUS_METER_REGISTRY);
        JVM_INFO_METRICS.bindTo(PROMETHEUS_METER_REGISTRY);
        JVM_THREAD_METRICS.bindTo(PROMETHEUS_METER_REGISTRY);
        PROCESS_MEMORY_METRICS.bindTo(PROMETHEUS_METER_REGISTRY);

        try {
            final LogbackMetrics logbackMetrics = new LogbackMetrics();
            logbackMetrics.bindTo(PROMETHEUS_METER_REGISTRY);
        } catch (Throwable ignore) {
        }
    }


    public static String scrape() {
        return PROMETHEUS_METER_REGISTRY.scrape();
    }
}