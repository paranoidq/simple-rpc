package me.framework.rpc.core.client;

import com.google.common.util.concurrent.*;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import me.framework.rpc.serialize.support.RpcSerializeProtocol;
import me.framework.rpc.util.pool.RpcThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class RpcServerLoader {

    private static final Logger logger = LoggerFactory.getLogger(RpcServerLoader.class);

    private static volatile RpcServerLoader instance;

    private final static String DELIMITER = ":";

    private final static int parallel = Runtime.getRuntime().availableProcessors() * 2;
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(parallel);

    private static ListeningExecutorService threadPoolExecutor = MoreExecutors.listeningDecorator(
        (ThreadPoolExecutor) RpcThreadPool.getExecutor(16, -1));

    private volatile MessageSendHandler sendHandler;

    private Lock lock = new ReentrantLock();
    private Condition connectStatus = lock.newCondition();
    private Condition handlerStatus = lock.newCondition();


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

            ListenableFuture<Boolean> listenableFuture = threadPoolExecutor.submit(
                new MessageSendInitializeTask(eventLoopGroup, remoteAddr, serializeProtocol)
            );
            Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    logger.info("连接服务端成功");
                    try {
                        lock.lock();
                         if (sendHandler == null) {
                             handlerStatus.await();
                         }

                         if (result == Boolean.TRUE && sendHandler != null) {
                            connectStatus.signalAll();
                         }
                    } catch (InterruptedException e) {

                    } finally {
                        lock.unlock();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    logger.error("连接失败", t);
                }
            }, threadPoolExecutor);
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
            handlerStatus.signalAll();
        } finally {
            lock.unlock();
        }
    }


    public MessageSendHandler getSendHandler() throws InterruptedException {
        try {
            lock.lock();
            if (sendHandler == null) {
                connectStatus.await();
            }
            return sendHandler;
        } finally {
            lock.unlock();
        }
    }

}
