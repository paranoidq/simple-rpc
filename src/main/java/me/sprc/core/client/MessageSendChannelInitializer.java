package me.sprc.core.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageSendChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * ObjectDecoder 底层默认继承半包解码器LengthFiledBasedFrameDecoder处理TCP粘包问题的时候，
     * 消息头开始极为长度字段，占据4个字节。
     */
    public static final int MESSAGE_LENGTH = 4;

    /**
     * This method will be called once the {@link Channel} was registered. After the method returns this instance
     * will be removed from the {@link ChannelPipeline} of the {@link Channel}.
     *
     * @param ch the {@link Channel} which was registered.
     * @throws Exception is thrown if an error occurs. In that case the {@link Channel} will be closed.
     */
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, MESSAGE_LENGTH, 0, MESSAGE_LENGTH));
        // 利用LengthFiledPrepender回填补充ObjectDecoder消息报文头
        pipeline.addLast(new LengthFieldPrepender(MESSAGE_LENGTH));
        pipeline.addLast(new ObjectEncoder());
        // 考虑到并发性能，采用weakCachingConcurrentResolver缓存策略，一般情况下，使用cacheDisabled即可
        pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE,
            ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
        pipeline.addLast(new MessageSendHandler());
    }
}
