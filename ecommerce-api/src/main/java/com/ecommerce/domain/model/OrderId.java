package com.ecommerce.domain.model;

import java.util.Objects;

/**
 * OrderId - 値オブジェクト
 * 注文の識別子を表すクラス
 */
public class OrderId {
    private final Long value;

    public OrderId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("OrderId must be a positive number");
        }
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId that = (OrderId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
