package com.ecommerce.infrastructure.persistence;

import com.ecommerce.domain.model.Customer;
import com.ecommerce.domain.model.CustomerId;
import com.ecommerce.domain.repository.CustomerRepository;
import com.ecommerce.infrastructure.persistence.jpa.CustomerJpaEntity;
import com.ecommerce.infrastructure.persistence.jpa.CustomerJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * CustomerRepositoryImpl - ドメイン層のリポジトリインターフェースの実装
 */
@Component
public class CustomerRepositoryImpl implements CustomerRepository {
    private final CustomerJpaRepository jpaRepository;

    public CustomerRepositoryImpl(CustomerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        return jpaRepository.findById(id.getValue())
            .map(this::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
            .map(this::toDomain);
    }

    @Override
    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        return jpaRepository.findByPhoneNumber(phoneNumber)
            .map(this::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return jpaRepository.findAll().stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public void save(Customer customer) {
        CustomerJpaEntity entity = toJpa(customer);
        jpaRepository.save(entity);
    }

    @Override
    public void delete(CustomerId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public boolean exists(CustomerId id) {
        return jpaRepository.existsById(id.getValue());
    }

    /**
     * JPA エンティティからドメインモデルへの変換
     */
    private Customer toDomain(CustomerJpaEntity entity) {
        return Customer.restore(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getEmail(),
            entity.getPhoneNumber(),
            entity.getAddress(),
            entity.getCity(),
            entity.getState(),
            entity.getZipCode(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * ドメインモデルから JPA エンティティへの変換
     */
    private CustomerJpaEntity toJpa(Customer domain) {
        CustomerJpaEntity entity = new CustomerJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setPhoneNumber(domain.getPhoneNumber());
        entity.setAddress(domain.getAddress());
        entity.setCity(domain.getCity());
        entity.setState(domain.getState());
        entity.setZipCode(domain.getZipCode());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
