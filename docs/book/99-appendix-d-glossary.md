# Appendix D: Glossary of Terms

## A

**Aggregate**  
A cluster of domain objects (entities and value objects) that can be treated as a single unit for data changes. An aggregate has a root entity (aggregate root) and a boundary that defines what is inside the aggregate.

**Aggregate Root**  
The single entity through which external objects interact with an aggregate. The aggregate root is responsible for maintaining the consistency of the entire aggregate and enforcing its invariants.

**Anemic Domain Model**  
An anti-pattern where domain objects contain only data (properties) and no behavior (methods). Business logic is placed in services instead of the domain objects themselves.

**Anti-Corruption Layer (ACL)**  
A translation layer that protects a bounded context from the details of external systems. It translates between the ubiquitous language of your context and the language/model of external systems.

**Application Service**  
A service in the application layer that orchestrates use cases. It coordinates domain objects but contains no business logic itself. Also called "Use Case" or "Orchestration Service."

---

## B

**Bounded Context**  
A boundary within which a particular domain model is defined and applicable. Within a bounded context, all terms have specific meanings and the model is consistent. Different bounded contexts can have different models for the same concepts.

**Business Logic**  
The rules, workflows, and processes that define how a business operates. In DDD, business logic should reside in the domain layer, not scattered throughout the application.

**Business Rule**  
A specific constraint or policy that governs how the business operates. Example: "A device can have a maximum of 10 feeds."

---

## C

**Command**  
An instruction to perform an action that will change the state of the system. Commands represent intent (e.g., "ActivateDevice"). Contrast with Query.

**Command Handler**  
A component that executes a command by loading aggregates, performing operations, and persisting changes.

**Command Query Responsibility Segregation (CQRS)**  
A pattern that separates read operations (queries) from write operations (commands), often using different models for each. Enables independent optimization of reads and writes.

**Consistency**  
The property that data is correct and follows all business rules. In DDD, consistency is maintained within aggregate boundaries.

**Context Boundary**  
The edge of a bounded context where translation between models occurs. Also called "Integration Boundary."

**Context Map**  
A visual representation showing all bounded contexts in a system and their relationships (e.g., Customer-Supplier, Partnership, Conformist).

**CQRS**  
See Command Query Responsibility Segregation.

---

## D

**Domain**  
The subject area or sphere of knowledge that your software is addressing. Example: "Smart Home Automation" is a domain.

**Domain Event**  
Something significant that happened in the domain, expressed in past tense (e.g., "DeviceActivated"). Used for loose coupling within a bounded context.

**Domain Expert**  
A person with deep knowledge of the business domain. They provide the requirements and validate that the software correctly models the domain.

**Domain Layer**  
The layer in a DDD architecture that contains the business logic and domain model. It is independent of infrastructure and application concerns.

**Domain Logic**  
See Business Logic.

**Domain Model**  
The structured representation of the domain, including entities, value objects, aggregates, and their relationships. The domain model should reflect the ubiquitous language.

**Domain Service**  
A service in the domain layer that encapsulates domain logic that doesn't naturally fit within a single entity or value object. Contains business logic, unlike application services.

**Domain-Driven Design (DDD)**  
An approach to software development that focuses on modeling complex business domains and expressing that model in code. Emphasizes collaboration with domain experts and ubiquitous language.

---

## E

**Entity**  
A domain object defined by its identity rather than its attributes. Entities have a unique identifier and a lifecycle. Example: A Device with ID "device-123" is the same device even if its status changes.

**Event**  
See Domain Event or Integration Event.

**Event Handler**  
A component that reacts to and processes domain events or integration events.

**Event Sourcing**  
A pattern where state changes are stored as a sequence of events rather than storing just the current state. The current state can be reconstructed by replaying all events.

**Event Store**  
A database specialized for storing events in an event-sourced system. Provides append-only storage and event replay capabilities.

**Eventual Consistency**  
A consistency model where updates to different parts of a system may happen at different times, but eventually all parts will be consistent. Common in distributed systems and CQRS.

---

## F

**Factory**  
A pattern for creating complex domain objects or aggregates. Encapsulates the creation logic and ensures objects are created in a valid state.

**Feed**  
(SmartHome Hub specific) A data channel on a device (e.g., temperature feed, humidity feed). In DDD terms, often an entity within the Device aggregate.

---

## I

**Immutability**  
The property that an object cannot be modified after creation. Value objects should be immutable. In Kotlin, this is achieved with `val` properties and immutable collections.

**Infrastructure Layer**  
The layer that contains technical concerns like database access, messaging, file I/O. Should not contain business logic.

**Integration Event**  
An event published across bounded context boundaries to communicate between contexts. Often uses messaging infrastructure like Kafka.

**Invariant**  
A condition that must always be true for a domain object or aggregate. The aggregate root is responsible for maintaining invariants. Example: "A device must have at least one feed to be activated."

---

## L

**Layered Architecture**  
An architectural style that organizes code into horizontal layers (Presentation, Application, Domain, Infrastructure). Each layer depends only on layers below it.

---

## M

**Model**  
See Domain Model.

**Module**  
A grouping of related domain concepts. In code, typically represented as a package or namespace.

---

## P

**Persistence Ignorance**  
The principle that domain objects should not be concerned with how they are persisted. The domain model should be independent of database or ORM details.

**Policy**  
A pattern for encapsulating business rules that evaluate whether an operation is allowed. Returns a result indicating permission and any violations.

**Premises**  
(SmartHome Hub specific) A location (home, building) where devices are installed. In DDD terms, typically an aggregate root.

**Primitive Obsession**  
An anti-pattern where primitive types (String, Int, etc.) are used instead of domain-specific value objects. Example: Using `String` for email instead of an `Email` value object.

**Projection**  
In CQRS, a read model created by processing events or aggregates. Optimized for specific query needs.

---

## Q

**Query**  
A request for information that doesn't change system state. Contrast with Command.

**Query Handler**  
A component that executes a query, typically by loading read models or projections.

---

## R

**Read Model**  
In CQRS, a model optimized for read operations. Often denormalized for query performance. Contrast with Write Model.

**Repository**  
A pattern that provides a collection-like interface for accessing aggregates. Abstracts the underlying persistence mechanism.

**Rich Domain Model**  
A domain model where domain objects contain both data and behavior. Business logic resides in the domain objects. Contrast with Anemic Domain Model.

---

## S

**Saga**  
A pattern for managing long-running business processes that span multiple aggregates or bounded contexts. Coordinates distributed transactions through a series of steps with compensation logic.

**Separation of Concerns**  
The principle that different aspects of a system should be independent and not mixed. DDD layers (Domain, Application, Infrastructure) exemplify this.

**Service**  
See Domain Service or Application Service.

**Side Effect**  
An operation that modifies state outside its scope (e.g., database write, message sent). In DDD, side effects should be controlled and explicit.

**Snapshot**  
In event sourcing, a saved state of an aggregate at a point in time. Used to improve performance by avoiding replay of all events.

**Specification**  
A pattern for encapsulating query or validation logic in a composable, reusable way. Specifications can be combined with AND, OR, NOT operations.

**Strategic Design**  
The aspect of DDD focused on high-level organization: bounded contexts, context mapping, and relationships between contexts.

---

## T

**Tactical Design**  
The aspect of DDD focused on implementation patterns within a bounded context: entities, value objects, aggregates, repositories, etc.

**Tell, Don't Ask**  
A principle where you tell objects what to do rather than asking them for their state and making decisions externally. Promotes encapsulation and rich domain models.

**Transaction**  
A unit of work that either fully completes or fully fails. In DDD, transaction boundaries typically align with aggregate boundaries.

---

## U

**Ubiquitous Language**  
A common language shared by developers and domain experts. The ubiquitous language is used everywhere: in conversations, documentation, and code. Each bounded context has its own ubiquitous language.

**Use Case**  
A specific way a user interacts with the system. In DDD, use cases are implemented as application services.

---

## V

**Value Object**  
A domain object defined by its attributes rather than an identity. Value objects are immutable and interchangeable if their values are the same. Examples: Email, Address, Money.

---

## W

**Write Model**  
In CQRS, the model used for write operations (commands). Contains business logic and invariants. Contrast with Read Model.

---

## Common Acronyms

**ACL** - Anti-Corruption Layer  
**CQRS** - Command Query Responsibility Segregation  
**DDD** - Domain-Driven Design  
**DTO** - Data Transfer Object  
**ES** - Event Sourcing  
**ORM** - Object-Relational Mapping  

---

## SmartHome Hub Specific Terms

**Actor**  
A user or system performing an operation. Used for authorization and audit trails.

**Commissioning**  
The process of configuring a device after registration (setting up feeds, assigning to zones).

**Device**  
An IoT device that can sense or control physical environment (sensor, actuator, smart appliance).

**Feed**  
A data channel on a device representing a specific measurement or control (e.g., temperature, humidity, power state).

**Premises**  
A location (home, building, facility) where devices are installed. Users can manage multiple premises.

**Zone**  
A logical grouping of devices within a premises (e.g., "Living Room", "Kitchen").

---

## Pattern Relationships

### Entity vs Value Object
- **Entity:** Has identity, mutable lifecycle, equality by ID
- **Value Object:** No identity, immutable, equality by value

### Domain Service vs Application Service
- **Domain Service:** Contains business logic that doesn't fit in entities
- **Application Service:** Orchestrates use cases, no business logic

### Aggregate vs Entity
- **Aggregate:** A cluster with consistency boundary, has root
- **Entity:** Can exist independently or within an aggregate

### Domain Event vs Integration Event
- **Domain Event:** Within bounded context, fine-grained
- **Integration Event:** Between bounded contexts, coarse-grained

### Command vs Query
- **Command:** Changes state, returns void or ID
- **Query:** Reads data, returns results, no state change

### Specification vs Policy
- **Specification:** Selects objects matching criteria
- **Policy:** Evaluates whether an action is allowed

---

## Anti-Patterns to Avoid

**Anemic Domain Model:** Business logic in services, not domain objects  
**Primitive Obsession:** Using primitives instead of value objects  
**God Object:** One object doing too much  
**Feature Envy:** Service manipulating another object's data  
**Broken Encapsulation:** Exposing internal state  
**Transaction Script:** Procedural code instead of domain model  

---

## Quick Reference

**When to use Value Object:** Primitive type needs validation or domain meaning  
**When to use Entity:** Object has identity and lifecycle  
**When to use Aggregate:** Need transaction consistency across related objects  
**When to use Repository:** Persisting/retrieving aggregates  
**When to use Specification:** Reusable query logic  
**When to use Policy:** Complex business rules evaluation  
**When to use Domain Service:** Business logic spans multiple entities  
**When to use Application Service:** Orchestrating a use case  
**When to use Domain Event:** Loose coupling within context  
**When to use Integration Event:** Communication between contexts  
**When to use CQRS:** Read and write patterns very different  
**When to use Event Sourcing:** Need complete audit trail or time travel  

---

*For detailed explanations and examples of these terms, refer to the corresponding chapters in the book.*

