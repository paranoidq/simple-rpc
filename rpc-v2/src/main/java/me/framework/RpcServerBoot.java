package me.framework;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class RpcServerBoot {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-server.xml");
    }
}
