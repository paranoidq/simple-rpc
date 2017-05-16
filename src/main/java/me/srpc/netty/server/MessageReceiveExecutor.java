package me.srpc.netty.server;

import com.google.common.util.concurrent.*;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import me.srpc.common.RpcSystemConfig;
import me.srpc.model.MessageRequest;
import me.srpc.model.MessageResponse;
import me.srpc.pool.RpcThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author paranoidq
 * @since 1.0.0
 */
class MessageReceiveExecutor {
    private static Logger logger = LoggerFactory.getLogger(MessageReceiveExecutor.class);

    private volatile static ListeningExecutorService threadPoolExecutor;
    private static int threadNum = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS;
    private static int queueNum = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS;

    private static class Holder {
        static final MessageReceiveExecutor instance = new MessageReceiveExecutor();
    }
    public static MessageReceiveExecutor getInstance() {
        return Holder.instance;
    }


    /**
     * 将请求报文提交给后台线程池执行
     *
     * @param task
     * @param ctx
     * @param request
     * @param response
     */
    public void submit(Callable<Boolean> task, final ChannelHandlerContext ctx, final MessageRequest request, final MessageResponse response) {
        initIfNecessary();

        ListenableFuture<Boolean> listenableFuture = threadPoolExecutor.submit(task);
        Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        logger.info("Netty RPC Server send message response. messageId: {}", request.getMessageId());
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                logger.error("", t);
            }
        }, threadPoolExecutor);
    }

    private static void initIfNecessary() {
        if (threadPoolExecutor == null) {
            synchronized (MessageReceiveExecutor.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = MoreExecutors.listeningDecorator(
                        (ThreadPoolExecutor)
                            (RpcSystemConfig.isMonitorServerSupport()
                                ? RpcThreadPool.getExecutorWithJmx(threadNum, queueNum)
                                : RpcThreadPool.getExecutor(threadNum, queueNum))
                    );
                }
            }
        }
    }
}
