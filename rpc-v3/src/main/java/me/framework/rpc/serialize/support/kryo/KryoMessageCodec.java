package me.framework.rpc.serialize.support.kryo;

import com.esotericsoftware.kryo.pool.KryoPool;
import io.netty.buffer.ByteBuf;
import me.framework.rpc.serialize.support.MessageCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class KryoMessageCodec implements MessageCodec {

    public KryoMessageCodec() {}

    @Override
    public void encode(ByteBuf writeBuffer, Object message) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            KryoSerializer serializer = new KryoSerializer();
            serializer.serialize(byteArrayOutputStream, message);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            writeBuffer.writeInt(dataLength);
            writeBuffer.writeBytes(body);
        }
    }

    @Override
    public Object decode(byte[] body) throws IOException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body)) {
            KryoSerializer serializer = new KryoSerializer();
            return serializer.deserialize(byteArrayInputStream);
        }
    }
}
