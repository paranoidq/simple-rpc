package me.framework.rpc.core.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.framework.rpc.model.MessageRequest;
import me.framework.rpc.model.MessageResponse;

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
        MessageRecvHandleTask task = new MessageRecvHandleTask(request, response, handlerMap);
        MessageRecvExecutor.submit(task, ctx, request, response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
