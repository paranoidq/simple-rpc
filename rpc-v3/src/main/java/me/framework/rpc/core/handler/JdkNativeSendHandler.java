package me.framework.rpc.core.handler;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import me.framework.rpc.core.client.MessageSendHandler;
import me.framework.rpc.serialize.support.MessageCodec;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class JdkNativeSendHandler implements NettyRpcSendHandler {

    @Override
    public void handle(ChannelPipeline pipeline) {
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, MessageCodec.MESSAGE_LENGTH_BYTES, 0, MessageCodec.MESSAGE_LENGTH_BYTES));
        pipeline.addLast(new LengthFieldPrepender(MessageCodec.MESSAGE_LENGTH_BYTES));
        pipeline.addLast(new ObjectEncoder());
        pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
        pipeline.addLast(new MessageSendHandler());
    }
}
