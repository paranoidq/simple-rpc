package me.srpc.model;

import me.srpc.common.RpcSystemConfig;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageCallBack {

    private MessageRequest request;
    private MessageResponse response;

    private Lock lock = new ReentrantLock();
    private Condition receivedCondition = lock.newCondition();

    public MessageCallBack(MessageRequest request) {
        this.request = request;
    }

    public Object start() throws InterruptedException {
        try {
            lock.lock();

            receivedCondition.await(RpcSystemConfig.SYSTEM_PROPERTY_MESSAGE_CALLBACK_TIMEOUT, TimeUnit.MILLISECONDS);
            if (this.response != null) {
                return response.getResult();
            } else {
                 return null;
            }
        } finally {
            lock.unlock();
        }
    }

    public void over(MessageResponse response) {
        try {
            lock.lock();
            receivedCondition.signal();
            this.response = response;
        } finally {
            lock.unlock();
        }
    }
}
