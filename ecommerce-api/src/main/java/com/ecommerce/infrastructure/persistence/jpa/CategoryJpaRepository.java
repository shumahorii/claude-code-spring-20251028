package com.ecommerce.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CategoryJpaRepository - Spring Data JPA リポジトリ
 * インフラストラクチャ層のリポジトリ実装の一部
 */
@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Long> {
    Optional<CategoryJpaEntity> findByName(String name);
}
