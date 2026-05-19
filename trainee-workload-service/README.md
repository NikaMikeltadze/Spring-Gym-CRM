# Trainer Workload Service

`Trainer Workload Service` is a small Spring Boot microservice that is part of the Spring-Gym-CRM project. It receives training events from the main trainer management system and keeps monthly workload totals in an in-memory H2 database, as well as a MongoDB database (NoSQL task).

## What it does

- Accepts workload updates when a training session is added or deleted.
- Aggregates training duration by trainer, year, and month.
- Concurrently stores totals to H2 via JPA and MongoDB via Spring Data MongoDB.
- Returns the stored monthly workload for a trainer on demand.

## Running MongoDB

This service requires a running instance of MongoDB to log and store NoSQL data. You can start it locally using Docker:

```cmd
docker run -d --name mongodb -p 27017:27017 mongo:latest
```

### NoSQL Task Details

- **Host**: `localhost`
- **Port**: `27017`
- **Database**: `gym`
- **Collection**: Stores `TrainerTrainingSummary` documents containing nested year and month summaries. 

The MongoDB connection string is configured in `application.properties` as `spring.data.mongodb.uri=mongodb://localhost:27017/gym`. You must ensure MongoDB is running before bringing down/starting up the Trainer Workload microservice or it may lose the ability to flush metric events.

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
