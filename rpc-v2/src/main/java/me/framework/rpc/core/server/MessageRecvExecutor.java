package me.framework.rpc.core.server;

import com.google.common.util.concurrent.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import me.framework.rpc.model.MessageRequest;
import me.framework.rpc.model.MessageResponse;
import me.framework.rpc.model.ServiceHolder;
import me.framework.rpc.serialize.support.RpcSerializeProtocol;
import me.framework.rpc.util.nettybuilder.NettyServerBootstrapBuilder;
import me.framework.rpc.util.pool.NamedThreadFactory;
import me.framework.rpc.util.pool.RpcThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.nio.channels.spi.SelectorProvider;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageRecvExecutor implements ApplicationContextAware, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MessageRecvExecutor.class);

    private final static String DELIMITER = ":";

    private static Map<String, Object> handlerMap = new ConcurrentHashMap<>();
    private static volatile ListeningExecutorService threadPoolExecutor;

    private String serverAddress;
    // 默认JDK序列化方式
    private RpcSerializeProtocol protocol = RpcSerializeProtocol.JDK_SERIALIZE;


    public MessageRecvExecutor(String serverAddress, String protocol) {
        this.serverAddress = serverAddress;
        this.protocol = Enum.valueOf(RpcSerializeProtocol.class, protocol);
    }


    /**
     * 提交请求去进行业务处理
     * @param task
     * @param ctx
     * @param request
     * @param response
     */
    public static void submit(Callable<Boolean> task, ChannelHandlerContext ctx, MessageRequest request, MessageResponse response) {
        if (threadPoolExecutor == null) {
            synchronized (MessageRecvExecutor.class) {
                if (threadPoolExecutor == null) {
                   threadPoolExecutor = MoreExecutors.listeningDecorator((ThreadPoolExecutor) RpcThreadPool.getExecutor(16, -1));
                }
            }
        }
        ListenableFuture<Boolean> listenableFuture = threadPoolExecutor.submit(task);

        // rpc-v2通过listeningFuture移除了v1中callback，更加简洁、高效
        Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        logger.info("RPC Server send response. messageId=[{}]", request.getMessageId());
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                logger.error("", t);
            }
        }, threadPoolExecutor);
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
            bootstrap.childHandler(new MessageRecvChannelInitializer(handlerMap)
                .setSerializeProtocol(protocol));

            String[] ipAddr = serverAddress.split(MessageRecvExecutor.DELIMITER);
            if (ipAddr.length == 2) {
                String host = ipAddr[0];
                int port = Integer.parseInt(ipAddr[1]);
                ChannelFuture future = bootstrap.bind(host, port);
                System.out.printf("Netty RPC Server started success ip:%s port:%d\n", host, port);
                future.channel().closeFuture().sync();
            } else {
                System.out.printf("Netty RPC Server started fail!\n");
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
                "me.framework.rpc.model.ServiceHolder"
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
