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

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MetricsImpl implements Metrics {

    private Logger.ALogger logger = Log.Logger;

    @Inject
    MetricsImpl(ApplicationLifecycle lifecycle, Configuration configuration) {
        logger.debug("Metrics Plugin Initializing");
        final String GRAPHITE_HOST = configuration.getString("metrics.graphiteHost");
        final int GRAPHITE_PORT = configuration.getInt("metrics.graphitePort");
        final String SERVICE_NAME = configuration.getString("metrics.serviceName");
        final int PING_INTERVAL = configuration.getInt("metrics.pingInterval");
        final MetricRegistry registry = SharedMetricRegistries.getOrCreate("phenommetrics");
        /*final Graphite graphite = new Graphite(new InetSocketAddress(GRAPHITE_HOST, GRAPHITE_PORT));
        final GraphiteReporter graphiteReporter = GraphiteReporter.forRegistry(registry)
                .prefixedWith(SERVICE_NAME+"."+"HOST_NAME"+".")
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);
        graphiteReporter.start(PING_INTERVAL, TimeUnit.SECONDS);*/
        //
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.SECONDS);
        //
        lifecycle.addStopHook(()-> {
            logger.debug("Shutting down metrics plugin");
            //graphiteReporter.stop();
            return CompletableFuture.completedFuture(null);
        });
    }
}
