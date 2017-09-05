package me.framework.rpc.core.client;

import me.framework.rpc.model.MessageRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageSendProxy<T> implements InvocationHandler {

    private Class<T> cls;


    public MessageSendProxy(Class<T> cls) {
        this.cls = cls;
    }

    /**
     * 该函数会阻塞调用线程，需要与netty的io线程独立出来，否则会阻塞eventLoop
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MessageRequest request = new MessageRequest();
        request.setMessageId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setTypeParameters(method.getParameterTypes());
        request.setParameterValues(args);

        // 底层RPC通信
        MessageSendHandler sendHandler = RpcServerLoader.getInstance().getSendHandler();
        MessageCallBack callBack = sendHandler.sendRequest(request);
        return callBack.get();
    }
}
