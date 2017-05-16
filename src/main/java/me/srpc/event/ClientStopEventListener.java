package me.srpc.event;

import com.google.common.eventbus.Subscribe;
import me.srpc.netty.client.MessageSendExecutor;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ClientStopEventListener {

    public int lastMessage = 0;

    @Subscribe
    public void listen(ClientStopEvent event) {
        lastMessage = event.getMessage();
        MessageSendExecutor.getInstance().stop();
    }

    public int getLastMessage() {
        return lastMessage;
    }
}
