create database userdb;
use userdb;
CREATE TABLE users (
id BIGINT NOT NULL AUTO_INCREMENT,
first_name VARCHAR(50) NOT NULL,
last_name VARCHAR(50) NOT NULL,
email VARCHAR(100) NOT NULL,
username VARCHAR(50) NOT NULL,
password VARCHAR(255) NOT NULL,
phone VARCHAR(15) DEFAULT NULL,
status ENUM('ACTIVE','INACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
created_at DATETIME NOT NULL,
updated_at DATETIME DEFAULT NULL,
PRIMARY KEY (id),
UNIQUE KEY uk_users_email (email),
UNIQUE KEY uk_users_username (username)
);
