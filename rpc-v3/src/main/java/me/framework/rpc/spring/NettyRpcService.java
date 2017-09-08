package me.framework.rpc.spring;

import com.sun.corba.se.spi.activation.Server;
import me.framework.rpc.core.server.MessageRecvExecutor;
import me.framework.rpc.event.ServerStartEvent;
import me.framework.rpc.filter.Filter;
import me.framework.rpc.filter.ServiceFilterBinder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class NettyRpcService implements ApplicationContextAware, ApplicationListener {

    private String interfaceName;
    private String ref;
    private String filter;
    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        applicationContext.publishEvent(new ServerStartEvent(new Object()));
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        ServiceFilterBinder binder = new ServiceFilterBinder();

        // TODO
        // 如果不存在filter就只绑定service bean，否则也要绑定filter bean
        if (StringUtils.isBlank(filter) || !(applicationContext.getBean(filter) instanceof Filter)) {
            binder.setObject(applicationContext.getBean(ref));
        } else {
            binder.setObject(applicationContext.getBean(ref));
            binder.setFilter((Filter) applicationContext.getBean(filter));
        }

        // 放入service池
        MessageRecvExecutor.getInstance().getHandlerMap().put(interfaceName, binder);
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
