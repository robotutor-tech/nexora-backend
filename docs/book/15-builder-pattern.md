# Chapter 15
# Builder Pattern for Complex Aggregates

> *"Separate the construction of a complex object from its representation so that the same construction process can create different representations."*  
> — Gang of Four, Design Patterns

---

## In This Chapter

Creating complex aggregates with many parameters leads to unwieldy constructors, unclear parameter ordering, and error-prone object creation. The Builder pattern provides a fluent, readable way to construct complex objects step by step, with validation ensuring correct construction.

**What You'll Learn:**
- The complex constructor problem (15+ parameters)
- What the Builder pattern is and when to use it
- Designing fluent builder APIs
- Validation strategies in builders
- Real Device and Automation builders from SmartHome Hub
- Director pattern for common configurations
- Test data builders for fixture creation
- Common pitfalls and how to avoid them
- Testing with builders

---

## Table of Contents

1. The Problem: Complex Object Creation
2. What is the Builder Pattern?
3. Fluent Builder API
4. Validation in Builders
5. Real-World Examples from SmartHome Hub
6. Director Pattern
7. Builder for Testing
8. Common Pitfalls
9. Testing with Builders
10. Chapter Summary

---

## 1. The Problem: Complex Object Creation

### The Scenario: The Constructor Nightmare

You're building SmartHome Hub when you encounter a nightmare:

> **Developer Complaint:** "Device constructor has 15+ parameters! Can't remember the order. Tests are impossible to write. Creating devices is error-prone!"

You investigate and find chaos:

```kotlin
// Device with massive constructor ❌
class Device(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val name: Name,
    val serialNumber: SerialNumber,
    val type: DeviceType,
    val status: DeviceStatus,
    val health: DeviceHealth,
    val feeds: Map<FeedId, Feed>,
    val os: DeviceOS?,
    val zoneId: ZoneId?,
    val metadata: Map<String, String>,
    val configuration: DeviceConfiguration,
    val createdBy: ActorId,
    val createdAt: Instant,
    val updatedBy: ActorId,
    val updatedAt: Instant,
    val lastSeenAt: Instant?,
    val version: Long?
    // 18 parameters! 💥
)

// Creating device is a nightmare ❌
val device = Device(
    DeviceId("device-123"),
    PremisesId("premises-123"),
    Name("Living Room Sensor"),
    SerialNumber("SN12345"),
    DeviceType.SENSOR,
    DeviceStatus.PENDING,
    DeviceHealth.ONLINE,
    mapOf(
        FeedId("feed-1") to Feed(...),  // Need to create feeds first
        FeedId("feed-2") to Feed(...)
    ),
    DeviceOS.ZIGBEE,
    ZoneId("zone-123"),
    mapOf("location" to "living room"),
    DeviceConfiguration(...),  // Need to create config
    ActorId("actor-123"),
    Instant.now(),
    ActorId("actor-123"),
    Instant.now(),
    null,
    null
)
// What was parameter 7 again? 💥
// Easy to pass wrong value in wrong position! 💥
```

### The Problems

**Problem 1: Parameter Order Confusion**

```kotlin
// Which is which? ❌
val device = Device(
    DeviceId("device-123"),
    PremisesId("premises-123"),
    Name("Sensor"),
    SerialNumber("SN12345"),
    DeviceType.SENSOR,
    DeviceStatus.PENDING,  // Is this parameter 6 or 7?
    DeviceHealth.ONLINE,
    // ... 10 more parameters
)

// Easy to swap parameters ❌
val device = Device(
    DeviceId("device-123"),
    Name("Sensor"),  // Oops! Should be PremisesId
    PremisesId("premises-123"),  // Oops! Should be Name
    // Compiles but wrong! 💥
)
```

**Problem 2: Optional Parameters**

```kotlin
// Kotlin default parameters help, but...
class Device(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val name: Name,
    val serialNumber: SerialNumber,
    val type: DeviceType,
    val status: DeviceStatus = DeviceStatus.PENDING,  // Default
    val health: DeviceHealth = DeviceHealth.ONLINE,   // Default
    val feeds: Map<FeedId, Feed> = emptyMap(),        // Default
    val os: DeviceOS? = null,                         // Optional
    val zoneId: ZoneId? = null,                       // Optional
    // ... more parameters
)

// Still confusing ❌
val device = Device(
    deviceId = DeviceId("device-123"),
    premisesId = PremisesId("premises-123"),
    name = Name("Sensor"),
    serialNumber = SerialNumber("SN12345"),
    type = DeviceType.SENSOR,
    zoneId = ZoneId("zone-123"),  // Skip status, health, feeds, os
    // Which parameters did I skip? Easy to miss required ones!
)
```

**Problem 3: Complex Validation**

```kotlin
// Validation scattered in constructor ❌
class Device(
    val deviceId: DeviceId,
    val name: Name,
    val feeds: Map<FeedId, Feed>,
    // ...
) {
    init {
        require(name.value.isNotBlank()) { "Name required" }
        require(feeds.isNotEmpty()) { "At least one feed required" }
        // More validation...
        
        // But what if feeds depend on device type?
        // What if some fields are conditional?
        // Constructor becomes huge! 💥
    }
}
```

**Problem 4: Test Data Creation**

```kotlin
// Tests need to create many devices ❌
class DeviceServiceTest {
    
    @Test
    fun `test device activation`() {
        // 18 parameters for every test! ❌
        val device = Device(
            DeviceId("device-123"),
            PremisesId("premises-123"),
            Name("Test Device"),
            SerialNumber("TEST123"),
            DeviceType.SENSOR,
            DeviceStatus.PENDING,
            DeviceHealth.ONLINE,
            mapOf(FeedId("feed-1") to Feed(...)),
            DeviceOS.ZIGBEE,
            null,
            emptyMap(),
            DeviceConfiguration(...),
            ActorId("test-actor"),
            Instant.now(),
            ActorId("test-actor"),
            Instant.now(),
            null,
            null
        )
        
        // Repeated in every test! 💥
    }
}
```

### The Real Cost

**Measured impact:**
- 🔴 **30 minutes to create complex aggregate** - Developers frustrated
- 🔴 **High bug rate** - Wrong parameters in wrong position
- 🔴 **Tests take forever to write** - 18 parameters per test
- 🔴 **Hard to maintain** - Adding field = update 50+ places
- 🔴 **New developers overwhelmed** - Can't remember parameter order

**Root Cause:** No pattern for creating complex objects with many parameters, optional fields, and validation logic.

---

## <a name="what-is-builder"></a>2. What is the Builder Pattern?

### Definition

> **Builder Pattern:** Separate the construction of a complex object from its representation so that the same construction process can create different representations.
> 
> — Gang of Four, Design Patterns

### Core Concept

```
Instead of this (Telescoping Constructor):
┌─────────────────────────────────────────────────────┐
│  Device(p1, p2, p3, p4, p5, p6, p7, p8, ...)        │
│  18 parameters in constructor! 💥                   │
└─────────────────────────────────────────────────────┘

Use this (Builder):
┌─────────────────────────────────────────────────────┐
│  DeviceBuilder()                                    │
│    .withDeviceId(deviceId)                          │
│    .withPremisesId(premisesId)                      │
│    .withName(name)                                  │
│    .withSerialNumber(serialNumber)                  │
│    .withType(DeviceType.SENSOR)                     │
│    .addFeed(feed1)                                  │
│    .addFeed(feed2)                                  │
│    .inZone(zoneId)                                  │
│    .build()                                         │
│                                                     │
│  ✅ Clear, readable, self-documenting              │
└─────────────────────────────────────────────────────┘
```

### Benefits

1. **Readable** - Method names explain what's being set
2. **Flexible** - Optional parameters easy to skip
3. **Validated** - Validation at build time
4. **Reusable** - Same builder for different configurations
5. **Testable** - Easy to create test data

---

## <a name="fluent-api"></a>3. Fluent Builder API

### Basic Builder Implementation

```kotlin
// Device builder
class DeviceBuilder {
    
    private var deviceId: DeviceId? = null
    private var premisesId: PremisesId? = null
    private var name: Name? = null
    private var serialNumber: SerialNumber? = null
    private var type: DeviceType? = null
    private var status: DeviceStatus = DeviceStatus.PENDING
    private var health: DeviceHealth = DeviceHealth.ONLINE
    private val feeds: MutableMap<FeedId, Feed> = mutableMapOf()
    private var os: DeviceOS? = null
    private var zoneId: ZoneId? = null
    private var metadata: MutableMap<String, String> = mutableMapOf()
    private var configuration: DeviceConfiguration? = null
    private var createdBy: ActorId? = null
    private var createdAt: Instant = Instant.now()
    private var version: Long? = null
    
    // Required fields
    fun withDeviceId(deviceId: DeviceId): DeviceBuilder {
        this.deviceId = deviceId
        return this
    }
    
    fun withPremisesId(premisesId: PremisesId): DeviceBuilder {
        this.premisesId = premisesId
        return this
    }
    
    fun withName(name: Name): DeviceBuilder {
        this.name = name
        return this
    }
    
    fun withSerialNumber(serialNumber: SerialNumber): DeviceBuilder {
        this.serialNumber = serialNumber
        return this
    }
    
    fun withType(type: DeviceType): DeviceBuilder {
        this.type = type
        return this
    }
    
    // Optional fields
    fun withStatus(status: DeviceStatus): DeviceBuilder {
        this.status = status
        return this
    }
    
    fun withHealth(health: DeviceHealth): DeviceBuilder {
        this.health = health
        return this
    }
    
    fun withOS(os: DeviceOS): DeviceBuilder {
        this.os = os
        return this
    }
    
    fun inZone(zoneId: ZoneId): DeviceBuilder {
        this.zoneId = zoneId
        return this
    }
    
    // Collections
    fun addFeed(feed: Feed): DeviceBuilder {
        this.feeds[feed.feedId] = feed
        return this
    }
    
    fun addFeeds(feeds: List<Feed>): DeviceBuilder {
        feeds.forEach { addFeed(it) }
        return this
    }
    
    fun withMetadata(key: String, value: String): DeviceBuilder {
        this.metadata[key] = value
        return this
    }
    
    // Configuration
    fun withConfiguration(configuration: DeviceConfiguration): DeviceBuilder {
        this.configuration = configuration
        return this
    }
    
    fun createdBy(actorId: ActorId): DeviceBuilder {
        this.createdBy = actorId
        return this
    }
    
    fun createdAt(createdAt: Instant): DeviceBuilder {
        this.createdAt = createdAt
        return this
    }
    
    // Build
    fun build(): Device {
        // Validate required fields
        requireNotNull(deviceId) { "Device ID is required" }
        requireNotNull(premisesId) { "Premises ID is required" }
        requireNotNull(name) { "Name is required" }
        requireNotNull(serialNumber) { "Serial number is required" }
        requireNotNull(type) { "Device type is required" }
        requireNotNull(createdBy) { "Created by is required" }
        
        // Business validation
        require(feeds.isNotEmpty()) {
            "Device must have at least one feed"
        }
        
        // Create device
        return Device(
            deviceId = deviceId!!,
            premisesId = premisesId!!,
            name = name!!,
            serialNumber = serialNumber!!,
            type = type!!,
            status = status,
            health = health,
            feeds = feeds.toMap(),
            os = os,
            zoneId = zoneId,
            metadata = metadata.toMap(),
            configuration = configuration ?: DeviceConfiguration.default(),
            createdBy = createdBy!!,
            createdAt = createdAt,
            updatedBy = createdBy!!,
            updatedAt = createdAt,
            lastSeenAt = null,
            version = version
        )
    }
}

// Usage - Clear and readable! ✅
val device = DeviceBuilder()
    .withDeviceId(DeviceId.generate())
    .withPremisesId(PremisesId("premises-123"))
    .withName(Name("Living Room Sensor"))
    .withSerialNumber(SerialNumber("SN12345"))
    .withType(DeviceType.SENSOR)
    .withOS(DeviceOS.ZIGBEE)
    .inZone(ZoneId("zone-living-room"))
    .addFeed(temperatureFeed)
    .addFeed(humidityFeed)
    .withMetadata("location", "living room")
    .withMetadata("room", "living-room")
    .createdBy(ActorId("actor-123"))
    .build()

// Much better! ✅
```

### Nested Builders

```kotlin
// Feed builder
class FeedBuilder {
    private var feedId: FeedId? = null
    private var deviceId: DeviceId? = null
    private var name: String? = null
    private var type: FeedType? = null
    private var value: Int = 0
    private var unit: String? = null
    private var minValue: Int? = null
    private var maxValue: Int? = null
    
    fun withFeedId(feedId: FeedId): FeedBuilder {
        this.feedId = feedId
        return this
    }
    
    fun withDeviceId(deviceId: DeviceId): FeedBuilder {
        this.deviceId = deviceId
        return this
    }
    
    fun withName(name: String): FeedBuilder {
        this.name = name
        return this
    }
    
    fun ofType(type: FeedType): FeedBuilder {
        this.type = type
        return this
    }
    
    fun withInitialValue(value: Int): FeedBuilder {
        this.value = value
        return this
    }
    
    fun withUnit(unit: String): FeedBuilder {
        this.unit = unit
        return this
    }
    
    fun withRange(min: Int, max: Int): FeedBuilder {
        this.minValue = min
        this.maxValue = max
        return this
    }
    
    fun build(): Feed {
        requireNotNull(feedId) { "Feed ID is required" }
        requireNotNull(deviceId) { "Device ID is required" }
        requireNotNull(name) { "Name is required" }
        requireNotNull(type) { "Feed type is required" }
        
        return Feed(
            feedId = feedId!!,
            deviceId = deviceId!!,
            name = name!!,
            type = type!!,
            value = value,
            unit = unit,
            minValue = minValue,
            maxValue = maxValue,
            lastUpdated = Instant.now()
        )
    }
}

// Combine builders ✅
val device = DeviceBuilder()
    .withDeviceId(deviceId)
    .withPremisesId(premisesId)
    .withName(Name("Temperature Sensor"))
    .withSerialNumber(SerialNumber("SN12345"))
    .withType(DeviceType.SENSOR)
    .addFeed(
        FeedBuilder()
            .withFeedId(FeedId.generate())
            .withDeviceId(deviceId)
            .withName("Temperature")
            .ofType(FeedType.TEMPERATURE)
            .withInitialValue(20)
            .withUnit("°C")
            .withRange(-40, 125)
            .build()
    )
    .addFeed(
        FeedBuilder()
            .withFeedId(FeedId.generate())
            .withDeviceId(deviceId)
            .withName("Humidity")
            .ofType(FeedType.HUMIDITY)
            .withInitialValue(50)
            .withUnit("%")
            .withRange(0, 100)
            .build()
    )
    .createdBy(ActorId("actor-123"))
    .build()
```

---

## <a name="validation"></a>4. Validation in Builders

### Validation at Build Time

```kotlin
class DeviceBuilder {
    // ... fields
    
    fun build(): Device {
        // 1. Check required fields
        val errors = mutableListOf<String>()
        
        if (deviceId == null) errors.add("Device ID is required")
        if (premisesId == null) errors.add("Premises ID is required")
        if (name == null) errors.add("Name is required")
        if (serialNumber == null) errors.add("Serial number is required")
        if (type == null) errors.add("Device type is required")
        if (createdBy == null) errors.add("Created by is required")
        
        // 2. Business rules validation
        if (feeds.isEmpty()) {
            errors.add("Device must have at least one feed")
        }
        
        if (type == DeviceType.SENSOR && feeds.none { it.value.type == FeedType.SENSOR }) {
            errors.add("Sensor device must have at least one sensor feed")
        }
        
        if (type == DeviceType.ACTUATOR && feeds.none { it.value.type == FeedType.ACTUATOR }) {
            errors.add("Actuator device must have at least one actuator feed")
        }
        
        // 3. Throw if errors
        if (errors.isNotEmpty()) {
            throw ValidationException("Device validation failed: ${errors.joinToString(", ")}")
        }
        
        // 4. Create device
        return Device(...)
    }
}
```

### Step-by-Step Validation

```kotlin
// Step builder - enforces order
interface DeviceIdStep {
    fun withDeviceId(deviceId: DeviceId): PremisesIdStep
}

interface PremisesIdStep {
    fun withPremisesId(premisesId: PremisesId): NameStep
}

interface NameStep {
    fun withName(name: Name): SerialNumberStep
}

interface SerialNumberStep {
    fun withSerialNumber(serialNumber: SerialNumber): TypeStep
}

interface TypeStep {
    fun withType(type: DeviceType): OptionalFieldsStep
}

interface OptionalFieldsStep {
    fun withStatus(status: DeviceStatus): OptionalFieldsStep
    fun withHealth(health: DeviceHealth): OptionalFieldsStep
    fun addFeed(feed: Feed): OptionalFieldsStep
    fun inZone(zoneId: ZoneId): OptionalFieldsStep
    fun createdBy(actorId: ActorId): BuildStep
}

interface BuildStep {
    fun build(): Device
}

// Implementation
class DeviceStepBuilder private constructor() : 
    DeviceIdStep,
    PremisesIdStep,
    NameStep,
    SerialNumberStep,
    TypeStep,
    OptionalFieldsStep,
    BuildStep {
    
    private lateinit var deviceId: DeviceId
    private lateinit var premisesId: PremisesId
    private lateinit var name: Name
    private lateinit var serialNumber: SerialNumber
    private lateinit var type: DeviceType
    private var status: DeviceStatus = DeviceStatus.PENDING
    private var health: DeviceHealth = DeviceHealth.ONLINE
    private val feeds: MutableMap<FeedId, Feed> = mutableMapOf()
    private var zoneId: ZoneId? = null
    private lateinit var createdBy: ActorId
    
    companion object {
        fun builder(): DeviceIdStep = DeviceStepBuilder()
    }
    
    override fun withDeviceId(deviceId: DeviceId): PremisesIdStep {
        this.deviceId = deviceId
        return this
    }
    
    override fun withPremisesId(premisesId: PremisesId): NameStep {
        this.premisesId = premisesId
        return this
    }
    
    override fun withName(name: Name): SerialNumberStep {
        this.name = name
        return this
    }
    
    override fun withSerialNumber(serialNumber: SerialNumber): TypeStep {
        this.serialNumber = serialNumber
        return this
    }
    
    override fun withType(type: DeviceType): OptionalFieldsStep {
        this.type = type
        return this
    }
    
    override fun withStatus(status: DeviceStatus): OptionalFieldsStep {
        this.status = status
        return this
    }
    
    override fun withHealth(health: DeviceHealth): OptionalFieldsStep {
        this.health = health
        return this
    }
    
    override fun addFeed(feed: Feed): OptionalFieldsStep {
        this.feeds[feed.feedId] = feed
        return this
    }
    
    override fun inZone(zoneId: ZoneId): OptionalFieldsStep {
        this.zoneId = zoneId
        return this
    }
    
    override fun createdBy(actorId: ActorId): BuildStep {
        this.createdBy = actorId
        return this
    }
    
    override fun build(): Device {
        require(feeds.isNotEmpty()) { "Device must have at least one feed" }
        
        return Device(
            deviceId = deviceId,
            premisesId = premisesId,
            name = name,
            serialNumber = serialNumber,
            type = type,
            status = status,
            health = health,
            feeds = feeds.toMap(),
            // ... other fields
        )
    }
}

// Usage - Compiler enforces order! ✅
val device = DeviceStepBuilder.builder()
    .withDeviceId(DeviceId.generate())  // Must be first
    .withPremisesId(PremisesId("premises-123"))  // Must be second
    .withName(Name("Sensor"))  // Must be third
    .withSerialNumber(SerialNumber("SN12345"))  // Must be fourth
    .withType(DeviceType.SENSOR)  // Must be fifth
    .addFeed(feed1)  // Optional
    .addFeed(feed2)  // Optional
    .createdBy(ActorId("actor-123"))  // Before build
    .build()  // Must be last

// Can't compile if order is wrong! ✅
```

---

## <a name="real-examples"></a>5. Real-World Examples from SmartHome Hub

### Example 1: Device Registration Test Data

```kotlin
// Test data builder
object TestDeviceBuilder {
    
    fun aDevice(
        deviceId: DeviceId = DeviceId.generate(),
        premisesId: PremisesId = PremisesId("test-premises"),
        name: Name = Name("Test Device"),
        serialNumber: SerialNumber = SerialNumber("TEST-${UUID.randomUUID()}"),
        type: DeviceType = DeviceType.SENSOR
    ): DeviceBuilder {
        return DeviceBuilder()
            .withDeviceId(deviceId)
            .withPremisesId(premisesId)
            .withName(name)
            .withSerialNumber(serialNumber)
            .withType(type)
            .addFeed(aTemperatureFeed(deviceId))
            .createdBy(ActorId("test-actor"))
    }
    
    fun aTemperatureFeed(deviceId: DeviceId): Feed {
        return FeedBuilder()
            .withFeedId(FeedId.generate())
            .withDeviceId(deviceId)
            .withName("Temperature")
            .ofType(FeedType.TEMPERATURE)
            .withInitialValue(20)
            .withUnit("°C")
            .build()
    }
    
    fun aSensor(premisesId: PremisesId = PremisesId("test-premises")): Device {
        val deviceId = DeviceId.generate()
        return aDevice(
            deviceId = deviceId,
            premisesId = premisesId,
            type = DeviceType.SENSOR
        )
            .addFeed(aTemperatureFeed(deviceId))
            .addFeed(aHumidityFeed(deviceId))
            .build()
    }
    
    fun anActuator(premisesId: PremisesId = PremisesId("test-premises")): Device {
        val deviceId = DeviceId.generate()
        return aDevice(
            deviceId = deviceId,
            premisesId = premisesId,
            type = DeviceType.ACTUATOR
        )
            .addFeed(aSwitchFeed(deviceId))
            .build()
    }
}

// Usage in tests - Easy! ✅
class DeviceServiceTest {
    
    @Test
    fun `should activate device`() {
        // Create test device with builder
        val device = TestDeviceBuilder.aSensor()
        
        // Test activation
        val activated = device.activate()
        
        assertEquals(DeviceStatus.ACTIVE, activated.getStatus())
    }
    
    @Test
    fun `should not activate device without feeds`() {
        // Custom device without feeds
        val device = TestDeviceBuilder.aDevice()
            .build()  // Don't add feeds
        
        assertThrows<ValidationException> {
            device.activate()
        }
    }
    
    @Test
    fun `should add device to zone`() {
        val zoneId = ZoneId("zone-123")
        
        // Device in specific zone
        val device = TestDeviceBuilder.aSensor()
            .inZone(zoneId)
            .build()
        
        assertEquals(zoneId, device.getZoneId())
    }
}
```

### Example 2: Automation Builder

```kotlin
// Automation with complex structure
class AutomationBuilder {
    
    private var automationId: AutomationId? = null
    private var premisesId: PremisesId? = null
    private var name: Name? = null
    private val triggers: MutableList<Trigger> = mutableListOf()
    private val conditions: MutableList<Condition> = mutableListOf()
    private val actions: MutableList<Action> = mutableListOf()
    private var isActive: Boolean = true
    private var createdBy: ActorId? = null
    
    fun withAutomationId(automationId: AutomationId): AutomationBuilder {
        this.automationId = automationId
        return this
    }
    
    fun withPremisesId(premisesId: PremisesId): AutomationBuilder {
        this.premisesId = premisesId
        return this
    }
    
    fun withName(name: Name): AutomationBuilder {
        this.name = name
        return this
    }
    
    // Fluent trigger API
    fun whenDeviceValueChanges(
        deviceId: DeviceId,
        feedId: FeedId,
        operator: ComparisonOperator,
        threshold: Int
    ): AutomationBuilder {
        triggers.add(
            Trigger.DeviceValueChanged(
                deviceId = deviceId,
                feedId = feedId,
                operator = operator,
                threshold = threshold
            )
        )
        return this
    }
    
    fun whenDeviceGoesOnline(deviceId: DeviceId): AutomationBuilder {
        triggers.add(Trigger.DeviceOnline(deviceId))
        return this
    }
    
    fun whenDeviceGoesOffline(deviceId: DeviceId): AutomationBuilder {
        triggers.add(Trigger.DeviceOffline(deviceId))
        return this
    }
    
    // Fluent condition API
    fun ifDeviceStatus(deviceId: DeviceId, status: DeviceStatus): AutomationBuilder {
        conditions.add(Condition.DeviceHasStatus(deviceId, status))
        return this
    }
    
    fun ifTimeBetween(startTime: LocalTime, endTime: LocalTime): AutomationBuilder {
        conditions.add(Condition.TimeBetween(startTime, endTime))
        return this
    }
    
    // Fluent action API
    fun thenSetDeviceValue(
        deviceId: DeviceId,
        feedId: FeedId,
        value: Int
    ): AutomationBuilder {
        actions.add(
            Action.SetDeviceValue(
                deviceId = deviceId,
                feedId = feedId,
                value = value
            )
        )
        return this
    }
    
    fun thenSendNotification(message: String): AutomationBuilder {
        actions.add(Action.SendNotification(message))
        return this
    }
    
    fun isActive(active: Boolean): AutomationBuilder {
        this.isActive = active
        return this
    }
    
    fun createdBy(actorId: ActorId): AutomationBuilder {
        this.createdBy = actorId
        return this
    }
    
    fun build(): Automation {
        requireNotNull(automationId) { "Automation ID required" }
        requireNotNull(premisesId) { "Premises ID required" }
        requireNotNull(name) { "Name required" }
        requireNotNull(createdBy) { "Created by required" }
        require(triggers.isNotEmpty()) { "At least one trigger required" }
        require(actions.isNotEmpty()) { "At least one action required" }
        
        return Automation(
            automationId = automationId!!,
            premisesId = premisesId!!,
            name = name!!,
            triggers = triggers.toList(),
            conditions = conditions.toList(),
            actions = actions.toList(),
            isActive = isActive,
            createdBy = createdBy!!,
            createdAt = Instant.now()
        )
    }
}

// Usage - Reads like natural language! ✅
val automation = AutomationBuilder()
    .withAutomationId(AutomationId.generate())
    .withPremisesId(premisesId)
    .withName(Name("Turn on heater when cold"))
    .whenDeviceValueChanges(
        deviceId = temperatureSensorId,
        feedId = temperatureFeedId,
        operator = ComparisonOperator.LESS_THAN,
        threshold = 18
    )
    .ifTimeBetween(
        startTime = LocalTime.of(6, 0),
        endTime = LocalTime.of(22, 0)
    )
    .thenSetDeviceValue(
        deviceId = heaterId,
        feedId = heaterSwitchFeedId,
        value = 1  // Turn on
    )
    .thenSendNotification("Heater turned on due to low temperature")
    .createdBy(ActorId("actor-123"))
    .build()
```

### Example 3: Query Builder

```kotlin
// Complex query with many filters
class DeviceQueryBuilder {
    
    private var premisesId: PremisesId? = null
    private var status: DeviceStatus? = null
    private var type: DeviceType? = null
    private var zoneId: ZoneId? = null
    private var health: DeviceHealth? = null
    private var searchTerm: String? = null
    private var sortBy: DeviceSortField = DeviceSortField.CREATED_AT
    private var sortOrder: SortOrder = SortOrder.DESC
    private var page: Int = 0
    private var pageSize: Int = 20
    
    fun forPremises(premisesId: PremisesId): DeviceQueryBuilder {
        this.premisesId = premisesId
        return this
    }
    
    fun withStatus(status: DeviceStatus): DeviceQueryBuilder {
        this.status = status
        return this
    }
    
    fun ofType(type: DeviceType): DeviceQueryBuilder {
        this.type = type
        return this
    }
    
    fun inZone(zoneId: ZoneId): DeviceQueryBuilder {
        this.zoneId = zoneId
        return this
    }
    
    fun withHealth(health: DeviceHealth): DeviceQueryBuilder {
        this.health = health
        return this
    }
    
    fun searchFor(term: String): DeviceQueryBuilder {
        this.searchTerm = term
        return this
    }
    
    fun sortBy(field: DeviceSortField, order: SortOrder = SortOrder.ASC): DeviceQueryBuilder {
        this.sortBy = field
        this.sortOrder = order
        return this
    }
    
    fun page(page: Int, pageSize: Int = 20): DeviceQueryBuilder {
        this.page = page
        this.pageSize = pageSize
        return this
    }
    
    fun build(): DeviceQuery {
        requireNotNull(premisesId) { "Premises ID required" }
        
        return DeviceQuery(
            premisesId = premisesId!!,
            filters = DeviceFilters(
                status = status,
                type = type,
                zoneId = zoneId,
                health = health,
                searchTerm = searchTerm
            ),
            sorting = DeviceSorting(sortBy, sortOrder),
            pagination = Pagination(page, pageSize)
        )
    }
}

// Usage - Flexible and readable! ✅
val query = DeviceQueryBuilder()
    .forPremises(premisesId)
    .withStatus(DeviceStatus.ACTIVE)
    .ofType(DeviceType.SENSOR)
    .inZone(ZoneId("living-room"))
    .searchFor("temperature")
    .sortBy(DeviceSortField.NAME, SortOrder.ASC)
    .page(0, 50)
    .build()

val devices = deviceQueryHandler.handle(query)
```

---

## <a name="director-pattern"></a>6. Director Pattern

### Director for Common Configurations

```kotlin
// Director creates complex objects using builder
class DeviceDirector(
    private val premisesId: PremisesId,
    private val actorId: ActorId
) {
    
    fun createTemperatureSensor(
        name: String,
        serialNumber: String,
        zoneId: ZoneId? = null
    ): Device {
        val deviceId = DeviceId.generate()
        
        return DeviceBuilder()
            .withDeviceId(deviceId)
            .withPremisesId(premisesId)
            .withName(Name(name))
            .withSerialNumber(SerialNumber(serialNumber))
            .withType(DeviceType.SENSOR)
            .withOS(DeviceOS.ZIGBEE)
            .addFeed(
                FeedBuilder()
                    .withFeedId(FeedId.generate())
                    .withDeviceId(deviceId)
                    .withName("Temperature")
                    .ofType(FeedType.TEMPERATURE)
                    .withInitialValue(20)
                    .withUnit("°C")
                    .withRange(-40, 125)
                    .build()
            )
            .apply { if (zoneId != null) inZone(zoneId) }
            .createdBy(actorId)
            .build()
    }
    
    fun createSmartSwitch(
        name: String,
        serialNumber: String,
        zoneId: ZoneId? = null
    ): Device {
        val deviceId = DeviceId.generate()
        
        return DeviceBuilder()
            .withDeviceId(deviceId)
            .withPremisesId(premisesId)
            .withName(Name(name))
            .withSerialNumber(SerialNumber(serialNumber))
            .withType(DeviceType.ACTUATOR)
            .withOS(DeviceOS.WIFI)
            .addFeed(
                FeedBuilder()
                    .withFeedId(FeedId.generate())
                    .withDeviceId(deviceId)
                    .withName("Switch")
                    .ofType(FeedType.SWITCH)
                    .withInitialValue(0)
                    .withRange(0, 1)
                    .build()
            )
            .apply { if (zoneId != null) inZone(zoneId) }
            .createdBy(actorId)
            .build()
    }
    
    fun createMotionSensor(
        name: String,
        serialNumber: String,
        zoneId: ZoneId? = null
    ): Device {
        val deviceId = DeviceId.generate()
        
        return DeviceBuilder()
            .withDeviceId(deviceId)
            .withPremisesId(premisesId)
            .withName(Name(name))
            .withSerialNumber(SerialNumber(serialNumber))
            .withType(DeviceType.SENSOR)
            .withOS(DeviceOS.ZIGBEE)
            .addFeed(
                FeedBuilder()
                    .withFeedId(FeedId.generate())
                    .withDeviceId(deviceId)
                    .withName("Motion")
                    .ofType(FeedType.BINARY)
                    .withInitialValue(0)
                    .withRange(0, 1)
                    .build()
            )
            .apply { if (zoneId != null) inZone(zoneId) }
            .createdBy(actorId)
            .build()
    }
}

// Usage - Simple! ✅
val director = DeviceDirector(premisesId, actorId)

val tempSensor = director.createTemperatureSensor(
    name = "Living Room Temperature",
    serialNumber = "TEMP-001",
    zoneId = livingRoomZoneId
)

val switch = director.createSmartSwitch(
    name = "Living Room Light",
    serialNumber = "SWITCH-001",
    zoneId = livingRoomZoneId
)

val motionSensor = director.createMotionSensor(
    name = "Hallway Motion",
    serialNumber = "MOTION-001",
    zoneId = hallwayZoneId
)
```

---

## <a name="builder-testing"></a>7. Builder for Testing

### Test Data Builder Pattern

```kotlin
// Fixture builder for tests
object DeviceFixtures {
    
    fun defaultDevice(): DeviceBuilder {
        val deviceId = DeviceId.generate()
        return DeviceBuilder()
            .withDeviceId(deviceId)
            .withPremisesId(PremisesId("test-premises"))
            .withName(Name("Test Device"))
            .withSerialNumber(SerialNumber("TEST-${UUID.randomUUID()}"))
            .withType(DeviceType.SENSOR)
            .addFeed(defaultFeed(deviceId))
            .createdBy(ActorId("test-actor"))
    }
    
    fun defaultFeed(deviceId: DeviceId): Feed {
        return FeedBuilder()
            .withFeedId(FeedId.generate())
            .withDeviceId(deviceId)
            .withName("Test Feed")
            .ofType(FeedType.SENSOR)
            .withInitialValue(0)
            .build()
    }
    
    fun activatedDevice(): Device {
        return defaultDevice()
            .withStatus(DeviceStatus.ACTIVE)
            .build()
    }
    
    fun deviceInZone(zoneId: ZoneId): Device {
        return defaultDevice()
            .inZone(zoneId)
            .build()
    }
    
    fun deviceWithMultipleFeeds(feedCount: Int = 3): Device {
        val deviceId = DeviceId.generate()
        val builder = defaultDevice()
            .withDeviceId(deviceId)
        
        repeat(feedCount) { index ->
            builder.addFeed(
                FeedBuilder()
                    .withFeedId(FeedId.generate())
                    .withDeviceId(deviceId)
                    .withName("Feed $index")
                    .ofType(FeedType.SENSOR)
                    .withInitialValue(index * 10)
                    .build()
            )
        }
        
        return builder.build()
    }
}

// Usage in tests ✅
class DeviceTest {
    
    @Test
    fun `should activate device`() {
        val device = DeviceFixtures.defaultDevice()
            .withStatus(DeviceStatus.PENDING)
            .build()
        
        val activated = device.activate()
        
        assertEquals(DeviceStatus.ACTIVE, activated.getStatus())
    }
    
    @Test
    fun `should move device to zone`() {
        val device = DeviceFixtures.defaultDevice().build()
        val newZone = ZoneId("new-zone")
        
        val moved = device.moveToZone(newZone)
        
        assertEquals(newZone, moved.getZoneId())
    }
    
    @Test
    fun `should calculate device health score`() {
        val device = DeviceFixtures.deviceWithMultipleFeeds(5)
        
        val healthScore = deviceHealthService.calculateScore(device)
        
        assertTrue(healthScore > 0)
    }
}
```

---

## <a name="pitfalls"></a>8. Common Pitfalls

### Pitfall 1: Builder with Too Many Methods

```kotlin
// Builder bloat ❌
class DeviceBuilder {
    // 50+ methods!
    fun withDeviceId(...)
    fun setDeviceId(...)  // Duplicate!
    fun deviceId(...)     // Another duplicate!
    fun id(...)           // Yet another!
    
    // Too many ways to do same thing ❌
}

// Better - One clear way ✅
class DeviceBuilder {
    fun withDeviceId(deviceId: DeviceId): DeviceBuilder
    // Just one method per field ✅
}
```

### Pitfall 2: Mutable Builder State

```kotlin
// Mutable builder reused ❌
val builder = DeviceBuilder()
    .withDeviceId(DeviceId("device-1"))
    .withPremisesId(PremisesId("premises-1"))

val device1 = builder.withName(Name("Device 1")).build()
val device2 = builder.withName(Name("Device 2")).build()
// device2 has same state as device1 except name! 💥

// Better - Create new builder each time ✅
fun createDevice(name: String): Device {
    return DeviceBuilder()
        .withDeviceId(DeviceId.generate())
        .withPremisesId(premisesId)
        .withName(Name(name))
        .build()
}
```

### Pitfall 3: No Validation

```kotlin
// No validation ❌
class DeviceBuilder {
    fun build(): Device {
        return Device(deviceId!!, name!!, ...) // NullPointerException!
    }
}

// Better - Validate in build() ✅
class DeviceBuilder {
    fun build(): Device {
        requireNotNull(deviceId) { "Device ID required" }
        requireNotNull(name) { "Name required" }
        require(feeds.isNotEmpty()) { "At least one feed required" }
        
        return Device(...)
    }
}
```

---

## <a name="testing"></a>9. Testing with Builders

### Unit Tests

```kotlin
class DeviceBuilderTest {
    
    @Test
    fun `should build device with required fields`() {
        val deviceId = DeviceId.generate()
        val device = DeviceBuilder()
            .withDeviceId(deviceId)
            .withPremisesId(PremisesId("premises-123"))
            .withName(Name("Test Device"))
            .withSerialNumber(SerialNumber("SN12345"))
            .withType(DeviceType.SENSOR)
            .addFeed(createTestFeed(deviceId))
            .createdBy(ActorId("actor-123"))
            .build()
        
        assertEquals(deviceId, device.deviceId)
        assertEquals("Test Device", device.getName().value)
    }
    
    @Test
    fun `should fail when required field missing`() {
        assertThrows<IllegalArgumentException> {
            DeviceBuilder()
                .withDeviceId(DeviceId.generate())
                // Missing other required fields
                .build()
        }
    }
    
    @Test
    fun `should fail when no feeds added`() {
        assertThrows<IllegalArgumentException> {
            DeviceBuilder()
                .withDeviceId(DeviceId.generate())
                .withPremisesId(PremisesId("premises-123"))
                .withName(Name("Test Device"))
                .withSerialNumber(SerialNumber("SN12345"))
                .withType(DeviceType.SENSOR)
                // No feeds!
                .createdBy(ActorId("actor-123"))
                .build()
        }
    }
}
```

---

## 10. Chapter Summary

In this chapter, we've explored the Builder pattern—a creational pattern that separates complex object construction from representation, providing a fluent, readable way to create objects with many parameters while ensuring validation and correctness.

### What We Covered

**The Complex Constructor Problem:**
- Constructors with 15+ parameters
- Unclear parameter ordering
- Easy to pass wrong arguments
- Difficult to read and maintain
- Optional parameters create combinatorial explosion
- Testing becomes nightmare

**The Builder Solution:**
- Fluent, readable API
- Clear parameter names
- Validation at build time
- Optional parameters made easy
- Step-by-step construction
- Self-documenting code

**Core Pattern:**
```kotlin
// Before (Constructor Hell) ❌
val device = Device(
    DeviceId.generate(),
    premisesId,
    Name("Sensor"),
    SerialNumber("ABC123"),
    DeviceType.SENSOR,
    DeviceStatus.PENDING,
    DeviceHealth.UNKNOWN,
    emptyMap(),
    null,
    null,
    emptyMap(),
    DeviceConfiguration.default(),
    ActorId("user-1"),
    Instant.now(),
    ActorId("user-1"),
    Instant.now()
)  // What is what?? ❌

// After (Builder) ✅
val device = DeviceBuilder()
    .withDeviceId(DeviceId.generate())
    .withPremisesId(premisesId)
    .withName("Sensor")
    .withSerialNumber("ABC123")
    .withType(DeviceType.SENSOR)
    .build()  // Clear and readable! ✅
```

### Key Insights

1. **Builders solve parameter explosion** - 15+ parameters become manageable.

2. **Fluent API improves readability** - Code reads like natural language.

3. **Validation at build time** - Catch errors before object creation.

4. **Optional parameters handled elegantly** - Defaults and conditionals.

5. **Step builders enforce order** - Compiler ensures required fields.

6. **Nested builders for composition** - Complex structures built cleanly.

7. **Director pattern for common configs** - Reusable construction recipes.

8. **Test data builders essential** - Simplify test fixture creation.

9. **Immutable builders preferred** - Create new for each use.

10. **One method per field** - Don't create duplicate setters.

### SmartHome Hub Transformation

**Before (Unwieldy Constructor):**
```kotlin
// 15-parameter constructor ❌
class Device(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val name: Name,
    val serialNumber: SerialNumber,
    val type: DeviceType,
    val status: DeviceStatus,
    val health: DeviceHealth,
    val feeds: Map<FeedId, Feed>,
    val os: DeviceOS?,
    val zoneId: ZoneId?,
    val metadata: Map<String, String>,
    val configuration: DeviceConfiguration,
    val createdBy: ActorId,
    val createdAt: Instant,
    val updatedBy: ActorId,
    val updatedAt: Instant
)

// Creating in tests is horrible ❌
val device = Device(
    DeviceId.generate(),
    PremisesId("prem-1"),
    Name("Test"),
    SerialNumber("TEST123"),
    DeviceType.SENSOR,
    DeviceStatus.ACTIVE,
    DeviceHealth.ONLINE,
    emptyMap(),
    null,  // What is this for?
    null,  // And this?
    emptyMap(),
    DeviceConfiguration.default(),
    ActorId("user-1"),
    Instant.now(),
    ActorId("user-1"),
    Instant.now()
)
```

**After (Fluent Builder):**
```kotlin
class DeviceBuilder {
    private var deviceId: DeviceId? = null
    private var premisesId: PremisesId? = null
    private var name: String = "Default Device"
    private var serialNumber: String? = null
    private var type: DeviceType = DeviceType.SENSOR
    private var status: DeviceStatus = DeviceStatus.PENDING
    private var feeds: MutableMap<FeedId, Feed> = mutableMapOf()
    
    fun withDeviceId(deviceId: DeviceId) = apply { this.deviceId = deviceId }
    fun withPremisesId(premisesId: PremisesId) = apply { this.premisesId = premisesId }
    fun withName(name: String) = apply { this.name = name }
    fun withSerialNumber(serialNumber: String) = apply { this.serialNumber = serialNumber }
    fun withType(type: DeviceType) = apply { this.type = type }
    fun withStatus(status: DeviceStatus) = apply { this.status = status }
    
    fun build(): Device {
        require(premisesId != null) { "PremisesId is required" }
        require(serialNumber != null) { "Serial number is required" }
        
        return Device(
            deviceId = deviceId ?: DeviceId.generate(),
            premisesId = premisesId!!,
            name = Name(name),
            serialNumber = SerialNumber(serialNumber!!),
            type = type,
            status = status,
            // ... sensible defaults for rest
        )
    }
}

// Creating in tests is beautiful ✅
val device = DeviceBuilder()
    .withPremisesId(premisesId)
    .withName("Living Room Sensor")
    .withSerialNumber("LR-001")
    .withType(DeviceType.SENSOR)
    .build()  // Clear! ✅
```

**Impact:**
- Clear, self-documenting code
- Easy to add optional parameters
- Validation prevents invalid objects
- Tests become readable
- 70% less code in tests

### Fluent Builder Patterns

**1. Simple Builder:**
```kotlin
class DeviceBuilder {
    private var name: String = ""
    
    fun withName(name: String) = apply { this.name = name }
    
    fun build(): Device {
        require(name.isNotBlank()) { "Name required" }
        return Device(name)
    }
}

// Usage
val device = DeviceBuilder()
    .withName("Sensor")
    .build()
```

**2. Step Builder (Type-Safe):**
```kotlin
class DeviceBuilder private constructor() {
    class DeviceIdStep(private val deviceId: DeviceId) {
        fun withPremisesId(premisesId: PremisesId) = 
            PremisesIdStep(deviceId, premisesId)
    }
    
    class PremisesIdStep(
        private val deviceId: DeviceId,
        private val premisesId: PremisesId
    ) {
        fun withName(name: String) = 
            NameStep(deviceId, premisesId, name)
    }
    
    class NameStep(
        private val deviceId: DeviceId,
        private val premisesId: PremisesId,
        private val name: String
    ) {
        fun build() = Device(deviceId, premisesId, Name(name))
    }
    
    companion object {
        fun create(deviceId: DeviceId) = DeviceIdStep(deviceId)
    }
}

// Compiler enforces correct order ✅
val device = DeviceBuilder.create(deviceId)
    .withPremisesId(premisesId)
    .withName("Sensor")
    .build()

// Won't compile if you skip a step ✅
```

**3. Nested Builders:**
```kotlin
class AutomationBuilder {
    fun addTrigger(block: TriggerBuilder.() -> Unit) = apply {
        val trigger = TriggerBuilder().apply(block).build()
        triggers.add(trigger)
    }
    
    fun addAction(block: ActionBuilder.() -> Unit) = apply {
        val action = ActionBuilder().apply(block).build()
        actions.add(action)
    }
}

// Usage with nested DSL
val automation = AutomationBuilder()
    .withName("Temperature Alert")
    .addTrigger {
        withDeviceId(deviceId)
        withFeedId(tempFeedId)
        withCondition(GreaterThan(25.0))
    }
    .addAction {
        withType(ActionType.SEND_NOTIFICATION)
        withMessage("Temperature high!")
    }
    .build()
```

### Validation Strategies

**1. Validate in build():**
```kotlin
fun build(): Device {
    require(premisesId != null) { "PremisesId is required" }
    require(serialNumber != null) { "Serial number is required" }
    require(serialNumber!!.length in 8..20) { 
        "Serial number must be 8-20 characters" 
    }
    
    return Device(...)
}
```

**2. Validate immediately:**
```kotlin
fun withSerialNumber(serialNumber: String) = apply {
    require(serialNumber.length in 8..20) {
        "Serial number must be 8-20 characters"
    }
    this.serialNumber = serialNumber
}
```

**3. Collect errors:**
```kotlin
fun build(): Device {
    val errors = mutableListOf<String>()
    
    if (premisesId == null) errors.add("PremisesId is required")
    if (serialNumber == null) errors.add("Serial number is required")
    if (name.isBlank()) errors.add("Name cannot be blank")
    
    if (errors.isNotEmpty()) {
        throw ValidationException("Invalid device: ${errors.joinToString()}")
    }
    
    return Device(...)
}
```

### Director Pattern

**Common Configurations:**
```kotlin
class DeviceDirector(private val builder: DeviceBuilder) {
    
    fun buildBasicSensor(
        premisesId: PremisesId,
        serialNumber: String
    ): Device {
        return builder
            .withPremisesId(premisesId)
            .withSerialNumber(serialNumber)
            .withType(DeviceType.SENSOR)
            .withStatus(DeviceStatus.PENDING)
            .build()
    }
    
    fun buildAdvancedActuator(
        premisesId: PremisesId,
        serialNumber: String,
        zoneId: ZoneId
    ): Device {
        return builder
            .withPremisesId(premisesId)
            .withSerialNumber(serialNumber)
            .withType(DeviceType.ACTUATOR)
            .withStatus(DeviceStatus.ACTIVE)
            .withZoneId(zoneId)
            .withConfiguration(DeviceConfiguration.advanced())
            .build()
    }
}

// Usage
val director = DeviceDirector(DeviceBuilder())
val sensor = director.buildBasicSensor(premisesId, "SN-001")
val actuator = director.buildAdvancedActuator(premisesId, "SN-002", zoneId)
```

### Test Data Builders

**Object Mother Pattern:**
```kotlin
object DeviceFixtures {
    fun aDevice(block: DeviceBuilder.() -> Unit = {}): Device {
        return DeviceBuilder()
            .withDeviceId(DeviceId.generate())
            .withPremisesId(PremisesId("test-premises"))
            .withName("Test Device")
            .withSerialNumber("TEST-${UUID.randomUUID()}")
            .withType(DeviceType.SENSOR)
            .apply(block)
            .build()
    }
    
    fun anActiveDevice() = aDevice {
        withStatus(DeviceStatus.ACTIVE)
    }
    
    fun aSensorWithFeeds(feedCount: Int) = aDevice {
        withType(DeviceType.SENSOR)
        repeat(feedCount) { index ->
            addFeed(FeedFixtures.aFeed { withName("Feed-$index") })
        }
    }
}

// Usage in tests
@Test
fun `should activate device`() {
    val device = DeviceFixtures.aDevice {
        withStatus(DeviceStatus.PENDING)
    }
    
    val activated = device.activate()
    
    assertEquals(DeviceStatus.ACTIVE, activated.status)
}
```

### Common Pitfalls Avoided

**1. Mutable Builder Reuse:**
```kotlin
// ❌ Don't reuse builders
val builder = DeviceBuilder().withName("Sensor")
val device1 = builder.build()
val device2 = builder.build()  // Same object!

// ✅ Create new builder each time
val device1 = DeviceBuilder().withName("Sensor 1").build()
val device2 = DeviceBuilder().withName("Sensor 2").build()
```

**2. No Validation:**
```kotlin
// ❌ Missing validation
fun build() = Device(name, serialNumber)  // Could be invalid!

// ✅ Validate before creating
fun build(): Device {
    require(name.isNotBlank()) { "Name required" }
    require(serialNumber != null) { "Serial number required" }
    return Device(name, serialNumber!!)
}
```

**3. Too Many Builders:**
```kotlin
// ❌ Don't create builders for simple objects
class EmailBuilder {
    fun withValue(value: String) = apply { this.value = value }
    fun build() = Email(value)  // Overkill!
}

// ✅ Just use constructor for simple objects
val email = Email("test@example.com")
```

### Testing Strategies

**Builder Test:**
```kotlin
@Test
fun `should build device with all fields`() {
    val device = DeviceBuilder()
        .withDeviceId(deviceId)
        .withPremisesId(premisesId)
        .withName("Test Device")
        .withSerialNumber("TEST123")
        .withType(DeviceType.SENSOR)
        .build()
    
    assertEquals(deviceId, device.deviceId)
    assertEquals(premisesId, device.premisesId)
    assertEquals("Test Device", device.getName().value)
}

@Test
fun `should validate required fields`() {
    assertThrows<IllegalArgumentException> {
        DeviceBuilder()
            .withName("Test")
            // Missing required premisesId
            .build()
    }
}

@Test
fun `should use defaults for optional fields`() {
    val device = DeviceBuilder()
        .withPremisesId(premisesId)
        .withSerialNumber("TEST123")
        .build()
    
    assertEquals(DeviceStatus.PENDING, device.status)  // Default
    assertEquals(DeviceType.SENSOR, device.type)  // Default
}
```

**Using Builders in Tests:**
```kotlin
@Test
fun `should find active devices`() {
    // Arrange - builders make setup easy
    val activeDevice = DeviceFixtures.anActiveDevice()
    val inactiveDevice = DeviceFixtures.aDevice {
        withStatus(DeviceStatus.INACTIVE)
    }
    
    repository.save(activeDevice)
    repository.save(inactiveDevice)
    
    // Act
    val devices = repository.findAll(ActiveDeviceSpec())
    
    // Assert
    assertEquals(1, devices.size)
    assertEquals(activeDevice.deviceId, devices.first().deviceId)
}
```

### Measured Benefits

Teams using builders see:
- **70% reduction** in test setup code
- **Zero invalid objects** (validation prevents)
- **Self-documenting** code (method names clear)
- **Easy refactoring** (add fields without breaking tests)
- **Better readability** (fluent API)
- **Faster test writing** (fixtures ready)

### Practice Exercise

Add builders to your aggregates:

1. **Identify complex aggregates** - 5+ constructor parameters
2. **Create builder class** - One method per field
3. **Add validation** - In build() method
4. **Test the builder** - Required fields, defaults
5. **Create test fixtures** - Object mother pattern
6. **Use in tests** - Refactor existing tests
7. **Measure improvement** - Lines of code before/after

### Design Checklist

When implementing builders:
- ✅ Fluent API (return `this` or `apply`)
- ✅ Validation in build() method
- ✅ Sensible defaults for optional fields
- ✅ Clear error messages
- ✅ One method per field (no duplicates)
- ✅ Immutable builder (don't reuse)
- ✅ Test fixtures using builders
- ✅ Director for common configurations
- ✅ Step builder for required order
- ✅ Documentation for complex builders

---

### Additional Reading

For deeper understanding of builders:
- **"Effective Java"** by Joshua Bloch (2018) - Builder pattern chapter
- **"Design Patterns"** by Gang of Four (1994) - Original builder pattern
- **"Growing Object-Oriented Software, Guided by Tests"** by Freeman & Pryce (2009) - Test data builders

---

## What's Next

In **Chapter 16**, we'll explore Ubiquitous Language—creating a shared vocabulary between developers and domain experts. You'll learn:
- What ubiquitous language is and why it matters
- Creating domain dictionaries
- Naming conventions that reflect business
- Code that speaks the domain language
- Real terminology from SmartHome Hub
- Evolving language as domain understanding grows

With builders simplifying object creation, ubiquitous language will ensure your code speaks the business domain clearly.

Turn the page to master Ubiquitous Language...

---

*"The Builder pattern is especially useful when creating complex objects with many optional parameters."  
— Effective Java, Joshua Bloch*

