package me.framework.rpc.logger;

import com.google.common.base.Throwables;
import com.google.common.reflect.ClassPath;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 *
 * 请在系统启动时第一时间运行该注入器
 *
 * @author paranoidq
 * @since 1.0.0
 */
public class AppLoggerInjector {

    public static void injectAll(String basePackage) {
        try {
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if (info.getName().startsWith(basePackage)) {
                    Class<?> clazz = info.load();
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(AppLoggerInject.class)) {
                            field.setAccessible(true);
                            if (org.slf4j.Logger.class.isAssignableFrom(field.getType())) {
                                field.set(null, LoggerFactory.getLogger(clazz));
                            } else if (org.apache.log4j.Logger.class.isAssignableFrom(field.getType())) {
                                field.set(null, org.apache.log4j.Logger.getLogger(clazz));
                            } else if (java.util.logging.Logger.class.isAssignableFrom(field.getType())) {
                                field.set(null, java.util.logging.Logger.getLogger(clazz.getName()));
                            } else {
                                System.err.println("不支持该Logger门面的注入: " + field.getType().getName());
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("AppLogger自动注入失败" + Throwables.getStackTraceAsString(e));
        } catch (IllegalAccessException e) {
            System.err.println("AppLogger自动注入失败" + Throwables.getStackTraceAsString(e));
        }
    }

}
