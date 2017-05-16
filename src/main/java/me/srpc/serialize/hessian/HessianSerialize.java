package me.srpc.serialize.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import me.srpc.serialize.RpcSerialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class HessianSerialize implements RpcSerialize {

    public void serialize(OutputStream outputStream, Object object) throws IOException {
        Hessian2Output hessian2Output = new Hessian2Output(outputStream);
        hessian2Output.startMessage();
        hessian2Output.writeObject(object);
        hessian2Output.completeMessage();
        hessian2Output.close();
        outputStream.close();
    }

    public Object deserialize(InputStream inputStream) throws IOException {
        Hessian2Input hessian2Input = new Hessian2Input(inputStream);
        hessian2Input.startMessage();
        Object result = hessian2Input.readObject();
        hessian2Input.completeMessage();
        hessian2Input.close();
        return result;
    }
}
