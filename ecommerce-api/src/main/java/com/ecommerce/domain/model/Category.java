package com.ecommerce.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Category - エンティティ（集約ルート）
 * ドメイン層のカテゴリエンティティ
 * JPA依存なし
 */
public class Category {
    private CategoryId id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * プライベートコンストラクタ - ドメイン層内での構築用
     */
    private Category() {
    }

    /**
     * 新規カテゴリの作成
     */
    public static Category create(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        Category category = new Category();
        category.name = name;
        category.description = description;
        category.createdAt = LocalDateTime.now();
        category.updatedAt = LocalDateTime.now();
        return category;
    }

    /**
     * 既存カテゴリの復元（リポジトリから読み込む際）
     */
    public static Category restore(Long id, String name, String description,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        Category category = new Category();
        category.id = new CategoryId(id);
        category.name = name;
        category.description = description;
        category.createdAt = createdAt;
        category.updatedAt = updatedAt;
        return category;
    }

    // ゲッター
    public CategoryId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * カテゴリ名を更新
     */
    public void updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        this.name = newName;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 説明を更新
     */
    public void updateDescription(String newDescription) {
        this.description = newDescription;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
