package me.framework.rpc.util;

import com.google.common.collect.ImmutableMap;
import me.framework.rpc.exception.CreateProxyException;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ReflectionUtils {

    private static ImmutableMap.Builder<Class, Object> builder
        = ImmutableMap.builder();
    private StringBuilder provider = new StringBuilder();


    static {
        builder.put(Boolean.class, Boolean.FALSE);
        builder.put(Byte.class, Byte.valueOf((byte) 0));
        builder.put(Character.class, Character.valueOf((char) 0));
        builder.put(Short.class, Short.valueOf((short) 0));
        builder.put(Double.class, Double.valueOf(0));
        builder.put(Float.class, Float.valueOf(0));
        builder.put(Integer.class, Integer.valueOf(0));
        builder.put(Long.class, Long.valueOf(0));
        builder.put(boolean.class, Boolean.FALSE);
        builder.put(byte.class, Byte.valueOf((byte) 0));
        builder.put(char.class, Character.valueOf((char) 0));
        builder.put(short.class, Short.valueOf((short) 0));
        builder.put(double.class, Double.valueOf(0));
        builder.put(float.class, Float.valueOf(0));
        builder.put(int.class, Integer.valueOf(0));
        builder.put(long.class, Long.valueOf(0));
    }


    public static Class<?>[] filterInterfaces(Class<?>[] proxyClasses) {
        Set<Class<?>> interfaces = new HashSet<Class<?>>();
        for (Class<?> proxyClass : proxyClasses) {
            if (proxyClass.isInterface()) {
                interfaces.add(proxyClass);
            }
        }
        interfaces.add(Serializable.class);
        return interfaces.toArray(new Class[interfaces.size()]);
    }

    public static Class<?>[] filterNonInterfaces(Class<?>[] proxyClasses) {
        Set<Class<?>> nonInterfaces = new HashSet<Class<?>>();
        for (Class<?> proxyClass : proxyClasses) {
            if (!proxyClass.isInterface()) {
                nonInterfaces.add(proxyClass);
            }
        }
        return nonInterfaces.toArray(new Class[nonInterfaces.size()]);
    }

    public static boolean existDefaultConstructor(Class<?> clazz) {
        final Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
        for (Constructor constructor : declaredConstructors) {
            if (constructor.getParameterTypes().length == 0
                && Modifier.isPublic(constructor.getModifiers())) {
                return true;
            }
        }
        return false;
    }

    public static Class<?> getParentClassForProxy(Class<?>[] proxyClasses) {
        final Class<?>[] parents = filterNonInterfaces(proxyClasses);
        switch (parents.length) {
            case 0:
                return Object.class;
            case 1:
                Class<?> superClass = parents[0];
                if (Modifier.isFinal(superClass.getModifiers())) {
                    if (Modifier.isFinal(superClass.getModifiers())) {
                        throw new CreateProxyException(
                            "proxy can't build " + superClass.getName() + " because it is final");
                    }
                    if (!existDefaultConstructor(superClass)) {
                        throw new CreateProxyException(
                            "proxy can't build " + superClass.getName() + ", because it has no default constructor");
                    }
                    return superClass;
                }
            default:
                StringBuilder errorMessage = new StringBuilder("proxy class can't build");
                for (int i = 0; i < parents.length; i++) {
                    Class<?> c = parents[i];
                    errorMessage.append(c.getName());
                    if (i != parents.length - 1) {
                        errorMessage.append(", ");
                    }
                }

                errorMessage.append("; multiple implement not allowed");
                throw new CreateProxyException(errorMessage.toString());
        }
    }

    public static boolean isHashCodeMethod(Method method) {
        return "hashCode".equals(method.getName()) && Integer.TYPE.equals(method.getReturnType())
            && method.getParameterTypes().length == 0;
    }

    public static boolean isEqualsMethod(Method method) {
        return "equals".equals(method.getName()) && Boolean.TYPE.equals(method.getReturnType())
            && method.getParameterTypes().length == 1 && Object.class.equals(method.getParameterTypes()[0]);
    }


    public static <T> T newInstance(Class<T> type) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor constructor = null;
        Object[] args = new Object[0];
        try {
            constructor = type.getConstructor(new Class<?>[]{});
        } catch (NoSuchMethodException e) {
        }

        // 如果不存在默认构造函数，则取第一个含参构造函数，并传入默认值
        if (constructor == null) {
            Constructor[] constructors = type.getConstructors();
            if (constructors.length == 0) {
                return null;
            }
            constructor = constructors[0];
            Class[] params = constructor.getParameterTypes();
            args = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                args[i] = getDefaultValue(params[i]);
            }
        }
        return (T) constructor.newInstance(args);
    }

    public static Object getDefaultValue(Class<?> clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (clazz.isArray()) {
            return Array.newInstance(clazz.getComponentType(), 0);
        } else if (clazz.isPrimitive() || builder.build().containsKey(clazz)) {
            return builder.build().get(clazz);
        } else {
            return newInstance(clazz);
        }
    }

    public static Class<?> getGenericClass(ParameterizedType parameterizedType, int i) {
        Object genericClass = parameterizedType.getActualTypeArguments()[i];
        if (genericClass instanceof GenericArrayType) {
            return (Class<?>) ((GenericArrayType) genericClass).getGenericComponentType();
        } else if (genericClass instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) genericClass).getRawType();
        } else {
            return (Class<?>) genericClass;
        }
    }

    private String modifiers(int m) {
        return  m != 0 ? Modifier.toString(m) + " " : "";
    }

    private String getType(Class<?> t) {
        String brackets = "";
        while (t.isArray()) {
            brackets += "[]";
            t = t.getComponentType();
        }
        return t.getName() + brackets;
    }

    private void listTypes(Class<?>[] types) {
        for (int i = 0; i < types.length; i++) {
            if (i > 0) {
                provider.append(", ");
            }
            provider.append(getType(types[i]));
        }
    }

    private void listField(Field f, boolean html) {
        provider.append((html ? "&nbsp&nbsp" : "  ") + modifiers(f.getModifiers()) +
            getType(f.getType()) + " " +
            f.getName() + (html ? ";<br>" : ";\n"));
    }

    private void listMethod(Executable member, boolean html) {
        provider.append(html ? "<br>&nbsp&nbsp" : "\n  " + modifiers(member.getModifiers()));
        if (member instanceof Method) {
            provider.append(getType(((Method) member).getReturnType()) + " ");
        }
        provider.append(member.getName() + "(");
        listTypes(member.getParameterTypes());
        provider.append(")");
        Class<?>[] exceptions = member.getExceptionTypes();
        if (exceptions.length > 0) {
            provider.append(" throws ");
        }
        listTypes(exceptions);
        provider.append(";");
    }

    public void listRpcProviderDetail(Class<?> c, boolean html) {
        if (!c.isInterface()) {
            return;
        } else {
            provider.append(Modifier.toString(c.getModifiers()) + " " + c.getName());
            provider.append(html ? " {<br>" : " {\n");

            boolean hasFields = false;
            Field[] fields = c.getDeclaredFields();
            if (fields.length != 0) {
                provider.append(html ? "&nbsp&nbsp//&nbspFields<br>" : "  // Fields\n");
                hasFields = true;
                for (Field field : fields) {
                    listField(field, html);
                }
            }

            provider.append(hasFields ? (html ? "<br>&nbsp&nbsp//&nbspMethods" : "\n  // Methods") : (html ? "&nbsp&nbsp//&nbspMethods" : "  // Methods"));
            Method[] methods = c.getDeclaredMethods();
            for (Method method : methods) {
                listMethod(method, html);
            }
            provider.append(html ? "<br>}<p>" : "\n}\n\n");
        }
    }

    public StringBuilder getProvider() {
        return provider;
    }
}
