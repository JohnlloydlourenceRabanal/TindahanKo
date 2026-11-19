# TindahanKo

> **A Web-Based Inventory and Customer Credit Management System with SMS Reminders and Digital Payment Integration for Neighborhood Sari-Sari Stores**  
> *Course:* IT342 - Systems Integration and Architecture 1  
> *Author:* John Lloyd Lourence C. Rabanal  
> *Submission:* Project Development Increment 1 (User Accounts & Authentication Module)  

---

## 1. Project Overview
**TindahanKo** is designed for community sari-sari stores in the Philippines to replace vulnerable paper notebooks (*kuwaderno*) with an automated, web-based system. 

For **Increment 1**, the system delivers a complete, secure **User Accounts and Authentication** module with:
* **User Account Creation:** Validated registration for Store Owners and Customers with BCrypt password hashing.
* **User Login and Logout:** Stateless JWT authentication, secure session handling, and protected routes.
* **Role-Based User Dashboards:** Separate dashboards for `STORE_OWNER` (Admin) and `CUSTOMER` (Borrower) with strict Role-Based Access Control (RBAC) enforced on both the React frontend and Spring Boot backend.

---

## 2. Tech Stack
* **Backend:** Spring Boot (Java 17+), Spring Security, Spring Data JPA, Hibernate, JJWT (io.jsonwebtoken 0.12.6)
* **Frontend:** ReactJS (Vite, React Router v6, Context API, CSS3)
* **Database:** PostgreSQL (Cloud-hosted on Supabase)
* **Architecture:** Decoupled 3-tier RESTful API with stateless JWT session management

---

## 3. Prerequisites
Ensure you have the following installed on your machine:
* **Java Development Kit (JDK):** Version 17 or higher (`java -version`, `javac -version`)
* **Node.js:** Version 18 or higher with npm (`node -v`, `npm -v`)
* **Git:** Version 2.x (`git --version`)
* *(Maven is included via the provided `./mvnw.cmd` wrapper; no global Maven installation required)*

---

## 4. Supabase Database Setup
1. Create a free PostgreSQL project on [Supabase](https://supabase.com).
2. Open the **SQL Editor** in your Supabase dashboard and execute the following DDL script:

```sql
CREATE TABLE IF NOT EXISTS users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    mobile_number VARCHAR(15) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('STORE_OWNER', 'CUSTOMER')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
```

---

## 5. Environment Configuration

### Backend Configuration (`backend/.env`)
Copy `backend/.env.example` to `backend/.env` and update with your Supabase credentials:

```properties
PORT=8080
SPRING_DATASOURCE_URL=jdbc:postgresql://<YOUR_SUPABASE_POOLER_HOST>:5432/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres.<YOUR_PROJECT_REF>
SPRING_DATASOURCE_PASSWORD=your_actual_db_password
JWT_SECRET=TindahanKoSuperSecretKeyForJwtAuthenticationSecurityWorkFactor2026
JWT_EXPIRATION_MS=86400000
CORS_ALLOWED_ORIGINS=http://localhost:5173
```
> **Security Note:** Real `.env` files are strictly excluded from version control via `.gitignore`. Never commit passwords to GitHub.

### Frontend Configuration (`frontend/.env`)
Copy `frontend/.env.example` to `frontend/.env`:

```properties
VITE_API_BASE_URL=http://localhost:8080/api
```

---

## 6. How to Run the Application

### Running the Backend (Spring Boot)
1. Open a terminal and navigate to the `backend` directory:
   ```bash
   cd backend
   ```
2. Start the Spring Boot application using the Maven Wrapper:
   ```bash
   # Windows PowerShell / CMD
   .\mvnw.cmd spring-boot:run

   # macOS / Linux
   ./mvnw spring-boot:run
   ```
3. The backend will boot up and listen on: **`http://localhost:8080`**.

### Running the Frontend (React Vite)
1. Open a second terminal and navigate to the `frontend` directory:
   ```bash
   cd frontend
   ```
2. Install dependencies (first-time only):
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
4. Access the web app in your browser at: **`http://localhost:5173`**.

---

## 7. Default Ports & Service Summary

| Service | Technology | Port / URL | Description |
| :--- | :--- | :--- | :--- |
| **Frontend Client** | ReactJS + Vite | `http://localhost:5173` | Single-Page Application (Login, Register, Dashboards) |
| **Backend API** | Spring Boot | `http://localhost:8080` | REST API, Spring Security, JWT Validation |
| **Database** | PostgreSQL | Supabase Cloud (`:5432`) | Persistent transactional user & ledger storage |

---

## 8. User Roles & Verification Guide

### System Roles Defined in SRS

| Role | Target Actor | Accessible Dashboard | Responsibilities & Permissions |
| :--- | :--- | :--- | :--- |
| **`STORE_OWNER`** | Sari-Sari Store Owner (Admin) | `/owner/dashboard` | Full store administration, product catalog, POS cash/utang sales recording, customer profile management, and analytics overview. |
| **`CUSTOMER`** | Store Customer (Borrower) | `/customer/dashboard` | Access to Customer Balance Portal, views running debt balance, credit limit, due date, and payment status. |

### How to Test Each Role
1. **Test Registration:**
   * Open `http://localhost:5173/register`.
   * Fill in Full Name, Username, Philippine Mobile Number (`09XXXXXXXXX`), and Password.
   * Choose role: **Sari-Sari Store Owner** or **Store Customer**.
   * Click **Create Account** $\rightarrow$ User is saved to PostgreSQL with a BCrypt hash, JWT is issued, and user is redirected to their specific dashboard.
2. **Test Duplicate Prevention:**
   * Try registering again with the exact same username or mobile number.
   * Verify that an inline validation error appears: *"Username '...' is already registered"*.
3. **Test Bad Credentials:**
   * Go to `http://localhost:5173/login`.
   * Enter an incorrect password $\rightarrow$ Verify error: *"Invalid username or password"*.
4. **Test Role-Based Access Control (RBAC):**
   * Log in as a **Customer** (`customer_ana`).
   * Manually type `http://localhost:5173/owner/dashboard` into the browser URL bar.
   * Verify you are blocked by the route guard and redirected to the `/unauthorized` (403 Access Denied) page.
   * Verify the Spring Boot backend returns HTTP 403 Forbidden if a Customer tries to fetch `/api/owner/dashboard`.
5. **Test Logout:**
   * Click the **Logout** button in the top navigation bar.
   * Verify that your token and user data are purged from `localStorage`.
   * Try navigating directly to `/owner/dashboard` $\rightarrow$ You are redirected to `/login`.

---

## 9. Authentication & RBAC API Endpoint Reference

| Method | Endpoint | Access Level | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Public | Registers a new user (`STORE_OWNER` or `CUSTOMER`) with BCrypt password hashing. Returns JWT token. |
| `POST` | `/api/auth/login` | Public | Authenticates credentials and returns JWT Bearer token + user role profile. |
| `GET` | `/api/auth/me` | Authenticated | Retrieves current authenticated user profile using JWT token. |
| `GET` | `/api/owner/dashboard` | `ROLE_STORE_OWNER` | Retrieves Store Owner command center metrics. Rejects non-owners with HTTP 403. |
| `GET` | `/api/customer/dashboard`| `ROLE_CUSTOMER` | Retrieves Customer balance portal overview. Rejects non-customers with HTTP 403. |

---

## 10. Security Compliance
* **NFR-007 (Password Encryption):** Passwords are encrypted before storage using `BCryptPasswordEncoder` with a work factor of 10. Exactly 0 plaintext passwords stored.
* **NFR-009 (Role-Based Access Control):** All administrative and customer endpoints are strictly protected by Spring Security filters and JWT claims.
