package me.framework.rpc.core.server;

import me.framework.rpc.filter.Filter;
import me.framework.rpc.filter.ServiceFilterBinder;
import me.framework.rpc.model.MessageRequest;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.context.ApplicationEvent;

import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * 这段代码主要做service执行的拦截工作
 *
 * @author paranoidq
 * @since 1.0.0
 */
public class MethodProxyAdvisor implements MethodInterceptor {
    private Map<String, Object> handlerMap;
    private boolean returnNotNull = true;

    public MethodProxyAdvisor(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] params = invocation.getArguments();
        if (params.length < 0) {
            return null;
        }

        MessageRequest request = (MessageRequest) params[0];
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);
        String methodName = request.getMethodName();
        Object[] parameters = request.getParameterValues();

        /**
         *
         * TODO
         * 是否可以以此标准来判断存在filter
         * {@link me.framework.rpc.spring.NettyRpcService#onApplicationEvent(ApplicationEvent)} ??
         */
        boolean existFilter = ServiceFilterBinder.class.isAssignableFrom(serviceBean.getClass());
        ((MethodInvoker)invocation.getThis()).setServiceBean (
            existFilter ? ((ServiceFilterBinder) serviceBean).getObject() : serviceBean
        );

        if (existFilter) {
            ServiceFilterBinder processors = (ServiceFilterBinder) serviceBean;
            if (processors.getFilter() != null) {
                Filter filter = processors.getFilter();
                Object[] args = ArrayUtils.nullToEmpty(parameters);
                Class<?>[] parameterTypes = ClassUtils.toClass(args);
                Method method = MethodUtils.getMatchingAccessibleMethod(processors.getObject().getClass(), methodName, parameterTypes);
                if (filter.before(method, processors.getObject(), parameters)) {
                    Object result = invocation.proceed();
                    filter.after(method, processors.getObject(), parameters);
                    setReturnNotNull(result != null);
                    return result;
                } else {
                    return null;
                }
            }
        }

        Object result = invocation.proceed();
        setReturnNotNull(result != null);
        return result;
    }


    public boolean isReturnNotNull() {
        return returnNotNull;
    }

    public void setReturnNotNull(boolean returnNotNull) {
        this.returnNotNull = returnNotNull;
    }
}
