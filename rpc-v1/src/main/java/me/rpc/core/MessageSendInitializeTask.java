package me.rpc.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import me.rpc.core.channel.MessageSendChannelInitializer;
import me.rpc.netty.NettyClientBootstrapBuilder;

import java.net.InetSocketAddress;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageSendInitializeTask implements Runnable {

    private EventLoopGroup eventLoopGroup;
    private InetSocketAddress serverAddress;
    private RpcServerLoader loader;

    public MessageSendInitializeTask(EventLoopGroup eventLoopGroup, InetSocketAddress serverAddress, RpcServerLoader loader) {
        this.eventLoopGroup = eventLoopGroup;
        this.serverAddress = serverAddress;
        this.loader = loader;
    }

    @Override
    public void run() {
        Bootstrap bootstrap = NettyClientBootstrapBuilder.getInstance(eventLoopGroup).build();
        bootstrap.handler(new MessageSendChannelInitializer());
        ChannelFuture future = bootstrap.connect(serverAddress);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    // 连接成功后，保存MessageSendHander，业务层要写消息就通过这个对象
                    // 实际就是保存channel，只不过这里没有选择将channel单独保存
                    MessageSendHandler sendHandler = future.channel().pipeline().get(MessageSendHandler.class);
                    MessageSendInitializeTask.this.loader.setMessageSendHandler(sendHandler);
                }
            }
        });
    }
}
