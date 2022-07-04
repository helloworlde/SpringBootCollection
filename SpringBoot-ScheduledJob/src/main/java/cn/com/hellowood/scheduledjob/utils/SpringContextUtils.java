package cn.com.hellowood.scheduledjob.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * The type Spring context utils.
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {

    /**
     * The constant applicationContext.
     */
    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }

    /**
     * Gets bean.
     *
     * @param name the name
     * @return the bean
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * Gets bean.
     *
     * @param <T>          the type parameter
     * @param name         the name
     * @param requiredType the required type
     * @return the bean
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    /**
     * Contains bean boolean.
     *
     * @param name the name
     * @return the boolean
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * Is singleton boolean.
     *
     * @param name the name
     * @return the boolean
     */
    public static boolean isSingleton(String name) {
        return applicationContext.isSingleton(name);
    }

    /**
     * Gets type.
     *
     * @param name the name
     * @return the type
     */
    public static Class<? extends Object> getType(String name) {
        return applicationContext.getType(name);
    }
}
