package cn.com.hellowood.mapper.utils;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

import static cn.com.hellowood.mapper.common.CommonConstant.DEV_ENV;


/**
 * The type My batis interceptor.
 */
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class MyBatisInterceptor implements Interceptor {

    @Value("${spring.profiles.active}")
    private String env;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameterObject = null;
        if (invocation.getArgs().length > 1) {
            parameterObject = invocation.getArgs()[1];
        }

        long startTime = Calendar.getInstance().getTimeInMillis();

        Object result = invocation.proceed();

        String statementId = mappedStatement.getId();
        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        Configuration configuration = mappedStatement.getConfiguration();
        String formattedSql = getFormattedSql(boundSql, parameterObject, configuration);

        long endTime = Calendar.getInstance().getTimeInMillis();
        long costTime = endTime - startTime;

        if (DEV_ENV.equals(env)) {
            if (costTime > 1000) {
                logger.warn("Slow SQL in method [{}] cost {} ms", statementId, costTime);
            } else {
                logger.debug("Execute SQL in method [{}] cost {} ms", statementId, costTime);
            }
            logger.debug(formattedSql);
        }

        return result;
    }


    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
    }


    private String getFormattedSql(BoundSql boundSql, Object parameterObject, Configuration configuration) {
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        if (parameterMappings != null) {
            for (ParameterMapping parameterMapping : parameterMappings) {
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object propertyValue;
                    String propertyName = parameterMapping.getProperty();

                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        propertyValue = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        propertyValue = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        propertyValue = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        propertyValue = metaObject.getValue(propertyName);
                    }
                    sql = replaceQuestionMarkAsValue(sql, propertyValue);
                }
            }
        }
        return sql;
    }

    private String replaceQuestionMarkAsValue(String sql, Object propertyValue) {
        String result;
        if (propertyValue != null) {
            if (propertyValue instanceof String) {
                result = "'" + propertyValue + "'";
            } else if (propertyValue instanceof Date) {
                result = "'" + ApplicationUtils.formatDate((Date) propertyValue) + "'";
            } else {
                result = propertyValue.toString();
            }
        } else {
            result = "null";
        }
        return sql.replaceFirst("\\?", Matcher.quoteReplacement(result));
    }
}


