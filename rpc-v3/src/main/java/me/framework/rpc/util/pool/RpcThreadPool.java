package me.framework.rpc.util.pool;

import me.framework.rpc.util.pool.policy.AbortPolicy;
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

    public static Executor getExecutor(int threads, int queues) {
        String name = "RpcThreadPool";
        return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
            queues == 0 ? new SynchronousQueue<>() :
                (queues < 0 ? new LinkedBlockingQueue<>() : new LinkedBlockingQueue<>(queues)),
            new NamedThreadFactory(name, true),
            new AbortPolicy(name)
            );
    }
}
