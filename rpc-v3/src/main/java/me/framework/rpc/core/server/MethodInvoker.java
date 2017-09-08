package me.framework.rpc.core.server;

import me.framework.rpc.model.MessageRequest;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MethodInvoker {
    private Object serviceBean;

    public Object getServiceBean() {
        return serviceBean;
    }

    public void setServiceBean(Object serviceBean) {
        this.serviceBean = serviceBean;
    }

    public Object invoke(MessageRequest request) throws Throwable {
        String methodName = request.getMethodName();
        Object[] parameters = request.getParameterValues();
        return MethodUtils.invokeMethod(serviceBean, methodName, parameters);
    }
}
