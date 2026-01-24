# Chapter 7: Repository Pattern Done Right - Avoiding Common Pitfalls

**Series:** Building an Enterprise IoT Platform with DDD  
**Part:** 2 of 20 - Tactical Patterns  
**Reading Time:** 24 minutes  
**Level:** Intermediate to Advanced  

---

## 📋 Table of Contents

1. [The Problem: Repository Anti-Patterns](#the-problem)
2. [What is the Repository Pattern?](#what-is-repository)
3. [Repository Design Principles](#design-principles)
4. [Real-World Implementation from SmartHome Hub](#real-implementation)
5. [Generic vs Specific Repositories](#generic-vs-specific)
6. [Query Object Pattern](#query-object)
7. [Unit of Work Pattern](#unit-of-work)
8. [Common Pitfalls](#pitfalls)
9. [Testing Repositories](#testing)

---

## <a name="the-problem"></a>1. The Problem: Repository Anti-Patterns

### The Scenario: The Repository Explosion

You're reviewing the SmartHome Hub codebase when you discover a nightmare:

> **Code Review Finding:** "Repository layer has grown to 15 repository interfaces with 200+ methods total. New developers can't find the right method. Code is unmaintainable."

You investigate and find multiple anti-patterns:

#### Anti-Pattern 1: Leaking Infrastructure Concerns

```kotlin
// DeviceRepository.kt - Infrastructure leaking into domain ❌
interface DeviceRepository {
    // MongoDB-specific types leak into domain!
    fun findAll(query: Query): List<Device>  // Query is from Spring Data MongoDB ❌
    fun save(document: DeviceDocument): DeviceDocument  // Document, not domain entity ❌
    
    // JPA-specific method names
    fun findByPremisesIdAndStatusAndTypeOrderByCreatedAtDesc(
        premisesId: String,
        status: String,
        type: String
    ): List<Device>  // Method name explosion! ❌
}
```

#### Anti-Pattern 2: CRUD Repository Abuse

```kotlin
// DeviceRepository.kt - Just a CRUD wrapper ❌
interface DeviceRepository : CrudRepository<Device, String> {
    // No domain meaning!
    // Just exposing CRUD operations
    // Where's the domain language?
}

// Usage - Domain logic leaks everywhere
@Service
class DeviceService {
    fun activateDevice(deviceId: String) {
        val device = deviceRepository.findById(deviceId).orElse(null)  // Optional handling ❌
        device?.status = "ACTIVE"  // String magic! ❌
        deviceRepository.save(device)
    }
}
```

#### Anti-Pattern 3: Repository as Query Engine

```kotlin
// DeviceRepository.kt - 50+ query methods ❌
interface DeviceRepository {
    fun findAll(): List<Device>
    fun findById(id: String): Device?
    fun findByPremisesId(premisesId: String): List<Device>
    fun findByStatus(status: String): List<Device>
    fun findByType(type: String): List<Device>
    fun findByPremisesIdAndStatus(premisesId: String, status: String): List<Device>
    fun findByPremisesIdAndType(premisesId: String, type: String): List<Device>
    fun findByPremisesIdAndStatusAndType(premisesId: String, status: String, type: String): List<Device>
    fun findActiveDevices(premisesId: String): List<Device>
    fun findInactiveDevices(premisesId: String): List<Device>
    fun findOnlineDevices(premisesId: String): List<Device>
    fun findOfflineDevices(premisesId: String): List<Device>
    // ... 38 more methods! 💥
}
```

#### Anti-Pattern 4: No Aggregate Awareness

```kotlin
// DeviceRepository.kt - Breaks aggregate boundaries ❌
interface DeviceRepository {
    fun save(device: Device): Device
}

// FeedRepository.kt - Feed is part of Device aggregate! ❌
interface FeedRepository {
    fun save(feed: Feed): Feed  // Should be saved with Device!
    fun findByDeviceId(deviceId: String): List<Feed>
    fun deleteByDeviceId(deviceId: String)
}

// Usage - Can break invariants!
val device = deviceRepository.findById(deviceId)
val feed = Feed(...)
feedRepository.save(feed)  // Saved separately! 💥
// What if device is deleted? Orphaned feed!
```

#### Anti-Pattern 5: Transaction Management in Repository

```kotlin
// DeviceRepository.kt - Transaction logic in repository ❌
interface DeviceRepository {
    @Transactional  // Shouldn't be here!
    fun registerDeviceWithFeeds(device: Device, feeds: List<Feed>): Device {
        val savedDevice = save(device)
        feeds.forEach { feed ->
            feedRepository.save(feed)
        }
        return savedDevice
    }
}
```

### The Real Cost

**Problems discovered:**

1. **15 Repository Interfaces** - Hard to find the right one
2. **200+ Methods** - Query explosion everywhere
3. **Infrastructure Leakage** - MongoDB/JPA types in domain
4. **Broken Aggregates** - Entities saved separately
5. **No Domain Language** - Generic CRUD operations
6. **Transaction Chaos** - Transaction logic scattered

**Cost:**
- 🔴 New developer onboarding: 2 weeks instead of 2 days
- 🔴 Feature development 3x slower
- 🔴 Data integrity bugs in production
- 🔴 Can't switch database (too coupled)
- 🔴 Tests require full database setup

---

## <a name="what-is-repository"></a>2. What is the Repository Pattern?

### Definition

> **Repository Pattern:** A pattern that mediates between the domain and data mapping layers using a collection-like interface for accessing domain objects.
> 
> — Martin Fowler, Patterns of Enterprise Application Architecture

### Core Concepts

#### 1. Collection-Like Interface

Repository should feel like an in-memory collection:

```kotlin
// Think of repository as a collection
interface DeviceRepository {
    // Like: collection.add(device)
    fun save(device: Device): Device
    
    // Like: collection.find { it.id == deviceId }
    fun findById(deviceId: DeviceId): Device?
    
    // Like: collection.remove(device)
    fun delete(deviceId: DeviceId)
    
    // Like: collection.filter { it.premisesId == premisesId }
    fun findAll(premisesId: PremisesId): List<Device>
}
```

#### 2. Aggregate Root Repository Only

One repository per aggregate root:

```kotlin
// ✅ Device is aggregate root - has repository
interface DeviceRepository {
    fun save(device: Device): Device  // Saves device + feeds
    fun findById(deviceId: DeviceId): Device?
}

// ❌ Feed is part of Device aggregate - NO separate repository
// Access feeds through Device:
val device = deviceRepository.findById(deviceId)
val feeds = device.getFeeds()
```

#### 3. Domain Language

Use ubiquitous language, not database terms:

```kotlin
// ❌ Database language
interface DeviceRepository {
    fun insert(device: Device)
    fun update(device: Device)
    fun selectById(id: String)
    fun selectAll()
}

// ✅ Domain language
interface DeviceRepository {
    fun save(device: Device): Device
    fun findById(deviceId: DeviceId): Device?
    fun findAll(premisesId: PremisesId): List<Device>
    fun remove(deviceId: DeviceId)
}
```

#### 4. Hide Infrastructure

Infrastructure details hidden behind interface:

```kotlin
// ✅ Domain interface - no infrastructure types
interface DeviceRepository {
    fun save(device: Device): Device
    fun findById(deviceId: DeviceId): Device?
    fun findAll(premisesId: PremisesId, spec: Specification<Device>): List<Device>
}

// Implementation - infrastructure details hidden
class MongoDeviceRepository(
    private val mongoTemplate: MongoTemplate  // Hidden from domain
) : DeviceRepository {
    override fun save(device: Device): Device {
        val document = device.toDocument()  // Map to infrastructure
        val saved = mongoTemplate.save(document)
        return saved.toDomain()  // Map back to domain
    }
}
```

---

## <a name="design-principles"></a>3. Repository Design Principles

### Principle 1: One Repository Per Aggregate Root

```kotlin
// ✅ GOOD - Aggregate boundaries respected
interface DeviceRepository {
    fun save(device: Device): Device  // Saves entire aggregate
}

// Device aggregate includes feeds
class Device(
    val deviceId: DeviceId,
    private val feeds: Map<FeedId, Feed>  // Part of aggregate
)

// ❌ BAD - Breaking aggregate boundary
interface DeviceRepository {
    fun save(device: Device): Device
}

interface FeedRepository {  // Shouldn't exist!
    fun save(feed: Feed): Feed
}
```

### Principle 2: Repository Returns Domain Objects

```kotlin
// ✅ GOOD - Returns domain objects
interface DeviceRepository {
    fun save(device: Device): Device
    fun findById(deviceId: DeviceId): Device?
}

// ❌ BAD - Returns infrastructure objects
interface DeviceRepository {
    fun save(device: Device): DeviceDocument  // Document is infrastructure!
    fun findById(deviceId: DeviceId): Optional<DeviceDocument>  // Optional is Java!
}
```

### Principle 3: Simple Interface

Keep the interface minimal and focused:

```kotlin
// ✅ GOOD - Simple, focused interface
interface DeviceRepository {
    fun save(device: Device): Device
    fun findById(deviceId: DeviceId): Device?
    fun findAll(premisesId: PremisesId): List<Device>
    fun findAll(premisesId: PremisesId, spec: Specification<Device>): List<Device>
    fun delete(deviceId: DeviceId)
    fun exists(deviceId: DeviceId): Boolean
    fun count(premisesId: PremisesId): Long
}
// 7 methods - clear and focused

// ❌ BAD - Bloated interface
interface DeviceRepository {
    fun save(device: Device): Device
    fun findById(deviceId: DeviceId): Device?
    fun findBySerialNumber(serialNumber: SerialNumber): Device?
    fun findByName(name: String): List<Device>
    fun findActiveDevices(): List<Device>
    fun findInactiveDevices(): List<Device>
    fun findOnlineDevices(): List<Device>
    fun findOfflineDevices(): List<Device>
    fun findDevicesWithNoFeeds(): List<Device>
    fun findDevicesInZone(zoneId: ZoneId): List<Device>
    fun findRecentDevices(since: Instant): List<Device>
    // ... 40+ more methods 💥
}
```

### Principle 4: Use Specifications for Queries

```kotlin
// ✅ GOOD - Use specifications for flexible queries
interface DeviceRepository {
    fun findAll(
        premisesId: PremisesId,
        specification: Specification<Device>
    ): List<Device>
}

// Usage - infinite query combinations
val spec = DeviceIsActive()
    .and(DeviceIsSensor())
    .and(DeviceInZone(zoneId))

val devices = repository.findAll(premisesId, spec)

// ❌ BAD - Method explosion
interface DeviceRepository {
    fun findActiveDevicesInZone(premisesId: PremisesId, zoneId: ZoneId): List<Device>
    fun findActiveSensorsInZone(premisesId: PremisesId, zoneId: ZoneId): List<Device>
    // ... hundreds more combinations
}
```

### Principle 5: No Business Logic in Repository

```kotlin
// ✅ GOOD - Repository only handles persistence
class MongoDeviceRepository : DeviceRepository {
    override fun save(device: Device): Device {
        val document = device.toDocument()
        val saved = mongoTemplate.save(document, "devices")
        return saved.toDomain()
    }
}

// ❌ BAD - Business logic in repository
class MongoDeviceRepository : DeviceRepository {
    override fun save(device: Device): Device {
        // Validation in repository! ❌
        if (device.getFeeds().isEmpty()) {
            throw ValidationException("Device must have feeds")
        }
        
        // Business rule in repository! ❌
        if (countByPremisesId(device.premisesId) >= 100) {
            throw BusinessException("Too many devices")
        }
        
        val document = device.toDocument()
        return mongoTemplate.save(document).toDomain()
    }
}
```

---

## <a name="real-implementation"></a>4. Real-World Implementation from SmartHome Hub

### Repository Interface (Domain Layer)

```kotlin
// device/domain/repository/DeviceRepository.kt
package com.smarthomehub.device.domain.repository

interface DeviceRepository {
    /**
     * Save device aggregate (including all feeds)
     * @return saved device with updated version
     */
    fun save(device: Device): Device
    
    /**
     * Find device by ID
     * @return device if found, null otherwise
     */
    fun findById(deviceId: DeviceId): Device?
    
    /**
     * Find device by premises and device ID
     * Used for multi-tenant queries
     */
    fun findByPremisesIdAndDeviceId(
        premisesId: PremisesId,
        deviceId: DeviceId
    ): Device?
    
    /**
     * Find all devices for a premises
     */
    fun findAll(premisesId: PremisesId): List<Device>
    
    /**
     * Find devices matching specification
     */
    fun findAll(
        premisesId: PremisesId,
        specification: Specification<Device>
    ): List<Device>
    
    /**
     * Find device by unique serial number
     */
    fun findBySerialNumber(serialNumber: SerialNumber): Device?
    
    /**
     * Check if device exists
     */
    fun exists(deviceId: DeviceId): Boolean
    
    /**
     * Count devices for premises
     */
    fun countByPremisesId(premisesId: PremisesId): Long
    
    /**
     * Count devices by premises and status
     */
    fun countByPremisesIdAndStatus(
        premisesId: PremisesId,
        status: DeviceStatus
    ): Long
    
    /**
     * Delete device (removes entire aggregate)
     */
    fun delete(deviceId: DeviceId)
}
```

### Repository Implementation (Infrastructure Layer)

```kotlin
// device/infrastructure/persistence/MongoDeviceRepository.kt
package com.smarthomehub.device.infrastructure.persistence

@Repository
class MongoDeviceRepository(
    private val mongoTemplate: MongoTemplate
) : DeviceRepository {
    
    override fun save(device: Device): Device {
        val document = DeviceMapper.toDocument(device)
        
        // Save with optimistic locking
        val saved = try {
            mongoTemplate.save(document, COLLECTION_NAME)
        } catch (e: OptimisticLockingFailureException) {
            throw ConcurrentModificationException(
                "Device ${device.deviceId} was modified by another transaction",
                e
            )
        }
        
        return DeviceMapper.toDomain(saved)
    }
    
    override fun findById(deviceId: DeviceId): Device? {
        val query = Query.query(Criteria.where("_id").`is`(deviceId.value))
        
        val document = mongoTemplate.findOne(
            query,
            DeviceDocument::class.java,
            COLLECTION_NAME
        ) ?: return null
        
        return DeviceMapper.toDomain(document)
    }
    
    override fun findByPremisesIdAndDeviceId(
        premisesId: PremisesId,
        deviceId: DeviceId
    ): Device? {
        val query = Query.query(
            Criteria.where("_id").`is`(deviceId.value)
                .and("premisesId").`is`(premisesId.value)
        )
        
        val document = mongoTemplate.findOne(
            query,
            DeviceDocument::class.java,
            COLLECTION_NAME
        ) ?: return null
        
        return DeviceMapper.toDomain(document)
    }
    
    override fun findAll(premisesId: PremisesId): List<Device> {
        val query = Query.query(
            Criteria.where("premisesId").`is`(premisesId.value)
        )
        
        val documents = mongoTemplate.find(
            query,
            DeviceDocument::class.java,
            COLLECTION_NAME
        )
        
        return documents.map { DeviceMapper.toDomain(it) }
    }
    
    override fun findAll(
        premisesId: PremisesId,
        specification: Specification<Device>
    ): List<Device> {
        // Start with premises filter
        val query = Query.query(
            Criteria.where("premisesId").`is`(premisesId.value)
        )
        
        // Add specification criteria if it's a query specification
        if (specification is QuerySpecification) {
            val specCriteria = specification.toCriteria()
            query.addCriteria(specCriteria)
        }
        
        val documents = mongoTemplate.find(
            query,
            DeviceDocument::class.java,
            COLLECTION_NAME
        )
        
        val devices = documents.map { DeviceMapper.toDomain(it) }
        
        // Apply in-memory filter for complex specifications
        return devices.filter { specification.isSatisfiedBy(it) }
    }
    
    override fun findBySerialNumber(serialNumber: SerialNumber): Device? {
        val query = Query.query(
            Criteria.where("serialNumber").`is`(serialNumber.value)
        )
        
        val document = mongoTemplate.findOne(
            query,
            DeviceDocument::class.java,
            COLLECTION_NAME
        ) ?: return null
        
        return DeviceMapper.toDomain(document)
    }
    
    override fun exists(deviceId: DeviceId): Boolean {
        val query = Query.query(Criteria.where("_id").`is`(deviceId.value))
        return mongoTemplate.exists(query, COLLECTION_NAME)
    }
    
    override fun countByPremisesId(premisesId: PremisesId): Long {
        val query = Query.query(
            Criteria.where("premisesId").`is`(premisesId.value)
        )
        return mongoTemplate.count(query, COLLECTION_NAME)
    }
    
    override fun countByPremisesIdAndStatus(
        premisesId: PremisesId,
        status: DeviceStatus
    ): Long {
        val query = Query.query(
            Criteria.where("premisesId").`is`(premisesId.value)
                .and("status").`is`(status.name)
        )
        return mongoTemplate.count(query, COLLECTION_NAME)
    }
    
    override fun delete(deviceId: DeviceId) {
        val query = Query.query(Criteria.where("_id").`is`(deviceId.value))
        mongoTemplate.remove(query, COLLECTION_NAME)
    }
    
    companion object {
        private const val COLLECTION_NAME = "devices"
    }
}
```

### Document Model (Infrastructure)

```kotlin
// device/infrastructure/persistence/document/DeviceDocument.kt
@Document(collection = "devices")
data class DeviceDocument(
    @Id
    val id: String,
    
    val premisesId: String,
    val name: String,
    val serialNumber: String,
    val type: String,
    val status: String,
    val health: String,
    
    // Feeds embedded within device document
    val feeds: List<FeedDocument>,
    
    val os: String?,
    val zoneId: String?,
    
    val createdBy: String,
    val createdAt: Long,
    val lastSeenAt: Long?,
    
    @Version
    val version: Long?
)

data class FeedDocument(
    val id: String,
    val name: String,
    val type: String,
    val value: Int,
    val lastUpdated: Long
)
```

### Mapper (Infrastructure)

```kotlin
// device/infrastructure/persistence/mapper/DeviceMapper.kt
object DeviceMapper {
    
    fun toDomain(document: DeviceDocument): Device {
        val feeds = document.feeds.map { feedDoc ->
            val feedId = FeedId(feedDoc.id)
            val feed = Feed(
                feedId = feedId,
                deviceId = DeviceId(document.id),
                name = feedDoc.name,
                type = FeedType.valueOf(feedDoc.type),
                value = feedDoc.value,
                lastUpdated = Instant.ofEpochMilli(feedDoc.lastUpdated)
            )
            feedId to feed
        }.toMap()
        
        return Device(
            deviceId = DeviceId(document.id),
            premisesId = PremisesId(document.premisesId),
            name = Name(document.name),
            serialNumber = SerialNumber(document.serialNumber),
            type = DeviceType.valueOf(document.type),
            status = DeviceStatus.valueOf(document.status),
            health = DeviceHealth.valueOf(document.health),
            feeds = feeds,
            os = document.os?.let { DeviceOS.valueOf(it) },
            zoneId = document.zoneId?.let { ZoneId(it) },
            createdBy = ActorId(document.createdBy),
            createdAt = Instant.ofEpochMilli(document.createdAt),
            lastSeenAt = document.lastSeenAt?.let { Instant.ofEpochMilli(it) },
            version = document.version
        )
    }
    
    fun toDocument(device: Device): DeviceDocument {
        val feedDocs = device.getFeeds().map { feed ->
            FeedDocument(
                id = feed.feedId.value,
                name = feed.name,
                type = feed.type.name,
                value = feed.getValue(),
                lastUpdated = feed.getLastUpdated().toEpochMilli()
            )
        }
        
        return DeviceDocument(
            id = device.deviceId.value,
            premisesId = device.premisesId.value,
            name = device.getName().value,
            serialNumber = device.serialNumber.value,
            type = device.getType().name,
            status = device.getStatus().name,
            health = device.getHealth().name,
            feeds = feedDocs,
            os = device.os?.name,
            zoneId = device.getZoneId()?.value,
            createdBy = device.createdBy.value,
            createdAt = device.createdAt.toEpochMilli(),
            lastSeenAt = device.getLastSeenAt()?.toEpochMilli(),
            version = device.version
        )
    }
}
```

---

## <a name="generic-vs-specific"></a>5. Generic vs Specific Repositories

### The Debate

Should you use generic base repository or specific implementations?

#### Option 1: Generic Base Repository

```kotlin
// GenericRepository.kt
interface GenericRepository<T, ID> {
    fun save(entity: T): T
    fun findById(id: ID): T?
    fun findAll(): List<T>
    fun delete(id: ID)
    fun exists(id: ID): Boolean
}

// DeviceRepository.kt - extends generic
interface DeviceRepository : GenericRepository<Device, DeviceId> {
    // Add device-specific methods
    fun findBySerialNumber(serialNumber: SerialNumber): Device?
    fun countByPremisesId(premisesId: PremisesId): Long
}
```

**Pros:**
- ✅ Less code duplication
- ✅ Consistent interface across repositories
- ✅ Easy to add new repositories

**Cons:**
- ❌ May include methods not needed for specific aggregate
- ❌ One size doesn't fit all
- ❌ Can encourage CRUD thinking

#### Option 2: Specific Repositories

```kotlin
// DeviceRepository.kt - specific interface
interface DeviceRepository {
    fun save(device: Device): Device
    fun findById(deviceId: DeviceId): Device?
    fun findByPremisesIdAndDeviceId(premisesId: PremisesId, deviceId: DeviceId): Device?
    fun findAll(premisesId: PremisesId): List<Device>
    fun findBySerialNumber(serialNumber: SerialNumber): Device?
    fun delete(deviceId: DeviceId)
}

// PremisesRepository.kt - different interface
interface PremisesRepository {
    fun save(premises: Premises): Premises
    fun findById(premisesId: PremisesId): Premises?
    fun findByOwnerId(ownerId: UserId): List<Premises>
    // Different methods based on needs
}
```

**Pros:**
- ✅ Tailored to aggregate needs
- ✅ Explicit about what's supported
- ✅ No unnecessary methods
- ✅ Encourages domain thinking

**Cons:**
- ❌ More code to write
- ❌ Potential duplication

### Recommendation

**Use specific repositories** for better domain modeling:

```kotlin
// ✅ Recommended approach
interface DeviceRepository {
    // Only methods actually needed for Device aggregate
    fun save(device: Device): Device
    fun findById(deviceId: DeviceId): Device?
    fun findByPremisesIdAndDeviceId(premisesId: PremisesId, deviceId: DeviceId): Device?
    fun findAll(premisesId: PremisesId): List<Device>
    fun findAll(premisesId: PremisesId, spec: Specification<Device>): List<Device>
    fun findBySerialNumber(serialNumber: SerialNumber): Device?
    fun exists(deviceId: DeviceId): Boolean
    fun countByPremisesId(premisesId: PremisesId): Long
    fun delete(deviceId: DeviceId)
}
```

If you must use generic, make it optional:

```kotlin
// Base interface (optional to implement)
interface Repository<T, ID> {
    fun save(entity: T): T
    fun findById(id: ID): T?
}

// Specific repository can implement base or not
interface DeviceRepository : Repository<Device, DeviceId> {
    // Add specific methods
    fun findBySerialNumber(serialNumber: SerialNumber): Device?
}

// Or don't extend base
interface AutomationRepository {
    // Completely custom interface
    fun save(automation: Automation): Automation
    fun findActiveAutomations(premisesId: PremisesId): List<Automation>
}
```

---

## <a name="query-object"></a>6. Query Object Pattern

### Problem: Complex Query Parameters

```kotlin
// ❌ Method parameter explosion
fun findDevices(
    premisesId: PremisesId,
    status: DeviceStatus?,
    type: DeviceType?,
    zoneId: ZoneId?,
    health: DeviceHealth?,
    minFeeds: Int?,
    maxFeeds: Int?,
    seenSince: Instant?,
    sortBy: String?,
    sortOrder: SortOrder?,
    page: Int?,
    pageSize: Int?
): List<Device>
```

### Solution: Query Object

```kotlin
// DeviceQuery.kt
data class DeviceQuery(
    val premisesId: PremisesId,
    val specification: Specification<Device>? = null,
    val sortBy: DeviceSortField = DeviceSortField.CREATED_AT,
    val sortOrder: SortOrder = SortOrder.DESC,
    val pagination: Pagination? = null
) {
    enum class DeviceSortField {
        CREATED_AT,
        NAME,
        SERIAL_NUMBER,
        LAST_SEEN_AT,
        STATUS
    }
}

data class Pagination(
    val page: Int,
    val pageSize: Int
) {
    init {
        require(page >= 0) { "Page must be >= 0" }
        require(pageSize in 1..100) { "Page size must be between 1 and 100" }
    }
    
    val offset: Int get() = page * pageSize
}

data class SortOrder {
    companion object {
        val ASC = SortOrder("ASC")
        val DESC = SortOrder("DESC")
    }
    
    val value: String
    
    private constructor(value: String) {
        this.value = value
    }
}

// Repository method
interface DeviceRepository {
    fun findAll(query: DeviceQuery): List<Device>
}

// Usage - clean and flexible!
val query = DeviceQuery(
    premisesId = premisesId,
    specification = DeviceIsActive().and(DeviceIsSensor()),
    sortBy = DeviceQuery.DeviceSortField.NAME,
    sortOrder = SortOrder.ASC,
    pagination = Pagination(page = 0, pageSize = 20)
)

val devices = deviceRepository.findAll(query)
```

### Query Result Object

```kotlin
// QueryResult.kt - Paginated result
data class QueryResult<T>(
    val items: List<T>,
    val totalCount: Long,
    val page: Int,
    val pageSize: Int
) {
    val totalPages: Int get() = (totalCount / pageSize).toInt() + if (totalCount % pageSize > 0) 1 else 0
    val hasNext: Boolean get() = page < totalPages - 1
    val hasPrevious: Boolean get() = page > 0
}

// Repository returns result
interface DeviceRepository {
    fun findAll(query: DeviceQuery): QueryResult<Device>
}

// Usage
val result = deviceRepository.findAll(query)

println("Showing ${result.items.size} of ${result.totalCount} devices")
println("Page ${result.page + 1} of ${result.totalPages}")

if (result.hasNext) {
    println("More results available")
}
```

---

## <a name="unit-of-work"></a>7. Unit of Work Pattern

### Problem: Transaction Management

```kotlin
// ❌ Scattered transaction management
@Service
class DeviceService {
    @Transactional  // Transaction here
    fun registerDevice(command: RegisterDeviceCommand): Device {
        val device = Device.register(...)
        deviceRepository.save(device)
        
        // Another transaction?
        auditService.log("DEVICE_REGISTERED", device.deviceId)
        
        return device
    }
}
```

### Solution: Explicit Unit of Work

```kotlin
// UnitOfWork.kt
interface UnitOfWork {
    /**
     * Execute operations within a transaction
     */
    fun <T> execute(operation: () -> T): T
    
    /**
     * Check if currently in a unit of work
     */
    fun isActive(): Boolean
}

// SpringTransactionalUnitOfWork.kt
@ComponentInline
class SpringTransactionalUnitOfWork : UnitOfWork {
    
    @Transactional
    override fun <T> execute(operation: () -> T): T {
        return operation()
    }
    
    override fun isActive(): Boolean {
        return TransactionSynchronizationManager.isActualTransactionActive()
    }
}

// Usage in Use Case
@Service
class RegisterDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val unitOfWork: UnitOfWork,
    private val eventPublisher: EventPublisher
) {
    fun execute(command: RegisterDeviceCommand): Device {
        // Explicit transaction boundary
        val device = unitOfWork.execute {
            val device = Device.register(...)
            deviceRepository.save(device)
        }
        
        // Outside transaction - publishes events asynchronously
        eventPublisher.publish(device.domainEvents)
        
        return device
    }
}
```

### Advanced: Unit of Work with Event Publishing

```kotlin
// TransactionalUnitOfWork.kt
class TransactionalUnitOfWork(
    private val eventPublisher: EventPublisher
) : UnitOfWork {
    
    private val events = ThreadLocal<MutableList<DomainEvent>>()
    
    @Transactional
    override fun <T> execute(operation: () -> T): T {
        events.set(mutableListOf())
        
        return try {
            val result = operation()
            
            // Publish events after successful transaction
            TransactionSynchronizationManager.registerSynchronization(
                object : TransactionSynchronization {
                    override fun afterCommit() {
                        events.get()?.forEach { event ->
                            eventPublisher.publish(event)
                        }
                    }
                }
            )
            
            result
        } finally {
            events.remove()
        }
    }
    
    fun addEvent(event: DomainEvent) {
        events.get()?.add(event)
    }
}
```

---

## <a name="pitfalls"></a>8. Common Pitfalls

### Pitfall 1: Repository Inheritance Hierarchy

**Problem:**

```kotlin
// ❌ Complex inheritance
interface CrudRepository<T, ID> {
    fun save(entity: T): T
    fun findAll(): List<T>
    fun delete(id: ID)
}

interface PagingRepository<T, ID> : CrudRepository<T, ID> {
    fun findAll(page: Int, size: Int): List<T>
}

interface SpecificationRepository<T, ID> : PagingRepository<T, ID> {
    fun findAll(spec: Specification<T>): List<T>
}

interface DeviceRepository : SpecificationRepository<Device, DeviceId> {
    // Inherits 20+ methods, needs only 5
}
```

**Solution:**

```kotlin
// ✅ Flat, specific interface
interface DeviceRepository {
    fun save(device: Device): Device
    fun findById(deviceId: DeviceId): Device?
    fun findAll(premisesId: PremisesId): List<Device>
    fun findAll(premisesId: PremisesId, spec: Specification<Device>): List<Device>
    fun delete(deviceId: DeviceId)
}
```

### Pitfall 2: Returning Null vs Optional

**Problem:**

```kotlin
// ❌ Inconsistent null handling
interface DeviceRepository {
    fun findById(deviceId: DeviceId): Device?  // Kotlin nullable
    fun findBySerialNumber(sn: SerialNumber): Optional<Device>  // Java Optional
}
```

**Solution:**

```kotlin
// ✅ Consistent - use Kotlin nullable
interface DeviceRepository {
    fun findById(deviceId: DeviceId): Device?
    fun findBySerialNumber(serialNumber: SerialNumber): Device?
}

// Usage - idiomatic Kotlin
val device = deviceRepository.findById(deviceId)
    ?: throw DeviceNotFoundException(deviceId)
```

### Pitfall 3: Repository Knows About Use Cases

**Problem:**

```kotlin
// ❌ Repository has use case methods
interface DeviceRepository {
    fun save(device: Device): Device
    
    // Use case logic in repository! ❌
    fun registerNewDevice(
        name: String,
        serialNumber: String,
        premisesId: String
    ): Device {
        val device = Device.register(...)
        return save(device)
    }
    
    // Another use case! ❌
    fun activateDevice(deviceId: String): Device {
        val device = findById(DeviceId(deviceId))
            ?: throw DeviceNotFoundException()
        val activated = device.activate()
        return save(activated)
    }
}
```

**Solution:**

```kotlin
// ✅ Repository only handles persistence
interface DeviceRepository {
    fun save(device: Device): Device
    fun findById(deviceId: DeviceId): Device?
}

// Use cases in application layer
@Service
class RegisterDeviceUseCase(
    private val deviceRepository: DeviceRepository
) {
    fun execute(command: RegisterDeviceCommand): Device {
        val device = Device.register(...)
        return deviceRepository.save(device)
    }
}
```

### Pitfall 4: Infrastructure Types in Interface

**Problem:**

```kotlin
// ❌ MongoDB types leak into interface
interface DeviceRepository {
    fun findAll(query: Query): List<Device>  // Query is MongoDB!
    fun save(device: Device): UpdateResult  // UpdateResult is MongoDB!
}
```

**Solution:**

```kotlin
// ✅ Only domain types in interface
interface DeviceRepository {
    fun findAll(premisesId: PremisesId, spec: Specification<Device>): List<Device>
    fun save(device: Device): Device
}
```

### Pitfall 5: Not Using Optimistic Locking

**Problem:**

```kotlin
// ❌ No version control - lost updates
@Document
data class DeviceDocument(
    @Id val id: String,
    val status: String
    // No version field!
)

// Two concurrent updates:
// User 1: Load device, change status, save
// User 2: Load device, change status, save
// Result: User 1's changes lost! 💥
```

**Solution:**

```kotlin
// ✅ With optimistic locking
@Document
data class DeviceDocument(
    @Id val id: String,
    val status: String,
    @Version val version: Long?  // Version field
)

// Save fails if version doesn't match
try {
    deviceRepository.save(device)
} catch (e: OptimisticLockingFailureException) {
    // Handle concurrent modification
    throw ConcurrentModificationException("Device was modified by another user")
}
```

---

## <a name="testing"></a>9. Testing Repositories

### In-Memory Repository for Testing

```kotlin
// InMemoryDeviceRepository.kt - For tests
class InMemoryDeviceRepository : DeviceRepository {
    
    private val devices = ConcurrentHashMap<DeviceId, Device>()
    private var versionCounter = 0L
    
    override fun save(device: Device): Device {
        // Simulate optimistic locking
        if (device.version != null && devices.containsKey(device.deviceId)) {
            val existing = devices[device.deviceId]
            if (existing?.version != device.version) {
                throw ConcurrentModificationException("Version mismatch")
            }
        }
        
        val newVersion = ++versionCounter
        val deviceWithVersion = device.copy(version = newVersion)
        devices[device.deviceId] = deviceWithVersion
        
        return deviceWithVersion
    }
    
    override fun findById(deviceId: DeviceId): Device? {
        return devices[deviceId]
    }
    
    override fun findByPremisesIdAndDeviceId(
        premisesId: PremisesId,
        deviceId: DeviceId
    ): Device? {
        return devices[deviceId]?.takeIf { it.premisesId == premisesId }
    }
    
    override fun findAll(premisesId: PremisesId): List<Device> {
        return devices.values.filter { it.premisesId == premisesId }
    }
    
    override fun findAll(
        premisesId: PremisesId,
        specification: Specification<Device>
    ): List<Device> {
        return devices.values
            .filter { it.premisesId == premisesId }
            .filter { specification.isSatisfiedBy(it) }
    }
    
    override fun findBySerialNumber(serialNumber: SerialNumber): Device? {
        return devices.values.find { it.serialNumber == serialNumber }
    }
    
    override fun exists(deviceId: DeviceId): Boolean {
        return devices.containsKey(deviceId)
    }
    
    override fun countByPremisesId(premisesId: PremisesId): Long {
        return devices.values.count { it.premisesId == premisesId }.toLong()
    }
    
    override fun countByPremisesIdAndStatus(
        premisesId: PremisesId,
        status: DeviceStatus
    ): Long {
        return devices.values.count { 
            it.premisesId == premisesId && it.getStatus() == status 
        }.toLong()
    }
    
    override fun delete(deviceId: DeviceId) {
        devices.remove(deviceId)
    }
    
    fun clear() {
        devices.clear()
        versionCounter = 0
    }
}
```

### Unit Tests with In-Memory Repository

```kotlin
class RegisterDeviceUseCaseTest {
    
    private lateinit var deviceRepository: InMemoryDeviceRepository
    private lateinit var useCase: RegisterDeviceUseCase
    
    @BeforeEach
    fun setup() {
        deviceRepository = InMemoryDeviceRepository()
        useCase = RegisterDeviceUseCase(deviceRepository)
    }
    
    @Test
    fun `should register device successfully`() {
        // Given
        val command = RegisterDeviceCommand(
            deviceId = DeviceId.generate(),
            premisesId = PremisesId("premises-123"),
            name = Name("Living Room Sensor"),
            serialNumber = SerialNumber("ABC12345"),
            type = DeviceType.SENSOR,
            createdBy = ActorId("actor-123")
        )
        
        // When
        val device = useCase.execute(command)
        
        // Then
        assertNotNull(device)
        assertEquals(command.deviceId, device.deviceId)
        assertEquals(DeviceStatus.PENDING, device.getStatus())
        
        // Verify saved
        val saved = deviceRepository.findById(device.deviceId)
        assertNotNull(saved)
    }
    
    @Test
    fun `should reject duplicate serial number`() {
        // Given - device with serial number exists
        val existingDevice = Device.register(...)
        deviceRepository.save(existingDevice)
        
        val command = RegisterDeviceCommand(
            serialNumber = existingDevice.serialNumber  // Same serial!
        )
        
        // When/Then
        assertThrows<DuplicateSerialNumberException> {
            useCase.execute(command)
        }
    }
}
```

### Integration Tests with Real Database

```kotlin
@SpringBootTest
@Testcontainers
class MongoDeviceRepositoryIntegrationTest {
    
    @Container
    val mongoContainer = MongoDBContainer("mongo:6.0")
        .withExposedPorts(27017)
    
    @Autowired
    lateinit var deviceRepository: DeviceRepository
    
    @Autowired
    lateinit var mongoTemplate: MongoTemplate
    
    @BeforeEach
    fun cleanup() {
        mongoTemplate.dropCollection("devices")
    }
    
    @Test
    fun `should save and retrieve device`() {
        // Given
        val device = Device.register(
            deviceId = DeviceId.generate(),
            premisesId = PremisesId("premises-123"),
            name = Name("Test Device"),
            serialNumber = SerialNumber("TEST12345"),
            type = DeviceType.SENSOR,
            createdBy = ActorId("actor-123")
        )
        
        // When
        val saved = deviceRepository.save(device)
        val retrieved = deviceRepository.findById(saved.deviceId)
        
        // Then
        assertNotNull(retrieved)
        assertEquals(saved.deviceId, retrieved?.deviceId)
        assertEquals(saved.getName(), retrieved?.getName())
    }
    
    @Test
    fun `should handle optimistic locking`() {
        // Given - save device
        val device = Device.register(...)
        val saved = deviceRepository.save(device)
        
        // When - load twice and modify both
        val device1 = deviceRepository.findById(saved.deviceId)!!
        val device2 = deviceRepository.findById(saved.deviceId)!!
        
        val updated1 = device1.rename(Name("New Name 1"))
        val updated2 = device2.rename(Name("New Name 2"))
        
        // First update succeeds
        deviceRepository.save(updated1)
        
        // Second update fails
        assertThrows<ConcurrentModificationException> {
            deviceRepository.save(updated2)
        }
    }
    
    @Test
    fun `should find devices by specification`() {
        // Given - create test data
        val premisesId = PremisesId("premises-123")
        
        val sensor1 = createDevice(premisesId, DeviceType.SENSOR, DeviceStatus.ACTIVE)
        val sensor2 = createDevice(premisesId, DeviceType.SENSOR, DeviceStatus.INACTIVE)
        val actuator = createDevice(premisesId, DeviceType.ACTUATOR, DeviceStatus.ACTIVE)
        
        deviceRepository.save(sensor1)
        deviceRepository.save(sensor2)
        deviceRepository.save(actuator)
        
        // When - query with specification
        val spec = DeviceIsActive().and(DeviceIsSensor())
        val result = deviceRepository.findAll(premisesId, spec)
        
        // Then
        assertEquals(1, result.size)
        assertEquals(sensor1.deviceId, result.first().deviceId)
    }
}
```

---

## 💡 Key Takeaways

1. **Repository mediates between domain and data** - Hides infrastructure

2. **One repository per aggregate root** - Respects boundaries

3. **Collection-like interface** - Feels like in-memory collection

4. **Use domain language** - Not database terms

5. **Keep interface simple** - 5-10 methods maximum

6. **Use specifications for queries** - Avoid method explosion

7. **No business logic in repository** - Only persistence

8. **Use optimistic locking** - Prevent lost updates

9. **Test with in-memory implementation** - Fast unit tests

10. **Hide infrastructure types** - Domain objects only in interface

---

## 🎯 Practical Exercise

Refactor your repositories:

1. **Identify anti-patterns** in your repositories
2. **Create proper interfaces** with domain language
3. **Separate infrastructure** into implementation
4. **Add specifications** for flexible queries
5. **Create in-memory version** for testing
6. **Add optimistic locking** for concurrency
7. **Write tests** for both implementations
8. **Measure improvement** - count methods before/after

---

## 📚 What We've Covered

In this chapter, you learned:

✅ Repository anti-patterns to avoid  
✅ What the Repository pattern really is  
✅ Design principles for repositories  
✅ Complete real-world implementation  
✅ Generic vs specific repositories  
✅ Query object pattern  
✅ Unit of work pattern  
✅ Common pitfalls and solutions  
✅ Testing strategies (in-memory and integration)  

---

## 🚀 Next Chapter

Ready to understand the difference between services?

👉 **[Chapter 8: Domain Services vs Application Services - Clear Separation](./08-domain-vs-application-services.md)**

**You'll learn:**
- When to use domain services
- When to use application services
- Clear separation of concerns
- Real examples from SmartHome Hub
- Service anti-patterns

**Reading Time:** 20 minutes  
**Difficulty:** Intermediate  

---

**Questions?** Open an issue in the repository!

**Found this helpful?** Star and share with your team!

---

*"The Repository pattern is the bridge between your rich domain model and the database. Get it right, and everything else falls into place."  
— DDD Community*

