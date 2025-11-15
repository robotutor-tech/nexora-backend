# Chapter 5: Specification Pattern - Taming the Query Beast

**Series:** Building an Enterprise IoT Platform with DDD  
**Part:** 2 of 20 - Tactical Patterns  
**Reading Time:** 22 minutes  
**Level:** Intermediate  

---

## 📋 Table of Contents

1. [The Problem: Query Explosion](#the-problem)
2. [What is the Specification Pattern?](#what-is-specification)
3. [Basic Implementation](#basic-implementation)
4. [Real-World Examples from SmartHome Hub](#real-examples)
5. [Combining Specifications](#combining-specifications)
6. [Repository Integration](#repository-integration)
7. [Advanced Patterns](#advanced-patterns)
8. [Common Pitfalls](#pitfalls)
9. [Testing Specifications](#testing)

---

## <a name="the-problem"></a>1. The Problem: Query Explosion

### The Scenario: The Method Explosion Nightmare

You're working on SmartHome Hub when the product manager asks for a simple feature:

> **Feature Request:** "Users need to filter devices by various criteria: status, type, zone, last seen time, feed count, etc. And they want to combine these filters."

You think: *"Easy! Just add some query methods."*

A week later, your DeviceRepository looks like this:

```kotlin
// DeviceRepository.kt - The Horror Show ❌
interface DeviceRepository {
    fun findAll(): List<Device>
    fun findById(deviceId: DeviceId): Device?
    fun findByPremisesId(premisesId: PremisesId): List<Device>
    
    // Filter by status
    fun findActiveDevices(premisesId: PremisesId): List<Device>
    fun findInactiveDevices(premisesId: PremisesId): List<Device>
    fun findDeletedDevices(premisesId: PremisesId): List<Device>
    
    // Filter by type
    fun findSensorDevices(premisesId: PremisesId): List<Device>
    fun findActuatorDevices(premisesId: PremisesId): List<Device>
    
    // Filter by zone
    fun findDevicesByZone(premisesId: PremisesId, zoneId: ZoneId): List<Device>
    
    // Combinations start...
    fun findActiveDevicesByZone(premisesId: PremisesId, zoneId: ZoneId): List<Device>
    fun findActiveSensorDevices(premisesId: PremisesId): List<Device>
    fun findActiveSensorDevicesByZone(premisesId: PremisesId, zoneId: ZoneId): List<Device>
    fun findInactiveSensorDevices(premisesId: PremisesId): List<Device>
    fun findInactiveSensorDevicesByZone(premisesId: PremisesId, zoneId: ZoneId): List<Device>
    fun findActiveActuatorDevices(premisesId: PremisesId): List<Device>
    fun findActiveActuatorDevicesByZone(premisesId: PremisesId, zoneId: ZoneId): List<Device>
    
    // Time-based filters
    fun findDevicesSeenInLastHour(premisesId: PremisesId): List<Device>
    fun findDevicesSeenInLastDay(premisesId: PremisesId): List<Device>
    fun findDevicesNotSeenSince(premisesId: PremisesId, since: Instant): List<Device>
    
    // More combinations...
    fun findActiveDevicesSeenInLastHour(premisesId: PremisesId): List<Device>
    fun findActiveSensorDevicesSeenInLastHour(premisesId: PremisesId): List<Device>
    fun findActiveSensorDevicesByZoneSeenInLastHour(
        premisesId: PremisesId, 
        zoneId: ZoneId
    ): List<Device>
    
    // Feed-based filters
    fun findDevicesWithNoFeeds(premisesId: PremisesId): List<Device>
    fun findDevicesWithFeeds(premisesId: PremisesId): List<Device>
    fun findDevicesWithFeedCount(premisesId: PremisesId, count: Int): List<Device>
    
    // Even more combinations...
    fun findActiveDevicesWithNoFeeds(premisesId: PremisesId): List<Device>
    fun findActiveSensorDevicesWithNoFeeds(premisesId: PremisesId): List<Device>
    
    // Health-based
    fun findOnlineDevices(premisesId: PremisesId): List<Device>
    fun findOfflineDevices(premisesId: PremisesId): List<Device>
    fun findActiveOnlineDevices(premisesId: PremisesId): List<Device>
    fun findActiveOnlineSensorDevices(premisesId: PremisesId): List<Device>
    fun findActiveOnlineSensorDevicesByZone(premisesId: PremisesId, zoneId: ZoneId): List<Device>
    
    // And it keeps growing... 50+ methods and counting! 💥
}
```

### The Implementation Nightmare

Each method needs implementation:

```kotlin
// MongoDeviceRepository.kt - Duplicate queries everywhere ❌
@Repository
class MongoDeviceRepository : DeviceRepository {
    
    override fun findActiveDevices(premisesId: PremisesId): List<Device> {
        val query = Query()
            .addCriteria(Criteria.where("premisesId").`is`(premisesId.value))
            .addCriteria(Criteria.where("status").`is`("ACTIVE"))
        
        return mongoTemplate.find(query, DeviceDocument::class.java)
            .map { it.toDomain() }
    }
    
    override fun findActiveSensorDevices(premisesId: PremisesId): List<Device> {
        val query = Query()
            .addCriteria(Criteria.where("premisesId").`is`(premisesId.value))
            .addCriteria(Criteria.where("status").`is`("ACTIVE"))
            .addCriteria(Criteria.where("type").`is`("SENSOR"))  // One more criteria
        
        return mongoTemplate.find(query, DeviceDocument::class.java)
            .map { it.toDomain() }
    }
    
    override fun findActiveSensorDevicesByZone(
        premisesId: PremisesId, 
        zoneId: ZoneId
    ): List<Device> {
        val query = Query()
            .addCriteria(Criteria.where("premisesId").`is`(premisesId.value))
            .addCriteria(Criteria.where("status").`is`("ACTIVE"))
            .addCriteria(Criteria.where("type").`is`("SENSOR"))
            .addCriteria(Criteria.where("zoneId").`is`(zoneId.value))  // Another one
        
        return mongoTemplate.find(query, DeviceDocument::class.java)
            .map { it.toDomain() }
    }
    
    // 47 more methods to go... 😱
}
```

### The Real Cost

**Problems identified:**

1. **Method Explosion** - 50+ repository methods
2. **Code Duplication** - Same query logic repeated everywhere
3. **Maintenance Nightmare** - Change criteria format? Update 50+ methods
4. **Testing Hell** - Need to test every combination
5. **Poor Flexibility** - Can't create new combinations without new methods
6. **Business Logic Leakage** - Query criteria scattered across repository

**New feature request arrives:**

> "Add filter by last maintenance date"

**Impact:**
- 25 new methods needed (all combinations)
- 25 new implementations
- 25 new tests
- 1 week of work for one filter! 💥

**The team realizes:** *"We need a better way!"*

---

## <a name="what-is-specification"></a>2. What is the Specification Pattern?

### Definition

> **Specification Pattern:** A pattern that encapsulates business rules into reusable, composable objects that can be evaluated against entities or used to build queries.
> 
> — Eric Evans & Martin Fowler

### The Core Idea

Instead of 50 repository methods, we have:
- **Reusable specifications** that encapsulate criteria
- **Composable logic** that combines specifications
- **One generic method** in repository

```kotlin
// The transformation
// FROM: 50+ specific methods ❌
fun findActiveSensorDevicesByZoneSeenInLastHour(...)

// TO: One flexible method + specifications ✅
fun findAll(specification: Specification<Device>): List<Device>

// Usage
val spec = DeviceIsActive()
    .and(DeviceIsSensor())
    .and(DeviceInZone(zoneId))
    .and(DeviceSeenInLastHour())

val devices = deviceRepository.findAll(spec)
```

### The Three Uses of Specifications

#### 1. In-Memory Validation

Test if an entity satisfies a condition:

```kotlin
val spec = DeviceIsActive()
if (spec.isSatisfiedBy(device)) {
    println("Device is active")
}
```

#### 2. Query Building

Build database queries:

```kotlin
val spec = DeviceIsActive().and(DeviceIsSensor())
val query = spec.toQuery()  // Converts to MongoDB query
val devices = mongoTemplate.find(query, DeviceDocument::class.java)
```

#### 3. Business Rules

Express complex business logic:

```kotlin
class DeviceCanBeActivatedSpec : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getStatus() == DeviceStatus.PENDING &&
               device.getFeeds().isNotEmpty() &&
               device.isRegisteredWithinLast24Hours()
    }
}
```

---

## <a name="basic-implementation"></a>3. Basic Implementation

### The Specification Interface

```kotlin
// Specification.kt - Core interface
interface Specification<T> {
    /**
     * Check if entity satisfies this specification
     */
    fun isSatisfiedBy(entity: T): Boolean
    
    /**
     * Combine with another specification using AND
     */
    fun and(other: Specification<T>): Specification<T> {
        return AndSpecification(this, other)
    }
    
    /**
     * Combine with another specification using OR
     */
    fun or(other: Specification<T>): Specification<T> {
        return OrSpecification(this, other)
    }
    
    /**
     * Negate this specification
     */
    fun not(): Specification<T> {
        return NotSpecification(this)
    }
}
```

### Composite Specifications

```kotlin
// AndSpecification.kt
class AndSpecification<T>(
    private val left: Specification<T>,
    private val right: Specification<T>
) : Specification<T> {
    
    override fun isSatisfiedBy(entity: T): Boolean {
        return left.isSatisfiedBy(entity) && right.isSatisfiedBy(entity)
    }
}

// OrSpecification.kt
class OrSpecification<T>(
    private val left: Specification<T>,
    private val right: Specification<T>
) : Specification<T> {
    
    override fun isSatisfiedBy(entity: T): Boolean {
        return left.isSatisfiedBy(entity) || right.isSatisfiedBy(entity)
    }
}

// NotSpecification.kt
class NotSpecification<T>(
    private val specification: Specification<T>
) : Specification<T> {
    
    override fun isSatisfiedBy(entity: T): Boolean {
        return !specification.isSatisfiedBy(entity)
    }
}
```

### Simple Specification Example

```kotlin
// DeviceIsActiveSpec.kt
class DeviceIsActive : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getStatus() == DeviceStatus.ACTIVE
    }
}

// Usage
val device = deviceRepository.findById(deviceId)
val spec = DeviceIsActive()

if (spec.isSatisfiedBy(device)) {
    println("Device is active!")
}
```

---

## <a name="real-examples"></a>4. Real-World Examples from SmartHome Hub

### Device Specifications

#### Specification 1: Device Status

```kotlin
// DeviceIsActiveSpec.kt
class DeviceIsActive : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getStatus() == DeviceStatus.ACTIVE
    }
}

// DeviceIsInactiveSpec.kt
class DeviceIsInactive : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getStatus() == DeviceStatus.INACTIVE
    }
}

// DeviceIsDeletedSpec.kt
class DeviceIsDeleted : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getStatus() == DeviceStatus.DELETED
    }
}

// DeviceHasStatusSpec.kt - Parameterized
class DeviceHasStatus(private val status: DeviceStatus) : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getStatus() == status
    }
}
```

#### Specification 2: Device Type

```kotlin
// DeviceIsSensorSpec.kt
class DeviceIsSensor : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getType() == DeviceType.SENSOR
    }
}

// DeviceIsActuatorSpec.kt
class DeviceIsActuator : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getType() == DeviceType.ACTUATOR
    }
}

// DeviceHasTypeSpec.kt - Parameterized
class DeviceHasType(private val type: DeviceType) : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getType() == type
    }
}
```

#### Specification 3: Device Zone

```kotlin
// DeviceInZoneSpec.kt
class DeviceInZone(private val zoneId: ZoneId) : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getZoneId() == zoneId
    }
}

// DeviceNotInAnyZoneSpec.kt
class DeviceNotInAnyZone : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getZoneId() == null
    }
}
```

#### Specification 4: Device Health

```kotlin
// DeviceIsOnlineSpec.kt
class DeviceIsOnline : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getHealth() == DeviceHealth.ONLINE
    }
}

// DeviceIsOfflineSpec.kt
class DeviceIsOffline : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getHealth() == DeviceHealth.OFFLINE
    }
}

// DeviceIsResponsiveSpec.kt - Time-based
class DeviceIsResponsive(
    private val timeout: Duration = Duration.ofMinutes(5)
) : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.isResponsive(timeout)
    }
}
```

#### Specification 5: Device Feeds

```kotlin
// DeviceHasFeedsSpec.kt
class DeviceHasFeeds : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getFeeds().isNotEmpty()
    }
}

// DeviceHasNoFeedsSpec.kt
class DeviceHasNoFeeds : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getFeeds().isEmpty()
    }
}

// DeviceHasFeedCountSpec.kt
class DeviceHasFeedCount(private val count: Int) : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getFeeds().size == count
    }
}

// DeviceHasMinimumFeedsSpec.kt
class DeviceHasMinimumFeeds(private val minimum: Int) : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getFeeds().size >= minimum
    }
}
```

#### Specification 6: Time-Based

```kotlin
// DeviceSeenInLastHourSpec.kt
class DeviceSeenInLastHour : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        val lastSeen = device.getLastSeenAt() ?: return false
        val hourAgo = Instant.now().minus(Duration.ofHours(1))
        return lastSeen.isAfter(hourAgo)
    }
}

// DeviceSeenSinceSpec.kt - Parameterized
class DeviceSeenSince(private val since: Instant) : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        val lastSeen = device.getLastSeenAt() ?: return false
        return lastSeen.isAfter(since)
    }
}

// DeviceNotSeenSinceSpec.kt
class DeviceNotSeenSince(private val since: Instant) : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        val lastSeen = device.getLastSeenAt() ?: return true
        return lastSeen.isBefore(since)
    }
}

// DeviceRegisteredWithinSpec.kt
class DeviceRegisteredWithin(
    private val duration: Duration = Duration.ofHours(24)
) : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        val threshold = Instant.now().minus(duration)
        return device.getCreatedAt().isAfter(threshold)
    }
}
```

#### Specification 7: Complex Business Rules

```kotlin
// DeviceCanBeActivatedSpec.kt - Combines multiple rules
class DeviceCanBeActivated : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getStatus() == DeviceStatus.PENDING &&
               device.getFeeds().isNotEmpty() &&
               device.isRegisteredWithinLast24Hours()
    }
}

// DeviceRequiresMaintenanceSpec.kt
class DeviceRequiresMaintenance : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        // Device requires maintenance if:
        // 1. Not seen in last 7 days, OR
        // 2. Has more than 5 error events in last 24 hours, OR
        // 3. Battery level below 10% (if battery-powered)
        
        val notSeenInWeek = device.getLastSeenAt()?.let {
            Duration.between(it, Instant.now()).toDays() >= 7
        } ?: true
        
        if (notSeenInWeek) return true
        
        // Additional checks would go here...
        return false
    }
}

// DeviceEligibleForAutomationSpec.kt
class DeviceEligibleForAutomation : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getStatus() == DeviceStatus.ACTIVE &&
               device.getHealth() == DeviceHealth.ONLINE &&
               device.getFeeds().isNotEmpty() &&
               device.getType() in listOf(DeviceType.SENSOR, DeviceType.ACTUATOR)
    }
}
```

---

## <a name="combining-specifications"></a>5. Combining Specifications

### Basic Combinations

```kotlin
// AND combination
val activeAndSensor = DeviceIsActive().and(DeviceIsSensor())

// OR combination
val sensorOrActuator = DeviceIsSensor().or(DeviceIsActuator())

// NOT combination
val notDeleted = DeviceIsDeleted().not()

// Complex combination
val eligibleDevices = DeviceIsActive()
    .and(DeviceIsSensor())
    .and(DeviceInZone(zoneId))
    .and(DeviceIsOnline())
```

### Real-World Combinations

#### Example 1: Find Devices Needing Attention

```kotlin
fun findDevicesNeedingAttention(premisesId: PremisesId): List<Device> {
    val spec = DeviceIsActive()
        .and(
            DeviceIsOffline()
                .or(DeviceNotSeenSince(Instant.now().minus(Duration.ofHours(24))))
                .or(DeviceHasNoFeeds())
        )
    
    return deviceRepository.findAll(premisesId, spec)
}
```

#### Example 2: Find Devices Ready for Automation

```kotlin
fun findDevicesReadyForAutomation(premisesId: PremisesId, zoneId: ZoneId): List<Device> {
    val spec = DeviceIsActive()
        .and(DeviceIsOnline())
        .and(DeviceInZone(zoneId))
        .and(DeviceHasFeeds())
        .and(
            DeviceIsSensor().or(DeviceIsActuator())
        )
    
    return deviceRepository.findAll(premisesId, spec)
}
```

#### Example 3: Find Stale Devices

```kotlin
fun findStaleDevices(premisesId: PremisesId): List<Device> {
    val oneWeekAgo = Instant.now().minus(Duration.ofDays(7))
    
    val spec = DeviceIsActive()
        .and(DeviceNotSeenSince(oneWeekAgo))
        .and(DeviceIsDeleted().not())  // Exclude deleted
    
    return deviceRepository.findAll(premisesId, spec)
}
```

#### Example 4: Find Problematic Sensors

```kotlin
fun findProblematicSensors(premisesId: PremisesId): List<Device> {
    val spec = DeviceIsSensor()
        .and(DeviceIsActive())
        .and(
            // Either offline OR not seen recently OR no feeds
            DeviceIsOffline()
                .or(DeviceSeenInLastHour().not())
                .or(DeviceHasNoFeeds())
        )
    
    return deviceRepository.findAll(premisesId, spec)
}
```

### Reusable Specification Builders

Create common combinations:

```kotlin
// DeviceSpecifications.kt - Factory for common specs
object DeviceSpecifications {
    
    fun healthyDevices(): Specification<Device> {
        return DeviceIsActive()
            .and(DeviceIsOnline())
            .and(DeviceHasFeeds())
            .and(DeviceSeenInLastHour())
    }
    
    fun unhealthyDevices(): Specification<Device> {
        return DeviceIsActive()
            .and(
                DeviceIsOffline()
                    .or(DeviceSeenInLastHour().not())
                    .or(DeviceHasNoFeeds())
            )
    }
    
    fun eligibleForAutomation(): Specification<Device> {
        return DeviceIsActive()
            .and(DeviceIsOnline())
            .and(DeviceHasFeeds())
            .and(
                DeviceIsSensor().or(DeviceIsActuator())
            )
    }
    
    fun requiresMaintenance(): Specification<Device> {
        val oneWeekAgo = Instant.now().minus(Duration.ofDays(7))
        
        return DeviceIsActive()
            .and(DeviceNotSeenSince(oneWeekAgo))
    }
    
    fun inZoneAndType(zoneId: ZoneId, type: DeviceType): Specification<Device> {
        return DeviceInZone(zoneId).and(DeviceHasType(type))
    }
}

// Usage
val healthy = deviceRepository.findAll(premisesId, DeviceSpecifications.healthyDevices())
val needMaintenance = deviceRepository.findAll(premisesId, DeviceSpecifications.requiresMaintenance())
```

---

## <a name="repository-integration"></a>6. Repository Integration

### Enhanced Repository Interface

```kotlin
// DeviceRepository.kt - Now clean and flexible ✅
interface DeviceRepository {
    // Basic CRUD
    fun save(device: Device): Device
    fun findById(deviceId: DeviceId): Device?
    fun delete(deviceId: DeviceId)
    
    // Find by specification - ONE METHOD FOR ALL QUERIES ✅
    fun findAll(
        premisesId: PremisesId, 
        specification: Specification<Device>
    ): List<Device>
    
    // Convenience method without specification
    fun findAll(premisesId: PremisesId): List<Device> {
        return findAll(premisesId, AllDevicesSpec())
    }
    
    // Count with specification
    fun count(
        premisesId: PremisesId,
        specification: Specification<Device>
    ): Long
    
    // Check if any match
    fun exists(
        premisesId: PremisesId,
        specification: Specification<Device>
    ): Boolean
}
```

### Query-Building Specifications

For database queries, extend the specification:

```kotlin
// QuerySpecification.kt
interface QuerySpecification<T> : Specification<T> {
    /**
     * Convert to database query (MongoDB in this case)
     */
    fun toQuery(): Query
}

// Example implementation
class DeviceIsActive : QuerySpecification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getStatus() == DeviceStatus.ACTIVE
    }
    
    override fun toQuery(): Query {
        return Query().addCriteria(
            Criteria.where("status").`is`("ACTIVE")
        )
    }
}
```

### Repository Implementation

```kotlin
// MongoDeviceRepository.kt
@Repository
class MongoDeviceRepository(
    private val mongoTemplate: MongoTemplate
) : DeviceRepository {
    
    override fun findAll(
        premisesId: PremisesId,
        specification: Specification<Device>
    ): List<Device> {
        // Start with premises filter
        val query = Query()
            .addCriteria(Criteria.where("premisesId").`is`(premisesId.value))
        
        // Add specification criteria if it's a query specification
        if (specification is QuerySpecification) {
            val specQuery = specification.toQuery()
            specQuery.queryObject.forEach { (key, value) ->
                query.addCriteria(Criteria.where(key).`is`(value))
            }
        }
        
        // Execute query
        val documents = mongoTemplate.find(query, DeviceDocument::class.java)
        val devices = documents.map { it.toDomain() }
        
        // Apply in-memory specification filter (for complex specs)
        return devices.filter { specification.isSatisfiedBy(it) }
    }
    
    override fun count(
        premisesId: PremisesId,
        specification: Specification<Device>
    ): Long {
        return findAll(premisesId, specification).size.toLong()
    }
    
    override fun exists(
        premisesId: PremisesId,
        specification: Specification<Device>
    ): Boolean {
        return findAll(premisesId, specification).isNotEmpty()
    }
}
```

### Composite Query Specifications

Handle AND/OR/NOT in queries:

```kotlin
// AndQuerySpecification.kt
class AndQuerySpecification<T>(
    private val left: QuerySpecification<T>,
    private val right: QuerySpecification<T>
) : QuerySpecification<T> {
    
    override fun isSatisfiedBy(entity: T): Boolean {
        return left.isSatisfiedBy(entity) && right.isSatisfiedBy(entity)
    }
    
    override fun toQuery(): Query {
        val query = Query()
        
        // Merge criteria from both specifications
        val leftQuery = left.toQuery()
        val rightQuery = right.toQuery()
        
        leftQuery.queryObject.forEach { (key, value) ->
            query.addCriteria(Criteria.where(key).`is`(value))
        }
        
        rightQuery.queryObject.forEach { (key, value) ->
            query.addCriteria(Criteria.where(key).`is`(value))
        }
        
        return query
    }
}
```

---

## <a name="advanced-patterns"></a>7. Advanced Patterns

### Pattern 1: Parameterized Specifications with Builder

```kotlin
// DeviceFilterSpec.kt - Builder pattern
class DeviceFilterSpec private constructor(
    private val statuses: List<DeviceStatus>,
    private val types: List<DeviceType>,
    private val zoneIds: List<ZoneId>,
    private val healthStatuses: List<DeviceHealth>,
    private val minFeeds: Int?,
    private val maxFeeds: Int?,
    private val seenSince: Instant?,
    private val registeredSince: Instant?
) : Specification<Device> {
    
    override fun isSatisfiedBy(device: Device): Boolean {
        if (statuses.isNotEmpty() && device.getStatus() !in statuses) return false
        if (types.isNotEmpty() && device.getType() !in types) return false
        if (zoneIds.isNotEmpty() && device.getZoneId() !in zoneIds) return false
        if (healthStatuses.isNotEmpty() && device.getHealth() !in healthStatuses) return false
        
        minFeeds?.let { if (device.getFeeds().size < it) return false }
        maxFeeds?.let { if (device.getFeeds().size > it) return false }
        
        seenSince?.let { threshold ->
            val lastSeen = device.getLastSeenAt() ?: return false
            if (lastSeen.isBefore(threshold)) return false
        }
        
        registeredSince?.let { threshold ->
            if (device.getCreatedAt().isBefore(threshold)) return false
        }
        
        return true
    }
    
    class Builder {
        private var statuses = mutableListOf<DeviceStatus>()
        private var types = mutableListOf<DeviceType>()
        private var zoneIds = mutableListOf<ZoneId>()
        private var healthStatuses = mutableListOf<DeviceHealth>()
        private var minFeeds: Int? = null
        private var maxFeeds: Int? = null
        private var seenSince: Instant? = null
        private var registeredSince: Instant? = null
        
        fun withStatus(status: DeviceStatus) = apply { statuses.add(status) }
        fun withStatuses(vararg statuses: DeviceStatus) = apply { 
            this.statuses.addAll(statuses) 
        }
        
        fun withType(type: DeviceType) = apply { types.add(type) }
        fun withTypes(vararg types: DeviceType) = apply { 
            this.types.addAll(types) 
        }
        
        fun inZone(zoneId: ZoneId) = apply { zoneIds.add(zoneId) }
        fun inZones(vararg zoneIds: ZoneId) = apply { 
            this.zoneIds.addAll(zoneIds) 
        }
        
        fun withHealth(health: DeviceHealth) = apply { 
            healthStatuses.add(health) 
        }
        
        fun withMinFeeds(count: Int) = apply { minFeeds = count }
        fun withMaxFeeds(count: Int) = apply { maxFeeds = count }
        
        fun seenSince(instant: Instant) = apply { seenSince = instant }
        fun registeredSince(instant: Instant) = apply { registeredSince = instant }
        
        fun build(): DeviceFilterSpec {
            return DeviceFilterSpec(
                statuses, types, zoneIds, healthStatuses,
                minFeeds, maxFeeds, seenSince, registeredSince
            )
        }
    }
    
    companion object {
        fun builder() = Builder()
    }
}

// Usage - Very readable!
val spec = DeviceFilterSpec.builder()
    .withStatuses(DeviceStatus.ACTIVE, DeviceStatus.INACTIVE)
    .withType(DeviceType.SENSOR)
    .inZone(zoneId)
    .withHealth(DeviceHealth.ONLINE)
    .withMinFeeds(1)
    .seenSince(Instant.now().minus(Duration.ofHours(1)))
    .build()

val devices = deviceRepository.findAll(premisesId, spec)
```

### Pattern 2: Specification with Context

Pass additional context for evaluation:

```kotlin
// ContextualSpecification.kt
interface ContextualSpecification<T, C> {
    fun isSatisfiedBy(entity: T, context: C): Boolean
}

// DeviceAccessibleByActorSpec.kt
class DeviceAccessibleByActor(
    private val actorId: ActorId
) : ContextualSpecification<Device, ActorPermissions> {
    
    override fun isSatisfiedBy(device: Device, context: ActorPermissions): Boolean {
        return context.canAccess(actorId, device.deviceId)
    }
}

// Usage
val spec = DeviceAccessibleByActor(actorId)
val permissions = permissionService.getPermissions(actorId)
val accessibleDevices = devices.filter { spec.isSatisfiedBy(it, permissions) }
```

### Pattern 3: Cached Specifications

For expensive checks:

```kotlin
// CachedSpecification.kt
class CachedSpecification<T>(
    private val specification: Specification<T>,
    private val cacheDuration: Duration = Duration.ofMinutes(5)
) : Specification<T> {
    
    private val cache = ConcurrentHashMap<T, Pair<Boolean, Instant>>()
    
    override fun isSatisfiedBy(entity: T): Boolean {
        val cached = cache[entity]
        
        // Return cached result if still valid
        if (cached != null) {
            val (result, timestamp) = cached
            if (Duration.between(timestamp, Instant.now()) < cacheDuration) {
                return result
            }
        }
        
        // Evaluate and cache
        val result = specification.isSatisfiedBy(entity)
        cache[entity] = Pair(result, Instant.now())
        
        return result
    }
}

// Usage - Expensive checks cached
val expensiveSpec = DeviceHasValidCertificate() // Calls external service
val cachedSpec = CachedSpecification(expensiveSpec, Duration.ofMinutes(10))

devices.filter { cachedSpec.isSatisfiedBy(it) }
```

### Pattern 4: Specification with Explanation

For debugging and user feedback:

```kotlin
// ExplainableSpecification.kt
interface ExplainableSpecification<T> : Specification<T> {
    /**
     * Explain why entity satisfied or failed the specification
     */
    fun explain(entity: T): String
}

// DeviceIsHealthySpec.kt
class DeviceIsHealthy : ExplainableSpecification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getStatus() == DeviceStatus.ACTIVE &&
               device.getHealth() == DeviceHealth.ONLINE &&
               device.getFeeds().isNotEmpty() &&
               device.isResponsive(Duration.ofMinutes(5))
    }
    
    override fun explain(device: Device): String {
        val reasons = mutableListOf<String>()
        
        if (device.getStatus() != DeviceStatus.ACTIVE) {
            reasons.add("Device is not active (status: ${device.getStatus()})")
        }
        
        if (device.getHealth() != DeviceHealth.ONLINE) {
            reasons.add("Device is not online (health: ${device.getHealth()})")
        }
        
        if (device.getFeeds().isEmpty()) {
            reasons.add("Device has no feeds")
        }
        
        if (!device.isResponsive(Duration.ofMinutes(5))) {
            reasons.add("Device not responsive (last seen: ${device.getLastSeenAt()})")
        }
        
        return if (reasons.isEmpty()) {
            "Device is healthy"
        } else {
            "Device is not healthy: ${reasons.joinToString(", ")}"
        }
    }
}

// Usage
val spec = DeviceIsHealthy()
val explanation = spec.explain(device)
println(explanation)
// Output: "Device is not healthy: Device is not online (health: OFFLINE), Device not responsive (last seen: 2025-11-10T10:00:00Z)"
```

---

## <a name="pitfalls"></a>8. Common Pitfalls

### Pitfall 1: Overly Complex Specifications

**Problem:**

```kotlin
// ❌ Too complex - hard to understand
class SuperComplexDeviceSpec : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return (device.getStatus() == DeviceStatus.ACTIVE || 
                device.getStatus() == DeviceStatus.INACTIVE) &&
               (device.getType() == DeviceType.SENSOR || 
                device.getType() == DeviceType.ACTUATOR) &&
               device.getFeeds().isNotEmpty() &&
               device.getFeeds().any { it.getValue() > 0 } &&
               (device.getZoneId() != null || device.getFeeds().size > 5) &&
               // ... 20 more conditions
               true
    }
}
```

**Solution:**

```kotlin
// ✅ Break into smaller, composable specs
val spec = DeviceIsActiveOrInactive()
    .and(DeviceIsSensorOrActuator())
    .and(DeviceHasFeeds())
    .and(DeviceHasActiveFeeds())
    .and(
        DeviceInAnyZone().or(DeviceHasMinimumFeeds(5))
    )
```

### Pitfall 2: Mixing Concerns

**Problem:**

```kotlin
// ❌ Specification does more than checking
class DeviceIsActiveSpec : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        // Side effect! ❌
        logger.info("Checking if device ${device.deviceId} is active")
        
        // Database call! ❌
        val settings = settingsRepository.getDeviceSettings(device.deviceId)
        
        return device.getStatus() == DeviceStatus.ACTIVE && settings.isEnabled
    }
}
```

**Solution:**

```kotlin
// ✅ Pure function, no side effects
class DeviceIsActive : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getStatus() == DeviceStatus.ACTIVE
    }
}
```

### Pitfall 3: Performance Issues

**Problem:**

```kotlin
// ❌ N+1 query problem in specification
class DeviceHasValidSubscription : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        // Database call for each device! ❌
        val premises = premisesRepository.findById(device.premisesId)
        val subscription = subscriptionRepository.findByPremises(premises.premisesId)
        return subscription.isActive
    }
}

// Used like this = disaster
devices.filter { spec.isSatisfiedBy(it) }  // N database calls!
```

**Solution:**

```kotlin
// ✅ Load data upfront, pass as context
class DeviceHasValidSubscription(
    private val activeSubscriptions: Set<PremisesId>
) : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.premisesId in activeSubscriptions
    }
}

// Usage
val activeSubscriptions = subscriptionRepository.findAllActivePremisesIds()
val spec = DeviceHasValidSubscription(activeSubscriptions)
devices.filter { spec.isSatisfiedBy(it) }  // No extra queries!
```

### Pitfall 4: Not Reusing Specifications

**Problem:**

```kotlin
// ❌ Duplicate logic everywhere
fun findHealthyDevices() {
    return devices.filter { 
        it.getStatus() == DeviceStatus.ACTIVE &&
        it.getHealth() == DeviceHealth.ONLINE &&
        it.getFeeds().isNotEmpty()
    }
}

fun canActivateAutomation(device: Device): Boolean {
    return device.getStatus() == DeviceStatus.ACTIVE &&
           device.getHealth() == DeviceHealth.ONLINE &&
           device.getFeeds().isNotEmpty()
}
```

**Solution:**

```kotlin
// ✅ Reuse specification everywhere
val healthyDeviceSpec = DeviceIsActive()
    .and(DeviceIsOnline())
    .and(DeviceHasFeeds())

fun findHealthyDevices() {
    return devices.filter { healthyDeviceSpec.isSatisfiedBy(it) }
}

fun canActivateAutomation(device: Device): Boolean {
    return healthyDeviceSpec.isSatisfiedBy(device)
}
```

---

## <a name="testing"></a>9. Testing Specifications

### Unit Testing Individual Specifications

```kotlin
class DeviceIsActiveSpecTest {
    
    @Test
    fun `should return true for active device`() {
        val device = createDevice(status = DeviceStatus.ACTIVE)
        val spec = DeviceIsActive()
        
        assertTrue(spec.isSatisfiedBy(device))
    }
    
    @Test
    fun `should return false for inactive device`() {
        val device = createDevice(status = DeviceStatus.INACTIVE)
        val spec = DeviceIsActive()
        
        assertFalse(spec.isSatisfiedBy(device))
    }
    
    @Test
    fun `should return false for deleted device`() {
        val device = createDevice(status = DeviceStatus.DELETED)
        val spec = DeviceIsActive()
        
        assertFalse(spec.isSatisfiedBy(device))
    }
}
```

### Testing Composite Specifications

```kotlin
class CompositeSpecificationTest {
    
    @Test
    fun `AND specification should require both conditions`() {
        val device = createDevice(
            status = DeviceStatus.ACTIVE,
            type = DeviceType.SENSOR
        )
        
        val spec = DeviceIsActive().and(DeviceIsSensor())
        
        assertTrue(spec.isSatisfiedBy(device))
    }
    
    @Test
    fun `AND specification should fail if one condition fails`() {
        val device = createDevice(
            status = DeviceStatus.ACTIVE,
            type = DeviceType.ACTUATOR  // Not a sensor
        )
        
        val spec = DeviceIsActive().and(DeviceIsSensor())
        
        assertFalse(spec.isSatisfiedBy(device))
    }
    
    @Test
    fun `OR specification should pass if any condition passes`() {
        val device = createDevice(type = DeviceType.SENSOR)
        
        val spec = DeviceIsSensor().or(DeviceIsActuator())
        
        assertTrue(spec.isSatisfiedBy(device))
    }
    
    @Test
    fun `NOT specification should negate result`() {
        val device = createDevice(status = DeviceStatus.DELETED)
        
        val spec = DeviceIsDeleted().not()
        
        assertFalse(spec.isSatisfiedBy(device))
    }
    
    @Test
    fun `complex combination should work correctly`() {
        val device = createDevice(
            status = DeviceStatus.ACTIVE,
            type = DeviceType.SENSOR,
            health = DeviceHealth.ONLINE,
            feeds = listOf(createFeed())
        )
        
        val spec = DeviceIsActive()
            .and(DeviceIsSensor())
            .and(DeviceIsOnline())
            .and(DeviceHasFeeds())
        
        assertTrue(spec.isSatisfiedBy(device))
    }
}
```

### Testing with Repository

```kotlin
@SpringBootTest
class DeviceRepositorySpecificationTest {
    
    @Autowired
    lateinit var deviceRepository: DeviceRepository
    
    @Test
    fun `should find devices matching specification`() {
        // Setup test data
        val premisesId = PremisesId("premises-123")
        val activeDevice = createAndSaveDevice(premisesId, DeviceStatus.ACTIVE)
        val inactiveDevice = createAndSaveDevice(premisesId, DeviceStatus.INACTIVE)
        
        // Find with specification
        val spec = DeviceIsActive()
        val found = deviceRepository.findAll(premisesId, spec)
        
        // Verify
        assertEquals(1, found.size)
        assertEquals(activeDevice.deviceId, found.first().deviceId)
    }
    
    @Test
    fun `should find devices with complex specification`() {
        val premisesId = PremisesId("premises-123")
        val zoneId = ZoneId("zone-123")
        
        // Create test devices
        createAndSaveDevice(premisesId, DeviceStatus.ACTIVE, DeviceType.SENSOR, zoneId)
        createAndSaveDevice(premisesId, DeviceStatus.ACTIVE, DeviceType.ACTUATOR, zoneId)
        createAndSaveDevice(premisesId, DeviceStatus.INACTIVE, DeviceType.SENSOR, zoneId)
        createAndSaveDevice(premisesId, DeviceStatus.ACTIVE, DeviceType.SENSOR, ZoneId("other"))
        
        // Complex spec
        val spec = DeviceIsActive()
            .and(DeviceIsSensor())
            .and(DeviceInZone(zoneId))
        
        val found = deviceRepository.findAll(premisesId, spec)
        
        // Only one device matches all criteria
        assertEquals(1, found.size)
    }
}
```

### Property-Based Testing

```kotlin
class SpecificationPropertyTest {
    
    @Test
    fun `AND should be commutative`() {
        // For all devices: (A and B) == (B and A)
        forAll { device: Device ->
            val specAB = DeviceIsActive().and(DeviceIsSensor())
            val specBA = DeviceIsSensor().and(DeviceIsActive())
            
            specAB.isSatisfiedBy(device) == specBA.isSatisfiedBy(device)
        }
    }
    
    @Test
    fun `OR should be commutative`() {
        // For all devices: (A or B) == (B or A)
        forAll { device: Device ->
            val specAB = DeviceIsActive().or(DeviceIsSensor())
            val specBA = DeviceIsSensor().or(DeviceIsActive())
            
            specAB.isSatisfiedBy(device) == specBA.isSatisfiedBy(device)
        }
    }
    
    @Test
    fun `NOT NOT should equal original`() {
        // For all devices: not(not(A)) == A
        forAll { device: Device ->
            val spec = DeviceIsActive()
            val notNotSpec = spec.not().not()
            
            spec.isSatisfiedBy(device) == notNotSpec.isSatisfiedBy(device)
        }
    }
}
```

---

## 💡 Key Takeaways

1. **Specification pattern eliminates query explosion** - From 50+ methods to 1 generic method

2. **Reusable and composable** - Build complex queries from simple specifications

3. **Testable in isolation** - Test business logic without database

4. **Express business rules clearly** - Specifications read like English

5. **Single responsibility** - Each specification does one thing

6. **Works in-memory and database** - Same spec for both contexts

7. **Combine freely** - AND, OR, NOT create infinite combinations

8. **Keep specifications simple** - Break complex rules into smaller ones

---

## 🎯 Practical Exercise

Refactor your repository:

1. **Identify query methods** in your repository
2. **Extract specifications** for each criteria
3. **Create composite specs** for combinations
4. **Replace methods** with `findAll(specification)`
5. **Write tests** for specifications
6. **Measure improvement** - Count methods before/after

---

## 📚 What We've Covered

In this chapter, you learned:

✅ The query explosion problem  
✅ What the Specification pattern is  
✅ How to implement specifications  
✅ Real examples from SmartHome Hub  
✅ Combining specifications with AND/OR/NOT  
✅ Repository integration  
✅ Advanced patterns (builder, context, cache)  
✅ Common pitfalls and solutions  
✅ Comprehensive testing strategies  

---

## 🚀 Next Chapter

Ready to centralize business rules?

👉 **[Chapter 6: Policy Pattern - Centralizing Business Rules](./06-policy-pattern.md)**

**You'll learn:**
- The scattered rules problem
- How policies work
- Policy vs Specification
- Real policy implementations
- Policy chain pattern

**Reading Time:** 20 minutes  
**Difficulty:** Intermediate  

---

**Questions?** Open an issue in the repository!

**Found this helpful?** Star and share with your team!

---

*"The Specification pattern is one of the most underutilized patterns in DDD, yet one of the most powerful."  
— Eric Evans*

