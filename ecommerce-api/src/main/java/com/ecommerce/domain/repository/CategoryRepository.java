package com.ecommerce.domain.repository;

import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.CategoryId;

import java.util.List;
import java.util.Optional;

/**
 * CategoryRepository - ドメイン層のリポジトリインターフェース
 * JPA非依存
 * 実装はインフラ層で行われる
 */
public interface CategoryRepository {
    /**
     * IDでカテゴリを取得
     */
    Optional<Category> findById(CategoryId id);

    /**
     * 名前でカテゴリを取得
     */
    Optional<Category> findByName(String name);

    /**
     * すべてのカテゴリを取得
     */
    List<Category> findAll();

    /**
     * カテゴリを保存（新規作成と更新の両方に対応）
     */
    void save(Category category);

    /**
     * カテゴリを削除
     */
    void delete(CategoryId id);

    /**
     * 指定されたIDが存在するかどうかをチェック
     */
    boolean exists(CategoryId id);
}
