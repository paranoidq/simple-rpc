package me.framework.rpc.core.server;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import io.netty.channel.ChannelPipeline;
import me.framework.rpc.core.handler.JdkNativeRecvHandler;
import me.framework.rpc.core.handler.KryoRecvHandler;
import me.framework.rpc.core.handler.NettyRpcRecvHandler;
import me.framework.rpc.message.RpcSerializeFrame;
import me.framework.rpc.serialize.support.RpcSerializeProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class RpcRecvSerializeFrame implements RpcSerializeFrame {
    private static final Logger logger = LoggerFactory.getLogger(RpcRecvSerializeFrame.class);

    private Map<String, Object> handlerMap;

    public RpcRecvSerializeFrame(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    private static ClassToInstanceMap<NettyRpcRecvHandler> handler = MutableClassToInstanceMap.create();
    /**
     * 可以用注解代替
     */
    static {
        handler.putInstance(JdkNativeRecvHandler.class, new JdkNativeRecvHandler());
        handler.putInstance(KryoRecvHandler.class, new KryoRecvHandler());
        // ...
    }

    /**
     * 将组装channelHandler的部分利用策略模式独立出去
     * 不同的序列化方式组装的handler不同
     * @param protocol
     * @param pipeline
     */
    @Override
    public void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline) {
        switch (protocol) {
            case JDK_SERIALIZE: {
                logger.info("Use KRYO_SERIALIZE");
                handler.getInstance(JdkNativeRecvHandler.class).handle(handlerMap, pipeline);
                break;
            }
            case KRYO_SERIALIZE: {
                logger.info("Use KRYO_SERIALIZE");
                handler.getInstance(KryoRecvHandler.class).handle(handlerMap, pipeline);
                break;
            }
            case HESSIAN_SERIALIZE: {
                logger.info("Use HESSIAN_SERIALIZE");
                break;
            }
        }
    }
}
