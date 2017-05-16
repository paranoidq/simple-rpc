package me.srpc.jmx;

import me.srpc.common.Constants;
import me.srpc.netty.server.NettyRpcServer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.*;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.jmx.support.MBeanServerConnectionFactoryBean;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * @author paranoidq
 * @since 1.0.0
 */
@Configuration
@EnableMBeanExport
@ComponentScan("me.srpc.jmx")
public class ThreadPoolMonitorProvider {

    public final static String DELIMITER = Constants.COLON;
    public static String url;
    public static String jmxPoolSizeMethod = "setPoolSize";
    public static String jmxActiveCountMethod = "setActiveCount";
    public static String jmxCorePoolSizeMethod = "setCorePoolSize";
    public static String jmxMaximumPoolSizeMethod = "setMaximumPoolSize";
    public static String jmxLargestPoolSizeMethod = "setLargestPoolSize";
    public static String jmxTaskCountMethod = "setTaskCount";
    public static String jmxCompletedTaskCountMethod = "setCompletedTaskCount";


    @Bean
    public ThreadPoolStatus threadPoolStatus() {
        return new ThreadPoolStatus();
    }

    @Bean
    public MBeanServerFactoryBean mbeanServer() {
        return new MBeanServerFactoryBean();
    }

    @Bean
    public RmiRegistryFactoryBean registry() {
        return new RmiRegistryFactoryBean();
    }

    @Bean
    @DependsOn("registry")
    public ConnectorServerFactoryBean connectorServer() throws MalformedObjectNameException {
        NettyRpcServer ref = NettyRpcServer.getInstance();
        String ipAddr = StringUtils.isNotEmpty(ref.getServerAddress()) ?
            StringUtils.substringBeforeLast(ref.getServerAddress(), DELIMITER) : "localhost";
        url = "service:jmx:rmi://" + ipAddr + "/jndi/rmi://" + ipAddr + ":1099/nettyrpcstatus";
        System.out.println("Netty Rpc JMX monitorURL: [" + url + "]");
        ConnectorServerFactoryBean connectorServerFactoryBean = new ConnectorServerFactoryBean();
        connectorServerFactoryBean.setObjectName("connector:name=rmi");
        connectorServerFactoryBean.setServiceUrl(url);
        return connectorServerFactoryBean;
    }

    public static void provide(ThreadPoolStatus status) throws Exception {
        MBeanServerConnectionFactoryBean mBeanServerConnectionFactoryBean = new MBeanServerConnectionFactoryBean();
        mBeanServerConnectionFactoryBean.setServiceUrl(url);
        mBeanServerConnectionFactoryBean.afterPropertiesSet();
        MBeanServerConnection connection = mBeanServerConnectionFactoryBean.getObject();
        ObjectName objectName = new ObjectName("me.srpc.paralle.jmx:name=threadPoolStatus, type=ThreadPoolStatus");

        connection.invoke(objectName, jmxPoolSizeMethod, new Object[]{status.getPoolSize()}, new String[]{int.class.getName()});
        connection.invoke(objectName, jmxActiveCountMethod, new Object[]{status.getActiveCount()}, new String[]{int.class.getName()});
        connection.invoke(objectName, jmxCorePoolSizeMethod, new Object[]{status.getCorePoolSize()}, new String[]{int.class.getName()});
        connection.invoke(objectName, jmxMaximumPoolSizeMethod, new Object[]{status.getMaximumPoolSize()}, new String[]{int.class.getName()});
        connection.invoke(objectName, jmxLargestPoolSizeMethod, new Object[]{status.getLargestPoolSize()}, new String[]{int.class.getName()});
        connection.invoke(objectName, jmxTaskCountMethod, new Object[]{status.getTaskCount()}, new String[]{long.class.getName()});
        connection.invoke(objectName, jmxCompletedTaskCountMethod, new Object[]{status.getCompletedTaskCount()}, new String[]{long.class.getName()});

    }
}
