package me.rpc.core;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import me.rpc.model.MessageRequest;
import me.rpc.model.MessageResponse;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageRecvInitializeTask implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(MessageRecvInitializeTask.class);

    private MessageRequest request;
    private MessageResponse response;
    private Map<String, Object> handlerMap;
    private ChannelHandlerContext ctx;

    public MessageRecvInitializeTask(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap, ChannelHandlerContext ctx) {
        this.request = request;
        this.response = response;
        this.handlerMap = handlerMap;
        this.ctx = ctx;
    }

    @Override
    public void run() {
        response.setMessageId(request.getMessageId());
        try {
            Object result = invoke(request);
            response.setResult(result);
        } catch (Throwable e) {
            response.setError(e.toString());
            logger.error("", e);
        }

        ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                    logger.info("Respond message successful, messageId={}", request.getMessageId());
            }
        });

    }

    private Object invoke(MessageRequest request) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);
        String methodName = request.getMethodName();
        Object[] parameters = request.getParameterValues();
        return MethodUtils.invokeMethod(serviceBean, methodName, parameters);
    }
}
