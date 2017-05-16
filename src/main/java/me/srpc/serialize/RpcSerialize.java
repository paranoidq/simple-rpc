package me.srpc.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public interface RpcSerialize {

    void serialize(OutputStream outputStream, Object object) throws IOException;

    Object deserialize(InputStream inputStream) throws IOException;
}
