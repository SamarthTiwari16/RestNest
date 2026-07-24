# Architecture.md — RentNest Technical Architecture

---

## 1. Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React.js, HTML5, CSS3, Tailwind CSS, Axios |
| Backend | Java 21, Spring Boot, Spring MVC, Spring Security, Spring Data JPA (Hibernate) |
| Auth | JWT (stateless), BCrypt |
| Database | MySQL 8 |
| API Docs | Swagger / OpenAPI 3 (springdoc-openapi) |
| Build | Maven (backend), npm/Vite (frontend) |
| Testing | JUnit 5, Mockito |
| Tooling | Git, GitHub, Postman, Docker |
| Deployment | Docker Compose → AWS EC2 + Nginx, GitHub Actions CI/CD |
| Future | Redis (cache), Elasticsearch (search), Kafka/RabbitMQ (notifications), Spring Cloud (microservices) |

---

## 2. High-Level System Architecture

```mermaid
flowchart TB
    subgraph Client["Client Layer"]
        A[React SPA<br/>Axios API client]
    end

    subgraph Edge["Edge"]
        N[Nginx<br/>reverse proxy + TLS]
    end

    subgraph App["Spring Boot Application"]
        C[Controller Layer<br/>REST Endpoints]
        S[Service Layer<br/>Business Logic]
        R[Repository Layer<br/>Spring Data JPA]
        SEC[Spring Security<br/>JWT Filter Chain]
        EX[Global Exception Handler<br/>@ControllerAdvice]
    end

    subgraph Data["Data Layer"]
        DB[(MySQL)]
        FS[(Image Storage<br/>local volume / S3-compatible)]
    end

    subgraph Cross["Cross-Cutting"]
        LOG[SLF4J / Logback]
        DOC[Swagger / OpenAPI]
    end

    A -->|HTTPS / REST + JWT| N --> C
    C --> SEC
    SEC --> S
    S --> R
    R --> DB
    S --> FS
    C -.-> EX
    C -.-> LOG
    C -.-> DOC
```

---

## 3. Property Lifecycle (State Machine)

This is the core workflow that elevates the project beyond basic CRUD.

```mermaid
stateDiagram-v2
    [*] --> Draft
    Draft --> PendingVerification: Owner submits listing
    PendingVerification --> Approved: Admin approves
    PendingVerification --> Rejected: Admin rejects (reason required)
    Rejected --> Draft: Owner edits & resubmits
    Approved --> Active: Auto-published, visible to search
    Active --> Rented: Owner marks as rented
    Active --> Archived: Owner withdraws
    Rented --> Archived: Auto-archived after grace period
    Archived --> [*]
```

---

## 4. Enquiry Workflow (Sequence Diagram)

```mermaid
sequenceDiagram
    actor Tenant
    actor Owner
    participant API as RentNest API
    participant DB as MySQL

    Tenant->>API: POST /api/enquiries (propertyId, message, moveInDate)
    API->>DB: Save Enquiry (status=PENDING)
    API-->>Tenant: 201 Created (no owner contact info)

    Owner->>API: GET /api/enquiries/received
    API->>DB: Fetch enquiries for owner's properties
    API-->>Owner: List of PENDING enquiries

    Owner->>API: PATCH /api/enquiries/{id}/accept
    API->>DB: Update status=ACCEPTED
    API-->>Owner: 200 OK

    Tenant->>API: GET /api/enquiries/{id}
    API->>DB: Fetch enquiry + owner contact (conditional)
    API-->>Tenant: 200 OK (phone number now included)
```

---

## 5. Layered Backend Architecture

```mermaid
flowchart LR
    subgraph L1["Presentation"]
        Ctl["@RestController<br/>DTO in / DTO out"]
    end
    subgraph L2["Business"]
        Svc["@Service<br/>Rules, workflow, transactions"]
    end
    subgraph L3["Persistence"]
        Repo["@Repository<br/>JpaRepository + Specifications"]
    end
    subgraph L4["Domain"]
        Ent["@Entity<br/>JPA models"]
    end

    Ctl -->|DTO| Svc -->|Domain object| Repo -->|maps to| Ent
    Ent -->|Hibernate| MySQL[(MySQL)]
```

**Rule:** Entities never cross into the Controller layer directly — every request/response uses a DTO (see Rules.md §Data Boundaries). Mapping happens in the Service layer (manually or via MapStruct).

---

## 6. Entity-Relationship Diagram

```mermaid
erDiagram
    USER ||--o{ PROPERTY : owns
    USER ||--o{ ENQUIRY : sends
    USER ||--o{ FAVOURITE : saves
    PROPERTY ||--o{ PROPERTY_IMAGE : has
    PROPERTY ||--o{ ENQUIRY : receives
    PROPERTY ||--o{ FAVOURITE : "saved as"

    USER {
        bigint id PK
        string name
        string email UK
        string phone
        string password_hash
        string role
        datetime created_at
    }
    PROPERTY {
        bigint id PK
        bigint owner_id FK
        string title
        string city
        string locality
        decimal rent
        int bhk
        string property_type
        boolean furnished
        boolean pet_friendly
        boolean parking
        date available_from
        string status
        datetime created_at
    }
    PROPERTY_IMAGE {
        bigint id PK
        bigint property_id FK
        string image_url
        int sort_order
    }
    ENQUIRY {
        bigint id PK
        bigint property_id FK
        bigint tenant_id FK
        string message
        date move_in_date
        int occupants
        string status
        datetime created_at
    }
    FAVOURITE {
        bigint id PK
        bigint user_id FK
        bigint property_id FK
        datetime created_at
    }
```

---

## 7. Backend Folder Structure

```
rentnest-backend/
├── src/main/java/com/rentnest/
│   ├── RentNestApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── SwaggerConfig.java
│   │   ├── CorsConfig.java
│   │   └── JwtConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── PropertyController.java
│   │   ├── SearchController.java
│   │   ├── EnquiryController.java
│   │   ├── FavouriteController.java
│   │   ├── DashboardController.java
│   │   └── AdminController.java
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── PropertyService.java
│   │   ├── SearchService.java
│   │   ├── EnquiryService.java
│   │   ├── FavouriteService.java
│   │   ├── ImageStorageService.java
│   │   └── impl/
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── PropertyRepository.java
│   │   ├── PropertySpecification.java
│   │   ├── EnquiryRepository.java
│   │   └── FavouriteRepository.java
│   ├── entity/
│   │   ├── User.java
│   │   ├── Property.java
│   │   ├── PropertyImage.java
│   │   ├── Enquiry.java
│   │   └── Favourite.java
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   ├── security/
│   │   ├── JwtAuthFilter.java
│   │   ├── JwtTokenProvider.java
│   │   └── UserDetailsServiceImpl.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   └── UnauthorizedActionException.java
│   └── util/
├── src/main/resources/
│   ├── application.yml
│   ├── application-docker.yml
│   └── db/migration/          # Flyway scripts (V1__init.sql, ...)
├── src/test/java/com/rentnest/
├── Dockerfile
└── pom.xml
```

## 8. Frontend Folder Structure

```
rentnest-frontend/
├── src/
│   ├── main.jsx
│   ├── App.jsx
│   ├── api/
│   │   ├── axiosClient.js
│   │   ├── authApi.js
│   │   ├── propertyApi.js
│   │   └── enquiryApi.js
│   ├── assets/
│   │   ├── fonts/
│   │   └── illustrations/       # hand-drawn SVG motifs (see Design.md)
│   ├── components/
│   │   ├── layout/              # Navbar, Footer, PageShell
│   │   ├── property/            # PropertyCard, PropertyGallery, FilterPanel
│   │   ├── enquiry/              # EnquiryForm, EnquiryStatusBadge
│   │   ├── dashboard/
│   │   └── ui/                  # Button, Input, Modal, AnnotationUnderline
│   ├── pages/
│   │   ├── Home.jsx
│   │   ├── Search.jsx
│   │   ├── PropertyDetail.jsx
│   │   ├── CreateListing.jsx
│   │   ├── OwnerDashboard.jsx
│   │   ├── TenantDashboard.jsx
│   │   ├── AdminReview.jsx
│   │   └── Auth/ (Login.jsx, Register.jsx)
│   ├── context/
│   │   └── AuthContext.jsx
│   ├── hooks/
│   ├── styles/
│   │   ├── tokens.css           # design tokens: color, type, spacing (see Design.md)
│   │   └── index.css
│   └── utils/
├── public/
├── tailwind.config.js
└── package.json
```

## 9. Deployment Architecture

```mermaid
flowchart TB
    Dev[Developer] -->|git push| GH[GitHub]
    GH -->|GitHub Actions| CI[Build + Test + Docker Image]
    CI -->|push image| Reg[Container Registry]
    Reg --> EC2[AWS EC2 Instance]
    subgraph EC2Box["EC2: docker-compose"]
        Nginx[Nginx] --> Frontend[React static build]
        Nginx --> Backend[Spring Boot container]
        Backend --> MySQLC[(MySQL container / RDS)]
    end
```

`docker-compose up` starts three services: `frontend`, `backend`, `db`. Nginx sits in front to serve the React build and reverse-proxy `/api/**` to the backend container.

---

## 10. Key Architectural Decisions

| Decision | Rationale |
|---|---|
| Single `USER` entity/role instead of separate Owner/Tenant tables | Matches the product decision that any user can list or search; avoids duplicate auth logic. |
| DTOs at every API boundary | Prevents entity/lazy-loading leakage into JSON, keeps API contract stable independent of schema changes. |
| Specifications/Criteria API for search | Filters are optional and composable (city + budget + BHK, any combination) — a fixed set of `findBy...` methods can't scale to this. |
| Images stored as files, URL in DB | Matches real-world practice; keeps the database small and fast. |
| Stateless JWT (no server session) | Enables horizontal scaling and a clean split between frontend and backend. |
| Property state machine | Encodes real business rules (a rented property must not receive new enquiries) instead of a boolean `active` flag. |
