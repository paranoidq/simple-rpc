package me.framework.rpc.core.handler;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import me.framework.rpc.core.server.MessageRecvHandler;
import me.framework.rpc.serialize.support.MessageCodec;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class JdkNativeRecvHandler implements NettyRpcRecvHandler {

    @Override
    public void handle(Map<String, Object> handlerMap, ChannelPipeline pipeline) {
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, MessageCodec.MESSAGE_LENGTH_BYTES, 0, MessageCodec.MESSAGE_LENGTH_BYTES));
        pipeline.addLast(new LengthFieldPrepender(MessageCodec.MESSAGE_LENGTH_BYTES));
        pipeline.addLast(new ObjectEncoder());
        pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
        pipeline.addLast(new MessageRecvHandler(handlerMap));
    }
}
