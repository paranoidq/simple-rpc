package me.framework.rpc.test;

import me.framework.rpc.core.client.MessageSendExecutor;
import me.framework.rpc.serialize.support.RpcSerializeProtocol;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.CountDownLatch;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class RpcTest {

    public static void main(String[] args) throws InterruptedException {
        final MessageSendExecutor executor = new MessageSendExecutor("127.0.0.1:18888",
            RpcSerializeProtocol.KRYO_SERIAZLIZE);
        int parallel = 1;

        StopWatch sw = new StopWatch();
        sw.start();

        CountDownLatch signal = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(parallel);

        for (int index = 0; index < parallel; index++) {
            CalcParallelRequestThread client = new CalcParallelRequestThread(
                signal, finish, executor, index
            );
            new Thread(client).start();
        }

        signal.countDown();
        finish.await();

        sw.stop();

        String tip  = String.format("RPC调用总耗时：[%s] 毫秒", sw.getTime());
        System.out.println(tip);

        executor.stop();
    }
}
