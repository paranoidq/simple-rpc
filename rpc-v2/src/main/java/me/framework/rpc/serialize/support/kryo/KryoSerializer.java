package me.framework.rpc.serialize.support.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import me.framework.rpc.serialize.support.RpcSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 是否可以公用Serializer实例 ???
 *
 * @author paranoidq
 * @since 1.0.0
 */
public class KryoSerializer implements RpcSerializer {

    private KryoPool pool;

    public KryoSerializer() {
        pool = KryoPoolFactory.getKryoPoolInstance();
    }

    @Override
    public void serialize(OutputStream outputStream, Object object) throws IOException {
        Kryo instance = pool.borrow();
        Output output = new Output(outputStream);
        try {
            instance.writeClassAndObject(output, object);
        } finally {
            output.close();
            pool.release(instance);
        }


    }

    @Override
    public Object deserialize(InputStream inputStream) throws IOException {
        Kryo instance = pool.borrow();
        Input input = new Input(inputStream);
        try {
            return instance.readClassAndObject(input);
        } finally {
            input.close();
            pool.release(instance);
        }
    }
}
