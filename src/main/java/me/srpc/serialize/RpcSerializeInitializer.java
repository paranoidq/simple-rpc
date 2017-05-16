package me.srpc.serialize;

import io.netty.channel.ChannelPipeline;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public interface RpcSerializeInitializer {

    void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline);
}
