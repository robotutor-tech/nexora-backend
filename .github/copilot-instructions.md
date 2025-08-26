# Copilot Instructions for Nexora

## Overview
Nexora is a modular IoT automation platform with the following components:
- **UI**: Next.js, TypeScript, Zustand, Material-UI, Axios
- **BFF (Backend For Frontend)**: NestJS microservices (bff-ui, bff-iot, bff-mqtt-handler)
- **Backend**: Spring Boot (Kotlin), MongoDB, Domain Driven Design, Reactive Programming
- **Device Firmware**: ESP32 (PlatformIO, MQTT)

---

## Directory Structure
- `/nexora-ui` - Next.js frontend
- `/nexora-bff/bff-ui` - Handles UI requests, forwards to backend, uses Kafka & WebSocket (EMQX)
- `/nexora-bff/bff-iot` - Handles device (ESP32/Raspberry Pi) MQTT requests, communicates with backend via Kafka
- `/nexora-bff/mqtt-handler` - Authenticates MQTT connections, authorizes topic access
- `/nexora-backend` - Spring Boot (Kotlin), DDD, MongoDB, reactive APIs, bounded contexts
- `/nexora-infra` - Infrastructure as code, deployment scripts, docker-compose, Kubernetes manifests
- `/esp32` - PlatformIO firmware for ESP32 devices

---

## UI (Next.js)
- Use **Zustand** for state management.
- Use **Material-UI** for UI components.
- Use **Axios** for API calls to `/bff-ui` endpoints.
- All API requests should be typed (TypeScript interfaces).
- Use environment variables for API base URLs.
- Use React hooks for data fetching and state updates.

## BFF-UI (NestJS)
- Receives HTTP/WebSocket requests from UI.
- Forwards requests to backend via Kafka.
- Uses EMQX for WebSocket communication.
- Handles authentication/authorization for UI users.
- Expose REST endpoints for UI, and WebSocket endpoints for real-time updates.

## BFF-IOT (NestJS)
- Handles MQTT requests from ESP32/Raspberry Pi devices.
- Forwards device data/events to backend via Kafka.
- Receives commands from backend and publishes to devices via MQTT.
- Handles device registration, status, and telemetry.

## BFF-MQTT-HANDLER (NestJS)
- Authenticates MQTT connections (username/password, tokens).
- Authorizes publish/subscribe access to topics per device/user.
- Integrates with backend for dynamic topic permissions.

## Backend (Spring Boot, Kotlin)
- Use **Domain Driven Design** for business logic.
- Use **MongoDB** for persistence.
- Use **Reactive Programming** (WebFlux, coroutines).
- Expose REST and Kafka endpoints for BFFs.
- Implement event-driven communication (Kafka topics for commands/events).
- Use design patterns for maintainability and scalability.

## ESP32 (PlatformIO)
- Use **MQTT** for communication with BFF-IOT.
- Handle WiFi setup, device registration, telemetry, and actuator control.
- Use secure credentials for MQTT connections.
- Support OTA updates if required.

---

## General Guidelines
- Use environment variables for all secrets and endpoints.
- Write unit and integration tests for all modules.
- Use code linting and formatting tools (e.g., ESLint, Prettier, ktlint).
- Document all public APIs and MQTT topics.
- Use CI/CD for automated builds, tests, and deployments.
- Follow best practices for security, error handling, and logging.

---

## Deployment
- All major components (UI, BFF-UI, BFF-IOT, BFF-MQTT-HANDLER, Backend, ESP32 firmware build server) should be containerized using Docker.
- Each service should have its own `Dockerfile` and be published as a versioned Docker image.
- Use `docker-compose` or Kubernetes manifests for local development and production orchestration.
- Environment variables must be used for all configuration and secrets in Docker containers.
- For deployment:
  - Build Docker images for each component.
  - Push images to a container registry (e.g., Docker Hub, GitHub Container Registry).
  - Pull and run images on the target environment (local, staging, production).
  - Use rolling updates and health checks for zero-downtime deployments.
- ESP32 firmware is built and flashed separately, but OTA updates can be managed via the BFF-IOT service.

---

## Backend Architecture (Domain Driven Design & Bounded Context)
- The backend must be organized into bounded contexts, each representing a distinct domain (e.g., User Management, Device Management, Licensing, Rules Engine, etc.).
- Each bounded context should have its own domain models, repositories, services, and API endpoints.
- Use aggregates, value objects, and entities as per DDD best practices.
- Bounded contexts should communicate via well-defined interfaces (REST, Kafka events).
- Initially, all bounded contexts can reside in a single codebase (modular monolith), but must be designed so they can be split into independent microservices in the future with minimal refactoring.
- Strictly enforce domain boundaries and avoid leaking domain logic between contexts.
- Use MongoDB collections per bounded context as needed.
- Apply event-driven patterns for cross-context communication where appropriate.

---

## Naming Conventions
- Use `camelCase` for variables and functions.
- Use `PascalCase` for components, classes, and types.
- Use `kebab-case` for file and directory names.

---

## Communication
- UI <-> BFF-UI: HTTP (REST), WebSocket
- BFF-UI <-> Backend: HTTP (REST), Kafka
- BFF-IOT <-> Devices: MQTT
- BFF-IOT <-> Backend: HTTP (REST), Kafka
- BFF-MQTT-HANDLER <-> EMQX: MQTT Auth/ACL

---

## Contribution
- Follow the PR and code review process.
- Write clear commit messages.
- Update documentation with every feature or change.

---

For more details, refer to the documentation in each module's README.
