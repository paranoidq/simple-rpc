package me.srpc.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.srpc.common.Constants;
import me.srpc.common.RpcSystemConfig;
import me.srpc.compiler.AccessAdaptiveProvider;
import me.srpc.api.AbilityDetailProvider;
import me.srpc.netty.resolver.ApiEchoResolver;
import me.srpc.pool.NamedThreadFactory;
import me.srpc.serialize.RpcSerializeProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class NettyRpcServer {
    private static Logger logger = LoggerFactory.getLogger(MessageReceiveExecutor.class);

    private String serverAddress;
    private int echoApiPort;
    private RpcSerializeProtocol protocol;
    private final static String DELIMITER = Constants.COLON;
    private static final int numberOfEchoThreadPool = 1;
    private static final int parallel = RpcSystemConfig.PARALLEL * 2;

    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup worker = new NioEventLoopGroup(parallel, new NamedThreadFactory("NettyRpc ThreadFactory"), SelectorProvider.provider());

    private NettyRpcServer() {
        register();
    }

    private static final class Holder {
        private static final NettyRpcServer INSTANCE = new NettyRpcServer();
    }
    public static NettyRpcServer getInstance() {
        return Holder.INSTANCE;
    }


    public final void start() {
        ServiceHolder.print();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                .childHandler(new MessageReceiveChannelInitializer().buildRpcSerializeProtocol(protocol))
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
            ;

            String[] ipAddr = serverAddress.split(DELIMITER);
            if (ipAddr.length == 2) {
                String host = ipAddr[0];
                int port = Integer.parseInt(ipAddr[1]);
                ChannelFuture future = bootstrap.bind(host, port).sync();

                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            final ExecutorService executor = Executors.newFixedThreadPool(numberOfEchoThreadPool);
                            ExecutorCompletionService<Boolean> completionService = new ExecutorCompletionService<Boolean>(executor);
                            completionService.submit(new ApiEchoResolver(host, port));
                            System.out.printf("Netty RPC Server start success!\nip:%s\nport:%d\nprotocol:%s\n\n", host, port, protocol);
                            future.channel().closeFuture().sync().addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) throws Exception {
                                    executor.shutdownNow();
                                }
                            });
                        }
                    }
                });
            } else {
                logger.error("Netty Rpc Server start failed!\n");
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public final void stop() {
        worker.shutdownGracefully();
        boss.shutdownGracefully();
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getEchoApiPort() {
        return echoApiPort;
    }

    public void setEchoApiPort(int echoApiPort) {
        this.echoApiPort = echoApiPort;
    }

    public RpcSerializeProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(RpcSerializeProtocol protocol) {
        this.protocol = protocol;
    }

    public void register() {
        ServiceHolder.addService(RpcSystemConfig.RPC_COMPILER_SPI_ATTR, new AccessAdaptiveProvider());
        ServiceHolder.addService(RpcSystemConfig.RPC_ABILITY_DETAIL_SPI_ATTR, new AbilityDetailProvider());
    }
}
