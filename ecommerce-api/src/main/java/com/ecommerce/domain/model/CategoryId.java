package com.ecommerce.domain.model;

import java.util.Objects;

/**
 * CategoryId - 値オブジェクト
 * カテゴリの識別子を表すクラス
 */
public class CategoryId {
    private final Long value;

    public CategoryId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("CategoryId must be a positive number");
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
        CategoryId that = (CategoryId) o;
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
