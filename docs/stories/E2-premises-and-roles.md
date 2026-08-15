# Epic E2 — Premises & Roles (FR-1.6, FR-3.1)

This epic delivers multi-tenant premises creation and role assignment.

> Status legend:
> - ✅ **DONE**: Implemented in code and wired for use.
> - 🟡 **PARTIAL**: Some pieces exist, but not fully wired / missing key behaviours.
> - ❌ **NOT DONE**: Not implemented or currently commented out.

## Current implementation status (repo check)
- Premises endpoints exist: `context/premises/interfaces/controller/PremisesController.kt`.
- Identity role/resource authorization endpoints exist: `context/Identity/interfaces/controller/AuthorizationController.kt`.
- “View assigned premises with pagination + search” was not found as a dedicated API; existing `GET /premises` takes `premisesIds` as input.

---

## E2-S1 — Create premises (FR-1.6)
**Status:** ✅ DONE

**As a** user  
**I want** to create a premises  
**So that** I can manage automation for a physical location

### Acceptance Criteria
1. Creating a premises returns a unique premisesId.
2. Creator is automatically assigned **Owner** role.
3. A premises has a name and basic metadata.

**Evidence pointers:**
- `POST /premises` exists in `PremisesController`.

---

## E2-S2 — Default roles: owner/admin/guest (FR-1.6)
**Status:** 🟡 PARTIAL

**As a** system  
**I want** default roles to exist for each premises  
**So that** access can be managed consistently

### Acceptance Criteria
1. Owner/admin/guest roles exist for a premises.
2. Permissions baseline is defined at least for MVP endpoints.

**Evidence pointers:**
- Identity supports actors and resource authorization (`/Identity/actors`, `/Identity/resources`).
- Explicit default role seeding per premises not validated from code yet.

---

## E2-S3 — View assigned premises (pagination + search) (FR-3.1)
**Status:** ❌ NOT DONE

**As a** user  
**I want** to see premises assigned to me with pagination and search  
**So that** I can navigate quickly even with many premises

### Acceptance Criteria
1. List endpoint supports pagination.
2. Supports search by premises name.
3. Results are scoped to the logged-in user.

---

## E2-S4 — WiFi credentials with premises ID (FR-4.4)
**Status:** ❌ NOT DONE

**As an** owner/admin  
**I want** to set WiFi credentials for a premises  
**So that** boards can be commissioned securely

### Acceptance Criteria
1. WiFi credentials are stored encrypted/secured.
2. Provisioning uses a temporary token mechanism.
3. Only owner/admin can set it.

---

## E2-S5 — Custom roles + feed access (FR-4.10)
**Status:** ❌ NOT DONE

**As an** owner/admin  
**I want** to create custom roles and assign feed access  
**So that** permissions match real-world team structures

### Acceptance Criteria
1. Custom roles can be created per premises.
2. Feed permissions can be assigned to roles.


