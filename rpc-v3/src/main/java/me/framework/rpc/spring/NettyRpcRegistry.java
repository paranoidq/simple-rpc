package me.framework.rpc.spring;

import me.framework.rpc.config.RpcSystemConfig;
import me.framework.rpc.core.server.MessageRecvExecutor;
import me.framework.rpc.jmx.ThreadPoolMonitorProvider;
import me.framework.rpc.serialize.support.RpcSerializeProtocol;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * 使用InitializingBean和DisposableBean可以自定义Bean的生命周期过程中的动作
 * 但是这样讲系统与spring耦合起来了，
 *
 * 另一种比较有效的方式是在配置文件中bean的定义的内部定义init-method标签和destroy-method标签
 *
 * 现org.springframework.beans.factory.InitializingBean接口允许一个bean在它的所有必须属性被BeanFactory设置后，来执行初始化的工作
 * 实现org.springframework.beans.factory.DisposableBean接口的bean允许在容器销毁该bean的时候获得一次回调
 *
 * @author paranoidq
 * @since 1.0.0
 */
public class NettyRpcRegistry implements InitializingBean, DisposableBean {

    private String ipAddr;
    private String protocol;
    private String echoApiPort;
    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Override
    public void destroy() throws Exception {
        MessageRecvExecutor.getInstance().stop();
    }

    /**
     * Spring构建该bean之后，执行初始化：
     *      1. 创建{@link MessageRecvExecutor}实例，并注入相关属性
     *      2. 如果有线程池监控JMX，则进行注册
     *      3. 启动{@link MessageRecvExecutor
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
        ref.setServerAddress(ipAddr);
        ref.setEchoApiPort(echoApiPort);
        ref.setSerializeProtocol(
            Enum.valueOf(RpcSerializeProtocol.class, protocol)
        );
        if (RpcSystemConfig.isMonitorServerSupport()) {
            context.register(ThreadPoolMonitorProvider.class);
            context.refresh();
        }
        ref.start();
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
