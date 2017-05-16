package me.srpc.netty.server;

import io.netty.channel.*;
import me.srpc.model.MessageRequest;
import me.srpc.model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageReceiveHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MessageReceiveHandler.class);

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
        MessageReceiveProcessingTask processingTask = new MessageReceiveProcessingTask(request, response);
        // 不要阻塞Netty的NIO线程，复杂逻辑丢给后天线程池去处理
        MessageReceiveExecutor.getInstance().submit(processingTask, ctx, request, response);
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
