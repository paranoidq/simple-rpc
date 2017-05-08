package me.sprc.core.client;

import me.sprc.service.Calculate;

import java.lang.reflect.Proxy;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageSendExecutor {

    private RpcServerLoader loader = RpcServerLoader.getInstance();

    public MessageSendExecutor(String serverAddress) {
        loader.load(serverAddress);
    }

    public void stop() {

    }

    public <T> T execute(Class<T> rpcInterface) {
        return (T) Proxy.newProxyInstance(
            rpcInterface.getClassLoader(),
            new Class<?>[]{rpcInterface},
            new MessageSendProxy<T>(rpcInterface)
        );
    }

    public static void main(String[] args) {
        MessageSendExecutor executor = new MessageSendExecutor("127.0.0.1:14000");
        Calculate calculate = executor.execute(Calculate.class);
        int result = calculate.add(3, 4);
        System.out.println("RPC Call result: " + result);
        result = calculate.add(3, 12);
        System.out.println("RPC Call result: " + result);

    }
}
