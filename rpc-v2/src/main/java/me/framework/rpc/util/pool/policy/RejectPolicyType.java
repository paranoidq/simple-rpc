package me.framework.rpc.util.pool.policy;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public enum RejectPolicyType {
    BLOCKING_POLICY("BlockingPolicy"),
    CALLER_RUNS_POLICY("CallerRunsPolicy"),
    ABORT_POLICY("AbortPolicy"),
    DISCARDED_POLICY("DiscardedPolicy"),
    REJECTED_POLICY("RejectedPolicy")
    ;

    private String value;

    RejectPolicyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RejectPolicyType fromString(String value) {
        for (RejectPolicyType type : RejectPolicyType.values()) {
            if (type.getValue().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Mismatched type with value=" + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
