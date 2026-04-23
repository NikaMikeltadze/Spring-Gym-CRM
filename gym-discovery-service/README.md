# Eureka Discovery Server

This is the central Service Registry for the microservices architecture, built with Spring Cloud Netflix Eureka.

## Overview
The Eureka Server acts as a discovery service, allowing microservices to register themselves and discover other registered services without hardcoding their hostnames and ports.

## Configuration
This server runs in **standalone mode**:
- Port: `8761`
- Self-registration is disabled (`eureka.client.register-with-eureka=false`).
- Registry fetching is disabled (`eureka.client.fetch-registry=false`).

## Running the Application
You can run the application using Maven:
```bash
./mvnw spring-boot:run
```
Or by running the `EurekaServerApplication.java` main class from your IDE.

## Accessing the Dashboard
Once the application is running, you can access the Eureka dashboard to view registered services at:
[http://localhost:8761](http://localhost:8761)
