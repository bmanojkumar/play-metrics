package com.phenom.metrics;

import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;

public class MetricsModule extends Module {

    @Override
    public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
        return seq(
            bind(Metrics.class).to(MetricsImpl.class).eagerly()
        );
    }
}
