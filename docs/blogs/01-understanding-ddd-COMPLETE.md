# Chapter 1: Understanding Domain-Driven Design - Why Your Code Needs Business Logic

**Series:** Building an Enterprise IoT Platform with DDD  
**Part:** 1 of 20 - Foundation & Core Concepts  
**Reading Time:** 20 minutes  
**Level:** Beginner  

---

## 📋 Table of Contents

1. [The Problem: When Good Code Goes Bad](#the-problem)
2. [What is Domain-Driven Design?](#what-is-ddd)
3. [The Three Pillars of DDD](#three-pillars)
4. [Real-World Example: The SmartHome Hub Story](#real-world-example)
5. [Core DDD Building Blocks](#core-concepts)
6. [DDD Layers: Where Everything Lives](#ddd-layers)
7. [When to Use DDD (and When Not To)](#when-to-use)
8. [The Journey Ahead](#journey-ahead)

---

## <a name="the-problem"></a>1. The Problem: When Good Code Goes Bad

### The Scenario: Your First Day at SmartHome Hub

You've just joined a new team working on **SmartHome Hub**, a smart home IoT automation platform. The codebase is 2 years old with 100K+ lines of code. Your first task seems simple:

> **Feature Request:** "Users can't register more than 100 devices per premises. Show an error message when they try to exceed the limit."

You think: *"Easy! Just add a validation check. Should take 30 minutes."*

### The Reality: A 3-Day Nightmare

After diving into the code, you discover the device registration flow touches **7 different files**:

```kotlin
// 1. DeviceController.kt - The entry point
@RestController
@RequestMapping("/api/devices")
class DeviceController(
    private val deviceService: DeviceService
) {
    @PostMapping
    fun registerDevice(@RequestBody request: DeviceRegistrationRequest): DeviceResponse {
        // Validation scattered here
        if (request.name.isBlank()) {
            throw BadRequestException("Device name is required")
        }
        
        if (request.serialNumber.length < 8) {
            throw BadRequestException("Serial number must be at least 8 characters")
        }
        
        // Where do I add the device limit check? Here?
        val device = deviceService.registerDevice(request)
        return DeviceMapper.toResponse(device)
    }
}

// 2. DeviceService.kt - Business logic layer
@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val feedService: FeedService,
    private val authService: AuthService,
    private val zoneService: ZoneService,
    private val eventPublisher: EventPublisher
) {
    fun registerDevice(request: DeviceRegistrationRequest): Device {
        // More validation here?
        if (request.type == "SENSOR" && request.feeds.isEmpty()) {
            throw BusinessException("Sensor devices must have at least one feed")
        }
        
        // Should I check device count here?
        val count = deviceRepository.countByPremisesId(request.premisesId)
        
        // Wait, there's already a check here!
        if (count >= 50) {  // But it says 50, not 100! 😱
            throw BusinessException("Maximum 50 devices allowed per premises")
        }
        
        // Create the device
        val device = Device(
            id = UUID.randomUUID().toString(),
            name = request.name,
            serialNumber = request.serialNumber,
            type = request.type,
            premisesId = request.premisesId,
            status = "PENDING"
        )
        
        deviceRepository.save(device)
        
        // Create feeds
        feedService.createFeedsForDevice(device.id, request.feeds)
        
        // Register in auth system
        authService.registerDeviceCredentials(device.id, device.serialNumber)
        
        // Assign to zone
        zoneService.addDeviceToZone(device.id, request.zoneId)
        
        // Publish events
        eventPublisher.publish(DeviceRegisteredEvent(device.id))
        
        // Update status
        device.status = "ACTIVE"
        return deviceRepository.save(device)
    }
}

// 3. DeviceValidator.kt - Yet another validation layer!
@ComponentInline
class DeviceValidator {
    fun validateDeviceRegistration(request: DeviceRegistrationRequest): ValidationResult {
        val errors = mutableListOf<String>()
        
        // The SAME validations, duplicated!
        if (request.name.isBlank()) {
            errors.add("Device name is required")
        }
        
        if (request.serialNumber.length < 8) {
            errors.add("Serial number must be at least 8 characters")
        }
        
        // Different device limit here! 🤦
        if (request.premisesId.deviceCount >= 75) {
            errors.add("Maximum 75 devices allowed")
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
}

// 4. PremisesService.kt - ANOTHER device count check!
@Service
class PremisesService(
    private val premisesRepository: PremisesRepository,
    private val deviceRepository: DeviceRepository
) {
    fun canAddDevice(premisesId: String): Boolean {
        val count = deviceRepository.countByPremisesId(premisesId)
        return count < 100  // Another limit! Which one is correct?!
    }
}
```

### The Horror Unfolds

As you dig deeper, you discover:

1. **Three Different Limits!**
   - Controller validation: Not checked
   - DeviceService: 50 devices
   - DeviceValidator: 75 devices
   - PremisesService: 100 devices
   - **Which one is the actual business rule?!**

2. **Validation Duplicated Everywhere**
   - Serial number validation in 4 different places
   - Name validation in 3 places
   - Each with slightly different logic!

3. **Business Logic Scattered**
   - Device creation logic spans 5 services
   - No single place to see the complete registration flow
   - Changes in one place don't reflect in others

4. **No Domain Language**
   - Code talks about "requests", "responses", "DTOs"
   - Business concepts like "Smart Device", "Sensor", "Actuator" missing
   - Domain experts can't read or understand the code

5. **Testing Nightmare**
   - Must mock 5 different services to test device registration
   - Business logic tests mixed with infrastructure tests
   - Simple feature takes 47 unit tests to cover all paths

6. **Hidden Dependencies**
   - DeviceService depends on FeedService, AuthService, ZoneService, EventPublisher
   - Change one, potentially break all of them
   - Circular dependencies lurking

### The Cost: Real Numbers

After 3 days of investigation, you discover:

- ✗ **7 files** need to be modified (not just 1)
- ✗ **23 unit tests** need updating
- ✗ **5 integration tests** broken
- ✗ **2 bugs** found in production (duplicate device counts, inconsistent limits)
- ✗ **1 critical incident** - devices registered beyond limit, system overloaded
- ✗ **Lost confidence** - No one understands the full flow anymore

**Total Time:** 3 days instead of 30 minutes  
**Bugs Introduced:** 3 new bugs  
**Tech Debt Added:** Validation copied to yet another place  

### What Went Wrong?

This codebase suffers from:

1. **Anemic Domain Model** - Domain objects are just data bags
2. **Missing Business Layer** - Logic scattered in application services
3. **Primitive Obsession** - Everything is String, Int, Boolean
4. **No Ubiquitous Language** - Code doesn't speak business
5. **Poor Encapsulation** - Anyone can modify anything
6. **Tight Coupling** - Services depend on too many other services

**This is what happens without Domain-Driven Design.**

---

## <a name="what-is-ddd"></a>2. What is Domain-Driven Design?

### The Core Philosophy

> **"Place the project's primary focus on the core domain and domain logic."**  
> — Eric Evans, Domain-Driven Design (2003)

Domain-Driven Design is not a technology or framework. It's a **way of thinking** about software design that puts the **business domain** at the center of everything.

### What DDD is NOT

Before we dive into what DDD is, let's clear up misconceptions:

❌ **DDD is NOT:**
- A technology (not Spring, MongoDB, Kafka)
- An architecture style (not microservices, not hexagonal)
- A design pattern (not factory, not strategy)
- A silver bullet (won't solve all problems)
- Only for large systems (can benefit small ones too)
- Only for enterprise (useful for startups)

### What DDD IS

✅ **DDD IS:**
- A **design philosophy** focused on understanding and modeling the business domain
- A **collaboration approach** between developers and domain experts
- A **set of patterns** (tactical and strategic) for organizing complex business logic
- A **language** shared between business and technical teams
- A **way to manage complexity** by clear boundaries and focus

### The DDD Promise

If you apply DDD correctly, you get:

✅ **Maintainable Code** - Business logic in one place  
✅ **Fewer Bugs** - Invariants protected by domain  
✅ **Faster Features** - Clear where to add new logic  
✅ **Better Communication** - Code speaks business language  
✅ **Easier Testing** - Domain logic tests without infrastructure  
✅ **Scalable Architecture** - Clear boundaries enable independent teams  

### The DDD Equation

```
DDD = Strategic Design + Tactical Design + Ubiquitous Language

Where:
Strategic Design = How to organize large systems (Bounded Contexts)
Tactical Design = How to implement the domain (Entities, Value Objects, etc.)
Ubiquitous Language = Common vocabulary between everyone
```

---

## <a name="three-pillars"></a>3. The Three Pillars of DDD

### Pillar 1: Ubiquitous Language

**The Problem:**

```
// Developers say:
"The user DTO is passed to the service layer which validates 
the request object and persists the entity to the repository."

// Business people say:
"When a homeowner registers their smart home premises, 
we verify their identity and create their automation hub."

❌ They're NOT speaking the same language!
```

**The Solution: Ubiquitous Language**

A **common vocabulary** used by everyone - developers, business people, documentation, and code.

```
// Ubiquitous Language for SmartHome Hub:
- User → "Homeowner" or "Tenant"
- Service → "AutomationHub"
- Request/Response → "Registration" or "Command"
- DTO/Entity → "Premises", "Device", "Automation"
```

**In Code:**

```kotlin
// ❌ BAD - Technical language
class UserService {
    fun processRequest(dto: RequestDTO): ResponseDTO {
        val entity = repository.findByUserId(dto.userId)
        // ...
    }
}

// ✅ GOOD - Business language
class PremisesRegistrationService {
    fun registerPremises(command: RegisterPremisesCommand): Premises {
        val homeowner = homeownerRepository.findByHomeownerId(command.homeownerId)
        // ...
    }
}
```

### Pillar 2: Strategic Design (Bounded Contexts)

**The Problem:**

In a large system, the word "User" means different things in different parts:

- In **Authentication**: User = Login credentials, password, 2FA
- In **Billing**: User = Payment methods, subscription, invoices  
- In **Device Management**: User = Device owner, permissions
- In **Analytics**: User = Usage patterns, behavior data

Trying to create **one User class** for all these contexts leads to:
- God objects with 50+ properties
- Confusion (which properties matter where?)
- Coupling (change billing, break authentication)

**The Solution: Bounded Contexts**

Separate **independent models** for separate **business contexts**.

```
┌─────────────────────┐     ┌─────────────────────┐     ┌─────────────────────┐
│  AUTHENTICATION     │     │  DEVICE MANAGEMENT  │     │  AUTOMATION         │
│     Context         │     │      Context        │     │    Context          │
│                     │     │                     │     │                     │
│  - AuthUser         │     │  - Device           │     │  - Rule             │
│  - Credentials      │     │  - Feed             │     │  - Trigger          │
│  - Token            │     │  - Health           │     │  - Action           │
│                     │     │                     │     │                     │
└─────────────────────┘     └─────────────────────┘     └─────────────────────┘
         │                            │                            │
         └────────────────────────────┴────────────────────────────┘
                            Integration Events
```

Each context has its own:
- Model (entities, value objects)
- Language (terms specific to that context)
- Team (can work independently)
- Database (if needed)

### Pillar 3: Tactical Design (Domain Building Blocks)

**The Problem:**

Where do you put business logic?

```kotlin
// ❌ In controllers?
@PostMapping("/devices")
fun register(@RequestBody request: DeviceRequest): DeviceResponse {
    if (request.serialNumber.length < 8) throw Exception()
    // ...
}

// ❌ In services?
@Service
class DeviceService {
    fun register(request: DeviceRequest): Device {
        if (request.serialNumber.length < 8) throw Exception()
        // ...
    }
}

// ❌ Scattered everywhere?
```

**The Solution: Tactical Patterns**

DDD provides **building blocks** for organizing business logic:

1. **Entities** - Objects with identity (User, Device, Premises)
2. **Value Objects** - Objects defined by values (Email, SerialNumber, Temperature)
3. **Aggregates** - Clusters with consistency boundaries
4. **Domain Services** - Operations spanning multiple entities
5. **Repositories** - Collections of aggregates
6. **Domain Events** - Things that happened
7. **Specifications** - Reusable query logic
8. **Policies** - Business rules

**Example:**

```kotlin
// Business logic in DOMAIN layer
class Device private constructor(
    val deviceId: DeviceId,
    val serialNumber: SerialNumber,  // Value object with validation
    private var status: DeviceStatus
) : AggregateRoot<DeviceEvent>() {
    
    // Business method
    fun activate() {
        if (status == DeviceStatus.DELETED) {
            throw DeviceCannotBeActivatedException("Device is deleted")
        }
        
        status = DeviceStatus.ACTIVE
        addDomainEvent(DeviceActivatedEvent(deviceId))
    }
    
    // Factory method
    companion object {
        fun register(
            deviceId: DeviceId,
            serialNumber: SerialNumber,
            premisesId: PremisesId
        ): Device {
            val device = Device(deviceId, serialNumber, DeviceStatus.PENDING)
            device.addDomainEvent(DeviceRegisteredEvent(deviceId, premisesId))
            return device
        }
    }
}

// Value object with validation
@JvmInline
value class SerialNumber(val value: String) {
    init {
        require(value.length >= 8) {
            "Serial number must be at least 8 characters"
        }
        require(value.matches(Regex("^[A-Z0-9]+$"))) {
            "Serial number must be alphanumeric"
        }
    }
}

// Application service is THIN
@Service
class RegisterDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val eventPublisher: EventPublisher
) {
    fun execute(command: RegisterDeviceCommand): Device {
        // Domain creates device with business rules
        val device = Device.register(
            deviceId = DeviceId.generate(),
            serialNumber = SerialNumber(command.serialNumber),
            premisesId = command.premisesId
        )
        
        // Save and publish
        val saved = deviceRepository.save(device)
        eventPublisher.publish(device.domainEvents)
        
        return saved
    }
}
```

---

## <a name="real-world-example"></a>4. Real-World Example: The SmartHome Hub Story

### The Business Domain

**SmartHome Hub** is a smart home automation platform for homeowners and tenants:

**Core Concepts:**
- **Homeowner** registers and owns a **Premises** (home/apartment/office)
- **Premises** contains **Devices** (sensors, switches, cameras, thermostats)
- **Devices** have **Feeds** (temperature readings, humidity, switch state)
- Homeowners create **Automations** ("Turn on AC when temperature > 30°C")
- Homeowners invite **Tenants** as **Actors** with specific **Roles**
- Devices are organized into **Zones** (Living Room, Bedroom, Kitchen)
- **Widgets** display feeds on dashboards

**Business Rules:**
- Maximum 100 devices per premises
- Devices must be activated within 24 hours of registration
- Automation rules can't create infinite loops
- Temperature sensors must report at least once every 5 minutes
- Actors can only control devices they have permission for

### Without DDD: The Mess (Current State)

```kotlin
// Everything is a String! 😱
data class Device(
    val id: String,              // Could be anything!
    val name: String,            // Could be empty!
    val serialNumber: String,    // No validation!
    val type: String,            // "SENSOR"? "sensor"? "Sensor"?
    val status: String,          // "active"? "ACTIVE"? "Active"?
    val premisesId: String,      // Could be wrong ID!
    val temperature: String?,    // Temperature as String?!
    val lastSeen: Long           // Timestamp as Long
)

// Business logic everywhere
@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val feedService: FeedService,
    private val authService: AuthService,
    private val notificationService: NotificationService
) {
    fun registerDevice(request: DeviceRequest): Device {
        // Validation
        if (request.serialNumber.length < 8) throw Exception()
        if (request.name.isBlank()) throw Exception()
        if (request.type !in listOf("SENSOR", "ACTUATOR")) throw Exception()
        
        // Business rule
        val count = deviceRepository.countByPremisesId(request.premisesId)
        if (count >= 100) throw Exception("Too many devices")
        
        val device = Device(
            id = UUID.randomUUID().toString(),
            name = request.name,
            serialNumber = request.serialNumber,
            type = request.type,
            status = "PENDING",
            premisesId = request.premisesId,
            temperature = null,
            lastSeen = System.currentTimeMillis()
        )
        
        val saved = deviceRepository.save(device)
        
        // Create feeds
        feedService.createFeeds(saved.id, request.feeds)
        
        // Register credentials
        authService.register(saved.id, saved.serialNumber)
        
        // Send notification
        notificationService.send("Device registered", saved.id)
        
        // Update status
        saved.status = "ACTIVE"
        
        return deviceRepository.save(saved)
    }
}
```

**Problems:**
- Device is anemic (just data)
- Validation scattered
- Business rules in service
- Coupling (4 dependencies)
- No type safety
- Can't test business logic without infrastructure

### With DDD: Clean and Maintainable

```kotlin
// 1. VALUE OBJECTS - Type safety and validation
@JvmInline
value class DeviceId(val value: String) {
    companion object {
        fun generate(): DeviceId = DeviceId(UUID.randomUUID().toString())
    }
}

@JvmInline
value class SerialNumber(val value: String) {
    init {
        require(value.matches(Regex("^[A-Z0-9]{8,20}$"))) {
            "Serial number must be 8-20 alphanumeric characters"
        }
    }
}

data class Name(val value: String) {
    init {
        require(value.trim().length in 4..50) {
            "Name must be between 4 and 50 characters"
        }
    }
}

enum class DeviceType {
    SENSOR,
    ACTUATOR,
    CONTROLLER
}

enum class DeviceStatus {
    PENDING,
    ACTIVE,
    INACTIVE,
    DELETED
}

// 2. ENTITY - Rich domain model with behavior
class Device private constructor(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val name: Name,
    val serialNumber: SerialNumber,
    val type: DeviceType,
    private var status: DeviceStatus = DeviceStatus.PENDING,
    private var health: DeviceHealth = DeviceHealth.OFFLINE,
    private val feedIds: List<FeedId> = emptyList(),
    val registeredAt: Instant = Instant.now(),
    private var lastSeenAt: Instant? = null
) : AggregateRoot<DeviceEvent>() {
    
    // BUSINESS METHODS with business rules
    fun activate(): Device {
        if (status == DeviceStatus.ACTIVE) {
            throw DeviceAlreadyActiveException(deviceId)
        }
        
        if (status == DeviceStatus.DELETED) {
            throw CannotActivateDeletedDeviceException(deviceId)
        }
        
        return copy(status = DeviceStatus.ACTIVE).also {
            it.addDomainEvent(DeviceActivatedEvent(deviceId))
        }
    }
    
    fun markOnline(): Device {
        if (status != DeviceStatus.ACTIVE) {
            throw InactiveDeviceCannotBeOnlineException(deviceId)
        }
        
        return copy(
            health = DeviceHealth.ONLINE,
            lastSeenAt = Instant.now()
        ).also {
            it.addDomainEvent(DeviceHealthChangedEvent(deviceId, DeviceHealth.ONLINE))
        }
    }
    
    fun markOffline(): Device {
        return copy(health = DeviceHealth.OFFLINE).also {
            it.addDomainEvent(DeviceHealthChangedEvent(deviceId, DeviceHealth.OFFLINE))
        }
    }
    
    fun updateFeeds(newFeedIds: List<FeedId>): Device {
        if (status == DeviceStatus.ACTIVE && newFeedIds.isEmpty()) {
            throw ActiveDeviceRequiresFeedsException(deviceId)
        }
        
        return copy(feedIds = newFeedIds).also {
            it.addDomainEvent(DeviceFeedsUpdatedEvent(deviceId, newFeedIds))
        }
    }
    
    // FACTORY METHOD
    companion object {
        fun register(
            deviceId: DeviceId,
            premisesId: PremisesId,
            name: Name,
            serialNumber: SerialNumber,
            type: DeviceType
        ): Device {
            val device = Device(
                deviceId = deviceId,
                premisesId = premisesId,
                name = name,
                serialNumber = serialNumber,
                type = type
            )
            device.addDomainEvent(
                DeviceRegisteredEvent(deviceId, premisesId, serialNumber)
            )
            return device
        }
    }
    
    private fun copy(
        status: DeviceStatus = this.status,
        health: DeviceHealth = this.health,
        feedIds: List<FeedId> = this.feedIds,
        lastSeenAt: Instant? = this.lastSeenAt
    ): Device {
        return Device(
            deviceId, premisesId, name, serialNumber, type,
            status, health, feedIds, registeredAt, lastSeenAt
        )
    }
}

// 3. DOMAIN POLICY - Business rule
class DeviceRegistrationPolicy(
    private val deviceRepository: DeviceRepository
) {
    fun evaluate(premisesId: PremisesId): PolicyResult {
        val deviceCount = deviceRepository.countByPremisesId(premisesId)
        
        return if (deviceCount >= MAX_DEVICES_PER_PREMISES) {
            PolicyResult.violation(
                "Maximum $MAX_DEVICES_PER_PREMISES devices allowed per premises"
            )
        } else {
            PolicyResult.valid()
        }
    }
    
    companion object {
        const val MAX_DEVICES_PER_PREMISES = 100
    }
}

// 4. APPLICATION USE CASE - Thin orchestration
@Service
class RegisterDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val deviceRegistrationPolicy: DeviceRegistrationPolicy,
    private val eventPublisher: EventPublisher<DeviceEvent>
) {
    fun execute(command: RegisterDeviceCommand): Device {
        // Check policy
        val policyResult = deviceRegistrationPolicy.evaluate(command.premisesId)
        if (!policyResult.isValid) {
            throw PolicyViolationException(policyResult.violations)
        }
        
        // Domain creates device with all business rules
        val device = Device.register(
            deviceId = DeviceId.generate(),
            premisesId = command.premisesId,
            name = Name(command.name),
            serialNumber = SerialNumber(command.serialNumber),
            type = command.type
        )
        
        // Save and publish events
        val saved = deviceRepository.save(device)
        eventPublisher.publish(device.domainEvents)
        
        return saved
    }
}

// 5. DOMAIN EVENT
data class DeviceRegisteredEvent(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val serialNumber: SerialNumber,
    val occurredAt: Instant = Instant.now()
) : DomainEvent
```

**Benefits:**
- ✅ Device has behavior (activate, markOnline)
- ✅ Validation in value objects (SerialNumber, Name)
- ✅ Business rules in domain (policy, entity methods)
- ✅ Type safety (can't pass wrong types)
- ✅ Use case is thin (just orchestration)
- ✅ Easy to test (domain tests without infrastructure)
- ✅ Code speaks business language
- ✅ Single source of truth for business rules

---

## <a name="core-concepts"></a>5. Core DDD Building Blocks

### 5.1 Ubiquitous Language

**The language used by domain experts, developers, and code.**

❌ **Bad** (Technical language):
```kotlin
class RecordProcessor {
    fun processData(dto: DataTransferObject): ProcessingResult
}
```

✅ **Good** (Business language):
```kotlin
class TemperatureSensor {
    fun recordTemperature(reading: TemperatureReading): SensorReading
}
```

### 5.2 Bounded Context

**A boundary within which a specific domain model is valid.**

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  USER Context   │     │  AUTH Context   │     │ DEVICE Context  │
│                 │     │                 │     │                 │
│  User:          │     │  AuthUser:      │     │  Device:        │
│  - Profile      │     │  - Credentials  │     │  - Hardware     │
│  - Preferences  │     │  - Tokens       │     │  - Feeds        │
│  - Settings     │     │  - Sessions     │     │  - Health       │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

**Key:** User in USER context ≠ AuthUser in AUTH context

### 5.3 Entity

**Object with identity that persists over time.**

```kotlin
// Entity - Identity = deviceId
class Device(
    val deviceId: DeviceId,  // Identity (never changes)
    val name: Name,          // Can change
    val status: DeviceStatus // Can change
)

// Two devices with same name are different if deviceId differs
val device1 = Device(DeviceId("123"), Name("Sensor"), DeviceStatus.ACTIVE)
val device2 = Device(DeviceId("456"), Name("Sensor"), DeviceStatus.ACTIVE)
// device1 != device2 (different identity)
```

### 5.4 Value Object

**Object defined by its values, no identity.**

```kotlin
// Value Object - No identity
@JvmInline
value class Temperature(val celsius: Double)

// Two temperatures with same value are equal
val temp1 = Temperature(25.0)
val temp2 = Temperature(25.0)
// temp1 == temp2 (same value)
```

### 5.5 Aggregate

**Cluster of entities and value objects with a root entity.**

```kotlin
// Premises is aggregate root
class Premises(
    val premisesId: PremisesId,     // Root identity
    val name: Name,
    val address: Address,            // Value object
    private val deviceIds: List<DeviceId>  // References to other aggregates
)

// Access through aggregate root only
premisesRepository.findById(premisesId)  // ✅ Good
deviceRepository.findById(deviceId)      // ✅ Good (separate aggregate)
// Never access Device through Premises directly
```

### 5.6 Domain Service

**Operations that don't naturally belong to any entity.**

```kotlin
// Domain Service - Password hashing doesn't belong to User or AuthUser
interface PasswordService {
    fun hash(password: Password): HashedPassword
    fun verify(password: Password, hash: HashedPassword): Boolean
}
```

### 5.7 Repository

**Collection interface for aggregates.**

```kotlin
// Repository for Device aggregate
interface DeviceRepository {
    fun save(device: Device): Device
    fun findById(deviceId: DeviceId): Device?
    fun findAllByPremisesId(premisesId: PremisesId): List<Device>
    fun delete(deviceId: DeviceId)
}
```

### 5.8 Domain Event

**Something important that happened in the domain.**

```kotlin
// Domain Event
data class DeviceActivatedEvent(
    val deviceId: DeviceId,
    val occurredAt: Instant = Instant.now()
) : DomainEvent
```

### 5.9 Specification

**Reusable query criteria.**

```kotlin
// Specification
interface DeviceSpecification {
    fun isSatisfiedBy(device: Device): Boolean
}

class ActiveDeviceSpecification : DeviceSpecification {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.status == DeviceStatus.ACTIVE
    }
}
```

### 5.10 Policy

**Business rules and validation.**

```kotlin
// Policy
class DeviceLimitPolicy {
    fun evaluate(premisesId: PremisesId): PolicyResult {
        // Business rule logic
    }
}
```

---

## <a name="ddd-layers"></a>6. DDD Layers: Where Everything Lives

### The Four-Layer Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│  (REST Controllers, GraphQL, WebSocket, CLI)                │
│  - DeviceController.kt                                      │
│  - DeviceRequest, DeviceResponse (DTOs)                     │
│  - Input validation, mapping                                │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    APPLICATION LAYER                        │
│  (Use Cases, Application Services)                          │
│  - RegisterDeviceUseCase.kt                                 │
│  - ActivateDeviceUseCase.kt                                 │
│  - Commands, orchestration, transaction management          │
│  - Event publishing                                         │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                           │
│  (Business Logic - The Heart!)                              │
│                                                             │
│  Entities:                                                  │
│  - Device.kt (with activate(), markOnline() methods)        │
│  - Premises.kt                                              │
│                                                             │
│  Value Objects:                                             │
│  - DeviceId.kt                                              │
│  - SerialNumber.kt                                          │
│  - Name.kt                                                  │
│                                                             │
│  Domain Services:                                           │
│  - PasswordService.kt (interface)                           │
│  - DeviceActivationService.kt                               │
│                                                             │
│  Policies:                                                  │
│  - DeviceRegistrationPolicy.kt                              │
│                                                             │
│  Repositories (Interfaces):                                 │
│  - DeviceRepository.kt                                      │
│                                                             │
│  Domain Events:                                             │
│  - DeviceActivatedEvent.kt                                  │
│                                                             │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   INFRASTRUCTURE LAYER                       │
│  (Technical Implementation)                                  │
│  - MongoDeviceRepository.kt (implements DeviceRepository)    │
│  - DeviceDocument.kt (MongoDB model)                         │
│  - BcryptPasswordService.kt (implements PasswordService)     │
│  - KafkaEventPublisher.kt                                    │
│  - External API clients                                      │
└─────────────────────────────────────────────────────────────┘
```

### Dependency Rule

**Dependencies point INWARD (toward domain)**

```
Presentation → Application → Domain
Infrastructure → Domain

Domain does NOT depend on anything!
```

### Example: Device Registration Flow

```
1. Presentation Layer
   ↓
   DeviceController receives HTTP POST /api/devices
   Validates input, creates RegisterDeviceCommand
   
2. Application Layer
   ↓
   RegisterDeviceUseCase.execute(command)
   Checks policy, calls domain
   
3. Domain Layer
   ↓
   Device.register() creates device with business rules
   Adds DeviceRegisteredEvent
   
4. Application Layer (continued)
   ↓
   Saves device via DeviceRepository
   Publishes events
   
5. Infrastructure Layer
   ↓
   MongoDeviceRepository saves to database
   KafkaEventPublisher publishes to Kafka
```

---

## <a name="when-to-use"></a>7. When to Use DDD (and When Not To)

### ✅ Use DDD When:

#### 1. Complex Business Logic

```kotlin
// Complex: Automation rules with triggers, conditions, actions
class Automation(
    val triggers: List<Trigger>,
    val conditions: ConditionTree,
    val actions: List<Action>
) {
    fun shouldExecute(context: AutomationContext): Boolean {
        return triggers.any { it.isMet(context) } &&
               conditions.evaluate(context)
    }
}
```

#### 2. Long-Lived Projects

Projects that will evolve over 2+ years need maintainable architecture.

#### 3. Domain Experts Available

You have business people who understand the domain deeply and can collaborate.

#### 4. Multiple Teams

Bounded contexts allow teams to work independently:
```
Team A: Device Context
Team B: Automation Context
Team C: Analytics Context
```

#### 5. Changing Requirements

Business rules change frequently, DDD makes changes easier:
```kotlin
// Easy to change device limit
class DeviceRegistrationPolicy {
    companion object {
        const val MAX_DEVICES = 100  // ← Change here only
    }
}
```

### ❌ Don't Use DDD When:

#### 1. Simple CRUD

```kotlin
// Simple blog: Just saving/reading posts
@Service
class BlogService {
    fun createPost(title: String, content: String): Post {
        return postRepository.save(Post(title, content))
    }
}
// ← No complex business logic, don't need DDD
```

#### 2. Tight Deadlines

MVP that needs to ship in 2 weeks - focus on speed, not architecture.

#### 3. No Domain Complexity

Data transformation pipelines, reporting tools, admin panels don't need DDD.

#### 4. Pure Technical Problems

Building a database engine, compiler, or OS - these are technical, not business domains.

#### 5. Solo Side Projects

DDD overhead isn't worth it for one-person projects.

### The SmartHome Hub Decision

**SmartHome Hub SHOULD use DDD because:**

✅ **Complex Domain**
- Devices, feeds, automations, rules, triggers, conditions, actions
- Multi-tenancy, permissions, roles
- Real-time device health monitoring

✅ **Multiple Bounded Contexts**
- User Management
- Authentication
- Device Management
- Automation Engine
- Identity (Identity and Access Management)
- Analytics
- Billing

✅ **Long-Lived Project**
- 5+ year vision
- Will evolve and grow

✅ **Multiple Teams**
- Will scale to 3-4 teams
- Each can own a bounded context

✅ **Changing Business Rules**
- Device limits change
- New device types added
- Automation rules evolve
- Pricing tiers change

---

## <a name="journey-ahead"></a>8. The Journey Ahead

### What We'll Build in This Series

Over the next 19 chapters, we'll transform the SmartHome Hub codebase from a tangled mess to a clean DDD architecture:

```
TRANSFORMATION ROADMAP

Chapter 1: Understanding DDD [You are here!]
    ↓
Chapter 2: Anemic vs Rich Domain Models
    Learn to add behavior to entities
    ↓
Chapter 3: Value Objects
    Build type-safe, self-validating value objects
    ↓
Chapter 4: Entities and Aggregates
    Design proper aggregate boundaries
    ↓
Chapter 5: Specification Pattern
    Solve query explosion
    ↓
Chapter 6: Policy Pattern
    Centralize business rules
    ↓
Chapter 7: Repository Pattern
    Implement repositories correctly
    ↓
Chapter 8: Domain vs Application Services
    Clear separation of concerns
    ↓
Chapter 9: Bounded Contexts
    Design independent contexts
    ↓
Chapter 10: Anti-Corruption Layer
    Protect your domain
    ↓
Chapter 11: Domain & Integration Events
    Event-driven architecture
    ↓
Chapter 12: Saga Pattern
    Distributed transactions
    ↓
Chapters 13-20: Advanced patterns and implementation
```

### Success Metrics

**Before DDD:**
```
❌ 3 days to add device limit feature
❌ 5 bugs found in testing
❌ 23 unit tests to update
❌ New developers need 2 weeks to be productive
❌ 70% of changes break existing features
❌ Device limit in 3 different places (50, 75, 100)
❌ Can't test business logic without infrastructure
```

**After DDD:**
```
✅ 1 hour to add device limit feature
✅ 0 bugs (caught by domain tests)
✅ 2 unit tests to update (only domain tests)
✅ New developers productive in 3 days
✅ 95% of changes isolated, no breakage
✅ Device limit in ONE place (DeviceRegistrationPolicy)
✅ Business logic tested in isolation
```

### Your Learning Path

Each chapter builds on the previous one:

1. **Foundation (Chapters 1-4)** - Core concepts
2. **Tactical Patterns (Chapters 5-8)** - Implementation details
3. **Strategic Patterns (Chapters 9-12)** - Large-scale design
4. **Advanced Topics (Chapters 13-16)** - Optional patterns
5. **Implementation (Chapters 17-20)** - Real-world application

---

## 💡 Key Takeaways

1. **DDD is about understanding the business domain** - Technology is secondary

2. **Code should speak the business language** - Use terms domain experts use

3. **Put business logic in the domain layer** - Not in controllers or services

4. **Bounded contexts provide clear boundaries** - Enable independent teams

5. **Tactical patterns organize code** - Entities, value objects, aggregates, etc.

6. **Start small, grow incrementally** - Don't rewrite everything at once

7. **DDD is a journey, not a destination** - Continuous improvement

8. **Not every project needs DDD** - Use it when complexity justifies it

---

## 🎯 Practical Exercise

Before moving to Chapter 2, try this exercise:

### Exercise 1: Identify Your Domain

1. **List 5-10 core business concepts** in your current project
   - Example: User, Device, Premises, Automation, Rule

2. **Find your ubiquitous language**
   - What terms do your business people use?
   - Are those terms in your code?
   - Example: Do you say "Homeowner" or "User"?

3. **Spot anemic models**
   - Find 3 classes that are just data holders
   - Where is the business logic for those classes?
   - Example: Is Device just fields, or does it have activate() method?

4. **Identify bounded contexts**
   - What are the major boundaries in your system?
   - Are they clear in the code?
   - Example: User Management, Device Management, Billing

5. **Find scattered validation**
   - Pick one field (like email)
   - Search where it's validated
   - How many places? Are they consistent?

### Exercise 2: Calculate Technical Debt

Answer these questions about a recent feature you added:

1. How many files did you modify?
2. How many services/classes did you touch?
3. How many unit tests needed updating?
4. How long did it take? (estimate vs actual)
5. How many bugs were found in testing?
6. Could you test the business logic without infrastructure?

**If the answers are high numbers**, you likely need DDD!

---

## 📚 Additional Resources

### Books
- **"Domain-Driven Design"** by Eric Evans (2003) - The original
- **"Implementing Domain-Driven Design"** by Vaughn Vernon (2013) - Practical guide
- **"Domain-Driven Design Distilled"** by Vaughn Vernon (2016) - Quick overview

### Online Resources
- Martin Fowler's DDD articles: https://martinfowler.com/tags/domain%20driven%20design.html
- DDD Community: https://dddcommunity.org/
- Kotlin DDD: https://github.com/kotlin-ddd/kotlin-ddd

### Code Examples
- SmartHome Hub Backend Repository: (your repository URL)
- Complete working examples in this series

---

## 🚀 Next Chapter

Ready to fix the most common DDD mistake?

👉 **[Chapter 2: Anemic vs Rich Domain Models - The Most Common DDD Mistake](./02-anemic-vs-rich-domain-COMPLETE.md)**

**You'll learn:**
- What makes a domain model "anemic"
- How to add behavior to entities
- Step-by-step refactoring guide
- Real examples from SmartHome Hub
- Testing strategies for rich models

**Reading Time:** 20 minutes  
**Difficulty:** Beginner to Intermediate  

---

## 💬 Join the Discussion

Have questions about DDD? Struggling with a specific pattern? Want to share your DDD journey?

- **GitHub Issues:** Open an issue in the repository
- **Team Chat:** Join our Slack/Discord channel
- **Comments:** Leave feedback on Medium

---

**Questions?** Leave them in the comments!

**Found this helpful?** Share it with your team!

**Ready to continue?** Let's dive into [Chapter 2](./02-anemic-vs-rich-domain-COMPLETE.md)!

---

*"The heart of software is its ability to solve domain-related problems for its user. All other features, vital though they may be, support this basic purpose."*  
— Eric Evans, Domain-Driven Design

