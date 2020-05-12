package io.github.helloworlde.prometheus.config;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@Component
public class DataSourceStatusProbe implements MeterBinder {
    private static final String SELECT_1 = "SELECT 1;";
    private static final int QUERY_TIMEOUT = 1;
    private static final double UP = 1.0;
    private static final double DOWN = 0.0;
    private final String name;
    private final String description;
    private final Iterable<Tag> tags;
    private final DataSource dataSource;

    public DataSourceStatusProbe(final DataSource dataSource) {
        Objects.requireNonNull(dataSource, "dataSource cannot be null");
        this.dataSource = dataSource;
        this.name = "datasource";
        this.description = "DataSource status";
        this.tags = tags(dataSource);
    }

    protected static Iterable<Tag> tags(DataSource dataSource) {
        Objects.requireNonNull(dataSource, "dataSource cannot be null");
        try {
            return Tags.of(Tag.of("url", dataSource.getConnection().getMetaData().getURL()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean status() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SELECT_1);
            statement.setQueryTimeout(QUERY_TIMEOUT);
            statement.executeQuery();

            return true;
        } catch (SQLException ignored) {
            return false;
        }
    }

    @Override
    public void bindTo(final MeterRegistry meterRegistry) {
        Gauge.builder("datasource_time", this, value -> {
            long startTime = System.currentTimeMillis();
            value.status();
            return System.currentTimeMillis() - startTime;
        })
             .description("数据库请求响应时间")
             .tags(tags)
             .baseUnit("ms")
             .register(meterRegistry);

        Gauge.builder(name, this, value -> value.status() ? UP : DOWN)
             .description(description)
             .tags(tags)
             .baseUnit("status")
             .register(meterRegistry);
    }
}
