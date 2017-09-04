package me.srpc.netty.client;

import com.google.common.reflect.Reflection;
import me.srpc.serialize.RpcSerializeProtocol;
import me.srpc.service.AddCalculate;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageSendExecutor {

    private RpcServerLoader loader = RpcServerLoader.getInstance();

    private static class Holder {
        private static final MessageSendExecutor instance = new MessageSendExecutor();
    }
    public static MessageSendExecutor getInstance() {
        return Holder.instance;
    }

    public MessageSendExecutor() {
    }

    public MessageSendExecutor(String serverAddress, RpcSerializeProtocol protocol) {
        loader.load(serverAddress, protocol);
    }

    public void stop() {
        loader.unload();
    }

    public void setRpcServerLoader(String serverAddress, RpcSerializeProtocol protocol) {
        loader.load(serverAddress, protocol);
    }


    /**
     * 本质上其实就是通过反射创建出需要调用的服务的对象实例
     * 然后进行调用
     * 但是调用的过程显然不是直接调用服务对象，因为这只是个接口。
     * 因此发射的实现中需要进行真正的RPC远程调用
     * @param rpcInterface
     * @param <T>
     * @return
     */
    public <T> T execute(Class<T> rpcInterface) {
        return (T) Reflection.newProxy(rpcInterface, new MessageSendProxy<T>());
    }

    public static void main(String[] args) {
        MessageSendExecutor executor = new MessageSendExecutor("127.0.0.1:14000", RpcSerializeProtocol.JDK_SERIALIZE);
        AddCalculate calculate = executor.execute(AddCalculate.class);
        int result = calculate.add(3, 4);
        System.out.println("RPC Call result: " + result);
        result = calculate.add(3, 12);
        System.out.println("RPC Call result: " + result);

    }
}
