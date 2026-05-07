CREATE DATABASE IF NOT EXISTS auction_db;
USE auction_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL -- ADMIN, SELLER, BIDDER
);

CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL DEFAULT 'GENERAL', -- ELECTRONICS, ART, VEHICLE
    starting_price DOUBLE NOT NULL,
    current_price DOUBLE NOT NULL,
    seller_name VARCHAR(50) NOT NULL,
    owner_name VARCHAR(50),
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' -- PENDING, APPROVED, OPEN, FINISHED, PAID, CANCELED
);

CREATE TABLE IF NOT EXISTS bid_transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    username VARCHAR(50) NOT NULL,
    bid_amount DOUBLE NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS auto_bids (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    username VARCHAR(50) NOT NULL,
    max_bid DOUBLE NOT NULL,
    increment DOUBLE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(product_id, username),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

-- Dữ liệu mẫu (Tùy chọn)
INSERT IGNORE INTO users (username, password, role) VALUES ('admin', '123456', 'ADMIN');
INSERT IGNORE INTO users (username, password, role) VALUES ('seller', '123', 'SELLER');
INSERT IGNORE INTO users (username, password, role) VALUES ('user1', '123', 'BIDDER');
INSERT IGNORE INTO users (username, password, role) VALUES ('user2', '123', 'BIDDER');
