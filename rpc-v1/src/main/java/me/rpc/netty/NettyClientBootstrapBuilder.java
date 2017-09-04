package me.rpc.netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannel;

import java.util.concurrent.TimeUnit;

/**
 * Netty客户端构造器
 *
 * 1. 该构造器整合了{@link Bootstrap}的各种配置和功能，并且提供了默认值
 * 2. 允许可配置
 * 3. 设置了发送区和接收区缓存，避免上层代码无界导致的拥堵和资源耗尽
 * 4. 可共享EventLoopGroup
 *
 *
 * @author paranoidq
 * @since 1.0.0
 */
public class NettyClientBootstrapBuilder {
    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;

    private boolean useEpoll;
    private boolean keepAlive;
    private int connectTimeMillis;
    private int socketTimeSeconds;
    private boolean tcpNoDelay;
    private int eventLoopThreads;
    private int writeBufferLowWaterMarkBytes;
    private int writeBufferHighWaterMarkBytes;

    private int recvByteBufSizeMin;
    private int recvByteBufSizeInit;
    private int recvByteBufSizeMax;

    private int recvBufferSizeBytes;

    public static NettyClientBootstrapBuilder getInstance() {
        return new NettyClientBootstrapBuilder();
    }

    /**
     * 指定EventLoop共享
     * @param eventLoopGroup
     * @return
     */
    public static NettyClientBootstrapBuilder getInstance(EventLoopGroup eventLoopGroup) {
        return new NettyClientBootstrapBuilder(eventLoopGroup);
    }


    /**
     * 私有构造函数，赋默认值
     */
    private NettyClientBootstrapBuilder() {
        this.bootstrap = new Bootstrap();
        this.useEpoll = false;
        this.keepAlive = true;
        this.connectTimeMillis = 1000;
        this.socketTimeSeconds = 5;
        this.tcpNoDelay = true;
        this.eventLoopThreads = 5;
        this.writeBufferLowWaterMarkBytes = 32 * 1024;
        this.writeBufferHighWaterMarkBytes = 64 * 1024;
        this.recvByteBufSizeMin = 64;
        this.recvByteBufSizeInit = 1024;
        this.recvByteBufSizeMax = 65536;

        this.recvBufferSizeBytes = recvByteBufSizeInit;
    }

    private NettyClientBootstrapBuilder(EventLoopGroup eventLoopGroup) {
        this();
        this.eventLoopGroup = eventLoopGroup;
    }

    public NettyClientBootstrapBuilder setUseEpoll(boolean useEpoll) {
        this.useEpoll = useEpoll;
        return this;
    }

    public NettyClientBootstrapBuilder setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
        return this;
    }

    public NettyClientBootstrapBuilder setConnectTimeMillis(int connectTimeMillis) {
        this.connectTimeMillis = connectTimeMillis;
        return this;
    }

    public NettyClientBootstrapBuilder setSocketTimeMillis(int socketTimeMillis) {
        this.socketTimeSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(socketTimeMillis);
        return this;
    }

    public NettyClientBootstrapBuilder setEventLoopThreads(int nThreads) {
        this.eventLoopThreads = nThreads;
        return this;
    }

    public NettyClientBootstrapBuilder setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
        return this;
    }

    public NettyClientBootstrapBuilder setWriteBufferLowWaterMarkBytes(int writeBufferLowWaterMarkBytes) {
        this.writeBufferLowWaterMarkBytes = writeBufferLowWaterMarkBytes;
        return this;
    }

    public NettyClientBootstrapBuilder setWriteBufferHighWaterMarkBytes(int writeBufferHighWaterMarkBytes) {
        this.writeBufferHighWaterMarkBytes = writeBufferHighWaterMarkBytes;
        return this;
    }

    public NettyClientBootstrapBuilder setRecvByteBufSizeMin(int recvByteBufSizeMin) {
        this.recvByteBufSizeMin = recvByteBufSizeMin;
        return this;
    }

    public NettyClientBootstrapBuilder setRecvByteBufSizeInit(int recvByteBufSizeInit) {
        this.recvByteBufSizeInit = recvByteBufSizeInit;
        return this;
    }

    public NettyClientBootstrapBuilder setRecvByteBufSizeMax(int recvByteBufSizeMax) {
        this.recvByteBufSizeMax = recvByteBufSizeMax;
        return this;
    }

    public NettyClientBootstrapBuilder setRecvBufferSizeBytes(int recvBufferSizeBytes) {
        this.recvBufferSizeBytes = recvBufferSizeBytes;
        return this;
    }

    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }

    public Bootstrap build() {
        Class<? extends SocketChannel> channelClass = NioSocketChannel.class;
        if (this.eventLoopGroup == null) {
            this.eventLoopGroup = new NioEventLoopGroup(eventLoopThreads);
            if (useEpoll) {
                channelClass = EpollSocketChannel.class;
                this.eventLoopGroup = new EpollEventLoopGroup(eventLoopThreads);
            }
        }
        bootstrap
            .channel(channelClass);
        bootstrap
            .option(ChannelOption.SO_KEEPALIVE, keepAlive)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeMillis)
            // SO_TIMEOUT只有在OIO的模式下才有效
            .option(ChannelOption.SO_TIMEOUT, (channelClass.isAssignableFrom(OioSocketChannel.class)) ? socketTimeSeconds : null)
            .option(ChannelOption.TCP_NODELAY, tcpNoDelay)
            // 控制当socket层阻塞时，应用层的buffer不会无限增长。应用层可以通过channel.isWritable判断是否继续写数据
            .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, writeBufferLowWaterMarkBytes)
            .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, writeBufferHighWaterMarkBytes)
            .option(ChannelOption.AUTO_READ, true)
            .option(ChannelOption.SO_RCVBUF, recvBufferSizeBytes)
            .option(ChannelOption.RCVBUF_ALLOCATOR,
                new AdaptiveRecvByteBufAllocator(recvByteBufSizeMin, recvByteBufSizeInit, recvByteBufSizeMax))
        ;
        bootstrap.group(eventLoopGroup);
        return bootstrap;
    }
}
