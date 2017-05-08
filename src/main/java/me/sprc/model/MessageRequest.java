package me.sprc.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import sun.plugin2.message.Message;

import java.io.Serializable;

/**
 * Request message definition
 *
 * @author paranoidq
 * @since 1.0.0
 */
public class MessageRequest implements Serializable {

    private String messageId;
    private String className;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] paramValues;

    public MessageRequest() {

    }

    public MessageRequest(String messageId, String className, String methodName, Class<?>[] paramTypes, Object[] paramValues) {
        this.messageId = messageId;
        this.className = className;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.paramValues = paramValues;
    }

    public static MessageRequest create(String messageId, String className, String methodName, Class<?>[] paramTypes, Object[] paramValues) {
        return new MessageRequest(messageId, className, methodName, paramTypes, paramValues);
    }


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

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParamValues() {
        return paramValues;
    }

    public void setParamValues(Object[] paramValues) {
        this.paramValues = paramValues;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("messageId", messageId)
            .append("className", className)
            .append("methodName", methodName)
            .toString();
    }
}
