package me.srpc.compiler;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public interface Compiler {

    Class<?> compile(String code, ClassLoader classLoader);
}
