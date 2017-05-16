package me.srpc.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import me.srpc.serialize.RpcSerialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class KryoSerialize implements RpcSerialize {

    private KryoPool pool;


    public KryoSerialize(KryoPool pool) {
        this.pool = pool;
    }

    @Override
    public void serialize(OutputStream outputStream, Object object) throws IOException {
        Kryo kryo = pool.borrow();
        Output out = new Output(outputStream);
        kryo.writeClassAndObject(out, object);
        out.close();
        pool.release(kryo);
    }

    @Override
    public Object deserialize(InputStream inputStream) throws IOException {
        Kryo kryo = pool.borrow();
        Input input = new Input(inputStream);
        Object result = kryo.readClassAndObject(input);
        input.close();
        pool.release(kryo);
        return result;
    }
}
