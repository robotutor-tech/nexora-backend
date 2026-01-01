# Nexora Backlog (PRD-aligned)

This backlog is derived from `.github/project-requirement-document.md` and rewritten into **business-value epics** suitable for iterative delivery.

## How to use
1. Start with the iteration sequencing: [`ITERATION-PLAN.md`](./ITERATION-PLAN.md)
2. Pick stories from epics; each story has a stable ID (e.g., `E3-S2`).
3. Use story **Status** markers to understand what’s already implemented.

## Status legend
- ✅ **DONE**: Implemented in code and wired for use.
- 🟡 **PARTIAL**: Some pieces exist, but not fully wired / missing key behaviours.
- ❌ **NOT DONE**: Not implemented or currently commented out.

## Epics
- [`E1-identity-and-access.md`](./E1-identity-and-access.md)
- [`E2-premises-and-roles.md`](./E2-premises-and-roles.md)
- [`E3-invitations-and-membership.md`](./E3-invitations-and-membership.md)
- [`E4-device-and-board-inventory.md`](./E4-device-and-board-inventory.md)
- [`E5-zones-widgets-feeds.md`](./E5-zones-widgets-feeds.md)
- [`E6-control-and-telemetry.md`](./E6-control-and-telemetry.md)
- [`E7-rules-and-scheduler.md`](./E7-rules-and-scheduler.md)
- [`E8-licensing.md`](./E8-licensing.md)
- [`E9-audit-history-sync-notifications.md`](./E9-audit-history-sync-notifications.md)

## Notes on “outdated” requirements
The PRD contains items like OAuth sign-in, voice assistant, and local-server sync. These may still be valid long-term, but the iteration plan treats them as **later phases** unless explicitly required now.
