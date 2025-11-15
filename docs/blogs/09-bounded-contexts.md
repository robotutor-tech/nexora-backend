# Chapter 9: Bounded Contexts - The Key to Microservices Success

**Series:** Building an Enterprise IoT Platform with DDD  
**Part:** 3 of 20 - Strategic Patterns  
**Reading Time:** 26 minutes  
**Level:** Advanced  

---

## 📋 Table of Contents

1. [The Problem: The Big Ball of Mud](#the-problem)
2. [What is a Bounded Context?](#what-is-bounded-context)
3. [Identifying Bounded Contexts](#identifying-contexts)
4. [Context Mapping Patterns](#context-mapping)
5. [Real-World Contexts from SmartHome Hub](#real-contexts)
6. [Integration Strategies](#integration-strategies)
7. [Common Pitfalls](#pitfalls)
8. [Migration Strategy](#migration-strategy)
9. [Testing Bounded Contexts](#testing)

---

## <a name="the-problem"></a>1. The Problem: The Big Ball of Mud

### The Scenario: The Monolith That Ate Itself

You're reviewing SmartHome Hub when you discover a nightmare:

> **Architecture Review Finding:** "All modules are tightly coupled. A change in device management breaks authentication. User model is used in 47 different files across the codebase. Impossible to scale or split into microservices."

You investigate and find chaos:

```kotlin
// Shared "User" entity used EVERYWHERE ❌
// user/domain/User.kt
@Document("users")
data class User(
    @Id val id: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    
    // Authentication context needs these
    val refreshToken: String?,
    val lastLoginAt: Instant?,
    val failedLoginAttempts: Int,
    
    // Device context needs these
    val premisesIds: List<String>,
    val favoriteDeviceIds: List<String>,
    
    // Billing context needs these
    val subscriptionTier: String,
    val billingAddress: String?,
    val paymentMethodId: String?,
    
    // Notification context needs these
    val emailNotificationsEnabled: Boolean,
    val smsNotificationsEnabled: Boolean,
    val notificationPreferences: Map<String, Boolean>,
    
    // Analytics context needs these
    val lastSeenAt: Instant?,
    val deviceUsageStats: Map<String, Int>,
    
    // Support context needs these
    val supportTicketIds: List<String>,
    val supportTier: String,
    
    // And more... 50+ fields! 💥
)

// Device entity references User entity directly ❌
data class Device(
    val id: String,
    val name: String,
    val owner: User,  // Direct reference! ❌
    val sharedWith: List<User>  // More tight coupling! ❌
)

// Authentication service depends on device logic ❌
@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val deviceRepository: DeviceRepository  // Why?! ❌
) {
    fun login(email: String, password: String): AuthToken {
        val user = userRepository.findByEmail(email)
        
        // Why is authentication checking devices?! ❌
        val devices = deviceRepository.findByUserId(user.id)
        if (devices.isEmpty()) {
            throw NoDevicesException("User must have devices to login")
        }
        
        // ...
    }
}

// DeviceService depends on billing logic ❌
@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val subscriptionService: SubscriptionService  // Cross-context! ❌
) {
    fun registerDevice(userId: String, device: Device) {
        // Checking subscription in device service?! ❌
        val subscription = subscriptionService.getSubscription(userId)
        if (subscription.tier == "FREE" && getDeviceCount(userId) >= 10) {
            throw SubscriptionLimitException()
        }
        // ...
    }
}
```

### The Disaster Unfolds

**Attempt 1: Split into microservices (failed)**

```kotlin
// Tried to split into services...
// user-service/
//   - Needs 50+ fields for User
//   - Can't deploy independently (too many dependencies)
//   - Database still shared
//   - Every service still calls every other service

// Result: Distributed monolith! 💥
```

**Attempt 2: Add features (disaster)**

```
New Feature: Add automation rules

Problem: Where does it go?
- DeviceService already has 3000 lines
- UserService can't handle more logic
- Need to touch 8 different services
- 3 weeks to implement a 2-day feature!
```

### The Real Cost

**Measured impact:**
- 🔴 Deploy time: 45 minutes (can't deploy independently)
- 🔴 Test time: 2 hours (everything depends on everything)
- 🔴 Feature velocity: 3x slower
- 🔴 Bug rate: 5x higher (changes break unrelated features)
- 🔴 Team scalability: Impossible (everyone blocks everyone)
- 🔴 Onboarding: 6 weeks (too complex to understand)

**Root Cause:** No bounded contexts. Everything is one big tangled mess.

---

## <a name="what-is-bounded-context"></a>2. What is a Bounded Context?

### Definition

> **Bounded Context:** A boundary within which a particular domain model is defined and applicable. Each bounded context has its own ubiquitous language and can have different representations of the same concept.
> 
> — Eric Evans, Domain-Driven Design

### Core Concepts

#### 1. Different Models in Different Contexts

The same concept can mean different things in different contexts:

```kotlin
// In Authentication Context:
// User = Someone who can login
data class User(
    val userId: UserId,
    val email: Email,
    val passwordHash: String,
    val isActive: Boolean,
    val lastLoginAt: Instant?
)

// In Device Management Context:
// Actor = Someone who can manage devices
data class Actor(
    val actorId: ActorId,
    val name: Name,
    val role: ActorRole,
    val premisesIds: List<PremisesId>
)

// In Billing Context:
// Customer = Someone who pays
data class Customer(
    val customerId: CustomerId,
    val billingEmail: Email,
    val subscriptionTier: SubscriptionTier,
    val paymentMethod: PaymentMethod
)

// Same person, different models! ✅
// Each context sees them differently
```

#### 2. Autonomous Teams

Each context can be owned by a different team:

```
┌─────────────────────────────────────────────────────────┐
│         SmartHome Hub Platform                          │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Authentication Context                                 │
│  - Team: Security Team                                  │
│  - Model: User (for login)                              │
│  - DB: auth_db                                          │
│  - Can deploy independently                             │
│                                                         │
│  ┌───────────────────────────────────────────────────┐ │
│  │                                                     │ │
│  │  Device Management Context                          │ │
│  │  - Team: IoT Team                                   │ │
│  │  - Model: Actor, Device, Feed                       │ │
│  │  - DB: device_db                                    │ │
│  │  - Can deploy independently                         │ │
│  │                                                     │ │
│  └───────────────────────────────────────────────────┘ │
│                                                         │
│  Billing Context                                        │
│  - Team: Finance Team                                   │
│  - Model: Customer, Subscription                        │
│  - DB: billing_db                                       │
│  - Can deploy independently                             │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

#### 3. Clear Boundaries

Each context has explicit boundaries:

```kotlin
// ✅ Inside Device Management Context
package com.smarthomehub.device.domain

class Device(
    val deviceId: DeviceId,
    val actorId: ActorId,  // Reference by ID
    // ... device-specific logic
)

// ✅ Inside Authentication Context
package com.smarthomehub.auth.domain

class User(
    val userId: UserId,
    // ... authentication-specific logic
)

// ❌ NEVER do this:
class Device(
    val deviceId: DeviceId,
    val user: User  // Cross-context dependency! ❌
)
```

#### 4. Different Databases (Optional)

Each context can have its own database:

```
Authentication Context → auth_database
    └── users (userId, email, passwordHash, ...)

Device Management Context → device_database
    └── devices (deviceId, actorId, ...)
    └── actors (actorId, name, userId, ...)  // Denormalized

Billing Context → billing_database
    └── customers (customerId, userId, ...)
    └── subscriptions (...)
```

---

## <a name="identifying-contexts"></a>3. Identifying Bounded Contexts

### Strategy 1: Follow the Language

Different teams use different terms:

```
Authentication Team talks about:
- "Users", "Login", "Password", "Session", "Token"

Device Team talks about:
- "Actors", "Devices", "Feeds", "Premises", "Zones"

Billing Team talks about:
- "Customers", "Subscriptions", "Invoices", "Payments"

→ These are different contexts! ✅
```

### Strategy 2: Look for Different Meanings

When the same word means different things:

```kotlin
// "User" means different things:

// In Authentication:
// User = Someone who can authenticate
data class User(
    val userId: UserId,
    val credentials: Credentials
)

// In Device Management:
// User is not relevant, we have "Actor"
data class Actor(
    val actorId: ActorId,
    val userId: UserId,  // Reference
    val role: ActorRole
)

// In Analytics:
// User = Source of events
data class UserProfile(
    val profileId: ProfileId,
    val userId: UserId,  // Reference
    val lastActiveAt: Instant
)

// Different meanings → Different contexts ✅
```

### Strategy 3: Identify Organizational Boundaries

Follow team structure:

```
Security Team → Authentication Context
IoT Team → Device Management Context
Finance Team → Billing Context
Data Team → Analytics Context
```

### Strategy 4: Look for Independent Change Rates

Features that change together belong in the same context:

```
Change Frequency Analysis:

Authentication features:
- Login flow changed: 2 times/year
- Password policy changed: 1 time/year
→ Stable, slow-changing

Device features:
- New device types: 10 times/year
- Feed management: 20 times/year
→ Fast-changing, volatile

→ Different contexts! ✅
```

### Strategy 5: Event Storming

Map out domain events:

```
Event Storm Results:

Authentication Context emits:
- UserRegistered
- UserLoggedIn
- UserLoggedOut
- PasswordChanged

Device Context emits:
- DeviceRegistered
- DeviceActivated
- FeedValueUpdated
- DeviceHealthChanged

Billing Context emits:
- SubscriptionCreated
- PaymentProcessed
- InvoiceGenerated

→ Clear context boundaries! ✅
```

---

## <a name="context-mapping"></a>4. Context Mapping Patterns

### Pattern 1: Customer-Supplier

One context depends on another:

```
┌─────────────────────┐
│ Authentication      │  Upstream (Supplier)
│ Context             │  - Defines the contract
│                     │  - Controls changes
└──────────┬──────────┘
           │
           │ Uses API
           │
           ▼
┌─────────────────────┐
│ Device Management   │  Downstream (Customer)
│ Context             │  - Consumes the API
│                     │  - Adapts to changes
└─────────────────────┘
```

**Example:**

```kotlin
// Authentication Context (Upstream)
// Provides API for other contexts
interface AuthenticationApi {
    fun validateToken(token: String): TokenValidationResult
    fun getUserInfo(userId: UserId): UserInfo
}

data class UserInfo(
    val userId: UserId,
    val email: Email,
    val isActive: Boolean
)

// Device Context (Downstream)
// Uses authentication API
@Service
class DeviceAuthorizationService(
    private val authenticationApi: AuthenticationApi  // Depends on upstream
) {
    fun authorize(token: String, deviceId: DeviceId): Boolean {
        val validation = authenticationApi.validateToken(token)
        if (!validation.isValid) return false
        
        // Use validated user info
        val userInfo = authenticationApi.getUserInfo(validation.userId)
        return canAccessDevice(userInfo.userId, deviceId)
    }
}
```

### Pattern 2: Shared Kernel

Two contexts share a common subset:

```
┌─────────────────────────────────────────┐
│         Shared Kernel                   │
│   - Common value objects                │
│   - Shared domain concepts              │
│   - Must be agreed upon by both teams   │
└──────────┬────────────────────┬─────────┘
           │                    │
           ▼                    ▼
┌──────────────────┐  ┌──────────────────┐
│ Device Context   │  │ Automation       │
│                  │  │ Context          │
└──────────────────┘  └──────────────────┘
```

**Example:**

```kotlin
// Shared Kernel (shared by Device and Automation contexts)
package com.smarthomehub.shared.domain

@JvmInline
value class DeviceId(val value: String)

@JvmInline
value class PremisesId(val value: String)

enum class DeviceType {
    SENSOR, ACTUATOR
}

// Both Device and Automation contexts use these
// Changes require agreement from both teams
```

### Pattern 3: Anti-Corruption Layer (ACL)

Protect your context from external complexity:

```
┌─────────────────────┐
│ External System     │
│ (Legacy, complex)   │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Anti-Corruption     │  Translates external model
│ Layer (ACL)         │  to internal model
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Your Clean Context  │  Protected from external
│                     │  complexity
└─────────────────────┘
```

**Example:**

```kotlin
// External legacy system
interface LegacyDeviceApi {
    fun getDeviceData(deviceSerialNo: String): LegacyDeviceData
}

data class LegacyDeviceData(
    val sno: String,
    val devName: String,
    val stat: Int,  // 0=off, 1=on
    val loc: String?,
    // ... 50 more cryptic fields
)

// Anti-Corruption Layer
@Component
class LegacyDeviceAdapter(
    private val legacyApi: LegacyDeviceApi
) {
    fun getDevice(serialNumber: SerialNumber): Device? {
        val legacyData = legacyApi.getDeviceData(serialNumber.value)
        
        // Translate to our clean domain model
        return Device(
            deviceId = DeviceId(legacyData.sno),
            name = Name(legacyData.devName),
            status = translateStatus(legacyData.stat),
            // ... clean mapping
        )
    }
    
    private fun translateStatus(legacyStat: Int): DeviceStatus {
        return when (legacyStat) {
            0 -> DeviceStatus.INACTIVE
            1 -> DeviceStatus.ACTIVE
            else -> DeviceStatus.UNKNOWN
        }
    }
}

// Our domain stays clean! ✅
```

### Pattern 4: Published Language

Define a common integration language:

```
┌─────────────────────────────────────────────────────┐
│         Published Language (Events)                 │
│                                                     │
│   DeviceRegistered {                                │
│     deviceId: string                                │
│     premisesId: string                              │
│     timestamp: ISO8601                              │
│   }                                                 │
│                                                     │
└──────────┬──────────────────────────────┬──────────┘
           │                              │
           ▼                              ▼
┌──────────────────┐          ┌──────────────────┐
│ Device Context   │          │ Analytics        │
│                  │          │ Context          │
└──────────────────┘          └──────────────────┘
```

**Example:**

```kotlin
// Published event schema (in shared module)
package com.smarthomehub.events

data class DeviceRegisteredEvent(
    val eventId: String,
    val deviceId: String,
    val premisesId: String,
    val deviceType: String,
    val timestamp: Instant,
    val version: Int = 1
)

// Device Context publishes
@Service
class DeviceEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, DeviceRegisteredEvent>
) {
    fun publishDeviceRegistered(device: Device) {
        val event = DeviceRegisteredEvent(
            eventId = UUID.randomUUID().toString(),
            deviceId = device.deviceId.value,
            premisesId = device.premisesId.value,
            deviceType = device.type.name,
            timestamp = Instant.now()
        )
        
        kafkaTemplate.send("device-events", event)
    }
}

// Analytics Context consumes
@Component
class DeviceRegisteredEventHandler {
    @KafkaListener(topics = ["device-events"])
    fun handle(event: DeviceRegisteredEvent) {
        // Process in analytics context
    }
}
```

### Pattern 5: Separate Ways

Contexts with no relationship:

```
┌──────────────────┐          ┌──────────────────┐
│ Device Context   │          │ Marketing        │
│                  │  No      │ Context          │
│                  │  Link    │                  │
└──────────────────┘          └──────────────────┘

// Completely independent
// No shared code, no events, no API calls
```

---

## <a name="real-contexts"></a>5. Real-World Contexts from SmartHome Hub

### Context 1: Authentication & Authorization

```kotlin
// auth/domain/model/User.kt
package com.smarthomehub.auth.domain.model

data class User(
    val userId: UserId,
    val email: Email,
    val passwordHash: PasswordHash,
    val status: UserStatus,
    val roles: Set<Role>,
    val createdAt: Instant,
    val lastLoginAt: Instant?
) {
    fun authenticate(password: String, cryptoService: CryptographyService): Boolean {
        return cryptoService.verify(password, passwordHash)
    }
    
    fun hasRole(role: Role): Boolean = roles.contains(role)
    
    fun activate(): User = copy(status = UserStatus.ACTIVE)
    fun suspend(): User = copy(status = UserStatus.SUSPENDED)
}

enum class UserStatus {
    PENDING, ACTIVE, SUSPENDED, DELETED
}

// auth/application/LoginUseCase.kt
@Service
class LoginUseCase(
    private val userRepository: UserRepository,
    private val cryptographyService: CryptographyService,
    private val tokenService: TokenService
) {
    fun execute(command: LoginCommand): LoginResult {
        val user = userRepository.findByEmail(command.email)
            ?: return LoginResult.Failed("Invalid credentials")
        
        if (!user.authenticate(command.password, cryptographyService)) {
            return LoginResult.Failed("Invalid credentials")
        }
        
        if (user.status != UserStatus.ACTIVE) {
            return LoginResult.Failed("Account is ${user.status}")
        }
        
        val token = tokenService.generateToken(user.userId)
        
        return LoginResult.Success(
            userId = user.userId,
            token = token,
            expiresAt = Instant.now().plus(Duration.ofHours(24))
        )
    }
}
```

### Context 2: Device Management

```kotlin
// device/domain/model/Device.kt
package com.smarthomehub.device.domain.model

class Device(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    private val name: Name,
    val serialNumber: SerialNumber,
    private val type: DeviceType,
    private val status: DeviceStatus,
    private val feeds: Map<FeedId, Feed>,
    val createdBy: ActorId,  // Reference to auth context
    val createdAt: Instant
) {
    // Device-specific business logic
    fun activate(): Device {
        require(status == DeviceStatus.PENDING) {
            "Can only activate pending devices"
        }
        require(feeds.isNotEmpty()) {
            "Device must have at least one feed"
        }
        return copy(status = DeviceStatus.ACTIVE)
    }
    
    fun addFeed(feed: Feed): Device {
        require(feeds.size < MAX_FEEDS) {
            "Maximum $MAX_FEEDS feeds allowed"
        }
        return copy(feeds = feeds + (feed.feedId to feed))
    }
}

// device/domain/model/Actor.kt
// Actor is Device context's representation of a User
data class Actor(
    val actorId: ActorId,
    val userId: UserId,  // Reference to auth context
    val name: Name,
    val role: ActorRole,
    val premisesIds: List<PremisesId>
) {
    fun hasAccessTo(premisesId: PremisesId): Boolean {
        return premisesIds.contains(premisesId)
    }
    
    fun canManageDevices(): Boolean {
        return role in listOf(ActorRole.OWNER, ActorRole.ADMIN)
    }
}
```

### Context 3: Automation Rules

```kotlin
// automation/domain/model/Automation.kt
package com.smarthomehub.automation.domain.model

class Automation(
    val automationId: AutomationId,
    val premisesId: PremisesId,
    private val name: Name,
    private val triggers: List<Trigger>,
    private val conditions: List<Condition>,
    private val actions: List<Action>,
    private val isActive: Boolean,
    val createdBy: ActorId
) {
    fun shouldExecute(event: DeviceEvent): Boolean {
        if (!isActive) return false
        
        // Check if event matches triggers
        val triggerMatches = triggers.any { it.matches(event) }
        if (!triggerMatches) return false
        
        // Check if all conditions are met
        return conditions.all { it.evaluate(event) }
    }
    
    fun getActionsToExecute(): List<Action> = actions
    
    fun activate(): Automation = copy(isActive = true)
    fun deactivate(): Automation = copy(isActive = false)
}

sealed class Trigger {
    abstract fun matches(event: DeviceEvent): Boolean
    
    data class DeviceValueChanged(
        val deviceId: DeviceId,
        val feedId: FeedId,
        val operator: ComparisonOperator,
        val threshold: Int
    ) : Trigger() {
        override fun matches(event: DeviceEvent): Boolean {
            if (event !is DeviceEvent.FeedValueChanged) return false
            if (event.deviceId != deviceId) return false
            if (event.feedId != feedId) return false
            
            return when (operator) {
                ComparisonOperator.GREATER_THAN -> event.newValue > threshold
                ComparisonOperator.LESS_THAN -> event.newValue < threshold
                ComparisonOperator.EQUALS -> event.newValue == threshold
            }
        }
    }
}

sealed class Action {
    data class SetDeviceValue(
        val deviceId: DeviceId,
        val feedId: FeedId,
        val value: Int
    ) : Action()
    
    data class SendNotification(
        val recipientIds: List<ActorId>,
        val message: String
    ) : Action()
}
```

### Context 4: Billing & Subscription

```kotlin
// billing/domain/model/Customer.kt
package com.smarthomehub.billing.domain.model

class Customer(
    val customerId: CustomerId,
    val userId: UserId,  // Reference to auth context
    private val email: Email,
    private val subscription: Subscription,
    private val paymentMethod: PaymentMethod?,
    val createdAt: Instant
) {
    fun canRegisterDevice(): Boolean {
        return subscription.isActive() && !subscription.hasReachedDeviceLimit()
    }
    
    fun getMaxDevices(): Int = subscription.tier.maxDevices
    
    fun upgradeSubscription(newTier: SubscriptionTier): Customer {
        val newSubscription = subscription.upgradeTo(newTier)
        return copy(subscription = newSubscription)
    }
}

data class Subscription(
    val subscriptionId: SubscriptionId,
    val tier: SubscriptionTier,
    val status: SubscriptionStatus,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val deviceCount: Int
) {
    fun isActive(): Boolean = status == SubscriptionStatus.ACTIVE
    
    fun hasReachedDeviceLimit(): Boolean = deviceCount >= tier.maxDevices
    
    fun upgradeTo(newTier: SubscriptionTier): Subscription {
        require(newTier.level > tier.level) {
            "New tier must be higher than current tier"
        }
        return copy(tier = newTier)
    }
}

enum class SubscriptionTier(val level: Int, val maxDevices: Int) {
    FREE(1, 10),
    BASIC(2, 50),
    PRO(3, 100),
    ENTERPRISE(4, 500)
}
```

---

## <a name="integration-strategies"></a>6. Integration Strategies

### Strategy 1: REST API

Synchronous request-response:

```kotlin
// Authentication Context exposes API
@RestController
@RequestMapping("/api/auth")
class AuthenticationController(
    private val getUserInfoQuery: GetUserInfoQueryHandler
) {
    @GetMapping("/users/{userId}")
    fun getUserInfo(@PathVariable userId: String): UserInfoDto {
        val query = GetUserInfoQuery(UserId(userId))
        val userInfo = getUserInfoQuery.execute(query)
        return UserInfoDto.from(userInfo)
    }
}

// Device Context consumes API
@Component
class AuthenticationApiClient(
    private val restTemplate: RestTemplate,
    @Value("\${auth.service.url}") private val authServiceUrl: String
) {
    fun getUserInfo(userId: UserId): UserInfo? {
        return try {
            val response = restTemplate.getForEntity(
                "$authServiceUrl/api/auth/users/${userId.value}",
                UserInfoDto::class.java
            )
            response.body?.toDomain()
        } catch (e: Exception) {
            logger.error("Failed to get user info", e)
            null
        }
    }
}
```

### Strategy 2: Domain Events (Async)

Eventual consistency through events:

```kotlin
// Device Context publishes event
@Service
class DeviceEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    fun publishDeviceRegistered(device: Device) {
        val event = DeviceRegisteredEventDto(
            eventId = UUID.randomUUID().toString(),
            deviceId = device.deviceId.value,
            premisesId = device.premisesId.value,
            ownerId = device.createdBy.value,
            timestamp = Instant.now()
        )
        
        val json = objectMapper.writeValueAsString(event)
        kafkaTemplate.send("device-events", event.deviceId, json)
    }
}

// Billing Context listens to event
@Component
class DeviceRegisteredEventHandler(
    private val customerRepository: CustomerRepository
) {
    @KafkaListener(topics = ["device-events"], groupId = "billing-service")
    fun handle(@Payload message: String) {
        val event = objectMapper.readValue(message, DeviceRegisteredEventDto::class.java)
        
        // Update device count in billing context
        val customer = customerRepository.findByUserId(UserId(event.ownerId))
        customer?.let {
            val updated = it.incrementDeviceCount()
            customerRepository.save(updated)
        }
    }
}
```

### Strategy 3: Shared Database (Anti-Pattern for Microservices)

Each context has its own database:

```sql
-- Authentication Database
CREATE TABLE users (
    user_id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Device Database
CREATE TABLE actors (
    actor_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,  -- Denormalized from auth
    name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE devices (
    device_id VARCHAR(36) PRIMARY KEY,
    premises_id VARCHAR(36) NOT NULL,
    actor_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    serial_number VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL
);

-- Billing Database
CREATE TABLE customers (
    customer_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,  -- Denormalized from auth
    email VARCHAR(255) NOT NULL,   -- Denormalized from auth
    subscription_tier VARCHAR(20) NOT NULL,
    device_count INT NOT NULL
);
```

### Strategy 4: Data Replication

Keep local copies for performance:

```kotlin
// Device Context keeps local copy of user data
@Document("actors")
data class ActorDocument(
    @Id val actorId: String,
    val userId: String,  // From auth context
    val email: String,   // Replicated from auth
    val name: String,    // Replicated from auth
    val role: String,
    val premisesIds: List<String>,
    val lastSyncedAt: Instant
)

// Sync on user updates
@Component
class UserUpdatedEventHandler(
    private val actorRepository: ActorRepository
) {
    @KafkaListener(topics = ["auth-events"])
    fun handle(event: UserUpdatedEvent) {
        // Update replicated data
        val actors = actorRepository.findByUserId(UserId(event.userId))
        actors.forEach { actor ->
            val updated = actor.copy(
                email = event.email,
                name = event.name,
                lastSyncedAt = Instant.now()
            )
            actorRepository.save(updated)
        }
    }
}
```

---

## <a name="pitfalls"></a>7. Common Pitfalls

### Pitfall 1: Too Many Bounded Contexts

**Problem:**

```
Created 50 bounded contexts:
- UserProfileContext
- UserPreferencesContext
- UserNotificationsContext
- UserAuthenticationContext
- UserRegistrationContext
// ... 45 more

Result: Microservices hell! 💥
```

**Solution:**

```
Start with larger contexts, split later:
- Authentication Context (includes profile, preferences)
- Device Management Context
- Billing Context
- Analytics Context

Rule: Start with 3-7 contexts, not 50!
```

### Pitfall 2: Shared Database

**Problem:**

```kotlin
// All contexts use same database
auth_service → shared_database ← device_service
                    ↑
              billing_service

// Can't deploy independently
// Schema changes break everything
```

**Solution:**

```kotlin
// Each context owns its data
auth_service → auth_database
device_service → device_database
billing_service → billing_database

// Communicate through APIs/events
```

### Pitfall 3: Distributed Monolith

**Problem:**

```kotlin
// Every service calls every other service
auth_service ↔ device_service ↔ billing_service
     ↕              ↕                 ↕
analytics_service ↔ notification_service

// Synchronous calls everywhere
// Can't deploy independently
// Performance nightmare
```

**Solution:**

```kotlin
// Use asynchronous events
auth_service → [Events] → device_service
device_service → [Events] → billing_service
device_service → [Events] → analytics_service

// Loose coupling through events
```

### Pitfall 4: No Anti-Corruption Layer

**Problem:**

```kotlin
// External model leaks into our domain
class Device(
    val externalSystemData: LegacyDeviceData  // ❌
)
```

**Solution:**

```kotlin
// Anti-corruption layer protects domain
class LegacyDeviceAdapter {
    fun toDomain(legacy: LegacyDeviceData): Device {
        // Translate external model to our model
    }
}
```

---

## <a name="migration-strategy"></a>8. Migration Strategy

### Step 1: Identify Contexts

```kotlin
// Current monolith analysis
Monolith (15,000 lines):
- User management (2,000 lines)
- Authentication (1,500 lines)
- Device management (5,000 lines)
- Automation (3,000 lines)
- Billing (1,500 lines)
- Analytics (2,000 lines)

// Identified contexts:
1. Authentication Context
2. Device Management Context
3. Billing Context
```

### Step 2: Define Context Boundaries

```kotlin
// Authentication Context
Entities: User, Token, RefreshToken
APIs: login, logout, validateToken
Events: UserRegistered, UserLoggedIn

// Device Management Context  
Entities: Device, Feed, Actor, Premises
APIs: registerDevice, updateDevice, addFeed
Events: DeviceRegistered, DeviceActivated
```

### Step 3: Extract One Context at a Time

```kotlin
// Phase 1: Extract Authentication (safest)
Week 1-2: Create auth-service module
Week 3-4: Move authentication code
Week 5: Deploy auth-service
Week 6: Switch to auth API

// Phase 2: Extract Device Management
Week 7-8: Create device-service module
Week 9-10: Move device code
Week 11: Deploy device-service
Week 12: Switch to device API

// Phase 3: Extract Billing
// ...
```

### Step 4: Introduce Events

```kotlin
// Before: Direct calls
deviceService.registerDevice(...)
billingService.incrementDeviceCount(...)  // Direct call ❌

// After: Events
deviceService.registerDevice(...)
eventPublisher.publish(DeviceRegisteredEvent(...))

// Billing listens to event
@EventHandler
class DeviceRegisteredHandler {
    fun handle(event: DeviceRegisteredEvent) {
        // Update billing asynchronously ✅
    }
}
```

---

## <a name="testing"></a>9. Testing Bounded Contexts

### Contract Testing

```kotlin
// Authentication Context defines contract
interface AuthenticationApi {
    fun validateToken(token: String): TokenValidationResult
}

// Contract test (runs in auth context CI)
class AuthenticationApiContractTest {
    @Test
    fun `should validate valid token`() {
        val api = AuthenticationApiImpl()
        val token = generateValidToken()
        
        val result = api.validateToken(token)
        
        assertTrue(result.isValid)
        assertNotNull(result.userId)
    }
    
    @Test
    fun `should reject expired token`() {
        val api = AuthenticationApiImpl()
        val token = generateExpiredToken()
        
        val result = api.validateToken(token)
        
        assertFalse(result.isValid)
        assertEquals("Token expired", result.error)
    }
}

// Device Context runs same tests against real API
class AuthenticationApiIntegrationTest {
    @Test
    fun `auth API contract should be maintained`() {
        // Same tests run against real auth service
        val api = AuthenticationApiClient(authServiceUrl)
        
        // Tests verify contract is maintained ✅
    }
}
```

---

## 💡 Key Takeaways

1. **Bounded contexts define clear boundaries** - Each context is independent

2. **Same concept, different models** - User in auth ≠ Actor in device

3. **Contexts can have different databases** - True autonomy

4. **Integration through APIs and events** - Not shared database

5. **Anti-corruption layer protects domain** - External complexity stays out

6. **Start with 3-7 contexts** - Not 50 microservices

7. **Extract one context at a time** - Gradual migration

8. **Context mapping is crucial** - Know the relationships

9. **Test contracts between contexts** - Prevent breaking changes

10. **Context = Team = Microservice** - Clear ownership

---

## 🎯 Practical Exercise

Identify bounded contexts in your system:

1. **List all domain concepts** in your system
2. **Group by team/feature** that owns them
3. **Identify different meanings** of same terms
4. **Draw context map** showing relationships
5. **Pick one context** to extract first
6. **Define API contract** for that context
7. **Add contract tests** to prevent breakage
8. **Extract gradually** over 4-6 weeks

---

## 📚 What We've Covered

In this chapter, you learned:

✅ What bounded contexts are and why they matter  
✅ How to identify context boundaries  
✅ Context mapping patterns (Customer-Supplier, ACL, etc.)  
✅ Real contexts from SmartHome Hub  
✅ Integration strategies (REST, events, replication)  
✅ Common pitfalls (too many contexts, shared DB)  
✅ Migration strategy from monolith  
✅ Testing strategies for contexts  

---

## 🚀 Next Chapter

Ready to protect your domain from external complexity?

👉 **[Chapter 10: Anti-Corruption Layer - Protecting Your Domain](./10-anti-corruption-layer.md)**

**You'll learn:**
- When to use ACL
- ACL implementation patterns
- Adapter pattern
- Translator pattern
- Real ACL examples

**Reading Time:** 20 minutes  
**Difficulty:** Advanced  

---

**Questions?** Open an issue in the repository!

**Found this helpful?** Star and share with your team!

---

*"Explicitly define the context within which a model applies. Keep the model strictly consistent within these bounds."  
— Eric Evans, Domain-Driven Design*

