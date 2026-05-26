import os

base = r'C:\Users\sxp267\IdeaProjects\holon-vaadin-flow\demo\src\main\java\com\holonplatform\vaadin\flow\demo\ui\views'
files = [
    'ListingBundleDemoView.java',
    'MenuBarDemoView.java',
    'AlertModalDemoView.java',
    'PageSizeSelectorDemoView.java',
    'PropertyListingDemoView.java',
    'TooltipDemoView.java',
    'LayoutDemoView.java',
]

for f in files:
    p = os.path.join(base, f)
    if not os.path.exists(p):
        print(f'NOT FOUND: {f}')
        continue
    data = open(p, 'rb').read()
    if data[:3] == b'\xef\xbb\xbf':
        open(p, 'wb').write(data[3:])
        print(f'BOM removed: {f}')
    else:
        print(f'No BOM:      {f}')

