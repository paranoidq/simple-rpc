package me.framework.rpc.test;

import me.framework.rpc.core.client.MessageSendExecutor;
import me.framework.rpc.services.Calculate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class CalcParallelRequestThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CalcParallelRequestThread.class);

    private CountDownLatch signal;
    private CountDownLatch finish;

    private MessageSendExecutor executor;
    private int taskNumber;

    public CalcParallelRequestThread(CountDownLatch signal, CountDownLatch finish, MessageSendExecutor executor, int taskNumber) {
        this.signal = signal;
        this.finish = finish;
        this.executor = executor;
        this.taskNumber = taskNumber;
    }

    @Override
    public void run() {
        try {
            signal.await();
            Calculate calculate = executor.execute(Calculate.class);
            int add = calculate.add(taskNumber, taskNumber);
            System.out.println("Calculate add result: [" + add + "]");
        } catch (InterruptedException e) {
            logger.error("", e);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            finish.countDown();
        }
    }
}
