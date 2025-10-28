package com.ecommerce.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CategoryDTO - Data Transfer Object for Category
 * アプリケーション層と Presentation層（Controller）間のデータ転送用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
}
