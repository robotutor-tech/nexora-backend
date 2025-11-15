# Chapter 20: DDD Best Practices - Lessons from the Field

**Series:** Building an Enterprise IoT Platform with DDD  
**Part:** 5 of 20 - Real-World Implementation  
**Reading Time:** 24 minutes  
**Level:** Intermediate to Advanced  

---

## 📋 Table of Contents

1. [The Journey: What We've Learned](#the-journey)
2. [Common DDD Mistakes](#common-mistakes)
3. [When to Use DDD (and When Not To)](#when-to-use)
4. [Team Collaboration Strategies](#team-collaboration)
5. [Migration Strategies](#migration-strategies)
6. [Lessons from Production](#lessons-from-production)
7. [The DDD Maturity Model](#maturity-model)
8. [Final Thoughts](#final-thoughts)

---

## <a name="the-journey"></a>1. The Journey: What We've Learned

### From Spaghetti to Clean Architecture

**Where we started:**
- Anemic domain models (just data classes)
- Business logic scattered everywhere
- 2000-line God services
- Primitive obsession throughout
- No clear boundaries
- Tight coupling everywhere

**Where we are now:**
- Rich domain models with behavior
- Business logic encapsulated in entities
- Thin application services
- Type-safe value objects
- Clear aggregate boundaries
- Loose coupling via events

### The Transformation

```kotlin
// Before: Anemic, procedural, scattered logic ❌
data class Device(
    var id: String,
    var name: String,
    var status: String
)

@Service
class DeviceService {
    fun activateDevice(deviceId: String) {
        val device = deviceRepo.findById(deviceId)
        
        // Validation scattered
        if (device.status != "PENDING") throw Exception()
        if (device.feeds.isEmpty()) throw Exception()
        
        // State mutation
        device.status = "ACTIVE"
        deviceRepo.save(device)
        
        // Side effects
        notificationService.send(...)
        analyticsService.track(...)
    }
}

// After: Rich, declarative, encapsulated ✅
class Device private constructor(
    val deviceId: DeviceId,
    private var status: DeviceStatus,
    private val feeds: Map<FeedId, Feed>
) {
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
}

@Service
class ActivateDeviceUseCase(
    private val repository: DeviceRepository,
    private val eventPublisher: EventPublisher
) {
    fun execute(command: ActivateDeviceCommand) {
        val device = repository.findById(command.deviceId)
        val activated = device.activate()
        repository.save(activated)
        activated.domainEvents.forEach { eventPublisher.publish(it) }
    }
}
```

---

## <a name="common-mistakes"></a>2. Common DDD Mistakes

### Mistake 1: DDD Everywhere

**The Problem:**
```kotlin
// Using DDD for simple CRUD ❌
class EmailAddress private constructor(val value: String) {
    init {
        require(value.contains("@")) { "Invalid email" }
    }
    
    companion object {
        fun create(value: String): EmailAddress = EmailAddress(value)
    }
}

class EmailAddressRepository {
    fun save(email: EmailAddress)
    fun findById(id: EmailAddressId): EmailAddress
}

// For a simple settings screen that just stores email!
// Massive overkill! ❌
```

**The Fix:**
```kotlin
// Just use simple data class for CRUD ✅
data class UserSettings(
    val userId: String,
    val email: String,
    val notifications: Boolean
)

// Reserve DDD for complex business logic ✅
```

**When NOT to use DDD:**
- Simple CRUD operations
- Data-centric applications
- No complex business rules
- Small, short-lived projects
- Tight deadlines with no complexity

### Mistake 2: Anemic Domain Models (Still!)

**The Problem:**
```kotlin
// Calling it "Domain" doesn't make it DDD ❌
class Device(
    val deviceId: DeviceId,
    var status: DeviceStatus,  // Mutable!
    var feeds: MutableList<Feed>  // Direct access!
) {
    // No behavior!
    // No validation!
    // Just a data holder!
}

@Service
class DeviceService {
    fun activate(device: Device) {
        // All logic in service ❌
        device.status = DeviceStatus.ACTIVE
    }
}
```

**The Fix:**
```kotlin
// Actually rich domain model ✅
class Device private constructor(
    val deviceId: DeviceId,
    private var status: DeviceStatus,
    private val feeds: Map<FeedId, Feed>
) {
    fun activate(): Device {
        // Business logic HERE ✅
        require(status == DeviceStatus.PENDING)
        require(feeds.isNotEmpty())
        return copy(status = DeviceStatus.ACTIVE)
    }
    
    // Controlled access ✅
    fun getStatus(): DeviceStatus = status
    fun getFeeds(): List<Feed> = feeds.values.toList()
}
```

### Mistake 3: Aggregate Boundaries Too Large

**The Problem:**
```kotlin
// God aggregate ❌
class Premises(
    val premisesId: PremisesId,
    private val devices: MutableMap<DeviceId, Device>,  // 100+ devices
    private val zones: MutableMap<ZoneId, Zone>,        // 20+ zones
    private val automations: MutableMap<AutomationId, Automation>,  // 50+ automations
    private val users: MutableMap<UserId, User>,        // 10+ users
    private val history: MutableList<Event>             // 10,000+ events
) {
    // Managing everything! ❌
    // Huge transaction boundary!
    // Terrible performance!
}
```

**The Fix:**
```kotlin
// Separate aggregates ✅
class Premises(
    val premisesId: PremisesId,
    private val name: Name,
    private val address: Address
    // Just premises data ✅
)

class Device(
    val deviceId: DeviceId,
    val premisesId: PremisesId,  // Reference, not embedded
    // Device data only ✅
)

class Zone(
    val zoneId: ZoneId,
    val premisesId: PremisesId,  // Reference
    // Zone data only ✅
)

// Separate aggregates, loose coupling ✅
```

### Mistake 4: Over-Engineering Value Objects

**The Problem:**
```kotlin
// Excessive abstraction ❌
interface Identifier<T> {
    val value: T
}

abstract class StringIdentifier(override val value: String) : Identifier<String> {
    init {
        require(value.isNotBlank())
    }
}

class DeviceId(value: String) : StringIdentifier(value) {
    companion object : IdentifierFactory<DeviceId> {
        override fun create(value: String) = DeviceId(value)
        override fun generate() = DeviceId(UUID.randomUUID().toString())
    }
}

// Way too complex for an ID! ❌
```

**The Fix:**
```kotlin
// Simple value object ✅
@JvmInline
value class DeviceId(val value: String) {
    companion object {
        fun generate() = DeviceId(UUID.randomUUID().toString())
    }
}

// Simple, effective, no over-engineering ✅
```

### Mistake 5: Ignoring Performance

**The Problem:**
```kotlin
// Loading entire aggregate for simple query ❌
@Service
class GetDeviceNameUseCase(
    private val deviceRepository: DeviceRepository
) {
    fun execute(deviceId: DeviceId): String {
        val device = deviceRepository.findById(deviceId)
        // Loaded 50 fields, all feeds, all history ❌
        
        return device.getName().value
        // Used 1 field! ❌
    }
}
```

**The Fix:**
```kotlin
// Use projection ✅
data class DeviceNameProjection(val name: String)

@Service
class GetDeviceNameUseCase(
    private val deviceRepository: DeviceRepository
) {
    fun execute(deviceId: DeviceId): String {
        val projection = deviceRepository.findNameById(deviceId)
        return projection.name
        // Only loaded 1 field! ✅
    }
}
```

---

## <a name="when-to-use"></a>3. When to Use DDD (and When Not To)

### The DDD Decision Matrix

```
┌─────────────────────────────────────────────────────┐
│         Complexity vs Business Value                │
│                                                     │
│  High  │                    │                       │
│  Value │  USE DDD ✅       │  USE DDD ✅           │
│        │  (Core Domain)    │  (Supporting)         │
│        ├───────────────────┼───────────────────────┤
│        │                   │                       │
│  Low   │  SIMPLE CODE ✅   │  OFF-THE-SHELF ✅     │
│  Value │  (Generic)        │  (Commodity)          │
│        │                   │                       │
└─────────────────────────────────────────────────────┘
         Low                High
         Complexity         Complexity
```

### Use DDD When:

✅ **Complex business logic**
- Device lifecycle management
- Automation rule evaluation
- Billing calculations
- Access control policies

✅ **Core domain**
- Differentiates your product
- High business value
- Frequent changes

✅ **Long-term project**
- Will evolve over years
- Team will grow
- Complexity will increase

✅ **Collaboration needed**
- Domain experts available
- Cross-functional teams
- Ubiquitous language valuable

### Don't Use DDD When:

❌ **Simple CRUD**
- User settings
- Configuration storage
- Lookup tables

❌ **Data-centric**
- Reports
- Analytics dashboards
- Data warehousing

❌ **No domain experts**
- Can't build ubiquitous language
- No one to validate rules

❌ **Short-term project**
- Quick prototype
- 3-month deadline
- Won't evolve much

---

## <a name="team-collaboration"></a>4. Team Collaboration Strategies

### Event Storming Sessions

**How to run:**

```
1. Gather the team (developers + domain experts)

2. Start with domain events (orange stickies)
   "DeviceRegistered"
   "DeviceActivated"
   "FeedValueChanged"

3. Add commands that trigger events (blue)
   "RegisterDevice" → DeviceRegistered
   "ActivateDevice" → DeviceActivated

4. Identify aggregates (yellow)
   Device handles:
   - RegisterDevice
   - ActivateDevice
   - AddFeed

5. Draw boundaries (pink tape)
   Device Management context
   Automation context
   Billing context

6. Document decisions
   Capture in domain dictionary
   Update architecture docs
```

### Code Reviews for DDD

**What to look for:**

```kotlin
// ✅ Good: Business logic in domain
class Device {
    fun activate(): Device {
        require(status == DeviceStatus.PENDING)
        require(feeds.isNotEmpty())
        return copy(status = DeviceStatus.ACTIVE)
    }
}

// ❌ Bad: Business logic in service
@Service
class DeviceService {
    fun activate(device: Device) {
        if (device.status != "PENDING") throw Exception()
        device.status = "ACTIVE"
    }
}

// Review checklist:
// - Is business logic in domain? ✓
// - Are invariants protected? ✓
// - Are value objects used? ✓
// - Is aggregate boundary clear? ✓
// - Are events published? ✓
```

### Pair Programming

**Effective patterns:**

```
Pattern 1: Expert + Junior
- Expert writes domain model
- Junior writes tests
- Junior learns DDD patterns

Pattern 2: Domain Expert + Developer
- Domain expert explains rules
- Developer codes in real-time
- Immediate feedback loop

Pattern 3: Two Developers
- Driver writes code
- Navigator reviews DDD principles
- Switch every 25 minutes
```

---

## <a name="migration-strategies"></a>5. Migration Strategies

### The Strangler Fig Approach (Recommended)

**Phase 1: New Features in DDD**
```kotlin
// New feature: Device transfer between premises
// Implement fully in DDD ✅

class Device {
    fun transfer(newPremisesId: PremisesId): Device {
        // Domain logic
        return copy(premisesId = newPremisesId)
            .also { it.addDomainEvent(DeviceTransferred(...)) }
    }
}

@Service
class TransferDeviceUseCase(
    private val repository: DeviceRepository
) {
    fun execute(command: TransferDeviceCommand) {
        val device = repository.findById(command.deviceId)
        val transferred = device.transfer(command.newPremisesId)
        repository.save(transferred)
    }
}

// Coexists with legacy code ✅
```

**Phase 2: Refactor Critical Paths**
```kotlin
// Critical path: Device activation
// Refactor to DDD ✅

// Before (legacy)
@Service
class OldDeviceService {
    fun activate(deviceId: String) {
        // 200 lines of procedural code
    }
}

// After (DDD) - gradually replace
@Service
class ActivateDeviceUseCase {
    fun execute(command: ActivateDeviceCommand) {
        // Clean DDD implementation
    }
}

// Route new calls to new implementation
// Keep old code for backward compatibility
```

**Phase 3: Complete Migration**
```kotlin
// Eventually remove all legacy code
// System fully DDD ✅
```

### The Parallel Run Strategy

```kotlin
// Run both old and new implementations
@Service
class DeviceActivationFacade(
    private val legacyService: LegacyDeviceService,
    private val newUseCase: ActivateDeviceUseCase,
    private val featureFlag: FeatureFlags
) {
    fun activate(deviceId: DeviceId) {
        if (featureFlag.isEnabled("use-ddd-activation")) {
            // New DDD implementation ✅
            newUseCase.execute(ActivateDeviceCommand(deviceId))
        } else {
            // Legacy implementation
            legacyService.activate(deviceId.value)
        }
    }
}

// Gradually roll out with feature flags
// Monitor metrics
// Compare results
// When confident, remove legacy
```

---

## <a name="lessons-from-production"></a>6. Lessons from Production

### Lesson 1: Start Simple, Evolve

**What we learned:**
```kotlin
// V1: Started simple
class Device(val deviceId: DeviceId, val name: Name)

// V2: Added behavior as needed
class Device {
    fun activate(): Device { ... }
}

// V3: Added events when needed
class Device {
    fun activate(): Device {
        // ... domain logic
        addDomainEvent(DeviceActivated(...))
    }
}

// Don't build everything upfront!
// Evolve as you understand the domain better
```

### Lesson 2: Tests Are Critical

**What we learned:**
```kotlin
// Without tests, refactoring is scary
// With tests, refactoring is safe

@Test
fun `should activate pending device with feeds`() {
    val device = createTestDevice(
        status = DeviceStatus.PENDING,
        feeds = mapOf(FeedId("feed-1") to createTestFeed())
    )
    
    val activated = device.activate()
    
    assertEquals(DeviceStatus.ACTIVE, activated.getStatus())
}

// This test enabled us to:
// - Refactor with confidence
// - Change implementation details
// - Maintain business rules
```

### Lesson 3: Documentation Decays

**What we learned:**
```kotlin
// Documentation in code stays up-to-date ✅
class Device {
    /**
     * Activate the device.
     * 
     * Requirements:
     * - Device must be in PENDING status
     * - Device must have at least one feed
     */
    fun activate(): Device {
        require(status == DeviceStatus.PENDING)
        require(feeds.isNotEmpty())
        return copy(status = DeviceStatus.ACTIVE)
    }
}

// External docs get outdated quickly ❌
// Keep docs close to code ✅
```

### Lesson 4: Premature Optimization Hurts

**What we learned:**
```kotlin
// V1: Simple, worked fine
@Service
class GetDeviceUseCase(
    private val repository: DeviceRepository
) {
    fun execute(deviceId: DeviceId): Device {
        return repository.findById(deviceId)
    }
}

// V2: Optimized too early ❌
@Service
class GetDeviceUseCase(
    private val repository: DeviceRepository,
    private val cache: DeviceCache,
    private val readModel: DeviceReadModel,
    private val projection: DeviceProjection
) {
    // Complex caching logic
    // Projections
    // Read models
    // All before we had performance problems! ❌
}

// Lesson: Start simple, optimize when needed ✅
```

### Lesson 5: Eventual Consistency is Hard

**What we learned:**
```kotlin
// Problem: User confusion
// 1. User registers device
// 2. API returns success
// 3. User refreshes page
// 4. Device not in list (read model not updated yet)
// 5. User: "Bug! My device disappeared!"

// Solution: Set expectations
@RestController
class DeviceController {
    @PostMapping
    fun register(@RequestBody request: RegisterDeviceRequest): ResponseEntity<RegisterDeviceResponse> {
        val deviceId = useCase.execute(...)
        
        return ResponseEntity
            .status(HttpStatus.ACCEPTED)  // 202, not 201 ✅
            .body(RegisterDeviceResponse(
                deviceId = deviceId.value,
                message = "Device registration in progress"  // Clear! ✅
            ))
    }
}

// Or return result immediately from write model
```

---

## <a name="maturity-model"></a>7. The DDD Maturity Model

### Level 0: Anemic

```kotlin
// Just data classes
data class Device(var id: String, var name: String)

// All logic in services
@Service
class DeviceService {
    fun activate(device: Device) {
        device.status = "ACTIVE"
    }
}
```

### Level 1: Value Objects

```kotlin
// Using value objects
@JvmInline
value class DeviceId(val value: String)

data class Name(val value: String) {
    init {
        require(value.isNotBlank())
    }
}

// But still anemic entities
data class Device(val deviceId: DeviceId, val name: Name)
```

### Level 2: Rich Entities

```kotlin
// Entities with behavior
class Device private constructor(
    val deviceId: DeviceId,
    private var status: DeviceStatus
) {
    fun activate(): Device {
        require(status == DeviceStatus.PENDING)
        return copy(status = DeviceStatus.ACTIVE)
    }
}

// But no aggregates or events yet
```

### Level 3: Aggregates

```kotlin
// Clear aggregate boundaries
class Device private constructor(
    val deviceId: DeviceId,
    private val feeds: Map<FeedId, Feed>  // Controlled access ✅
) {
    fun addFeed(feed: Feed): Device {
        require(feeds.size < MAX_FEEDS)
        // Aggregate protects invariants ✅
    }
}

// But no events yet
```

### Level 4: Domain Events

```kotlin
// Publishing domain events
class Device {
    private val _domainEvents = mutableListOf<DeviceEvent>()
    val domainEvents: List<DeviceEvent> get() = _domainEvents
    
    fun activate(): Device {
        val activated = copy(status = DeviceStatus.ACTIVE)
        activated.addDomainEvent(DeviceActivated(deviceId))
        return activated
    }
}

// Loose coupling via events ✅
```

### Level 5: Strategic Design

```kotlin
// Bounded contexts
package com.smarthome.device  // Device Management context
package com.smarthome.automation  // Automation context
package com.smarthome.billing  // Billing context

// Context mapping
class Device {
    fun activate(): Device {
        // Publish integration event for other contexts
        publishIntegrationEvent(DeviceActivatedIntegrationEvent(...))
    }
}

// Complete DDD implementation ✅
```

---

## <a name="final-thoughts"></a>8. Final Thoughts

### What DDD Gave Us

**Technical Benefits:**
- ✅ Clean, maintainable code
- ✅ Business logic encapsulated
- ✅ Type safety everywhere
- ✅ Easy to test
- ✅ Easy to refactor
- ✅ Loose coupling
- ✅ High cohesion

**Business Benefits:**
- ✅ Shared language with business
- ✅ Faster feature development
- ✅ Fewer bugs
- ✅ Easier onboarding
- ✅ Better domain understanding
- ✅ Flexible architecture

### The Journey Continues

**DDD is not a destination, it's a journey:**

```
Year 1: Learning
- Understand patterns
- Make mistakes
- Refactor often
- Build confidence

Year 2: Applying
- Natural patterns
- Fewer mistakes
- Faster development
- Teaching others

Year 3+: Mastering
- Intuitive design
- Right abstractions
- Pragmatic choices
- Continuous improvement
```

### Key Principles to Remember

1. **Business logic belongs in the domain** - Not in services, not in controllers

2. **Protect invariants** - Aggregates enforce business rules

3. **Ubiquitous language** - Same terms everywhere

4. **Bounded contexts** - Clear boundaries between domains

5. **Domain events** - Loose coupling, event-driven architecture

6. **Start simple** - Evolve as you learn

7. **Test thoroughly** - Safety net for refactoring

8. **Collaborate** - Work with domain experts

9. **Be pragmatic** - DDD where it adds value

10. **Keep learning** - Domain understanding deepens over time

### Final Exercise: Assess Your Codebase

```kotlin
// Use this checklist:

// ✅ Business logic in domain models?
class Device {
    fun activate(): Device { /* logic here */ }  // ✅ or ❌?
}

// ✅ Value objects instead of primitives?
data class Name(val value: String)  // ✅ or String? ❌

// ✅ Aggregate boundaries clear?
class Device {
    private val feeds: Map<FeedId, Feed>  // ✅ or public? ❌
}

// ✅ Domain events published?
fun activate(): Device {
    addDomainEvent(DeviceActivated(...))  // ✅ or missing? ❌
}

// ✅ Ubiquitous language used?
fun register()  // ✅ or createEntity()? ❌

// Count your ✅ and ❌
// Improve one thing each sprint
```

---

## 💡 Key Takeaways

1. **DDD is a journey, not a destination** - Continuous improvement

2. **Start with core domain** - Highest value first

3. **Avoid common mistakes** - Anemic models, over-engineering

4. **Use DDD where it adds value** - Not everywhere

5. **Collaborate with domain experts** - Essential for success

6. **Migrate gradually** - Strangler fig pattern works

7. **Learn from production** - Iterate and improve

8. **Measure maturity** - Know where you are

9. **Keep it simple** - Pragmatism over perfection

10. **Never stop learning** - Domain understanding evolves

---

## 🎯 Your Next Steps

Now that you've completed this series:

1. **Assess your current codebase** - Where are you on the maturity model?

2. **Identify one aggregate** to refactor using DDD patterns

3. **Extract value objects** from primitive types

4. **Add behavior** to your entities

5. **Define aggregate boundaries** and protect invariants

6. **Introduce domain events** for loose coupling

7. **Create ubiquitous language** with your team

8. **Measure improvement** - Code quality, velocity, bugs

9. **Share knowledge** - Teach DDD to your team

10. **Keep practicing** - DDD mastery takes time

---

## 🎊 SERIES COMPLETE!

Congratulations on completing **"Building an Enterprise IoT Platform with DDD"**!

You've mastered:
- ✅ DDD fundamentals and philosophy
- ✅ Core concepts (Value Objects, Entities, Aggregates)
- ✅ Tactical patterns (Specification, Policy, Repository, Services)
- ✅ Strategic patterns (Bounded Contexts, ACL, Events, Sagas)
- ✅ Advanced topics (CQRS, Event Sourcing, Builders, Language)
- ✅ Real-world implementation (Refactoring, Testing, Performance, Best Practices)

**20 chapters, 191,000+ words, and a complete DDD education!**

---

## 📚 Additional Resources

**Books:**
- "Domain-Driven Design" by Eric Evans
- "Implementing Domain-Driven Design" by Vaughn Vernon
- "Domain-Driven Design Distilled" by Vaughn Vernon

**Online:**
- DDD Community: dddcommunity.org
- Event Storming: eventstorming.com
- Martin Fowler's blog: martinfowler.com

**Practice:**
- Refactor your current project
- Start a side project with DDD
- Contribute to open-source DDD projects
- Join DDD community discussions

---

## 🙏 Thank You!

Thank you for joining me on this journey through Domain-Driven Design!

**What's next?**
- Apply these patterns to your projects
- Share knowledge with your team
- Continue learning and improving
- Give feedback on this series

**Questions? Feedback? Success stories?**
- Open an issue in the repository
- Share your DDD journey
- Help others learn DDD

---

## 🚀 Go Build Something Amazing!

You now have the knowledge to:
- Design clean, maintainable systems
- Model complex business domains
- Build scalable, event-driven architectures
- Write code that speaks business
- Collaborate effectively with domain experts

**The world needs more well-designed software.**

**Go make a difference!** 🌟

---

*"Software development is a learning process; working code is a side effect."  
— Alberto Brandolini*

*"Make it work, make it right, make it fast."  
— Kent Beck*

*"The best architectures, requirements, and designs emerge from self-organizing teams."  
— Agile Manifesto*

---

## 🎉 THE END 🎉

**Thank you for reading!**

**Happy coding!** 💻✨

