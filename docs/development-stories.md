# Development Stories (Epics, Stories & Acceptance Criteria)

This document derives actionable development stories from the architectural intent of the Nexora Backend. It is organized by epics (aligned to bounded contexts and cross‑cutting capabilities). Each story follows the format:

Format:
As a <role> I want <capability> so that <business value>
Acceptance Criteria (AC): Numbered, testable.
Definition of Done (DoD): Code + tests + docs + coverage thresholds.

Story Point Scale (guideline):
1 = Trivial (config / small DTO), 2 = Simple (one class, no branching), 3 = Moderate (2–3 collaborating classes), 5 = Complex (multiple layers + persistence + events), 8 = High risk / multi-flow.

---
## Epic A: Authentication & Invitation Management (auth context)

### Story A1: Create Invitation Aggregate
As an internal system I want to create an invitation so that a user can later accept and onboard.
AC:
1. Invitation aggregate stores: invitationId, premisesId, zoneId, invitedBy, tokenId, status=INVITED, createdAt.
2. Invitation must expose markAsAccepted() that transitions status -> ACCEPTED and emits InvitationAcceptedEvent.
3. Validation: Cannot accept if already ACCEPTED (throws domain exception).
4. Repository persists and retrieves domain model (no leakage of persistence model).
DoD: Unit tests for aggregate behavior (state + emitted event), repository integration test.
Points: 3

### Story A2: Invitation REST Endpoint (Create)
As an admin I want to create an invitation via REST so that I can onboard a collaborator.
AC:
1. POST /auth/invitations with required fields returns 201 + invitationId.
2. Validation errors return 400 with field-level details.
3. Controller maps DTO -> domain via application service.
4. No domain event published yet on creation (only on acceptance).
DoD: WebFlux controller test + contract of response.
Points: 3

### Story A3: Accept Invitation
As an invitee I want to accept an invitation so that my account is activated.
AC:
1. POST /auth/invitations/{id}/accept accepts valid invitation.
2. Emits InvitationAcceptedEvent -> published to Kafka topic auth.invitation.accepted.
3. Idempotent: Accepting an already ACCEPTED invitation returns 409 or defined domain error code.
4. Correlation ID propagated in log context + Kafka message headers.
DoD: Integration test (HTTP -> Mongo -> Kafka mock), domain event assertion.
Points: 5

### Story A4: Register Auth User on Acceptance
As the platform I want to create an AuthUser entity when an invitation is accepted so the user can authenticate.
AC:
1. Consumes InvitationAcceptedEvent.
2. Creates AuthUser entry with linkage to invitationId + premisesId.
3. Handles duplicate gracefully (ignore / log).
DoD: Kafka consumer test + repository integration test.
Points: 5

### Story A5: Token Generation & Validation
As a user I want a secure token issued upon successful registration so I can access protected endpoints.
AC:
1. Token entity persisted with expiry.
2. Token validation strategy (internal vs device flows placeholder) behind TokenValidator port.
3. Expired tokens rejected with 401.
DoD: Unit tests (service + strategy), integration test (issue + validate + reject expired).
Points: 5

---
## Epic B: Premises & Zone Modeling (premises, zone contexts)

### Story B1: Define Premises Aggregate
As an operator I want to model a premises so that devices and zones can be scoped.
AC:
1. Premises aggregate fields: premisesId, name, createdAt.
2. Validation for name (length > 2, sanitized characters).
3. Repository + create endpoint.
DoD: Domain + repository + controller tests.
Points: 3

### Story B2: Define Zone Aggregate
As an operator I want to define zones inside a premises so that automation can target sub-areas.
AC:
1. Zone has zoneId, premisesId (FK as Value Object), name.
2. Cannot create zone with non-existent premises (application validation).
3. Repository + create/list by premises.
DoD: Integration test with embedded Mongo.
Points: 5

### Story B3: Query Zones by Premises
As a client app I want to list zones for a premises to drive UI selection.
AC:
1. GET /zones?premisesId= returns list sorted by name asc.
2. Returns 404 if premises does not exist.
DoD: Controller test (success + invalid premises).
Points: 2

---
## Epic C: Device Registry (device context)

### Story C1: Device Registration
As a device onboarding service I want to register a device so that it becomes addressable.
AC:
1. Device entity: deviceId, premisesId, zoneId (optional), status=REGISTERED.
2. Duplicate registration updates metadata (idempotent).
3. Event DeviceRegisteredEvent emitted.
DoD: Domain tests + Kafka publish integration.
Points: 5

### Story C2: Device Lookup API
As a backend service I need to fetch device metadata by ID for authorization decisions.
AC:
1. GET /devices/{id} returns device JSON or 404.
2. Cache layer optional (placeholder strategy interface).
DoD: Controller + repository test.
Points: 3

---
## Epic D: IAM / Access Control (iam context)

### Story D1: Entitlement Facade Integration
As the security layer I want a facade to query entitlements so that controllers remain thin.
AC:
1. Port EntitlementFacade defines contract (hasPermission(actorId, resource, action)).
2. Stub implementation returns ALLOW for MVP; logs decision.
DoD: Unit test for facade path.
Points: 2

### Story D2: Apply Authorization to Invitation Acceptance
As a security reviewer I want access checks on invitation acceptance so that only permitted actors perform the action.
AC:
1. Authorization executed before domain mutation.
2. Unauthorized returns 403 with audit log entry.
DoD: Integration test (authorized vs unauthorized).
Points: 3

---
## Epic E: Automation Foundation (automation context)

### Story E1: Rule Aggregate Skeleton
As a future automation developer I want a placeholder rule model so later logic can attach.
AC:
1. Rule entity: ruleId, premisesId, trigger definition (string placeholder), action definition (string placeholder), status.
2. Basic create + enable/disable state transitions with domain events.
DoD: Domain tests only (no API yet).
Points: 3

### Story E2: Rule Persistence & API
As an operator I want to create and list rules scoped to a premises.
AC:
1. POST /automation/rules creates rule.
2. GET /automation/rules?premisesId= lists rules.
3. Validation for required trigger/action placeholders.
DoD: Controller + repository test.
Points: 5

---
## Epic F: Event Streaming & Messaging

### Story F1: Kafka Event Publisher Abstraction
As a platform engineer I want a publisher abstraction so that domain events map uniformly to Kafka messages.
AC:
1. Interface DomainEventPublisher<T> with publish(events: Collection<T>).
2. AuthEventPublisher implements using Reactor Kafka or Spring Kafka template (non-blocking).
3. Correlation + event type headers added.
DoD: Unit test (mapper) + integration test with embedded broker (spring-kafka-test).
Points: 5

### Story F2: Event Mapper Versioning Strategy
As a maintainer I want versioned event payloads to allow evolution.
AC:
1. Event message models include version field.
2. Mapper sets version constant (v1) per event type.
3. Unknown version log warning on consumer side (placeholder logic).
DoD: Mapper test.
Points: 3

---
## Epic G: Observability & Logging

### Story G1: Reactive Context Logging
As an operator I want correlation IDs in every log line to trace requests across layers.
AC:
1. Filter populates Reactor Context with correlationId (or generated UUID).
2. Logger utility pulls from context automatically.
3. Logged in controller -> domain transition shows same ID.
DoD: Unit test for context propagation + manual log assertion in integration test.
Points: 5

### Story G2: Structured Error Logging
As a support engineer I need structured JSON logs for errors.
AC:
1. Error handler outputs JSON with fields (timestamp, correlationId, path, errorCode, message).
2. 5xx vs 4xx differentiated.
DoD: WebFlux error handler test (400 + 500 cases).
Points: 3

---
## Epic H: Shared Kernel & ID Generation

### Story H1: Mongo ID Sequence Service
As a developer I want consistent ID generation so domain entities avoid collisions.
AC:
1. MongoIdGeneratorService returns strongly typed ID value objects.
2. Each sequence type configurable (collection or counter pattern).
DoD: Unit test + integration test (increment guarantee).
Points: 3

### Story H2: Value Object Validation
As a domain modeler I want construction-time validation for core value objects.
AC:
1. Name, Mobile, Resource value objects enforce length/format invariants.
2. Invalid creation throws domain exception; not nullable fallback.
DoD: Unit tests for all failure + success cases.
Points: 3

---
## Epic I: Data Seeding (seed context)

### Story I1: Seed Initial Premises & Admin
As a platform operator I want a bootstrap command to create an initial premises and admin user for testing.
AC:
1. Runs only if environment variable SEED_ENABLED=true.
2. Idempotent: re-run does not duplicate.
DoD: Integration test (two executions -> single data set).
Points: 3

---
## Epic J: Widget & Feed Projections (future read models)

### Story J1: Projection Skeleton
As a product engineer I want a projection module to consume domain events for UI widgets.
AC:
1. Module skeleton with consumer subscribing to auth + device events.
2. No persistence yet; logs receipt.
DoD: Consumer wiring test with embedded Kafka.
Points: 2

---
## Cross-Cutting Non-Functional Stories

### Story NF1: Code Coverage Gate Enforcement
As a tech lead I want coverage thresholds enforced so quality remains high.
AC:
1. Jacoco thresholds in build.gradle.kts active in CI.
2. Build fails if below defined metrics.
DoD: Demonstrate failing example locally (documentation only).
Points: 1

### Story NF2: ktlint / Static Analysis (Planned)
As a maintainer I want standardized formatting to reduce review noise.
AC:
1. Add ktlint plugin & Gradle task.
2. Fails build on violations (configurable exclude for generated code).
DoD: Build script modification + sample violation test.
Points: 3

### Story NF3: Central Problem+JSON Error Handling (Planned)
As an API client I want consistent error format so clients can parse uniformly.
AC:
1. RFC 7807 fields: type, title, status, detail, instance.
2. Domain exceptions mapped to type URNs.
DoD: Integration test (one domain error, one validation error).
Points: 5

---
## Traceability Matrix (Story ↔ Architectural Goals)
- Domain purity: A1, A3, C1, E1, H2
- Event-driven seams: A3, A4, C1, F1, J1
- Strong typing & value objects: H2, A1, B1
- Modular boundaries: All epics map to distinct contexts
- Observability: G1, G2, NF1

---
## Suggested Iteration Plan (First 3 Sprints Outline)
Sprint 1 (Foundations): A1, A2, H1, H2, B1, NF1
Sprint 2 (Acceptance & Events): A3, F1, G1, B2, C1
Sprint 3 (Expansion): A4, C2, B3, G2, E1, F2

Adjust based on capacity and emergent risk.

---
## Definition of Done (Global)
1. Code compiles, tests green, coverage thresholds met.
2. No blocking calls introduced (reactive purity maintained).
3. Domain layer free of Spring annotations.
4. Kafka events versioned and include correlation + event-type headers.
5. README / docs updated if public API or model changes.

---
## Open Risks / Assumptions
- Rule engine complexity deferred (E1 intentionally minimal).
- Event versioning schema registry not yet integrated (F2 is interim solution).
- Authorization decisions simplified (D1 stub) pending IAM expansion.

---
End of document.

