package io.github.helloworlde.prometheus.config;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PrometheusMetrics {
    /**
     * @return
     */
    String name() default "";
}