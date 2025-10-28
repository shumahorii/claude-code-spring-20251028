package com.ecommerce.application.service;

import com.ecommerce.domain.model.CustomerId;
import com.ecommerce.domain.model.Money;
import com.ecommerce.domain.model.Order;
import com.ecommerce.domain.model.OrderId;
import com.ecommerce.domain.model.OrderItem;
import com.ecommerce.domain.model.OrderStatus;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.ProductId;
import com.ecommerce.domain.repository.CustomerRepository;
import com.ecommerce.domain.repository.OrderRepository;
import com.ecommerce.domain.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * OrderApplicationService - アプリケーションサービス
 * 注文に関するユースケースを集約
 */
public class OrderApplicationService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public OrderApplicationService(OrderRepository orderRepository,
                                  ProductRepository productRepository,
                                  CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * 注文を作成
     */
    public void createOrder(Long customerId, List<OrderItemInput> items) {
        CustomerId cId = new CustomerId(customerId);

        // 顧客が存在するかチェック
        if (!customerRepository.exists(cId)) {
            throw new IllegalArgumentException("Customer not found with id: " + customerId);
        }

        // 注文明細を作成
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemInput item : items) {
            ProductId pId = new ProductId(item.productId);

            // 商品が存在するかチェック
            Product product = productRepository.findById(pId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + item.productId));

            // 在庫が十分かチェック
            if (!product.hasEnoughStock(item.quantity)) {
                throw new IllegalStateException(
                    String.format("Insufficient stock for product '%s'. Available: %d, Requested: %d",
                        product.getName(), product.getStock(), item.quantity)
                );
            }

            // 注文明細を作成（商品の価格を使用）
            OrderItem orderItem = OrderItem.create(pId, item.quantity, product.getPrice());
            orderItems.add(orderItem);
        }

        // 注文を作成
        Order order = Order.create(cId, orderItems);

        // 商品の在庫を減らす
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem orderItem = orderItems.get(i);
            ProductId pId = orderItem.getProductId();
            Product product = productRepository.findById(pId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            product.decreaseStock(orderItem.getQuantity());
            productRepository.save(product);
        }

        // リポジトリに保存
        orderRepository.save(order);
    }

    /**
     * IDで注文を取得
     */
    public Optional<Order> getOrder(Long id) {
        return orderRepository.findById(new OrderId(id));
    }

    /**
     * 顧客IDで注文を検索
     */
    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(new CustomerId(customerId));
    }

    /**
     * ステータスで注文を検索
     */
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * すべての注文を取得
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * 注文のステータスを更新
     */
    public void updateOrderStatus(Long id, OrderStatus newStatus) {
        OrderId orderId = new OrderId(id);

        // 既存の注文を取得
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));

        // ドメインモデルでステータスを更新
        order.updateStatus(newStatus);

        // リポジトリに保存
        orderRepository.save(order);
    }

    /**
     * 注文をキャンセル
     */
    public void cancelOrder(Long id) {
        OrderId orderId = new OrderId(id);

        // 既存の注文を取得
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));

        // 注文をキャンセル
        order.cancel();

        // 商品の在庫を戻す
        for (OrderItem item : order.getItems()) {
            ProductId pId = item.getProductId();
            Product product = productRepository.findById(pId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            product.increaseStock(item.getQuantity());
            productRepository.save(product);
        }

        // リポジトリに保存
        orderRepository.save(order);
    }

    /**
     * 注文を削除
     */
    public void deleteOrder(Long id) {
        OrderId orderId = new OrderId(id);

        if (!orderRepository.exists(orderId)) {
            throw new IllegalArgumentException("Order not found with id: " + id);
        }

        orderRepository.delete(orderId);
    }

    /**
     * 注文明細の入力DTO
     */
    public static class OrderItemInput {
        public final Long productId;
        public final Integer quantity;

        public OrderItemInput(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }
}
