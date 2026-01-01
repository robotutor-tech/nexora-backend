# Iteration Plan (PRD-aligned, business value)

This plan maps the PRD requirements into deliverable iterations. It prioritizes features that unlock core usage: identity → premises → membership → devices → control → automation.

## Iteration 0 — Walking Skeleton (platform usable by 1 tenant)
**Outcome:** A user can register/login and create a premises.
- E1-S1 Register (email/mobile + password)
- E1-S2 Login
- E2-S1 Create premises
- E2-S2 Assign default roles (owner/admin/guest) for the premises owner

## Iteration 1 — Team onboarding to a premises
**Outcome:** Owner/admin can invite members; members can accept/decline.
- E3-S1 Invite user to premises with role
- E3-S2 Accept invite
- E3-S3 Decline invite
- E3-S4 Remove user with “at least one owner remains” invariant

## Iteration 2 — Tenant navigation & basic inventory
**Outcome:** User can see premises assigned and add/register boards/devices.
- E2-S3 View assigned premises (pagination + search)
- E4-S1 Add predefined board/device set to premises
- E4-S2 View board health and metadata

## Iteration 3 — Structure the home (zones / widgets / feeds)
**Outcome:** Zones and widgets exist for UI; basic browsing works.
- E5-S1 Create zones
- E5-S2 View zones with infinite scrolling
- E5-S3 View widgets per zone
- E5-S4 Rename feeds/widgets
- E5-S5 Reassign widgets to zones

## Iteration 4 — Core control loop (actuators + feeds)
**Outcome:** User can control actuators and system records feed values/history.
- E6-S1 Operate actuator widgets (boolean/integer)
- E9-S2 Feed value updates + history maintenance
- E6-S2 Sensor feeds only updatable by board

## Iteration 5 — Rules & scheduling (automation foundation)
**Outcome:** Sensor updates can trigger rules; scheduled rules run.
- E7-S1 Trigger rules on sensor updates
- E7-S2 Scheduler triggers at scheduled times

## Iteration 6+ — Advanced + enterprise
**Outcome:** Licensing, custom roles, local server sync, notifications.
- E8 (licensing)
- E2/E3 (custom roles + permissions)
- E9 (local server sync, notifications)

## Parallelization guidance
- Iteration 0: E1 and E2 can be built in parallel.
- Iteration 1: invitation flows can proceed while premises listing/search is being built.
- Iterations 2–4: device inventory and zone/widget modelling can proceed in parallel, then converge for control screens.

