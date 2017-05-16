package me.srpc.netty.handler;

import io.netty.channel.ChannelPipeline;
import me.srpc.netty.server.MessageReceiveHandler;
import me.srpc.serialize.hessian.HessianCodecUtil;
import me.srpc.serialize.hessian.HessianDecoder;
import me.srpc.serialize.hessian.HessianEncoder;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class HessianRecvHandler implements NettyRpcRecvHandler {
    @Override
    public void build(ChannelPipeline pipeline) {
        HessianCodecUtil hessianCodecUtil = new HessianCodecUtil();
        pipeline.addLast(new HessianEncoder(hessianCodecUtil));
        pipeline.addLast(new HessianDecoder(hessianCodecUtil));
        pipeline.addLast(new MessageReceiveHandler());
    }
}
