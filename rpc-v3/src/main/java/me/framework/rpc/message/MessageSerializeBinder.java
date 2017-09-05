package me.framework.rpc.message;

import io.netty.channel.ChannelPipeline;
import me.framework.rpc.serialize.support.RpcSerializeProtocol;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public interface MessageSerializeBinder {

    void bind(RpcSerializeProtocol protocol, ChannelPipeline pipeline);

}
