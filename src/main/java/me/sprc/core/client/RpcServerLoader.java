package me.sprc.core.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import me.sprc.core.RpcSerializeProtocol;
import me.sprc.core.RpcThreadPool;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class RpcServerLoader {

    private volatile static RpcServerLoader rpcServerLoader;
    private final static String DELIMITER = ":";
    private RpcSerializeProtocol serializeProtocol = RpcSerializeProtocol.JDK_SERIALIZE;

    private final static int parallel = Runtime.getRuntime().availableProcessors() * 2;
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(parallel);
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) RpcThreadPool.getExecutor(16, -1);
    private MessageSendHandler messageSendHandler = null;


    // 等待netty服务端链路建立的通知信号
    private Lock lock = new ReentrantLock();
    private Condition signal = lock.newCondition();

    /**
     * Private constructor for lazy initialization
     */
    private RpcServerLoader(){}

    public static RpcServerLoader getInstance() {
        if (rpcServerLoader == null) {
            synchronized (RpcServerLoader.class) {
                if (rpcServerLoader == null) {
                    rpcServerLoader = new RpcServerLoader();
                }
            }
        }
        return rpcServerLoader;
    }

    public void load(String serverAddress) {
        this.load(serverAddress, RpcSerializeProtocol.JDK_SERIALIZE);
    }

    public void load(String serverAddress, RpcSerializeProtocol serializeProtocol) {
        String[] ipAddress = serverAddress.split(DELIMITER);
        if (ipAddress.length == 2) {
            String host = ipAddress[0];
            int port = Integer.parseInt(ipAddress[1]);
            final InetSocketAddress remoteAddress  = new InetSocketAddress(host, port);
            threadPoolExecutor.submit(new MessageSendInitializeTask(
                eventLoopGroup, remoteAddress, this));
        }
    }

    public void unload() {
        messageSendHandler.close();
        threadPoolExecutor.shutdown();
        eventLoopGroup.shutdownGracefully();
    }

    public void setMessageSendHandler(MessageSendHandler messageSendHandler) {
        try {
            lock.lock();
            this.messageSendHandler = messageSendHandler;
            // 唤醒所有等待客户端的RPC线程
            signal.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public MessageSendHandler getMessageSendHandler() throws InterruptedException {
        try {
            lock.lock();
            if (messageSendHandler == null) {
                // TODO ?? 永久等待Netty服务端链路建立完毕
                signal.await();
            }
            return messageSendHandler;
        } finally {
            lock.unlock();
        }
    }

    public void setSerializeProtocol(RpcSerializeProtocol serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
    }


}
