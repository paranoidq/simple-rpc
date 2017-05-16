package me.srpc.netty.client;

import com.google.common.reflect.AbstractInvocationHandler;
import me.srpc.model.MessageCallBack;
import me.srpc.model.MessageRequest;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageSendProxy<T> extends AbstractInvocationHandler {

    public Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
        MessageRequest request = MessageRequest.create(
            UUID.randomUUID().toString(),
            method.getDeclaringClass().getName(),
            method.getName(),
            method.getParameterTypes(),
            args
        );

        MessageSendHandler handler = RpcServerLoader.getInstance().getMessageSendHandler();
        MessageCallBack callBack = handler.sendRequest(request);
        return callBack.start();
    }
}
