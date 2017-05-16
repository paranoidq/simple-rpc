package me.srpc.serialize.kryo;

import me.srpc.serialize.MessageCodecUtil;
import me.srpc.serialize.support.MessageDecoder;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class KryoDecoder extends MessageDecoder {

    public KryoDecoder(MessageCodecUtil util) {
        super(util);
    }
}
