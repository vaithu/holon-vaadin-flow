import re, pathlib
p = pathlib.Path(r'C:\Users\sxp267\IdeaProjects\holon-vaadin-flow\demo\src\main\java\com\holonplatform\vaadin\flow\demo\ui\views\AccordionDemoView.java')
lines = p.read_text(encoding='utf-8').splitlines(keepends=True)
pat = re.compile(r'^\s*\S*\.?addClassName\s*\(\s*"demo-[^"]*"\s*\)\s*;?\s*$')
for i, l in enumerate(lines[:200]):
    if 'addClassName' in l:
        m = pat.match(l)
        print(i, repr(l[:80]), bool(m))

