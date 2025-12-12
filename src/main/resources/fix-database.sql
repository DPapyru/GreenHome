-- 修复数据库表结构问题

-- 1. 删除现有的外键约束（如果存在）
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS articles;
DROP TABLE IF EXISTS greenhome_user;
SET FOREIGN_KEY_CHECKS=1;

-- 2. 重新创建用户表
CREATE TABLE greenhome_user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    password VARCHAR(100) NOT NULL,
    password_salt VARCHAR(255),
    email VARCHAR(255),
    inv_code VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    UNIQUE KEY user_id (user_id)
);

-- 3. 重新创建文章表，正确设置外键
CREATE TABLE articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    user_id INT,
    created_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES greenhome_user(user_id)
);

-- 4. 为外键字段创建索引以提高性能
CREATE INDEX idx_articles_user_id ON articles(user_id);