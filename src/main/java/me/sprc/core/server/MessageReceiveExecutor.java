package me.sprc.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.sprc.common.Constants;
import me.sprc.core.RpcThreadPool;
import me.sprc.model.ServiceBeanHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.nio.channels.spi.SelectorProvider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageReceiveExecutor implements ApplicationContextAware, InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(MessageReceiveExecutor.class);

    private final static String DELIMITER = Constants.SEMICOLON;

    private String serverAddress;

    private Map<String, Object> handlerMap = new ConcurrentHashMap<String, Object>();

    private static ThreadPoolExecutor threadPoolExecutor;

    public MessageReceiveExecutor(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public static void submit(Runnable task) {
        if (threadPoolExecutor == null) {
            synchronized (MessageReceiveExecutor.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = (ThreadPoolExecutor) RpcThreadPool.getExecutor(16, -1);
                }
            }
        }
        threadPoolExecutor.submit(task);
    }

    public void afterPropertiesSet() throws Exception {
        ThreadFactory nettyRpcThreadFactory = new RpcThreadPool.NamedThreadFactory("NettyRpcThreadFactory");

        int parallel = Runtime.getRuntime().availableProcessors() * 2;

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup(parallel, nettyRpcThreadFactory, SelectorProvider.provider());
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                .childHandler(new MessageReceiveChannelInitializer(handlerMap))
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            String[] ipAddress = serverAddress.split(DELIMITER);
            if (ipAddress.length == 2) {
                String host = ipAddress[0];
                int port = Integer.parseInt(ipAddress[1]);
                ChannelFuture future = bootstrap.bind(host, port).sync();
                logger.info("Netty RPC Server start success! ip:{}, port:{}\n", host, port);
                future.channel().closeFuture().sync();
            } else {
                logger.error("Netty RPC Server start failed!");
            }
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            ServiceBeanHolder keyVal = (ServiceBeanHolder) applicationContext.getBean(
                Class.forName("me.sprc.model.ServiceBeanHolder"));
            Map<String, Object> rpcServiceObject = keyVal.getServiceHolder();

            for (Map.Entry<String, Object> entry : rpcServiceObject.entrySet()) {
                handlerMap.put(entry.getKey(), entry.getValue());
            }
        } catch (ClassNotFoundException e) {
            logger.error("", e);
        }
    }
}
