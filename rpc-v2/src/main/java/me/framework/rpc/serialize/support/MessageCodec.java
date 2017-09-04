package me.framework.rpc.serialize.support;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * 面向报文的编解码接口，调用{@link me.framework.rpc.serialize}序列化包完成对应的功能
 * 是一组比序列化包更高层的抽象
 *
 * @author paranoidq
 * @since 1.0.0
 */
public interface MessageCodec {
    int MESSAGE_LENGTH_BYTES = 4;

    void encode(final ByteBuf writeBuffer, final Object message) throws IOException;

    Object decode(byte[] body) throws IOException;
}
