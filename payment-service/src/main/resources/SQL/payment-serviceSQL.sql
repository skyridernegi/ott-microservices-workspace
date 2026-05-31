CREATE DATABASE IF NOT EXISTS payment_db;
USE payment_db;

CREATE TABLE IF NOT EXISTS payments (
    payment_id       BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT         NOT NULL,
    plan_id          VARCHAR(10)    NOT NULL, -- will come from catalog_db.ottplans
    amount           DECIMAL(10,2)  NOT NULL,
    status           ENUM('PENDING','SUCCESS','FAILED','REFUNDED')
                                    NOT NULL DEFAULT 'PENDING',
    payment_method   VARCHAR(50)    DEFAULT 'CARD',
    transaction_id   VARCHAR(100)   UNIQUE,   -- from payment gateway (Stripe etc)
    failure_reason   VARCHAR(255),
    card_last_four   VARCHAR(4),
    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payment_retries (
    retry_id        BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
    payment_id      BIGINT    NOT NULL,
    retry_count     INT       NOT NULL DEFAULT 0,
    last_retry_at   TIMESTAMP,
    next_retry_at   TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payments(payment_id)
);