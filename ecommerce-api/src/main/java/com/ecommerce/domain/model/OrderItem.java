package com.ecommerce.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * OrderItem - エンティティ（集約の一部）
 * 注文明細
 * JPA依存なし
 */
public class OrderItem {
    private Long id;  // マッピング用のみ、ドメインロジックでは使用しない
    private ProductId productId;
    private Integer quantity;
    private Money priceAtPurchase;
    private LocalDateTime createdAt;

    /**
     * プライベートコンストラクタ
     */
    private OrderItem() {
    }

    /**
     * 新規注文明細の作成
     */
    public static OrderItem create(ProductId productId, Integer quantity, Money priceAtPurchase) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (priceAtPurchase == null || priceAtPurchase.isZero()) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        OrderItem item = new OrderItem();
        item.productId = productId;
        item.quantity = quantity;
        item.priceAtPurchase = priceAtPurchase;
        item.createdAt = LocalDateTime.now();
        return item;
    }

    /**
     * 既存注文明細の復元
     */
    public static OrderItem restore(Long id, ProductId productId, Integer quantity,
                                    Money priceAtPurchase, LocalDateTime createdAt) {
        OrderItem item = new OrderItem();
        item.id = id;
        item.productId = productId;
        item.quantity = quantity;
        item.priceAtPurchase = priceAtPurchase;
        item.createdAt = createdAt;
        return item;
    }

    // ゲッター
    public Long getId() {
        return id;
    }

    public ProductId getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Money getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * この明細の小計を計算（価格 × 数量）
     */
    public Money getSubtotal() {
        return priceAtPurchase.multiply(quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem item = (OrderItem) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
