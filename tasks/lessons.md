# Agent Lessons

## Lesson 001 — 2026-04-15
**Context:** Updating AGENTS.md to reflect codebase changes.

**Mistake:** Skipped plan mode for a non-trivial multi-step task (codebase research → diff → edit → verify).

**Rule:** For ANY documentation update that requires codebase exploration + multiple edits, write a brief spec first:
1. List the files/sections to explore
2. State exactly what will be added/changed/removed
3. Get implicit confirmation via step-by-step output before applying

**Also missed:** Did not check `tasks/lessons.md` at session start (file did not exist yet — create it on first session).

