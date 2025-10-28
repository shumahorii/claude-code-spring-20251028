# E-Commerce API - クリーンアーキテクチャ & DDD 実装

## 概要

このプロジェクトは、**クリーンアーキテクチャ**と**ドメイン駆動設計（DDD）**の原則に基づいて構築された Spring Boot の e-コマース REST API です。

JPA（Hibernate）の技術依存をインフラ層に完全に隔離し、ドメインモデルを純粋なビジネスロジックの実装に専念させています。

## アーキテクチャの層構成

```
com.ecommerce/
├── domain/                          [ドメイン層]
│   ├── model/                       JPA 非依存のドメインモデル
│   │   ├── *Id.java                 (CategoryId, ProductId, CustomerId, OrderId)
│   │   ├── Category.java            集約ルート
│   │   ├── Product.java             集約ルート（在庫管理ロジック含む）
│   │   ├── Customer.java            集約ルート（顧客検証ロジック含む）
│   │   ├── Order.java               集約ルート（注文ステータス遷移含む）
│   │   ├── OrderItem.java           エンティティ
│   │   ├── Money.java               値オブジェクト（金額計算）
│   │   └── OrderStatus.java         値オブジェクト（ステータス管理）
│   └── repository/                  リポジトリインターフェース（実装なし）
│       ├── CategoryRepository.java
│       ├── ProductRepository.java
│       ├── CustomerRepository.java
│       └── OrderRepository.java
│
├── application/                     [ユースケース層]
│   ├── service/                     ビジネスロジック
│   │   ├── CategoryApplicationService.java
│   │   ├── ProductApplicationService.java
│   │   ├── CustomerApplicationService.java
│   │   ├── OrderApplicationService.java
│   │   └── CreateCategoryUseCase.java
│   └── dto/                         DTO（Presentation層とのデータ転送）
│       ├── CategoryDTO.java
│       ├── ProductDTO.java
│       ├── CustomerDTO.java
│       ├── OrderDTO.java
│       └── OrderItemDTO.java
│
├── infrastructure/                  [インフラ層]
│   ├── persistence/                 データ永続化実装
│   │   ├── jpa/                     JPA エンティティ（DB マッピング専用）
│   │   │   ├── CategoryJpaEntity.java
│   │   │   ├── ProductJpaEntity.java
│   │   │   ├── CustomerJpaEntity.java
│   │   │   ├── OrderJpaEntity.java
│   │   │   ├── OrderItemJpaEntity.java
│   │   │   ├── CategoryJpaRepository.java
│   │   │   ├── ProductJpaRepository.java
│   │   │   ├── CustomerJpaRepository.java
│   │   │   └── OrderJpaRepository.java
│   │   ├── CategoryRepositoryImpl.java      実装（変換含む）
│   │   ├── ProductRepositoryImpl.java
│   │   ├── CustomerRepositoryImpl.java
│   │   └── OrderRepositoryImpl.java
│   └── mapper/                      ドメインモデル ← → DTO 変換
│       ├── CategoryMapper.java
│       ├── ProductMapper.java
│       ├── CustomerMapper.java
│       └── OrderMapper.java
│
└── presentation/                    [プレゼンテーション層]
    └── controller/                  REST API エンドポイント
        ├── CategoryController.java
        ├── ProductController.java
        ├── CustomerController.java
        └── OrderController.java
```

## 主な改善点

### 1. JPA 依存の完全な分離

**Before（旧構造）**
```java
// Entity が JPA アノテーションで汚染
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    // ...
}
```

**After（新構造）**
```java
// ドメイン層：純粋なビジネスロジック
public class Product {
    private ProductId id;
    private String name;
    private Money price;
    private Integer stock;
    private CategoryId categoryId;

    // ドメイン操作
    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        this.stock -= quantity;
    }
}

// インフラ層：JPA マッピング専用
@Entity
@Table(name = "products")
public class ProductJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryJpaEntity category;
    // ...
}
```

### 2. ドメインモデルの豊化（Rich Domain Model）

#### 値オブジェクト
```java
// Money - 金額を型安全に表現
Money price = new Money(BigDecimal.valueOf(100.0));
Money subtotal = price.multiply(quantity);
boolean isExpensive = price.isGreaterThan(threshold);

// *Id - 識別子の型安全性
CategoryId categoryId = new CategoryId(1L);
ProductId productId = new ProductId(5L);
```

#### ドメインロジック
```java
// Product - 在庫管理ロジックを集約内に保持
Product product = Product.create("Item", "Description", price, stock, categoryId);
product.decreaseStock(3);        // 在庫減少（検証付き）
product.increaseStock(5);        // 在庫増加
boolean canOrder = product.hasEnoughStock(2);  // 在庫チェック

// Order - ステータス遷移ルールを実装
Order order = Order.create(customerId, items);
order.updateStatus(OrderStatus.CONFIRMED);  // 状態遷移の検証
order.cancel();                              // キャンセル時の在庫復元

// Customer - メール検証などのドメイン検証
Customer customer = Customer.create(
    "John", "Doe", "john@example.com", "090-1234-5678",
    "Address", "City", "State", "12345"
);
customer.updateEmail("newemail@example.com");  // メール形式検証
```

#### ステータス管理
```java
public enum OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED;

    public boolean isFinal() {
        return this == DELIVERED || this == CANCELLED;
    }

    public boolean canTransitionTo(OrderStatus nextStatus) {
        // ステータス遷移ルールをドメインに実装
        return /* ... */;
    }
}
```

### 3. ユースケース層の明確化

```java
// アプリケーションサービス - ユースケース実装
public class OrderApplicationService {
    public void createOrder(Long customerId, List<OrderItemInput> items) {
        // 1. 検証（顧客、商品、在庫チェック）
        CustomerId cId = new CustomerId(customerId);
        if (!customerRepository.exists(cId)) {
            throw new IllegalArgumentException("Customer not found");
        }

        // 2. ドメインモデル作成
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemInput item : items) {
            Product product = productRepository.findById(new ProductId(item.productId))
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            if (!product.hasEnoughStock(item.quantity)) {
                throw new IllegalStateException("Insufficient stock");
            }

            OrderItem orderItem = OrderItem.create(
                product.getId(), item.quantity, product.getPrice()
            );
            orderItems.add(orderItem);
        }

        // 3. 集約を作成
        Order order = Order.create(cId, orderItems);

        // 4. 在庫減少処理
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem item = orderItems.get(i);
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            product.decreaseStock(item.getQuantity());
            productRepository.save(product);
        }

        // 5. 永続化
        orderRepository.save(order);
    }
}
```

### 4. Mapper による変換の一元化

```java
// ドメインモデルと DTO の変換を明示的に管理
public class CategoryMapper {
    public CategoryDTO toDTO(Category domain) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(domain.getId().getValue());
        dto.setName(domain.getName());
        dto.setDescription(domain.getDescription());
        return dto;
    }
}

// Controller では Mapper を使用
@GetMapping
public ResponseEntity<List<CategoryDTO>> getAllCategories() {
    return ResponseEntity.ok(
        applicationService.getAllCategories().stream()
            .map(mapper::toDTO)
            .toList()
    );
}
```

### 5. 明確なレイヤー境界

| レイヤー | 責務 | 依存関係 |
|---------|------|--------|
| **ドメイン層** | ビジネスルール、ドメイン知識 | なし（最も安定） |
| **アプリケーション層** | ユースケース実装、トランザクション | ドメイン層のみ |
| **インフラ層** | JPA、DB操作、ORM詳細 | ドメイン層、アプリケーション層 |
| **プレゼンテーション層** | HTTP、REST API | すべてのレイヤー |

## ドメイン駆動設計（DDD）の実装パターン

| パターン | 実装 | 例 |
|---------|------|-----|
| **集約ルート** | Order, Product, Category, Customer | `Order`（`OrderItem`を保有） |
| **エンティティ** | ID を持つ要素 | `OrderItem`（Order内） |
| **値オブジェクト** | ID のない不変オブジェクト | `Money`, `OrderStatus`, `*Id` |
| **リポジトリ** | 集約の永続化抽象化 | `CategoryRepository` |
| **アプリケーションサービス** | ユースケース実装 | `CategoryApplicationService` |
| **ドメインイベント** | ドメイン内のイベント | 将来拡張可能 |

## テスト性の向上

### ドメイン層のテスト（JPA 非依存）

```java
@Test
public void testProductStockDecrease() {
    // ドメインモデルを直接テスト（DB 不要）
    Product product = Product.create(
        "Item", "Description", new Money("100"), 10, new CategoryId(1L)
    );

    product.decreaseStock(3);
    assertEquals(7, product.getStock());
}

@Test
public void testProductInsufficientStock() {
    Product product = Product.create(
        "Item", "Description", new Money("100"), 5, new CategoryId(1L)
    );

    assertThrows(IllegalStateException.class, () -> product.decreaseStock(10));
}
```

### ユースケース層のテスト

```java
@Test
public void testCreateOrder() {
    // モック化されたリポジトリを使用
    OrderApplicationService service = new OrderApplicationService(
        mockOrderRepository, mockProductRepository, mockCustomerRepository
    );

    List<OrderApplicationService.OrderItemInput> items = List.of(
        new OrderApplicationService.OrderItemInput(1L, 2)
    );

    service.createOrder(1L, items);

    // 呼び出しの検証
    verify(orderRepository).save(any());
}
```

## ビルド & 実行

### 前提条件
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### ビルド

```bash
mvn clean package -DskipTests
```

### 起動

```bash
# Spring Boot Maven プラグインで実行
mvn spring-boot:run

# または JAR ファイルで実行
java -jar target/ecommerce-api-1.0.0.jar
```

### API エンドポイント

#### カテゴリ管理
```bash
GET    /api/categories              # 全カテゴリ取得
GET    /api/categories/{id}         # カテゴリ取得
POST   /api/categories              # カテゴリ作成
PUT    /api/categories/{id}         # カテゴリ更新
DELETE /api/categories/{id}         # カテゴリ削除
```

#### 商品管理
```bash
GET    /api/products                           # 全商品取得
GET    /api/products/{id}                      # 商品取得
GET    /api/products/category/{categoryId}     # カテゴリ別商品取得
POST   /api/products                           # 商品作成
PUT    /api/products/{id}                      # 商品更新
DELETE /api/products/{id}                      # 商品削除
```

#### 顧客管理
```bash
GET    /api/customers              # 全顧客取得
GET    /api/customers/{id}         # 顧客取得
POST   /api/customers              # 顧客作成
PUT    /api/customers/{id}         # 顧客更新
DELETE /api/customers/{id}         # 顧客削除
```

#### 注文管理
```bash
GET    /api/orders                              # 全注文取得
GET    /api/orders/{id}                         # 注文取得
GET    /api/orders/customer/{customerId}        # 顧客別注文取得
GET    /api/orders/status/{status}              # ステータス別注文取得
POST   /api/orders?customerId=1                 # 注文作成
PUT    /api/orders/{id}/status                  # ステータス更新
DELETE /api/orders/{id}                         # 注文削除
```

## 技術スタック

- **フレームワーク**: Spring Boot 3.1.5
- **言語**: Java 17
- **ビルドツール**: Maven
- **ORM**: Spring Data JPA + Hibernate
- **データベース**: MySQL 8.0
- **ユーティリティ**: Lombok

## プロジェクト構造の利点

1. **保守性の向上**
   - ドメインロジックが明確に分離
   - レイヤー間の責務が明確
   - コードの意図が読みやすい

2. **テスト性の向上**
   - ドメイン層をJPA非依存でテスト可能
   - モックによるユースケース層のテストが容易
   - 統合テストも実施可能

3. **拡張性の向上**
   - 新しいユースケースの追加が容易
   - ORM の変更が容易（インフラ層のみ）
   - ドメイン層の変更による影響が限定的

4. **チームの生産性**
   - 異なるレイヤーの並行開発が可能
   - ドメイン層の設計から開発開始可能
   - ビジネス要件との対応が明確

## ドメイン駆動設計の実践

このプロジェクトで実装された DDD パターン：

- **ユビキタス言語**: `Order`, `Product`, `Stock`, `Money` など、ビジネス用語を直接コードに反映
- **集約**: `Order` が `OrderItem` をまとめる集約として機能
- **値オブジェクト**: `Money`, `OrderStatus`, `*Id` で型安全性を確保
- **ドメイン操作**: `product.decreaseStock()` のようにドメインロジックが集約内に定義
- **リポジトリ**: データベース詳細を隠蔽し、ビジネスロジックに集中

## 将来の拡張方針

- **ドメインイベント**: `OrderCreatedEvent`, `StockDecreaseEvent` などの実装
- **仕様パターン**: 複雑な検索条件を仕様オブジェクトで表現
- **イベントソーシング**: 注文履歴の完全な追跡
- **CQRS**: 読み取りと書き込みの最適化

## ライセンス

このプロジェクトは MIT ライセンスの下で公開されています。

## 参考資料

- [クリーンアーキテクチャ - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [ドメイン駆動設計 - Eric Evans](https://www.domainlanguage.com/ddd/)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
