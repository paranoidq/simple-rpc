package me.framework.rpc.core.handler;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.logging.LoggingHandler;
import me.framework.rpc.core.client.MessageSendHandler;
import me.framework.rpc.core.server.MessageRecvHandler;
import me.framework.rpc.message.kryo.KryoDecoder;
import me.framework.rpc.message.kryo.KryoEncoder;
import me.framework.rpc.serialize.support.kryo.KryoMessageCodec;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class KryoSendHandler implements NettyRpcSendHandler {

    @Override
    public void handle(ChannelPipeline pipeline) {
        KryoMessageCodec util = new KryoMessageCodec();
        pipeline.addLast(new LoggingHandler());
        pipeline.addLast(new KryoEncoder(util));
        pipeline.addLast(new KryoDecoder(util));
        pipeline.addLast(new MessageSendHandler());
    }
}
