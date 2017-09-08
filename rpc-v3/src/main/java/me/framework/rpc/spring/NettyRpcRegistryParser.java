package me.framework.rpc.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class NettyRpcRegistryParser implements BeanDefinitionParser {


    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String id = element.getAttribute("id");
        String ipAddr = element.getAttribute("ipAddr");
        String echoApiPort = element.getAttribute("echoApiPort");
        String protocol = element.getAttribute("protocol");

        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(NettyRpcRegistry.class);
        beanDefinition.getPropertyValues().addPropertyValue("ipAddr", ipAddr);
        beanDefinition.getPropertyValues().addPropertyValue("echoApiPort", echoApiPort);
        beanDefinition.getPropertyValues().addPropertyValue("protocol", protocol);
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);

        return beanDefinition;
    }
}
