# Appendix A: Quick Reference Guide

## Value Objects Quick Reference

### Basic Value Object Pattern

```kotlin
@JvmInline
value class Email(val value: String) {
    init {
        require(value.contains("@")) {
            "Invalid email format"
        }
    }
}
```

### Complex Value Object Pattern

```kotlin
data class Address(
    val street: Street,
    val city: City,
    val state: State,
    val postalCode: PostalCode,
    val country: CountryCode
) {
    fun format(): String {
        return "${street.value}\n${city.value}, ${state.value} ${postalCode.value}\n${country.value}"
    }
}
```

### Key Principles
- ✅ Immutable
- ✅ Validated in constructor
- ✅ Equality by value
- ✅ No identity
- ✅ Side-effect free operations

---

## Entity Pattern Quick Reference

### Basic Entity Pattern

```kotlin
class Device private constructor(
    val deviceId: DeviceId,  // Identity
    private val name: Name,
    private var status: DeviceStatus
) {
    fun activate(): Device {
        require(status == DeviceStatus.PENDING)
        return copy(status = DeviceStatus.ACTIVE)
    }
    
    fun getName(): Name = name
    fun getStatus(): DeviceStatus = status
}
```

### Key Principles
- ✅ Has identity (ID)
- ✅ Mutable lifecycle
- ✅ Equality by identity, not value
- ✅ Protects invariants
- ✅ Encapsulates behavior

---

## Aggregate Pattern Quick Reference

### Aggregate Root Pattern

```kotlin
class Device private constructor(
    val deviceId: DeviceId,  // Aggregate root ID
    private val feeds: Map<FeedId, Feed>,  // Internal entities
    private var status: DeviceStatus
) {
    companion object {
        fun register(...): Device { }
    }
    
    // All modifications go through aggregate root
    fun addFeed(feed: Feed): Device {
        // Validate invariants
        require(feeds.size < MAX_FEEDS)
        // Return new instance
        return copy(feeds = feeds + (feed.feedId to feed))
    }
    
    // Controlled access to internals
    fun getFeeds(): List<Feed> = feeds.values.toList()
}
```

### Key Principles
- ✅ Single entry point (aggregate root)
- ✅ Protects invariants
- ✅ Transactional boundary
- ✅ No external references to internals
- ✅ Publishes domain events

---

## Repository Pattern Quick Reference

### Repository Interface

```kotlin
interface DeviceRepository {
    fun findById(deviceId: DeviceId): Device?
    fun save(device: Device): Device
    fun delete(deviceId: DeviceId)
    fun findBySpecification(spec: Specification<Device>): List<Device>
}
```

### Key Principles
- ✅ One repository per aggregate root
- ✅ Collection-like interface
- ✅ Hides persistence details
- ✅ Returns domain objects
- ✅ Uses specifications for queries

---

## Specification Pattern Quick Reference

### Basic Specification

```kotlin
interface Specification<T> {
    fun isSatisfiedBy(candidate: T): Boolean
    
    fun and(other: Specification<T>): Specification<T> {
        return AndSpecification(this, other)
    }
    
    fun or(other: Specification<T>): Specification<T> {
        return OrSpecification(this, other)
    }
    
    fun not(): Specification<T> {
        return NotSpecification(this)
    }
}
```

### Usage Example

```kotlin
val spec = DeviceSpecifications.inPremises(premisesId)
    .and(DeviceSpecifications.withStatus(DeviceStatus.ACTIVE))
    .and(DeviceSpecifications.nameContains("sensor"))

val devices = deviceRepository.findBySpecification(spec)
```

### Key Principles
- ✅ Encapsulates query logic
- ✅ Reusable and composable
- ✅ Domain language
- ✅ Testable independently

---

## Policy Pattern Quick Reference

### Basic Policy

```kotlin
interface Policy<T> {
    fun evaluate(subject: T): PolicyResult
}

data class PolicyResult(
    val isAllowed: Boolean,
    val violations: List<String>
) {
    companion object {
        fun allowed() = PolicyResult(true, emptyList())
        fun denied(violations: List<String>) = PolicyResult(false, violations)
    }
}
```

### Usage Example

```kotlin
class DeviceActivationPolicy : Policy<Device> {
    override fun evaluate(subject: Device): PolicyResult {
        val violations = mutableListOf<String>()
        
        if (subject.getStatus() != DeviceStatus.PENDING) {
            violations.add("Device must be pending")
        }
        if (subject.getFeeds().isEmpty()) {
            violations.add("Device must have feeds")
        }
        
        return if (violations.isEmpty()) {
            PolicyResult.allowed()
        } else {
            PolicyResult.denied(violations)
        }
    }
}
```

### Key Principles
- ✅ Encapsulates business rules
- ✅ Testable independently
- ✅ Composable
- ✅ Clear violation messages

---

## CQRS Quick Reference

### Command Side (Write Model)

```kotlin
// Command
data class ActivateDeviceCommand(
    val deviceId: DeviceId,
    val actorId: ActorId
)

// Command Handler
class ActivateDeviceCommandHandler(
    private val repository: DeviceRepository
) {
    fun handle(command: ActivateDeviceCommand) {
        val device = repository.findById(command.deviceId)
        val activated = device.activate()
        repository.save(activated)
    }
}
```

### Query Side (Read Model)

```kotlin
// Read Model
data class DeviceListReadModel(
    val deviceId: String,
    val name: String,
    val status: String,
    val zoneName: String?
)

// Query Handler
class GetDeviceListQueryHandler(
    private val readModelRepository: DeviceListReadModelRepository
) {
    fun handle(query: GetDeviceListQuery): List<DeviceListReadModel> {
        return readModelRepository.findByPremisesId(query.premisesId)
    }
}
```

### Key Principles
- ✅ Separate models for read and write
- ✅ Optimized independently
- ✅ Different data stores possible
- ✅ Eventual consistency

---

## Event Sourcing Quick Reference

### Event Store Pattern

```kotlin
interface EventStore {
    fun save(aggregateId: String, events: List<DomainEvent>)
    fun getEvents(aggregateId: String): List<DomainEvent>
    fun getEventsAfterVersion(aggregateId: String, version: Long): List<DomainEvent>
}
```

### Aggregate from Events

```kotlin
class Device {
    companion object {
        fun fromEvents(events: List<DeviceEvent>): Device {
            var device: Device? = null
            events.forEach { event ->
                device = applyEvent(device, event)
            }
            return device!!
        }
        
        private fun applyEvent(device: Device?, event: DeviceEvent): Device {
            return when (event) {
                is DeviceRegistered -> Device.register(...)
                is DeviceActivated -> device!!.activate()
                // Handle all events
            }
        }
    }
}
```

### Key Principles
- ✅ Store events, not state
- ✅ Rebuild from events
- ✅ Complete audit trail
- ✅ Use snapshots for performance
- ✅ Event versioning important

---

## Domain Events Quick Reference

### Event Definition

```kotlin
sealed class DeviceEvent : DomainEvent {
    abstract val deviceId: DeviceId
    abstract val occurredAt: Instant
}

data class DeviceActivated(
    override val deviceId: DeviceId,
    override val occurredAt: Instant = Instant.now()
) : DeviceEvent()
```

### Publishing Events

```kotlin
class Device {
    private val _domainEvents = mutableListOf<DeviceEvent>()
    val domainEvents: List<DeviceEvent> get() = _domainEvents
    
    fun activate(): Device {
        val activated = copy(status = DeviceStatus.ACTIVE)
        activated.addDomainEvent(DeviceActivated(deviceId))
        return activated
    }
    
    private fun addDomainEvent(event: DeviceEvent) {
        _domainEvents.add(event)
    }
}
```

### Event Handler

```kotlin
@Component
class DeviceActivatedEventHandler(
    private val notificationService: NotificationService
) {
    @EventListener
    fun handle(event: DeviceActivated) {
        notificationService.sendDeviceActivated(event.deviceId)
    }
}
```

### Key Principles
- ✅ Past tense naming (DeviceActivated)
- ✅ Immutable
- ✅ Published after persistence
- ✅ Loose coupling
- ✅ Eventual consistency

---

## Common DDD Patterns Cheat Sheet

### When to Use What

| Pattern | Use When | Don't Use When |
|---------|----------|----------------|
| Value Object | Primitive obsession, validation needed | Simple strings OK |
| Entity | Has identity, mutable lifecycle | No identity needed |
| Aggregate | Need transactional consistency | Can be separate |
| Repository | Persisting aggregates | Not per-entity |
| Specification | Complex query logic | Simple queries |
| Policy | Business rules evaluation | Simple validation |
| Domain Event | Loose coupling needed | Direct call OK |
| CQRS | Read/write patterns differ | Simple CRUD |
| Event Sourcing | Audit trail critical | Not worth complexity |
| Saga | Distributed transaction | Single DB transaction OK |

---

## Code Smells and Fixes

### Smell: Anemic Domain Model
**Problem:** Data class with getters/setters only

```kotlin
// ❌ Anemic
data class Device(var status: String)

// ✅ Rich
class Device {
    fun activate(): Device { }
}
```

### Smell: Primitive Obsession
**Problem:** Using String for everything

```kotlin
// ❌ Primitive
fun register(email: String)

// ✅ Value Object
fun register(email: Email)
```

### Smell: Feature Envy
**Problem:** Service doing work that should be in entity

```kotlin
// ❌ Feature Envy
class Service {
    fun activate(device: Device) {
        device.status = "ACTIVE"
    }
}

// ✅ Tell, Don't Ask
class Device {
    fun activate(): Device { }
}
```

### Smell: Broken Encapsulation
**Problem:** Public mutable state

```kotlin
// ❌ Broken
class Device(var feeds: MutableList<Feed>)

// ✅ Encapsulated
class Device {
    private val feeds: Map<FeedId, Feed>
    fun addFeed(feed: Feed): Device { }
    fun getFeeds(): List<Feed> = feeds.values.toList()
}
```

---

## Testing Patterns Quick Reference

### Test Value Object

```kotlin
@Test
fun `should validate on creation`() {
    assertThrows<IllegalArgumentException> {
        Email("invalid")
    }
}
```

### Test Entity

```kotlin
@Test
fun `should activate pending device`() {
    val device = Device.register(...)
    val activated = device.activate()
    assertEquals(DeviceStatus.ACTIVE, activated.getStatus())
}
```

### Test Aggregate Invariants

```kotlin
@Test
fun `should enforce max feeds`() {
    var device = createDeviceWithFeeds(count = 10)
    
    assertThrows<IllegalArgumentException> {
        device.addFeed(createFeed())
    }
}
```

### Test Specification

```kotlin
@Test
fun `should match active devices`() {
    val spec = DeviceSpecifications.withStatus(DeviceStatus.ACTIVE)
    val activeDevice = createDevice(status = DeviceStatus.ACTIVE)
    
    assertTrue(spec.isSatisfiedBy(activeDevice))
}
```

---

## Performance Optimization Quick Tips

### Use Projections for Reads

```kotlin
// ❌ Load full aggregate
val device = repository.findById(id)
return device.getName()

// ✅ Use projection
data class DeviceNameProjection(val name: String)
return repository.findNameById(id)
```

### Batch Queries

```kotlin
// ❌ N+1 queries
devices.forEach { device ->
    val zone = zoneRepository.findById(device.zoneId)
}

// ✅ Batch load
val zones = zoneRepository.findByIds(devices.map { it.zoneId })
```

### Use Caching

```kotlin
@Cacheable("devices")
fun findById(deviceId: DeviceId): Device? { }
```

### Use Snapshots for Event Sourcing

```kotlin
// Every 100 events, save snapshot
if (version % 100 == 0) {
    snapshotStore.save(device, version)
}
```

---

## This quick reference covers the essential patterns you'll use daily in DDD. For detailed explanations and advanced topics, refer to the relevant chapters in the book.

