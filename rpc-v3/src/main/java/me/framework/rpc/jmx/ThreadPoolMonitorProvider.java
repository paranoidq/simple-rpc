package me.framework.rpc.jmx;

import me.framework.rpc.core.server.MessageRecvExecutor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.jmx.support.MBeanServerConnectionFactoryBean;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

import javax.management.*;
import java.io.IOException;

/**
 * @author paranoidq
 * @since 1.0.0
 */
@Configuration
@EnableMBeanExport
@ComponentScan(basePackages = "me.frameworkd.rpc.jmx")
public class ThreadPoolMonitorProvider {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolMonitorProvider.class);

    public static final String DELIMITER = ":";

    public static String url;
    public static String JMX_POOL_SIZE_METHOD = "setPoolSize";
    public static String JMX_ACTIVE_COUNT_METHOD = "setActiveCount";
    public static String JMX_CORE_POOL_SIZE_METHOD = "setCoolPoolSize";
    public static String JMX_MAXIMUM_POOL_SIZE_METHOD = "setMaximumPoolSize";
    public static String JMX_LARGEST_POOL_SIZE_METHOD = "setLargestPoolSize";
    public static String JMX_TASK_COUNT_METHOD = "setTaskCount";
    public static String JMX_COMPLETED_TASK_COUNT_METHOD = "setCompletedTaskCount";

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
    public ConnectorServerFactoryBean connectServer() throws MalformedObjectNameException {
        MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
        String ipAddr = StringUtils.isNotEmpty(ref.getServerAddress()) ? StringUtils.substringBeforeLast(ref.getServerAddress(), DELIMITER) : "localhost";
        url = "service:jmx:rmi://" + ipAddr + "/jndi/rmi://" + ipAddr + ":1099/nettyrpcstatus";
        logger.info("NettyRpc jmx Monitor: [{}]", url);
        ConnectorServerFactoryBean connectorServerFactoryBean = new ConnectorServerFactoryBean();
        connectorServerFactoryBean.setObjectName("connector:name=rmi");
        connectorServerFactoryBean.setServiceUrl(url);
        return connectorServerFactoryBean;
    }

    public static void monitor(ThreadPoolStatus status) throws IOException, MalformedObjectNameException, MBeanException, InstanceNotFoundException, ReflectionException {
        MBeanServerConnectionFactoryBean mBeanServerConnectionFactoryBean = new MBeanServerConnectionFactoryBean();
        mBeanServerConnectionFactoryBean.setServiceUrl(url);
        mBeanServerConnectionFactoryBean.afterPropertiesSet();
        MBeanServerConnection connection = mBeanServerConnectionFactoryBean.getObject();
        ObjectName objectName = new ObjectName("me.framework.rpc.jmx:name=threadPoolStatus,type=ThreadPoolStatus");

        connection.invoke(objectName, JMX_POOL_SIZE_METHOD, new Object[]{status.getPoolSize()}, new String[]{int.class.getName()});
        connection.invoke(objectName, JMX_ACTIVE_COUNT_METHOD, new Object[]{status.getActiveCount()}, new String[]{int.class.getName()});
        connection.invoke(objectName, JMX_CORE_POOL_SIZE_METHOD, new Object[]{status.getCorePoolSize()}, new String[]{int.class.getName()});
        connection.invoke(objectName, JMX_MAXIMUM_POOL_SIZE_METHOD, new Object[]{status.getMaximumPoolSize()}, new String[]{int.class.getName()});
        connection.invoke(objectName, JMX_LARGEST_POOL_SIZE_METHOD, new Object[]{status.getLargestPoolSize()}, new String[]{int.class.getName()});
        connection.invoke(objectName, JMX_TASK_COUNT_METHOD, new Object[]{status.getTaskCount()}, new String[]{long.class.getName()});
        connection.invoke(objectName, JMX_COMPLETED_TASK_COUNT_METHOD, new Object[]{status.getCompletedTaskCount()}, new String[]{long.class.getName()});
    }

}
