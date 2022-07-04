package io.github.helloworlde.prometheus.config;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * Custom business metrics
 */
@Aspect
@Component
@Slf4j
public class PrometheusMetricsAspect {

    @Autowired
    private CollectorRegistry collectorRegistry;

    private Counter orderCounter;
    private Gauge orderInProgress;

    private Summary orderMoney;

    @PostConstruct
    public void init() {
        orderCounter = Counter.build("custom_order_total", "自定义下单业务统计")
                              .labelNames("path", "area")
                              .create();
        orderInProgress = Gauge.build("custom_order_in_progress", "自定义下单业务处理中的业务")
                               .labelNames("path", "area")
                               .create();
        orderMoney = Summary.build("custom_order_money", "自定义下单金额统计")
                            .labelNames("path", "area")
                            .create();

        orderCounter.register(collectorRegistry);
        orderInProgress.register(collectorRegistry);
        orderMoney.register(collectorRegistry);
    }

    @Pointcut(value = "@annotation(io.github.helloworlde.prometheus.config.PrometheusMetrics)")
    public void metricsAnnotation() {
    }

    @Around(value = "metricsAnnotation() && @annotation(annotation)")
    public Object MetricsCollector(ProceedingJoinPoint joinPoint, PrometheusMetrics annotation) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        PrometheusMetrics prometheusMetrics = methodSignature.getMethod().getAnnotation(PrometheusMetrics.class);
        if (prometheusMetrics != null) {
            String path;
            String area;
            String money;

            if (StringUtils.isNotEmpty(prometheusMetrics.name())) {
                area = "UNKNOWN";
                money = "0";
                path = prometheusMetrics.name();
            } else {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                area = StringUtils.defaultString(request.getParameter("area"), "UNKNOWN");
                money = StringUtils.defaultString(request.getParameter("money"), "0");
                path = request.getRequestURI();
            }


            orderCounter.labels(path, area).inc();
            orderMoney.labels(path, area).observe(Double.parseDouble(money));
            orderInProgress.labels(path, area).inc();
            Object object;
            try {
                object = joinPoint.proceed();
            } catch (Exception e) {
                log.error("{}", e.getMessage(), e);
                throw e;
            } finally {
                orderInProgress.labels(path, area).dec();
            }
            return object;
        } else {
            return joinPoint.proceed();
        }
    }

}