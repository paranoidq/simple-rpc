package me.framework.rpc.logger;

import java.lang.annotation.*;

/**
 * 该注解仅用于应用层的Logger注解，避免多次重复书写
 * <code>
 *     Logger logger = LoggerFactory.getLogger(...)
 * </code>
 *
 * 基础组件层面的logger可能在应用层之前执行，因此不受该注解控制
 * 使用方式如下，{@link AppLoggerInjector}会在系统启动时自动进行logger实例注入static field中
 * <code>
 *     @AppLoggerInject
 *     private <b>static</b> Logger logger;
 * </code>
 *
 * @author paranoidq
 * @since 1.0.0
 */

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AppLoggerInject {
}
