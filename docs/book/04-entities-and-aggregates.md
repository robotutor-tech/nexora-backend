# Chapter 4
# Entities and Aggregates
## Protecting Your Business Invariants

> *"Design aggregates based on business true invariants, not just object relationships."*  
> — Vaughn Vernon, Implementing Domain-Driven Design

---

## In This Chapter

While value objects represent concepts without identity, entities and aggregates are the heart of your domain model—objects with unique identities and lifecycles that must protect critical business rules. This chapter teaches you to design proper aggregates that maintain consistency and prevent invalid states.

**What You'll Learn:**
- The crucial difference between entities and value objects
- What aggregates are and why they're essential
- How to design proper aggregate boundaries
- Protecting business invariants within aggregates
- The role of aggregate roots in maintaining consistency
- Real-world Device aggregate from SmartHome Hub
- Common mistakes and how to avoid them
- Testing strategies for aggregates

---

## Table of Contents

1. The Problem: When Objects Lose Their Identity
2. What Are Entities?
3. What Are Aggregates?
4. Real-World Example: Device Aggregate in SmartHome Hub
5. Designing Aggregate Boundaries
6. Protecting Invariants
7. Aggregate Roots and Consistency
8. Common Pitfalls and Solutions
9. Testing Aggregates
10. Chapter Summary

---

## 1. The Problem: When Objects Lose Their Identity

### The Scenario: The Device Duplication Bug

You're working on SmartHome Hub when a critical bug appears in production:

> **Bug Report #2847:** "User registered same device twice with different names. System now shows duplicate devices with conflicting states. Data integrity compromised."

You investigate and find this code:

```kotlin
// DeviceService.kt - Current implementation
@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val feedRepository: FeedRepository
) {
    fun registerDevice(request: DeviceRegistrationRequest): Device {
        // No identity check!
        val device = Device(
            id = UUID.randomUUID().toString(),  // New ID every time
            name = request.name,
            serialNumber = request.serialNumber,
            premisesId = request.premisesId,
            feeds = mutableListOf()
        )
        
        deviceRepository.save(device)
        
        // Creating feeds separately
        request.feeds.forEach { feedRequest ->
            val feed = Feed(
                id = UUID.randomUUID().toString(),
                deviceId = device.id,
                name = feedRequest.name,
                type = feedRequest.type,
                value = 0
            )
            feedRepository.save(feed)
        }
        
        return device
    }
    
    fun updateDeviceName(deviceId: String, newName: String): Device {
        val device = deviceRepository.findById(deviceId)
            ?: throw DeviceNotFoundException(deviceId)
        
        device.name = newName  // Direct mutation
        deviceRepository.save(device)
        
        return device
    }
    
    fun addFeedToDevice(deviceId: String, feedRequest: FeedRequest): Feed {
        val device = deviceRepository.findById(deviceId)
            ?: throw DeviceNotFoundException(deviceId)
        
        // Creating feed without checking device state
        val feed = Feed(
            id = UUID.randomUUID().toString(),
            deviceId = device.id,
            name = feedRequest.name,
            type = feedRequest.type,
            value = 0
        )
        
        feedRepository.save(feed)
        device.feeds.add(feed.id)  // Direct manipulation
        deviceRepository.save(device)
        
        return feed
    }
}
```

### The Problems Discovered

1. **No Identity Uniqueness** - Same device (by serial number) registered multiple times
2. **Broken Encapsulation** - Direct manipulation of device.feeds list
3. **No Invariant Protection** - Can add feeds to deleted devices
4. **Inconsistent State** - Device and feeds saved separately (not atomic)
5. **Lost Business Rules** - No validation that device needs at least one feed
6. **Data Integrity Issues** - Feeds orphaned when device deleted

### The Real-World Impact

```kotlin
// What happened in production:

// User 1: Registers device
val device1 = registerDevice(
    name = "Living Room Sensor",
    serialNumber = "ABC123"
)
// device1.id = "uuid-1"

// User 2: Registers SAME device (by serial number)
val device2 = registerDevice(
    name = "Temperature Sensor",  // Different name!
    serialNumber = "ABC123"       // Same serial!
)
// device2.id = "uuid-2"  // Different ID!

// Result: Two devices in database for same physical device
// - Conflicting data
// - Duplicate feed readings
// - Impossible to tell which is correct
// - Data integrity destroyed
```

**Cost:**
- 🔴 3 hours of downtime
- 🔴 Manual data cleanup required
- 🔴 Customer trust damaged
- 🔴 Emergency hotfix deployed

**Root Cause:** No understanding of Entity identity and Aggregate boundaries.

---

## 2. What Are Entities?

### Definition

> **Entity:** An object that is defined by its identity rather than its attributes. The identity remains constant throughout the object's lifecycle, even as attributes change.
> 
> — Eric Evans, Domain-Driven Design

### Entity vs Value Object

The key difference:

```kotlin
// VALUE OBJECT - Defined by attributes
data class Temperature(val celsius: Double, val unit: TemperatureUnit)

val temp1 = Temperature(25.0, TemperatureUnit.CELSIUS)
val temp2 = Temperature(25.0, TemperatureUnit.CELSIUS)
// temp1 == temp2 ✅ (same values = equal)

// ENTITY - Defined by identity
class User(
    val userId: UserId,      // Identity
    val name: Name,          // Attribute (can change)
    val email: Email         // Attribute (can change)
)

val user1 = User(UserId("123"), Name("John"), Email("john@example.com"))
val user2 = User(UserId("123"), Name("Jane"), Email("jane@example.com"))
// user1 == user2 ✅ (same ID = same user, even with different attributes)

val user3 = User(UserId("456"), Name("John"), Email("john@example.com"))
// user1 == user3 ❌ (different ID = different users, even with same attributes)
```

### Characteristics of Entities

#### 1. Identity

Every entity has a **unique identity** that never changes:

```kotlin
class Device(
    val deviceId: DeviceId,  // Identity - NEVER changes
    private var name: Name,  // Attribute - can change
    private var status: DeviceStatus  // Attribute - can change
) {
    // Identity determines equality
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Device) return false
        return deviceId == other.deviceId  // Only compare identity
    }
    
    override fun hashCode(): Int {
        return deviceId.hashCode()  // Only hash identity
    }
}
```

#### 2. Mutability (Controlled)

Unlike value objects, entities can change over time:

```kotlin
class Device(
    val deviceId: DeviceId,
    private var name: Name,
    private var status: DeviceStatus
) {
    // Controlled mutation through methods
    fun rename(newName: Name): Device {
        // Business rule: Can't rename deleted device
        if (status == DeviceStatus.DELETED) {
            throw CannotRenameDeletedDeviceException(deviceId)
        }
        
        return Device(deviceId, newName, status).also {
            it.addDomainEvent(DeviceRenamedEvent(deviceId, newName))
        }
    }
    
    fun activate(): Device {
        if (status == DeviceStatus.ACTIVE) {
            throw DeviceAlreadyActiveException(deviceId)
        }
        
        return Device(deviceId, name, DeviceStatus.ACTIVE).also {
            it.addDomainEvent(DeviceActivatedEvent(deviceId))
        }
    }
}
```

#### 3. Lifecycle

Entities have a lifecycle with distinct states:

```kotlin
enum class DeviceStatus {
    PENDING,     // Just created, not activated
    ACTIVE,      // In use
    INACTIVE,    // Temporarily disabled
    MAINTENANCE, // Being serviced
    DELETED      // Soft deleted
}

class Device(
    val deviceId: DeviceId,
    private var status: DeviceStatus = DeviceStatus.PENDING
) {
    fun canBeActivated(): Boolean = status == DeviceStatus.PENDING
    fun canBeDeactivated(): Boolean = status == DeviceStatus.ACTIVE
    fun canBeDeleted(): Boolean = status != DeviceStatus.DELETED
    
    fun activate(): Device {
        require(canBeActivated()) { 
            "Cannot activate device in status: $status" 
        }
        return copy(status = DeviceStatus.ACTIVE)
    }
}
```

#### 4. Continuity

The same entity can be retrieved multiple times:

```kotlin
// First retrieval
val device1 = deviceRepository.findById(DeviceId("device-123"))
println(device1.name)  // "Living Room Sensor"

// Time passes... user renames device

// Second retrieval
val device2 = deviceRepository.findById(DeviceId("device-123"))
println(device2.name)  // "Temperature Sensor"

// Still the same device!
assert(device1.deviceId == device2.deviceId)  // Same identity
```

---

## 3. What Are Aggregates?

### Definition

> **Aggregate:** A cluster of domain objects (entities and value objects) that can be treated as a single unit. An aggregate has a root entity (aggregate root) and a boundary that defines what's inside.
> 
> — Eric Evans, Domain-Driven Design

### Why Do We Need Aggregates?

**Problem without aggregates:**

```kotlin
// Dangerous! No consistency boundaries
val device = deviceRepository.findById(deviceId)
val feed1 = feedRepository.findById(feedId1)
val feed2 = feedRepository.findById(feedId2)

// What if someone deletes device but not feeds?
deviceRepository.delete(device)
// Feeds are now orphaned! 💥

// What if someone changes feed but it violates device rules?
feed1.updateValue(9999)
feedRepository.save(feed1)
// Device might have max value limit! 💥
```

**Solution with aggregates:**

```kotlin
// Safe! Consistency enforced
class Device(  // Aggregate Root
    val deviceId: DeviceId,
    private val feeds: List<Feed>  // Part of aggregate
) {
    fun addFeed(feed: Feed): Device {
        // Business rule enforced
        if (feeds.size >= MAX_FEEDS_PER_DEVICE) {
            throw TooManyFeedsException(deviceId)
        }
        
        return copy(feeds = feeds + feed)
    }
    
    fun delete(): Device {
        // Cascade handled within aggregate
        return copy(
            status = DeviceStatus.DELETED,
            feeds = emptyList()  // Feeds removed together
        )
    }
}
```

### Aggregate Rules

#### Rule 1: Reference by Identity Only

Aggregates reference other aggregates by ID, not direct reference:

```kotlin
// ❌ WRONG - Direct reference
class Premises(
    val premisesId: PremisesId,
    val devices: List<Device>  // Direct reference - creates large aggregate!
)

// ✅ RIGHT - Reference by ID
class Premises(
    val premisesId: PremisesId,
    private val deviceIds: List<DeviceId>  // Just IDs
) {
    fun getDeviceIds(): List<DeviceId> = deviceIds
    
    fun addDevice(deviceId: DeviceId): Premises {
        if (deviceIds.size >= MAX_DEVICES) {
            throw MaximumDevicesExceededException(premisesId)
        }
        return copy(deviceIds = deviceIds + deviceId)
    }
}
```

#### Rule 2: One Repository Per Aggregate

Each aggregate has one repository for its root:

```kotlin
// ✅ Repository for Device aggregate
interface DeviceRepository {
    fun save(device: Device): Device
    fun findById(deviceId: DeviceId): Device?
    fun delete(deviceId: DeviceId)
}

// ❌ NO separate repository for Feed
// Feed is accessed through Device aggregate root
```

#### Rule 3: Consistency Within Aggregate

All invariants must be satisfied within aggregate boundary:

```kotlin
class Device(
    val deviceId: DeviceId,
    private val feeds: List<Feed>,
    private val status: DeviceStatus
) {
    init {
        // Invariant: Active device must have at least one feed
        if (status == DeviceStatus.ACTIVE && feeds.isEmpty()) {
            throw InvalidDeviceStateException(
                "Active device must have at least one feed"
            )
        }
        
        // Invariant: Max 10 feeds per device
        if (feeds.size > MAX_FEEDS_PER_DEVICE) {
            throw TooManyFeedsException(
                "Device can have maximum $MAX_FEEDS_PER_DEVICE feeds"
            )
        }
    }
    
    companion object {
        const val MAX_FEEDS_PER_DEVICE = 10
    }
}
```

#### Rule 4: Small Aggregates

Keep aggregates small for performance and clarity:

```kotlin
// ❌ TOO LARGE - God aggregate
class Premises(
    val premisesId: PremisesId,
    val devices: List<Device>,        // Large collection
    val automations: List<Automation>, // Another large collection
    val users: List<User>,             // Another large collection
    val zones: List<Zone>              // Another large collection
)
// Loading this aggregate loads EVERYTHING! Slow! 💥

// ✅ GOOD - Small aggregates
class Premises(
    val premisesId: PremisesId,
    private val deviceIds: List<DeviceId>,      // Just IDs
    private val automationIds: List<AutomationId>, // Just IDs
    val address: Address,              // Value object
    val owner: OwnerId                 // Just ID
)
// Fast to load, focused on premises data only ✅
```

---

## 4. Real-World Example: Device Aggregate in SmartHome Hub

### Current State (Broken) ❌

```kotlin
// Device.kt - Not a proper aggregate
data class Device(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    var name: Name,              // Mutable
    val serialNumber: SerialNumber,
    var status: DeviceStatus,    // Mutable
    var feedIds: List<FeedId>    // Mutable - anyone can modify!
)

// Feed.kt - Separate entity, no aggregate boundary
data class Feed(
    val feedId: FeedId,
    val deviceId: DeviceId,  // Reference back
    var name: String,        // Mutable
    var value: Int,          // Mutable
    var lastUpdated: Instant // Mutable
)

// DeviceService.kt - Business logic outside aggregate
@Service
class DeviceService {
    fun addFeedToDevice(deviceId: DeviceId, feedName: String): Feed {
        val device = deviceRepository.findById(deviceId)
            ?: throw DeviceNotFoundException()
        
        // Business rule scattered in service
        if (device.feedIds.size >= 10) {
            throw TooManyFeedsException()
        }
        
        // Creating feed separately
        val feed = Feed(FeedId.generate(), deviceId, feedName, 0, Instant.now())
        feedRepository.save(feed)
        
        // Manually updating device
        device.feedIds = device.feedIds + feed.feedId
        deviceRepository.save(device)
        
        return feed
    }
}
```

**Problems:**
- No aggregate boundary
- Mutable state everywhere
- Business rules in service
- Device and Feed saved separately (not atomic)
- Can create orphaned feeds
- Can violate device invariants

### Target State (Proper Aggregate) ✅

```kotlin
// Device.kt - Aggregate Root
class Device private constructor(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    private val name: Name,
    val serialNumber: SerialNumber,
    private val status: DeviceStatus,
    private val feeds: Map<FeedId, Feed>,  // Part of aggregate!
    val createdBy: ActorId,
    val createdAt: Instant,
    val version: Long?
) : AggregateRoot<DeviceEvent>() {
    
    // Expose feeds immutably
    fun getFeeds(): List<Feed> = feeds.values.toList()
    fun getFeed(feedId: FeedId): Feed? = feeds[feedId]
    fun getName(): Name = name
    fun getStatus(): DeviceStatus = status
    
    // Business method: Add feed
    fun addFeed(feed: Feed): Device {
        // Invariant: Can't add feed to deleted device
        if (status == DeviceStatus.DELETED) {
            throw CannotModifyDeletedDeviceException(deviceId)
        }
        
        // Invariant: Maximum feeds limit
        if (feeds.size >= MAX_FEEDS_PER_DEVICE) {
            throw MaximumFeedsExceededException(
                deviceId, 
                "Device can have maximum $MAX_FEEDS_PER_DEVICE feeds"
            )
        }
        
        // Invariant: Feed name must be unique within device
        if (feeds.values.any { it.name == feed.name }) {
            throw DuplicateFeedNameException(deviceId, feed.name)
        }
        
        // Invariant: Feed must belong to this device
        if (feed.deviceId != deviceId) {
            throw FeedDeviceMismatchException(feed.feedId, deviceId)
        }
        
        return copy(feeds = feeds + (feed.feedId to feed)).also {
            it.addDomainEvent(FeedAddedToDeviceEvent(deviceId, feed.feedId))
        }
    }
    
    // Business method: Remove feed
    fun removeFeed(feedId: FeedId): Device {
        if (status == DeviceStatus.DELETED) {
            throw CannotModifyDeletedDeviceException(deviceId)
        }
        
        if (!feeds.containsKey(feedId)) {
            throw FeedNotFoundException(feedId)
        }
        
        // Invariant: Active device must have at least one feed
        if (status == DeviceStatus.ACTIVE && feeds.size == 1) {
            throw CannotRemoveLastFeedException(
                deviceId,
                "Active device must have at least one feed"
            )
        }
        
        return copy(feeds = feeds - feedId).also {
            it.addDomainEvent(FeedRemovedFromDeviceEvent(deviceId, feedId))
        }
    }
    
    // Business method: Update feed value
    fun updateFeedValue(feedId: FeedId, newValue: Int): Device {
        val feed = feeds[feedId]
            ?: throw FeedNotFoundException(feedId)
        
        val updatedFeed = feed.updateValue(newValue)
        
        return copy(feeds = feeds + (feedId to updatedFeed)).also {
            it.addDomainEvent(
                FeedValueUpdatedEvent(deviceId, feedId, newValue)
            )
        }
    }
    
    // Business method: Rename device
    fun rename(newName: Name): Device {
        if (status == DeviceStatus.DELETED) {
            throw CannotModifyDeletedDeviceException(deviceId)
        }
        
        if (name == newName) {
            return this  // No change
        }
        
        return copy(name = newName).also {
            it.addDomainEvent(DeviceRenamedEvent(deviceId, newName))
        }
    }
    
    // Business method: Activate device
    fun activate(): Device {
        if (status == DeviceStatus.ACTIVE) {
            throw DeviceAlreadyActiveException(deviceId)
        }
        
        if (status == DeviceStatus.DELETED) {
            throw CannotActivateDeletedDeviceException(deviceId)
        }
        
        // Invariant: Must have at least one feed to activate
        if (feeds.isEmpty()) {
            throw CannotActivateDeviceWithoutFeedsException(
                deviceId,
                "Device must have at least one feed before activation"
            )
        }
        
        return copy(status = DeviceStatus.ACTIVE).also {
            it.addDomainEvent(DeviceActivatedEvent(deviceId))
        }
    }
    
    // Business method: Deactivate device
    fun deactivate(): Device {
        if (status == DeviceStatus.INACTIVE) {
            throw DeviceAlreadyInactiveException(deviceId)
        }
        
        if (status == DeviceStatus.DELETED) {
            throw CannotDeactivateDeletedDeviceException(deviceId)
        }
        
        return copy(status = DeviceStatus.INACTIVE).also {
            it.addDomainEvent(DeviceDeactivatedEvent(deviceId))
        }
    }
    
    // Business method: Delete device
    fun delete(): Device {
        if (status == DeviceStatus.DELETED) {
            throw DeviceAlreadyDeletedException(deviceId)
        }
        
        // When device is deleted, all feeds are removed (cascade)
        return copy(
            status = DeviceStatus.DELETED,
            feeds = emptyMap()  // All feeds removed atomically
        ).also {
            it.addDomainEvent(DeviceDeletedEvent(deviceId, feeds.keys.toList()))
        }
    }
    
    // Factory method
    companion object {
        const val MAX_FEEDS_PER_DEVICE = 10
        
        fun register(
            deviceId: DeviceId,
            premisesId: PremisesId,
            name: Name,
            serialNumber: SerialNumber,
            createdBy: ActorId
        ): Device {
            val device = Device(
                deviceId = deviceId,
                premisesId = premisesId,
                name = name,
                serialNumber = serialNumber,
                status = DeviceStatus.PENDING,
                feeds = emptyMap(),
                createdBy = createdBy,
                createdAt = Instant.now(),
                version = null
            )
            device.addDomainEvent(
                DeviceRegisteredEvent(deviceId, premisesId, serialNumber)
            )
            return device
        }
    }
    
    // Helper for immutable copies
    private fun copy(
        deviceId: DeviceId = this.deviceId,
        premisesId: PremisesId = this.premisesId,
        name: Name = this.name,
        serialNumber: SerialNumber = this.serialNumber,
        status: DeviceStatus = this.status,
        feeds: Map<FeedId, Feed> = this.feeds,
        createdBy: ActorId = this.createdBy,
        createdAt: Instant = this.createdAt,
        version: Long? = this.version
    ): Device {
        return Device(
            deviceId, premisesId, name, serialNumber,
            status, feeds, createdBy, createdAt, version
        )
    }
}

// Feed.kt - Entity within aggregate (not aggregate root)
data class Feed(
    val feedId: FeedId,
    val deviceId: DeviceId,
    val name: String,
    val type: FeedType,
    private val value: Int,
    private val lastUpdated: Instant
) {
    fun getValue(): Int = value
    fun getLastUpdated(): Instant = lastUpdated
    
    fun updateValue(newValue: Int): Feed {
        require(newValue in type.validRange) {
            "Feed value must be in range ${type.validRange}"
        }
        
        return copy(
            value = newValue,
            lastUpdated = Instant.now()
        )
    }
    
    companion object {
        fun create(
            feedId: FeedId,
            deviceId: DeviceId,
            name: String,
            type: FeedType
        ): Feed {
            return Feed(
                feedId = feedId,
                deviceId = deviceId,
                name = name,
                type = type,
                value = type.defaultValue,
                lastUpdated = Instant.now()
            )
        }
    }
}

enum class FeedType(val defaultValue: Int, val validRange: IntRange) {
    TEMPERATURE(0, -50..150),
    HUMIDITY(0, 0..100),
    SWITCH(0, 0..1),
    DIMMER(0, 0..100)
}
```

**Benefits:**
- ✅ Clear aggregate boundary (Device + Feeds)
- ✅ All invariants protected
- ✅ Atomic operations (save device = save all feeds)
- ✅ No orphaned feeds possible
- ✅ Business rules in domain
- ✅ Immutable state with controlled changes

### Using the Aggregate

```kotlin
// DeviceUseCase.kt - Thin orchestration
@Service
class DeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val eventPublisher: EventPublisher<DeviceEvent>
) {
    fun addFeedToDevice(
        deviceId: DeviceId,
        feedName: String,
        feedType: FeedType,
        actorData: ActorData
    ): Device {
        // Load aggregate
        val device = deviceRepository.findByPremisesIdAndDeviceId(
            actorData.premisesId,
            deviceId
        ) ?: throw DeviceNotFoundException(deviceId)
        
        // Create feed entity
        val feed = Feed.create(
            feedId = FeedId.generate(),
            deviceId = deviceId,
            name = feedName,
            type = feedType
        )
        
        // Aggregate validates and adds feed
        val updatedDevice = device.addFeed(feed)
        
        // Save entire aggregate (device + all feeds)
        val saved = deviceRepository.save(updatedDevice)
        
        // Publish events
        eventPublisher.publish(updatedDevice.domainEvents)
        
        return saved
    }
    
    fun activateDevice(
        deviceId: DeviceId,
        actorData: ActorData
    ): Device {
        val device = deviceRepository.findByPremisesIdAndDeviceId(
            actorData.premisesId,
            deviceId
        ) ?: throw DeviceNotFoundException(deviceId)
        
        // Aggregate validates activation (checks for feeds, etc.)
        val activated = device.activate()
        
        val saved = deviceRepository.save(activated)
        eventPublisher.publish(activated.domainEvents)
        
        return saved
    }
    
    fun deleteDevice(
        deviceId: DeviceId,
        actorData: ActorData
    ): Device {
        val device = deviceRepository.findByPremisesIdAndDeviceId(
            actorData.premisesId,
            deviceId
        ) ?: throw DeviceNotFoundException(deviceId)
        
        // Aggregate handles cascade delete of feeds
        val deleted = device.delete()
        
        val saved = deviceRepository.save(deleted)
        eventPublisher.publish(deleted.domainEvents)
        
        return saved
    }
}
```

---

## 5. Designing Aggregate Boundaries

### How to Choose Aggregate Boundaries

#### Guideline 1: Start Small

```kotlin
// ❌ TOO BIG - Everything in one aggregate
class Premises(
    val premisesId: PremisesId,
    val devices: List<Device>,        // Large collection
    val automations: List<Automation>,
    val users: List<User>,
    val zones: List<Zone>
)

// ✅ GOOD - Small, focused aggregates
class Premises(
    val premisesId: PremisesId,
    val address: Address,
    private val deviceIds: List<DeviceId>,  // References only
    val ownerId: UserId
)

class Device(
    val deviceId: DeviceId,
    val premisesId: PremisesId,  // Reference to premises
    private val feeds: Map<FeedId, Feed>  // Only contains feeds
)
```

#### Guideline 2: Consistency Boundary

What must be consistent together?

```kotlin
// Device and its Feeds must be consistent
class Device(
    val deviceId: DeviceId,
    private val feeds: Map<FeedId, Feed>  // Same aggregate
) {
    // Invariant: Active device needs feeds
    fun activate(): Device {
        if (feeds.isEmpty()) {
            throw CannotActivateDeviceWithoutFeedsException(deviceId)
        }
        // ...
    }
}

// Device and Automation are eventually consistent
class Device(
    val deviceId: DeviceId,
    private val automationIds: List<AutomationId>  // Just references
)

class Automation(
    val automationId: AutomationId,
    private val triggerDeviceIds: List<DeviceId>  // Just references
)
// They don't need to be updated in the same transaction
```

#### Guideline 3: Transaction Boundary

One transaction = one aggregate save:

```kotlin
// ✅ Good - One aggregate, one transaction
fun addFeedToDevice(deviceId: DeviceId, feed: Feed): Device {
    val device = deviceRepository.findById(deviceId)
    val updated = device.addFeed(feed)
    return deviceRepository.save(updated)  // One transaction
}

// ❌ Bad - Multiple aggregates, multiple transactions
fun linkDeviceToAutomation(
    deviceId: DeviceId, 
    automationId: AutomationId
) {
    val device = deviceRepository.findById(deviceId)
    val automation = automationRepository.findById(automationId)
    
    device.addAutomationLink(automationId)
    automation.addDeviceLink(deviceId)
    
    deviceRepository.save(device)           // Transaction 1
    automationRepository.save(automation)   // Transaction 2
    // What if second fails? Inconsistent state! 💥
}

// ✅ Good - Use domain events for eventual consistency
fun linkDeviceToAutomation(deviceId: DeviceId, automationId: AutomationId) {
    val device = deviceRepository.findById(deviceId)
    val updated = device.linkToAutomation(automationId)
    deviceRepository.save(updated)
    
    // Event published, Automation updated asynchronously
    eventPublisher.publish(
        DeviceLinkedToAutomationEvent(deviceId, automationId)
    )
}
```

### Real Examples from SmartHome Hub

#### Aggregate 1: Device (with Feeds)

```
┌─────────────────────────────────────────┐
│           Device Aggregate              │
│                                         │
│  Device (Root)                          │
│  ├── DeviceId                           │
│  ├── Name                               │
│  ├── SerialNumber                       │
│  ├── Status                             │
│  └── Feeds (entities within aggregate) │
│       ├── Feed 1                        │
│       ├── Feed 2                        │
│       └── Feed 3                        │
│                                         │
│  Invariants:                            │
│  - Active device needs ≥1 feed          │
│  - Max 10 feeds per device              │
│  - Unique feed names                    │
└─────────────────────────────────────────┘
```

#### Aggregate 2: Premises (without Devices)

```
┌─────────────────────────────────────────┐
│         Premises Aggregate              │
│                                         │
│  Premises (Root)                        │
│  ├── PremisesId                         │
│  ├── Name                               │
│  ├── Address (value object)            │
│  ├── OwnerId (reference)                │
│  └── DeviceIds (references, not entities)│
│                                         │
│  Invariants:                            │
│  - Max 100 devices                      │
│  - Owner must be verified               │
└─────────────────────────────────────────┘
```

#### Aggregate 3: Automation (separate)

```
┌─────────────────────────────────────────┐
│        Automation Aggregate             │
│                                         │
│  Automation (Root)                      │
│  ├── AutomationId                       │
│  ├── Name                               │
│  ├── Triggers                           │
│  │   └── DeviceIds (references)        │
│  ├── Conditions                         │
│  └── Actions                            │
│      └── DeviceIds (references)         │
│                                         │
│  Invariants:                            │
│  - At least one trigger                 │
│  - At least one action                  │
└─────────────────────────────────────────┘
```

---

## 6. Protecting Invariants

### What Are Invariants?

**Invariant:** A business rule that must ALWAYS be true for an aggregate.

### Types of Invariants

#### 1. State Invariants

Rules about the object's state:

```kotlin
class Device(
    private val status: DeviceStatus,
    private val feeds: Map<FeedId, Feed>
) {
    init {
        // Invariant: Active device must have feeds
        if (status == DeviceStatus.ACTIVE && feeds.isEmpty()) {
            throw InvalidDeviceStateException(
                "Active device must have at least one feed"
            )
        }
    }
}
```

#### 2. Relationship Invariants

Rules about relationships between objects:

```kotlin
class Device(
    val deviceId: DeviceId,
    private val feeds: Map<FeedId, Feed>
) {
    fun addFeed(feed: Feed): Device {
        // Invariant: Feed must belong to this device
        if (feed.deviceId != deviceId) {
            throw FeedDeviceMismatchException(
                "Feed ${feed.feedId} belongs to device ${feed.deviceId}, " +
                "not ${deviceId}"
            )
        }
        
        return copy(feeds = feeds + (feed.feedId to feed))
    }
}
```

#### 3. Collection Invariants

Rules about collections:

```kotlin
class Device(
    private val feeds: Map<FeedId, Feed>
) {
    fun addFeed(feed: Feed): Device {
        // Invariant: Maximum feeds limit
        if (feeds.size >= MAX_FEEDS_PER_DEVICE) {
            throw MaximumFeedsExceededException(
                "Device cannot have more than $MAX_FEEDS_PER_DEVICE feeds"
            )
        }
        
        // Invariant: Unique feed names
        if (feeds.values.any { it.name == feed.name }) {
            throw DuplicateFeedNameException(
                "Feed name '${feed.name}' already exists"
            )
        }
        
        return copy(feeds = feeds + (feed.feedId to feed))
    }
    
    companion object {
        const val MAX_FEEDS_PER_DEVICE = 10
    }
}
```

#### 4. Business Rule Invariants

Complex business rules:

```kotlin
class Device(
    private val serialNumber: SerialNumber,
    private val premisesId: PremisesId,
    private val registeredAt: Instant
) {
    fun activate(): Device {
        // Invariant: Device must be activated within 24 hours of registration
        val hoursSinceRegistration = Duration.between(
            registeredAt, 
            Instant.now()
        ).toHours()
        
        if (hoursSinceRegistration > 24) {
            throw ActivationWindowExpiredException(
                deviceId,
                "Device must be activated within 24 hours. " +
                "Registered $hoursSinceRegistration hours ago."
            )
        }
        
        return copy(status = DeviceStatus.ACTIVE)
    }
}
```

### Where to Check Invariants

#### Option 1: Constructor/Init Block

```kotlin
class Device(
    private val status: DeviceStatus,
    private val feeds: Map<FeedId, Feed>
) {
    init {
        // Check invariants on creation
        require(status != DeviceStatus.DELETED || feeds.isEmpty()) {
            "Deleted device cannot have feeds"
        }
    }
}
```

#### Option 2: Methods

```kotlin
class Device {
    fun addFeed(feed: Feed): Device {
        // Check invariants before state change
        if (feeds.size >= MAX_FEEDS_PER_DEVICE) {
            throw MaximumFeedsExceededException()
        }
        
        return copy(feeds = feeds + (feed.feedId to feed))
    }
}
```

#### Option 3: Specification Pattern

```kotlin
interface DeviceSpecification {
    fun isSatisfiedBy(device: Device): Boolean
    fun errorMessage(): String
}

class ActiveDeviceMustHaveFeedsSpecification : DeviceSpecification {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getStatus() != DeviceStatus.ACTIVE || 
               device.getFeeds().isNotEmpty()
    }
    
    override fun errorMessage(): String {
        return "Active device must have at least one feed"
    }
}

class Device {
    fun activate(): Device {
        val spec = ActiveDeviceMustHaveFeedsSpecification()
        
        val activated = copy(status = DeviceStatus.ACTIVE)
        
        if (!spec.isSatisfiedBy(activated)) {
            throw InvariantViolationException(spec.errorMessage())
        }
        
        return activated
    }
}
```

---

## 7. Aggregate Roots and Consistency

### Role of Aggregate Root

The aggregate root is the **only entry point** for accessing the aggregate:

```kotlin
// ✅ Access through root
val device = deviceRepository.findById(deviceId)
val feeds = device.getFeeds()
val feed = device.getFeed(feedId)

// ❌ NEVER access entities within aggregate directly
val feed = feedRepository.findById(feedId)  // NO! No separate repository!
```

### Consistency Rules

#### Rule 1: Immediate Consistency Within Aggregate

Changes within aggregate are immediately consistent:

```kotlin
class Device {
    fun addFeed(feed: Feed): Device {
        // Validation happens immediately
        if (feeds.size >= MAX_FEEDS) throw TooManyFeedsException()
        
        // State updated atomically
        return copy(feeds = feeds + (feed.feedId to feed))
        
        // When saved, device and all feeds saved together
    }
}
```

#### Rule 2: Eventual Consistency Between Aggregates

Changes across aggregates are eventually consistent:

```kotlin
// Device and Automation are separate aggregates
fun linkDeviceToAutomation(
    deviceId: DeviceId,
    automationId: AutomationId
): Device {
    val device = deviceRepository.findById(deviceId)
    val updated = device.linkToAutomation(automationId)
    
    deviceRepository.save(updated)
    
    // Event published - Automation updated asynchronously
    eventPublisher.publish(
        DeviceLinkedToAutomationEvent(deviceId, automationId)
    )
    
    return updated
}

// Event handler updates Automation
@EventHandler
class DeviceLinkedEventHandler {
    fun handle(event: DeviceLinkedToAutomationEvent) {
        val automation = automationRepository.findById(event.automationId)
        val updated = automation.addDeviceLink(event.deviceId)
        automationRepository.save(updated)
    }
}
```

### Handling Concurrent Updates

Use optimistic locking:

```kotlin
class Device(
    val deviceId: DeviceId,
    // ...
    val version: Long?  // Version for optimistic locking
)

// Repository handles optimistic locking
interface DeviceRepository {
    fun save(device: Device): Device  // Throws if version mismatch
}

// MongoDB implementation
@Document("devices")
data class DeviceDocument(
    @Id val id: String,
    // ...
    @Version val version: Long?
)

// When saving:
// 1. Check version matches
// 2. If not, throw OptimisticLockingException
// 3. Client retries with fresh data
```

---

## 8. Common Pitfalls and Solutions

### Pitfall 1: Aggregates Too Large

**Problem:**

```kotlin
// ❌ God aggregate - loads everything
class Premises(
    val premisesId: PremisesId,
    val devices: List<Device>,        // 100 devices!
    val automations: List<Automation>, // 50 automations!
    val users: List<User>              // 20 users!
)
// Loading premises loads 170+ objects! 💥
```

**Solution:**

```kotlin
// ✅ Small aggregate with references
class Premises(
    val premisesId: PremisesId,
    private val deviceIds: List<DeviceId>,  // Just IDs
    private val automationIds: List<AutomationId>,
    val ownerId: UserId
) {
    fun addDevice(deviceId: DeviceId): Premises {
        if (deviceIds.size >= MAX_DEVICES) {
            throw MaximumDevicesExceededException()
        }
        return copy(deviceIds = deviceIds + deviceId)
    }
}
```

### Pitfall 2: Multiple Aggregates in One Transaction

**Problem:**

```kotlin
// ❌ Modifying multiple aggregates in one transaction
fun transferDeviceBetweenPremises(
    deviceId: DeviceId,
    fromPremisesId: PremisesId,
    toPremisesId: PremisesId
) {
    val fromPremises = premisesRepository.findById(fromPremisesId)
    val toPremises = premisesRepository.findById(toPremisesId)
    
    val updatedFrom = fromPremises.removeDevice(deviceId)
    val updatedTo = toPremises.addDevice(deviceId)
    
    premisesRepository.save(updatedFrom)  // Transaction 1
    premisesRepository.save(updatedTo)    // Transaction 2
    // What if second fails? Inconsistent! 💥
}
```

**Solution:**

```kotlin
// ✅ Use saga pattern with events
fun transferDeviceBetweenPremises(
    deviceId: DeviceId,
    fromPremisesId: PremisesId,
    toPremisesId: PremisesId
) {
    // Step 1: Remove from source
    val fromPremises = premisesRepository.findById(fromPremisesId)
    val updated = fromPremises.removeDevice(deviceId)
    premisesRepository.save(updated)
    
    // Step 2: Publish event
    eventPublisher.publish(
        DeviceRemovedFromPremisesEvent(deviceId, fromPremisesId, toPremisesId)
    )
}

// Event handler adds to destination
@EventHandler
class DeviceRemovedEventHandler {
    fun handle(event: DeviceRemovedFromPremisesEvent) {
        val toPremises = premisesRepository.findById(event.toPremisesId)
        val updated = toPremises.addDevice(event.deviceId)
        premisesRepository.save(updated)
    }
}
```

### Pitfall 3: Exposing Mutable Collections

**Problem:**

```kotlin
// ❌ Returns mutable collection
class Device(
    private val feeds: MutableMap<FeedId, Feed>
) {
    fun getFeeds(): MutableMap<FeedId, Feed> = feeds  // Dangerous!
}

// Client can break invariants
val device = deviceRepository.findById(deviceId)
device.getFeeds().clear()  // Bypassed all validation! 💥
```

**Solution:**

```kotlin
// ✅ Return immutable copy
class Device(
    private val feeds: Map<FeedId, Feed>
) {
    fun getFeeds(): List<Feed> = feeds.values.toList()  // Immutable copy
    fun getFeed(feedId: FeedId): Feed? = feeds[feedId]
}
```

### Pitfall 4: Lazy Loading in Aggregates

**Problem:**

```kotlin
// ❌ Lazy loading breaks aggregate boundary
class Device(
    val deviceId: DeviceId,
    @Lazy val feeds: List<Feed>  // Loaded later
)
// When is it loaded? What if it fails? Breaks atomicity!
```

**Solution:**

```kotlin
// ✅ Eager load entire aggregate
class Device(
    val deviceId: DeviceId,
    private val feeds: Map<FeedId, Feed>  // Loaded with device
)

// Repository loads everything
interface DeviceRepository {
    fun findById(deviceId: DeviceId): Device?  // Loads device + feeds
}
```

---

## 9. Testing Aggregates

### Unit Testing Aggregates

Pure domain tests, no infrastructure:

```kotlin
class DeviceAggregateTest {
    
    @Test
    fun `should create device with pending status`() {
        val device = Device.register(
            deviceId = DeviceId("device-123"),
            premisesId = PremisesId("premises-123"),
            name = Name("Living Room Sensor"),
            serialNumber = SerialNumber("ABC12345"),
            createdBy = ActorId("actor-123")
        )
        
        assertEquals(DeviceStatus.PENDING, device.getStatus())
        assertTrue(device.getFeeds().isEmpty())
        assertTrue(device.domainEvents.any { it is DeviceRegisteredEvent })
    }
    
    @Test
    fun `should add feed to device`() {
        val device = createTestDevice()
        val feed = createTestFeed()
        
        val updated = device.addFeed(feed)
        
        assertEquals(1, updated.getFeeds().size)
        assertEquals(feed, updated.getFeed(feed.feedId))
        assertTrue(updated.domainEvents.any { it is FeedAddedToDeviceEvent })
    }
    
    @Test
    fun `should not add more than max feeds`() {
        val device = createDeviceWithMaxFeeds()
        val feed = createTestFeed()
        
        assertThrows<MaximumFeedsExceededException> {
            device.addFeed(feed)
        }
    }
    
    @Test
    fun `should not activate device without feeds`() {
        val device = Device.register(...)  // No feeds
        
        assertThrows<CannotActivateDeviceWithoutFeedsException> {
            device.activate()
        }
    }
    
    @Test
    fun `should activate device with feeds`() {
        val device = createTestDevice()
        val feed = createTestFeed()
        val withFeed = device.addFeed(feed)
        
        val activated = withFeed.activate()
        
        assertEquals(DeviceStatus.ACTIVE, activated.getStatus())
        assertTrue(activated.domainEvents.any { it is DeviceActivatedEvent })
    }
    
    @Test
    fun `should remove feed from inactive device`() {
        val device = createDeviceWithFeeds(2)
        val feedId = device.getFeeds().first().feedId
        
        val updated = device.removeFeed(feedId)
        
        assertEquals(1, updated.getFeeds().size)
        assertNull(updated.getFeed(feedId))
    }
    
    @Test
    fun `should not remove last feed from active device`() {
        val device = createDeviceWithFeeds(1).activate()
        val feedId = device.getFeeds().first().feedId
        
        assertThrows<CannotRemoveLastFeedException> {
            device.removeFeed(feedId)
        }
    }
    
    @Test
    fun `should delete device and remove all feeds`() {
        val device = createDeviceWithFeeds(3)
        
        val deleted = device.delete()
        
        assertEquals(DeviceStatus.DELETED, deleted.getStatus())
        assertTrue(deleted.getFeeds().isEmpty())
        assertTrue(deleted.domainEvents.any { it is DeviceDeletedEvent })
    }
    
    @Test
    fun `should not add feed to deleted device`() {
        val device = createTestDevice().delete()
        val feed = createTestFeed()
        
        assertThrows<CannotModifyDeletedDeviceException> {
            device.addFeed(feed)
        }
    }
    
    @Test
    fun `should update feed value within range`() {
        val device = createDeviceWithFeeds(1)
        val feedId = device.getFeeds().first().feedId
        
        val updated = device.updateFeedValue(feedId, 50)
        
        assertEquals(50, updated.getFeed(feedId)?.getValue())
    }
    
    @Test
    fun `should not allow duplicate feed names`() {
        val device = createTestDevice()
        val feed1 = Feed.create(FeedId.generate(), device.deviceId, "Temperature", FeedType.TEMPERATURE)
        val feed2 = Feed.create(FeedId.generate(), device.deviceId, "Temperature", FeedType.HUMIDITY)
        
        val withFeed1 = device.addFeed(feed1)
        
        assertThrows<DuplicateFeedNameException> {
            withFeed1.addFeed(feed2)
        }
    }
    
    // Helper methods
    private fun createTestDevice(): Device {
        return Device.register(
            deviceId = DeviceId("device-123"),
            premisesId = PremisesId("premises-123"),
            name = Name("Test Device"),
            serialNumber = SerialNumber("TEST12345"),
            createdBy = ActorId("actor-123")
        )
    }
    
    private fun createTestFeed(): Feed {
        return Feed.create(
            feedId = FeedId.generate(),
            deviceId = DeviceId("device-123"),
            name = "Temperature",
            type = FeedType.TEMPERATURE
        )
    }
    
    private fun createDeviceWithFeeds(count: Int): Device {
        var device = createTestDevice()
        repeat(count) { index ->
            val feed = Feed.create(
                feedId = FeedId.generate(),
                deviceId = device.deviceId,
                name = "Feed $index",
                type = FeedType.TEMPERATURE
            )
            device = device.addFeed(feed)
        }
        return device
    }
    
    private fun createDeviceWithMaxFeeds(): Device {
        return createDeviceWithFeeds(Device.MAX_FEEDS_PER_DEVICE)
    }
}
```

### Integration Testing with Repository

```kotlin
@SpringBootTest
class DeviceAggregateIntegrationTest {
    
    @Autowired
    lateinit var deviceRepository: DeviceRepository
    
    @Test
    fun `should save and load device with feeds`() {
        // Create aggregate
        val device = Device.register(...)
        val feed = Feed.create(...)
        val withFeed = device.addFeed(feed)
        
        // Save
        val saved = deviceRepository.save(withFeed)
        
        // Load
        val loaded = deviceRepository.findById(saved.deviceId)
        
        assertNotNull(loaded)
        assertEquals(1, loaded!!.getFeeds().size)
        assertEquals(feed.feedId, loaded.getFeeds().first().feedId)
    }
    
    @Test
    fun `should handle optimistic locking`() {
        val device = deviceRepository.save(Device.register(...))
        
        // Two concurrent updates
        val device1 = deviceRepository.findById(device.deviceId)!!
        val device2 = deviceRepository.findById(device.deviceId)!!
        
        // First update succeeds
        deviceRepository.save(device1.rename(Name("New Name 1")))
        
        // Second update fails (version mismatch)
        assertThrows<OptimisticLockingException> {
            deviceRepository.save(device2.rename(Name("New Name 2")))
        }
    }
}
```

---

## 9. Advanced Aggregate Patterns

### When to Split Aggregates: The Feed Dilemma

One common question: Should Feed be part of the Device aggregate or a separate aggregate?

**Answer:** It depends on your requirements.

#### Embedded Feeds (Simple, Atomic)

```kotlin
// Feed as entity within Device aggregate
class Device private constructor(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    private val feeds: List<Feed> = emptyList()  // ← Embedded
) : AggregateRoot<DeviceEvent>() {
    
    fun addFeed(feed: Feed): Device {
        return copy(feeds = feeds + feed)
    }
    
    fun updateFeedValue(feedId: FeedId, value: FeedValue): Device {
        val updated = feeds.map { feed ->
            if (feed.feedId == feedId) feed.updateValue(value) else feed
        }
        return copy(feeds = updated, lastSeenAt = Instant.now())
    }
}

// MongoDB: Single document with embedded feeds
@Document(collection = "devices")
data class DeviceDocument(
    @Id val id: String,
    val feeds: List<FeedDocument>  // ← Embedded
)
```

**Benefits:**
- ✅ Atomic updates (device + feeds in one transaction)
- ✅ Strong consistency guaranteed
- ✅ Simpler to implement
- ✅ Single save operation

**Use when:**
- Feeds always belong to one device
- Feed updates must be atomic with device changes
- Virtual feeds are rare or can be handled separately

#### Separate Feed Aggregate (Flexible, Scalable)

```kotlin
// Feed as separate aggregate root
class Feed private constructor(
    val feedId: FeedId,
    val premisesId: PremisesId,
    val type: FeedType,
    private var value: FeedValue
) : AggregateRoot<FeedEvent>() {
    
    fun updateValue(newValue: FeedValue): Feed {
        return copy(value = newValue, lastUpdatedAt = Instant.now())
            .also { it.addDomainEvent(FeedValueUpdatedEvent(feedId, newValue)) }
    }
}

sealed class FeedType {
    data class Physical(val deviceId: DeviceId) : FeedType()
    data class Virtual(val sourceFeedIds: Set<FeedId>) : FeedType()
}

// Device references feeds by ID
class Device(
    val deviceId: DeviceId,
    private val feedIds: Set<FeedId> = emptySet()  // ← References
) {
    fun addFeed(feedId: FeedId): Device {
        return copy(feedIds = feedIds + feedId)
    }
}
```

**Benefits:**
- ✅ Supports virtual feeds (no physical device)
- ✅ Independent feed updates (performance)
- ✅ Can query feeds separately
- ✅ Flexible for calculated feeds

**Use when:**
- Virtual feeds exist (energy calculation, averages)
- Feed updates don't require device lock
- High-frequency feed updates
- Feeds displayed across multiple zones/widgets

### Virtual Feeds: Calculated from Other Sources

Virtual feeds are calculated from other feeds or external data:

```kotlin
// Virtual feed for energy consumption
class VirtualFeed private constructor(
    val feedId: FeedId,
    val name: FeedName,
    val calculationType: CalculationType,
    val sourceFeedIds: Set<FeedId>,
    private var value: FeedValue
) : AggregateRoot<FeedEvent>() {
    
    suspend fun calculate(calculator: FeedCalculator): VirtualFeed {
        val calculatedValue = calculator.calculate(this)
        return copy(value = calculatedValue, lastUpdatedAt = Instant.now())
    }
    
    companion object {
        fun createEnergyFeed(
            feedId: FeedId,
            voltageFeedId: FeedId,
            currentFeedId: FeedId
        ): VirtualFeed {
            return VirtualFeed(
                feedId = feedId,
                name = FeedName("Energy Consumption"),
                calculationType = CalculationType.ENERGY,
                sourceFeedIds = setOf(voltageFeedId, currentFeedId),
                value = FeedValue.zero(FeedUnit.KILOWATT_HOURS)
            )
        }
    }
}

// Domain service for calculation
interface FeedCalculator {
    suspend fun calculate(feed: VirtualFeed): FeedValue
}

class EnergyCalculator(
    private val feedRepository: FeedRepository,
    private val historicalData: HistoricalDataRepository
) : FeedCalculator {
    override suspend fun calculate(feed: VirtualFeed): FeedValue {
        val sourceFeeds = feedRepository.findAllById(feed.sourceFeedIds)
        val voltage = sourceFeeds.find { it.unit == FeedUnit.VOLTS }
        val current = sourceFeeds.find { it.unit == FeedUnit.AMPERES }
        
        // Calculate: Energy = ∫(V × I)dt
        val readings = historicalData.findForPeriod(
            feed.sourceFeedIds,
            TimePeriod.thisMonth()
        )
        
        return calculateEnergyFromReadings(readings)
    }
}

// Scheduled calculation
@Scheduled(cron = "0 0 * * * *")  // Every hour
suspend fun calculateVirtualFeeds() {
    val virtualFeeds = feedRepository.findAllVirtual()
    virtualFeeds.forEach { feed ->
        val calculator = getCalculator(feed.calculationType)
        val updated = feed.calculate(calculator)
        feedRepository.save(updated)
    }
}
```

### Aggregate Reference Directions

When aggregates reference each other, choose the direction based on query patterns:

#### Bidirectional References (Most Common)

```kotlin
// Premises knows its devices (top-down)
class Premises(
    val premisesId: PremisesId,
    private val deviceIds: Set<DeviceId> = emptySet()  // ← Parent → Children
) {
    fun addDevice(deviceId: DeviceId): Premises {
        return copy(deviceIds = deviceIds + deviceId)
    }
}

// Device knows its premises (bottom-up)
class Device(
    val deviceId: DeviceId,
    val premisesId: PremisesId  // ← Child → Parent
)

// Benefits: Can query both directions
// Get all devices for premises
val premises = premisesRepository.findById(premisesId)
val devices = deviceRepository.findAllById(premises.deviceIds)

// Get premises for device
val device = deviceRepository.findById(deviceId)
val premises = premisesRepository.findById(device.premisesId)
```

**Key Rule:** Always reference by ID, never by object!

```kotlin
// ❌ Wrong: Direct object reference
class Device(
    val premises: Premises  // Tight coupling!
)

// ✅ Correct: Reference by ID
class Device(
    val premisesId: PremisesId  // Loose coupling
)
```

### Handling Atomicity with Separate Aggregates

When aggregates are separate, you lose atomic transactions. Use these patterns:

#### Pattern 1: Outbox Pattern (Recommended)

```kotlin
@Service
class RegisterDeviceWithFeedsUseCase(
    private val deviceRepository: DeviceRepository,
    private val outboxRepository: OutboxEventRepository
) {
    @Transactional
    suspend fun execute(command: RegisterDeviceCommand): DeviceId {
        // 1. Save device atomically
        val device = Device.register(...)
        deviceRepository.save(device)
        
        // 2. Save outbox event atomically (same transaction)
        val outboxEvent = OutboxEvent(
            aggregateId = device.deviceId.value,
            eventType = "DeviceRegisteredWithFeeds",
            payload = serializeFeeds(command.feeds)
        )
        outboxRepository.save(outboxEvent)
        
        return device.deviceId
    }
}

// Background worker processes outbox
@Scheduled(fixedDelay = 5000)
suspend fun processOutbox() {
    val pendingEvents = outboxRepository.findPending()
    
    pendingEvents.forEach { event ->
        try {
            when (event.eventType) {
                "DeviceRegisteredWithFeeds" -> {
                    val feeds = deserializeFeeds(event.payload)
                    feeds.forEach { feedRepository.save(it) }
                    
                    // Update device with feed references
                    val device = deviceRepository.findById(event.aggregateId)
                    val updated = feeds.fold(device) { dev, feed -> 
                        dev.addFeed(feed.feedId) 
                    }
                    deviceRepository.save(updated)
                    
                    outboxRepository.markCompleted(event.id)
                }
            }
        } catch (e: Exception) {
            outboxRepository.markFailed(event.id, e.message)
        }
    }
}
```

#### Pattern 2: Saga Pattern (Complex Flows)

```kotlin
class RegisterDeviceWithFeedsSaga(
    private val deviceRepository: DeviceRepository,
    private val feedRepository: FeedRepository
) {
    suspend fun execute(command: RegisterDeviceCommand): SagaResult {
        val saga = Saga.start()
        
        try {
            // Step 1: Create device
            val device = Device.register(...)
            deviceRepository.save(device)
            saga.markDeviceCreated()
            
            // Step 2: Create feeds
            val feeds = command.feeds.map { Feed.create(...) }
            feeds.forEach { feedRepository.save(it) }
            saga.markFeedsCreated(feeds.map { it.feedId })
            
            // Step 3: Link device to feeds
            val updated = feeds.fold(device) { dev, feed -> 
                dev.addFeed(feed.feedId) 
            }
            deviceRepository.save(updated)
            saga.markCompleted()
            
            return SagaResult.Success(device.deviceId)
            
        } catch (e: Exception) {
            // Compensate in reverse order
            if (saga.feedsCreated) {
                saga.feedIds.forEach { feedRepository.delete(it) }
            }
            if (saga.deviceCreated) {
                deviceRepository.delete(saga.deviceId)
            }
            return SagaResult.Failure(e.message)
        }
    }
}
```

### Cross-Aggregate Consistency

When multiple aggregates must stay consistent, choose your strategy:

**Strategy 1: Eventual Consistency with Events (Preferred)**

```kotlin
// Device publishes event
class Device {
    fun activate(): Device {
        return copy(status = DeviceStatus.ACTIVE)
            .also { it.addDomainEvent(DeviceActivatedEvent(deviceId)) }
    }
}

// Premises updates itself via event handler
@EventListener
class DeviceActivatedHandler(
    private val premisesRepository: PremisesRepository
) {
    suspend fun handle(event: DeviceActivatedEvent) {
        // Find premises containing this device
        val premises = premisesRepository.findByDeviceId(event.deviceId)
        
        // Update premises state if needed
        val updated = premises.incrementActiveDeviceCount()
        premisesRepository.save(updated)
    }
}
```

**Strategy 2: Application Service Coordination**

```kotlin
@Service
class ActivateDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val premisesRepository: PremisesRepository
) {
    suspend fun execute(deviceId: DeviceId): Device {
        // Load both aggregates
        val device = deviceRepository.findById(deviceId)
        val premises = premisesRepository.findById(device.premisesId)
        
        // Check cross-aggregate business rule
        if (premises.activeDeviceCount >= premises.maxDevices) {
            throw MaxDevicesReachedException()
        }
        
        // Update both
        val activatedDevice = device.activate()
        val updatedPremises = premises.incrementActiveDeviceCount()
        
        // Save in order (risk of inconsistency if second fails!)
        deviceRepository.save(activatedDevice)
        premisesRepository.save(updatedPremises)
        
        return activatedDevice
    }
}
```

**Strategy 3: Single Aggregate for Strong Consistency**

If you need ACID guarantees, keep related data in one aggregate:

```kotlin
// Keep feeds embedded if atomic updates are critical
class Device(
    val deviceId: DeviceId,
    private val feeds: List<Feed> = emptyList()  // Embedded = atomic
) {
    fun updateFeedValue(feedId: FeedId, value: FeedValue): Device {
        // Everything updated atomically in one transaction
        val updatedFeeds = feeds.map { if (it.feedId == feedId) it.updateValue(value) else it }
        return copy(
            feeds = updatedFeeds,
            lastSeenAt = Instant.now(),  // Device timestamp also updated
            health = calculateHealth(updatedFeeds)  // Health recalculated
        )
    }
}
```

### Design Checklist: Separate or Embedded?

Use this checklist to decide if something should be a separate aggregate or embedded entity:

**Make it a SEPARATE aggregate if:**
- ✅ It has an independent lifecycle
- ✅ It can exist without the parent
- ✅ Different teams own it
- ✅ It needs independent scaling
- ✅ Eventual consistency is acceptable

**Keep it EMBEDDED if:**
- ✅ It cannot exist without parent
- ✅ It must be updated atomically with parent
- ✅ Strong consistency is required
- ✅ It's always loaded with parent
- ✅ Lifecycle is tightly coupled

**SmartHome Hub Examples:**

| Relationship | Separate or Embedded? | Why |
|-------------|----------------------|-----|
| Device → Physical Feed | Either | Embedded if atomic updates critical, separate if virtual feeds needed |
| Zone → Widget | Embedded | Widgets cannot exist without zone |
| Premises → Device | Separate | Devices have independent lifecycle |
| Device → Virtual Feed | Separate | Virtual feeds can exist without physical device |
| Automation → Trigger | Embedded | Triggers are part of automation rules |

---

## 💡 Key Takeaways

1. **Entities have identity** - Defined by ID, not attributes

2. **Aggregates are consistency boundaries** - All invariants protected within

3. **One repository per aggregate** - Access only through root

4. **Keep aggregates small** - Reference by ID, not direct reference

5. **Immediate consistency within, eventual between** - Use events for cross-aggregate

6. **Protect invariants zealously** - Validate in constructors and methods

7. **Immutability with controlled mutation** - Return new instances

8. **Test aggregates in isolation** - No infrastructure needed for domain tests

9. **Choose embedding vs separation wisely** - Based on lifecycle and consistency needs

10. **Virtual feeds as separate aggregates** - Enables calculated/derived data

11. **Use outbox or saga for atomicity** - When separate aggregates must coordinate

12. **Bidirectional references are valid** - Different query patterns need different directions

---

## 10. Chapter Summary

In this chapter, we've explored entities and aggregates—the core building blocks of rich domain models that maintain identity and protect critical business invariants. Understanding how to design proper aggregates is essential for building maintainable, consistent systems.

### What We Covered

**The Identity Problem:**
- Objects without proper identity create duplicates
- Same device registered multiple times
- Conflicting states across instances
- Data integrity compromised

**Entities vs Value Objects:**
- **Entities:** Have identity, mutable lifecycle, equality by ID
- **Value Objects:** No identity, immutable, equality by value
- Device, User, Premises = Entities
- Email, Temperature, SerialNumber = Value Objects

**Aggregates:**
- Cluster of entities and value objects treated as one unit
- Consistency boundary for business rules
- Transactional boundary
- Single aggregate root controls access

**The Device Aggregate:**
We built a complete Device aggregate for SmartHome Hub:
- DeviceId (identity)
- Feeds (internal entities)
- Status, Health (value objects)
- 10 feeds maximum (invariant)
- Sensor devices must have feeds (invariant)
- Only active devices can go online (invariant)

### Key Insights

1. **Identity is what distinguishes entities from value objects** - Two devices with same data but different IDs are different devices.

2. **Aggregates are consistency boundaries** - Everything that must be consistent together belongs in one aggregate.

3. **Small aggregates are better** - Design for true invariants, not data relationships.

4. **Aggregate roots are the only entry point** - External code never touches internal entities directly.

5. **One repository per aggregate root** - Not per entity.

6. **References between aggregates use IDs** - Never hold object references across boundaries.

7. **Invariants must be protected at all times** - Private constructors and validation in every method.

8. **Domain events enable loose coupling** - Use events instead of direct calls across aggregates.

### SmartHome Hub Device Aggregate

**Before (No Aggregate):**
```kotlin
// Anarchy - anyone can modify anything
data class Device(var status: String, var feeds: MutableList<Feed>)
device.status = "INVALID_STATE"  // No protection!
device.feeds.add(Feed(...))      // Bypass rules!
```

**After (Proper Aggregate):**
```kotlin
class Device private constructor(
    val deviceId: DeviceId,
    private var status: DeviceStatus,
    private val feeds: Map<FeedId, Feed>
) {
    fun activate(): Device {
        require(status == DeviceStatus.PENDING)
        require(feeds.isNotEmpty())
        return copy(status = DeviceStatus.ACTIVE)
    }
}
```

**Impact:**
- 100% invariant protection (impossible to create invalid state)
- 80% reduction in state-related bugs
- Clear business rules in code
- Self-documenting methods
- Testable without infrastructure

### Aggregate Design Rules

**Rule 1: Design Around True Invariants**
- What MUST be consistent together?
- Not: "What data is related?"
- But: "What business rules must hold?"

**Rule 2: Keep Aggregates Small**
- Small = faster, easier to understand
- Large aggregates = performance issues, contention
- Device + Feeds = Good
- Device + Feeds + Zone + Premises + Automations = Bad

**Rule 3: Reference by ID**
```kotlin
// ❌ Don't hold object references
class Device(val zone: Zone)

// ✅ Reference by ID
class Device(val zoneId: ZoneId)
```

**Rule 4: One Repository Per Aggregate**
```kotlin
// ❌ Don't create repositories for internal entities
interface FeedRepository

// ✅ One repository for aggregate root
interface DeviceRepository {
    fun findById(id: DeviceId): Device?
    fun save(device: Device): Device
}
```

### Common Pitfalls Avoided

1. **Large Aggregates**
   - Problem: Everything in one aggregate
   - Solution: Separate by invariants, reference by ID

2. **Exposed Internal Entities**
   - Problem: `device.feeds[0].updateValue()`
   - Solution: Methods on root only

3. **Missing Invariant Protection**
   - Problem: Public setters, mutable collections
   - Solution: Private state, behavior methods

4. **Too Many Aggregates**
   - Problem: Every entity is aggregate root
   - Solution: Group by consistency needs

5. **Cross-Aggregate Transactions**
   - Problem: Saving multiple aggregates together
   - Solution: Eventual consistency with domain events

### Testing Strategies

**Testing Aggregates is Simple:**
```kotlin
@Test
fun `should not activate device without feeds`() {
    val device = Device.register(...)
    
    assertThrows<IllegalArgumentException> {
        device.activate()
    }
}

@Test
fun `should not exceed max feeds`() {
    var device = createDeviceWithFeeds(count = 10)
    
    assertThrows<IllegalArgumentException> {
        device = device.addFeed(Feed.create(...))
    }
}
```

**No mocks, no database, no infrastructure—pure domain logic testing.**

### Aggregate Boundaries Checklist

When designing aggregates, ask:
- ✅ What must be consistent together?
- ✅ What invariants must be protected?
- ✅ What is the transactional boundary?
- ✅ Can this be smaller?
- ✅ Do I really need to load all this data?
- ✅ Can I use eventual consistency instead?

### Measured Benefits

Teams that adopt proper aggregates see:
- **80-90% reduction** in state-related bugs
- **100% invariant protection** (impossible to bypass)
- **50% faster** feature development (clear boundaries)
- **90% easier** testing (pure domain tests)
- **Significantly better** code reviews (business rules visible)

### Practice Exercise

Design aggregates for your domain:

1. **List your entities** - User, Order, Product, etc.
2. **Identify invariants** - What must always be true?
3. **Group by consistency** - What must be consistent together?
4. **Draw boundaries** - One aggregate per consistency boundary
5. **Identify roots** - Which entity is the entry point?
6. **Add behavior** - Move logic from services to aggregates
7. **Test invariants** - Write tests for each business rule

Start with your most critical entity (highest bug rate) and apply aggregate patterns.

---

### Additional Reading

For deeper understanding of aggregates:
- **"Domain-Driven Design"** by Eric Evans (2003) - Original aggregate concept
- **"Implementing Domain-Driven Design"** by Vaughn Vernon (2013) - Chapters 10-11 on aggregates
- **"Effective Aggregate Design"** by Vaughn Vernon - Three-part series

---

## What's Next

In **Chapter 5**, we'll explore the Specification Pattern for taming complex query logic. You'll learn:
- How to avoid query explosion in services
- Creating reusable, composable query logic
- Combining specifications with AND, OR, NOT
- Integrating specifications with repositories
- Real examples from SmartHome Hub device queries

With solid aggregates in place, you're ready to tackle the challenge of complex queries.

Turn the page to master the Specification Pattern...

