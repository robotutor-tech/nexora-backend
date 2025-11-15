# Chapter 14: Event Sourcing - Audit Trail and Time Travel

**Series:** Building an Enterprise IoT Platform with DDD  
**Part:** 4 of 20 - Advanced Topics  
**Reading Time:** 28 minutes  
**Level:** Advanced  

---

## 📋 Table of Contents

1. [The Problem: Lost History and No Audit Trail](#the-problem)
2. [What is Event Sourcing?](#what-is-event-sourcing)
3. [Event Store Implementation](#event-store)
4. [Rebuilding State from Events](#rebuilding-state)
5. [Real-World Examples from SmartHome Hub](#real-examples)
6. [Snapshots for Performance](#snapshots)
7. [Time Travel Queries](#time-travel)
8. [Event Versioning and Upcasting](#event-versioning)
9. [Testing Event-Sourced Systems](#testing)

---

## <a name="the-problem"></a>1. The Problem: Lost History and No Audit Trail

### The Scenario: The Compliance Audit Disaster

You're running SmartHome Hub when legal team arrives with devastating news:

> **Legal Alert:** "Regulatory audit requires complete history of all device status changes for the past year. Customer claims their device was offline on specific dates. We have no historical data. Potential $500,000 fine!"

You investigate and find a nightmare:

```kotlin
// Traditional state-based storage ❌
@Document("devices")
data class Device(
    @Id val id: String,
    val premisesId: String,
    val name: String,
    val status: String,  // Current status only!
    val health: String,  // Current health only!
    val lastSeenAt: Long?,  // Only last timestamp!
    val updatedAt: Long
    // No history! Lost forever! 💥
)

// When device status changes, old state is overwritten
@Service
class DeviceService(
    private val deviceRepository: DeviceRepository
) {
    fun updateDeviceStatus(deviceId: String, newStatus: String) {
        val device = deviceRepository.findById(deviceId)
        
        // Old status lost forever! ❌
        device.status = newStatus
        device.updatedAt = System.currentTimeMillis()
        
        deviceRepository.save(device)
        // History gone! Can't prove what happened! 💥
    }
}
```

### The Questions You Can't Answer

**Customer Support Questions:**

```kotlin
// Question 1: "When did this device go offline?"
// Answer: We don't know. We only have current status.

// Question 2: "Was device active between Jan 1-15?"
// Answer: No idea. History not stored.

// Question 3: "Who changed the device configuration on Feb 5?"
// Answer: Can't tell. No audit trail.

// Question 4: "What was the device status at 3pm yesterday?"
// Answer: Unknown. Only have current status.

// Question 5: "Show me all status changes last month"
// Answer: Impossible. Data overwritten.
```

**Attempted Solution:**

```kotlin
// Try to add history table ❌
@Document("device_status_history")
data class DeviceStatusHistory(
    @Id val id: String,
    val deviceId: String,
    val oldStatus: String,
    val newStatus: String,
    val changedAt: Long,
    val changedBy: String
)

@Service
class DeviceService {
    fun updateDeviceStatus(deviceId: String, newStatus: String, actorId: String) {
        val device = deviceRepository.findById(deviceId)
        
        // Save history
        val history = DeviceStatusHistory(
            id = UUID.randomUUID().toString(),
            deviceId = deviceId,
            oldStatus = device.status,  // What if device was null?
            newStatus = newStatus,
            changedAt = System.currentTimeMillis(),
            changedBy = actorId
        )
        historyRepository.save(history)
        
        // Update current state
        device.status = newStatus
        deviceRepository.save(device)
        
        // Problems:
        // 1. History can get out of sync with current state
        // 2. What about other fields? (health, name, config)
        // 3. Need separate history table for each field
        // 4. History might not be saved if update fails
        // 5. Can't rebuild current state from history
    }
}
```

### The Real Cost

**Measured impact:**
- 🔴 **$500,000 potential fine** - Can't prove compliance
- 🔴 **Customer disputes** - Can't verify claims
- 🔴 **No debugging** - Can't trace what happened
- 🔴 **No audit trail** - Failed security audit
- 🔴 **Lost analytics** - Can't analyze historical patterns
- 🔴 **Legal liability** - No evidence of device behavior

**Additional problems:**

```kotlin
// Problem 1: Concurrent updates lose data
// User A reads device (status = "ACTIVE")
// User B reads device (status = "ACTIVE")
// User A updates (status = "INACTIVE")
// User B updates (status = "ERROR")
// Result: User A's change lost! No record it ever happened!

// Problem 2: Soft deletes don't help
@Document("devices")
data class Device(
    val id: String,
    val status: String,
    val deleted: Boolean = false  // Soft delete
)
// Device marked deleted, but all history still lost

// Problem 3: Backup/restore loses changes
// Restore from yesterday's backup
// All changes made today = gone forever!
```

**Root Cause:** Storing only current state instead of the sequence of events that led to that state.

---

## <a name="what-is-event-sourcing"></a>2. What is Event Sourcing?

### Definition

> **Event Sourcing:** Store the complete history of changes to application state as a sequence of events. Current state is derived by replaying all events.
> 
> — Martin Fowler, Event Sourcing

### Core Concept

```
Traditional Approach (State-Based):
┌─────────────────────────────────────────────────────┐
│                 Current State                       │
│                                                     │
│  Device {                                           │
│    id: "device-123"                                 │
│    status: "ACTIVE"  ← Only current value          │
│    health: "ONLINE"                                 │
│  }                                                  │
│                                                     │
│  History: LOST! 💥                                 │
└─────────────────────────────────────────────────────┘

Event Sourcing Approach (Event-Based):
┌─────────────────────────────────────────────────────┐
│                 Event Store                         │
│                                                     │
│  Event 1: DeviceRegistered(id, name, ...)          │
│  Event 2: DeviceActivated(id, timestamp)           │
│  Event 3: FeedAdded(id, feedId, ...)               │
│  Event 4: DeviceWentOffline(id, timestamp)         │
│  Event 5: DeviceWentOnline(id, timestamp)          │
│  Event 6: DeviceActivated(id, timestamp)           │
│                                                     │
│  Complete History: PRESERVED! ✅                   │
└──────────────────┬──────────────────────────────────┘
                   │
                   │ Replay Events
                   ▼
┌─────────────────────────────────────────────────────┐
│             Current State (Derived)                 │
│                                                     │
│  Device {                                           │
│    id: "device-123"                                 │
│    status: "ACTIVE"  ← Calculated from events      │
│    health: "ONLINE"                                 │
│  }                                                  │
│                                                     │
│  Can rebuild anytime! ✅                           │
└─────────────────────────────────────────────────────┘
```

### Key Principles

#### 1. Events are Immutable

```kotlin
// Events never change once stored
data class DeviceActivated(
    val eventId: String,
    val deviceId: DeviceId,
    val activatedBy: ActorId,
    val timestamp: Instant
) : DeviceEvent()

// NEVER do this:
// event.timestamp = newTimestamp  ❌
// Events represent facts that happened - can't change history!
```

#### 2. Events are Append-Only

```kotlin
// Can only add new events, never modify or delete existing ones
eventStore.append("device-123", DeviceActivated(...))  ✅
eventStore.append("device-123", DeviceDeactivated(...))  ✅

// NEVER do this:
eventStore.update(eventId, newData)  ❌
eventStore.delete(eventId)  ❌
```

#### 3. Current State is Derived

```kotlin
// Current state = Initial state + all events
fun getCurrentState(deviceId: String): Device {
    val events = eventStore.getEvents(deviceId)
    
    var state: Device? = null
    events.forEach { event ->
        state = when (event) {
            is DeviceRegistered -> Device.from(event)
            is DeviceActivated -> state?.activate()
            is DeviceDeactivated -> state?.deactivate()
            is FeedAdded -> state?.addFeed(event.feed)
            // ... more events
            else -> state
        }
    }
    
    return state ?: throw DeviceNotFoundException(deviceId)
}
```

#### 4. Complete Audit Trail

```kotlin
// Every change is recorded
val history = eventStore.getEvents("device-123")

history.forEach { event ->
    println("${event.timestamp}: ${event.javaClass.simpleName}")
}

// Output:
// 2025-01-01T10:00:00Z: DeviceRegistered
// 2025-01-01T10:05:00Z: FeedAdded
// 2025-01-01T10:10:00Z: DeviceActivated
// 2025-01-02T14:30:00Z: DeviceWentOffline
// 2025-01-02T14:35:00Z: DeviceWentOnline
// Complete history! ✅
```

---

## <a name="event-store"></a>3. Event Store Implementation

### Event Store Interface

```kotlin
// Core event store operations
interface EventStore {
    /**
     * Append events to a stream
     */
    fun append(
        streamId: String,
        events: List<DomainEvent>,
        expectedVersion: Long? = null
    ): Long
    
    /**
     * Get all events for a stream
     */
    fun getEvents(streamId: String): List<StoredEvent>
    
    /**
     * Get events after a specific version
     */
    fun getEventsAfterVersion(
        streamId: String,
        afterVersion: Long
    ): List<StoredEvent>
    
    /**
     * Get stream version
     */
    fun getStreamVersion(streamId: String): Long
    
    /**
     * Get all stream IDs
     */
    fun getAllStreamIds(): List<String>
}

// Stored event wrapper
data class StoredEvent(
    val eventId: String,
    val streamId: String,
    val streamType: String,
    val eventType: String,
    val eventData: String,  // JSON
    val eventMetadata: Map<String, String>,
    val version: Long,
    val timestamp: Instant
)
```

### MongoDB Event Store Implementation

```kotlin
// Event store document
@Document("event_store")
data class EventDocument(
    @Id val id: String,
    @Indexed val streamId: String,
    val streamType: String,
    val eventType: String,
    val eventData: String,
    val eventMetadata: Map<String, String>,
    @Indexed val version: Long,
    val timestamp: Long,
    
    @CompoundIndex(def = "{'streamId': 1, 'version': 1}", unique = true)
    val uniqueIndex: String? = null
)

@Component
class MongoEventStore(
    private val mongoTemplate: MongoTemplate,
    private val objectMapper: ObjectMapper
) : EventStore {
    
    override fun append(
        streamId: String,
        events: List<DomainEvent>,
        expectedVersion: Long?
    ): Long {
        if (events.isEmpty()) return getStreamVersion(streamId)
        
        // Get current version
        val currentVersion = getStreamVersion(streamId)
        
        // Optimistic concurrency check
        if (expectedVersion != null && currentVersion != expectedVersion) {
            throw OptimisticLockingException(
                "Expected version $expectedVersion but found $currentVersion"
            )
        }
        
        // Create event documents
        val eventDocuments = events.mapIndexed { index, event ->
            EventDocument(
                id = UUID.randomUUID().toString(),
                streamId = streamId,
                streamType = extractStreamType(streamId),
                eventType = event.javaClass.simpleName,
                eventData = objectMapper.writeValueAsString(event),
                eventMetadata = mapOf(
                    "eventId" to (event as? DeviceEvent)?.eventId ?: UUID.randomUUID().toString(),
                    "occurredAt" to (event as? DeviceEvent)?.occurredAt?.toString() ?: Instant.now().toString()
                ),
                version = currentVersion + index + 1,
                timestamp = Instant.now().toEpochMilli()
            )
        }
        
        // Save all events (atomic)
        try {
            eventDocuments.forEach { doc ->
                mongoTemplate.save(doc, "event_store")
            }
        } catch (e: DuplicateKeyException) {
            throw ConcurrencyException("Concurrent modification detected", e)
        }
        
        return currentVersion + events.size
    }
    
    override fun getEvents(streamId: String): List<StoredEvent> {
        val query = Query.query(Criteria.where("streamId").`is`(streamId))
            .with(Sort.by(Sort.Direction.ASC, "version"))
        
        val documents = mongoTemplate.find(query, EventDocument::class.java, "event_store")
        
        return documents.map { doc ->
            StoredEvent(
                eventId = doc.id,
                streamId = doc.streamId,
                streamType = doc.streamType,
                eventType = doc.eventType,
                eventData = doc.eventData,
                eventMetadata = doc.eventMetadata,
                version = doc.version,
                timestamp = Instant.ofEpochMilli(doc.timestamp)
            )
        }
    }
    
    override fun getEventsAfterVersion(
        streamId: String,
        afterVersion: Long
    ): List<StoredEvent> {
        val query = Query.query(
            Criteria.where("streamId").`is`(streamId)
                .and("version").gt(afterVersion)
        ).with(Sort.by(Sort.Direction.ASC, "version"))
        
        val documents = mongoTemplate.find(query, EventDocument::class.java, "event_store")
        
        return documents.map { it.toStoredEvent() }
    }
    
    override fun getStreamVersion(streamId: String): Long {
        val query = Query.query(Criteria.where("streamId").`is`(streamId))
            .with(Sort.by(Sort.Direction.DESC, "version"))
            .limit(1)
        
        val lastEvent = mongoTemplate.findOne(query, EventDocument::class.java, "event_store")
        
        return lastEvent?.version ?: 0L
    }
    
    override fun getAllStreamIds(): List<String> {
        val distinctStreamIds = mongoTemplate.findDistinct(
            Query(),
            "streamId",
            "event_store",
            String::class.java
        )
        
        return distinctStreamIds.toList()
    }
    
    private fun extractStreamType(streamId: String): String {
        // Extract type from stream ID (e.g., "device-123" -> "device")
        return streamId.substringBefore("-")
    }
}
```

### Event-Sourced Aggregate

```kotlin
// Base class for event-sourced aggregates
abstract class EventSourcedAggregate {
    
    private val _uncommittedEvents = mutableListOf<DomainEvent>()
    val uncommittedEvents: List<DomainEvent> get() = _uncommittedEvents.toList()
    
    var version: Long = 0
        protected set
    
    /**
     * Apply event and add to uncommitted events
     */
    protected fun applyNewEvent(event: DomainEvent) {
        applyEvent(event)
        _uncommittedEvents.add(event)
    }
    
    /**
     * Apply event (update state)
     */
    protected abstract fun applyEvent(event: DomainEvent)
    
    /**
     * Load from history
     */
    fun loadFromHistory(events: List<DomainEvent>) {
        events.forEach { event ->
            applyEvent(event)
            version++
        }
    }
    
    /**
     * Clear uncommitted events
     */
    fun markEventsAsCommitted() {
        _uncommittedEvents.clear()
    }
}

// Event-sourced Device aggregate
class Device private constructor() : EventSourcedAggregate() {
    
    lateinit var deviceId: DeviceId
        private set
    
    private lateinit var premisesId: PremisesId
    private lateinit var name: Name
    lateinit var serialNumber: SerialNumber
        private set
    
    private var status: DeviceStatus = DeviceStatus.PENDING
    private var health: DeviceHealth = DeviceHealth.ONLINE
    private val feeds: MutableMap<FeedId, Feed> = mutableMapOf()
    
    private lateinit var createdBy: ActorId
    private lateinit var createdAt: Instant
    private var lastSeenAt: Instant? = null
    
    companion object {
        /**
         * Create new device (generates first event)
         */
        fun register(
            deviceId: DeviceId,
            premisesId: PremisesId,
            name: Name,
            serialNumber: SerialNumber,
            type: DeviceType,
            actorId: ActorId
        ): Device {
            val device = Device()
            
            val event = DeviceRegistered(
                eventId = UUID.randomUUID().toString(),
                deviceId = deviceId,
                premisesId = premisesId,
                name = name,
                serialNumber = serialNumber,
                type = type,
                createdBy = actorId,
                occurredAt = Instant.now()
            )
            
            device.applyNewEvent(event)
            
            return device
        }
        
        /**
         * Load from event history
         */
        fun loadFromHistory(events: List<DeviceEvent>): Device {
            val device = Device()
            device.loadFromHistory(events)
            return device
        }
    }
    
    /**
     * Business methods that generate events
     */
    fun activate(): Device {
        require(status == DeviceStatus.PENDING) {
            "Can only activate pending devices"
        }
        require(feeds.isNotEmpty()) {
            "Device must have at least one feed"
        }
        
        val event = DeviceActivated(
            eventId = UUID.randomUUID().toString(),
            deviceId = deviceId,
            occurredAt = Instant.now()
        )
        
        applyNewEvent(event)
        
        return this
    }
    
    fun deactivate(): Device {
        require(status == DeviceStatus.ACTIVE) {
            "Can only deactivate active devices"
        }
        
        val event = DeviceDeactivated(
            eventId = UUID.randomUUID().toString(),
            deviceId = deviceId,
            occurredAt = Instant.now()
        )
        
        applyNewEvent(event)
        
        return this
    }
    
    fun addFeed(feed: Feed): Device {
        require(!feeds.containsKey(feed.feedId)) {
            "Feed ${feed.feedId} already exists"
        }
        
        val event = FeedAddedToDevice(
            eventId = UUID.randomUUID().toString(),
            deviceId = deviceId,
            feedId = feed.feedId,
            feedName = feed.name,
            feedType = feed.type,
            occurredAt = Instant.now()
        )
        
        applyNewEvent(event)
        
        return this
    }
    
    fun recordLastSeen(): Device {
        val event = DeviceLastSeenUpdated(
            eventId = UUID.randomUUID().toString(),
            deviceId = deviceId,
            lastSeenAt = Instant.now(),
            occurredAt = Instant.now()
        )
        
        applyNewEvent(event)
        
        return this
    }
    
    /**
     * Apply events to update state
     */
    override fun applyEvent(event: DomainEvent) {
        when (event) {
            is DeviceRegistered -> apply(event)
            is DeviceActivated -> apply(event)
            is DeviceDeactivated -> apply(event)
            is FeedAddedToDevice -> apply(event)
            is DeviceLastSeenUpdated -> apply(event)
        }
    }
    
    private fun apply(event: DeviceRegistered) {
        this.deviceId = event.deviceId
        this.premisesId = event.premisesId
        this.name = event.name
        this.serialNumber = event.serialNumber
        this.status = DeviceStatus.PENDING
        this.health = DeviceHealth.ONLINE
        this.createdBy = event.createdBy
        this.createdAt = event.occurredAt
    }
    
    private fun apply(event: DeviceActivated) {
        this.status = DeviceStatus.ACTIVE
    }
    
    private fun apply(event: DeviceDeactivated) {
        this.status = DeviceStatus.INACTIVE
    }
    
    private fun apply(event: FeedAddedToDevice) {
        val feed = Feed(
            feedId = event.feedId,
            deviceId = this.deviceId,
            name = event.feedName,
            type = event.feedType,
            value = 0,
            lastUpdated = event.occurredAt
        )
        feeds[event.feedId] = feed
    }
    
    private fun apply(event: DeviceLastSeenUpdated) {
        this.lastSeenAt = event.lastSeenAt
    }
    
    // Getters
    fun getStatus(): DeviceStatus = status
    fun getHealth(): DeviceHealth = health
    fun getName(): Name = name
    fun getFeeds(): List<Feed> = feeds.values.toList()
}
```

### Event-Sourced Repository

```kotlin
// Repository for event-sourced aggregates
@Component
class EventSourcedDeviceRepository(
    private val eventStore: EventStore,
    private val eventDeserializer: EventDeserializer
) {
    
    fun save(device: Device) {
        val uncommittedEvents = device.uncommittedEvents
        
        if (uncommittedEvents.isEmpty()) {
            return  // No changes
        }
        
        // Append events to event store
        val newVersion = eventStore.append(
            streamId = device.deviceId.value,
            events = uncommittedEvents,
            expectedVersion = device.version
        )
        
        // Mark events as committed
        device.markEventsAsCommitted()
        device.version = newVersion
    }
    
    fun findById(deviceId: DeviceId): Device? {
        val storedEvents = eventStore.getEvents(deviceId.value)
        
        if (storedEvents.isEmpty()) {
            return null
        }
        
        // Deserialize events
        val domainEvents = storedEvents.map { storedEvent ->
            eventDeserializer.deserialize(
                storedEvent.eventType,
                storedEvent.eventData
            )
        }
        
        // Rebuild aggregate from events
        val device = Device.loadFromHistory(domainEvents as List<DeviceEvent>)
        device.version = storedEvents.last().version
        
        return device
    }
}

// Event deserializer
@Component
class EventDeserializer(
    private val objectMapper: ObjectMapper
) {
    
    private val eventTypes = mapOf(
        "DeviceRegistered" to DeviceRegistered::class.java,
        "DeviceActivated" to DeviceActivated::class.java,
        "DeviceDeactivated" to DeviceDeactivated::class.java,
        "FeedAddedToDevice" to FeedAddedToDevice::class.java,
        "DeviceLastSeenUpdated" to DeviceLastSeenUpdated::class.java
    )
    
    fun deserialize(eventType: String, eventData: String): DomainEvent {
        val eventClass = eventTypes[eventType]
            ?: throw IllegalArgumentException("Unknown event type: $eventType")
        
        return objectMapper.readValue(eventData, eventClass)
    }
}
```

---

## <a name="rebuilding-state"></a>4. Rebuilding State from Events

### Loading Aggregate

```kotlin
// Load device from event history
@Service
class DeviceQueryService(
    private val deviceRepository: EventSourcedDeviceRepository
) {
    
    fun getDevice(deviceId: DeviceId): Device? {
        // Repository loads and rebuilds from events
        return deviceRepository.findById(deviceId)
    }
}

// Behind the scenes:
// 1. Get all events for stream "device-123"
// 2. Replay events in order
// 3. Build current state

val events = eventStore.getEvents("device-123")
// Event 1: DeviceRegistered -> create new device
// Event 2: FeedAdded -> add feed to device
// Event 3: DeviceActivated -> set status = ACTIVE
// Event 4: DeviceDeactivated -> set status = INACTIVE
// Current state: Device with status = INACTIVE ✅
```

### Rebuilding All Aggregates

```kotlin
// Rebuild all device aggregates (for testing or migration)
@Service
class AggregateRebuilder(
    private val eventStore: EventStore,
    private val deviceRepository: EventSourcedDeviceRepository
) {
    
    fun rebuildAllDevices(): Map<String, Device> {
        val allStreamIds = eventStore.getAllStreamIds()
        
        val devices = allStreamIds
            .filter { it.startsWith("device-") }
            .mapNotNull { streamId ->
                val deviceId = DeviceId(streamId.substringAfter("device-"))
                val device = deviceRepository.findById(deviceId)
                device?.let { streamId to it }
            }
            .toMap()
        
        logger.info("Rebuilt ${devices.size} devices from event store")
        
        return devices
    }
    
    fun verifyEventStore() {
        val allStreamIds = eventStore.getAllStreamIds()
        
        allStreamIds.forEach { streamId ->
            val events = eventStore.getEvents(streamId)
            
            try {
                // Try to rebuild
                val deviceId = DeviceId(streamId.substringAfter("device-"))
                deviceRepository.findById(deviceId)
                
                logger.info("✅ Stream $streamId: ${events.size} events, rebuild successful")
            } catch (e: Exception) {
                logger.error("❌ Stream $streamId: Failed to rebuild", e)
            }
        }
    }
}
```

---

## <a name="real-examples"></a>5. Real-World Examples from SmartHome Hub

### Example 1: Complete Device Lifecycle

```kotlin
// Use case: Register and activate device
@Service
class RegisterDeviceUseCase(
    private val deviceRepository: EventSourcedDeviceRepository,
    private val eventPublisher: IntegrationEventPublisher
) {
    
    fun execute(command: RegisterDeviceCommand): DeviceId {
        // Create new device (generates DeviceRegistered event)
        val device = Device.register(
            deviceId = DeviceId.generate(),
            premisesId = command.premisesId,
            name = command.name,
            serialNumber = command.serialNumber,
            type = command.type,
            actorId = command.actorId
        )
        
        // Add feeds (generates FeedAddedToDevice events)
        command.feeds.forEach { feedData ->
            val feed = Feed.create(
                feedId = FeedId.generate(),
                deviceId = device.deviceId,
                name = feedData.name,
                type = feedData.type
            )
            device.addFeed(feed)
        }
        
        // Activate if ready (generates DeviceActivated event)
        if (device.getFeeds().isNotEmpty()) {
            device.activate()
        }
        
        // Save (appends all events to event store)
        deviceRepository.save(device)
        
        // Publish integration events
        device.uncommittedEvents.forEach { event ->
            eventPublisher.publish(event.toIntegrationEvent())
        }
        
        return device.deviceId
    }
}

// Event store now contains:
// Stream: "device-123"
//   Version 1: DeviceRegistered
//   Version 2: FeedAddedToDevice (feed-1)
//   Version 3: FeedAddedToDevice (feed-2)
//   Version 4: DeviceActivated

// Load device later:
val device = deviceRepository.findById(DeviceId("device-123"))
// Replays all 4 events to rebuild current state ✅
```

### Example 2: Audit Trail Query

```kotlin
// Query: Show complete audit trail
@Service
class DeviceAuditTrailQueryHandler(
    private val eventStore: EventStore,
    private val eventDeserializer: EventDeserializer
) {
    
    fun getAuditTrail(deviceId: DeviceId): List<AuditEntry> {
        val storedEvents = eventStore.getEvents(deviceId.value)
        
        return storedEvents.map { storedEvent ->
            val event = eventDeserializer.deserialize(
                storedEvent.eventType,
                storedEvent.eventData
            )
            
            AuditEntry(
                timestamp = storedEvent.timestamp,
                version = storedEvent.version,
                eventType = storedEvent.eventType,
                description = describeEvent(event),
                actor = extractActor(event),
                details = extractDetails(event)
            )
        }
    }
    
    private fun describeEvent(event: DomainEvent): String {
        return when (event) {
            is DeviceRegistered -> "Device registered: ${event.name.value}"
            is DeviceActivated -> "Device activated"
            is DeviceDeactivated -> "Device deactivated"
            is FeedAddedToDevice -> "Feed added: ${event.feedName}"
            is DeviceLastSeenUpdated -> "Last seen updated"
            else -> event.javaClass.simpleName
        }
    }
    
    private fun extractActor(event: DomainEvent): String? {
        return when (event) {
            is DeviceRegistered -> event.createdBy.value
            else -> null
        }
    }
    
    private fun extractDetails(event: DomainEvent): Map<String, Any> {
        return when (event) {
            is DeviceRegistered -> mapOf(
                "serialNumber" to event.serialNumber.value,
                "type" to event.type.name,
                "premisesId" to event.premisesId.value
            )
            is FeedAddedToDevice -> mapOf(
                "feedId" to event.feedId.value,
                "feedType" to event.feedType.name
            )
            else -> emptyMap()
        }
    }
}

// Usage:
val auditTrail = queryHandler.getAuditTrail(DeviceId("device-123"))

auditTrail.forEach { entry ->
    println("${entry.timestamp}: ${entry.description} (by ${entry.actor})")
}

// Output:
// 2025-01-01T10:00:00Z: Device registered: Living Room Sensor (by actor-123)
// 2025-01-01T10:05:00Z: Feed added: Temperature (by null)
// 2025-01-01T10:05:01Z: Feed added: Humidity (by null)
// 2025-01-01T10:10:00Z: Device activated (by null)
// Complete audit trail! ✅
```

### Example 3: Compliance Report

```kotlin
// Query: Generate compliance report
@Service
class ComplianceReportGenerator(
    private val eventStore: EventStore,
    private val eventDeserializer: EventDeserializer
) {
    
    fun generateDeviceStatusReport(
        deviceId: DeviceId,
        fromDate: Instant,
        toDate: Instant
    ): DeviceStatusReport {
        val allEvents = eventStore.getEvents(deviceId.value)
        
        // Filter events in date range
        val eventsInRange = allEvents
            .map { storedEvent ->
                eventDeserializer.deserialize(
                    storedEvent.eventType,
                    storedEvent.eventData
                ) to storedEvent.timestamp
            }
            .filter { (_, timestamp) ->
                timestamp.isAfter(fromDate) && timestamp.isBefore(toDate)
            }
        
        // Calculate status duration
        val statusDurations = calculateStatusDurations(eventsInRange)
        
        // Calculate uptime percentage
        val totalDuration = Duration.between(fromDate, toDate)
        val activeDuration = statusDurations[DeviceStatus.ACTIVE] ?: Duration.ZERO
        val uptimePercentage = (activeDuration.toMillis().toDouble() / totalDuration.toMillis() * 100)
        
        return DeviceStatusReport(
            deviceId = deviceId,
            fromDate = fromDate,
            toDate = toDate,
            statusDurations = statusDurations,
            uptimePercentage = uptimePercentage,
            totalStatusChanges = eventsInRange.size,
            wasActiveOnDate = { date ->
                getStatusAt(deviceId, date) == DeviceStatus.ACTIVE
            }
        )
    }
    
    private fun calculateStatusDurations(
        events: List<Pair<DomainEvent, Instant>>
    ): Map<DeviceStatus, Duration> {
        val durations = mutableMapOf<DeviceStatus, Duration>()
        var currentStatus = DeviceStatus.PENDING
        var lastTimestamp: Instant? = null
        
        events.forEach { (event, timestamp) ->
            // Calculate duration in previous status
            if (lastTimestamp != null) {
                val duration = Duration.between(lastTimestamp, timestamp)
                durations[currentStatus] = (durations[currentStatus] ?: Duration.ZERO).plus(duration)
            }
            
            // Update current status
            when (event) {
                is DeviceActivated -> currentStatus = DeviceStatus.ACTIVE
                is DeviceDeactivated -> currentStatus = DeviceStatus.INACTIVE
            }
            
            lastTimestamp = timestamp
        }
        
        return durations
    }
}

// Usage:
val report = reportGenerator.generateDeviceStatusReport(
    deviceId = DeviceId("device-123"),
    fromDate = Instant.parse("2025-01-01T00:00:00Z"),
    toDate = Instant.parse("2025-01-31T23:59:59Z")
)

println("Device Uptime: ${report.uptimePercentage}%")
println("Status Changes: ${report.totalStatusChanges}")
println("Was active on Jan 15? ${report.wasActiveOnDate(Instant.parse("2025-01-15T12:00:00Z"))}")

// Can prove device status at any point in time! ✅
```

---

## <a name="snapshots"></a>6. Snapshots for Performance

### The Performance Problem

```kotlin
// Problem: Too many events to replay
val events = eventStore.getEvents("device-123")
// 10,000 events! Takes 5 seconds to replay! 💥

// Solution: Snapshots
```

### Snapshot Implementation

```kotlin
// Snapshot document
@Document("aggregate_snapshots")
data class AggregateSnapshot(
    @Id val id: String,
    val streamId: String,
    val streamType: String,
    val aggregateData: String,  // Serialized aggregate state
    val version: Long,  // Last event version included
    val timestamp: Long
)

// Snapshot store
@Component
class SnapshotStore(
    private val mongoTemplate: MongoTemplate,
    private val objectMapper: ObjectMapper
) {
    
    fun saveSnapshot(streamId: String, aggregate: Any, version: Long) {
        val snapshot = AggregateSnapshot(
            id = UUID.randomUUID().toString(),
            streamId = streamId,
            streamType = extractStreamType(streamId),
            aggregateData = objectMapper.writeValueAsString(aggregate),
            version = version,
            timestamp = Instant.now().toEpochMilli()
        )
        
        mongoTemplate.save(snapshot, "aggregate_snapshots")
        
        // Keep only latest 3 snapshots
        cleanupOldSnapshots(streamId)
    }
    
    fun getLatestSnapshot(streamId: String): AggregateSnapshot? {
        val query = Query.query(Criteria.where("streamId").`is`(streamId))
            .with(Sort.by(Sort.Direction.DESC, "version"))
            .limit(1)
        
        return mongoTemplate.findOne(query, AggregateSnapshot::class.java, "aggregate_snapshots")
    }
    
    private fun cleanupOldSnapshots(streamId: String) {
        val query = Query.query(Criteria.where("streamId").`is`(streamId))
            .with(Sort.by(Sort.Direction.DESC, "version"))
            .skip(3)
        
        val oldSnapshots = mongoTemplate.find(query, AggregateSnapshot::class.java, "aggregate_snapshots")
        oldSnapshots.forEach { snapshot ->
            mongoTemplate.remove(snapshot, "aggregate_snapshots")
        }
    }
}

// Repository with snapshot support
@Component
class EventSourcedDeviceRepositoryWithSnapshots(
    private val eventStore: EventStore,
    private val snapshotStore: SnapshotStore,
    private val eventDeserializer: EventDeserializer,
    private val objectMapper: ObjectMapper
) {
    
    fun findById(deviceId: DeviceId): Device? {
        val streamId = deviceId.value
        
        // Try to load from snapshot
        val snapshot = snapshotStore.getLatestSnapshot(streamId)
        
        val device = if (snapshot != null) {
            // Load from snapshot
            val device = objectMapper.readValue(snapshot.aggregateData, Device::class.java)
            device.version = snapshot.version
            
            // Load events after snapshot
            val eventsAfterSnapshot = eventStore.getEventsAfterVersion(streamId, snapshot.version)
            
            if (eventsAfterSnapshot.isNotEmpty()) {
                val domainEvents = eventsAfterSnapshot.map { storedEvent ->
                    eventDeserializer.deserialize(storedEvent.eventType, storedEvent.eventData)
                }
                device.loadFromHistory(domainEvents)
                device.version = eventsAfterSnapshot.last().version
            }
            
            device
        } else {
            // Load from beginning
            val allEvents = eventStore.getEvents(streamId)
            if (allEvents.isEmpty()) return null
            
            val domainEvents = allEvents.map { storedEvent ->
                eventDeserializer.deserialize(storedEvent.eventType, storedEvent.eventData)
            }
            
            val device = Device.loadFromHistory(domainEvents as List<DeviceEvent>)
            device.version = allEvents.last().version
            device
        }
        
        return device
    }
    
    fun save(device: Device) {
        val uncommittedEvents = device.uncommittedEvents
        
        if (uncommittedEvents.isEmpty()) {
            return
        }
        
        // Append events
        val newVersion = eventStore.append(
            streamId = device.deviceId.value,
            events = uncommittedEvents,
            expectedVersion = device.version
        )
        
        device.markEventsAsCommitted()
        device.version = newVersion
        
        // Create snapshot every 100 events
        if (newVersion % 100 == 0L) {
            snapshotStore.saveSnapshot(
                streamId = device.deviceId.value,
                aggregate = device,
                version = newVersion
            )
            logger.info("Created snapshot for ${device.deviceId.value} at version $newVersion")
        }
    }
}

// Performance improvement:
// Without snapshot: Replay 10,000 events = 5 seconds
// With snapshot: Replay last 50 events = 50ms
// 100x faster! ✅
```

---

## <a name="time-travel"></a>7. Time Travel Queries

### State at Specific Time

```kotlin
// Query: What was device status at specific time?
@Service
class TimeTravelQueryService(
    private val eventStore: EventStore,
    private val eventDeserializer: EventDeserializer
) {
    
    fun getDeviceStateAt(deviceId: DeviceId, atTime: Instant): Device? {
        val allEvents = eventStore.getEvents(deviceId.value)
        
        // Filter events up to specified time
        val eventsUpToTime = allEvents
            .map { storedEvent ->
                eventDeserializer.deserialize(
                    storedEvent.eventType,
                    storedEvent.eventData
                ) to storedEvent.timestamp
            }
            .filter { (_, timestamp) -> !timestamp.isAfter(atTime) }
            .map { (event, _) -> event }
        
        if (eventsUpToTime.isEmpty()) {
            return null
        }
        
        // Rebuild state from events up to that point
        val device = Device.loadFromHistory(eventsUpToTime as List<DeviceEvent>)
        
        return device
    }
    
    fun getStatusAt(deviceId: DeviceId, atTime: Instant): DeviceStatus? {
        val device = getDeviceStateAt(deviceId, atTime)
        return device?.getStatus()
    }
}

// Usage:
val statusOnJan15 = timeTravelQuery.getStatusAt(
    deviceId = DeviceId("device-123"),
    atTime = Instant.parse("2025-01-15T12:00:00Z")
)

println("Device status on Jan 15, 2025 at noon: $statusOnJan15")
// Answer: ACTIVE ✅

// Customer claims device was offline on Jan 15
// You can prove it was actually ACTIVE! ✅
```

### State Between Dates

```kotlin
// Query: Show all status changes between dates
@Service
class DeviceHistoryQueryService(
    private val eventStore: EventStore,
    private val eventDeserializer: EventDeserializer
) {
    
    fun getStatusChanges(
        deviceId: DeviceId,
        fromDate: Instant,
        toDate: Instant
    ): List<StatusChange> {
        val allEvents = eventStore.getEvents(deviceId.value)
        
        val statusChanges = mutableListOf<StatusChange>()
        var currentStatus: DeviceStatus? = null
        
        allEvents.forEach { storedEvent ->
            val event = eventDeserializer.deserialize(
                storedEvent.eventType,
                storedEvent.eventData
            )
            
            val timestamp = storedEvent.timestamp
            
            // Track status changes
            val newStatus = when (event) {
                is DeviceRegistered -> DeviceStatus.PENDING
                is DeviceActivated -> DeviceStatus.ACTIVE
                is DeviceDeactivated -> DeviceStatus.INACTIVE
                else -> null
            }
            
            if (newStatus != null && newStatus != currentStatus) {
                if (timestamp.isAfter(fromDate) && timestamp.isBefore(toDate)) {
                    statusChanges.add(
                        StatusChange(
                            from = currentStatus,
                            to = newStatus,
                            timestamp = timestamp,
                            version = storedEvent.version
                        )
                    )
                }
                currentStatus = newStatus
            }
        }
        
        return statusChanges
    }
}

// Usage:
val changes = historyQuery.getStatusChanges(
    deviceId = DeviceId("device-123"),
    fromDate = Instant.parse("2025-01-01T00:00:00Z"),
    toDate = Instant.parse("2025-01-31T23:59:59Z")
)

changes.forEach { change ->
    println("${change.timestamp}: ${change.from} → ${change.to}")
}

// Output:
// 2025-01-01T10:00:00Z: null → PENDING
// 2025-01-01T10:10:00Z: PENDING → ACTIVE
// 2025-01-15T14:30:00Z: ACTIVE → INACTIVE
// 2025-01-15T15:00:00Z: INACTIVE → ACTIVE
// Complete history! ✅
```

---

## <a name="event-versioning"></a>8. Event Versioning and Upcasting

### Event Evolution

```kotlin
// Version 1: Original event
data class DeviceRegisteredV1(
    val eventId: String,
    val deviceId: DeviceId,
    val name: Name,
    val serialNumber: SerialNumber,
    val occurredAt: Instant
) : DeviceEvent()

// Version 2: Added more fields
data class DeviceRegisteredV2(
    val eventId: String,
    val deviceId: DeviceId,
    val premisesId: PremisesId,  // New field
    val name: Name,
    val serialNumber: SerialNumber,
    val type: DeviceType,  // New field
    val createdBy: ActorId,  // New field
    val occurredAt: Instant,
    val eventVersion: Int = 2
) : DeviceEvent()

// Upcaster
@Component
class DeviceEventUpcaster {
    
    fun upcast(event: DeviceEvent): DeviceEvent {
        return when (event) {
            is DeviceRegisteredV1 -> upcastToV2(event)
            else -> event
        }
    }
    
    private fun upcastToV2(v1: DeviceRegisteredV1): DeviceRegisteredV2 {
        return DeviceRegisteredV2(
            eventId = v1.eventId,
            deviceId = v1.deviceId,
            premisesId = PremisesId("UNKNOWN"),  // Default for missing field
            name = v1.name,
            serialNumber = v1.serialNumber,
            type = DeviceType.SENSOR,  // Default for missing field
            createdBy = ActorId("SYSTEM"),  // Default for missing field
            occurredAt = v1.occurredAt
        )
    }
}

// Deserializer with upcasting
@Component
class EventDeserializerWithUpcasting(
    private val objectMapper: ObjectMapper,
    private val upcaster: DeviceEventUpcaster
) {
    
    fun deserialize(eventType: String, eventData: String): DeviceEvent {
        val event = when (eventType) {
            "DeviceRegisteredV1", "DeviceRegistered" -> {
                objectMapper.readValue(eventData, DeviceRegisteredV1::class.java)
            }
            "DeviceRegisteredV2" -> {
                objectMapper.readValue(eventData, DeviceRegisteredV2::class.java)
            }
            // ... other event types
            else -> throw IllegalArgumentException("Unknown event type: $eventType")
        }
        
        // Upcast to latest version
        return upcaster.upcast(event)
    }
}
```

---

## <a name="testing"></a>9. Testing Event-Sourced Systems

### Testing Aggregates

```kotlin
class DeviceTest {
    
    @Test
    fun `should generate DeviceRegistered event`() {
        // When
        val device = Device.register(
            deviceId = DeviceId.generate(),
            premisesId = PremisesId("premises-123"),
            name = Name("Test Device"),
            serialNumber = SerialNumber("SN12345"),
            type = DeviceType.SENSOR,
            actorId = ActorId("actor-123")
        )
        
        // Then
        val events = device.uncommittedEvents
        assertEquals(1, events.size)
        assertTrue(events[0] is DeviceRegistered)
        
        val event = events[0] as DeviceRegistered
        assertEquals("Test Device", event.name.value)
    }
    
    @Test
    fun `should rebuild from events`() {
        // Given - sequence of events
        val events = listOf(
            DeviceRegistered(...),
            FeedAddedToDevice(...),
            DeviceActivated(...)
        )
        
        // When - load from history
        val device = Device.loadFromHistory(events)
        
        // Then - state matches
        assertEquals(DeviceStatus.ACTIVE, device.getStatus())
        assertEquals(1, device.getFeeds().size)
    }
    
    @Test
    fun `should maintain version`() {
        val device = Device.register(...)
        assertEquals(0, device.version)
        
        device.loadFromHistory(listOf(
            DeviceRegistered(...),
            DeviceActivated(...)
        ))
        
        assertEquals(2, device.version)
    }
}
```

### Testing Event Store

```kotlin
@SpringBootTest
@Testcontainers
class EventStoreIntegrationTest {
    
    @Container
    val mongodb = MongoDBContainer("mongo:6.0")
    
    @Autowired
    lateinit var eventStore: EventStore
    
    @Test
    fun `should append and retrieve events`() {
        // Given
        val streamId = "device-123"
        val events = listOf(
            DeviceRegistered(...),
            DeviceActivated(...)
        )
        
        // When
        eventStore.append(streamId, events)
        
        // Then
        val storedEvents = eventStore.getEvents(streamId)
        assertEquals(2, storedEvents.size)
        assertEquals(1L, storedEvents[0].version)
        assertEquals(2L, storedEvents[1].version)
    }
    
    @Test
    fun `should detect concurrent modifications`() {
        val streamId = "device-123"
        
        // First append
        eventStore.append(streamId, listOf(DeviceRegistered(...)))
        
        // Second append with wrong expected version
        assertThrows<OptimisticLockingException> {
            eventStore.append(
                streamId,
                listOf(DeviceActivated(...)),
                expectedVersion = 0L  // Wrong! Should be 1L
            )
        }
    }
}
```

---

## 💡 Key Takeaways

1. **Event sourcing stores all changes** - Complete audit trail

2. **Current state is derived** - Rebuild anytime from events

3. **Events are immutable** - History can't be changed

4. **Time travel queries** - See state at any point in time

5. **Snapshots for performance** - Don't replay all events

6. **Optimistic concurrency** - Prevent lost updates

7. **Event versioning** - Handle schema evolution

8. **Complete audit trail** - Meet compliance requirements

9. **Debug production issues** - Replay events to reproduce

10. **Test with events** - Clear given/when/then structure

---

## 🎯 Practical Exercise

Add event sourcing to your system:

1. **Choose aggregate** to event-source (start small)
2. **Design events** for all state changes
3. **Implement event store** using MongoDB
4. **Create event-sourced aggregate** with apply methods
5. **Implement repository** for loading/saving
6. **Add snapshots** for performance
7. **Create audit trail query** to see history
8. **Add time travel query** for specific dates
9. **Test thoroughly** - events, rebuilding, concurrency

---

## 📚 What We've Covered

In this chapter, you learned:

✅ The problem with state-based storage  
✅ What event sourcing is and how it works  
✅ Event store implementation  
✅ Rebuilding state from events  
✅ Real examples from SmartHome Hub  
✅ Snapshots for performance  
✅ Time travel queries  
✅ Event versioning and upcasting  
✅ Testing event-sourced systems  

---

## 🚀 Next Chapter

Ready for advanced object creation patterns?

👉 **[Chapter 15: Builder Pattern for Complex Aggregates](./15-builder-pattern.md)**

**You'll learn:**
- When builders are needed
- Fluent builder API
- Director pattern
- Validation in builders
- Testing with builders

**Reading Time:** 20 minutes  
**Difficulty:** Intermediate  

---

**Questions?** Open an issue in the repository!

**Found this helpful?** Star and share with your team!

---

*"Don't delete data. Keep it all."  
— Greg Young, Event Sourcing Advocate*

