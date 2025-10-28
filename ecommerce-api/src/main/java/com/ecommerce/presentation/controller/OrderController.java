package com.ecommerce.presentation.controller;

import com.ecommerce.application.dto.OrderDTO;
import com.ecommerce.application.service.OrderApplicationService;
import com.ecommerce.domain.model.OrderStatus;
import com.ecommerce.infrastructure.mapper.OrderMapper;
import com.ecommerce.infrastructure.persistence.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * OrderController - Presentation層
 * HTTP リクエスト/レスポンスの処理
 * ドメインロジックはアプリケーションサービスに委譲
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderApplicationService applicationService;
    private final OrderMapper mapper;
    private final OrderJpaRepository jpaRepository;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(
            jpaRepository.findAll().stream()
                .map(mapper::toDTOWithCustomer)
                .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return jpaRepository.findById(id)
            .map(mapper::toDTOWithCustomer)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(
            jpaRepository.findByCustomerId(customerId).stream()
                .map(mapper::toDTOWithCustomer)
                .toList()
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(@PathVariable String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(
                applicationService.getOrdersByStatus(orderStatus).stream()
                    .map(mapper::toDTO)
                    .toList()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @RequestParam Long customerId,
            @RequestBody List<CreateOrderItemRequest> items) {
        try {
            // OrderItemInput に変換
            List<OrderApplicationService.OrderItemInput> orderItems = items.stream()
                .map(item -> new OrderApplicationService.OrderItemInput(
                    item.getProductId(),
                    item.getQuantity()
                ))
                .toList();

            applicationService.createOrder(customerId, orderItems);

            // 作成した注文を取得して返却
            return applicationService.getOrdersByCustomer(customerId).stream()
                .max((o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()))
                .map(mapper::toDTO)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String statusStr = request.get("status");
            OrderStatus status = OrderStatus.fromString(statusStr);
            applicationService.updateOrderStatus(id, status);

            return jpaRepository.findById(id)
                .map(mapper::toDTOWithCustomer)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            applicationService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 注文作成時の入力DTO
     */
    public static class CreateOrderItemRequest {
        private Long productId;
        private Integer quantity;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
