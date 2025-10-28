package com.ecommerce.domain.model;

import java.util.Objects;

/**
 * CustomerId - 値オブジェクト
 * 顧客の識別子を表すクラス
 */
public class CustomerId {
    private final Long value;

    public CustomerId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("CustomerId must be a positive number");
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
        CustomerId that = (CustomerId) o;
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
