package me.framework.rpc.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public final class MessageRequest implements Serializable {

    private static final long serialVersionUID = 4311927371520632075L;

    private String messageId;
    private String className;
    private String methodName;
    private Class<?>[] typeParameters;
    private Object[] parameterValues;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getTypeParameters() {
        return typeParameters;
    }

    public void setTypeParameters(Class<?>[] typeParameters) {
        this.typeParameters = typeParameters;
    }

    public Object[] getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(Object[] parameterValues) {
        this.parameterValues = parameterValues;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, new String[]{"typeParameters", "parameterValues"});
    }
}
