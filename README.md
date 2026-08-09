# 簡易社群媒體平台

本專案採用**前後端分離架構**，後端負責商業邏輯與資安防護，前端提供直覺的操作介面，所有與資料庫的互動皆透過預存程序 (Stored Procedure) 執行。

## 技術棧

### 後端
* **語言/框架**：Java 17 / Spring Boot 4.1
* **資料庫存取**：Spring Data JPA
* **身分驗證與資安**：Spring Security + JWT
* **建置工具**：Maven

### 前端 
* **框架**：Vue.js 3 (Composition API)
* **建置工具**：Vite
* **路由管理**：Vue Router
* **API 串接**：Axios

### 資料庫 
* **關聯式資料庫**：MySQL 8

---

## 系統功能

1. **使用者註冊與登入**
   * 以手機號碼作為帳號進行註冊。
   * 採用 JWT 進行無狀態 (Stateless) 的身分驗證與路由攔截 (Router Guard)。
2. **貼文系統 (CRUD)**
   * **新增**：可發布包含文字與圖片網址的動態貼文。
   * **讀取**：於首頁動態牆依時間反序 (最新至最舊) 列出所有貼文。
   * **編輯**：支援 PATCH 部分更新，可單獨修改文字或清除圖片，並同步刪除實體圖片節省資源。
   * **刪除**：實作 Transaction，刪除貼文時會連動刪除該貼文底下的所有留言，確保資料一致性。
3. **留言功能**
   * 登入使用者可對特定貼文進行留言，並即時顯示於動態牆上。
4. **資安防禦機制**
   * **防範 SQL Injection**：透過強制綁定變數並呼叫 Stored Procedure 存取資料庫。
   * **防範 XSS 攻擊**：後端接收文字內容時，統一透過 `HtmlUtils.htmlEscape` 進行轉義過濾。
   * **防範 IDOR (越權攻擊)**：後端嚴格校驗 JWT 解析出的 User ID 是否與欲編輯/刪除之文章作者相符。

---

## 專案目錄結構

```text
esun-social-project/
├── backend/               # Spring Boot 後端專案
├── frontend/              # Vue.js 前端專案
└── DB/                    # 資料庫腳本
    └── schema_and_data.sql # 包含所有 DDL (建表) 與 DML (預存程序) 語法
```

---

## 啟動指南

### 1. 資料庫設定
1. 確保已安裝並啟動 MySQL 伺服器。
2. 建立資料庫：`CREATE DATABASE esun_social;`
3. 匯入專案中 `DB/schema_and_data.sql` 的腳本以建立資料表與預存程序。

### 2. 後端啟動 (Spring Boot)
1. 進入 `backend` 資料夾。
2. 修改 `src/main/resources/application.properties` 中的資料庫帳號與密碼。
3. 透過 IDE (如 VS Code / IntelliJ) 啟動 `SocialMediaApplication.java`。
4. 後端伺服器將預設運行於 `http://localhost:8080`。

### 3. 前端啟動 (Vue.js)
1. 進入 `frontend` 資料夾。
2. 開啟終端機，安裝依賴套件：`npm install`
3. 安裝前端串接 API 工具：`npm install axios `
4. 啟動前端開發伺服器：`npm run dev`
5. 開啟瀏覽器訪問 `http://localhost:5173` 
