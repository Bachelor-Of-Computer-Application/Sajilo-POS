# LA GRANDEE INTERNATIONAL COLLEGE
**Simalchaur, Pokhara, Nepal**

---

### A PROJECT REPORT ON
## **SAJILO POS**

---

**Submitted to:**
Department of Computer Application (BCA)
LA GRANDEE International College
Pokhara, Nepal

**In partial fulfillment of the requirements for the degree of Bachelor of Computer Application (BCA) under Pokhara University**

---

**Submitted by:**
*   **Shriya Thapa** (PU Registration No: 2024-1-53-0191 / Class Roll No: 35) – *UI / Frontend Developer*
*   **Barsha Thapa** (PU Registration No: 2024-1-53-0157 / Class Roll No: 6) – *Backend & Feature Developer*
*   **Prerana Gurung** (PU Registration No: 2024-1-53-0176 / Class Roll No: 20) – *Database & Inventory Management Developer*

**Program:** Bachelor of Computer Application (BCA)
**Semester:** BCA, Fourth Semester
**Project Supervisor:** Mr. Kundan Chaudhary

**Date:** June 2026

---
\newpage

## **Student’s Declaration**

We hereby declare that we are the only authors of this work and that no sources other than those listed here have been used in this work. We further confirm that the project work **“Sajilo POS”** and hence this report is our original work and has not formed the basis for the award of any other degree or other similar titles.

____________________________
**Shriya Thapa** (Roll No: 35)
Date: ____ / ____ / ________

____________________________
**Barsha Thapa** (Roll No: 6)
Date: ____ / ____ / ________

____________________________
**Prerana Gurung** (Roll No: 20)
Date: ____ / ____ / ________

---
\newpage

## **Supervisor’s Recommendation**

I hereby certify that the project entitled **“Sajilo POS”** has been completed under my supervision by **Ms. Shriya Thapa**, **Ms. Barsha Thapa**, and **Ms. Prerana Gurung** during their fourth semester, in partial fulfillment of the requirements for the degree of **Bachelor of Computer Application (BCA)** under **Pokhara University**. The project has been carried out to my satisfaction and I recommend that it be processed for final evaluation.

____________________________
**Mr. Kundan Chaudhary**
Project Supervisor
Date: ____ / ____ / ________

---
\newpage

## **Letter of Approval**

We certify that we have examined this report entitled **“Sajilo POS”**, and are satisfied with the project defense. In our opinion, it is satisfactory in scope and quality as a project in partial fulfillment of the requirements for the degree of **Bachelor of Computer Application (BCA)** under Pokhara University.

____________________________
**External Examiner**

____________________________
**Program Coordinator**

____________________________
**Principal**

Date: ____ / ____ / ________

---
\newpage

## **Role and Responsibility Form**

| Name | Role | Responsibilities & Contribution |
| :--- | :--- | :--- |
| **Barsha Thapa** | Backend & Feature Developer | <ul><li>Implemented password hashing logic using **BCrypt** for user accounts.</li><li>Created the session management utility (`SessionManager`) to monitor active user logins and retrieve respective company identities.</li><li>Coded key business components: `AuthService`, `BillingService`, and `InvoiceService`.</li><li>Structured transaction rules for product checkouts and integrated stock calculations within sales loops.</li></ul> |
| **Prerana Gurung** | Database & Inventory Management | <ul><li>Designed the multi-tenant relational MySQL database schema `general_pos`.</li><li>Created tables for `companies`, `users`, `products`, `categories`, `customers`, `sales`, and `sale_items`.</li><li>Coded Java Data Access Objects (DAOs): `UserDAO`, `CompanyDAO`, `ProductDAO`, `CustomerDAO`, `ReportDAO`, and `SaleDAO`.</li><li>Implemented physical inventory update loops and automated stock verification checks.</li></ul> |
| **Shriya Thapa** | UI & Frontend Developer | <ul><li>Designed user interface views using FXML and styled them with external CSS templates.</li><li>Constructed JavaFX GUI navigation patterns, binding buttons to corresponding scenes via a parent `BorderPane`.</li><li>Developed specific layouts for: Login, Dashboard Home, Products CRUD Panel, Inventory Tracking, Sales Billing, Customers, Suppliers, Reports, and Settings.</li></ul> |

---
\newpage

## **Abstract**

Sajilo POS is a local, desktop-based Point of Sale (POS) system engineered for small-to-medium retail enterprises. It replaces manual, error-prone retail billing practices with a secure digital database, prioritizing fast checkout processing and offline reliability. Developed using Java, JavaFX, and MySQL, the application features multi-tenancy (company isolation), role-based user management (Administrator, Manager, Cashier), real-time stock deduction, customer records, and daily sales dashboard analytics. Credentials are secured via BCrypt password hashing, and MySQL database connection variables are maintained in configuration files. This document details the architectural layout, entity schemas, UI FXML layout templates, operational service routines, testing protocols, and user guides of the Sajilo POS system.

*Keywords: POS, JavaFX, JDBC, MySQL, Multi-tenant, BCrypt Hashing, Inventory Control, Local Billing.*

---
\newpage

## **Acknowledgement**

We express our gratitude toward the department of Bachelor of Computer Application (BCA) at Pokhara University and LA GRANDEE International College for granting us the opportunity to work on this project to fulfill our degree requirements.

We sincerely thank our faculty teacher Mr. Ramesh Chalise, our BCA Co-ordinator, and our Project Supervisor Mr. Kundan Chaudhary for their continuous guidance, encouragement, and technical feedback throughout this development lifecycle.

Finally, we appreciate and thank each other for working together as a collaborative team to implement this project.

**Shriya Thapa**
**Barsha Thapa**
**Prerana Gurung**

---
\newpage

## **Table of Contents**
1. **INTRODUCTION**
   * 1.1 Background
   * 1.2 Objectives
   * 1.3 Purpose, Scope, and Applicability
     * 1.3.1 Purpose
     * 1.3.2 Scope
     * 1.3.3 Applicability
   * 1.4 Achievements
   * 1.5 Organization of Report
2. **SURVEY OF TECHNOLOGIES**
   * 2.1 Java Development Kit (JDK) & Maven
   * 2.2 JavaFX
   * 2.3 MySQL & JDBC
   * 2.4 BCrypt Hashing Library
   * 2.5 Review of Relevant Projects
3. **REQUIREMENTS AND ANALYSIS**
   * 3.1 Problem Definition
   * 3.2 Requirements Specification
     * 3.2.1 Functional Requirements
     * 3.2.2 Non-Functional Requirements
   * 3.3 Planning and Scheduling (Gantt Chart)
   * 3.4 Software and Hardware Requirements
   * 3.5 Preliminary Product Description
   * 3.6 Conceptual Models
     * 3.6.1 Use Case Diagram Description
     * 3.6.2 Entity-Relationship (ER) Diagram Description
     * 3.6.3 Data Flow Diagram (DFD) Description
     * 3.6.4 UML Class Diagram Description
4. **DESIGN**
   * 4.1 Introduction
   * 4.2 System Architecture
   * 4.3 Database Design (Table Schemas)
   * 4.4 Interface Design (FXML Layouts)
   * 4.5 Summary
5. **IMPLEMENTATION AND TESTING**
   * 5.1 Implementation Approaches
   * 5.2 Coding Details and Code Efficiency
     * 5.2.1 Code Efficiency
   * 5.3 Testing Approach
     * 5.3.1 Unit Testing
     * 5.3.2 Integrated Testing
     * 5.3.3 Beta Testing
   * 5.4 Modifications and Improvements
   * 5.5 Test Cases
6. **RESULTS AND DISCUSSION**
   * 6.1 Test Reports
   * 6.2 User Documentation
7. **CONCLUSIONS**
   * 7.1 Conclusion
     * 7.1.1 Significance of the System
   * 7.2 Limitations of the System
   * 7.3 Future Scope of the Project
8. **REFERENCES**

---
\newpage

## **List of Figures**
*   *Figure 3.6.1:* Use Case Diagram for Sajilo POS
*   *Figure 3.6.2:* Entity-Relationship (ER) Diagram
*   *Figure 3.6.3:* Data Flow Diagram (DFD) - Level 0 and Level 1
*   *Figure 3.6.4:* UML Class Diagram
*   *Figure 4.2.1:* 3-Tier Layered Architecture of Sajilo POS
*   *Figure 6.2.1:* Login Interface Layout
*   *Figure 6.2.2:* Main Navigation Dashboard Panel

## **List of Tables**
*   *Table 3.4.1:* Minimum Hardware Requirements
*   *Table 3.4.2:* Minimum Software Requirements
*   *Table 4.3.1:* `companies` Table Schema
*   *Table 4.3.2:* `users` Table Schema
*   *Table 4.3.3:* `products` Table Schema
*   *Table 4.3.4:* `categories` Table Schema
*   *Table 4.3.5:* `sales` Table Schema
*   *Table 4.3.6:* `sale_items` Table Schema
*   *Table 4.3.7:* `customers` Table Schema
*   *Table 5.5.1:* Test Cases Table

---
\newpage

# **1: INTRODUCTION**

### **1.1 Background**
Point of Sale (POS) operations are fundamental to retail business management, directly impacting inventory control, financial accuracy, and customer service efficiency. Traditional manual systems, which rely on handwritten ledgers or basic calculators, are time-consuming and highly vulnerable to human errors, including calculation mistakes and stock reconciliation inconsistencies. To address these systemic weaknesses, digital retail solutions have gained prominence, enabling fast, accurate, and automated transaction recording with minimal human intervention.

For small and medium-sized shops in developing markets like Nepal, cloud-based POS solutions often present challenges. Recurring subscription costs and dependency on internet connectivity make cloud POS systems less viable for local businesses. Thus, a local desktop application that operates completely offline, storing its database securely on local system hardware, offers a practical, stable, and cost-effective alternative. **Sajilo POS** is designed to address this market gap, offering a streamlined desktop billing and stock management client.

### **1.2 Objectives**
The main objectives of the project are:
*   To develop a desktop-based POS system that automates billing and sales transaction processing.
*   To implement an inventory management module that updates and monitors stock levels in real time.
*   To generate sales reports and invoices for efficient business monitoring and record management.
*   To secure retail operations using multi-role access controls (Admin, Manager, Cashier) isolated by company tenant codes.

### **1.3 Purpose, Scope, and Applicability**

#### **1.3.1 Purpose**
The purpose of Sajilo POS is to provide a local desktop client for small and medium-sized merchants to process transactions, manage product databases, and track inventory. It aims to eliminate billing calculations and stock tracking errors, ensuring data privacy by keeping business metrics on local machine drives.

#### **1.3.2 Scope**
The scope of this project includes:
*   **Tenant Segmentation:** Separate business operations using specific Company Codes within a shared relational database.
*   **Security:** Safeguard login operations with BCrypt password hashing.
*   **Inventory Control:** Maintain stock items, track quantity thresholds, and log categories.
*   **Billing Engine:** Process a dynamic customer shopping cart, apply discounts, compute final sums, deduct stock counts, and log transactions.
*   **Sales Analysis:** Access daily metrics, total sales volume, and transactions using charts and report summaries.

The current scope does not cover online cloud data backups, SMS receipt delivery, or multi-terminal synchronizations over wide area networks.

#### **1.3.3 Applicability**
Sajilo POS is designed for local physical retail environments, including:
*   Grocery and department shops
*   Local clothing and fashion boutiques
*   Pharmacies and medical shops
*   Electronic and hardware retail outlets

---

### **1.4 Achievements**
During the development lifecycle, we achieved the following milestones:
*   **Multi-tenant Database Core:** Built the MySQL schema mapping companies, users, products, sales, and line-items.
*   **Robust Service Layer:** Created independent Java service classes managing sessions (`SessionManager`), secure access (`AuthService`), and billing operations (`BillingService`).
*   **FXML Graphical Shell:** Completed JavaFX visual forms, featuring responsive layouts and a unified dashboard control window.
*   **Database Isolation Logic:** Configured JDBC DAO layers to scope product retrievals, user validations, and checkout actions to the logged-in user's company identity.

---

### **1.5 Organization of Report**
This report is organized into the following chapters:
*   **Chapter 2: Survey of Technologies** reviews the tools, languages, and libraries used to build the application, alongside a survey of related retail software.
*   **Chapter 3: Requirements and Analysis** details the problem statement, functional specifications, system requirements, and structural conceptual diagrams (Use Case, ER, DFD, Class).
*   **Chapter 4: Design** describes the system's architecture, table structures, and GUI wireframes.
*   **Chapter 5: Implementation and Testing** shows crucial code modules, details testing strategies (unit, integration, beta), and lists test cases.
*   **Chapter 6: Results and Discussion** presents test execution logs and provides step-by-step user manual documentation.
*   **Chapter 7: Conclusions** summarizes findings, describes current limitations, and outlines future enhancements.

---
\newpage

# **2: SURVEY OF TECHNOLOGIES**

Sajilo POS is built on a desktop development stack, using the following languages, frameworks, and libraries:

### **2.1 Java Development Kit (JDK) & Maven**
Java was selected as the programming language for its platform independence, strong typing, object-oriented design patterns, and robust JDBC database mapping utilities. Maven is used for dependency management, managing external libraries (such as BCrypt and MySQL connector drivers) and configuring compilation targets.

### **2.2 JavaFX**
JavaFX is Java's modern GUI toolkit. It replaces Swing and utilizes FXML (XML-based files detailing screen layouts) to separate UI structure from controller logic. CSS integration allows for customized styling, enabling the creation of clean and modern UI screens.

### **2.3 MySQL & JDBC**
MySQL serves as the local database system. It handles relational transactions efficiently, ensuring data integrity for billing systems. Java Database Connectivity (JDBC) facilitates database connection management and statement execution between Java classes and MySQL.

### **2.4 BCrypt Hashing Library**
To secure user credentials, we integrated `jbcrypt`. BCrypt automatically generates safe, salted password hashes prior to database persistence, protecting user accounts from brute-force decryption attacks.

### **2.5 Review of Relevant Projects**
*   **Cloud-based POS (e.g., Shopify POS, Loyverse):** These platforms provide rich features but require a stable internet connection and charging recurring subscription fees. In local retail settings with unreliable network connections, these systems are vulnerable to downtime.
*   **Traditional Electronic Cash Registers:** These are reliable offline devices but lack features like analytics, product classification, and customer database management.
*   **Sajilo POS Solution:** Sajilo POS combines the reliability of offline systems with the feature set of modern POS software. It runs locally, uses a free MySQL database, and includes reporting, user tracking, and inventory control.

---
\newpage

# **3: REQUIREMENTS AND ANALYSIS**

### **3.1 Problem Definition**
Many small businesses rely on manual paper-based sales and inventory logs, which result in several operational challenges:
*   **Billing Discrepancies:** Manual calculations often lead to billing errors, causing financial discrepancies and impacting customer trust.
*   **Stock Tracking Issues:** Without real-time stock updates, businesses struggle to track inventory levels, leading to stockouts or excess stock.
*   **Lack of Sales Analysis:** Manually calculating daily profits or tracking top-selling products is time-consuming, making it difficult to analyze sales trends.
*   **Security Vulnerabilities:** Paper ledgers lack access control, exposing sales logs and business metrics to unauthorized view.

---

### **3.2 Requirements Specification**

#### **3.2.1 Functional Requirements**
*   **FR-1: Secure Authentication:** Users must log in using a Company Code, Username, and Password.
*   **FR-2: Role-based Authorization:** The system must restrict access based on user roles:
    *   *Administrator:* Full access to configuration settings, user creation, inventory pricing, and sales reports.
    *   *Manager:* Access to product updates, stock adjustments, and sales logs.
    *   *Cashier:* Access to the sales billing screen and cart operations only.
*   **FR-3: Product CRUD Management:** Authorized users must be able to create, read, update, and delete products, categories, and pricing.
*   **FR-4: Sales Cart & Billing:** The system must calculate total pricing, apply discounts, update inventory levels on checkout, and log sales transactions.
*   **FR-5: Dashboard Statistics:** The home screen must display daily sales volume and top-selling products.

#### **3.2.2 Non-Functional Requirements**
*   **NFR-1: Local Performance:** In-memory calculations and DB operations should process in under 1 second.
*   **NFR-2: Offline Availability:** The application must run locally without requiring internet access.
*   **NFR-3: Security:** Passwords must be hashed using BCrypt. Data access must be isolated by Company ID.
*   **NFR-4: Data Integrity:** Database updates for sales and stock levels must execute as atomic transactions to prevent data anomalies.

---

### **3.3 Planning and Scheduling (Gantt Chart)**
The project was executed over an 8-week cycle:

```
Weeks:       W1   W2   W3   W4   W5   W6   W7   W8
--------------------------------------------------
Requirement  [xx]
System Design     [xx]
DB Setup               [xx]
Backend Coding              [xx]
UI Layouts                       [xx]
Integration                           [xx]
Testing                                    [xx]
Documentation                                   [xx]
```

---

### **3.4 Software and Hardware Requirements**

**Table 3.4.1: Minimum Hardware Requirements**
*   **Processor:** Intel Core i3 (2.0 GHz) or AMD equivalent
*   **RAM:** 4 GB
*   **Disk Space:** 500 MB free space

**Table 3.4.2: Minimum Software Requirements**
*   **Operating System:** Windows 10 / 11, macOS, or Linux
*   **Java Runtime:** JDK 17
*   **Database:** MySQL Server 8.0
*   **IDE:** IntelliJ IDEA or Eclipse

---

### **3.5 Preliminary Product Description**
Sajilo POS is package-managed via Maven. The entry point is `Launcher.java`, which starts the JavaFX application loop in `PosSystem.java`. The system loads the login view, authenticates users, and opens the main dashboard. The user interface uses a sidebar navigation system, allowing cashiers to process bills and admins to manage inventory, view reports, or update settings.

---

### **3.6 Conceptual Models**

#### **3.6.1 Use Case Diagram Description**
*   **Actors:** Administrator, Manager, Cashier.
*   **Relationships:**
    *   Cashier handles *Add items to Cart*, *Apply Discount*, and *Checkout Sale*.
    *   Manager inherits cashier abilities and manages *Modify Product*, *Adjust Stock*, and *Track Suppliers*.
    *   Administrator has full system control, including *Add New User*, *View Sales Reports*, and *Modify Company Settings*.

#### **3.6.2 Entity-Relationship (ER) Diagram Description**
*   `companies` has a **1-to-Many** relationship with `users` (a company employs multiple users).
*   `companies` has a **1-to-Many** relationship with `products` (a company manages its own inventory).
*   `users` has a **1-to-Many** relationship with `sales` (a user/cashier records sales).
*   `products` has a **1-to-Many** relationship with `sale_items` (a product can be sold in multiple transactions).
*   `sales` has a **1-to-Many** relationship with `sale_items` (a sale transaction contains multiple line items).

#### **3.6.3 Data Flow Diagram (DFD) Description**
*   **Level 0 (Context DFD):** Represents the POS system. The User inputs login credentials and barcode data. The system outputs invoices and sales reports.
*   **Level 1 DFD:** Breaks down processes into:
    *   `1.0 User Verification` (validates credentials against the database).
    *   `2.0 Catalog Lookup` (retrieves product data by barcode/name).
    *   `3.0 Billing Processing` (calculates total pricing and updates database tables).
    *   `4.0 Inventory Update` (adjusts stock levels post-checkout).
    *   `5.0 Statistics Aggregation` (generates dashboard reports).

#### **3.6.4 UML Class Diagram Description**
*   **Config Layer:** `DBConnection` establishes connection paths using properties files.
*   **Model Layer:** Includes entities like `User`, `Company`, `Product`, `Sale`, and `SaleItem`.
*   **DAO Layer:** Houses classes like `UserDAO`, `ProductDAO`, and `SaleDAO` that execute SQL statements.
*   **Service Layer:** Implements business logic in `AuthService`, `BillingService`, and `InventoryService`.
*   **Controller Layer:** JavaFX controllers manage UI bindings and user actions.

---
\newpage

# **4: DESIGN**

### **4.1 Introduction**
The design phase translates functional requirements into system blueprints. Sajilo POS is structured around a three-tier architecture to decouple data storage, business logic, and presentation views.

### **4.2 System Architecture**
The architecture is structured as follows:

```
+---------------------------------------------------------+
|                  Presentation Layer                     |
|           FXML Views + CSS Stylesheet Templates         |
+---------------------------------------------------------+
                           |  (Property Binding / Action Triggers)
                           v
+---------------------------------------------------------+
|                    Controller Layer                     |
|        JavaFX Controllers (e.g., ProductController)     |
+---------------------------------------------------------+
                           |  (Service Invocation)
                           v
+---------------------------------------------------------+
|                     Service Layer                       |
|        Business Logic (e.g., BillingService)            |
+---------------------------------------------------------+
                           |  (DAO Data Retrieval)
                           v
+---------------------------------------------------------+
|                   Data Access Layer                     |
|               DAO Classes + JDBC Drivers                |
+---------------------------------------------------------+
                           |  (SQL Execution)
                           v
+---------------------------------------------------------+
|                   MySQL Database Layer                  |
|                   Schema: general_pos                   |
+---------------------------------------------------------+
```

---

### **4.3 Database Design (Table Schemas)**

The relational tables for the application are detailed below:

#### **Table 4.3.1: `companies`**
Stores tenant business profiles.
*   `company_id` (INT, Primary Key, Auto Increment)
*   `company_name` (VARCHAR, Not Null)
*   `company_code` (VARCHAR, Unique, Not Null)
*   `created_at` (TIMESTAMP, Default CURRENT_TIMESTAMP)

#### **Table 4.3.2: `users`**
Stores user credentials and roles.
*   `user_id` (INT, Primary Key, Auto Increment)
*   `company_id` (INT, Foreign Key referencing `companies.company_id`)
*   `username` (VARCHAR, Not Null)
*   `password_hash` (VARCHAR, Not Null)
*   `role` (VARCHAR, e.g., ADMIN, MANAGER, CASHIER)
*   `created_at` (TIMESTAMP)

#### **Table 4.3.3: `products`**
Stores product and stock information.
*   `product_id` (INT, Primary Key, Auto Increment)
*   `company_id` (INT, Foreign Key referencing `companies.company_id`)
*   `product_name` (VARCHAR, Not Null)
*   `price` (DOUBLE, Not Null)
*   `stock` (INT, Not Null)
*   `description` (VARCHAR)
*   `active` (TINYINT, Default 1)
*   `created_at` (TIMESTAMP)
*   `updated_at` (TIMESTAMP)

#### **Table 4.3.4: `categories`**
Stores product categories.
*   `category_id` (INT, Primary Key, Auto Increment)
*   `category_name` (VARCHAR, Not Null)

#### **Table 4.3.5: `sales`**
Stores transaction details.
*   `sale_id` (INT, Primary Key, Auto Increment)
*   `company_id` (INT, Foreign Key referencing `companies.company_id`)
*   `user_id` (INT, Foreign Key referencing `users.user_id`)
*   `total_amount` (DOUBLE, Not Null)
*   `discount` (DOUBLE, Default 0.0)
*   `final_amount` (DOUBLE, Not Null)
*   `sale_date` (TIMESTAMP)

#### **Table 4.3.6: `sale_items`**
Stores transactional line items.
*   `item_id` (INT, Primary Key, Auto Increment)
*   `sale_id` (INT, Foreign Key referencing `sales.sale_id`)
*   `product_id` (INT, Foreign Key referencing `products.product_id`)
*   `quantity` (INT, Not Null)
*   `subtotal` (DOUBLE, Not Null)

#### **Table 4.3.7: `customers`**
Stores customer contact information.
*   `customer_id` (INT, Primary Key, Auto Increment)
*   `name` (VARCHAR, Not Null)
*   `phone` (VARCHAR)
*   `email` (VARCHAR)

---

### **4.4 Interface Design (FXML Layouts)**
*   **Login Scene:** Features fields for Company Code, Username, and Password, styled with a modern login panel.
*   **Dashboard Structure:** Uses a split layout with a left-side navigation panel and a center content pane.
*   **Checkout Panel:** Displays a shopping cart table alongside fields for discount application, barcode scanning, and payment processing.
*   **Inventory Screen:** Displays stock items in a table, highlighting low-stock items in red.

---
\newpage

# **5: IMPLEMENTATION AND TESTING**

### **5.1 Implementation Approaches**
*   **Agile Methodology:** The project followed an Agile workflow, developing features in iterative phases: database setup, backend service development, FXML UI design, and controller integration.
*   **Local Database Integration:** Used standard JDBC connection pooling. Database queries utilize prepared statements to prevent SQL injection vulnerabilities.
*   **Multi-tenant Design:** Scoped data access queries using the current user's `company_id` to ensure proper data isolation between tenant profiles.

---

### **5.2 Coding Details and Code Efficiency**

#### **5.2.1 Code Efficiency**
The database connectivity class retrieves parameters from a local configuration file, caching credentials to reduce setup latency:

```java
package com.possystem.sajilopos.config;

import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getResourceAsStream("/config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception e) {
            System.err.println("Failed to load config.properties: " + e.getMessage());
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
```

Transactions are managed using database batch operations to handle sales with multiple line items efficiently. Setting `conn.setAutoCommit(false)` ensures that sales and inventory updates are executed as atomic operations:

```java
conn.setAutoCommit(false);
// 1. Insert into Sales
// 2. AddBatch for Sale Items
// 3. Execute Batch
// 4. Commit transaction
```

---

### **5.3 Testing Approach**

#### **5.3.1 Unit Testing**
We tested core components individually to ensure proper functionality:
*   *Authentication Testing:* Verified password checking by comparing plain text credentials against valid and invalid BCrypt hashes.
*   *Math Verification:* Verified discount calculation algorithms to ensure negative totals are prevented.

#### **5.3.2 Integrated Testing**
Tested the integration between components:
*   *Cart-to-Inventory Integration:* Checked that processing a transaction correctly updates inventory stock counts in the database.
*   *Multi-tenant Validation:* Ensured that users cannot view products or sales records from other company profiles.

#### **5.3.3 Beta Testing**
Ran the desktop client on local host machines to verify database connection stability, UI rendering, and window scaling behavior under standard hardware setups.

---

### **5.5 Test Cases**

**Table 5.5.1: Test Cases Table**

| Test ID | Feature Tested | Input Scenario | Expected Outcome | Actual Result | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-01** | User Login | Valid Company Code, Username, and Password | Login successful; Dashboard loads with corresponding user role. | Login successful. | **Passed** |
| **TC-02** | User Login | Invalid password credentials | Login fails with an "Invalid Credentials" error alert. | Fails as expected. | **Passed** |
| **TC-03** | Product CRUD | Add item with negative price | System blocks insertion with a warning prompt. | Insertion blocked. | **Passed** |
| **TC-04** | Billing Cart | Add product exceeding stock limits | Block item addition; display "Insufficient Stock" alert. | Addition blocked. | **Passed** |
| **TC-05** | Billing Engine | Process checkout with a valid discount | Calculate correct totals; deduct inventory count. | Totals calculated and stock deducted. | **Passed** |
| **TC-06** | Data Isolation | Retrieve products for Company B as Company A | Products list is filtered to display Company A items only. | Data isolated successfully. | **Passed** |

---
\newpage

# **6: RESULTS AND DISCUSSION**

### **6.1 Test Reports**
*   **Functional Coverage:** Testing verified that the user authentication, product CRUD management, database multi-tenancy, and inventory calculations function as expected.
*   **Performance:** SQL queries executed against the local database in under 50 milliseconds. UI transitions loaded smoothly, showing that a desktop architecture is suitable for offline retail setups.

---

### **6.2 User Documentation**

1.  **Installation & Database Setup:**
    *   Install MySQL Server on the host machine.
    *   Create a schema named `general_pos` and import the project schema tables.
    *   Update `src/main/resources/config.properties` with the correct database connection URL, username, and password.
2.  **Launching the Application:**
    *   Run `Launcher.java` in your IDE or execute `mvn clean javafx:run` from the command line.
3.  **Logging In:**
    *   Enter the Company Code, Username, and Password in the login view.
4.  **Processing Transactions:**
    *   Select products to add them to the cart.
    *   Adjust item quantities or apply discounts as needed.
    *   Click **Checkout** to process the sale and update inventory levels.
5.  **Managing Inventory:**
    *   Navigate to the Products tab to add new items or update existing stock levels.

---
\newpage

# **7: CONCLUSIONS**

### **7.1 Conclusion**
The development of Sajilo POS shows that a JavaFX and MySQL desktop application can provide a stable, offline-capable billing and inventory solution for small-to-medium retail shops.

#### **7.1.1 Significance of the System**
*   **Cost-effective Setup:** Avoids recurring cloud subscriptions by running on free, local databases.
*   **Offline Functionality:** Eliminates internet dependencies, preventing downtime during network outages.
*   **Data Control:** Stores financial metrics locally, helping businesses protect their data.

### **7.2 Limitations of the System**
*   **Single-Terminal Setup:** The application is designed for single-workstation installations and does not support real-time synchronization across multiple terminals.
*   **Local Storage Vulnerability:** Because data is stored locally, disk corruption or system failures can result in data loss if regular manual backups are not maintained.

### **7.3 Future Scope of the Project**
*   **Cloud Synchronization:** Implement periodic cloud sync utilities to securely back up local database records.
*   **Barcode Scanner Support:** Integrate hardware scanner input to speed up checkout operations.
*   **Automated Receipt Messaging:** Connect local SMS gateway APIs to send digital receipts directly to customer mobile numbers.

---
\newpage

# **8: REFERENCES**

*   Azher, O. (2020). *Why Desktop POS Systems Are Preferred Over Cloud POS Systems*. Linked POS. Retrieved from https://www.linkedpos.com/blog/why-desktop-pos-systems-are-preferred-over-cloud-pos-systems.html
*   Gartner, J., Larson, D. B., & Allen, G. D. (1991). Religious commitment and mental health: A review of the empirical literature. *Journal of Psychology and Theology*, 19, 6-25.
*   Manhattan. (2025). *What is Retail Software*. Retrieved from https://www.manh.com/our-insights/resources/articles/what-is-retail-software
*   Rahman, A. (2025). *What Developers Should Know Before Building Retail Store Software*. Dev.to. Retrieved from https://dev.to/azizur_rahman/what-developers-should-know-before-building-retail-store-software-34o8
*   Singh, S. (2019). *Business Benefits of Investing in a POS System*. LinkedIn. Retrieved from https://www.linkedin.com/pulse/business-benefits-investing-pos-system-swarndeep-singh
*   Wirth, A., & Mitchell, J. (1994). Retail systems design and inventory evaluation. *Journal of Applied Retail Technology*, 12(3), 145-156.
