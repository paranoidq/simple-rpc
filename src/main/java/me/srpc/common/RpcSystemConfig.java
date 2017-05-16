package me.srpc.common;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class RpcSystemConfig {

    public static final String SystemPropertyThreadPoolRejectedPolicyAttr = "";
    public static final String SystemPropertyThreadPoolQueueNameyAttr = "";
    public static final int PARALLEL = Math.max(2, Runtime.getRuntime().availableProcessors());
    public static final int SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS = 0;
    public static final int SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS = 0;
    public static final String RPC_COMPILER_SPI_ATTR = "me.srpc.compiler.AccessAdaptive";
    public static final String RPC_ABILITY_DETAIL_SPI_ATTR = "me.srpc.api.AbilityDetail";
    public static final int SYSTEM_PROPERTY_MESSAGE_CALLBACK_TIMEOUT = 60000;
    private static boolean monitorServerSupport = true;


    public static boolean isMonitorServerSupport() {
        return monitorServerSupport;
    }

    public static void setMonitorServerSupport(boolean jmxSupport) {
        monitorServerSupport = jmxSupport;
    }
}
