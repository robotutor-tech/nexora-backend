# Epic E7 — Rules & Scheduler (FR-5.3, FR-5.6, FR-7.*)

This epic enables automation in response to sensor updates, usage history, and schedules.

> Status legend:
> - ✅ **DONE**: Implemented in code and wired for use.
> - 🟡 **PARTIAL**: Some pieces exist, but not fully wired / missing key behaviours.
> - ❌ **NOT DONE**: Not implemented or currently commented out.

## Current implementation status (repo check)
- Rule endpoints exist: `modules/automation/interfaces/controller/RuleController.kt` (`POST /rules`, `GET /rules`, `GET /rules/{ruleId}`).
- Automation endpoints exist: `modules/automation/interfaces/controller/AutomationController.kt` (`POST /automations`, `GET /automations`).
- Rule listing currently uses an empty rule id list in controller; trigger evaluation/scheduler wiring to sensor updates is not evident from controllers.

---

## E7-S1 — Sensor updates trigger assigned rules (FR-5.3)
**Status:** 🟡 PARTIAL

**As a** system  
**I want** sensor updates to trigger assigned rules  
**So that** automations happen in real time

### Acceptance Criteria
1. System can associate rules with trigger sources (sensor/feed).
2. On sensor update, matching rules are evaluated and actions executed.
3. Failures are logged and do not block ingest path.

**Evidence pointers:**
- Rules and automations can be created via `/rules` and `/automations`.
- No evidence yet of sensor-update → rule-evaluation integration.

---

## E7-S2 — Scheduler triggers at scheduled times (FR-5.6)
**Status:** ❌ NOT DONE

**As a** system  
**I want** scheduled tasks to run at configured times  
**So that** time-based automations work

### Acceptance Criteria
1. A schedule can be configured for a rule.
2. Task executes at correct time in configured timezone strategy.
3. Missed executions on downtime are handled predictably (document approach).

---

## E7-S3 — Rules based on usage history (FR-7.1)
**Status:** ❌ NOT DONE

**As an** owner/admin  
**I want** to create rules based on usage history  
**So that** I can automate based on patterns

### Acceptance Criteria
1. Rule can reference a bounded time window of history.
2. History query is efficient and scoped.

---

## E7-S4 — Conditions based on trigger source + feed value (FR-7.2)
**Status:** 🟡 PARTIAL

**As an** owner/admin  
**I want** conditions based on trigger source and feed value  
**So that** rules are expressive

### Acceptance Criteria
1. Condition model supports comparisons (>, <, ==, ranges).
2. Condition references a known feed.

**Evidence pointers:**
- Rule creation endpoint exists; condition expressiveness not validated from this scan.
