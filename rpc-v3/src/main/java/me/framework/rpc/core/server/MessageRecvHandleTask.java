package me.framework.rpc.core.server;

import me.framework.rpc.config.RpcSystemConfig;
import me.framework.rpc.model.MessageRequest;
import me.framework.rpc.model.MessageResponse;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;

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
    private static final String METHOD_MAPPED_NAME = "invoke";
    private boolean returnNotNull = true;

    public MessageRecvHandleTask(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
        this.request = request;
        this.response = response;
        this.handlerMap = handlerMap;
    }

    @Override
    public Boolean call() {
        response.setMessageId(request.getMessageId());
        try {
            Object result = reflect(request);
            if (!returnNotNull || (returnNotNull && result != null)) {
                response.setResult(result);
                response.setError("");
                response.setReturnNotNull(returnNotNull);
            } else {
                logger.error(RpcSystemConfig.FILTER_RESPONSE_MSG);
                response.setResult(null);
                response.setError(RpcSystemConfig.FILTER_RESPONSE_MSG);
            }
            return Boolean.TRUE;
        } catch (Throwable e) {
            logger.error("RPC Server reflect error!", e);
            response.setError(e.toString());
            return Boolean.FALSE;
        }
    }

    private Object reflect(MessageRequest request) throws Throwable, IllegalAccessException, InvocationTargetException {
        ProxyFactory weaver = new ProxyFactory(new MethodInvoker());
        NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
        advisor.setMappedName(METHOD_MAPPED_NAME);
        advisor.setAdvice(new MethodProxyAdvisor(handlerMap));
        weaver.addAdvisor(advisor);
        MethodInvoker methodInvoker = (MethodInvoker) weaver.getProxy();
        Object object = methodInvoker.invoke(request);
        setReturnNotNull(((MethodProxyAdvisor)advisor.getAdvice()).isReturnNotNull());
        return object;
    }

    public MessageRequest getRequest() {
        return request;
    }

    public void setRequest(MessageRequest request) {
        this.request = request;
    }

    public MessageResponse getResponse() {
        return response;
    }

    public void setResponse(MessageResponse response) {
        this.response = response;
    }

    public boolean isReturnNotNull() {
        return returnNotNull;
    }

    public void setReturnNotNull(boolean returnNotNull) {
        this.returnNotNull = returnNotNull;
    }
}
