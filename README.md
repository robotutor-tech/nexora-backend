# Nexora Backend

A modular, reactive IoT automation backend built with Spring Boot 3 (Kotlin), WebFlux, Reactive MongoDB, Reactor Kafka, and Domain-Driven Design (DDD). The codebase is intentionally structured as a modular monolith with **explicit bounded contexts** that can be split into independent services later with minimal refactoring.

---
## Table of Contents
1. Vision & Purpose
2. High-Level Architecture
3. Domain-Driven Design in This Project
4. Bounded Contexts
5. Layering & Package Conventions
6. Entity / Aggregate / Value Object Design
7. Reactive & Event-Driven Architecture
8. Persistence Strategy
9. Cross-Cutting Concerns (Security, Logging, IDs)
10. Example Domain Flow (Invitation Acceptance)
11. Module Template (How to Add a New Context)
12. Testing Strategy & Quality Gates
13. Build, Run & Tooling
14. Coding & Architectural Guardrails
15. Migration Path to Microservices
16. FAQ / Decision Log

---
## 1. Vision & Purpose
Nexora orchestrates automation across premises, devices, zones, and users. The backend provides:
- Auth & identity boundaries
- Device + premises modeling
- Automation & rule execution (future expansion)
- Reactive APIs & event publishing for downstream consumers
- Clear domain boundaries to avoid coupling and enable future decomposition

---
## 2. High-Level Architecture
```
                ┌──────────────────────────┐
                │        API Layer         │  (WebFlux Controllers)
                └─────────────┬────────────┘
                              │ DTO ↔ Domain Mapping
                ┌─────────────▼────────────┐
                │     Application Layer    │  (Commands, Services, Strategies)
                └─────────────┬────────────┘
                              │ invokes
                ┌─────────────▼────────────┐
                │       Domain Layer       │  (Aggregates, Entities, Value Objects, Domain Events)
                └─────────────┬────────────┘
                      Domain Events▲  │Repositories (interfaces)
                ┌─────────────▼────────────┐
                │   Infrastructure Layer   │  (Mongo Repos, Mappers, Kafka Publishers)
                └──────────────────────────┘
                              │
                      External Systems (MongoDB, Redis, Kafka)
```
Cross-cutting modules (security, shared kernel, logging) provide reusable abstractions WITHOUT leaking domain logic.

---
## 3. Domain-Driven Design in This Project
DDD principles applied:
- Bounded Contexts: Each domain area (e.g., `auth`, `device`, `premises`) is isolated under `modules/<context>`.
- Ubiquitous Language: Code constructs (e.g., `Invitation`, `PremisesId`, `ZoneId`, `ActorId`) reflect business terminology.
- Aggregates enforce invariants (e.g., `Invitation.markAsAccepted()` emits `InvitationAcceptedEvent`).
- Domain Events decouple write-side operations from asynchronous concerns (Kafka publishing).
- Value Objects encapsulate identity, validation, and formatting concerns (see `shared/domain/model/ValueObjects.kt`).
- Repositories are **domain-facing interfaces**; Mongo implementations live in `infrastructure/persistence`.
- Application Layer orchestrates domain operations—no business rules in controllers or persistence.

---
## 4. Bounded Contexts
Current contexts (under `src/main/kotlin/com/robotutor/nexora/modules`):
- `auth` – Invitations, tokens, auth users, registration events.
- `user` – User identity & profile (extension point).
- `iam` – Access control / entitlements (integration via security ports).
- `device` – Device registry & metadata (future automation integration).
- `premises` – Physical premises, hierarchical structure.
- `zone` – Sub-areas within premises.
- `automation` – Automation/rules (scaffolding for future rule engine).
- `widget` – UI/visual components domain (projection layer growth area).
- `feed` – Activity feed / event projection (future read model).
- `seed` – Bootstrapping / data seeding logic (isolated to avoid leaking into domain services).

Supporting shared modules:
- `common.security` – Security strategies, ports, token validation abstractions.
- `shared` – Shared kernel (Value Objects, ID types, domain base classes, logging, mappers, ID generation).

Each context owns:
- Its aggregates & entities
- Its repositories & persistence mapping
- Its domain events & event mappers
- Its REST controllers (only for its API surface)

No domain object crosses context boundaries directly—use DTOs or events.

---
## 5. Layering & Package Conventions
Within a bounded context (e.g., `auth`):
```
modules/auth/
  domain/
    entity/        (Aggregates + Entities)
    event/         (Domain Events)
    repository/    (Repository interfaces)
    service/       (Pure domain services if needed)
    exception/     (Domain-specific errors)
  application/
    command/       (Command objects / intent models)
    dto/           (Inbound/Outbound API DTOs)
    factory/       (Aggregate / Value Object creation helpers)
    strategy/      (Pluggable behaviors)
  infrastructure/
    persistence/
      document/    (Mongo documents)
      mapper/      (Domain ↔ Document mappers)
      repository/  (Reactive Mongo implementations)
    messaging/
      message/     (Serialized event payload models)
      mapper/      (Domain Event → Message mapping)
  interfaces/
    controller/    (WebFlux REST controllers)
  config/          (Context-local Spring configs)
```
Separation rules:
- Controllers never access `infrastructure` directly.
- Domain layer has zero Spring annotations (pure Kotlin where possible).
- Application layer wires domain + repos + events.
- Infrastructure depends inward only (never the reverse).

---
## 6. Entity / Aggregate / Value Object Design
Example Aggregate: `Invitation` (`auth` context)
```kotlin
// domain/entity/Invitation.kt
fun markAsAccepted(): Invitation {
    this.status = InvitationStatus.ACCEPTED
    this.addDomainEvent(InvitationAcceptedEvent(invitationId))
    return this
}
```
Key patterns:
- Mutating methods return `this` for fluent chaining in orchestration code.
- Invariants enforced inside aggregate methods (never from controllers).
- `DomainAggregate<TEvent>` base captures and exposes uncommitted domain events.

Value Objects (in `shared/domain/model`):
- Strongly typed IDs (e.g., `InvitationId`, `PremisesId`, `ZoneId`) prevent mixing unrelated identifiers.
- Reduced primitive obsession improves correctness and readability.

---
## 7. Reactive & Event-Driven Architecture
Reactive Principles:
- All persistence via `spring-boot-starter-data-mongodb-reactive`.
- Composition using Reactor + Kotlin coroutines (`kotlinx-coroutines-reactor`).
- No blocking calls—IO is non-blocking edge to edge.

Event Flow:
1. Aggregate method adds domain events.
2. Application service persists aggregate + retrieves pending events.
3. Events mapped to Kafka messages (in `infrastructure/messaging/mapper`).
4. Publisher (`AuthEventPublisher`) sends via Reactor Kafka / Spring Kafka.

Benefits:
- Loose coupling between write model and projections.
- Extensible for CQRS / read model generation (`feed`, `widget` contexts later).

---
## 8. Persistence Strategy
- Repositories expose domain interfaces (`domain/repository/*Repository.kt`).
- Implementations: Reactive Mongo (`infrastructure/persistence/repository`).
- Separate document models vs domain models (mapping layer isolates schema evolution).
- ID generation delegated to `MongoIdGeneratorService` (shared infrastructure).
- Versioning support (optimistic locking placeholder via `version` field in aggregates where needed).

Caching / Redis:
- Reactive Redis configured (starter present) for future token/session/lookup caches.

---
## 9. Cross-Cutting Concerns
Security (`common.security`):
- Ports define retrieval contracts (`UserDataRetriever`, `InvitationDataRetriever`, etc.).
- Strategies implement retrieval paths (internal vs token-based vs device-based).
- Token validation isolated (`AuthTokenValidator`).

Logging (`shared/logger`):
- Reactive context propagation with structured log details.

ID & Value Semantics (`shared/domain/model`):
- Centralized canonical types ensure consistency across contexts.

---
## 10. Example Domain Flow: Invitation Acceptance
Sequence (Happy Path):
1. HTTP `POST /auth/invitations/{id}/accept` hits `InvitationController`.
2. Controller validates path/body → delegates to application command handler.
3. Application service loads `Invitation` via `InvitationRepository`.
4. Calls `invitation.markAsAccepted()` → adds `InvitationAcceptedEvent` to aggregate.
5. Persists updated invitation (reactive Mongo repository).
6. Collects domain events; maps to messaging payload(s).
7. Publishes Kafka message (async, non-blocking).
8. Controller returns response (no blocking on consumer side-effects).

Error Handling:
- Domain invariants throw domain exceptions.
- Application layer translates to HTTP errors (Spring WebFlux exception handlers—future centralization possible).

---
## 11. Module Template: Adding a New Bounded Context
1. Create folder: `modules/<context>`.
2. Define domain model first:
   - `domain/entity`, `domain/value` (if local), `domain/event`, `domain/repository`.
3. Add application orchestrators (commands, services, strategies if variation exists).
4. Add persistence mappers + repositories (infrastructure) ONLY after domain stabilizes.
5. Add messaging (event publication) only if domain emits events.
6. Expose REST endpoints under `interfaces/controller`.
7. Provide integration tests (repository + API + event emission) under `src/integrationTest`.
8. Keep cross-context calls via events or dedicated facades (never import another context's domain types).

Scaffold Example:
```
modules/example/
  domain/
    entity/Example.kt
    event/ExampleCreatedEvent.kt
    repository/ExampleRepository.kt
  application/
    command/CreateExampleCommand.kt
    service/ExampleApplicationService.kt
  infrastructure/
    persistence/document/ExampleDocument.kt
    persistence/mapper/ExampleDocumentMapper.kt
    persistence/repository/MongoExampleRepository.kt
    messaging/mapper/ExampleEventMapper.kt
    messaging/ExampleEventPublisher.kt
  interfaces/controller/ExampleController.kt
  config/ExampleConfig.kt
```

---
## 12. Testing Strategy & Quality Gates
Test Types:
- Unit Tests: Pure domain logic + application services (Mock repos).
- Integration Tests: Reactive Mongo (embedded via `flapdoodle`), HTTP endpoints, Kafka publishing (using `spring-kafka-test`).
- (Future) Contract / Consumer-Driven Tests for events.

Coverage Enforcement (`build.gradle.kts`):
- Line ≥ 80%, Branch ≥ 80%, Class ≥ 80%, Method ≥ 75%, Instruction ≥ 75%, Complexity ≥ 70%.
- Guardrail: No class < 20% line coverage.

Jacoco pipeline target ensures architectural confidence before merge.

Recommended Patterns:
- Use `StepVerifier` or coroutines test utilities for reactive flows.
- Assert domain events emitted after state transitions.

---
## 13. Build, Run & Tooling
Prerequisites: Java 21, Docker (for Mongo/Redis/Kafka if running locally).

Build:
```bash
./gradlew clean build
```

Run (local):
```bash
./gradlew bootRun
```

Integration Tests:
```bash
./gradlew integrationTest
```

Coverage Report (HTML): `build/jacocoHtml/index.html`.

Key Dependencies:
- Spring Boot 3.4.x WebFlux
- Reactive MongoDB Starter
- Reactor Kafka + Spring Kafka
- Kotlin Coroutines Reactor
- Redis Reactive Starter

---
## 14. Coding & Architectural Guardrails
Do:
- Keep domain pure (no Spring annotations in entities/value objects).
- Emit domain events inside aggregates—not in services.
- Use Value Objects over primitives.
- Fail fast with explicit domain exceptions.
- Map boundary DTOs in the application layer (avoid leaking domain internals).

Avoid:
- Cross-context imports of domain packages.
- Placing business rules in controllers or repositories.
- Blocking calls (`Thread.sleep`, JDBC drivers, synchronous clients).
- Direct usage of infrastructure models in domain logic.

Static Analysis / Consistency:
- (Optional) Add ktlint/Detekt future—placeholder for pipeline enhancement.

---
## 15. Migration Path to Microservices
The modular monolith is intentionally structured for extraction:
- Each context already encapsulates domain + adapters.
- Kafka events form natural async seams.
- Shared kernel (`shared`) is minimal & stable (Value Objects + infrastructure utilities).
- Security strategies abstract identity/entitlement resolution; can be externalized.

Extraction Steps (future):
1. Identify context workload (e.g., `auth`).
2. Extract module as a standalone service reusing same package boundaries.
3. Replace in-process repository/event calls with HTTP/Kafka equivalents.
4. Keep shared kernel versioned across services.

---
## 16. FAQ / Decision Log
Q: Why modular monolith first?
A: Faster iteration, cohesive refactoring, avoids premature distributed complexity.

Q: Why both Reactor + Coroutines?
A: Spring WebFlux core is Reactor; coroutines provide idiomatic Kotlin bridging (`kotlinx-coroutines-reactor`).

Q: Why explicit mappers?
A: Prevent domain leakage + allow persistence schema evolution.

Q: Why domain events vs direct service calls across contexts?
A: Encourages autonomy + enables eventual split to microservices.

Q: Why strong ID types?
A: Compile-time safety (prevents mixing unrelated identifiers) and readability.

---
## Appendix: Directory Snapshot (Truncated)
```
src/main/kotlin/com/robotutor/nexora/
  modules/
    auth/... (see structure above)
    device/
    premises/
    zone/
    automation/
    feed/
    user/
    iam/
    widget/
    seed/
  common/security/... (ports, strategies, facades)
  shared/
    domain/model/ (Value Objects, IDs)
    infrastructure/persistence/ (Generic Mongo abstractions)
    logger/ (Reactive logging helpers)
```

---
## Contributing
1. Open an architectural issue for new bounded contexts.
2. Model domain language (entities, value objects, events) first.
3. Add application orchestration + persistence adapters.
4. Write tests (unit first, then integration).
5. Keep coverage green; do not lower thresholds.
6. Submit PR with concise, domain-oriented commit messages.

---
## Future Enhancements (Backlog Candidates)
- Rule Engine in `automation` context
- Event versioning + schema registry
- Read model projections for `feed` and `widget`
- Central exception handling / problem+json responses
- Observability: OpenTelemetry tracing
- Policy-based authorization in `iam`

---
If you are reviewing this as a technical principal: the structure was intentionally optimized for **clarity of domain boundaries**, **reactive purity**, and **incremental scalability**. Feedback on boundary sharpness, event granularity, and shared-kernel surface is welcome.

