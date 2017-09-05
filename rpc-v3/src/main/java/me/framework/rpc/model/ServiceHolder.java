package me.framework.rpc.model;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ServiceHolder {

    private Map<String, Object> services;

    public Map<String, Object> getServices() {
        return services;
    }

    public void setServices(Map<String, Object> services) {
        this.services = services;
    }
}
