package me.framework.rpc.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.framework.rpc.serialize.support.MessageCodec;
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

    private static final int MESSAGE_LENGTH_BYTES = MessageCodec.MESSAGE_LENGTH_BYTES;
    private MessageCodec codec;

    public MessageDecoder(MessageCodec codec) {
        this.codec = codec;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 出现粘包导致消息头长度不对，直接返回不处理，等待后续包
        if (in.readableBytes() < MESSAGE_LENGTH_BYTES) {
            return;
        }

        in.markReaderIndex();

        int messageLength = in.readInt();
        // 长度异常，清空缓冲区，关闭channel
        if (messageLength < 0) {
//            in.clear(); // ??
            ctx.close();
        }

        // 消息没有满足消息头长度，重置已读的index，等待后续包
        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        } else {
            byte[] messageBody = new byte[messageLength];
            in.readBytes(messageBody);

            try {
                Object object = codec.decode(messageBody);
                out.add(object);
            } catch (IOException e) {
                logger.error("Message decode failed", e);
            }
        }

    }
}
