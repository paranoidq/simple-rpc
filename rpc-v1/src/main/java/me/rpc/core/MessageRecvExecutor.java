package me.rpc.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import me.rpc.core.channel.MessageRecvChannelInitializer;
import me.rpc.model.ServiceHolder;
import me.rpc.netty.NettyServerBootstrapBuilder;
import me.rpc.pool.NamedThreadFactory;
import me.rpc.pool.RpcThreadPool;
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
public class MessageRecvExecutor implements ApplicationContextAware, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MessageRecvExecutor.class);

    private String serverAddress;
    private final static String DELIMITER = ":";

    private static Map<String, Object> handlerMap = new ConcurrentHashMap<>();
    private static volatile ThreadPoolExecutor threadPoolExecutor;

    public MessageRecvExecutor(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public static void submit(Runnable task) {
        if (threadPoolExecutor == null) {
            synchronized (MessageRecvExecutor.class) {
                if (threadPoolExecutor == null) {
                   threadPoolExecutor = (ThreadPoolExecutor) RpcThreadPool.getExecutor(16, -1);
                }
            }
        }
        threadPoolExecutor.submit(task);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        ThreadFactory threadFactory = new NamedThreadFactory("Netty RPC Factory");
        int parallel = Runtime.getRuntime().availableProcessors() * 2;
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup(parallel,threadFactory, SelectorProvider.provider());

        try {
            ServerBootstrap bootstrap = NettyServerBootstrapBuilder.getInstance(boss, worker)
                .setAcceptSocketsMax(128)
                .build();
            bootstrap.childHandler(new MessageRecvChannelInitializer(handlerMap));

            String[] ipAddr = serverAddress.split(MessageRecvExecutor.DELIMITER);
            if (ipAddr.length == 2) {
                String host = ipAddr[0];
                int port = Integer.parseInt(ipAddr[1]);
                ChannelFuture future = bootstrap.bind(host, port).sync();
                System.out.printf("[author qianwei] Netty RPC Server get success ip:%s port:%d\n", host, port);
                future.channel().closeFuture().sync();
            } else {
                System.out.printf("[author qianwei] Netty RPC Server get fail!\n");
            }
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            ServiceHolder holder = (ServiceHolder) applicationContext.getBean(Class.forName(
                "me.rpc.model.ServiceHolder"
            ));
            Map<String, Object> services = holder.getServices();
            for (Map.Entry<String, Object> service : services.entrySet()) {
                handlerMap.put(service.getKey(), service.getValue());
            }
        } catch (ClassNotFoundException e) {
            logger.error("", e);
        }
    }
}
