package com.ecommerce.domain.repository;

import com.ecommerce.domain.model.CategoryId;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.ProductId;

import java.util.List;
import java.util.Optional;

/**
 * ProductRepository - ドメイン層のリポジトリインターフェース
 * JPA非依存
 * 実装はインフラ層で行われる
 */
public interface ProductRepository {
    /**
     * IDで商品を取得
     */
    Optional<Product> findById(ProductId id);

    /**
     * 名前で商品を取得
     */
    Optional<Product> findByName(String name);

    /**
     * カテゴリIDで商品を検索
     */
    List<Product> findByCategoryId(CategoryId categoryId);

    /**
     * すべての商品を取得
     */
    List<Product> findAll();

    /**
     * 商品を保存（新規作成と更新の両方に対応）
     */
    void save(Product product);

    /**
     * 商品を削除
     */
    void delete(ProductId id);

    /**
     * 指定されたIDが存在するかどうかをチェック
     */
    boolean exists(ProductId id);
}
