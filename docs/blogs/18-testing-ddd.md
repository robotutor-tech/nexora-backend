# Chapter 18: Testing DDD Applications - Unit, Integration, and Domain Tests

**Series:** Building an Enterprise IoT Platform with DDD  
**Part:** 5 of 20 - Real-World Implementation  
**Reading Time:** 24 minutes  
**Level:** Intermediate to Advanced  

---

## 📋 Table of Contents

1. [The Problem: Testing Complexity](#the-problem)
2. [Testing Strategy for DDD](#testing-strategy)
3. [Testing Value Objects](#testing-value-objects)
4. [Testing Entities and Aggregates](#testing-entities)
5. [Testing Domain Events](#testing-events)
6. [Testing Specifications and Policies](#testing-specifications)
7. [Testing Repositories](#testing-repositories)
8. [Integration Testing](#integration-testing)
9. [Test Data Builders](#test-data-builders)

---

## <a name="the-problem"></a>1. The Problem: Testing Complexity

### The Scenario: The Testing Nightmare

You've refactored to DDD, but now tests are failing:

> **Developer:** "I can't test anything! Entities need value objects, value objects need validation, aggregates need events, events need publishers... It's a mess!" 💥

You look at the test file:

```kotlin
// Attempting to test Device entity ❌
class DeviceTest {
    
    @Test
    fun `should activate device`() {
        // How do I create a Device?! 💥
        
        // Need DeviceId
        val deviceId = DeviceId(???)  // What format?
        
        // Need Name
        val name = Name(???)  // What's valid?
        
        // Need SerialNumber
        val serial = SerialNumber(???)  // What's the format?
        
        // Need Feeds
        val feeds = ???  // How do I create feeds?
        
        // Need PremisesId
        val premisesId = PremisesId(???)
        
        // After 50 lines of setup, finally test!
        val device = Device(deviceId, premisesId, name, serial, ...)
        val activated = device.activate()
        
        assertEquals(DeviceStatus.ACTIVE, activated.getStatus())
    }
}
```

### The Questions

- **How do I create test objects?** Too many dependencies
- **How do I test invariants?** They're protected
- **How do I test events?** They're internal
- **How do I test aggregates?** Complex object graphs
- **How do I avoid duplication?** Setup code everywhere

---

## <a name="testing-strategy"></a>2. Testing Strategy for DDD

### The Testing Pyramid for DDD

```
                    ▲
                   ╱ ╲
                  ╱   ╲
                 ╱  E2E ╲          Few, slow, fragile
                ╱───────╲
               ╱         ╲
              ╱Integration╲        Some, medium speed
             ╱─────────────╲
            ╱               ╲
           ╱  Unit (Domain) ╲      Many, fast, reliable
          ╱─────────────────╲
         ╱___________________╲
```

### What to Test at Each Level

**Unit Tests (70%):**
- Value objects validation
- Entity behavior
- Aggregate invariants
- Domain events
- Specifications
- Policies

**Integration Tests (25%):**
- Repositories
- Event handlers
- Use cases
- Database interactions

**E2E Tests (5%):**
- Complete user flows
- API endpoints
- Cross-boundary interactions

---

## <a name="testing-value-objects"></a>3. Testing Value Objects

### Testing Validation

```kotlin
// Value object with validation
@JvmInline
value class SerialNumber(val value: String) {
    init {
        require(value.matches(Regex("SN\\d{6}"))) {
            "Serial number must match format SN######"
        }
    }
}

// Test validation ✅
class SerialNumberTest {
    
    @Test
    fun `should create valid serial number`() {
        // Valid format
        val serial = SerialNumber("SN123456")
        
        assertEquals("SN123456", serial.value)
    }
    
    @Test
    fun `should reject serial without SN prefix`() {
        assertThrows<IllegalArgumentException> {
            SerialNumber("123456")
        }
    }
    
    @Test
    fun `should reject serial with wrong number of digits`() {
        assertThrows<IllegalArgumentException> {
            SerialNumber("SN12345")  // Only 5 digits
        }
        
        assertThrows<IllegalArgumentException> {
            SerialNumber("SN1234567")  // 7 digits
        }
    }
    
    @Test
    fun `should reject serial with letters after SN`() {
        assertThrows<IllegalArgumentException> {
            SerialNumber("SNABCDEF")
        }
    }
    
    @Test
    fun `should reject blank serial`() {
        assertThrows<IllegalArgumentException> {
            SerialNumber("")
        }
    }
}
```

### Testing Value Object Equality

```kotlin
data class Name(val value: String) {
    init {
        require(value.isNotBlank()) { "Name cannot be blank" }
        require(value.length <= 100) { "Name too long" }
    }
}

class NameTest {
    
    @Test
    fun `names with same value should be equal`() {
        val name1 = Name("Living Room Sensor")
        val name2 = Name("Living Room Sensor")
        
        assertEquals(name1, name2)
        assertEquals(name1.hashCode(), name2.hashCode())
    }
    
    @Test
    fun `names with different values should not be equal`() {
        val name1 = Name("Living Room Sensor")
        val name2 = Name("Kitchen Sensor")
        
        assertNotEquals(name1, name2)
    }
}
```

### Testing Value Object Behavior

```kotlin
data class Temperature(val celsius: Double) {
    init {
        require(celsius >= -273.15) { "Temperature below absolute zero" }
    }
    
    val fahrenheit: Double
        get() = celsius * 9.0 / 5.0 + 32.0
    
    val kelvin: Double
        get() = celsius + 273.15
    
    fun isFreezing(): Boolean = celsius <= 0.0
    fun isBoiling(): Boolean = celsius >= 100.0
}

class TemperatureTest {
    
    @Test
    fun `should convert to fahrenheit`() {
        val temp = Temperature(20.0)
        
        assertEquals(68.0, temp.fahrenheit, 0.01)
    }
    
    @Test
    fun `should convert to kelvin`() {
        val temp = Temperature(20.0)
        
        assertEquals(293.15, temp.kelvin, 0.01)
    }
    
    @Test
    fun `should detect freezing temperature`() {
        val freezing = Temperature(0.0)
        val belowFreezing = Temperature(-5.0)
        val aboveFreezing = Temperature(5.0)
        
        assertTrue(freezing.isFreezing())
        assertTrue(belowFreezing.isFreezing())
        assertFalse(aboveFreezing.isFreezing())
    }
    
    @Test
    fun `should detect boiling temperature`() {
        val boiling = Temperature(100.0)
        val aboveBoiling = Temperature(105.0)
        val belowBoiling = Temperature(95.0)
        
        assertTrue(boiling.isBoiling())
        assertTrue(aboveBoiling.isBoiling())
        assertFalse(belowBoiling.isBoiling())
    }
}
```

---

## <a name="testing-entities"></a>4. Testing Entities and Aggregates

### Testing Entity Behavior

```kotlin
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
        
        return copy(status = DeviceStatus.ACTIVE)
    }
}

// Test entity behavior ✅
class DeviceTest {
    
    @Test
    fun `should activate pending device with feeds`() {
        // Given - pending device with feeds
        val device = createTestDevice(
            status = DeviceStatus.PENDING,
            feeds = mapOf(
                FeedId("feed-1") to createTestFeed()
            )
        )
        
        // When - activate
        val activated = device.activate()
        
        // Then - status changed
        assertEquals(DeviceStatus.ACTIVE, activated.getStatus())
    }
    
    @Test
    fun `should not activate device without feeds`() {
        // Given - device without feeds
        val device = createTestDevice(
            status = DeviceStatus.PENDING,
            feeds = emptyMap()
        )
        
        // When/Then - activation fails
        val exception = assertThrows<IllegalArgumentException> {
            device.activate()
        }
        
        assertEquals("Device must have at least one feed", exception.message)
    }
    
    @Test
    fun `should not activate already active device`() {
        // Given - already active device
        val device = createTestDevice(
            status = DeviceStatus.ACTIVE,
            feeds = mapOf(FeedId("feed-1") to createTestFeed())
        )
        
        // When/Then - activation fails
        assertThrows<IllegalArgumentException> {
            device.activate()
        }
    }
}
```

### Testing Aggregate Invariants

```kotlin
class Device {
    companion object {
        private const val MAX_FEEDS = 10
    }
    
    fun addFeed(feed: Feed): Device {
        require(feeds.size < MAX_FEEDS) {
            "Device can have maximum $MAX_FEEDS feeds"
        }
        require(!feeds.containsKey(feed.feedId)) {
            "Feed ${feed.feedId} already exists"
        }
        
        val newFeeds = feeds.toMutableMap()
        newFeeds[feed.feedId] = feed
        
        return copy(feeds = newFeeds)
    }
}

class DeviceInvariantTest {
    
    @Test
    fun `should enforce maximum feeds limit`() {
        // Given - device with 10 feeds (max)
        var device = createTestDevice(feeds = emptyMap())
        
        repeat(10) { index ->
            val feed = createTestFeed(feedId = FeedId("feed-$index"))
            device = device.addFeed(feed)
        }
        
        assertEquals(10, device.getFeeds().size)
        
        // When/Then - adding 11th feed fails
        val exception = assertThrows<IllegalArgumentException> {
            device.addFeed(createTestFeed(feedId = FeedId("feed-11")))
        }
        
        assertEquals("Device can have maximum 10 feeds", exception.message)
    }
    
    @Test
    fun `should prevent duplicate feed IDs`() {
        // Given - device with one feed
        val feedId = FeedId("feed-1")
        val device = createTestDevice(
            feeds = mapOf(feedId to createTestFeed(feedId))
        )
        
        // When/Then - adding duplicate fails
        assertThrows<IllegalArgumentException> {
            device.addFeed(createTestFeed(feedId))
        }
    }
}
```

### Testing Immutability

```kotlin
class DeviceImmutabilityTest {
    
    @Test
    fun `activating device should return new instance`() {
        // Given
        val original = createTestDevice(
            status = DeviceStatus.PENDING,
            feeds = mapOf(FeedId("feed-1") to createTestFeed())
        )
        
        // When
        val activated = original.activate()
        
        // Then - different instances
        assertNotSame(original, activated)
        
        // Original unchanged
        assertEquals(DeviceStatus.PENDING, original.getStatus())
        
        // New instance has new status
        assertEquals(DeviceStatus.ACTIVE, activated.getStatus())
    }
    
    @Test
    fun `adding feed should return new instance`() {
        // Given
        val original = createTestDevice(feeds = emptyMap())
        val feed = createTestFeed()
        
        // When
        val updated = original.addFeed(feed)
        
        // Then - different instances
        assertNotSame(original, updated)
        
        // Original unchanged
        assertEquals(0, original.getFeeds().size)
        
        // New instance has feed
        assertEquals(1, updated.getFeeds().size)
    }
}
```

---

## <a name="testing-events"></a>5. Testing Domain Events

### Testing Event Publishing

```kotlin
class Device {
    private val _domainEvents = mutableListOf<DeviceEvent>()
    val domainEvents: List<DeviceEvent> get() = _domainEvents.toList()
    
    fun activate(): Device {
        val activated = copy(status = DeviceStatus.ACTIVE)
        activated.addDomainEvent(
            DeviceActivated(deviceId, Instant.now())
        )
        return activated
    }
}

class DeviceEventTest {
    
    @Test
    fun `should publish DeviceActivated event when activated`() {
        // Given
        val device = createTestDevice(
            status = DeviceStatus.PENDING,
            feeds = mapOf(FeedId("feed-1") to createTestFeed())
        )
        
        // When
        val activated = device.activate()
        
        // Then - event published
        val events = activated.domainEvents
        assertEquals(1, events.size)
        
        val event = events[0]
        assertTrue(event is DeviceActivated)
        assertEquals(device.deviceId, (event as DeviceActivated).deviceId)
    }
    
    @Test
    fun `should publish multiple events for multiple operations`() {
        // Given
        var device = createTestDevice(feeds = emptyMap())
        
        // When - multiple operations
        val feed1 = createTestFeed(FeedId("feed-1"))
        device = device.addFeed(feed1)
        
        val feed2 = createTestFeed(FeedId("feed-2"))
        device = device.addFeed(feed2)
        
        device = device.activate()
        
        // Then - multiple events
        val events = device.domainEvents
        assertEquals(3, events.size)
        
        assertTrue(events[0] is FeedAdded)
        assertTrue(events[1] is FeedAdded)
        assertTrue(events[2] is DeviceActivated)
    }
    
    @Test
    fun `should clear events after retrieval`() {
        // Given
        val device = createTestDevice(
            status = DeviceStatus.PENDING,
            feeds = mapOf(FeedId("feed-1") to createTestFeed())
        )
        val activated = device.activate()
        
        // When - get events
        val events = activated.domainEvents
        assertEquals(1, events.size)
        
        // Clear events
        activated.clearDomainEvents()
        
        // Then - no events
        assertEquals(0, activated.domainEvents.size)
    }
}
```

### Testing Event Handlers

```kotlin
@ComponentInline
class DeviceActivatedNotificationHandler(
    private val notificationService: NotificationService
) {
    @EventListener
    fun handle(event: DeviceActivated) {
        notificationService.sendDeviceActivated(event.deviceId)
    }
}

class DeviceActivatedNotificationHandlerTest {
    
    private lateinit var handler: DeviceActivatedNotificationHandler
    private lateinit var notificationService: NotificationService
    
    @BeforeEach
    fun setup() {
        notificationService = mockk()
        handler = DeviceActivatedNotificationHandler(notificationService)
    }
    
    @Test
    fun `should send notification when device activated`() {
        // Given
        val event = DeviceActivated(
            deviceId = DeviceId("device-123"),
            activatedAt = Instant.now()
        )
        
        every { notificationService.sendDeviceActivated(any()) } just Runs
        
        // When
        handler.handle(event)
        
        // Then
        verify { notificationService.sendDeviceActivated(DeviceId("device-123")) }
    }
}
```

---

## <a name="testing-specifications"></a>6. Testing Specifications and Policies

### Testing Specifications

```kotlin
class DeviceSpecifications {
    companion object {
        fun withStatus(status: DeviceStatus): Specification<Device> {
            return Specification { device ->
                device.getStatus() == status
            }
        }
        
        fun inPremises(premisesId: PremisesId): Specification<Device> {
            return Specification { device ->
                device.premisesId == premisesId
            }
        }
    }
}

class DeviceSpecificationsTest {
    
    @Test
    fun `should match device with specified status`() {
        // Given
        val spec = DeviceSpecifications.withStatus(DeviceStatus.ACTIVE)
        val activeDevice = createTestDevice(status = DeviceStatus.ACTIVE)
        val pendingDevice = createTestDevice(status = DeviceStatus.PENDING)
        
        // When/Then
        assertTrue(spec.isSatisfiedBy(activeDevice))
        assertFalse(spec.isSatisfiedBy(pendingDevice))
    }
    
    @Test
    fun `should match device in specified premises`() {
        // Given
        val premisesId = PremisesId("premises-123")
        val spec = DeviceSpecifications.inPremises(premisesId)
        
        val deviceInPremises = createTestDevice(premisesId = premisesId)
        val deviceInOtherPremises = createTestDevice(premisesId = PremisesId("premises-456"))
        
        // When/Then
        assertTrue(spec.isSatisfiedBy(deviceInPremises))
        assertFalse(spec.isSatisfiedBy(deviceInOtherPremises))
    }
    
    @Test
    fun `should combine specifications with AND`() {
        // Given
        val premisesId = PremisesId("premises-123")
        val spec = DeviceSpecifications.inPremises(premisesId)
            .and(DeviceSpecifications.withStatus(DeviceStatus.ACTIVE))
        
        val matchingDevice = createTestDevice(
            premisesId = premisesId,
            status = DeviceStatus.ACTIVE
        )
        
        val wrongPremises = createTestDevice(
            premisesId = PremisesId("other"),
            status = DeviceStatus.ACTIVE
        )
        
        val wrongStatus = createTestDevice(
            premisesId = premisesId,
            status = DeviceStatus.PENDING
        )
        
        // When/Then
        assertTrue(spec.isSatisfiedBy(matchingDevice))
        assertFalse(spec.isSatisfiedBy(wrongPremises))
        assertFalse(spec.isSatisfiedBy(wrongStatus))
    }
}
```

### Testing Policies

```kotlin
class DeviceActivationPolicy : Policy<Device> {
    override fun evaluate(subject: Device): PolicyResult {
        val violations = mutableListOf<String>()
        
        if (subject.getStatus() != DeviceStatus.PENDING) {
            violations.add("Device must be in PENDING status")
        }
        
        if (subject.getFeeds().isEmpty()) {
            violations.add("Device must have at least one feed")
        }
        
        if (subject.getFeeds().size > 10) {
            violations.add("Device cannot have more than 10 feeds")
        }
        
        return if (violations.isEmpty()) {
            PolicyResult.allowed()
        } else {
            PolicyResult.denied(violations)
        }
    }
}

class DeviceActivationPolicyTest {
    
    private val policy = DeviceActivationPolicy()
    
    @Test
    fun `should allow activation of valid device`() {
        // Given - valid device
        val device = createTestDevice(
            status = DeviceStatus.PENDING,
            feeds = mapOf(FeedId("feed-1") to createTestFeed())
        )
        
        // When
        val result = policy.evaluate(device)
        
        // Then
        assertTrue(result.isAllowed)
        assertTrue(result.violations.isEmpty())
    }
    
    @Test
    fun `should deny activation of active device`() {
        // Given - already active
        val device = createTestDevice(
            status = DeviceStatus.ACTIVE,
            feeds = mapOf(FeedId("feed-1") to createTestFeed())
        )
        
        // When
        val result = policy.evaluate(device)
        
        // Then
        assertFalse(result.isAllowed)
        assertTrue(result.violations.contains("Device must be in PENDING status"))
    }
    
    @Test
    fun `should deny activation of device without feeds`() {
        // Given - no feeds
        val device = createTestDevice(
            status = DeviceStatus.PENDING,
            feeds = emptyMap()
        )
        
        // When
        val result = policy.evaluate(device)
        
        // Then
        assertFalse(result.isAllowed)
        assertTrue(result.violations.contains("Device must have at least one feed"))
    }
    
    @Test
    fun `should collect multiple violations`() {
        // Given - multiple violations
        val device = createTestDevice(
            status = DeviceStatus.ACTIVE,  // Wrong status
            feeds = emptyMap()              // No feeds
        )
        
        // When
        val result = policy.evaluate(device)
        
        // Then
        assertFalse(result.isAllowed)
        assertEquals(2, result.violations.size)
    }
}
```

---

## <a name="testing-repositories"></a>7. Testing Repositories

### Testing Repository with In-Memory Implementation

```kotlin
// In-memory repository for testing
class InMemoryDeviceRepository : DeviceRepository {
    private val devices = mutableMapOf<DeviceId, Device>()
    
    override fun findById(deviceId: DeviceId): Device? {
        return devices[deviceId]
    }
    
    override fun save(device: Device): Device {
        devices[device.deviceId] = device
        return device
    }
    
    override fun delete(deviceId: DeviceId) {
        devices.remove(deviceId)
    }
    
    override fun findBySpecification(spec: Specification<Device>): List<Device> {
        return devices.values.filter { spec.isSatisfiedBy(it) }
    }
    
    fun clear() {
        devices.clear()
    }
}

class DeviceRepositoryTest {
    
    private lateinit var repository: InMemoryDeviceRepository
    
    @BeforeEach
    fun setup() {
        repository = InMemoryDeviceRepository()
    }
    
    @Test
    fun `should save and retrieve device`() {
        // Given
        val device = createTestDevice()
        
        // When
        repository.save(device)
        val retrieved = repository.findById(device.deviceId)
        
        // Then
        assertNotNull(retrieved)
        assertEquals(device.deviceId, retrieved?.deviceId)
    }
    
    @Test
    fun `should return null for non-existent device`() {
        // When
        val result = repository.findById(DeviceId("non-existent"))
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun `should update existing device`() {
        // Given - saved device
        val device = createTestDevice(status = DeviceStatus.PENDING)
        repository.save(device)
        
        // When - update device
        val activated = device.activate()
        repository.save(activated)
        
        // Then - updated version retrieved
        val retrieved = repository.findById(device.deviceId)
        assertEquals(DeviceStatus.ACTIVE, retrieved?.getStatus())
    }
    
    @Test
    fun `should delete device`() {
        // Given
        val device = createTestDevice()
        repository.save(device)
        
        // When
        repository.delete(device.deviceId)
        
        // Then
        val retrieved = repository.findById(device.deviceId)
        assertNull(retrieved)
    }
    
    @Test
    fun `should find devices by specification`() {
        // Given - multiple devices
        val premisesId = PremisesId("premises-123")
        val device1 = createTestDevice(
            deviceId = DeviceId("device-1"),
            premisesId = premisesId,
            status = DeviceStatus.ACTIVE
        )
        val device2 = createTestDevice(
            deviceId = DeviceId("device-2"),
            premisesId = premisesId,
            status = DeviceStatus.PENDING
        )
        val device3 = createTestDevice(
            deviceId = DeviceId("device-3"),
            premisesId = PremisesId("other-premises"),
            status = DeviceStatus.ACTIVE
        )
        
        repository.save(device1)
        repository.save(device2)
        repository.save(device3)
        
        // When - find active devices in premises
        val spec = DeviceSpecifications.inPremises(premisesId)
            .and(DeviceSpecifications.withStatus(DeviceStatus.ACTIVE))
        
        val results = repository.findBySpecification(spec)
        
        // Then - only device1 matches
        assertEquals(1, results.size)
        assertEquals(device1.deviceId, results[0].deviceId)
    }
}
```

---

## <a name="integration-testing"></a>8. Integration Testing

### Testing with Real Database

```kotlin
@SpringBootTest
@Testcontainers
class DeviceRepositoryIntegrationTest {
    
    @Container
    val mongodb = MongoDBContainer("mongo:6.0")
    
    @Autowired
    lateinit var repository: DeviceRepository
    
    @Autowired
    lateinit var mongoTemplate: MongoTemplate
    
    @BeforeEach
    fun cleanup() {
        mongoTemplate.dropCollection("devices")
    }
    
    @Test
    fun `should persist and retrieve device from MongoDB`() {
        // Given
        val device = createTestDevice()
        
        // When
        repository.save(device)
        val retrieved = repository.findById(device.deviceId)
        
        // Then
        assertNotNull(retrieved)
        assertEquals(device.deviceId, retrieved?.deviceId)
        assertEquals(device.getName(), retrieved?.getName())
        assertEquals(device.getStatus(), retrieved?.getStatus())
    }
    
    @Test
    fun `should persist complex device with feeds`() {
        // Given - device with multiple feeds
        val device = createTestDevice(
            feeds = mapOf(
                FeedId("feed-1") to createTestFeed(FeedId("feed-1")),
                FeedId("feed-2") to createTestFeed(FeedId("feed-2"))
            )
        )
        
        // When
        repository.save(device)
        val retrieved = repository.findById(device.deviceId)
        
        // Then
        assertNotNull(retrieved)
        assertEquals(2, retrieved?.getFeeds()?.size)
    }
}
```

### Testing Use Cases

```kotlin
@SpringBootTest
class ActivateDeviceUseCaseIntegrationTest {
    
    @Autowired
    lateinit var useCase: ActivateDeviceUseCase
    
    @Autowired
    lateinit var deviceRepository: DeviceRepository
    
    @MockBean
    lateinit var eventPublisher: DomainEventPublisher
    
    @BeforeEach
    fun setup() {
        every { eventPublisher.publish(any()) } just Runs
    }
    
    @Test
    fun `should activate device end-to-end`() {
        // Given - saved device
        val device = createTestDevice(
            status = DeviceStatus.PENDING,
            feeds = mapOf(FeedId("feed-1") to createTestFeed())
        )
        deviceRepository.save(device)
        
        // When - execute use case
        val command = ActivateDeviceCommand(device.deviceId)
        useCase.execute(command)
        
        // Then - device activated
        val activated = deviceRepository.findById(device.deviceId)
        assertEquals(DeviceStatus.ACTIVE, activated?.getStatus())
        
        // Event published
        verify { eventPublisher.publish(ofType<DeviceActivated>()) }
    }
}
```

---

## <a name="test-data-builders"></a>9. Test Data Builders

### Creating Test Data Builders

```kotlin
// Test data builder for Device
object TestDeviceBuilder {
    
    fun aDevice(
        deviceId: DeviceId = DeviceId.generate(),
        premisesId: PremisesId = PremisesId("test-premises"),
        name: Name = Name("Test Device"),
        serialNumber: SerialNumber = SerialNumber("SN123456"),
        status: DeviceStatus = DeviceStatus.PENDING,
        feeds: Map<FeedId, Feed> = emptyMap()
    ): Device {
        return createDevice(
            deviceId = deviceId,
            premisesId = premisesId,
            name = name,
            serialNumber = serialNumber,
            status = status,
            feeds = feeds
        )
    }
    
    fun anActiveDevice(
        premisesId: PremisesId = PremisesId("test-premises")
    ): Device {
        return aDevice(
            premisesId = premisesId,
            status = DeviceStatus.ACTIVE,
            feeds = mapOf(FeedId("feed-1") to aFeed())
        )
    }
    
    fun aDeviceWithFeeds(
        feedCount: Int = 3,
        premisesId: PremisesId = PremisesId("test-premises")
    ): Device {
        val feeds = (1..feedCount).associate { index ->
            val feedId = FeedId("feed-$index")
            feedId to aFeed(feedId)
        }
        
        return aDevice(premisesId = premisesId, feeds = feeds)
    }
    
    fun aFeed(
        feedId: FeedId = FeedId.generate(),
        name: String = "Test Feed",
        type: FeedType = FeedType.SENSOR,
        value: Int = 0
    ): Feed {
        return Feed.create(feedId, name, type, value)
    }
}

// Usage in tests ✅
class DeviceTestWithBuilder {
    
    @Test
    fun `should activate device`() {
        // Simple!
        val device = TestDeviceBuilder.aDevice(
            status = DeviceStatus.PENDING,
            feeds = mapOf(FeedId("feed-1") to TestDeviceBuilder.aFeed())
        )
        
        val activated = device.activate()
        
        assertEquals(DeviceStatus.ACTIVE, activated.getStatus())
    }
    
    @Test
    fun `should work with active device`() {
        // Even simpler!
        val device = TestDeviceBuilder.anActiveDevice()
        
        assertTrue(device.isActive())
    }
    
    @Test
    fun `should handle device with multiple feeds`() {
        // Super simple!
        val device = TestDeviceBuilder.aDeviceWithFeeds(feedCount = 5)
        
        assertEquals(5, device.getFeeds().size)
    }
}
```

### Mother Object Pattern

```kotlin
// Mother objects for common test scenarios
object DeviceMother {
    
    fun pendingDeviceReadyForActivation(): Device {
        return TestDeviceBuilder.aDevice(
            status = DeviceStatus.PENDING,
            feeds = mapOf(
                FeedId("feed-1") to TestDeviceBuilder.aFeed(
                    type = FeedType.TEMPERATURE
                )
            )
        )
    }
    
    fun fullyConfiguredDevice(): Device {
        return TestDeviceBuilder.aDevice(
            status = DeviceStatus.ACTIVE,
            feeds = mapOf(
                FeedId("temp") to TestDeviceBuilder.aFeed(
                    name = "Temperature",
                    type = FeedType.TEMPERATURE
                ),
                FeedId("humidity") to TestDeviceBuilder.aFeed(
                    name = "Humidity",
                    type = FeedType.HUMIDITY
                )
            )
        )
    }
    
    fun deviceAtMaximumFeeds(): Device {
        return TestDeviceBuilder.aDeviceWithFeeds(feedCount = 10)
    }
}

// Usage
class DeviceMotherTest {
    
    @Test
    fun `should activate pending device ready for activation`() {
        val device = DeviceMother.pendingDeviceReadyForActivation()
        
        val activated = device.activate()
        
        assertEquals(DeviceStatus.ACTIVE, activated.getStatus())
    }
    
    @Test
    fun `should not exceed maximum feeds`() {
        val device = DeviceMother.deviceAtMaximumFeeds()
        
        assertThrows<IllegalArgumentException> {
            device.addFeed(TestDeviceBuilder.aFeed())
        }
    }
}
```

---

## 💡 Key Takeaways

1. **Test pyramid matters** - Many unit tests, some integration, few E2E

2. **Test value objects thoroughly** - Validation is critical

3. **Test entity behavior** - Not just data

4. **Test invariants** - Aggregates must protect their rules

5. **Test events** - Ensure they're published correctly

6. **Use test builders** - Simplify test setup

7. **In-memory repositories** - Fast unit tests

8. **Real databases for integration** - Use Testcontainers

9. **Test data builders** - Reusable test fixtures

10. **Mother objects** - Common test scenarios

---

## 🎯 Practical Exercise

Improve your test suite:

1. **Calculate test coverage** - Aim for 80%+ on domain
2. **Create test builders** - For all aggregates
3. **Add value object tests** - Test all validation
4. **Test all invariants** - Aggregate rules must hold
5. **Test event publishing** - Verify all events
6. **Add integration tests** - Critical paths
7. **Measure improvement** - Speed, reliability, coverage

---

## 📚 What We've Covered

In this chapter, you learned:

✅ Testing strategy for DDD applications  
✅ Testing value objects with validation  
✅ Testing entities and aggregates  
✅ Testing domain events  
✅ Testing specifications and policies  
✅ Testing repositories (in-memory and real)  
✅ Integration testing strategies  
✅ Test data builders and mother objects  
✅ Complete test examples  

---

## 🚀 Next Chapter

Ready for performance optimization?

👉 **[Chapter 19: Performance Considerations in DDD](./19-performance-in-ddd.md)**

**You'll learn:**
- Performance bottlenecks in DDD
- Optimizing aggregates
- Caching strategies
- Query optimization
- Event sourcing performance

**Reading Time:** 22 minutes  
**Difficulty:** Advanced  

---

**Questions?** Open an issue in the repository!

**Found this helpful?** Star and share with your team!

---

*"Testing shows the presence, not the absence of bugs."  
— Edsger W. Dijkstra*

