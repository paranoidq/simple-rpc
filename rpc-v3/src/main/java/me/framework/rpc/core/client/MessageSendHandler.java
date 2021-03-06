package me.framework.rpc.core.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import me.framework.rpc.message.kryo.KryoEncoder;
import me.framework.rpc.model.MessageRequest;
import me.framework.rpc.model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageSendHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageSendHandler.class);

    private ConcurrentHashMap<String, MessageCallBack> waitForResponseMap = new ConcurrentHashMap<>();
    private volatile Channel channel;
    private SocketAddress remoteAddr;

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemoteAddr() {
        return remoteAddr;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remoteAddr = this.channel.remoteAddress();
        ChannelHandler handler = ctx.channel().pipeline().get(KryoEncoder.class);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("", cause);
        ctx.close();
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
