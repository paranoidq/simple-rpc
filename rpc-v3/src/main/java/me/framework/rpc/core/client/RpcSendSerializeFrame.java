package me.framework.rpc.core.client;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import io.netty.channel.ChannelPipeline;
import me.framework.rpc.core.handler.JdkNativeSendHandler;
import me.framework.rpc.core.handler.KryoSendHandler;
import me.framework.rpc.core.handler.NettyRpcSendHandler;
import me.framework.rpc.message.RpcSerializeFrame;
import me.framework.rpc.serialize.support.RpcSerializeProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class RpcSendSerializeFrame implements RpcSerializeFrame {
    private static final Logger logger = LoggerFactory.getLogger(RpcSendSerializeFrame.class);

    private static ClassToInstanceMap<NettyRpcSendHandler> handlers = MutableClassToInstanceMap.create();
    static {
        handlers.putInstance(JdkNativeSendHandler.class, new JdkNativeSendHandler());
        handlers.putInstance(KryoSendHandler.class, new KryoSendHandler());
    }

    public RpcSendSerializeFrame() {}

    @Override
    public void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline) {
        switch (protocol) {
            case JDK_SERIALIZE: {
                logger.info("Use JDK_SERIALIZE");
                handlers.getInstance(JdkNativeSendHandler.class).handle(pipeline);
                break;
            }
            case KRYO_SERIALIZE: {
                logger.info("Use KRYO_SERIALIZE");
                handlers.getInstance(KryoSendHandler.class).handle(pipeline);
                break;
            }
            case HESSIAN_SERIALIZE: {
                logger.info("Use HESSIAN_SERIALIZE");
                break;
            }
        }
    }
}
