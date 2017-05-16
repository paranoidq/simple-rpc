package me.srpc.serialize.protostuff;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ProtostuffSerializePool {
    private static final Logger logger = LoggerFactory.getLogger(ProtostuffSerializePool.class);

    private GenericObjectPool<ProtostuffSerialize> protostuffPool;
    private volatile static ProtostuffSerializePool instance = null;

    private ProtostuffSerializePool() {
        this.protostuffPool = new GenericObjectPool<ProtostuffSerialize>(new ProtostuffSerializeFactory());
    }

    public static ProtostuffSerializePool getInstance() {
        if (instance == null) {
            synchronized (ProtostuffSerializePool.class) {
                if (instance == null) {
                    instance = new ProtostuffSerializePool();
                }
            }
        }
        return instance;
    }

    public ProtostuffSerializePool(final int maxTotal, final int minIdle, final long maxWaitMillis, final long minEvitableIdleTimeMillis) {
        protostuffPool = new GenericObjectPool<ProtostuffSerialize>(new ProtostuffSerializeFactory());
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMinEvictableIdleTimeMillis(minEvitableIdleTimeMillis);
        protostuffPool.setConfig(config);
    }

    public ProtostuffSerialize borrow() {
        try {
            return protostuffPool.borrowObject();
        } catch (Exception e) {
            logger.error("Cannot borrow ProtostuffSerialize from protostuffPool", e);
            return null;
        }
    }

    public void restore(final ProtostuffSerialize object) {
        protostuffPool.returnObject(object);
    }

    /**
     * 从代码封装的角度，该函数不应该对外开放
     * @return
     */
    private GenericObjectPool<ProtostuffSerialize> getProtostuffPool() {
        return this.protostuffPool;
    }
}
