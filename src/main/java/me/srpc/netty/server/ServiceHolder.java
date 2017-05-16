package me.srpc.netty.server;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ServiceHolder {

    private static Map<String, Object> services = new ConcurrentHashMap<>();

    private ServiceHolder() {
    }

    public static void clear() {
        services.clear();
    }

    public static void addService(String className, Object instance) {
        services.put(className, instance);
    }

    public static Object getService(String className) {
        return services.get(className);
    }

    public static Map<String, Object> getImmutableServicesView() {
        return Collections.unmodifiableMap(services);
    }

    public static void print() {
        System.out.println("-----");
        services.forEach((k, v) -> System.out.println(k + "=" + v));
        System.out.println("-----");
    }

}
