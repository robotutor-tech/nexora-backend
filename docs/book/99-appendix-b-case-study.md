# Appendix B: SmartHome Hub Case Study

## Overview

Throughout this book, all examples are based on **SmartHome Hub**, a real enterprise IoT automation platform for smart homes. This appendix provides a complete overview of the system architecture, bounded contexts, and implementation details.

---

## Business Domain

### What is SmartHome Hub?

SmartHome Hub is an enterprise-grade IoT platform that enables users to:
- **Register and manage** IoT devices (sensors, actuators, smart appliances)
- **Create automation rules** based on device states and conditions
- **Monitor device health** and receive notifications
- **Control devices** remotely through mobile and web applications
- **Analyze usage patterns** and optimize energy consumption
- **Manage subscriptions** with different feature tiers

### Key Business Requirements

1. **Multi-Tenancy:** Support multiple premises (homes/buildings) per customer
2. **Real-Time:** Device state changes must be reflected immediately
3. **Reliability:** 99.9% uptime for critical automation rules
4. **Scalability:** Support 100,000+ devices across 10,000+ premises
5. **Security:** Device communication encrypted, role-based access control
6. **Compliance:** Audit trail for all device operations

---

## Technology Stack

### Backend
- **Language:** Kotlin
- **Framework:** Spring Boot 3.x
- **Database:** MongoDB (document-oriented for flexibility)
- **Messaging:** Kafka (event streaming)
- **Cache:** Redis (for read models and sessions)
- **API:** REST (WebFlux for async operations where needed)

### Frontend (Not Covered in This Book)
- **Web:** React + TypeScript
- **Mobile:** React Native
- **Real-Time:** WebSockets for live updates

### Infrastructure
- **Containerization:** Docker
- **Orchestration:** Kubernetes
- **CI/CD:** GitHub Actions
- **Monitoring:** Prometheus + Grafana
- **Logging:** ELK Stack

---

## Complete Architecture

### System Context Diagram

```
┌────────────────────────────────────────────────────────────┐
│                      SmartHome Hub                         │
│                                                            │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐ │
│  │   Device     │  │  Automation  │  │   Billing &     │ │
│  │  Management  │  │    Engine    │  │  Subscription   │ │
│  └──────────────┘  └──────────────┘  └─────────────────┘ │
│                                                            │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐ │
│  │     User     │  │   Analytics  │  │  Notification   │ │
│  │  Management  │  │   & Reports  │  │    Service      │ │
│  └──────────────┘  └──────────────┘  └─────────────────┘ │
│                                                            │
└────────────────────────────────────────────────────────────┘
         ▲                    ▲                    ▲
         │                    │                    │
    ┌────────┐          ┌─────────┐         ┌──────────┐
    │  Web   │          │ Mobile  │         │  IoT     │
    │  App   │          │   App   │         │ Devices  │
    └────────┘          └─────────┘         └──────────┘
```

---

## Bounded Contexts

SmartHome Hub is organized into six bounded contexts:

### 1. Device Management Context

**Responsibility:** Managing the lifecycle of IoT devices

**Core Aggregates:**
- Device (root)
- Feed (part of Device aggregate)
- Zone (separate aggregate)

**Key Use Cases:**
- Register device
- Commission device (configure feeds, assign zone)
- Activate/deactivate device
- Update device configuration
- Monitor device health
- Decommission device

**Domain Events:**
- DeviceRegistered
- DeviceCommissioned
- DeviceActivated
- DeviceDeactivated
- FeedValueChanged
- DeviceHealthChanged

**Example Code:**
```kotlin
class Device private constructor(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    private val serialNumber: SerialNumber,
    private var status: DeviceStatus,
    private val feeds: Map<FeedId, Feed>,
    private var zoneId: ZoneId?
) {
    companion object {
        fun register(
            deviceId: DeviceId,
            premisesId: PremisesId,
            serialNumber: SerialNumber
        ): Device {
            // Registration logic
        }
    }
    
    fun commission(feeds: List<Feed>, zoneId: ZoneId): Device {
        // Commissioning logic
    }
    
    fun activate(): Device {
        // Activation logic
    }
}
```

### 2. Automation Engine Context

**Responsibility:** Evaluating automation rules and triggering actions

**Core Aggregates:**
- Automation (root)
- Trigger (part of Automation)
- Condition (part of Automation)
- Action (part of Automation)

**Key Use Cases:**
- Create automation rule
- Enable/disable automation
- Evaluate triggers
- Execute actions
- Log automation execution

**Domain Events:**
- AutomationCreated
- AutomationEnabled
- AutomationDisabled
- AutomationTriggered
- AutomationExecuted
- ActionFailed

**Example Code:**
```kotlin
class Automation(
    val automationId: AutomationId,
    val premisesId: PremisesId,
    private val name: Name,
    private val triggers: List<Trigger>,
    private val conditions: List<Condition>,
    private val actions: List<Action>,
    private var isEnabled: Boolean
) {
    fun shouldExecute(event: TriggerEvent): Boolean {
        if (!isEnabled) return false
        
        val triggerMatches = triggers.any { it.matches(event) }
        if (!triggerMatches) return false
        
        return conditions.all { it.isMet() }
    }
    
    fun getActions(): List<Action> = actions.toList()
}
```

### 3. Billing & Subscription Context

**Responsibility:** Managing customer subscriptions and billing

**Core Aggregates:**
- Subscription (root)
- SubscriptionTier (value object)
- BillingPeriod (value object)

**Key Use Cases:**
- Create subscription
- Upgrade/downgrade tier
- Renew subscription
- Cancel subscription
- Process payment
- Apply limits based on tier

**Domain Events:**
- SubscriptionCreated
- SubscriptionUpgraded
- SubscriptionDowngraded
- SubscriptionRenewed
- SubscriptionCancelled
- SubscriptionExpired
- PaymentProcessed
- PaymentFailed

**Example Code:**
```kotlin
class Subscription(
    val subscriptionId: SubscriptionId,
    val customerId: CustomerId,
    private var tier: SubscriptionTier,
    private var status: SubscriptionStatus,
    private val startDate: LocalDate,
    private var endDate: LocalDate
) {
    fun upgrade(newTier: SubscriptionTier): Subscription {
        require(newTier.level > tier.level)
        require(status == SubscriptionStatus.ACTIVE)
        
        return copy(tier = newTier).also {
            it.addDomainEvent(SubscriptionUpgraded(...))
        }
    }
}

enum class SubscriptionTier(val level: Int, val maxDevices: Int) {
    FREE(1, 5),
    BASIC(2, 20),
    PRO(3, 50),
    ENTERPRISE(4, 200)
}
```

### 4. User Management Context

**Responsibility:** Managing users, authentication, and authorization

**Core Aggregates:**
- User (root)
- Actor (separate aggregate for device operations)
- PremisesOwner (separate aggregate)
- PremisesMember (separate aggregate)

**Key Use Cases:**
- Register user
- Authenticate user
- Authorize operations
- Manage premises membership
- Assign roles

**Domain Events:**
- UserRegistered
- UserActivated
- UserDeactivated
- PremisesMemberInvited
- PremisesMemberAccepted
- PremisesMemberRemoved

### 5. Analytics & Reports Context

**Responsibility:** Analyzing device data and generating insights

**Core Aggregates:**
- DeviceAnalytics (root)
- UsageReport (root)
- EnergyReport (root)

**Key Use Cases:**
- Collect device metrics
- Generate usage reports
- Analyze energy consumption
- Detect anomalies
- Provide recommendations

**Integration Events (Subscribed):**
- FeedValueChanged (from Device Management)
- AutomationExecuted (from Automation Engine)

### 6. Notification Service Context

**Responsibility:** Sending notifications to users

**Core Aggregates:**
- Notification (root)
- NotificationPreference (separate aggregate)

**Key Use Cases:**
- Send device alerts
- Send automation notifications
- Send system notifications
- Manage notification preferences

**Integration Events (Subscribed):**
- DeviceActivated
- DeviceDeactivated
- DeviceHealthChanged
- AutomationTriggered
- AutomationExecuted

---

## Context Mapping

### Relationships Between Contexts

```
Device Management (Upstream)
    │
    ├─→ Automation Engine (Downstream)
    │   └─ ACL: Translates device events to triggers
    │
    ├─→ Analytics (Downstream)
    │   └─ Conformist: Uses device domain model directly
    │
    └─→ Notification Service (Downstream)
        └─ Customer/Supplier: Provides device state

Billing & Subscription (Upstream)
    │
    └─→ Device Management (Downstream)
        └─ ACL: Enforces device limits based on tier

User Management (Upstream)
    │
    ├─→ Device Management (Downstream)
    │   └─ Partnership: Shared user/actor concepts
    │
    └─→ Billing & Subscription (Downstream)
        └─ Customer/Supplier: Provides user data
```

### Integration Patterns Used

**1. Domain Events (Within Context)**
- Used for loose coupling within same bounded context
- Example: DeviceActivated event handled by same context

**2. Integration Events (Between Contexts)**
- Used for communication between contexts
- Example: DeviceActivatedIntegrationEvent sent to Automation Engine

**3. Anti-Corruption Layer**
- Used when integrating with legacy systems
- Example: Legacy billing system adapter

**4. Shared Kernel**
- Minimal shared types (IDs, common value objects)
- Example: PremisesId, UserId used across contexts

---

## Event Flow Example

### Scenario: User Activates Device

```
1. User Management Context
   └─ User authenticated, authorized
   
2. Device Management Context
   ├─ RegisterDeviceCommand received
   ├─ Device.register() creates device
   ├─ DeviceRegistered event published
   ├─ Device saved to repository
   └─ DeviceRegisteredIntegrationEvent published to Kafka

3. Automation Engine Context
   ├─ Subscribes to DeviceRegisteredIntegrationEvent
   ├─ Creates default automations for device
   └─ AutomationCreated events published

4. Notification Service Context
   ├─ Subscribes to DeviceRegisteredIntegrationEvent
   ├─ Sends welcome notification to user
   └─ NotificationSent event published

5. Analytics Context
   ├─ Subscribes to DeviceRegisteredIntegrationEvent
   ├─ Initializes analytics for device
   └─ DeviceAnalyticsInitialized event published
```

---

## Database Schema Design

### Device Management Collections

**devices:**
```javascript
{
  "_id": "device-uuid",
  "premisesId": "premises-uuid",
  "serialNumber": "SN123456",
  "name": "Living Room Sensor",
  "type": "TEMPERATURE_SENSOR",
  "status": "ACTIVE",
  "zoneId": "zone-uuid",
  "feeds": [
    {
      "feedId": "feed-uuid",
      "name": "Temperature",
      "type": "TEMPERATURE",
      "value": 22.5,
      "unit": "CELSIUS",
      "minValue": -10,
      "maxValue": 50
    }
  ],
  "createdAt": "2025-11-11T10:00:00Z",
  "updatedAt": "2025-11-11T10:00:00Z"
}
```

**zones:**
```javascript
{
  "_id": "zone-uuid",
  "premisesId": "premises-uuid",
  "name": "Living Room",
  "type": "ROOM",
  "deviceIds": ["device-uuid-1", "device-uuid-2"]
}
```

### Automation Engine Collections

**automations:**
```javascript
{
  "_id": "automation-uuid",
  "premisesId": "premises-uuid",
  "name": "Turn on lights at sunset",
  "isEnabled": true,
  "triggers": [
    {
      "type": "TIME_OF_DAY",
      "time": "18:00"
    }
  ],
  "conditions": [
    {
      "type": "DEVICE_STATUS",
      "deviceId": "device-uuid",
      "status": "ACTIVE"
    }
  ],
  "actions": [
    {
      "type": "SET_DEVICE_VALUE",
      "deviceId": "device-uuid",
      "feedId": "feed-uuid",
      "value": 100
    }
  ]
}
```

### CQRS Read Models

**device_list_read_model:**
```javascript
{
  "_id": "device-uuid",
  "premisesId": "premises-uuid",
  "name": "Living Room Sensor",
  "status": "ACTIVE",
  "zoneName": "Living Room",  // Denormalized
  "feedCount": 2,
  "lastSeenAt": "2025-11-11T10:00:00Z"
}
```

---

## Deployment Architecture

### Microservices Deployment

```
┌─────────────────────────────────────────────────────┐
│                   API Gateway                       │
│            (Authentication, Rate Limiting)          │
└─────────────────────────────────────────────────────┘
                        │
        ┌───────────────┼───────────────┐
        │               │               │
┌───────▼──────┐ ┌─────▼──────┐ ┌──────▼─────┐
│   Device     │ │ Automation │ │  Billing   │
│  Management  │ │   Engine   │ │  Service   │
│   Service    │ │  Service   │ │            │
└──────────────┘ └────────────┘ └────────────��
        │               │               │
        └───────────────┼───────────────┘
                        │
              ┌─────────▼─────────┐
              │   Kafka Cluster   │
              │  (Event Streaming)│
              └───────────────────┘
```

### Scalability Considerations

**Device Management Service:**
- Stateless: Can scale horizontally
- Database: MongoDB with sharding by premisesId
- Cache: Redis for frequently accessed devices

**Automation Engine:**
- Stateful: Uses Kafka streams for processing
- Scales by partition (one partition per premises group)
- In-memory state for active automations

**Billing Service:**
- Stateless: Can scale horizontally
- Handles payment processing asynchronously
- Uses saga pattern for subscription changes

---

## Lessons Learned

### What Worked Well

1. **Bounded Contexts:** Clear separation made independent deployment possible
2. **Event-Driven:** Loose coupling enabled easy feature addition
3. **CQRS for Dashboard:** Solved performance issues immediately
4. **Value Objects:** Eliminated entire categories of bugs
5. **Test Data Builders:** Made testing significantly easier

### Challenges Faced

1. **Eventual Consistency:** Users confused when data not immediately visible
   - **Solution:** Added loading states, clear messaging

2. **Event Versioning:** Breaking changes in events caused issues
   - **Solution:** Implemented event upcasting, versioning strategy

3. **Aggregate Boundaries:** Initially made Device aggregate too large
   - **Solution:** Split into Device, Zone, and Premises aggregates

4. **Performance:** Event sourcing replay was slow
   - **Solution:** Added snapshots every 100 events

5. **Team Learning Curve:** DDD patterns took time to learn
   - **Solution:** Pair programming, code reviews, internal training

### Metrics After DDD Implementation

**Before DDD:**
- Average feature development: 2-3 weeks
- Bugs per release: 15-20
- Code coverage: 45%
- Onboarding time: 6 weeks

**After DDD:**
- Average feature development: 3-5 days
- Bugs per release: 2-5
- Code coverage: 85%
- Onboarding time: 2 weeks

---

## Code Organization

### Package Structure

```
com.smarthome.hub/
├── common/
│   ├── domain/
│   │   ├── AggregateRoot.kt
│   │   ├── DomainEvent.kt
│   │   ├── Entity.kt
│   │   └── ValueObject.kt
│   └── infrastructure/
│       ├── EventPublisher.kt
│       └── Repository.kt
│
├── device/
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Device.kt
│   │   │   ├── Feed.kt
│   │   │   ├── Zone.kt
│   │   │   └── valueobjects/
│   │   ├── event/
│   │   │   ├── DeviceRegistered.kt
│   │   │   └── DeviceActivated.kt
│   │   ├── repository/
│   │   │   └── DeviceRepository.kt
│   │   └── service/
│   │       └── DeviceDomainService.kt
│   ├── application/
│   │   ├── command/
│   │   │   ├── RegisterDeviceCommand.kt
│   │   │   └── ActivateDeviceCommand.kt
│   │   └── usecase/
│   │       ├── RegisterDeviceUseCase.kt
│   │       └── ActivateDeviceUseCase.kt
│   └── infrastructure/
│       ├── persistence/
│       │   ├── MongoDeviceRepository.kt
│       │   └── document/
│       └── messaging/
│           └── DeviceEventPublisher.kt
│
├── automation/
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
└── [other contexts...]
```

---

## Conclusion

SmartHome Hub demonstrates how Domain-Driven Design principles can be applied to build a real, scalable enterprise system. The bounded contexts, tactical patterns, and strategic design decisions discussed throughout this book are not theoretical—they're proven in production.

**Key Success Factors:**
- Clear bounded contexts from the start
- Strong collaboration with domain experts
- Iterative refinement of the domain model
- Comprehensive testing at all levels
- Team commitment to DDD principles

The architecture continues to evolve as the business grows, but the foundation established through DDD has made that evolution manageable and sustainable.

For complete code examples and more details, refer to the relevant chapters in the main book.

