package com.ecommerce.domain.model;

import java.util.Objects;

/**
 * ProductId - 値オブジェクト
 * 商品の識別子を表すクラス
 */
public class ProductId {
    private final Long value;

    public ProductId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ProductId must be a positive number");
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
        ProductId that = (ProductId) o;
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
