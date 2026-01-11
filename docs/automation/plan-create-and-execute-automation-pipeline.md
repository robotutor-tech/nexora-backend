# Plan — Create & Execute Automation Pipeline (DDD-aligned)

> Goal: remove confusion in the Automation flow by using a consistent, layered pipeline:
>
> - **Create**: Resolve → Validate → Create aggregate → Persist
> - **Execute**: Load aggregate → Resolve → Validate → Execute
>
> All without `subscribe()`/`block()`; everything stays as a composed reactive chain.

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
`Mono<ExecutionResult>` (or a domain event / execution record)

### Flow

1) `automationRepository.findByAutomationId(automationId): Mono<AutomationAggregate>`
2) `.flatMap(dataResolver::resolve): Mono<ResolvedAutomation>`
3) `.enforcePolicy(executeAutomationPolicy, ...)` (or reuse the same policy if it matches)
4) `.flatMap(executor::execute): Mono<ExecutionResult>`

**Executor** here is a dedicated application service / domain service depending on whether it needs I/O.

- If execution needs I/O (calling devices, writing feeds) → application service.
- If execution is fully pure (building commands/events) → domain service.

---

## 6) The missing piece in your current code

### 6.1 Policy implementation is incomplete

`CreateAutomationPolicy` currently has `TODO()` branches and doesn’t traverse the condition tree.

The intended rule is correct:

- Validate triggers
- Validate actions
- Validate condition tree

But implementation must:

- walk the resolved condition spec tree
- validate each resolved leaf
- validate cross-component invariants (e.g. trigger/action compatibility)

### 6.2 ExecuteAutomationService return type should be resolved

Current code:

- `ExecuteAutomationService.execute()` returns `Mono<AutomationAggregate>`
- but `dataResolver.resolve(automation)` returns `Mono<ResolvedAutomation>`

So execution service should return `Mono<ResolvedAutomation>` (if its only job is resolve) OR move into a proper execution pipeline and return `Mono<ExecutionResult>`.

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
     - optional executor support

4) **Easy to evolve into resumable execution**
   - execution state can be stored in an `AutomationExecution` record
   - WAIT becomes persistence-driven

---

## 8) Concrete next steps (sequence)

1) Make `ExecuteAutomationService` consistent:
   - either rename it to `ResolveAutomationService` and return `Mono<ResolvedAutomation>`
   - or implement a full execution pipeline and return `Mono<ExecutionResult>`

2) Implement `CreateAutomationPolicy` fully:
   - traverse resolved triggers/actions
   - traverse resolved condition tree
   - validate component-specific invariants

3) Define an `AutomationExecutor` contract (application layer):
   - run actions in order
   - support WAIT (later)

4) Decide canonical storage model (rule IDs vs embedded components) and align docs/mappers.

