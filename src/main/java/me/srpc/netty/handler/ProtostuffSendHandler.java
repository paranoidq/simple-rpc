package me.srpc.netty.handler;

import io.netty.channel.ChannelPipeline;
import me.srpc.netty.client.MessageSendHandler;
import me.srpc.serialize.protostuff.ProtostuffCodecUtil;
import me.srpc.serialize.protostuff.ProtostuffDecoder;
import me.srpc.serialize.protostuff.ProtostuffEncoder;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ProtostuffSendHandler implements NettyRpcSendHandler {
    @Override
    public void build(ChannelPipeline pipeline) {
        ProtostuffCodecUtil protostuffCodecUtil = new ProtostuffCodecUtil();
        pipeline.addLast(new ProtostuffEncoder(protostuffCodecUtil));
        pipeline.addLast(new ProtostuffDecoder(protostuffCodecUtil));
        pipeline.addLast(new MessageSendHandler());
    }
}
