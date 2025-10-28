package com.ecommerce.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Order - エンティティ（集約ルート）
 * ドメイン層の注文エンティティ
 * JPA依存なし
 * OrderItem を含むことで集約を形成
 */
public class Order {
    private OrderId id;
    private CustomerId customerId;
    private OrderStatus status;
    private Money totalPrice;
    private List<OrderItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * プライベートコンストラクタ
     */
    private Order() {
        this.items = new ArrayList<>();
    }

    /**
     * 新規注文の作成
     */
    public static Order create(CustomerId customerId, List<OrderItem> items) {
        if (customerId == null) {
            throw new IllegalArgumentException("CustomerId cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        Order order = new Order();
        order.customerId = customerId;
        order.items = new ArrayList<>(items);
        order.status = OrderStatus.PENDING;
        order.totalPrice = calculateTotal(items);
        order.createdAt = LocalDateTime.now();
        order.updatedAt = LocalDateTime.now();
        return order;
    }

    /**
     * 既存注文の復元
     */
    public static Order restore(Long id, CustomerId customerId, OrderStatus status,
                                Money totalPrice, List<OrderItem> items,
                                LocalDateTime createdAt, LocalDateTime updatedAt) {
        Order order = new Order();
        order.id = new OrderId(id);
        order.customerId = customerId;
        order.status = status;
        order.totalPrice = totalPrice;
        order.items = new ArrayList<>(items);
        order.createdAt = createdAt;
        order.updatedAt = updatedAt;
        return order;
    }

    // ゲッター
    public OrderId getId() {
        return id;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Money getTotalPrice() {
        return totalPrice;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * ステータスを更新
     */
    public void updateStatus(OrderStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", status, newStatus)
            );
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 注文をキャンセル
     */
    public void cancel() {
        if (status.isFinal()) {
            throw new IllegalStateException("Cannot cancel a finalized order");
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 注文に明細を追加（ドメインロジック保護）
     */
    public void addItem(OrderItem item) {
        if (!status.equals(OrderStatus.PENDING)) {
            throw new IllegalStateException("Cannot add items to a non-pending order");
        }
        this.items.add(item);
        this.totalPrice = calculateTotal(items);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 注文の明細数を取得
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * 合計金額を計算（ドメイン計算）
     */
    private static Money calculateTotal(List<OrderItem> items) {
        Money total = Money.zero();
        for (OrderItem item : items) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
