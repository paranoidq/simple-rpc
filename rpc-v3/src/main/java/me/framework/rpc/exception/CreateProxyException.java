package me.framework.rpc.exception;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class CreateProxyException extends RuntimeException {

    public CreateProxyException() {
    }

    public CreateProxyException(String message) {
        super(message);
    }

    public CreateProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateProxyException(Throwable cause) {
        super(cause);
    }

    public CreateProxyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
