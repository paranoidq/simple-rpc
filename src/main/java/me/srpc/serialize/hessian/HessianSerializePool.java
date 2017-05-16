package me.srpc.serialize.hessian;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class HessianSerializePool {

    private static final Logger logger = LoggerFactory.getLogger(HessianSerializePool.class);

    // Netty采用hessian序列化、反序列化的时候，为了避免重复产生对象，提高JVM的内存效率
    // 故引入对象池技术
    private GenericObjectPool<HessianSerialize> hessianPool;
    private volatile static HessianSerializePool instance = null;

    private HessianSerializePool() {
        hessianPool = new GenericObjectPool<HessianSerialize>(new HessianSerializeFactory());
    }

    public static HessianSerializePool getHessianPoolInstance() {
        if (instance == null) {
            synchronized (HessianSerializePool.class) {
                if (instance == null) {
                    instance = new HessianSerializePool();
                }
            }
        }
        return instance;
    }

    /**
     * 预留，为spring依赖注入
     * @param maxTotal
     * @param minIdle
     * @param maxWaitMillis
     * @param minEvictableIdleTimeMillis
     */
    public HessianSerializePool(final int maxTotal, final int minIdle, final long maxWaitMillis, final long minEvictableIdleTimeMillis) {
        hessianPool = new GenericObjectPool<HessianSerialize>(new HessianSerializeFactory());
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        hessianPool.setConfig(config);
    }

    public HessianSerialize borrow() {
        try {
            return hessianPool.borrowObject();
        } catch (Exception e) {
            logger.error("Cannot borrow HessianSerialize from pool", e);
            return null;
        }
    }

    public void restore(HessianSerialize hessianSerialize) {
        hessianPool.returnObject(hessianSerialize);
    }

    /**
     * 从代码封装角度，该函数不应该对外开放
     *
     * @return
     */
    private GenericObjectPool<HessianSerialize> getHessianPool() {
        return hessianPool;
    }



}
