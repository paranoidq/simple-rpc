package me.framework.rpc.spring;

import com.google.common.io.CharStreams;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class NettyRpcNamespaceHandler extends NamespaceHandlerSupport {
    static {
        Resource resource = new ClassPathResource("NettyRPC-logo.txt");
        if (resource.exists()) {
            try {
                Reader reader = new InputStreamReader(resource.getInputStream());
                String text = CharStreams.toString(reader);
                System.out.println(text);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("");
            System.out.println(" _      _____ _____ _____ ___  _ ____  ____  ____ ");
            System.out.println("/ \\  /|/  __//__ __Y__ __\\\\  \\///  __\\/  __\\/   _\\");
            System.out.println("| |\\ |||  \\    / \\   / \\   \\  / |  \\/||  \\/||  /  ");
            System.out.println("| | \\|||  /_   | |   | |   / /  |    /|  __/|  \\_ ");
            System.out.println("\\_/  \\|\\____\\  \\_/   \\_/  /_/   \\_/\\_\\\\_/   \\____/");
            System.out.println("[NettyRPC 2.0,Build 2017/09/06,Author:Qianwei. Fork and revised from http://www.cnblogs.com/jietang/]");
            System.out.println("");
        }
    }


    @Override
    public void init() {
        registerBeanDefinitionParser("service", new NettyRpcServiceParser());
        registerBeanDefinitionParser("registry", new NettyRpcRegistryParser());
        registerBeanDefinitionParser("reference", new NettyRpcReferenceParser());
    }
}
