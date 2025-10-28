package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    List<Product> findByCategory(Category category);
    List<Product> findByCategoryId(Long categoryId);
}
