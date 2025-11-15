# Chapter 6: Policy Pattern - Centralizing Business Rules

**Series:** Building an Enterprise IoT Platform with DDD  
**Part:** 2 of 20 - Tactical Patterns  
**Reading Time:** 20 minutes  
**Level:** Intermediate  

---

## 📋 Table of Contents

1. [The Problem: Scattered Business Rules](#the-problem)
2. [What is the Policy Pattern?](#what-is-policy)
3. [Policy vs Specification](#policy-vs-specification)
4. [Real-World Examples from SmartHome Hub](#real-examples)
5. [Policy Chain Pattern](#policy-chain)
6. [Policy with Result Pattern](#policy-result)
7. [Advanced Policy Patterns](#advanced-patterns)
8. [Common Pitfalls](#pitfalls)
9. [Testing Policies](#testing)

---

## <a name="the-problem"></a>1. The Problem: Scattered Business Rules

### The Scenario: The Compliance Disaster

You're working on SmartHome Hub when a critical security audit reveals a major issue:

> **Audit Finding #001:** "Device registration rules are inconsistent across the application. Found 7 different validation implementations with conflicting requirements. Critical security vulnerability."

You investigate and discover business rules scattered everywhere:

```kotlin
// Location 1: DeviceController.kt
@PostMapping("/register")
fun registerDevice(@RequestBody request: DeviceRegistrationRequest): Device {
    // Rule: Max 100 devices per premises
    val deviceCount = deviceRepository.countByPremisesId(request.premisesId)
    if (deviceCount >= 100) {
        throw MaxDevicesExceededException("Maximum 100 devices allowed")
    }
    
    // Rule: Serial number must be unique
    val existing = deviceRepository.findBySerialNumber(request.serialNumber)
    if (existing != null) {
        throw DuplicateSerialNumberException("Serial number already registered")
    }
    
    return deviceService.registerDevice(request)
}

// Location 2: DeviceService.kt
@Service
class DeviceService {
    fun registerDevice(request: DeviceRegistrationRequest): Device {
        // Same rules duplicated! 😱
        val deviceCount = deviceRepository.countByPremisesId(request.premisesId)
        if (deviceCount >= 100) {  // Different error message
            throw BusinessException("Cannot register more devices")
        }
        
        // Different validation logic!
        if (request.serialNumber.length < 8) {
            throw ValidationException("Serial number too short")
        }
        
        // Additional rule only here
        val premises = premisesRepository.findById(request.premisesId)
        if (premises.subscriptionTier == SubscriptionTier.FREE && deviceCount >= 10) {
            throw SubscriptionLimitException("Free tier limited to 10 devices")
        }
        
        // ... more logic
    }
}

// Location 3: DeviceValidator.kt
@Component
class DeviceValidator {
    fun validateDeviceRegistration(request: DeviceRegistrationRequest) {
        // Yet another implementation! 😱
        val deviceCount = deviceRepository.countByPremisesId(request.premisesId)
        
        // Different limit here!
        if (deviceCount >= 50) {  // Wait, is it 50 or 100? 🤔
            throw ValidationException("Too many devices")
        }
        
        // Different serial number validation
        if (!request.serialNumber.matches(Regex("^[A-Z0-9]{8,20}$"))) {
            throw ValidationException("Invalid serial number format")
        }
    }
}

// Location 4: RegisterDeviceUseCase.kt
@Service
class RegisterDeviceUseCase {
    fun execute(command: RegisterDeviceCommand): Device {
        // More duplication! 😱
        val premises = premisesRepository.findById(command.premisesId)
        val deviceCount = deviceRepository.countByPremisesId(command.premisesId)
        
        // Another different limit!
        val maxDevices = when (premises.subscriptionTier) {
            SubscriptionTier.FREE -> 10
            SubscriptionTier.BASIC -> 50
            SubscriptionTier.PRO -> 100
            SubscriptionTier.ENTERPRISE -> 500
        }
        
        if (deviceCount >= maxDevices) {
            throw BusinessException("Subscription limit reached")
        }
        
        // ... rest of logic
    }
}

// Location 5: DeviceRegistrationEventHandler.kt
@EventHandler
class DeviceRegistrationEventHandler {
    fun handle(event: DeviceRegistrationRequestedEvent) {
        // Yet more validation! 😱
        val deviceCount = deviceRepository.countByPremisesId(event.premisesId)
        
        // Another variation!
        if (deviceCount > 100) {  // >= vs > difference!
            rejectRegistration(event, "Device limit exceeded")
        }
    }
}

// Location 6: BulkDeviceImporter.kt
@Service
class BulkDeviceImporter {
    fun importDevices(premisesId: PremisesId, devices: List<DeviceData>) {
        // Completely different logic! 😱
        val currentCount = deviceRepository.countByPremisesId(premisesId)
        
        // No limit check at all! 💥
        devices.forEach { deviceData ->
            registerDevice(deviceData)
        }
    }
}

// Location 7: API Gateway (different service!)
// External validation before reaching our service
if (request.devices.size > 200) {
    return error("Cannot register more than 200 devices")
}
```

### The Real Impact

**Problems discovered:**

1. **Inconsistent Limits** - Found 6 different device limits: 10, 50, 100, 200, 500, unlimited
2. **Duplicate Code** - Same validation in 7 different places
3. **Conflicting Logic** - `>=` vs `>`, different error messages
4. **Missing Validation** - Bulk import bypasses all checks
5. **No Single Source of Truth** - Business rules scattered everywhere
6. **Security Vulnerability** - Inconsistent validation = security holes

**Cost:**
- 🔴 Failed security audit
- 🔴 Production incident (user registered 1000+ devices)
- 🔴 2 days emergency fix
- 🔴 Compliance fine risk
- 🔴 Customer trust damaged

**Root Cause:** No centralized business rule management.

---

## <a name="what-is-policy"></a>2. What is the Policy Pattern?

### Definition

> **Policy Pattern:** A pattern that encapsulates business rules as first-class objects that can evaluate conditions, make decisions, and provide clear feedback about why a rule passed or failed.
> 
> — Domain-Driven Design Community

### The Core Idea

Instead of scattering business rules everywhere, we:
- **Centralize rules** in dedicated policy objects
- **Evaluate consistently** across the application
- **Get clear feedback** about pass/fail reasons
- **Change rules once** - affect everywhere

### Basic Policy Interface

```kotlin
// Policy.kt
interface Policy<T> {
    /**
     * Evaluate if the target satisfies this policy
     */
    fun evaluate(target: T): PolicyResult
}

// PolicyResult.kt
sealed class PolicyResult {
    abstract val isValid: Boolean
    abstract val violations: List<String>
    
    data class Valid(
        override val isValid: Boolean = true,
        override val violations: List<String> = emptyList()
    ) : PolicyResult()
    
    data class Invalid(
        override val isValid: Boolean = false,
        override val violations: List<String>
    ) : PolicyResult()
    
    companion object {
        fun valid() = Valid()
        
        fun invalid(vararg violations: String) = Invalid(
            violations = violations.toList()
        )
        
        fun invalid(violations: List<String>) = Invalid(
            violations = violations
        )
    }
}
```

### Simple Policy Example

```kotlin
// DeviceRegistrationPolicy.kt
class DeviceRegistrationPolicy(
    private val deviceRepository: DeviceRepository
) : Policy<RegisterDeviceCommand> {
    
    override fun evaluate(command: RegisterDeviceCommand): PolicyResult {
        val violations = mutableListOf<String>()
        
        // Rule 1: Check device limit
        val deviceCount = deviceRepository.countByPremisesId(command.premisesId)
        if (deviceCount >= MAX_DEVICES_PER_PREMISES) {
            violations.add(
                "Maximum device limit reached ($MAX_DEVICES_PER_PREMISES devices). " +
                "Current count: $deviceCount"
            )
        }
        
        // Rule 2: Check serial number uniqueness
        val existing = deviceRepository.findBySerialNumber(command.serialNumber)
        if (existing != null) {
            violations.add(
                "Serial number ${command.serialNumber} is already registered " +
                "to device ${existing.deviceId}"
            )
        }
        
        return if (violations.isEmpty()) {
            PolicyResult.valid()
        } else {
            PolicyResult.invalid(violations)
        }
    }
    
    companion object {
        const val MAX_DEVICES_PER_PREMISES = 100
    }
}

// Usage in Use Case
@Service
class RegisterDeviceUseCase(
    private val deviceRepository: DeviceRepository,
    private val deviceRegistrationPolicy: DeviceRegistrationPolicy
) {
    fun execute(command: RegisterDeviceCommand): Device {
        // Evaluate policy - SINGLE SOURCE OF TRUTH ✅
        val policyResult = deviceRegistrationPolicy.evaluate(command)
        
        if (!policyResult.isValid) {
            throw PolicyViolationException(policyResult.violations)
        }
        
        // Policy passed, proceed with registration
        val device = Device.register(...)
        return deviceRepository.save(device)
    }
}
```

---

## <a name="policy-vs-specification"></a>3. Policy vs Specification

### Key Differences

| Aspect | Specification | Policy |
|--------|---------------|--------|
| **Purpose** | Query/filter entities | Validate business rules |
| **Return** | Boolean (true/false) | Rich result (with reasons) |
| **Composition** | AND/OR/NOT | Chain, combine |
| **Context** | Single entity | Command, multiple entities |
| **Usage** | In-memory filter, queries | Validation, decisions |
| **Side Effects** | None (pure) | May query dependencies |

### Specification Example

```kotlin
// Specification - Filter/query
class DeviceIsActive : Specification<Device> {
    override fun isSatisfiedBy(device: Device): Boolean {
        return device.getStatus() == DeviceStatus.ACTIVE
    }
}

// Usage: Filter collection
val activeDevices = devices.filter { DeviceIsActive().isSatisfiedBy(it) }
```

### Policy Example

```kotlin
// Policy - Validate business rule
class DeviceActivationPolicy(
    private val deviceRepository: DeviceRepository,
    private val subscriptionService: SubscriptionService
) : Policy<ActivateDeviceCommand> {
    
    override fun evaluate(command: ActivateDeviceCommand): PolicyResult {
        val violations = mutableListOf<String>()
        
        // May query multiple sources
        val device = deviceRepository.findById(command.deviceId)
            ?: return PolicyResult.invalid("Device not found")
        
        val subscription = subscriptionService.getSubscription(device.premisesId)
        
        // Check multiple rules
        if (!subscription.isActive) {
            violations.add("Subscription is not active")
        }
        
        if (device.getFeeds().isEmpty()) {
            violations.add("Device must have at least one feed")
        }
        
        return if (violations.isEmpty()) {
            PolicyResult.valid()
        } else {
            PolicyResult.invalid(violations)
        }
    }
}

// Usage: Validate command
val result = deviceActivationPolicy.evaluate(command)
if (!result.isValid) {
    throw PolicyViolationException(result.violations)
}
```

### When to Use Which?

**Use Specification when:**
- Filtering collections in memory
- Building database queries
- Simple true/false checks
- No external dependencies

**Use Policy when:**
- Validating commands/operations
- Need detailed failure reasons
- Multiple rules to check
- Need to query external data
- Business decisions

---

## <a name="real-examples"></a>4. Real-World Examples from SmartHome Hub

### Policy 1: Device Registration Policy

```kotlin
// DeviceRegistrationPolicy.kt
class DeviceRegistrationPolicy(
    private val deviceRepository: DeviceRepository,
    private val premisesRepository: PremisesRepository,
    private val subscriptionService: SubscriptionService
) : Policy<RegisterDeviceCommand> {
    
    override fun evaluate(command: RegisterDeviceCommand): PolicyResult {
        val violations = mutableListOf<String>()
        
        // Rule 1: Premises must exist
        val premises = premisesRepository.findById(command.premisesId)
        if (premises == null) {
            return PolicyResult.invalid("Premises ${command.premisesId} not found")
        }
        
        // Rule 2: Subscription must be active
        val subscription = subscriptionService.getSubscription(command.premisesId)
        if (!subscription.isActive) {
            violations.add(
                "Cannot register device. Subscription for premises ${command.premisesId} " +
                "is ${subscription.status}. Please renew subscription."
            )
        }
        
        // Rule 3: Check subscription tier limits
        val deviceCount = deviceRepository.countByPremisesId(command.premisesId)
        val maxDevices = getMaxDevicesForTier(subscription.tier)
        
        if (deviceCount >= maxDevices) {
            violations.add(
                "Device limit reached for ${subscription.tier} tier. " +
                "Current: $deviceCount, Maximum: $maxDevices. " +
                "Upgrade subscription to add more devices."
            )
        }
        
        // Rule 4: Serial number must be unique
        val existing = deviceRepository.findBySerialNumber(command.serialNumber)
        if (existing != null) {
            violations.add(
                "Serial number ${command.serialNumber} is already registered. " +
                "Device ID: ${existing.deviceId}, Premises: ${existing.premisesId}"
            )
        }
        
        // Rule 5: Serial number format validation
        if (!isValidSerialNumberFormat(command.serialNumber)) {
            violations.add(
                "Invalid serial number format. Must be 8-20 alphanumeric characters. " +
                "Provided: ${command.serialNumber}"
            )
        }
        
        // Rule 6: Device name must be unique within premises
        val duplicateName = deviceRepository.findByPremisesIdAndName(
            command.premisesId,
            command.name
        )
        if (duplicateName != null) {
            violations.add(
                "Device name '${command.name}' already exists in this premises. " +
                "Please choose a different name."
            )
        }
        
        return if (violations.isEmpty()) {
            PolicyResult.valid()
        } else {
            PolicyResult.invalid(violations)
        }
    }
    
    private fun getMaxDevicesForTier(tier: SubscriptionTier): Int {
        return when (tier) {
            SubscriptionTier.FREE -> 10
            SubscriptionTier.BASIC -> 50
            SubscriptionTier.PRO -> 100
            SubscriptionTier.ENTERPRISE -> 500
        }
    }
    
    private fun isValidSerialNumberFormat(serialNumber: SerialNumber): Boolean {
        return serialNumber.value.matches(Regex("^[A-Z0-9]{8,20}$"))
    }
}
```

### Policy 2: Device Activation Policy

```kotlin
// DeviceActivationPolicy.kt
class DeviceActivationPolicy(
    private val deviceRepository: DeviceRepository,
    private val subscriptionService: SubscriptionService
) : Policy<ActivateDeviceCommand> {
    
    override fun evaluate(command: ActivateDeviceCommand): PolicyResult {
        val violations = mutableListOf<String>()
        
        // Rule 1: Device must exist
        val device = deviceRepository.findById(command.deviceId)
        if (device == null) {
            return PolicyResult.invalid("Device ${command.deviceId} not found")
        }
        
        // Rule 2: Device must be in PENDING status
        if (device.getStatus() != DeviceStatus.PENDING) {
            violations.add(
                "Device cannot be activated. Current status: ${device.getStatus()}. " +
                "Only PENDING devices can be activated."
            )
        }
        
        // Rule 3: Device must have at least one feed
        if (device.getFeeds().isEmpty()) {
            violations.add(
                "Device must have at least one feed before activation. " +
                "Please add feeds first."
            )
        }
        
        // Rule 4: Device must be activated within 24 hours of registration
        val hoursSinceRegistration = Duration.between(
            device.getCreatedAt(),
            Instant.now()
        ).toHours()
        
        if (hoursSinceRegistration > 24) {
            violations.add(
                "Device activation window expired. " +
                "Devices must be activated within 24 hours of registration. " +
                "Registered: ${device.getCreatedAt()}, " +
                "Time elapsed: $hoursSinceRegistration hours."
            )
        }
        
        // Rule 5: Subscription must still be active
        val subscription = subscriptionService.getSubscription(device.premisesId)
        if (!subscription.isActive) {
            violations.add(
                "Cannot activate device. Subscription is ${subscription.status}."
            )
        }
        
        // Rule 6: Check active device limit
        val activeDeviceCount = deviceRepository.countByPremisesIdAndStatus(
            device.premisesId,
            DeviceStatus.ACTIVE
        )
        val maxActiveDevices = getMaxActiveDevicesForTier(subscription.tier)
        
        if (activeDeviceCount >= maxActiveDevices) {
            violations.add(
                "Active device limit reached for ${subscription.tier} tier. " +
                "Current active: $activeDeviceCount, Maximum: $maxActiveDevices. " +
                "Please deactivate some devices or upgrade subscription."
            )
        }
        
        return if (violations.isEmpty()) {
            PolicyResult.valid()
        } else {
            PolicyResult.invalid(violations)
        }
    }
    
    private fun getMaxActiveDevicesForTier(tier: SubscriptionTier): Int {
        return when (tier) {
            SubscriptionTier.FREE -> 5
            SubscriptionTier.BASIC -> 25
            SubscriptionTier.PRO -> 50
            SubscriptionTier.ENTERPRISE -> 250
        }
    }
}
```

### Policy 3: Automation Creation Policy

```kotlin
// AutomationCreationPolicy.kt
class AutomationCreationPolicy(
    private val automationRepository: AutomationRepository,
    private val deviceRepository: DeviceRepository,
    private val subscriptionService: SubscriptionService
) : Policy<CreateAutomationCommand> {
    
    override fun evaluate(command: CreateAutomationCommand): PolicyResult {
        val violations = mutableListOf<String>()
        
        // Rule 1: Check automation limit for subscription tier
        val subscription = subscriptionService.getSubscription(command.premisesId)
        val automationCount = automationRepository.countByPremisesId(command.premisesId)
        val maxAutomations = getMaxAutomationsForTier(subscription.tier)
        
        if (automationCount >= maxAutomations) {
            violations.add(
                "Automation limit reached for ${subscription.tier} tier. " +
                "Current: $automationCount, Maximum: $maxAutomations."
            )
        }
        
        // Rule 2: All trigger devices must exist and be active
        command.triggerDeviceIds.forEach { deviceId ->
            val device = deviceRepository.findById(deviceId)
            when {
                device == null -> violations.add(
                    "Trigger device $deviceId not found"
                )
                device.getStatus() != DeviceStatus.ACTIVE -> violations.add(
                    "Trigger device $deviceId is not active (status: ${device.getStatus()})"
                )
                device.getHealth() == DeviceHealth.OFFLINE -> violations.add(
                    "Trigger device $deviceId is offline"
                )
                device.getFeeds().isEmpty() -> violations.add(
                    "Trigger device $deviceId has no feeds"
                )
            }
        }
        
        // Rule 3: All action devices must exist and be actuators
        command.actionDeviceIds.forEach { deviceId ->
            val device = deviceRepository.findById(deviceId)
            when {
                device == null -> violations.add(
                    "Action device $deviceId not found"
                )
                device.getType() != DeviceType.ACTUATOR -> violations.add(
                    "Action device $deviceId is not an actuator (type: ${device.getType()})"
                )
                device.getStatus() != DeviceStatus.ACTIVE -> violations.add(
                    "Action device $deviceId is not active"
                )
            }
        }
        
        // Rule 4: Must have at least one trigger
        if (command.triggerDeviceIds.isEmpty()) {
            violations.add("Automation must have at least one trigger device")
        }
        
        // Rule 5: Must have at least one action
        if (command.actionDeviceIds.isEmpty()) {
            violations.add("Automation must have at least one action device")
        }
        
        // Rule 6: Prevent infinite loops
        val wouldCreateLoop = checkForInfiniteLoop(
            command.triggerDeviceIds,
            command.actionDeviceIds,
            command.premisesId
        )
        
        if (wouldCreateLoop) {
            violations.add(
                "This automation would create an infinite loop. " +
                "An action device cannot trigger another automation that affects the trigger device."
            )
        }
        
        return if (violations.isEmpty()) {
            PolicyResult.valid()
        } else {
            PolicyResult.invalid(violations)
        }
    }
    
    private fun getMaxAutomationsForTier(tier: SubscriptionTier): Int {
        return when (tier) {
            SubscriptionTier.FREE -> 3
            SubscriptionTier.BASIC -> 10
            SubscriptionTier.PRO -> 50
            SubscriptionTier.ENTERPRISE -> Int.MAX_VALUE
        }
    }
    
    private fun checkForInfiniteLoop(
        triggerDeviceIds: List<DeviceId>,
        actionDeviceIds: List<DeviceId>,
        premisesId: PremisesId
    ): Boolean {
        // Get all existing automations
        val existingAutomations = automationRepository.findByPremisesId(premisesId)
        
        // Check if any action device is a trigger for another automation
        // that affects our trigger devices
        actionDeviceIds.forEach { actionDeviceId ->
            existingAutomations.forEach { automation ->
                if (automation.triggerDeviceIds.contains(actionDeviceId) &&
                    automation.actionDeviceIds.any { it in triggerDeviceIds }
                ) {
                    return true  // Would create loop
                }
            }
        }
        
        return false
    }
}
```

### Policy 4: Actor Permission Policy

```kotlin
// ActorPermissionPolicy.kt
class ActorPermissionPolicy(
    private val actorRepository: ActorRepository,
    private val deviceRepository: DeviceRepository
) : Policy<DeviceOperationCommand> {
    
    override fun evaluate(command: DeviceOperationCommand): PolicyResult {
        val violations = mutableListOf<String>()
        
        // Rule 1: Actor must exist
        val actor = actorRepository.findById(command.actorId)
        if (actor == null) {
            return PolicyResult.invalid("Actor ${command.actorId} not found")
        }
        
        // Rule 2: Actor must be active
        if (actor.status != ActorStatus.ACTIVE) {
            violations.add(
                "Actor account is ${actor.status}. Only active actors can perform operations."
            )
        }
        
        // Rule 3: Actor must have access to premises
        if (!actor.hasAccessTo(command.premisesId)) {
            violations.add(
                "Actor does not have access to premises ${command.premisesId}"
            )
        }
        
        // Rule 4: Device must exist and belong to premises
        val device = deviceRepository.findById(command.deviceId)
        if (device == null) {
            return PolicyResult.invalid("Device ${command.deviceId} not found")
        }
        
        if (device.premisesId != command.premisesId) {
            violations.add(
                "Device ${command.deviceId} does not belong to premises ${command.premisesId}"
            )
        }
        
        // Rule 5: Actor must have permission for operation type
        val hasPermission = when (command) {
            is ActivateDeviceCommand -> actor.canActivateDevices()
            is DeactivateDeviceCommand -> actor.canDeactivateDevices()
            is DeleteDeviceCommand -> actor.canDeleteDevices()
            is UpdateDeviceCommand -> actor.canUpdateDevices()
            else -> false
        }
        
        if (!hasPermission) {
            violations.add(
                "Actor role '${actor.role}' does not have permission for operation: " +
                "${command.javaClass.simpleName}"
            )
        }
        
        // Rule 6: Owner can't be removed as actor
        if (command is RemoveActorCommand && actor.id == command.premisesId.ownerId) {
            violations.add("Owner cannot be removed from premises")
        }
        
        return if (violations.isEmpty()) {
            PolicyResult.valid()
        } else {
            PolicyResult.invalid(violations)
        }
    }
}
```

---

## <a name="policy-chain"></a>5. Policy Chain Pattern

### The Problem: Multiple Policies to Check

```kotlin
// Without chain - manual checks everywhere ❌
fun registerDevice(command: RegisterDeviceCommand): Device {
    // Check policy 1
    val registrationResult = deviceRegistrationPolicy.evaluate(command)
    if (!registrationResult.isValid) {
        throw PolicyViolationException(registrationResult.violations)
    }
    
    // Check policy 2
    val securityResult = deviceSecurityPolicy.evaluate(command)
    if (!securityResult.isValid) {
        throw PolicyViolationException(securityResult.violations)
    }
    
    // Check policy 3
    val complianceResult = deviceCompliancePolicy.evaluate(command)
    if (!complianceResult.isValid) {
        throw PolicyViolationException(complianceResult.violations)
    }
    
    // Finally, do the work
    return actuallyRegisterDevice(command)
}
```

### The Solution: Policy Chain

```kotlin
// PolicyChain.kt
class PolicyChain<T>(
    private val policies: List<Policy<T>>
) : Policy<T> {
    
    override fun evaluate(target: T): PolicyResult {
        val allViolations = mutableListOf<String>()
        
        policies.forEach { policy ->
            val result = policy.evaluate(target)
            if (!result.isValid) {
                allViolations.addAll(result.violations)
            }
        }
        
        return if (allViolations.isEmpty()) {
            PolicyResult.valid()
        } else {
            PolicyResult.invalid(allViolations)
        }
    }
    
    companion object {
        fun <T> of(vararg policies: Policy<T>): PolicyChain<T> {
            return PolicyChain(policies.toList())
        }
    }
}

// Usage - Clean! ✅
@Service
class RegisterDeviceUseCase(
    private val deviceRegistrationPolicy: DeviceRegistrationPolicy,
    private val deviceSecurityPolicy: DeviceSecurityPolicy,
    private val deviceCompliancePolicy: DeviceCompliancePolicy,
    private val deviceRepository: DeviceRepository
) {
    
    private val policyChain = PolicyChain.of(
        deviceRegistrationPolicy,
        deviceSecurityPolicy,
        deviceCompliancePolicy
    )
    
    fun execute(command: RegisterDeviceCommand): Device {
        // One call checks all policies! ✅
        val result = policyChain.evaluate(command)
        
        if (!result.isValid) {
            throw PolicyViolationException(result.violations)
        }
        
        // All policies passed, proceed
        val device = Device.register(...)
        return deviceRepository.save(device)
    }
}
```

### Short-Circuit Policy Chain

Stop on first failure:

```kotlin
// ShortCircuitPolicyChain.kt
class ShortCircuitPolicyChain<T>(
    private val policies: List<Policy<T>>
) : Policy<T> {
    
    override fun evaluate(target: T): PolicyResult {
        policies.forEach { policy ->
            val result = policy.evaluate(target)
            if (!result.isValid) {
                // Return immediately on first failure
                return result
            }
        }
        
        return PolicyResult.valid()
    }
}
```

### Conditional Policy Chain

Execute policies conditionally:

```kotlin
// ConditionalPolicyChain.kt
class ConditionalPolicyChain<T>(
    private val policies: List<Pair<(T) -> Boolean, Policy<T>>>
) : Policy<T> {
    
    override fun evaluate(target: T): PolicyResult {
        val allViolations = mutableListOf<String>()
        
        policies.forEach { (condition, policy) ->
            // Only evaluate if condition is true
            if (condition(target)) {
                val result = policy.evaluate(target)
                if (!result.isValid) {
                    allViolations.addAll(result.violations)
                }
            }
        }
        
        return if (allViolations.isEmpty()) {
            PolicyResult.valid()
        } else {
            PolicyResult.invalid(allViolations)
        }
    }
}

// Usage
val chain = ConditionalPolicyChain(
    listOf(
        // Only check security policy for enterprise tier
        { cmd: RegisterDeviceCommand -> 
            cmd.subscriptionTier == SubscriptionTier.ENTERPRISE 
        } to deviceSecurityPolicy,
        
        // Always check compliance policy
        { _ -> true } to deviceCompliancePolicy
    )
)
```

---

## <a name="policy-result"></a>6. Policy with Result Pattern

### Enhanced Policy Result

```kotlin
// EnhancedPolicyResult.kt
sealed class EnhancedPolicyResult<out T> {
    abstract val isValid: Boolean
    abstract val violations: List<PolicyViolation>
    
    data class Valid<T>(
        val value: T,
        override val isValid: Boolean = true,
        override val violations: List<PolicyViolation> = emptyList()
    ) : EnhancedPolicyResult<T>()
    
    data class Invalid<T>(
        override val isValid: Boolean = false,
        override val violations: List<PolicyViolation>
    ) : EnhancedPolicyResult<T>()
    
    companion object {
        fun <T> valid(value: T) = Valid(value)
        fun <T> invalid(vararg violations: PolicyViolation) = 
            Invalid<T>(violations = violations.toList())
    }
}

// PolicyViolation.kt - Rich violation information
data class PolicyViolation(
    val code: String,
    val message: String,
    val severity: Severity = Severity.ERROR,
    val field: String? = null,
    val metadata: Map<String, Any> = emptyMap()
) {
    enum class Severity {
        ERROR,    // Must fix
        WARNING,  // Should fix
        INFO      // Nice to know
    }
}

// Enhanced Policy Interface
interface EnhancedPolicy<in T, out R> {
    fun evaluate(target: T): EnhancedPolicyResult<R>
}
```

### Example with Enhanced Result

```kotlin
// DeviceRegistrationEnhancedPolicy.kt
class DeviceRegistrationEnhancedPolicy(
    private val deviceRepository: DeviceRepository,
    private val subscriptionService: SubscriptionService
) : EnhancedPolicy<RegisterDeviceCommand, RegisterDeviceContext> {
    
    override fun evaluate(
        command: RegisterDeviceCommand
    ): EnhancedPolicyResult<RegisterDeviceContext> {
        val violations = mutableListOf<PolicyViolation>()
        
        // Load required data
        val deviceCount = deviceRepository.countByPremisesId(command.premisesId)
        val subscription = subscriptionService.getSubscription(command.premisesId)
        val maxDevices = getMaxDevicesForTier(subscription.tier)
        
        // Check limit
        if (deviceCount >= maxDevices) {
            violations.add(
                PolicyViolation(
                    code = "DEVICE_LIMIT_EXCEEDED",
                    message = "Device limit reached for ${subscription.tier} tier",
                    severity = PolicyViolation.Severity.ERROR,
                    field = "deviceCount",
                    metadata = mapOf(
                        "current" to deviceCount,
                        "maximum" to maxDevices,
                        "tier" to subscription.tier.name
                    )
                )
            )
        }
        
        // Warning if approaching limit
        if (deviceCount >= maxDevices * 0.9 && deviceCount < maxDevices) {
            violations.add(
                PolicyViolation(
                    code = "DEVICE_LIMIT_WARNING",
                    message = "Approaching device limit (${deviceCount}/${maxDevices})",
                    severity = PolicyViolation.Severity.WARNING,
                    metadata = mapOf(
                        "current" to deviceCount,
                        "maximum" to maxDevices,
                        "percentUsed" to (deviceCount.toDouble() / maxDevices * 100)
                    )
                )
            )
        }
        
        // Check serial number
        val existing = deviceRepository.findBySerialNumber(command.serialNumber)
        if (existing != null) {
            violations.add(
                PolicyViolation(
                    code = "DUPLICATE_SERIAL_NUMBER",
                    message = "Serial number already registered",
                    severity = PolicyViolation.Severity.ERROR,
                    field = "serialNumber",
                    metadata = mapOf(
                        "serialNumber" to command.serialNumber.value,
                        "existingDeviceId" to existing.deviceId.value
                    )
                )
            )
        }
        
        // Filter only errors for validation
        val errors = violations.filter { it.severity == PolicyViolation.Severity.ERROR }
        
        return if (errors.isEmpty()) {
            // Return context with warnings
            EnhancedPolicyResult.valid(
                RegisterDeviceContext(
                    command = command,
                    currentDeviceCount = deviceCount,
                    maxDevices = maxDevices,
                    subscription = subscription,
                    warnings = violations.filter { it.severity == PolicyViolation.Severity.WARNING }
                )
            )
        } else {
            EnhancedPolicyResult.invalid(violations)
        }
    }
}

// Context returned on success
data class RegisterDeviceContext(
    val command: RegisterDeviceCommand,
    val currentDeviceCount: Int,
    val maxDevices: Int,
    val subscription: Subscription,
    val warnings: List<PolicyViolation>
)

// Usage
val result = policy.evaluate(command)

when (result) {
    is EnhancedPolicyResult.Valid -> {
        // Log warnings
        result.value.warnings.forEach { warning ->
            logger.warn("${warning.code}: ${warning.message}", warning.metadata)
        }
        
        // Proceed with registration
        val device = Device.register(...)
        deviceRepository.save(device)
    }
    
    is EnhancedPolicyResult.Invalid -> {
        // Log errors with details
        result.violations.forEach { violation ->
            logger.error(
                "${violation.code}: ${violation.message}",
                violation.metadata
            )
        }
        
        throw PolicyViolationException(result.violations)
    }
}
```

---

## <a name="advanced-patterns"></a>7. Advanced Policy Patterns

### Pattern 1: Cached Policy

For expensive policy checks:

```kotlin
// CachedPolicy.kt
class CachedPolicy<T>(
    private val policy: Policy<T>,
    private val cacheDuration: Duration = Duration.ofMinutes(5)
) : Policy<T> {
    
    private val cache = ConcurrentHashMap<T, Pair<PolicyResult, Instant>>()
    
    override fun evaluate(target: T): PolicyResult {
        val cached = cache[target]
        
        // Return cached result if still valid
        if (cached != null) {
            val (result, timestamp) = cached
            if (Duration.between(timestamp, Instant.now()) < cacheDuration) {
                return result
            }
        }
        
        // Evaluate and cache
        val result = policy.evaluate(target)
        cache[target] = Pair(result, Instant.now())
        
        return result
    }
    
    fun clearCache() {
        cache.clear()
    }
}
```

### Pattern 2: Async Policy

For policies that can run in parallel:

```kotlin
// AsyncPolicyChain.kt
class AsyncPolicyChain<T>(
    private val policies: List<Policy<T>>,
    private val executor: ExecutorService = Executors.newFixedThreadPool(4)
) : Policy<T> {
    
    override fun evaluate(target: T): PolicyResult {
        // Execute all policies in parallel
        val futures = policies.map { policy ->
            CompletableFuture.supplyAsync({ policy.evaluate(target) }, executor)
        }
        
        // Wait for all to complete
        val results = futures.map { it.get() }
        
        // Combine results
        val allViolations = results
            .filter { !it.isValid }
            .flatMap { it.violations }
        
        return if (allViolations.isEmpty()) {
            PolicyResult.valid()
        } else {
            PolicyResult.invalid(allViolations)
        }
    }
}
```

### Pattern 3: Retry Policy

For policies with transient failures:

```kotlin
// RetryPolicy.kt
class RetryPolicy<T>(
    private val policy: Policy<T>,
    private val maxRetries: Int = 3,
    private val retryDelay: Duration = Duration.ofMillis(100)
) : Policy<T> {
    
    override fun evaluate(target: T): PolicyResult {
        var lastResult: PolicyResult? = null
        
        repeat(maxRetries) { attempt ->
            try {
                val result = policy.evaluate(target)
                
                // Return immediately if valid
                if (result.isValid) {
                    return result
                }
                
                lastResult = result
                
                // Wait before retry
                if (attempt < maxRetries - 1) {
                    Thread.sleep(retryDelay.toMillis())
                }
            } catch (e: Exception) {
                // Last attempt, throw
                if (attempt == maxRetries - 1) {
                    throw e
                }
                Thread.sleep(retryDelay.toMillis())
            }
        }
        
        return lastResult ?: PolicyResult.invalid("Policy evaluation failed")
    }
}
```

### Pattern 4: Logging Policy Decorator

For audit and debugging:

```kotlin
// LoggingPolicyDecorator.kt
class LoggingPolicyDecorator<T>(
    private val policy: Policy<T>,
    private val logger: Logger,
    private val policyName: String
) : Policy<T> {
    
    override fun evaluate(target: T): PolicyResult {
        logger.info("[$policyName] Evaluating policy for: $target")
        
        val startTime = System.currentTimeMillis()
        
        val result = try {
            policy.evaluate(target)
        } catch (e: Exception) {
            logger.error("[$policyName] Policy evaluation failed", e)
            throw e
        }
        
        val duration = System.currentTimeMillis() - startTime
        
        if (result.isValid) {
            logger.info("[$policyName] Policy passed in ${duration}ms")
        } else {
            logger.warn(
                "[$policyName] Policy failed in ${duration}ms. " +
                "Violations: ${result.violations}"
            )
        }
        
        return result
    }
}

// Usage
val loggingPolicy = LoggingPolicyDecorator(
    policy = DeviceRegistrationPolicy(...),
    logger = LoggerFactory.getLogger("PolicyEvaluator"),
    policyName = "DeviceRegistration"
)
```

---

## <a name="pitfalls"></a>8. Common Pitfalls

### Pitfall 1: Policy with Side Effects

**Problem:**

```kotlin
// ❌ Policy modifies state - BAD!
class BadDeviceRegistrationPolicy : Policy<RegisterDeviceCommand> {
    override fun evaluate(command: RegisterDeviceCommand): PolicyResult {
        // Modifying state in policy! ❌
        deviceRepository.save(Device.register(...))
        
        // Sending emails in policy! ❌
        emailService.sendWelcomeEmail(command.userEmail)
        
        return PolicyResult.valid()
    }
}
```

**Solution:**

```kotlin
// ✅ Policy only validates - GOOD!
class GoodDeviceRegistrationPolicy : Policy<RegisterDeviceCommand> {
    override fun evaluate(command: RegisterDeviceCommand): PolicyResult {
        // Only reads and validates ✅
        val deviceCount = deviceRepository.countByPremisesId(command.premisesId)
        
        if (deviceCount >= MAX_DEVICES) {
            return PolicyResult.invalid("Device limit exceeded")
        }
        
        return PolicyResult.valid()
    }
}
```

### Pitfall 2: Policies Too Coupled

**Problem:**

```kotlin
// ❌ Policy depends on too many things
class OverlyComplexPolicy(
    private val deviceRepo: DeviceRepository,
    private val premisesRepo: PremisesRepository,
    private val userRepo: UserRepository,
    private val subscriptionService: SubscriptionService,
    private val billingService: BillingService,
    private val notificationService: NotificationService,
    private val analyticsService: AnalyticsService
    // ... 10 more dependencies
) : Policy<RegisterDeviceCommand>
```

**Solution:**

```kotlin
// ✅ Break into smaller policies
class DeviceRegistrationPolicy(
    private val deviceRepository: DeviceRepository,
    private val subscriptionService: SubscriptionService
) : Policy<RegisterDeviceCommand>

class DeviceSecurityPolicy(
    private val deviceRepository: DeviceRepository
) : Policy<RegisterDeviceCommand>

class DeviceCompliancePolicy(
    private val complianceService: ComplianceService
) : Policy<RegisterDeviceCommand>

// Compose them
val chain = PolicyChain.of(
    deviceRegistrationPolicy,
    deviceSecurityPolicy,
    deviceCompliancePolicy
)
```

### Pitfall 3: Vague Violation Messages

**Problem:**

```kotlin
// ❌ Unhelpful messages
return PolicyResult.invalid("Invalid request")
return PolicyResult.invalid("Error")
return PolicyResult.invalid("Cannot register device")
```

**Solution:**

```kotlin
// ✅ Clear, actionable messages
return PolicyResult.invalid(
    "Device limit reached for FREE tier. " +
    "Current: 10 devices, Maximum: 10. " +
    "Please upgrade to BASIC tier (up to 50 devices) or higher."
)

return PolicyResult.invalid(
    "Serial number ABC123 is already registered to device device-456 " +
    "in premises premises-789. Each serial number must be unique."
)
```

### Pitfall 4: Not Testing Policies

**Problem:**

```kotlin
// No tests for policy! ❌
// What if the logic is wrong?
// What if limits change?
```

**Solution:**

```kotlin
// ✅ Comprehensive tests
class DeviceRegistrationPolicyTest {
    @Test
    fun `should allow registration within limit`()
    
    @Test
    fun `should reject when limit exceeded`()
    
    @Test
    fun `should reject duplicate serial number`()
    
    @Test
    fun `should provide clear violation messages`()
}
```

---

## <a name="testing"></a>9. Testing Policies

### Unit Testing Policies

```kotlin
class DeviceRegistrationPolicyTest {
    
    private lateinit var deviceRepository: DeviceRepository
    private lateinit var subscriptionService: SubscriptionService
    private lateinit var policy: DeviceRegistrationPolicy
    
    @BeforeEach
    fun setup() {
        deviceRepository = mockk()
        subscriptionService = mockk()
        policy = DeviceRegistrationPolicy(deviceRepository, subscriptionService)
    }
    
    @Test
    fun `should allow registration within device limit`() {
        // Given
        val command = createRegisterDeviceCommand()
        every { deviceRepository.countByPremisesId(any()) } returns 50
        every { subscriptionService.getSubscription(any()) } returns 
            createSubscription(SubscriptionTier.PRO, true)
        every { deviceRepository.findBySerialNumber(any()) } returns null
        
        // When
        val result = policy.evaluate(command)
        
        // Then
        assertTrue(result.isValid)
        assertTrue(result.violations.isEmpty())
    }
    
    @Test
    fun `should reject when device limit exceeded`() {
        // Given
        val command = createRegisterDeviceCommand()
        every { deviceRepository.countByPremisesId(any()) } returns 100
        every { subscriptionService.getSubscription(any()) } returns 
            createSubscription(SubscriptionTier.PRO, true)
        
        // When
        val result = policy.evaluate(command)
        
        // Then
        assertFalse(result.isValid)
        assertTrue(result.violations.any { 
            it.contains("Device limit reached") 
        })
    }
    
    @Test
    fun `should reject duplicate serial number`() {
        // Given
        val command = createRegisterDeviceCommand()
        every { deviceRepository.countByPremisesId(any()) } returns 50
        every { subscriptionService.getSubscription(any()) } returns 
            createSubscription(SubscriptionTier.PRO, true)
        every { deviceRepository.findBySerialNumber(any()) } returns 
            createExistingDevice()
        
        // When
        val result = policy.evaluate(command)
        
        // Then
        assertFalse(result.isValid)
        assertTrue(result.violations.any { 
            it.contains("Serial number") && it.contains("already registered") 
        })
    }
    
    @Test
    fun `should respect subscription tier limits`() {
        // Given - FREE tier
        val command = createRegisterDeviceCommand()
        every { deviceRepository.countByPremisesId(any()) } returns 10
        every { subscriptionService.getSubscription(any()) } returns 
            createSubscription(SubscriptionTier.FREE, true)
        
        // When
        val result = policy.evaluate(command)
        
        // Then - FREE tier limited to 10
        assertFalse(result.isValid)
        assertTrue(result.violations.any { it.contains("FREE tier") })
    }
    
    @Test
    fun `should reject when subscription is inactive`() {
        // Given
        val command = createRegisterDeviceCommand()
        every { deviceRepository.countByPremisesId(any()) } returns 5
        every { subscriptionService.getSubscription(any()) } returns 
            createSubscription(SubscriptionTier.PRO, isActive = false)
        
        // When
        val result = policy.evaluate(command)
        
        // Then
        assertFalse(result.isValid)
        assertTrue(result.violations.any { it.contains("Subscription") })
    }
    
    @Test
    fun `should provide multiple violations when multiple rules fail`() {
        // Given
        val command = createRegisterDeviceCommand()
        every { deviceRepository.countByPremisesId(any()) } returns 100
        every { subscriptionService.getSubscription(any()) } returns 
            createSubscription(SubscriptionTier.PRO, false)
        every { deviceRepository.findBySerialNumber(any()) } returns 
            createExistingDevice()
        
        // When
        val result = policy.evaluate(command)
        
        // Then - Multiple violations
        assertFalse(result.isValid)
        assertEquals(3, result.violations.size)
    }
}
```

### Testing Policy Chains

```kotlin
class PolicyChainTest {
    
    @Test
    fun `should pass when all policies pass`() {
        val policy1 = mockk<Policy<String>>()
        val policy2 = mockk<Policy<String>>()
        
        every { policy1.evaluate(any()) } returns PolicyResult.valid()
        every { policy2.evaluate(any()) } returns PolicyResult.valid()
        
        val chain = PolicyChain.of(policy1, policy2)
        val result = chain.evaluate("test")
        
        assertTrue(result.isValid)
    }
    
    @Test
    fun `should fail when any policy fails`() {
        val policy1 = mockk<Policy<String>>()
        val policy2 = mockk<Policy<String>>()
        
        every { policy1.evaluate(any()) } returns PolicyResult.valid()
        every { policy2.evaluate(any()) } returns 
            PolicyResult.invalid("Policy 2 failed")
        
        val chain = PolicyChain.of(policy1, policy2)
        val result = chain.evaluate("test")
        
        assertFalse(result.isValid)
        assertEquals(1, result.violations.size)
    }
    
    @Test
    fun `should collect all violations from all policies`() {
        val policy1 = mockk<Policy<String>>()
        val policy2 = mockk<Policy<String>>()
        val policy3 = mockk<Policy<String>>()
        
        every { policy1.evaluate(any()) } returns 
            PolicyResult.invalid("Violation 1")
        every { policy2.evaluate(any()) } returns 
            PolicyResult.invalid("Violation 2")
        every { policy3.evaluate(any()) } returns 
            PolicyResult.invalid("Violation 3")
        
        val chain = PolicyChain.of(policy1, policy2, policy3)
        val result = chain.evaluate("test")
        
        assertFalse(result.isValid)
        assertEquals(3, result.violations.size)
    }
}
```

### Integration Tests

```kotlin
@SpringBootTest
class DeviceRegistrationPolicyIntegrationTest {
    
    @Autowired
    lateinit var policy: DeviceRegistrationPolicy
    
    @Autowired
    lateinit var deviceRepository: DeviceRepository
    
    @Test
    fun `should enforce device limit in real repository`() {
        // Setup - register 100 devices
        val premisesId = PremisesId("test-premises")
        repeat(100) {
            deviceRepository.save(createDevice(premisesId))
        }
        
        // Try to register 101st device
        val command = createRegisterDeviceCommand(premisesId)
        val result = policy.evaluate(command)
        
        // Should fail
        assertFalse(result.isValid)
    }
}
```

---

## 💡 Key Takeaways

1. **Policies centralize business rules** - Single source of truth

2. **Policies return rich results** - Not just true/false, but why

3. **Different from specifications** - Policies validate, specs filter

4. **Composable with chains** - Combine multiple policies easily

5. **Testable in isolation** - Mock dependencies, test logic

6. **Clear violation messages** - Help users understand what's wrong

7. **No side effects** - Policies should only validate, not modify

8. **Keep policies focused** - One policy per concern

---

## 🎯 Practical Exercise

Centralize your business rules:

1. **Find scattered validations** in your codebase
2. **Extract into policies** - one policy per concern
3. **Create policy chains** for complex validations
4. **Add rich violation messages**
5. **Write tests** for each policy
6. **Measure improvement** - count validation locations before/after

---

## 📚 What We've Covered

In this chapter, you learned:

✅ The scattered rules problem  
✅ What the Policy pattern is  
✅ Policy vs Specification differences  
✅ Real policy implementations from SmartHome Hub  
✅ Policy chain pattern for composition  
✅ Enhanced results with violations  
✅ Advanced patterns (cache, async, retry)  
✅ Common pitfalls and solutions  
✅ Comprehensive testing strategies  

---

## 🚀 Next Chapter

Ready to implement repositories correctly?

👉 **[Chapter 7: Repository Pattern Done Right - Avoiding Common Pitfalls](./07-repository-pattern.md)**

**You'll learn:**
- Repository anti-patterns
- Generic vs specific repositories
- Query object pattern
- Unit of work
- Real implementations

**Reading Time:** 22 minutes  
**Difficulty:** Intermediate  

---

**Questions?** Open an issue in the repository!

**Found this helpful?** Star and share with your team!

---

*"Business rules should be explicit, centralized, and testable. The Policy pattern makes this possible."  
— Domain-Driven Design Community*

