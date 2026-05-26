"""
Script to remove color-related CSS properties from all CSS source files.

Color-related CSS properties targeted:
  - color, background-color, border-color, border-*-color
  - outline-color, text-decoration-color, caret-color, accent-color
  - fill, stroke
  - -webkit-tap-highlight-color
  - background (only when value is a color, not gradient/image)
  - Custom properties (--*) whose name contains 'color' or 'background'
    when the value is a color reference

Also removes entire rule blocks if they become empty after property removal.
Reports emptied CSS selectors for Java cleanup.
"""

import re
import os
import sys
from pathlib import Path

# ─── Configuration ──────────────────────────────────────────────────────────

# Color-related CSS properties (exact match on property name)
COLOR_PROPERTIES = {
    'color',
    'background-color',
    'border-color',
    'border-top-color',
    'border-bottom-color',
    'border-left-color',
    'border-right-color',
    'border-inline-start-color',
    'border-inline-end-color',
    'border-block-start-color',
    'border-block-end-color',
    'outline-color',
    'text-decoration-color',
    'column-rule-color',
    'caret-color',
    'accent-color',
    'fill',
    'stroke',
    '-webkit-tap-highlight-color',
}

# Patterns that indicate a value IS a color
COLOR_VALUE_PATTERNS = [
    r'#[0-9a-fA-F]{3,8}\b',             # hex colors
    r'\brgb\s*\(',                        # rgb()
    r'\brgba\s*\(',                       # rgba()
    r'\bhsl\s*\(',                        # hsl()
    r'\bhsla\s*\(',                       # hsla()
    r'\bvar\s*\(\s*--.*color',            # var(--*color*)
    r'\bvar\s*\(\s*--.*\-fg\b',           # var(--*-fg)
    r'\bvar\s*\(\s*--.*\-bg\b',           # var(--*-bg)
    r'\bcurrentColor\b',                  # currentColor
    r'\btransparent\b',                   # transparent
    r'\binherit\b',                       # inherit (only when property is color-related)
]

# Custom property name patterns that are color-related
COLOR_CUSTOM_PROP_PATTERNS = [
    r'--.*color',
    r'--.*\-fg\b',
    r'--.*\-bg\b',
]

def is_color_value(value):
    """Check if a CSS value appears to be a color."""
    value = value.strip().rstrip(';').strip()
    for pattern in COLOR_VALUE_PATTERNS:
        if re.search(pattern, value, re.IGNORECASE):
            return True
    return False

def is_color_custom_property(prop_name):
    """Check if a custom property name is color-related."""
    prop_name = prop_name.strip().lower()
    for pattern in COLOR_CUSTOM_PROP_PATTERNS:
        if re.search(pattern, prop_name, re.IGNORECASE):
            return True
    return False

def is_color_property_line(line):
    """
    Determine if a CSS line is a color-related property declaration.
    Returns True if the line should be removed.
    """
    stripped = line.strip()

    # Skip comments, empty lines, selectors, braces
    if not stripped or stripped.startswith('/*') or stripped.startswith('*') or stripped.startswith('//'):
        return False
    if stripped.endswith('{') or stripped == '}' or stripped == '};':
        return False
    # Skip @-rules
    if stripped.startswith('@'):
        return False

    # Match "property: value" or "property: value;"
    m = re.match(r'^([a-zA-Z\-]+)\s*:\s*(.+?)[\s;]*$', stripped)
    if not m:
        # Also match custom properties: --name: value
        m = re.match(r'^(--[a-zA-Z0-9\-]+)\s*:\s*(.+?)[\s;]*$', stripped)
        if not m:
            return False
        prop_name = m.group(1).lower()
        prop_value = m.group(2)

        # Custom property: check if name is color-related
        if is_color_custom_property(prop_name):
            return True
        # Also check if name contains 'background' and value is a color
        if 'background' in prop_name and is_color_value(prop_value):
            return True
        return False

    prop_name = m.group(1).lower().strip()
    prop_value = m.group(2)

    # Direct color property match
    if prop_name in COLOR_PROPERTIES:
        return True

    # 'background' (shorthand) — only if value is a color, not url/gradient/etc
    if prop_name == 'background':
        # Skip if it looks like a gradient or image
        if re.search(r'\burl\s*\(', prop_value, re.IGNORECASE):
            return False
        if re.search(r'\b(linear|radial|conic)-gradient\s*\(', prop_value, re.IGNORECASE):
            return False
        if is_color_value(prop_value):
            return True
        return False

    return False


def process_css_file(filepath):
    """
    Process a single CSS file, removing color-related property lines.
    Returns (modified_content, removed_count, emptied_selectors).
    """
    with open(filepath, 'r', encoding='utf-8') as f:
        original = f.read()

    lines = original.split('\n')
    new_lines = []
    removed_count = 0

    # Track which lines were removed for reporting
    removed_lines = []

    for i, line in enumerate(lines):
        if is_color_property_line(line):
            removed_count += 1
            removed_lines.append((i + 1, line.strip()))
        else:
            new_lines.append(line)

    if removed_count == 0:
        return original, 0, [], []

    # Now clean up empty rule blocks
    content = '\n'.join(new_lines)

    # Remove rule blocks that are now empty: selector { (whitespace only) }
    emptied_selectors = []

    def track_empty_rule(match):
        selector = match.group(1).strip()
        emptied_selectors.append(selector)
        return ''

    # Pattern for empty rule blocks (allowing whitespace/newlines between braces)
    # Must handle multi-line selectors
    empty_rule_pattern = r'([^\{\}]+?)\s*\{\s*\}'
    prev_content = None
    while prev_content != content:
        prev_content = content
        content = re.sub(empty_rule_pattern, track_empty_rule, content)

    # Clean up excessive blank lines (3+ consecutive → 2)
    content = re.sub(r'\n{3,}', '\n\n', content)

    # Ensure file ends with a newline
    if content and not content.endswith('\n'):
        content += '\n'

    return content, removed_count, removed_lines, emptied_selectors


def main():
    workspace = Path(r'c:\Users\sxp267\IdeaProjects\holon-vaadin-flow')
    core_css_dir = workspace / 'core' / 'src' / 'main' / 'resources' / 'META-INF' / 'resources'
    demo_css = workspace / 'demo' / 'src' / 'main' / 'resources' / 'META-INF' / 'resources' / 'demo.css'

    # Collect all source CSS files
    css_files = []
    if core_css_dir.exists():
        css_files.extend(sorted(core_css_dir.glob('*.css')))
    if demo_css.exists():
        css_files.append(demo_css)

    total_removed = 0
    all_emptied_selectors = {}

    print(f"Processing {len(css_files)} CSS files...\n")

    for css_file in css_files:
        content, removed, removed_lines, emptied = process_css_file(str(css_file))

        if removed > 0:
            # Write modified content
            with open(css_file, 'w', encoding='utf-8') as f:
                f.write(content)

            rel_path = css_file.relative_to(workspace)
            print(f"  {rel_path}: removed {removed} color properties")
            for line_num, line_text in removed_lines:
                print(f"    L{line_num}: {line_text}")

            if emptied:
                all_emptied_selectors[str(rel_path)] = emptied
                print(f"    Emptied selectors: {emptied}")

            total_removed += removed

    print(f"\n{'='*60}")
    print(f"TOTAL: Removed {total_removed} color-related CSS properties from {len(css_files)} files")

    if all_emptied_selectors:
        print(f"\nEmptied CSS selectors (may need Java cleanup):")
        for file_path, selectors in all_emptied_selectors.items():
            for sel in selectors:
                # Extract class names from selectors
                classes = re.findall(r'\.([\w\-]+)', sel)
                if classes:
                    print(f"  {file_path}: {', '.join(classes)}")

    return total_removed


if __name__ == '__main__':
    removed = main()
    sys.exit(0)
