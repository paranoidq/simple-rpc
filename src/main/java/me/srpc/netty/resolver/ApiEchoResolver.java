package me.srpc.netty.resolver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.util.concurrent.Callable;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ApiEchoResolver implements Callable<Boolean> {
    private static final boolean SSL = System.getProperty("ssl") != null;
    private String host;
    private int port;

    public ApiEchoResolver(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Boolean call() throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            SslContext sslContext = null;
            if (SSL) {
                SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
                sslContext = SslContextBuilder.forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey()).build();
            }

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ApiEchoInitializer(sslContext));

            Channel ch = bootstrap.bind(port).sync().channel();
            System.err.println("You can open your web browser to see Netty RPC server api interface: " +
                (SSL ? "https" : "http") + "://"  + host + ":" + port + "/NettyRPC.html" );

            ch.closeFuture().sync();
            return Boolean.TRUE;
        } catch (Exception e)  {
            e.printStackTrace();
            return Boolean.FALSE;
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }
}
