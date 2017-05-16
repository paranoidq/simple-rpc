package me.srpc.netty.handler;

import io.netty.channel.ChannelPipeline;
import me.srpc.netty.client.MessageSendHandler;
import me.srpc.serialize.hessian.HessianCodecUtil;
import me.srpc.serialize.hessian.HessianDecoder;
import me.srpc.serialize.hessian.HessianEncoder;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class HessianSendHandler implements NettyRpcSendHandler {
    @Override
    public void build(ChannelPipeline pipeline) {
        HessianCodecUtil hessianCodecUtil = new HessianCodecUtil();
        pipeline.addLast(new HessianEncoder(hessianCodecUtil));
        pipeline.addLast(new HessianDecoder(hessianCodecUtil));
        pipeline.addLast(new MessageSendHandler());
    }
}
