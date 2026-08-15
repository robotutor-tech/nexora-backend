# Epic E3 — Invitations & Membership (FR-4.1..FR-4.3)

This epic enables multi-user premises membership management.

> Status legend:
> - ✅ **DONE**: Implemented in code and wired for use.
> - 🟡 **PARTIAL**: Some pieces exist, but not fully wired / missing key behaviours.
> - ❌ **NOT DONE**: Not implemented or currently commented out.

## Current implementation status (repo check)
- Invitation HTTP controller exists at `context/Identity/interfaces/controller/InvitationController.kt`, but its endpoints are **commented out**.
- No active `InvitationUseCase` implementation was found by code search.

---

## E3-S1 — Invite users to premises with roles (FR-4.1)
**Status:** ❌ NOT DONE

**As an** owner/admin  
**I want** to invite a user to my premises with a role  
**So that** I can onboard teammates and guests

### Acceptance Criteria
1. Owner/admin can create an invite for a premises with an assigned role.
2. Invites have an expiry.
3. Invites can be sent (email/SMS integration may be mocked; link generation must exist).

---

## E3-S2 — Accept invite (FR-4.2)
**Status:** ❌ NOT DONE

**As an** invited user  
**I want** to accept my invite via a link  
**So that** I gain access to the premises

### Acceptance Criteria
1. Accepting a valid, unexpired invite grants premises membership with the assigned role.
2. Accepting an expired/invalid invite fails with a clear error.
3. Accept is idempotent (second accept does not create duplicates).

---

## E3-S3 — Decline invite (FR-4.2)
**Status:** ❌ NOT DONE

**As an** invited user  
**I want** to decline an invite  
**So that** I do not get added to the premises

### Acceptance Criteria
1. Decline marks invite as declined (and cannot later be accepted unless re-invited).
2. Decline is idempotent.

---

## E3-S4 — Remove users with owner constraint (FR-4.3)
**Status:** ❌ NOT DONE

**As an** owner/admin  
**I want** to remove users from a premises  
**So that** access is controlled

### Acceptance Criteria
1. Owner/admin can remove a user.
2. System enforces: **at least one owner remains**.
3. Removed user loses access immediately.

---

## E3-S5 — Update permissions for feeds/roles (FR-4.7)
**Status:** ❌ NOT DONE

**As an** owner/admin  
**I want** to update feed permissions for roles  
**So that** access evolves safely

### Acceptance Criteria
1. Owner/admin can update permissions.
2. Permission changes take effect on subsequent actions.
