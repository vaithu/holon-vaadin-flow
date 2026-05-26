/**
 * Script to remove color-related CSS properties from all CSS source files.
 *
 * Color-related CSS properties targeted:
 *   - color, background-color, border-color, border-*-color
 *   - outline-color, text-decoration-color, caret-color, accent-color
 *   - fill, stroke
 *   - -webkit-tap-highlight-color
 *   - background (only when value is a color, not gradient/image)
 *   - Custom properties (--*) whose name contains 'color', '-fg', '-bg'
 *   - Custom properties with 'background' in name when value is a color
 *
 * Also removes entire rule blocks if they become empty after property removal.
 * Reports emptied CSS selectors for Java cleanup.
 */

const fs = require('fs');
const path = require('path');

// ─── Configuration ──────────────────────────────────────────────────────

const COLOR_PROPERTIES = new Set([
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
]);

function isColorValue(value) {
    const v = value.trim().replace(/;$/, '').trim();
    const patterns = [
        /#[0-9a-fA-F]{3,8}\b/,
        /\brgb\s*\(/i,
        /\brgba\s*\(/i,
        /\bhsl\s*\(/i,
        /\bhsla\s*\(/i,
        /\bvar\s*\(\s*--.*color/i,
        /\bvar\s*\(\s*--.*-fg\b/i,
        /\bvar\s*\(\s*--.*-bg\b/i,
        /\bvar\s*\(\s*--surface/i,
        /\bvar\s*\(\s*--text/i,
        /\bcurrentColor\b/i,
        /\btransparent\b/i,
    ];
    return patterns.some(p => p.test(v));
}

function isColorCustomProperty(propName) {
    const name = propName.trim().toLowerCase();
    return /--.*color/i.test(name) || /--.*-fg\b/i.test(name) || /--.*-bg\b/i.test(name);
}

function isColorPropertyLine(line) {
    const stripped = line.trim();

    // Skip comments, empty lines, selectors, braces, @-rules
    if (!stripped) return false;
    if (stripped.startsWith('/*') || stripped.startsWith('*') || stripped.startsWith('//')) return false;
    if (stripped.endsWith('{') || stripped === '}' || stripped === '};') return false;
    if (stripped.startsWith('@')) return false;
    // Skip lines that are just comments ending
    if (stripped === '*/') return false;

    // Match custom properties: --name: value
    let m = stripped.match(/^(--[a-zA-Z0-9\-]+)\s*:\s*(.+?)[\s;]*$/);
    if (m) {
        const propName = m[1].toLowerCase();
        const propValue = m[2];

        if (isColorCustomProperty(propName)) return true;
        if (/background/i.test(propName) && isColorValue(propValue)) return true;
        return false;
    }

    // Match standard properties: property: value
    m = stripped.match(/^([a-zA-Z\-]+)\s*:\s*(.+?)[\s;]*$/);
    if (!m) return false;

    const propName = m[1].toLowerCase().trim();
    const propValue = m[2];

    if (COLOR_PROPERTIES.has(propName)) return true;

    // 'background' shorthand — only if value is a color
    if (propName === 'background') {
        if (/\burl\s*\(/i.test(propValue)) return false;
        if (/\b(linear|radial|conic)-gradient\s*\(/i.test(propValue)) return false;
        if (isColorValue(propValue)) return true;
        return false;
    }

    return false;
}

function processCssFile(filepath) {
    const original = fs.readFileSync(filepath, 'utf8');
    const lines = original.split('\n');
    const newLines = [];
    const removedLines = [];
    let removedCount = 0;

    for (let i = 0; i < lines.length; i++) {
        if (isColorPropertyLine(lines[i])) {
            removedCount++;
            removedLines.push({ lineNum: i + 1, text: lines[i].trim() });
        } else {
            newLines.push(lines[i]);
        }
    }

    if (removedCount === 0) {
        return { content: original, removedCount: 0, removedLines: [], emptiedSelectors: [] };
    }

    let content = newLines.join('\n');

    // Remove empty rule blocks iteratively
    const emptiedSelectors = [];
    const emptyRulePattern = /([^\{\}]+?)\s*\{\s*\}/g;
    let prev = null;
    while (prev !== content) {
        prev = content;
        content = content.replace(emptyRulePattern, (match, selector) => {
            emptiedSelectors.push(selector.trim());
            return '';
        });
    }

    // Clean up excessive blank lines (3+ → 2)
    content = content.replace(/\n{3,}/g, '\n\n');

    // Ensure file ends with newline
    if (content && !content.endsWith('\n')) {
        content += '\n';
    }

    return { content, removedCount, removedLines, emptiedSelectors };
}

function main() {
    const workspace = path.resolve(__dirname, '..');
    const coreCssDir = path.join(workspace, 'core', 'src', 'main', 'resources', 'META-INF', 'resources');
    const demoCss = path.join(workspace, 'demo', 'src', 'main', 'resources', 'META-INF', 'resources', 'demo.css');

    // Collect all source CSS files
    const cssFiles = [];
    if (fs.existsSync(coreCssDir)) {
        const files = fs.readdirSync(coreCssDir).filter(f => f.endsWith('.css')).sort();
        files.forEach(f => cssFiles.push(path.join(coreCssDir, f)));
    }
    if (fs.existsSync(demoCss)) {
        cssFiles.push(demoCss);
    }

    let totalRemoved = 0;
    const allEmptiedSelectors = {};
    const allEmptiedClasses = new Set();

    console.log(`Processing ${cssFiles.length} CSS files...\n`);

    for (const cssFile of cssFiles) {
        const { content, removedCount, removedLines, emptiedSelectors } = processCssFile(cssFile);

        if (removedCount > 0) {
            fs.writeFileSync(cssFile, content, 'utf8');

            const relPath = path.relative(workspace, cssFile);
            console.log(`  ${relPath}: removed ${removedCount} color properties`);

            if (emptiedSelectors.length > 0) {
                allEmptiedSelectors[relPath] = emptiedSelectors;
                console.log(`    Emptied selectors:`);
                for (const sel of emptiedSelectors) {
                    console.log(`      ${sel}`);
                    // Extract class names from selectors
                    const classes = sel.match(/\.([\w\-]+)/g);
                    if (classes) {
                        classes.forEach(c => allEmptiedClasses.add(c.substring(1))); // remove leading dot
                    }
                }
            }

            totalRemoved += removedCount;
        }
    }

    console.log(`\n${'='.repeat(60)}`);
    console.log(`TOTAL: Removed ${totalRemoved} color-related CSS properties`);

    if (allEmptiedClasses.size > 0) {
        console.log(`\nEmptied CSS classes (need Java cleanup):`);
        const sortedClasses = [...allEmptiedClasses].sort();
        for (const cls of sortedClasses) {
            console.log(`  .${cls}`);
        }
        // Write to a file for reference
        const outputFile = path.join(workspace, 'tasks', 'emptied_css_classes.txt');
        fs.writeFileSync(outputFile, sortedClasses.join('\n') + '\n', 'utf8');
        console.log(`\nEmptied class list written to: tasks/emptied_css_classes.txt`);
    }
}

main();
