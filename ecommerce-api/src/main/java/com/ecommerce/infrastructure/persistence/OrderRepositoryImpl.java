package com.ecommerce.infrastructure.persistence;

import com.ecommerce.domain.model.CustomerId;
import com.ecommerce.domain.model.Money;
import com.ecommerce.domain.model.Order;
import com.ecommerce.domain.model.OrderId;
import com.ecommerce.domain.model.OrderItem;
import com.ecommerce.domain.model.OrderStatus;
import com.ecommerce.domain.model.ProductId;
import com.ecommerce.domain.repository.OrderRepository;
import com.ecommerce.infrastructure.persistence.jpa.OrderJpaEntity;
import com.ecommerce.infrastructure.persistence.jpa.OrderItemJpaEntity;
import com.ecommerce.infrastructure.persistence.jpa.OrderJpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * OrderRepositoryImpl - ドメイン層のリポジトリインターフェースの実装
 */
@Component
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository jpaRepository;

    public OrderRepositoryImpl(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(id.getValue())
            .map(this::toDomain);
    }

    @Override
    public List<Order> findByCustomerId(CustomerId customerId) {
        return jpaRepository.findByCustomerId(customerId.getValue()).stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        OrderJpaEntity.OrderStatusJpa jpaStatus = toJpaStatus(status);
        return jpaRepository.findByStatus(jpaStatus).stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll().stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public void save(Order order) {
        OrderJpaEntity entity = toJpa(order);
        jpaRepository.save(entity);
    }

    @Override
    public void delete(OrderId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public boolean exists(OrderId id) {
        return jpaRepository.existsById(id.getValue());
    }

    /**
     * JPA エンティティからドメインモデルへの変換
     */
    private Order toDomain(OrderJpaEntity entity) {
        CustomerId customerId = new CustomerId(entity.getCustomer().getId());
        OrderStatus status = toDomainStatus(entity.getStatus());
        Money totalPrice = new Money(entity.getTotalPrice());

        List<OrderItem> items = new ArrayList<>();
        for (OrderItemJpaEntity itemEntity : entity.getItems()) {
            ProductId productId = new ProductId(itemEntity.getProduct().getId());
            Money priceAtPurchase = new Money(itemEntity.getPriceAtPurchase());
            OrderItem item = OrderItem.restore(
                itemEntity.getId(),
                productId,
                itemEntity.getQuantity(),
                priceAtPurchase,
                itemEntity.getCreatedAt()
            );
            items.add(item);
        }

        return Order.restore(
            entity.getId(),
            customerId,
            status,
            totalPrice,
            items,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * ドメインモデルから JPA エンティティへの変換
     */
    private OrderJpaEntity toJpa(Order domain) {
        OrderJpaEntity entity = new OrderJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setStatus(toJpaStatus(domain.getStatus()));
        entity.setTotalPrice(domain.getTotalPrice().getAmount());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    /**
     * ドメイン層の OrderStatus を JPA 層の OrderStatusJpa に変換
     */
    private OrderJpaEntity.OrderStatusJpa toJpaStatus(OrderStatus status) {
        return switch (status) {
            case PENDING -> OrderJpaEntity.OrderStatusJpa.PENDING;
            case CONFIRMED -> OrderJpaEntity.OrderStatusJpa.CONFIRMED;
            case SHIPPED -> OrderJpaEntity.OrderStatusJpa.SHIPPED;
            case DELIVERED -> OrderJpaEntity.OrderStatusJpa.DELIVERED;
            case CANCELLED -> OrderJpaEntity.OrderStatusJpa.CANCELLED;
        };
    }

    /**
     * JPA 層の OrderStatusJpa をドメイン層の OrderStatus に変換
     */
    private OrderStatus toDomainStatus(OrderJpaEntity.OrderStatusJpa status) {
        return switch (status) {
            case PENDING -> OrderStatus.PENDING;
            case CONFIRMED -> OrderStatus.CONFIRMED;
            case SHIPPED -> OrderStatus.SHIPPED;
            case DELIVERED -> OrderStatus.DELIVERED;
            case CANCELLED -> OrderStatus.CANCELLED;
        };
    }
}
