package me.framework.rpc.util.pool;

import me.framework.rpc.config.RpcSystemConfig;
import me.framework.rpc.jmx.ThreadPoolMonitorProvider;
import me.framework.rpc.jmx.ThreadPoolStatus;
import me.framework.rpc.util.pool.policy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
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

    /**
     * 用于进行JMX监控的定时器，
     * 设置为守护线程
     */
    private static final Timer timer = new Timer("ThreadPoolMonitor", true);
    private static long monitorDelay = 100L;
    public static long monitorPeriod = 300L;

    private static RejectedExecutionHandler createPolicy() {
        RejectPolicyType rejectPolicyType = RejectPolicyType.fromString(
            System.getProperty(RpcSystemConfig.SystemPropertyThreadPoolRejectedPolicyAttr,
                "AbortPolicy"));
        switch (rejectPolicyType) {
            case BLOCKING_POLICY:
                return new BlockingPolicy();
            case CALLER_RUNS_POLICY:
                return new CallerRunsPolicy();
            case REJECTED_POLICY:
                return new RejectedPolicy();
            case DISCARDED_POLICY:
                return new DiscardedPolicy();
            case ABORT_POLICY:
                return new AbortPolicy();
            default:
                return null;
        }
    }

    private static BlockingQueue<Runnable> createBlockingQueue(int queues) {
        BlockingQueueType queueType = BlockingQueueType.fromString(
            System.getProperty(RpcSystemConfig.SystemPropertyThreadPoolQueueTypeAttr,
                "LinkedBlockingQueue"));

        switch (queueType) {
            case LINKED_BLOCKING_QUEUE:
                return new LinkedBlockingQueue<>();
            case ARRAY_BLOCKING_QUEUE:
                return new ArrayBlockingQueue<Runnable>(RpcSystemConfig.PARALLEL * queues);
            case SYNCHRONOUS_QUEUE:
                return new SynchronousQueue<>();
            default:
                return null;
        }
    }

    /**
     * 获取并发执行器
     * @param threads
     * @param queues
     * @return
     */
    public static Executor getExecutor(int threads, int queues) {
        String name = "RpcThreadPool";
        return new ThreadPoolExecutor(
            threads,
            threads,
            0,
            TimeUnit.MILLISECONDS,
            createBlockingQueue(queues),
            new NamedThreadFactory(name, true),
            createPolicy()
            );
    }

    public static Executor getExecutorWithJmx(int threads, int queues){
        final ThreadPoolExecutor executor = (ThreadPoolExecutor) getExecutor(threads, queues);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // 将线程池的状态写入me.framework.rpc.jmx.ThreadPoolStatus中
                // 监控线程定时刷新ThreadPoolStatus
                ThreadPoolStatus status = new ThreadPoolStatus();
                status.setPoolSize(executor.getPoolSize());
                status.setActiveCount(executor.getActiveCount());
                status.setCorePoolSize(executor.getCorePoolSize());
                status.setMaximumPoolSize(executor.getMaximumPoolSize());
                status.setLargestPoolSize(executor.getLargestPoolSize());
                status.setTaskCount(executor.getTaskCount());
                status.setCompletedTaskCount(executor.getCompletedTaskCount());
                try {
                    // 通过spring提供的JXM接口注册ThreadPoolStatus
                    ThreadPoolMonitorProvider.monitor(status);
                } catch (Throwable t) {
                    logger.error("Register ThreadPoolStatus failed!", t);
                }
            }
        }, monitorDelay, monitorPeriod);
        return executor;
    }
}
