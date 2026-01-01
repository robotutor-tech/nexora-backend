# Epic E6 — Control & Telemetry (FR-3.3..FR-3.4, FR-3.6, FR-5.4)

This epic covers controlling actuators and receiving sensor/button updates.

> Status legend:
> - ✅ **DONE**: Implemented in code and wired for use.
> - 🟡 **PARTIAL**: Some pieces exist, but not fully wired / missing key behaviours.
> - ❌ **NOT DONE**: Not implemented or currently commented out.

## Current implementation status (repo check)
- Device inventory endpoints exist (`/devices`) and feed inventory endpoints exist (`/feeds`).
- No explicit “actuator command” endpoint was identified in the current REST controllers.
- No explicit “board-only sensor feed update” endpoint was identified.

---

## E6-S1 — Control actuators via app (WiFi/Bluetooth) (FR-3.3)
**Status:** ❌ NOT DONE

**As a** user  
**I want** to control actuators via my mobile app  
**So that** I can operate devices remotely or locally

### Acceptance Criteria
1. API exists to request an actuator change.
2. Only users with appropriate premises permissions can control.
3. Command is routed to the board/bff (integration boundary documented).

---

## E6-S2 — Operate actuator widgets (boolean/integer) (FR-3.6)
**Status:** ❌ NOT DONE

**As a** user  
**I want** actuator widgets to support boolean and integer values  
**So that** I can control toggles and dimmers

### Acceptance Criteria
1. Widget carries type (boolean/integer) and allowed range.
2. API validates values against widget type/range.

---

## E6-S3 — Push switch control (FR-3.4)
**Status:** ❌ NOT DONE

**As a** user  
**I want** physical push switches to control actuators  
**So that** the system works without always using the app

### Acceptance Criteria
1. Switch → actuator mapping exists.
2. Board reports switch events; system updates state accordingly.

---

## E6-S4 — Sensor feeds only updatable by board (FR-5.4)
**Status:** ❌ NOT DONE

**As a** system  
**I want** sensor feeds to be updatable only by the board  
**So that** user clients cannot spoof sensor data

### Acceptance Criteria
1. Sensor feed update endpoint requires board authentication/identity.
2. User tokens cannot update sensor feeds.
