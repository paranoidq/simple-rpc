package me.srpc.serialize.kryo;

import me.srpc.serialize.MessageCodecUtil;
import me.srpc.serialize.support.MessageEncoder;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class KryoEncoder extends MessageEncoder {

    public KryoEncoder(MessageCodecUtil util) {
        super(util);
    }
}
