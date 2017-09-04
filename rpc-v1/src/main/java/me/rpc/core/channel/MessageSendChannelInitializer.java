package me.rpc.core.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import me.rpc.core.MessageSendHandler;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageSendChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final int MESSAGE_LENGTH_BYTES = 4;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline
            .addLast(new LengthFieldBasedFrameDecoder(
                Integer.MAX_VALUE, 0, MESSAGE_LENGTH_BYTES, 0, MESSAGE_LENGTH_BYTES))
            .addLast(new LengthFieldPrepender(MESSAGE_LENGTH_BYTES))
            .addLast(new ObjectEncoder())
            .addLast(new ObjectDecoder(
                Integer.MAX_VALUE, ClassResolvers.weakCachingResolver(this.getClass().getClassLoader())
            ))
            .addLast(new MessageSendHandler());
        ;
    }
}
