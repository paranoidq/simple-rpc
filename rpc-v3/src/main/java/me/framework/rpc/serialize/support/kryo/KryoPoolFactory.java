package me.framework.rpc.serialize.support.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import me.framework.rpc.model.MessageRequest;
import me.framework.rpc.model.MessageResponse;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class KryoPoolFactory {

    private static KryoPoolFactory instance;

    private KryoFactory factory = new KryoFactory() {
        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.register(MessageRequest.class);
            kryo.register(MessageResponse.class);
            kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }
    };

    private KryoPool pool = new KryoPool.Builder(factory).build();
    private KryoPoolFactory() {}

    public static KryoPool getKryoPoolInstance() {
        if (instance == null) {
            synchronized (KryoPoolFactory.class) {
                if (instance == null) {
                    instance = new KryoPoolFactory();
                }
            }
        }
        return instance.getPool();
    }

    public KryoPool getPool() {
        return pool;
    }
}
