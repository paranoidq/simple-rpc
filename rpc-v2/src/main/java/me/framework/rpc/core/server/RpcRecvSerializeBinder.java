package me.framework.rpc.core.server;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LoggingHandler;
import me.framework.rpc.message.MessageSerializeBinder;
import me.framework.rpc.message.kryo.KryoDecoder;
import me.framework.rpc.message.kryo.KryoEncoder;
import me.framework.rpc.serialize.support.MessageCodec;
import me.framework.rpc.serialize.support.RpcSerializeProtocol;
import me.framework.rpc.serialize.support.kryo.KryoMessageCodec;
import me.framework.rpc.serialize.support.kryo.KryoPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class RpcRecvSerializeBinder implements MessageSerializeBinder {
    private static final Logger logger = LoggerFactory.getLogger(RpcRecvSerializeBinder.class);

    private Map<String, Object> handlerMap;

    public RpcRecvSerializeBinder(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void bind(RpcSerializeProtocol protocol, ChannelPipeline pipeline) {
        switch (protocol) {
            case JDK_SERIALIZE: {
                logger.info("Use KRYO_SERIAZLIZE");
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, MessageCodec.MESSAGE_LENGTH_BYTES, 0, MessageCodec.MESSAGE_LENGTH_BYTES));
                pipeline.addLast(new LengthFieldPrepender(MessageCodec.MESSAGE_LENGTH_BYTES));
                pipeline.addLast(new ObjectEncoder());
                pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                pipeline.addLast(new MessageRecvHandler(handlerMap));
                break;
            }
            case KRYO_SERIAZLIZE: {
                logger.info("Use KRYO_SERIAZLIZE");
                KryoMessageCodec util = new KryoMessageCodec();
                pipeline.addLast(new LoggingHandler());
                pipeline.addLast(new KryoEncoder(util));
                pipeline.addLast(new KryoDecoder(util));
                pipeline.addLast(new MessageRecvHandler(handlerMap));
                break;
            }
            case HESSIAN_SERIALIZE: {
                logger.info("Use HESSIAN_SERIALIZE");
//                HessianCodecUtil util = new HessianCodecUtil();
//                pipeline.addLast(new HessianEncoder(util));
//                pipeline.addLast(new HessianDecoder(util));
//                pipeline.addLast(new MessageRecvHandler(handlerMap));
                break;
            }
        }
    }
}
