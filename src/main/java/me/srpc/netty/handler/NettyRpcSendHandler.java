package me.srpc.netty.handler;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import io.netty.channel.ChannelPipeline;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public interface NettyRpcSendHandler {

    void build(ChannelPipeline pipeline);

    final class INSTANCES {
        private static final ClassToInstanceMap<NettyRpcSendHandler> handlers = MutableClassToInstanceMap.create();
        static {
            handlers.putInstance(JdkNativeSendHandler.class, new JdkNativeSendHandler());
            handlers.putInstance(KryoSendHandler.class, new KryoSendHandler());
            handlers.putInstance(HessianSendHandler.class, new HessianSendHandler());
            handlers.putInstance(ProtostuffSendHandler.class, new ProtostuffSendHandler());
        }
        public static NettyRpcSendHandler get(Class<? extends NettyRpcSendHandler> cls) {
            return handlers.get(cls);
        }
    }
}
