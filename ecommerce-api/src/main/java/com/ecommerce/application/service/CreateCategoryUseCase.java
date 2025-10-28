package com.ecommerce.application.service;

import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.repository.CategoryRepository;

/**
 * CreateCategoryUseCase - ユースケース
 * カテゴリ作成のビジネスロジック
 */
public class CreateCategoryUseCase {
    private final CategoryRepository categoryRepository;

    public CreateCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * カテゴリを作成
     */
    public void execute(String name, String description) {
        // 同じ名前のカテゴリが既に存在するかチェック
        if (categoryRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Category with name '" + name + "' already exists");
        }

        // ドメインモデルでカテゴリを作成
        Category category = Category.create(name, description);

        // リポジトリに保存
        categoryRepository.save(category);
    }
}
