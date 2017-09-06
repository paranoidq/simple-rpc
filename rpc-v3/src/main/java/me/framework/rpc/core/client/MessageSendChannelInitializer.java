package me.framework.rpc.core.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import me.framework.rpc.message.RpcSerializeFrame;
import me.framework.rpc.serialize.support.RpcSerializeProtocol;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageSendChannelInitializer extends ChannelInitializer<SocketChannel> {

    private RpcSerializeProtocol protocol;
    private RpcSerializeFrame frame = new RpcSendSerializeFrame();

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        frame.select(protocol, ch.pipeline());
    }

    public MessageSendChannelInitializer setRpcSerializeProtocol(RpcSerializeProtocol protocol) {
        this.protocol = protocol;
        return this;
    }
}
