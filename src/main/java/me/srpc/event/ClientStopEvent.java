package me.srpc.event;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ClientStopEvent {

    private final int message;

    public ClientStopEvent(int message) {
        this.message = message;
    }

    public int getMessage() {
        return message;
    }
}
