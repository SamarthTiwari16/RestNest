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
- **Current Phase:** Phase 2 (Completed & Verified)
- **Next Phase:** Phase 3 (Image Upload)
- **Local Databases:** 
  - MySQL database `rentnest` created.
  - User `rentnest_app` created with password `change-me` and full privileges on `rentnest` schema.
  - Flyway migrations applied up to `V2__create_properties.sql`.

---

## 3. Progress History & Completed Phases

### Phase 0 — Project Setup (Completed & Verified)
- **Goals Met:** Empty-but-running skeleton for backend and frontend.
- **Key Actions:** Setup Maven/React skeletons, fixed test config and whitelisted Swagger UI entrypoints.

### Phase 1 — Authentication & User Management (Completed & Verified)
- **Goals Met:** Users register and log in securely.
- **Key Actions:** Created User entity, AuthController REST endpoints, exception handlers, Spring Security JWT filter chain, session storage provider, and React registration/login pages. Tested via JUnit/Mockito and browser integration session.

### Phase 2 — Property Listing Core (Completed & Verified)
- **Goals Met:** Property listing CRUD and state transitions.
- **Key Actions:** Created V2 properties migration, Property entity, Repository, Service (validating state machine), REST Controller, JUnit/Mockito tests, and React CreateListing form/MyListings dashboard.

---

## 4. Next Phase Details

### Phase 3 — Image Upload (Ready to Start)
- **Goal:** Listings support multiple images stored outside the database.
- **Tasks:**
  - Create `PropertyImage` entity and schema migration.
  - Build `ImageStorageService` saving to local disk (behind an interface for future S3 integration).
  - Create upload endpoint (multipart) returning stored URLs.
  - Add file-type validation (jpg/png/webp), max size checks, and require at least 1 image before verification submission.
  - Build frontend image uploader with preview and reordering support in the listing form.

