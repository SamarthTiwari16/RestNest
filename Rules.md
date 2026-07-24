# Rules.md — AI Development Rules for RentNest

These rules govern how an AI coding assistant (or any contributor) should write code for this project. They exist so the codebase stays consistent, secure, and interview-defensible, regardless of who or what wrote each line.

---

## 1. General Principles

- Follow **Architecture.md** and **PRD.md** as the source of truth. If a request conflicts with them, flag the conflict instead of silently deviating.
- Prefer **explicit, readable code** over clever one-liners. This is a portfolio project meant to be explained line-by-line in an interview.
- Never implement a feature that isn't in **Phases.md** for the current phase. Don't jump ahead to Phase 5 work while on Phase 2 — small, reviewable increments only.
- When unsure between two valid approaches, pick the one that is more standard/idiomatic Spring Boot / React, not the most novel one.

## 2. Approved Libraries & Tools

| Purpose | Use | Do NOT use |
|---|---|---|
| Backend framework | Spring Boot (MVC, Security, Data JPA) | Plain Servlets, JAX-RS |
| ORM | Hibernate via Spring Data JPA | Raw JDBC (except migration scripts) |
| Auth | `jjwt` or `java-jwt` + Spring Security | Custom hand-rolled crypto |
| Password hashing | `BCryptPasswordEncoder` | MD5, SHA-1, plain text |
| Validation | Jakarta Bean Validation (`@Valid`, `@NotNull`, etc.) | Manual `if` chains for basic field checks |
| DB migrations | Flyway | Manual schema edits in prod, Hibernate `ddl-auto: update` in prod |
| API docs | springdoc-openapi (Swagger UI) | Hand-written Postman-only docs as the sole source |
| Frontend framework | React (functional components + hooks) | Class components (except if explicitly required) |
| Styling | Tailwind CSS + design tokens from Design.md | Inline styles as the primary styling method, random ad-hoc CSS frameworks |
| HTTP client | Axios with a shared instance (`axiosClient.js`) | `fetch` scattered across components |
| State | React Context / local state; component-level `useState`/`useReducer` | Redux/MobX (overkill for this project's scope) |
| Testing | JUnit 5 + Mockito | Skipping tests on service-layer business logic |
| Containers | Docker + docker-compose | Manual server setup instructions as the only deployment path |

**Adding a new dependency:** only after checking it isn't already solved by something in this table. If a genuinely new library is needed, note it and the reason in the relevant phase's notes before using it.

## 3. Data Boundaries

- **Entities never leave the Service layer.** Controllers accept and return DTOs only.
- Every write endpoint DTO uses Bean Validation annotations (`@NotBlank`, `@Min`, `@Email`, etc.) — no unvalidated input reaches the Service layer.
- Passwords are never returned in any response DTO, ever, under any circumstance.
- Owner contact info (`phone`) is only included in an `Enquiry`/`Property` response DTO when the requesting user is authorized to see it (see Architecture.md §4 enquiry workflow). This check happens in the Service layer, not the frontend.

## 4. Error Handling

- All exceptions are handled centrally via a single `@ControllerAdvice` (`GlobalExceptionHandler`). No `try/catch` swallowing exceptions silently in controllers.
- Every error response follows one consistent shape:
```json
{
  "timestamp": "2026-07-22T10:15:00Z",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Property with id 42 not found",
  "path": "/api/properties/42"
}
```
- Use specific custom exceptions (`ResourceNotFoundException`, `UnauthorizedActionException`, `DuplicateResourceException`, `ValidationException`) — never throw a bare `RuntimeException` for expected business failures.
- Never expose stack traces, SQL errors, or internal class names to the client.
- Log the full exception server-side (with stack trace) even when the client only sees a clean message.

## 5. Security Rules

- All endpoints are secured by default; public endpoints (`/api/auth/**`, `GET /api/properties/search`, Swagger UI) must be **explicitly** whitelisted in `SecurityConfig`.
- Role checks (`ROLE_USER`, `ROLE_ADMIN`) are enforced server-side via `@PreAuthorize` or security config — never trust a role flag sent from the frontend.
- JWTs are short-lived; store them in memory/HttpOnly cookies on the frontend, never in `localStorage` if it can be avoided (if `localStorage` is used for simplicity in early phases, note it as a known trade-off, not a final answer).
- Never log raw passwords, JWTs, or full request bodies containing sensitive fields.
- CORS is configured explicitly for the known frontend origin(s) — never `allowedOrigins("*")` alongside credentials.

## 6. Logging

- Use SLF4J (`LoggerFactory.getLogger(...)`), never `System.out.println`.
- Log at `INFO` for domain events (user registered, property created, enquiry accepted), `WARN` for recoverable issues (validation failure, duplicate submission), `ERROR` for unexpected exceptions.
- Never log PII beyond what's needed to debug (e.g., log `userId`, not full name + phone + email together).

## 7. Git & Commit Discipline

- Commit in small, phase-aligned units (see Phases.md). One feature or one fix per commit — no giant "final version" commits.
- Commit message format: `<phase>: <short imperative summary>` e.g. `phase2: add JWT login endpoint`.
- Never commit `.env`, real credentials, or `application-local.yml` with secrets.
- Every phase ends with a working, runnable state on `main` (or a merged feature branch) — never leave `main` broken between sessions.

## 8. What the AI Should NOT Do

- Do not silently add features beyond the current phase's scope.
- Do not introduce a new architectural pattern (e.g., microservices, CQRS, event sourcing) without it being explicitly requested and reflected in Architecture.md first.
- Do not hardcode secrets, API keys, or database credentials in source files — use environment variables / `application.yml` placeholders.
- Do not disable security, validation, or CORS checks "temporarily to make it work" and leave them disabled.
- Do not generate placeholder/fake data logic that pretends to be real business logic (e.g., a fake "AI verification" that just returns `true`).
- Do not remove or weaken existing tests to make a build pass.
- Do not restructure the folder layout in Architecture.md without flagging the change and updating that document.

## 9. Definition of Done (per feature)

A feature is "done" only when:
1. Endpoint(s) implemented with DTOs in/out.
2. Validation added on all inputs.
3. Errors handled via the global handler with meaningful messages.
4. Unit test(s) added for the service-layer logic.
5. Endpoint visible and correctly documented in Swagger UI.
6. Relevant log statements added for key events.
7. Frontend (if applicable) matches the design tokens in Design.md — no ad-hoc colors/fonts.
