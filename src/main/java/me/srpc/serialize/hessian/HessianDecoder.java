package me.srpc.serialize.hessian;

import me.srpc.serialize.MessageCodecUtil;
import me.srpc.serialize.support.MessageDecoder;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class HessianDecoder extends MessageDecoder {

    public HessianDecoder(MessageCodecUtil util) {
        super(util);
    }
}
