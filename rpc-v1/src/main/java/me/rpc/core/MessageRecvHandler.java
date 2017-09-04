package me.rpc.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.rpc.model.MessageRequest;
import me.rpc.model.MessageResponse;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageRecvHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Object> handlerMap;

    public MessageRecvHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageRequest request = (MessageRequest) msg;
        MessageResponse response = new MessageResponse();
        MessageRecvInitializeTask task = new MessageRecvInitializeTask(request, response, handlerMap, ctx);
        MessageRecvExecutor.submit(task);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
