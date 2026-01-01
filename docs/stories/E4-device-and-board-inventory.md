# Epic E4 — Device & Board Inventory (FR-3.2, FR-4.5..FR-4.6, FR-4.11, FR-4.12)

This epic manages boards/devices installed in a premises.

> Status legend:
> - ✅ **DONE**: Implemented in code and wired for use.
> - 🟡 **PARTIAL**: Some pieces exist, but not fully wired / missing key behaviours.
> - ❌ **NOT DONE**: Not implemented or currently commented out.

## Current implementation status (repo check)
- Device endpoints exist: `context/device/interfaces/controller/DeviceController.kt`:
  - `POST /devices` register
  - `GET /devices` list
  - `POST /devices/commission`
  - `GET /devices/me`
- Board health/meta endpoints are partially present (device metadata/commissioning), but no dedicated “board health” read model endpoint was identified.

---

## E4-S1 — Add predefined devices/boards (FR-4.5)
**Status:** 🟡 PARTIAL

**As an** owner/admin  
**I want** to add predefined boards/devices to a premises  
**So that** feeds/widgets/permissions/rules are created automatically

### Acceptance Criteria
1. Owner/admin can add a predefined board/device set to a premises.
2. System auto-creates required feeds/widgets and default permissions.
3. The created inventory can be listed and retrieved.

**Evidence pointers:**
- Device registration/commissioning exists, but “auto-create feeds/widgets/permissions/rules” is not confirmed.

---

## E4-S2 — View board health + metadata (FR-3.2)
**Status:** 🟡 PARTIAL

**As a** user  
**I want** to view board health and metadata  
**So that** I can troubleshoot and confirm device status

### Acceptance Criteria
1. Board status includes: serial no, model no, OS version, health/online status.
2. Status is scoped to premises membership.

**Evidence pointers:**
- `POST /devices/commission` and device metadata request type exists.
- Dedicated board health/online ingestion endpoint appears commented out in `DeviceController`.

---

## E4-S3 — Add sensors to open slots (FR-4.6)
**Status:** ❌ NOT DONE

**As an** owner/admin  
**I want** to add sensors to open slots on a device  
**So that** I can expand monitoring capabilities

### Acceptance Criteria
1. System lists available slots.
2. Adding a sensor to a slot succeeds only if slot is free.

---

## E4-S4 — Deactivate predefined devices (FR-4.11)
**Status:** ❌ NOT DONE

**As an** owner/admin  
**I want** to deactivate a predefined device  
**So that** it stops participating without losing history

### Acceptance Criteria
1. Device is marked inactive (not deleted).
2. Inactive device cannot receive control actions.

---

## E4-S5 — Add custom boards with custom code (FR-4.12)
**Status:** ❌ NOT DONE

**As an** owner/admin  
**I want** to add custom boards via APIs  
**So that** custom hardware can be integrated

### Acceptance Criteria
1. API allows registering a custom board type.
2. Validation ensures compatibility with feed/widget model.
