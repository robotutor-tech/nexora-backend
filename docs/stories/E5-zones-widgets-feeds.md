# Epic E5 — Zones, Widgets, Feeds (FR-3.5, FR-4.8..FR-4.9)

This epic provides the structure used by the mobile app to display and control the home.

> Status legend:
> - ✅ **DONE**: Implemented in code and wired for use.
> - 🟡 **PARTIAL**: Some pieces exist, but not fully wired / missing key behaviours.
> - ❌ **NOT DONE**: Not implemented or currently commented out.

## Current implementation status (repo check)
- Zones endpoints exist: `context/zone/interfaces/controller/ZoneController.kt`:
  - `POST /zones` (create)
  - `GET /zones` (list by authorized resources)
  - `GET /zones/{zoneId}`
  - `POST /zones/widgets` (create widgets)
- Feeds endpoints exist: `context/feed/interfaces/controller/FeedController.kt`:
  - `GET /feeds` (list)
  - `POST /feeds` (register)

---

## E5-S1 — View zones and widgets (infinite scrolling) (FR-3.5)
**Status:** 🟡 PARTIAL

**As a** user  
**I want** to view all zones and widgets with infinite scrolling  
**So that** I can browse large homes smoothly

### Acceptance Criteria
1. API supports cursor-based pagination (preferred for infinite scrolling).
2. Zones and widgets are scoped to a premises.
3. Results are stable (no duplicates across pages).

**Evidence pointers:**
- `GET /zones` exists, but pagination/cursor semantics are not visible from controller signature.

---

## E5-S2 — Rename feeds and widgets (FR-4.8)
**Status:** ❌ NOT DONE

**As an** owner/admin  
**I want** to rename feeds and widgets  
**So that** the UI matches my naming conventions

### Acceptance Criteria
1. Only owner/admin can rename.
2. Names are validated (length, characters).

---

## E5-S3 — Reassign widgets to zones (FR-4.9)
**Status:** ❌ NOT DONE

**As an** owner/admin  
**I want** to move widgets between zones  
**So that** the UI reflects physical layout changes

### Acceptance Criteria
1. Widget can be moved to another zone in the same premises.
2. Permissions are preserved.

---

## (Supporting) Create zones and widgets
**Status:** ✅ DONE

- Create zones: `POST /zones`
- Create widgets: `POST /zones/widgets`
