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
