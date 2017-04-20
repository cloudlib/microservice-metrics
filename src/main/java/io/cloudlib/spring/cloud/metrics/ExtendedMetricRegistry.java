package io.cloudlib.spring.cloud.metrics;

import com.codahale.metrics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
@Component
public class ExtendedMetricRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedMetricRegistry.class);

    private final String appprefix;
    private final MetricRegistry metricRegistry;

    public static final String TOTAL = "total";
    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";
    public static final String DURATION = "duration";

    public ExtendedMetricRegistry(final String appprefix, final MetricRegistry metricRegistry){
        this.appprefix = appprefix;
        this.metricRegistry = metricRegistry;
    }

    public String getAppprefix()
    {
        return this.appprefix;
    }

    public MetricRegistry getMetricRegistry(){
        return this.metricRegistry;
    }

    public Counter counter(final String name){
        return metricRegistry.counter(MetricRegistry.name(appprefix, name));
    }

    public Histogram histogram(final String name){
        return metricRegistry.histogram(MetricRegistry.name(appprefix, name));
    }

    public Meter meter(final String name){
        return metricRegistry.meter(MetricRegistry.name(appprefix, name));
    }

    public Timer timer(final String name){
        return metricRegistry.timer(MetricRegistry.name(appprefix, name));
    }

    public void recordTime(final String name, long elapsed){
        metricRegistry.timer(MetricRegistry.name(appprefix, name)).update(elapsed, TimeUnit.NANOSECONDS);
    }
}