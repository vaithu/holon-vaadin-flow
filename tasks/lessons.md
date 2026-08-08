# Agent Lessons

## Lesson 001 — 2026-04-15
**Context:** Updating AGENTS.md to reflect codebase changes.

**Mistake:** Skipped plan mode for a non-trivial multi-step task (codebase research → diff → edit → verify).

**Rule:** For ANY documentation update that requires codebase exploration + multiple edits, write a brief spec first:
1. List the files/sections to explore
2. State exactly what will be added/changed/removed
3. Get implicit confirmation via step-by-step output before applying

**Also missed:** Did not check `tasks/lessons.md` at session start (file did not exist yet — create it on first session).

## Lesson 002 — 2026-06-01
**Context:** Introducing MasterDetailBuilderV2.

**Mistake:** Used `*Impl` suffixes for nested implementation classes in a codebase that standardizes on `Default*`.

**Rule:** Match the repository naming convention exactly. Prefer `DefaultXXX` for concrete builder/configurator implementations and avoid introducing `Impl` suffixes in new code.

## Lesson 003 — 2026-06-06
**Context:** Rewriting Java sources during a master-detail configurator refactor.

**Mistake:** Rewriting Java files via terminal/editor operations introduced a UTF-8 BOM, which javac treated as an illegal leading character and caused parse failures across the build.

**Rule:** After any file rewrite of Java sources, verify the first bytes are BOM-free UTF-8 before compiling. Prefer direct patch/edit tools that preserve encoding, and if using shell rewrites, always write with `UTF8Encoding(false)`.

## Lesson 004 — 2026-07-03
**Context:** Implementing a reusable wizard component from mockup screenshots.

**Mistake:** Built a domain-specific customer wizard in the feature module instead of a reusable component in the core library.

**Rule:** When the user asks for a reusable framework component, implement it in core as a generic primitive with injected content and callbacks; keep domain-specific composition only in demos or consuming apps.

## Lesson 005 — 2026-07-03
**Context:** Refining the wizard refactor after user feedback.

**Mistake:** Introduced a new wrapper shell around raw Vaadin components even though the codebase already provides reusable Holon components for the same layout and stepper concerns.

**Rule:** Prefer existing Holon components and builders first; only use raw Vaadin primitives when no equivalent exists, and do not add a new wrapper component if the same UI can be composed from existing primitives.

## Lesson 006 — 2026-07-03
**Context:** Revamping the wizard demo into entity-backed forms.

**Mistake:** Manually composing form controls and buttons where `EntityFormPanel` and Holon button presets already express the workflow more cleanly.

**Rule:** For CRUD/wizard forms, start with `EntityFormPanel` plus `ButtonPreset`-driven buttons; only drop to manual component composition when a field or interaction cannot be modeled that way.

## Lesson 007 — 2026-07-03
**Context:** Finishing the mockup clone with Holon inputs.

**Mistake:** I nearly reached for raw Vaadin text/select controls too early.

**Rule:** Before adding a wrapper or raw field, verify the existing Holon input builders cover it; use raw Vaadin only for the one confirmed gap (the avatar upload here).

## Lesson 008 — 2026-07-03
**Context:** Refining the wizard shell after review.

**Mistake:** Introduced a custom `Mode` enum and raw `Div` shells even though `ViewMode` and `Panel` already exist.

**Rule:** Reuse the framework’s semantic viewport enums and container primitives first; do not create parallel mode enums or generic wrappers when the codebase already provides a matching abstraction.

## Lesson 009 — 2026-07-03
**Context:** Correcting the `WizardFrame` container interpretation.

**Mistake:** I extended `Panel` instead of only replacing the internal `Div` slots with `Panel`.

**Rule:** When the user asks to replace inner containers, keep the outer component contract unchanged unless they explicitly request a broader refactor.

## Lesson 010 — 2026-07-24
**Context:** Converting menu APIs to consumer-based indentation.

**Mistake:** I kept the old no-arg submenu entry point alongside the new consumer overload, which duplicated the pattern and left stale call sites behind.

**Rule:** When moving a builder API to `Consumer<T>` style, remove the old sibling entry point, update all usages in one pass, and verify there are no remaining no-arg calls before concluding.
