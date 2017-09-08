package me.framework.rpc.core.client;

import me.framework.rpc.config.RpcSystemConfig;
import me.framework.rpc.exception.RejectedResponseException;
import me.framework.rpc.model.MessageRequest;
import me.framework.rpc.model.MessageResponse;

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
            return response.getResult();
        }
        try {
            lock.lock();
            finish.await(RpcSystemConfig.SYSTEM_PROPERTY_MESSAGE_CALLBACK_TIMEOUT, TimeUnit.MILLISECONDS);
            if (!this.response.getError().equals(RpcSystemConfig.FILTER_RESPONSE_MSG) && (!this.response.isReturnNotNull() || (this.response.isReturnNotNull() && this.response.getResult() != null))) {
                return this.response.getResult();
            } else {
                throw new RejectedResponseException(RpcSystemConfig.FILTER_RESPONSE_MSG);
            }
        } finally {
            lock.unlock();
        }
    }

}
