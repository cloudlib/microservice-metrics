package io.cloudlib.spring.cloud.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class MetricsCollector {
    private static final Logger LOG = LoggerFactory.getLogger(MetricsCollector.class);

    @Autowired
    private ExtendedMetricRegistry metricRegistry;

    @Pointcut("@annotation(collectMetrics)")
    public void collectable(CollectMetrics collectMetrics){}

    @Around("collectable(collectMetrics)")
    public Object collectMetrics(ProceedingJoinPoint pjp, CollectMetrics collectMetrics) throws Throwable {
    	LOG.info("Inside collectMetrics method. collectMetrics flag: {}", collectMetrics.value());
    	Object targetObject;
        final String methodName = pjp.getSignature().getName();

        // start  timer
        final Timer.Context timerContext = metricRegistry.timer(MetricRegistry.name(methodName, ExtendedMetricRegistry.DURATION)).time();

        //increment total requests meter
        LOG.info("Incrementing total request meter");
        metricRegistry.meter(MetricRegistry.name(methodName, ExtendedMetricRegistry.TOTAL)).mark();
        try {
            // log arguments
            logArguments(pjp, methodName);
            LOG.info("Proceeding to the actual method: {}", methodName);
            targetObject = pjp.proceed();
            //increment success meter
            metricRegistry.meter(MetricRegistry.name(methodName, ExtendedMetricRegistry.SUCCESS)).mark();
        } finally {
            final long elapsed = timerContext.stop();
            LOG.info("Time Elapsed: {}", elapsed);
            metricRegistry.recordTime(MetricRegistry.name(methodName, ExtendedMetricRegistry.DURATION), elapsed);
        }
        return targetObject;
    }

    @AfterThrowing(value = "@annotation(io.cloudlib.spring.cloud.metrics.CollectMetrics)", throwing = "e")
    public void handleException(final JoinPoint jp, final Exception e){
    	LOG.info("Inside handleException. Exception: {}", e.getLocalizedMessage());
        final String methodName = jp.getSignature().getName();
        metricRegistry.meter(MetricRegistry.name(methodName, ExtendedMetricRegistry.FAILURE)).mark();
    }

    private void logArguments(final JoinPoint joinPoint, final String methodName) {
    	LOG.info("Inside logArguments. methodName: {}", methodName);
        String arguments = Arrays.toString(joinPoint.getArgs());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing method: [ {} ] with arguments: {}. ", methodName, arguments);
        }
    }
}