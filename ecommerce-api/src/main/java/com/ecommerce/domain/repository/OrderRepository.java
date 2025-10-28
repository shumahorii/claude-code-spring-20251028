package com.ecommerce.domain.repository;

import com.ecommerce.domain.model.CustomerId;
import com.ecommerce.domain.model.Order;
import com.ecommerce.domain.model.OrderId;
import com.ecommerce.domain.model.OrderStatus;

import java.util.List;
import java.util.Optional;

/**
 * OrderRepository - ドメイン層のリポジトリインターフェース
 * JPA非依存
 * 実装はインフラ層で行われる
 */
public interface OrderRepository {
    /**
     * IDで注文を取得
     */
    Optional<Order> findById(OrderId id);

    /**
     * 顧客IDで注文を検索
     */
    List<Order> findByCustomerId(CustomerId customerId);

    /**
     * ステータスで注文を検索
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * すべての注文を取得
     */
    List<Order> findAll();

    /**
     * 注文を保存（新規作成と更新の両方に対応）
     */
    void save(Order order);

    /**
     * 注文を削除
     */
    void delete(OrderId id);

    /**
     * 指定されたIDが存在するかどうかをチェック
     */
    boolean exists(OrderId id);
}
