package me.rpc.core;

import me.rpc.model.MessageRequest;
import me.rpc.model.MessageResponse;

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
    private volatile MessageResponse response;

    private Lock lock = new ReentrantLock();
    private Condition finish = lock.newCondition();

    public MessageCallBack(MessageRequest request) {
        this.request = request;
    }

    public MessageRequest getRequest() {
        return request;
    }

    public void setRequest(MessageRequest request) {
        this.request = request;
    }


    public void over(MessageResponse response) {
        try {
            lock.lock();
            finish.signal();
            this.response = response;
        } finally {
            lock.unlock();
        }
    }

    public Object get() throws InterruptedException {
        // 服务器在lock之前返回，无需lock，直接return结果
        if (response != null) {
            return response.getResultDesc();
        }
        try {
            lock.lock();
            finish.await(10 * 1000, TimeUnit.MILLISECONDS);
            if (this.response != null) {
                return this.response.getResultDesc();
            } else {
                return null;
            }
        } finally {
            lock.unlock();
        }
    }

}
