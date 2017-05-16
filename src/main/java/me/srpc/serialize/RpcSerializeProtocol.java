package me.srpc.serialize;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public enum RpcSerializeProtocol {

    JDK_SERIALIZE("jdknative"),
    KRYO_SERIALIZE("kryo"),
    HESSIAN_SERIALIZE("hessian"),
    PROTOSTUFF_SERIALIZE("protostuff");


    private String serializeProtocol;

    RpcSerializeProtocol(String serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
    }

    public String toString() {
        ReflectionToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);
        return ReflectionToStringBuilder.toString(this);
    }

    public String getProtocol() {
        return this.serializeProtocol;
    }
}
