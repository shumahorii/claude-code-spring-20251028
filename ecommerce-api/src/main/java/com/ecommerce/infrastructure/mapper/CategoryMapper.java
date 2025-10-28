package com.ecommerce.infrastructure.mapper;

import com.ecommerce.application.dto.CategoryDTO;
import com.ecommerce.domain.model.Category;
import org.springframework.stereotype.Component;

/**
 * CategoryMapper - ドメインモデルと DTO の相互変換
 */
@Component
public class CategoryMapper {
    /**
     * ドメインモデルを DTO に変換
     */
    public CategoryDTO toDTO(Category domain) {
        if (domain == null) {
            return null;
        }

        CategoryDTO dto = new CategoryDTO();
        dto.setId(domain.getId().getValue());
        dto.setName(domain.getName());
        dto.setDescription(domain.getDescription());
        return dto;
    }
}
