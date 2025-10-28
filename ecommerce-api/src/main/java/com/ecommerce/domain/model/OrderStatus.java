package com.ecommerce.domain.model;

/**
 * OrderStatus - 値オブジェクト（列挙型）
 * 注文のステータスを表す列挙型
 */
public enum OrderStatus {
    PENDING("pending"),
    CONFIRMED("confirmed"),
    SHIPPED("shipped"),
    DELIVERED("delivered"),
    CANCELLED("cancelled");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static OrderStatus fromString(String value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown order status: " + value);
    }

    /**
     * ステータスが最終状態かどうかを判定
     */
    public boolean isFinal() {
        return this == DELIVERED || this == CANCELLED;
    }

    /**
     * PENDING から CONFIRMED に変更可能か
     */
    public boolean canTransitionTo(OrderStatus nextStatus) {
        if (this.isFinal()) {
            return false;
        }

        return switch (this) {
            case PENDING -> nextStatus == CONFIRMED || nextStatus == CANCELLED;
            case CONFIRMED -> nextStatus == SHIPPED || nextStatus == CANCELLED;
            case SHIPPED -> nextStatus == DELIVERED;
            default -> false;
        };
    }
}
