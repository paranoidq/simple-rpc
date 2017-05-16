package me.srpc.netty.handler;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import io.netty.channel.ChannelPipeline;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public interface NettyRpcRecvHandler {

    void build(ChannelPipeline pipeline);

    final class INSTANCES {
        private static ClassToInstanceMap<NettyRpcRecvHandler> handlers = MutableClassToInstanceMap.create();
        static {
            handlers.putInstance(JdkNativeRecvHandler.class, new JdkNativeRecvHandler());
            handlers.putInstance(KryoRecvHandler.class, new KryoRecvHandler());
            handlers.putInstance(HessianRecvHandler.class, new HessianRecvHandler());
            handlers.putInstance(ProtostuffRecvHandler.class, new ProtostuffRecvHandler());
        }

        public static NettyRpcRecvHandler get(Class<? extends NettyRpcRecvHandler> cls) {
            return handlers.get(cls);
        }
    }
}
