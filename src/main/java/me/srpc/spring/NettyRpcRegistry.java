package me.srpc.spring;

import me.srpc.common.RpcSystemConfig;
import me.srpc.jmx.ThreadPoolMonitorProvider;
import me.srpc.netty.server.NettyRpcServer;
import me.srpc.serialize.RpcSerializeProtocol;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class NettyRpcRegistry implements InitializingBean, DisposableBean{
    private String ipAddr;
    private String protocol;
    private String echoApiPort;
    private AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();

    /**
     * Invoked by a BeanFactory on destruction of a singleton.
     *
     * @throws Exception in case of shutdown errors.
     *                   Exceptions will get logged but not rethrown to allow
     *                   other beans to release their resources too.
     */
    @Override
    public void destroy() throws Exception {
        NettyRpcServer.getInstance().stop();
    }

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied
     * (and satisfied BeanFactoryAware and ApplicationContextAware).
     * <p>This method allows the bean instance to perform initialization only
     * possible when all bean properties have been set and to throw an
     * exception in the event of misconfiguration.
     *
     * @throws Exception in the event of misconfiguration (such
     *                   as failure to set an essential property) or if initialization fails.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        NettyRpcServer server = NettyRpcServer.getInstance();
        server.setServerAddress(ipAddr);
        server.setEchoApiPort(Integer.parseInt(echoApiPort));
        server.setProtocol(Enum.valueOf(RpcSerializeProtocol.class, protocol));

        if (RpcSystemConfig.isMonitorServerSupport()) {
            configApplicationContext.register(ThreadPoolMonitorProvider.class);
            configApplicationContext.refresh();
        }
        server.start();
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

    public String getEchoApiPort() {
        return echoApiPort;
    }

    public void setEchoApiPort(String echoApiPort) {
        this.echoApiPort = echoApiPort;
    }
}
