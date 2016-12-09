package com.phenom.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

public class TimedImpl extends Action<Timed> {

    @Override
    public CompletionStage<Result> call(Http.Context context) {
        final MetricRegistry registry = SharedMetricRegistries.getOrCreate("phenommetrics");
        final Timer timer = registry.timer(context.request().path());
        final Timer.Context c = timer.time();
        try {
            return delegate.call(context);
        }
        finally {
            c.stop();
        }
    }
}
