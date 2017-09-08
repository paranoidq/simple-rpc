package me.framework.rpc.exception;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class AsyncCallException extends RuntimeException {

    public AsyncCallException() {
    }

    public AsyncCallException(String message) {
        super(message);
    }

    public AsyncCallException(String message, Throwable cause) {
        super(message, cause);
    }

    public AsyncCallException(Throwable cause) {
        super(cause);
    }

    public AsyncCallException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
