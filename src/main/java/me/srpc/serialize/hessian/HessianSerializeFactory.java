package me.srpc.serialize.hessian;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class HessianSerializeFactory extends BasePooledObjectFactory<HessianSerialize> {

    public HessianSerialize create() throws Exception {
        return createHessian();
    }

    public PooledObject<HessianSerialize> wrap(HessianSerialize hessianSerialize) {
        return new DefaultPooledObject<HessianSerialize>(hessianSerialize);
    }

    private HessianSerialize createHessian() {
        return new HessianSerialize();
    }
}
