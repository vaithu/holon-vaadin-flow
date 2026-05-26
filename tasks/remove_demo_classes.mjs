import { readdirSync, readFileSync, writeFileSync } from 'fs';
import { join } from 'path';

const VIEWS_DIR = String.raw`C:\Users\sxp267\IdeaProjects\holon-vaadin-flow\demo\src\main\java\com\holonplatform\vaadin\flow\demo\ui\views`;

// Matches a whole line whose only content is an addClassName("demo-...") call.
// Handles optional receiver (e.g. "title.", "this."), optional trailing CR (\r\n files).
const LINE_RE = /^[ \t]*(?:\w[\w.]*\.)?addClassName\("demo-[^"]*"\);[ \t]*\r?$/;

let totalRemoved = 0, filesChanged = 0;

for (const name of readdirSync(VIEWS_DIR).filter(f => f.endsWith('.java'))) {
  const file = join(VIEWS_DIR, name);
  const lines = readFileSync(file, 'utf8').split('\n');
  const kept = lines.filter(l => !LINE_RE.test(l));
  const removed = lines.length - kept.length;
  if (removed > 0) {
    writeFileSync(file, kept.join('\n'), 'utf8');
    console.log(`  ${name}: removed ${removed} line(s)`);
    totalRemoved += removed;
    filesChanged++;
  }
}

console.log(`\nDone. ${totalRemoved} line(s) removed across ${filesChanged} file(s).`);


