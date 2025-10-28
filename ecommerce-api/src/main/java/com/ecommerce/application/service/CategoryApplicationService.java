package com.ecommerce.application.service;

import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.CategoryId;
import com.ecommerce.domain.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

/**
 * CategoryApplicationService - アプリケーションサービス
 * カテゴリに関するユースケースを集約
 */
public class CategoryApplicationService {
    private final CategoryRepository categoryRepository;

    public CategoryApplicationService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * カテゴリを作成
     */
    public void createCategory(String name, String description) {
        // 同じ名前のカテゴリが既に存在するかチェック
        if (categoryRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Category with name '" + name + "' already exists");
        }

        // ドメインモデルでカテゴリを作成
        Category category = Category.create(name, description);

        // リポジトリに保存
        categoryRepository.save(category);
    }

    /**
     * IDでカテゴリを取得
     */
    public Optional<Category> getCategory(Long id) {
        return categoryRepository.findById(new CategoryId(id));
    }

    /**
     * 名前でカテゴリを取得
     */
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    /**
     * すべてのカテゴリを取得
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * カテゴリを更新
     */
    public void updateCategory(Long id, String name, String description) {
        CategoryId categoryId = new CategoryId(id);

        // 既存のカテゴリを取得
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        // 名前が変更される場合、同じ名前の他のカテゴリが存在しないかチェック
        if (!category.getName().equals(name)) {
            if (categoryRepository.findByName(name).isPresent()) {
                throw new IllegalArgumentException("Category with name '" + name + "' already exists");
            }
        }

        // ドメインモデルを更新
        category.updateName(name);
        category.updateDescription(description);

        // リポジトリに保存
        categoryRepository.save(category);
    }

    /**
     * カテゴリを削除
     */
    public void deleteCategory(Long id) {
        CategoryId categoryId = new CategoryId(id);

        if (!categoryRepository.exists(categoryId)) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }

        categoryRepository.delete(categoryId);
    }
}
