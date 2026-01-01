# AgriGuide Backend

**農業機械用ガイダンスシステム - バックエンドAPI**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17-red)](https://www.oracle.com/java/)
[![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-orange)](https://jakarta.ee/)
[![Tomcat](https://img.shields.io/badge/Tomcat-10.1-yellow)](https://tomcat.apache.org/)

## 📖 概要

AgriGuide Backendは、農業機械の走行データを管理・提供するREST APIサーバーです。Jakarta EE 10（Servlet 6.0）とMySQLを使用し、リアルタイムデータと履歴データの両方を提供します。

### 🎯 主な機能

- **リアルタイムデータAPI** - 現在の速度・距離情報の提供
- **履歴データAPI** - 過去の走行記録の取得（最新10件）
- **CORS対応** - クロスオリジンリクエストのサポート
- **データベース連携** - MySQLによる永続化

## 🛠️ 技術スタック

### バックエンド
- **Java 17** - LTS版による安定した開発環境
- **Jakarta EE 10** - エンタープライズJavaの最新仕様
- **Jakarta Servlet 6.0** - Webアプリケーション基盤
- **Apache Tomcat 10.1** - Servletコンテナ

### データベース
- **MySQL 8.0** - リレーショナルデータベース
- **JDBC Driver 8.0.33** - データベース接続

### ビルドツール
- **Maven** - 依存関係管理とビルド自動化

## 📁 プロジェクト構成

```
agri-guide-backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── agriguide/
│       │           └── api/
│       │               ├── SensorDataServlet.java
│       │               └── HistoryDataServlet.java
│       └── webapp/
│           └── WEB-INF/
│               └── web.xml
├── pom.xml
└── README.md
```

## 🚀 セットアップ

### 前提条件

- **Java JDK 17以上**
- **Apache Tomcat 10.1以上**
- **MySQL 8.0以上**
- **Maven 3.6以上**
- **Eclipse IDE**（推奨）

### データベースセットアップ

1. **MySQLにログイン**
   ```bash
   mysql -u root -p
   ```

2. **データベースとテーブルの作成**
   ```sql
   CREATE DATABASE agriguide_db;
   
   USE agriguide_db;
   
   CREATE TABLE trip_data (
       id INT AUTO_INCREMENT PRIMARY KEY,
       speed DOUBLE NOT NULL,
       distance DOUBLE NOT NULL,
       latitude DOUBLE NOT NULL,
       longitude DOUBLE NOT NULL,
       timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );
   ```

3. **サンプルデータの挿入**（オプション）
   ```sql
   INSERT INTO trip_data (speed, distance, latitude, longitude) VALUES
   (8.5, 150.3, 36.5, 138.5),
   (9.2, 165.7, 36.51, 138.52),
   (7.8, 140.2, 36.49, 138.48);
   ```

### アプリケーションセットアップ

1. **リポジトリのクローン**
   ```bash
   git clone https://github.com/maki-hiradate/agri-guide-backend.git
   ```

2. **データベース接続情報の設定**
   
   `SensorDataServlet.java` と `HistoryDataServlet.java` の以下を編集：
   ```java
   private static final String DB_URL = "jdbc:mysql://localhost:3306/agriguide_db";
   private static final String DB_USER = "root";
   private static final String DB_PASSWORD = "your_password"; // ← 変更
   ```

3. **Mavenビルド**
   ```bash
   mvn clean install
   ```

4. **Tomcatにデプロイ**
   
   生成された `target/agri-guide-backend.war` をTomcatにデプロイ

5. **起動確認**
   ```
   http://localhost:8080/agri-guide-backend/api/sensor-data
   ```

## 📡 API仕様

### 1. センサーデータ取得API

**エンドポイント:** `GET /api/sensor-data`

**説明:** 現在のセンサーデータを取得

**レスポンス:**
```
8.5,150.3
```
（CSV形式: `speed,distance`）

**ステータスコード:**
- `200 OK` - 成功
- `500 Internal Server Error` - データベースエラー

---

### 2. 履歴データ取得API

**エンドポイント:** `GET /api/history-data`

**説明:** 過去の走行記録を最新10件取得

**レスポンス:**
```json
[
  {
    "id": 1,
    "speed": 8.5,
    "distance": 150.3,
    "latitude": 36.5,
    "longitude": 138.5,
    "timestamp": "2025-12-30 10:30:00"
  },
  ...
]
```

**ステータスコード:**
- `200 OK` - 成功
- `500 Internal Server Error` - データベースエラー

**CORS ヘッダー:**
```
Access-Control-Allow-Origin: http://localhost:8000
Access-Control-Allow-Methods: GET, POST, OPTIONS
Access-Control-Allow-Headers: Content-Type
```

## 🏗️ アーキテクチャ

### システム構成

```
[フロントエンド]
  ↓ HTTP Request
[Tomcat Servlet Container]
  ├─ SensorDataServlet
  └─ HistoryDataServlet
       ↓ JDBC
[MySQL Database]
  └─ agriguide_db
      └─ trip_data
```

### データベーススキーマ

#### trip_data テーブル

| カラム名 | データ型 | 説明 |
|---------|---------|------|
| id | INT | 主キー（自動採番） |
| speed | DOUBLE | 速度（km/h） |
| distance | DOUBLE | 距離（m） |
| latitude | DOUBLE | 緯度 |
| longitude | DOUBLE | 経度 |
| timestamp | TIMESTAMP | 記録日時 |

## 🔧 開発

### Eclipse での開発

1. **プロジェクトのインポート**
   ```
   File → Import → Existing Maven Projects
   ```

2. **Tomcatサーバーの追加**
   ```
   Servers タブ → New → Apache Tomcat v10.1
   ```

3. **デプロイして起動**
   ```
   プロジェクト右クリック → Run As → Run on Server
   ```

### トラブルシューティング

#### CORS エラーが発生する場合

`HistoryDataServlet.java` の `setCorsHeaders()` メソッドで許可するオリジンを確認：

```java
if (origin != null && 
    (origin.equals("http://localhost:8000") || origin.equals("http://127.0.0.1:8000"))) {
    response.setHeader("Access-Control-Allow-Origin", origin);
}
```

#### データベース接続エラー

- MySQLが起動しているか確認
- 接続情報（URL, USER, PASSWORD）が正しいか確認
- ファイアウォールがポート3306をブロックしていないか確認

## 📈 今後の拡張予定

- [ ] データ登録API（POST）の実装
- [ ] 認証・認可機能の追加
- [ ] ページネーション対応
- [ ] データ集計・分析API
- [ ] WebSocket通信対応

## 🧪 テスト

### 手動テスト

```bash
# センサーデータAPI
curl http://localhost:8080/agri-guide-backend/api/sensor-data

# 履歴データAPI
curl http://localhost:8080/agri-guide-backend/api/history-data
```

## 📄 ライセンス

[MIT License](LICENSE)

## 👤 作者

**Maki Hiradate**
- GitHub: [@maki-hiradate](https://github.com/maki-hiradate)

## 🔗 関連リポジトリ

- [agri-guide-dashboard](https://github.com/maki-hiradate/agri-guide-dashboard) - フロントエンド

---

**Built with ❤️ for Smart Agriculture**
