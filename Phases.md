# Phases.md — Build Roadmap for RentNest

The project is broken into phases so the AI/developer builds incrementally, with a working, demoable state at the end of every phase. Do not start a phase until the previous one is functionally complete and committed.

---

## Phase 0 — Project Setup
**Goal:** Empty-but-running skeleton for both backend and frontend.

- Initialize Spring Boot project (Java 21, Maven) with dependencies: Web, Security, Data JPA, MySQL Driver, Validation, Lombok, springdoc-openapi.
- Initialize React project (Vite) with Tailwind CSS configured.
- Set up MySQL locally (or via Docker) and confirm connection.
- Set up Flyway with an initial empty migration.
- Configure `application.yml` (dev profile) and `.env`/`application-docker.yml` placeholders.
- Initialize Git repo, `.gitignore`, base README.
- **Exit criteria:** Backend starts on `localhost:8080`, frontend starts on `localhost:5173`, Swagger UI loads at `/swagger-ui.html`.

## Phase 1 — Authentication & User Management
**Goal:** Users can register and log in securely.

- `User` entity + Flyway migration.
- Registration endpoint with validation (unique email, strong password, valid phone).
- Login endpoint issuing a JWT.
- Spring Security config: JWT filter, password encoding (BCrypt), public vs. protected routes.
- Frontend: Register/Login pages, `AuthContext`, Axios interceptor attaching the JWT.
- Basic global exception handler (`ResourceNotFoundException`, `ValidationException` at minimum).
- Unit tests: `AuthService` (registration validation, password hashing call, duplicate email rejection).
- **Exit criteria:** A user can register, log in, and hit a protected `/api/users/me` endpoint with their token.

## Phase 2 — Property Listing Core (CRUD + Lifecycle)
**Goal:** Owners can create and manage listings through the state machine.

- `Property` entity + migration, linked to `User` (owner).
- Property lifecycle: `DRAFT → PENDING_VERIFICATION → APPROVED → ACTIVE → RENTED → ARCHIVED` (Architecture.md §3).
- Endpoints: create, update (owner only, only while editable), get by id, list own properties, submit for verification, mark as rented, withdraw.
- DTOs in/out; entity never exposed directly.
- Validation: rent > 0, required fields, valid enum values.
- Frontend: "Create Listing" form, "My Listings" page showing status badges.
- **Exit criteria:** A logged-in user can create a draft listing, submit it, and see its status change.

## Phase 3 — Image Upload
**Goal:** Listings support multiple images stored outside the database.

- `PropertyImage` entity + migration.
- `ImageStorageService`: save to local disk (dev) behind an interface, so swapping to S3-compatible storage later is a config change, not a rewrite.
- Upload endpoint (multipart), returns stored URL(s).
- Validation: file type (jpg/png/webp), max size, mandatory at least 1 image before submitting for verification.
- Frontend: image uploader with preview + reordering in the listing form.
- **Exit criteria:** A property can be created with 1–5 images, viewable in the listing detail view.

## Phase 4 — Search & Filtering
**Goal:** Tenants can find relevant properties fast.

- `PropertySpecification` (JPA Criteria/Specifications) supporting: city, locality, budget range, BHK, furnishing, parking, pet-friendly, available-from, property type — all optional and combinable.
- Pagination + sorting (`Pageable`) on the search endpoint.
- Only `ACTIVE` properties are searchable.
- Frontend: `Search` page with a filter panel, results grid, pagination controls.
- **Exit criteria:** Filtering by any combination of fields returns correct, paginated results in under the same request.

## Phase 5 — Favourites
**Goal:** Tenants can save properties to revisit.

- `Favourite` entity (user ↔ property, unique constraint).
- Endpoints: save, unsave, list my favourites.
- Frontend: heart/save icon on `PropertyCard`, "Saved Properties" list.
- **Exit criteria:** Saving/unsaving updates instantly and persists across sessions.

## Phase 6 — Enquiry Workflow
**Goal:** The privacy-first contact exchange, this project's signature feature.

- `Enquiry` entity: `PENDING → ACCEPTED / DECLINED`.
- Tenant sends enquiry (message, move-in date, occupants) — no owner contact returned.
- Owner views received enquiries, accepts/declines.
- On accept, owner contact info becomes visible to that specific tenant only (checked server-side).
- Frontend: `EnquiryForm` on property detail page, "My Enquiries" (sent) and "Received Enquiries" (owner) views with status badges.
- **Exit criteria:** End-to-end: tenant enquires → owner accepts → tenant now sees the phone number; a declined enquiry never reveals it.

## Phase 7 — Dashboards
**Goal:** At-a-glance summaries for both sides of the single account.

- Owner dashboard: total properties, active listings, total enquiries received, rented count.
- Tenant dashboard: saved properties, my enquiries (with status), recently viewed (simple view-tracking on property detail visits).
- Aggregation queries in the Service layer (avoid N+1 — use projections or grouped queries).
- **Exit criteria:** Dashboard numbers match the underlying data exactly after manual verification.

## Phase 8 — Admin Moderation
**Goal:** Listings are verified before going public.

- `ROLE_ADMIN` secured endpoints.
- Moderation queue: list `PENDING_VERIFICATION` properties.
- Approve → `APPROVED` (then auto `ACTIVE`); Reject → back to `DRAFT` with a required reason stored/returned to the owner.
- Deactivate: admin can archive a live listing (policy violation, fraud report, etc.).
- Frontend: `AdminReview` page, accessible only to `ROLE_ADMIN`.
- **Exit criteria:** A non-admin cannot access moderation endpoints (403), and the full approve/reject loop works.

## Phase 9 — Polish: Validation, Exceptions, Logging, Docs
**Goal:** Enterprise-grade finishing pass across everything built so far.

- Audit every endpoint against Rules.md §9 "Definition of Done."
- Complete `@ControllerAdvice` coverage for all custom + common exceptions (constraint violations, auth failures, 404s, 403s).
- SLF4J logging pass on all key domain events.
- Swagger annotations/descriptions completed for every endpoint, grouped by tag.
- Add remaining JUnit/Mockito tests to reach solid coverage on the Service layer.
- **Exit criteria:** No endpoint returns a raw stack trace; Swagger UI reads like real API documentation.

## Phase 10 — Frontend Design Pass
**Goal:** Apply the full Ellipsus-inspired editorial design system (Design.md) across every screen.

- Implement design tokens (`tokens.css`): color, type scale, spacing.
- Dark editorial hero on the landing page with the serif headline and signature animated element.
- Hand-drawn annotation accents on key UI moments (verified badge, saved indicator, empty states).
- Consistent component library: buttons, inputs, cards, badges — all pulling from tokens, no ad-hoc styles.
- Responsive pass down to mobile widths; visible keyboard focus states.
- **Exit criteria:** Every page reflects the same type/color system; no default browser-blue links or unstyled elements remain.

## Phase 11 — Docker & Deployment
**Goal:** One-command startup, optionally deployed live.

- `Dockerfile` for backend (multi-stage build), `Dockerfile`/static build for frontend.
- `docker-compose.yml`: `frontend`, `backend`, `db` services, networked together.
- Nginx config to serve the frontend build and reverse-proxy `/api/**`.
- (Optional) GitHub Actions workflow: build + test on push, build Docker image.
- (Optional) Deploy to AWS EC2.
- **Exit criteria:** `docker-compose up` on a clean machine brings up the full working app.

## Phase 12 — Optional AI Feature
**Goal:** One well-scoped AI enhancement, not the project's focus.

- Owner provides structured inputs (BHK, locality, amenities) → LLM call generates a draft property description they can edit before publishing.
- Isolated behind its own service/interface so it can be disabled without affecting core functionality.
- **Exit criteria:** Feature works but the app is fully functional with it turned off.

---

## Working Agreement

- Each phase = its own set of commits, ending in a state matching that phase's "Exit criteria."
- Do not begin the next phase's endpoints/entities early, even if it seems convenient.
- If a phase reveals that an earlier decision in Architecture.md needs to change, update Architecture.md in the same commit — don't let the docs drift from the code.
