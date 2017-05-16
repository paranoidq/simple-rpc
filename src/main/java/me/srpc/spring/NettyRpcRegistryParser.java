package me.srpc.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class NettyRpcRegistryParser implements BeanDefinitionParser {


    /**
     * Parse the specified {@link Element} and register the resulting
     * {@link BeanDefinition BeanDefinition(s)} with the
     * {@link ParserContext#getRegistry() BeanDefinitionRegistry}
     * embedded in the supplied {@link ParserContext}.
     * <p>Implementations must return the primary {@link BeanDefinition} that results
     * from the parse if they will ever be used in a nested fashion (for example as
     * an inner tag in a {@code <property/>} tag). Implementations may return
     * {@code null} if they will <strong>not</strong> be used in a nested fashion.
     *
     * @param element       the element that is to be parsed into one or more {@link BeanDefinition BeanDefinitions}
     * @param parserContext the object encapsulating the current state of the parsing process;
     *                      provides access to a {@link BeanDefinitionRegistry}
     * @return the primary {@link BeanDefinition}
     */
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String id = element.getAttribute("id");
        String ipAddr = element.getAttribute("ipAddr");
        String echoApiPort = element.getAttribute("echoApiPort");
        String protocolType = element.getAttribute("protocol");

        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(NettyRpcRegistry.class);
        beanDefinition.setLazyInit(false);
        beanDefinition.getPropertyValues().addPropertyValue("ipAddr", ipAddr);
        beanDefinition.getPropertyValues().addPropertyValue("echoApiPort", echoApiPort);
        beanDefinition.getPropertyValues().addPropertyValue("protocol", protocolType);
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
        return beanDefinition;
    }
}
