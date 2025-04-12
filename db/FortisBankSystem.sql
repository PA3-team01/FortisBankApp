-- USERS (base table for all user types)
CREATE TABLE users (
user_id VARCHAR2(50) PRIMARY KEY,
first_name VARCHAR2(100) NOT NULL,
last_name VARCHAR2(100) NOT NULL,
email VARCHAR2(150) UNIQUE NOT NULL,
hashed_password VARCHAR2(255) NOT NULL,
pin_hash VARCHAR2(255) NOT NULL,
role VARCHAR2(20) CHECK (role IN ('CUSTOMER', 'MANAGER')) NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CUSTOMERS (inherits from users)
CREATE TABLE customers (
user_id VARCHAR2(50) PRIMARY KEY,
phone_number VARCHAR2(20),
FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- BANK MANAGERS (inherits from users)
CREATE TABLE bank_managers (
user_id VARCHAR2(50) PRIMARY KEY,
FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- ACCOUNTS (shared by all account types)
CREATE TABLE accounts (
account_id VARCHAR2(50) PRIMARY KEY,
customer_id VARCHAR2(50) NOT NULL,
account_type VARCHAR2(30) NOT NULL CHECK (account_type IN (
'CHECKING', 'SAVINGS', 'CREDIT', 'CURRENCY', 'LINE_OF_CREDIT'
)),
opened_date DATE NOT NULL,
is_active NUMBER(1) DEFAULT 1 CHECK (is_active IN (0, 1)),
available_balance NUMBER(18, 2) DEFAULT 0,
credit_limit NUMBER(18, 2),
FOREIGN KEY (customer_id) REFERENCES customers(user_id)
);

-- TRANSACTIONS (linked to 1 or 2 accounts)
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
FOREIGN KEY (source_account_id) REFERENCES accounts(account_id),
FOREIGN KEY (destination_account_id) REFERENCES accounts(account_id)
);

-- NOTIFICATIONS (user inbox system)
CREATE TABLE notifications (
notification_id VARCHAR2(50) PRIMARY KEY,
title VARCHAR2(255) NOT NULL,
recipient_user_id VARCHAR2(50) NOT NULL, -- NEW: recipient user ID (customer or manager)
account_id VARCHAR2(50), -- nullable
message VARCHAR2(500),
type VARCHAR2(30),
seen NUMBER(1) DEFAULT 0 CHECK (seen IN (0, 1)),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (recipient_user_id) REFERENCES users(user_id),
FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);

