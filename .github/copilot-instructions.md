# Copilot Instructions for Nexora

## Overview

Nexora Backend is a modular, reactive IoT automation backend platform written in Spring Boot (Kotlin), following Domain Driven Design and best coding practices. It is designed for scalability, maintainability, and event-driven architecture.

---

## Directory Structure

- `/nexora-backend` - Spring Boot (Kotlin), DDD, MongoDB, reactive APIs, bounded contexts
- `/nexora-infra` - Infrastructure as code, deployment scripts, docker-compose, Kubernetes manifests

---

## Backend (Spring Boot Reactive, Kotlin) — Copilot Instructions

The backend is implemented in **Spring Boot** (Kotlin) using **reactive programming** throughout. All code should be non-blocking, event-driven, and leverage the full power of the reactive stack (WebFlux, coroutines, and reactive MongoDB drivers). Follow these guidelines for all contributions:

- **Reactive by Default:**

  - All APIs, data flows, and repository interactions must be non-blocking and reactive. Use `suspend` functions and coroutines for asynchronous logic.
  - Use Project Reactor or Kotlin coroutines for composing reactive flows.
  - Avoid blocking calls (e.g., `Thread.sleep`, synchronous I/O, or classic Spring MVC patterns).

- **Domain Driven Design (DDD):**

  - Organize code into clear bounded contexts (e.g., User Management, Device Management, Licensing, Rules Engine).
  - Each context should have its own domain models, aggregates, value objects, repositories, and services.
  - Use aggregates to enforce invariants and encapsulate business logic.
  - Keep domain logic pure and free from infrastructure concerns.

- **Persistence:**

  - Use **reactive MongoDB repositories** for all persistence.
  - Each bounded context should have its own collections as needed.
  - Avoid blocking database operations; always use reactive drivers.

- **API Design:**

  - Expose REST endpoints using **Spring WebFlux** controllers.
  - Use DTOs for API boundaries and map to domain models internally.
  - Ensure all endpoints are non-blocking and return reactive types (`Mono`, `Flux`, or `suspend` functions).

- **Event-Driven Architecture:**

  - Use **Kafka** for asynchronous communication between BFFs and backend.
  - Publish and consume events in a reactive, non-blocking manner.
  - Apply event sourcing and CQRS patterns where appropriate.

- **Best Practices & Design Patterns:**

  - Use the **Repository**, **Service**, **Factory**, and **Builder** patterns where applicable.
  - Apply the **Strategy** and **Observer** patterns for extensibility and event handling.
  - Use **Value Objects** for domain concepts that require immutability and equality.
  - Write **unit and integration tests** for all modules, especially for domain logic and reactive flows.
  - Use **environment variables** for all secrets and configuration.
  - Apply **error handling** and **logging** using reactive best practices (e.g., `onErrorResume`, `log()` operators).

- **Code Quality:**

  - Use code linting and formatting tools (e.g., `ktlint`).
  - Document all public APIs and domain models.
  - Follow naming conventions: `camelCase` for variables/functions, `PascalCase` for classes/types, `kebab-case` for files/directories.

- **Deployment:**
  - Containerize the backend using Docker.
  - Use environment variables for all configuration and secrets in containers.
  - Support zero-downtime deployments with health checks and rolling updates.

**Summary:**

> All backend code must be reactive, non-blocking, and follow DDD principles. Organize by bounded context, use best design patterns, and ensure maintainability, scalability, and testability at all times.

## ESP32 (PlatformIO)

- Use **MQTT** for communication with BFF-IOT.
- Handle WiFi setup, device registration, telemetry, and actuator control.
- Use secure credentials for MQTT connections.
- Support OTA updates if required.

---

## Industry-Standard Best Coding Practices

- **Code Quality & Readability:**

  - Write clean, self-documenting code with meaningful names and clear structure.
  - Keep functions and classes small and focused on a single responsibility (SRP).
  - Use comments judiciously to explain why (not what) when the intent is not obvious.
  - Avoid code duplication; use abstraction and reuse where appropriate.

- **Testing:**

  - Follow Test-Driven Development (TDD) or write tests alongside features.
  - Ensure high test coverage for business logic, especially for domain and service layers.
  - Use mocks and stubs for external dependencies in unit tests.
  - Write integration tests for critical flows and persistence.

- **Security:**

  - Validate and sanitize all user input and external data.
  - Use secure authentication and authorization mechanisms.
  - Store secrets and credentials securely (never hard-code them).
  - Keep dependencies up to date and monitor for vulnerabilities.

- **Maintainability:**

  - Refactor code regularly to improve structure and reduce technical debt.
  - Use SOLID principles and design patterns to guide architecture.
  - Modularize code by feature or bounded context.
  - Document architecture decisions and important business rules.

- **Collaboration:**
  - Use clear, descriptive commit messages and follow the repository's branching strategy.
  - Perform code reviews for all pull requests; provide constructive feedback.
  - Keep documentation up to date with code changes.
  - Communicate design decisions and changes with the team.

---

- Use environment variables for all secrets and endpoints.
- Write unit and integration tests for all modules.
- Use code linting and formatting tools (e.g., ESLint, Prettier, ktlint).
- Document all public APIs and MQTT topics.
- Use CI/CD for automated builds, tests, and deployments.
- Follow best practices for security, error handling, and logging.

---

## Deployment

- Backend and ESP32 firmware build server should be containerized using Docker.
- Each service should have its own `Dockerfile` and be published as a versioned Docker image.
- Use `docker-compose` or Kubernetes manifests for local development and production orchestration.
- Environment variables must be used for all configuration and secrets in Docker containers.
- For deployment:
  - Build Docker images for each component.
  - Push images to a container registry (e.g., Docker Hub, GitHub Container Registry).
  - Pull and run images on the target environment (local, staging, production).
  - Use rolling updates and health checks for zero-downtime deployments.
- ESP32 firmware is built and flashed separately, but OTA updates can be managed via the backend as needed.

---

## Backend Architecture (Reactive DDD & Bounded Context)

- Structure the backend as a **modular monolith** with clear bounded contexts, each representing a business domain.
- Each context contains its own domain models, aggregates, value objects, repositories (reactive), and services.
- Use **reactive REST** and **Kafka events** for inter-context and external communication.
- Design so that contexts can be split into independent microservices in the future with minimal refactoring.
- Enforce strict domain boundaries; never leak domain logic between contexts.
- Use **event-driven** and **reactive** patterns for all cross-context communication.
- Leverage **reactive MongoDB** collections per context as needed.

---

## Naming Conventions

- Use `camelCase` for variables and functions.
- Use `PascalCase` for components, classes, and types.
- Use `kebab-case` for file and directory names.

---

## Communication

- Backend <-> ESP32 Devices: MQTT (via external BFF-IOT service, not covered here)
- Backend <-> Other services: HTTP (REST), Kafka (event-driven)

---

## Contribution

- Follow the PR and code review process.
- Write clear commit messages.
- Update documentation with every feature or change.

---

For more details, refer to the documentation in each module's README.
