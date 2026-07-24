# RentNest — Project Memory & Progress Tracker

This file serves as a persistent context buffer for AI coding assistants and developers. It tracks the overall project status, key architectural decisions, and step-by-step phase progress so that context is not lost between sessions.

---

## 1. Project Overview & Tech Stack
**RentNest** is a full-stack rental housing web application for the Indian market, focusing on a **privacy-first enquiry workflow** where owner contact info is only exchanged upon owner approval.
- **Backend:** Java 21, Spring Boot 3.4.5, Spring Security, Spring Data JPA, Flyway, MySQL 8
- **Frontend:** React (Vite), Tailwind CSS, Axios
- **APIs & Docs:** Swagger / OpenAPI 3 (accessed at `http://localhost:8080/swagger-ui.html`)
- **Single Account Model:** A single user role (`ROLE_USER`) can act as both owner and tenant. `ROLE_ADMIN` is used for moderation.

---

## 2. Current Project Status
- **Current Phase:** Phase 0 (Completed & Verified)
- **Next Phase:** Phase 1 (Authentication & User Management)
- **Local Databases:** 
  - MySQL database `rentnest` created.
  - User `rentnest_app` created with password `change-me` and full privileges on `rentnest` schema.
  - Flyway migration history table initialized and `V1__create_users.sql` applied.

---

## 3. Progress History & Completed Phases

### Phase 0 — Project Setup (Completed & Verified)
- **Goals Met:** Empty-but-running skeleton for backend and frontend.
- **Key Actions & Fixes:**
  - Initialized Spring Boot backend (running on `localhost:8080`).
  - Initialized React + Vite frontend (running on `localhost:5173`).
  - Fixed `AuthControllerTest` and `UserControllerTest` configuration by properly importing `SecurityConfig.class` and `JwtAuthenticationFilter.class` to fix the `401 Unauthorized` issues.
  - Whitelisted `/swagger-ui.html` in `SecurityConfig.java` to prevent 401s on Swagger UI redirects.
  - Verified local database connection and Flyway migration execution.
  - Verified UI rendering via browser subagent screenshot validation.

---

## 4. Next Phase Details

### Phase 1 — Authentication & User Management (Ready to Start)
- **Goal:** Users can register and log in securely.
- **Tasks:**
  - Map `User` entity to the `users` table + Flyway migration verification.
  - Create registration endpoint with bean validation (unique email, strong password, valid phone).
  - Create login endpoint issuing a short-lived JWT.
  - Implement Spring Security filter chain processing (JWT verification, BCrypt password encoding).
  - Build frontend registration/login pages, configure `AuthContext`, and add an Axios interceptor for JWT attachments.
  - Add a basic global exception handler (`ResourceNotFoundException`, `ValidationException`).
  - Implement unit tests for `AuthService` (registration validations, password hashing, duplicate check).
