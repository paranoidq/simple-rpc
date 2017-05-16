package me.srpc.serialize.protostuff;

import me.srpc.serialize.MessageCodecUtil;
import me.srpc.serialize.support.MessageDecoder;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ProtostuffDecoder extends MessageDecoder {

    public ProtostuffDecoder(MessageCodecUtil util) {
        super(util);
    }
}
