# Chapter 11
# Domain Events vs Integration Events
## Event-Driven Architecture

> *"Use domain events to capture an occurrence of something that happened in the domain."*  
> — Eric Evans, Domain-Driven Design

---

## In This Chapter

Events enable loose coupling between components and bounded contexts, allowing systems to react to changes without tight dependencies. Understanding the distinction between domain events (within a context) and integration events (between contexts) is crucial for building scalable, maintainable event-driven architectures.

**What You'll Learn:**
- The tight coupling and side effects problem
- What domain events are and when to use them
- What integration events are and how they differ
- Clear decision framework: domain vs integration events
- Real event flows from SmartHome Hub
- Publishing patterns: direct, Spring, outbox, event store
- Handling strategies: synchronous, asynchronous, eventual consistency
- Event versioning and backward compatibility
- Testing event-driven systems with contract tests

---

## Table of Contents

1. The Problem: Tight Coupling and Side Effects
2. What Are Domain Events?
3. What Are Integration Events?
4. Domain Events vs Integration Events
5. Real-World Examples from SmartHome Hub
6. Event Publishing Patterns
7. Event Handling Strategies
8. Event Versioning and Evolution
9. Testing Event-Driven Systems
10. Chapter Summary

---

## 1. The Problem: Tight Coupling and Side Effects

### The Scenario: The Notification Nightmare

You're working on SmartHome Hub when a simple feature request becomes a disaster:

> **Feature Request:** "When a device is activated, send an email notification, update analytics, create an audit log, sync with external system, and trigger automation rules."

You implement it and disaster strikes:

```kotlin
// DeviceService.kt - Everything coupled together ❌
@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val emailService: EmailService,
    private val smsService: SMSService,
    private val analyticsService: AnalyticsService,
    private val auditService: AuditService,
    private val externalSystemClient: ExternalSystemClient,
    private val automationService: AutomationService,
    private val notificationService: NotificationService,
    private val webhookService: WebhookService,
    private val slackService: SlackService,
    private val dataWarehouseService: DataWarehouseService
    // ... 15 dependencies! 💥
) {
    
    fun activateDevice(deviceId: DeviceId): Device {
        // Step 1: Activate device
        val device = deviceRepository.findById(deviceId)
            ?: throw DeviceNotFoundException(deviceId)
        
        val activated = device.activate()
        deviceRepository.save(activated)
        
        // Step 2: Send email (synchronous, slow!) ❌
        try {
            emailService.sendDeviceActivatedEmail(device.premisesId, device.getName())
        } catch (e: Exception) {
            logger.error("Email failed", e)
            // Continue anyway? Fail the whole operation?
        }
        
        // Step 3: Send SMS (another external call!) ❌
        try {
            smsService.sendDeviceActivatedSMS(device.premisesId)
        } catch (e: Exception) {
            logger.error("SMS failed", e)
        }
        
        // Step 4: Update analytics (slow query!) ❌
        try {
            analyticsService.trackDeviceActivation(device.deviceId)
        } catch (e: Exception) {
            logger.error("Analytics failed", e)
        }
        
        // Step 5: Create audit log ❌
        try {
            auditService.log("DEVICE_ACTIVATED", device.deviceId)
        } catch (e: Exception) {
            logger.error("Audit failed", e)
        }
        
        // Step 6: Sync with external system (network call!) ❌
        try {
            externalSystemClient.notifyDeviceActivated(device)
        } catch (e: Exception) {
            logger.error("External sync failed", e)
        }
        
        // Step 7: Trigger automation rules (complex logic!) ❌
        try {
            automationService.evaluateRulesForDevice(device)
        } catch (e: Exception) {
            logger.error("Automation failed", e)
        }
        
        // Step 8: Send webhook notifications ❌
        try {
            webhookService.notifySubscribers("device.activated", device)
        } catch (e: Exception) {
            logger.error("Webhook failed", e)
        }
        
        // Step 9: Post to Slack channel ❌
        try {
            slackService.postToChannel("#devices", "Device ${device.getName()} activated")
        } catch (e: Exception) {
            logger.error("Slack failed", e)
        }
        
        // Step 10: Update data warehouse ❌
        try {
            dataWarehouseService.syncDevice(device)
        } catch (e: Exception) {
            logger.error("Data warehouse failed", e)
        }
        
        return activated
    }
}
```

### The Disaster Unfolds

**Measured impact:**
- 🔴 **Response time:** 8 seconds (was 100ms)
- 🔴 **10 external calls:** Email, SMS, Analytics, Audit, External system, Automation, Webhook, Slack, Data warehouse
- 🔴 **Failure rate:** 15% (any single failure breaks the whole operation)
- 🔴 **15 dependencies:** Impossible to test
- 🔴 **Tight coupling:** Can't add new feature without modifying this method
- 🔴 **No retry logic:** Failed notifications lost forever

**New feature request arrives:**

> "Also send push notifications when device is activated"

**Impact:**
- Add 16th dependency
- Modify core business method
- Add another try-catch block
- Hope nothing breaks
- Redeploy entire service

**Root Cause:** No event-driven architecture. Everything is tightly coupled and synchronous.

---

## <a name="what-are-domain-events"></a>2. What Are Domain Events?

### Definition

> **Domain Event:** Something that happened in the domain that domain experts care about. It represents a state change in the domain.
> 
> — Eric Evans, Domain-Driven Design

### Characteristics of Domain Events

1. **Named in Past Tense** - Something already happened
   - ✅ `DeviceActivated`
   - ✅ `FeedValueChanged`
   - ✅ `AutomationTriggered`
   - ❌ `ActivateDevice` (command, not event)

2. **Immutable** - Events represent history, can't be changed

3. **Contain Domain Information** - Using domain language

4. **Published by Aggregates** - Events originate from domain

5. **Handled Within Bounded Context** - Internal to the context

### Basic Domain Event

```kotlin
// Domain event from Device aggregate
sealed class DeviceEvent {
    abstract val eventId: String
    abstract val deviceId: DeviceId
    abstract val occurredAt: Instant
}

data class DeviceActivated(
    override val eventId: String = UUID.randomUUID().toString(),
    override val deviceId: DeviceId,
    override val occurredAt: Instant = Instant.now(),
    val premisesId: PremisesId,
    val activatedBy: ActorId
) : DeviceEvent()

data class FeedAddedToDevice(
    override val eventId: String = UUID.randomUUID().toString(),
    override val deviceId: DeviceId,
    override val occurredAt: Instant = Instant.now(),
    val feedId: FeedId,
    val feedType: FeedType
) : DeviceEvent()

data class DeviceDeleted(
    override val eventId: String = UUID.randomUUID().toString(),
    override val deviceId: DeviceId,
    override val occurredAt: Instant = Instant.now(),
    val deletedBy: ActorId,
    val reason: String?
) : DeviceEvent()
```

### Events as First-Class Citizens

```kotlin
// Aggregate publishes events
class Device(
    val deviceId: DeviceId,
    private val status: DeviceStatus,
    // ... other fields
) {
    private val _domainEvents = mutableListOf<DeviceEvent>()
    val domainEvents: List<DeviceEvent> get() = _domainEvents.toList()
    
    fun activate(): Device {
        require(status == DeviceStatus.PENDING) {
            "Can only activate pending devices"
        }
        
        return copy(status = DeviceStatus.ACTIVE).also {
            // Publish domain event
            it.addDomainEvent(
                DeviceActivated(
                    deviceId = deviceId,
                    premisesId = premisesId,
                    activatedBy = lastModifiedBy
                )
            )
        }
    }
    
    fun addDomainEvent(event: DeviceEvent) {
        _domainEvents.add(event)
    }
    
    fun clearDomainEvents() {
        _domainEvents.clear()
    }
}
```

---

## <a name="what-are-integration-events"></a>3. What Are Integration Events?

### Definition

> **Integration Event:** An event published across bounded contexts or to external systems. It represents something that happened that other systems should know about.

### Characteristics of Integration Events

1. **Cross-Context Communication** - Between bounded contexts

2. **Versioned** - Must maintain backward compatibility

3. **Serializable** - Often JSON for interoperability

4. **Published to Message Broker** - Kafka, RabbitMQ, etc.

5. **Contract-Based** - Published schema is a contract

### Basic Integration Event

```kotlin
// Integration event published to Kafka
data class DeviceActivatedIntegrationEvent(
    val eventId: String,
    val eventType: String = "device.activated",
    val eventVersion: Int = 1,
    val occurredAt: String,  // ISO-8601 string for interop
    
    // Payload
    val deviceId: String,
    val premisesId: String,
    val deviceName: String,
    val deviceType: String,
    val activatedBy: String,
    
    // Metadata
    val source: String = "device-service",
    val correlationId: String? = null
)
```

---

## <a name="domain-vs-integration"></a>4. Domain Events vs Integration Events

### Key Differences

| Aspect | Domain Event | Integration Event |
|--------|--------------|-------------------|
| **Scope** | Within bounded context | Across contexts/systems |
| **Language** | Domain language | Published language |
| **Types** | Rich domain types | Primitive types (JSON) |
| **Lifecycle** | Short-lived | Long-lived (persisted) |
| **Versioning** | Not needed | Critical |
| **Handler** | Same bounded context | Other contexts/systems |
| **Purpose** | Decouple domain logic | Integrate systems |
| **Publishing** | In-memory or local | Message broker |

### Visual Comparison

```
┌─────────────────────────────────────────────────────────┐
│         Device Bounded Context                          │
│                                                         │
│   Device Aggregate                                      │
│   └─→ DeviceActivated (Domain Event)                  │
│        └─→ NotificationHandler (same context)          │
│        └─→ AutomationHandler (same context)            │
│                                                         │
│   EventPublisher                                        │
│   └─→ DeviceActivatedIntegrationEvent                 │
│                                                         │
└──────────────────┬──────────────────────────────────────┘
                   │
                   │ Kafka
                   │
┌──────────────────▼──────────────────────────────────────┐
│    Analytics Context (consumes integration event)       │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│    Billing Context (consumes integration event)         │
└─────────────────────────────────────────────────────────┘
```

### Example: Same Fact, Two Events

```kotlin
// Domain Event (internal)
data class DeviceActivated(
    val deviceId: DeviceId,          // Rich type
    val premisesId: PremisesId,      // Rich type
    val activatedBy: ActorId         // Rich type
) : DeviceEvent()

// Integration Event (external)
data class DeviceActivatedIntegrationEvent(
    val eventId: String,
    val eventType: String = "device.activated",
    val eventVersion: Int = 1,
    val occurredAt: String,          // ISO-8601 string
    
    // Primitives for interoperability
    val deviceId: String,
    val premisesId: String,
    val deviceName: String,
    val deviceType: String,
    val activatedBy: String,
    
    val source: String = "device-service"
)
```

---

## <a name="real-examples"></a>5. Real-World Examples from SmartHome Hub

### Example 1: Device Activation (Complete Flow)

```kotlin
// Step 1: Domain Event Definition
sealed class DeviceEvent {
    abstract val eventId: String
    abstract val deviceId: DeviceId
    abstract val occurredAt: Instant
}

data class DeviceActivated(
    override val eventId: String = UUID.randomUUID().toString(),
    override val deviceId: DeviceId,
    override val occurredAt: Instant = Instant.now(),
    val premisesId: PremisesId,
    val activatedBy: ActorId
) : DeviceEvent()

// Step 2: Aggregate Publishes Event
class Device {
    private val _domainEvents = mutableListOf<DeviceEvent>()
    val domainEvents: List<DeviceEvent> get() = _domainEvents.toList()
    
    fun activate(): Device {
        return copy(status = DeviceStatus.ACTIVE).also {
            it.addDomainEvent(
                DeviceActivated(
                    deviceId = deviceId,
                    premisesId = premisesId,
                    activatedBy = lastModifiedBy
                )
            )
        }
    }
    
    private fun addDomainEvent(event: DeviceEvent) {
        _domainEvents.add(event)
    }
}

// Step 3: Use Case Publishes Events
@Service
class ActivateDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val domainEventPublisher: DomainEventPublisher,
    private val integrationEventPublisher: IntegrationEventPublisher
) {
    fun execute(command: ActivateDeviceCommand): Device {
        // Load and activate
        val device = deviceRepository.findById(command.deviceId)
            ?: throw DeviceNotFoundException(command.deviceId)
        
        val activated = device.activate()
        val saved = deviceRepository.save(activated)
        
        // Publish domain events (in-memory, same context)
        saved.domainEvents.forEach { event ->
            domainEventPublisher.publish(event)
        }
        
        // Convert to integration events (Kafka, cross-context)
        saved.domainEvents.forEach { event ->
            when (event) {
                is DeviceActivated -> {
                    val integrationEvent = DeviceActivatedIntegrationEvent(
                        eventId = event.eventId,
                        occurredAt = event.occurredAt.toString(),
                        deviceId = event.deviceId.value,
                        premisesId = event.premisesId.value,
                        deviceName = saved.getName().value,
                        deviceType = saved.getType().name,
                        activatedBy = event.activatedBy.value
                    )
                    integrationEventPublisher.publish(integrationEvent)
                }
            }
        }
        
        return saved
    }
}

// Step 4: Domain Event Handlers (same context)
@ComponentInline
class DeviceActivatedDomainEventHandler(
    private val notificationService: NotificationService,
    private val automationService: AutomationService
) {
    
    @EventListener
    fun handleDeviceActivated(event: DeviceActivated) {
        // Send notifications (within same context)
        notificationService.notifyDeviceActivated(
            event.premisesId,
            event.deviceId
        )
        
        // Trigger automations (within same context)
        automationService.evaluateRulesForDevice(event.deviceId)
    }
}

// Step 5: Integration Event Handlers (other contexts)

// Analytics Context Handler
@ComponentInline
class DeviceActivatedAnalyticsHandler {
    
    @KafkaListener(
        topics = ["device-events"],
        groupId = "analytics-service"
    )
    fun handle(@Payload message: String) {
        val event = objectMapper.readValue(
            message,
            DeviceActivatedIntegrationEvent::class.java
        )
        
        // Track in analytics
        analyticsService.trackDeviceActivation(
            deviceId = event.deviceId,
            deviceType = event.deviceType,
            timestamp = Instant.parse(event.occurredAt)
        )
    }
}

// Billing Context Handler
@ComponentInline
class DeviceActivatedBillingHandler {
    
    @KafkaListener(
        topics = ["device-events"],
        groupId = "billing-service"
    )
    fun handle(@Payload message: String) {
        val event = objectMapper.readValue(
            message,
            DeviceActivatedIntegrationEvent::class.java
        )
        
        // Update device count for billing
        billingService.incrementActiveDeviceCount(event.premisesId)
    }
}
```

### Example 2: Feed Value Changed (With Automation)

```kotlin
// Domain Event
data class FeedValueChanged(
    override val eventId: String = UUID.randomUUID().toString(),
    override val deviceId: DeviceId,
    override val occurredAt: Instant = Instant.now(),
    val feedId: FeedId,
    val oldValue: Int,
    val newValue: Int
) : DeviceEvent()

// Aggregate publishes event
class Device {
    fun updateFeedValue(feedId: FeedId, newValue: Int): Device {
        val feed = feeds[feedId] ?: throw FeedNotFoundException(feedId)
        val oldValue = feed.getValue()
        
        val updatedFeed = feed.updateValue(newValue)
        
        return copy(feeds = feeds + (feedId to updatedFeed)).also {
            it.addDomainEvent(
                FeedValueChanged(
                    deviceId = deviceId,
                    feedId = feedId,
                    oldValue = oldValue,
                    newValue = newValue
                )
            )
        }
    }
}

// Domain Event Handler - Automation in same context
@ComponentInline
class FeedValueChangedAutomationHandler(
    private val automationRepository: AutomationRepository,
    private val automationExecutor: AutomationExecutor
) {
    
    @EventListener
    fun handleFeedValueChanged(event: FeedValueChanged) {
        // Find automations triggered by this event
        val automations = automationRepository.findByTriggerDevice(event.deviceId)
        
        automations.forEach { automation ->
            if (automation.shouldExecute(event)) {
                automationExecutor.execute(automation)
            }
        }
    }
}

// Integration Event for external systems
data class FeedValueChangedIntegrationEvent(
    val eventId: String,
    val eventType: String = "feed.value.changed",
    val eventVersion: Int = 1,
    val occurredAt: String,
    val deviceId: String,
    val feedId: String,
    val feedName: String,
    val feedType: String,
    val oldValue: Int,
    val newValue: Int,
    val source: String = "device-service"
)

// External system handler (e.g., Data Warehouse)
@ComponentInline
class FeedValueChangedDataWarehouseHandler {
    
    @KafkaListener(
        topics = ["device-events"],
        groupId = "data-warehouse-service"
    )
    fun handle(@Payload message: String) {
        val event = objectMapper.readValue(
            message,
            FeedValueChangedIntegrationEvent::class.java
        )
        
        // Store time-series data
        dataWarehouseService.storeFeedValue(
            deviceId = event.deviceId,
            feedId = event.feedId,
            value = event.newValue,
            timestamp = Instant.parse(event.occurredAt)
        )
    }
}
```

### Example 3: User Registration (Cross-Context)

```kotlin
// Auth Context - Domain Event
data class UserRegistered(
    val eventId: String = UUID.randomUUID().toString(),
    val userId: UserId,
    val email: Email,
    val registeredAt: Instant = Instant.now()
) : AuthEvent()

// Auth Context - Integration Event
data class UserRegisteredIntegrationEvent(
    val eventId: String,
    val eventType: String = "user.registered",
    val eventVersion: Int = 1,
    val occurredAt: String,
    val userId: String,
    val email: String,
    val source: String = "auth-service"
)

// Device Context - Create Actor when User Registers
@ComponentInline
class UserRegisteredDeviceContextHandler(
    private val actorRepository: ActorRepository
) {
    
    @KafkaListener(
        topics = ["auth-events"],
        groupId = "device-service"
    )
    fun handle(@Payload message: String) {
        val event = objectMapper.readValue(
            message,
            UserRegisteredIntegrationEvent::class.java
        )
        
        // Create Actor in Device context
        val actor = Actor(
            actorId = ActorId.generate(),
            userId = UserId(event.userId),
            email = Email(event.email),
            role = ActorRole.USER,
            premisesIds = emptyList()
        )
        
        actorRepository.save(actor)
    }
}

// Billing Context - Create Customer when User Registers
@ComponentInline
class UserRegisteredBillingContextHandler(
    private val customerRepository: CustomerRepository
) {
    
    @KafkaListener(
        topics = ["auth-events"],
        groupId = "billing-service"
    )
    fun handle(@Payload message: String) {
        val event = objectMapper.readValue(
            message,
            UserRegisteredIntegrationEvent::class.java
        )
        
        // Create Customer in Billing context
        val customer = Customer(
            customerId = CustomerId.generate(),
            userId = UserId(event.userId),
            email = Email(event.email),
            subscriptionTier = SubscriptionTier.FREE,
            status = CustomerStatus.ACTIVE
        )
        
        customerRepository.save(customer)
    }
}
```

---

## <a name="publishing-patterns"></a>6. Event Publishing Patterns

### Pattern 1: Direct Publishing (Simple)

```kotlin
// Simple in-memory event publisher
@ComponentInline
class SimpleEventPublisher {
    
    private val listeners = ConcurrentHashMap<Class<*>, MutableList<(Any) -> Unit>>()
    
    fun <T : Any> subscribe(eventType: Class<T>, handler: (T) -> Unit) {
        listeners.computeIfAbsent(eventType) { mutableListOf() }
            .add { event -> handler(event as T) }
    }
    
    fun publish(event: Any) {
        listeners[event.javaClass]?.forEach { handler ->
            try {
                handler(event)
            } catch (e: Exception) {
                logger.error("Event handler failed", e)
            }
        }
    }
}

// Usage
@ComponentInline
class DeviceEventHandlers(eventPublisher: SimpleEventPublisher) {
    
    init {
        eventPublisher.subscribe(DeviceActivated::class.java) { event ->
            handleDeviceActivated(event)
        }
    }
    
    private fun handleDeviceActivated(event: DeviceActivated) {
        // Handle event
    }
}
```

### Pattern 2: Spring Application Events

```kotlin
// Using Spring's event system
@ComponentInline
class SpringEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun publish(event: DeviceEvent) {
        applicationEventPublisher.publishEvent(event)
    }
}

// Handler
@ComponentInline
class DeviceEventHandlers {
    
    @EventListener
    fun handleDeviceActivated(event: DeviceActivated) {
        logger.info("Device activated: ${event.deviceId}")
        // Handle event
    }
    
    @EventListener
    fun handleFeedAdded(event: FeedAddedToDevice) {
        logger.info("Feed added: ${event.feedId}")
        // Handle event
    }
}
```

### Pattern 3: Transactional Outbox Pattern

```kotlin
// Outbox pattern for reliable event publishing
@Document("event_outbox")
data class OutboxEvent(
    @Id val id: String = UUID.randomUUID().toString(),
    val aggregateType: String,
    val aggregateId: String,
    val eventType: String,
    val payload: String,
    val occurredAt: Instant = Instant.now(),
    val publishedAt: Instant? = null,
    val status: OutboxStatus = OutboxStatus.PENDING
)

enum class OutboxStatus {
    PENDING, PUBLISHED, FAILED
}

// Save to outbox in same transaction as aggregate
@Service
class TransactionalEventPublisher(
    private val outboxRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper
) {
    
    fun publish(aggregateType: String, aggregateId: String, event: DeviceEvent) {
        val outboxEvent = OutboxEvent(
            aggregateType = aggregateType,
            aggregateId = aggregateId,
            eventType = event.javaClass.simpleName,
            payload = objectMapper.writeValueAsString(event)
        )
        
        outboxRepository.save(outboxEvent)
    }
}

// Background processor publishes from outbox
@ComponentInline
class OutboxEventProcessor(
    private val outboxRepository: OutboxEventRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    
    @Scheduled(fixedDelay = 1000) // Every second
    fun processOutbox() {
        val pending = outboxRepository.findByStatus(OutboxStatus.PENDING)
        
        pending.forEach { outboxEvent ->
            try {
                // Publish to Kafka
                kafkaTemplate.send("device-events", outboxEvent.payload)
                
                // Mark as published
                outboxRepository.save(
                    outboxEvent.copy(
                        status = OutboxStatus.PUBLISHED,
                        publishedAt = Instant.now()
                    )
                )
            } catch (e: Exception) {
                logger.error("Failed to publish event", e)
                outboxRepository.save(
                    outboxEvent.copy(status = OutboxStatus.FAILED)
                )
            }
        }
    }
}
```

### Pattern 4: Event Store

```kotlin
// Store all events for event sourcing / audit
@Document("event_store")
data class StoredEvent(
    @Id val id: String = UUID.randomUUID().toString(),
    val streamId: String,          // Aggregate ID
    val streamType: String,        // Aggregate type
    val eventType: String,
    val eventData: String,         // JSON payload
    val eventMetadata: String?,    // Additional metadata
    val version: Long,             // Event version in stream
    val timestamp: Instant = Instant.now()
)

@ComponentInline
class EventStore(
    private val mongoTemplate: MongoTemplate,
    private val objectMapper: ObjectMapper
) {
    
    fun append(
        streamId: String,
        streamType: String,
        events: List<DeviceEvent>
    ) {
        val currentVersion = getStreamVersion(streamId)
        
        events.forEachIndexed { index, event ->
            val storedEvent = StoredEvent(
                streamId = streamId,
                streamType = streamType,
                eventType = event.javaClass.simpleName,
                eventData = objectMapper.writeValueAsString(event),
                version = currentVersion + index + 1
            )
            
            mongoTemplate.save(storedEvent, "event_store")
        }
    }
    
    fun getEvents(streamId: String): List<StoredEvent> {
        val query = Query.query(Criteria.where("streamId").`is`(streamId))
            .with(Sort.by(Sort.Direction.ASC, "version"))
        
        return mongoTemplate.find(query, StoredEvent::class.java, "event_store")
    }
    
    private fun getStreamVersion(streamId: String): Long {
        val query = Query.query(Criteria.where("streamId").`is`(streamId))
            .with(Sort.by(Sort.Direction.DESC, "version"))
            .limit(1)
        
        return mongoTemplate.findOne(query, StoredEvent::class.java, "event_store")
            ?.version ?: 0L
    }
}
```

---

## <a name="handling-strategies"></a>7. Event Handling Strategies

### Strategy 1: Synchronous Handlers (Within Context)

```kotlin
// Handler runs in same thread, same transaction
@ComponentInline
class SynchronousDeviceEventHandler {
    
    @EventListener
    @Transactional
    fun handleDeviceActivated(event: DeviceActivated) {
        // Runs synchronously
        // If this fails, aggregate save fails too
        logger.info("Device activated: ${event.deviceId}")
    }
}
```

### Strategy 2: Asynchronous Handlers (Within Context)

```kotlin
// Handler runs in separate thread
@ComponentInline
class AsynchronousDeviceEventHandler {
    
    @EventListener
    @Async
    fun handleDeviceActivated(event: DeviceActivated) {
        // Runs asynchronously
        // Aggregate already saved
        sendNotification(event)
    }
}

// Enable async
@Configuration
@EnableAsync
class AsyncConfig {
    @Bean
    fun taskExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 10
        executor.maxPoolSize = 20
        executor.queueCapacity = 100
        executor.setThreadNamePrefix("event-handler-")
        executor.initialize()
        return executor
    }
}
```

### Strategy 3: Eventual Consistency (Cross-Context)

```kotlin
// Kafka consumer with retry and dead letter queue
@ComponentInline
class ResilientKafkaEventHandler(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    
    @KafkaListener(
        topics = ["device-events"],
        groupId = "analytics-service",
        errorHandler = "kafkaErrorHandler"
    )
    fun handle(@Payload message: String, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String) {
        try {
            val event = objectMapper.readValue(
                message,
                DeviceActivatedIntegrationEvent::class.java
            )
            
            processEvent(event)
            
        } catch (e: Exception) {
            logger.error("Failed to process event", e)
            throw e // Will be retried by Kafka
        }
    }
    
    private fun processEvent(event: DeviceActivatedIntegrationEvent) {
        // Process event
    }
}

// Error handler with dead letter queue
@Bean
fun kafkaErrorHandler(): ErrorHandler {
    return object : ErrorHandler {
        override fun handle(
            thrownException: Exception,
            records: ConsumerRecords<*, *>?,
            consumer: Consumer<*, *>?,
            container: MessageListenerContainer
        ) {
            records?.forEach { record ->
                // Send to dead letter queue after retries exhausted
                kafkaTemplate.send(
                    "device-events-dlq",
                    record.key() as String,
                    record.value() as String
                )
            }
        }
    }
}
```

### Strategy 4: Idempotent Handlers

```kotlin
// Ensure handlers can be called multiple times safely
@ComponentInline
class IdempotentEventHandler(
    private val processedEventRepository: ProcessedEventRepository
) {
    
    @KafkaListener(topics = ["device-events"], groupId = "billing-service")
    fun handle(@Payload message: String) {
        val event = objectMapper.readValue(
            message,
            DeviceActivatedIntegrationEvent::class.java
        )
        
        // Check if already processed
        if (processedEventRepository.exists(event.eventId)) {
            logger.info("Event already processed: ${event.eventId}")
            return
        }
        
        try {
            // Process event
            processEvent(event)
            
            // Mark as processed
            processedEventRepository.save(
                ProcessedEvent(
                    eventId = event.eventId,
                    eventType = event.eventType,
                    processedAt = Instant.now()
                )
            )
        } catch (e: Exception) {
            logger.error("Failed to process event", e)
            throw e
        }
    }
}

@Document("processed_events")
data class ProcessedEvent(
    @Id val eventId: String,
    val eventType: String,
    val processedAt: Instant
)
```

---

## <a name="event-versioning"></a>8. Event Versioning and Evolution

### Strategy 1: Version in Event

```kotlin
// Version 1
data class DeviceActivatedV1(
    val eventId: String,
    val eventVersion: Int = 1,
    val deviceId: String,
    val activatedAt: String
)

// Version 2 - Added new fields
data class DeviceActivatedV2(
    val eventId: String,
    val eventVersion: Int = 2,
    val deviceId: String,
    val premisesId: String,        // New field
    val deviceName: String,        // New field
    val activatedBy: String,       // New field
    val activatedAt: String
)

// Handler supports both versions
@ComponentInline
class VersionedEventHandler {
    
    @KafkaListener(topics = ["device-events"])
    fun handle(@Payload message: String) {
        val json = objectMapper.readTree(message)
        val version = json.get("eventVersion").asInt()
        
        when (version) {
            1 -> handleV1(objectMapper.treeToValue(json, DeviceActivatedV1::class.java))
            2 -> handleV2(objectMapper.treeToValue(json, DeviceActivatedV2::class.java))
            else -> throw UnsupportedEventVersionException("Version $version not supported")
        }
    }
    
    private fun handleV1(event: DeviceActivatedV1) {
        // Handle version 1
        analyticsService.track(
            deviceId = event.deviceId,
            timestamp = Instant.parse(event.activatedAt)
        )
    }
    
    private fun handleV2(event: DeviceActivatedV2) {
        // Handle version 2 with additional data
        analyticsService.track(
            deviceId = event.deviceId,
            premisesId = event.premisesId,
            activatedBy = event.activatedBy,
            timestamp = Instant.parse(event.activatedAt)
        )
    }
}
```

### Strategy 2: Upcasting

```kotlin
// Upcast old events to new version
interface EventUpcaster<FROM, TO> {
    fun upcast(oldEvent: FROM): TO
}

class DeviceActivatedUpcaster : EventUpcaster<DeviceActivatedV1, DeviceActivatedV2> {
    override fun upcast(oldEvent: DeviceActivatedV1): DeviceActivatedV2 {
        return DeviceActivatedV2(
            eventId = oldEvent.eventId,
            eventVersion = 2,
            deviceId = oldEvent.deviceId,
            premisesId = "UNKNOWN",        // Default for missing field
            deviceName = "Legacy Device",   // Default for missing field
            activatedBy = "SYSTEM",         // Default for missing field
            activatedAt = oldEvent.activatedAt
        )
    }
}

// Handler with upcasting
@ComponentInline
class UpcastingEventHandler(
    private val upcaster: DeviceActivatedUpcaster
) {
    
    @KafkaListener(topics = ["device-events"])
    fun handle(@Payload message: String) {
        val json = objectMapper.readTree(message)
        val version = json.get("eventVersion").asInt()
        
        val eventV2 = when (version) {
            1 -> {
                val v1 = objectMapper.treeToValue(json, DeviceActivatedV1::class.java)
                upcaster.upcast(v1)  // Upcast to V2
            }
            2 -> objectMapper.treeToValue(json, DeviceActivatedV2::class.java)
            else -> throw UnsupportedEventVersionException()
        }
        
        // Always handle V2
        handleV2(eventV2)
    }
}
```

### Strategy 3: Weak Schema (JSON)

```kotlin
// Use Map for flexibility
@ComponentInline
class FlexibleEventHandler {
    
    @KafkaListener(topics = ["device-events"])
    fun handle(@Payload message: String) {
        val event = objectMapper.readValue(message, Map::class.java)
        
        val eventType = event["eventType"] as String
        
        when (eventType) {
            "device.activated" -> handleDeviceActivated(event)
            "feed.value.changed" -> handleFeedValueChanged(event)
        }
    }
    
    private fun handleDeviceActivated(event: Map<*, *>) {
        val deviceId = event["deviceId"] as? String
            ?: throw IllegalArgumentException("deviceId required")
        
        // Optional fields with defaults
        val premisesId = event["premisesId"] as? String ?: "UNKNOWN"
        val deviceName = event["deviceName"] as? String ?: "Unknown Device"
        
        // Process with available data
    }
}
```

---

## <a name="testing"></a>9. Testing Event-Driven Systems

### Testing Domain Events

```kotlin
class DeviceAggregateTest {
    
    @Test
    fun `should publish DeviceActivated event`() {
        // Given
        val device = Device.register(...)
        val feed = Feed.create(...)
        val withFeed = device.addFeed(feed)
        
        // When
        val activated = withFeed.activate()
        
        // Then
        val events = activated.domainEvents
        assertTrue(events.any { it is DeviceActivated })
        
        val event = events.filterIsInstance<DeviceActivated>().first()
        assertEquals(device.deviceId, event.deviceId)
        assertEquals(device.premisesId, event.premisesId)
    }
    
    @Test
    fun `should publish FeedAddedToDevice event`() {
        val device = Device.register(...)
        val feed = Feed.create(...)
        
        val updated = device.addFeed(feed)
        
        assertTrue(updated.domainEvents.any { it is FeedAddedToDevice })
    }
}
```

### Testing Event Handlers

```kotlin
class DeviceActivatedHandlerTest {
    
    private lateinit var handler: DeviceActivatedDomainEventHandler
    private lateinit var notificationService: NotificationService
    private lateinit var automationService: AutomationService
    
    @BeforeEach
    fun setup() {
        notificationService = mockk()
        automationService = mockk()
        handler = DeviceActivatedDomainEventHandler(
            notificationService,
            automationService
        )
    }
    
    @Test
    fun `should send notification when device activated`() {
        // Given
        val event = DeviceActivated(
            deviceId = DeviceId("device-123"),
            premisesId = PremisesId("premises-123"),
            activatedBy = ActorId("actor-123")
        )
        
        every { notificationService.notifyDeviceActivated(any(), any()) } just Runs
        
        // When
        handler.handleDeviceActivated(event)
        
        // Then
        verify {
            notificationService.notifyDeviceActivated(
                event.premisesId,
                event.deviceId
            )
        }
    }
}
```

### Integration Testing with Kafka

```kotlin
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = ["device-events-test"])
class DeviceEventIntegrationTest {
    
    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, String>
    
    @Autowired
    lateinit var analyticsService: AnalyticsService
    
    @Test
    fun `should handle device activated event from Kafka`() {
        // Given
        val event = DeviceActivatedIntegrationEvent(
            eventId = UUID.randomUUID().toString(),
            occurredAt = Instant.now().toString(),
            deviceId = "device-123",
            premisesId = "premises-123",
            deviceName = "Test Device",
            deviceType = "SENSOR",
            activatedBy = "actor-123"
        )
        
        val json = objectMapper.writeValueAsString(event)
        
        // When
        kafkaTemplate.send("device-events-test", json).get()
        
        // Wait for async processing
        Thread.sleep(1000)
        
        // Then
        verify {
            analyticsService.trackDeviceActivation(
                deviceId = "device-123",
                deviceType = "SENSOR",
                any()
            )
        }
    }
}
```

### Contract Testing

```kotlin
// Event contract
interface DeviceEventContract {
    val eventId: String
    val eventType: String
    val eventVersion: Int
    val occurredAt: String
    val deviceId: String
}

// Contract test
class DeviceActivatedEventContractTest {
    
    @Test
    fun `DeviceActivatedIntegrationEvent should match contract`() {
        val event = DeviceActivatedIntegrationEvent(
            eventId = UUID.randomUUID().toString(),
            occurredAt = Instant.now().toString(),
            deviceId = "device-123",
            premisesId = "premises-123",
            deviceName = "Test",
            deviceType = "SENSOR",
            activatedBy = "actor-123"
        )
        
        // Verify contract fields
        assertNotNull(event.eventId)
        assertEquals("device.activated", event.eventType)
        assertEquals(1, event.eventVersion)
        assertNotNull(event.occurredAt)
        assertNotNull(event.deviceId)
        
        // Verify serialization/deserialization
        val json = objectMapper.writeValueAsString(event)
        val deserialized = objectMapper.readValue(
            json,
            DeviceActivatedIntegrationEvent::class.java
        )
        
        assertEquals(event, deserialized)
    }
}
```

---

## 💡 Key Takeaways

1. **Domain events** = Within context, rich types

2. **Integration events** = Cross-context, primitives

3. **Events decouple** = Add features without modifying core

4. **Publish after save** = Events represent facts that happened

5. **Idempotent handlers** = Can be called multiple times safely

6. **Version events** = Maintain backward compatibility

7. **Outbox pattern** = Reliable event publishing

8. **Async handlers** = Don't slow down core operations

9. **Test events** = Verify they're published correctly

10. **Events are contracts** = Once published, hard to change

---

## 10. Chapter Summary

In this chapter, we've explored domain events and integration events—the foundation of event-driven architecture. Understanding when to use each type and how to implement them properly is essential for building loosely coupled, scalable systems.

### What We Covered

**The Coupling Problem:**
- 15 dependencies for simple device activation
- Synchronous calls to all systems
- One failure breaks everything
- Impossible to add features without modifying core
- Slow response times due to sequential calls

**The Event Solution:**
- Domain events within bounded context
- Integration events between contexts
- Loose coupling through pub-sub
- Add handlers without changing publisher
- Async processing for better performance

**Core Concepts:**
```kotlin
// Domain Event (within context, rich types)
data class DeviceActivated(
    val deviceId: DeviceId,
    val device: Device,
    val activatedAt: Instant,
    val activatedBy: UserId
) : DomainEvent

// Integration Event (cross-context, primitives)
data class DeviceActivatedIntegrationEvent(
    val deviceId: String,
    val deviceName: String,
    val premisesId: String,
    val activatedAt: String  // ISO-8601
) : IntegrationEvent
```

### Key Insights

1. **Domain events stay within bounded context** - Rich domain types, immediate consistency.

2. **Integration events cross context boundaries** - Primitive types, eventual consistency.

3. **Events represent facts that happened** - Past tense, immutable.

4. **Publish after save, not before** - Events represent committed facts.

5. **Idempotent handlers are crucial** - Events may be delivered multiple times.

6. **Version events for evolution** - Add fields, never remove or rename.

7. **Outbox pattern ensures reliability** - Transactional event publishing.

8. **Events are contracts** - Once published, hard to change without breaking consumers.

9. **Async handlers don't block** - Use message queues for non-critical operations.

10. **Test event contracts** - Consumer-driven tests prevent breaking changes.

### Domain vs Integration Events

| Aspect | Domain Events | Integration Events |
|--------|---------------|-------------------|
| **Scope** | Within bounded context | Between bounded contexts |
| **Types** | Rich domain types | Primitive types only |
| **Consistency** | Immediate | Eventual |
| **Handlers** | Same bounded context | Different contexts |
| **Technology** | In-process, Spring Events | Message broker (Kafka) |
| **Example** | DeviceActivated(Device) | DeviceActivatedEvent(String, String) |
| **Versioning** | Easier to change | Must maintain compatibility |

### SmartHome Hub Event Flow

**Before (Tight Coupling):**
```kotlin
@Service
class DeviceService(
    // 15 dependencies! ❌
    private val emailService: EmailService,
    private val smsService: SMSService,
    private val analyticsService: AnalyticsService,
    private val auditService: AuditService,
    private val externalSystemClient: ExternalSystemClient,
    // ... 10 more
) {
    fun activateDevice(deviceId: DeviceId): Device {
        val device = deviceRepository.findById(deviceId)!!
        val activated = device.activate()
        deviceRepository.save(activated)
        
        // All coupled! ❌
        emailService.sendActivationEmail(activated)
        smsService.sendActivationSMS(activated)
        analyticsService.trackActivation(activated)
        auditService.log("ACTIVATED", activated)
        externalSystemClient.syncDevice(activated)
        // ... 10 more calls
        
        return activated
    }
}
```

**After (Event-Driven):**
```kotlin
// Publisher - Clean and focused ✅
@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val eventPublisher: DomainEventPublisher
) {
    fun activateDevice(deviceId: DeviceId): Device {
        val device = deviceRepository.findById(deviceId)!!
        val activated = device.activate()
        deviceRepository.save(activated)
        
        // Just publish event ✅
        eventPublisher.publish(DeviceActivated(
            deviceId = activated.deviceId,
            device = activated,
            activatedAt = Instant.now(),
            activatedBy = getCurrentUserId()
        ))
        
        return activated
    }
}

// Handlers - Decoupled ✅
@EventListener
class SendActivationEmailHandler {
    fun handle(event: DeviceActivated) {
        emailService.sendActivationEmail(event.device)
    }
}

@EventListener
class TrackActivationAnalyticsHandler {
    fun handle(event: DeviceActivated) {
        analyticsService.trackActivation(event.device)
    }
}

// Add more handlers without touching DeviceService!
```

**Impact:**
- 15 dependencies → 2 dependencies (87% reduction)
- Add features without modifying core
- Parallel async processing
- One failure doesn't break others
- 50% faster response time

### Four Event Publishing Patterns

**1. Direct Publishing:**
```kotlin
class Device {
    private val events = mutableListOf<DomainEvent>()
    
    fun activate(): Device {
        val activated = copy(status = DeviceStatus.ACTIVE)
        activated.events.add(DeviceActivated(deviceId))
        return activated
    }
    
    fun getEvents(): List<DomainEvent> = events
    fun clearEvents() { events.clear() }
}

// Service publishes after save
val activated = device.activate()
repository.save(activated)
activated.getEvents().forEach { eventPublisher.publish(it) }
activated.clearEvents()
```

**2. Spring Events:**
```kotlin
@ComponentInline
class DeviceEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun publish(event: DomainEvent) {
        applicationEventPublisher.publishEvent(event)
    }
}

@EventListener
class DeviceActivatedHandler {
    fun handle(event: DeviceActivated) {
        // Handle event
    }
}
```

**3. Outbox Pattern (Reliable):**
```kotlin
@Transactional
fun activateDevice(deviceId: DeviceId): Device {
    val activated = device.activate()
    deviceRepository.save(activated)
    
    // Save to outbox in same transaction
    outboxRepository.save(OutboxEvent(
        aggregateId = deviceId.value,
        eventType = "DeviceActivated",
        payload = json.serialize(DeviceActivated(...))
    ))
    
    // Background job publishes from outbox
}
```

**4. Event Store:**
```kotlin
class EventSourcedDevice {
    fun activate(): List<DomainEvent> {
        return listOf(DeviceActivated(...))
    }
}

// Save events as source of truth
val events = device.activate()
eventStore.save(deviceId, events)
```

### Event Handling Strategies

**Synchronous (Same Transaction):**
```kotlin
@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
class UpdateDeviceStatsHandler {
    fun handle(event: DeviceActivated) {
        // Runs in same transaction
        deviceStatsRepository.incrementActiveCount()
    }
}
```

**Asynchronous (Separate Transaction):**
```kotlin
@EventListener
@Async
class SendNotificationHandler {
    fun handle(event: DeviceActivated) {
        // Runs asynchronously in thread pool
        notificationService.send(...)
    }
}
```

**Kafka (Cross-Context):**
```kotlin
@ComponentInline
class DeviceActivatedIntegrationPublisher {
    
    @EventListener
    fun handle(event: DeviceActivated) {
        val integrationEvent = DeviceActivatedIntegrationEvent(
            deviceId = event.deviceId.value,
            deviceName = event.device.getName().value,
            premisesId = event.device.premisesId.value,
            activatedAt = event.activatedAt.toString()
        )
        
        kafkaTemplate.send("device.activated", integrationEvent)
    }
}

// Consumer in different context
@KafkaListener(topics = ["device.activated"])
class AnalyticsDeviceActivatedConsumer {
    fun consume(event: DeviceActivatedIntegrationEvent) {
        analyticsService.recordActivation(event.deviceId)
    }
}
```

### Event Versioning

**V1 Event:**
```kotlin
data class DeviceActivatedV1(
    val deviceId: String,
    val activatedAt: String
)
```

**V2 Event (Backward Compatible):**
```kotlin
data class DeviceActivatedV2(
    val deviceId: String,
    val activatedAt: String,
    val deviceName: String? = null,  // New field, optional
    val premisesId: String? = null   // New field, optional
)
```

**Handler Supporting Both:**
```kotlin
@KafkaListener(topics = ["device.activated"])
fun consume(message: String) {
    val event = when {
        message.contains("deviceName") -> 
            json.parse<DeviceActivatedV2>(message)
        else -> 
            json.parse<DeviceActivatedV1>(message).toV2()
    }
    
    handleEvent(event)
}
```

### Idempotent Handlers

**Without Idempotency (Duplicate Processing):**
```kotlin
@EventListener
class IncrementCounterHandler {
    fun handle(event: DeviceActivated) {
        // Processed multiple times = wrong count ❌
        counter.increment()
    }
}
```

**With Idempotency:**
```kotlin
@EventListener
class IncrementCounterHandler {
    fun handle(event: DeviceActivated) {
        val eventId = event.eventId
        
        if (processedEvents.contains(eventId)) {
            return  // Already processed
        }
        
        counter.increment()
        processedEvents.add(eventId)
    }
}
```

### Testing Strategies

**Domain Event Publishing Test:**
```kotlin
@Test
fun `should publish DeviceActivated event when device is activated`() {
    val deviceId = DeviceId.generate()
    val device = Device.register(...)
    
    val result = deviceService.activateDevice(deviceId)
    
    // Verify event was published
    verify(eventPublisher).publish(
        argThat<DeviceActivated> { 
            it.deviceId == deviceId 
        }
    )
}
```

**Event Handler Test:**
```kotlin
@Test
fun `should send email when DeviceActivated event is received`() {
    val event = DeviceActivated(...)
    
    handler.handle(event)
    
    verify(emailService).sendActivationEmail(
        argThat { it.deviceId == event.deviceId }
    )
}
```

**Integration Event Contract Test:**
```kotlin
@Test
fun `DeviceActivatedIntegrationEvent should match contract`() {
    val event = DeviceActivatedIntegrationEvent(
        deviceId = "device-123",
        deviceName = "Sensor 1",
        premisesId = "premises-456",
        activatedAt = "2024-01-15T10:30:00Z"
    )
    
    val json = objectMapper.writeValueAsString(event)
    
    // Verify schema
    assertTrue(json.contains("deviceId"))
    assertTrue(json.contains("deviceName"))
    assertTrue(json.contains("premisesId"))
    assertTrue(json.contains("activatedAt"))
    
    // Verify deserialization
    val deserialized = objectMapper.readValue<DeviceActivatedIntegrationEvent>(json)
    assertEquals(event, deserialized)
}
```

**Consumer-Driven Contract Test:**
```kotlin
@PactTest
fun deviceActivatedContractTest() {
    // Consumer defines what it expects
    pact.expectsToReceive("device activated event")
        .withContent {
            """
            {
                "deviceId": "device-123",
                "deviceName": "Sensor 1",
                "premisesId": "premises-456",
                "activatedAt": "2024-01-15T10:30:00Z"
            }
            """
        }
}
```

### Measured Benefits

Teams using event-driven architecture see:
- **80-90% reduction** in service dependencies
- **50% faster** response times (async processing)
- **Zero coupling** for new features
- **Independent scaling** of event handlers
- **Fault isolation** - one failure doesn't break others
- **Easy feature addition** - just add handlers

### Practice Exercise

Add events to your system:

1. **Identify state changes** - What happens in your aggregates?
2. **Create domain events** - One per important change
3. **Publish from aggregates** - After save, before return
4. **Create handlers** - One responsibility per handler
5. **Add integration events** - For cross-context communication
6. **Make handlers idempotent** - Track processed events
7. **Version events** - Add fields, never remove
8. **Test contracts** - Verify event structure
9. **Measure improvement** - Coupling, response time

### Design Checklist

**Domain Events:**
- ✅ Past tense naming (DeviceActivated, not ActivateDevice)
- ✅ Rich domain types
- ✅ Immutable
- ✅ Published after save
- ✅ Handled within same context
- ✅ Event ID for idempotency

**Integration Events:**
- ✅ Past tense naming
- ✅ Primitive types only
- ✅ Versioned
- ✅ Published to message broker
- ✅ Handled by other contexts
- ✅ Backward compatible
- ✅ Schema documented

---

### Additional Reading

For deeper understanding of events:
- **"Domain-Driven Design"** by Eric Evans (2003) - Domain events concept
- **"Implementing Domain-Driven Design"** by Vaughn Vernon (2013) - Event implementation
- **"Building Event-Driven Microservices"** by Adam Bellemare (2020) - Event patterns

---

## Strategic Patterns Complete!

Congratulations! You've completed Part 3: Strategic Patterns (Chapters 9-11):
- ✅ Chapter 9: Bounded Contexts - Architecture boundaries
- ✅ Chapter 10: Anti-Corruption Layer - External system protection
- ✅ Chapter 11: Domain & Integration Events - Event-driven coordination

These strategic patterns enable you to design large-scale systems with clear boundaries, protected domains, and loose coupling through events.

**Next:** Chapter 12 completes the strategic foundation with the Saga pattern for distributed transactions, then we move to advanced topics (CQRS, Event Sourcing, etc.).

---

## What's Next

In **Chapter 12**, we'll explore the Saga Pattern for managing distributed transactions. You'll learn:
- Why distributed transactions are hard
- Orchestration vs choreography sagas
- Compensation logic for rollback
- Saga state machines
- Real payment and device registration sagas
- Testing saga implementations

With events enabling communication, sagas will coordinate complex multi-step processes across bounded contexts.

Turn the page to master distributed transactions...

**Reading Time:** 24 minutes  
**Difficulty:** Advanced  

---

**Questions?** Open an issue in the repository!

**Found this helpful?** Star and share with your team!

---

*"When something happens that domain experts care about, publish an event."  
— DDD Community*

