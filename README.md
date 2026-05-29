# Spring Gym CRM

Spring Gym CRM is a microservices-based project for managing trainees, trainers, and training sessions.
It includes authentication, profile management, training scheduling, health checks, Prometheus-ready metrics, and service discovery.

## Architecture & Microservices

This repository is structured as a multi-module Maven project consisting of several independent microservices:

1. **gym-main-service** (Port `8080`): The core Spring Boot REST API for managing trainees, trainers, and training sessions. It handles authentication, data access (H2/PostgreSQL), and main business logic.
2. **gym-discovery-service** (Port `8761`): A Spring Cloud Netflix Eureka Server acting as the central service registry. It allows microservices to register and discover each other without hardcoding network addresses.
3. **trainee-workload-service** (Port `8081`): A separate microservice for processing and aggregating trainer workload. It receives training events asynchronously via ActiveMQ from the main service and maintains monthly workload totals in its own database.

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
- Spring Boot 4.0.5 / Spring Cloud 2025.1.1
- Spring Web, Validation, Data JPA, Security, Actuator, JMS
- H2 (local/dev) and PostgreSQL (stg/prod)
- Apache ActiveMQ (Message-Oriented Middleware)
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
- Docker Desktop (for running the ActiveMQ MOM and MongoDB)

## Run the Application

First, start the required infrastructure using Docker.

Start the **ActiveMQ** Message-Oriented Middleware (MOM) broker:

```bat
docker run -d --name activemq -p 61616:61616 -p 8161:8161 rmohr/activemq
```

*(Note: ActiveMQ broker listens on TCP 61616 for messaging. The Web Admin Console runs on port 8161 and can be accessed at [http://localhost:8161/admin/](http://localhost:8161/admin/). The default credentials are `admin` / `admin`.)*

Start the **MongoDB** database (used by `trainee-workload-service` for NoSQL storage):

```bat
docker run -d --name mongodb -p 27017:27017 mongo:latest
```

The NoSQL task aggregates and logs trainer workloads to the `gym` database on the default port `27017`.

Next, start the **Eureka Discovery Server** (`gym-discovery-service`) on port `8761`.
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

## Run with Docker

Each service ships a multi-stage `Dockerfile` (Maven build → JRE runtime), and a root
`docker-compose.yml` wires the full stack on a dedicated bridge network. Docker Desktop
is the only prerequisite — Maven/JDK are not needed on the host.

> The command blocks below use Windows `cmd.exe` syntax (`^` for line continuation). In
> **PowerShell** use a backtick (`` ` ``) instead, and in **bash/zsh** use a backslash (`\`).
> Mode 1 and Mode 2 cannot run at the same time — they use the same container names and host
> ports (8080/8081). Tear one down before starting the other (see [Teardown](#teardown)).

### Mode 1 — standalone (integrations disabled)

Build the images, then run each service with in-memory H2 only (no database, queue, or
discovery required):

```bat
docker build -t gym-main:standalone ./gym-main-service
docker build -t gym-report:standalone ./trainee-workload-service
docker build -t gym-discovery:standalone ./gym-discovery-service
```

```bat
docker run -d --name gym-main -p 8080:8080 ^
  -e SPRING_PROFILES_ACTIVE=local ^
  -e EUREKA_CLIENT_ENABLED=false ^
  -e SPRING_CLOUD_DISCOVERY_ENABLED=false ^
  gym-main:standalone

docker run -d --name gym-report -p 8081:8081 ^
  -e EUREKA_CLIENT_ENABLED=false ^
  -e SPRING_CLOUD_DISCOVERY_ENABLED=false ^
  -e SPRING_JMS_LISTENER_AUTO_STARTUP=false ^
  gym-report:standalone
```

Quick check (no auth needed):

```bat
REM expected: 201
curl -X POST http://localhost:8080/api/trainee/register -H "Content-Type: application/json" -d "{\"firstName\":\"Test\",\"lastName\":\"User\",\"dateOfBirth\":\"1990-01-01\",\"address\":\"1 Demo St\"}"

REM expected: 404 (service up, trainer unknown)
curl "http://localhost:8081/api/workloads/nobody/workload?year=2026&month=1"
```

### Mode 2 — full stack (integrations enabled)

`docker-compose.yml` brings up PostgreSQL, MongoDB, ActiveMQ, Eureka, and both application
services on a shared `gym-net` bridge network. Services reach each other by container name.

```bat
docker compose up --build -d
```

Eureka registration lags startup by ~30s. Once the dashboard at
[http://localhost:8761](http://localhost:8761) lists `GYM-MAIN-SERVICE` and `TRAINER-WORKLOAD`,
the stack is ready.

#### End-to-end walkthrough (proves the integrations)

This exercises the full path: a training is added on the main service → published to ActiveMQ
→ consumed by the report service → stored in H2 + MongoDB → read back through Eureka/OpenFeign.
All of it can be driven from **Swagger UI** at
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html):

1. **Register a trainer** (`POST /api/trainer/register`). The response includes a generated
   `username`, `password`, and a JWT `token` — copy the token.
2. **Register a trainee** (`POST /api/trainee/register`) — note its generated `username`.
3. Click **Authorize** (top-right in Swagger), paste the trainer token, and confirm — every
   protected call now sends the `Bearer` header.
4. **Add a training** (`POST /api/training/add`) with the two usernames. **`trainingName` must
   be an existing training type** — one of `Fitness`, `Yoga`, `Zumba`, `Stretching`,
   `Resistance` — otherwise it fails with `400 Training type not found`. Expect `201 Created`.
5. **Read the monthly workload** (`GET /api/trainer/{username}/workload`) for the trainer, with
   the `year`/`month` of the training. The main service resolves the report service via
   Eureka/OpenFeign; a non-zero `trainingSummaryDuration` confirms the ActiveMQ → report → Feign
   round trip worked.

You can also see the stored NoSQL document directly:

```bat
docker exec mongo mongosh gym --quiet --eval "db.trainer_training_summary.find().toArray()"
```

#### Manual network setup (without Compose)

```bat
docker network create gym-net
docker run -d --network gym-net --name postgres -e POSTGRES_DB=gymdb -e POSTGRES_USER=gym -e POSTGRES_PASSWORD=gym -p 5432:5432 postgres:16
docker run -d --network gym-net --name mongo -p 27017:27017 mongo:latest
docker run -d --network gym-net --name activemq -p 8161:8161 rmohr/activemq
docker run -d --network gym-net --name discovery -p 8761:8761 gym-discovery:standalone
docker run -d --network gym-net --name gym-report -p 8081:8081 ^
  -e SPRING_MONGODB_URI=mongodb://mongo:27017/gym ^
  -e SPRING_DATA_MONGODB_URI=mongodb://mongo:27017/gym ^
  -e SPRING_ACTIVEMQ_BROKER_URL=tcp://activemq:61616 ^
  -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery:8761/eureka/ ^
  gym-report:standalone
docker run -d --network gym-net --name gym-main -p 8080:8080 ^
  -e SPRING_PROFILES_ACTIVE=local ^
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/gymdb ^
  -e SPRING_DATASOURCE_USERNAME=gym -e SPRING_DATASOURCE_PASSWORD=gym ^
  -e SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect ^
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=update -e SPRING_H2_CONSOLE_ENABLED=false ^
  -e SPRING_ACTIVEMQ_BROKER_URL=tcp://activemq:61616 ^
  -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery:8761/eureka/ ^
  gym-main:standalone
```

### Inspect containers and logs

```bat
docker compose ps
docker compose logs -f gym-main
docker exec -it gym-main sh
REM inside the container: env | sort   OR   ps -ef
docker exec mongo mongosh gym --eval "db.getCollectionNames()"
```

### Teardown

Mode 1 (standalone) and Mode 2 (Compose) share container names and host ports (8080/8081), so
stop one before starting the other — otherwise you get `container name already in use` or
`port is already allocated`.

```bat
REM stop the standalone (Mode 1) containers
docker rm -f gym-main gym-report

REM stop the Compose stack (Mode 2); add -v to also drop the Postgres volume for a clean re-run
docker compose down
docker compose down -v
```

### Notes / caveats

- **ActiveMQ broker port (61616) is not published to the host** in `docker-compose.yml`.
  Inter-service traffic uses the `gym-net` network, and on Windows port 61616 falls inside
  the reserved WinNAT range (`61564-61663`) and fails to bind. Only the ActiveMQ web
  console (`8161`) is published. If you need 61616 on the host, pick a free host port
  (e.g. `61617:61616`).
- The `local` profile re-runs `data.sql` on every startup. Restarting `gym-main` against
  the persistent PostgreSQL container will fail on duplicate primary keys. For a clean
  re-run use `docker compose down -v && docker compose up -d`.

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
