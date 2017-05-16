package me.srpc.netty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import me.srpc.netty.handler.*;
import me.srpc.serialize.RpcSerializeInitializer;
import me.srpc.serialize.RpcSerializeProtocol;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageSendChannelInitializer extends ChannelInitializer<SocketChannel> implements RpcSerializeInitializer {
    private RpcSerializeProtocol protocol = null;

    public MessageSendChannelInitializer setRpcSerializeProtocol(RpcSerializeProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        this.select(protocol, pipeline);
    }

    @Override
    public void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline) {
        switch (protocol) {
            case JDK_SERIALIZE:
                NettyRpcSendHandler.INSTANCES.get(JdkNativeSendHandler.class).build(pipeline);
                break;
            case KRYO_SERIALIZE:
                NettyRpcSendHandler.INSTANCES.get(KryoSendHandler.class).build(pipeline);
                break;
            case HESSIAN_SERIALIZE:
                NettyRpcSendHandler.INSTANCES.get(HessianSendHandler.class).build(pipeline);
                break;
            case PROTOSTUFF_SERIALIZE:
                NettyRpcSendHandler.INSTANCES.get(ProtostuffSendHandler.class).build(pipeline);
                break;
            default:
                NettyRpcSendHandler.INSTANCES.get(JdkNativeSendHandler.class).build(pipeline);
                break;
        }
    }
}
