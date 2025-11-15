# Chapter 13: CQRS Pattern - Separating Reads and Writes

**Series:** Building an Enterprise IoT Platform with DDD  
**Part:** 4 of 20 - Advanced Topics  
**Reading Time:** 26 minutes  
**Level:** Advanced  

---

## 📋 Table of Contents

1. [The Problem: One Model for Everything](#the-problem)
2. [What is CQRS?](#what-is-cqrs)
3. [When to Use CQRS](#when-to-use)
4. [Simple CQRS Implementation](#simple-implementation)
5. [Real-World Examples from SmartHome Hub](#real-examples)
6. [Read Models and Projections](#read-models)
7. [Eventual Consistency](#eventual-consistency)
8. [Advanced CQRS Patterns](#advanced-patterns)
9. [Testing CQRS Applications](#testing)

---

## <a name="the-problem"></a>1. The Problem: One Model for Everything

### The Scenario: The Dashboard Performance Nightmare

You're building SmartHome Hub when users complain about slow dashboards:

> **User Complaint:** "The dashboard takes 15 seconds to load! I just want to see my devices and their status. Why is it so slow?!"

You investigate and find a disaster:

```kotlin
// Single model used for EVERYTHING ❌
@Document("devices")
data class Device(
    @Id val id: String,
    val premisesId: String,
    val name: String,
    val serialNumber: String,
    val type: String,
    val status: String,
    val health: String,
    
    // Complex nested structures for writes
    val feeds: List<Feed>,
    val configuration: DeviceConfiguration,
    val metadata: Map<String, Any>,
    
    // Audit fields needed for writes
    val createdBy: String,
    val createdAt: Long,
    val updatedBy: String,
    val updatedAt: Long,
    
    // History for auditing
    val statusHistory: List<StatusChange>,
    val configurationHistory: List<ConfigChange>,
    
    // Relationships
    val zoneId: String?,
    val actors: List<String>,
    val automations: List<String>,
    
    // 50+ more fields for various purposes...
)

// Dashboard query that loads EVERYTHING ❌
@Service
class DashboardService(
    private val deviceRepository: DeviceRepository,
    private val automationRepository: AutomationRepository,
    private val analyticsRepository: AnalyticsRepository
) {
    
    fun getDashboard(premisesId: String): DashboardData {
        // Query 1: Load ALL devices with ALL fields
        val devices = deviceRepository.findByPremisesId(premisesId)
        // Returns 100+ devices with 50+ fields each
        // Loads feeds, history, relationships, everything! ❌
        
        // Query 2: For each device, load automations
        val automations = devices.flatMap { device ->
            automationRepository.findByDeviceId(device.id)
        }
        // 100+ queries! N+1 problem! ❌
        
        // Query 3: For each device, calculate statistics
        val stats = devices.map { device ->
            analyticsRepository.getDeviceStats(device.id)
        }
        // Another 100+ queries! ❌
        
        // Query 4: Join everything together
        val dashboardData = devices.map { device ->
            DashboardDeviceItem(
                id = device.id,
                name = device.name,
                status = device.status,
                // Dashboard only needs 5 fields
                // But loaded 50+ fields for each device! ❌
            )
        }
        
        return DashboardData(
            devices = dashboardData,
            automations = automations,
            stats = stats
        )
    }
}
```

### The Performance Disaster

**Measured impact:**

```
Dashboard Load Time Analysis:
- 100 devices to display
- Each device has 50+ fields
- Each device loads feeds (5-10 per device)
- Each device loads status history (50+ entries)
- Each device loads configuration history

Query 1: Load devices = 2 seconds
Query 2: Load automations (100 queries) = 5 seconds
Query 3: Load analytics (100 queries) = 8 seconds
Total: 15 seconds! 💥

Data Loaded:
- Total fields loaded: 5000+
- Fields actually displayed: 500
- Wasted data: 90%! 💥
```

**Additional problems:**

```kotlin
// Problem 1: Can't optimize for reads
// Dashboard needs simple flat data:
// - Device name
// - Status
// - Last seen time
// - Zone name
// But model is optimized for complex writes

// Problem 2: Complex joins everywhere
@Service
class DeviceListService {
    fun getDeviceList(premisesId: String): List<DeviceListItem> {
        val devices = deviceRepository.findByPremisesId(premisesId)
        
        // Join with zones
        val zones = zoneRepository.findByPremisesId(premisesId)
        
        // Join with actors
        val actors = actorRepository.findByPremisesId(premisesId)
        
        // Manually join everything ❌
        return devices.map { device ->
            val zone = zones.find { it.id == device.zoneId }
            val deviceActors = actors.filter { it.id in device.actors }
            
            DeviceListItem(
                id = device.id,
                name = device.name,
                zoneName = zone?.name ?: "No Zone",
                actorNames = deviceActors.map { it.name }
            )
        }
    }
}

// Problem 3: Write operations suffer too
@Service
class UpdateDeviceService {
    fun updateDeviceStatus(deviceId: String, newStatus: String) {
        // Load entire device with all fields ❌
        val device = deviceRepository.findById(deviceId)
        
        // Just want to update status
        device.status = newStatus
        device.updatedAt = System.currentTimeMillis()
        
        // Save entire device with all fields ❌
        deviceRepository.save(device)
        
        // Inefficient! Just needed to update 2 fields
    }
}
```

### The Real Cost

**Measured impact:**
- 🔴 **Dashboard load time:** 15 seconds (should be < 1 second)
- 🔴 **Database load:** 200+ queries per page load
- 🔴 **Memory usage:** 50MB per dashboard (should be 5MB)
- 🔴 **Bandwidth:** 10MB transferred (should be 100KB)
- 🔴 **User complaints:** 100+ per day
- 🔴 **Mobile users:** Can't use app (too slow)

**Root Cause:** Using same complex model for reads and writes. Reads need simple, optimized, denormalized data. Writes need complex, normalized, validated domain models.

---

## <a name="what-is-cqrs"></a>2. What is CQRS?

### Definition

> **CQRS (Command Query Responsibility Segregation):** A pattern that separates read operations (Queries) from write operations (Commands) using different models optimized for each purpose.
> 
> — Greg Young, CQRS Documents

### Core Concept

```
Traditional Approach (One Model):
┌─────────────────────────────────────────────────────┐
│                                                     │
│              Single Domain Model                    │
│         (Complex, normalized, slow)                 │
│                                                     │
└────────────┬────────────────────────┬───────────────┘
             │                        │
             ▼                        ▼
        ┌─────────┐              ┌─────────┐
        │  Reads  │              │ Writes  │
        │  Slow   │              │  Slow   │
        └─────────┘              └─────────┘

CQRS Approach (Two Models):
┌─────────────────────────────────────────────────────┐
│                                                     │
│          Write Model (Commands)                     │
│     - Complex domain logic                          │
│     - Validation & invariants                       │
│     - Normalized structure                          │
│                                                     │
└────────────┬────────────────────────────────────────┘
             │
             │ Domain Events
             ▼
┌─────────────────────────────────────────────────────┐
│                                                     │
│          Read Model (Queries)                       │
│     - Simple flat structure                         │
│     - Denormalized for speed                        │
│     - Optimized for display                         │
│                                                     │
└────────────┬────────────────────────────────────────┘
             │
             ▼
        ┌─────────┐
        │  Reads  │
        │  Fast!  │
        └─────────┘
```

### Key Principles

#### 1. Commands (Writes)

```kotlin
// Command - Intent to change state
data class RegisterDeviceCommand(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val name: Name,
    val serialNumber: SerialNumber,
    val type: DeviceType,
    val actorId: ActorId
)

// Command handler - Changes state
@Service
class RegisterDeviceCommandHandler(
    private val deviceRepository: DeviceRepository,
    private val eventPublisher: EventPublisher
) {
    fun handle(command: RegisterDeviceCommand): Device {
        // Validate
        // Execute business logic
        // Save to write model
        val device = Device.register(...)
        val saved = deviceRepository.save(device)
        
        // Publish events
        eventPublisher.publish(saved.domainEvents)
        
        return saved
    }
}
```

#### 2. Queries (Reads)

```kotlin
// Query - Request for data
data class GetDeviceDashboardQuery(
    val premisesId: PremisesId
)

// Query result - Optimized DTO
data class DeviceDashboardDto(
    val deviceId: String,
    val deviceName: String,
    val status: String,
    val health: String,
    val lastSeenAt: String?,
    val zoneName: String?,
    val feedCount: Int
)

// Query handler - Returns data
@Service
class GetDeviceDashboardQueryHandler(
    private val readModelRepository: DeviceDashboardReadModelRepository
) {
    fun handle(query: GetDeviceDashboardQuery): List<DeviceDashboardDto> {
        // Query optimized read model (not domain model!)
        return readModelRepository.findByPremisesId(query.premisesId)
    }
}
```

#### 3. Separate Databases (Optional)

```
Commands → Write Database (Normalized)
   └─→ Events
        └─→ Read Database (Denormalized)
              └─→ Queries
```

---

## <a name="when-to-use"></a>3. When to Use CQRS

### Use CQRS When:

✅ **Read and write patterns differ significantly**
- Reads need denormalized data
- Writes need complex validation

✅ **Performance is critical**
- Slow queries impacting UX
- High read-to-write ratio

✅ **Complex query requirements**
- Multiple views of same data
- Reporting and analytics

✅ **Scalability requirements**
- Scale reads independently
- Different databases for read/write

### Don't Use CQRS When:

❌ **Simple CRUD applications**
- No complex business logic
- Reads and writes similar

❌ **Small applications**
- Adds complexity
- Not worth overhead

❌ **Team unfamiliar with pattern**
- Learning curve steep
- Can introduce bugs

❌ **Immediate consistency required**
- CQRS typically eventual
- May not fit requirements

---

## <a name="simple-implementation"></a>4. Simple CQRS Implementation

### Step 1: Separate Commands and Queries

```kotlin
// Commands - No return values (or just ID)
interface Command

data class RegisterDeviceCommand(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val name: Name,
    val serialNumber: SerialNumber,
    val type: DeviceType
) : Command

data class ActivateDeviceCommand(
    val deviceId: DeviceId,
    val actorId: ActorId
) : Command

// Queries - Return data
interface Query<T>

data class GetDeviceQuery(
    val deviceId: DeviceId
) : Query<DeviceDto>

data class GetDeviceListQuery(
    val premisesId: PremisesId,
    val filters: DeviceFilters?
) : Query<List<DeviceListItemDto>>
```

### Step 2: Command Handlers

```kotlin
// Command handler interface
interface CommandHandler<C : Command> {
    fun handle(command: C)
}

// Implementation
@Service
class RegisterDeviceCommandHandler(
    private val deviceRepository: DeviceRepository,
    private val policy: DeviceRegistrationPolicy,
    private val eventPublisher: EventPublisher
) : CommandHandler<RegisterDeviceCommand> {
    
    override fun handle(command: RegisterDeviceCommand) {
        // Validate with policy
        val policyResult = policy.evaluate(command)
        if (!policyResult.isValid) {
            throw PolicyViolationException(policyResult.violations)
        }
        
        // Execute business logic (domain model)
        val device = Device.register(
            deviceId = command.deviceId,
            premisesId = command.premisesId,
            name = command.name,
            serialNumber = command.serialNumber,
            type = command.type
        )
        
        // Save to write model
        deviceRepository.save(device)
        
        // Publish events (will update read model)
        eventPublisher.publish(device.domainEvents)
    }
}
```

### Step 3: Query Handlers

```kotlin
// Query handler interface
interface QueryHandler<Q : Query<R>, R> {
    fun handle(query: Q): R
}

// Read model (optimized for queries)
@Document("device_list_read_model")
data class DeviceListReadModel(
    @Id val deviceId: String,
    val premisesId: String,
    val deviceName: String,
    val status: String,
    val health: String,
    val deviceType: String,
    val zoneName: String?,
    val lastSeenAt: Long?,
    val feedCount: Int,
    val isOnline: Boolean,
    val activeAutomations: Int
    // Flat, denormalized, optimized for display
)

// Query handler implementation
@Service
class GetDeviceListQueryHandler(
    private val readModelRepository: DeviceListReadModelRepository
) : QueryHandler<GetDeviceListQuery, List<DeviceListItemDto>> {
    
    override fun handle(query: GetDeviceListQuery): List<DeviceListItemDto> {
        // Query read model (fast!)
        val readModels = if (query.filters != null) {
            readModelRepository.findByPremisesIdWithFilters(
                query.premisesId.value,
                query.filters
            )
        } else {
            readModelRepository.findByPremisesId(query.premisesId.value)
        }
        
        // Map to DTO
        return readModels.map { rm ->
            DeviceListItemDto(
                deviceId = rm.deviceId,
                name = rm.deviceName,
                status = rm.status,
                health = rm.health,
                type = rm.deviceType,
                zoneName = rm.zoneName,
                lastSeenAt = rm.lastSeenAt?.let { Instant.ofEpochMilli(it).toString() },
                isOnline = rm.isOnline,
                feedCount = rm.feedCount
            )
        }
    }
}
```

### Step 4: Update Read Model from Events

```kotlin
// Event handler updates read model
@Component
class DeviceRegisteredReadModelUpdater(
    private val readModelRepository: DeviceListReadModelRepository,
    private val zoneRepository: ZoneRepository
) {
    
    @EventListener
    fun handle(event: DeviceRegistered) {
        // Get zone name for denormalization
        val zoneName = event.zoneId?.let { zoneId ->
            zoneRepository.findById(zoneId)?.name
        }
        
        // Create read model
        val readModel = DeviceListReadModel(
            deviceId = event.deviceId.value,
            premisesId = event.premisesId.value,
            deviceName = event.name.value,
            status = DeviceStatus.PENDING.name,
            health = DeviceHealth.ONLINE.name,
            deviceType = event.type.name,
            zoneName = zoneName,
            lastSeenAt = event.occurredAt.toEpochMilli(),
            feedCount = 0,
            isOnline = true,
            activeAutomations = 0
        )
        
        readModelRepository.save(readModel)
    }
    
    @EventListener
    fun handle(event: DeviceActivated) {
        // Update existing read model
        val readModel = readModelRepository.findById(event.deviceId.value)
            ?: return
        
        readModelRepository.save(
            readModel.copy(
                status = DeviceStatus.ACTIVE.name,
                lastSeenAt = event.occurredAt.toEpochMilli()
            )
        )
    }
    
    @EventListener
    fun handle(event: FeedAddedToDevice) {
        // Update feed count
        val readModel = readModelRepository.findById(event.deviceId.value)
            ?: return
        
        readModelRepository.save(
            readModel.copy(feedCount = readModel.feedCount + 1)
        )
    }
}
```

### Step 5: Command and Query Bus (Optional)

```kotlin
// Command bus
interface CommandBus {
    fun <C : Command> send(command: C)
}

@Service
class SimpleCommandBus(
    private val handlers: Map<Class<out Command>, CommandHandler<*>>
) : CommandBus {
    
    override fun <C : Command> send(command: C) {
        val handler = handlers[command.javaClass]
            ?: throw IllegalArgumentException("No handler for ${command.javaClass}")
        
        @Suppress("UNCHECKED_CAST")
        (handler as CommandHandler<C>).handle(command)
    }
}

// Query bus
interface QueryBus {
    fun <Q : Query<R>, R> send(query: Q): R
}

@Service
class SimpleQueryBus(
    private val handlers: Map<Class<out Query<*>>, QueryHandler<*, *>>
) : QueryBus {
    
    override fun <Q : Query<R>, R> send(query: Q): R {
        val handler = handlers[query.javaClass]
            ?: throw IllegalArgumentException("No handler for ${query.javaClass}")
        
        @Suppress("UNCHECKED_CAST")
        return (handler as QueryHandler<Q, R>).handle(query)
    }
}

// Usage in controller
@RestController
@RequestMapping("/api/devices")
class DeviceController(
    private val commandBus: CommandBus,
    private val queryBus: QueryBus
) {
    
    @PostMapping
    fun registerDevice(@RequestBody request: RegisterDeviceRequest): ResponseEntity<Unit> {
        val command = RegisterDeviceCommand(
            deviceId = DeviceId.generate(),
            premisesId = PremisesId(request.premisesId),
            name = Name(request.name),
            serialNumber = SerialNumber(request.serialNumber),
            type = DeviceType.valueOf(request.type)
        )
        
        commandBus.send(command)
        
        return ResponseEntity.accepted().build()
    }
    
    @GetMapping
    fun getDeviceList(
        @RequestParam premisesId: String
    ): ResponseEntity<List<DeviceListItemDto>> {
        val query = GetDeviceListQuery(PremisesId(premisesId))
        val result = queryBus.send(query)
        
        return ResponseEntity.ok(result)
    }
}
```

---

## <a name="real-examples"></a>5. Real-World Examples from SmartHome Hub

### Example 1: Device Dashboard (Before and After)

**Before CQRS (Slow):**

```kotlin
// 15 seconds to load! ❌
@Service
class DashboardService(
    private val deviceRepository: DeviceRepository
) {
    fun getDashboard(premisesId: PremisesId): DashboardData {
        // Load full domain model
        val devices = deviceRepository.findByPremisesId(premisesId)
        // 100 devices × 50 fields = 5000 fields loaded!
        
        return DashboardData(
            devices = devices.map { device ->
                DeviceSummary(
                    id = device.deviceId.value,
                    name = device.getName().value,
                    status = device.getStatus().name
                    // Only need 3 fields but loaded 50!
                )
            }
        )
    }
}
```

**After CQRS (Fast):**

```kotlin
// < 1 second to load! ✅
@Service
class GetDashboardQueryHandler(
    private val dashboardReadModel: DashboardReadModelRepository
) : QueryHandler<GetDashboardQuery, DashboardData> {
    
    override fun handle(query: GetDashboardQuery): DashboardData {
        // Query optimized read model
        val dashboard = dashboardReadModel.findByPremisesId(query.premisesId.value)
            ?: return DashboardData.empty()
        
        return DashboardData(
            deviceSummary = dashboard.deviceSummary,
            recentActivity = dashboard.recentActivity,
            alerts = dashboard.alerts,
            statistics = dashboard.statistics
        )
    }
}

// Read model - Single document with all dashboard data
@Document("dashboard_read_model")
data class DashboardReadModel(
    @Id val premisesId: String,
    val deviceSummary: DeviceSummary,
    val recentActivity: List<ActivityItem>,
    val alerts: List<AlertItem>,
    val statistics: DashboardStatistics,
    val lastUpdated: Long
)

// Updated by event handlers
@Component
class DashboardReadModelUpdater(
    private val repository: DashboardReadModelRepository
) {
    
    @EventListener
    fun handle(event: DeviceRegistered) {
        val dashboard = repository.findById(event.premisesId.value)
            ?: createNew(event.premisesId)
        
        repository.save(
            dashboard.copy(
                deviceSummary = dashboard.deviceSummary.addDevice(event),
                lastUpdated = Instant.now().toEpochMilli()
            )
        )
    }
    
    @EventListener
    fun handle(event: FeedValueChanged) {
        val dashboard = repository.findById(event.premisesId.value)
            ?: return
        
        repository.save(
            dashboard.copy(
                recentActivity = dashboard.recentActivity.add(
                    ActivityItem.fromFeedChange(event)
                ),
                lastUpdated = Instant.now().toEpochMilli()
            )
        )
    }
}
```

**Performance improvement:**
- Before: 15 seconds, 200+ queries
- After: 800ms, 1 query
- **18x faster!** ✅

### Example 2: Device List with Filters

```kotlin
// Query with filters
data class GetFilteredDeviceListQuery(
    val premisesId: PremisesId,
    val statusFilter: DeviceStatus?,
    val typeFilter: DeviceType?,
    val zoneFilter: ZoneId?,
    val searchTerm: String?,
    val sortBy: SortField,
    val sortOrder: SortOrder,
    val page: Int,
    val pageSize: Int
) : Query<PagedResult<DeviceListItemDto>>

// Optimized read model with indexes
@Document("device_list_read_model")
@CompoundIndexes(
    CompoundIndex(def = "{'premisesId': 1, 'status': 1, 'deviceType': 1}"),
    CompoundIndex(def = "{'premisesId': 1, 'zoneName': 1}"),
    CompoundIndex(def = "{'premisesId': 1, 'deviceName': 'text'}")
)
data class DeviceListReadModel(
    @Id val deviceId: String,
    @Indexed val premisesId: String,
    @Indexed val status: String,
    @Indexed val deviceType: String,
    val deviceName: String,
    val health: String,
    val zoneName: String?,
    val lastSeenAt: Long?,
    val feedCount: Int,
    val isOnline: Boolean
)

// Query handler with optimized queries
@Service
class GetFilteredDeviceListQueryHandler(
    private val readModelRepository: DeviceListReadModelRepository
) : QueryHandler<GetFilteredDeviceListQuery, PagedResult<DeviceListItemDto>> {
    
    override fun handle(query: GetFilteredDeviceListQuery): PagedResult<DeviceListItemDto> {
        // Build query with filters
        val criteria = buildCriteria(query)
        val mongoQuery = Query(criteria)
            .with(Sort.by(getSortDirection(query.sortOrder), query.sortBy.name))
            .skip((query.page * query.pageSize).toLong())
            .limit(query.pageSize)
        
        // Execute single optimized query
        val items = readModelRepository.findAll(mongoQuery)
        val total = readModelRepository.count(Query(criteria))
        
        return PagedResult(
            items = items.map { it.toDto() },
            page = query.page,
            pageSize = query.pageSize,
            totalItems = total,
            totalPages = (total / query.pageSize).toInt() + 1
        )
    }
    
    private fun buildCriteria(query: GetFilteredDeviceListQuery): Criteria {
        var criteria = Criteria.where("premisesId").`is`(query.premisesId.value)
        
        query.statusFilter?.let {
            criteria = criteria.and("status").`is`(it.name)
        }
        
        query.typeFilter?.let {
            criteria = criteria.and("deviceType").`is`(it.name)
        }
        
        query.zoneFilter?.let {
            criteria = criteria.and("zoneId").`is`(it.value)
        }
        
        query.searchTerm?.let {
            criteria = criteria.and("deviceName").regex(it, "i")
        }
        
        return criteria
    }
}
```

### Example 3: Analytics Dashboard (Multiple Read Models)

```kotlin
// Different read models for different views

// 1. Real-time device status
@Document("device_status_read_model")
data class DeviceStatusReadModel(
    @Id val deviceId: String,
    val premisesId: String,
    val status: String,
    val health: String,
    val lastSeenAt: Long?,
    val currentValues: Map<String, Int>  // feedId -> value
)

// 2. Historical data for charts
@Document("device_history_read_model")
data class DeviceHistoryReadModel(
    @Id val id: String,
    val deviceId: String,
    val premisesId: String,
    val timestamp: Long,
    val feedValues: Map<String, Int>,
    val status: String,
    val health: String
)

// 3. Aggregated statistics
@Document("device_statistics_read_model")
data class DeviceStatisticsReadModel(
    @Id val premisesId: String,
    val totalDevices: Int,
    val activeDevices: Int,
    val onlineDevices: Int,
    val devicesByType: Map<String, Int>,
    val devicesByZone: Map<String, Int>,
    val averageUptime: Double,
    val lastCalculated: Long
)

// Different query handlers for different needs
@Service
class GetDeviceStatusQueryHandler(
    private val statusReadModel: DeviceStatusReadModelRepository
) : QueryHandler<GetDeviceStatusQuery, DeviceStatusDto> {
    override fun handle(query: GetDeviceStatusQuery): DeviceStatusDto {
        val status = statusReadModel.findById(query.deviceId.value)
            ?: throw DeviceNotFoundException(query.deviceId)
        
        return status.toDto()
    }
}

@Service
class GetDeviceHistoryQueryHandler(
    private val historyReadModel: DeviceHistoryReadModelRepository
) : QueryHandler<GetDeviceHistoryQuery, List<DeviceHistoryDto>> {
    override fun handle(query: GetDeviceHistoryQuery): List<DeviceHistoryDto> {
        val history = historyReadModel.findByDeviceIdAndTimestampBetween(
            deviceId = query.deviceId.value,
            from = query.from.toEpochMilli(),
            to = query.to.toEpochMilli()
        )
        
        return history.map { it.toDto() }
    }
}

@Service
class GetPremisesStatisticsQueryHandler(
    private val statsReadModel: DeviceStatisticsReadModelRepository
) : QueryHandler<GetPremisesStatisticsQuery, PremisesStatisticsDto> {
    override fun handle(query: GetPremisesStatisticsQuery): PremisesStatisticsDto {
        val stats = statsReadModel.findById(query.premisesId.value)
            ?: return PremisesStatisticsDto.empty()
        
        return stats.toDto()
    }
}
```

---

## <a name="read-models"></a>6. Read Models and Projections

### Creating Read Models

```kotlin
// Projector - Builds read models from events
@Component
class DeviceListProjector(
    private val readModelRepository: DeviceListReadModelRepository,
    private val zoneRepository: ZoneRepository
) {
    
    @EventListener
    fun project(event: DeviceRegistered) {
        val zoneName = event.zoneId?.let { zoneId ->
            zoneRepository.findById(zoneId)?.name
        }
        
        val readModel = DeviceListReadModel(
            deviceId = event.deviceId.value,
            premisesId = event.premisesId.value,
            deviceName = event.name.value,
            status = DeviceStatus.PENDING.name,
            health = DeviceHealth.ONLINE.name,
            deviceType = event.type.name,
            zoneName = zoneName,
            lastSeenAt = event.occurredAt.toEpochMilli(),
            feedCount = 0,
            isOnline = true,
            activeAutomations = 0
        )
        
        readModelRepository.save(readModel)
    }
    
    @EventListener
    fun project(event: DeviceActivated) {
        updateReadModel(event.deviceId) { readModel ->
            readModel.copy(
                status = DeviceStatus.ACTIVE.name,
                lastSeenAt = event.occurredAt.toEpochMilli()
            )
        }
    }
    
    @EventListener
    fun project(event: DeviceMovedToZone) {
        val zoneName = zoneRepository.findById(event.newZoneId)?.name
        
        updateReadModel(event.deviceId) { readModel ->
            readModel.copy(zoneName = zoneName)
        }
    }
    
    @EventListener
    fun project(event: FeedAddedToDevice) {
        updateReadModel(event.deviceId) { readModel ->
            readModel.copy(feedCount = readModel.feedCount + 1)
        }
    }
    
    @EventListener
    fun project(event: FeedValueChanged) {
        updateReadModel(event.deviceId) { readModel ->
            readModel.copy(
                lastSeenAt = event.occurredAt.toEpochMilli(),
                isOnline = true
            )
        }
    }
    
    private fun updateReadModel(
        deviceId: DeviceId,
        update: (DeviceListReadModel) -> DeviceListReadModel
    ) {
        val readModel = readModelRepository.findById(deviceId.value)
            ?: return
        
        val updated = update(readModel)
        readModelRepository.save(updated)
    }
}
```

### Rebuilding Read Models

```kotlin
// Rebuild read models from event store
@Service
class ReadModelRebuilder(
    private val eventStore: EventStore,
    private val projectors: List<Projector>
) {
    
    fun rebuildAll() {
        logger.info("Starting read model rebuild...")
        
        // Clear existing read models
        projectors.forEach { it.clear() }
        
        // Replay all events
        val allEvents = eventStore.getAllEvents()
        
        allEvents.forEach { storedEvent ->
            val domainEvent = deserializeEvent(storedEvent)
            projectors.forEach { projector ->
                projector.project(domainEvent)
            }
        }
        
        logger.info("Read model rebuild completed. Processed ${allEvents.size} events")
    }
    
    fun rebuildForAggregate(aggregateId: String) {
        logger.info("Rebuilding read models for aggregate $aggregateId")
        
        val events = eventStore.getEvents(aggregateId)
        
        events.forEach { storedEvent ->
            val domainEvent = deserializeEvent(storedEvent)
            projectors.forEach { projector ->
                projector.project(domainEvent)
            }
        }
    }
}
```

---

## <a name="eventual-consistency"></a>7. Eventual Consistency

### Understanding the Delay

```kotlin
// Write side (immediate)
@RestController
class DeviceController(
    private val commandBus: CommandBus
) {
    
    @PostMapping("/devices")
    fun registerDevice(@RequestBody request: RegisterDeviceRequest): ResponseEntity<DeviceRegisteredResponse> {
        val deviceId = DeviceId.generate()
        
        val command = RegisterDeviceCommand(
            deviceId = deviceId,
            premisesId = PremisesId(request.premisesId),
            name = Name(request.name),
            serialNumber = SerialNumber(request.serialNumber),
            type = DeviceType.valueOf(request.type)
        )
        
        // Command executed (write model updated immediately)
        commandBus.send(command)
        
        // Return device ID
        return ResponseEntity
            .accepted()
            .body(DeviceRegisteredResponse(deviceId.value))
        // Read model NOT YET updated! ⚠️
    }
    
    @GetMapping("/devices/{deviceId}")
    fun getDevice(@PathVariable deviceId: String): ResponseEntity<DeviceDto> {
        val query = GetDeviceQuery(DeviceId(deviceId))
        
        // Query read model
        val device = queryBus.send(query)
        
        // May not find device immediately after registration!
        // (if read model not yet updated)
        return ResponseEntity.ok(device)
    }
}
```

### Handling Eventual Consistency

**Strategy 1: Return Command Result**

```kotlin
@RestController
class DeviceController(
    private val commandHandler: RegisterDeviceCommandHandler,
    private val queryHandler: GetDeviceQueryHandler
) {
    
    @PostMapping("/devices")
    fun registerDevice(@RequestBody request: RegisterDeviceRequest): ResponseEntity<DeviceDto> {
        val command = RegisterDeviceCommand(...)
        
        // Execute command and get result
        val device = commandHandler.handle(command)
        
        // Build DTO from write model (immediate, consistent)
        val dto = DeviceDto.from(device)
        
        return ResponseEntity.ok(dto)
        // No eventual consistency issue! ✅
    }
}
```

**Strategy 2: Polling**

```kotlin
// Client polls until read model updated
@RestController
class DeviceController(
    private val commandBus: CommandBus,
    private val queryHandler: GetDeviceQueryHandler
) {
    
    @PostMapping("/devices")
    fun registerDevice(@RequestBody request: RegisterDeviceRequest): ResponseEntity<DeviceRegisteredResponse> {
        val deviceId = DeviceId.generate()
        val command = RegisterDeviceCommand(deviceId, ...)
        
        commandBus.send(command)
        
        // Return 202 Accepted with location header
        return ResponseEntity
            .accepted()
            .location(URI.create("/devices/${deviceId.value}"))
            .body(DeviceRegisteredResponse(deviceId.value))
    }
    
    @GetMapping("/devices/{deviceId}")
    fun getDevice(@PathVariable deviceId: String): ResponseEntity<DeviceDto> {
        val query = GetDeviceQuery(DeviceId(deviceId))
        
        return try {
            val device = queryHandler.handle(query)
            ResponseEntity.ok(device)
        } catch (e: DeviceNotFoundException) {
            // Not yet in read model
            ResponseEntity.status(HttpStatus.ACCEPTED).build()
            // Client will poll again
        }
    }
}
```

**Strategy 3: Versioning**

```kotlin
// Track version in read model
@Document("device_read_model")
data class DeviceReadModel(
    @Id val deviceId: String,
    val version: Long,  // Event version
    // ... other fields
)

// Event includes version
data class DeviceActivated(
    val deviceId: DeviceId,
    val eventVersion: Long,  // Aggregate version
    // ...
) : DeviceEvent()

// Client can check version
@GetMapping("/devices/{deviceId}")
fun getDevice(
    @PathVariable deviceId: String,
    @RequestParam(required = false) minVersion: Long?
): ResponseEntity<DeviceDto> {
    val query = GetDeviceQuery(DeviceId(deviceId))
    val device = queryHandler.handle(query)
    
    if (minVersion != null && device.version < minVersion) {
        // Read model not yet updated to required version
        return ResponseEntity.status(HttpStatus.ACCEPTED).build()
    }
    
    return ResponseEntity.ok(device)
}
```

---

## <a name="advanced-patterns"></a>8. Advanced CQRS Patterns

### Pattern 1: Separate Databases

```kotlin
// Write database - MongoDB (normalized)
@Configuration
class WriteDataSourceConfig {
    @Bean
    @Primary
    fun writeMongoTemplate(): MongoTemplate {
        return MongoTemplate(
            MongoClients.create("mongodb://localhost:27017"),
            "smarthome_write"
        )
    }
}

// Read database - MongoDB (denormalized)
@Configuration
class ReadDataSourceConfig {
    @Bean
    fun readMongoTemplate(): MongoTemplate {
        return MongoTemplate(
            MongoClients.create("mongodb://localhost:27017"),
            "smarthome_read"
        )
    }
}

// Or different database types
@Configuration
class ReadDataSourceConfig {
    @Bean
    fun readPostgresTemplate(): JdbcTemplate {
        // Use Postgres for complex queries
        return JdbcTemplate(...)
    }
}
```

### Pattern 2: Multiple Read Models

```kotlin
// Different read models for different use cases

// 1. List view (simple, fast)
@Document("device_list_view")
data class DeviceListView(
    @Id val deviceId: String,
    val name: String,
    val status: String,
    val lastSeen: Long?
)

// 2. Detail view (comprehensive)
@Document("device_detail_view")
data class DeviceDetailView(
    @Id val deviceId: String,
    val name: String,
    val status: String,
    val health: String,
    val feeds: List<FeedView>,
    val history: List<StatusChange>,
    val configuration: DeviceConfigView
)

// 3. Analytics view (aggregated)
@Document("device_analytics_view")
data class DeviceAnalyticsView(
    @Id val deviceId: String,
    val uptimePercentage: Double,
    val avgResponseTime: Long,
    val totalEvents: Long,
    val errorCount: Long,
    val lastCalculated: Long
)

// Each updated by separate projectors
@Component
class DeviceListViewProjector {
    @EventListener
    fun project(event: DeviceEvent) {
        // Update list view
    }
}

@Component
class DeviceDetailViewProjector {
    @EventListener
    fun project(event: DeviceEvent) {
        // Update detail view
    }
}

@Component
class DeviceAnalyticsViewProjector {
    @EventListener
    fun project(event: DeviceEvent) {
        // Update analytics view
    }
}
```

### Pattern 3: Snapshotting

```kotlin
// Snapshot read model periodically
@Service
class ReadModelSnapshotter(
    private val eventStore: EventStore,
    private val snapshotRepository: ReadModelSnapshotRepository
) {
    
    @Scheduled(fixedDelay = 3600000)  // Every hour
    fun createSnapshots() {
        // Get all aggregate IDs
        val aggregateIds = eventStore.getAllAggregateIds()
        
        aggregateIds.forEach { aggregateId ->
            createSnapshot(aggregateId)
        }
    }
    
    private fun createSnapshot(aggregateId: String) {
        // Get current read model state
        val readModel = readModelRepository.findById(aggregateId)
            ?: return
        
        // Get current version
        val version = eventStore.getStreamVersion(aggregateId)
        
        // Save snapshot
        val snapshot = ReadModelSnapshot(
            aggregateId = aggregateId,
            readModel = readModel,
            version = version,
            timestamp = Instant.now()
        )
        
        snapshotRepository.save(snapshot)
    }
}

// Rebuild from snapshot instead of replaying all events
@Service
class ReadModelRebuilder {
    fun rebuildFromSnapshot(aggregateId: String) {
        // Load latest snapshot
        val snapshot = snapshotRepository.findLatest(aggregateId)
            ?: return rebuildFromScratch(aggregateId)
        
        // Start from snapshot
        var readModel = snapshot.readModel
        
        // Replay only events after snapshot
        val events = eventStore.getEventsAfterVersion(
            aggregateId,
            snapshot.version
        )
        
        events.forEach { event ->
            readModel = projector.project(readModel, event)
        }
        
        readModelRepository.save(readModel)
    }
}
```

---

## <a name="testing"></a>9. Testing CQRS Applications

### Testing Command Handlers

```kotlin
class RegisterDeviceCommandHandlerTest {
    
    private lateinit var handler: RegisterDeviceCommandHandler
    private lateinit var deviceRepository: DeviceRepository
    private lateinit var eventPublisher: EventPublisher
    
    @BeforeEach
    fun setup() {
        deviceRepository = InMemoryDeviceRepository()
        eventPublisher = InMemoryEventPublisher()
        handler = RegisterDeviceCommandHandler(
            deviceRepository,
            mockk(relaxed = true),
            eventPublisher
        )
    }
    
    @Test
    fun `should register device and publish event`() {
        // Given
        val command = RegisterDeviceCommand(
            deviceId = DeviceId.generate(),
            premisesId = PremisesId("premises-123"),
            name = Name("Test Device"),
            serialNumber = SerialNumber("SN12345"),
            type = DeviceType.SENSOR
        )
        
        // When
        handler.handle(command)
        
        // Then
        val device = deviceRepository.findById(command.deviceId)
        assertNotNull(device)
        assertEquals(command.name, device?.getName())
        
        // Verify event published
        val events = eventPublisher.getPublishedEvents()
        assertTrue(events.any { it is DeviceRegistered })
    }
}
```

### Testing Query Handlers

```kotlin
class GetDeviceListQueryHandlerTest {
    
    private lateinit var handler: GetDeviceListQueryHandler
    private lateinit var readModelRepository: DeviceListReadModelRepository
    
    @BeforeEach
    fun setup() {
        readModelRepository = InMemoryDeviceListReadModelRepository()
        handler = GetDeviceListQueryHandler(readModelRepository)
    }
    
    @Test
    fun `should return device list from read model`() {
        // Given
        val premisesId = PremisesId("premises-123")
        val readModel1 = DeviceListReadModel(
            deviceId = "device-1",
            premisesId = premisesId.value,
            deviceName = "Device 1",
            status = "ACTIVE",
            health = "ONLINE",
            deviceType = "SENSOR",
            zoneName = "Living Room",
            lastSeenAt = Instant.now().toEpochMilli(),
            feedCount = 5,
            isOnline = true,
            activeAutomations = 2
        )
        readModelRepository.save(readModel1)
        
        val query = GetDeviceListQuery(premisesId)
        
        // When
        val result = handler.handle(query)
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Device 1", result[0].name)
    }
}
```

### Testing Projectors

```kotlin
class DeviceListProjectorTest {
    
    private lateinit var projector: DeviceListProjector
    private lateinit var readModelRepository: DeviceListReadModelRepository
    
    @BeforeEach
    fun setup() {
        readModelRepository = InMemoryDeviceListReadModelRepository()
        projector = DeviceListProjector(
            readModelRepository,
            mockk(relaxed = true)
        )
    }
    
    @Test
    fun `should create read model when device registered`() {
        // Given
        val event = DeviceRegistered(
            deviceId = DeviceId("device-123"),
            premisesId = PremisesId("premises-123"),
            name = Name("Test Device"),
            serialNumber = SerialNumber("SN12345"),
            type = DeviceType.SENSOR,
            occurredAt = Instant.now()
        )
        
        // When
        projector.project(event)
        
        // Then
        val readModel = readModelRepository.findById("device-123")
        assertNotNull(readModel)
        assertEquals("Test Device", readModel?.deviceName)
        assertEquals("PENDING", readModel?.status)
    }
    
    @Test
    fun `should update read model when device activated`() {
        // Given - existing read model
        val readModel = DeviceListReadModel(
            deviceId = "device-123",
            premisesId = "premises-123",
            deviceName = "Test Device",
            status = "PENDING",
            // ... other fields
        )
        readModelRepository.save(readModel)
        
        val event = DeviceActivated(
            deviceId = DeviceId("device-123"),
            occurredAt = Instant.now()
        )
        
        // When
        projector.project(event)
        
        // Then
        val updated = readModelRepository.findById("device-123")
        assertEquals("ACTIVE", updated?.status)
    }
}
```

### Integration Testing

```kotlin
@SpringBootTest
@Testcontainers
class CQRSIntegrationTest {
    
    @Container
    val mongodb = MongoDBContainer("mongo:6.0")
    
    @Autowired
    lateinit var commandBus: CommandBus
    
    @Autowired
    lateinit var queryBus: QueryBus
    
    @Autowired
    lateinit var readModelRepository: DeviceListReadModelRepository
    
    @Test
    fun `should update read model after command execution`() {
        // Given
        val deviceId = DeviceId.generate()
        val command = RegisterDeviceCommand(
            deviceId = deviceId,
            premisesId = PremisesId("premises-123"),
            name = Name("Test Device"),
            serialNumber = SerialNumber("SN12345"),
            type = DeviceType.SENSOR
        )
        
        // When - send command
        commandBus.send(command)
        
        // Wait for eventual consistency
        await().atMost(Duration.ofSeconds(5)).until {
            readModelRepository.findById(deviceId.value) != null
        }
        
        // Then - query read model
        val query = GetDeviceQuery(deviceId)
        val result = queryBus.send(query)
        
        assertNotNull(result)
        assertEquals("Test Device", result.name)
    }
}
```

---

## 💡 Key Takeaways

1. **CQRS separates reads and writes** - Different models for different purposes

2. **Read models optimized for queries** - Denormalized, fast, simple

3. **Write model enforces business rules** - Normalized, validated, complex

4. **Eventual consistency is OK** - Read models updated asynchronously

5. **Multiple read models possible** - Different views for different needs

6. **Events update read models** - Projectors build read models from events

7. **Can use different databases** - Optimize each for its purpose

8. **Performance gains significant** - 10-20x faster queries

9. **Complexity increases** - Only use when benefits outweigh costs

10. **Test each side separately** - Commands, queries, and projectors

---

## 🎯 Practical Exercise

Add CQRS to your system:

1. **Identify slow queries** that need optimization
2. **Separate commands and queries** in your API
3. **Create read models** optimized for those queries
4. **Build projectors** to update read models from events
5. **Update controllers** to use command and query buses
6. **Test performance** before and after
7. **Measure improvement** - query time, database load
8. **Monitor eventual consistency** - how long until read model updates

---

## 📚 What We've Covered

In this chapter, you learned:

✅ The problem with single model for reads and writes  
✅ What CQRS is and how it works  
✅ When to use (and not use) CQRS  
✅ Simple CQRS implementation  
✅ Real examples from SmartHome Hub  
✅ Creating and updating read models  
✅ Handling eventual consistency  
✅ Advanced patterns (separate DBs, snapshots)  
✅ Testing CQRS applications  

---

## 🚀 Next Chapter

Ready for complete audit trails?

👉 **[Chapter 14: Event Sourcing - Audit Trail and Time Travel](./14-event-sourcing.md)**

**You'll learn:**
- What event sourcing is
- Event store implementation
- Rebuilding state from events
- Time travel queries
- Real event sourcing examples

**Reading Time:** 24 minutes  
**Difficulty:** Advanced  

---

**Questions?** Open an issue in the repository!

**Found this helpful?** Star and share with your team!

---

*"CQRS is simply the creation of two objects where there was previously only one."  
— Greg Young*

