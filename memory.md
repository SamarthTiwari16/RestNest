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
- **Current Phase:** Phase 10 (Completed & Verified)
- **Next Phase:** None (All development phases completed successfully)
- **Local Databases:** 
  - MySQL database `rentnest` created.
  - User `rentnest_app` created with password `change-me` and full privileges on `rentnest` schema.
  - Flyway migrations applied up to `V7__add_rejection_reason.sql`.

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

### Phase 6 — Enquiry Workflow (Completed & Verified)
- **Goals Met:** Privacy-first contact exchange, tenant submitting message/move-in/occupants and owners accepting/declining requests to unmask contact credentials.
- **Key Actions:** Created V5 enquiries migration, Enquiry entity, EnquiryStatus enum, EnquiryRepository, EnquiryService, and REST Controller. Configured server-side contact masking. Created frontend enquiriesApi, PropertyDetailsModal with Carousel & EnquiryForm, SentEnquiries sent list, ReceivedEnquiries received list, and linked them to App.jsx. Verified with JUnit tests and browser E2E flow.

### Phase 7 — Dashboards (Completed & Verified)
- **Goals Met:** At-a-glance summaries for both owner and tenant modes of the single user account, including recently viewed listings tracking.
- **Key Actions:** Created V6 recently viewed migration, RecentlyViewed entity and repository, DashboardService with optimized queries to avoid N+1 issues, REST endpoints `/api/dashboard/owner` and `/api/dashboard/tenant`, unit tests (`DashboardServiceTest`), a premium dashboard UI component on the frontend (`Dashboard.jsx`), and integrated detail page view tracking by fetching property details on modal open.

### Phase 8 — Admin Moderation (Completed & Verified)
- **Goals Met:** Admin user role, listing verification queue, property approve/reject with rejection reason, and active listing deactivation.
- **Key Actions:** Created V7 database migration adding `rejection_reason` column, seeded default admin `admin@rentnest.com` with role `ROLE_ADMIN` on startup, created `AdminController` with moderation endpoints, implemented custom Spring Security role checks, added full test suites, and built the frontend `AdminReview` queue portal and detail deactivation hooks.

### Phase 9 — Polish: Validation, Exceptions, Logging, Docs (Completed & Verified)
- **Goals Met:** Unified global REST exception handler, comprehensive SLF4J logging coverage for file storage events, fully descriptive Swagger UI documentation for all endpoints/DTO fields, and robust service testing.
- **Key Actions:** Extended `GlobalExceptionHandler` to gracefully format key Spring Security and Web MVC exceptions (e.g. AccessDeniedException, HttpMessageNotReadableException, ConstraintViolationException). Integrated SLF4J logger to `LocalStorageServiceImpl` and replaced raw console printing. Annotated all request and response DTO schemas with `@Schema` details/examples and `/search` parameters with `@Parameter` descriptors. Wrote `LocalStorageServiceImplTest` unit tests and verified all 62 backend tests run and pass successfully.

### Phase 10 — Frontend Design Pass (Completed & Verified)
- **Goals Met:** Applied the full Ellipsus-inspired editorial design system (Design.md) across every screen.
- **Key Actions:**
  - **Typography & Fonts:** Extended `index.css` to import Google Fonts (Fraunces, Inter, Caveat), define global type scale utility classes (`.text-hero`, `.text-h1`, `.text-h2`, `.text-h3`, `.text-body`, `.text-annotation`).
  - **Animated SVG Key Motif:** Added hand-drawn key SVG in the Search hero section (`key-motif` class) with `stroke-dashoffset` draw animation via `@keyframes draw-line` in index.css.
  - **Hand-Drawn Signature Circles:** Wrapped the "verified listing" badge and the rent price in `PropertyDetailsModal.jsx` with animating SVG ellipse paths (`signature-circle` class) that draw on load.
  - **Dark Ink Hero:** Replaced the simple page header in `Search.jsx` with a full-width dark editorial hero section (`background: var(--ink)`) featuring the tagline "Every rental is the start of a chapter.", a subtext, the gold-key SVG motif, and an integrated city search bar with a gold glowing border.
  - **Nav Underline Animations:** Added `.nav-link-btn` CSS class with a sliding gold `::after` underline on hover and a persistent underline for the `.active` state. All six page navbars were consolidated into a single reusable `Header.jsx` component.
  - **Empty-State Floor-Plan Watermarks:** Added editorial hand-drawn dashed SVG floor-plan illustrations to empty states in `Search.jsx`, `MyListings.jsx`, and `SavedListings.jsx`.
  - **Production Build:** Vite build passed with ✓ 101 modules transformed, zero errors.

---

## 4. Key Technical Notes

- **Configuration:** Application settings are in `application.yml` (not `application.properties`).
- **Admin Login:** Seeded via `DataSeeder.java` on first boot: `admin@rentnest.com` / `AdminPassword123!`.
- **Test Bypasses:** E2E test mock: `btn-add-mock-image` in ImageUploader.jsx and mock dialog overrides in main.jsx.
- **CSS Variables:** Defined in `tokens.css` — `--ink`, `--parchment`, `--paper-white`, `--clay`, `--fog`, `--charcoal`, `--sage`, `--gold-thread`, `--border`.
