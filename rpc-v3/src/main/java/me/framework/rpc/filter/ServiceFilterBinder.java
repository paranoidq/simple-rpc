package me.framework.rpc.filter;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ServiceFilterBinder {

    private Object object;
    private Filter filter;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}
