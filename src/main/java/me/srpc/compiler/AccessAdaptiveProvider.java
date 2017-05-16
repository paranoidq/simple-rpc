package me.srpc.compiler;

import com.google.common.io.Files;
import me.srpc.api.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class AccessAdaptiveProvider extends AbstractAccessAdaptive implements AccessAdaptive {


    @Override
    public Object invoke(String javaSource, String method, Object[] args) {
        if (StringUtils.isEmpty(javaSource) || StringUtils.isEmpty(method)) {
            return null;
        }

        try {
            ClassProxy main = new ClassProxy();
            Class type = compile(javaSource, null);
            Class<?> objectClass = main.createDynamicSubClass(type);
            Object object = ReflectionUtils.newInstance(objectClass);
            return MethodUtils.invokeMethod(object, method, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Class<?> doCompile(String className, String javaSource) throws Throwable {
        NativeCompiler compiler = null;
        try {
            File tempFileLocation = Files.createTempDir();
            compiler = new NativeCompiler(tempFileLocation);
            Class type = compiler.compile(className, javaSource);
            return type;
        } finally {
            compiler.close();
        }
    }
}
