# Epic E1 — Identity & Access (FR-1.*, FR-2.1)

This epic delivers secure user identity: registration, verification, login, recovery, and optional OAuth.

> Status legend:
> - ✅ **DONE**: Implemented in code and wired for use.
> - 🟡 **PARTIAL**: Some pieces exist, but not fully wired / missing key behaviours.
> - ❌ **NOT DONE**: Not implemented or currently commented out.

## Current implementation status (repo check)
- User registration endpoint exists: `context/user/interfaces/controller/UserController.kt` (`POST /users/register`).
- IAM account registration/authentication endpoints exist: `context/iam/interfaces/controller/AccountController.kt` (`POST /iam/accounts/register`, `POST /iam/accounts/authenticate`).
- Session validation/refresh exists: `context/iam/interfaces/controller/SessionController.kt`.
- No email/mobile verification or OTP password reset endpoints were found.

---

## E1-S1 — Register with email/mobile + password (FR-1.1)
**Status:** 🟡 PARTIAL

**As a** new user  
**I want** to register an account  
**So that** I can access the system securely

### Acceptance Criteria
1. User can register with required fields (at minimum: email or mobile + password).
2. Duplicate email/mobile is rejected with a clear error.
3. Password policy is enforced (length and complexity documented).
4. Registration result returns a stable user identifier.

**Evidence pointers:**
- `POST /users/register` in `UserController`.
- `POST /iam/accounts/register` in `AccountController`.

---

## E1-S2 — Login with credentials (FR-1.1)
**Status:** 🟡 PARTIAL

**As a** user  
**I want** to log in with my credentials  
**So that** I can access protected features

### Acceptance Criteria
1. Valid credentials return an access token/session.
2. Invalid credentials return 401.
3. Rate limiting / lockout behavior is defined (even if MVP uses basic throttling).

**Evidence pointers:**
- `POST /iam/accounts/authenticate` (account authentication)
- `GET /iam/sessions/validate`, `GET /iam/sessions/refresh`

---

## E1-S3 — Email + mobile verification (FR-1.2)
**Status:** ❌ NOT DONE

**As a** user  
**I want** to verify my email and mobile  
**So that** my account is secure

### Acceptance Criteria
1. System can issue verification code/link for email and mobile.
2. User must verify before “full access” starts (define what is blocked pre-verification).
3. Expired/invalid verification attempts are rejected.

---

## E1-S4 — OTP password reset (FR-1.3)
**Status:** ❌ NOT DONE

**As a** user  
**I want** to reset my password using OTP  
**So that** I can recover my account safely

### Acceptance Criteria
1. User can request OTP for password reset.
2. OTP expires after configured TTL.
3. Password can be reset only with a valid OTP.

---

## E1-S5 — Profile view/update (FR-1.4)
**Status:** 🟡 PARTIAL

**As a** user  
**I want** to view and update my profile  
**So that** my info stays current

### Acceptance Criteria
1. User can fetch their profile.
2. User can update allowed fields.
3. Unauthorized modifications are rejected.

**Evidence pointers:**
- `GET /users/me` exists in `UserController` (view).
- Update endpoint not found.

---

## E1-S6 — Credential management (FR-1.5)
**Status:** 🟡 PARTIAL

**As a** user  
**I want** to update my credentials  
**So that** I can keep my account safe

### Acceptance Criteria
1. Password change requires current password (or equivalent secure mechanism).
2. Email/mobile change requires re-verification.

**Evidence pointers:**
- Credential rotation endpoint exists: `PATCH /iam/accounts/principal/{principalId}/credentials/rotate`.

---

## E1-S7 — OAuth signup/login (FR-2.1)
**Status:** ❌ NOT DONE

**As a** user  
**I want** to sign up/login using Google/Github/Facebook  
**So that** onboarding is faster

### Acceptance Criteria
1. At least one provider supported.
2. Accounts are linked or created safely.
