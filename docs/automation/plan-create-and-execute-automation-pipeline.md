# Plan — Create & Execute Automation Pipeline (DDD-aligned)

> Goal: remove confusion in the Automation flow by using a consistent, layered pipeline:
>
> - **Create**: Resolve → Validate → Create aggregate → Persist
> - **Execute**: Load aggregate → Resolve → Validate → Execute
>
> All without `subscribe()`/`block()`; everything stays as a composed reactive chain.

---

## 0) Implemented changes (Jan 2026)

To make the pipeline real (not just conceptual), the following changes were applied:

- **Execution pipeline is now end-to-end and consistent**
  - `ExecuteAutomationService` now follows: **Load → Resolve → Validate → Execute**
  - It returns `Mono<AutomationExecutionResult>` (application-level result)

- **Executor pattern introduced (application layer)**
  - Added `AutomationExecutor` contract and `DefaultAutomationExecutor` implementation.
  - This keeps execution orchestration out of the domain and makes it easy to swap in a real executor later.

- **Policy separation for create vs execute**
  - Added `ExecuteAutomationPolicy` (currently delegates to `CreateAutomationPolicy`).
  - This allows execute-time validation to diverge later (runtime checks, permissions, resource availability).

- **Specification model unified**
  - `ResolvedAutomation.condition`, `DataResolver`, and policies now use `com.robotutor.nexora.shared.domain.specification.Specification`.

- **Option B (typed resolution) to prevent cross-mapping**
  - `ComponentInline` value objects (e.g., `Voice`, `Wait`) no longer implement `ComponentData` directly.
  - Every component resolves into a dedicated `ComponentData` type:
    - `FeedValue -> FeedValueData`
    - `FeedControl -> FeedControlData`
    - `Automation -> AutomationData`
    - `Voice -> VoiceData` (no I/O, but kept as separate data type for consistency)
    - `Wait -> WaitData` (no I/O, but kept as separate data type for consistency)

- **Strategy pattern (registry-based) for resolution**
  - Introduced a single typed resolver strategy:
    - `ComponentResolver<C, D>` (implements `resolve(component)`)
  - Added `ComponentResolverRegistry` which discovers all resolvers from Spring and routes by component type.
  - `DataResolver` delegates all leaf resolution to the registry; condition trees are still resolved recursively.

  Notes:
  - We intentionally resolve by **component type only** (not role) to keep the model simple.
  - If you later find role-specific resolution is needed, you can evolve `ComponentResolver` into a role-aware handler
    (e.g., `resolve(component, role)`) without changing the domain types.

  ✅ To add a new component type, you now typically only need:
  1) new ComponentInline VO (domain)
  2) new ComponentData (domain)
  3) new resolver bean implementing `ComponentResolver` (application)

---

## 1) What problem we’re actually solving

You’re building a component-based automation system where the domain must stay pure, but creating/executing automations requires **runtime context** (feeds, device capabilities, referenced automation existence, etc.).

That creates two different representations:

1) **Definition / Intent** (persisted): components defined with references/parameters (e.g., `feedId`).
2) **Resolved / Execution view** (derived): components enriched with runtime data needed for decisions.

The key is to make this explicit in design, and to enforce invariants at the correct layer.

---

## 2) DDD layering responsibilities (what goes where)

### Interfaces layer (WebFlux controller / Kafka listener)

Responsibilities:
- Parse request/event → command
- Provide actor/context (principal, correlation ids) via framework integration
- Call **application service**
- Map result → DTO

Non-responsibilities:
- No persistence, no orchestration logic, no domain rule decisions.

### Application layer (Use-case / Application service)

This is where orchestration lives.

Responsibilities:
- Load external data (repositories, facades)
- Compose the pipeline (resolve/validate/create/execute)
- Manage transactions (if relevant) and idempotency boundaries
- Publish integration events (after persistence)

Non-responsibilities:
- No domain invariants encoded as if/else business logic (put those in domain objects/policies/specs).

### Domain layer

Responsibilities:
- Define the Ubiquitous Language:
  - Automation, Trigger, Action, Condition, Specification
- Enforce invariants that can be enforced without I/O
- Contain domain services/policies that are pure and testable

Non-responsibilities:
- No DB, no network calls, no framework dependencies.

### Infrastructure layer

Responsibilities:
- Mongo repositories, adapters, Kafka client, external BC clients
- Mapping between persistence documents and domain aggregates

---

## 3) Proposed design pattern: Pipeline + Strategy + Policy

### 3.1 Pipeline shape (consistent for create and execute)

A single reusable pipeline pattern:

1) **Resolve** (application service)
   - `AutomationAggregate` or `CreateAutomationCommand` → `ResolvedAutomation`
   - Uses resolver strategies for each component type.

2) **Validate** (domain policy)
   - Validate the `ResolvedAutomation` (now you have all required meaning/context)
   - Return structured reasons (PolicyResult) for API error payload.

3) **Write** (create) or **Act** (execute)
   - Create: `AutomationAggregate.register(...)` then persist.
   - Execute: evaluate condition then run actions.

### 3.2 Why this helps

- **Separation of concerns**: each step has one job.
- **DDD-friendly**: domain stays pure; application is orchestration.
- **Testability**: resolution can be tested with mocks; policy can be unit-tested without Spring.
- **Reusability**: same resolver + policy can be reused in REST and Kafka.
- **Reactive-friendly**: each stage returns `Mono`/`Flux`, composed into one chain.

---

## 4) Create Automation — recommended flow

### Input
`CreateAutomationCommand`

### Output
`Mono<AutomationAggregate>` (persisted)

### Flow (reactive chain)

1) `dataResolver.resolve(command): Mono<ResolvedAutomation>`
2) `.enforcePolicy(createAutomationPolicy, ...)`
3) `.map { AutomationAggregate.register(...) }`
4) `.flatMap(automationRepository::save)`

This is already very close to your existing `CreateAutomationService`.

### Key design point

**What to validate depends on the layer:**

- On the command/aggregate: validate shape (non-empty triggers/actions, valid values).
- On resolved automation: validate correctness that requires context (feed exists, automation exists, operators supported, permissions allowed for actor/premises, etc.).

---

## 5) Execute Automation — recommended flow

### Input
`ExecuteAutomationCommand(automationId, ...)`

### Output
`Mono<AutomationExecutionResult>` (application result)

### Flow

1) `automationRepository.findByAutomationId(automationId): Mono<AutomationAggregate>`
2) `.flatMap(dataResolver::resolve): Mono<ResolvedAutomation>`
3) `.enforcePolicy(executeAutomationPolicy, ...)`
4) `.flatMap(automationExecutor::execute): Mono<AutomationExecutionResult>`

---

## 6) The missing piece in your current code

### 6.1 Policy implementation must become business-real

The policy traversal is now in place for triggers/actions/condition trees.

Next (business) work is to add real reasons:

- feed existence / permission checks (if not already guaranteed by resolver)
- operator/value range checks
- cross-component compatibility rules

### 6.2 Executor currently is a placeholder

`DefaultAutomationExecutor` currently returns a result without side-effects.

This is intentional: it wires the flow end-to-end while leaving action semantics open.

---

## 7) Benefit analysis (why this is the “best” design for your case)

1) **Aligned with DDD**
   - domain stays free of I/O
   - application layer orchestrates the use-case

2) **Aligned with reactive programming**
   - everything is composed; no manual subscription
   - supports reuse for REST, Kafka, scheduled triggers

3) **Extensible**
   - new component types only require:
     - a component VO
     - a resolver strategy
     - policy rules
     - executor support

4) **Easy to evolve into resumable execution**
   - execution state can be stored in an `AutomationExecution` record
   - WAIT becomes persistence-driven

---

## 8) Concrete next steps (sequence)

1) Add a controller/Kafka handler call path for `ExecuteAutomationService`.
2) Implement real validation rules in policies.
3) Implement real side-effects in `AutomationExecutor` (with proper idempotency and retries).
4) Decide canonical storage model (rule IDs vs embedded components) and align docs/mappers.
