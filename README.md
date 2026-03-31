# Spring Gym CRM

Spring Gym CRM is a Spring Boot REST API for managing trainees, trainers, and training sessions.
It includes authentication, profile management, training scheduling, health checks, and Prometheus-ready metrics.

## Features

- Trainee and trainer registration and profile management
- Training session creation and training type lookup
- JWT-based authentication for protected API endpoints
- Password change support and temporary account lock on repeated failed login
- OpenAPI/Swagger documentation
- Spring Boot Actuator health and metrics endpoints
- Multiple runtime profiles (`local`, `dev`, `stg`, `prod`)

## Tech Stack

- Java 17
- Spring Boot 3.5.7
- Spring Web, Validation, Data JPA, Security, Actuator
- H2 (local/dev) and PostgreSQL (stg/prod)
- Springdoc OpenAPI UI
- Micrometer + Prometheus registry
- JUnit + Spring Boot Test + Rest Assured
- Maven

## Project Structure

- `src/main/java/com/gym/crm/controller` - REST controllers
- `src/main/java/com/gym/crm/service` - business logic
- `src/main/java/com/gym/crm/dao` - data access layer
- `src/main/java/com/gym/crm/entity` - JPA entities
- `src/main/java/com/gym/crm/config` - security, OpenAPI, web config
- `src/main/resources` - profile configs and seed data (`data.sql`)

## Prerequisites

- JDK 17+
- Maven 3.9+

## Run the Application

Default profile is `local` (in-memory H2).

```bat
mvn clean spring-boot:run
```

Run with a specific profile (Windows `cmd.exe`):

```bat
set SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

For `stg` and `prod`, set database credentials first:

```bat
set SPRING_PROFILES_ACTIVE=stg
set DB_USERNAME=your_db_user
set DB_PASSWORD=your_db_password
mvn spring-boot:run
```

## Build and Test

```bat
mvn clean verify
```

Run only tests:

```bat
mvn test
```

## API Documentation

After startup (default port `8080`):

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Authentication

- Public endpoints:
  - `POST /api/auth/login`
  - `POST /api/trainee/register`
  - `POST /api/trainer/register`
- Protected endpoints: all other `/api/**` endpoints require `Authorization: Bearer <jwt>`

Example login request:

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "your.username",
  "password": "yourPassword"
}
```

## Main API Areas

- `AuthController` (`/api/auth`) - login and password change
- `TraineeController` (`/api/trainee`) - trainee lifecycle, profile, trainings, trainer assignment
- `TrainerController` (`/api/trainer`) - trainer lifecycle, profile, trainings
- `TrainingController` (`/api/training`) - add training and fetch training types

## Observability

Actuator base path: `/actuator`

Useful endpoints:

- `GET /actuator/health`
- `GET /actuator/metrics`
- `GET /actuator/prometheus`

Custom business metrics include:

- `gym.trainees.total`
- `gym.trainers.total`
- `gym.trainings.total`

## Data and Profiles

- `local` profile: in-memory H2, schema created on startup, SQL seed enabled
- `dev` profile: file-based H2 (`./devdb`), SQL seed enabled
- `stg` and `prod` profiles: PostgreSQL placeholders, SQL seed disabled

Seed data is defined in `src/main/resources/data.sql` and includes:

- training types
- demo users
- initial trainers, trainees, and trainings

## Notes

- JWT secret and expiration are configured in `src/main/resources/application.properties` (`security.jwt.*`).
- CORS policy is configurable via `security.cors.*` properties.
- H2 console is enabled in `local` and `dev` at `http://localhost:8080/h2-console`.
