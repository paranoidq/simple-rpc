package me.srpc.compiler;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class StringJavaFileObject extends SimpleJavaFileObject {

    private static final String SCHEME = "string://";
    private final String code;

    public StringJavaFileObject(String className, String code) {
        super(URI.create(SCHEME + className.replace(".", "/") + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return code;
    }
}
