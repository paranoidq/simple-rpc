package me.srpc.serialize.protostuff;

import com.google.common.io.Closer;
import io.netty.buffer.ByteBuf;
import me.srpc.serialize.MessageCodecUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ProtostuffCodecUtil implements MessageCodecUtil {

    private Closer closer = Closer.create();
    private static ProtostuffSerializePool protostuffSerializePool = ProtostuffSerializePool.getInstance();
    private boolean rpcDirect = false;


    public boolean isRpcDirect() {
        return rpcDirect;
    }

    public void setRpcDirect(boolean rpcDirect) {
        this.rpcDirect = rpcDirect;
    }

    public void encode(ByteBuf out, Object message) throws IOException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            closer.register(byteArrayOutputStream);
            ProtostuffSerialize protostuffSerialize = protostuffSerializePool.borrow();
            protostuffSerialize.serialize(byteArrayOutputStream, message);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
            protostuffSerializePool.restore(protostuffSerialize);
        } finally {
            closer.close();
        }

    }

    public Object decode(byte[] body) throws IOException {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
            closer.register(byteArrayInputStream);
            ProtostuffSerialize protostuffSerialize = protostuffSerializePool.borrow();
            Object object = protostuffSerialize.deserialize(byteArrayInputStream);
            protostuffSerializePool.restore(protostuffSerialize);
            return object;
        } finally {
            closer.close();
        }
    }
}
