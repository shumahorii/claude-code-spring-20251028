package com.ecommerce.infrastructure.persistence;

import com.ecommerce.domain.model.CategoryId;
import com.ecommerce.domain.model.Money;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.ProductId;
import com.ecommerce.domain.repository.ProductRepository;
import com.ecommerce.infrastructure.persistence.jpa.ProductJpaEntity;
import com.ecommerce.infrastructure.persistence.jpa.ProductJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * ProductRepositoryImpl - ドメイン層のリポジトリインターフェースの実装
 */
@Component
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository jpaRepository;

    public ProductRepositoryImpl(ProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        return jpaRepository.findById(id.getValue())
            .map(this::toDomain);
    }

    @Override
    public Optional<Product> findByName(String name) {
        return jpaRepository.findByName(name)
            .map(this::toDomain);
    }

    @Override
    public List<Product> findByCategoryId(CategoryId categoryId) {
        return jpaRepository.findByCategoryId(categoryId.getValue()).stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public void save(Product product) {
        ProductJpaEntity entity = toJpa(product);
        jpaRepository.save(entity);
    }

    @Override
    public void delete(ProductId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public boolean exists(ProductId id) {
        return jpaRepository.existsById(id.getValue());
    }

    /**
     * JPA エンティティからドメインモデルへの変換
     */
    private Product toDomain(ProductJpaEntity entity) {
        return Product.restore(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            new Money(entity.getPrice()),
            entity.getStock(),
            new CategoryId(entity.getCategory().getId()),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * ドメインモデルから JPA エンティティへの変換
     */
    private ProductJpaEntity toJpa(Product domain) {
        ProductJpaEntity entity = new ProductJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPrice(domain.getPrice().getAmount());
        entity.setStock(domain.getStock());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
