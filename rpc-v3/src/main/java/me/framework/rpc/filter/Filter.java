package me.framework.rpc.filter;

import java.lang.reflect.Method;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public interface Filter {

    // If returns false, indicates that the RPC request method is intercept.
    boolean before(Method method, Object processor, Object[] requestObjects);

    // If filter's before function returns false, filter's after function will not be called.
    void after(Method method, Object processor, Object[] requestObjects);
}
