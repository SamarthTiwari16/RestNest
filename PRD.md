# PRD.md — Project Requirements Document
## RentNest — Smart Rental Housing Platform with Secure Enquiry Management

---

## 1. Overview

**RentNest** is a full-stack rental housing web application for the Indian market. It replaces the fragmented experience of browsing multiple listing sites, dealing with brokers, and exposing personal contact details to strangers, with a single unified platform where any user can both **list** a property and **search** for one, using one account.

The platform's defining idea is **privacy-first enquiry management**: contact details are never shown upfront. A tenant sends a structured enquiry, the owner reviews and accepts it, and only then is contact information exchanged.

---

## 2. Goals

- Give property owners a simple way to list, verify, and manage rental properties through a clear lifecycle (not just create/edit/delete).
- Give tenants a fast, filterable search experience with saved favourites and enquiry tracking.
- Protect personal contact information until both parties opt in.
- Demonstrate enterprise-grade engineering: layered architecture, security, validation, exception handling, logging, documented APIs, and containerized deployment.

## 3. Non-Goals (Out of Scope for v1)

- Online rent payment / escrow processing.
- In-app chat or messaging beyond the enquiry workflow.
- Native mobile apps (web-responsive only).
- Multi-language localization.
- Broker/agency accounts as a distinct role (may be a future enhancement).

---

## 4. Target Users & Personas

| Persona | Description | Primary Needs |
|---|---|---|
| **Tenant (Priya, 27)** | Software employee relocating for a job, searching for a 2BHK near her office. | Fast filtering by budget/locality, ability to save shortlisted properties, no spam calls until she's ready. |
| **Owner (Ramesh, 45)** | Owns two flats he rents out, currently manages enquiries over WhatsApp. | Easy listing creation, control over who gets his number, a dashboard to see enquiry status at a glance. |
| **Admin (Platform Ops)** | Reviews new listings before they go public. | Quick approve/reject workflow, ability to remove fraudulent or duplicate listings. |

Because RentNest uses a **single account type**, Priya and Ramesh are the same underlying user role (`ROLE_USER`) — the UI simply shows both "My Listings" and "My Search" surfaces to every logged-in user. `ROLE_ADMIN` is a separate, elevated role.

---

## 5. Core User Stories

### Authentication
- As a user, I can register with email/password and log in securely, so I can access my account.
- As a user, I want validation on registration (strong password, valid phone, no duplicate email) so my account is created correctly the first time.

### Property Listing (Owner side)
- As a user, I can create a property listing with details, images, rent, and availability, so tenants can discover it.
- As a user, I want my listing to go through admin verification before it's public, so the platform stays trustworthy.
- As a user, I can mark my property as **Rented**, so it's archived and stops receiving enquiries.
- As a user, I can edit or withdraw my own listings at any time before they're rented.

### Search & Discovery (Tenant side)
- As a user, I can search and filter properties by city, locality, budget, BHK, furnishing, parking, pet-friendliness, property type, and availability date, so I find relevant matches quickly.
- As a user, I can save properties to a favourites list, so I can compare them later.
- As a user, I can browse paginated, sorted results without the app slowing down.

### Enquiry Workflow
- As a tenant, I can send a structured enquiry (move-in date, occupants, message) to an owner without seeing their phone number.
- As an owner, I can view incoming enquiries and accept or decline them.
- As a tenant, once my enquiry is accepted, I can see the owner's contact number.
- As a user, I can track the status of every enquiry I've sent or received (Pending / Accepted / Declined).

### Dashboards
- As an owner, I want a dashboard showing total properties, active listings, total enquiries, and rented count.
- As a tenant, I want a dashboard showing saved properties, my enquiries, and recently viewed listings.

### Admin
- As an admin, I can review pending property submissions and approve or reject them with a reason.
- As an admin, I can deactivate a listing that violates platform rules.

### Platform Quality
- As a user, I receive clear error messages when something goes wrong (not raw stack traces).
- As a user, my data and password are stored securely.
- As a developer, I can read auto-generated API documentation (Swagger) to understand every endpoint.

---

## 6. Functional Requirements Summary

| # | Module | Requirement |
|---|---|---|
| FR-1 | Auth | JWT-based registration/login, BCrypt password hashing, role-based access (`ROLE_USER`, `ROLE_ADMIN`) |
| FR-2 | Property | Full lifecycle: Draft → Pending Verification → Approved → Active → Rented → Archived (see Architecture.md §3) |
| FR-3 | Search | Dynamic multi-field filtering via Spring Data JPA Specifications, pagination, sorting |
| FR-4 | Images | Multiple images per property, stored on disk/object storage, URLs persisted in MySQL |
| FR-5 | Enquiry | Enquiry creation, owner accept/decline, conditional contact-info disclosure |
| FR-6 | Favourites | Save/unsave properties, list saved properties |
| FR-7 | Dashboard | Aggregate counts per user role, recently viewed tracking |
| FR-8 | Admin | Property moderation queue, approve/reject with reason, listing deactivation |
| FR-9 | Validation | Field-level validation on all write endpoints (Bean Validation) |
| FR-10 | Errors | Centralized exception handling (`@ControllerAdvice`), consistent error response shape |
| FR-11 | Logging | Structured logs for auth, property, and enquiry events |
| FR-12 | Docs | Swagger/OpenAPI documentation for all endpoints |
| FR-13 (optional) | AI | Owner can auto-generate a property description from structured inputs via an LLM call |

## 7. Non-Functional Requirements

- **Security:** JWT expiry + refresh strategy, password hashing, protected endpoints by role, input sanitization.
- **Performance:** Paginated queries only; no unbounded `findAll()` on property listings.
- **Reliability:** Global exception handling ensures no unhandled 500s reach the client without a structured body.
- **Maintainability:** Layered architecture (Controller → Service → Repository), DTOs at API boundaries (no entity leakage).
- **Observability:** SLF4J/Logback logging for key domain events.
- **Portability:** Dockerized backend + database, single-command startup via `docker-compose`.
- **Documentation:** Every REST endpoint visible and testable via Swagger UI.

## 8. Success Metrics (for this as a portfolio project)

- All 12–15 features listed above fully implemented and demoable end-to-end.
- Clean, incremental Git history reflecting real development phases.
- A recruiter/interviewer can trace one enquiry from creation → owner acceptance → contact disclosure live in the app.
- Swagger UI covers 100% of implemented endpoints.
