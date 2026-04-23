# Spring Gym CRM

Spring Gym CRM is a microservices-based project for managing trainees, trainers, and training sessions.
It includes authentication, profile management, training scheduling, health checks, Prometheus-ready metrics, and service discovery.

## Architecture & Microservices

This repository is structured as a multi-module Maven project consisting of several independent microservices:

1. **gym-main-service** (Port `8080`): The core Spring Boot REST API for managing trainees, trainers, and training sessions. It handles authentication, data access (H2/PostgreSQL), and main business logic.
2. **gym-discovery-service** (Port `8761`): A Spring Cloud Netflix Eureka Server acting as the central service registry. It allows microservices to register and discover each other without hardcoding network addresses.
3. **trainee-workload-service** (Port `8081`): A separate microservice for processing and aggregating trainer workload. It receives training events from the main service and maintains monthly workload totals in its own database.

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

- `gym-main-service/` - The core CRM REST API
- `gym-discovery-service/` - Eureka Discovery Server
- `trainee-workload-service/` - Trainer workload aggregation service
- `gym-main-service/src/main/java/com/gym/crm/controller` - REST controllers
- `gym-main-service/src/main/java/com/gym/crm/service` - business logic
- `gym-main-service/src/main/java/com/gym/crm/dao` - data access layer
- `gym-main-service/src/main/java/com/gym/crm/entity` - JPA entities
- `gym-main-service/src/main/java/com/gym/crm/config` - security, OpenAPI, web config
- `gym-main-service/src/main/resources` - profile configs and seed data (`data.sql`)

## Prerequisites

- JDK 17+
- Maven 3.9+

## Run the Application

Start the **Eureka Discovery Server** (`gym-discovery-service`) first on port `8761`.
Then start the other services.

For `gym-main-service`, the default profile is `local` (in-memory H2).

```bat
cd gym-main-service
mvn clean spring-boot:run
```

Run with a specific profile (Windows `cmd.exe`):

```bat
set SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

For `stg` and `prod` under `gym-main-service`, set database credentials first:

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
