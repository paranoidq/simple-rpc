package me.framework.rpc.util.pool.policy;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public interface RejectedRunnable extends Runnable {
    void rejected();
}
