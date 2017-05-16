package me.srpc.netty.server;

import com.google.common.base.Throwables;
import me.srpc.model.MessageRequest;
import me.srpc.model.MessageResponse;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageReceiveProcessingTask implements Callable<Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(MessageReceiveProcessingTask.class);

    private MessageRequest request;
    private MessageResponse response;

    public MessageReceiveProcessingTask(MessageRequest request, MessageResponse response) {
        this.request = request;
        this.response = response;
    }

    public static MessageReceiveProcessingTask create(MessageRequest request, MessageResponse response) {
        return new MessageReceiveProcessingTask(request, response);
    }

    @Override
    public Boolean call() {
        response.setMessageId(request.getMessageId());
        try {
            Object result = reflect(request);
            response.setResult(result);
            return Boolean.TRUE;
        } catch (Throwable t) {
            response.setError(Throwables.getStackTraceAsString(t));
            logger.error("", t);
            return Boolean.FALSE;
        }
    }

    /**
     * TODO ?? apache commons MethodUtils usage
     * @param request
     * @return
     * @throws Exception
     */
    private Object reflect(MessageRequest request) throws Exception {
        String className = request.getClassName();
        Object serviceBean = ServiceHolder.getService(className);
        String methodName = request.getMethodName();
        Object[] parameters = request.getParamValues();
        return MethodUtils.invokeMethod(serviceBean, methodName, parameters, request.getParamTypes());
    }
}
