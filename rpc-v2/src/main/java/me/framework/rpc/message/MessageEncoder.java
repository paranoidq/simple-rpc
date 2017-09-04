package me.framework.rpc.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.framework.rpc.serialize.support.MessageCodec;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageEncoder extends MessageToByteEncoder<Object> {

    private MessageCodec codec;

    public MessageEncoder(MessageCodec codec) {
        this.codec = codec;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        codec.encode(out, msg);
    }
}
