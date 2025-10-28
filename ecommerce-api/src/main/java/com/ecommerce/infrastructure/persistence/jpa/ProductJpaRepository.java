package com.ecommerce.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ProductJpaRepository - Spring Data JPA リポジトリ
 */
@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {
    Optional<ProductJpaEntity> findByName(String name);
    List<ProductJpaEntity> findByCategoryId(Long categoryId);
}
