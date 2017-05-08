package me.sprc.core.server;

import com.google.common.base.Throwables;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import me.sprc.model.MessageRequest;
import me.sprc.model.MessageResponse;
import org.apache.commons.lang.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageReceiveProcessingTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MessageReceiveProcessingTask.class);

    private MessageRequest request;
    private MessageResponse response;
    private Map<String, Object> handlerMap;
    private ChannelHandlerContext ctx;

    public MessageReceiveProcessingTask(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap, ChannelHandlerContext ctx) {
        this.request = request;
        this.response = response;
        this.handlerMap = handlerMap;
        this.ctx = ctx;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        response.setMessageId(request.getMessageId());
        try {
            Object result = reflect(request);
            response.setResult(result);
        } catch (Throwable t) {
            response.setError(Throwables.getStackTraceAsString(t));
            logger.error("", t);
        }
        this.ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                logger.info("Netty RPC Server send message response. messageId: {}", response.getMessageId());
            }
        });
    }

    /**
     * TODO ?? apache commons MethodUtils us
     * @param request
     * @return
     * @throws Exception
     */
    private Object reflect(MessageRequest request) throws Exception {
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);
        String methodName = request.getMethodName();
        Object[] parameters = request.getParamValues();
        return MethodUtils.invokeMethod(serviceBean, methodName, parameters, request.getParamTypes());
    }
}
