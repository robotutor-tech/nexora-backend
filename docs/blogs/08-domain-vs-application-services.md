# Chapter 8: Domain Services vs Application Services - Clear Separation

**Series:** Building an Enterprise IoT Platform with DDD  
**Part:** 2 of 20 - Tactical Patterns  
**Reading Time:** 22 minutes  
**Level:** Intermediate  

---

## 📋 Table of Contents

1. [The Problem: Service Layer Confusion](#the-problem)
2. [What Are Services in DDD?](#what-are-services)
3. [Domain Services Explained](#domain-services)
4. [Application Services Explained](#application-services)
5. [Clear Decision Framework](#decision-framework)
6. [Real-World Examples from SmartHome Hub](#real-examples)
7. [Service Anti-Patterns](#anti-patterns)
8. [Orchestration vs Coordination](#orchestration-coordination)
9. [Testing Services](#testing)

---

## <a name="the-problem"></a>1. The Problem: Service Layer Confusion

### The Scenario: The 3000-Line God Service

You're reviewing SmartHome Hub when you find a monstrous service:

> **Code Review Alert:** "DeviceService has grown to 3000+ lines with 50+ methods. Contains validation, business logic, orchestration, integration, and everything else. Impossible to maintain or test."

You open the file and discover chaos:

```kotlin
// DeviceService.kt - Everything mixed together ❌
@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val feedRepository: FeedRepository,
    private val premisesRepository: PremisesRepository,
    private val userRepository: UserRepository,
    private val subscriptionService: SubscriptionService,
    private val notificationService: NotificationService,
    private val emailService: EmailService,
    private val smsService: SMSService,
    private val analyticsService: AnalyticsService,
    private val auditService: AuditService,
    private val deviceRegistrationPolicy: DeviceRegistrationPolicy,
    private val eventPublisher: EventPublisher,
    private val kafkaProducer: KafkaProducer,
    private val restTemplate: RestTemplate
    // ... 10 more dependencies 💥
) {
    
    // Business logic mixed with infrastructure
    fun registerDevice(request: DeviceRegistrationRequest): Device {
        // Validation (should be in domain?)
        if (request.name.isBlank()) {
            throw ValidationException("Name required")
        }
        
        // Business logic (should be in domain?)
        val deviceCount = deviceRepository.countByPremisesId(request.premisesId)
        if (deviceCount >= 100) {
            throw BusinessException("Too many devices")
        }
        
        // Domain logic
        val device = Device(
            id = UUID.randomUUID().toString(),
            name = request.name,
            serialNumber = request.serialNumber,
            status = "PENDING"
        )
        
        // Persistence
        deviceRepository.save(device)
        
        // Integration with external service
        val externalResponse = restTemplate.postForEntity(
            "https://external-api.com/register",
            device,
            String::class.java
        )
        
        // Send notifications
        emailService.sendDeviceRegisteredEmail(device.premisesId, device.name)
        smsService.sendDeviceRegisteredSMS(device.premisesId, device.name)
        
        // Analytics
        analyticsService.trackEvent("device_registered", mapOf(
            "deviceId" to device.id,
            "type" to device.type
        ))
        
        // Audit
        auditService.log("DEVICE_REGISTERED", device.id)
        
        // Kafka event
        kafkaProducer.send("device-events", DeviceRegisteredEvent(device.id))
        
        // Domain event
        eventPublisher.publish(DeviceRegisteredDomainEvent(device.id))
        
        return device
    }
    
    // More methods with same mess...
    fun activateDevice(deviceId: String): Device { /* 100 lines */ }
    fun deactivateDevice(deviceId: String): Device { /* 100 lines */ }
    fun updateDevice(deviceId: String, request: UpdateRequest): Device { /* 150 lines */ }
    fun deleteDevice(deviceId: String) { /* 80 lines */ }
    fun addFeedToDevice(deviceId: String, feed: Feed): Device { /* 120 lines */ }
    fun removeFeedFromDevice(deviceId: String, feedId: String): Device { /* 90 lines */ }
    fun updateDeviceLocation(deviceId: String, location: Location): Device { /* 60 lines */ }
    fun assignDeviceToZone(deviceId: String, zoneId: String): Device { /* 70 lines */ }
    // ... 42 more methods! 💥
}
```

### The Problems Discovered

1. **Massive God Service** - 3000+ lines, 50+ methods, 15+ dependencies
2. **Mixed Concerns** - Business logic + orchestration + integration + infrastructure
3. **No Clear Boundaries** - Can't tell what's domain logic vs application logic
4. **Impossible to Test** - Requires mocking 15+ dependencies
5. **Tight Coupling** - Changes to email service break device registration
6. **No Reusability** - Can't reuse business logic outside this service
7. **Anemic Domain** - All logic in service, entities are just data holders

### The Real Cost

**Impact:**
- 🔴 New feature takes 2 weeks instead of 2 days
- 🔴 Every test needs 15+ mocks
- 🔴 Can't reuse business logic
- 🔴 Bug fixes break other features
- 🔴 New developers overwhelmed

**Root Cause:** No separation between domain services and application services.

---

## <a name="what-are-services"></a>2. What Are Services in DDD?

### Definition

> **Service:** When a significant process or transformation in the domain is not a natural responsibility of an ENTITY or VALUE OBJECT, add an operation to the model as a standalone interface declared as a SERVICE.
> 
> — Eric Evans, Domain-Driven Design

### The Three Types of Services

```
┌─────────────────────────────────────────────────────────────┐
│                     Service Layers                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. Application Services (Use Cases)                        │
│     - Orchestrate workflow                                  │
│     - Transaction boundaries                                │
│     - Call domain services                                  │
│     - Thin layer                                            │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐ │
│  │                                                         │ │
│  │  2. Domain Services                                     │ │
│  │     - Pure business logic                               │ │
│  │     - Stateless operations                              │ │
│  │     - Domain language                                   │ │
│  │     - No infrastructure                                 │ │
│  │                                                         │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
│  3. Infrastructure Services                                 │
│     - Email, SMS, External APIs                             │
│     - File storage, caching                                 │
│     - Technical concerns                                    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Key Differences

| Aspect | Domain Service | Application Service |
|--------|----------------|---------------------|
| **Layer** | Domain | Application |
| **Purpose** | Business logic | Orchestration |
| **Dependencies** | Domain objects only | Domain + Infrastructure |
| **State** | Stateless | Stateless |
| **Transactions** | No | Yes (boundaries) |
| **Reusability** | High (pure logic) | Low (specific workflow) |
| **Testing** | Easy (no mocks) | Harder (needs mocks) |
| **Language** | Domain terms | Use case terms |

---

## <a name="domain-services"></a>3. Domain Services Explained

### When to Use Domain Services

Use a domain service when:

1. **Operation spans multiple aggregates**
2. **Operation doesn't naturally belong to any entity**
3. **Operation is a domain concept** (in ubiquitous language)
4. **Logic needs to be reused** across multiple use cases

### Characteristics of Domain Services

```kotlin
// ✅ Good Domain Service
class DeviceAuthenticationService(
    private val deviceRepository: DeviceRepository,
    private val cryptographyService: CryptographyService  // Domain concept
) {
    /**
     * Authenticate device using serial number and secret
     * This is a DOMAIN CONCEPT - part of ubiquitous language
     */
    fun authenticate(
        serialNumber: SerialNumber,
        secret: String
    ): AuthenticationResult {
        // Load device
        val device = deviceRepository.findBySerialNumber(serialNumber)
            ?: return AuthenticationResult.failed("Device not found")
        
        // Verify device can authenticate
        if (device.getStatus() == DeviceStatus.DELETED) {
            return AuthenticationResult.failed("Device is deleted")
        }
        
        if (device.getStatus() == DeviceStatus.INACTIVE) {
            return AuthenticationResult.failed("Device is inactive")
        }
        
        // Verify secret (domain logic)
        val isValid = cryptographyService.verify(
            secret,
            device.getAuthenticationHash()
        )
        
        if (!isValid) {
            return AuthenticationResult.failed("Invalid credentials")
        }
        
        // Generate token (domain concept)
        val token = cryptographyService.generateToken(device.deviceId)
        
        return AuthenticationResult.success(
            deviceId = device.deviceId,
            token = token,
            expiresAt = Instant.now().plus(Duration.ofHours(24))
        )
    }
}

// Domain concepts - no infrastructure
sealed class AuthenticationResult {
    data class Success(
        val deviceId: DeviceId,
        val token: String,
        val expiresAt: Instant
    ) : AuthenticationResult()
    
    data class Failed(
        val reason: String
    ) : AuthenticationResult()
    
    companion object {
        fun success(deviceId: DeviceId, token: String, expiresAt: Instant) =
            Success(deviceId, token, expiresAt)
        
        fun failed(reason: String) = Failed(reason)
    }
}
```

### Domain Service Example 2: Transfer Between Aggregates

```kotlin
// DeviceTransferService.kt - Domain service
class DeviceTransferService(
    private val deviceRepository: DeviceRepository,
    private val premisesRepository: PremisesRepository
) {
    /**
     * Transfer device between premises
     * Involves TWO aggregates - perfect for domain service
     */
    fun transferDevice(
        deviceId: DeviceId,
        fromPremisesId: PremisesId,
        toPremisesId: PremisesId,
        actorId: ActorId
    ): DeviceTransferResult {
        // Load both aggregates
        val device = deviceRepository.findByPremisesIdAndDeviceId(fromPremisesId, deviceId)
            ?: return DeviceTransferResult.failed("Device not found in source premises")
        
        val fromPremises = premisesRepository.findById(fromPremisesId)
            ?: return DeviceTransferResult.failed("Source premises not found")
        
        val toPremises = premisesRepository.findById(toPremisesId)
            ?: return DeviceTransferResult.failed("Destination premises not found")
        
        // Domain rule: Can only transfer inactive devices
        if (device.getStatus() != DeviceStatus.INACTIVE) {
            return DeviceTransferResult.failed(
                "Device must be inactive to transfer. Current status: ${device.getStatus()}"
            )
        }
        
        // Domain rule: Destination premises must have capacity
        val destinationDeviceCount = deviceRepository.countByPremisesId(toPremisesId)
        val maxDevices = toPremises.getMaxDevices()
        
        if (destinationDeviceCount >= maxDevices) {
            return DeviceTransferResult.failed(
                "Destination premises has reached maximum device limit ($maxDevices)"
            )
        }
        
        // Domain rule: Actor must have permission on both premises
        if (!fromPremises.hasActor(actorId)) {
            return DeviceTransferResult.failed("Actor does not have access to source premises")
        }
        
        if (!toPremises.hasActor(actorId)) {
            return DeviceTransferResult.failed("Actor does not have access to destination premises")
        }
        
        // Execute transfer (domain operation)
        val transferredDevice = device.transferToPremises(toPremisesId, actorId)
        
        return DeviceTransferResult.success(
            device = transferredDevice,
            fromPremisesId = fromPremisesId,
            toPremisesId = toPremisesId
        )
    }
}
```

### Domain Service Example 3: Complex Calculation

```kotlin
// DeviceHealthScoreService.kt - Domain service
class DeviceHealthScoreService {
    /**
     * Calculate device health score based on multiple factors
     * Complex calculation that doesn't belong in Device entity
     */
    fun calculateHealthScore(device: Device): HealthScore {
        var score = 100.0
        
        // Factor 1: Response time
        val avgResponseTime = device.getAverageResponseTime()
        if (avgResponseTime > Duration.ofSeconds(5)) {
            score -= 20.0
        } else if (avgResponseTime > Duration.ofSeconds(2)) {
            score -= 10.0
        }
        
        // Factor 2: Uptime percentage
        val uptime = device.getUptimePercentage()
        if (uptime < 95.0) {
            score -= (95.0 - uptime)
        }
        
        // Factor 3: Error rate
        val errorRate = device.getErrorRate()
        if (errorRate > 0.05) {
            score -= (errorRate - 0.05) * 100
        }
        
        // Factor 4: Last seen
        val hoursSinceLastSeen = device.getHoursSinceLastSeen()
        if (hoursSinceLastSeen > 24) {
            score -= 30.0
        } else if (hoursSinceLastSeen > 12) {
            score -= 15.0
        }
        
        // Factor 5: Feed health
        val unhealthyFeeds = device.getFeeds().count { !it.isHealthy() }
        score -= (unhealthyFeeds * 5.0)
        
        // Ensure score is within bounds
        val finalScore = score.coerceIn(0.0, 100.0)
        
        return HealthScore(
            value = finalScore,
            rating = when {
                finalScore >= 90 -> HealthRating.EXCELLENT
                finalScore >= 75 -> HealthRating.GOOD
                finalScore >= 50 -> HealthRating.FAIR
                finalScore >= 25 -> HealthRating.POOR
                else -> HealthRating.CRITICAL
            },
            factors = mapOf(
                "responseTime" to avgResponseTime.toMillis(),
                "uptime" to uptime,
                "errorRate" to errorRate,
                "hoursSinceLastSeen" to hoursSinceLastSeen,
                "unhealthyFeeds" to unhealthyFeeds
            )
        )
    }
}

data class HealthScore(
    val value: Double,
    val rating: HealthRating,
    val factors: Map<String, Any>
)

enum class HealthRating {
    EXCELLENT, GOOD, FAIR, POOR, CRITICAL
}
```

---

## <a name="application-services"></a>4. Application Services Explained

### Purpose of Application Services

Application services (Use Cases) orchestrate workflows:

1. **Accept commands/queries** from presentation layer
2. **Load aggregates** from repositories
3. **Execute business logic** (call domain methods)
4. **Apply policies** for validation
5. **Save aggregates** back to repositories
6. **Publish events** after successful transaction
7. **Call infrastructure services** (email, SMS, etc.)
8. **Return results** to presentation layer

### Characteristics of Application Services

```kotlin
// ✅ Good Application Service (Use Case)
@Service
class RegisterDeviceUseCase(
    // Domain dependencies
    private val deviceRepository: DeviceRepository,
    private val deviceRegistrationPolicy: DeviceRegistrationPolicy,
    
    // Infrastructure dependencies
    private val eventPublisher: EventPublisher,
    private val unitOfWork: UnitOfWork
) {
    /**
     * Register new device - Use case orchestration
     */
    fun execute(command: RegisterDeviceCommand): Device {
        // 1. Validate with policy
        val policyResult = deviceRegistrationPolicy.evaluate(command)
        if (!policyResult.isValid) {
            throw PolicyViolationException(policyResult.violations)
        }
        
        // 2. Execute in transaction
        val device = unitOfWork.execute {
            // 3. Create domain object (business logic in domain)
            val device = Device.register(
                deviceId = DeviceId.generate(),
                premisesId = command.premisesId,
                name = command.name,
                serialNumber = command.serialNumber,
                type = command.type,
                createdBy = command.actorId
            )
            
            // 4. Save aggregate
            deviceRepository.save(device)
        }
        
        // 5. Publish events (after transaction)
        eventPublisher.publish(device.domainEvents)
        
        // 6. Return result
        return device
    }
}
```

### Application Service is Thin

Notice what's NOT in the application service:

- ❌ Business logic (it's in Device.register())
- ❌ Validation logic (it's in Policy)
- ❌ Invariant checks (they're in Device aggregate)
- ❌ Complex calculations
- ❌ Business rules

**Application service only orchestrates!**

### Application Service Example 2: Multiple Aggregates

```kotlin
// ActivateDeviceUseCase.kt - Application service
@Service
class ActivateDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val deviceActivationPolicy: DeviceActivationPolicy,
    private val premisesRepository: PremisesRepository,
    private val eventPublisher: EventPublisher,
    private val notificationService: NotificationService,
    private val unitOfWork: UnitOfWork
) {
    fun execute(command: ActivateDeviceCommand): Device {
        // 1. Load device
        val device = deviceRepository.findByPremisesIdAndDeviceId(
            command.premisesId,
            command.deviceId
        ) ?: throw DeviceNotFoundException(command.deviceId)
        
        // 2. Validate with policy
        val policyResult = deviceActivationPolicy.evaluate(command)
        if (!policyResult.isValid) {
            throw PolicyViolationException(policyResult.violations)
        }
        
        // 3. Execute in transaction
        val activated = unitOfWork.execute {
            // 4. Business logic in domain
            val activatedDevice = device.activate()
            
            // 5. Save
            deviceRepository.save(activatedDevice)
        }
        
        // 6. Publish events (after transaction)
        eventPublisher.publish(activated.domainEvents)
        
        // 7. Send notifications (infrastructure)
        val premises = premisesRepository.findById(command.premisesId)
        premises?.let {
            notificationService.sendDeviceActivated(
                premisesId = it.premisesId,
                deviceName = activated.getName(),
                recipients = it.getNotificationRecipients()
            )
        }
        
        // 8. Return result
        return activated
    }
}
```

### Application Service Example 3: Query Handler

```kotlin
// GetDeviceDetailsQueryHandler.kt - Application service for queries
@Service
class GetDeviceDetailsQueryHandler(
    private val deviceRepository: DeviceRepository,
    private val deviceHealthScoreService: DeviceHealthScoreService,
    private val actorPermissionPolicy: ActorPermissionPolicy
) {
    fun execute(query: GetDeviceDetailsQuery): DeviceDetails {
        // 1. Check permissions
        val permissionResult = actorPermissionPolicy.evaluate(
            DeviceOperationCommand(
                actorId = query.actorId,
                premisesId = query.premisesId,
                deviceId = query.deviceId
            )
        )
        
        if (!permissionResult.isValid) {
            throw PermissionDeniedException(permissionResult.violations)
        }
        
        // 2. Load device
        val device = deviceRepository.findByPremisesIdAndDeviceId(
            query.premisesId,
            query.deviceId
        ) ?: throw DeviceNotFoundException(query.deviceId)
        
        // 3. Calculate health score (domain service)
        val healthScore = deviceHealthScoreService.calculateHealthScore(device)
        
        // 4. Build DTO (projection)
        return DeviceDetails(
            deviceId = device.deviceId.value,
            name = device.getName().value,
            serialNumber = device.serialNumber.value,
            type = device.getType().name,
            status = device.getStatus().name,
            health = device.getHealth().name,
            healthScore = healthScore,
            feeds = device.getFeeds().map { feed ->
                FeedDetails(
                    feedId = feed.feedId.value,
                    name = feed.name,
                    type = feed.type.name,
                    value = feed.getValue(),
                    lastUpdated = feed.getLastUpdated()
                )
            },
            zoneId = device.getZoneId()?.value,
            createdAt = device.createdAt,
            lastSeenAt = device.getLastSeenAt()
        )
    }
}

// DTOs for presentation layer
data class DeviceDetails(
    val deviceId: String,
    val name: String,
    val serialNumber: String,
    val type: String,
    val status: String,
    val health: String,
    val healthScore: HealthScore,
    val feeds: List<FeedDetails>,
    val zoneId: String?,
    val createdAt: Instant,
    val lastSeenAt: Instant?
)

data class FeedDetails(
    val feedId: String,
    val name: String,
    val type: String,
    val value: Int,
    val lastUpdated: Instant
)
```

---

## <a name="decision-framework"></a>5. Clear Decision Framework

### Decision Tree

```
┌─────────────────────────────────────────────────────────┐
│         Is this operation needed?                       │
└───────────────┬─────────────────────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────────────────────┐
│    Does it naturally belong to an Entity/Value Object?  │
│                                                          │
│    Examples:                                             │
│    - Device.activate()                                   │
│    - Email.isValid()                                     │
│    - Temperature.toCelsius()                             │
└───────────┬──────────────────────────────────────┬──────┘
            │ YES                                   │ NO
            ▼                                       ▼
    ┌───────────────┐                   ┌────────────────────┐
    │ Put it in the │                   │ Is it business     │
    │ Entity/VO     │                   │ logic (domain)?    │
    └───────────────┘                   └─────┬──────────┬───┘
                                              │ YES      │ NO
                                              ▼          ▼
                                    ┌────────────────┐  ┌──────────────────┐
                                    │ Domain Service │  │ Infrastructure   │
                                    │                │  │ Service          │
                                    │ Examples:      │  │                  │
                                    │ - Authenticate │  │ Examples:        │
                                    │ - Transfer     │  │ - Email          │
                                    │ - Calculate    │  │ - SMS            │
                                    └────────────────┘  │ - External API   │
                                                        └──────────────────┘

Is orchestration needed (transaction, events, notifications)?
                    │
                    ▼ YES
            ┌──────────────────┐
            │ Application      │
            │ Service          │
            │ (Use Case)       │
            │                  │
            │ Orchestrates:    │
            │ - Domain logic   │
            │ - Policies       │
            │ - Transactions   │
            │ - Events         │
            │ - Infrastructure │
            └──────────────────┘
```

### Decision Checklist

**Put logic in Entity/Value Object if:**
- ✅ It naturally belongs to that object
- ✅ It only needs data from that object
- ✅ It's an invariant or business rule about that object

**Put logic in Domain Service if:**
- ✅ It involves multiple entities/aggregates
- ✅ It's a domain concept (ubiquitous language)
- ✅ It's pure business logic
- ✅ It needs to be reused across use cases
- ✅ It doesn't naturally belong to any entity

**Put logic in Application Service if:**
- ✅ It's orchestrating a workflow
- ✅ It's a use case from requirements
- ✅ It needs transaction management
- ✅ It needs to publish events
- ✅ It needs to call infrastructure services

---

## <a name="real-examples"></a>6. Real-World Examples from SmartHome Hub

### Example 1: Device Registration

```kotlin
// ❌ BEFORE - Everything in one service
@Service
class DeviceService {
    fun registerDevice(request: DeviceRegistrationRequest): Device {
        // Validation
        if (request.name.isBlank()) throw ValidationException()
        
        // Business rules
        val count = deviceRepository.countByPremisesId(request.premisesId)
        if (count >= 100) throw BusinessException()
        
        // Create device
        val device = Device(...)
        
        // Save
        deviceRepository.save(device)
        
        // Send email
        emailService.send(...)
        
        // Analytics
        analyticsService.track(...)
        
        return device
    }
}

// ✅ AFTER - Clear separation

// 1. Domain Entity - Business logic
class Device {
    companion object {
        fun register(
            deviceId: DeviceId,
            premisesId: PremisesId,
            name: Name,
            serialNumber: SerialNumber,
            type: DeviceType,
            createdBy: ActorId
        ): Device {
            // Invariants checked here
            return Device(
                deviceId = deviceId,
                premisesId = premisesId,
                name = name,
                serialNumber = serialNumber,
                type = type,
                status = DeviceStatus.PENDING,
                // ... rest of initialization
            ).also {
                it.addDomainEvent(
                    DeviceRegisteredEvent(deviceId, premisesId, serialNumber)
                )
            }
        }
    }
}

// 2. Domain Service - NOT NEEDED in this case
// (Registration logic belongs to Device entity)

// 3. Policy - Validation rules
class DeviceRegistrationPolicy(
    private val deviceRepository: DeviceRepository,
    private val subscriptionService: SubscriptionService
) : Policy<RegisterDeviceCommand> {
    override fun evaluate(command: RegisterDeviceCommand): PolicyResult {
        val violations = mutableListOf<String>()
        
        // Check device limit
        val count = deviceRepository.countByPremisesId(command.premisesId)
        val subscription = subscriptionService.getSubscription(command.premisesId)
        val maxDevices = getMaxDevicesForTier(subscription.tier)
        
        if (count >= maxDevices) {
            violations.add("Device limit reached ($maxDevices)")
        }
        
        // Check serial uniqueness
        val existing = deviceRepository.findBySerialNumber(command.serialNumber)
        if (existing != null) {
            violations.add("Serial number already registered")
        }
        
        return if (violations.isEmpty()) {
            PolicyResult.valid()
        } else {
            PolicyResult.invalid(violations)
        }
    }
}

// 4. Application Service - Orchestration
@Service
class RegisterDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val deviceRegistrationPolicy: DeviceRegistrationPolicy,
    private val eventPublisher: EventPublisher,
    private val unitOfWork: UnitOfWork
) {
    fun execute(command: RegisterDeviceCommand): Device {
        // Validate
        val policyResult = deviceRegistrationPolicy.evaluate(command)
        if (!policyResult.isValid) {
            throw PolicyViolationException(policyResult.violations)
        }
        
        // Execute in transaction
        val device = unitOfWork.execute {
            // Domain logic
            val device = Device.register(
                deviceId = DeviceId.generate(),
                premisesId = command.premisesId,
                name = command.name,
                serialNumber = command.serialNumber,
                type = command.type,
                createdBy = command.actorId
            )
            
            // Save
            deviceRepository.save(device)
        }
        
        // Publish events (triggers email, analytics, etc.)
        eventPublisher.publish(device.domainEvents)
        
        return device
    }
}

// 5. Event Handlers - Infrastructure concerns
@EventHandler
class DeviceRegisteredEventHandler(
    private val emailService: EmailService,
    private val analyticsService: AnalyticsService
) {
    fun handle(event: DeviceRegisteredEvent) {
        // Send email
        emailService.sendDeviceRegisteredEmail(event.premisesId, event.deviceId)
        
        // Track analytics
        analyticsService.track("device_registered", mapOf(
            "deviceId" to event.deviceId.value,
            "premisesId" to event.premisesId.value
        ))
    }
}
```

### Example 2: Device Transfer (Needs Domain Service)

```kotlin
// Transfer involves TWO aggregates - needs domain service

// 1. Domain Service - Business logic spanning aggregates
class DeviceTransferService(
    private val deviceRepository: DeviceRepository,
    private val premisesRepository: PremisesRepository
) {
    fun canTransferDevice(
        device: Device,
        fromPremises: Premises,
        toPremises: Premises,
        actor: Actor
    ): TransferValidationResult {
        val violations = mutableListOf<String>()
        
        // Business rule: Device must be inactive
        if (device.getStatus() != DeviceStatus.INACTIVE) {
            violations.add("Device must be inactive to transfer")
        }
        
        // Business rule: Destination has capacity
        val destinationCount = deviceRepository.countByPremisesId(toPremises.premisesId)
        if (destinationCount >= toPremises.getMaxDevices()) {
            violations.add("Destination premises at capacity")
        }
        
        // Business rule: Actor has permission on both
        if (!fromPremises.hasActor(actor.actorId)) {
            violations.add("No access to source premises")
        }
        
        if (!toPremises.hasActor(actor.actorId)) {
            violations.add("No access to destination premises")
        }
        
        return TransferValidationResult(
            isValid = violations.isEmpty(),
            violations = violations
        )
    }
    
    fun transferDevice(
        device: Device,
        toPremisesId: PremisesId,
        transferredBy: ActorId
    ): Device {
        // Domain logic
        return device.transferToPremises(toPremisesId, transferredBy)
    }
}

// 2. Application Service - Orchestration
@Service
class TransferDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val premisesRepository: PremisesRepository,
    private val deviceTransferService: DeviceTransferService,
    private val eventPublisher: EventPublisher,
    private val unitOfWork: UnitOfWork
) {
    fun execute(command: TransferDeviceCommand): Device {
        // Load aggregates
        val device = deviceRepository.findByPremisesIdAndDeviceId(
            command.fromPremisesId,
            command.deviceId
        ) ?: throw DeviceNotFoundException(command.deviceId)
        
        val fromPremises = premisesRepository.findById(command.fromPremisesId)
            ?: throw PremisesNotFoundException(command.fromPremisesId)
        
        val toPremises = premisesRepository.findById(command.toPremisesId)
            ?: throw PremisesNotFoundException(command.toPremisesId)
        
        val actor = actorRepository.findById(command.actorId)
            ?: throw ActorNotFoundException(command.actorId)
        
        // Validate using domain service
        val validation = deviceTransferService.canTransferDevice(
            device, fromPremises, toPremises, actor
        )
        
        if (!validation.isValid) {
            throw TransferValidationException(validation.violations)
        }
        
        // Execute in transaction
        val transferred = unitOfWork.execute {
            // Transfer using domain service
            val transferredDevice = deviceTransferService.transferDevice(
                device,
                command.toPremisesId,
                command.actorId
            )
            
            // Save
            deviceRepository.save(transferredDevice)
        }
        
        // Publish events
        eventPublisher.publish(transferred.domainEvents)
        
        return transferred
    }
}
```

---

## <a name="anti-patterns"></a>7. Service Anti-Patterns

### Anti-Pattern 1: Anemic Domain + Fat Services

**Problem:**

```kotlin
// ❌ Anemic entity
data class Device(
    val id: String,
    var name: String,
    var status: String
    // Just data, no behavior
)

// ❌ Fat service with all logic
@Service
class DeviceService {
    fun activate(deviceId: String): Device {
        val device = deviceRepository.findById(deviceId)
        
        // All business logic here! ❌
        if (device.status == "DELETED") {
            throw Exception("Cannot activate deleted device")
        }
        
        if (device.status == "ACTIVE") {
            throw Exception("Already active")
        }
        
        device.status = "ACTIVE"
        return deviceRepository.save(device)
    }
}
```

**Solution:**

```kotlin
// ✅ Rich entity with behavior
class Device {
    fun activate(): Device {
        // Business logic in entity ✅
        if (status == DeviceStatus.DELETED) {
            throw CannotActivateDeletedDeviceException(deviceId)
        }
        
        if (status == DeviceStatus.ACTIVE) {
            throw DeviceAlreadyActiveException(deviceId)
        }
        
        return copy(status = DeviceStatus.ACTIVE).also {
            it.addDomainEvent(DeviceActivatedEvent(deviceId))
        }
    }
}

// ✅ Thin service for orchestration
@Service
class ActivateDeviceUseCase {
    fun execute(command: ActivateDeviceCommand): Device {
        val device = deviceRepository.findById(command.deviceId)
            ?: throw DeviceNotFoundException(command.deviceId)
        
        val activated = device.activate()  // Business logic in domain ✅
        
        return deviceRepository.save(activated)
    }
}
```

### Anti-Pattern 2: Domain Service with Infrastructure

**Problem:**

```kotlin
// ❌ Domain service depends on infrastructure
class DeviceAuthenticationService(
    private val deviceRepository: DeviceRepository,
    private val kafkaProducer: KafkaProducer,  // Infrastructure! ❌
    private val emailService: EmailService,     // Infrastructure! ❌
    private val redisCache: RedisTemplate       // Infrastructure! ❌
) {
    fun authenticate(serialNumber: SerialNumber, secret: String): AuthResult {
        val device = deviceRepository.findBySerialNumber(serialNumber)
        
        // Business logic mixed with infrastructure ❌
        val cached = redisCache.get("auth:${serialNumber}")
        
        val result = // ... authentication logic
        
        kafkaProducer.send("auth-events", result)
        emailService.send("Login detected")
        
        return result
    }
}
```

**Solution:**

```kotlin
// ✅ Domain service - pure business logic
class DeviceAuthenticationService(
    private val deviceRepository: DeviceRepository,
    private val cryptographyService: CryptographyService  // Domain concept ✅
) {
    fun authenticate(
        serialNumber: SerialNumber,
        secret: String
    ): AuthenticationResult {
        // Pure business logic ✅
        val device = deviceRepository.findBySerialNumber(serialNumber)
            ?: return AuthenticationResult.failed("Device not found")
        
        if (!device.canAuthenticate()) {
            return AuthenticationResult.failed("Device cannot authenticate")
        }
        
        val isValid = cryptographyService.verify(secret, device.getAuthenticationHash())
        
        return if (isValid) {
            AuthenticationResult.success(device.deviceId)
        } else {
            AuthenticationResult.failed("Invalid credentials")
        }
    }
}

// ✅ Application service - handles infrastructure
@Service
class AuthenticateDeviceUseCase(
    private val authenticationService: DeviceAuthenticationService,
    private val eventPublisher: EventPublisher,
    private val cacheService: CacheService  // Infrastructure ✅
) {
    fun execute(command: AuthenticateDeviceCommand): AuthenticationResult {
        // Check cache (infrastructure)
        val cached = cacheService.get<AuthenticationResult>("auth:${command.serialNumber}")
        if (cached != null) return cached
        
        // Domain logic
        val result = authenticationService.authenticate(
            command.serialNumber,
            command.secret
        )
        
        // Cache result (infrastructure)
        if (result is AuthenticationResult.Success) {
            cacheService.put("auth:${command.serialNumber}", result, Duration.ofMinutes(5))
        }
        
        // Publish event (infrastructure)
        eventPublisher.publish(DeviceAuthenticatedEvent(result))
        
        return result
    }
}
```

### Anti-Pattern 3: Use Case Calling Another Use Case

**Problem:**

```kotlin
// ❌ Use case calling another use case
@Service
class RegisterAndActivateDeviceUseCase(
    private val registerDeviceUseCase: RegisterDeviceUseCase,  // Use case! ❌
    private val activateDeviceUseCase: ActivateDeviceUseCase   // Use case! ❌
) {
    fun execute(command: RegisterAndActivateCommand): Device {
        // Calling another use case creates nested transactions ❌
        val device = registerDeviceUseCase.execute(
            RegisterDeviceCommand(...)
        )
        
        return activateDeviceUseCase.execute(
            ActivateDeviceCommand(device.deviceId)
        )
    }
}
```

**Solution:**

```kotlin
// ✅ Use case orchestrates domain directly
@Service
class RegisterAndActivateDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val deviceRegistrationPolicy: DeviceRegistrationPolicy,
    private val eventPublisher: EventPublisher,
    private val unitOfWork: UnitOfWork
) {
    fun execute(command: RegisterAndActivateCommand): Device {
        // Validate
        val policyResult = deviceRegistrationPolicy.evaluate(command.toRegisterCommand())
        if (!policyResult.isValid) {
            throw PolicyViolationException(policyResult.violations)
        }
        
        // Single transaction ✅
        val device = unitOfWork.execute {
            // Register (domain logic)
            val registered = Device.register(...)
            
            // Add feeds (domain logic)
            var deviceWithFeeds = registered
            command.feeds.forEach { feedData ->
                val feed = Feed.create(...)
                deviceWithFeeds = deviceWithFeeds.addFeed(feed)
            }
            
            // Activate (domain logic)
            val activated = deviceWithFeeds.activate()
            
            // Save once
            deviceRepository.save(activated)
        }
        
        // Publish events
        eventPublisher.publish(device.domainEvents)
        
        return device
    }
}
```

### Anti-Pattern 4: Transaction Scripts in Application Service

**Problem:**

```kotlin
// ❌ All business logic in application service (Transaction Script)
@Service
class DeviceService {
    fun processDeviceData(deviceId: String, data: Map<String, Any>) {
        val device = deviceRepository.findById(deviceId)
        
        // 200 lines of business logic here! ❌
        if (data.containsKey("temperature")) {
            val temp = data["temperature"] as Double
            if (temp > 100) {
                device.status = "OVERHEATING"
                // Send alert
                // Update analytics
                // Log to audit
            } else if (temp > 80) {
                device.status = "WARNING"
                // Send warning
            } else {
                device.status = "NORMAL"
            }
        }
        
        // 100 more lines of if/else logic...
        
        deviceRepository.save(device)
    }
}
```

**Solution:**

```kotlin
// ✅ Business logic in domain
class Device {
    fun processTemperatureReading(temperature: Temperature): Device {
        // Business logic in domain ✅
        val newStatus = when {
            temperature.celsius > 100 -> DeviceStatus.OVERHEATING
            temperature.celsius > 80 -> DeviceStatus.WARNING
            else -> DeviceStatus.NORMAL
        }
        
        return copy(status = newStatus).also {
            when (newStatus) {
                DeviceStatus.OVERHEATING -> it.addDomainEvent(
                    DeviceOverheatingDetectedEvent(deviceId, temperature)
                )
                DeviceStatus.WARNING -> it.addDomainEvent(
                    DeviceWarningDetectedEvent(deviceId, temperature)
                )
                else -> {}
            }
        }
    }
}

// ✅ Thin orchestration
@Service
class ProcessDeviceDataUseCase {
    fun execute(command: ProcessDeviceDataCommand): Device {
        val device = deviceRepository.findById(command.deviceId)
            ?: throw DeviceNotFoundException(command.deviceId)
        
        // Domain logic
        val processed = device.processTemperatureReading(command.temperature)
        
        val saved = deviceRepository.save(processed)
        
        // Events trigger alerts, analytics, audit
        eventPublisher.publish(saved.domainEvents)
        
        return saved
    }
}
```

---

## <a name="orchestration-coordination"></a>8. Orchestration vs Coordination

### Orchestration (Application Service)

**Centralized control** - one service coordinates everything:

```kotlin
// Orchestration - Application service controls the flow
@Service
class OrderProcessingUseCase(
    private val inventoryService: InventoryService,
    private val paymentService: PaymentService,
    private val shippingService: ShippingService,
    private val notificationService: NotificationService
) {
    fun execute(command: ProcessOrderCommand): Order {
        // Application service controls each step
        
        // Step 1
        val inventoryReserved = inventoryService.reserve(command.items)
        if (!inventoryReserved) {
            throw InsufficientInventoryException()
        }
        
        // Step 2
        val paymentResult = paymentService.charge(command.payment)
        if (!paymentResult.isSuccess) {
            inventoryService.release(command.items)
            throw PaymentFailedException()
        }
        
        // Step 3
        val shipment = shippingService.createShipment(command.address)
        
        // Step 4
        notificationService.sendOrderConfirmation(command.customerId)
        
        return Order(...)
    }
}
```

**Pros:**
- ✅ Easy to understand (linear flow)
- ✅ Easy to debug
- ✅ Clear transaction boundaries

**Cons:**
- ❌ Tight coupling
- ❌ Hard to change flow
- ❌ Synchronous (slow)

### Choreography (Event-Driven)

**Decentralized** - services react to events:

```kotlin
// Choreography - Event-driven coordination

// 1. Application service triggers initial event
@Service
class PlaceOrderUseCase {
    fun execute(command: PlaceOrderCommand): Order {
        val order = Order.place(...)
        orderRepository.save(order)
        
        // Just publish event
        eventPublisher.publish(OrderPlacedEvent(order.orderId))
        
        return order
    }
}

// 2. Inventory service reacts
@EventHandler
class InventoryEventHandler {
    fun handle(event: OrderPlacedEvent) {
        inventoryService.reserve(event.items)
        eventPublisher.publish(InventoryReservedEvent(event.orderId))
    }
}

// 3. Payment service reacts
@EventHandler
class PaymentEventHandler {
    fun handle(event: InventoryReservedEvent) {
        paymentService.charge(event.orderId)
        eventPublisher.publish(PaymentCompletedEvent(event.orderId))
    }
}

// 4. Shipping service reacts
@EventHandler
class ShippingEventHandler {
    fun handle(event: PaymentCompletedEvent) {
        shippingService.createShipment(event.orderId)
        eventPublisher.publish(ShipmentCreatedEvent(event.orderId))
    }
}
```

**Pros:**
- ✅ Loose coupling
- ✅ Easy to add new behavior
- ✅ Asynchronous (fast)
- ✅ Scalable

**Cons:**
- ❌ Harder to understand (distributed flow)
- ❌ Harder to debug
- ❌ Eventual consistency

### Recommendation

Use **orchestration** for:
- Simple workflows
- Critical transactions
- When you need immediate consistency

Use **choreography** for:
- Complex workflows
- When services should be independent
- When eventual consistency is acceptable

---

## <a name="testing"></a>9. Testing Services

### Testing Domain Services

Easy - no infrastructure dependencies:

```kotlin
class DeviceAuthenticationServiceTest {
    
    private lateinit var deviceRepository: InMemoryDeviceRepository
    private lateinit var cryptographyService: CryptographyService
    private lateinit var authenticationService: DeviceAuthenticationService
    
    @BeforeEach
    fun setup() {
        deviceRepository = InMemoryDeviceRepository()
        cryptographyService = MockCryptographyService()
        authenticationService = DeviceAuthenticationService(
            deviceRepository,
            cryptographyService
        )
    }
    
    @Test
    fun `should authenticate device with valid credentials`() {
        // Given
        val device = Device.register(...)
        deviceRepository.save(device)
        
        every { cryptographyService.verify(any(), any()) } returns true
        
        // When
        val result = authenticationService.authenticate(
            device.serialNumber,
            "valid-secret"
        )
        
        // Then
        assertTrue(result is AuthenticationResult.Success)
    }
    
    @Test
    fun `should reject authentication for deleted device`() {
        // Given
        val device = Device.register(...).delete()
        deviceRepository.save(device)
        
        // When
        val result = authenticationService.authenticate(
            device.serialNumber,
            "any-secret"
        )
        
        // Then
        assertTrue(result is AuthenticationResult.Failed)
        assertEquals("Device cannot authenticate", result.reason)
    }
}
```

### Testing Application Services

Need to mock infrastructure:

```kotlin
@SpringBootTest
class RegisterDeviceUseCaseTest {
    
    @Autowired
    lateinit var useCase: RegisterDeviceUseCase
    
    @MockBean
    lateinit var eventPublisher: EventPublisher
    
    @Autowired
    lateinit var deviceRepository: DeviceRepository
    
    @BeforeEach
    fun cleanup() {
        deviceRepository.clear()
    }
    
    @Test
    fun `should register device successfully`() {
        // Given
        val command = RegisterDeviceCommand(...)
        
        // When
        val device = useCase.execute(command)
        
        // Then
        assertNotNull(device)
        assertEquals(DeviceStatus.PENDING, device.getStatus())
        
        // Verify event published
        verify { eventPublisher.publish(any<DeviceRegisteredEvent>()) }
        
        // Verify saved
        val saved = deviceRepository.findById(device.deviceId)
        assertNotNull(saved)
    }
    
    @Test
    fun `should reject registration when policy fails`() {
        // Given - create 100 devices
        repeat(100) {
            deviceRepository.save(Device.register(...))
        }
        
        val command = RegisterDeviceCommand(...)
        
        // When/Then
        assertThrows<PolicyViolationException> {
            useCase.execute(command)
        }
    }
}
```

---

## 💡 Key Takeaways

1. **Domain services** = Pure business logic, no infrastructure

2. **Application services** = Thin orchestration layer

3. **Keep entities rich** - Most logic belongs in entities

4. **Use domain services** when logic spans multiple aggregates

5. **Application services orchestrate** - They don't contain business logic

6. **One transaction per use case** - Application service defines boundary

7. **Events for infrastructure** - Don't call email/SMS directly

8. **Test domain services easily** - No mocks needed

9. **Avoid service to service calls** - Services coordinate through events

10. **Choose orchestration or choreography** - Based on requirements

---

## 🎯 Practical Exercise

Refactor your services:

1. **Identify fat services** in your codebase
2. **Extract business logic** to entities
3. **Create domain services** for multi-aggregate operations
4. **Thin application services** to just orchestration
5. **Add policies** for validation
6. **Use events** for infrastructure concerns
7. **Test domain services** without mocks
8. **Measure improvement** - lines of code, test coverage

---

## 📚 What We've Covered

In this chapter, you learned:

✅ The service layer confusion problem  
✅ Three types of services in DDD  
✅ When to use domain services  
✅ When to use application services  
✅ Clear decision framework  
✅ Real examples from SmartHome Hub  
✅ 4 service anti-patterns to avoid  
✅ Orchestration vs choreography  
✅ How to test both service types  

---

## 🎊 Tactical Patterns Complete!

You've finished all 8 tactical pattern chapters:

1. ✅ Understanding DDD
2. ✅ Anemic vs Rich Models
3. ✅ Value Objects
4. ✅ Entities & Aggregates
5. ✅ Specification Pattern
6. ✅ Policy Pattern
7. ✅ Repository Pattern
8. ✅ Domain vs Application Services

**Next:** Strategic patterns (Bounded Contexts, Events, Sagas)

---

## 🚀 Next Chapter

Ready for strategic patterns?

👉 **[Chapter 9: Bounded Contexts - The Key to Microservices Success](./09-bounded-contexts.md)**

**You'll learn:**
- What bounded contexts are
- How to identify boundaries
- Context mapping patterns
- Integration strategies
- Real context examples

**Reading Time:** 25 minutes  
**Difficulty:** Advanced  

---

**Questions?** Open an issue in the repository!

**Found this helpful?** Star and share with your team!

---

*"Application services are the use cases, domain services are the domain logic that doesn't fit in entities. Keep them separate, keep them focused."  
— DDD Community*

