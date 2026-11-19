# TindahanKo

> **A Web-Based Inventory and Customer Credit Management System for Neighborhood Sari-Sari Stores**  
> *Course:* IT342 - Systems Integration and Architecture 1  
> *Author:* John Lloyd Lourence C. Rabanal  

---

## Project Overview
TindahanKo modernizes micro-retail operations in Philippine sari-sari stores by replacing handwritten notebooks (*kuwaderno*) with automated stock monitoring, customer *utang* ledgers, automated Semaphore SMS reminders, and digital debt settlements (GCash/Maya via PayMongo).

### Increment 1: User Accounts & Authentication Module
This initial development increment implements:
1. **User Account Creation:** Secure registration with field validations, duplicate prevention, and BCrypt password hashing.
2. **User Login & Logout:** Stateless JWT-based authentication with secure session handling and protected endpoints.
3. **Role-Based User Dashboards:** Role-Based Access Control (RBAC) separating `STORE_OWNER` and `CUSTOMER` interfaces and API endpoints.

---

## Tech Stack
* **Backend:** Spring Boot (Java 17+), Spring Security, Spring Data JPA
* **Frontend:** ReactJS (Vite, React Router)
* **Database:** PostgreSQL (Hosted on Supabase)
* **Authentication:** JSON Web Tokens (JJWT)
