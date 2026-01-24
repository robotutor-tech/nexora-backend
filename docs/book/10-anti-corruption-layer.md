# Chapter 10
# Anti-Corruption Layer
## Protecting Your Domain

> *"An Anti-Corruption Layer is a translator that protects your domain model from the corrupting influence of external models."*  
> — Eric Evans, Domain-Driven Design

---

## In This Chapter

External systems—whether legacy, third-party, or poorly designed—threaten to pollute your clean domain model with their complexity and inconsistencies. The Anti-Corruption Layer (ACL) pattern provides a protective barrier, translating external models into your domain language and preventing corruption from spreading.

**What You'll Learn:**
- The external system pollution problem
- What an Anti-Corruption Layer is and how it works
- When to use ACL (legacy systems, external APIs, multiple providers)
- Implementation patterns: Adapter, Translator, Facade
- Real legacy system integration from SmartHome Hub
- Handling multiple protocols (REST, SOAP, gRPC)
- Caching and retry strategies in ACL
- Testing ACL with contract tests
- Making external systems replaceable

---

## Table of Contents

1. The Problem: External System Pollution
2. What is an Anti-Corruption Layer?
3. When to Use ACL
4. ACL Implementation Patterns
5. Real-World Examples from SmartHome Hub
6. Adapter Pattern in ACL
7. Translator Pattern
8. Facade Pattern
9. Testing ACL
10. Chapter Summary

---

## 1. The Problem: External System Pollution

### The Scenario: The Legacy System Invasion

You're integrating SmartHome Hub with a legacy device management system when disaster strikes:

> **Code Review Alert:** "External system's complex model has leaked into our domain. 200+ lines of integration code scattered across 15 files. Domain entities now have 50+ fields from external system. Our clean domain is polluted!"

You investigate and find contamination everywhere:

```kotlin
// External legacy system's model (horrible!) ❌
data class LegacyDeviceResponse(
    val dev_sno: String,              // Device serial number
    val dev_nm: String,               // Device name
    val dev_st: Int,                  // Status: 0=off, 1=on, 2=error, 3=unknown
    val dev_tp: String,               // Type: "S"=sensor, "A"=actuator
    val loc_cd: String?,              // Location code
    val own_id: String,               // Owner ID
    val reg_dt: Long,                 // Registration timestamp (milliseconds)
    val lst_cn_dt: Long?,             // Last connection timestamp
    val meta: Map<String, Any>?,      // Metadata (inconsistent structure)
    val feeds: List<LegacyFeedData>?, // Feeds (nullable, inconsistent)
    val tags: String?,                // Comma-separated tags
    val prps: String?,                // JSON string of properties (not parsed!)
    // ... 30 more cryptic fields
)

// Our domain entity got polluted! ❌
@Document("devices")
data class Device(
    @Id val id: String,
    val name: String,
    val status: DeviceStatus,
    
    // Legacy pollution starts here! ❌
    val legacyDevSno: String?,        // Why is this here?!
    val legacyDevSt: Int?,            // What does this mean?!
    val legacyLocCd: String?,         // Cryptic!
    val legacyMeta: Map<String, Any>?, // Untyped!
    val legacyTags: String?,          // String instead of List!
    val legacyPrps: String?,          // Unparsed JSON!
    
    // More pollution from another system
    val externalSystemId: String?,
    val externalData: String?,        // Generic string field!
    val syncStatus: String?,          // What values are valid?
    val lastSyncError: String?,
    
    // And more...
    // Our clean 10-field entity now has 40+ fields! 💥
)

// Business logic contaminated with external concerns ❌
@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val legacySystemClient: LegacySystemClient
) {
    fun registerDevice(request: DeviceRegistrationRequest): Device {
        // Our domain logic mixed with legacy translation! ❌
        val device = Device(
            id = UUID.randomUUID().toString(),
            name = request.name,
            status = DeviceStatus.PENDING,
            
            // Translation logic in service! ❌
            legacyDevSno = request.serialNumber,
            legacyDevSt = when (request.status) {
                "ACTIVE" -> 1
                "INACTIVE" -> 0
                else -> 3
            },
            legacyLocCd = translateLocationToLegacyCode(request.location),
            legacyTags = request.tags.joinToString(","),
            // ... 20 more lines of translation
        )
        
        deviceRepository.save(device)
        
        // Sync to legacy system (blocking call!) ❌
        try {
            val legacyResponse = legacySystemClient.createDevice(
                LegacyCreateDeviceRequest(
                    dev_sno = device.legacyDevSno,
                    dev_nm = device.name,
                    dev_st = device.legacyDevSt ?: 3,
                    // ... complex transformation
                )
            )
            
            // Update with legacy data ❌
            device.externalSystemId = legacyResponse.ext_id
            device.syncStatus = "SYNCED"
            deviceRepository.save(device)
        } catch (e: Exception) {
            device.syncStatus = "FAILED"
            device.lastSyncError = e.message
            deviceRepository.save(device)
        }
        
        return device
    }
}
```

### The Contamination Spreads

```kotlin
// Now every part of our code knows about legacy system! ❌

// Use cases polluted
class ActivateDeviceUseCase {
    fun execute(command: ActivateDeviceCommand) {
        val device = deviceRepository.findById(command.deviceId)
        
        // Checking legacy fields in our use case! ❌
        if (device.legacyDevSt == null) {
            throw LegacySystemNotSyncedException()
        }
        
        device.status = DeviceStatus.ACTIVE
        device.legacyDevSt = 1  // Update legacy field
        
        // Call legacy system
        legacySystemClient.updateDeviceStatus(device.legacyDevSno, 1)
        
        deviceRepository.save(device)
    }
}

// Tests polluted
class DeviceTest {
    @Test
    fun `should activate device`() {
        val device = Device(
            id = "device-123",
            name = "Test Device",
            status = DeviceStatus.PENDING,
            
            // Tests now need to know about legacy! ❌
            legacyDevSno = "LEGACY-123",
            legacyDevSt = 0,
            legacyLocCd = "LOC-001",
            // ... 20 more legacy fields to set
        )
        // Test is now coupled to external system!
    }
}
```

### The Real Cost

**Impact measured:**
- 🔴 Domain entities: 10 fields → 40+ fields (4x bloat)
- 🔴 External system downtime breaks our system
- 🔴 Can't change domain model (tied to external system)
- 🔴 Tests require external system knowledge
- 🔴 Can't replace legacy system (too integrated)
- 🔴 Every developer needs to understand cryptic external model
- 🔴 Business logic scattered with translation logic

**Root Cause:** No Anti-Corruption Layer. External complexity leaked into our clean domain.

---

## <a name="what-is-acl"></a>2. What is an Anti-Corruption Layer?

### Definition

> **Anti-Corruption Layer (ACL):** A layer that isolates a model from corrupting influences of external systems by translating between different models and protocols.
> 
> — Eric Evans, Domain-Driven Design

### Core Purpose

The ACL acts as a protective barrier:

```
┌─────────────────────────────────────────────────────┐
│         Your Clean Domain                           │
│                                                     │
│   Device(deviceId, name, status, feeds)            │
│   - Clean model                                     │
│   - Domain language                                 │
│   - Business logic                                  │
│                                                     │
└──────────────────┬──────────────────────────────────┘
                   │
                   │ Protected by ACL
                   │
┌──────────────────▼──────────────────────────────────┐
│    Anti-Corruption Layer (ACL)                      │
│                                                     │
│    - Translates external model to domain model     │
│    - Hides external complexity                      │
│    - Adapts different protocols                     │
│    - Handles errors and retries                     │
│                                                     │
└──────────────────┬──────────────────────────────────┘
                   │
                   │ External interface
                   │
┌──────────────────▼──────────────────────────────────┐
│    External System (Legacy, Complex)                │
│                                                     │
│    LegacyDeviceResponse(dev_sno, dev_st, ...)      │
│    - Cryptic names                                  │
│    - Complex structure                              │
│    - Inconsistent data                              │
│                                                     │
└─────────────────────────────────────────────────────┘
```

### Key Benefits

1. **Domain Isolation** - External changes don't affect domain
2. **Clean Domain Model** - No external pollution
3. **Single Translation Point** - All conversion in one place
4. **Testability** - Can test domain without external system
5. **Replaceability** - Can swap external systems easily

---

## <a name="when-to-use"></a>3. When to Use ACL

### Use ACL When:

#### Scenario 1: Integrating Legacy Systems

```kotlin
// Legacy system with horrible model
interface LegacyInventorySystem {
    fun GET_ITEM_DTL(itm_cd: String): ITEM_DTL_RESP
    fun UPD_ITEM_QTY(itm_cd: String, qty: Int, opr: String): Boolean
}

// Use ACL to protect your domain ✅
```

#### Scenario 2: External APIs You Don't Control

```kotlin
// Third-party weather API
interface WeatherApi {
    fun getCurrentWeather(lat: Double, lon: Double): WeatherResponse
}

// WeatherResponse has 50+ fields, you need 3
// Use ACL to extract what you need ✅
```

#### Scenario 3: Multiple External Systems

```kotlin
// Different device protocols
interface ZigbeeDeviceApi
interface ZWaveDeviceApi  
interface WiFiDeviceApi

// Each has different model
// Use ACL to normalize to your domain model ✅
```

#### Scenario 4: Temporary Integration

```kotlin
// Migrating from old system to new
interface OldSystemApi
interface NewSystemApi

// Use ACL to support both during migration ✅
```

### Don't Use ACL When:

❌ **Integrating with system you control** - Just fix the model  
❌ **Simple, clean external API** - Direct usage is fine  
❌ **Over-engineering** - Don't add complexity unnecessarily  

---

## <a name="implementation-patterns"></a>4. ACL Implementation Patterns

### Pattern 1: Adapter

Adapts external interface to match your domain's expected interface:

```kotlin
// Your domain expects this interface
interface DeviceProvider {
    fun getDevice(deviceId: DeviceId): Device?
    fun saveDevice(device: Device): Device
}

// Adapter translates legacy system to your interface
class LegacyDeviceAdapter(
    private val legacyClient: LegacyDeviceClient
) : DeviceProvider {
    
    override fun getDevice(deviceId: DeviceId): Device? {
        val legacyResponse = legacyClient.getDeviceDetails(deviceId.value)
            ?: return null
        
        return translateToDomain(legacyResponse)
    }
    
    override fun saveDevice(device: Device): Device {
        val legacyRequest = translateToLegacy(device)
        val legacyResponse = legacyClient.updateDevice(legacyRequest)
        return translateToDomain(legacyResponse)
    }
    
    private fun translateToDomain(legacy: LegacyDeviceResponse): Device {
        // Translation logic isolated here
    }
    
    private fun translateToLegacy(device: Device): LegacyDeviceRequest {
        // Translation logic isolated here
    }
}
```

### Pattern 2: Facade

Simplifies complex external system:

```kotlin
// Complex external system with many operations
interface ComplexExternalSystem {
    fun operation1(p1: String, p2: Int): Response1
    fun operation2(p1: Map<String, Any>): Response2
    fun operation3(p1: List<String>, p2: Boolean, p3: Long): Response3
    // ... 50 more operations
}

// Facade provides simple interface for your domain
class ExternalSystemFacade(
    private val externalSystem: ComplexExternalSystem,
    private val cache: CacheService
) {
    fun syncDevice(device: Device): SyncResult {
        // Orchestrates multiple operations
        val resp1 = externalSystem.operation1(device.id, 1)
        val resp2 = externalSystem.operation2(mapOf("id" to device.id))
        
        // Caches results
        cache.put("device:${device.id}", resp1)
        
        // Returns simple domain result
        return SyncResult.success()
    }
}
```

### Pattern 3: Translator

Converts between different models:

```kotlin
// Translator for bidirectional conversion
class DeviceTranslator {
    
    fun toDomain(external: ExternalDevice): Device {
        return Device(
            deviceId = DeviceId(external.id),
            name = Name(external.deviceName),
            status = translateStatus(external.statusCode),
            type = translateType(external.typeString),
            feeds = external.dataPoints?.map { translateFeed(it) } ?: emptyList()
        )
    }
    
    fun toExternal(domain: Device): ExternalDevice {
        return ExternalDevice(
            id = domain.deviceId.value,
            deviceName = domain.getName().value,
            statusCode = translateStatusToCode(domain.getStatus()),
            typeString = translateTypeToString(domain.getType()),
            dataPoints = domain.getFeeds().map { translateFeedToExternal(it) }
        )
    }
    
    private fun translateStatus(code: Int): DeviceStatus {
        return when (code) {
            0 -> DeviceStatus.INACTIVE
            1 -> DeviceStatus.ACTIVE
            2 -> DeviceStatus.ERROR
            else -> DeviceStatus.UNKNOWN
        }
    }
}
```

---

## <a name="real-examples"></a>5. Real-World Examples from SmartHome Hub

### Example 1: Legacy Device System Integration

```kotlin
// External legacy system (horrible model)
data class LegacyDeviceData(
    val dev_sno: String,
    val dev_nm: String,
    val dev_st: Int,
    val dev_tp: String,
    val loc_cd: String?,
    val own_id: String,
    val reg_dt: Long,
    val feeds: List<Map<String, Any>>?
)

interface LegacyDeviceClient {
    fun getDevice(serialNumber: String): LegacyDeviceData?
    fun createDevice(request: Map<String, Any>): LegacyDeviceData
    fun updateDevice(serialNumber: String, updates: Map<String, Any>): LegacyDeviceData
}

// ACL: Adapter + Translator
@ComponentInline
class LegacyDeviceAdapter(
    private val legacyClient: LegacyDeviceClient,
    private val translator: LegacyDeviceTranslator
) {
    
    fun getDevice(serialNumber: SerialNumber): Device? {
        return try {
            val legacyData = legacyClient.getDevice(serialNumber.value)
                ?: return null
            
            translator.toDomain(legacyData)
        } catch (e: Exception) {
            logger.error("Failed to get device from legacy system", e)
            null
        }
    }
    
    fun syncDevice(device: Device): SyncResult {
        return try {
            val request = translator.toLegacyCreateRequest(device)
            val response = legacyClient.createDevice(request)
            
            SyncResult.Success(
                externalId = response.dev_sno,
                syncedAt = Instant.now()
            )
        } catch (e: Exception) {
            logger.error("Failed to sync device to legacy system", e)
            SyncResult.Failed(e.message ?: "Unknown error")
        }
    }
}

// Translator (isolated translation logic)
@ComponentInline
class LegacyDeviceTranslator {
    
    fun toDomain(legacy: LegacyDeviceData): Device {
        return Device(
            deviceId = DeviceId.generate(), // Generate our ID
            premisesId = PremisesId(legacy.own_id),
            name = Name(legacy.dev_nm),
            serialNumber = SerialNumber(legacy.dev_sno),
            type = translateType(legacy.dev_tp),
            status = translateStatus(legacy.dev_st),
            feeds = translateFeeds(legacy.feeds),
            createdAt = Instant.ofEpochMilli(legacy.reg_dt)
        )
    }
    
    fun toLegacyCreateRequest(device: Device): Map<String, Any> {
        return mapOf(
            "dev_sno" to device.serialNumber.value,
            "dev_nm" to device.getName().value,
            "dev_st" to translateStatusToCode(device.getStatus()),
            "dev_tp" to translateTypeToCode(device.getType()),
            "own_id" to device.premisesId.value,
            "reg_dt" to device.createdAt.toEpochMilli()
        )
    }
    
    private fun translateType(legacyType: String): DeviceType {
        return when (legacyType.uppercase()) {
            "S" -> DeviceType.SENSOR
            "A" -> DeviceType.ACTUATOR
            else -> throw IllegalArgumentException("Unknown device type: $legacyType")
        }
    }
    
    private fun translateStatus(legacyStatus: Int): DeviceStatus {
        return when (legacyStatus) {
            0 -> DeviceStatus.INACTIVE
            1 -> DeviceStatus.ACTIVE
            2 -> DeviceStatus.ERROR
            else -> DeviceStatus.UNKNOWN
        }
    }
    
    private fun translateFeeds(legacyFeeds: List<Map<String, Any>>?): Map<FeedId, Feed> {
        if (legacyFeeds == null) return emptyMap()
        
        return legacyFeeds.mapNotNull { feedData ->
            try {
                val feedId = FeedId.generate()
                val feed = Feed(
                    feedId = feedId,
                    deviceId = DeviceId.generate(), // Will be set by device
                    name = feedData["name"] as? String ?: "Unknown",
                    type = FeedType.valueOf(feedData["type"] as? String ?: "SENSOR"),
                    value = (feedData["value"] as? Number)?.toInt() ?: 0,
                    lastUpdated = Instant.now()
                )
                feedId to feed
            } catch (e: Exception) {
                logger.warn("Failed to translate feed: ${feedData}", e)
                null
            }
        }.toMap()
    }
    
    private fun translateStatusToCode(status: DeviceStatus): Int {
        return when (status) {
            DeviceStatus.INACTIVE -> 0
            DeviceStatus.ACTIVE -> 1
            DeviceStatus.ERROR -> 2
            DeviceStatus.DELETED -> 0
            else -> 3
        }
    }
    
    private fun translateTypeToCode(type: DeviceType): String {
        return when (type) {
            DeviceType.SENSOR -> "S"
            DeviceType.ACTUATOR -> "A"
        }
    }
}

// Clean usage in domain
@Service
class DeviceSyncService(
    private val legacyDeviceAdapter: LegacyDeviceAdapter,
    private val deviceRepository: DeviceRepository
) {
    fun syncFromLegacy(serialNumber: SerialNumber): Device? {
        // Domain doesn't know about legacy complexity! ✅
        val device = legacyDeviceAdapter.getDevice(serialNumber)
            ?: return null
        
        return deviceRepository.save(device)
    }
}
```

### Example 2: Third-Party Weather Service Integration

```kotlin
// External weather API (complex, many fields)
data class WeatherApiResponse(
    val coord: Coordinates,
    val weather: List<WeatherCondition>,
    val base: String,
    val main: MainWeatherData,
    val visibility: Int,
    val wind: WindData,
    val clouds: CloudData,
    val rain: RainData?,
    val snow: SnowData?,
    val dt: Long,
    val sys: SystemData,
    val timezone: Int,
    val id: Long,
    val name: String,
    val cod: Int
    // 50+ nested fields total
)

// Our domain only needs this
data class WeatherConditions(
    val temperature: Temperature,
    val humidity: Int,
    val conditions: WeatherType
)

enum class WeatherType {
    CLEAR, CLOUDY, RAINY, SNOWY
}

// ACL: Simplifying adapter
@ComponentInline
class WeatherServiceAdapter(
    private val weatherApiClient: WeatherApiClient,
    private val cache: CacheService
) {
    
    fun getCurrentConditions(location: Location): WeatherConditions? {
        // Check cache first
        val cached = cache.get<WeatherConditions>("weather:${location.latitude}:${location.longitude}")
        if (cached != null) return cached
        
        return try {
            val response = weatherApiClient.getCurrentWeather(
                lat = location.latitude,
                lon = location.longitude
            )
            
            val conditions = simplify(response)
            
            // Cache for 10 minutes
            cache.put(
                "weather:${location.latitude}:${location.longitude}",
                conditions,
                Duration.ofMinutes(10)
            )
            
            conditions
        } catch (e: Exception) {
            logger.error("Failed to get weather", e)
            null
        }
    }
    
    private fun simplify(response: WeatherApiResponse): WeatherConditions {
        return WeatherConditions(
            temperature = Temperature(
                celsius = response.main.temp - 273.15 // Convert from Kelvin
            ),
            humidity = response.main.humidity,
            conditions = determineWeatherType(response)
        )
    }
    
    private fun determineWeatherType(response: WeatherApiResponse): WeatherType {
        val mainCondition = response.weather.firstOrNull()?.main ?: return WeatherType.CLEAR
        
        return when {
            mainCondition.contains("rain", ignoreCase = true) -> WeatherType.RAINY
            mainCondition.contains("snow", ignoreCase = true) -> WeatherType.SNOWY
            mainCondition.contains("cloud", ignoreCase = true) -> WeatherType.CLOUDY
            else -> WeatherType.CLEAR
        }
    }
}

// Clean usage in automation
@Service
class WeatherBasedAutomationService(
    private val weatherAdapter: WeatherServiceAdapter
) {
    fun shouldActivateHeating(premises: Premises): Boolean {
        val conditions = weatherAdapter.getCurrentConditions(premises.location)
            ?: return false
        
        // Domain logic using simple weather model ✅
        return conditions.temperature.celsius < 18.0
    }
}
```

### Example 3: Multiple IoT Protocol Integration

```kotlin
// Different IoT protocols (Zigbee, Z-Wave, WiFi)

// ACL: Protocol adapter interface
interface IoTProtocolAdapter {
    fun discoverDevices(): List<Device>
    fun sendCommand(deviceId: DeviceId, command: DeviceCommand): CommandResult
    fun readSensorValue(deviceId: DeviceId, feedId: FeedId): Int?
}

// Zigbee adapter
@ComponentInline
class ZigbeeProtocolAdapter(
    private val zigbeeGateway: ZigbeeGateway
) : IoTProtocolAdapter {
    
    override fun discoverDevices(): List<Device> {
        val zigbeeDevices = zigbeeGateway.scanNetwork()
        
        return zigbeeDevices.map { zigbeeDevice ->
            Device(
                deviceId = DeviceId(zigbeeDevice.ieeeAddress),
                name = Name(zigbeeDevice.modelId),
                serialNumber = SerialNumber(zigbeeDevice.ieeeAddress),
                type = determineType(zigbeeDevice),
                status = translateStatus(zigbeeDevice.online),
                // ... translate Zigbee model to domain
            )
        }
    }
    
    override fun sendCommand(deviceId: DeviceId, command: DeviceCommand): CommandResult {
        val zigbeeCommand = translateCommand(command)
        val response = zigbeeGateway.sendCommand(deviceId.value, zigbeeCommand)
        return translateResponse(response)
    }
    
    override fun readSensorValue(deviceId: DeviceId, feedId: FeedId): Int? {
        val attributes = zigbeeGateway.readAttributes(deviceId.value)
        return attributes[translateFeedIdToAttribute(feedId)]
    }
}

// Z-Wave adapter
@ComponentInline
class ZWaveProtocolAdapter(
    private val zWaveController: ZWaveController
) : IoTProtocolAdapter {
    
    override fun discoverDevices(): List<Device> {
        val zWaveNodes = zWaveController.getNodes()
        
        return zWaveNodes.map { node ->
            Device(
                deviceId = DeviceId(node.nodeId.toString()),
                name = Name(node.productName),
                serialNumber = SerialNumber(node.homeId.toString()),
                type = determineType(node),
                status = translateStatus(node.isAwake),
                // ... translate Z-Wave model to domain
            )
        }
    }
    
    override fun sendCommand(deviceId: DeviceId, command: DeviceCommand): CommandResult {
        val nodeId = deviceId.value.toInt()
        val zWaveCommand = translateCommand(command)
        val success = zWaveController.sendCommand(nodeId, zWaveCommand)
        return if (success) CommandResult.Success else CommandResult.Failed("Command failed")
    }
    
    override fun readSensorValue(deviceId: DeviceId, feedId: FeedId): Int? {
        val nodeId = deviceId.value.toInt()
        return zWaveController.getSensorValue(nodeId, translateFeedIdToSensorType(feedId))
    }
}

// WiFi adapter
@ComponentInline
class WiFiProtocolAdapter(
    private val mqttClient: MqttClient
) : IoTProtocolAdapter {
    
    override fun discoverDevices(): List<Device> {
        // WiFi devices announce themselves via MQTT
        val announcements = mqttClient.getDeviceAnnouncements()
        
        return announcements.map { announcement ->
            Device(
                deviceId = DeviceId(announcement.macAddress),
                name = Name(announcement.deviceName),
                serialNumber = SerialNumber(announcement.macAddress),
                type = DeviceType.valueOf(announcement.type),
                status = DeviceStatus.ACTIVE,
                // ... translate WiFi device to domain
            )
        }
    }
    
    override fun sendCommand(deviceId: DeviceId, command: DeviceCommand): CommandResult {
        val topic = "devices/${deviceId.value}/commands"
        val payload = translateCommandToJson(command)
        
        return try {
            mqttClient.publish(topic, payload)
            CommandResult.Success
        } catch (e: Exception) {
            CommandResult.Failed(e.message ?: "MQTT publish failed")
        }
    }
    
    override fun readSensorValue(deviceId: DeviceId, feedId: FeedId): Int? {
        val topic = "devices/${deviceId.value}/sensors/${feedId.value}"
        return mqttClient.getLastValue(topic)
    }
}

// Protocol factory (chooses adapter based on device)
@ComponentInline
class IoTProtocolFactory(
    private val zigbeeAdapter: ZigbeeProtocolAdapter,
    private val zWaveAdapter: ZWaveProtocolAdapter,
    private val wifiAdapter: WiFiProtocolAdapter
) {
    
    fun getAdapter(device: Device): IoTProtocolAdapter {
        return when (device.os) {
            DeviceOS.ZIGBEE -> zigbeeAdapter
            DeviceOS.ZWAVE -> zWaveAdapter
            DeviceOS.WIFI -> wifiAdapter
            null -> throw IllegalStateException("Device OS not set")
        }
    }
}

// Clean usage in domain
@Service
class DeviceCommandService(
    private val deviceRepository: DeviceRepository,
    private val protocolFactory: IoTProtocolFactory
) {
    fun executeCommand(deviceId: DeviceId, command: DeviceCommand): CommandResult {
        val device = deviceRepository.findById(deviceId)
            ?: throw DeviceNotFoundException(deviceId)
        
        // Domain doesn't know about protocols! ✅
        val adapter = protocolFactory.getAdapter(device)
        return adapter.sendCommand(deviceId, command)
    }
}
```

---

## <a name="adapter-pattern"></a>6. Adapter Pattern in ACL

### Structure

```kotlin
// Domain interface (what domain expects)
interface PaymentGateway {
    fun processPayment(amount: Money, customer: Customer): PaymentResult
    fun refundPayment(transactionId: TransactionId): RefundResult
}

// External system (Stripe)
interface StripeApi {
    fun createCharge(
        amount: Long,              // Amount in cents
        currency: String,
        customer: String,
        description: String?
    ): StripeCharge
    
    fun createRefund(
        chargeId: String,
        amount: Long?,
        reason: String?
    ): StripeRefund
}

// Adapter translates between domain and external
@ComponentInline
class StripePaymentAdapter(
    private val stripeApi: StripeApi,
    private val stripeKeyProvider: StripeKeyProvider
) : PaymentGateway {
    
    override fun processPayment(amount: Money, customer: Customer): PaymentResult {
        return try {
            val charge = stripeApi.createCharge(
                amount = (amount.value * 100).toLong(), // Convert to cents
                currency = amount.currency.code.lowercase(),
                customer = customer.externalPaymentId ?: throw IllegalStateException("No payment ID"),
                description = "SmartHome Hub subscription"
            )
            
            PaymentResult.Success(
                transactionId = TransactionId(charge.id),
                amount = amount,
                processedAt = Instant.ofEpochSecond(charge.created)
            )
        } catch (e: StripeException) {
            PaymentResult.Failed(
                reason = translateStripeError(e),
                retryable = isRetryable(e)
            )
        }
    }
    
    override fun refundPayment(transactionId: TransactionId): RefundResult {
        return try {
            val refund = stripeApi.createRefund(
                chargeId = transactionId.value,
                amount = null, // Full refund
                reason = null
            )
            
            RefundResult.Success(
                refundId = RefundId(refund.id),
                refundedAt = Instant.ofEpochSecond(refund.created)
            )
        } catch (e: StripeException) {
            RefundResult.Failed(translateStripeError(e))
        }
    }
    
    private fun translateStripeError(e: StripeException): String {
        return when (e.code) {
            "card_declined" -> "Payment method declined"
            "insufficient_funds" -> "Insufficient funds"
            "expired_card" -> "Payment method expired"
            else -> "Payment processing failed: ${e.message}"
        }
    }
    
    private fun isRetryable(e: StripeException): Boolean {
        return e.code in listOf("rate_limit", "api_connection_error")
    }
}
```

---

## <a name="translator-pattern"></a>7. Translator Pattern

### Bidirectional Translation

```kotlin
// Translator for complex conversions
@ComponentInline
class DeviceDataTranslator {
    
    // External → Domain
    fun externalToDomain(external: ExternalDeviceDto): Device {
        return Device(
            deviceId = DeviceId(external.id),
            premisesId = PremisesId(external.locationId),
            name = Name(external.deviceName),
            serialNumber = SerialNumber(external.serialNo),
            type = translateDeviceType(external.category, external.subCategory),
            status = translateStatus(external.state),
            health = determineHealth(external),
            feeds = translateFeeds(external.dataPoints),
            os = determineOS(external.protocol),
            zoneId = external.roomId?.let { ZoneId(it) },
            createdBy = ActorId(external.installedBy),
            createdAt = Instant.parse(external.installationDate),
            lastSeenAt = external.lastCommunication?.let { Instant.parse(it) }
        )
    }
    
    // Domain → External
    fun domainToExternal(domain: Device): ExternalDeviceDto {
        return ExternalDeviceDto(
            id = domain.deviceId.value,
            locationId = domain.premisesId.value,
            deviceName = domain.getName().value,
            serialNo = domain.serialNumber.value,
            category = translateDeviceCategory(domain.getType()),
            subCategory = translateDeviceSubCategory(domain.getType()),
            state = translateStatusToExternal(domain.getStatus()),
            protocol = translateOSToProtocol(domain.os),
            roomId = domain.getZoneId()?.value,
            installedBy = domain.createdBy.value,
            installationDate = domain.createdAt.toString(),
            lastCommunication = domain.getLastSeenAt()?.toString(),
            dataPoints = translateFeedsToExternal(domain.getFeeds())
        )
    }
    
    private fun translateDeviceType(category: String, subCategory: String?): DeviceType {
        return when {
            category.equals("sensor", ignoreCase = true) -> DeviceType.SENSOR
            category.equals("actuator", ignoreCase = true) -> DeviceType.ACTUATOR
            category.equals("switch", ignoreCase = true) -> DeviceType.ACTUATOR
            subCategory?.contains("sensor", ignoreCase = true) == true -> DeviceType.SENSOR
            else -> throw IllegalArgumentException("Unknown device category: $category/$subCategory")
        }
    }
    
    private fun translateStatus(state: String): DeviceStatus {
        return when (state.lowercase()) {
            "online", "active" -> DeviceStatus.ACTIVE
            "offline" -> DeviceStatus.INACTIVE
            "error", "fault" -> DeviceStatus.ERROR
            "pending" -> DeviceStatus.PENDING
            "deleted", "removed" -> DeviceStatus.DELETED
            else -> DeviceStatus.UNKNOWN
        }
    }
    
    private fun determineHealth(external: ExternalDeviceDto): DeviceHealth {
        val lastComm = external.lastCommunication?.let { Instant.parse(it) }
        val hoursSinceComm = lastComm?.let { 
            Duration.between(it, Instant.now()).toHours() 
        } ?: Long.MAX_VALUE
        
        return when {
            hoursSinceComm < 1 -> DeviceHealth.ONLINE
            hoursSinceComm < 24 -> DeviceHealth.OFFLINE
            else -> DeviceHealth.UNREACHABLE
        }
    }
    
    private fun translateFeeds(dataPoints: List<ExternalDataPoint>?): Map<FeedId, Feed> {
        if (dataPoints == null) return emptyMap()
        
        return dataPoints.associate { dp ->
            val feedId = FeedId(dp.id)
            val feed = Feed(
                feedId = feedId,
                deviceId = DeviceId("temp"), // Will be set by Device
                name = dp.label,
                type = translateFeedType(dp.dataType),
                value = dp.currentValue.toIntOrNull() ?: 0,
                lastUpdated = dp.lastUpdate?.let { Instant.parse(it) } ?: Instant.now()
            )
            feedId to feed
        }
    }
    
    private fun translateFeedType(dataType: String): FeedType {
        return when (dataType.lowercase()) {
            "temperature" -> FeedType.TEMPERATURE
            "humidity" -> FeedType.HUMIDITY
            "switch", "binary" -> FeedType.SWITCH
            "dimmer", "level" -> FeedType.DIMMER
            else -> FeedType.SENSOR
        }
    }
}
```

---

## <a name="facade-pattern"></a>8. Facade Pattern

### Simplifying Complex External Systems

```kotlin
// Complex external API with many operations
interface ExternalDeviceManagementApi {
    fun authenticateDevice(serialNo: String, secret: String): AuthToken
    fun registerDevice(token: AuthToken, metadata: Map<String, Any>): DeviceRegistration
    fun configureDevice(deviceId: String, config: DeviceConfig): ConfigResult
    fun activateDevice(deviceId: String): ActivationResult
    fun provisionCertificate(deviceId: String): Certificate
    fun enrollInNetwork(deviceId: String, networkId: String): EnrollmentResult
    fun setDevicePolicy(deviceId: String, policy: Policy): PolicyResult
    fun enableMonitoring(deviceId: String): MonitoringResult
    fun createDeviceGroup(deviceId: String, groupId: String): GroupResult
    // ... 50 more operations
}

// Facade simplifies for domain
@ComponentInline
class DeviceProvisioningFacade(
    private val externalApi: ExternalDeviceManagementApi,
    private val configProvider: DeviceConfigProvider,
    private val certificateStore: CertificateStore
) {
    
    fun provisionDevice(device: Device, secret: String): ProvisioningResult {
        return try {
            // Step 1: Authenticate
            val token = externalApi.authenticateDevice(
                device.serialNumber.value,
                secret
            )
            
            // Step 2: Register
            val metadata = buildMetadata(device)
            val registration = externalApi.registerDevice(token, metadata)
            
            // Step 3: Configure
            val config = configProvider.getConfigFor(device.getType())
            externalApi.configureDevice(registration.deviceId, config)
            
            // Step 4: Provision certificate
            val certificate = externalApi.provisionCertificate(registration.deviceId)
            certificateStore.store(device.deviceId, certificate)
            
            // Step 5: Activate
            val activation = externalApi.activateDevice(registration.deviceId)
            
            // Simple result for domain
            ProvisioningResult.Success(
                externalDeviceId = registration.deviceId,
                certificateId = certificate.id,
                activatedAt = Instant.now()
            )
            
        } catch (e: Exception) {
            logger.error("Device provisioning failed", e)
            ProvisioningResult.Failed(e.message ?: "Unknown error")
        }
    }
    
    private fun buildMetadata(device: Device): Map<String, Any> {
        return mapOf(
            "name" to device.getName().value,
            "type" to device.getType().name,
            "serialNumber" to device.serialNumber.value,
            "premisesId" to device.premisesId.value,
            "timestamp" to Instant.now().toString()
        )
    }
}

// Clean usage in domain
@Service
class DeviceProvisioningService(
    private val provisioningFacade: DeviceProvisioningFacade
) {
    fun provisionNewDevice(device: Device, secret: String): Device {
        // Domain doesn't know about complex external process! ✅
        val result = provisioningFacade.provisionDevice(device, secret)
        
        return when (result) {
            is ProvisioningResult.Success -> {
                device.markAsProvisioned(result.externalDeviceId)
            }
            is ProvisioningResult.Failed -> {
                throw ProvisioningFailedException(result.reason)
            }
        }
    }
}
```

---

## <a name="testing"></a>9. Testing ACL

### Unit Testing Translators

```kotlin
class DeviceTranslatorTest {
    
    private val translator = DeviceDataTranslator()
    
    @Test
    fun `should translate external device to domain`() {
        // Given
        val external = ExternalDeviceDto(
            id = "ext-123",
            locationId = "loc-456",
            deviceName = "Living Room Sensor",
            serialNo = "SN12345",
            category = "sensor",
            subCategory = "temperature",
            state = "online",
            protocol = "zigbee",
            installedBy = "user-789",
            installationDate = "2025-01-01T00:00:00Z",
            lastCommunication = "2025-11-11T12:00:00Z",
            dataPoints = listOf(
                ExternalDataPoint(
                    id = "dp-1",
                    label = "Temperature",
                    dataType = "temperature",
                    currentValue = "25",
                    lastUpdate = "2025-11-11T12:00:00Z"
                )
            )
        )
        
        // When
        val domain = translator.externalToDomain(external)
        
        // Then
        assertEquals(DeviceId("ext-123"), domain.deviceId)
        assertEquals(Name("Living Room Sensor"), domain.getName())
        assertEquals(DeviceType.SENSOR, domain.getType())
        assertEquals(DeviceStatus.ACTIVE, domain.getStatus())
        assertEquals(1, domain.getFeeds().size)
    }
    
    @Test
    fun `should handle missing optional fields`() {
        val external = ExternalDeviceDto(
            id = "ext-123",
            locationId = "loc-456",
            deviceName = "Device",
            serialNo = "SN12345",
            category = "sensor",
            subCategory = null,
            state = "pending",
            protocol = null,
            roomId = null,
            installedBy = "user-789",
            installationDate = "2025-01-01T00:00:00Z",
            lastCommunication = null,
            dataPoints = null
        )
        
        val domain = translator.externalToDomain(external)
        
        assertNotNull(domain)
        assertTrue(domain.getFeeds().isEmpty())
        assertNull(domain.getZoneId())
    }
    
    @Test
    fun `should roundtrip external to domain and back`() {
        val original = ExternalDeviceDto(/* ... */)
        
        val domain = translator.externalToDomain(original)
        val backToExternal = translator.domainToExternal(domain)
        
        // Key fields should match
        assertEquals(original.id, backToExternal.id)
        assertEquals(original.deviceName, backToExternal.deviceName)
        assertEquals(original.serialNo, backToExternal.serialNo)
    }
}
```

### Integration Testing ACL

```kotlin
@SpringBootTest
class LegacyDeviceAdapterIntegrationTest {
    
    @Autowired
    lateinit var adapter: LegacyDeviceAdapter
    
    @MockBean
    lateinit var legacyClient: LegacyDeviceClient
    
    @Test
    fun `should get device through adapter`() {
        // Given
        val legacyData = LegacyDeviceData(
            dev_sno = "LEG-123",
            dev_nm = "Legacy Device",
            dev_st = 1,
            dev_tp = "S",
            loc_cd = "LOC-001",
            own_id = "owner-123",
            reg_dt = Instant.now().toEpochMilli(),
            feeds = emptyList()
        )
        
        every { legacyClient.getDevice("LEG-123") } returns legacyData
        
        // When
        val device = adapter.getDevice(SerialNumber("LEG-123"))
        
        // Then
        assertNotNull(device)
        assertEquals("Legacy Device", device?.getName()?.value)
        assertEquals(DeviceType.SENSOR, device?.getType())
    }
    
    @Test
    fun `should handle legacy system errors gracefully`() {
        // Given
        every { legacyClient.getDevice(any()) } throws RuntimeException("Legacy system down")
        
        // When
        val device = adapter.getDevice(SerialNumber("ANY"))
        
        // Then - returns null instead of throwing
        assertNull(device)
    }
}
```

### Contract Testing

```kotlin
// Define contract for ACL
interface DeviceProviderContract {
    fun getDevice(deviceId: DeviceId): Device?
    fun saveDevice(device: Device): Device
}

// Contract tests (run against all implementations)
abstract class DeviceProviderContractTest {
    
    abstract fun getProvider(): DeviceProviderContract
    
    @Test
    fun `should return null for non-existent device`() {
        val provider = getProvider()
        val device = provider.getDevice(DeviceId("non-existent"))
        assertNull(device)
    }
    
    @Test
    fun `should save and retrieve device`() {
        val provider = getProvider()
        val device = createTestDevice()
        
        val saved = provider.saveDevice(device)
        val retrieved = provider.getDevice(saved.deviceId)
        
        assertNotNull(retrieved)
        assertEquals(saved.deviceId, retrieved?.deviceId)
    }
}

// Run contract tests against adapter
class LegacyDeviceAdapterContractTest : DeviceProviderContractTest() {
    override fun getProvider(): DeviceProviderContract = LegacyDeviceAdapter(...)
}

// Run same tests against real repository
class MongoDeviceRepositoryContractTest : DeviceProviderContractTest() {
    override fun getProvider(): DeviceProviderContract = MongoDeviceRepository(...)
}
```

---

## 10. Chapter Summary

In this chapter, we've explored the Anti-Corruption Layer pattern—a critical defensive pattern that protects your domain model from external system complexity and prevents pollution from spreading throughout your codebase.

### What We Covered

**The Pollution Problem:**
- External system's complex model leaking into domain
- 200+ lines of integration code scattered across 15 files
- Domain entities with 50+ foreign fields
- Cryptic field names (dev_sno, dev_st, loc_cd)
- Inconsistent data structures
- No single point of translation

**The ACL Solution:**
- Single translation point between systems
- Domain stays clean and focused
- External complexity isolated
- Easy to replace external system
- Testable without external dependencies

**Core Components:**
```kotlin
// Domain Interface (what we need)
interface DeviceProvider {
    fun getDevice(deviceId: DeviceId): Device?
    fun listDevices(premisesId: PremisesId): List<Device>
}

// ACL Adapter (implements domain interface)
class LegacyDeviceAdapter(
    private val legacyClient: LegacySystemClient,
    private val translator: LegacyDeviceTranslator
) : DeviceProvider {
    override fun getDevice(deviceId: DeviceId): Device? {
        val legacy = legacyClient.getDevice(deviceId.value)
        return legacy?.let { translator.toDomain(it) }
    }
}

// Translator (converts models)
class LegacyDeviceTranslator {
    fun toDomain(legacy: LegacyDeviceResponse): Device {
        // Clean translation logic
    }
}
```

### Key Insights

1. **ACL protects domain integrity** - External complexity never enters domain layer.

2. **Translation in one place** - All conversions centralized, not scattered.

3. **External systems become replaceable** - Swap implementation, keep domain intact.

4. **Bidirectional translation** - Domain → External and External → Domain.

5. **Handles technical concerns** - Retry logic, caching, error handling in ACL.

6. **Multiple patterns combined** - Adapter + Translator + Facade work together.

7. **Testable independently** - Mock external system, test translation logic.

8. **Domain stays clean** - No cryptic field names, no nullable hell.

9. **Contract testing crucial** - Verify ACL maintains external system contract.

10. **Gradual extraction** - ACL enables phased migration from legacy systems.

### Three ACL Patterns

**1. Adapter Pattern (Interface Implementation):**
```kotlin
// Domain defines interface
interface DeviceProvider {
    fun getDevice(deviceId: DeviceId): Device?
}

// ACL implements with external system
class LegacyDeviceAdapter(
    private val legacyClient: LegacySystemClient
) : DeviceProvider {
    override fun getDevice(deviceId: DeviceId): Device? {
        return legacyClient.getDevice(deviceId.value)
            ?.let { translator.toDomain(it) }
    }
}
```

**2. Translator Pattern (Model Conversion):**
```kotlin
class LegacyDeviceTranslator {
    fun toDomain(legacy: LegacyDeviceResponse): Device {
        return Device.register(
            deviceId = DeviceId(legacy.dev_sno),
            name = Name(legacy.dev_nm),
            serialNumber = SerialNumber(legacy.dev_sno),
            type = translateType(legacy.dev_tp),
            status = translateStatus(legacy.dev_st)
        )
    }
    
    fun toExternal(device: Device): LegacyDeviceRequest {
        return LegacyDeviceRequest(
            dev_sno = device.deviceId.value,
            dev_nm = device.getName().value,
            dev_tp = when(device.type) {
                DeviceType.SENSOR -> "S"
                DeviceType.ACTUATOR -> "A"
            }
        )
    }
    
    private fun translateStatus(status: Int): DeviceStatus {
        return when(status) {
            0 -> DeviceStatus.INACTIVE
            1 -> DeviceStatus.ACTIVE
            2 -> DeviceStatus.ERROR
            else -> DeviceStatus.UNKNOWN
        }
    }
}
```

**3. Facade Pattern (Simplification):**
```kotlin
class LegacySystemFacade(
    private val deviceClient: LegacyDeviceClient,
    private val userClient: LegacyUserClient,
    private val locationClient: LegacyLocationClient
) {
    fun getDeviceWithOwner(deviceId: DeviceId): DeviceWithOwner? {
        val device = deviceClient.getDevice(deviceId.value) ?: return null
        val owner = userClient.getUser(device.own_id)
        val location = device.loc_cd?.let { 
            locationClient.getLocation(it) 
        }
        
        return DeviceWithOwner(
            device = translator.toDomain(device),
            owner = owner?.let { userTranslator.toDomain(it) },
            location = location?.let { locationTranslator.toDomain(it) }
        )
    }
}
```

### SmartHome Hub Legacy Integration

**Before (No ACL - Pollution):**
```kotlin
// Domain entity polluted ❌
data class Device(
    val deviceId: DeviceId,
    val name: Name,
    
    // Legacy fields leak in! ❌
    val dev_sno: String?,
    val dev_st: Int?,
    val loc_cd: String?,
    val meta: Map<String, Any>?,
    // ... 30 more external fields
)

// Translation scattered everywhere ❌
class DeviceService {
    fun getDevice(id: DeviceId): Device {
        val legacy = legacyClient.get(id.value)
        // Translation logic mixed with business logic ❌
        return Device(
            deviceId = DeviceId(legacy.dev_sno),
            name = Name(legacy.dev_nm),
            dev_sno = legacy.dev_sno,  // Keeping legacy fields!
            dev_st = legacy.dev_st
        )
    }
}
```

**After (With ACL - Protected):**
```kotlin
// Clean domain entity ✅
data class Device(
    val deviceId: DeviceId,
    val name: Name,
    val serialNumber: SerialNumber,
    val type: DeviceType,
    val status: DeviceStatus
)

// ACL handles all translation ✅
class LegacyDeviceAdapter(
    private val legacyClient: LegacySystemClient,
    private val translator: LegacyDeviceTranslator,
    private val cache: Cache<DeviceId, Device>
) : DeviceProvider {
    
    override fun getDevice(deviceId: DeviceId): Device? {
        return cache.get(deviceId) {
            try {
                legacyClient.getDevice(deviceId.value)
                    ?.let { translator.toDomain(it) }
            } catch (e: Exception) {
                logger.error("Failed to fetch device", e)
                null
            }
        }
    }
}
```

**Impact:**
- 200+ lines scattered → 50 lines centralized
- 50 foreign fields → 0 pollution
- Domain stays clean
- Legacy system replaceable
- 100% testable

### When to Use ACL

**Use ACL When:**
- ✅ Integrating with legacy systems
- ✅ External system has poor model
- ✅ Multiple external providers for same data
- ✅ External system will be replaced
- ✅ External model conflicts with domain
- ✅ Need to isolate external changes

**Don't Use ACL When:**
- ❌ External model is clean and matches domain
- ❌ Full control over external system
- ❌ Simple pass-through with no translation
- ❌ Over-engineering simple integration

### Multi-Protocol Integration

ACL can handle different protocols:

**REST Integration:**
```kotlin
class RestLegacyClient {
    fun getDevice(id: String): LegacyDeviceResponse? {
        return restTemplate.getForObject(
            "https://legacy.com/api/devices/$id",
            LegacyDeviceResponse::class.java
        )
    }
}
```

**SOAP Integration:**
```kotlin
class SoapLegacyClient {
    fun getDevice(id: String): LegacyDeviceResponse? {
        val request = GetDeviceRequest().apply { deviceId = id }
        val response = webServiceTemplate.marshalSendAndReceive(request)
        return (response as GetDeviceResponse).device
    }
}
```

**gRPC Integration:**
```kotlin
class GrpcLegacyClient(
    private val stub: DeviceServiceGrpc.DeviceServiceBlockingStub
) {
    fun getDevice(id: String): LegacyDeviceResponse? {
        val request = GetDeviceRequest.newBuilder()
            .setDeviceId(id)
            .build()
        return stub.getDevice(request).toInternal()
    }
}
```

### Advanced ACL Features

**1. Caching:**
```kotlin
class CachedDeviceAdapter(
    private val adapter: DeviceProvider,
    private val cache: Cache<DeviceId, Device>
) : DeviceProvider {
    
    override fun getDevice(deviceId: DeviceId): Device? {
        return cache.get(deviceId) {
            adapter.getDevice(deviceId)
        }
    }
}
```

**2. Retry Logic:**
```kotlin
class ResilientDeviceAdapter(
    private val adapter: DeviceProvider,
    private val retryPolicy: RetryPolicy
) : DeviceProvider {
    
    override fun getDevice(deviceId: DeviceId): Device? {
        return retryPolicy.execute {
            adapter.getDevice(deviceId)
        }
    }
}
```

**3. Circuit Breaker:**
```kotlin
class CircuitBreakerDeviceAdapter(
    private val adapter: DeviceProvider,
    private val circuitBreaker: CircuitBreaker
) : DeviceProvider {
    
    override fun getDevice(deviceId: DeviceId): Device? {
        return circuitBreaker.execute {
            adapter.getDevice(deviceId)
        }
    }
}
```

### Testing Strategies

**Unit Tests (Translator):**
```kotlin
class LegacyDeviceTranslatorTest {
    private val translator = LegacyDeviceTranslator()
    
    @Test
    fun `should translate legacy device to domain`() {
        val legacy = LegacyDeviceResponse(
            dev_sno = "ABC123",
            dev_nm = "Sensor 1",
            dev_st = 1,
            dev_tp = "S"
        )
        
        val domain = translator.toDomain(legacy)
        
        assertEquals(DeviceId("ABC123"), domain.deviceId)
        assertEquals(Name("Sensor 1"), domain.getName())
        assertEquals(DeviceStatus.ACTIVE, domain.status)
        assertEquals(DeviceType.SENSOR, domain.type)
    }
    
    @Test
    fun `should handle missing optional fields`() {
        val legacy = LegacyDeviceResponse(
            dev_sno = "ABC123",
            dev_nm = "Sensor 1",
            dev_st = 1,
            dev_tp = "S",
            loc_cd = null,  // Missing location
            meta = null     // Missing metadata
        )
        
        val domain = translator.toDomain(legacy)
        
        assertNotNull(domain)  // Should not fail
    }
}
```

**Integration Tests (Adapter):**
```kotlin
@SpringBootTest
class LegacyDeviceAdapterIntegrationTest {
    
    @MockBean
    private lateinit var legacyClient: LegacySystemClient
    
    @Autowired
    private lateinit var adapter: DeviceProvider
    
    @Test
    fun `should fetch and translate device from legacy system`() {
        val legacyResponse = createLegacyDeviceResponse()
        every { legacyClient.getDevice(any()) } returns legacyResponse
        
        val device = adapter.getDevice(DeviceId("ABC123"))
        
        assertNotNull(device)
        assertEquals(DeviceId("ABC123"), device!!.deviceId)
        verify { legacyClient.getDevice("ABC123") }
    }
}
```

**Contract Tests:**
```kotlin
@WireMockTest
class LegacySystemContractTest {
    
    @Test
    fun `should match legacy system API contract`() {
        stubFor(get("/api/devices/ABC123")
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""
                    {
                        "dev_sno": "ABC123",
                        "dev_nm": "Sensor 1",
                        "dev_st": 1,
                        "dev_tp": "S"
                    }
                """)))
        
        val device = adapter.getDevice(DeviceId("ABC123"))
        
        assertNotNull(device)
        // Verifies ACL correctly interprets contract
    }
}
```

### Measured Benefits

Projects using ACL properly see:
- **80-90% reduction** in scattered integration code
- **Zero domain pollution** from external systems
- **3x faster** external system replacement
- **100% testable** without external dependencies
- **Isolated failures** - external issues don't break domain
- **Clear boundaries** between systems

### Practice Exercise

Add ACL to your integration:

1. **Identify pollution** - Find external models in domain
2. **Define domain interface** - What does domain need?
3. **Create adapter** - Implement domain interface
4. **Build translator** - Convert external ↔ domain
5. **Add facade** - Simplify complex external APIs
6. **Test translator** - Unit tests for conversions
7. **Test adapter** - Integration tests with mocks
8. **Add contract tests** - Verify external API expectations
9. **Remove pollution** - Clean external fields from domain

### Design Checklist

When implementing ACL:
- ✅ Domain interface defined (no external types)
- ✅ Adapter implements domain interface
- ✅ Translator handles bidirectional conversion
- ✅ Facade simplifies complex external APIs
- ✅ Error handling in ACL (not domain)
- ✅ Caching and retry in ACL
- ✅ Unit tests for translator
- ✅ Integration tests for adapter
- ✅ Contract tests for external API
- ✅ No external types in domain layer

---

### Additional Reading

For deeper understanding of ACL:
- **"Domain-Driven Design"** by Eric Evans (2003) - Original ACL pattern
- **"Implementing Domain-Driven Design"** by Vaughn Vernon (2013) - ACL implementation chapters
- **"Building Microservices"** by Sam Newman (2021) - Integration patterns

---

## What's Next

In **Chapter 11**, we'll explore Domain Events and Integration Events for event-driven architecture. You'll learn:
- Difference between domain and integration events
- When to use each event type
- Event publishing and handling patterns
- Event store implementation
- Real event flows from SmartHome Hub
- Testing event-driven systems

With ACL protecting your domain from external systems, events will enable loose coupling within and between bounded contexts.

Turn the page to master event-driven architecture...

*"Create an isolating layer to provide clients with functionality in terms of their own domain model."  
— Eric Evans, Domain-Driven Design*

