package me.srpc.serialize.protostuff;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import me.srpc.model.MessageRequest;
import me.srpc.model.MessageResponse;
import me.srpc.serialize.RpcSerialize;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ProtostuffSerialize implements RpcSerialize {

    private static SchemaCache cachedSchema = SchemaCache.getInstance();
    private static Objenesis objenesis = new ObjenesisStd(true);
    private boolean rpcDirect = false;

    public boolean isRpcDirect() {
        return rpcDirect;
    }

    public void setRpcDirect(boolean rpcDirect) {
        this.rpcDirect = rpcDirect;
    }

    private static <T> Schema<T> getSchema(Class<T> cls) {
        return (Schema<T>) cachedSchema.get(cls);
    }

    public void serialize(OutputStream outputStream, Object object) throws IOException {
        Class cls = isRpcDirect() ? MessageRequest.class : MessageResponse.class;
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema schema = getSchema(cls);
            ProtobufIOUtil.writeTo(outputStream, object, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public Object deserialize(InputStream inputStream) throws IOException {
        try {
            Class cls = isRpcDirect() ? MessageRequest.class : MessageResponse.class;
            Object message = objenesis.newInstance(cls);
            Schema<Object> schema = getSchema(cls);
            ProtobufIOUtil.mergeFrom(inputStream, message, schema);
            return message;
        } catch (Exception e) {
            return new IllegalStateException(e.getMessage(), e);
        }
    }
}
