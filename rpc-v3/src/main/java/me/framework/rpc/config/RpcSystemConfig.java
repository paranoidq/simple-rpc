package me.framework.rpc.config;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public interface RpcSystemConfig {

    String SystemPropertyThreadPoolRejectedPolicyAttr = "";

    String SystemPropertyThreadPoolQueueNameAttr = "";

    int PARALLEL = Runtime.getRuntime().availableProcessors() * 2;

    int SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS = 16;
    int SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS = -1;
}
