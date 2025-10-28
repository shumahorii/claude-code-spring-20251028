package com.ecommerce.presentation.controller;

import com.ecommerce.application.dto.ProductDTO;
import com.ecommerce.application.service.ProductApplicationService;
import com.ecommerce.infrastructure.mapper.ProductMapper;
import com.ecommerce.infrastructure.persistence.jpa.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProductController - Presentation層
 * HTTP リクエスト/レスポンスの処理
 * ドメインロジックはアプリケーションサービスに委譲
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductApplicationService applicationService;
    private final ProductMapper mapper;
    private final ProductJpaRepository jpaRepository;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(
            jpaRepository.findAll().stream()
                .map(mapper::toDTOWithCategory)
                .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return jpaRepository.findById(id)
            .map(mapper::toDTOWithCategory)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(
            jpaRepository.findByCategoryId(categoryId).stream()
                .map(mapper::toDTOWithCategory)
                .toList()
        );
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        try {
            applicationService.createProduct(
                productDTO.getName(),
                productDTO.getDescription(),
                productDTO.getPrice(),
                productDTO.getStock(),
                productDTO.getCategoryId()
            );

            // 作成した商品を取得して返却
            return applicationService.getProductByName(productDTO.getName())
                .map(mapper::toDTO)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO productDTO) {
        try {
            applicationService.updateProduct(id, productDTO.getName(),
                productDTO.getDescription(), productDTO.getPrice());

            return jpaRepository.findById(id)
                .map(mapper::toDTOWithCategory)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            applicationService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
