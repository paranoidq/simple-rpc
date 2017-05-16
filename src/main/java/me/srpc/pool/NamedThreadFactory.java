package me.srpc.pool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class NamedThreadFactory implements ThreadFactory {

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
