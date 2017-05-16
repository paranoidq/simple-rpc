package me.srpc.serialize.protostuff;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ProtostuffSerializeFactory extends BasePooledObjectFactory<ProtostuffSerialize> {

    public ProtostuffSerialize create() throws Exception {
        return createProtostuff();
    }

    public PooledObject<ProtostuffSerialize> wrap(ProtostuffSerialize obj) {
        return new DefaultPooledObject<ProtostuffSerialize>(obj);
    }

    private ProtostuffSerialize createProtostuff() {
        return new ProtostuffSerialize();
    }
}
