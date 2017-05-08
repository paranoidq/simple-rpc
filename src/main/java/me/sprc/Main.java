package me.sprc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class Main {

    public static void main(String[] args) {

        new ClassPathXmlApplicationContext("rpc-invoke-config.xml");
    }
}
