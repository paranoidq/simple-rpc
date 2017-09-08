package me.framework.rpc.core.client;

import com.google.common.reflect.Reflection;
import me.framework.rpc.serialize.support.RpcSerializeProtocol;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageSendExecutor {

    private RpcServerLoader loader = RpcServerLoader.getInstance();

    private static class Holder {
        public static final MessageSendExecutor instance = new MessageSendExecutor();
    }

    public static MessageSendExecutor getInstance() {
        return Holder.instance;
    }

    public void setRpcServerLoader(String serverAddress, RpcSerializeProtocol protocol) {
        loader.load(serverAddress, protocol);
    }

    public void stop() {
        loader.unload();
    }

    public static <T> T execute(Class<T> rpcInterface) {
        return Reflection.newProxy(rpcInterface, new MessageSendProxy(rpcInterface));
    }
}
