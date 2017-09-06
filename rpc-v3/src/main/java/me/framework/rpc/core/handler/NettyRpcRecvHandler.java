package me.framework.rpc.core.handler;

import io.netty.channel.ChannelPipeline;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public interface NettyRpcRecvHandler {

    void handle(Map<String, Object> handlerMap, ChannelPipeline pipeline);
}
