package me.rpc.core;

import com.google.common.reflect.Reflection;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageSendExecutor {

    private RpcServerLoader loader = RpcServerLoader.getInstance();

    public MessageSendExecutor(String serverAddress, RpcSerializeProtocol serializeProtocol) {
        loader.load(serverAddress, serializeProtocol);
    }

    public void stop() {
        loader.unload();
    }

    public static <T> T execute(Class<T> rpcInterface) {
        return Reflection.newProxy(rpcInterface, new MessageSendProxy(rpcInterface));
    }
}
