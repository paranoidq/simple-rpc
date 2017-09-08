package me.framework.rpc.exception;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class RejectedResponseException extends RuntimeException {

    public RejectedResponseException() {
    }

    public RejectedResponseException(String message) {
        super(message);
    }

    public RejectedResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public RejectedResponseException(Throwable cause) {
        super(cause);
    }

    public RejectedResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
