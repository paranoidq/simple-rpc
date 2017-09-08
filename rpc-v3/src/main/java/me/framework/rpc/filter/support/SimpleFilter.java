package me.framework.rpc.filter.support;

import me.framework.rpc.filter.Filter;
import me.framework.rpc.logger.AppLoggerInject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class SimpleFilter implements Filter {
    @AppLoggerInject
    private static Logger logger;

    @Override
    public boolean before(Method method, Object processor, Object[] requestObjects) {
        logger.info(StringUtils.center("[SimpleFilter##before]", 48, "*"));
        return true;
    }

    @Override
    public void after(Method method, Object processor, Object[] requestObjects) {
        logger.info(StringUtils.center("[SimpleFilter##after]", 48, "*"));
    }
}
