-- 建立資料庫
CREATE DATABASE IF NOT EXISTS esun_social;
USE esun_social;

-- ==========================================
-- DDL (資料定義語言)：建立資料表
-- ==========================================

-- 1. 使用者表 (User)
-- 題目要求以手機號碼註冊登入，所以我們新增 Phone_Number 並設為 UNIQUE
CREATE TABLE IF NOT EXISTS User (
    User_ID INT AUTO_INCREMENT PRIMARY KEY,
    Phone_Number VARCHAR(15) UNIQUE NOT NULL,
    User_Name VARCHAR(50) NOT NULL,
    Email VARCHAR(100) NOT NULL,
    Password VARCHAR(255) NOT NULL,
    Cover_Image VARCHAR(255),
    Biography TEXT
);

-- 2. 發文表 (Post)
CREATE TABLE IF NOT EXISTS Post (
    Post_ID INT AUTO_INCREMENT PRIMARY KEY,
    User_ID INT NOT NULL,
    Content TEXT NOT NULL,
    Image VARCHAR(255),
    Created_At TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (User_ID) REFERENCES User(User_ID) ON DELETE CASCADE
);

-- 3. 留言表 (Comment)
CREATE TABLE IF NOT EXISTS Comment (
    Comment_ID INT AUTO_INCREMENT PRIMARY KEY,
    User_ID INT NOT NULL,
    Post_ID INT NOT NULL,
    Content TEXT NOT NULL,
    Created_At TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (User_ID) REFERENCES User(User_ID) ON DELETE CASCADE,
    FOREIGN KEY (Post_ID) REFERENCES Post(Post_ID) ON DELETE CASCADE
);

-- ==========================================
-- Stored Procedure (預存程序)
-- ==========================================

-- 移除舊的 SP 避免重複建立錯誤
DROP PROCEDURE IF EXISTS SP_RegisterUser;

-- 改變分隔符號，讓 MySQL 知道這是一整個區塊的指令
DELIMITER //

-- 1. 建立註冊使用者的 Stored Procedure
CREATE PROCEDURE SP_RegisterUser (
    IN p_phone VARCHAR(15),
    IN p_name VARCHAR(50),
    IN p_email VARCHAR(100),
    IN p_password VARCHAR(255)
)
BEGIN
    INSERT INTO User (Phone_Number, User_Name, Email, Password)
    VALUES (p_phone, p_name, p_email, p_password);
END //

DELIMITER ;

-- 變更結束符號為 //，防止中間的 SQL 分號導致提前結束
DELIMITER //

CREATE PROCEDURE SP_CreatePost (
    IN p_user_id INT,
    IN p_content TEXT,
    IN p_image VARCHAR(255)
)
BEGIN
	-- 這裡寫入建立貼文的商業邏輯，新增一筆資料到 post 資料表
    INSERT INTO Post (User_ID, Content, Image)
    VALUES (p_user_id, p_content, p_image);
END //

-- 將結束符號改回預設的分號
DELIMITER ;

DELIMITER //

-- 3. 查詢所有發文的 SP (依照時間越新的排在越上面)
CREATE PROCEDURE SP_GetAllPosts ()
BEGIN
    SELECT * FROM Post ORDER BY Created_At DESC;
END //

-- 4. 新增留言的 SP
CREATE PROCEDURE SP_CreateComment (
    IN p_user_id INT,
    IN p_post_id INT,
    IN p_content TEXT
)
BEGIN
    INSERT INTO Comment (User_ID, Post_ID, Content)
    VALUES (p_user_id, p_post_id, p_content);
END //

DELIMITER ;

DELIMITER //

-- 5. 刪除特定文章底下的所有留言
CREATE PROCEDURE SP_DeleteCommentsByPost (
    IN p_post_id INT
)
BEGIN
    DELETE FROM Comment WHERE Post_ID = p_post_id;
END //

-- 6. 刪除發文本身
CREATE PROCEDURE SP_DeletePost (
    IN p_post_id INT
)
BEGIN
    DELETE FROM Post WHERE Post_ID = p_post_id;
END //

DELIMITER ;

-- 7. 編輯文章內容
DELIMITER //
-- 先刪除舊的，避免衝突
DROP PROCEDURE IF EXISTS SP_UpdatePost //

CREATE PROCEDURE SP_UpdatePost (
    IN p_post_id INT,
    IN p_content TEXT,
    IN p_image VARCHAR(255)
)
BEGIN
    UPDATE Post 
    SET Content = p_content, Image = p_image 
    WHERE Post_ID = p_post_id;
END //
DELIMITER ;

-- 8. 取得所有留言的 SP (依照時間由舊到新排序)
DELIMITER //

CREATE PROCEDURE SP_GetAllComments ()
BEGIN
    -- 依照時間由舊到新排序，讓最舊的留言在最上面
    SELECT * FROM Comment ORDER BY Created_At ASC; 
END //

DELIMITER ;

-- ==========================================
-- DML (資料操作語言)：建立假資料測試
-- ==========================================
-- 密碼欄位先放明碼做示意，後續我們會在 Java 中實作 Hash 加密
INSERT INTO User (Phone_Number, User_Name, Email, Password, Biography) 
VALUES ('0912345678', 'EsunTester', 'test@esun.com', 'hashed_password_here', 'Hello E.SUN');