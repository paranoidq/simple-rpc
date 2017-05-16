package me.srpc.netty.client;

import com.google.common.util.concurrent.*;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import me.srpc.pool.RpcThreadPool;
import me.srpc.serialize.RpcSerializeProtocol;

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

    private final static int parallel = Runtime.getRuntime().availableProcessors() * 2;
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(parallel);
    private static ListeningExecutorService threadPoolExecutor = MoreExecutors.listeningDecorator((
        ThreadPoolExecutor) RpcThreadPool.getExecutor(16, -1));
    private MessageSendHandler messageSendHandler = null;


    // 等待netty服务端链路建立的通知信号
    private Lock lock = new ReentrantLock();
    private Condition connectStatus = lock.newCondition();
    private Condition handlerStatus = lock.newCondition();

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
            ListenableFuture<Boolean> listenableFuture = threadPoolExecutor.submit(new MessageSendInitializeTask(
                eventLoopGroup, remoteAddress, serializeProtocol));

            Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    try {
                        lock.lock();

                        // 在通知等待connect完毕的发送线程时，必须先确保messageHandler已经设置完毕了
                        if (messageSendHandler == null) {
                            handlerStatus.await();
                        }

                        // 如果有发送线程阻塞在getMessangerSendHandler处，那么通过signalAll解除阻塞
                        if (result == Boolean.TRUE && messageSendHandler != null) {
                            connectStatus.signalAll();
                        }
                    } catch (InterruptedException e) {

                    } finally {
                        lock.unlock();
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            }, threadPoolExecutor);
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
            handlerStatus.signal();
        } finally {
            lock.unlock();
        }
    }

    public MessageSendHandler getMessageSendHandler() throws InterruptedException {
        try {
            lock.lock();
            if (messageSendHandler == null) {
                // TODO ?? 永久等待Netty服务端链路建立完毕
                connectStatus.await();
            }
            return messageSendHandler;
        } finally {
            lock.unlock();
        }
    }
}
