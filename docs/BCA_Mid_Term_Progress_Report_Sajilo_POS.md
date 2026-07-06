# LA GRANDEE INTERNATIONAL COLLEGE
**Simalchaur, Pokhara, Nepal**

---

### A MID-TERM PROGRESS REPORT
ON
## **SAJILO POS**

---

**Submitted to:**
Department of Computer Application (BCA)
LA GRANDEE International College
Pokhara, Nepal

**In partial fulfillment of the requirements for the degree of Bachelor of Computer Application (BCA) under Pokhara University**

---

**Submitted by:**
*   **Shriya Thapa** (PU Registration No: 2024-1-53-0191) – *UI / Frontend Developer*
*   **Barsha Thapa** (PU Registration No: 2024-1-53-0157) – *Backend & Feature Developer*
*   **Prerana Gurung** (PU Registration No: 2024-1-53-0176) – *Database & Inventory Management Developer*

**Semester:** BCA, Fourth Semester
**Project Supervisor:** Mr. Kundan Chaudhary

**Date:** June 2026

---
\newpage

## **Table of Contents**
1. **Introduction & Project Overview**
2. **Project Objectives & Scope**
3. **Roles and Responsibilities**
4. **Work Completed (Current Progress)**
   * 4.1 Database & Storage Layer Progress
   * 4.2 Backend & Service Layer Progress
   * 4.3 Frontend & UI (JavaFX) Progress
5. **Proof of Concept & Code Snippets**
   * 5.1 Database Connection Utility (`DBConnection.java`)
   * 5.2 User Authentication and BCrypt Verification (`UserDAO.java` / `AuthService.java`)
   * 5.3 Billing Processing Logic (`BillingService.java`)
6. **Work Remaining (Next Steps)**
7. **Bottlenecks and Challenges**
8. **Conclusion & Future Plan**

---
\newpage

### **1. Introduction & Project Overview**
**Sajilo POS** is a desktop-based Point of Sale (POS) system designed specifically for small to medium-sized retail businesses. The primary objective is to replace the slow, error-prone manual paper-based invoicing and billing systems with a fast, offline-capable, secure, and user-friendly digital application. 

By operating locally, Sajilo POS eliminates dependency on active internet connections, which is a common issue for local retailers in Nepal. The system provides integrated functionalities for sales transaction recording, real-time inventory tracking, multi-tenant company separation (multi-tenancy on a single database), secure BCrypt user authentication, customer relationship management, and sales report generation. 

The software is being built using the **Java** programming language with **JavaFX** for its graphic presentation layer, and **MySQL** as its database management system, linked via **JDBC** (Java Database Connectivity) drivers.

---

### **2. Project Objectives & Scope**
The development goals for Sajilo POS are defined as follows:
*   **Automated Billing & Transaction Processing:** Perform quick billing calculations, calculate discounts, track items, and process transactions cleanly.
*   **Real-time Inventory Management:** Automatically deduct stock numbers upon completed checkouts and raise alerts for low-stock items.
*   **Secure Access Controls:** Multi-role system (Administrator, Manager, Cashier) with company code isolation so multiple retailers can be hosted securely.
*   **Local Reporting & Analytics:** Accumulate daily, weekly, and monthly sales transaction reports to calculate gross revenue.

---

### **3. Roles and Responsibilities**
Our project team is divided into three functional areas:
1.  **Barsha Thapa (Backend and Features):** 
    *   Designed and implemented core business logic, including the session management system, password hashing (BCrypt), and processing rules for sale transactions.
    *   Coded Java service layers (`AuthService`, `BillingService`, `ProductService`, `ReportService`, etc.) to bridge presentation and storage layers.
2.  **Prerana Gurung (Database with Inventory Management):**
    *   Designed the relational database schema in MySQL including tables, foreign keys, and referential integrity constraints.
    *   Implemented Data Access Objects (DAOs) like `ProductDAO`, `UserDAO`, `CompanyDAO`, and `SaleDAO` to execute secure SQL statements.
    *   Developed the inventory management module, automating stock calculation operations and low-stock alerts.
3.  **Shriya Thapa (UI or Frontend):**
    *   Designed user interface layouts using FXML and styled them using CSS stylesheets.
    *   Developed JavaFX screen flows, including the multi-tenant Login view, the master BorderPane Dashboard layout, and panels for Sales, Inventory, Customers, Reports, Settings, and User Management.

---

### **4. Work Completed (Current Progress)**
Since the approval of our project proposal, our development has progressed through database creation, backend data mapper construction, core business logic, and UI mockups:

#### **4.1 Database & Storage Layer Progress**
The MySQL schema `general_pos` has been successfully designed. The physical tables established include:
*   `companies`: Manages multiple tenant profiles, mapping unique company codes (e.g., GPS001) to specific entities.
*   `users`: Stores usernames, roles (Admin, Manager, Cashier), and password hashes using BCrypt encryption.
*   `categories`: Stores categorization tags for retail items.
*   `products`: Stores items, selling prices, real-time stock levels, and flags for active items.
*   `customers`: Maintains customer profiles with phone numbers and email addresses.
*   `sales` and `sale_items`: Stores transactional data, including unit price, quantities, sub-totals, discount rates, final totals, and timestamps.

#### **4.2 Backend & Service Layer Progress**
The core functionality is complete, including:
*   **Database Utilities (`DBConnection.java`, `Config.java`):** Connects JDBC to MySQL via external properties file settings (`config.properties`).
*   **Session Management (`SessionManager.java`):** Tracks currently logged-in users, roles, and company IDs globally during application runtime.
*   **Authentication & Hashing (`AuthService.java`, `UserDAO.java`):** Performs secure logins using company codes and user credentials.
*   **Billing Engine (`BillingService.java`, `SaleDAO.java`):** Manages a dynamic cart, performs price math, updates stock values upon sales checkout, and logs sales details in transactional SQL queries.

#### **4.3 Frontend & UI (JavaFX) Progress**
The visual layouts have been generated using FXML files:
*   **Login View (`login.fxml`):** Features input fields for Company Code, Username, and Password, directly invoking the `LoginController`.
*   **Dashboard (`dashboard.fxml`):** Uses a responsive sidebar configuration. Buttons load dynamic sub-scenes into the center layout pane.
*   **Modular Sub-Views:** Panels for Products, Inventory, Customers, Sales, and Reports are created and linked to the sidebar navigation.

---

### **5. Proof of Concept & Code Snippets**
The following code files highlight the current operational state of the codebase:

#### **5.1 Database Connection Utility (`DBConnection.java`)**
This utility connects the Java execution environment with our local MySQL server, pulling credentials from `config.properties`:

```java
package com.possystem.sajilopos.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() {
        try {
            String url = Config.get("db.url");
            String username = Config.get("db.username");
            String password = Config.get("db.password");

            if (url == null || url.trim().isEmpty()) {
                System.err.println("Database configuration missing in config.properties");
                return null;
            }

            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("MySQL database connection established successfully");
            return conn;
        } catch (SQLException e) {
            System.err.println("MySQL database connection failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
```

#### **5.2 User Authentication and BCrypt Verification (`UserDAO.java`)**
The authentication system secures data using BCrypt password hashing. The code below retrieves credentials based on user and company filters:

```java
public User login(String companyCode, String username, String password) {
    String sql = "SELECT u.user_id, u.company_id, u.username, u.password_hash, u.role, u.created_at " +
                 "FROM users u " +
                 "INNER JOIN companies c ON u.company_id = c.company_id " +
                 "WHERE c.company_code = ? AND u.username = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, companyCode);
        stmt.setString(2, username);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String storedHash = rs.getString("password_hash");

            // Verify password using BCrypt algorithm
            if (BCrypt.checkpw(password, storedHash)) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getInt("company_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
            }
        }
    } catch (SQLException e) {
        System.err.println("Error during login execution: " + e.getMessage());
    }
    return null;
}
```

#### **5.3 Billing Processing Logic (`BillingService.java`)**
This script manages cart items, processes checkout transactions, and deducts inventory quantities automatically:

```java
public boolean processSale(double discount) {
    if (currentItems.isEmpty()) {
        System.err.println("Cannot process empty bill.");
        return false;
    }
    try {
        User currentUser = sessionManager.getCurrentUser();
        int companyId = sessionManager.getCurrentCompanyId();
        Sale sale = new Sale(companyId, new ArrayList<>(currentItems), discount, currentUser.getUserId());

        // Save transactional records into database
        boolean saved = saleDAO.saveSale(sale);

        if (saved) {
            // Deduct stock values for each checkout product
            for (SaleItem item : currentItems) {
                int newStock = item.getProduct().getStock() - item.getQuantity();
                productDAO.updateStock(item.getProduct().getProductId(), newStock, companyId);
            }
            currentItems.clear(); 
            return true;
        }
    } catch (Exception e) {
        System.err.println("Error processing sale transaction: " + e.getMessage());
    }
    return false;
}
```

---

### **6. Work Remaining (Next Steps)**
The next stages of the development cycle include:
1.  **Fully Integrate GUI Controllers with Java Services:** Currently, files like `salescontroller.java` contain simple mock print statements. We need to hook these controls up to `BillingService` and `ProductService` so changes show in the UI.
2.  **Report Aggregation and Dashboard Graphing:** Hook the dashboard home panel and reporting interface charts directly to database aggregated metrics returned by `ReportDAO`.
3.  **Complete Invoice Printing Service:** Extend the functionality of `InvoiceService.java` to export text format or PDF receipts.
4.  **Database Seeding and Migration Utility:** Build a robust startup script to automatically seed default roles, initial administrator users, and company configurations for local installations.
5.  **Comprehensive Integration and Stress Testing:** Execute verification loops to ensure concurrent sales processes run without hitting database deadlocks.

---

### **7. Bottlenecks and Challenges**
The key development challenges currently being addressed are:
*   **JavaFX Component Binding:** Coordinating the live synchronization between database updates and JavaFX `TableView` elements can lead to thread synchronization challenges.
*   **Database Transaction Scope:** Ensuring that saving a sale and updating product inventory stock counts are bound within a single ACID transaction block so partial database failures rollback cleanly.
*   **Local Setup Configuration:** Resolving configuration issues when deploying the application on machines with different MySQL installation profiles (ports, passwords, and service variables).

---

### **8. Conclusion & Future Plan**
The development of Sajilo POS is progressing on schedule. The database design and backend services are fully functional, and UI layouts are complete. The remaining steps focus on integrating the user interface controllers with these backend services. Once integrated, the team will run comprehensive tests to prepare the system for the final demonstration.

---
**Prepared and Submitted by:**
*   Shriya Thapa (Frontend Developer)
*   Barsha Thapa (Backend Developer)
*   Prerana Gurung (Database Developer)
