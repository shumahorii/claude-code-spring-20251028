package com.ecommerce.infrastructure.persistence;

import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.CategoryId;
import com.ecommerce.domain.repository.CategoryRepository;
import com.ecommerce.infrastructure.persistence.jpa.CategoryJpaEntity;
import com.ecommerce.infrastructure.persistence.jpa.CategoryJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * CategoryRepositoryImpl - ドメイン層のリポジトリインターフェースの実装
 * JPA（インフラ層）とドメイン層を仲介するアダプター
 */
@Component
public class CategoryRepositoryImpl implements CategoryRepository {
    private final CategoryJpaRepository jpaRepository;

    public CategoryRepositoryImpl(CategoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Category> findById(CategoryId id) {
        return jpaRepository.findById(id.getValue())
            .map(this::toDomain);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return jpaRepository.findByName(name)
            .map(this::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll().stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public void save(Category category) {
        CategoryJpaEntity entity = toJpa(category);
        jpaRepository.save(entity);
    }

    @Override
    public void delete(CategoryId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public boolean exists(CategoryId id) {
        return jpaRepository.existsById(id.getValue());
    }

    /**
     * JPA エンティティからドメインモデルへの変換
     */
    private Category toDomain(CategoryJpaEntity entity) {
        return Category.restore(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * ドメインモデルから JPA エンティティへの変換
     */
    private CategoryJpaEntity toJpa(Category domain) {
        CategoryJpaEntity entity = new CategoryJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
