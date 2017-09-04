package me.framework.rpc.serialize.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public interface RpcSerializer {


    /**
     * 序列化
     * @param outputStream
     * @param object
     * @throws IOException
     */
    void serialize(OutputStream outputStream, Object object) throws IOException;


    /**
     * 反序列化
     * @param inputStream
     * @return
     * @throws IOException
     */
    Object deserialize(InputStream inputStream) throws IOException;
}
