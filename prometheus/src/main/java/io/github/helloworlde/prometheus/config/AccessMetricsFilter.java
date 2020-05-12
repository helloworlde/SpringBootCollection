package io.github.helloworlde.prometheus.config;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class AccessMetricsFilter implements Filter {

    @Autowired
    private CollectorRegistry collectorRegistry;

    private Counter totalCounter;
    private Counter errorCounter;
    private Histogram responseTime;


    @PostConstruct
    private void init() {
        log.info("初始化counter");

        totalCounter = Counter.build("custom_request_total", "自定义请求次数统计")
                              .labelNames("path")
                              .create();

        errorCounter = Counter.build("custom_request_error", "自定义请求错误次数统计")
                              .labelNames("path")
                              .create();

        responseTime = Histogram.build("custom_response_time", "自定义请求响应时间")
                                .labelNames("path")
                                .create();

        collectorRegistry.register(totalCounter);
        collectorRegistry.register(errorCounter);
        collectorRegistry.register(responseTime);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String path = request.getRequestURI();

        Histogram.Timer timer = responseTime.labels(path).startTimer();

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            errorCounter.labels(path).inc();
        } finally {
            totalCounter.labels(path).inc();
            timer.observeDuration();
        }
    }

}
