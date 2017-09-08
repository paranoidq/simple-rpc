package me.framework.rpc.config;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class RpcSystemConfig {

    public static final String SystemPropertyThreadPoolRejectedPolicyAttr = "nettyrpc.default.threadpool.rejectedPolicy";
    public static final String SystemPropertyThreadPoolQueueTypeAttr = "nettyrpc.default.threadpool.queueType";
    public static final String SystemPropertThreadPoolThreadNumsAttr = "nettyrpc.default.threadpool.threadNums";
    public static final String SystemPropertThreadPoolQueueNumsAttr = "nettyrpc.default.threadpool.queueNums";


    public static final int PARALLEL = Math.max(2, Runtime.getRuntime().availableProcessors());

    public static final int SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS = Integer.getInteger(SystemPropertThreadPoolThreadNumsAttr, 16);
    public static final int SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS = Integer.getInteger(SystemPropertThreadPoolQueueNumsAttr, -1);

    public static final String FILTER_RESPONSE_MSG = "Illegal request,NettyRPC server refused to respond!";

    public static final boolean ENABLE_FILTER = false;

    public static final int SYSTEM_PROPERTY_MESSAGE_CALLBACK_TIMEOUT = 10 * 1000;

    public static final int SYSTEM_PROPERTY_CLIENT_RECONNECT_DELAY = 10;

    public static boolean monitorServerSupport = false;

    public static final boolean isMonitorServerSupport() {
        return monitorServerSupport;
    }

    public static final void setMonitorServerSupport(boolean monitorServerSupport) {
        RpcSystemConfig.monitorServerSupport = monitorServerSupport;
    }

}
