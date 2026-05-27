# Sajilo POS System - Complete Setup & Documentation

## 📋 Overview

Sajilo POS is a **complete desktop Point-of-Sale system** built with JavaFX and Microsoft SQL Server. It provides:

- ✅ Secure user authentication with role-based access control
- ✅ Real-time inventory management
- ✅ Complete billing and sales processing
- ✅ Audit trail (who did what, when)
- ✅ Database-driven architecture

---

## 🚀 Quick Start (5 Minutes)

### Step 1: Run Database Setup

Execute this SQL script in SQL Server Management Studio:

```sql
-- Create login
CREATE LOGIN pos_user WITH PASSWORD = '1234';
GO

-- Create database
CREATE DATABASE POSsystem;
GO

-- Use database
USE POSsystem;
GO

-- Create user
CREATE USER pos_user FOR LOGIN pos_user;
GO

-- Grant permissions
ALTER ROLE db_owner ADD MEMBER pos_user;
GO
```

Then run: `src/main/resources/database_setup.sql`

### Step 2: Update Configuration

Edit `src/main/resources/config.properties`:

```properties
db.url=jdbc:sqlserver://localhost:1433;databaseName=POSsystem;encrypt=true;trustServerCertificate=true
db.username=pos_user
db.password=1234
```

### Step 3: Build & Run

```bash
mvn clean install
mvn javafx:run
```

### Step 4: Login

- **Username:** `admin`
- **Password:** `admin123`

---

## 📚 Documentation Files

### 1. **SYSTEM_ARCHITECTURE.md** (Recommended First Read)

Complete system design including:

- Database schema and relationships
- Application architecture and layers
- Complete flow diagrams
- Security implementation
- Error handling strategies
- Role-based permissions
- Technology stack

**When to read:** To understand how the system works end-to-end

### 2. **QUICK_START.md** (Setup & Troubleshooting)

Practical setup guide with:

- Step-by-step installation
- Prerequisites and version requirements
- Complete database setup script
- Configuration details
- Troubleshooting section
- Common issues and solutions
- Project structure

**When to read:** When setting up the system or encountering issues

### 3. **DEVELOPER_GUIDE.md** (Code Examples)

Comprehensive code examples for:

- Authentication and login
- Session management
- Product management
- Billing and sales
- Database operations (DAOs)
- Configuration management
- Complete transaction examples
- Error handling patterns
- Role-based access control

**When to read:** When developing features or integrating services

### 4. **database_setup.sql** (Database Schema)

Complete database initialization including:

- Table creation (roles, users, products, sales, sale_items)
- Foreign key relationships
- Default data (sample users and products)
- Indexes and constraints

**When to use:** First-time database setup

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────┐
│          JavaFX UI Layer                            │
│  (LoginController, MainController, BillingUI, etc)  │
└────────────────────┬────────────────────────────────┘
                     │
┌─────────────────────▼────────────────────────────────┐
│          Service Layer (Business Logic)             │
│  AuthService | BillingService | ProductService     │
└────────────────────┬────────────────────────────────┘
                     │
┌─────────────────────▼────────────────────────────────┐
│          Session Management                         │
│  SessionManager (Track current user & permissions)  │
└────────────────────┬────────────────────────────────┘
                     │
┌─────────────────────▼────────────────────────────────┐
│          Data Access Layer (DAOs)                   │
│  UserDAO | ProductDAO | SaleDAO                    │
└────────────────────┬────────────────────────────────┘
                     │
┌─────────────────────▼────────────────────────────────┐
│          Database Layer                             │
│  DBConnection → MSSQL Server                        │
│  POSsystem Database                                 │
└─────────────────────────────────────────────────────┘
```

---

## 🔐 User Roles & Permissions

| Feature          | ADMIN | MANAGER | CASHIER |
| ---------------- | ----- | ------- | ------- |
| Process Sales    | ✅    | ✅      | ✅      |
| View Products    | ✅    | ✅      | ✅      |
| Manage Users     | ✅    | ❌      | ❌      |
| View Reports     | ✅    | ✅      | ❌      |
| Manage Inventory | ✅    | ✅      | ❌      |
| System Config    | ✅    | ❌      | ❌      |

---

## 💾 Database Schema

### Core Tables

**roles** - User role definitions

```
role_id → role_name (ADMIN, MANAGER, CASHIER)
```

**users** - User accounts

```
user_id → full_name, username, password_hash, role_id, active
```

**products** - Inventory items

```
product_id → product_name, price, stock, description
```

**sales** - Sales transactions

```
sale_id → user_id, total_amount, discount, final_amount, sale_date
```

**sale_items** - Line items in each sale

```
sale_item_id → sale_id, product_id, quantity, subtotal
```

---

## 🔄 Complete Flow Example

```
1. USER STARTS APPLICATION
   ↓
   Load config.properties → Connect to database
   ↓
   Show Login Screen

2. USER LOGS IN
   ↓
   Username: "cashier" → Password: "cashier123"
   ↓
   AuthService validates credentials
   ↓
   SessionManager stores current user (John Cashier, ID=3, CASHIER)
   ↓
   Show Main POS Screen

3. CASHIER CREATES BILL
   ↓
   Select Product: Milk (Product ID: 1)
   Enter Quantity: 2
   ↓
   BillingService.addItem(1, 2)
   ↓
   Check stock: YES (100 available)
   ↓
   Add to in-memory bill: [Milk x2 @ $50 = $100]

4. CONTINUE ADDING ITEMS
   ↓
   Add Bread x1 = $25
   Add Butter x3 = $450
   ↓
   Total: $575

5. APPLY DISCOUNT & PROCESS SALE
   ↓
   Discount: $50
   Final: $525
   ↓
   BillingService.processSale(50)
   ↓
   Create Sale object:
   - user_id = 3 (John Cashier from SessionManager)
   - total_amount = 575
   - discount = 50
   - final_amount = 525
   - sale_date = NOW()

6. SAVE TO DATABASE
   ↓
   SaleDAO.saveSale(sale)
   ↓
   BEGIN TRANSACTION
   ├─ INSERT into sales table → sale_id = 1
   ├─ INSERT 3 rows into sale_items (Milk, Bread, Butter)
   ├─ UPDATE products: Milk stock: 100→98, Bread: 50→49, Butter: 30→27
   └─ COMMIT TRANSACTION
   ↓
   Bill cleared, ready for next customer

7. DATABASE STATE
   ↓
   sales table: 1 new row
   sale_items: 3 new rows
   products: stock updated
   audit_trail: All changes recorded with user_id=3
```

---

## 🛠️ Technology Stack

| Component        | Technology           | Version |
| ---------------- | -------------------- | ------- |
| Language         | Java                 | 17+     |
| UI Framework     | JavaFX               | 21.0.2  |
| Database         | Microsoft SQL Server | 2019+   |
| JDBC Driver      | mssql-jdbc           | 12.6.3  |
| Build Tool       | Maven                | 3.6+    |
| Password Hashing | BCrypt               | 0.4     |

---

## 📝 Key Features Implemented

### ✅ Authentication System

- Secure BCrypt password hashing (one-way encryption)
- User validation against database
- Session management with singleton pattern
- Role-based access control

### ✅ Billing System

- Add/remove items to cart
- Real-time stock validation
- Automatic discount calculation
- Transaction-safe database saves
- Audit trail with user tracking

### ✅ Inventory Management

- View all products
- Track stock levels
- Automatic stock updates on sales
- Product search capabilities

### ✅ User Management

- Create new user accounts
- Manage roles and permissions
- Track user activity
- Enable/disable user accounts

### ✅ Reporting

- Sales by date
- Sales by user
- Inventory reports
- Discount tracking

---

## ⚙️ Configuration

### config.properties

```properties
# MSSQL Connection
db.url=jdbc:sqlserver://localhost:1433;databaseName=POSsystem;encrypt=true;trustServerCertificate=true
db.username=pos_user
db.password=1234
```

**Required:**

- SQL Server running at specified host:port
- Database POSsystem exists
- User pos_user has db_owner role

---

## 🚨 Common Issues & Solutions

### "Cannot connect to database"

```
1. Check SQL Server is running: services.msc
2. Verify config.properties has correct host:port
3. Ensure database POSsystem exists
4. Verify pos_user login created
```

### "Invalid username or password" at login

```
1. Check user exists in database: SELECT * FROM users;
2. Run database_setup.sql to create default users
3. Verify password is correct (case-sensitive)
```

### "Maven dependency not found"

```
mvn clean install -U
```

### "JavaFX not found" in IDE

```
Configure IDE to add JavaFX SDK library
```

---

## 📦 Project Structure

```
Sajilo-POS/
├── src/main/java/com/possystem/sajilopos/
│   ├── config/              # Configuration & DB
│   │   ├── Config.java
│   │   ├── DBConnection.java
│   │   └── SessionManager.java
│   ├── model/               # Data Models
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── Sale.java
│   │   ├── SaleItem.java
│   │   └── Role.java
│   ├── dao/                 # Database Access
│   │   ├── UserDAO.java
│   │   ├── ProductDAO.java
│   │   └── SaleDAO.java
│   ├── service/             # Business Logic
│   │   ├── AuthService.java
│   │   └── BillingService.java
│   └── controller/          # UI Controllers
│       └── LoginController.java
├── src/main/resources/
│   ├── config.properties
│   ├── database_setup.sql
│   └── fxml/
│       └── login.fxml
├── pom.xml
├── SYSTEM_ARCHITECTURE.md   # System design
├── QUICK_START.md          # Setup guide
├── DEVELOPER_GUIDE.md      # Code examples
└── README.md               # This file
```

---

## 🔐 Security Best Practices

1. **Password Security**
   - All passwords hashed with BCrypt (one-way)
   - Never store plain-text passwords
   - Salt automatically included

2. **Session Management**
   - SessionManager singleton ensures single user
   - Session cleared on logout
   - Each transaction linked to user

3. **Database Access**
   - Parameterized queries (prevent SQL injection)
   - Connection pooling
   - Transaction management for data consistency

4. **Role-Based Access**
   - Each user has specific role
   - Permissions enforced per role
   - Admin approval for sensitive operations

---

## 📖 Documentation Navigation

**I want to...** → **Read this file:**

- Understand system design → `SYSTEM_ARCHITECTURE.md`
- Set up the system → `QUICK_START.md`
- Write code using the system → `DEVELOPER_GUIDE.md`
- Create database → `database_setup.sql`
- Understand complete flow → `SYSTEM_ARCHITECTURE.md` (Section: Complete Flow Example)
- Troubleshoot issues → `QUICK_START.md` (Section: Troubleshooting)
- See code examples → `DEVELOPER_GUIDE.md`

---

## ✨ Next Steps

1. ✅ Read `SYSTEM_ARCHITECTURE.md` to understand the design
2. ✅ Follow `QUICK_START.md` to set up the system
3. ✅ Log in and test the application
4. ✅ Read `DEVELOPER_GUIDE.md` to extend the system
5. ✅ Deploy to production

---

## 💡 Pro Tips

### For Developers

- Use `DEVELOPER_GUIDE.md` as reference for all services
- Check `SessionManager.getInstance()` to always get current user
- Use `AuthService` for all authentication needs
- Review error handling in each DAO class

### For DBAs

- Regular backups of `POSsystem` database
- Monitor sales table growth
- Archive old sales data periodically
- Update product prices and stock regularly

### For Admins

- Change default passwords after setup
- Create user accounts for each staff member
- Review sales reports daily
- Keep database secure with firewalls

---

## 🆘 Need Help?

### Common Questions

**Q: How do I create a new user?**
A: Use AuthService.hashPassword() to hash password, then UserDAO.createUser() to save

**Q: How do I check if user is admin?**
A: Use SessionManager.getInstance().isAdmin()

**Q: How do I process a sale?**
A: Use BillingService: addItem() → processSale()

**Q: How do I update product stock?**
A: Use ProductDAO.updateStock() (usually automatic after sale)

**Q: Where is the current user stored?**
A: SessionManager.getInstance().getCurrentUser()

---

## 📞 Support

For issues, refer to:

- `QUICK_START.md` - Troubleshooting section
- `SYSTEM_ARCHITECTURE.md` - Error handling section
- `DEVELOPER_GUIDE.md` - Error handling patterns
- Source code comments and JavaDoc

---

## 📄 License

This project is provided as-is for educational and commercial use.

---

## 🎉 You're All Set!

The complete POS system is now ready to use. Start with:

1. `QUICK_START.md` - Setup instructions
2. `SYSTEM_ARCHITECTURE.md` - Understand the design
3. Run the application and login
4. `DEVELOPER_GUIDE.md` - Extend as needed

Happy coding! 🚀
