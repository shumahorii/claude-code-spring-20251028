package com.ecommerce.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Customer - エンティティ（集約ルート）
 * ドメイン層の顧客エンティティ
 * JPA依存なし
 */
public class Customer {
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private CustomerId id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * プライベートコンストラクタ
     */
    private Customer() {
    }

    /**
     * 新規顧客の作成
     */
    public static Customer create(String firstName, String lastName, String email,
                                  String phoneNumber, String address, String city,
                                  String state, String zipCode) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (email == null || !isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email address");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }

        Customer customer = new Customer();
        customer.firstName = firstName;
        customer.lastName = lastName;
        customer.email = email;
        customer.phoneNumber = phoneNumber;
        customer.address = address;
        customer.city = city;
        customer.state = state;
        customer.zipCode = zipCode;
        customer.createdAt = LocalDateTime.now();
        customer.updatedAt = LocalDateTime.now();
        return customer;
    }

    /**
     * 既存顧客の復元
     */
    public static Customer restore(Long id, String firstName, String lastName,
                                   String email, String phoneNumber, String address,
                                   String city, String state, String zipCode,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        Customer customer = new Customer();
        customer.id = new CustomerId(id);
        customer.firstName = firstName;
        customer.lastName = lastName;
        customer.email = email;
        customer.phoneNumber = phoneNumber;
        customer.address = address;
        customer.city = city;
        customer.state = state;
        customer.zipCode = zipCode;
        customer.createdAt = createdAt;
        customer.updatedAt = updatedAt;
        return customer;
    }

    // ゲッター
    public CustomerId getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 顧客情報を更新
     */
    public void updateInfo(String firstName, String lastName, String phoneNumber,
                          String address, String city, String state, String zipCode) {
        if (firstName != null && !firstName.trim().isEmpty()) {
            this.firstName = firstName;
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            this.lastName = lastName;
        }
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            this.phoneNumber = phoneNumber;
        }
        if (address != null) {
            this.address = address;
        }
        if (city != null) {
            this.city = city;
        }
        if (state != null) {
            this.state = state;
        }
        if (zipCode != null) {
            this.zipCode = zipCode;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * メールアドレスを更新（検証付き）
     */
    public void updateEmail(String newEmail) {
        if (newEmail == null || !isValidEmail(newEmail)) {
            throw new IllegalArgumentException("Invalid email address");
        }
        this.email = newEmail;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * メールアドレスの検証
     */
    private static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 顧客の完全名を取得
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
