package com.ecommerce.infrastructure.mapper;

import com.ecommerce.application.dto.ProductDTO;
import com.ecommerce.domain.model.Product;
import com.ecommerce.infrastructure.persistence.jpa.ProductJpaEntity;
import org.springframework.stereotype.Component;

/**
 * ProductMapper - ドメインモデルと DTO の相互変換
 */
@Component
public class ProductMapper {
    /**
     * ドメインモデルを DTO に変換
     */
    public ProductDTO toDTO(Product domain) {
        if (domain == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(domain.getId().getValue());
        dto.setName(domain.getName());
        dto.setDescription(domain.getDescription());
        dto.setPrice(domain.getPrice().getAmount());
        dto.setStock(domain.getStock());
        dto.setCategoryId(domain.getCategoryId().getValue());
        return dto;
    }

    /**
     * JPA エンティティを DTO に変換（カテゴリ名を含む）
     */
    public ProductDTO toDTOWithCategory(ProductJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(jpaEntity.getId());
        dto.setName(jpaEntity.getName());
        dto.setDescription(jpaEntity.getDescription());
        dto.setPrice(jpaEntity.getPrice());
        dto.setStock(jpaEntity.getStock());
        dto.setCategoryId(jpaEntity.getCategory().getId());
        dto.setCategoryName(jpaEntity.getCategory().getName());
        return dto;
    }
}
