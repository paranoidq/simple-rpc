package me.srpc.compiler;

import javax.tools.*;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class NativeCompiler implements Closeable {
    private final File tempFileFolder;
    private final URLClassLoader classLoader;


    public NativeCompiler(File tempFileFolder) {
        this.tempFileFolder = tempFileFolder;
        this.classLoader = createClassLoader(tempFileFolder);
    }

    private static URLClassLoader createClassLoader(File tempFileFolder) {
        try {
            URL[] urls = {tempFileFolder.toURI().toURL()};
            return new URLClassLoader(urls);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public Class<?> compile(String className, String code) {
        try {
            JavaFileObject sourceFile = new StringJavaFileObject(className, code);
            compileClass(sourceFile);
            return classLoader.loadClass(className);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private void compileClass(JavaFileObject sourceFile) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = null;
        try {
            fileManager = compiler.getStandardFileManager(collector, Locale.ROOT, null);
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(tempFileFolder));
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, collector, null, null, Arrays.asList(sourceFile));
            task.call();
        } finally {
            fileManager.close();
        }
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     * <p>
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        try {
            classLoader.close();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
