-- =====================================================
-- CAMPAIGN SERVICE DATABASE SCHEMA
-- Database: campaign_db
-- Run this in MySQL Workbench before starting campaign-service
-- =====================================================

CREATE DATABASE IF NOT EXISTS campaign_db;
USE campaign_db;

-- ─────────────────────────────────────────────────
-- campaigns table
-- Stores all promotional campaigns created by admin
-- USE CASE: "Disney+ Free 3 Months for new users"
--           "30% off ESPN+ yearly plan"
--           "Bundle discount for Disney+ESPN+"
-- ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS campaigns (
    campaign_id      BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(100)   NOT NULL,
    description      VARCHAR(500),
    discount_percent DECIMAL(5,2)   NOT NULL DEFAULT 0.00,
    free_trial_days  INT            DEFAULT 0,
    promo_code       VARCHAR(20)    UNIQUE,
    start_date       DATE           NOT NULL,
    end_date         DATE           NOT NULL,

    -- eligible_plans: comma-separated plan IDs or "ALL"
    -- e.g. "PLAN001,PLAN002" or "ALL"
    eligible_plans   VARCHAR(500)   DEFAULT 'ALL',

    -- max_uses: NULL means unlimited
    max_uses         INT            DEFAULT NULL,
    current_uses     INT            NOT NULL DEFAULT 0,

    -- campaign_type drives business logic in CampaignService
    campaign_type    ENUM('FREE_TRIAL','DISCOUNT','BUNDLE','RENEWAL')
                                    NOT NULL DEFAULT 'DISCOUNT',

    is_active        BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────
-- campaign_applications table
-- Tracks which user applied which campaign — prevents reuse
-- USE CASE: User "john.doe" used promo code "WELCOME90"
--           → stored here so he can't use it again
-- ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS campaign_applications (
    application_id   BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    campaign_id      BIGINT         NOT NULL,
    user_id          BIGINT         NOT NULL,
    plan_id          VARCHAR(10)    NOT NULL,
    discount_applied DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    applied_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Prevents same user applying same campaign twice
    UNIQUE KEY uq_user_campaign (user_id, campaign_id),
    FOREIGN KEY (campaign_id) REFERENCES campaigns(campaign_id)
);

-- ─────────────────────────────────────────────────
-- SEED DATA — sample campaigns for testing
-- ─────────────────────────────────────────────────
INSERT INTO campaigns
    (name, description, discount_percent, free_trial_days, promo_code,
     start_date, end_date, eligible_plans, max_uses, campaign_type, is_active)
VALUES
-- Campaign 1: New user free trial
('Welcome Free Trial',
 'Get Disney+ Basic free for your first 90 days — new users only',
 100.00, 90, 'WELCOME90',
 CURDATE(), DATE_ADD(CURDATE(), INTERVAL 6 MONTH),
 'PLAN001', 1000, 'FREE_TRIAL', TRUE),

-- Campaign 2: Bundle discount
('Sports Bundle Deal',
 'Subscribe to Disney+ and ESPN+ together and save 30%',
 30.00, 0, 'BUNDLE30',
 CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 MONTH),
 'PLAN005', 500, 'BUNDLE', TRUE),

-- Campaign 3: Yearly renewal reward
('Renewal Reward',
 'Renew your yearly ESPN+ plan and save 20%',
 20.00, 0, 'RENEW20',
 CURDATE(), DATE_ADD(CURDATE(), INTERVAL 12 MONTH),
 'PLAN004', NULL, 'RENEWAL', TRUE),

-- Campaign 4: Flash sale
('Flash Sale — 50% Off',
 'Limited time 50% discount on Disney+ Premium monthly plan',
 50.00, 0, 'FLASH50',
 CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY),
 'PLAN002', 200, 'DISCOUNT', TRUE),

-- Campaign 5: Inactive example
('Summer Special',
 'Past campaign — kept for records',
 15.00, 0, 'SUMMER15',
 '2024-06-01', '2024-08-31',
 'ALL', 500, 'DISCOUNT', FALSE);
