# Chapter 17
# Refactoring to DDD
## A Step-by-Step Guide

> *"Any fool can write code that a computer can understand. Good programmers write code that humans can understand."*  
> — Martin Fowler

---

## In This Chapter

Most teams don't start with DDD—they inherit legacy codebases full of anemic models, scattered business logic, and tight coupling. This chapter provides a practical, incremental strategy for refactoring existing code to DDD without the risk and cost of a complete rewrite.

**What You'll Learn:**
- Assessing legacy codebases for DDD readiness
- The Strangler Fig pattern for gradual migration
- Six-step refactoring process
- Extracting value objects from primitives
- Creating rich entities from anemic models
- Defining aggregate boundaries
- Implementing clean repositories
- Adding domain events for decoupling
- Complete before/after refactoring example
- Measuring improvement

---

## Table of Contents

1. The Problem: Legacy Codebase
2. Assessing Your Current State
3. The Refactoring Strategy
4. Step 1: Identify Domain Logic
5. Step 2: Extract Value Objects
6. Step 3: Create Rich Entities
7. Step 4: Define Aggregates
8. Step 5: Implement Repositories
9. Step 6: Add Domain Events
10. Real Refactoring Example
11. Chapter Summary

---

## 1. The Problem: Legacy Codebase

### The Scenario: The Refactoring Request

Your CTO approaches you with a challenge:

> **CTO:** "Our codebase is a mess. 50,000 lines of spaghetti code. Features take weeks instead of days. Bugs everywhere. Can you refactor it to DDD?"  
> **You:** "Where do I even start?" 💥

You open the codebase and find:

```kotlin
// Typical legacy service - 2000+ lines ❌
@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val userRepository: UserRepository,
    private val automationRepository: AutomationRepository,
    private val notificationService: NotificationService,
    private val analyticsService: AnalyticsService,
    private val billingService: BillingService,
    // ... 20 more dependencies
) {
    
    fun registerDevice(request: DeviceRegistrationRequest): DeviceResponse {
        // 500 lines of procedural code
        // Validation scattered everywhere
        // Business logic mixed with infrastructure
        // No domain model
        // Everything is a data class with getters/setters
        // 💥💥💥
    }
    
    fun updateDeviceStatus(deviceId: String, status: String) {
        // Another 300 lines
    }
    
    fun deleteDevice(deviceId: String) {
        // Another 200 lines
    }
    
    // 50+ more methods
    // Total: 2000+ lines in one service!
}
```

### The Questions

- **Where do I start?** 50,000 lines to refactor
- **How long will it take?** Business wants features NOW
- **Will it break production?** Can't afford downtime
- **What's the ROI?** Management wants justification
- **How do I get buy-in?** Team resists change

---

## <a name="assessment"></a>2. Assessing Your Current State

### Code Smell Checklist

**🔴 Critical Smells (Refactor ASAP):**

```kotlin
// Smell 1: Anemic Domain Models
data class Device(
    var id: String,
    var name: String,
    var status: String,  // Just a string!
    var feeds: MutableList<Feed>  // Mutable!
)
// No behavior, no validation, no invariants ❌

// Smell 2: God Service
@Service
class DeviceService {
    // 2000+ lines
    // 50+ methods
    // 20+ dependencies
}

// Smell 3: Primitive Obsession
fun registerDevice(
    deviceId: String,  // Should be DeviceId
    name: String,      // Should be Name
    status: String,    // Should be DeviceStatus enum
    // All primitives! ❌
)

// Smell 4: Business Logic in Controllers
@RestController
class DeviceController {
    @PostMapping
    fun register(@RequestBody request: DeviceRequest): DeviceResponse {
        // Validation here ❌
        if (request.name.isBlank()) throw Exception()
        
        // Business logic here ❌
        if (request.feeds.size < 1) throw Exception()
        
        // Should be in domain!
    }
}

// Smell 5: Scattered Validation
// Validation in controller
if (name.isBlank()) throw Exception()

// Validation in service
if (device.feeds.isEmpty()) throw Exception()

// Validation in entity
init { require(id.isNotBlank()) }

// No single source of truth! ❌
```

**🟡 Warning Smells (Refactor Soon):**

```kotlin
// Smell 6: Transaction Script Pattern
fun processOrder() {
    // Step 1: Load data
    val device = deviceRepo.findById(id)
    val user = userRepo.findById(userId)
    
    // Step 2: Update data
    device.status = "ACTIVE"
    device.updatedAt = Instant.now()
    
    // Step 3: Save data
    deviceRepo.save(device)
    
    // Procedural, no domain model ❌
}

// Smell 7: Feature Envy
class DeviceService {
    fun activate(device: Device) {
        // Service doing work that should be in Device
        device.status = "ACTIVE"
        device.lastSeenAt = Instant.now()
        device.health = "ONLINE"
    }
}

// Smell 8: Data Transfer Object as Domain Model
@Document("devices")
data class Device(
    val id: String,
    val name: String
)
// Used both as DB entity AND domain model ❌
```

### Scoring Your Codebase

```kotlin
// Assessment Tool
object CodebaseAssessment {
    
    fun assessFile(file: File): AssessmentScore {
        var score = 100
        val issues = mutableListOf<String>()
        
        // Check for anemic models
        if (hasOnlyGettersSetters(file)) {
            score -= 20
            issues.add("Anemic domain model")
        }
        
        // Check for primitive obsession
        if (usesPrimitives(file)) {
            score -= 15
            issues.add("Primitive obsession")
        }
        
        // Check for god services
        if (linesOfCode(file) > 500) {
            score -= 25
            issues.add("God service/class")
        }
        
        // Check for business logic in wrong layer
        if (hasBusinessLogicInController(file)) {
            score -= 20
            issues.add("Business logic in controller")
        }
        
        return AssessmentScore(score, issues)
    }
}

// Results:
// Score 80-100: Good shape, minor refactoring
// Score 60-79: Needs refactoring
// Score 40-59: Significant refactoring needed
// Score 0-39: Complete rewrite recommended
```

---

## <a name="strategy"></a>3. The Refactoring Strategy

### The Strangler Fig Pattern

**Don't rewrite everything at once!** Use the Strangler Fig pattern:

```
Old System (Spaghetti Code)
┌─────────────────────────────────────┐
│                                     │
│   All features here                 │
│   Legacy code                       │
│                                     │
└─────────────────────────────────────┘

Step 1: New feature in new DDD code
┌─────────────────────────────────────┐
│   Old System (90%)                  │
└─────────────────────────────────────┘
┌─────────────────────────────────────┐
│   New DDD Code (10%)                │
└─────────────────────────────────────┘

Step 2: Refactor critical path
┌─────────────────────────────────────┐
│   Old System (70%)                  │
└─────────────────────────────────────┘
┌─────────────────────────────────────┐
│   New DDD Code (30%)                │
└─────────────────────────────────────┘

Step 3: Continue gradually
┌─────────────────────────────────────┐
│   Old System (30%)                  │
└─────────────────────────────────────┘
┌─────────────────────────────────────┐
│   New DDD Code (70%)                │
└─────────────────────────────────────┘

Final: Old code strangled
┌─────────────────────────────────────┐
│   New DDD Code (100%)               │
└─────────────────────────────────────┘
```

### Refactoring Priorities

```kotlin
// Priority 1: Core Domain (High value, High complexity)
// - Device registration and activation
// - Automation rules
// - Billing logic
// Refactor FIRST ✅

// Priority 2: Supporting Subdomains (Medium value)
// - User management
// - Notification system
// Refactor SECOND

// Priority 3: Generic Subdomains (Low value)
// - Logging
// - Email sending
// - File storage
// Refactor LAST or use off-the-shelf solutions
```

### Timeline Strategy

```kotlin
// Phase 1: Foundation (Week 1-2)
// - Set up DDD structure
// - Create first value objects
// - Extract first domain entity
// Result: Small win, proof of concept

// Phase 2: Core Domain (Week 3-6)
// - Refactor device management
// - Add aggregates
// - Implement repositories
// Result: Core functionality in DDD

// Phase 3: Supporting (Week 7-10)
// - Refactor automation
// - Refactor billing
// Result: Most features in DDD

// Phase 4: Cleanup (Week 11-12)
// - Remove old code
// - Consolidate
// Result: Clean DDD codebase
```

---

## <a name="step-1"></a>4. Step 1: Identify Domain Logic

### Find the Hidden Domain Model

```kotlin
// Legacy code (domain logic hidden in service)
@Service
class DeviceService {
    fun activateDevice(deviceId: String) {
        val device = deviceRepo.findById(deviceId)
        
        // This is domain logic! ✅
        if (device.status != "PENDING") {
            throw IllegalStateException("Can only activate pending devices")
        }
        
        // This is domain logic! ✅
        if (device.feeds.isEmpty()) {
            throw IllegalStateException("Device must have feeds")
        }
        
        // Update state
        device.status = "ACTIVE"
        device.activatedAt = Instant.now()
        
        deviceRepo.save(device)
    }
}
```

### Extract to Domain

```kotlin
// Step 1: Identify domain logic
// ✅ Validation: "Can only activate pending devices"
// ✅ Business rule: "Device must have feeds"
// ✅ State transition: PENDING → ACTIVE

// Step 2: Move to domain entity
class Device(
    val deviceId: DeviceId,
    private var status: DeviceStatus,
    private val feeds: Map<FeedId, Feed>
) {
    /**
     * Activate the device.
     * 
     * Domain rules:
     * - Device must be in PENDING status
     * - Device must have at least one feed
     */
    fun activate(): Device {
        // Domain validation ✅
        require(status == DeviceStatus.PENDING) {
            "Can only activate pending devices"
        }
        
        // Business rule ✅
        require(feeds.isNotEmpty()) {
            "Device must have at least one feed"
        }
        
        // State transition ✅
        return copy(
            status = DeviceStatus.ACTIVE,
            activatedAt = Instant.now()
        )
    }
}

// Service becomes thin
@Service
class ActivateDeviceUseCase(
    private val deviceRepository: DeviceRepository
) {
    fun execute(command: ActivateDeviceCommand) {
        val device = deviceRepository.findById(command.deviceId)
        val activated = device.activate()  // Domain logic here ✅
        deviceRepository.save(activated)
    }
}
```

---

## <a name="step-2"></a>5. Step 2: Extract Value Objects

### Before: Primitive Obsession

```kotlin
// Legacy: Everything is primitives ❌
@Service
class DeviceService {
    fun registerDevice(
        id: String,        // Just a string
        name: String,      // Just a string
        serial: String,    // Just a string
        email: String      // Just a string
    ) {
        // Validation repeated everywhere
        if (id.isBlank()) throw Exception("ID required")
        if (name.isBlank()) throw Exception("Name required")
        if (!serial.matches(Regex("SN\\d{6}"))) {
            throw Exception("Invalid serial")
        }
        if (!email.contains("@")) throw Exception("Invalid email")
        
        // Easy to mix up parameters!
        deviceRepo.save(Device(serial, name, id, email)) // Wrong order! 💥
    }
}
```

### After: Value Objects

```kotlin
// Step 1: Create value objects
@JvmInline
value class DeviceId(val value: String) {
    init {
        require(value.isNotBlank()) { "Device ID cannot be blank" }
        require(value.length <= 50) { "Device ID too long" }
    }
    
    companion object {
        fun generate() = DeviceId(UUID.randomUUID().toString())
    }
}

@JvmInline
value class SerialNumber(val value: String) {
    init {
        require(value.matches(Regex("SN\\d{6}"))) {
            "Serial number must match format SN######"
        }
    }
}

data class Name(val value: String) {
    init {
        require(value.isNotBlank()) { "Name cannot be blank" }
        require(value.length in 1..100) { "Name must be 1-100 characters" }
    }
}

data class Email(val value: String) {
    init {
        require(value.contains("@")) { "Invalid email format" }
    }
}

// Step 2: Use value objects
@Service
class RegisterDeviceUseCase {
    fun execute(
        deviceId: DeviceId,      // Type-safe! ✅
        name: Name,              // Validated! ✅
        serialNumber: SerialNumber,  // Correct format! ✅
        email: Email             // Valid email! ✅
    ) {
        // No validation needed - value objects are always valid!
        // Can't mix up parameters - different types!
        
        val device = Device.register(
            deviceId = deviceId,
            name = name,
            serialNumber = serialNumber,
            ownerEmail = email
        )
        
        deviceRepository.save(device)
    }
}
```

---

## <a name="step-3"></a>6. Step 3: Create Rich Entities

### Before: Anemic Entity

```kotlin
// Anemic: Just data, no behavior ❌
data class Device(
    var id: String,
    var name: String,
    var status: String,
    var feeds: MutableList<Feed> = mutableListOf()
) {
    // No methods!
    // No validation!
    // No business logic!
}

// All logic in service ❌
@Service
class DeviceService {
    fun addFeed(deviceId: String, feed: Feed) {
        val device = deviceRepo.findById(deviceId)
        
        // Validation in service ❌
        if (device.feeds.size >= 10) {
            throw Exception("Max 10 feeds")
        }
        
        // State mutation ❌
        device.feeds.add(feed)
        
        deviceRepo.save(device)
    }
}
```

### After: Rich Entity

```kotlin
// Rich: Behavior + data ✅
class Device private constructor(
    val deviceId: DeviceId,
    private val name: Name,
    private val serialNumber: SerialNumber,
    private var status: DeviceStatus,
    private val feeds: MutableMap<FeedId, Feed>
) {
    companion object {
        private const val MAX_FEEDS = 10
        
        fun register(
            deviceId: DeviceId,
            name: Name,
            serialNumber: SerialNumber
        ): Device {
            return Device(
                deviceId = deviceId,
                name = name,
                serialNumber = serialNumber,
                status = DeviceStatus.PENDING,
                feeds = mutableMapOf()
            )
        }
    }
    
    /**
     * Add a feed to the device.
     * 
     * Business rules:
     * - Maximum 10 feeds per device
     * - No duplicate feed IDs
     */
    fun addFeed(feed: Feed): Device {
        // Validation ✅
        require(feeds.size < MAX_FEEDS) {
            "Device can have maximum $MAX_FEEDS feeds"
        }
        
        require(!feeds.containsKey(feed.feedId)) {
            "Feed ${feed.feedId} already exists"
        }
        
        // Create new instance (immutability) ✅
        val newFeeds = feeds.toMutableMap()
        newFeeds[feed.feedId] = feed
        
        return copy(feeds = newFeeds)
    }
    
    /**
     * Activate the device.
     */
    fun activate(): Device {
        require(status == DeviceStatus.PENDING) {
            "Can only activate pending devices"
        }
        require(feeds.isNotEmpty()) {
            "Device must have at least one feed"
        }
        
        return copy(status = DeviceStatus.ACTIVE)
    }
    
    // Getters with business meaning ✅
    fun getName(): Name = name
    fun getStatus(): DeviceStatus = status
    fun getFeeds(): List<Feed> = feeds.values.toList()
    fun isActive(): Boolean = status == DeviceStatus.ACTIVE
    fun canAddMoreFeeds(): Boolean = feeds.size < MAX_FEEDS
    
    private fun copy(
        name: Name = this.name,
        status: DeviceStatus = this.status,
        feeds: MutableMap<FeedId, Feed> = this.feeds
    ): Device {
        return Device(deviceId, name, serialNumber, status, feeds)
    }
}

// Service becomes thin ✅
@Service
class AddFeedToDeviceUseCase(
    private val deviceRepository: DeviceRepository
) {
    fun execute(command: AddFeedCommand) {
        val device = deviceRepository.findById(command.deviceId)
        val feed = Feed.create(command.feedId, command.name, command.type)
        
        // Business logic in domain ✅
        val updated = device.addFeed(feed)
        
        deviceRepository.save(updated)
    }
}
```

---

## <a name="step-4"></a>7. Step 4: Define Aggregates

### Before: No Boundaries

```kotlin
// Everything is public, anyone can modify anything ❌
data class Device(
    var id: String,
    var name: String,
    var feeds: MutableList<Feed>  // Direct access! ❌
)

data class Feed(
    var id: String,
    var value: Int  // Anyone can change! ❌
)

// Service can modify feeds directly ❌
@Service
class SomeService {
    fun doSomething() {
        val device = deviceRepo.findById(id)
        
        // Bypass all validation! ❌
        device.feeds[0].value = 999
        device.feeds.add(Feed("new-feed", 0))
        
        deviceRepo.save(device)
    }
}
```

### After: Aggregate Root

```kotlin
// Device is the aggregate root ✅
class Device private constructor(
    val deviceId: DeviceId,  // ID is public (identity)
    private val name: Name,  // Private fields ✅
    private val feeds: MutableMap<FeedId, Feed>  // Private! ✅
) {
    /**
     * Update feed value.
     * 
     * ONLY way to change feed value - goes through aggregate root ✅
     */
    fun updateFeedValue(feedId: FeedId, newValue: Int): Device {
        val feed = feeds[feedId]
            ?: throw FeedNotFoundException(feedId)
        
        // Validation at aggregate boundary ✅
        require(newValue in feed.getMinValue()..feed.getMaxValue()) {
            "Value $newValue outside valid range"
        }
        
        val updatedFeed = feed.updateValue(newValue)
        val newFeeds = feeds.toMutableMap()
        newFeeds[feedId] = updatedFeed
        
        return copy(feeds = newFeeds)
    }
    
    /**
     * Get feeds (defensive copy) ✅
     */
    fun getFeeds(): List<Feed> = feeds.values.toList()
    
    // NO setters! ✅
    // NO direct feed access! ✅
}

// Feed is part of the aggregate, not independent ✅
class Feed internal constructor(  // internal = can't create outside package
    val feedId: FeedId,
    private val name: String,
    private val type: FeedType,
    private val value: Int,
    private val minValue: Int,
    private val maxValue: Int
) {
    internal fun updateValue(newValue: Int): Feed {
        return copy(value = newValue)
    }
    
    fun getValue(): Int = value
    fun getMinValue(): Int = minValue
    fun getMaxValue(): Int = maxValue
    
    private fun copy(value: Int = this.value): Feed {
        return Feed(feedId, name, type, value, minValue, maxValue)
    }
}

// Repository only for aggregate root ✅
interface DeviceRepository {
    fun findById(deviceId: DeviceId): Device?
    fun save(device: Device): Device
    
    // NO feed repository! ✅
    // Feeds are always accessed through Device ✅
}

// Service must go through aggregate ✅
@Service
class UpdateFeedValueUseCase(
    private val deviceRepository: DeviceRepository
) {
    fun execute(command: UpdateFeedValueCommand) {
        val device = deviceRepository.findById(command.deviceId)
            ?: throw DeviceNotFoundException(command.deviceId)
        
        // Must use aggregate method ✅
        val updated = device.updateFeedValue(command.feedId, command.newValue)
        
        deviceRepository.save(updated)
    }
}
```

---

## <a name="step-5"></a>8. Step 5: Implement Repositories

### Before: Repository Chaos

```kotlin
// Repository does too much ❌
interface DeviceRepository : MongoRepository<Device, String> {
    fun findByStatus(status: String): List<Device>
    fun findByName(name: String): List<Device>
    fun findByNameContaining(name: String): List<Device>
    fun findByStatusAndName(status: String, name: String): List<Device>
    fun findByPremisesId(premisesId: String): List<Device>
    fun findByPremisesIdAndStatus(premisesId: String, status: String): List<Device>
    fun findByPremisesIdAndNameContaining(premisesId: String, name: String): List<Device>
    // 50+ query methods! ❌
    
    // Business logic in repository! ❌
    fun findActiveDevices(): List<Device> {
        return findByStatus("ACTIVE")
    }
}
```

### After: Clean Repository

```kotlin
// Repository: Simple persistence ✅
interface DeviceRepository {
    fun findById(deviceId: DeviceId): Device?
    fun save(device: Device): Device
    fun delete(deviceId: DeviceId)
    
    // One flexible query method ✅
    fun findBySpecification(spec: Specification<Device>): List<Device>
}

// Queries using specifications ✅
class DeviceSpecifications {
    companion object {
        fun withStatus(status: DeviceStatus): Specification<Device> {
            return Specification { device ->
                device.getStatus() == status
            }
        }
        
        fun inPremises(premisesId: PremisesId): Specification<Device> {
            return Specification { device ->
                device.premisesId == premisesId
            }
        }
        
        fun nameContains(searchTerm: String): Specification<Device> {
            return Specification { device ->
                device.getName().value.contains(searchTerm, ignoreCase = true)
            }
        }
    }
}

// Usage ✅
@Service
class FindActiveDevicesInPremisesUseCase(
    private val deviceRepository: DeviceRepository
) {
    fun execute(premisesId: PremisesId): List<Device> {
        val spec = DeviceSpecifications.inPremises(premisesId)
            .and(DeviceSpecifications.withStatus(DeviceStatus.ACTIVE))
        
        return deviceRepository.findBySpecification(spec)
    }
}
```

---

## <a name="step-6"></a>9. Step 6: Add Domain Events

### Before: Direct Calls

```kotlin
// Service calls everything directly ❌
@Service
class DeviceService(
    private val deviceRepo: DeviceRepository,
    private val notificationService: NotificationService,
    private val analyticsService: AnalyticsService,
    private val automationService: AutomationService
) {
    fun activateDevice(deviceId: String) {
        val device = deviceRepo.findById(deviceId)
        device.status = "ACTIVE"
        deviceRepo.save(device)
        
        // Tightly coupled! ❌
        notificationService.sendDeviceActivated(device)
        analyticsService.trackActivation(device)
        automationService.evaluateRules(device)
    }
}
```

### After: Domain Events

```kotlin
// Domain publishes events ✅
class Device {
    private val _domainEvents = mutableListOf<DomainEvent>()
    val domainEvents: List<DomainEvent> get() = _domainEvents.toList()
    
    fun activate(): Device {
        val activated = copy(status = DeviceStatus.ACTIVE)
        
        // Publish domain event ✅
        activated.addDomainEvent(
            DeviceActivated(
                deviceId = deviceId,
                activatedAt = Instant.now()
            )
        )
        
        return activated
    }
    
    private fun addDomainEvent(event: DomainEvent) {
        _domainEvents.add(event)
    }
    
    fun clearDomainEvents() {
        _domainEvents.clear()
    }
}

// Event
data class DeviceActivated(
    val deviceId: DeviceId,
    val activatedAt: Instant
) : DomainEvent

// Use case publishes events ✅
@Service
class ActivateDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val eventPublisher: DomainEventPublisher
) {
    fun execute(command: ActivateDeviceCommand) {
        val device = deviceRepository.findById(command.deviceId)
        val activated = device.activate()
        
        deviceRepository.save(activated)
        
        // Publish events ✅
        activated.domainEvents.forEach { event ->
            eventPublisher.publish(event)
        }
    }
}

// Event handlers (loosely coupled) ✅
@ComponentInline
class DeviceActivatedEventHandler(
    private val notificationService: NotificationService
) {
    @EventListener
    fun handle(event: DeviceActivated) {
        notificationService.sendDeviceActivated(event.deviceId)
    }
}

@ComponentInline
class DeviceActivatedAnalyticsHandler(
    private val analyticsService: AnalyticsService
) {
    @EventListener
    fun handle(event: DeviceActivated) {
        analyticsService.trackActivation(event.deviceId)
    }
}
```

---

## <a name="real-example"></a>10. Real Refactoring Example

### Complete Before/After

**BEFORE: Legacy Code**

```kotlin
// Legacy: 500 lines of spaghetti ❌
@Service
class DeviceService(
    private val deviceRepo: MongoRepository<DeviceDoc, String>,
    private val userRepo: MongoRepository<UserDoc, String>,
    private val notificationService: NotificationService,
    private val analyticsService: AnalyticsService
) {
    
    fun registerDevice(request: DeviceRegRequest): DeviceResponse {
        // Validation scattered
        if (request.name.isBlank()) {
            throw IllegalArgumentException("Name required")
        }
        if (request.serialNumber.isBlank()) {
            throw IllegalArgumentException("Serial required")
        }
        if (!request.serialNumber.matches(Regex("SN\\d{6}"))) {
            throw IllegalArgumentException("Invalid serial format")
        }
        
        // Check user exists
        val user = userRepo.findById(request.userId)
            .orElseThrow { Exception("User not found") }
        
        // Check duplicate serial
        val existing = deviceRepo.findBySerialNumber(request.serialNumber)
        if (existing != null) {
            throw IllegalStateException("Serial number already registered")
        }
        
        // Create device
        val device = DeviceDoc()
        device.id = UUID.randomUUID().toString()
        device.name = request.name
        device.serialNumber = request.serialNumber
        device.status = "PENDING"
        device.userId = request.userId
        device.createdAt = System.currentTimeMillis()
        
        // Save
        val saved = deviceRepo.save(device)
        
        // Side effects
        notificationService.sendDeviceRegistered(saved.id, user.email)
        analyticsService.trackRegistration(saved.id)
        
        // Map to response
        return DeviceResponse(
            id = saved.id,
            name = saved.name,
            status = saved.status
        )
    }
    
    fun activateDevice(deviceId: String, userId: String) {
        // Load
        val device = deviceRepo.findById(deviceId)
            .orElseThrow { Exception("Device not found") }
        
        // Check ownership
        if (device.userId != userId) {
            throw SecurityException("Not authorized")
        }
        
        // Validation
        if (device.status != "PENDING") {
            throw IllegalStateException("Can only activate pending devices")
        }
        
        if (device.feeds.isEmpty()) {
            throw IllegalStateException("Device must have feeds")
        }
        
        // Update
        device.status = "ACTIVE"
        device.activatedAt = System.currentTimeMillis()
        
        // Save
        deviceRepo.save(device)
        
        // Side effects
        notificationService.sendDeviceActivated(device.id)
        analyticsService.trackActivation(device.id)
    }
}
```

**AFTER: DDD Code**

```kotlin
// Domain Model ✅
@JvmInline
value class DeviceId(val value: String) {
    companion object {
        fun generate() = DeviceId(UUID.randomUUID().toString())
    }
}

@JvmInline
value class SerialNumber(val value: String) {
    init {
        require(value.matches(Regex("SN\\d{6}"))) {
            "Serial number must match format SN######"
        }
    }
}

data class Name(val value: String) {
    init {
        require(value.isNotBlank()) { "Name cannot be blank" }
    }
}

enum class DeviceStatus {
    PENDING, ACTIVE, INACTIVE
}

// Rich Entity ✅
class Device private constructor(
    val deviceId: DeviceId,
    private val name: Name,
    private val serialNumber: SerialNumber,
    private var status: DeviceStatus,
    private val feeds: Map<FeedId, Feed>,
    private val ownerId: UserId
) {
    private val _domainEvents = mutableListOf<DeviceEvent>()
    val domainEvents: List<DeviceEvent> get() = _domainEvents.toList()
    
    companion object {
        fun register(
            deviceId: DeviceId,
            name: Name,
            serialNumber: SerialNumber,
            ownerId: UserId
        ): Device {
            val device = Device(
                deviceId = deviceId,
                name = name,
                serialNumber = serialNumber,
                status = DeviceStatus.PENDING,
                feeds = emptyMap(),
                ownerId = ownerId
            )
            
            device.addDomainEvent(
                DeviceRegistered(deviceId, serialNumber, ownerId)
            )
            
            return device
        }
    }
    
    fun activate(): Device {
        require(status == DeviceStatus.PENDING) {
            "Can only activate pending devices"
        }
        require(feeds.isNotEmpty()) {
            "Device must have at least one feed"
        }
        
        val activated = copy(status = DeviceStatus.ACTIVE)
        activated.addDomainEvent(DeviceActivated(deviceId))
        
        return activated
    }
    
    fun isOwnedBy(userId: UserId): Boolean = ownerId == userId
    
    private fun addDomainEvent(event: DeviceEvent) {
        _domainEvents.add(event)
    }
    
    private fun copy(status: DeviceStatus = this.status): Device {
        return Device(deviceId, name, serialNumber, status, feeds, ownerId)
    }
}

// Use Case ✅
@Service
class RegisterDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val eventPublisher: DomainEventPublisher
) {
    fun execute(command: RegisterDeviceCommand): DeviceId {
        // Validation
        checkSerialNumberUnique(command.serialNumber)
        
        // Domain logic
        val device = Device.register(
            deviceId = DeviceId.generate(),
            name = command.name,
            serialNumber = command.serialNumber,
            ownerId = command.ownerId
        )
        
        // Persist
        deviceRepository.save(device)
        
        // Publish events
        device.domainEvents.forEach { event ->
            eventPublisher.publish(event)
        }
        
        return device.deviceId
    }
    
    private fun checkSerialNumberUnique(serialNumber: SerialNumber) {
        val existing = deviceRepository.findBySerialNumber(serialNumber)
        if (existing != null) {
            throw DuplicateSerialNumberException(serialNumber)
        }
    }
}

@Service
class ActivateDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val eventPublisher: DomainEventPublisher
) {
    fun execute(command: ActivateDeviceCommand) {
        val device = deviceRepository.findById(command.deviceId)
            ?: throw DeviceNotFoundException(command.deviceId)
        
        // Authorization
        require(device.isOwnedBy(command.actorId)) {
            "Not authorized to activate this device"
        }
        
        // Domain logic
        val activated = device.activate()
        
        // Persist
        deviceRepository.save(activated)
        
        // Publish events
        activated.domainEvents.forEach { event ->
            eventPublisher.publish(event)
        }
    }
}

// Event Handlers ✅
@ComponentInline
class DeviceRegisteredNotificationHandler(
    private val notificationService: NotificationService
) {
    @EventListener
    fun handle(event: DeviceRegistered) {
        notificationService.sendDeviceRegistered(event.deviceId)
    }
}

@ComponentInline
class DeviceActivatedAnalyticsHandler(
    private val analyticsService: AnalyticsService
) {
    @EventListener
    fun handle(event: DeviceActivated) {
        analyticsService.trackActivation(event.deviceId)
    }
}
```

**Improvements:**
- ✅ Business logic in domain
- ✅ Value objects with validation
- ✅ Rich entities with behavior
- ✅ Thin use cases
- ✅ Domain events for decoupling
- ✅ Type safety
- ✅ Testability
- ✅ Maintainability

---

## 11. Chapter Summary

In this chapter, we've explored how to refactor legacy codebases to DDD incrementally using the Strangler Fig pattern. Rather than risky big-bang rewrites, this approach allows teams to gradually transform anemic models into rich domain-driven designs while maintaining continuous delivery.

### What We Covered

**The Legacy Problem:**
- 50,000 lines of spaghetti code
- 2000-line services with 20+ dependencies
- Business logic scattered everywhere
- Anemic domain models (getters/setters only)
- Primitive obsession
- Features take weeks instead of days

**The Strangler Fig Strategy:**
- Don't rewrite everything at once
- Build new alongside old
- Gradually replace old with new
- Continuous delivery throughout
- Reduce risk, show value early

**Six-Step Refactoring Process:**
1. Identify domain logic in services
2. Extract value objects from primitives
3. Create rich entities with behavior
4. Define aggregate boundaries
5. Implement clean repositories
6. Add domain events for decoupling

### Key Insights

1. **Don't rewrite everything** - Big-bang rewrites fail 80% of the time.

2. **Start with core domain** - Highest business value, highest complexity first.

3. **Extract value objects first** - Remove primitive obsession, add validation.

4. **Move logic to entities** - Fat services become thin, entities become rich.

5. **Define aggregates carefully** - Protect invariants, clear boundaries.

6. **Simplify repositories** - Use specifications, eliminate query explosion.

7. **Add domain events** - Decouple components, enable choreography.

8. **Refactor incrementally** - Small steps, continuous testing, always shippable.

9. **Test extensively** - Safety net enables confident refactoring.

10. **Get buy-in early** - Show improved velocity and reduced bugs quickly.

### Before/After Comparison

**Before (Anemic Model + Fat Service):**
```kotlin
// Anemic entity - 2000-line service ❌
data class Device(
    var id: String,
    var name: String,
    var serialNumber: String,
    var status: String
)

@Service
class DeviceService(
    // 20+ dependencies ❌
) {
    fun registerDevice(request: DeviceRegistrationRequest): DeviceResponse {
        // 500 lines of validation ❌
        if (request.serialNumber.isEmpty()) throw IllegalArgumentException()
        if (request.serialNumber.length < 8) throw IllegalArgumentException()
        // ... 498 more lines
        
        // 500 lines of business logic ❌
        val device = Device()
        device.id = UUID.randomUUID().toString()
        device.name = request.name
        device.serialNumber = request.serialNumber
        device.status = "PENDING"
        // ... 496 more lines
        
        repository.save(device)
        notificationService.sendWelcome()
        analyticsService.track()
        // ...
    }
}
```

**After (Rich Domain Model):**
```kotlin
// Rich entity ✅
class Device private constructor(
    val deviceId: DeviceId,
    private var name: Name,
    private val serialNumber: SerialNumber,
    private var status: DeviceStatus
) {
    companion object {
        fun register(
            serialNumber: SerialNumber,
            name: Name,
            premises: Premises
        ): Device {
            // Validation in value objects ✅
            // Business logic in entity ✅
            return Device(
                deviceId = DeviceId.generate(),
                name = name,
                serialNumber = serialNumber,
                status = DeviceStatus.PENDING
            )
        }
    }
    
    fun activate(): Device {
        require(status == DeviceStatus.PENDING) {
            "Can only activate pending devices"
        }
        return copy(status = DeviceStatus.ACTIVE)
    }
}

// Thin service ✅
@Service
class RegisterDeviceUseCase(
    private val repository: DeviceRepository,
    private val eventPublisher: EventPublisher
) {
    fun execute(command: RegisterDeviceCommand): Device {
        val device = Device.register(
            serialNumber = SerialNumber(command.serialNumber),
            name = Name(command.name),
            premises = repository.findPremises(command.premisesId)
        )
        
        repository.save(device)
        eventPublisher.publish(DeviceRegistered(device.deviceId))
        
        return device
    }
}
```

**Impact:**
- 2000 lines → 100 lines (95% reduction!)
- 20 dependencies → 2 dependencies
- Business logic in entities (testable!)
- Thin, focused services
- Features in days, not weeks

### Six-Step Refactoring Process

**Step 1: Identify Domain Logic**
```kotlin
// Find business rules in services
if (device.status == "PENDING") {  // Business rule!
    device.status = "ACTIVE"
}

// Move to entity
fun activate(): Device {
    require(status == DeviceStatus.PENDING)
    return copy(status = DeviceStatus.ACTIVE)
}
```

**Step 2: Extract Value Objects**
```kotlin
// Before: Primitive obsession ❌
val serialNumber: String  // No validation!

// After: Value object ✅
data class SerialNumber(val value: String) {
    init {
        require(value.length in 8..20) {
            "Serial number must be 8-20 characters"
        }
        require(value.matches(Regex("[A-Z0-9-]+"))) {
            "Serial number must be alphanumeric with hyphens"
        }
    }
}
```

**Step 3: Create Rich Entities**
```kotlin
// Before: Anemic ❌
data class Device(
    var status: String
)

// After: Rich ✅
class Device private constructor(
    private var status: DeviceStatus
) {
    fun activate(): Device {
        require(status == DeviceStatus.PENDING)
        return copy(status = DeviceStatus.ACTIVE)
    }
    
    fun isActive(): Boolean = status == DeviceStatus.ACTIVE
}
```

**Step 4: Define Aggregates**
```kotlin
// Device is aggregate root
class Device {
    private val feeds: Map<FeedId, Feed>  // Internal entities
    
    fun addFeed(feed: Feed): Device {
        // Validate invariant
        require(feeds.size < 10) { "Max 10 feeds per device" }
        return copy(feeds = feeds + (feed.feedId to feed))
    }
}

// No FeedRepository! Access through Device
deviceRepository.findById(deviceId)
    .map { it.addFeed(newFeed) }
    .map { deviceRepository.save(it) }
```

**Step 5: Implement Repositories**
```kotlin
// Before: 50 query methods ❌
interface DeviceRepository {
    fun findActiveDevices(): List<Device>
    fun findInactiveDevices(): List<Device>
    fun findActiveSensors(): List<Device>
    // ... 47 more
}

// After: Specification pattern ✅
interface DeviceRepository {
    fun findAll(spec: Specification<Device>): List<Device>
    fun save(device: Device): Device
}

// Usage
val devices = repository.findAll(
    ActiveDeviceSpec().and(SensorDeviceSpec())
)
```

**Step 6: Add Domain Events**
```kotlin
// Before: Tight coupling ❌
fun activateDevice(deviceId: String) {
    device.status = "ACTIVE"
    repository.save(device)
    notificationService.send(...)  // Coupled!
    analyticsService.track(...)    // Coupled!
}

// After: Events decouple ✅
fun activateDevice(deviceId: DeviceId): Device {
    val activated = device.activate()
    repository.save(activated)
    eventPublisher.publish(DeviceActivated(deviceId))
    return activated
}

// Handlers react independently
@EventListener
class SendActivationNotificationHandler {
    fun handle(event: DeviceActivated) {
        notificationService.send(...)
    }
}
```

### Refactoring Metrics

**Code Quality Improvements:**
- Service size: 2000 lines → 100 lines (95% reduction)
- Service dependencies: 20 → 2 (90% reduction)
- Cyclomatic complexity: 150 → 5 (97% reduction)
- Test coverage: 30% → 85% (183% improvement)

**Business Impact:**
- Feature velocity: 2 weeks → 2 days (7x faster)
- Bug rate: 15/month → 3/month (80% reduction)
- Onboarding time: 3 months → 2 weeks (6x faster)
- Production incidents: 10/month → 2/month (80% reduction)

### Strangler Fig Timeline

**Month 1-2: Assessment & Planning**
- Code smell analysis
- Identify core domain
- Create domain model sketch
- Get team buy-in

**Month 3-4: First Aggregate**
- Extract value objects
- Create rich entity
- Implement repository
- Add tests
- Deploy incrementally

**Month 5-8: Core Domain**
- Refactor 3-5 key aggregates
- Add domain events
- Remove old code paths
- Measure improvements

**Month 9-12: Supporting Subdomains**
- Refactor remaining areas
- Complete event-driven decoupling
- Remove legacy code
- Document patterns

**Total: 12-18 months for complete migration**

### Testing Strategy

**Test Before Refactoring:**
```kotlin
@Test
fun `should register device`() {
    // Characterization test
    val result = oldService.registerDevice(request)
    
    assertEquals("PENDING", result.status)
    // Capture current behavior
}
```

**Test After Refactoring:**
```kotlin
@Test
fun `should register device with valid serial number`() {
    val device = Device.register(
        serialNumber = SerialNumber("ABC-123"),
        name = Name("Sensor"),
        premises = premises
    )
    
    assertEquals(DeviceStatus.PENDING, device.status)
    assertEquals(SerialNumber("ABC-123"), device.serialNumber)
}

@Test
fun `should not register device with invalid serial`() {
    assertThrows<IllegalArgumentException> {
        SerialNumber("AB")  // Too short
    }
}
```

### Measured Benefits

Teams refactoring to DDD see:
- **7x faster** feature development
- **80% reduction** in bugs
- **95% reduction** in service size
- **85% test coverage** (from 30%)
- **6x faster** onboarding
- **80% fewer** production incidents

### Practice Exercise

Refactor your codebase:

1. **Run assessment** - Use code smell checklist
2. **Identify core domain** - Where's the most value?
3. **Start small** - Pick one entity (e.g., Order, Device)
4. **Extract value objects** - Email, Money, SerialNumber
5. **Add behavior to entity** - Move validation and logic
6. **Write tests** - Characterization tests, then unit tests
7. **Define aggregate** - What entities belong together?
8. **Simplify repository** - Remove query explosion
9. **Add events** - Decouple side effects
10. **Measure improvement** - Lines, bugs, velocity

### Design Checklist

When refactoring to DDD:
- ✅ Characterization tests before refactoring
- ✅ Value objects extracted (no primitive obsession)
- ✅ Business logic in entities (not services)
- ✅ Aggregates defined (clear boundaries)
- ✅ Repositories simplified (specifications used)
- ✅ Domain events added (decoupling achieved)
- ✅ Services thin (orchestration only)
- ✅ Tests comprehensive (85%+ coverage)
- ✅ Incremental deployment (Strangler Fig)
- ✅ Metrics tracked (velocity, bugs, size)

---

### Additional Reading

For deeper understanding of refactoring:
- **"Refactoring"** by Martin Fowler (2018) - Comprehensive refactoring catalog
- **"Working Effectively with Legacy Code"** by Michael Feathers (2004) - Essential techniques
- **"Refactoring to Patterns"** by Joshua Kerievsky (2004) - Pattern-based refactoring

---

## What's Next

In **Chapter 18**, we'll explore Testing DDD Applications comprehensively. You'll learn:
- Testing value objects and entities
- Testing aggregates and invariants
- Testing domain events
- Integration testing strategies
- Test data builders
- Property-based testing
- Complete testing examples from SmartHome Hub

With refactored domain models, comprehensive testing will ensure correctness and enable confident evolution.

Turn the page to master DDD testing...

