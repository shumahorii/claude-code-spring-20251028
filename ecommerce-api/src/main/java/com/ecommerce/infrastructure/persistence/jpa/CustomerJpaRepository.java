package com.ecommerce.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CustomerJpaRepository - Spring Data JPA リポジトリ
 */
@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, Long> {
    Optional<CustomerJpaEntity> findByEmail(String email);
    Optional<CustomerJpaEntity> findByPhoneNumber(String phoneNumber);
}
