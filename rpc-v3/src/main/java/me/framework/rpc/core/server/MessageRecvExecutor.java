package me.framework.rpc.core.server;

import com.google.common.util.concurrent.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import me.framework.rpc.config.RpcSystemConfig;
import me.framework.rpc.model.MessageRequest;
import me.framework.rpc.model.MessageResponse;
import me.framework.rpc.model.ServiceHolder;
import me.framework.rpc.serialize.support.RpcSerializeProtocol;
import me.framework.rpc.spring.NettyRpcRegistry;
import me.framework.rpc.util.nettybuilder.NettyServerBootstrapBuilder;
import me.framework.rpc.util.pool.NamedThreadFactory;
import me.framework.rpc.util.pool.RpcThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.nio.channels.spi.SelectorProvider;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * v3 版本引入了spring之后，不在需要通过手动调用start进行启动了
 *
 * 启动在{@link NettyRpcRegistry#afterPropertiesSet()}中进行
 * 该函数会首先从配置文件中获取对应的参数，并注入到{@link MessageRecvExecutor}实例中，
 * 然后调用{@link MessageRecvExecutor#start()}启动线程池，坚挺即将到来的请求
 *
 * {@link me.framework.rpc.core.client.MessageSendExecutor}同理
 *
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageRecvExecutor implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(MessageRecvExecutor.class);
    private final static String DELIMITER = ":";

    /**
     * 存储RPC服务映射关系
     * <ClassName --> Object Instance>
     */
    private Map<String, Object> handlerMap = new ConcurrentHashMap<>();

    /**
     * 通过spring注入方式设置具体值
     * 参见：{@link me.framework.rpc.spring.NettyRpcRegistry}
     */
    private String serverAddress;
    /**
     * 通过spring注入方式设置具体值
     * 参见：{@link me.framework.rpc.spring.NettyRpcRegistry}
     */
    private RpcSerializeProtocol serializeProtocol = RpcSerializeProtocol.JDK_SERIALIZE;

    /**
     * 通过spring注入方式设置具体值
     * 参见：{@link me.framework.rpc.spring.NettyRpcRegistry}
     */
    private String echoApiPort;

    /**
     * 并发执行executor
     */
    private volatile ListeningExecutorService threadPoolExecutor;

    /**
     * 线程池线程数
     */
    private static int threadNums = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS;

    /**
     * 线程池队列长度
     */
    private static int queueNums = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS;

    private ThreadFactory threadFactory = new NamedThreadFactory("Netty RPC Factory");
    private int parallel = Runtime.getRuntime().availableProcessors() * 2;
    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup worker = new NioEventLoopGroup(parallel,threadFactory, SelectorProvider.provider());


    public MessageRecvExecutor() {
        handlerMap.clear();
    }

    private static class Holder {
        private static final MessageRecvExecutor instance = new MessageRecvExecutor();
    }
    public static MessageRecvExecutor getInstance() {
        return Holder.instance;
    }

    /**
     * 提交请求去进行业务处理
     * @param task
     * @param ctx
     * @param request
     * @param response
     */
    public void submit(Callable<Boolean> task, ChannelHandlerContext ctx, MessageRequest request, MessageResponse response) {
        if (threadPoolExecutor == null) {
            synchronized (MessageRecvExecutor.class) {
                if (threadPoolExecutor == null) {
                   threadPoolExecutor =
                       MoreExecutors.listeningDecorator((ThreadPoolExecutor)
                           (RpcSystemConfig.isMonitorServerSupport()
                               ? RpcThreadPool.getExecutorWithJmx(threadNums, queueNums)
                               : RpcThreadPool.getExecutor(threadNums, queueNums)
                           )
                       );
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
            logger.error("未找到ServiceHolder类", e);
        }
    }

    public void start() {
        try {
            ServerBootstrap bootstrap = NettyServerBootstrapBuilder.getInstance(boss, worker)
                .setAcceptSocketsMax(128)
                .build();
            bootstrap.childHandler(new MessageRecvChannelInitializer(handlerMap)
                .setSerializeProtocol(serializeProtocol));

            String[] ipAddr = serverAddress.split(MessageRecvExecutor.DELIMITER);
            if (ipAddr.length == 2) {
                String host = ipAddr[0];
                int port = Integer.parseInt(ipAddr[1]);
                ChannelFuture future = bootstrap.bind(host, port).sync();

                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            System.out.printf("Netty RPC Server started success ip:%s port:%d\n", host, port);
//                            future.channel().closeFuture().sync().addListener(new ChannelFutureListener() {
//                                @Override
//                                public void operationComplete(ChannelFuture future) throws Exception {
//                                    threadPoolExecutor.shutdown();
//                                }
//                            });
                        }
                    }
                });
            } else {
                logger.error("Netty RPC Server started fail!\n");
            }
        } catch (InterruptedException e) {
            logger.error("Netty RPC Server started fail!\n", e);
        }
    }


    public void stop() {
        worker.shutdownGracefully();
        boss.shutdownGracefully();
    }

    public Map<String, Object> getHandlerMap() {
        return handlerMap;
    }

    public void setHandlerMap(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RpcSerializeProtocol getSerializeProtocol() {
        return serializeProtocol;
    }

    public void setSerializeProtocol(RpcSerializeProtocol serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
    }

    public String getEchoApiPort() {
        return echoApiPort;
    }

    public void setEchoApiPort(String echoApiPort) {
        this.echoApiPort = echoApiPort;
    }
}
