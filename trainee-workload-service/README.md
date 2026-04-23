# Trainer Workload Service

`Trainer Workload Service` is a small Spring Boot microservice that is part of the Spring-Gym-CRM project. It receives training events from the main trainer management system and keeps monthly workload totals in an in-memory H2 database.

## What it does

- Accepts workload updates when a training session is added or deleted.
- Aggregates training duration by trainer, year, and month.
- Returns the stored monthly workload for a trainer on demand.

## REST API

- `POST /api/workloads` — stores a workload event.
- `GET /api/workloads/{trainerUsername}/workload?year={year}&month={month}` — returns the trainer's monthly training summary.

## Notes

- Uses H2 in-memory storage and runs on port `8081`.
- Application logs are enabled at `DEBUG` level for the service package.
- The H2 console is available during development.

## Run tests

```cmd
mvnw.cmd test
```
