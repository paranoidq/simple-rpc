package me.srpc.pool.policy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class CallerRunsPolicy extends ThreadPoolExecutor.CallerRunsPolicy {
    private static final Logger logger = LoggerFactory.getLogger(CallerRunsPolicy.class);
    private final String threadName;

    public CallerRunsPolicy(String threadName) {
        this.threadName = threadName;
    }

    public CallerRunsPolicy() {
        this(null);
    }

    /**
     * Executes task r in the caller's thread, unless the executor
     * has been shut down, in which case the task is discarded.
     *
     * @param r the runnable task requested to be executed
     * @param executor the executor attempting to execute this task
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (!StringUtils.isEmpty(threadName)) {
            logger.error("RPC Thread pool [{}] exhausted, executor={}", threadName, executor.toString());
        }
        super.rejectedExecution(r, executor);
    }
}
