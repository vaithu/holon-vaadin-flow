import os

f = r'C:\Users\sxp267\IdeaProjects\holon-vaadin-flow\demo\src\main\java\com\holonplatform\vaadin\flow\demo\ui\views\TabsDemoView.java'

with open(f, 'r', encoding='utf-8') as fp:
    content = fp.read()

print(f"File length: {len(content)}")

# The duplicate starts with a blank line then the old imports
markers = [
    '\n\nimport com.iyensoft.vaadin.flow.components.builders.TabsBuilder;',
    '\n\n\nimport com.iyensoft.vaadin.flow.components.builders.TabsBuilder;',
]

idx = -1
for m in markers:
    idx = content.find(m)
    if idx > 0:
        print(f"Found marker at {idx}")
        break

if idx > 0:
    clean = content[:idx] + '\n'
    with open(f, 'w', encoding='utf-8') as fp:
        fp.write(clean)
    print(f"Written {len(clean)} chars")
    # Verify
    with open(f, 'r', encoding='utf-8') as fp:
        lines = fp.readlines()
    print(f"Now has {len(lines)} lines")
    print("Last 3 lines:")
    for l in lines[-3:]:
        print(repr(l))
else:
    print("Marker not found - printing around line 374")
    lines = content.split('\n')
    for i in range(370, min(380, len(lines))):
        print(f"{i+1}: {lines[i]}")

