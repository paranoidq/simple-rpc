package me.srpc.model;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ServiceHolder {

    /**
     * key = class name
     * value = service implementation object
     */
    private Map<String, Object> serviceHolder;

    public Map<String, Object> getServiceHolder() {
        return serviceHolder;
    }

    public void setServiceHolder(Map<String, Object> serviceHolder) {
        this.serviceHolder = serviceHolder;
    }
}
