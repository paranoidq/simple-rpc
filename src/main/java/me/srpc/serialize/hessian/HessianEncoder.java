package me.srpc.serialize.hessian;

import me.srpc.serialize.MessageCodecUtil;
import me.srpc.serialize.support.MessageEncoder;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class HessianEncoder extends MessageEncoder {

    public HessianEncoder(MessageCodecUtil util) {
        super(util);
    }
}
