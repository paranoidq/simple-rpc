package me.framework.rpc.core.server;

import me.framework.rpc.model.MessageRequest;
import me.framework.rpc.model.MessageResponse;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageRecvHandleTask implements Callable<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(MessageRecvHandleTask.class);

    private MessageRequest request;
    private MessageResponse response;
    private Map<String, Object> handlerMap;

    public MessageRecvHandleTask(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
        this.request = request;
        this.response = response;
        this.handlerMap = handlerMap;
    }

    @Override
    public Boolean call() {
        response.setMessageId(request.getMessageId());
        try {
            Object result = invoke(request);
            response.setResult(result);
            return Boolean.TRUE;
        } catch (Throwable e) {
            response.setError(e.toString());
            logger.error("", e);
            return Boolean.FALSE;
        }
    }

    private Object invoke(MessageRequest request) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);
        String methodName = request.getMethodName();
        Object[] parameters = request.getParameterValues();
        return MethodUtils.invokeMethod(serviceBean, methodName, parameters);
    }
}
