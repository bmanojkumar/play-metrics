package com.phenom.metrics;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.google.inject.Inject;
import play.Configuration;
import play.Logger;
import play.inject.ApplicationLifecycle;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MetricsImpl implements Metrics {

    private static Logger.ALogger logger = Log.Logger;

    @Inject
    MetricsImpl(ApplicationLifecycle lifecycle, Configuration configuration) {
        logger.debug("Metrics Plugin Initializing");
        String HOST_NAME = execCommandAndReadToString("hostname");
        if(HOST_NAME.length() == 0) HOST_NAME = "HOST_NAME_NOT_FOUND";
        HOST_NAME = HOST_NAME.replace(".", "_");
        final String GRAPHITE_HOST = configuration.getString("metrics.graphiteHost");
        final int GRAPHITE_PORT = configuration.getInt("metrics.graphitePort");
        final String SERVICE_NAME = configuration.getString("metrics.serviceName");
        final int PING_INTERVAL = configuration.getInt("metrics.pingInterval");
        final MetricRegistry registry = SharedMetricRegistries.getOrCreate("phenommetrics");
        final Graphite graphite = new Graphite(new InetSocketAddress(GRAPHITE_HOST, GRAPHITE_PORT));
        final GraphiteReporter graphiteReporter = GraphiteReporter.forRegistry(registry)
                .prefixedWith(SERVICE_NAME+"."+HOST_NAME+".")
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);
        graphiteReporter.start(PING_INTERVAL, TimeUnit.SECONDS);
        logger.info("Phenom Metrics Graphite Reporter started -- " + GRAPHITE_HOST + ":" + GRAPHITE_PORT + ":" + HOST_NAME);
        startConsoleReport(registry);
        lifecycle.addStopHook(()-> {
            logger.debug("Shutting down metrics plugin");
            graphiteReporter.stop();
            return CompletableFuture.completedFuture(null);
        });
    }

    public static String execCommandAndReadToString(String execCommand) {
        try {
            Process proc = Runtime.getRuntime().exec(execCommand);
            try (InputStream stream = proc.getInputStream()) {
                try (Scanner s = new Scanner(stream).useDelimiter("\\A")) {
                    return s.hasNext() ? s.next() : "";
                }
            }
        }
        catch (Exception ex) {
            logger.error("Error fetching hostname");
            return "";
        }
    }

    static void startConsoleReport(MetricRegistry registry) {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.SECONDS);
    }
}
