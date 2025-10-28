package com.ecommerce.presentation.controller;

import com.ecommerce.application.dto.CategoryDTO;
import com.ecommerce.application.service.CategoryApplicationService;
import com.ecommerce.infrastructure.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CategoryController - Presentation層
 * HTTP リクエスト/レスポンスの処理
 * ドメインロジックはアプリケーションサービスに委譲
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryApplicationService applicationService;
    private final CategoryMapper mapper;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(
            applicationService.getAllCategories().stream()
                .map(mapper::toDTO)
                .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return applicationService.getCategory(id)
            .map(mapper::toDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        applicationService.createCategory(categoryDTO.getName(), categoryDTO.getDescription());

        // 作成したカテゴリを取得して返却
        return applicationService.getCategoryByName(categoryDTO.getName())
            .map(mapper::toDTO)
            .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
            .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryDTO categoryDTO) {
        try {
            applicationService.updateCategory(id, categoryDTO.getName(), categoryDTO.getDescription());

            return applicationService.getCategory(id)
                .map(mapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            applicationService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
