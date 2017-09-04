package me.rpc.pool.policy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class AbortPolicy extends ThreadPoolExecutor.AbortPolicy {
    private static final Logger logger = LoggerFactory.getLogger(AbortPolicy.class);
    private final String threadName;

    public AbortPolicy(String threadName) {
        this.threadName = threadName;
    }

    public AbortPolicy() {
        this(null);
    }

    /**
     * Always throws RejectedExecutionException.
     *
     * @param r the runnable task requested to be executed
     * @param e the executor attempting to execute this task
     * @throws RejectedExecutionException always
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (!StringUtils.isEmpty(threadName)) {
            logger.error("RPC thread pool [{}] is exhausted, executor={}", threadName, executor.toString());
        }

        String msg = String.format("Rpc Server[" +
            "Thread name: %sm Pool Size: %d (active: %d, netty: %d, max: %d, largest: %d), " +
            "Task: %d (completed %d), " +
            "Executor status: (isShutdown:%s, isTerminated:%s, isTerminating:%s)]",
            threadName, executor.getPoolSize(), executor.getActiveCount(), executor.getMaximumPoolSize(), executor.getLargestPoolSize(),
            executor.isShutdown(), executor.isTerminated(), executor.isTerminating());
        logger.error(msg);
        super.rejectedExecution(r, executor);
    }
}
