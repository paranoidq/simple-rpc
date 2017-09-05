package me.framework.rpc.serialize.support;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 序列化协议
 *
 * @author paranoidq
 * @since 1.0.0
 */
public enum RpcSerializeProtocol {

    JDK_SERIALIZE("jdknative"),
    KRYO_SERIAZLIZE("kyro"),
    HESSIAN_SERIALIZE("hessian")
    ;

    private String protocol;

    RpcSerializeProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        ReflectionToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);
        return ReflectionToStringBuilder.toString(this);
    }

    public String getProtocol() {
        return protocol;
    }
}
