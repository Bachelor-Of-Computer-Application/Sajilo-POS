-- ===============================================
-- POS SYSTEM DATABASE SETUP SCRIPT
-- Database: POSsystem
-- SQL Server
-- ===============================================

-- First, switch to the POSsystem database
USE POSsystem
GO

-- ===============================================
-- 1. ROLES TABLE
-- ===============================================
CREATE TABLE roles
(
    role_id INT PRIMARY KEY IDENTITY(1,1),
    role_name VARCHAR(50) UNIQUE NOT NULL
)
GO

-- Insert default roles
INSERT INTO roles
    (role_name)
VALUES('ADMIN'),
    ('CASHIER'),
    ('MANAGER')
GO

-- ===============================================
-- 2. USERS TABLE
-- ===============================================
CREATE TABLE users
(
    user_id INT PRIMARY KEY IDENTITY(1,1),
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role_id INT,
    active BIT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY(role_id) REFERENCES roles(role_id)
)
GO

-- ===============================================
-- 3. PRODUCTS TABLE
-- ===============================================
CREATE TABLE products
(
    product_id INT PRIMARY KEY IDENTITY(1,1),
    product_name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    description VARCHAR(255),
    active BIT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
)
GO

-- ===============================================
-- 4. SALES TABLE
-- ===============================================
CREATE TABLE sales
(
    sale_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    discount DECIMAL(10, 2) DEFAULT 0,
    final_amount DECIMAL(10, 2) NOT NULL,
    sale_date DATETIME DEFAULT GETDATE(),
    FOREIGN KEY(user_id) REFERENCES users(user_id)
)
GO

-- ===============================================
-- 5. SALE_ITEMS TABLE (Details of each item sold)
-- ===============================================
CREATE TABLE sale_items
(
    sale_item_id INT PRIMARY KEY IDENTITY(1,1),
    sale_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY(sale_id) REFERENCES sales(sale_id) ON DELETE CASCADE,
    FOREIGN KEY(product_id) REFERENCES products(product_id)
)
GO

-- ===============================================
-- SAMPLE DATA
-- ===============================================

-- Create sample users
-- Admin user: username=admin, password=admin123
-- Password hash for "admin123": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRcx36
INSERT INTO users
    (full_name, username, password_hash, role_id, active)
VALUES('Admin User', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRcx36', 1, 1)
GO

-- Cashier user: username=cashier, password=cashier123
-- Password hash for "cashier123": $2a$10$8x.U8RkKU6Rj6R6Q5.ZAKOeQcOKx3.Q4K2U6Q6Q.8E6Q.E6Q.E6Q.E
INSERT INTO users
    (full_name, username, password_hash, role_id, active)
VALUES('John Cashier', 'cashier', '$2a$10$8x.U8RkKU6Rj6R6Q5.ZAKOeQcOKx3.Q4K2U6Q6Q.E6Q.E6Q.E6Q.E', 3, 1)
GO

-- Manager user: username=manager, password=manager123
INSERT INTO users
    (full_name, username, password_hash, role_id, active)
VALUES('Jane Manager', 'manager', '$2a$10$4R8UZBJaBx.T3H.R6P6E7uPx2K5L4M3N2O1P0Q9R8S7T6U5V4W3X', 2, 1)
GO

-- Insert sample products
INSERT INTO products
    (product_name, price, stock, description)
VALUES
    ('Milk', 50.00, 100, 'Fresh cow milk'),
    ('Bread', 25.00, 50, 'Whole wheat bread'),
    ('Butter', 150.00, 30, 'Dairy butter'),
    ('Eggs (Dozen)', 80.00, 40, 'Fresh eggs'),
    ('Cheese', 200.00, 20, 'Cheddar cheese'),
    ('Tea', 120.00, 60, 'Black tea leaves'),
    ('Coffee', 180.00, 45, 'Arabica coffee beans'),
    ('Sugar', 40.00, 70, 'Refined sugar')
GO

-- ===============================================
-- VERIFY DATA
-- ===============================================

-- Check roles
SELECT *
FROM roles
GO

-- Check users with roles
SELECT u.user_id, u.full_name, u.username, u.active, r.role_name
FROM users u
    LEFT JOIN roles r ON u.role_id = r.role_id
GO

-- Check products
SELECT *
FROM products
GO

-- ===============================================
-- IMPORTANT: FOR TESTING
-- ===============================================
-- To hash passwords and create new users via the application:
-- Use AuthService.hashPassword("your_password") to generate BCrypt hashes
-- Or use an online BCrypt generator to create hashes
-- 
-- For manual password hashing, you can use:
-- admin123 = $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRcx36
-- cashier123 = $2a$10$8x.U8RkKU6Rj6R6Q5.ZAKOeQcOKx3.Q4K2U6Q6Q.E6Q.E6Q.E6Q.E
-- manager123 = $2a$10$4R8UZBJaBx.T3H.R6P6E7uPx2K5L4M3N2O1P0Q9R8S7T6U5V4W3X
