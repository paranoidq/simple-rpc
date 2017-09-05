package me.framework.rpc.core.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import me.framework.rpc.serialize.support.RpcSerializeProtocol;
import me.framework.rpc.util.nettybuilder.NettyClientBootstrapBuilder;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageSendInitializeTask implements Callable<Boolean> {

    private EventLoopGroup eventLoopGroup;
    private InetSocketAddress serverAddress;
    private RpcSerializeProtocol protocol;

    public MessageSendInitializeTask(EventLoopGroup eventLoopGroup, InetSocketAddress serverAddress, RpcSerializeProtocol protocol) {
        this.eventLoopGroup = eventLoopGroup;
        this.serverAddress = serverAddress;
        this.protocol = protocol;
    }

    @Override
    public Boolean call() {
        Bootstrap bootstrap = NettyClientBootstrapBuilder.getInstance(eventLoopGroup).build();
        bootstrap.handler(new MessageSendChannelInitializer().setRpcSerializeProtocol(protocol));
        ChannelFuture future = bootstrap.connect(serverAddress);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    // 连接成功后，保存MessageSendHander，业务层要写消息就通过这个对象
                    // 实际就是保存channel，只不过这里没有选择将channel单独保存
                    MessageSendHandler sendHandler = future.channel().pipeline().get(MessageSendHandler.class);
                    RpcServerLoader.getInstance().setMessageSendHandler(sendHandler);
                } else {
                    System.err.println(future.cause());
                }
            }
        });
        return Boolean.TRUE;
    }
}
