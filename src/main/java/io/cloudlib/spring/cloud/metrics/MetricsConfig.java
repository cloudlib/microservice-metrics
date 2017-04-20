package io.cloudlib.spring.cloud.metrics;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;

import io.cloudlib.spring.cloud.metrics.properties.GraphiteProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@EnableMetrics
@Configuration
@EnableConfigurationProperties(value = {
        GraphiteProperties.class
})
public class MetricsConfig extends MetricsConfigurerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(MetricsConfig.class);

    @Autowired
    private GraphiteProperties graphiteProperties;

    private MetricRegistry metricRegistry;

    @PostConstruct
    protected void postConstruct() throws IOException {
        LOG.info(graphiteProperties.toString());
    }

    @Override
    public void configureReporters(final MetricRegistry metricRegistry) {
        if(graphiteProperties.isEnabled()) {
            final Graphite graphite = new Graphite(new InetSocketAddress(graphiteProperties.getHostname(),
                    graphiteProperties.getPort()));
            GraphiteReporter.forRegistry(metricRegistry)
                    .prefixedWith(graphiteProperties.getPrefix())
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .filter(MetricFilter.ALL)
                    .build(graphite)
                    .start(graphiteProperties.getPeriod(), TimeUnit.SECONDS);
        }
    }

    @Override
    public MetricRegistry getMetricRegistry() {
        if(metricRegistry == null){
            metricRegistry = new MetricRegistry();
        }
        return metricRegistry;
    }
}
