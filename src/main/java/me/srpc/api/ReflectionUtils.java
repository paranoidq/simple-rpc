package me.srpc.api;

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.*;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class ReflectionUtils {

    private static ImmutableMap.Builder<Class, Object> builder = ImmutableMap.builder();
    private StringBuilder provider = new StringBuilder();

    public StringBuilder getProvider() {
        return provider;
    }

    public void clearProvider() {
        this.provider.delete(0, provider.length());
    }

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

    public static Object newInstance(Class type) {
        Constructor constructor = null;
        Object[] args = new Object[0];
        try {
            constructor = type.getConstructor(new Class[]{});
        } catch (NoSuchMethodException e) {

        }

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

        try {
            return constructor.newInstance(args);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getDefaultValue(Class cls) {
        if (cls.isArray()) {
            return Array.newInstance(cls.getComponentType(), 0);
        } else if (cls.isPrimitive() || builder.build().containsKey(cls)) {
            return builder.build().get(cls);
        } else {
            return newInstance(cls);
        }
    }

    public static Class<?> getGenericClass(ParameterizedType parameterizedType, int i) {
        Object genericClass = parameterizedType.getActualTypeArguments()[i];
        if (genericClass instanceof GenericArrayType) {
            return (Class<?>) ((GenericArrayType)genericClass).getGenericComponentType();
        } else if (genericClass instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) genericClass).getRawType();
        } else {
            return (Class<?>) genericClass;
        }
    }


    private String modifiers(int m) {
        return m != 0 ? Modifier.toString(m) + " " : "";
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

    private void listFields(Field f, boolean html) {
        provider.append((html ? "&nbsp&nbsp" : " ") + modifiers(f.getModifiers()) +
            getType(f.getType()) + " " + f.getName() + (html ? ";<br>" : ";<\n>"));
    }

    /**
     * {@link Executable} requires jdk1.8
     * @param member
     * @param html
     */
    private void listMethod(Executable member, boolean html) {
        provider.append(html ? "<br>&nbsp&nbsp" : "\n" + modifiers(member.getModifiers()));
        if (member instanceof Method) {
            provider.append(getType(((Method) member).getReturnType()) + " ");
        }
        provider.append(member.getName() + "(");
        listTypes(member.getParameterTypes());
        provider.append(")");
        Class<?>[] exceptions = member.getExceptionTypes();
        if (exceptions.length > 0) {
            provider.append("throws ");
        }
        listTypes(exceptions);
        provider.append(";");
    }


    public void listRpcProviderDetail(Class<?> cls, boolean html) {
        if (!cls.isInterface()) {
            return;
        } else {
            provider.append(Modifier.toString(cls.getModifiers()) + " " + cls.getName());
            provider.append(html ? " {<br>" : " {\n");

            boolean hasFields = false;
            Field[] fields = cls.getDeclaredFields();
            if (fields.length != 0) {
                provider.append(html ? "&nbsp&nbsp//&nbspFields<br>" : "// Fields\n");
                hasFields = true;
                for (Field field : fields) {
                    listFields(field, html);
                }
            }
            provider.append(hasFields ? (html ? "<br>&nbsp&nbsp//&nbspMethods" : "\n  // Methods") : (html ? "&nbsp&nbsp//&nbspMethods" : "  // Methods"));
            Method[] methods = cls.getDeclaredMethods();
            for (Method method : methods) {
                listMethod(method, html);
            }
            provider.append(html ? "<br>}<p>" : "\n}\n\n");
        }
    }


}
