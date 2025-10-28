package com.ecommerce.infrastructure.mapper;

import com.ecommerce.application.dto.CustomerDTO;
import com.ecommerce.domain.model.Customer;
import org.springframework.stereotype.Component;

/**
 * CustomerMapper - ドメインモデルと DTO の相互変換
 */
@Component
public class CustomerMapper {
    /**
     * ドメインモデルを DTO に変換
     */
    public CustomerDTO toDTO(Customer domain) {
        if (domain == null) {
            return null;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setId(domain.getId().getValue());
        dto.setFirstName(domain.getFirstName());
        dto.setLastName(domain.getLastName());
        dto.setEmail(domain.getEmail());
        dto.setPhoneNumber(domain.getPhoneNumber());
        dto.setAddress(domain.getAddress());
        dto.setCity(domain.getCity());
        dto.setState(domain.getState());
        dto.setZipCode(domain.getZipCode());
        return dto;
    }
}
