package me.framework.rpc.event;

import com.google.common.eventbus.Subscribe;
import me.framework.rpc.core.client.MessageSendExecutor;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ClientStopEventListener {
    private int lastMessage;

    @Subscribe
    public void listen(ClientStopEvent event) {
        lastMessage = event.getMessage();
        MessageSendExecutor.getInstance().stop();
    }

    public int getLastMessage() {
        return lastMessage;
    }
}
