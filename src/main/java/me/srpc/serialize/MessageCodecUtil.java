package me.srpc.serialize;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public interface MessageCodecUtil {

    int MESSAGE_LENGTH = 4;

    void encode(final ByteBuf out, final Object message) throws IOException;

    Object decode(byte[] body) throws IOException;
}
