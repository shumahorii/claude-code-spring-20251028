package com.ecommerce.infrastructure.mapper;

import com.ecommerce.application.dto.OrderDTO;
import com.ecommerce.application.dto.OrderItemDTO;
import com.ecommerce.domain.model.Order;
import com.ecommerce.domain.model.OrderItem;
import com.ecommerce.infrastructure.persistence.jpa.OrderJpaEntity;
import com.ecommerce.infrastructure.persistence.jpa.OrderItemJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * OrderMapper - ドメインモデルと DTO の相互変換
 */
@Component
public class OrderMapper {
    /**
     * ドメインモデルを DTO に変換
     */
    public OrderDTO toDTO(Order domain) {
        if (domain == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(domain.getId().getValue());
        dto.setCustomerId(domain.getCustomerId().getValue());
        dto.setStatus(domain.getStatus().name());
        dto.setTotalPrice(domain.getTotalPrice().getAmount());
        dto.setCreatedAt(domain.getCreatedAt());
        dto.setUpdatedAt(domain.getUpdatedAt());

        List<OrderItemDTO> itemDTOs = new ArrayList<>();
        for (OrderItem item : domain.getItems()) {
            OrderItemDTO itemDTO = new OrderItemDTO();
            if (item.getId() != null) {
                itemDTO.setId(item.getId());
            }
            itemDTO.setProductId(item.getProductId().getValue());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPriceAtPurchase(item.getPriceAtPurchase().getAmount());
            itemDTO.setSubtotal(item.getSubtotal().getAmount());
            itemDTOs.add(itemDTO);
        }
        dto.setItems(itemDTOs);

        return dto;
    }

    /**
     * JPA エンティティを DTO に変換（顧客名を含む）
     */
    public OrderDTO toDTOWithCustomer(OrderJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(jpaEntity.getId());
        dto.setCustomerId(jpaEntity.getCustomer().getId());
        dto.setCustomerName(jpaEntity.getCustomer().getFirstName() + " " + jpaEntity.getCustomer().getLastName());
        dto.setStatus(jpaEntity.getStatus().name());
        dto.setTotalPrice(jpaEntity.getTotalPrice());
        dto.setCreatedAt(jpaEntity.getCreatedAt());
        dto.setUpdatedAt(jpaEntity.getUpdatedAt());

        List<OrderItemDTO> itemDTOs = new ArrayList<>();
        for (OrderItemJpaEntity itemEntity : jpaEntity.getItems()) {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setId(itemEntity.getId());
            itemDTO.setProductId(itemEntity.getProduct().getId());
            itemDTO.setProductName(itemEntity.getProduct().getName());
            itemDTO.setQuantity(itemEntity.getQuantity());
            itemDTO.setPriceAtPurchase(itemEntity.getPriceAtPurchase());
            itemDTO.setSubtotal(itemEntity.getPriceAtPurchase().multiply(java.math.BigDecimal.valueOf(itemEntity.getQuantity())));
            itemDTOs.add(itemDTO);
        }
        dto.setItems(itemDTOs);

        return dto;
    }
}
