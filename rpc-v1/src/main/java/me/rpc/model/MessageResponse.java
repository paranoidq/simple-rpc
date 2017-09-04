package me.rpc.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageResponse implements Serializable {


    private static final long serialVersionUID = 3006535874960565163L;

    private String messageId;
    private String error;
    private Object resultDesc;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResultDesc() {
        return resultDesc;
    }

    public void setResult(Object resultDesc) {
        this.resultDesc = resultDesc;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
            "messageId='" + messageId + '\'' +
            ", error='" + error + '\'' +
            ", resultDesc=" + resultDesc +
            '}';
    }
}

