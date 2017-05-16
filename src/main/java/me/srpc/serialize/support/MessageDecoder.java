package me.srpc.serialize.support;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.srpc.serialize.MessageCodecUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(MessageDecoder.class);

    private static final int MESSAGE_LENGTH = MessageCodecUtil.MESSAGE_LENGTH;
    private MessageCodecUtil util = null;

    public MessageDecoder(MessageCodecUtil util) {
        this.util = util;
    }

    /**
     * Decode the from one {@link ByteBuf} to an other. This method will be called till either the input
     * {@link ByteBuf} has nothing to read when return from this method or till nothing was read from the input
     * {@link ByteBuf}.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link ByteToMessageDecoder} belongs to
     * @param in  the {@link ByteBuf} from which to read data
     * @param out the {@link List} to which decoded messages should be added
     * @throws Exception is thrown if an error accour
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 出现TCP粘包，报文长度小于约定的长度，下次再读
        if (in.readableBytes() < MESSAGE_LENGTH) {
            return;
        }

        in.markReaderIndex();
        int messageLength = in.readInt();
        if (messageLength < 0) {
            ctx.close();
        }

        if (in.readableBytes() < messageLength) {
            // 出现TCP粘包，接受到的byte是还没有满足消息长度值，重置读索引，下次再读
            in.resetReaderIndex();
            return;
        } else {
            byte[] messageBody = new byte[messageLength];
            in.readBytes(messageBody);

            try {
                Object object = util.decode(messageBody);
                out.add(object);
            } catch (IOException e) {
                logger.error("", e);
            }
        }
    }
}
