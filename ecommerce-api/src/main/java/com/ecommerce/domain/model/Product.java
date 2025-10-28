package com.ecommerce.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Product - エンティティ（集約ルート）
 * ドメイン層の商品エンティティ
 * JPA依存なし
 */
public class Product {
    private ProductId id;
    private String name;
    private String description;
    private Money price;
    private Integer stock;
    private CategoryId categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * プライベートコンストラクタ
     */
    private Product() {
    }

    /**
     * 新規商品の作成
     */
    public static Product create(String name, String description, Money price,
                                 Integer stock, CategoryId categoryId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (price == null || price.isZero()) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("Product must belong to a category");
        }

        Product product = new Product();
        product.name = name;
        product.description = description;
        product.price = price;
        product.stock = stock;
        product.categoryId = categoryId;
        product.createdAt = LocalDateTime.now();
        product.updatedAt = LocalDateTime.now();
        return product;
    }

    /**
     * 既存商品の復元
     */
    public static Product restore(Long id, String name, String description,
                                  Money price, Integer stock, CategoryId categoryId,
                                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        Product product = new Product();
        product.id = new ProductId(id);
        product.name = name;
        product.description = description;
        product.price = price;
        product.stock = stock;
        product.categoryId = categoryId;
        product.createdAt = createdAt;
        product.updatedAt = updatedAt;
        return product;
    }

    // ゲッター
    public ProductId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Money getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public CategoryId getCategoryId() {
        return categoryId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 商品情報を更新
     */
    public void updateInfo(String name, String description, Money price) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (price != null && !price.isZero()) {
            this.price = price;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 在庫を増やす
     */
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.stock += quantity;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 在庫を減らす
     */
    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (this.stock < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        this.stock -= quantity;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 在庫が十分かチェック
     */
    public boolean hasEnoughStock(int quantity) {
        return this.stock >= quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
