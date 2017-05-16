package me.srpc.serialize.support;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.srpc.serialize.MessageCodecUtil;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageEncoder extends MessageToByteEncoder<Object>{
    private MessageCodecUtil util = null;


    public MessageEncoder(MessageCodecUtil util) {
        this.util = util;
    }

    /**
     * Encode a message into a {@link ByteBuf}. This method will be called for each written message that can be handled
     * by this encoder.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link MessageToByteEncoder} belongs to
     * @param msg the message to encode
     * @param out the {@link ByteBuf} into which the encoded message will be written
     * @throws Exception is thrown if an error accour
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        util.encode(out, msg);
    }
}
