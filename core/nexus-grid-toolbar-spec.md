# Nexus — Grid Toolbar Component

Component spec for the action bar above a Vaadin Grid: search, filter, and bulk row actions. Distinct from the grid's column header row.

## States

The toolbar has exactly two states, occupying the same 52–56px bar. It crossfades between them (opacity + 4–6px translateY, ~150–200ms) rather than showing both at once.

### Default state (no rows selected)
- **Left:** search input, icon-prefixed, placeholder text names what's searched (e.g. "Search applicants…"), debounced filter-as-you-type
- **Right:** Filter button → opens a popover (not inline controls); primary action button (e.g. "Add applicant")
- **Filter badge:** small count badge on the Filter button, visible only when ≥1 filter is active

### Selection state (n rows selected)
- Background shifts to a tinted version of the module's accent color — this is the primary signal that mode changed, not just the text
- **Left:** count chip ("3") + "selected" label, and a "Clear" link to deselect all
- **Right:** bulk action buttons, safe actions (Export, Assign) grouped together, destructive actions (Delete) separated by a divider and visually distinct (outlined red, not filled)

## Filter popover

- Triggered from the Filter button, anchored bottom-right, closes on outside click
- Structured controls only (selects, date ranges, checkboxes) — never free text; free text belongs in the search field
- Footer row: "Reset" (text link, left) and "Apply" (primary button, right)
- Badge count on the trigger button updates only after Apply, not on every change inside the popover

## Selection semantics

- "Select all" in the header checkbox selects the current page only
- For paginated grids, after selecting a full page, show a secondary link — "Select all 240 records" — to extend selection beyond the page. Do not silently conflate "all on page" with "all matching filter"; this is a common source of bulk-action bugs
- Selected rows get a subtle background tint (accent-50) in the grid body so the connection between checkbox and highlighted row is visible without hunting

## Tokens used

| Role | Token | Notes |
|---|---|---|
| Toolbar bg (default) | `--slate-25` | |
| Toolbar bg (selected) | `--accent-50` | swap per module: Blue/CRM, Amber/Finance, Indigo/Journal, Purple/Contracts |
| Toolbar border (selected) | `--accent-100` | |
| Selection count text | `--accent-700` | |
| Bulk action (safe) | outline `--accent-100`, text `--accent-700`, hover fill `--accent-100` | |
| Bulk action (destructive) | outline `--slate-200`, text `--red-500`, hover bg `--red-50`, hover border `#f3c9c6` | |
| Primary button | `--violet-500` / hover `--violet-600` | brand color, not module accent — reserved for the primary create/add action |
| Search focus ring | `--violet-500` @ 15% alpha, 3px | |

Accent color is implemented as generic `--accent-*` custom properties (not hardcoded per-module names) so switching a grid's module context is a five-line token change, not a find-and-replace.

## Density

- Header row (column labels): 8px vertical padding, 12px label size
- Body rows: 9px vertical padding — denser than the original 12px pass, matches PBMS's information-dense grids (CRM, attendance, contracts)

## Responsive behavior

- **Breakpoint:** below 480px of toolbar width, individual bulk-action buttons (Export, Assign, the divider, Delete) collapse into a single overflow trigger
- **Overflow trigger:** 36×36px icon button (kebab / three-dot), same accent outline treatment as the other bulk buttons it replaces
- **Overflow menu:** anchored bottom-right, lists the same actions in the same order (Export, Assign, divider, Delete). Delete keeps its red text treatment inside the menu — the safe/destructive visual distinction is preserved, just relocated from a divider-in-a-row to a divider-in-a-menu
- The search field also drops its fixed 260px width and flexes to fill available space below the same breakpoint, so it doesn't force horizontal scroll on its own
- Measured by container width (`@container`), not viewport width — the toolbar collapses based on the space it actually has, which matters if it's ever embedded somewhere narrower than the full page (a side panel, a modal)

## Open questions / not yet decided

- Whether "Select all N matching records" needs its own confirmation step before triggering a destructive bulk action across an unbounded result set
