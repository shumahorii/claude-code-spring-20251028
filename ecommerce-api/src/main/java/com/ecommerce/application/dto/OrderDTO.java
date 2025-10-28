package com.ecommerce.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderDTO - Data Transfer Object for Order
 * アプリケーション層と Presentation層（Controller）間のデータ転送用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private String status;
    private BigDecimal totalPrice;
    private List<OrderItemDTO> items = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
