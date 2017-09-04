package me.rpc.core;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import me.rpc.model.MessageRequest;
import me.rpc.model.MessageResponse;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageSendHandler extends ChannelDuplexHandler {

    private ConcurrentHashMap<String, MessageCallBack> waitForResponseMap = new ConcurrentHashMap<>();

    private volatile Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        channel = ctx.channel();
    }

    /**
     * 获取到应答消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageResponse response = (MessageResponse) msg;
        String messageId = response.getMessageId();
        MessageCallBack callBack = waitForResponseMap.get(messageId);
        if (callBack != null) {
            waitForResponseMap.remove(messageId);
            callBack.over(response);
        }
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }


    public MessageCallBack sendRequest(MessageRequest request) {
        MessageCallBack callBack = new MessageCallBack(request);
        waitForResponseMap.put(request.getMessageId(), callBack);
        channel.pipeline().writeAndFlush(request);
        return callBack;
    }

}
