package me.rpc.core;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import me.rpc.pool.RpcThreadPool;

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

    private static volatile RpcServerLoader instance;

    private final static String DELIMITER = ":";

    private RpcSerializeProtocol serializeProtocol = RpcSerializeProtocol.JDK_SERIALIZE;

    private final static int parallel = Runtime.getRuntime().availableProcessors() * 2;
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(parallel);
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) RpcThreadPool.getExecutor(16, -1);
    private MessageSendHandler sendHandler;

    private Lock lock = new ReentrantLock();
    private Condition signal = lock.newCondition();

    public static RpcServerLoader getInstance() {
        if (instance == null) {
            synchronized (RpcServerLoader.class) {
                if (instance == null) {
                    instance = new RpcServerLoader();
                }
            }
        }
        return instance;
    }

    public void load(String serverAddress, RpcSerializeProtocol serializeProtocol) {
        String[] ipAddr = serverAddress.split(DELIMITER);
        if (ipAddr.length == 2) {
             String host = ipAddr[0];
            int port = Integer.parseInt(ipAddr[1]);
            final InetSocketAddress remoteAddr = new InetSocketAddress(host, port);
            threadPoolExecutor.submit(
                new MessageSendInitializeTask(eventLoopGroup, remoteAddr, this)
            );
        }
    }

    public void unload() {
        sendHandler.close();
        threadPoolExecutor.shutdown();
        eventLoopGroup.shutdownGracefully();
    }


    public void setMessageSendHandler(MessageSendHandler sendHandler) {
        try {
            lock.lock();
            this.sendHandler = sendHandler;
            signal.signalAll();
        } finally {
            lock.unlock();
        }
    }


    public MessageSendHandler getSendHandler() throws InterruptedException {
        try {
            lock.lock();
            if (sendHandler == null) {
                signal.await();
            }
            return sendHandler;
        } finally {
            lock.unlock();
        }
    }

    public void setSerializeProtocol(RpcSerializeProtocol serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
    }
}
