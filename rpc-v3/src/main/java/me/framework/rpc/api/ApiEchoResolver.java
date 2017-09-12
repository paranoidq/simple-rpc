package me.framework.rpc.api;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import me.framework.rpc.logger.AppLoggerInject;
import me.framework.rpc.util.nettybuilder.NettyServerBootstrapBuilder;
import org.slf4j.Logger;

import java.util.concurrent.Callable;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ApiEchoResolver implements Callable<Boolean> {

    @AppLoggerInject
    private static Logger logger;

    private static final boolean SSL = System.getProperty("ssl") != null;

    private String host;
    private int port;

    public ApiEchoResolver(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Boolean call() throws Exception {
        ServerBootstrap serverBootstrap = NettyServerBootstrapBuilder.getInstance()
            .setAcceptEventLoopThreads(1)
            .setSoBacklog(1024)
            .build();
        try {
            SslContext sslContext = null;
            if (SSL) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            }
            serverBootstrap.handler(new LoggingHandler())
                .childHandler(new ApiEchoInitializer(sslContext));

            Channel ch = serverBootstrap.bind(port).sync().channel();

            System.err.println("You can open your web browser see NettyRPC server api interface: " +
                (SSL ? "https" : "http") + "://" + host + ":" + port + "/NettyRPC.html");

            ch.closeFuture().sync();
            return Boolean.TRUE;
        } catch (Exception e) {
            logger.error("", e);
            return Boolean.FALSE;
        } finally {
            serverBootstrap.group().shutdownGracefully();
            serverBootstrap.childGroup().shutdownGracefully();
        }

    }
}
