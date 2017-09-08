package me.framework.rpc.boot;

import me.framework.rpc.logger.AppLoggerInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * RPC Server启动器
 *
 * 负责启动整个RPC
 *
 * @author paranoidq
 * @since 1.0.0
 */
public class RpcServerBoot {

    public static void main(String[] args) {
        AppLoggerInjector.injectAll("me.framework.rpc");
        System.out.println("AppLogger annotation injected!");

        new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-server.xml");
    }
}
