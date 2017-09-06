package me.framework.rpc.core.handler;

import io.netty.channel.ChannelPipeline;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public interface NettyRpcSendHandler {

    void handle(ChannelPipeline pipeline);
}
