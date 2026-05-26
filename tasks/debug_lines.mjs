import { readFileSync } from 'fs';

const file = String.raw`C:\Users\sxp267\IdeaProjects\holon-vaadin-flow\demo\src\main\java\com\holonplatform\vaadin\flow\demo\ui\views\AccordionDemoView.java`;
const text = readFileSync(file, 'utf8');
const lines = text.split('\n');
for (let i = 0; i < lines.length && i < 80; i++) {
  if (lines[i].includes('addClassName')) {
    const l = lines[i];
    const codes = [...l].map(c => c.charCodeAt(0).toString(16)).join(' ');
    console.log(`LINE ${i}: ${JSON.stringify(l)}`);
    console.log(`  BYTES: ${codes}`);
    // Test the regex
    const re = /^[ \t]*(?:\w[\w.]*\.)?addClassName\("demo-[^"]*"\);[ \t]*$/;
    console.log(`  MATCH: ${re.test(l)}`);
  }
}

