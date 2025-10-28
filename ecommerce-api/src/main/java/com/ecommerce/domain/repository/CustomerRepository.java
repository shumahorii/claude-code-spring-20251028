package com.ecommerce.domain.repository;

import com.ecommerce.domain.model.Customer;
import com.ecommerce.domain.model.CustomerId;

import java.util.List;
import java.util.Optional;

/**
 * CustomerRepository - ドメイン層のリポジトリインターフェース
 * JPA非依存
 * 実装はインフラ層で行われる
 */
public interface CustomerRepository {
    /**
     * IDで顧客を取得
     */
    Optional<Customer> findById(CustomerId id);

    /**
     * メールアドレスで顧客を取得
     */
    Optional<Customer> findByEmail(String email);

    /**
     * 電話番号で顧客を取得
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    /**
     * すべての顧客を取得
     */
    List<Customer> findAll();

    /**
     * 顧客を保存（新規作成と更新の両方に対応）
     */
    void save(Customer customer);

    /**
     * 顧客を削除
     */
    void delete(CustomerId id);

    /**
     * 指定されたIDが存在するかどうかをチェック
     */
    boolean exists(CustomerId id);
}
