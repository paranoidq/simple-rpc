package me.sprc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    public static Executor getExecutor(int threads, int queues) {
        String name = "RpcThreadPool";
        return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
            queues == 0 ? new SynchronousQueue<Runnable>()
                : (queues < 0 ? new LinkedBlockingQueue<Runnable>() : new LinkedBlockingQueue<Runnable>(queues)),
            new NamedThreadFactory(name, true),
            new AbortPolicyWithReport(name)
            );
    }


    public static class NamedThreadFactory implements ThreadFactory {

        private static final AtomicInteger threadNumber = new AtomicInteger(1);

        private final AtomicInteger mThreadNum = new AtomicInteger(1);

        private final String prefix;

        private final boolean daemonThread;

        private final ThreadGroup threadGroup;


        public NamedThreadFactory() {
            this("srpc-thread-pool-" + threadNumber.getAndIncrement(), false);
        }

        public NamedThreadFactory(String prefix) {
            this(prefix, false);
        }

        public NamedThreadFactory(String prefix, boolean daemonThread) {
            this.prefix = prefix + "-thread-";
            this.daemonThread = daemonThread;
            SecurityManager s = System.getSecurityManager();
            threadGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
        }


        /**
         * Constructs a new {@code Thread}.  Implementations may also initialize
         * priority, name, daemon status, {@code ThreadGroup}, etc.
         *
         * @param r a runnable to be executed by new thread instance
         * @return constructed thread, or {@code null} if the request to
         * create a thread is rejected
         */
        public Thread newThread(Runnable r) {
            String name = prefix + mThreadNum.getAndIncrement();
            Thread thread = new Thread(threadGroup, r, name, 0); // ?? what is stack size
            thread.setDaemon(daemonThread);
            return thread;
        }

        public ThreadGroup getThreadGroup() {
            return this.threadGroup;
        }
    }

    private static class AbortPolicyWithReport extends ThreadPoolExecutor.AbortPolicy {
        private final String threadName;

        public AbortPolicyWithReport(String threadName) {
            this.threadName = threadName;
        }

        /**
         * Always throws RejectedExecutionException.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         * @throws RejectedExecutionException always
         */
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            String msg = String.format("SRpc["
                + "Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d)"
                + "Executor status: (isShutdown: %s, isTerminated: %s, isTerminating: %s)]",
                threadName, e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(),
                e.getTaskCount(), e.getCompletedTaskCount(), e.isShutdown(), e.isTerminated(), e.isTerminating());
            logger.error(msg);
            throw new RejectedExecutionException(msg);
        }
    }
}
