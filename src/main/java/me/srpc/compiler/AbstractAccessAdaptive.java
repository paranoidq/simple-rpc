package me.srpc.compiler;

import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public abstract class AbstractAccessAdaptive implements Compiler {
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([$_a-zA-Z][$_a-zA-Z0-9\\.]*);");
    private static final Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s+");

    protected ClassLoader overrideThreadContextClassLoader(ClassLoader loader) {
        Thread currentThread = Thread.currentThread();
        ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
        if (loader != null && !loader.equals(threadContextClassLoader)) {
            currentThread.setContextClassLoader(loader);
            return threadContextClassLoader;
        } else {
            return null;
        }
    }

    protected ClassLoader getClassLoader() {
        ClassLoader classLoader = null;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        } catch (Throwable e) {

        }
        if (classLoader == null) {
            classLoader = AbstractAccessAdaptive.class.getClassLoader();
            if (classLoader == null) {
                try {
                    classLoader = ClassLoader.getSystemClassLoader();
                } catch (Throwable e) {

                }
            }
        }
        return classLoader;
    }

    protected String report(Throwable e) {
        StringWriter w = new StringWriter();
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName() + ": ");
        if (e.getMessage() != null) {
            p.print(e.getMessage() + "\n");
        }
        p.println();
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }


    @Override
    public Class<?> compile(String code, ClassLoader classLoader) {
        code = code.trim();
        Matcher matcher = PACKAGE_PATTERN.matcher(code);
        String pkg;
        if (matcher.find()) {
            pkg = matcher.group();
        } else {
            pkg = "";
        }

        matcher = CLASS_PATTERN.matcher(code);
        String cls;
        if (matcher.find()) {
            cls = matcher.group(1);
        } else {
            throw new IllegalStateException("No such class name in " + code);
        }

        String className = StringUtils.isNotBlank(pkg) ? pkg + "." + cls : cls;
        try {
            return Class.forName(className, true, (classLoader != null ? classLoader : getClassLoader()));
        } catch (ClassNotFoundException e) {
            if (!code.endsWith("}")) {
                throw new IllegalStateException("The java code not ends with \"}\", code: \n" + code + "\n");
            }
            try {
                return doCompile(className, code);
            } catch (RuntimeException re) {
                throw re;
            } catch (Throwable t) {
                throw new IllegalStateException("failed to compile class, cause: " + t.getMessage() + ", class: " + className + ", code: \n" + code + "\n, stack: " + report(t));
            }
        }
    }

    protected abstract Class<?> doCompile(String className, String javaSource) throws Throwable;
}
