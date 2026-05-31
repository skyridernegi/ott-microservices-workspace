-- =====================================================
-- OTT PLATFORM - MySQL Schema
-- Run this script in MySQL Workbench or MySQL CLI
-- =====================================================

-- -------------------------
-- 1. Create Databases
-- -------------------------
CREATE DATABASE IF NOT EXISTS catalog_db;
CREATE DATABASE IF NOT EXISTS subscription_db;

-- =====================================================
-- CATALOG SERVICE DATABASE
-- =====================================================
USE catalog_db;

-- OTT Plans Table
CREATE TABLE IF NOT EXISTS ott_plans (
    plan_id       VARCHAR(10)    NOT NULL PRIMARY KEY,
    plan_name     VARCHAR(100)   NOT NULL,
    price         DECIMAL(10,2)  NOT NULL,
    duration      ENUM('MONTHLY','YEARLY') NOT NULL,
    description   VARCHAR(255),
    is_active     BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Seed Data - OTT Plans
INSERT INTO ott_plans (plan_id, plan_name, price, duration, description, is_active) VALUES
('PLAN001', 'Disney+ Basic',    199.00, 'MONTHLY', 'Access to Disney+ content in HD',          TRUE),
('PLAN002', 'Disney+ Premium',  299.00, 'MONTHLY', 'Access to Disney+ content in 4K + 4 screens', TRUE),
('PLAN003', 'ESPN+ Monthly',    149.00, 'MONTHLY', 'Live sports and ESPN originals',            TRUE),
('PLAN004', 'ESPN+ Yearly',     999.00, 'YEARLY',  'Live sports and ESPN originals - Save 44%', TRUE),
('PLAN005', 'Combo Pack',       399.00, 'MONTHLY', 'Disney+ Premium + ESPN+ combo',             TRUE);

select * from ott_plans;
-- =====================================================
-- SUBSCRIPTION SERVICE DATABASE
-- =====================================================
USE subscription_db;
select * from users;
SELECT * FROM subscription_db.subscriptions;
-- Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id       BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)    NOT NULL UNIQUE,
    email         VARCHAR(100)   NOT NULL UNIQUE,
    full_name     VARCHAR(100)   NOT NULL,
    phone         VARCHAR(15),
    is_active     BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Subscriptions Table
CREATE TABLE IF NOT EXISTS subscriptions (
    subscription_id   BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT         NOT NULL,
    plan_id           VARCHAR(10)    NOT NULL,
    status            ENUM('ACTIVE','EXPIRED','CANCELLED','PENDING') NOT NULL DEFAULT 'PENDING',
    start_date        DATE           NOT NULL,
    end_date          DATE           NOT NULL,
    amount_paid       DECIMAL(10,2)  NOT NULL,
    auto_renewal      BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Seed Data - Users
INSERT INTO users (username, email, full_name, phone) VALUES
('john.doe',    'john.doe@gmail.com',    'John Doe',    '9876543210'),
('jane.smith',  'jane.smith@gmail.com',  'Jane Smith',  '9876543211'),
('raj.kumar',   'raj.kumar@gmail.com',   'Raj Kumar',   '9876543212');

select * from users;