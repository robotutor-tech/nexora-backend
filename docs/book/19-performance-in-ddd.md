# Chapter 19
# Performance Considerations in DDD

> *"Premature optimization is the root of all evil."*  
> — Donald Knuth

---

## In This Chapter

While DDD provides excellent structure and maintainability, improper implementation can lead to performance issues—loading entire aggregates when only a few fields are needed, N+1 queries, and missing caching. This chapter provides practical strategies for optimizing DDD applications without sacrificing domain model integrity.

**What You'll Learn:**
- The production performance bottleneck problem
- Identifying performance issues with profiling
- Optimizing aggregate loading with projections
- Caching strategies: in-memory, Spring Cache, CQRS
- Query optimization: indexes, batch loading, avoiding N+1
- Event sourcing performance with snapshots
- Database optimization techniques
- Performance monitoring and metrics
- Load testing strategies

---

## Table of Contents

1. The Problem: Performance Bottlenecks
2. Identifying Performance Issues
3. Optimizing Aggregates
4. Caching Strategies
5. Query Optimization
6. Event Sourcing Performance
7. Database Optimization
8. Measuring and Monitoring
9. Chapter Summary

---

## 1. The Problem: Performance Bottlenecks

### The Scenario: The Production Slowdown

Your SmartHome Hub is in production when disaster strikes:

> **Operations Alert:** "Dashboard loading in 15 seconds! API timeouts! Users complaining! Database at 90% CPU! System is crawling!" 💥

You investigate and find multiple issues:

```kotlin
// Problem 1: Loading entire aggregate ❌
@Service
class GetDeviceUseCase(
    private val deviceRepository: DeviceRepository
) {
    fun execute(deviceId: DeviceId): DeviceDto {
        // Loads EVERYTHING ❌
        val device = deviceRepository.findById(deviceId)
        
        // Returns simple DTO
        return DeviceDto(
            id = device.deviceId.value,
            name = device.getName().value,
            status = device.getStatus().name
        )
        
        // Loaded 50 fields, used 3! 💥
        // Loaded all feeds, didn't use them! 💥
        // Loaded all history, didn't use it! 💥
    }
}

// Problem 2: N+1 Query Problem ❌
@Service
class GetDeviceListUseCase(
    private val deviceRepository: DeviceRepository,
    private val zoneRepository: ZoneRepository
) {
    fun execute(premisesId: PremisesId): List<DeviceListDto> {
        // Query 1: Get all devices
        val devices = deviceRepository.findByPremisesId(premisesId)
        // 100 devices loaded
        
        // Query 2-101: Get zone for each device ❌
        return devices.map { device ->
            val zone = zoneRepository.findById(device.zoneId)  // N+1! 💥
            
            DeviceListDto(
                id = device.deviceId.value,
                name = device.getName().value,
                zoneName = zone?.name ?: "Unknown"
            )
        }
        // 101 queries! 💥
    }
}

// Problem 3: No caching ❌
@Service
class GetPremisesUseCase(
    private val premisesRepository: PremisesRepository
) {
    fun execute(premisesId: PremisesId): PremisesDto {
        // Query database every time ❌
        val premises = premisesRepository.findById(premisesId)
        
        return PremisesDto.from(premises)
        
        // Called 1000 times per second for same premises! 💥
    }
}

// Problem 4: Event sourcing replay ❌
@Service
class LoadDeviceFromEventsUseCase(
    private val eventStore: EventStore
) {
    fun execute(deviceId: DeviceId): Device {
        // Load all events
        val events = eventStore.getEvents(deviceId.value)
        // 10,000 events! 💥
        
        // Replay all events
        var device: Device? = null
        events.forEach { event ->
            device = applyEvent(device, event)
        }
        // Takes 5 seconds! 💥
        
        return device!!
    }
}
```

### The Real Impact

**Measured metrics:**
- 🔴 **Dashboard:** 15 seconds load time (should be < 1s)
- 🔴 **API response:** 3-8 seconds (should be < 200ms)
- 🔴 **Database queries:** 1000+ per page load
- 🔴 **Memory usage:** 4GB per request
- 🔴 **CPU:** 90% usage constantly
- 🔴 **User complaints:** 500+ tickets

---

## <a name="identifying-issues"></a>2. Identifying Performance Issues

### Performance Profiling

```kotlin
// Add performance monitoring
@Component
class PerformanceMonitor {
    
    private val logger = LoggerFactory.getLogger(javaClass)
    
    fun <T> measure(operation: String, block: () -> T): T {
        val start = System.currentTimeMillis()
        
        try {
            return block()
        } finally {
            val duration = System.currentTimeMillis() - start
            
            if (duration > 1000) {
                logger.warn("SLOW: $operation took ${duration}ms")
            } else {
                logger.info("$operation took ${duration}ms")
            }
        }
    }
}

// Usage
@Service
class GetDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val monitor: PerformanceMonitor
) {
    fun execute(deviceId: DeviceId): DeviceDto {
        return monitor.measure("GetDevice") {
            val device = deviceRepository.findById(deviceId)
            DeviceDto.from(device)
        }
    }
}
```

### Query Counting

```kotlin
// Count database queries
@Component
class QueryCounter {
    
    private val queryCount = ThreadLocal<Int>()
    
    fun startCounting() {
        queryCount.set(0)
    }
    
    fun incrementCount() {
        queryCount.set((queryCount.get() ?: 0) + 1)
    }
    
    fun getCount(): Int = queryCount.get() ?: 0
    
    fun clear() {
        queryCount.remove()
    }
}

// Intercept repository calls
@Aspect
@Component
class RepositoryQueryCounterAspect(
    private val queryCounter: QueryCounter
) {
    
    @Around("execution(* com.smarthome.*.repository.*.*(..))")
    fun countQuery(joinPoint: ProceedingJoinPoint): Any? {
        queryCounter.incrementCount()
        return joinPoint.proceed()
    }
}

// Usage in tests
@Test
fun `should not have N+1 query problem`() {
    queryCounter.startCounting()
    
    val result = getDeviceListUseCase.execute(premisesId)
    
    val queryCount = queryCounter.getCount()
    assertTrue(queryCount < 5, "Too many queries: $queryCount")
}
```

---

## <a name="optimizing-aggregates"></a>3. Optimizing Aggregates

### Problem: Loading Entire Aggregate

```kotlin
// Before: Load everything ❌
class Device {
    val deviceId: DeviceId
    val name: Name
    val status: DeviceStatus
    val feeds: Map<FeedId, Feed>  // 10 feeds
    val history: List<StatusChange>  // 1000 entries
    val configuration: DeviceConfiguration  // Complex object
    val metadata: Map<String, String>  // 50 entries
    // 50+ fields total
}

// Use case only needs 3 fields
fun getDeviceStatus(deviceId: DeviceId): String {
    val device = deviceRepository.findById(deviceId)
    // Loaded all 50 fields! ❌
    
    return device.getStatus().name  // Only used 1 field!
}
```

### Solution 1: Projections

```kotlin
// Create lightweight projections
data class DeviceStatusProjection(
    val deviceId: String,
    val status: String
)

interface DeviceRepository {
    fun findById(deviceId: DeviceId): Device
    
    // Add projection queries ✅
    fun findStatusById(deviceId: DeviceId): DeviceStatusProjection?
}

// Use projection for read-only queries ✅
@Service
class GetDeviceStatusUseCase(
    private val deviceRepository: DeviceRepository
) {
    fun execute(deviceId: DeviceId): String {
        val projection = deviceRepository.findStatusById(deviceId)
            ?: throw DeviceNotFoundException(deviceId)
        
        return projection.status
    }
}

// MongoDB implementation
@Repository
class MongoDeviceRepository(
    private val mongoTemplate: MongoTemplate
) : DeviceRepository {
    
    override fun findStatusById(deviceId: DeviceId): DeviceStatusProjection? {
        val query = Query.query(Criteria.where("_id").`is`(deviceId.value))
        query.fields()
            .include("_id")
            .include("status")
        
        return mongoTemplate.findOne(
            query,
            DeviceStatusProjection::class.java,
            "devices"
        )
    }
}
```

### Solution 2: Lazy Loading

```kotlin
// Lazy load expensive collections
class Device(
    val deviceId: DeviceId,
    private val name: Name,
    private val status: DeviceStatus,
    
    // Lazy loaded ✅
    private val feedsLoader: () -> Map<FeedId, Feed>,
    private val historyLoader: () -> List<StatusChange>
) {
    private var _feeds: Map<FeedId, Feed>? = null
    private var _history: List<StatusChange>? = null
    
    fun getFeeds(): Map<FeedId, Feed> {
        if (_feeds == null) {
            _feeds = feedsLoader()
        }
        return _feeds!!
    }
    
    fun getHistory(): List<StatusChange> {
        if (_history == null) {
            _history = historyLoader()
        }
        return _history!!
    }
    
    // Immediate fields
    fun getStatus(): DeviceStatus = status
}
```

### Solution 3: Aggregate Splitting

```kotlin
// Before: One big aggregate ❌
class Device {
    val deviceId: DeviceId
    val status: DeviceStatus
    val feeds: Map<FeedId, Feed>
    val history: List<StatusChange>  // Huge!
    val analytics: DeviceAnalytics    // Huge!
}

// After: Split into smaller aggregates ✅

// Core device aggregate
class Device(
    val deviceId: DeviceId,
    private val status: DeviceStatus,
    private val feeds: Map<FeedId, Feed>
    // Only core data
)

// Separate aggregate for history
class DeviceHistory(
    val deviceId: DeviceId,
    private val changes: List<StatusChange>
)

// Separate aggregate for analytics
class DeviceAnalytics(
    val deviceId: DeviceId,
    private val metrics: Map<String, Metric>
)

// Load only what you need ✅
fun getDeviceStatus(deviceId: DeviceId): DeviceStatus {
    val device = deviceRepository.findById(deviceId)
    return device.getStatus()
    // No history or analytics loaded!
}
```

---

## <a name="caching-strategies"></a>4. Caching Strategies

### Strategy 1: In-Memory Caching

```kotlin
// Simple in-memory cache
@Component
class DeviceCache {
    
    private val cache = ConcurrentHashMap<DeviceId, Device>()
    private val ttl = Duration.ofMinutes(5)
    private val timestamps = ConcurrentHashMap<DeviceId, Instant>()
    
    fun get(deviceId: DeviceId): Device? {
        val timestamp = timestamps[deviceId] ?: return null
        
        if (timestamp.plus(ttl).isBefore(Instant.now())) {
            // Expired
            cache.remove(deviceId)
            timestamps.remove(deviceId)
            return null
        }
        
        return cache[deviceId]
    }
    
    fun put(device: Device) {
        cache[device.deviceId] = device
        timestamps[device.deviceId] = Instant.now()
    }
    
    fun invalidate(deviceId: DeviceId) {
        cache.remove(deviceId)
        timestamps.remove(deviceId)
    }
}

// Use cache in repository
@Repository
class CachedDeviceRepository(
    private val mongoRepository: MongoDeviceRepository,
    private val cache: DeviceCache
) : DeviceRepository {
    
    override fun findById(deviceId: DeviceId): Device? {
        // Try cache first ✅
        cache.get(deviceId)?.let { return it }
        
        // Load from database
        val device = mongoRepository.findById(deviceId)
        
        // Cache it
        device?.let { cache.put(it) }
        
        return device
    }
    
    override fun save(device: Device): Device {
        val saved = mongoRepository.save(device)
        
        // Invalidate cache ✅
        cache.invalidate(device.deviceId)
        
        return saved
    }
}
```

### Strategy 2: Spring Cache

```kotlin
// Use Spring Cache annotations
@Configuration
@EnableCaching
class CacheConfig {
    
    @Bean
    fun cacheManager(): CacheManager {
        return ConcurrentMapCacheManager(
            "devices",
            "premises",
            "zones"
        )
    }
}

@Repository
class CachedDeviceRepository(
    private val mongoRepository: MongoDeviceRepository
) : DeviceRepository {
    
    @Cacheable(value = ["devices"], key = "#deviceId.value")
    override fun findById(deviceId: DeviceId): Device? {
        return mongoRepository.findById(deviceId)
    }
    
    @CachePut(value = ["devices"], key = "#device.deviceId.value")
    override fun save(device: Device): Device {
        return mongoRepository.save(device)
    }
    
    @CacheEvict(value = ["devices"], key = "#deviceId.value")
    override fun delete(deviceId: DeviceId) {
        mongoRepository.delete(deviceId)
    }
}
```

### Strategy 3: Read Models (CQRS)

```kotlin
// Denormalized read model ✅
@Document("device_list_read_model")
data class DeviceListReadModel(
    @Id val deviceId: String,
    val premisesId: String,
    val name: String,
    val status: String,
    val zoneName: String?,  // Denormalized!
    val feedCount: Int,
    val lastSeenAt: Long?
)

// Update on events
@Component
class DeviceListReadModelUpdater(
    private val readModelRepository: DeviceListReadModelRepository
) {
    
    @EventListener
    fun handle(event: DeviceRegistered) {
        val readModel = DeviceListReadModel(
            deviceId = event.deviceId.value,
            premisesId = event.premisesId.value,
            name = event.name.value,
            status = "PENDING",
            zoneName = null,
            feedCount = 0,
            lastSeenAt = null
        )
        
        readModelRepository.save(readModel)
    }
    
    @EventListener
    fun handle(event: DeviceMovedToZone) {
        val readModel = readModelRepository.findById(event.deviceId.value)
            ?: return
        
        readModelRepository.save(
            readModel.copy(zoneName = event.zoneName)
        )
    }
}

// Query read model (fast!) ✅
@Service
class GetDeviceListUseCase(
    private val readModelRepository: DeviceListReadModelRepository
) {
    fun execute(premisesId: PremisesId): List<DeviceListDto> {
        return readModelRepository.findByPremisesId(premisesId.value)
            .map { DeviceListDto.from(it) }
        
        // Single query, no joins! ✅
    }
}
```

---

## <a name="query-optimization"></a>5. Query Optimization

### Problem: N+1 Queries

```kotlin
// Before: N+1 queries ❌
@Service
class GetDeviceListUseCase(
    private val deviceRepository: DeviceRepository,
    private val zoneRepository: ZoneRepository
) {
    fun execute(premisesId: PremisesId): List<DeviceListDto> {
        val devices = deviceRepository.findByPremisesId(premisesId)
        
        return devices.map { device ->
            val zone = zoneRepository.findById(device.zoneId)  // N+1! ❌
            
            DeviceListDto(
                id = device.deviceId.value,
                name = device.getName().value,
                zoneName = zone?.name
            )
        }
    }
}
```

### Solution 1: Eager Loading

```kotlin
// Load everything at once ✅
@Repository
class MongoDeviceRepository(
    private val mongoTemplate: MongoTemplate
) {
    fun findByPremisesIdWithZones(premisesId: PremisesId): List<DeviceWithZone> {
        // Use aggregation to join
        val aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("premisesId").`is`(premisesId.value)),
            Aggregation.lookup("zones", "zoneId", "_id", "zone")
        )
        
        return mongoTemplate.aggregate(
            aggregation,
            "devices",
            DeviceWithZone::class.java
        ).mappedResults
        
        // One query! ✅
    }
}
```

### Solution 2: Batch Loading

```kotlin
// Load zones in batch ✅
@Service
class GetDeviceListUseCase(
    private val deviceRepository: DeviceRepository,
    private val zoneRepository: ZoneRepository
) {
    fun execute(premisesId: PremisesId): List<DeviceListDto> {
        val devices = deviceRepository.findByPremisesId(premisesId)
        
        // Get all zone IDs
        val zoneIds = devices.mapNotNull { it.zoneId }.distinct()
        
        // Load all zones at once ✅
        val zones = zoneRepository.findByIds(zoneIds)
            .associateBy { it.zoneId }
        
        // Map with zones
        return devices.map { device ->
            DeviceListDto(
                id = device.deviceId.value,
                name = device.getName().value,
                zoneName = device.zoneId?.let { zones[it]?.name }
            )
        }
        
        // Only 2 queries! ✅
    }
}
```

### Solution 3: Database Indexes

```kotlin
// Add indexes for common queries
@Document("devices")
@CompoundIndexes(
    CompoundIndex(
        name = "premises_status_idx",
        def = "{'premisesId': 1, 'status': 1}"
    ),
    CompoundIndex(
        name = "premises_zone_idx",
        def = "{'premisesId': 1, 'zoneId': 1}"
    )
)
data class DeviceDocument(
    @Id val id: String,
    @Indexed val premisesId: String,  // Single field index
    val status: String,
    val zoneId: String?
)

// Query uses index ✅
fun findActiveDevicesInPremises(premisesId: PremisesId): List<Device> {
    // Uses premises_status_idx
    return deviceRepository.findByPremisesIdAndStatus(
        premisesId,
        DeviceStatus.ACTIVE
    )
}
```

---

## <a name="event-sourcing-performance"></a>6. Event Sourcing Performance

### Problem: Replaying Many Events

```kotlin
// Before: Replay 10,000 events ❌
@Service
class LoadDeviceUseCase(
    private val eventStore: EventStore
) {
    fun execute(deviceId: DeviceId): Device {
        val events = eventStore.getEvents(deviceId.value)
        // 10,000 events loaded! ❌
        
        var device: Device? = null
        events.forEach { event ->
            device = applyEvent(device, event)
        }
        // Takes 5 seconds! ❌
        
        return device!!
    }
}
```

### Solution: Snapshots

```kotlin
// Store snapshots periodically
@Document("device_snapshots")
data class DeviceSnapshot(
    @Id val id: String,
    val deviceId: String,
    val aggregateData: String,  // Serialized device
    val version: Long,
    val timestamp: Long
)

@Component
class SnapshotStore(
    private val mongoTemplate: MongoTemplate,
    private val objectMapper: ObjectMapper
) {
    
    fun saveSnapshot(device: Device, version: Long) {
        val snapshot = DeviceSnapshot(
            id = UUID.randomUUID().toString(),
            deviceId = device.deviceId.value,
            aggregateData = objectMapper.writeValueAsString(device),
            version = version,
            timestamp = Instant.now().toEpochMilli()
        )
        
        mongoTemplate.save(snapshot, "device_snapshots")
    }
    
    fun getLatestSnapshot(deviceId: DeviceId): DeviceSnapshot? {
        val query = Query.query(Criteria.where("deviceId").`is`(deviceId.value))
            .with(Sort.by(Sort.Direction.DESC, "version"))
            .limit(1)
        
        return mongoTemplate.findOne(query, DeviceSnapshot::class.java)
    }
}

// Load from snapshot ✅
@Service
class LoadDeviceUseCase(
    private val eventStore: EventStore,
    private val snapshotStore: SnapshotStore,
    private val objectMapper: ObjectMapper
) {
    fun execute(deviceId: DeviceId): Device {
        // Try snapshot first ✅
        val snapshot = snapshotStore.getLatestSnapshot(deviceId)
        
        val device = if (snapshot != null) {
            // Load from snapshot
            val device = objectMapper.readValue(
                snapshot.aggregateData,
                Device::class.java
            )
            
            // Load only events after snapshot
            val eventsAfterSnapshot = eventStore.getEventsAfterVersion(
                deviceId.value,
                snapshot.version
            )
            
            // Replay only new events (50 instead of 10,000!) ✅
            eventsAfterSnapshot.fold(device) { acc, event ->
                applyEvent(acc, event)
            }
        } else {
            // No snapshot, load all events
            val events = eventStore.getEvents(deviceId.value)
            events.fold(null as Device?) { acc, event ->
                applyEvent(acc, event)
            }!!
        }
        
        return device
    }
}

// Create snapshot every 100 events
@Service
class DeviceEventHandler(
    private val snapshotStore: SnapshotStore
) {
    fun handleEvents(device: Device, version: Long) {
        if (version % 100 == 0L) {
            snapshotStore.saveSnapshot(device, version)
        }
    }
}
```

---

## <a name="database-optimization"></a>7. Database Optimization

### Connection Pooling

```kotlin
// Configure connection pool
@Configuration
class DatabaseConfig {
    
    @Bean
    fun mongoClient(): MongoClient {
        val settings = MongoClientSettings.builder()
            .applyConnectionString(
                ConnectionString("mongodb://localhost:27017")
            )
            .applyToConnectionPoolSettings { builder ->
                builder
                    .maxSize(100)  // Max connections
                    .minSize(10)   // Min connections
                    .maxWaitTime(2000, TimeUnit.MILLISECONDS)
                    .build()
            }
            .build()
        
        return MongoClients.create(settings)
    }
}
```

### Batch Operations

```kotlin
// Before: Save one by one ❌
fun saveDevices(devices: List<Device>) {
    devices.forEach { device ->
        deviceRepository.save(device)  // N queries! ❌
    }
}

// After: Batch save ✅
fun saveDevices(devices: List<Device>) {
    deviceRepository.saveAll(devices)  // 1 query! ✅
}

// MongoDB bulk operations
@Repository
class MongoDeviceRepository(
    private val mongoTemplate: MongoTemplate
) {
    
    fun saveAll(devices: List<Device>): List<Device> {
        val bulkOps = mongoTemplate.bulkOps(
            BulkOperations.BulkMode.UNORDERED,
            Device::class.java
        )
        
        devices.forEach { device ->
            bulkOps.upsert(
                Query.query(Criteria.where("_id").`is`(device.deviceId.value)),
                Update().set("status", device.getStatus().name)
            )
        }
        
        bulkOps.execute()
        
        return devices
    }
}
```

---

## <a name="measuring"></a>8. Measuring and Monitoring

### Performance Metrics

```kotlin
// Add metrics collection
@Component
class PerformanceMetrics {
    
    private val registry = SimpleMeterRegistry()
    
    fun recordDuration(operation: String, duration: Long) {
        Timer.builder("operation.duration")
            .tag("operation", operation)
            .register(registry)
            .record(duration, TimeUnit.MILLISECONDS)
    }
    
    fun incrementCounter(metric: String) {
        Counter.builder(metric)
            .register(registry)
            .increment()
    }
    
    fun getMetrics(): Map<String, Double> {
        return registry.meters.associate { meter ->
            meter.id.name to meter.measure().first().value
        }
    }
}

// Use in repository
@Repository
class MonitoredDeviceRepository(
    private val delegate: DeviceRepository,
    private val metrics: PerformanceMetrics
) : DeviceRepository by delegate {
    
    override fun findById(deviceId: DeviceId): Device? {
        val start = System.currentTimeMillis()
        
        try {
            return delegate.findById(deviceId)
        } finally {
            val duration = System.currentTimeMillis() - start
            metrics.recordDuration("findById", duration)
            
            if (duration > 100) {
                metrics.incrementCounter("slow.queries")
            }
        }
    }
}
```

### Load Testing

```kotlin
// Simple load test
@Test
fun `should handle 100 concurrent device queries`() {
    val devices = (1..100).map { index ->
        createTestDevice(deviceId = DeviceId("device-$index"))
    }
    
    devices.forEach { deviceRepository.save(it) }
    
    val executor = Executors.newFixedThreadPool(10)
    val startTime = System.currentTimeMillis()
    
    val futures = devices.map { device ->
        executor.submit {
            deviceRepository.findById(device.deviceId)
        }
    }
    
    futures.forEach { it.get() }
    
    val duration = System.currentTimeMillis() - startTime
    
    assertTrue(duration < 5000, "Too slow: ${duration}ms")
    
    executor.shutdown()
}
```

---

## 9. Chapter Summary

In this chapter, we've explored performance optimization in DDD applications. While DDD provides excellent structure, improper implementation can cause performance issues. By measuring first, optimizing strategically, and using proven patterns, you can achieve both clean domain design and excellent performance.

### What We Covered

**The Performance Problem:**
- Dashboard loading in 15 seconds
- Database at 90% CPU
- Loading entire aggregates unnecessarily
- N+1 query problems
- Missing caching
- No database indexes

**The Performance Solution:**
- Measure before optimizing
- Use projections for reads
- Strategic caching
- Query optimization
- Event sourcing snapshots
- Database indexing
- Continuous monitoring

### Key Insights

1. **Measure first, optimize later** - Profile to find real bottlenecks, not assumed ones.

2. **Load only what you need** - Use projections instead of full aggregates for queries.

3. **Cache strategically** - Read models, frequently accessed data, computation results.

4. **Avoid N+1 queries** - Batch load, eager load related entities.

5. **Use database indexes** - Critical for query performance on large datasets.

6. **Snapshot event-sourced aggregates** - Don't replay thousands of events every time.

7. **Connection pooling** - Reuse expensive database connections.

8. **Batch operations** - Save multiple entities in one transaction.

9. **Monitor continuously** - Track metrics in production, not just development.

10. **Test under load** - Simulate production traffic to find breaking points.

### Performance Bottlenecks and Solutions

**Problem 1: Loading Entire Aggregate**
```kotlin
// Before: Load everything ❌
val device = deviceRepository.findById(deviceId)
return DeviceDto(
    id = device.deviceId.value,
    name = device.getName().value
)
// Loaded 50 fields, used 2! 💥

// After: Use projection ✅
val deviceSummary = deviceRepository.findSummaryById(deviceId)
return DeviceDto(
    id = deviceSummary.deviceId,
    name = deviceSummary.name
)
// Loaded 2 fields, used 2! ✅
```

**Problem 2: N+1 Queries**
```kotlin
// Before: N+1 queries ❌
val devices = deviceRepository.findAll()
devices.forEach { device ->
    val premises = premisesRepository.findById(device.premisesId)  // N queries!
    println("${device.name} at ${premises.name}")
}

// After: Batch load ✅
val devices = deviceRepository.findAll()
val premisesIds = devices.map { it.premisesId }.toSet()
val premisesMap = premisesRepository.findByIds(premisesIds)
    .associateBy { it.premisesId }
devices.forEach { device ->
    val premises = premisesMap[device.premisesId]
    println("${device.name} at ${premises?.name}")
}
```

**Problem 3: No Caching**
```kotlin
// Before: Always load from DB ❌
fun getDevice(deviceId: DeviceId): Device {
    return deviceRepository.findById(deviceId)
}

// After: Cache frequently accessed ✅
@Cacheable("devices")
fun getDevice(deviceId: DeviceId): Device {
    return deviceRepository.findById(deviceId)
}

@CacheEvict("devices", key = "#device.deviceId")
fun updateDevice(device: Device): Device {
    return deviceRepository.save(device)
}
```

### Optimization Strategies

**1. Aggregate Projections:**
```kotlin
// Full aggregate (heavy)
class Device {
    val feeds: Map<FeedId, Feed>
    val configuration: DeviceConfiguration
    val metadata: Map<String, String>
    // ... 30 more fields
}

// Projection (light)
data class DeviceSummary(
    val deviceId: DeviceId,
    val name: String,
    val status: DeviceStatus
)

interface DeviceRepository {
    fun findById(deviceId: DeviceId): Device
    fun findSummaryById(deviceId: DeviceId): DeviceSummary  // Fast!
}
```

**2. Caching Strategies:**

**In-Memory Cache:**
```kotlin
class CachedDeviceRepository(
    private val repository: DeviceRepository,
    private val cache: Cache<DeviceId, Device>
) : DeviceRepository {
    override fun findById(deviceId: DeviceId): Device? {
        return cache.get(deviceId) {
            repository.findById(deviceId)
        }
    }
}
```

**Spring Cache:**
```kotlin
@Service
class DeviceService(private val repository: DeviceRepository) {
    
    @Cacheable("devices", key = "#deviceId")
    fun getDevice(deviceId: DeviceId): Device? {
        return repository.findById(deviceId)
    }
    
    @CachePut("devices", key = "#device.deviceId")
    fun updateDevice(device: Device): Device {
        return repository.save(device)
    }
    
    @CacheEvict("devices", key = "#deviceId")
    fun deleteDevice(deviceId: DeviceId) {
        repository.delete(deviceId)
    }
}
```

**CQRS with Read Models:**
```kotlin
// Write model (normalized)
class Device { }

// Read model (denormalized, cached)
@Document("device_read_model")
data class DeviceReadModel(
    val deviceId: String,
    val name: String,
    val status: String,
    val premisesName: String,  // Denormalized
    val zoneName: String       // Denormalized
)

// Query service with cache
@Service
class DeviceQueryService(
    private val readRepository: DeviceReadModelRepository
) {
    @Cacheable("device-read-models")
    fun getDeviceReadModel(deviceId: String): DeviceReadModel? {
        return readRepository.findById(deviceId)
    }
}
```

**3. Query Optimization:**

**Add Indexes:**
```kotlin
@Document("devices")
@CompoundIndexes(
    CompoundIndex(name = "premises_status_idx", 
                  def = "{'premisesId': 1, 'status': 1}"),
    CompoundIndex(name = "serial_idx", 
                  def = "{'serialNumber': 1}", unique = true)
)
data class DeviceDocument(
    @Id val id: String,
    @Indexed val premisesId: String,
    val serialNumber: String,
    val status: String
)
```

**Batch Loading:**
```kotlin
// Find multiple by IDs in one query
interface DeviceRepository {
    fun findByIds(deviceIds: Set<DeviceId>): List<Device>
}

// Implementation
class MongoDeviceRepository : DeviceRepository {
    override fun findByIds(deviceIds: Set<DeviceId>): List<Device> {
        val query = Query.query(
            Criteria.where("_id").`in`(deviceIds.map { it.value })
        )
        return mongoTemplate.find(query, DeviceDocument::class.java)
            .map { it.toDomain() }
    }
}
```

**4. Event Sourcing Performance:**

**Without Snapshots (Slow):**
```kotlin
// Replay 10,000 events every time ❌
fun loadDevice(deviceId: DeviceId): Device {
    val events = eventStore.loadEvents(deviceId)  // 10,000 events!
    return Device.fromEvents(events)
}
```

**With Snapshots (Fast):**
```kotlin
// Load snapshot + recent events ✅
fun loadDevice(deviceId: DeviceId): Device {
    val snapshot = snapshotStore.loadLatest(deviceId)
    
    if (snapshot != null) {
        val recentEvents = eventStore.loadEventsAfter(deviceId, snapshot.version)
        return Device.fromSnapshot(snapshot).applyEvents(recentEvents)
    }
    
    val allEvents = eventStore.loadEvents(deviceId)
    return Device.fromEvents(allEvents)
}

// Save snapshot every 100 events
fun saveDevice(device: Device) {
    if (device.version % 100 == 0L) {
        snapshotStore.save(device.toSnapshot())
    }
}
```

**5. Database Optimization:**

**Connection Pooling:**
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/nexora
      # Connection pool settings
      max-connections: 100
      min-connections: 10
      connection-timeout: 5000
```

**Batch Operations:**
```kotlin
// Before: Individual saves ❌
devices.forEach { device ->
    repository.save(device)  // N DB calls
}

// After: Batch save ✅
repository.saveAll(devices)  // 1 DB call
```

### Performance Metrics

**SmartHome Hub Performance Improvements:**

**Before Optimization:**
- Dashboard load: 15 seconds
- API response: 3-5 seconds
- Database CPU: 90%
- Query count: 1000+ per page
- Cache hit rate: 0%

**After Optimization:**
- Dashboard load: 200ms (75x faster!)
- API response: 50-200ms (20x faster!)
- Database CPU: 15%
- Query count: 5-10 per page (99% reduction!)
- Cache hit rate: 95%

**Optimization Breakdown:**
1. Projections: 60% improvement
2. Caching: 90% improvement (for cached data)
3. Indexes: 10x faster queries
4. Batch loading: 95% fewer queries
5. Snapshots: 50x faster aggregate loading

### Monitoring and Metrics

**Key Metrics to Track:**
```kotlin
@Component
class PerformanceMetrics(
    private val meterRegistry: MeterRegistry
) {
    fun recordQueryTime(operation: String, durationMs: Long) {
        Timer.builder("query.duration")
            .tag("operation", operation)
            .register(meterRegistry)
            .record(durationMs, TimeUnit.MILLISECONDS)
    }
    
    fun recordCacheHit(cacheName: String) {
        Counter.builder("cache.hits")
            .tag("cache", cacheName)
            .register(meterRegistry)
            .increment()
    }
    
    fun recordCacheMiss(cacheName: String) {
        Counter.builder("cache.misses")
            .tag("cache", cacheName)
            .register(meterRegistry)
            .increment()
    }
}
```

**APM Integration:**
```kotlin
@Service
class DeviceService(
    private val repository: DeviceRepository
) {
    @Traced(operationName = "getDevice")
    fun getDevice(deviceId: DeviceId): Device? {
        val span = GlobalTracer.get().activeSpan()
        span?.setTag("device.id", deviceId.value)
        
        return repository.findById(deviceId)
    }
}
```

### Load Testing

**Gatling Load Test:**
```kotlin
class DeviceLoadTest : Simulation() {
    val httpProtocol = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/json")
    
    val scn = scenario("Device Load Test")
        .exec(
            http("Get Device")
                .get("/api/devices/${UUID.randomUUID()}")
                .check(status.is(200))
        )
        .pause(1)
    
    setUp(
        scn.inject(
            rampUsers(100).during(60.seconds),  // Ramp to 100 users
            constantUsersPerSec(50).during(300.seconds)  // Sustain 50 req/s
        )
    ).protocols(httpProtocol)
}
```

### Measured Benefits

Teams optimizing DDD performance see:
- **75x faster** dashboard loading (15s → 200ms)
- **20x faster** API responses
- **99% reduction** in query count
- **95% cache hit rate**
- **85% reduction** in database CPU
- **50x faster** aggregate loading (with snapshots)

### Practice Exercise

Optimize your application:

1. **Profile current performance** - Use APM, identify bottlenecks
2. **Count queries** - Find N+1 problems, excessive loading
3. **Create projections** - For read-heavy operations
4. **Add caching** - In-memory or Spring Cache
5. **Add indexes** - For common query patterns
6. **Implement snapshots** - If using event sourcing
7. **Load test** - Simulate production traffic
8. **Monitor production** - Track metrics continuously
9. **Measure improvement** - Compare before/after metrics

### Design Checklist

When optimizing DDD performance:
- ✅ Profiled to find real bottlenecks
- ✅ Projections for read queries
- ✅ Caching strategy implemented
- ✅ Database indexes added
- ✅ N+1 queries eliminated
- ✅ Batch operations where possible
- ✅ Snapshots for event sourcing
- ✅ Connection pooling configured
- ✅ Performance metrics tracked
- ✅ Load testing performed

---

### Additional Reading

For deeper understanding of performance:
- **"Release It!"** by Michael T. Nygard (2018) - Production readiness
- **"Database Performance Tuning"** - MongoDB, PostgreSQL optimization guides
- **"The Art of Scalability"** by Martin L. Abbott (2015) - Scaling strategies

---

## What's Next

In **Chapter 20**, the final chapter, we'll explore DDD Best Practices and lessons learned. You'll learn:
- Common DDD mistakes and how to avoid them
- When to use DDD (and when not to)
- Team collaboration strategies
- Migration strategies for existing teams
- Real-world lessons from SmartHome Hub
- The complete DDD journey

This concluding chapter synthesizes everything you've learned into practical wisdom for successfully adopting DDD in your organization.

Turn the page for the final chapter...

