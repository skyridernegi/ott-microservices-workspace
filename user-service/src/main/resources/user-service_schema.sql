-- Run this in MySQL Workbench before starting user-service
CREATE DATABASE IF NOT EXISTS user_db;
USE user_db;

CREATE TABLE IF NOT EXISTS users (
    user_id       BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,  -- BCrypt hashed, never plain text
    full_name     VARCHAR(100) NOT NULL,
    phone         VARCHAR(15),
    role          ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP
);

-- Seed users (password = "password123" BCrypt-hashed='$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J4JqXqBnO')
INSERT INTO users (username, email, password, full_name, phone, role) VALUES
('john.doe',   'john.doe@gmail.com',   '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J4JqXqBnO', 'John Doe',   '9876543210', 'USER'),
('jane.smith', 'jane.smith@gmail.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J4JqXqBnO', 'Jane Smith', '9876543211', 'USER'),
('admin',      'admin@espn.com',       '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J4JqXqBnO', 'Admin User', '9999999999', 'ADMIN');

select * from users;