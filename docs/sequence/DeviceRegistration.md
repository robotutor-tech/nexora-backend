# Device Registration and Commissioning Flows

## Prerequisites
- **Premises BC**: Premises must exist and be in ACTIVE state
- **IAM BC**: User/Actor creating the device must have Account with ADMIN/OWNER role in the Premises
- **Device BC**: Zone must exist in the Premises

---

## Part 1: Device Registration Flow

**Endpoint**: `/orchestration/device/register`

**Request**:
```json
{
  "premisesId": "PREM-001",
  "zoneId": "ZONE-001",
  "deviceName": "Living Room Sensor"
}
```

### Step 1: Create Account (IAM BC)
**Actor**: Orchestration Service
- Request: `CreateAccount(type=MACHINE, credentials=[apiSecret])`
- IAM BC:
  - Validate credentials format (secret, kind, etc.)
  - Generate `AccountId`
  - Create Account with status=`REGISTERED`
  - Publish Event: `AccountCreatedEvent(accountId, type=MACHINE, createdAt)`
- Response: `AccountId`

### Step 2: Register Device (Device BC)
**Actor**: Orchestration Service
- Request: `RegisterDevice(premisesId, zoneId, name, registeredBy, accountId)`
- Device BC:
  - Validate `premisesId` exists (via ACL or async validation)
  - Validate `zoneId` exists in `premisesId`
  - Validate `registeredBy` has permissions in `premisesId` (via IAM)
  - Create Device aggregate with status=`REGISTERED`
  - Link `accountId` to Device
  - Publish Event: `DeviceRegisteredEvent(deviceId, name, premisesId, accountId)`
- Response: `DeviceId`

**Error Handling (Compensation)**:
- If Device registration fails:
  - Publish Event: `CompensateAccountRegistrationEvent(accountId)`
  - IAM BC listens and marks Account as `FAILED` or deletes it
  - Return error to orchestration

### Step 3: Activate Account (IAM BC)
**Actor**: IAM BC (Event-driven)
- Listen to: `DeviceRegisteredEvent(deviceId, accountId)`
- IAM BC:
  - Retrieve Account by `accountId`
  - Validate Account is in `REGISTERED` state
  - Update Account status to `ACTIVE`
  - Publish Event: `AccountActivatedEvent(accountId, activatedAt)`

---

## Part 2: Device Commissioning Flow

**Endpoint**: `/orchestration/device/commission`

**Request**:
```json
{
  "deviceId": "DEV-001",
  "premisesId": "PREM-001",
  "metadata": {
    "firmware": "v1.2.3",
    "capabilities": ["temperature", "humidity"]
  },
  "actorId": "ACTOR-001"
}
```

**Purpose**: 
- Create an Actor representation of the Device within the Premises
- Assign roles and permissions (feeds)
- Prepare device for activation

### Step 1: Create Actor for Device (IAM BC)
**Actor**: Orchestration Service
- Request: `CreateActor(accountId, premisesId, displayName=deviceName, type=DEVICE)`
- IAM BC:
  - Validate Account exists and is `ACTIVE`
  - Validate Premises exists and is `ACTIVE`
  - Generate `ActorId`
  - Create Actor aggregate with status=`REGISTERED`, scoped to `premisesId`
  - Publish Event: `ActorRegisteredEvent(actorId, accountId, premisesId, type=DEVICE)`
- Response: `ActorId`

**Key Point**: Actor is the *Premises-specific representation* of Account. One Account can have multiple Actors (one per Premises).

### Step 2: Create/Assign Role (IAM BC)
**Actor**: IAM BC (Event-driven or orchestration)
- Request: `AssignRoleToActor(actorId, premisesId, roleType=DEVICE_CONTROL)`
- IAM BC:
  - Get or Create Role: `DEVICE_CONTROL` (scoped to `premisesId`)
  - Create Role assignment linking `ActorId` → `RoleId`
  - Publish Event: `RoleAssignedToActorEvent(actorId, roleId, premisesId)`
- Response: `RoleId`

**Role Details**:
- Roles are defined per Premises (not global)
- Predefined roles: `DEVICE_CONTROL`, `DEVICE_MONITOR`, `ADMIN`, `OWNER`
- Each role has associated resource permissions

### Step 3: Update Device Status (Device BC)
**Actor**: Orchestration Service or Device BC (event-driven)
- Request: `CommissionDevice(deviceId, actorId)`
- Device BC:
  - Validate Device is in `REGISTERED` state
  - Validate `actorId` exists and is `REGISTERED`
  - Update Device status to `COMMISSIONED`
  - Link Device ↔ Actor (store `actorId` in Device)
  - Publish Event: `DeviceCommissionedEvent(deviceId, actorId, premisesId)`

### Step 4: Create Feeds (Device BC)
**Actor**: Device BC
- Device BC:
  - Validate metadata
  - Create Feed aggregates (one per capability)
  - Store FeedIds in persistence
  - Publish Event: `FeedsCreatedEvent(deviceId, feedIds, metadata)`

**Feed Details**:
- Feeds represent control/telemetry points on the device
- Example: `temperature-feed`, `humidity-feed`
- Each Feed has: `feedId`, `deviceId`, `name`, `type` (TELEMETRY/CONTROL), `unit`

### Step 5: Update Device with Feeds (Device BC)
**Actor**: Device BC
- Device BC:
  - Link `feedIds` to Device aggregate
  - Publish Event: `DeviceUpdatedWithFeedsEvent(deviceId, feedIds)`

### Step 6: Create Zone Widgets (Zone BC)
**Actor**: Zone BC (Event-driven)
- Listen to: `DeviceCommissionedEvent(deviceId, premisesId)`
- Zone BC:
  - Validate Zone exists
  - Create Widget aggregates for each Feed
  - Link Widgets to Zone
  - Publish Event: `WidgetsCreatedEvent(zoneId, widgetIds, deviceId)`

**Potential Issue**: Race condition if Zone processes event slowly.
**Mitigation**: 
- Use idempotency keys
- Widget creation should be idempotent
- Validate widgets on Device activation

### Step 7: Assign Feed Permissions (IAM BC)
**Actor**: IAM BC (Event-driven)
- Listen to: `DeviceCommissionedEvent(deviceId, actorId, premisesId)` OR `FeedsCreatedEvent`
- IAM BC:
  - Retrieve Role assigned to `actorId` in `premisesId` (e.g., `DEVICE_CONTROL`)
  - For each Feed in Device:
    - Create ResourcePermission: `(feedId, roleId, actions=[READ, WRITE])`
    - Store in `ResourceAuthorization` aggregate
  - Publish Event: `PermissionsAssignedEvent(actorId, feedIds, permissionLevel=READ_WRITE)`

**Authorization Model**:
```
User/Device (Account)
  └─ Actor (per-Premises)
      └─ Role (e.g., DEVICE_CONTROL)
          └─ ResourcePermissions (feeds, devices, zones)
              ├─ FeedId → [READ, WRITE]
              ├─ ZoneId → [READ]
              └─ DeviceId → [READ, WRITE]
```

### Step 8: Activate Actor (IAM BC)
**Actor**: IAM BC
- Request: Automatic (or triggered by) `PermissionsAssignedEvent`
- IAM BC:
  - Validate Actor is in `REGISTERED` state
  - Validate Role is assigned
  - Validate Permissions are configured
  - Update Actor status to `ACTIVE`
  - Publish Event: `ActorActivatedEvent(actorId, premisesId)`

### Step 9: Activate Device (Device BC)
**Actor**: Device BC (Event-driven)
- Listen to: `ActorActivatedEvent(actorId)` where actor.deviceId == this.deviceId
- Device BC:
  - Validate Device is in `COMMISSIONED` state
  - Validate all Feeds exist
  - Validate Widgets are created (from Zone BC)
  - Update Device status to `ACTIVE`
  - Update with metadata (firmware version, etc.)
  - Publish Event: `DeviceActivatedEvent(deviceId, actorId)`

---

## Error Handling and Compensation

### Scenario 1: Actor Creation Fails
```
Orchestration → IAM: CreateActor(...)
IAM: FAILED due to invalid permissionsId

Action:
  - No event published
  - Return error to Orchestration
  - Orchestration: Device remains in REGISTERED state
  - Device can retry commissioning later
```

### Scenario 2: Feed Creation Fails
```
Device BC → Device: CreateFeeds(...)
Device: FAILED (invalid metadata)

Action:
  - No FeedsCreatedEvent published
  - Publish Event: `CommissioningFailedEvent(deviceId, reason)`
  - Orchestration listens and retries or notifies admin
  - Device remains in COMMISSIONED state (not activated)
```

### Scenario 3: Permission Assignment Fails
```
IAM BC → IAM: AssignResourcePermissions(...)
IAM: FAILED (feeds not found)

Action:
  - Publish Event: `PermissionAssignmentFailedEvent(actorId, feedIds)`
  - Actor remains in REGISTERED state (not activated)
  - Device remains in COMMISSIONED state (not activated)
  - Admin must investigate why Feeds are missing
```

### Compensation on Complete Failure
```
IF commissioning cannot complete (max retries exceeded):
  Orchestration → Publish: `CompensateDeviceCommissioningEvent(deviceId, actorId)`
  
  IAM BC:
    - Disable Actor (status = DISABLED)
    - Publish: `ActorDisabledEvent(actorId)`
  
  Device BC:
    - Reset Device to REGISTERED state
    - Clear actorId, feedIds
    - Publish: `DeviceResetEvent(deviceId)`
  
  Admin notification:
    - Send alert with deviceId, reason
    - Device can be manually investigated or re-commissioned
```

---

## State Transition Diagrams

### Account State Machine
```
REGISTERED --[DeviceRegistered]--> ACTIVE --[AccountDisabled]--> DISABLED
                                     ↑
                                     |
                          [Async activation]
```

### Device State Machine
```
REGISTERED --[RegisterDevice]--> COMMISSIONED --[ActorActivated]--> ACTIVE
    ↓
REGISTERED --[RegistrationFailed]--> FAILED
    
COMMISSIONED --[CommissioningFailed]--> REGISTERED
    
ACTIVE --[DeviceDisabled]--> DISABLED
REGISTERED --[Reset]--> REGISTERED (idempotent)
```

### Actor State Machine
```
REGISTERED --[RoleAssigned]--> REGISTERED --[PermissionsAssigned]--> ACTIVE
                                               ↓
                              [PermissionAssignmentFailed]
                                               ↓
                                          REGISTERED
ACTIVE --[ActorDisabled]--> DISABLED
```

---

## Key Design Decisions

### 1. Account vs Actor
- **Account**: Global identity (Device or User)
- **Actor**: Premises-scoped representation of Account
- **Why**: Allows single Account to have different roles/permissions in different Premises

### 2. Resource-Based Authorization
- Permissions are assigned to **Roles**, not Actors
- Actor inherits permissions from Role
- Resources (feeds, devices, zones) are the target of permissions
- **Why**: Scalable authorization model; roles can be reused

### 3. Event-Driven Activation
- Account activated by listening to DeviceRegisteredEvent
- Actor activated by listening to PermissionsAssignedEvent
- Device activated by listening to ActorActivatedEvent
- **Why**: Loose coupling; each BC is independent

### 4. Idempotency
- All operations should be idempotent
- Use idempotency keys for Device registration
- Actor creation is idempotent per (accountId, premisesId) pair
- **Why**: Resilient to retries and duplicate messages

### 5. Premises Scoping
- All actors, roles, and permissions are scoped to a Premises
- Device is bound to a single Premises
- User can have Actors in multiple Premises
- **Why**: Multi-tenancy; isolation between Premises

---

## Flow Summary

| Phase | BC | Action | Output |
|-------|----|----|--------|
| 1 | IAM | Create Account | AccountCreatedEvent |
| 2 | Device | Register Device | DeviceRegisteredEvent |
| 3 | IAM | Activate Account | AccountActivatedEvent |
| 4 | IAM | Create Actor | ActorRegisteredEvent |
| 5 | IAM | Assign Role | RoleAssignedEvent |
| 6 | Device | Commission Device | DeviceCommissionedEvent |
| 7 | Device | Create Feeds | FeedsCreatedEvent |
| 8 | Zone | Create Widgets | WidgetsCreatedEvent |
| 9 | IAM | Assign Permissions | PermissionsAssignedEvent |
| 10 | IAM | Activate Actor | ActorActivatedEvent |
| 11 | Device | Activate Device | DeviceActivatedEvent |

**Total Steps**: 11 (vs 6 in original)
**Key Additions**: 
- ✅ Explicit state transitions
- ✅ Error handling per step
- ✅ Authorization setup
- ✅ Compensation flows
- ✅ Idempotency considerations
