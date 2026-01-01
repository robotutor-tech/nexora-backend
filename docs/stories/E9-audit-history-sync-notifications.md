# Epic E9 — Audit, History, Sync, Notifications (FR-5.*, FR-5.5, FR-5.7, LS-*)

This epic contains platform-level system behaviors.

> Status legend:
> - ✅ **DONE**: Implemented in code and wired for use.
> - 🟡 **PARTIAL**: Some pieces exist, but not fully wired / missing key behaviours.
> - ❌ **NOT DONE**: Not implemented or currently commented out.

## Current implementation status (repo check)
- HTTP request logging with correlation id exists: `common/security/filter/LoggingFilter.kt`.
- No dedicated “audit event store” or “history query endpoints for charts” were identified in the current controller scan.

---

## E9-S1 — Audit all major events (FR-5.1)
**Status:** 🟡 PARTIAL

**As a** system  
**I want** to audit all major events  
**So that** security and monitoring requirements are met

### Acceptance Criteria
1. Key business events are recorded (membership changes, device changes, control actions).
2. Audit entries include actor, resource, timestamp, correlation id.

**Evidence pointers:**
- Correlation-aware HTTP logging exists, but this is not a full audit trail.

---

## E9-S2 — Feed value updates + history maintenance (FR-5.2)
**Status:** ❌ NOT DONE

**As a** system  
**I want** to update feed values and maintain history  
**So that** analytics and rules can rely on accurate data

### Acceptance Criteria
1. Feed values are stored as the latest value.
2. History is stored with timestamps and retention policy.
3. Query endpoints exist for history graphs (FR-3.7).

---

## E9-S3 — Graphical history & usage (FR-3.7)
**Status:** ❌ NOT DONE

**As a** user  
**I want** to view graphical history and usage  
**So that** I can understand consumption and patterns

### Acceptance Criteria
1. API provides data points for graphs with paging and time-range filters.

---

## E9-S4 — Local server sync with main server (FR-5.5, LS-*)
**Status:** ❌ NOT DONE

**As a** local server  
**I want** to sync records with the main server  
**So that** offline operation can reconcile when internet is restored

### Acceptance Criteria
1. Contract exists for initial sync and incremental sync.
2. Conflict resolution strategy is documented.
3. Sync includes at least: scheduler, rules, feeds, widgets, last 90 days of events.

---

## E9-S5 — System notifications (FR-5.7)
**Status:** ❌ NOT DONE

**As a** user  
**I want** to receive system notifications  
**So that** I am alerted about important events

### Acceptance Criteria
1. In-app notifications exist.
2. Critical notifications can be sent via message/call (later integration).
