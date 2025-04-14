-- =======================
-- USERS TABLES
-- =======================

CREATE TABLE users (
user_id VARCHAR2(50) PRIMARY KEY,
first_name VARCHAR2(100) NOT NULL,
last_name VARCHAR2(100) NOT NULL,
email VARCHAR2(150) UNIQUE NOT NULL,
hashed_password VARCHAR2(255) NOT NULL,
pin_hash VARCHAR2(255) NOT NULL,
role VARCHAR2(20) CHECK (role IN ('CUSTOMER', 'MANAGER')) NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE customers (
user_id VARCHAR2(50) PRIMARY KEY,
phone_number VARCHAR2(20),
FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE bank_managers (
user_id VARCHAR2(50) PRIMARY KEY,
FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- =======================
-- ACCOUNTS TABLE
-- =======================

CREATE TABLE accounts (
account_id VARCHAR2(50) PRIMARY KEY,
customer_id VARCHAR2(50) NOT NULL,
account_type VARCHAR2(30) NOT NULL CHECK (account_type IN (
'CHECKING', 'SAVINGS', 'CREDIT', 'CURRENCY', 'LINE_OF_CREDIT'
)),
opened_date DATE NOT NULL,
is_active NUMBER(1) DEFAULT 1 NOT NULL CHECK (is_active IN (0, 1)),
available_balance NUMBER(18, 2) DEFAULT 0 NOT NULL,
credit_limit NUMBER(18, 2),         -- For CREDIT accounts
interest_rate NUMBER(5, 2),         -- For SAVINGS and CREDIT accounts
currency_code VARCHAR2(10),         -- For CURRENCY accounts
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
FOREIGN KEY (customer_id) REFERENCES customers(user_id) ON DELETE CASCADE
);

-- =======================
-- TRANSACTIONS TABLE
-- =======================

CREATE TABLE transactions (
transaction_id VARCHAR2(50) PRIMARY KEY,
transaction_type VARCHAR2(20) NOT NULL CHECK (transaction_type IN (
'DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'FEE'
)),
transaction_date DATE NOT NULL,
amount NUMBER(18, 2) NOT NULL,
description VARCHAR2(255),
source_account_id VARCHAR2(50),
destination_account_id VARCHAR2(50),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
FOREIGN KEY (source_account_id) REFERENCES accounts(account_id) ON DELETE SET NULL,
FOREIGN KEY (destination_account_id) REFERENCES accounts(account_id) ON DELETE SET NULL
);

-- =======================
-- NOTIFICATIONS TABLE
-- =======================

CREATE TABLE notifications (
notification_id VARCHAR2(50) PRIMARY KEY,
title VARCHAR2(255) NOT NULL,
recipient_user_id VARCHAR2(50) NOT NULL,
account_id VARCHAR2(50), -- nullable
message VARCHAR2(500),
type VARCHAR2(30),
seen NUMBER(1) DEFAULT 0 NOT NULL CHECK (seen IN (0, 1)),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
related_customer_id VARCHAR2(50),-- nullable

FOREIGN KEY (recipient_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE SET NULL,
FOREIGN KEY (related_customer_id) REFERENCES  users(user_id) ON DELETE SET NULL
);



-- =======================
-- INDEXES FOR PERFORMANCE
-- =======================

-- Improve lookup by customer
CREATE INDEX idx_accounts_customer_id ON accounts(customer_id);

-- Support filtering/grouping by account type
CREATE INDEX idx_accounts_account_type ON accounts(account_type);

-- Optimize transactions filtering by account
CREATE INDEX idx_transactions_source_account_id ON transactions(source_account_id);
CREATE INDEX idx_transactions_destination_account_id ON transactions(destination_account_id);

-- Optimize recipient filtering in inbox
CREATE INDEX idx_notifications_user_id ON notifications(recipient_user_id);
