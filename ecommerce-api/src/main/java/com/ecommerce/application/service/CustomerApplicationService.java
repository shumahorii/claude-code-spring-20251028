package com.ecommerce.application.service;

import com.ecommerce.domain.model.Customer;
import com.ecommerce.domain.model.CustomerId;
import com.ecommerce.domain.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

/**
 * CustomerApplicationService - アプリケーションサービス
 * 顧客に関するユースケースを集約
 */
public class CustomerApplicationService {
    private final CustomerRepository customerRepository;

    public CustomerApplicationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * 顧客を作成
     */
    public void createCustomer(String firstName, String lastName, String email,
                              String phoneNumber, String address, String city,
                              String state, String zipCode) {
        // メールアドレスが既に使用されていないかチェック
        if (customerRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email '" + email + "' is already in use");
        }

        // 電話番号が既に使用されていないかチェック
        if (customerRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new IllegalArgumentException("Phone number '" + phoneNumber + "' is already in use");
        }

        // ドメインモデルで顧客を作成
        Customer customer = Customer.create(firstName, lastName, email, phoneNumber,
            address, city, state, zipCode);

        // リポジトリに保存
        customerRepository.save(customer);
    }

    /**
     * IDで顧客を取得
     */
    public Optional<Customer> getCustomer(Long id) {
        return customerRepository.findById(new CustomerId(id));
    }

    /**
     * メールアドレスで顧客を取得
     */
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    /**
     * 電話番号で顧客を取得
     */
    public Optional<Customer> getCustomerByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber);
    }

    /**
     * すべての顧客を取得
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * 顧客を更新
     */
    public void updateCustomer(Long id, String firstName, String lastName,
                              String phoneNumber, String address, String city,
                              String state, String zipCode) {
        CustomerId customerId = new CustomerId(id);

        // 既存の顧客を取得
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));

        // 電話番号が変更される場合、他の顧客が同じ番号を持たないかチェック
        if (phoneNumber != null && !customer.getPhoneNumber().equals(phoneNumber)) {
            if (customerRepository.findByPhoneNumber(phoneNumber).isPresent()) {
                throw new IllegalArgumentException("Phone number '" + phoneNumber + "' is already in use");
            }
        }

        // ドメインモデルを更新
        customer.updateInfo(firstName, lastName, phoneNumber, address, city, state, zipCode);

        // リポジトリに保存
        customerRepository.save(customer);
    }

    /**
     * メールアドレスを更新
     */
    public void updateEmail(Long id, String newEmail) {
        CustomerId customerId = new CustomerId(id);

        // 既存の顧客を取得
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));

        // 新しいメールアドレスが他の顧客に使用されていないかチェック
        if (!customer.getEmail().equals(newEmail)) {
            if (customerRepository.findByEmail(newEmail).isPresent()) {
                throw new IllegalArgumentException("Email '" + newEmail + "' is already in use");
            }
        }

        // ドメインモデルを更新
        customer.updateEmail(newEmail);

        // リポジトリに保存
        customerRepository.save(customer);
    }

    /**
     * 顧客を削除
     */
    public void deleteCustomer(Long id) {
        CustomerId customerId = new CustomerId(id);

        if (!customerRepository.exists(customerId)) {
            throw new IllegalArgumentException("Customer not found with id: " + id);
        }

        customerRepository.delete(customerId);
    }
}
