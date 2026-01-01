# Epic E8 — Licensing (FR-8.*)

This epic controls plan limits and billing-related constraints.

> Status legend:
> - ✅ **DONE**: Implemented in code and wired for use.
> - 🟡 **PARTIAL**: Some pieces exist, but not fully wired / missing key behaviours.
> - ❌ **NOT DONE**: Not implemented or currently commented out.

## Current implementation status (repo check)
- No licensing controllers/use cases were identified in the current code scan.

---

## E8-S1 — Purchase & manage licenses (FR-8.1)
**Status:** ❌ NOT DONE

**As a** user  
**I want** to purchase and manage licenses  
**So that** I can upgrade the platform capabilities

### Acceptance Criteria
1. Licenses support tiers: FREE/BASIC/PRO/ENTERPRISE.
2. License can be attached to a user/account.
3. License status is retrievable.

---

## E8-S2 — Display pricing based on premises/device count (FR-8.2)
**Status:** ❌ NOT DONE

**As a** user  
**I want** to see pricing based on my usage  
**So that** I understand the cost before upgrading

### Acceptance Criteria
1. API returns computed pricing for current counts.

---

## E8-S3 — Enforce license limits (FR-8.3)
**Status:** ❌ NOT DONE

**As a** system  
**I want** to enforce license limits  
**So that** plan constraints are respected

### Acceptance Criteria
1. System prevents exceeding limits (premises, devices, rules, credits).
2. Errors are user-friendly and actionable.

---

## E8-S4 — License expiration and validation (FR-8.4)
**Status:** ❌ NOT DONE

**As a** user  
**I want** to be notified of license expiration and validation  
**So that** I can renew and avoid disruption

### Acceptance Criteria
1. System validates license on relevant operations.
2. Expiring licenses generate notifications.

## Notes
- Billing integrations (payment provider) may be out of scope for early iterations.
