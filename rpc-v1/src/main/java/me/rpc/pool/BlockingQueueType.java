package me.rpc.pool;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public enum BlockingQueueType {

    LINKED_BLOCKING_QUEUE("LinkedBlockingQueue"),
    ARRAY_BLOCKING_QUEUE("ArrayBlockingQueue"),
    SYNCHRONOUS_QUEUE("SynchronousQueue")
    ;

    private String value;

    BlockingQueueType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static BlockingQueueType fromString(final String value) {
        for (BlockingQueueType type : BlockingQueueType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Mismatched type with value=" + value);
    }

    public String toString() {
        return value;
    }
}
