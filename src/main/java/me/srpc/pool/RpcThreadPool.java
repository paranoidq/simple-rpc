package me.srpc.pool;

import me.srpc.common.RpcSystemConfig;
import me.srpc.jmx.ThreadPoolMonitorProvider;
import me.srpc.jmx.ThreadPoolStatus;
import me.srpc.pool.policy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * SRpc Worker thread pool
 *
 * 独立出工作线程池，主要考虑对应对复杂的业务操作，不阻塞netty的handler IO线程
 * 当然如果业务足够简单，可以将处理逻辑写入netty的handler中也是可以的
 *
 * @author paranoidq
 * @since 1.0.0
 */
public class RpcThreadPool {
    private static Logger logger = LoggerFactory.getLogger(RpcThreadPool.class);

    private static long monitorDelay = 100L;
    private static long monitorPeriod = 300L;


    private static RejectedExecutionHandler createPolicy() {
        RejectPolicyType rejectPolicyType = RejectPolicyType.fromString(
            System.getProperty(RpcSystemConfig.SystemPropertyThreadPoolRejectedPolicyAttr, "AbortPolicy"));

        switch (rejectPolicyType) {
            case BLOCKING_POLICY:
                return new BlockingPolicy();
            case CALLER_RUNS_POLICY:
                return new CallerRunsPolicy();
            case ABORT_POLICY:
                return new AbortPolicy();
            case REJECTED_POLICY:
                return new RejectedPolicy();
            case DISCARDED_POLICY:
                return new DiscardedPolicy();
            default:
                return new CallerRunsPolicy();
        }
    }

    private static BlockingQueue<Runnable> createBlockingQueue(int queues) {
        BlockingQueueType queueType = BlockingQueueType.fromString(
            System.getProperty(RpcSystemConfig.SystemPropertyThreadPoolQueueNameyAttr, "LinkedBlockingQueue"));
        switch (queueType) {
            case LINKED_BLOCKING_QUEUE:
                return new LinkedBlockingDeque<Runnable>();
            case ARRAY_BLOCKING_QUEUE:
                return new ArrayBlockingQueue<Runnable>(RpcSystemConfig.PARALLEL * queues);
            case SYNCHRONOUS_QUEUE:
                return new SynchronousQueue<Runnable>();
            default:
                return new LinkedBlockingDeque<Runnable>();
        }
    }


    public static Executor getExecutor(int threads, int queues) {
        String name = "RpcThreadPool";
        return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
            createBlockingQueue(queues),
            new NamedThreadFactory(name, true),
            createPolicy()
            );
    }

    public static Executor getExecutorWithJmx(int threads, int queues) {
        final ThreadPoolExecutor executor = (ThreadPoolExecutor) getExecutor(threads, queues);
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            ThreadPoolStatus status = new ThreadPoolStatus();
            status.setPoolSize(executor.getPoolSize());
            status.setActiveCount(executor.getActiveCount());
            status.setCorePoolSize(executor.getCorePoolSize());
            status.setMaximumPoolSize(executor.getMaximumPoolSize());
            status.setLargestPoolSize(executor.getLargestPoolSize());
            status.setTaskCount(executor.getTaskCount());
            status.setCompletedTaskCount(executor.getCompletedTaskCount());

            try {
                ThreadPoolMonitorProvider.provide(status);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, monitorDelay, monitorPeriod, TimeUnit.MILLISECONDS);
        return executor;
    }
}
