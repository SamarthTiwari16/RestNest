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
- **Current Phase:** Phase 5 (Completed & Verified)
- **Next Phase:** Phase 6 (Enquiry Workflow)
- **Local Databases:** 
  - MySQL database `rentnest` created.
  - User `rentnest_app` created with password `change-me` and full privileges on `rentnest` schema.
  - Flyway migrations applied up to `V4__create_favourites.sql`.

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

### Phase 3 — Image Upload (Completed & Verified)
- **Goals Met:** Property listings support multiple images stored on disk with reordering and cover selections.
- **Key Actions:** Created V3 property_images migration, PropertyImage entity, LocalStorageServiceImpl storing images on disk (JPG, PNG, WEBP), WebMvcConfig serving uploads statically, REST upload endpoints, unit tests for submission validation (min 1 image), and frontend ImageUploader with preview/reordering features.

### Phase 4 — Search & Filtering (Completed & Verified)
- **Goals Met:** Tenants search and filter active property listings dynamically.
- **Key Actions:** Implemented PropertySpecification using JPA Criteria APIs, created paginated search endpoints on PropertyController, integrated Axios queries on frontend API, and built Search.jsx page containing side filter selectors and card grid layout.

### Phase 5 — Favourites (Completed & Verified)
- **Goals Met:** Tenants can save and unsave active properties, and view their saved list.
- **Key Actions:** Created V4 favourites migration, Favourite entity, FavouriteRepository, FavouriteService implementations, REST endpoints for save/unsave toggling, frontend favouritesApi, SavedListings page, and overlay heart icon button toggle on Search.jsx cards.

---

## 4. Next Phase Details

### Phase 6 — Enquiry Workflow (Ready to Start)
- **Goal:** The privacy-first contact exchange, this project's signature feature.
- **Tasks:**
  - Create `Enquiry` entity mapping tenant ID, owner ID, property ID, dynamic moves-in data, messages, and state (`PENDING`, `ACCEPTED`, `DECLINED`).
  - Create database migration for enquiries table.
  - Develop backend endpoints: send enquiry, accept/decline enquiry, and list received/sent enquiries.
  - Enforce constraint: hide owner email and phone number from the tenant until the owner accepts.
  - Develop owner received enquiries board, accept/decline buttons, and tenant active enquiries checklist page.

