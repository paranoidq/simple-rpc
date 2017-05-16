package me.srpc.compiler;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.NoOp;

import java.util.List;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ClassProxy {
    public <T> Class<T> createDynamicSubClass(Class<T> superClass) {
        Enhancer e = new Enhancer() {
            @Override
            protected void filterConstructors(Class sc, List constructors) {
                super.filterConstructors(sc, constructors);
                // FIXME: 2017/3/30 by tangjie
                // maybe change javassist support
            }
        };
        if (superClass.isInterface()) {
            e.setInterfaces(new Class[] {superClass});
        } else {
            e.setSuperclass(superClass);
        }

        e.setCallbackType(NoOp.class);
        Class<T> proxyClass = e.createClass();
        return proxyClass;
    }
}
