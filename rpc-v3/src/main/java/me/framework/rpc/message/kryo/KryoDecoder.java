package me.framework.rpc.message.kryo;

import me.framework.rpc.message.MessageDecoder;
import me.framework.rpc.serialize.support.MessageCodec;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class KryoDecoder extends MessageDecoder {

    public KryoDecoder(MessageCodec codec) {
        super(codec);
    }
}
