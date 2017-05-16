package me.srpc.netty.server;

import io.netty.channel.Channel;
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
public class MessageReceiveChannelInitializer extends ChannelInitializer<SocketChannel> implements RpcSerializeInitializer {
    private RpcSerializeProtocol protocol;

    public MessageReceiveChannelInitializer() {
    }

    public MessageReceiveChannelInitializer buildRpcSerializeProtocol(RpcSerializeProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    /**
     * This method will be called once the {@link Channel} was registered. After the method returns this instance
     * will be removed from the {@link ChannelPipeline} of the {@link Channel}.
     *
     * @param ch the {@link Channel} which was registered.
     * @throws Exception is thrown if an error occurs. In that case the {@link Channel} will be closed.
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        this.select(protocol, pipeline);
    }


    @Override
    public void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline) {
        switch (protocol) {
            case JDK_SERIALIZE:
                NettyRpcRecvHandler.INSTANCES.get(JdkNativeRecvHandler.class).build(pipeline);
                break;
            case KRYO_SERIALIZE:
                NettyRpcRecvHandler.INSTANCES.get(KryoRecvHandler.class).build(pipeline);
                break;
            case HESSIAN_SERIALIZE:
                NettyRpcRecvHandler.INSTANCES.get(HessianRecvHandler.class).build(pipeline);
                break;
            case PROTOSTUFF_SERIALIZE:
                NettyRpcRecvHandler.INSTANCES.get(ProtostuffRecvHandler.class).build(pipeline);
                break;
            default:
                NettyRpcRecvHandler.INSTANCES.get(JdkNativeRecvHandler.class).build(pipeline);
                break;
        }
    }
}
