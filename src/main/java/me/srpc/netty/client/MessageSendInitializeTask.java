package me.srpc.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.srpc.serialize.RpcSerializeProtocol;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageSendInitializeTask implements Callable<Boolean> {

    private EventLoopGroup eventLoopGroup = null;
    private InetSocketAddress serverAddress = null;
    private RpcSerializeProtocol protocol;

    public MessageSendInitializeTask(EventLoopGroup eventLoopGroup, InetSocketAddress serverAddress, RpcSerializeProtocol protocol) {
        this.eventLoopGroup = eventLoopGroup;
        this.serverAddress = serverAddress;
        this.protocol = protocol;
    }

    @Override
    public Boolean call() {
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup)
            .channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new MessageSendChannelInitializer().setRpcSerializeProtocol(protocol));

        final ChannelFuture channelFuture = b.connect(serverAddress);
        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (channelFuture.isSuccess()) {
                    MessageSendHandler handler = channelFuture.channel().pipeline().get(MessageSendHandler.class);
                    RpcServerLoader.getInstance().setMessageSendHandler(handler);
                }
            }
        });
        // 这里调用sync就能确保初始化完成之后才会返回RpcServerLoader的load中
        // 否则有可能并没有connect完毕就返回了RpcServerLoader中，
        // 那么需要在load中判断是否正确设置了MessageSendHandler
//        channelFuture.sync();
        return Boolean.TRUE;
    }
}
