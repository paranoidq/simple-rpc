package me.srpc.netty.handler;

import io.netty.channel.ChannelPipeline;
import me.srpc.netty.server.MessageReceiveHandler;
import me.srpc.serialize.kryo.KryoCodecUtil;
import me.srpc.serialize.kryo.KryoDecoder;
import me.srpc.serialize.kryo.KryoEncoder;
import me.srpc.serialize.kryo.KryoPoolFactory;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class KryoRecvHandler implements NettyRpcRecvHandler {
    @Override
    public void build(ChannelPipeline pipeline) {
        KryoCodecUtil kryoCodecUtil = new KryoCodecUtil(KryoPoolFactory.getKryoPoolInstance());
        pipeline.addLast(new KryoEncoder(kryoCodecUtil));
        pipeline.addLast(new KryoDecoder(kryoCodecUtil));
        pipeline.addLast(new MessageReceiveHandler());
    }
}
