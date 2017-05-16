package me.srpc.spring;

import com.google.common.io.CharStreams;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
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
        Resource resource = new ClassPathResource("NettyRpc-logo.txt");
        if (resource.exists()) {
            try {
                Reader reader = new InputStreamReader(resource.getInputStream(), "UTF-8");
                String text = CharStreams.toString(reader);
                System.out.println(text);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("NettyRpc 2.0 ...");
        }
    }


    /**
     * Invoked by the {@link DefaultBeanDefinitionDocumentReader} after
     * construction but before any custom elements are parsed.
     */
    @Override
    public void init() {
        registerBeanDefinitionParser("service", new NettyRpcServiceParser());
        registerBeanDefinitionParser("registry", new NettyRpcRegistryParser());
        registerBeanDefinitionParser("reference", new NettyRpcReferenceParser());
    }
}
