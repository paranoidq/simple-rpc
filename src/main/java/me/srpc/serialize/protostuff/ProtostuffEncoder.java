package me.srpc.serialize.protostuff;

import me.srpc.serialize.MessageCodecUtil;
import me.srpc.serialize.support.MessageEncoder;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ProtostuffEncoder extends MessageEncoder{

    public ProtostuffEncoder(MessageCodecUtil util) {
        super(util);
    }
}
