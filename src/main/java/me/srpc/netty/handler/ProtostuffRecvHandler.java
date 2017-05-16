package me.srpc.netty.handler;

import io.netty.channel.ChannelPipeline;
import me.srpc.netty.server.MessageReceiveHandler;
import me.srpc.serialize.protostuff.ProtostuffCodecUtil;
import me.srpc.serialize.protostuff.ProtostuffDecoder;
import me.srpc.serialize.protostuff.ProtostuffEncoder;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ProtostuffRecvHandler implements NettyRpcRecvHandler {
    @Override
    public void build(ChannelPipeline pipeline) {
        ProtostuffCodecUtil protostuffCodecUtil = new ProtostuffCodecUtil();
        pipeline.addLast(new ProtostuffEncoder(protostuffCodecUtil));
        pipeline.addLast(new ProtostuffDecoder(protostuffCodecUtil));
        pipeline.addLast(new MessageReceiveHandler());
    }
}
