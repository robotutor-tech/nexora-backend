# Automation (Module) — Components, Resolution, and Intended Execution Model

> Scope: This doc describes what the current **Automation** bounded context is trying to achieve, focused on the **ComponentInline** model (Trigger / Condition / Action) and the **resolution pipeline**.
>
> Notes:
> - This module appears to be mid-refactor: some parts model automations as **references to Rule IDs**, while other parts model automations as **component value objects**.
> - The most solid and “working direction” code today is the **component + resolver strategy + DataResolver** pipeline.

---

## 1) Goal of the Automation module

The Automation module aims to let users define **automations** like:

- *When* something happens (**Trigger**) and (optionally) some criteria are true (**Condition**)
- *Then* perform one or more **Actions**

This is a rules/automation engine bounded context, designed to stay DDD-friendly:

- **Domain stays pure**: it stores intent (what the automation is).
- **External data is loaded in application layer**: right before validation/evaluation/execution.
- **Execution can become resumable** (e.g., WAIT) using an execution record.

---

## 2) Core domain concepts

### 2.1 ComponentInline

A **ComponentInline** is the generic building block of an automation.

- `ComponentInline` (marker)
- `Trigger : ComponentInline`
- `Condition : ComponentInline`
- `Action : ComponentInline`

Conceptually:

- **Trigger** starts automation evaluation.
- **Condition** is a boolean rule evaluated when triggered.
- **Action** is an operation executed when conditions pass.

> Important: Conditions are also represented as a **tree** (AND/OR/NOT), not only a single leaf.

### 2.2 AutomationAggregate (persisted intent)

`AutomationAggregate` represents the persisted automation definition:

- Identity: `automationId`, `premisesId`
- Human metadata: `name`, `description`
- Behavior:
  - `triggers: Triggers` (non-empty + unique)
  - `condition: Specification<Condition>?` (optional boolean tree)
  - `actions: Actions` (non-empty)
  - `executionMode: ExecutionMode` (MULTIPLE/SINGLE/REPLACE)
  - state/time fields

It emits `AutomationRegisteredEvent` on creation.

### 2.3 ResolvedAutomation (execution-time derived view)

`ResolvedAutomation` is a *derived*, execution-oriented view:

- triggers/actions become `ComponentData<Trigger>` / `ComponentData<Action>`
- condition becomes `Specification<ComponentData<Condition>>?`

This is the key principle:

> **Trace/resolution data is derived at runtime**, not passed into the domain from outside.

---

## 3) ComponentInline examples currently in the code

The module currently has early component types:

- `FeedValue(feedId, value) : Action`
- `FeedControl(feedId, operator, expectedValue) : Trigger, Condition` (intent suggests it can be a trigger and also used in conditions)
- `Wait(duration, unit?) : Action` (conceptually a delayed continuation)
- `Voice(...) : Trigger` (voice command triggers)
- `Automation(automationId) : Action` (automation chaining)

These are a starting set and will likely expand.

---

## 4) Condition representation (Specification)

Conditions are represented using a classic specification tree:

- `AndSpecification<T>(specifications: List<Specification<T>>)`
- `OrSpecification<T>(specifications: List<Specification<T>>)`
- `NotSpecification<T>(specification: Specification<T>)`
- leaf specs (currently treated as `Condition` components)

This allows a condition like:

- `(A AND B) OR (NOT C)`

In the current direction:

- The **domain** stores `Specification<Condition>`
- The resolver converts that to `Specification<ComponentData<Condition>>`

---

## 5) Resolution pipeline (Application layer)

### 5.1 Why resolution exists

Most components need *runtime context*:

- a feed current value
- a device capability
- referenced automation definition
- external bounded context data

DDD rule followed here:

> **Data loading from DB or external bounded contexts happens in the application layer**.

So the module introduces a “materialization” step that derives an execution-ready model.

### 5.2 ResolverStrategy

Resolution is implemented using a strategy pattern:

- `ResolverStrategy<T : ComponentInline, D : ComponentData<T>>`
- `ComponentResolverStrategyFactory` selects a resolver based on the component type

Example strategies today:

- `FeedValueResolverStrategy` → uses `FeedFacade.getFeedById(feedId)` → returns `FeedValueData(feedId, value)`
- `FeedControlResolverStrategy` → uses `FeedFacade.getFeedById(feedId)` → returns `FeedControlData(...)`
- `AutomationResolverStrategy` → loads referenced automation → returns `AutomationData(automationId)`
- `NoResolverStrategy` → returns component “as is” (works only for components that already implement `ComponentData`)

### 5.3 DataResolver

`DataResolver` composes resolution without `subscribe()` or `block()`.

Responsibilities:

1) Resolve all triggers to `List<ComponentData<Trigger>>`
2) Resolve all actions to `List<ComponentData<Action>>`
3) Resolve the condition specification tree into `Specification<ComponentData<Condition>>?`
4) Zip them into `ResolvedAutomation`

This creates a single reactive chain that callers can attach to their existing stream.

---

## 6) Intended execution model (target direction)

The long-term intent implied by the documents and partial code is:

1) A domain signal happens (e.g., feed update / schedule event / voice command)
2) Identify candidate automations by trigger type
3) Resolve automation into `ResolvedAutomation`
4) Evaluate condition tree
5) Execute actions
6) If an action is `WAIT`, persist execution state and resume later

There’s an `AutomationExecution` document that suggests resumable execution:

- `status` (RUNNING/PAUSED/COMPLETED/FAILED)
- `currentActionIndex`
- `resumeAt`

Execution is not fully implemented yet, but the structure indicates where it’s going.

---

## 7) Current architectural tension: “Rules by reference” vs “Components by value”

The codebase currently shows two competing representations:

### A) Automations reference Rule IDs

- Persistence DTOs/documents store `triggers/actions` as `List<String>` (IDs)
- This implies there is a separate `Rule` entity with `RuleType + Config`

Benefits:
- deduplication/reuse of triggers/actions/conditions
- consistent rule catalog per premises

Challenges:
- more joins/reads at runtime
- mapping complexity (IDs ↔ domain components ↔ config)

### B) Automations embed components directly

- Domain aggregate currently stores `Triggers(values: List<Trigger>)` and `Actions(values: List<Action>)`

Benefits:
- simpler aggregate shape
- fewer indirections

Challenges:
- less reuse/dedup
- persistence needs to store polymorphic components, which isn’t finished yet

**Recommendation (decision needed):** pick one model as the “source of truth” and align controller DTOs, documents, mappers, and services around it.

---

## 8) Gaps / risks to address

These aren’t criticisms—just the practical gaps visible in current code:

1) **Compilation & wiring drift** in automation module
   - controllers/mappers/services reference types that don’t exist or have changed

2) **Condition persistence is incomplete**
   - `ConditionNodeDocument` is empty; mapping and storage aren’t defined

3) **NoResolverStrategy is unsafe**
   - it assumes some components implement `ComponentData`, but others don’t

4) **Execution engine not implemented**
   - resolve exists; evaluate/execute/wait-resume is still a roadmap item

5) **Potential data-loss in resolution**
   - resolvers must preserve the full semantics needed for evaluation (operators, expected values, etc.)

---

## 9) Suggested next steps (low-risk, DDD-aligned)

- Decide the canonical model:
  - either keep automations as **references to rules** (IDs)
  - or keep automations as **embedded component definitions** (polymorphic)
- Formalize condition storage:
  - define `ConditionNodeDocument` variants and mapper
- Define a small “execution contract”:
  - `ResolvedAutomation` + `ExecutionContext` → action execution result
- Ensure all resolution remains non-blocking and composed (no subscribe/block in application services).

