# E-Commerce REST API

SpringBootで構築されたECサイト向けのREST APIです。MySQL、Spring Data JPA、およびDockerコンテナを使用しています。

## 機能

このAPIは、ECサイトの以下の機能を提供します：

### カテゴリー管理 (Categories)
- カテゴリーの作成、読取、更新、削除（CRUD）
- `GET /api/categories` - すべてのカテゴリーを取得
- `GET /api/categories/{id}` - IDでカテゴリーを取得
- `POST /api/categories` - 新しいカテゴリーを作成
- `PUT /api/categories/{id}` - カテゴリーを更新
- `DELETE /api/categories/{id}` - カテゴリーを削除

### 製品管理 (Products)
- 製品の作成、読取、更新、削除
- カテゴリー別製品検索
- `GET /api/products` - すべての製品を取得
- `GET /api/products/{id}` - IDで製品を取得
- `GET /api/products/category/{categoryId}` - カテゴリー別に製品を取得
- `POST /api/products` - 新しい製品を作成
- `PUT /api/products/{id}` - 製品を更新
- `DELETE /api/products/{id}` - 製品を削除

### 顧客管理 (Customers)
- 顧客情報の作成、読取、更新、削除
- `GET /api/customers` - すべての顧客を取得
- `GET /api/customers/{id}` - IDで顧客を取得
- `POST /api/customers` - 新しい顧客を作成
- `PUT /api/customers/{id}` - 顧客情報を更新
- `DELETE /api/customers/{id}` - 顧客を削除

### 注文管理 (Orders)
- 注文の作成、読取、ステータス更新
- 顧客別注文検索
- ステータス別注文検索
- `GET /api/orders` - すべての注文を取得
- `GET /api/orders/{id}` - IDで注文を取得
- `GET /api/orders/customer/{customerId}` - 顧客別に注文を取得
- `GET /api/orders/status/{status}` - ステータス別に注文を取得
- `POST /api/orders` - 新しい注文を作成
- `PUT /api/orders/{id}/status` - 注文ステータスを更新
- `DELETE /api/orders/{id}` - 注文を削除

## プロジェクト構成

```
ecommerce-api/
├── src/main/java/com/ecommerce/
│   ├── controller/          # REST API コントローラー
│   │   ├── CategoryController.java
│   │   ├── ProductController.java
│   │   ├── CustomerController.java
│   │   └── OrderController.java
│   ├── service/             # ビジネスロジック
│   │   ├── CategoryService.java
│   │   ├── ProductService.java
│   │   ├── CustomerService.java
│   │   └── OrderService.java
│   ├── entity/              # JPAエンティティ
│   │   ├── Category.java
│   │   ├── Product.java
│   │   ├── Customer.java
│   │   ├── Order.java
│   │   └── OrderItem.java
│   ├── repository/          # Spring Data JPAリポジトリ
│   │   ├── CategoryRepository.java
│   │   ├── ProductRepository.java
│   │   ├── CustomerRepository.java
│   │   ├── OrderRepository.java
│   │   └── OrderItemRepository.java
│   ├── dto/                 # データ転送オブジェクト
│   │   ├── CategoryDTO.java
│   │   ├── ProductDTO.java
│   │   ├── CustomerDTO.java
│   │   ├── OrderDTO.java
│   │   └── OrderItemDTO.java
│   └── EcommerceApiApplication.java  # メインアプリケーション
├── src/main/resources/
│   └── application.yml      # アプリケーション設定
├── Dockerfile               # Docker イメージ定義
├── pom.xml                  # Maven 設定ファイル
└── docker-compose.yml       # Docker Compose 設定
```

## 技術スタック

- **Java 17** - プログラミング言語
- **Spring Boot 3.1.5** - フレームワーク
- **Spring Data JPA** - ORM(オブジェクト関連マッピング)
- **MySQL 8.0** - リレーショナルデータベース
- **Lombok** - ボイラープレート削減
- **Maven** - ビルドツール
- **Docker** - コンテナ化

## セットアップ手順

### 前提条件

- Docker と Docker Compose がインストールされていること
- Java 17 (ローカル開発の場合)
- Maven 3.6以上 (ローカル開発の場合)

### Dockerで実行する（推奨）

1. プロジェクトルートディレクトリに移動します

```bash
cd /path/to/ecommerce-api
```

2. アプリケーションをビルドします

```bash
mvn clean package -DskipTests
```

3. Docker Composeで起動します

```bash
cd ..
docker-compose up --build
```

4. APIが起動します。以下のURLでアクセス可能です：

```
http://localhost:8080/api
```

### ローカル開発環境での実行

1. MySQLサーバーが起動していることを確認します
   - デフォルト接続情報：
     - ホスト: localhost
     - ポート: 3306
     - ユーザー: root
     - パスワード: password
     - データベース: ecommerce_db

2. プロジェクトルートに移動します

```bash
cd ecommerce-api
```

3. Mavenでビルド・実行します

```bash
mvn clean package -DskipTests
mvn spring-boot:run
```

または JAR ファイルを直接実行します

```bash
java -jar target/ecommerce-api-1.0.0.jar
```

4. APIは以下のURLでアクセスできます：

```
http://localhost:8080/api
```

## APIの使用例

### カテゴリーの作成

```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Electronic devices and gadgets"
  }'
```

### 製品の作成

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 1500.00,
    "stock": 50,
    "categoryId": 1
  }'
```

### 顧客の作成

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "phoneNumber": "1234567890",
    "address": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001"
  }'
```

### 注文の作成

```bash
curl -X POST "http://localhost:8080/api/orders?customerId=1" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "productId": 1,
      "quantity": 2
    }
  ]'
```

### 注文ステータスの更新

```bash
curl -X PUT http://localhost:8080/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "SHIPPED"
  }'
```

### データの取得

```bash
# すべてのカテゴリーを取得
curl http://localhost:8080/api/categories

# すべての製品を取得
curl http://localhost:8080/api/products

# すべての顧客を取得
curl http://localhost:8080/api/customers

# すべての注文を取得
curl http://localhost:8080/api/orders

# 特定の顧客の注文を取得
curl http://localhost:8080/api/orders/customer/1

# 特定のステータスの注文を取得
curl "http://localhost:8080/api/orders/status/PENDING"
```

## データベース設定

`application.yml`ファイルでMySQL接続情報を設定できます：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ecommerce_db
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update  # スキーマを自動的に作成・更新
```

### DDL-Auto オプション

- `create` - 毎回テーブルを再作成
- `create-drop` - アプリケーション終了時にテーブルを削除
- `update` - スキーマを更新（本番環境では非推奨）
- `validate` - スキーマを検証
- `none` - 何もしない

## トラブルシューティング

### MySQLに接続できない場合

1. MySQLサーバーが起動していることを確認

```bash
# Dockerで実行している場合
docker ps | grep mysql

# ローカル環境の場合
mysql -u root -p
```

2. 接続情報が正しいか確認（`application.yml`）

3. データベースが存在することを確認

```sql
CREATE DATABASE IF NOT EXISTS ecommerce_db;
```

### ポート競合エラー

8080番ポートがすでに使用されている場合は、`application.yml`で変更できます：

```yaml
server:
  port: 8081
```

## ビルドとデプロイ

### Dockerイメージのビルド

```bash
cd ecommerce-api
mvn clean package -DskipTests
docker build -t ecommerce-api:latest .
```

### Dockerイメージの実行

```bash
docker run -d \
  --name ecommerce-api \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/ecommerce_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  ecommerce-api:latest
```

## 注文ステータス

注文は以下のステータスを持ちます：

- `PENDING` - 保留中
- `CONFIRMED` - 確認済み
- `SHIPPED` - 発送済み
- `DELIVERED` - 配達済み
- `CANCELLED` - キャンセル

## ライセンス

このプロジェクトはMITライセンスの下で公開されています。

## サポート

問題や質問がある場合は、プロジェクトのIssueセクションで報告してください。
