package me.srpc.compiler;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public interface AccessAdaptive {

    Object invoke(String code, String method, Object[] args);
}
