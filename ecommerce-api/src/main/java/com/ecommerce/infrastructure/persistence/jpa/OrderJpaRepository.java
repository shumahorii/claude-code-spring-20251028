package com.ecommerce.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OrderJpaRepository - Spring Data JPA リポジトリ
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {
    List<OrderJpaEntity> findByCustomerId(Long customerId);
    List<OrderJpaEntity> findByStatus(OrderJpaEntity.OrderStatusJpa status);
}
