# Chapter 12: Saga Pattern - Distributed Transactions Made Simple

**Series:** Building an Enterprise IoT Platform with DDD  
**Part:** 3 of 20 - Strategic Patterns  
**Reading Time:** 28 minutes  
**Level:** Advanced  

---

## 📋 Table of Contents

1. [The Problem: Distributed Transactions](#the-problem)
2. [What is the Saga Pattern?](#what-is-saga)
3. [Orchestration vs Choreography](#orchestration-vs-choreography)
4. [Saga Implementation Patterns](#implementation-patterns)
5. [Real-World Examples from SmartHome Hub](#real-examples)
6. [Compensation Logic](#compensation)
7. [Saga State Management](#state-management)
8. [Error Handling and Retries](#error-handling)
9. [Testing Sagas](#testing)

---

## <a name="the-problem"></a>1. The Problem: Distributed Transactions

### The Scenario: The Order Processing Disaster

You're building a device provisioning flow in SmartHome Hub when disaster strikes:

> **Production Issue:** "Device registration succeeds but billing fails. Now we have registered devices without active subscriptions. 1000+ devices in inconsistent state. Financial loss!"

You investigate and find the broken flow:

```kotlin
// DeviceRegistrationService.kt - Distributed transaction attempt ❌
@Service
class DeviceRegistrationService(
    private val deviceService: DeviceService,
    private val billingService: BillingService,
    private val inventoryService: InventoryService,
    private val notificationService: NotificationService
) {
    
    @Transactional  // This doesn't work across services! ❌
    fun registerDevice(request: DeviceRegistrationRequest): Device {
        // Step 1: Register device in device service
        val device = deviceService.registerDevice(
            serialNumber = request.serialNumber,
            premisesId = request.premisesId
        )
        // Device saved in device_database ✓
        
        // Step 2: Reserve inventory
        inventoryService.reserveDevice(device.serialNumber)
        // Inventory updated in inventory_database ✓
        
        // Step 3: Create billing subscription
        billingService.createSubscription(
            customerId = request.customerId,
            tier = request.subscriptionTier
        )
        // Subscription created in billing_database ✓
        
        // Step 4: Send notification
        notificationService.sendWelcomeEmail(request.email)
        // Email sent ✓
        
        return device
    }
}
```

### The Disaster Scenarios

**Scenario 1: Billing Service Fails**

```kotlin
// Step 1: Device registered ✓ (in device_database)
val device = deviceService.registerDevice(...)  // SUCCESS

// Step 2: Inventory reserved ✓ (in inventory_database)
inventoryService.reserveDevice(...)  // SUCCESS

// Step 3: Billing fails! ❌ (billing_database)
billingService.createSubscription(...)  // FAILS!
// Exception thrown

// Result:
// - Device registered ✓
// - Inventory reserved ✓
// - NO subscription ❌
// - User has device but can't use it 💥
// - Can't rollback device or inventory (different databases)
```

**Scenario 2: Partial Network Failure**

```kotlin
// Step 1: Device registered ✓
val device = deviceService.registerDevice(...)  // SUCCESS

// Step 2: Inventory call hangs...
inventoryService.reserveDevice(...)  // TIMEOUT after 30 seconds

// What happened?
// - Device registered? Yes ✓
// - Inventory reserved? Maybe? Unknown! 💥
// - Request eventually succeeded but we timed out
// - Now inventory is reserved but we don't know
// - Retry will fail (device already registered)
```

**Scenario 3: Service Crashes Mid-Transaction**

```kotlin
// Step 1: Device registered ✓
val device = deviceService.registerDevice(...)  // SUCCESS

// Step 2: Inventory reserved ✓
inventoryService.reserveDevice(...)  // SUCCESS

// Application crashes! 💥
// JVM killed, container restarted

// Result:
// - Device registered ✓
// - Inventory reserved ✓
// - Billing never called ❌
// - Notification never sent ❌
// - Inconsistent state forever
```

### The Real Cost

**Measured impact:**
- 🔴 **1000+ devices** in inconsistent state
- 🔴 **$50,000+ revenue loss** (devices without subscriptions)
- 🔴 **Manual cleanup required** (2 days of work)
- 🔴 **Customer support overload** (500+ tickets)
- 🔴 **No rollback mechanism** (can't undo partial success)
- 🔴 **Database inconsistency** (across 3 separate databases)

**Attempted solutions that failed:**

```kotlin
// Attempt 1: Two-Phase Commit ❌
// - Requires distributed transaction coordinator
// - Doesn't work with REST APIs
// - Doesn't work with external services
// - Too complex, too slow

// Attempt 2: Synchronous rollback ❌
try {
    deviceService.registerDevice(...)
    inventoryService.reserveDevice(...)
    billingService.createSubscription(...)
} catch (e: Exception) {
    // Try to rollback
    deviceService.deleteDevice(...)  // What if this fails?
    inventoryService.releaseDevice(...)  // What if this fails?
    throw e
}
// Doesn't handle crashes or timeouts

// Attempt 3: Ignore the problem ❌
// "Eventually consistent" without any consistency mechanism
// Just hope it works out
// Spoiler: It doesn't
```

**Root Cause:** No pattern for managing distributed transactions across multiple services and databases.

---

## <a name="what-is-saga"></a>2. What is the Saga Pattern?

### Definition

> **Saga:** A sequence of local transactions where each transaction updates data within a single service. If a step fails, the saga executes compensating transactions to undo the changes made by preceding transactions.
> 
> — Microservices Patterns, Chris Richardson

### Core Concepts

#### 1. Local Transactions

Each step is a local transaction in one service:

```kotlin
// Not one distributed transaction ❌
@Transactional  // Doesn't span services
fun processOrder() {
    deviceService.register()
    billingService.charge()
    inventoryService.reserve()
}

// Multiple local transactions ✅
fun processOrderSaga() {
    // Transaction 1 (device database)
    deviceService.register()
    
    // Transaction 2 (billing database)
    billingService.charge()
    
    // Transaction 3 (inventory database)
    inventoryService.reserve()
}
```

#### 2. Compensation

Each step has a compensating transaction to undo it:

```
┌─────────────────────────────────────────────────────────┐
│                  Forward Flow                           │
│                                                         │
│  Step 1 → Step 2 → Step 3 → Step 4 → SUCCESS          │
│                                                         │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│           Compensation Flow (on failure)                │
│                                                         │
│  Step 1 → Step 2 → Step 3 → FAIL                      │
│     ↓       ↓       ↓                                   │
│  Undo 1 ← Undo 2 ← Undo 3                             │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

#### 3. Eventual Consistency

The system is eventually consistent, not immediately:

```kotlin
// Time T0: Start saga
// - Device: NOT REGISTERED
// - Inventory: AVAILABLE
// - Billing: NO SUBSCRIPTION

// Time T1: Step 1 complete
// - Device: REGISTERED ✓
// - Inventory: AVAILABLE
// - Billing: NO SUBSCRIPTION

// Time T2: Step 2 complete
// - Device: REGISTERED ✓
// - Inventory: RESERVED ✓
// - Billing: NO SUBSCRIPTION

// Time T3: Step 3 complete (final)
// - Device: REGISTERED ✓
// - Inventory: RESERVED ✓
// - Billing: SUBSCRIPTION CREATED ✓
// NOW CONSISTENT! ✅
```

#### 4. Saga State

Track which steps completed:

```kotlin
enum class SagaStatus {
    STARTED,
    STEP_1_COMPLETED,
    STEP_2_COMPLETED,
    STEP_3_COMPLETED,
    COMPLETED,
    COMPENSATING,
    COMPENSATED,
    FAILED
}

data class SagaState(
    val sagaId: String,
    val status: SagaStatus,
    val completedSteps: List<String>,
    val currentStep: String?,
    val error: String?
)
```

---

## <a name="orchestration-vs-choreography"></a>3. Orchestration vs Choreography

### Orchestration Saga (Centralized)

One orchestrator coordinates all steps:

```
┌─────────────────────────────────────────────────────────┐
│               Saga Orchestrator                         │
│                                                         │
│  1. Call Device Service → Register device              │
│  2. Call Billing Service → Create subscription         │
│  3. Call Inventory Service → Reserve device            │
│  4. Call Notification Service → Send email             │
│                                                         │
└─────────────────────────────────────────────────────────┘
         │              │              │              │
         ▼              ▼              ▼              ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│   Device     │ │   Billing    │ │  Inventory   │ │ Notification │
│   Service    │ │   Service    │ │   Service    │ │   Service    │
└──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘
```

**Pros:**
- ✅ Easy to understand
- ✅ Centralized control
- ✅ Easy to monitor
- ✅ Clear compensation logic

**Cons:**
- ❌ Single point of failure (orchestrator)
- ❌ Tight coupling to orchestrator
- ❌ Orchestrator knows about all services

### Choreography Saga (Decentralized)

Services react to events:

```
┌──────────────┐
│   Device     │  Publishes: DeviceRegistered
│   Service    │  ─────────────────────────────┐
└──────────────┘                               │
                                               ▼
                                        ┌──────────────┐
                                        │   Billing    │  Publishes: SubscriptionCreated
                                        │   Service    │  ─────────────────────────────┐
                                        └──────────────┘                               │
                                                                                       ▼
                                                                                ┌──────────────┐
                                                                                │  Inventory   │
                                                                                │   Service    │
                                                                                └──────────────┘
```

**Pros:**
- ✅ Loose coupling
- ✅ No single point of failure
- ✅ Services independent

**Cons:**
- ❌ Hard to understand flow
- ❌ Hard to monitor
- ❌ Complex compensation

### When to Use Which?

**Use Orchestration when:**
- Complex workflow with many steps
- Need centralized control
- Need to monitor progress easily
- Compensation logic is complex

**Use Choreography when:**
- Simple workflow (2-3 steps)
- Services should be fully independent
- Each service owns its domain
- Loose coupling is critical

---

## <a name="implementation-patterns"></a>4. Saga Implementation Patterns

### Pattern 1: Command-Based Orchestration

```kotlin
// Saga orchestrator
@Service
class DeviceRegistrationSaga(
    private val deviceService: DeviceService,
    private val billingService: BillingService,
    private val inventoryService: InventoryService,
    private val sagaRepository: SagaRepository
) {
    
    fun execute(command: RegisterDeviceCommand): SagaResult {
        val sagaId = UUID.randomUUID().toString()
        val sagaState = SagaState(
            sagaId = sagaId,
            status = SagaStatus.STARTED,
            completedSteps = emptyList()
        )
        sagaRepository.save(sagaState)
        
        try {
            // Step 1: Register device
            val device = executeStep(sagaId, "RegisterDevice") {
                deviceService.registerDevice(command)
            }
            
            // Step 2: Create subscription
            val subscription = executeStep(sagaId, "CreateSubscription") {
                billingService.createSubscription(
                    customerId = command.customerId,
                    tier = command.subscriptionTier
                )
            }
            
            // Step 3: Reserve inventory
            executeStep(sagaId, "ReserveInventory") {
                inventoryService.reserveDevice(device.serialNumber)
            }
            
            // All steps succeeded
            completeSaga(sagaId)
            
            return SagaResult.Success(device)
            
        } catch (e: Exception) {
            // Compensate for completed steps
            compensate(sagaId)
            
            return SagaResult.Failed(e.message ?: "Saga failed")
        }
    }
    
    private fun <T> executeStep(
        sagaId: String,
        stepName: String,
        action: () -> T
    ): T {
        val result = action()
        
        // Record step completion
        val sagaState = sagaRepository.findById(sagaId)!!
        sagaRepository.save(
            sagaState.copy(
                completedSteps = sagaState.completedSteps + stepName
            )
        )
        
        return result
    }
    
    private fun compensate(sagaId: String) {
        val sagaState = sagaRepository.findById(sagaId)!!
        
        sagaRepository.save(
            sagaState.copy(status = SagaStatus.COMPENSATING)
        )
        
        // Compensate in reverse order
        sagaState.completedSteps.reversed().forEach { step ->
            try {
                when (step) {
                    "RegisterDevice" -> deviceService.deleteDevice(/* ... */)
                    "CreateSubscription" -> billingService.cancelSubscription(/* ... */)
                    "ReserveInventory" -> inventoryService.releaseDevice(/* ... */)
                }
            } catch (e: Exception) {
                logger.error("Compensation failed for step $step", e)
                // Continue compensating other steps
            }
        }
        
        sagaRepository.save(
            sagaState.copy(status = SagaStatus.COMPENSATED)
        )
    }
    
    private fun completeSaga(sagaId: String) {
        val sagaState = sagaRepository.findById(sagaId)!!
        sagaRepository.save(
            sagaState.copy(status = SagaStatus.COMPLETED)
        )
    }
}
```

### Pattern 2: Event-Based Choreography

```kotlin
// Step 1: Device Service publishes event
@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val eventPublisher: EventPublisher
) {
    fun registerDevice(command: RegisterDeviceCommand): Device {
        val device = Device.register(...)
        val saved = deviceRepository.save(device)
        
        // Publish event
        eventPublisher.publish(
            DeviceRegisteredEvent(
                deviceId = saved.deviceId,
                customerId = command.customerId,
                subscriptionTier = command.subscriptionTier,
                serialNumber = saved.serialNumber
            )
        )
        
        return saved
    }
}

// Step 2: Billing Service reacts to DeviceRegistered
@ComponentInline
class DeviceRegisteredBillingHandler(
    private val billingService: BillingService,
    private val eventPublisher: EventPublisher
) {
    
    @KafkaListener(topics = ["device-events"])
    fun handle(event: DeviceRegisteredEvent) {
        try {
            // Create subscription
            val subscription = billingService.createSubscription(
                customerId = event.customerId,
                tier = event.subscriptionTier
            )
            
            // Publish success event
            eventPublisher.publish(
                SubscriptionCreatedEvent(
                    deviceId = event.deviceId,
                    subscriptionId = subscription.subscriptionId,
                    serialNumber = event.serialNumber
                )
            )
            
        } catch (e: Exception) {
            // Publish failure event (triggers compensation)
            eventPublisher.publish(
                SubscriptionCreationFailedEvent(
                    deviceId = event.deviceId,
                    reason = e.message ?: "Unknown error"
                )
            )
        }
    }
}

// Step 3: Inventory Service reacts to SubscriptionCreated
@ComponentInline
class SubscriptionCreatedInventoryHandler(
    private val inventoryService: InventoryService,
    private val eventPublisher: EventPublisher
) {
    
    @KafkaListener(topics = ["billing-events"])
    fun handle(event: SubscriptionCreatedEvent) {
        try {
            // Reserve inventory
            inventoryService.reserveDevice(event.serialNumber)
            
            // Publish success event
            eventPublisher.publish(
                InventoryReservedEvent(
                    deviceId = event.deviceId,
                    serialNumber = event.serialNumber
                )
            )
            
        } catch (e: Exception) {
            // Publish failure event (triggers compensation)
            eventPublisher.publish(
                InventoryReservationFailedEvent(
                    deviceId = event.deviceId,
                    reason = e.message ?: "Unknown error"
                )
            )
        }
    }
}

// Compensation: Device Service listens for failure events
@ComponentInline
class CompensationHandler(
    private val deviceService: DeviceService,
    private val billingService: BillingService
) {
    
    @KafkaListener(topics = ["billing-events"])
    fun handleSubscriptionFailed(event: SubscriptionCreationFailedEvent) {
        // Compensate: Delete registered device
        deviceService.deleteDevice(event.deviceId)
    }
    
    @KafkaListener(topics = ["inventory-events"])
    fun handleInventoryFailed(event: InventoryReservationFailedEvent) {
        // Compensate: Cancel subscription and delete device
        billingService.cancelSubscription(event.deviceId)
        deviceService.deleteDevice(event.deviceId)
    }
}
```

### Pattern 3: State Machine Saga

```kotlin
// Define saga as state machine
enum class DeviceRegistrationState {
    STARTED,
    DEVICE_REGISTERED,
    SUBSCRIPTION_CREATED,
    INVENTORY_RESERVED,
    COMPLETED,
    FAILED,
    COMPENSATING_INVENTORY,
    COMPENSATING_SUBSCRIPTION,
    COMPENSATING_DEVICE,
    COMPENSATED
}

sealed class SagaEvent {
    data class DeviceRegistered(val deviceId: String) : SagaEvent()
    data class SubscriptionCreated(val subscriptionId: String) : SagaEvent()
    data class InventoryReserved(val serialNumber: String) : SagaEvent()
    data class StepFailed(val step: String, val reason: String) : SagaEvent()
}

@ComponentInline
class DeviceRegistrationStateMachine {
    
    fun transition(
        currentState: DeviceRegistrationState,
        event: SagaEvent
    ): DeviceRegistrationState {
        return when (currentState) {
            DeviceRegistrationState.STARTED -> {
                when (event) {
                    is SagaEvent.DeviceRegistered -> DeviceRegistrationState.DEVICE_REGISTERED
                    is SagaEvent.StepFailed -> DeviceRegistrationState.FAILED
                    else -> currentState
                }
            }
            
            DeviceRegistrationState.DEVICE_REGISTERED -> {
                when (event) {
                    is SagaEvent.SubscriptionCreated -> DeviceRegistrationState.SUBSCRIPTION_CREATED
                    is SagaEvent.StepFailed -> DeviceRegistrationState.COMPENSATING_DEVICE
                    else -> currentState
                }
            }
            
            DeviceRegistrationState.SUBSCRIPTION_CREATED -> {
                when (event) {
                    is SagaEvent.InventoryReserved -> DeviceRegistrationState.COMPLETED
                    is SagaEvent.StepFailed -> DeviceRegistrationState.COMPENSATING_SUBSCRIPTION
                    else -> currentState
                }
            }
            
            DeviceRegistrationState.COMPENSATING_SUBSCRIPTION -> {
                DeviceRegistrationState.COMPENSATING_DEVICE
            }
            
            DeviceRegistrationState.COMPENSATING_DEVICE -> {
                DeviceRegistrationState.COMPENSATED
            }
            
            else -> currentState
        }
    }
}
```

---

## <a name="real-examples"></a>5. Real-World Examples from SmartHome Hub

### Example 1: Device Registration Saga (Orchestration)

```kotlin
// Saga orchestrator with compensation
@Service
class DeviceRegistrationSagaOrchestrator(
    private val deviceService: DeviceService,
    private val billingService: BillingService,
    private val inventoryService: InventoryService,
    private val notificationService: NotificationService,
    private val sagaStateRepository: SagaStateRepository
) {
    
    fun execute(command: RegisterDeviceCommand): DeviceRegistrationResult {
        val sagaId = SagaId.generate()
        var sagaState = createInitialState(sagaId, command)
        
        var device: Device? = null
        var subscription: Subscription? = null
        var inventoryReservation: InventoryReservation? = null
        
        try {
            // Step 1: Register device
            device = deviceService.registerDevice(
                RegisterDeviceRequest(
                    premisesId = command.premisesId,
                    name = command.name,
                    serialNumber = command.serialNumber,
                    type = command.type
                )
            )
            sagaState = sagaState.recordStepCompleted("RegisterDevice", device.deviceId.value)
            sagaStateRepository.save(sagaState)
            
            // Step 2: Create subscription
            subscription = billingService.createSubscription(
                CreateSubscriptionRequest(
                    customerId = command.customerId,
                    tier = command.subscriptionTier,
                    deviceId = device.deviceId
                )
            )
            sagaState = sagaState.recordStepCompleted("CreateSubscription", subscription.subscriptionId.value)
            sagaStateRepository.save(sagaState)
            
            // Step 3: Reserve inventory
            inventoryReservation = inventoryService.reserveDevice(
                ReserveDeviceRequest(
                    serialNumber = command.serialNumber,
                    reservedFor = command.premisesId
                )
            )
            sagaState = sagaState.recordStepCompleted("ReserveInventory", inventoryReservation.reservationId)
            sagaStateRepository.save(sagaState)
            
            // Step 4: Send welcome notification
            notificationService.sendWelcomeEmail(
                email = command.email,
                deviceName = command.name
            )
            sagaState = sagaState.recordStepCompleted("SendNotification", "email-sent")
            
            // Mark saga as completed
            sagaState = sagaState.complete()
            sagaStateRepository.save(sagaState)
            
            return DeviceRegistrationResult.Success(device)
            
        } catch (e: Exception) {
            logger.error("Saga failed at step: ${sagaState.currentStep}", e)
            
            // Mark saga as failed
            sagaState = sagaState.fail(e.message ?: "Unknown error")
            sagaStateRepository.save(sagaState)
            
            // Execute compensation
            compensate(sagaState, device, subscription, inventoryReservation)
            
            return DeviceRegistrationResult.Failed(
                reason = e.message ?: "Device registration failed",
                sagaId = sagaId
            )
        }
    }
    
    private fun compensate(
        sagaState: SagaState,
        device: Device?,
        subscription: Subscription?,
        inventoryReservation: InventoryReservation?
    ) {
        logger.info("Starting compensation for saga ${sagaState.sagaId}")
        
        var compensationState = sagaState.startCompensation()
        sagaStateRepository.save(compensationState)
        
        // Compensate in reverse order
        
        // Step 4 compensation: Notification (no compensation needed)
        
        // Step 3 compensation: Release inventory
        if (inventoryReservation != null) {
            try {
                inventoryService.releaseReservation(inventoryReservation.reservationId)
                compensationState = compensationState.recordCompensationCompleted("ReserveInventory")
                sagaStateRepository.save(compensationState)
            } catch (e: Exception) {
                logger.error("Failed to compensate inventory", e)
                // Continue with other compensations
            }
        }
        
        // Step 2 compensation: Cancel subscription
        if (subscription != null) {
            try {
                billingService.cancelSubscription(subscription.subscriptionId)
                compensationState = compensationState.recordCompensationCompleted("CreateSubscription")
                sagaStateRepository.save(compensationState)
            } catch (e: Exception) {
                logger.error("Failed to compensate subscription", e)
                // Continue with other compensations
            }
        }
        
        // Step 1 compensation: Delete device
        if (device != null) {
            try {
                deviceService.deleteDevice(device.deviceId)
                compensationState = compensationState.recordCompensationCompleted("RegisterDevice")
                sagaStateRepository.save(compensationState)
            } catch (e: Exception) {
                logger.error("Failed to compensate device", e)
            }
        }
        
        // Mark compensation as complete
        compensationState = compensationState.completeCompensation()
        sagaStateRepository.save(compensationState)
        
        logger.info("Compensation completed for saga ${sagaState.sagaId}")
    }
    
    private fun createInitialState(sagaId: SagaId, command: RegisterDeviceCommand): SagaState {
        return SagaState(
            sagaId = sagaId,
            sagaType = "DeviceRegistration",
            status = SagaStatus.STARTED,
            completedSteps = emptyMap(),
            compensatedSteps = emptySet(),
            currentStep = "RegisterDevice",
            payload = mapOf(
                "premisesId" to command.premisesId.value,
                "serialNumber" to command.serialNumber.value,
                "customerId" to command.customerId.value
            ),
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            error = null
        )
    }
}

// Saga state tracking
data class SagaState(
    val sagaId: SagaId,
    val sagaType: String,
    val status: SagaStatus,
    val completedSteps: Map<String, String>,  // stepName -> result
    val compensatedSteps: Set<String>,
    val currentStep: String?,
    val payload: Map<String, String>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val error: String?
) {
    fun recordStepCompleted(stepName: String, result: String): SagaState {
        return copy(
            completedSteps = completedSteps + (stepName to result),
            currentStep = getNextStep(stepName),
            updatedAt = Instant.now()
        )
    }
    
    fun complete(): SagaState {
        return copy(
            status = SagaStatus.COMPLETED,
            currentStep = null,
            updatedAt = Instant.now()
        )
    }
    
    fun fail(error: String): SagaState {
        return copy(
            status = SagaStatus.FAILED,
            error = error,
            updatedAt = Instant.now()
        )
    }
    
    fun startCompensation(): SagaState {
        return copy(
            status = SagaStatus.COMPENSATING,
            updatedAt = Instant.now()
        )
    }
    
    fun recordCompensationCompleted(stepName: String): SagaState {
        return copy(
            compensatedSteps = compensatedSteps + stepName,
            updatedAt = Instant.now()
        )
    }
    
    fun completeCompensation(): SagaState {
        return copy(
            status = SagaStatus.COMPENSATED,
            updatedAt = Instant.now()
        )
    }
    
    private fun getNextStep(currentStep: String): String? {
        return when (currentStep) {
            "RegisterDevice" -> "CreateSubscription"
            "CreateSubscription" -> "ReserveInventory"
            "ReserveInventory" -> "SendNotification"
            else -> null
        }
    }
}

enum class SagaStatus {
    STARTED,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    COMPENSATING,
    COMPENSATED
}
```

### Example 2: Device Transfer Saga (Cross-Premises)

```kotlin
// Transfer device between premises (complex saga)
@Service
class DeviceTransferSaga(
    private val deviceService: DeviceService,
    private val billingService: BillingService,
    private val notificationService: NotificationService,
    private val sagaStateRepository: SagaStateRepository
) {
    
    fun execute(command: TransferDeviceCommand): TransferResult {
        val sagaId = SagaId.generate()
        var sagaState = createInitialState(sagaId, command)
        
        var fromPremisesDeviceCount: Int? = null
        var toPremisesDeviceCount: Int? = null
        var transferredDevice: Device? = null
        
        try {
            // Step 1: Verify source premises can release device
            fromPremisesDeviceCount = billingService.getActiveDeviceCount(command.fromPremisesId)
            sagaState = sagaState.recordStepCompleted("VerifySource", fromPremisesDeviceCount.toString())
            sagaStateRepository.save(sagaState)
            
            // Step 2: Verify destination premises can accept device
            toPremisesDeviceCount = billingService.getActiveDeviceCount(command.toPremisesId)
            val destinationSubscription = billingService.getSubscription(command.toPremisesId)
            
            if (toPremisesDeviceCount >= destinationSubscription.maxDevices) {
                throw SagaException("Destination premises has reached device limit")
            }
            sagaState = sagaState.recordStepCompleted("VerifyDestination", toPremisesDeviceCount.toString())
            sagaStateRepository.save(sagaState)
            
            // Step 3: Deactivate device in source premises
            deviceService.deactivateDevice(command.deviceId)
            sagaState = sagaState.recordStepCompleted("DeactivateDevice", command.deviceId.value)
            sagaStateRepository.save(sagaState)
            
            // Step 4: Update device premises
            transferredDevice = deviceService.transferDevice(
                deviceId = command.deviceId,
                fromPremisesId = command.fromPremisesId,
                toPremisesId = command.toPremisesId,
                transferredBy = command.actorId
            )
            sagaState = sagaState.recordStepCompleted("TransferDevice", transferredDevice.deviceId.value)
            sagaStateRepository.save(sagaState)
            
            // Step 5: Update billing for source premises
            billingService.decrementDeviceCount(command.fromPremisesId)
            sagaState = sagaState.recordStepCompleted("UpdateSourceBilling", "decremented")
            sagaStateRepository.save(sagaState)
            
            // Step 6: Update billing for destination premises
            billingService.incrementDeviceCount(command.toPremisesId)
            sagaState = sagaState.recordStepCompleted("UpdateDestinationBilling", "incremented")
            sagaStateRepository.save(sagaState)
            
            // Step 7: Notify both premises
            notificationService.notifyDeviceTransferred(
                fromPremisesId = command.fromPremisesId,
                toPremisesId = command.toPremisesId,
                deviceName = transferredDevice.getName()
            )
            sagaState = sagaState.recordStepCompleted("SendNotifications", "sent")
            
            // Complete saga
            sagaState = sagaState.complete()
            sagaStateRepository.save(sagaState)
            
            return TransferResult.Success(transferredDevice)
            
        } catch (e: Exception) {
            logger.error("Device transfer saga failed", e)
            
            sagaState = sagaState.fail(e.message ?: "Transfer failed")
            sagaStateRepository.save(sagaState)
            
            // Compensate
            compensateTransfer(sagaState, command, transferredDevice)
            
            return TransferResult.Failed(e.message ?: "Transfer failed")
        }
    }
    
    private fun compensateTransfer(
        sagaState: SagaState,
        command: TransferDeviceCommand,
        transferredDevice: Device?
    ) {
        logger.info("Compensating device transfer saga ${sagaState.sagaId}")
        
        var compensationState = sagaState.startCompensation()
        sagaStateRepository.save(compensationState)
        
        // Compensate in reverse order
        
        if (sagaState.completedSteps.containsKey("UpdateDestinationBilling")) {
            try {
                billingService.decrementDeviceCount(command.toPremisesId)
                compensationState = compensationState.recordCompensationCompleted("UpdateDestinationBilling")
                sagaStateRepository.save(compensationState)
            } catch (e: Exception) {
                logger.error("Failed to compensate destination billing", e)
            }
        }
        
        if (sagaState.completedSteps.containsKey("UpdateSourceBilling")) {
            try {
                billingService.incrementDeviceCount(command.fromPremisesId)
                compensationState = compensationState.recordCompensationCompleted("UpdateSourceBilling")
                sagaStateRepository.save(compensationState)
            } catch (e: Exception) {
                logger.error("Failed to compensate source billing", e)
            }
        }
        
        if (sagaState.completedSteps.containsKey("TransferDevice") && transferredDevice != null) {
            try {
                // Transfer back
                deviceService.transferDevice(
                    deviceId = command.deviceId,
                    fromPremisesId = command.toPremisesId,
                    toPremisesId = command.fromPremisesId,
                    transferredBy = command.actorId
                )
                compensationState = compensationState.recordCompensationCompleted("TransferDevice")
                sagaStateRepository.save(compensationState)
            } catch (e: Exception) {
                logger.error("Failed to compensate device transfer", e)
            }
        }
        
        if (sagaState.completedSteps.containsKey("DeactivateDevice")) {
            try {
                deviceService.activateDevice(command.deviceId)
                compensationState = compensationState.recordCompensationCompleted("DeactivateDevice")
                sagaStateRepository.save(compensationState)
            } catch (e: Exception) {
                logger.error("Failed to compensate device deactivation", e)
            }
        }
        
        compensationState = compensationState.completeCompensation()
        sagaStateRepository.save(compensationState)
        
        logger.info("Device transfer compensation completed")
    }
}
```

---

## <a name="compensation"></a>6. Compensation Logic

### Semantic Compensation

Not all actions can be truly undone - use semantic compensation:

```kotlin
// Can't truly undo payment, but can refund
interface PaymentCompensation {
    // Forward action
    fun chargePayment(amount: Money): PaymentResult
    
    // Compensation (not exact undo, but semantically equivalent)
    fun refundPayment(transactionId: TransactionId): RefundResult
}

// Can't undo email sent, but can send cancellation email
interface NotificationCompensation {
    // Forward action
    fun sendOrderConfirmation(email: Email, orderId: OrderId)
    
    // Compensation
    fun sendOrderCancellation(email: Email, orderId: OrderId)
}

// Can undo reservation
interface InventoryCompensation {
    // Forward action
    fun reserveItem(itemId: ItemId): Reservation
    
    // Compensation (exact undo)
    fun releaseReservation(reservationId: ReservationId)
}
```

### Pivot Transaction

Use pivot transaction pattern - no compensation after this point:

```kotlin
@Service
class OrderSaga {
    
    fun execute(command: CreateOrderCommand): OrderResult {
        try {
            // Step 1: Reserve inventory (can compensate)
            val reservation = inventoryService.reserve(command.items)
            
            // Step 2: Authorize payment (can compensate)
            val authorization = paymentService.authorize(command.payment)
            
            // ===== PIVOT TRANSACTION =====
            // Step 3: Capture payment (CANNOT compensate - pivot!)
            val payment = paymentService.capture(authorization)
            // Once payment is captured, we're committed!
            
            // Step 4: Create order (must succeed)
            val order = orderService.create(command, payment)
            
            // Step 5: Confirm inventory (must succeed)
            inventoryService.confirm(reservation)
            
            // Step 6: Send confirmation (best effort)
            notificationService.sendConfirmation(order)
            
            return OrderResult.Success(order)
            
        } catch (e: Exception) {
            // Determine if we passed pivot point
            if (passedPivot) {
                // Can't compensate - log and alert
                logger.error("Order failed after pivot - manual intervention needed", e)
                alertService.sendCriticalAlert("Order saga failed after payment capture")
                // Try to complete remaining steps with retries
            } else {
                // Can compensate - execute compensation
                compensate()
            }
            
            return OrderResult.Failed(e.message)
        }
    }
}
```

### Idempotent Compensation

Ensure compensation can be called multiple times:

```kotlin
@Service
class IdempotentBillingService(
    private val subscriptionRepository: SubscriptionRepository
) {
    
    // Idempotent - can call multiple times safely
    fun cancelSubscription(subscriptionId: SubscriptionId) {
        val subscription = subscriptionRepository.findById(subscriptionId)
            ?: return  // Already doesn't exist - idempotent ✓
        
        if (subscription.status == SubscriptionStatus.CANCELLED) {
            // Already cancelled - idempotent ✓
            logger.info("Subscription $subscriptionId already cancelled")
            return
        }
        
        // Cancel subscription
        val cancelled = subscription.cancel()
        subscriptionRepository.save(cancelled)
    }
}
```

---

## <a name="state-management"></a>7. Saga State Management

### Persistent Saga State

```kotlin
// Store saga state in database
@Document("saga_state")
data class SagaStateDocument(
    @Id val sagaId: String,
    val sagaType: String,
    val status: String,
    val completedSteps: Map<String, String>,
    val compensatedSteps: Set<String>,
    val currentStep: String?,
    val payload: Map<String, Any>,
    val createdAt: Long,
    val updatedAt: Long,
    val error: String?,
    
    @Version val version: Long?  // Optimistic locking
)

@Repository
interface SagaStateRepository : MongoRepository<SagaStateDocument, String> {
    fun findBySagaTypeAndStatus(sagaType: String, status: String): List<SagaStateDocument>
    fun findByStatusAndUpdatedAtBefore(status: String, before: Long): List<SagaStateDocument>
}

// Recovery mechanism for stuck sagas
@ComponentInline
class SagaRecoveryService(
    private val sagaStateRepository: SagaStateRepository,
    private val deviceRegistrationSaga: DeviceRegistrationSagaOrchestrator
) {
    
    @Scheduled(fixedDelay = 60000)  // Every minute
    fun recoverStuckSagas() {
        val oneHourAgo = Instant.now().minus(Duration.ofHours(1)).toEpochMilli()
        
        // Find sagas stuck in progress for over 1 hour
        val stuckSagas = sagaStateRepository.findByStatusAndUpdatedAtBefore(
            "IN_PROGRESS",
            oneHourAgo
        )
        
        stuckSagas.forEach { sagaState ->
            logger.warn("Found stuck saga: ${sagaState.sagaId}")
            
            try {
                // Attempt to recover or compensate
                recoverSaga(sagaState)
            } catch (e: Exception) {
                logger.error("Failed to recover saga ${sagaState.sagaId}", e)
            }
        }
    }
    
    private fun recoverSaga(sagaState: SagaStateDocument) {
        when (sagaState.sagaType) {
            "DeviceRegistration" -> {
                // Attempt to resume or compensate device registration saga
                logger.info("Attempting to recover DeviceRegistration saga ${sagaState.sagaId}")
                
                // Check if can resume or need to compensate
                if (canResume(sagaState)) {
                    resumeSaga(sagaState)
                } else {
                    compensateSaga(sagaState)
                }
            }
        }
    }
}
```

---

## <a name="error-handling"></a>8. Error Handling and Retries

### Retry Policy

```kotlin
// Retry with exponential backoff
@ComponentInline
class RetryableSagaStep {
    
    fun <T> executeWithRetry(
        stepName: String,
        maxRetries: Int = 3,
        initialDelay: Duration = Duration.ofSeconds(1),
        action: () -> T
    ): T {
        var lastException: Exception? = null
        var delay = initialDelay
        
        repeat(maxRetries) { attempt ->
            try {
                return action()
            } catch (e: Exception) {
                lastException = e
                logger.warn("Step $stepName failed (attempt ${attempt + 1}/$maxRetries)", e)
                
                if (attempt < maxRetries - 1) {
                    // Wait before retry with exponential backoff
                    Thread.sleep(delay.toMillis())
                    delay = delay.multipliedBy(2)
                }
            }
        }
        
        throw lastException ?: RuntimeException("Step $stepName failed")
    }
}

// Usage in saga
@Service
class ResilientDeviceRegistrationSaga(
    private val retryableStep: RetryableSagaStep,
    private val deviceService: DeviceService
) {
    
    fun execute(command: RegisterDeviceCommand): SagaResult {
        try {
            // Step with retry
            val device = retryableStep.executeWithRetry("RegisterDevice") {
                deviceService.registerDevice(command)
            }
            
            // More steps...
            
            return SagaResult.Success(device)
        } catch (e: Exception) {
            // All retries exhausted - compensate
            compensate()
            return SagaResult.Failed(e.message)
        }
    }
}
```

### Timeout Handling

```kotlin
// Execute step with timeout
@ComponentInline
class TimeLimitedSagaStep {
    
    fun <T> executeWithTimeout(
        stepName: String,
        timeout: Duration = Duration.ofSeconds(30),
        action: () -> T
    ): T {
        val future = CompletableFuture.supplyAsync {
            action()
        }
        
        return try {
            future.get(timeout.toMillis(), TimeUnit.MILLISECONDS)
        } catch (e: TimeoutException) {
            logger.error("Step $stepName timed out after $timeout")
            throw SagaStepTimeoutException("Step $stepName timed out", stepName)
        }
    }
}
```

---

## <a name="testing"></a>9. Testing Sagas

### Unit Testing Saga Orchestrator

```kotlin
class DeviceRegistrationSagaTest {
    
    private lateinit var saga: DeviceRegistrationSagaOrchestrator
    private lateinit var deviceService: DeviceService
    private lateinit var billingService: BillingService
    private lateinit var inventoryService: InventoryService
    private lateinit var sagaStateRepository: SagaStateRepository
    
    @BeforeEach
    fun setup() {
        deviceService = mockk()
        billingService = mockk()
        inventoryService = mockk()
        sagaStateRepository = InMemorySagaStateRepository()
        
        saga = DeviceRegistrationSagaOrchestrator(
            deviceService,
            billingService,
            inventoryService,
            mockk(relaxed = true),
            sagaStateRepository
        )
    }
    
    @Test
    fun `should complete saga successfully`() {
        // Given
        val command = RegisterDeviceCommand(...)
        val device = Device.register(...)
        val subscription = Subscription.create(...)
        val reservation = InventoryReservation(...)
        
        every { deviceService.registerDevice(any()) } returns device
        every { billingService.createSubscription(any()) } returns subscription
        every { inventoryService.reserveDevice(any()) } returns reservation
        
        // When
        val result = saga.execute(command)
        
        // Then
        assertTrue(result is DeviceRegistrationResult.Success)
        
        // Verify all steps called
        verify { deviceService.registerDevice(any()) }
        verify { billingService.createSubscription(any()) }
        verify { inventoryService.reserveDevice(any()) }
        
        // Verify saga state
        val sagaState = sagaStateRepository.findAll().first()
        assertEquals(SagaStatus.COMPLETED, sagaState.status)
        assertEquals(3, sagaState.completedSteps.size)
    }
    
    @Test
    fun `should compensate when billing fails`() {
        // Given
        val command = RegisterDeviceCommand(...)
        val device = Device.register(...)
        
        every { deviceService.registerDevice(any()) } returns device
        every { billingService.createSubscription(any()) } throws BillingException("Payment failed")
        every { deviceService.deleteDevice(any()) } just Runs
        
        // When
        val result = saga.execute(command)
        
        // Then
        assertTrue(result is DeviceRegistrationResult.Failed)
        
        // Verify compensation called
        verify { deviceService.deleteDevice(device.deviceId) }
        
        // Verify saga state
        val sagaState = sagaStateRepository.findAll().first()
        assertEquals(SagaStatus.COMPENSATED, sagaState.status)
        assertTrue(sagaState.compensatedSteps.contains("RegisterDevice"))
    }
    
    @Test
    fun `should compensate all completed steps when inventory fails`() {
        // Given
        val command = RegisterDeviceCommand(...)
        val device = Device.register(...)
        val subscription = Subscription.create(...)
        
        every { deviceService.registerDevice(any()) } returns device
        every { billingService.createSubscription(any()) } returns subscription
        every { inventoryService.reserveDevice(any()) } throws InventoryException("Out of stock")
        every { billingService.cancelSubscription(any()) } just Runs
        every { deviceService.deleteDevice(any()) } just Runs
        
        // When
        val result = saga.execute(command)
        
        // Then
        assertTrue(result is DeviceRegistrationResult.Failed)
        
        // Verify all compensations called in reverse order
        verifyOrder {
            billingService.cancelSubscription(subscription.subscriptionId)
            deviceService.deleteDevice(device.deviceId)
        }
    }
}
```

### Integration Testing with Test Containers

```kotlin
@SpringBootTest
@Testcontainers
class DeviceRegistrationSagaIntegrationTest {
    
    @Container
    val mongodb = MongoDBContainer("mongo:6.0")
    
    @Container
    val kafka = KafkaContainer("7.0.0")
    
    @Autowired
    lateinit var saga: DeviceRegistrationSagaOrchestrator
    
    @Autowired
    lateinit var deviceRepository: DeviceRepository
    
    @Autowired
    lateinit var subscriptionRepository: SubscriptionRepository
    
    @Test
    fun `should complete full saga end-to-end`() {
        // Given
        val command = RegisterDeviceCommand(...)
        
        // When
        val result = saga.execute(command)
        
        // Then
        assertTrue(result is DeviceRegistrationResult.Success)
        
        // Verify device created
        val device = deviceRepository.findById((result as DeviceRegistrationResult.Success).device.deviceId)
        assertNotNull(device)
        
        // Verify subscription created
        val subscriptions = subscriptionRepository.findByCustomerId(command.customerId)
        assertEquals(1, subscriptions.size)
    }
}
```

---

## 💡 Key Takeaways

1. **Sagas manage distributed transactions** - Without distributed locking

2. **Each step is local transaction** - Separate databases OK

3. **Compensation for failures** - Undo completed steps

4. **Orchestration = centralized** - Easier to understand

5. **Choreography = decentralized** - Looser coupling

6. **Track saga state** - Essential for recovery

7. **Idempotent operations** - Can retry safely

8. **Pivot transaction** - Point of no return

9. **Semantic compensation** - Not always perfect undo

10. **Test compensation logic** - Critical for reliability

---

## 🎯 Practical Exercise

Add saga to your system:

1. **Identify distributed transaction** that needs saga
2. **Choose orchestration or choreography**
3. **Define saga steps** with compensations
4. **Implement saga orchestrator** or event handlers
5. **Add state tracking** for recovery
6. **Implement compensation logic** for each step
7. **Add retry and timeout** handling
8. **Test happy path** and failure scenarios
9. **Test compensation** works correctly

---

## 📚 What We've Covered

In this chapter, you learned:

✅ The distributed transaction problem  
✅ What sagas are and how they work  
✅ Orchestration vs choreography patterns  
✅ Implementation patterns (command, event, state machine)  
✅ Real examples from SmartHome Hub  
✅ Compensation logic and pivot transactions  
✅ Saga state management and recovery  
✅ Error handling and retries  
✅ Testing strategies  

---

## 🎊 STRATEGIC PATTERNS COMPLETE!

Congratulations! You've completed all strategic patterns:
- ✅ Chapter 9: Bounded Contexts
- ✅ Chapter 10: Anti-Corruption Layer
- ✅ Chapter 11: Domain & Integration Events
- ✅ Chapter 12: Saga Pattern

**Next:** Advanced topics and real-world implementation!

---

## 🚀 Next Chapter

Ready for advanced patterns?

👉 **[Chapter 13: CQRS Pattern - Separating Reads and Writes](./13-cqrs-pattern.md)**

**You'll learn:**
- Command Query Responsibility Segregation
- When to use CQRS
- Implementation patterns
- Read models vs write models
- Real CQRS examples

**Reading Time:** 24 minutes  
**Difficulty:** Advanced  

---

**Questions?** Open an issue in the repository!

**Found this helpful?** Star and share with your team!

---

*"A saga is a sequence of local transactions coordinated using messages or events."  
— Chris Richardson, Microservices Patterns*

