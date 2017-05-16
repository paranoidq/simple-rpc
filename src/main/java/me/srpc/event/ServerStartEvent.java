package me.srpc.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ServerStartEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public ServerStartEvent(Object source) {
        super(source);
    }
}
