/**
 * Browserless test for vaadin-calendar-element.js logic.
 *
 * Run with Node.js 18+:
 *   node calendar/src/test/js/vaadin-calendar-element.test.mjs
 *
 * Tests:
 *  1. CalendarEvent JSON serialisation (buildJson helper)
 *  2. JSON field extraction helpers (strField / boolField / intField)
 *  3. Full round-trip (buildJson → field extraction)
 *  4. parseJsonArray helper
 *  5. FullCalendar 6 _toFC / _fromFC conversion helpers
 */

import assert from 'node:assert/strict';

// ── _localISO (mirrors vaadin-calendar-element.js) ───────────────────────────
function localISO(d) {
  if (!d) return '';
  const p = n => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${p(d.getMonth()+1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`;
}

// ── FullCalendar 6 conversion helpers (mirrors vaadin-calendar-element.js) ───

/** Our CalendarEvent JSON → FullCalendar EventInput */
const toFC = ev => ({
  id: ev.id || `evt_${Date.now()}`,
  title: ev.title || '(no title)',
  start: ev.start,
  end: ev.end || ev.start,
  allDay: ev.allDay || false,
  backgroundColor: ev.color || '#1a73e8',
  borderColor: ev.color || '#1a73e8',
  textColor: ev.colorText || '#ffffff',
  extendedProps: {
    description: ev.description || '',
    location: ev.location || '',
    repeatEvery: ev.repeatEvery || 0,
    organizerName: ev.organizerName || '',
    organizerEmailAddress: ev.organizerEmailAddress || '',
    url: ev.url || '',
    _meta: ev._meta || {},
    _color: ev.color || '#1a73e8',
    _colorText: ev.colorText || '#ffffff',
  },
});

/** FullCalendar EventApi (mock) → our CalendarEvent JSON */
const fromFC = e => ({
  id: e.id,
  title: e.title,
  start: localISO(e.start),
  end: localISO(e.end || e.start),
  allDay: e.allDay || false,
  color: e.extendedProps?._color || e.backgroundColor || '#1a73e8',
  colorText: e.extendedProps?._colorText || e.textColor || '#ffffff',
  description: e.extendedProps?.description || '',
  location: e.extendedProps?.location || '',
  repeatEvery: e.extendedProps?.repeatEvery || 0,
  organizerName: e.extendedProps?.organizerName || '',
  organizerEmailAddress: e.extendedProps?.organizerEmailAddress || '',
  url: e.extendedProps?.url || '',
  _meta: e.extendedProps?._meta || {},
});

// ── JSON helpers (mirrors VaadinCalendar.java / vaadin-calendar-element.js) ──

function jsonEscape(s) {
  if (!s) return '';
  return s.replace(/\\/g,'\\\\').replace(/"/g,'\\"').replace(/\n/g,'\\n').replace(/\r/g,'\\r').replace(/\t/g,'\\t');
}

function buildJson(ev) {
  const str  = (k, v) => `"${jsonEscape(k)}":"${jsonEscape(v||'')}"`;
  const bool = (k, v) => `"${jsonEscape(k)}":${v}`;
  const num  = (k, v) => `"${jsonEscape(k)}":${v}`;
  let sb = '{';
  sb += str('id',          ev.id)                 + ',';
  sb += str('title',       ev.title)               + ',';
  sb += str('start',       ev.start)               + ',';
  sb += str('end',         ev.end)                 + ',';
  sb += str('description', ev.description || '')   + ',';
  sb += str('location',    ev.location    || '')   + ',';
  sb += str('color',       ev.color       || '#1a73e8') + ',';
  sb += str('colorText',   ev.colorText   || '#ffffff') + ',';
  sb += bool('allDay',     ev.allDay || false)      + ',';
  sb += num('repeatEvery', ev.repeatEvery || 0)     + ',';
  sb += str('url',         ev.url || '')            + ',';
  sb += '"_meta":{';
  const metaEntries = Object.entries(ev.meta || ev._meta || {});
  metaEntries.forEach(([k,v], i) => { if (i) sb += ','; sb += str(k, v); });
  sb += '}}';
  return sb;
}

function strField(json, key) {
  const re = new RegExp(`"${key}"\\s*:\\s*"((?:[^"\\\\]|\\\\.)*)"`);
  const m  = json.match(re);
  if (!m) return '';
  return m[1].replace(/\\"/g,'"').replace(/\\\\/g,'\\').replace(/\\n/g,'\n').replace(/\\r/g,'\r').replace(/\\t/g,'\t');
}
function boolField(json, key) {
  const m = json.match(new RegExp(`"${key}"\\s*:\\s*(true|false)`));
  return m ? m[1] === 'true' : false;
}
function intField(json, key) {
  const m = json.match(new RegExp(`"${key}"\\s*:\\s*(\\d+)`));
  return m ? parseInt(m[1]) : 0;
}
function parseJsonArray(json) {
  const result = [];
  if (!json || json.trim() === '[]') return result;
  let depth = 0, start = -1;
  for (let i = 0; i < json.length; i++) {
    const c = json[i];
    if (c === '{') { if (depth++ === 0) start = i; }
    else if (c === '}') { if (--depth === 0 && start >= 0) { result.push(json.slice(start, i+1)); start = -1; } }
  }
  return result;
}

// ── Test harness ──────────────────────────────────────────────────────────────
let passed = 0, failed = 0;
function test(name, fn) {
  try {
    fn();
    console.log(`  ✅ ${name}`);
    passed++;
  } catch (e) {
    console.error(`  ❌ ${name}`);
    console.error(`     ${e.message}`);
    failed++;
  }
}

console.log('\n📅 vaadin-calendar-element — FullCalendar 6 — browserless tests\n');

// ── 1. buildJson serialisation ────────────────────────────────────────────────
console.log('1. JSON serialisation (buildJson)');

test('id is included', () => {
  const json = buildJson({ id:'evt-1', title:'T', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00' });
  assert.ok(json.includes('"id":"evt-1"'), `Got: ${json}`);
});

test('title is included', () => {
  const json = buildJson({ id:'e', title:'Hello', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00' });
  assert.ok(json.includes('"title":"Hello"'));
});

test('allDay:true is serialised as boolean', () => {
  const json = buildJson({ id:'e', title:'T', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00', allDay:true });
  assert.ok(json.includes('"allDay":true'), `Got: ${json}`);
});

test('allDay:false is serialised as boolean', () => {
  const json = buildJson({ id:'e', title:'T', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00', allDay:false });
  assert.ok(json.includes('"allDay":false'));
});

test('repeatEvery is a number not a string', () => {
  const json = buildJson({ id:'e', title:'T', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00', repeatEvery:2 });
  assert.ok(json.includes('"repeatEvery":2'));
});

test('color defaults to #1a73e8 when absent', () => {
  const json = buildJson({ id:'e', title:'T', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00' });
  assert.ok(json.includes('"color":"#1a73e8"'));
});

test('special chars in title are escaped', () => {
  const json = buildJson({ id:'e', title:'Say "Hi"', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00' });
  assert.ok(json.includes('\\"Hi\\"'));
});

test('newline in description is escaped', () => {
  const json = buildJson({ id:'e', title:'T', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00', description:'a\nb' });
  assert.ok(json.includes('\\n'));
});

test('meta object is included', () => {
  const json = buildJson({ id:'e', title:'T', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00', meta:{key:'val'} });
  assert.ok(json.includes('"key":"val"'));
});

// ── 2. strField / boolField / intField ────────────────────────────────────────
console.log('\n2. JSON field extraction');

test('strField extracts simple string', () => {
  assert.equal(strField('{"id":"abc","title":"Hello"}', 'id'), 'abc');
});

test('strField returns empty string for absent field', () => {
  assert.equal(strField('{"title":"T"}', 'color'), '');
});

test('strField unescapes \\n', () => {
  assert.equal(strField('{"description":"line1\\nline2"}', 'description'), 'line1\nline2');
});

test('strField unescapes \\"', () => {
  assert.equal(strField('{"title":"Say \\"Hi\\""}', 'title'), 'Say "Hi"');
});

test('boolField reads true', () => assert.equal(boolField('{"allDay":true}',  'allDay'), true));
test('boolField reads false',() => assert.equal(boolField('{"allDay":false}', 'allDay'), false));

test('boolField returns false for absent field', () => {
  assert.equal(boolField('{"title":"T"}', 'allDay'), false);
});

test('intField reads value', () => assert.equal(intField('{"repeatEvery":3}', 'repeatEvery'), 3));
test('intField returns 0 for absent field', () => assert.equal(intField('{}', 'repeatEvery'), 0));

// ── 3. Round-trip ─────────────────────────────────────────────────────────────
console.log('\n3. JSON round-trip');

test('full round-trip preserves all scalar fields', () => {
  const orig = { id:'r1', title:'Round Trip', start:'2025-06-15T10:00:00', end:'2025-06-15T11:30:00',
    description:'desc', location:'room', color:'#ea4335', colorText:'#fff', allDay:false, repeatEvery:1, url:'https://x.com' };
  const json = buildJson(orig);
  assert.equal(strField(json, 'id'),          orig.id);
  assert.equal(strField(json, 'title'),       orig.title);
  assert.equal(strField(json, 'start'),       orig.start);
  assert.equal(strField(json, 'end'),         orig.end);
  assert.equal(strField(json, 'description'), orig.description);
  assert.equal(strField(json, 'color'),       orig.color);
  assert.equal(boolField(json, 'allDay'),     orig.allDay);
  assert.equal(intField(json, 'repeatEvery'), orig.repeatEvery);
});

test('missing color defaults to #1a73e8 after round-trip', () => {
  const json    = '{"id":"x","title":"T","start":"2025-06-15T10:00:00","end":"2025-06-15T11:00:00"}';
  const color   = strField(json, 'color');
  const effective = (color && color.length > 0) ? color : '#1a73e8';
  assert.equal(effective, '#1a73e8');
});

test('meta survives round-trip', () => {
  const json = buildJson({ id:'m1', title:'T', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00',
    meta:{ proj:'alpha', ticket:'JIRA-1' } });
  assert.ok(json.includes('"proj":"alpha"'));
  assert.ok(json.includes('"ticket":"JIRA-1"'));
});

// ── 4. parseJsonArray ─────────────────────────────────────────────────────────
console.log('\n4. parseJsonArray');

test('empty array returns []', () => assert.deepEqual(parseJsonArray('[]'), []));
test('null returns []',        () => assert.deepEqual(parseJsonArray(null), []));
test('blank returns []',       () => assert.deepEqual(parseJsonArray(''), []));

test('single object array works', () => {
  const arr = parseJsonArray('[{"id":"a","title":"T"}]');
  assert.equal(arr.length, 1);
  assert.ok(arr[0].includes('"id":"a"'));
});

test('multi-object array splits correctly', () => {
  const ev1 = buildJson({ id:'1', title:'A', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00' });
  const ev2 = buildJson({ id:'2', title:'B', start:'2025-06-16T10:00:00', end:'2025-06-16T11:00:00' });
  const arr = parseJsonArray(`[${ev1},${ev2}]`);
  assert.equal(arr.length, 2);
  assert.ok(arr[0].includes('"id":"1"'));
  assert.ok(arr[1].includes('"id":"2"'));
});

test('nested objects (meta) do not break array parsing', () => {
  const ev  = buildJson({ id:'n1', title:'T', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00', meta:{k:'v'} });
  const arr = parseJsonArray(`[${ev}]`);
  assert.equal(arr.length, 1);
});

// ── 5. FullCalendar 6 _toFC / _fromFC helpers ─────────────────────────────────
console.log('\n5. FullCalendar 6 _toFC / _fromFC');

test('_toFC maps color → backgroundColor + borderColor', () => {
  const fc = toFC({ id:'t1', title:'T', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00', color:'#ea4335' });
  assert.equal(fc.backgroundColor, '#ea4335');
  assert.equal(fc.borderColor,     '#ea4335');
});

test('_toFC maps colorText → textColor', () => {
  const fc = toFC({ id:'t2', title:'T', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00', colorText:'#000' });
  assert.equal(fc.textColor, '#000');
});

test('_toFC passes start/end as ISO strings (FC parses them)', () => {
  const fc = toFC({ id:'t3', title:'T', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00' });
  assert.equal(fc.start, '2025-06-15T10:00:00');
  assert.equal(fc.end,   '2025-06-15T11:00:00');
});

test('_toFC puts description/location into extendedProps', () => {
  const fc = toFC({ id:'t4', title:'T', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00',
    description:'My desc', location:'Room B' });
  assert.equal(fc.extendedProps.description, 'My desc');
  assert.equal(fc.extendedProps.location,    'Room B');
});

test('_toFC stashes _color/_colorText in extendedProps for round-trip', () => {
  const fc = toFC({ id:'t5', title:'T', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00',
    color:'#34a853', colorText:'#fff' });
  assert.equal(fc.extendedProps._color,     '#34a853');
  assert.equal(fc.extendedProps._colorText, '#fff');
});

test('_toFC defaults color to #1a73e8 when absent', () => {
  const fc = toFC({ id:'t6', title:'T', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00' });
  assert.equal(fc.backgroundColor, '#1a73e8');
});

test('_toFC uses allDay flag', () => {
  const fcTrue  = toFC({ id:'t7a', title:'T', start:'2025-06-15T00:00:00', end:'2025-06-15T23:59:59', allDay:true });
  const fcFalse = toFC({ id:'t7b', title:'T', start:'2025-06-15T10:00:00', end:'2025-06-15T11:00:00', allDay:false });
  assert.equal(fcTrue.allDay,  true);
  assert.equal(fcFalse.allDay, false);
});

test('_fromFC restores color from extendedProps._color', () => {
  const fcEv = {
    id:'f1', title:'T', allDay:false, backgroundColor:'#34a853', textColor:'#fff',
    start: new Date(2025, 5, 15, 10, 0, 0),
    end:   new Date(2025, 5, 15, 11, 0, 0),
    extendedProps: { _color:'#34a853', _colorText:'#fff', description:'', location:'', repeatEvery:0, organizerName:'', organizerEmailAddress:'', url:'', _meta:{} },
  };
  const ev = fromFC(fcEv);
  assert.equal(ev.color,     '#34a853');
  assert.equal(ev.colorText, '#fff');
});

test('_fromFC converts Date start/end to local ISO strings', () => {
  const start = new Date(2025, 5, 15, 10, 0, 0);
  const end   = new Date(2025, 5, 15, 11, 30, 0);
  const fcEv = {
    id:'f2', title:'T', allDay:false, backgroundColor:'#1a73e8', textColor:'#fff',
    start, end,
    extendedProps: { _color:'#1a73e8', _colorText:'#fff', description:'', location:'', repeatEvery:0, organizerName:'', organizerEmailAddress:'', url:'', _meta:{} },
  };
  const ev = fromFC(fcEv);
  assert.equal(ev.start, localISO(start));
  assert.equal(ev.end,   localISO(end));
});

test('_fromFC restores extendedProps fields', () => {
  const fcEv = {
    id:'f3', title:'Stand-up', allDay:false, backgroundColor:'#1a73e8', textColor:'#fff',
    start: new Date(2025, 5, 15, 9, 0, 0),
    end:   new Date(2025, 5, 15, 9, 15, 0),
    extendedProps: {
      _color:'#1a73e8', _colorText:'#fff',
      description:'Daily sync', location:'Zoom', repeatEvery:1,
      organizerName:'Bob', organizerEmailAddress:'bob@example.com',
      url:'https://zoom.us/j/123', _meta:{ ticket:'SCRUM-5' },
    },
  };
  const ev = fromFC(fcEv);
  assert.equal(ev.description,           'Daily sync');
  assert.equal(ev.location,              'Zoom');
  assert.equal(ev.repeatEvery,           1);
  assert.equal(ev.organizerName,         'Bob');
  assert.equal(ev.organizerEmailAddress, 'bob@example.com');
  assert.equal(ev.url,                   'https://zoom.us/j/123');
  assert.equal(ev._meta.ticket,          'SCRUM-5');
});

test('_toFC → _fromFC full round-trip preserves all fields', () => {
  const orig = {
    id:'rt1', title:'Full Round Trip',
    start:'2025-06-15T14:00:00', end:'2025-06-15T15:30:00',
    allDay:false, color:'#a142f4', colorText:'#ffffff',
    description:'A description', location:'Building C', repeatEvery:0,
    organizerName:'Carol', organizerEmailAddress:'carol@example.com',
    url:'https://example.com', _meta:{ ref:'REF-99' },
  };

  const fc   = toFC(orig);
  // Simulate what FullCalendar does: it keeps start/end as Dates
  const fcEv = { ...fc, start: new Date(orig.start), end: new Date(orig.end) };
  const back = fromFC(fcEv);

  assert.equal(back.id,                   orig.id);
  assert.equal(back.title,                orig.title);
  assert.equal(back.color,                orig.color);
  assert.equal(back.colorText,            orig.colorText);
  assert.equal(back.description,          orig.description);
  assert.equal(back.location,             orig.location);
  assert.equal(back.organizerName,        orig.organizerName);
  assert.equal(back.organizerEmailAddress,orig.organizerEmailAddress);
  assert.equal(back.url,                  orig.url);
  assert.equal(back._meta.ref,            'REF-99');
  assert.equal(back.allDay,               orig.allDay);
});

// ── Summary ───────────────────────────────────────────────────────────────────
console.log(`\n─────────────────────────────────────────────`);
console.log(`  ${passed} passed  |  ${failed} failed  |  ${passed+failed} total`);
console.log(`─────────────────────────────────────────────\n`);
if (failed > 0) process.exit(1);

