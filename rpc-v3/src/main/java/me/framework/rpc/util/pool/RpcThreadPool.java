package me.framework.rpc.util.pool;

import com.sun.org.apache.regexp.internal.RE;
import me.framework.rpc.config.RpcSystemConfig;
import me.framework.rpc.util.pool.policy.*;
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
            default:
                return null;
        }
    }

    private static BlockingQueue<Runnable> createBlockingQueue(int queues) {
        BlockingQueueType queueType = BlockingQueueType.fromString(
            System.getProperty(RpcSystemConfig.SystemPropertyThreadPoolQueueNameAttr,
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
}
