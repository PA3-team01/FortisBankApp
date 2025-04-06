# FortisBank System

FortisBank is a robust, modular, enterprise-grade banking management system developed in Java. Designed to emulate real-world banking operations, it supports multiple user roles, secure financial transactions, automation, and administrative functions with a sleek dark-themed Swing UI.

---

## 🏗️ Architecture Overview

FortisBank adheres to a layered architecture embracing **clean code principles**, **separation of concerns**, and **resource modularity**:

- **Models** (`com.fortisbank.models`) – Define core domain objects: users, accounts, transactions, reports.
- **Repositories** (`com.fortisbank.data.repositories`) – Abstract storage logic via **Strategy pattern** for file-based and future database backends.
- **Services** (`com.fortisbank.business.services`) – Contain business logic with dependency injection, responsible for user management, transactions, and automation.
- **UI** (`com.fortisbank.ui`) – Built with Swing, featuring responsive components, panels, forms, and custom-styled dialogs.
- **Utilities** (`com.fortisbank.utils`, `com.fortisbank.ui.uiUtils`) – Provide encryption, validation, logging, styling, and formatting utilities.

The system leverages the **Factory Pattern** (`RepositoryFactory`, `AccountFactory`, etc.) and **Singleton Pattern** (e.g., `CustomerService`, `AccountService`) for optimized object lifecycle and resource control.

---

## 👤 User Roles & Functionalities

### 🔑 Login & Role-Based Access
- Secure role-based access using the `Role` enum (`MANAGER`, `CUSTOMER`)
- Credentials hashed with salt via `PBKDF2WithHmacSHA256` in `SecurityUtils`
- User credentials never stored or compared in plaintext

### 🧾 Account Management
- Supports multiple account types: checking, savings, credit, currency
- Interest configuration available for applicable accounts
- Modular UI panels for searching, filtering, creating, and updating accounts

### 🔁 Transactions
- Deposit, withdrawal, transfer, and fee operations
- Contextual validation ensures account compatibility and sufficient funds
- Transparent transaction history with real-time updates per account

---

## 🧠 Automation with Daemon Threading

FortisBank introduces intelligent background automation using a **custom `DaemonThread`** and the `AutomationService`. These non-blocking threads simulate real-world recurring events and offload routine tasks from the UI thread.

**Automated features include:**
- Monthly interest application
- Fee deductions
- Balance scanning for reporting or alerting

This approach ensures minimal performance overhead and adheres to **best concurrency practices**, maintaining UI responsiveness. The use of **thread lifecycle control**, exception safety, and system hooks allows graceful startup and shutdown.

---

## 📢 Notifications
- Context-aware alerts displayed per user
- Messages include timestamps, types, and are stored persistently
- Supports system-wide announcements and account-specific notifications

---

## 🧑‍💼 Manager Features
- **User Management**: Edit user profiles, securely update credentials (password & PIN)
- **Interest Configuration**: Update interest rates dynamically per account type
- **Reports**:
    - Monthly **Customer Statements**
    - Full **Bank Summary Reports**
    - Export to CSV with `ReportExporter` using well-formatted tables

---

## 📊 Reporting Module
Built atop the `ReportService` class, it leverages domain data from all layers:

- **Bank Summary**: System-wide statistics (balances, customers, credit usage, low balances)
- **Customer Statement**: Monthly breakdown of transactions, opening/closing balances
- **CSV Export**: Modern UI interaction with file explorer integration for export location

---

## 🎨 GUI Features
- Dark theme via `StyleUtils` supporting consistent styling across components
- Custom styled dialogs, dropdowns, scroll panes, and tables
- Role-adaptive dashboards (`CustomerUi`, `ManagerUi`)
- Form validation integrated directly into UI fields with real-time feedback

---

## 🔐 Security Highlights
- All sensitive data processed using secure `char[]` arrays
- Password & PIN hashed with random salt using PBKDF2
- Real-time validation checks on forms to enforce strong password/PIN policies
- Data never held in memory longer than necessary

---

## 🧪 Testing & Simulation
- Seamless switching between file storage and DB using `StorageMode`
- Modular repository interfaces to mock and simulate data during testing
- Complete lifecycle test support for account and transaction flows

---

## 🗺️ Future Enhancements
- JDBC/ORM integration for robust DB operations
- Multi-threaded queue processing for transaction pipelines
- User audit trail, role escalations, access logs
- Enhanced accessibility and internationalization

---

## 🧰 Tech Stack
- **Language**: Java 21
- **UI**: Java Swing (modular, styled)
- **Security**: PBKDF2 + Salted Hashing
- **Storage**: File-based (with DB-ready design)
- **Design Patterns**: Singleton, Factory, DAO, Strategy, MVC

---

## 📁 Package Overview (via Javadoc)

Top-level packages:
- `com.fortisbank.models.*`
- `com.fortisbank.business.services.*`
- `com.fortisbank.data.repositories.*`
- `com.fortisbank.ui.*`
- `com.fortisbank.utils.*`

---

📦 Developed as a comprehensive case study in designing secure, scalable, maintainable financial software systems.

