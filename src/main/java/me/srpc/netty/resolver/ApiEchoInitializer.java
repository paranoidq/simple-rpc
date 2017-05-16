package me.srpc.netty.resolver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ApiEchoInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslContext;

    public ApiEchoInitializer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();
        if (sslContext != null) {
            p.addLast(sslContext.newHandler(socketChannel.alloc()));  // ??
        }
        p.addLast(new HttpServerCodec()); // ??
        p.addLast(new ApiEchoHandler());
    }
}
