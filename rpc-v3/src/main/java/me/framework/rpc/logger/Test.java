package me.framework.rpc.logger;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class Test {

    @AppLoggerInject
    private static org.apache.log4j.Logger logger;

    public static void main(String[] args) {
        AppLoggerInjector.injectAll("me.package");
        logger.info("成功注入");
    }
}
