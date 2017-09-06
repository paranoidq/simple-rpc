package me.framework.rpc.core.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import me.framework.rpc.message.RpcSerializeFrame;
import me.framework.rpc.serialize.support.RpcSerializeProtocol;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageRecvChannelInitializer extends ChannelInitializer<SocketChannel> {

    private RpcSerializeProtocol protocol;
    private RpcSerializeFrame frame;


    public MessageRecvChannelInitializer setSerializeProtocol(RpcSerializeProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    public MessageRecvChannelInitializer(Map<String, Object> handlerMap) {
        frame = new RpcRecvSerializeFrame(handlerMap);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        frame.select(protocol, ch.pipeline());
    }
}
