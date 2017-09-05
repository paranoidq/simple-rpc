package me.framework.rpc.util.pool.policy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class RejectedPolicy implements RejectedExecutionHandler {
    private static Logger logger = LoggerFactory.getLogger(RejectedPolicy.class);
    private final String threadName;

    public RejectedPolicy(String threadName) {
        this.threadName = threadName;
    }

    public RejectedPolicy() {
        this(null);
    }

    /**
     * Method that may be invoked by a {@link ThreadPoolExecutor} when
     * {@link ThreadPoolExecutor#execute execute} cannot accept a
     * task.  This may occur when no more threads or queue slots are
     * available because their bounds would be exceeded, or upon
     * shutdown of the Executor.
     * <p>
     * <p>In the absence of other alternatives, the method may throw
     * an unchecked {@link RejectedExecutionException}, which will be
     * propagated to the caller of {@code execute}.
     *
     * @param r        the runnable task requested to be executed
     * @param executor the executor attempting to execute this task
     * @throws RejectedExecutionException if there is no remedy
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (!StringUtils.isEmpty(threadName)) {
            logger.error("RPC thread pool [{}] is exhausted, executor={}", threadName, executor.toString());
        }

        if (r instanceof RejectedRunnable) {
            ((RejectedRunnable)r).rejected();
        } else {
            if (!executor.isShutdown()) {
                BlockingQueue<Runnable> queue = executor.getQueue();
                int discardSize = queue.size() >> 1;
                for (int i = 0; i < discardSize; i++) {
                    queue.poll();
                }

                try {
                    queue.put(r);
                } catch (InterruptedException e) {

                }
            }
        }
    }
}
