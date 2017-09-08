package me.framework.rpc.spring;

import com.google.common.eventbus.EventBus;
import me.framework.rpc.core.client.MessageSendExecutor;
import me.framework.rpc.event.ClientStopEvent;
import me.framework.rpc.event.ClientStopEventListener;
import me.framework.rpc.serialize.support.RpcSerializeProtocol;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class NettyRpcReference implements FactoryBean, InitializingBean, DisposableBean {

    private String interfaceName;
    private String ipAddr;
    private String protocol;
    private EventBus eventBus = new EventBus();

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }


    /**
     * 销毁bean时通过EventBus通知注册的监听器，回收相关资源
     *
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        eventBus.post(new ClientStopEvent(0));
    }

    @Override
    public Object getObject() throws Exception {
        return MessageSendExecutor.getInstance().execute(getObjectType());
    }

    @Override
    public Class<?> getObjectType() {
        try {
            return this.getClass().getClassLoader().loadClass(interfaceName);
        } catch (ClassNotFoundException e) {
            System.err.println("spring analyze fail!");
        }
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        MessageSendExecutor.getInstance().setRpcServerLoader(ipAddr, RpcSerializeProtocol.valueOf(protocol));

        // 注册ClientStopEventListener，监听ClientStopEvent事件，在Client销毁时清理资源
        ClientStopEventListener listener = new ClientStopEventListener();
        eventBus.register(listener);
    }
}
