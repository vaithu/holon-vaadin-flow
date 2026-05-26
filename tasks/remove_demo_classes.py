"""
Removes all lines that contain addClassName("demo-  from every Java file
under demo/src/main/java/.../views/.

Also removes addClassName calls that set a "demo-" class on 'this'
via the bare form: addClassName("demo-...").

A line is removed only if its sole purpose is the addClassName call
(i.e. the line, when stripped, matches the pattern).
Lines that combine addClassName with another expression on the same line
are left untouched (safety guard).
"""
import re
import pathlib

VIEWS_DIR = pathlib.Path(
    r"C:\Users\sxp267\IdeaProjects\holon-vaadin-flow\demo\src\main\java"
    r"\com\holonplatform\vaadin\flow\demo\ui\views"
)

# Pattern: optional leading whitespace, optional receiver + dot, addClassName("demo-..."), optional semicolon, optional trailing whitespace
DEMO_CLASS_LINE = re.compile(r'^\s*\S*\.?addClassName\s*\(\s*"demo-[^"]*"\s*\)\s*;?\s*$')

removed_total = 0
files_changed = 0

for java_file in sorted(VIEWS_DIR.glob("*.java")):
    original = java_file.read_text(encoding="utf-8")
    lines = original.splitlines(keepends=True)
    kept = []
    removed_here = 0
    for line in lines:
        if DEMO_CLASS_LINE.match(line):
            removed_here += 1
        else:
            kept.append(line)
    if removed_here:
        java_file.write_text("".join(kept), encoding="utf-8")
        print(f"  {java_file.name}: removed {removed_here} line(s)")
        removed_total += removed_here
        files_changed += 1

print(f"\nDone. {removed_total} line(s) removed across {files_changed} file(s).")

