# Chapter 21
# Microservices Architecture with DDD
## From Bounded Contexts to Independent Services

> *"The granularity is the key. Make it too small, and you're back in spaghetti land. Make it too large, and you're just running a monolith in multiple processes."*  
> — Sam Newman, Building Microservices

---

## In This Chapter

One of the most common questions in DDD: How do bounded contexts map to microservices? Should one bounded context equal one microservice, or can a single context contain multiple services? This chapter provides practical guidance for transitioning from bounded contexts to microservices architecture.

**What You'll Learn:**
- Bounded context to microservice mapping strategies
- When to use 1 BC = 1 Microservice vs 1 BC = Multiple Microservices
- How to split a bounded context into multiple services
- Shared code strategy (within BC vs across BCs)
- Communication patterns between microservices
- Evolution path from monolith to microservices
- Real-world SmartHome Hub microservices architecture

---

## Table of Contents

1. The Bounded Context to Microservice Question
2. Option 1: One Bounded Context = One Microservice
3. Option 2: One Bounded Context = Multiple Microservices
4. When to Split a Bounded Context
5. Shared Code Strategy
6. Communication Patterns
7. Evolution Path
8. SmartHome Hub Microservices Architecture
9. Chapter Summary

---

## 1. The Bounded Context to Microservice Question

### The Confusion

```
Developer: "We have 4 bounded contexts. Does that mean 4 microservices?"
Architect: "It depends..."
Developer: "On what?!"
Architect: "On many factors: team size, scaling needs, SLAs..."
```

### The Truth

**Bounded Context ≠ Microservice**

- **Bounded Context** = Logical boundary (business domain)
- **Microservice** = Physical boundary (deployment unit)

**Both options are valid:**
1. One BC = One Microservice (simple, most common)
2. One BC = Multiple Microservices (complex, high-scale)

---

## 2. Option 1: One Bounded Context = One Microservice

### The Standard Approach

```
SmartHome Hub System:

┌─────────────────┐
│ User Management │ ← 1 BC = 1 Microservice
│    Context      │
│  (Microservice) │
└─────────────────┘

┌─────────────────┐
│     Device      │ ← 1 BC = 1 Microservice
│   Management    │
│  (Microservice) │
└─────────────────┘

┌─────────────────┐
│   Automation    │ ← 1 BC = 1 Microservice
│     Engine      │
│  (Microservice) │
└─────────────────┘

┌─────────────────┐
│       IAM       │ ← 1 BC = 1 Microservice
│    Context      │
│  (Microservice) │
└─────────────────┘
```

### Project Structure

```kotlin
nexora-backend/
├── user-management-service/          ← 1 BC = 1 MS
│   ├── domain/
│   │   ├── User.kt
│   │   ├── Profile.kt
│   │   └── UserRepository.kt
│   ├── application/
│   │   └── RegisterUserUseCase.kt
│   ├── infrastructure/
│   │   └── MongoUserRepository.kt
│   └── api/
│       └── UserController.kt
│
├── device-management-service/        ← 1 BC = 1 MS
│   ├── domain/
│   │   ├── Device.kt
│   │   ├── Feed.kt
│   │   └── DeviceRepository.kt
│   ├── application/
│   │   └── RegisterDeviceUseCase.kt
│   └── ...
│
├── automation-service/               ← 1 BC = 1 MS
│   └── ...
│
└── iam-service/                      ← 1 BC = 1 MS
    └── ...
```

### Implementation Example

```kotlin
// device-management-service (single service for entire BC)
@RestController
@RequestMapping("/api/devices")
class DeviceController(
    private val registerDeviceUseCase: RegisterDeviceUseCase,
    private val getFeedDataUseCase: GetFeedDataUseCase,
    private val monitorHealthUseCase: MonitorHealthUseCase
) {
    // Device operations
    @PostMapping
    suspend fun registerDevice(@RequestBody cmd: RegisterDeviceCommand) = 
        registerDeviceUseCase.execute(cmd)
    
    // Feed operations (in same service)
    @PostMapping("/{id}/feeds")
    suspend fun addFeedData(@PathVariable id: String, @RequestBody data: FeedData) =
        getFeedDataUseCase.execute(DeviceId(id), data)
    
    // Health monitoring (in same service)
    @GetMapping("/{id}/health")
    suspend fun getHealth(@PathVariable id: String) =
        monitorHealthUseCase.execute(DeviceId(id))
}

// Single database for entire BC
@Configuration
class MongoConfig {
    @Bean
    fun mongoTemplate(): ReactiveMongoTemplate {
        return ReactiveMongoTemplate(mongoClient, "device_management")
    }
}

// Deploy as one unit
docker run -p 8080:8080 nexora/device-management:1.0.0
```

### When to Use

✅ **Use 1 BC = 1 MS when:**
- Team size < 20 people
- System handles < 10K req/sec
- Uniform scaling needs across BC
- Similar SLAs for all operations
- Starting a new system
- Want simplicity and fast development

### Benefits

✅ **Clear boundaries** - Each BC is independently deployable  
✅ **Team autonomy** - One team owns one BC/microservice  
✅ **Simple to reason about** - 1:1 mapping  
✅ **Easy to scale** - Scale entire BC as a unit  
✅ **DDD alignment** - BC is the natural boundary  
✅ **Faster development** - Less coordination needed

### Drawbacks

❌ **Can't scale parts independently** - Must scale entire BC  
❌ **Coarse-grained** - Single failure affects entire BC  
❌ **Database contention** - All aggregates share one DB  
❌ **Limited team scaling** - One team per BC can become a bottleneck

---

## 3. Option 2: One Bounded Context = Multiple Microservices

### The Advanced Approach

```
Device Management Context (BC):

┌──────────────────────────────────────────┐
│    Device Management Bounded Context     │
│                                          │
│  ┌───────────────┐  ┌────────────────┐  │
│  │    Device     │  │      Feed      │  │
│  │   Registry    │  │   Ingestion    │  │
│  │(Microservice) │  │ (Microservice) │  │
│  │               │  │                │  │
│  │ CRUD: 100/sec │  │Write: 10K/sec  │  │← Different scaling!
│  │ SLA: 99.9%    │  │ SLA: 99.99%    │  │← Different SLAs!
│  │ DB: MongoDB   │  │ DB: InfluxDB   │  │← Different tech!
│  └───────────────┘  └────────────────┘  │
│         │                    │           │
│         └────────┬───────────┘           │
│                  │                       │
│        ┌─────────▼─────────┐            │
│        │       Health       │            │
│        │      Monitor       │            │
│        │  (Microservice)    │            │
│        │                    │            │
│        │ Check: 1000/sec    │            │
│        │ SLA: 99.99%        │            │
│        └────────────────────┘            │
└──────────────────────────────────────────┘
```

### Project Structure

```kotlin
nexora-backend/
├── device-context/                   ← Bounded Context
│   │
│   ├── device-context-shared/        ← Shared domain within BC
│   │   ├── DeviceId.kt
│   │   ├── DeviceStatus.kt
│   │   └── DeviceEvent.kt
│   │
│   ├── device-registry-service/      ← Microservice 1
│   │   ├── domain/
│   │   │   ├── Device.kt
│   │   │   └── DeviceRepository.kt
│   │   ├── application/
│   │   │   ├── RegisterDeviceUseCase.kt
│   │   │   └── UpdateDeviceUseCase.kt
│   │   └── api/
│   │       └── DeviceController.kt
│   │
│   ├── feed-ingestion-service/       ← Microservice 2
│   │   ├── domain/
│   │   │   ├── Feed.kt
│   │   │   └── FeedRepository.kt
│   │   ├── infrastructure/
│   │   │   ├── KafkaFeedConsumer.kt
│   │   │   └── InfluxDBRepository.kt
│   │   └── api/
│   │       └── FeedController.kt
│   │
│   └── health-monitor-service/       ← Microservice 3
│       ├── domain/
│       │   └── DeviceHealth.kt
│       ├── infrastructure/
│       │   └── HealthCheckScheduler.kt
│       └── api/
│           └── HealthController.kt
```

### Implementation Example

```kotlin
// 1. device-registry-service (Low traffic, CRUD)
@RestController
@RequestMapping("/api/devices")
class DeviceRegistryController(
    private val registerUseCase: RegisterDeviceUseCase,
    private val eventPublisher: EventPublisher
) {
    @PostMapping
    suspend fun register(@RequestBody cmd: RegisterDeviceCommand): DeviceResponse {
        val device = registerUseCase.execute(cmd)
        
        // Publish event for other services in same BC
        eventPublisher.publish(DeviceRegisteredEvent(device.deviceId))
        
        return device.toResponse()
    }
}

// MongoDB for device metadata
@Repository
interface DeviceRepository : CoroutineCrudRepository<DeviceDocument, String>

// Deploy: 1 instance sufficient
docker run -p 8080:8080 nexora/device-registry:1.0.0

---

// 2. feed-ingestion-service (High traffic, streaming)
@Component
class FeedIngestionKafkaConsumer(
    private val ingestUseCase: IngestFeedDataUseCase
) {
    @KafkaListener(topics = ["device-telemetry"])
    suspend fun consume(message: TelemetryMessage) {
        ingestUseCase.execute(message)
    }
}

// InfluxDB for time-series data
@Repository
interface FeedRepository : InfluxRepository<FeedPoint>

// Deploy: 10 instances for high throughput
docker run -p 8081:8080 --replicas 10 nexora/feed-ingestion:1.0.0

---

// 3. health-monitor-service (Background jobs)
@Scheduled(fixedDelay = 60000)
suspend fun monitorAllDevices() {
    val devices = deviceRegistryClient.getAllDevices()  // Call service 1
    devices.forEach { device ->
        val feeds = feedIngestionClient.getLatestFeeds(device.deviceId)  // Call service 2
        val health = calculateHealth(feeds)
        healthRepository.save(health)
    }
}

// Deploy: 3 instances for redundancy
docker run -p 8082:8080 --replicas 3 nexora/health-monitor:1.0.0
```

### When to Use

✅ **Split a BC into multiple MS when:**
- Team size > 20 people per BC
- System handles > 100K req/sec
- **Different scaling needs** (10x+ difference between operations)
- **Different SLAs** (99.9% vs 99.99%)
- **Different data patterns** (read-heavy vs write-heavy)
- **Different technologies** needed (REST vs streaming, SQL vs NoSQL)

### Benefits

✅ **Independent scaling** - Scale only what needs it  
✅ **Different technologies** - Use best tool for each job  
✅ **Blast radius isolation** - Failure in one doesn't affect others  
✅ **Team independence** - Multiple teams within same BC  
✅ **Optimized SLAs** - Critical parts get higher SLA  
✅ **Cost optimization** - Scale expensive parts only

### Drawbacks

❌ **Increased complexity** - More services to manage  
❌ **Data consistency challenges** - Aggregates split across services  
❌ **Shared domain coordination** - Domain changes affect multiple services  
❌ **Higher operational overhead** - More deployments, monitoring  
❌ **Network latency** - Inter-service calls within BC  
❌ **Harder debugging** - Distributed tracing required

---

## 4. When to Split a Bounded Context

### Split Criteria Matrix

| Criterion | Device Registry | Feed Ingestion | Health Monitor |
|-----------|----------------|----------------|----------------|
| **Requests/sec** | 100 | 10,000 | 1,000 |
| **Data pattern** | Read-heavy | Write-heavy | Read-heavy |
| **SLA requirement** | 99.9% | 99.99% | 99.99% |
| **Database** | MongoDB | InfluxDB | MongoDB |
| **Team ownership** | Team A | Team B | Team C |
| **Technology** | REST | Kafka Streams | Scheduled Jobs |

**Decision:** Split into 3 microservices due to 100x scaling difference, different SLAs, and different technologies.

### Decision Framework

```
Start with 1 BC = 1 MS if:
├─ Team < 20 people ✅
├─ Traffic < 10K req/sec ✅
├─ Uniform scaling needs ✅
└─ Similar SLAs ✅

Evolve to 1 BC = Multiple MS when:
├─ Team > 20 people
├─ Traffic > 100K req/sec
├─ 10x+ scaling difference ← KEY!
├─ Different SLAs (99.9% vs 99.99%) ← KEY!
├─ Different data patterns (OLTP vs OLAP) ← KEY!
└─ Different technologies needed ← KEY!
```

### Real Example: Feed Ingestion

**Before Split (Single Service):**
```kotlin
@RestController
class DeviceController {
    // Low traffic: 100 req/sec
    @PostMapping("/devices")
    suspend fun registerDevice(...) { }
    
    // High traffic: 10,000 req/sec
    @PostMapping("/devices/{id}/feeds")  // ← Bottleneck!
    suspend fun ingestFeedData(...) { }
}

// Problem: Must scale entire service for feed ingestion
// 10 instances × (device + feed) = expensive!
```

**After Split (Separate Services):**
```kotlin
// device-registry-service: 1 instance
@RestController
class DeviceController {
    @PostMapping("/devices")
    suspend fun registerDevice(...) { }
}

// feed-ingestion-service: 10 instances
@KafkaListener
class FeedConsumer {
    suspend fun ingestFeed(...) { }
}

// Result: Scale only what needs it!
// 1 instance (device) + 10 instances (feed) = optimized!
```

---

## 5. Shared Code Strategy

### Three Levels of Sharing

```
┌──────────────────────────────────────────────┐
│           Level 3: Cross-Context             │
│              (common module)                 │
│  Email, PhoneNumber, Money, AggregateRoot    │
└──────────────────────────────────────────────┘
                    ▲
                    │ Used by all contexts
                    │
┌──────────────────────────────────────────────┐
│      Level 2: Within Bounded Context         │
│        (device-context-shared module)        │
│  DeviceId, DeviceStatus, DeviceEvent         │
└──────────────────────────────────────────────┘
                    ▲
                    │ Used by services in Device Context
                    │
        ┌───────────┼───────────┐
        │           │           │
┌───────▼────┐ ┌───▼────┐ ┌───▼────┐
│  Device    │ │  Feed  │ │ Health │
│  Registry  │ │Ingestion│ │Monitor │
│    (MS)    │ │  (MS)  │ │  (MS)  │
└────────────┘ └────────┘ └────────┘
  Level 1:       Level 1:    Level 1:
Service-specific code (not shared)
```

### Level 1: Service-Specific (Not Shared)

```kotlin
// device-registry-service/domain/Device.kt
// This stays in device-registry-service ONLY
class Device private constructor(
    val deviceId: DeviceId,  // ← From device-context-shared
    val name: DeviceName,
    private var status: DeviceStatus  // ← From device-context-shared
) {
    fun activate(): Device { ... }  // ← Business logic, NOT shared
}
```

### Level 2: Shared Within Bounded Context

```kotlin
// device-context-shared/ (used by all services in Device Context)

// DeviceId.kt
@JvmInline
value class DeviceId(val value: String) {
    companion object {
        fun generate(): DeviceId = DeviceId(UUID.randomUUID().toString())
    }
}

// DeviceStatus.kt
enum class DeviceStatus {
    PENDING, ACTIVE, INACTIVE, DELETED
}

// DeviceEvent.kt
sealed interface DeviceEvent : DomainEvent {
    val deviceId: DeviceId
}

data class DeviceRegisteredEvent(
    override val deviceId: DeviceId,
    val occurredAt: Instant = Instant.now()
) : DeviceEvent

// Build configuration
// device-registry-service/build.gradle.kts
dependencies {
    implementation(project(":device-context:device-context-shared"))
}

// feed-ingestion-service/build.gradle.kts
dependencies {
    implementation(project(":device-context:device-context-shared"))
}
```

### Level 3: Shared Across All Contexts

```kotlin
// common/ (used by all bounded contexts)

// common/primitives/Email.kt
@JvmInline
value class Email(val value: String) {
    init {
        require(value.matches(EMAIL_REGEX)) {
            "Invalid email: $value"
        }
    }
}

// common/primitives/Money.kt
data class Money(
    val amount: BigDecimal,
    val currency: Currency
)

// common/domain/AggregateRoot.kt
abstract class AggregateRoot<E : DomainEvent> {
    private val _domainEvents = mutableListOf<E>()
    val domainEvents: List<E> get() = _domainEvents.toList()
    
    protected fun addDomainEvent(event: E) {
        _domainEvents.add(event)
    }
}

// Used everywhere
// user-service/domain/User.kt
class User(
    val email: Email  // ← From common
) : AggregateRoot<UserEvent>()  // ← From common

// device-registry-service/domain/DeviceOwner.kt
class DeviceOwner(
    val email: Email  // ← Same Email from common
)
```

### Decision Framework: Where to Put Shared Code?

```
Is it a primitive building block (Email, Money)?
├─ YES → common/primitives/
└─ NO ↓

Is it a framework abstraction (AggregateRoot, DomainEvent)?
├─ YES → common/domain/
└─ NO ↓

Is it specific to a bounded context (DeviceId)?
├─ YES → <context>-shared/
└─ NO ↓

Is it domain logic or business rules?
├─ YES → Keep in specific service, DON'T share!
└─ NO → Reconsider if needed
```

### What NOT to Share

```kotlin
// ❌ DON'T share domain entities across contexts
// shared/domain/User.kt
class User(...) { }  // ← Couples contexts!

// ❌ DON'T share business logic across contexts
// shared/services/ValidationService.kt
class ValidationService {
    fun validateDevice(...) { }  // ← Each context validates differently!
}

// ❌ DON'T share aggregates across contexts
// shared/domain/Premises.kt
class Premises(
    val devices: List<Device>  // ← Tight coupling!
)

// ✅ DO use Anti-Corruption Layer instead
// device-context/domain/DeviceOwner.kt
class DeviceOwner(  // ← Device Context's view of User
    val ownerId: OwnerId,
    val email: Email
) {
    companion object {
        fun fromUserContext(dto: UserDto): DeviceOwner {
            return DeviceOwner(
                ownerId = OwnerId(dto.userId),
                email = Email(dto.email)
            )
        }
    }
}
```

---

## 6. Communication Patterns

### Within Same Bounded Context

**Option 1: Direct HTTP (Synchronous)**

```kotlin
// device-registry-service calls feed-ingestion-service
@Service
class GetDeviceFeedsUseCase(
    private val feedIngestionClient: FeedIngestionClient
) {
    suspend fun execute(deviceId: DeviceId): List<Feed> {
        return feedIngestionClient.getFeeds(deviceId)
    }
}

interface FeedIngestionClient {
    suspend fun getFeeds(deviceId: DeviceId): List<Feed>
}

@Component
class HttpFeedIngestionClient(
    private val webClient: WebClient
) : FeedIngestionClient {
    override suspend fun getFeeds(deviceId: DeviceId): List<Feed> {
        return webClient.get()
            .uri("http://feed-ingestion-service/feeds?deviceId=${deviceId.value}")
            .retrieve()
            .awaitBody()
    }
}
```

**Option 2: Domain Events (Asynchronous, Preferred)**

```kotlin
// device-registry-service publishes event
@Service
class RegisterDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val eventPublisher: EventPublisher
) {
    suspend fun execute(cmd: RegisterDeviceCommand): Device {
        val device = Device.register(...)
        deviceRepository.save(device)
        
        // Publish to internal event bus
        eventPublisher.publish(DeviceRegisteredEvent(device.deviceId))
        
        return device
    }
}

// feed-ingestion-service consumes event
@Component
class DeviceRegisteredHandler(
    private val createDefaultFeedsUseCase: CreateDefaultFeedsUseCase
) {
    @EventListener
    suspend fun handle(event: DeviceRegisteredEvent) {
        // Create default feeds for new device
        createDefaultFeedsUseCase.execute(event.deviceId)
    }
}
```

### Across Bounded Contexts

**Always use Anti-Corruption Layer:**

```kotlin
// Device Context → User Context (via ACL)

// 1. User Context publishes event
@Service
class UserService(private val eventPublisher: EventPublisher) {
    fun createUser(cmd: CreateUserCommand): User {
        val user = User.create(...)
        eventPublisher.publishToKafka(
            topic = "user.events",
            event = UserCreatedEvent(user.userId, user.email)
        )
        return user
    }
}

// 2. Device Context consumes via ACL
@Component
class UserCreatedEventHandler(
    private val deviceOwnerRepository: DeviceOwnerRepository
) {
    @KafkaListener(topics = ["user.events"])
    suspend fun handle(event: UserCreatedEvent) {
        // Transform to Device Context's model
        val deviceOwner = DeviceOwner(
            ownerId = OwnerId(event.userId.value),
            email = Email(event.email.value)
        )
        deviceOwnerRepository.save(deviceOwner)
    }
}
```

---

## 7. Evolution Path

### Phase 1: Start Simple (Year 1)

```
4 Bounded Contexts = 4 Microservices

┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│    User     │ │   Device    │ │ Automation  │ │     IAM     │
│ Management  │ │ Management  │ ��   Engine    │ │  Context    │
│  (Service)  │ │  (Service)  │ │  (Service)  │ │  (Service)  │
└─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘

Benefits:
✅ Simple to build
✅ Fast development
✅ Clear boundaries
✅ Easy to deploy
```

### Phase 2: Monitor and Identify Bottlenecks (Year 2)

```
Metrics after 1 year:
- User Management: 50 req/sec ✅
- Device Management: 5,000 req/sec ⚠️
  - Device CRUD: 100 req/sec
  - Feed Ingestion: 4,900 req/sec ← BOTTLENECK!
- Automation: 200 req/sec ✅
- IAM: 500 req/sec ✅

Decision: Split Device Management BC
```

### Phase 3: Split High-Traffic BC (Year 3)

```
4 BCs = 6 Microservices

┌─────────────┐ ┌──────────────────────────┐ ┌─────────────┐ ┌─────────────┐
│    User     │ │   Device Context (BC)    │ │ Automation  │ │     IAM     │
│ Management  │ │  ┌────────┐ ┌──────────┐ │ │   Engine    │ │  Context    │
│  (Service)  │ │  │ Device │ │   Feed   │ │ │  (Service)  │ │  (Service)  │
│             │ │  │Registry│ │Ingestion │ │ │             │ │             │
│             │ │  │  (MS)  │ │   (MS)   │ │ │             │ │             │
└─────────────┘ │  └────────┘ └──────────┘ │ └─────────────┘ └─────────────┘
                └──────────────────────────┘

Benefits:
✅ Feed ingestion scales independently (10x instances)
✅ Device registry stays lightweight (1x instance)
✅ Cost optimized
✅ Better SLAs
```

---

## 8. SmartHome Hub Microservices Architecture

### Complete Architecture

```
SmartHome Hub Production Architecture:

┌─────────────────────────────────────────────────────────────┐
│                    API Gateway / BFF                        │
│                  (GraphQL/REST Gateway)                     │
└───────────────────────���┬────────────────────────────────────┘
                         │
         ┌───────────────┼───────────────┬──────────────┐
         │               │               │              │
         ▼               ▼               ▼              ▼
┌────────────┐  ┌────────────────┐  ┌──────────┐  ┌────────┐
│    User    │  │ Device Context │  │Automation│  │  IAM   │
│ Management │  │                │  │  Engine  │  │Context │
│  Service   │  │  ┌──────────┐  │  │ Service  │  │Service │
│            │  │  │  Device  │  │  │          │  │        │
│ - User     │  │  │ Registry │  │  │ - Rules  │  │- Actor │
│ - Profile  │  │  │ Service  │  │  │ - Saga   │  │- Role  │
│            │  │  └──────────┘  │  │          │  │- Perm  │
└────────────┘  │  ┌──────────┐  │  └──────────┘  └────────┘
                │  │   Feed   │  │
┌────────────┐  │  │Ingestion │  │  ┌──────────┐
│   Premises │  │  │ Service  │  │  │  Zones   │
│   Service  │  │  └──────────┘  │  │ Service  │
│            │  │  ┌──────────┐  │  │          │
│ - Premises │  │  │  Health  │  │  │ - Zone   │
│ - Address  │  │  │ Monitor  │  │  │ - Widget │
└────────────┘  │  │ Service  │  │  └──────────┘
                │  └──────────┘  │
                └────────────────┘

Data Stores:
┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
│ MongoDB │ │ MongoDB │ │InfluxDB │ │ MongoDB │ │ MongoDB │
│  users  │ │ devices │ │  feeds  │ │  rules  │ │   iam   │
└─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘

Event Bus:
┌─────────────────────────────────────────────────────────────┐
│                        Apache Kafka                         │
│  Topics: user.events, device.events, automation.events      │
└─────────────────────────────────────────────────────────────┘
```

### Service Specifications

| Service | BC | Technology | Database | Scaling | SLA |
|---------|----|-----------|---------|---------| -----|
| User Management | User | Spring WebFlux | MongoDB | 2x | 99.9% |
| Device Registry | Device | Spring WebFlux | MongoDB | 3x | 99.9% |
| Feed Ingestion | Device | Kafka Streams | InfluxDB | 10x | 99.99% |
| Health Monitor | Device | Spring Batch | MongoDB | 3x | 99.99% |
| Automation Engine | Automation | Spring WebFlux | MongoDB | 5x | 99.95% |
| IAM Service | IAM | Spring Security | MongoDB | 5x | 99.99% |
| Premises Service | Premises | Spring WebFlux | MongoDB | 2x | 99.9% |
| Zones Service | Zones | Spring WebFlux | MongoDB | 2x | 99.9% |

### Deployment Configuration

```yaml
# docker-compose.yml (simplified)
version: '3.8'

services:
  # 1 BC = 1 MS (simple services)
  user-service:
    image: nexora/user-management:1.0.0
    replicas: 2
    environment:
      - SPRING_DATA_MONGODB_URI=mongodb://mongo/users
      - KAFKA_BOOTSTRAP_SERVERS=kafka:9092
    ports:
      - "8081:8080"

  # 1 BC = 3 MS (Device Context split)
  device-registry:
    image: nexora/device-registry:1.0.0
    replicas: 3
    environment:
      - SPRING_DATA_MONGODB_URI=mongodb://mongo/devices
    ports:
      - "8082:8080"

  feed-ingestion:
    image: nexora/feed-ingestion:1.0.0
    replicas: 10  # High throughput
    environment:
      - INFLUXDB_URL=http://influxdb:8086
      - KAFKA_BOOTSTRAP_SERVERS=kafka:9092
    ports:
      - "8083:8080"

  health-monitor:
    image: nexora/health-monitor:1.0.0
    replicas: 3
    environment:
      - DEVICE_REGISTRY_URL=http://device-registry:8080
      - FEED_SERVICE_URL=http://feed-ingestion:8080
    ports:
      - "8084:8080"

  automation-service:
    image: nexora/automation-engine:1.0.0
    replicas: 5
    ports:
      - "8085:8080"

  iam-service:
    image: nexora/iam-service:1.0.0
    replicas: 5
    ports:
      - "8086:8080"

  # Infrastructure
  mongodb:
    image: mongo:6.0
    volumes:
      - mongo-data:/data/db

  influxdb:
    image: influxdb:2.7
    volumes:
      - influx-data:/var/lib/influxdb2

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    environment:
      - KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181
```

---

## 9. Chapter Summary

In this chapter, we explored the critical decision of mapping bounded contexts to microservices. We learned that both 1 BC = 1 MS and 1 BC = Multiple MS are valid approaches, depending on your requirements.

### What We Covered

**The Core Question:**
- Bounded Context ≠ Microservice
- BC is logical boundary, MS is physical boundary
- Both mapping strategies are valid

**Option 1: 1 BC = 1 Microservice (Simple)**
- Standard approach for most systems
- Clear boundaries, fast development
- Use when: team < 20, traffic < 10K req/sec
- SmartHome Hub Phase 1: 4 BCs = 4 MS

**Option 2: 1 BC = Multiple Microservices (Advanced)**
- Split when scaling/SLA requirements differ significantly
- More complex but optimized for scale
- Use when: 10x+ scaling difference, different SLAs
- SmartHome Hub Phase 3: 4 BCs = 6 MS (Device BC split)

**When to Split a BC:**
- 10x+ difference in request volume
- Different SLAs (99.9% vs 99.99%)
- Different data patterns (OLTP vs OLAP)
- Different technologies needed
- Large teams (20+ people per BC)

**Shared Code Strategy:**
- Level 1: Service-specific (not shared)
- Level 2: Within BC (context-shared module)
- Level 3: Across contexts (common module)
- Never share domain logic or entities across contexts

**Communication Patterns:**
- Within BC: Direct HTTP or events (prefer events)
- Across BCs: Always use Anti-Corruption Layer
- Kafka for cross-context events

**Evolution Path:**
- Start simple: 1 BC = 1 MS
- Monitor metrics: identify bottlenecks
- Split when justified: 1 BC = Multiple MS
- Continuous optimization

### Key Insights

1. **Start simple, evolve based on evidence** - Don't prematurely split

2. **Metrics drive decisions** - 10x difference justifies split

3. **Shared code has three levels** - Service, BC, cross-context

4. **Communication strategy matters** - Events within BC, ACL across BCs

5. **SmartHome Hub evolution** - 4 BCs → 4 MS (Year 1) → 6 MS (Year 3)

### Decision Matrix

```
Should I split this Bounded Context?

Traffic difference > 10x?        YES → Consider split
SLA requirements differ?         YES → Consider split
Data patterns different?         YES → Consider split
Technologies needed differ?      YES → Consider split
Team size > 20 people?          YES → Consider split

All NO? → Keep as 1 BC = 1 MS ✅
3+ YES? → Split to 1 BC = Multiple MS ✅
```

### Design Checklist

When designing microservices from bounded contexts:
- ✅ Start with 1 BC = 1 MS (simple)
- ✅ Monitor metrics (traffic, latency, errors)
- ✅ Identify 10x+ scaling differences
- ✅ Create context-shared module for split BCs
- ✅ Use common module for primitives
- ✅ Never share domain logic across contexts
- ✅ Use ACL for cross-context communication
- ✅ Document service boundaries clearly
- ✅ Plan evolution path (simple → optimized)
- ✅ Measure improvement after split

### Additional Reading

For deeper understanding:
- **"Building Microservices"** by Sam Newman (2021) - Comprehensive guide
- **"Monolith to Microservices"** by Sam Newman (2019) - Migration strategies
- **"Domain-Driven Design Distilled"** by Vaughn Vernon (2016) - BC to MS mapping

---

## What's Next

In **Chapter 22** (Appendix), we'll provide quick reference guides, complete case studies, and a glossary of all DDD terms.

The journey through DDD is complete. You now have the knowledge to build maintainable, scalable systems from bounded contexts to microservices.

Turn the page for the comprehensive appendices...

