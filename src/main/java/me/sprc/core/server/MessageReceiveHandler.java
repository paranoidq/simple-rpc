package me.sprc.core.server;

import io.netty.channel.*;
import me.sprc.model.MessageRequest;
import me.sprc.model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageReceiveHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MessageReceiveHandler.class);

    private final Map<String, Object> handlerMap;


    public MessageReceiveHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    /**
     * Calls {@link ChannelHandlerContext#fireChannelRead(Object)} to forward
     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
     * <p>
     * Sub-classes may override this method to change behavior.
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageRequest request = (MessageRequest) msg;
        MessageResponse response = new MessageResponse();
        MessageReceiveProcessingTask receiveTask = new MessageReceiveProcessingTask(request, response, handlerMap, ctx);
        // 不要阻塞Netty的NIO线程，复杂逻辑丢给后天线程池去处理
        MessageReceiveExecutor.submit(receiveTask);
    }

    /**
     * Calls {@link ChannelHandlerContext#fireExceptionCaught(Throwable)} to forward
     * to the next {@link ChannelHandler} in the {@link ChannelPipeline}.
     * <p>
     * Sub-classes may override this method to change behavior.
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Nett RPC Server异常", cause);
        ctx.close();
    }
}
