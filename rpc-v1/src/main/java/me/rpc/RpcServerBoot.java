package me.rpc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class RpcServerBoot {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("rpc-invoke.xml");
    }
}
