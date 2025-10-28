package com.ecommerce.application.service;

import com.ecommerce.domain.model.CategoryId;
import com.ecommerce.domain.model.Money;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.ProductId;
import com.ecommerce.domain.repository.CategoryRepository;
import com.ecommerce.domain.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * ProductApplicationService - アプリケーションサービス
 * 商品に関するユースケースを集約
 */
public class ProductApplicationService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductApplicationService(ProductRepository productRepository,
                                    CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * 商品を作成
     */
    public void createProduct(String name, String description, BigDecimal price,
                             Integer stock, Long categoryId) {
        // カテゴリが存在するかチェック
        CategoryId cId = new CategoryId(categoryId);
        if (!categoryRepository.exists(cId)) {
            throw new IllegalArgumentException("Category not found with id: " + categoryId);
        }

        // 同じ名前の商品が既に存在するかチェック
        if (productRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Product with name '" + name + "' already exists");
        }

        // ドメインモデルで商品を作成
        Money money = new Money(price);
        Product product = Product.create(name, description, money, stock, cId);

        // リポジトリに保存
        productRepository.save(product);
    }

    /**
     * IDで商品を取得
     */
    public Optional<Product> getProduct(Long id) {
        return productRepository.findById(new ProductId(id));
    }

    /**
     * 名前で商品を取得
     */
    public Optional<Product> getProductByName(String name) {
        return productRepository.findByName(name);
    }

    /**
     * カテゴリIDで商品を検索
     */
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(new CategoryId(categoryId));
    }

    /**
     * すべての商品を取得
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 商品を更新
     */
    public void updateProduct(Long id, String name, String description,
                             BigDecimal price) {
        ProductId productId = new ProductId(id);

        // 既存の商品を取得
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        // 名前が変更される場合、同じ名前の他の商品が存在しないかチェック
        if (name != null && !product.getName().equals(name)) {
            if (productRepository.findByName(name).isPresent()) {
                throw new IllegalArgumentException("Product with name '" + name + "' already exists");
            }
        }

        // ドメインモデルを更新
        Money money = price != null ? new Money(price) : null;
        product.updateInfo(name, description, money);

        // リポジトリに保存
        productRepository.save(product);
    }

    /**
     * 在庫を増やす
     */
    public void increaseStock(Long id, Integer quantity) {
        ProductId productId = new ProductId(id);

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        product.increaseStock(quantity);
        productRepository.save(product);
    }

    /**
     * 在庫を減らす
     */
    public void decreaseStock(Long id, Integer quantity) {
        ProductId productId = new ProductId(id);

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        product.decreaseStock(quantity);
        productRepository.save(product);
    }

    /**
     * 商品を削除
     */
    public void deleteProduct(Long id) {
        ProductId productId = new ProductId(id);

        if (!productRepository.exists(productId)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }

        productRepository.delete(productId);
    }
}
