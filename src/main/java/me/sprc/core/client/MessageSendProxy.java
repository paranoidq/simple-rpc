package me.sprc.core.client;

import me.sprc.model.MessageCallBack;
import me.sprc.model.MessageRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageSendProxy<T> implements InvocationHandler {

    // TODO ?? not used
    private Class<T> cls;

    public MessageSendProxy(Class<T> cls) {
        this.cls = cls;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
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
