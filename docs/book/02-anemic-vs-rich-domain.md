# Chapter 2
# Anemic vs Rich Domain Models
## The Most Common DDD Mistake

> *"Make the implicit explicit."*  
> — Eric Evans, Domain-Driven Design

---

## In This Chapter

The anemic domain model is perhaps the most common anti-pattern in object-oriented programming, yet it's rarely recognized as a problem. In this chapter, you'll learn to identify anemic models in your codebase and transform them into rich, behavior-driven domain models that truly capture business logic.

**What You'll Learn:**
- What anemic domain models are and why they're problematic
- How to identify anemic models in existing code
- The true cost of anemic models in maintenance and bugs
- Step-by-step refactoring from anemic to rich models
- How to test rich domain models effectively
- Migration strategies for legacy codebases

---

## Table of Contents

1. The Problem: The Anemic Domain Model Anti-Pattern
2. What is an Anemic Domain Model?
3. Real-World Disaster: Device Management in SmartHome Hub
4. The Cost of Anemic Models
5. Step-by-Step Refactoring Guide
6. Rich Domain Model Benefits
7. Common Pitfalls and How to Avoid Them
8. Testing Rich vs Anemic Models
9. Migration Strategy
10. Chapter Summary

---

## 1. The Problem: The Anemic Domain Model Anti-Pattern


### The Scenario: Code Review Day

You're reviewing a pull request for a new "Device Activation" feature. The code looks clean, follows SOLID principles, has 95% test coverage, and uses the latest Kotlin features. Your colleague is proud of their work.

But something feels wrong...

```kotlin
// Device.kt - The "Domain" entity
data class Device(
    val id: String,
    val name: String,
    val serialNumber: String,
    val type: String,
    val status: String,
    val health: String,
    val premisesId: String,
    val createdAt: Long,
    val updatedAt: Long,
    val lastSeenAt: Long?
)

// DeviceService.kt - Where ALL the logic lives
@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val eventPublisher: EventPublisher,
    private val notificationService: NotificationService,
    private val auditService: AuditService
) {
    fun activateDevice(deviceId: String, userId: String): Device {
        val device = deviceRepository.findById(deviceId)
            ?: throw DeviceNotFoundException(deviceId)
        
        // Validation in service
        if (device.status == "DELETED") {
            throw BusinessException("Cannot activate deleted device")
        }
        
        if (device.status == "ACTIVE") {
            throw BusinessException("Device is already active")
        }
        
        // Business logic in service
        val activatedDevice = device.copy(
            status = "ACTIVE",
            updatedAt = System.currentTimeMillis()
        )
        
        deviceRepository.save(activatedDevice)
        
        // Publish event
        eventPublisher.publish(
            DeviceActivatedEvent(device.id, device.premisesId)
        )
        
        // Send notification
        notificationService.sendDeviceActivated(device.id)
        
        // Audit log
        auditService.log("DEVICE_ACTIVATED", device.id, userId)
        
        return activatedDevice
    }
    
    fun deactivateDevice(deviceId: String, userId: String): Device {
        val device = deviceRepository.findById(deviceId)
            ?: throw DeviceNotFoundException(deviceId)
        
        // Same validation, duplicated!
        if (device.status == "DELETED") {
            throw BusinessException("Cannot deactivate deleted device")
        }
        
        if (device.status == "INACTIVE") {
            throw BusinessException("Device is already inactive")
        }
        
        // More business logic
        val deactivatedDevice = device.copy(
            status = "INACTIVE",
            updatedAt = System.currentTimeMillis()
        )
        
        val saved = deviceRepository.save(deactivatedDevice)
        
        // Publish event
        eventPublisher.publish(
            DeviceDeactivatedEvent(saved.id, saved.premisesId)
        )
        
        // Send notification
        notificationService.sendDeviceDeactivated(saved.id)
        
        // Log audit
        auditService.log("DEVICE_DEACTIVATED", saved.id, userId)
        
        return saved
    }
    
    fun markDeviceOnline(deviceId: String): Device {
        val device = deviceRepository.findById(deviceId)
            ?: throw DeviceNotFoundException(deviceId)
        
        // Yet more business logic
        if (device.status != "ACTIVE") {
            throw BusinessException("Only active devices can be online")
        }
        
        val onlineDevice = device.copy(
            health = "ONLINE",
            lastSeenAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        val saved = deviceRepository.save(onlineDevice)
        
        // Publish event
        eventPublisher.publish(
            DeviceHealthChangedEvent(saved.id, "ONLINE")
        )
        
        return saved
    }
    
    fun markDeviceOffline(deviceId: String): Device {
        val device = deviceRepository.findById(deviceId)
            ?: throw DeviceNotFoundException(deviceId)
        
        val offlineDevice = device.copy(
            health = "OFFLINE",
            lastSeenAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        val saved = deviceRepository.save(offlineDevice)
        
        // Publish event
        eventPublisher.publish(
            DeviceHealthChangedEvent(saved.id, "OFFLINE")
        )
        
        return saved
    }
    fun deleteDevice(deviceId: String, userId: String): Device {
        val device = deviceRepository.findById(deviceId)
            ?: throw DeviceNotFoundException(deviceId)
        
        if (device.status == "DELETED") {
            throw BusinessException("Device is already deleted")
        }
        
        val deletedDevice = device.copy(
            status = "DELETED",
            updatedAt = System.currentTimeMillis()
        )
        
        deviceRepository.save(deletedDevice)
        
        eventPublisher.publish(DeviceDeletedEvent(device.id, device.premisesId))
        
        auditService.log("DEVICE_DELETED", device.id, userId)
        
        return deletedDevice
    }
}
```

### You Ask: "Where's the Domain Logic?"

**Developer:** "What do you mean? The Device class is right there!"

**You:** "But Device is just a data class. Where are the business rules?"

**Developer:** "They're in DeviceService, where they belong!"

**You:** "That's the problem. This is an **Anemic Domain Model**."

### Martin Fowler's Warning

> "The basic symptom of an Anemic Domain Model is that at first blush it looks like the real thing. There are objects, many named after the nouns in the domain space, and these objects are connected with the rich relationships and structure that true domain models have. **The catch comes when you look at the behavior, and you realize that there is hardly any behavior on these objects**, making them little more than bags of getters and setters."
> 
> — Martin Fowler

---

## 2. What is an Anemic Domain Model?

### Definition

**Anemic Domain Model:** A domain model where the domain objects contain little or no business logic. Instead, business logic is implemented in service classes that operate on these domain objects.

### The Tell-Tale Signs

#### Sign 1: Data Classes Everywhere

```kotlin
// ❌ Anemic - Just data
data class Device(
    val id: String,
    val status: String,
    val health: String
)

data class Order(
    val id: String,
    val status: String,
    val total: Double
)

data class User(
    val id: String,
    val email: String,
    val verified: Boolean
)
```

#### Sign 2: Fat Services with Business Logic

```kotlin
// ❌ Service contains all business rules
@Service
class DeviceService {
    fun activate(device: Device): Device {
        if (device.status == "DELETED") throw Exception()
        return device.copy(status = "ACTIVE")
    }
    
    fun validate(device: Device): Boolean {
        return device.status in listOf("ACTIVE", "INACTIVE")
    }
    
    fun canGoOnline(device: Device): Boolean {
        return device.status == "ACTIVE"
    }
}
```

#### Sign 3: Primitive Obsession

```kotlin
// ❌ Everything is primitive types
data class Device(
    val id: String,              // Could be anything!
    val status: String,          // "ACTIVE"? "active"? "Active"?
    val temperature: Double,     // Celsius? Fahrenheit? Kelvin?
    val createdAt: Long          // Milliseconds? Seconds? Who knows!
)
```

#### Sign 4: Scattered Validation

```kotlin
// Validation in controller
if (device.status.isBlank()) throw Exception()

// Validation in service
if (device.status !in validStatuses) throw Exception()

// Validation in utility class
DeviceValidator.validate(device)

// Validation in persistence layer
if (device.status == "INVALID") return null
```

#### Sign 5: Lots of Copy Methods

```kotlin
// Everywhere you see:
device.copy(status = "ACTIVE")
device.copy(health = "ONLINE", updatedAt = now())
device.copy(status = "INACTIVE", health = "OFFLINE")
```

### Visual Comparison

```
ANEMIC DOMAIN MODEL (❌ Current State)
┌─────────────────────────────────────────────────────┐
│                SERVICE LAYER                        │
│  ┌──────────────────────────────────────────────┐  │
│  │  DeviceService (300 lines)                   │  │
│  │                                              │  │
│  │  ALL THE BUSINESS LOGIC:                     │  │
│  │  - activateDevice()                          │  │
│  │  - deactivateDevice()                        │  │
│  │  - markOnline()                              │  │
│  │  - markOffline()                             │  │
│  │  - deleteDevice()                            │  │
│  │  - validateStatus()                          │  │
│  │  - canGoOnline()                             │  │
│  │  - sendNotifications()                       │  │
│  │  - publishEvents()                           │  │
│  │  - auditActions()                            │  │
│  └──────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
                      ↓ operates on
┌─────────────────────────────────────────────────────┐
│                DOMAIN LAYER                         │
│  ┌──────────────────────────────────────────────┐  │
│  │  Device (20 lines)                           │  │
│  │                                              │  │
│  │  JUST DATA:                                  │  │
│  │  - id: String                                │  │
│  │  - status: String                            │  │
│  │  - health: String                            │  │
│  │  - premisesId: String                        │  │
│  │  - createdAt: Long                           │  │
│  │                                              │  │
│  │  (NO METHODS - PURE DATA BAG)                │  │
│  └──────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘


RICH DOMAIN MODEL (✅ Target State)
┌─────────────────────────────────────────────────────┐
│                SERVICE LAYER                        │
│  ┌──────────────────────────────────────────────┐  │
│  │  DeviceUseCase (80 lines)                    │  │
│  │                                              │  │
│  │  THIN ORCHESTRATION:                         │  │
│  │  - Loads device                              │  │
│  │  - Calls domain methods                      │  │
│  │  - Saves device                              │  │
│  │  - Publishes events                          │  │
│  └──────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
                      ↓ uses
┌─────────────────────────────────────────────────────┐
│                DOMAIN LAYER                         │
│  ┌──────────────────────────────────────────────┐  │
│  │  Device (200 lines)                          │  │
│  │                                              │  │
│  │  DATA + BEHAVIOR:                            │  │
│  │  - deviceId: DeviceId                        │  │
│  │  - status: DeviceStatus (enum)              │  │
│  │  - health: DeviceHealth (enum)              │  │
│  │                                              │  │
│  │  METHODS (Business Logic):                   │  │
│  │  + activate(): Device                        │  │
│  │  + deactivate(): Device                      │  │
│  │  + markOnline(): Device                      │  │
│  │  + markOffline(): Device                     │  │
│  │  + delete(): Device                          │  │
│  │  - validateStateTransition()                 │  │
│  │  - canGoOnline(): Boolean                    │  │
│  └──────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

---

## 3. Real-World Disaster: Device Management in SmartHome Hub

### Current State: The Anemic Mess

Let's look at the actual Device entity in SmartHome Hub:

```kotlin
// device/domain/entity/Device.kt - ANEMIC! ❌
data class Device(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val name: Name,
    val modelNo: ModelNo,
    val serialNo: SerialNo,
    val type: DeviceType,
    var feedIds: FeedIds = FeedIds(emptyList()),    // Mutable! 😱
    val state: DeviceState = DeviceState.ACTIVE,
    var health: DeviceHealth = DeviceHealth.OFFLINE, // Mutable! 😱
    val os: DeviceOS? = null,
    val createdBy: ActorId,
    val createdAt: Instant = Instant.now(),
    val version: Long? = null
) : DomainAggregate<DeviceEvent>() {
    
    // Only TWO methods!
    fun updateFeedIds(feedIds: FeedIds): Device {
        this.feedIds = feedIds  // Direct mutation, no validation!
        return this
    }

    fun updateHealth(health: DeviceHealth): Device {
        this.health = health    // No business rules!
        return this
    }
    
    companion object {
        fun create(
            deviceId: DeviceId,
            premisesId: PremisesId,
            name: Name,
            modelNo: ModelNo,
            serialNo: SerialNo,
            type: DeviceType,
            createdBy: ActorId,
            zoneId: ZoneId
        ): Device {
            val device = Device(
                deviceId, premisesId, name, modelNo, 
                serialNo, type, FeedIds(emptyList()),
                DeviceState.ACTIVE, DeviceHealth.OFFLINE,
                null, createdBy, Instant.now(), null
            )
            device.addDomainEvent(DeviceCreatedEvent(deviceId, modelNo, zoneId))
            return device
        }
    }
}
```

### The Problems

#### Problem 1: No State Transition Logic

```kotlin
// Anyone can do this:
val device = deviceRepository.findById(deviceId)
device.updateHealth(DeviceHealth.ONLINE)  // No checks!

// What if device is INACTIVE? Should inactive devices be online?
// No validation! ❌
```

#### Problem 2: No Business Rules

```kotlin
// device/application/DeviceUseCase.kt - ALL logic here!
@Service
class DeviceUseCase(
    private val deviceRepository: DeviceRepository
) {
    fun updateDeviceHealth(
        health: DeviceHealth,
        deviceData: DeviceData
    ): Device {
        val device = getDevice(deviceData.deviceId, deviceData.premisesId)
        val updated = device.updateHealth(health)  // No validation!
        
        val saved = deviceRepository.save(updated)
        logger.info("Successfully updated health")
        
        return saved
    }
}
```

**Question:** What if we need to add a rule: "Inactive devices can't go online"?

**Current Answer:** Add `if` statement in `DeviceUseCase`. But then we need to add it in every place that changes health! Duplication! ❌

#### Problem 3: Mutable State = Unsafe

```kotlin
// Direct mutation from anywhere!
val device = Device(...)
device.feedIds = FeedIds(emptyList())  // Bypassed business logic!
device.health = DeviceHealth.ONLINE    // No validation!
```

#### Problem 4: Can't Test Business Logic

```kotlin
// Want to test: "Inactive device can't go online"
// Current: Must test through DeviceUseCase with mocked repository

@Test
fun `inactive device cannot go online`() {
    val repository = mockk<DeviceRepository>()
    val useCase = DeviceUseCase(repository)
    
    val device = Device(..., state = DeviceState.INACTIVE)
    every { repository.findById(any()) } returns device
    every { repository.save(any()) } returns device
    
    // Test requires infrastructure mocks! ❌
    assertThrows<BusinessException> {
        useCase.updateDeviceHealth(DeviceHealth.ONLINE, deviceData)
    }
}
```

---

## 4. The Cost of Anemic Models

### Real Bug from Production

**Bug Report #1247:** "Inactive devices showing as online in dashboard"

**Root Cause:**
```kotlin
// Someone added a new endpoint that bypasses DeviceUseCase
@PostMapping("/device/{id}/heartbeat")
fun heartbeat(@PathVariable id: String) {
    val device = deviceRepository.findById(DeviceId(id))
        ?: return
    
    device.updateHealth(DeviceHealth.ONLINE)  // No checks!
    deviceRepository.save(device)
}

// Result: Inactive devices marked online
// Because there's NO BUSINESS LOGIC in Device! ❌
```

**Fix Time:** 2 days (find all places that update health, add validation)  
**Regression Risk:** HIGH (might miss some places)  
**Prevention:** Rich domain model would catch this at compile time!

### The Numbers

Let's calculate the actual cost of anemic models in SmartHome Hub:

#### Code Metrics - Device Management

| Metric | Anemic Model | Rich Model | Improvement |
|--------|--------------|------------|-------------|
| **Device.kt lines** | 50 | 250 | More code, but... |
| **DeviceUseCase.kt lines** | 300 | 100 | 67% reduction |
| **Total lines** | 350 | 350 | Same! |
| **Business logic locations** | 5 files | 1 file | 80% centralization |
| **Duplicate validation** | 4 places | 0 places | 100% elimination |
| **Test infrastructure mocks** | 5 mocks | 0 mocks | Pure domain tests |
| **Bugs in 6 months** | 12 bugs | 2 bugs | 83% reduction |
| **Time to add feature** | 4 hours | 30 minutes | 88% faster |

#### Developer Pain Points

**With Anemic Model:**
```
1. Add feature: "Device needs minimum 1 feed to be active"
   → Need to update: DeviceUseCase, RegisterDeviceUseCase, 
                     DeviceValidator, DeviceController
   → Time: 4 hours
   → Tests to update: 23 unit tests
   → Bugs introduced: 2 (missed DeviceController check)
```

**With Rich Model:**
```
1. Add feature: "Device needs minimum 1 feed to be active"
   → Update Device.activate() method
   → Time: 30 minutes
   → Tests to update: 2 unit tests
   → Bugs introduced: 0 (compiler enforces it everywhere)
```

---

## 5. Step-by-Step Refactoring Guide

Let's transform the anemic Device into a rich domain model, step by step.

### Step 1: Make State Immutable

**Why:** Prevent direct mutation, force changes through methods.

```kotlin
// BEFORE ❌
data class Device(
    var feedIds: FeedIds = FeedIds(emptyList()),  // Mutable!
    var health: DeviceHealth = DeviceHealth.OFFLINE // Mutable!
)

// AFTER ✅
class Device private constructor(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val name: Name,
    val modelNo: ModelNo,
    val serialNo: SerialNo,
    val type: DeviceType,
    private val feedIds: FeedIds = FeedIds(emptyList()),  // Private!
    private val state: DeviceState = DeviceState.ACTIVE,   // Private!
    private val health: DeviceHealth = DeviceHealth.OFFLINE, // Private!
    val os: DeviceOS? = null,
    val createdBy: ActorId,
    val createdAt: Instant = Instant.now(),
    private val lastSeenAt: Instant? = null,  // NEW - track last heartbeat
    val version: Long? = null
) : AggregateRoot<DeviceEvent>() {
    
    // Expose through methods only
    fun getState(): DeviceState = state
    fun getHealth(): DeviceHealth = health
    fun getFeedIds(): List<FeedId> = feedIds.value
    fun getLastSeenAt(): Instant? = lastSeenAt
    
    // Changes only through behavior methods (coming next)
}
```

### Step 2: Add Business Methods with Rules

**Why:** Encapsulate business logic in the entity.

```kotlin
class Device private constructor(...) : AggregateRoot<DeviceEvent>() {
    
    // Business Method 1: Activate
    fun activate(): Device {
        // Business Rule: Can't activate already active device
        if (state == DeviceState.ACTIVE) {
            throw DeviceAlreadyActiveException(deviceId)
        }
        
        // Business Rule: Can't activate deleted device
        if (state == DeviceState.DELETED) {
            throw DeviceCannotBeActivatedException(
                deviceId,
                "Cannot activate deleted device"
            )
        }
        
        // Business Rule: Device needs at least one feed to be active
        if (feedIds.value.isEmpty()) {
            throw DeviceRequiresFeedsException(
                deviceId,
                "Device must have at least one feed to be activated"
            )
        }
        
        return copy(state = DeviceState.ACTIVE).also {
            it.addDomainEvent(DeviceActivatedEvent(deviceId, premisesId))
        }
    }
    
    // Business Method 2: Deactivate
    fun deactivate(): Device {
        if (state == DeviceState.INACTIVE) {
            throw DeviceAlreadyInactiveException(deviceId)
        }
        
        if (state == DeviceState.DELETED) {
            throw DeviceCannotBeDeactivatedException(
                deviceId,
                "Cannot deactivate deleted device"
            )
        }
        
        return copy(
            state = DeviceState.INACTIVE,
            health = DeviceHealth.OFFLINE  // Inactive devices are offline
        ).also {
            it.addDomainEvent(DeviceDeactivatedEvent(deviceId, premisesId))
        }
    }
    
    // Business Method 3: Mark Online
    fun markOnline(): Device {
        // Business Rule: Only active devices can be online
        if (state != DeviceState.ACTIVE) {
            throw InactiveDeviceCannotBeOnlineException(
                deviceId,
                "Device must be active to go online"
            )
        }
        
        // Already online? No-op
        if (health == DeviceHealth.ONLINE) {
            return this
        }
        
        return copy(
            health = DeviceHealth.ONLINE,
            lastSeenAt = Instant.now()
        ).also {
            it.addDomainEvent(
                DeviceHealthChangedEvent(deviceId, DeviceHealth.ONLINE)
            )
        }
    }
    
    // Business Method 4: Mark Offline
    fun markOffline(): Device {
        if (health == DeviceHealth.OFFLINE) {
            return this  // Already offline
        }
        
        return copy(health = DeviceHealth.OFFLINE).also {
            it.addDomainEvent(
                DeviceHealthChangedEvent(deviceId, DeviceHealth.OFFLINE)
            )
        }
    }
    
    // Business Method 5: Update Feeds
    fun updateFeeds(newFeedIds: FeedIds): Device {
        // Business Rule: Active device must have feeds
        if (state == DeviceState.ACTIVE && newFeedIds.value.isEmpty()) {
            throw DeviceRequiresFeedsException(
                deviceId,
                "Active device must have at least one feed"
            )
        }
        
        return copy(feedIds = newFeedIds).also {
            it.addDomainEvent(DeviceFeedsUpdatedEvent(deviceId, newFeedIds))
        }
    }
    
    // Business Method 6: Delete (Soft Delete)
    fun delete(): Device {
        if (state == DeviceState.DELETED) {
            throw DeviceAlreadyDeletedException(deviceId)
        }
        
        return copy(
            state = DeviceState.DELETED,
            health = DeviceHealth.OFFLINE
        ).also {
            it.addDomainEvent(DeviceDeletedEvent(deviceId, premisesId))
        }
    }
    
    // Business Query: Can this device be activated?
    fun canBeActivated(): Boolean {
        return state == DeviceState.INACTIVE && feedIds.value.isNotEmpty()
    }
    
    // Business Query: Is device responsive?
    fun isResponsive(timeout: Duration = Duration.ofMinutes(5)): Boolean {
        return lastSeenAt?.let { 
            Duration.between(it, Instant.now()) < timeout 
        } ?: false
    }
    
    // Helper for immutable updates
    private fun copy(
        deviceId: DeviceId = this.deviceId,
        premisesId: PremisesId = this.premisesId,
        name: Name = this.name,
        modelNo: ModelNo = this.modelNo,
        serialNo: SerialNo = this.serialNo,
        type: DeviceType = this.type,
        feedIds: FeedIds = this.feedIds,
        state: DeviceState = this.state,
        health: DeviceHealth = this.health,
        os: DeviceOS? = this.os,
        createdBy: ActorId = this.createdBy,
        createdAt: Instant = this.createdAt,
        lastSeenAt: Instant? = this.lastSeenAt,
        version: Long? = this.version
    ): Device {
        return Device(
            deviceId, premisesId, name, modelNo, serialNo, type,
            feedIds, state, health, os, createdBy, createdAt,
            lastSeenAt, version
        )
    }
}
```

### Step 3: Add Domain-Specific Exceptions

**Why:** Clear, business-meaningful error messages.

```kotlin
// device/domain/exception/DeviceExceptions.kt
sealed class DeviceDomainException(
    message: String,
    val error: SmartHomeHubError
)

class DeviceAlreadyActiveException(deviceId: DeviceId) :
    DeviceDomainException(
        "Device ${deviceId.value} is already active",
        SmartHomeHubError.DEVICE_ALREADY_ACTIVE
    )
class DeviceAlreadyInactiveException(deviceId: DeviceId) :
    DeviceDomainException(
        "Device ${deviceId.value} is already inactive",
        SmartHomeHubError.DEVICE_ALREADY_INACTIVE
    )
class DeviceAlreadyDeletedException(deviceId: DeviceId) :
    DeviceDomainException(
        "Device ${deviceId.value} is already deleted",
        SmartHomeHubError.DEVICE_ALREADY_DELETED
    )
class DeviceCannotBeActivatedException(deviceId: DeviceId, reason: String) :
    DeviceDomainException(
        "Device ${deviceId.value} cannot be activated: $reason",
        SmartHomeHubError.DEVICE_CANNOT_BE_ACTIVATED
    )
class DeviceCannotBeDeactivatedException(deviceId: DeviceId, reason: String) :
    DeviceDomainException(
        "Device ${deviceId.value} cannot be deactivated: $reason",
        SmartHomeHubError.DEVICE_CANNOT_BE_DEACTIVATED
    )
class InactiveDeviceCannotBeOnlineException(deviceId: DeviceId, reason: String) :
    DeviceDomainException(
        "Inactive device ${deviceId.value} cannot be online: $reason",
        SmartHomeHubError.DEVICE_INACTIVE
    )
class DeviceRequiresFeedsException(deviceId: DeviceId, reason: String) :
    DeviceDomainException(
        "Device ${deviceId.value} requires feeds: $reason",
        SmartHomeHubError.DEVICE_REQUIRES_FEEDS
    )
```

### Step 4: Simplify Application Service

**Why:** Use case becomes thin orchestration.

```kotlin
// BEFORE ❌ - Fat service with business logic (300 lines)
@Service
class DeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val eventPublisher: EventPublisher,
    private val notificationService: NotificationService,
    private val auditService: AuditService
) {
    fun activateDevice(deviceId: DeviceId, actorData: ActorData): Device {
        return deviceRepository.findById(deviceId)
        val device = deviceRepository.findById(deviceId)
        
        // Validation
        if (device.status == "DELETED") {
            throw BusinessException("Cannot activate deleted")
        }
        if (device.status == "ACTIVE") {
            throw BusinessException("Already active")
        }
        
        // State change
        val activated = device.copy(status = "ACTIVE", updatedAt = now())
        val saved = deviceRepository.save(activated)
        
        // Publish event
        eventPublisher.publish(DeviceActivatedEvent(saved.id))
        
        // Send notification
        notificationService.send("Device activated", saved.id)
        
        // Log audit
        auditService.log("ACTIVATED", saved.id, actorData.actorId)
        
        return saved
    
    // ... 10 more methods with similar structure
}

// AFTER ✅ - Thin orchestration (100 lines)
@Service
class DeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val eventPublisher: EventPublisher<DeviceEvent>
) {
    fun activateDevice(
        deviceId: DeviceId,
        actorData: ActorData
    ): Device {
        // Load device
        val device = deviceRepository.findByPremisesIdAndDeviceId(
            actorData.premisesId,
            deviceId
        ) ?: throw DeviceNotFoundException(deviceId)
        
        // Domain handles ALL business logic
        val activated = device.activate()
        
        // Save and publish
        val saved = deviceRepository.save(activated)
        eventPublisher.publish(activated.domainEvents)
        
        return saved
    }
    
    fun markDeviceOnline(
        deviceId: DeviceId,
        deviceData: DeviceData
    ): Device {
        val device = deviceRepository.findByPremisesIdAndDeviceId(
            deviceData.premisesId,
            deviceId
        ) ?: throw DeviceNotFoundException(deviceId)
        
        // Domain validates and changes state
        val online = device.markOnline()
        
        val saved = deviceRepository.save(online)
        eventPublisher.publish(online.domainEvents)
        
        return saved
    }
    
    fun updateDeviceFeeds(
        deviceId: DeviceId,
        feedIds: FeedIds,
        actorData: ActorData
    ): Device {
        val device = deviceRepository.findByPremisesIdAndDeviceId(
            actorData.premisesId,
            deviceId
        ) ?: throw DeviceNotFoundException(deviceId)
        
        // Domain enforces rules
        val updated = device.updateFeeds(feedIds)
        
        val saved = deviceRepository.save(updated)
        eventPublisher.publish(updated.domainEvents)
        
        return saved
    }
}
```

**Reduction:** 300 lines → 100 lines (67% less code!)  
**Complexity:** 10 → 3 (complexity per method)  
**Dependencies:** 4 → 2 (50% reduction)

### Step 5: Update Tests

**Why:** Domain logic can now be tested in isolation!

```kotlin
// BEFORE ❌ - Need infrastructure mocks
@Test
fun `should activate device`() {
    val repository = mockk<DeviceRepository>()
    val eventPublisher = mockk<EventPublisher>()
    val notificationService = mockk<NotificationService>()
    val auditService = mockk<AuditService>()
    
    val useCase = DeviceUseCase(
        repository, 
        eventPublisher,
        notificationService,
        auditService
    )
    
    val device = Device(..., status = "INACTIVE")
    every { repository.findById(any()) } returns device
    every { repository.save(any()) } returns device.copy(status = "ACTIVE")
    every { eventPublisher.publish(any()) } just Runs
    every { notificationService.send(any(), any()) } just Runs
    every { auditService.log(any(), any(), any()) } just Runs
    
    val result = useCase.activateDevice(deviceId, actorData)
    
    assertEquals("ACTIVE", result?.status)
}

// AFTER ✅ - Pure domain test, no mocks!
@Test
fun `should activate inactive device`() {
    val device = Device.register(
        deviceId = DeviceId("device-123"),
        premisesId = PremisesId("premises-123"),
        name = Name("Living Room Sensor"),
        serialNumber = SerialNumber("ABC12345"),
        type = DeviceType.SENSOR,
        createdBy = ActorId("actor-123"),
        zoneId = ZoneId("zone-123")
    ).deactivate()  // Start with inactive
    
    val activated = device.activate()
    
    assertEquals(DeviceState.ACTIVE, activated.getState())
    assertTrue(activated.domainEvents.any { it is DeviceActivatedEvent })
}

@Test
fun `should not activate already active device`() {
    val device = Device.register(...)  // Created in ACTIVE state
    
    assertThrows<DeviceAlreadyActiveException> {
        device.activate()
    }
}

@Test
fun `should not activate deleted device`() {
    val device = Device.register(...).delete()
    
    assertThrows<DeviceCannotBeActivatedException> {
        device.activate()
    }
}

@Test
fun `should not mark inactive device as online`() {
    val device = Device.register(...).deactivate()
    
    assertThrows<InactiveDeviceCannotBeOnlineException> {
        device.markOnline()
    }
}

@Test
fun `should not allow active device without feeds`() {
    val device = Device.register(...)
    
    assertThrows<DeviceRequiresFeedsException> {
        device.updateFeeds(FeedIds(emptyList()))
    }
}
```

---

## 6. Rich Domain Model Benefits

### Benefit 1: Single Source of Truth

**Before (Anemic):**
```kotlin
// Business rule scattered in 4 places:

// DeviceController.kt
if (device.status == "INACTIVE") throw Exception()

// DeviceUseCase.kt
if (device.status != "ACTIVE") throw Exception()

// DeviceValidator.kt
if (listOf("ACTIVE", "INACTIVE").contains(device.status)) ...

// DeviceRepository.kt (query)
@Query("status != 'DELETED'")
```

**After (Rich):**
```kotlin
// Business rule in ONE place:

// Device.kt
fun markOnline(): Device {
    if (state != DeviceState.ACTIVE) {
        throw InactiveDeviceCannotBeOnlineException(deviceId)
    }
    // ...
}
```

### Benefit 2: Compile-Time Safety

**Before (Anemic):**
```kotlin
// Runtime error! 💥
val device = Device(..., status = "ACITVE")  // Typo!
device.status = "ONLINE"  // Wrong value!
device.health = "ACTVE"   // Another typo!
```

**After (Rich):**
```kotlin
// Compilation error! ✅
val device = Device(..., state = DeviceState.ACITVE)  // ← Won't compile!
device.activate()  // ← Type-safe methods only
```

### Benefit 3: Self-Documenting Code

**Before (Anemic):**
```kotlin
// What does this do? Need to read DeviceUseCase
device.copy(status = "ACTIVE", health = "OFFLINE", updatedAt = now())
```

**After (Rich):**
```kotlin
// Crystal clear intent!
device.activate()
```

### Benefit 4: Easy to Add Features

**Feature Request:** "Devices with no heartbeat for 5 minutes should be marked offline"

**Before (Anemic):** 
- Modify DeviceUseCase
- Modify DeviceScheduler
- Modify DeviceController
- Update 15 tests
- Time: 3 hours

**After (Rich):**
```kotlin
// Add method to Device.kt (5 minutes!)
fun shouldBeMarkedOffline(timeout: Duration = Duration.ofMinutes(5)): Boolean {
    return lastSeenAt?.let {
        Duration.between(it, Instant.now()) > timeout
    } ?: true
}

// Use in scheduler
class DeviceHealthScheduler {
    @Scheduled(fixedRate = 60000)
    fun checkDeviceHealth() {
        devices.filter { it.shouldBeMarkedOffline() }
               .forEach { it.markOffline() }
    }
}
```

### Benefit 5: Metrics Comparison

| Metric | Anemic | Rich | Change |
|--------|--------|------|--------|
| Lines of code (total) | 350 | 350 | 0% |
| Business logic locations | 5 files | 1 file | -80% |
| Code duplication | 4 places | 0 places | -100% |
| Test infrastructure mocks | 5 mocks | 0 mocks | -100% |
| Bugs in 6 months | 12 | 2 | -83% |
| Time to add feature | 3 hours | 30 min | -83% |
| Time to onboard new dev | 2 weeks | 3 days | -79% |

---

## 7. Common Pitfalls and How to Avoid Them

### Pitfall 1: God Object

**Problem:** Putting EVERYTHING in one entity.

```kotlin
// ❌ WRONG - Device does everything!
class Device {
    fun activate() { ... }
    fun deactivate() { ... }
    fun addFeed(feed: Feed) { ... }      // ← Should be separate!
    fun removeFeed(feedId: FeedId) { ... }
    fun createAutomation(rule: Rule) { ... }  // ← Wrong aggregate!
    fun sendCommand(cmd: Command) { ... }     // ← Should be service!
    fun generateReport(): Report { ... }      // ← Not domain logic!
    fun exportToJson(): String { ... }        // ← Infrastructure concern!
}
```

**Solution:** Respect aggregate boundaries.

```kotlin
// ✅ GOOD - Clear responsibilities
class Device {
    fun activate() { ... }
    fun deactivate() { ... }
    fun markOnline() { ... }
    fun updateFeeds(feedIds: List<FeedId>) { ... }  // References only
}

class Feed {  // Separate aggregate
    fun updateValue(newValue: Int) { ... }
}

class Automation {  // Separate aggregate
    fun execute(context: AutomationContext) { ... }
}
```

### Pitfall 2: Lazy Loading in Domain

**Problem:** Loading related data lazily.

```kotlin
// ❌ WRONG - Infrastructure in domain!
class Device(
    val deviceId: DeviceId,
    private val feedRepository: FeedRepository  // ← BAD!
) {
    val feeds: List<Feed> by lazy {
        feedRepository.findByDeviceId(deviceId)  // ← Database call!
    }
}
```

**Solution:** Use references, load in use case.

```kotlin
// ✅ GOOD - Store references
class Device(
    val deviceId: DeviceId,
    private val feedIds: List<FeedId>  // References only
) {
    fun getFeedIds(): List<FeedId> = feedIds
}

// Load in use case if needed
class DeviceUseCase {
    fun getDeviceWithFeeds(deviceId: DeviceId): DeviceWithFeeds {
        val device = deviceRepository.findById(deviceId)
        val feeds = feedRepository.findAllById(device.getFeedIds())
        return DeviceWithFeeds(device, feeds)  // DTO
    }
}
```

### Pitfall 3: Exposing Mutable Collections

**Problem:** Returning mutable collections.

```kotlin
// ❌ WRONG - Can be modified from outside!
class Device(
    private val feedIds: MutableList<FeedId>
) {
    fun getFeedIds(): MutableList<FeedId> = feedIds  // ← Dangerous!
}

// Client code breaks invariants!
val device = deviceRepository.findById(deviceId)
device.getFeedIds().clear()  // ← Bypassed business logic!
```

**Solution:** Return immutable copies.

```kotlin
// ✅ GOOD - Return immutable
class Device(
    private val feedIds: List<FeedId>
) {
    fun getFeedIds(): List<FeedId> = feedIds.toList()  // Defensive copy
}
```

### Pitfall 4: Too Many Constructor Parameters

**Problem:** Constructor with 15+ parameters.

```kotlin
// ❌ WRONG - Unmanageable constructor
class Device(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val name: Name,
    val modelNo: ModelNo,
    val serialNo: SerialNo,
    val type: DeviceType,
    val feedIds: FeedIds,
    val state: DeviceState,
    val health: DeviceHealth,
    val os: DeviceOS?,
    val createdBy: ActorId,
    val createdAt: Instant,
    val lastSeenAt: Instant?,
    val version: Long?,
    val metadata: Map<String, String>,
    val tags: List<String>
    // ... 20 more parameters
)
```

**Solution:** Use builder pattern or group related parameters.

```kotlin
// ✅ GOOD - Grouped value objects
data class DeviceMetadata(
    val os: DeviceOS?,
    val tags: List<String>,
    val customAttributes: Map<String, String>
)

data class DeviceTimestamps(
    val createdAt: Instant,
    val updatedAt: Instant,
    val lastSeenAt: Instant?
)

class Device(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val name: Name,
    val serialNumber: SerialNumber,
    val type: DeviceType,
    private val state: DeviceState,
    private val health: DeviceHealth,
    val metadata: DeviceMetadata,   // Grouped!
    val timestamps: DeviceTimestamps // Grouped!
)
```

---

## 8. Testing Rich vs Anemic Models

### Test Comparison

#### Anemic Model Test (Infrastructure Heavy)

```kotlin
class DeviceServiceTest {
    private lateinit var deviceRepository: DeviceRepository
    private lateinit var eventPublisher: EventPublisher
    private lateinit var notificationService: NotificationService
    private lateinit var auditService: AuditService
    private lateinit var deviceService: DeviceService
    
    @BeforeEach
    fun setup() {
        deviceRepository = mockk()
        eventPublisher = mockk()
        notificationService = mockk()
        auditService = mockk()
        
        deviceService = DeviceService(
            deviceRepository,
            eventPublisher,
            notificationService,
            auditService
        )
    }
    
    @Test
    fun `should activate device`() {
        // Arrange
        val device = Device(..., status = "INACTIVE")
        every { deviceRepository.findById(any()) } returns device
        every { deviceRepository.save(any()) } returns device
        every { eventPublisher.publish(any()) } just Runs
        every { notificationService.send(any(), any()) } just Runs
        every { auditService.log(any(), any(), any()) } just Runs
        
        // Act
        val result = deviceService.activateDevice(deviceId, userId)
        
        // Assert
        assertEquals("ACTIVE", result?.status)
        verify { deviceRepository.save(any()) }
        verify { eventPublisher.publish(any()) }
        verify { notificationService.send(any(), any()) }
        verify { auditService.log(any(), any(), any()) }
    }
}
```

**Problems:**
- 4 mocks needed
- Testing infrastructure, not business logic
- Brittle (breaks when changing infrastructure)
- Hard to understand what business rule is being tested

#### Rich Model Test (Pure Domain)

```kotlin
class DeviceTest {
    
    private lateinit var device: Device
    
    @BeforeEach
    fun setup() {
        device = Device.register(
            deviceId = DeviceId("device-123"),
            premisesId = PremisesId("premises-123"),
            name = Name("Test Device"),
            serialNumber = SerialNumber("ABC12345"),
            type = DeviceType.SENSOR,
            createdBy = ActorId("actor-123"),
            zoneId = ZoneId("zone-123")
        )
    }
    
    @Test
    fun `new device should be active and offline`() {
        assertEquals(DeviceState.ACTIVE, device.getState())
        assertEquals(DeviceHealth.OFFLINE, device.getHealth())
    }
    
    @Test
    fun `should activate inactive device`() {
        val inactive = device.deactivate()
        val active = inactive.activate()
        
        assertEquals(DeviceState.ACTIVE, active.getState())
        assertTrue(active.domainEvents.any { it is DeviceActivatedEvent })
    }
    
    @Test
    fun `should not activate already active device`() {
        assertThrows<DeviceAlreadyActiveException> {
            device.activate()
        }
    }
    
    @Test
    fun `should not activate deleted device`() {
        val deleted = device.delete()
        
        assertThrows<DeviceCannotBeActivatedException> {
            deleted.activate()
        }
    }
    
    @Test
    fun `should mark active device as online`() {
        val online = device.markOnline()
        
        assertEquals(DeviceHealth.ONLINE, online.getHealth())
        assertNotNull(online.getLastSeenAt())
        assertTrue(online.domainEvents.any { it is DeviceHealthChangedEvent })
    }
    
    @Test
    fun `should not mark inactive device as online`() {
        val inactive = device.deactivate()
        
        assertThrows<InactiveDeviceCannotBeOnlineException> {
            inactive.markOnline()
        }
    }
    
    @Test
    fun `should update feeds with valid feed ids`() {
        val feedIds = FeedIds(listOf(FeedId("feed-1"), FeedId("feed-2")))
        
        val updated = device.updateFeeds(feedIds)
        
        assertEquals(2, updated.getFeedIds().size)
        assertTrue(updated.domainEvents.any { it is DeviceFeedsUpdatedEvent })
    }
    
    @Test
    fun `should not allow active device without feeds`() {
        assertThrows<DeviceRequiresFeedsException> {
            device.updateFeeds(FeedIds(emptyList()))
        }
    }
    
    @Test
    fun `inactive device can be offline without feeds`() {
        val inactive = device.deactivate()
        
        // Should succeed
        val withoutFeeds = inactive.updateFeeds(FeedIds(emptyList()))
        
        assertEquals(0, withoutFeeds.getFeedIds().size)
    }
    
    @Test
    fun `should detect unresponsive device`() {
        val oldDevice = Device.register(...)
        // Simulate old lastSeenAt
        val staleDevice = oldDevice.markOnline()
        
        Thread.sleep(6000)  // Wait 6 seconds
        
        assertFalse(staleDevice.isResponsive(Duration.ofSeconds(5)))
    }
}
```

**Benefits:**
- 0 mocks needed
- Testing actual business logic
- Fast (no infrastructure)
- Clear what rule is being tested
- Refactoring-resistant

### Test Coverage Comparison

| Aspect | Anemic | Rich |
|--------|--------|------|
| **Test setup time** | 30 lines | 5 lines |
| **Mocks required** | 4+ | 0 |
| **Test execution time** | 150ms | 5ms |
| **Lines per test** | 25 | 8 |
| **Business logic coverage** | Indirect | Direct |
| **Refactoring resistance** | Low | High |

---

## 9. Migration Strategy

### Don't Rewrite Everything!

**Bad Approach:** ❌
```
1. Stop all feature development
2. Rewrite entire codebase
3. Hope nothing breaks
4. Deploy big bang
```

**Good Approach:** ✅
```
1. Pick one aggregate (Device)
2. Refactor it to rich model
3. Update tests
4. Deploy incrementally
5. Repeat for next aggregate
```

### Step-by-Step Migration

#### Week 1: Device Aggregate
```kotlin
// 1. Keep old DeviceService for compatibility
@Service
class DeviceServiceLegacy { ... }

// 2. Create new rich Device model
class Device { ... }

// 3. Create new thin use case
class DeviceUseCase { ... }

// 4. Route new features to new use case
@PostMapping("/v2/devices/{id}/activate")
fun activateV2(@PathVariable id: String) {
    return deviceUseCase.activate(DeviceId(id))
}

// 5. Gradually migrate old endpoints
```

#### Week 2: Feed Aggregate
```kotlin
// Same process for Feed
class Feed { ... }
class FeedUseCase { ... }
```

#### Week 3-4: Update Dependent Code
```kotlin
// Update code that uses Device
// Replace DeviceService with DeviceUseCase
```

#### Week 5: Remove Legacy Code
```kotlin
// Delete DeviceServiceLegacy
// Delete v1 endpoints
```

### Migration Checklist

- [ ] Identify anemic aggregates
- [ ] Prioritize by business value
- [ ] Create rich model for one aggregate
- [ ] Write domain tests
- [ ] Create thin use case
- [ ] Add new v2 endpoints
- [ ] Migrate existing endpoints gradually
- [ ] Monitor for issues
- [ ] Remove legacy code
- [ ] Repeat for next aggregate

---

## 10. Chapter Summary

In this chapter, we've tackled the most common mistake in Domain-Driven Design: the anemic domain model anti-pattern. Understanding and avoiding this pitfall is crucial for building maintainable, business-focused software.

### What We Covered

**The Anemic Domain Model Problem:**
- Domain objects that are just data holders with no behavior
- Business logic scattered across services
- Validation duplicated in multiple places
- No encapsulation of business rules
- "Tell, Don't Ask" principle violated

**The Rich Domain Model Solution:**
- Entities with behavior that encapsulate business rules
- Private constructors and factory methods
- Immutability through copy-on-write
- Clear invariant protection
- Domain events for loose coupling

### Key Insights

1. **Anemic models are procedural programming in OO disguise** - They violate core OOP principles

2. **Services should orchestrate, not contain logic** - Business rules belong in domain objects

3. **Rich models are self-documenting** - `device.activate()` is clearer than `device.copy(status = "ACTIVE")`

4. **Immutability enables safety** - Copy-on-write prevents unexpected mutations

5. **Type safety prevents bugs** - Value objects catch errors at compile time

6. **Testing becomes trivial** - Rich models test without infrastructure

7. **Single source of truth** - Business rules in one place

8. **Migrate incrementally** - Use strangler fig pattern for legacy code

### SmartHome Hub Transformation

**Before (Anemic):**
- 200+ lines of procedural logic in services
- Rules duplicated across 7 files
- 75% test coverage with integration tests
- 3 days average to add new features

**After (Rich):**
- 80 lines of focused domain logic
- Single source of truth
- 95% test coverage with unit tests
- 1 day average to add new features

### Practice Exercise

Transform one of your anemic models:

1. **Identify** - Find a data class with external business logic
2. **Add Behavior** - Move one operation from service to entity
3. **Test** - Write a pure unit test (no mocks)
4. **Measure** - Count lines of code and test simplicity

### Additional Reading

- **"Domain-Driven Design"** by Eric Evans (2003)
- **"Implementing Domain-Driven Design"** by Vaughn Vernon (2013)
- **Martin Fowler's "Anemic Domain Model"** article

---

## What's Next

In **Chapter 3**, we'll dive deep into value objects - the building blocks of rich domain models. You'll learn how to eliminate primitive obsession, implement proper validation, and create bulletproof type-safe domain models.

Turn the page to master value objects...

