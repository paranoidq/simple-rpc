package me.srpc.boot;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class RpcServerStarter {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("rpc-invoke-config-server.xml");
    }
}
