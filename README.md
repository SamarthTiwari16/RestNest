# RentNest

RentNest is a privacy-first rental housing platform for the Indian market. Its first implemented slice is secure user authentication: registration, JWT login, and access to the current user profile.

## Project structure

- `rentnest-backend/` — Java 21+ Spring Boot REST API
- `rentnest-frontend/` — React + Vite client
- `Architecture.md`, `Design.md`, `Phases.md`, `PRD.md`, `Rules.md` — project source documents

## Phase 1 environment

Copy `.env.example` values into your local environment. The backend uses MySQL and Flyway; it does not create or alter schema outside migration scripts.

Run the backend with Maven from `rentnest-backend` and the frontend with npm from `rentnest-frontend` after installing their respective toolchains.
