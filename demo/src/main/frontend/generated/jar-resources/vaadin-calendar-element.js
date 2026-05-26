/**
 * vaadin-calendar-element.js  – v2 (sidebar, groups, search, week-numbers, business-hours, timezone)
 *
 * Light-DOM Vaadin web component wrapping FullCalendar 6 (MIT).
 * Bundled by Vite via @NpmPackage annotations on VaadinCalendar.java — no CDN calls at runtime.
 */

import { Calendar }      from '@fullcalendar/core';
import dayGridPlugin     from '@fullcalendar/daygrid';
import timeGridPlugin    from '@fullcalendar/timegrid';
import listPlugin        from '@fullcalendar/list';
import interactionPlugin from '@fullcalendar/interaction';

// ── ID generator ──────────────────────────────────────────────────────────────
const _genId = () => `evt_${Date.now()}_${Math.random().toString(36).slice(2, 9)}`;

/** Format a JS Date as local-time ISO string — avoids UTC shift. */
const _localISO = d => {
  if (!d) return '';
  const p = n => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`;
};

/** Escape HTML attribute values used in dialog innerHTML. */
const _esc = s => (s || '').replace(/&/g, '&amp;').replace(/"/g, '&quot;').replace(/</g, '&lt;').replace(/>/g, '&gt;');

/** Our CalendarEvent JSON → FullCalendar EventInput */
const _toFC = ev => ({
  id: ev.id || _genId(),
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
    repeatUnit: ev.repeatUnit || 'NONE',
    organizerName: ev.organizerName || '',
    organizerEmailAddress: ev.organizerEmailAddress || '',
    url: ev.url || '',
    group: ev.group || '',
    _meta: ev._meta || {},
    _color: ev.color || '#1a73e8',
    _colorText: ev.colorText || '#ffffff',
  },
});

/** FullCalendar EventApi → our CalendarEvent JSON */
const _fromFC = e => ({
  id: e.id,
  title: e.title,
  start: _localISO(e.start),
  end: _localISO(e.end || e.start),
  allDay: e.allDay || false,
  color: e.extendedProps?._color || e.backgroundColor || '#1a73e8',
  colorText: e.extendedProps?._colorText || e.textColor || '#ffffff',
  description: e.extendedProps?.description || '',
  location: e.extendedProps?.location || '',
  repeatEvery: e.extendedProps?.repeatEvery || 0,
  repeatUnit: e.extendedProps?.repeatUnit || 'NONE',
  organizerName: e.extendedProps?.organizerName || '',
  organizerEmailAddress: e.extendedProps?.organizerEmailAddress || '',
  url: e.extendedProps?.url || '',
  group: e.extendedProps?.group || '',
  _meta: e.extendedProps?._meta || {},
});

/** Our view names → FullCalendar view names */
const FC_VIEW = { month: 'dayGridMonth', week: 'timeGridWeek', day: 'timeGridDay', agenda: 'listWeek' };

/** datetime-local input helpers */
const _toLocal   = iso => (iso || '').slice(0, 16);
const _fromLocal = val => val ? val + ':00' : '';

/** date-only input helpers (for all-day events) */
const _toLocalDate   = iso => (iso || '').slice(0, 10);
const _fromLocalDate = val => val ? val + 'T00:00:00' : '';

/** Google Calendar color swatches */
const EVENT_COLORS = [
  { hex: '#1a73e8', textHex: '#ffffff', label: 'Calendar Blue' },
  { hex: '#d50000', textHex: '#ffffff', label: 'Tomato' },
  { hex: '#e67c73', textHex: '#ffffff', label: 'Flamingo' },
  { hex: '#f4511e', textHex: '#ffffff', label: 'Tangerine' },
  { hex: '#f6bf26', textHex: '#202124', label: 'Banana' },
  { hex: '#33b679', textHex: '#ffffff', label: 'Sage' },
  { hex: '#0b8043', textHex: '#ffffff', label: 'Basil' },
  { hex: '#039be5', textHex: '#ffffff', label: 'Peacock' },
  { hex: '#3f51b5', textHex: '#ffffff', label: 'Blueberry' },
  { hex: '#7986cb', textHex: '#ffffff', label: 'Lavender' },
  { hex: '#8e24aa', textHex: '#ffffff', label: 'Grape' },
  { hex: '#616161', textHex: '#ffffff', label: 'Graphite' },
];

// ── Host + dialog CSS (injected once into document.head) ──────────────────────
let _cssInjected = false;
function _injectCss() {
  if (_cssInjected) return;
  _cssInjected = true;
  const s = document.createElement('style');
  s.textContent = `
/* ── Host ── */
vaadin-calendar { display: block; width: 100%; height: 100%; }
vaadin-calendar .vc-wrap {
  display: flex; flex-direction: row; height: 100%;
  background: var(--vaadin-calendar-surface, #fff);
  border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,.1);
  overflow: hidden; border: 1px solid var(--vaadin-calendar-border, #e0e0e0);
  font-family: 'DM Sans','Segoe UI',sans-serif;
}
vaadin-calendar[theme=dark] {
  --vaadin-calendar-primary: #8ab4f8;
  --vaadin-calendar-primary-light: #1e3a5f;
  --vaadin-calendar-surface: #1e1e2e;
  --vaadin-calendar-surface-raised: #2a2a3e;
  --vaadin-calendar-border: #3a3a52;
  --vaadin-calendar-text: #e8eaf6;
}
vaadin-calendar[theme=dark] .vc-wrap { background: #1e1e2e; border-color: #3a3a52; color: #e8eaf6; }
/* ── Sidebar ── */
vaadin-calendar .vc-sidebar {
  width: 200px; min-width: 200px; padding: 10px 12px;
  border-right: 1px solid var(--vaadin-calendar-border, #e0e0e0);
  display: flex; flex-direction: column; gap: 10px; overflow-y: auto;
  background: var(--vaadin-calendar-surface, #fff);
  transition: transform .25s ease;
}
vaadin-calendar[sidebar-hidden] .vc-sidebar { display: none; }
/* Sidebar backdrop (mobile overlay) */
vaadin-calendar .vc-sidebar-backdrop {
  display: none; position: absolute; inset: 0; z-index: 199;
  background: rgba(0,0,0,.35);
}
vaadin-calendar .vc-sidebar-backdrop.vc-sidebar-backdrop-open { display: block; }
/* Sidebar toggle button (hidden on desktop) */
vaadin-calendar .vc-sidebar-toggle {
  display: none; position: absolute; top: 10px; left: 10px; z-index: 10;
  background: transparent; border: none; cursor: pointer;
  font-size: 1.2rem; color: var(--vaadin-calendar-text, #5f6368);
  padding: 4px 8px; border-radius: 6px; line-height: 1;
}
vaadin-calendar .vc-sidebar-toggle:hover { background: var(--vaadin-calendar-surface-raised, #f1f3f4); }
/* ── Tablet (641–1024px): narrower sidebar ── */
@media (max-width: 1024px) {
  vaadin-calendar .vc-sidebar { width: 170px; min-width: 170px; padding: 8px 10px; }
}
/* ── Mobile (≤640px): sidebar becomes overlay ── */
@media (max-width: 640px) {
  vaadin-calendar .vc-wrap { position: relative; }
  vaadin-calendar .vc-sidebar {
    position: absolute; top: 0; left: 0; bottom: 0; z-index: 200;
    width: 240px !important; min-width: 240px !important;
    transform: translateX(-105%);
    box-shadow: 4px 0 24px rgba(0,0,0,.18);
    border-right: none;
  }
  vaadin-calendar .vc-sidebar.vc-sidebar-open { transform: translateX(0); }
  vaadin-calendar[sidebar-hidden] .vc-sidebar { display: flex; transform: translateX(-105%); }
  vaadin-calendar .vc-sidebar-toggle { display: flex; }
  vaadin-calendar .vc-main { padding-left: 0; }
  vaadin-calendar .vc-main .fc { padding: 36px 4px 4px !important; }
  vaadin-calendar .fc .fc-toolbar { gap: 4px; justify-content: space-between; }
  vaadin-calendar .fc .fc-toolbar-title { font-size: .85rem !important; }
  vaadin-calendar .fc .fc-button,
  vaadin-calendar .fc .fc-button-primary { padding: 5px 8px !important; font-size: .7rem !important; }
  vaadin-calendar .fc .fc-customAdd-button { display: none; }
}
/* Mini-month container */
vaadin-calendar .vc-mini-cal { width: 100%; }
vaadin-calendar .vc-mini-cal .fc { height: auto !important; padding: 0 !important; }
vaadin-calendar .vc-mini-cal .fc-view-harness { height: auto !important; }
vaadin-calendar .vc-mini-cal .fc-daygrid-body { width: 100% !important; }
vaadin-calendar .vc-mini-cal table { width: 100% !important; }
/* Mini-month overrides */
vaadin-calendar .vc-mini-cal .fc-toolbar { padding: 0 0 4px; gap: 0; flex-wrap: nowrap; }
vaadin-calendar .vc-mini-cal .fc-toolbar-title { font-size: .72rem !important; font-weight: 600 !important; color: var(--vaadin-calendar-text, #202124) !important; }
vaadin-calendar .vc-mini-cal .fc-button,
vaadin-calendar .vc-mini-cal .fc-button-primary {
  background: transparent !important; border: none !important;
  color: var(--vaadin-calendar-text, #5f6368) !important;
  padding: 2px 5px !important; font-size: .65rem !important; box-shadow: none !important; min-width: 0 !important;
}
vaadin-calendar .vc-mini-cal .fc-button:hover { background: var(--vaadin-calendar-surface-raised, #f1f3f4) !important; border-radius: 4px !important; }
vaadin-calendar .vc-mini-cal .fc-daygrid-day-number { font-size: .68rem !important; padding: 1px 3px !important; color: var(--vaadin-calendar-text, #3c4043) !important; }
vaadin-calendar .vc-mini-cal .fc-col-header-cell-cushion { font-size: .62rem !important; font-weight: 500 !important; color: #5f6368 !important; padding: 1px 0 !important; }
vaadin-calendar .vc-mini-cal .fc-daygrid-day.fc-day-today .fc-daygrid-day-number {
  background: var(--vaadin-calendar-primary, #1a73e8) !important; color: #fff !important;
  border-radius: 50%; width: 18px; height: 18px; display: flex; align-items: center; justify-content: center;
}
vaadin-calendar .vc-mini-cal .fc-daygrid-day:hover { background: var(--vaadin-calendar-surface-raised, #f1f3f4) !important; cursor: pointer; }
vaadin-calendar .vc-mini-cal .fc-event { display: none; }
vaadin-calendar .vc-mini-cal .fc-daygrid-more-link { display: none; }
/* Search */
vaadin-calendar .vc-search-wrap { position: relative; }
vaadin-calendar[search-hidden] .vc-search-wrap { display: none; }
vaadin-calendar .vc-search {
  width: 100%; box-sizing: border-box; padding: 6px 8px 6px 28px;
  border: 1px solid var(--vaadin-calendar-border, #e0e0e0); border-radius: 18px;
  font-size: .78rem; font-family: inherit; outline: none;
  background: var(--vaadin-calendar-surface-raised, #f1f3f4);
  color: var(--vaadin-calendar-text, #202124); transition: border-color .12s;
}
vaadin-calendar .vc-search:focus { border-color: var(--vaadin-calendar-primary, #1a73e8); background: #fff; }
vaadin-calendar .vc-search-icon { position: absolute; left: 8px; top: 50%; transform: translateY(-50%); font-size: .8rem; color: #5f6368; pointer-events: none; line-height: 1; }
/* Groups */
vaadin-calendar .vc-groups-section { display: flex; flex-direction: column; gap: 2px; flex: 1; min-height: 0; }
vaadin-calendar .vc-groups-title { font-size: .68rem; font-weight: 600; color: #5f6368; text-transform: uppercase; letter-spacing: .06em; padding: 6px 0 4px; border-top: 1px solid var(--vaadin-calendar-border, #e0e0e0); margin-top: 2px; }
vaadin-calendar .vc-group-item { display: flex; align-items: center; gap: 7px; padding: 4px 5px; border-radius: 6px; cursor: pointer; transition: background .1s; font-size: .78rem; color: var(--vaadin-calendar-text, #202124); }
vaadin-calendar .vc-group-item:hover { background: var(--vaadin-calendar-surface-raised, #f1f3f4); }
vaadin-calendar .vc-group-dot { width: 11px; height: 11px; border-radius: 3px; flex-shrink: 0; }
vaadin-calendar .vc-group-name { flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
vaadin-calendar .vc-group-check { width: 14px; height: 14px; border-radius: 3px; border: 2px solid; display: flex; align-items: center; justify-content: center; flex-shrink: 0; font-size: .55rem; color: #fff; box-sizing: border-box; transition: background .12s; }
/* Main area */
vaadin-calendar .vc-main { flex: 1; min-width: 0; display: flex; flex-direction: column; overflow: hidden; position: relative; }
vaadin-calendar .vc-main > div:not(.vc-sidebar-toggle) { flex: 1; min-height: 0; }
vaadin-calendar .vc-main .fc { height: 100% !important; padding: 8px; }
vaadin-calendar .fc .fc-toolbar { gap: 8px; flex-wrap: wrap; }
vaadin-calendar .fc .fc-toolbar-title { font-size: 1.05rem; font-weight: 600; color: var(--vaadin-calendar-text, #202124); }
vaadin-calendar .fc .fc-button,
vaadin-calendar .fc .fc-button-primary {
  background: var(--vaadin-calendar-surface, #fff) !important;
  color: var(--vaadin-calendar-text, #202124) !important;
  border: 1px solid var(--vaadin-calendar-border, #e0e0e0) !important;
  border-radius: 8px !important; font-family: inherit !important; font-size: .8rem !important;
  font-weight: 500 !important; padding: 6px 13px !important; box-shadow: none !important; transition: background .12s;
}
vaadin-calendar .fc .fc-button:hover,
vaadin-calendar .fc .fc-button-primary:not(:disabled):hover { background: var(--vaadin-calendar-surface-raised, #f8f9fa) !important; }
vaadin-calendar .fc .fc-button-primary:not(:disabled).fc-button-active,
vaadin-calendar .fc .fc-button-group > .fc-button.fc-button-active {
  background: var(--vaadin-calendar-primary-light, #e8f0fe) !important;
  color: var(--vaadin-calendar-primary, #1a73e8) !important;
  border-color: var(--vaadin-calendar-primary-light, #e8f0fe) !important;
}
vaadin-calendar .fc .fc-customAdd-button { background: var(--vaadin-calendar-primary, #1a73e8) !important; color: #fff !important; border-color: var(--vaadin-calendar-primary, #1a73e8) !important; }
vaadin-calendar .fc .fc-customAdd-button:hover { filter: brightness(1.08); }
/* Dark mode */
vaadin-calendar[theme=dark] .vc-sidebar { background: #1e1e2e; border-color: #3a3a52; }
vaadin-calendar[theme=dark] .vc-search { background: #2a2a3e; border-color: #3a3a52; color: #e8eaf6; }
vaadin-calendar[theme=dark] .vc-mini-cal .fc-daygrid-day { background: #1e1e2e !important; }vaadin-calendar[theme=dark] .vc-mini-cal .fc-daygrid-day-number { color: #9fa8da !important; }
vaadin-calendar[theme=dark] .vc-mini-cal .fc-col-header-cell-cushion { color: #7986cb !important; }
vaadin-calendar[theme=dark] .fc .fc-button,
vaadin-calendar[theme=dark] .fc .fc-button-primary { background: #2a2a3e !important; color: #e8eaf6 !important; border-color: #3a3a52 !important; }
vaadin-calendar[theme=dark] .fc .fc-button-primary:not(:disabled).fc-button-active,
vaadin-calendar[theme=dark] .fc .fc-button-group > .fc-button.fc-button-active { background: #1e3a5f !important; color: #8ab4f8 !important; border-color: #1e3a5f !important; }
vaadin-calendar[theme=dark] .fc .fc-toolbar-title { color: #e8eaf6 !important; }
vaadin-calendar[theme=dark] .fc-theme-standard td,
vaadin-calendar[theme=dark] .fc-theme-standard th { border-color: #3a3a52 !important; }
vaadin-calendar[theme=dark] .fc .fc-daygrid-day,
vaadin-calendar[theme=dark] .fc .fc-timegrid-slot,
vaadin-calendar[theme=dark] .fc .fc-list-table td,
vaadin-calendar[theme=dark] .fc .fc-list-table tr { background: #1e1e2e !important; color: #e8eaf6 !important; }
vaadin-calendar[theme=dark] .fc .fc-col-header-cell-cushion,
vaadin-calendar[theme=dark] .fc .fc-daygrid-day-number { color: #9fa8da !important; }
/* ── Loading spinner ── */
vaadin-calendar .vc-loading {
  position: absolute; inset: 0; z-index: 50;
  display: flex; align-items: center; justify-content: center;
  background: rgba(255,255,255,.55); border-radius: 12px;
  opacity: 0; pointer-events: none; transition: opacity .18s;
}
vaadin-calendar[theme=dark] .vc-loading { background: rgba(30,30,46,.6); }
vaadin-calendar .vc-loading.vc-loading-active { opacity: 1; pointer-events: all; }
vaadin-calendar .vc-spinner {
  width: 34px; height: 34px; border-radius: 50%;
  border: 3px solid var(--vaadin-calendar-border, #e0e0e0);
  border-top-color: var(--vaadin-calendar-primary, #1a73e8);
  animation: vc-spin .75s linear infinite;
}
@keyframes vc-spin { to { transform: rotate(360deg); } }
/* ── Event Popover ── */
.vc-popover {
  position: fixed; z-index: 99999;
  background: #fff; border-radius: 10px;
  box-shadow: 0 4px 28px rgba(0,0,0,.22), 0 1px 6px rgba(0,0,0,.1);
  width: 300px; max-width: 92vw;
  font-family: 'DM Sans','Segoe UI',sans-serif;
  overflow: hidden;
  animation: vc-pop-in .13s cubic-bezier(.2,.8,.4,1);
}
@keyframes vc-pop-in {
  from { opacity: 0; transform: scale(.93) translateY(-6px); }
  to   { opacity: 1; transform: scale(1)   translateY(0); }
}
.vc-popover-header {
  display: flex; align-items: flex-start; justify-content: space-between;
  padding: 12px 10px 8px 14px; background: #f8f9fa; gap: 6px;
}
.vc-popover-title {
  font-weight: 600; font-size: .9rem; color: #202124;
  flex: 1; line-height: 1.35; word-break: break-word;
}
.vc-popover-actions { display: flex; gap: 1px; flex-shrink: 0; align-items: center; }
.vc-popover-btn {
  background: transparent; border: none; cursor: pointer;
  padding: 5px 7px; border-radius: 5px; font-size: .9rem;
  color: #5f6368; line-height: 1; transition: background .1s;
}
.vc-popover-btn:hover { background: #e8eaed; }
.vc-popover-btn-del:hover { background: #fce8e6; color: #d93025; }
.vc-popover-body {
  padding: 10px 14px 14px; display: flex; flex-direction: column; gap: 6px;
}
.vc-popover-time {
  display: flex; align-items: center; gap: 6px;
  font-size: .8rem; color: #3c4043; font-weight: 500;
}
.vc-popover-icon { font-style: normal; flex-shrink: 0; }
.vc-popover-row {
  display: flex; align-items: center; gap: 6px;
  font-size: .79rem; color: #5f6368;
}
.vc-popover-row a { color: #1a73e8; text-decoration: none; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.vc-popover-row a:hover { text-decoration: underline; }
.vc-popover-desc {
  font-size: .79rem; color: #5f6368; line-height: 1.4;
  padding-top: 2px; border-top: 1px solid #f1f3f4; margin-top: 2px;
}
.vc-popover-dot { width: 10px; height: 10px; border-radius: 2px; flex-shrink: 0; }
/* ── Dialog ── */
.vc-dialog-bg { position: fixed; inset: 0; background: rgba(0,0,0,.45); z-index: 100000; display: flex; align-items: center; justify-content: center; }
.vc-dialog { background: #fff; border-radius: 12px; padding: 24px 28px; min-width: 340px; max-width: 480px; width: 92%; max-height: 90vh; overflow-y: auto; box-shadow: 0 8px 32px rgba(0,0,0,.18); font-family: 'DM Sans','Segoe UI',sans-serif; }
.vc-dialog h3 { margin: 0 0 16px; font-size: 1rem; font-weight: 600; color: #202124; }
.vc-dialog label { display: block; font-size: .8rem; color: #5f6368; margin-bottom: 4px; margin-top: 10px; }
.vc-dialog input[type=text], .vc-dialog input[type=url], .vc-dialog input[type=number],
.vc-dialog input[type=datetime-local], .vc-dialog input[type=date], .vc-dialog select {
  width: 100%; box-sizing: border-box; padding: 8px 10px; border: 1px solid #e0e0e0;
  border-radius: 8px; font-size: .875rem; font-family: inherit; outline: none; transition: border-color .12s;
  background: #fff; color: #202124; }
.vc-dialog input:focus, .vc-dialog select:focus { border-color: #1a73e8; }
.vc-dialog .vc-row { display: flex; gap: 8px; }
.vc-dialog .vc-row > div { flex: 1; min-width: 0; }
.vc-dialog-actions { display: flex; gap: 8px; margin-top: 20px; justify-content: flex-end; align-items: center; }
.vc-btn { padding: 8px 18px; border-radius: 8px; font-size: .85rem; font-weight: 500; cursor: pointer; border: none; font-family: inherit; }
.vc-btn-primary { background: #1a73e8; color: #fff; }
.vc-btn-primary:hover { filter: brightness(1.08); }
.vc-btn-ghost { background: transparent; color: #5f6368; border: 1px solid #e0e0e0; }
.vc-btn-ghost:hover { background: #f8f9fa; }
.vc-btn-danger { background: #ea4335; color: #fff; margin-right: auto; }
.vc-btn-danger:hover { filter: brightness(1.08); }
.vc-allday-row { display: flex; align-items: center; gap: 8px; margin-top: 10px; }
.vc-allday-row label { margin: 0; font-size: .85rem; color: #5f6368; cursor: pointer; }
.vc-toggle { width: 36px; height: 20px; -webkit-appearance: none; appearance: none; background: #dadce0; border-radius: 10px; cursor: pointer; position: relative; transition: background .15s; outline: none; border: none; }
.vc-toggle:checked { background: #1a73e8; }
.vc-toggle::after { content: ''; position: absolute; top: 2px; left: 2px; width: 16px; height: 16px; background: #fff; border-radius: 50%; transition: left .15s; }
.vc-toggle:checked::after { left: 18px; }
.vc-colors { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 6px; }
.vc-color-swatch { width: 22px; height: 22px; border-radius: 50%; cursor: pointer; border: 2px solid transparent; box-sizing: border-box; transition: transform .1s; }
.vc-color-swatch:hover { transform: scale(1.15); }
.vc-color-swatch.selected { border-color: #202124; box-shadow: 0 0 0 2px #fff inset; }
.vc-repeat-row { display: flex; gap: 8px; align-items: flex-end; }
.vc-repeat-row > div { flex: 1; min-width: 0; }
.vc-section-title { font-size: .75rem; font-weight: 600; color: #5f6368; text-transform: uppercase; letter-spacing: .06em; margin: 18px 0 4px; }
`;
  document.head.appendChild(s);
}

// ── Custom Element ─────────────────────────────────────────────────────────────
class VaadinCalendarElement extends HTMLElement {

  static get observedAttributes() {
    return [
      'view', 'locale', 'first-day', 'read-only', 'theme', 'date',
      'sidebar-hidden', 'search-hidden', 'week-numbers', 'business-hours', 'timezone',
      'lazy-load',
    ];
  }

  constructor() {
    super();
    this._cal      = null;
    this._miniCal  = null;
    this._events   = {};   // id → our CalendarEvent JSON
    this._groups   = {};   // id → { id, name, color, visible }
    this._search   = '';
    this._ready    = false;
    this._pending  = [];
    this._dialogBg = null;
    this._popoverEl = null;
    this._uid      = `vcal_${Math.random().toString(36).slice(2, 8)}`;
    this._uid2     = `vmin_${Math.random().toString(36).slice(2, 8)}`;
    // Default English i18n – fully overridable via setI18n(json) from Java
    this._i18n = {
      today: 'Today', month: 'Month', week: 'Week', day: 'Day', agenda: 'Agenda',
      addEvent: '+ Add Event',
      searchPlaceholder: 'Search events', myCalendars: 'My calendars',
      ariaToggleSidebar: 'Toggle sidebar',
      newEvent: 'New Event', editEvent: 'Edit Event',
      labelTitle: 'Title', placeholderTitle: 'Add title',
      allDay: 'All day', labelStart: 'Start', labelEnd: 'End',
      labelDescription: 'Description', placeholderDescription: 'Add description',
      labelLocation: 'Location', placeholderLocation: 'Add location',
      labelUrl: 'URL', labelCalendar: 'Calendar', noneCalendar: '— none —',
      labelColor: 'Color', labelRepeat: 'Repeat', labelEvery: 'Every', labelUnit: 'Unit',
      delete: 'Delete', cancel: 'Cancel', saveChanges: 'Save changes', addEventBtn: 'Add Event',
      tooltipEdit: 'Edit', tooltipDelete: 'Delete', tooltipClose: 'Close',
      repeatLabels: { NONE: 'None', DAILY: 'Daily', WEEKLY: 'Weekly', MONTHLY: 'Monthly', YEARLY: 'Yearly' },
    };
  }

  connectedCallback() {
    _injectCss();
    this._render();
    this._init();
  }

  disconnectedCallback() {
    this._closePopover();
    this._closeDialog();
    try { this._miniCal?.destroy(); } catch (_) {}
    try { this._cal?.destroy();     } catch (_) {}
    this._cal     = null;
    this._miniCal = null;
    this._ready   = false;
  }

  attributeChangedCallback(name, old, val) {
    if (old === val || !this._ready) return;
    if (name === 'view' && val)  this._cal.changeView(FC_VIEW[val] || 'dayGridMonth');
    if (name === 'date' && val)  { this._cal.gotoDate(new Date(val)); try { this._miniCal?.gotoDate(new Date(val)); } catch(_){} }
    if (name === 'sidebar-hidden' || name === 'search-hidden') { /* CSS attribute selector handles it */ }
    if (name === 'lazy-load') { /* dynamic toggle — reinit to wire/unwire the datesSet handler */ this._reinit(); }
    if (name === 'locale' || name === 'first-day' || name === 'read-only'
        || name === 'week-numbers' || name === 'business-hours' || name === 'timezone') {
      this._reinit();
    }
  }

  // ── Public API (invoked from VaadinCalendar.java via callJsFunction) ──────────

  addEvents(events) {
    const list = typeof events === 'string' ? JSON.parse(events) : (Array.isArray(events) ? events : [events]);
    list.forEach(ev => {
      if (!ev.id) ev.id = _genId();
      this._events[ev.id] = ev;
      if (this._ready && this._isVisible(ev)) this._cal.addEvent(_toFC(ev));
    });
    if (!this._ready) this._pending.push({ op: 'add', list });
  }

  setEvents(events) {
    const list = typeof events === 'string' ? JSON.parse(events) : (Array.isArray(events) ? events : []);
    this._events = {};
    list.forEach(ev => { if (!ev.id) ev.id = _genId(); this._events[ev.id] = ev; });
    if (this._ready) {
      this._cal.getEvents().forEach(e => e.remove());
      list.filter(ev => this._isVisible(ev)).forEach(ev => this._cal.addEvent(_toFC(ev)));
      this._showLoading(false);  // hide spinner whether lazy or not
    } else {
      this._pending.push({ op: 'setAll' });
    }
  }

  updateEvent(id, patch) {
    const p = typeof patch === 'string' ? JSON.parse(patch) : patch;
    if (!this._events[id]) return;
    Object.assign(this._events[id], p, { id });
    if (this._ready) {
      const ev   = this._events[id];
      const fcEv = this._cal.getEventById(id);
      if (this._isVisible(ev)) {
        if (fcEv) {
          fcEv.setProp('title', ev.title || '(no title)');
          fcEv.setDates(ev.start, ev.end, { allDay: ev.allDay || false });
          fcEv.setProp('backgroundColor', ev.color || '#1a73e8');
          fcEv.setProp('borderColor',     ev.color || '#1a73e8');
          fcEv.setProp('textColor',       ev.colorText || '#ffffff');
        } else {
          this._cal.addEvent(_toFC(ev));
        }
      } else {
        if (fcEv) fcEv.remove();
      }
    } else {
      this._pending.push({ op: 'update', id });
    }
  }

  deleteEvent(id) {
    delete this._events[id];
    if (this._ready) {
      const fcEv = this._cal.getEventById(id);
      if (fcEv) fcEv.remove();
    } else {
      this._pending.push({ op: 'delete', id });
    }
  }

  getAllEventsJson() { return JSON.stringify(Object.values(this._events)); }
  getEventJson(id)  { return this._events[id] ? JSON.stringify(this._events[id]) : 'null'; }

  today()         { if (this._ready) { this._cal.today(); try { this._miniCal?.today(); } catch(_){} } }
  next()          { if (this._ready) this._cal.next(); }
  previous()      { if (this._ready) this._cal.prev(); }
  navigateTo(iso) {
    const d = new Date(iso);
    if (this._ready) { this._cal.gotoDate(d); try { this._miniCal?.gotoDate(d); } catch(_){} }
    else this.setAttribute('date', iso);
  }
  setView(view)   { this.setAttribute('view', view); }

  openAddDialog(iso) {
    if (!this._ready) return;
    const start = iso ? new Date(iso) : (this._cal.getDate() || new Date());
    const end   = new Date(start.getTime() + 3_600_000);
    this._showDialog({ start: _localISO(start), end: _localISO(end), title: '' }, false, null);
  }

  // ── i18n API ─────────────────────────────────────────────────────────────────

  /**
   * Merge a new set of localized labels into _i18n and re-render if already live.
   * Called from VaadinCalendar.java via callJsFunction("setI18n", json).
   */
  setI18n(json) {
    const updates = typeof json === 'string' ? JSON.parse(json) : json;
    if (!updates) return;
    if (updates.repeatLabels) {
      Object.assign(this._i18n.repeatLabels, updates.repeatLabels);
      delete updates.repeatLabels;
    }
    Object.assign(this._i18n, updates);
    if (this._ready) this._reinit();
  }

  // ── Groups API ────────────────────────────────────────────────────────────────

  setGroups(json) {
    const list = typeof json === 'string' ? JSON.parse(json) : (Array.isArray(json) ? json : []);
    this._groups = {};
    list.forEach(g => { this._groups[g.id] = g; });
    this._renderGroups();
    this._applyFilters();
  }

  addGroup(json) {
    const g = typeof json === 'string' ? JSON.parse(json) : json;
    if (!g?.id) return;
    this._groups[g.id] = g;
    this._renderGroups();
  }

  removeGroup(id) {
    delete this._groups[id];
    this._renderGroups();
    this._applyFilters();
  }

  setGroupVisible(id, visible) {
    if (this._groups[id]) {
      this._groups[id] = { ...this._groups[id], visible: !!visible };
      this._renderGroups();
      this._applyFilters();
    }
  }

  // ── Render ────────────────────────────────────────────────────────────────────

  _render() {
    this.innerHTML = `
      <div class="vc-wrap">
        <div class="vc-sidebar-backdrop"></div>
        <div class="vc-sidebar">
          <div class="vc-mini-cal" id="${this._uid2}"></div>
          <div class="vc-search-wrap">
            <span class="vc-search-icon">&#128269;</span>
            <input type="search" class="vc-search" placeholder="${_esc(this._i18n.searchPlaceholder)}" autocomplete="off" />
          </div>
          <div class="vc-groups-section">
            <div class="vc-groups-title">${_esc(this._i18n.myCalendars)}</div>
            <div class="vc-groups-list"></div>
          </div>
        </div>
        <div class="vc-main">
          <button class="vc-sidebar-toggle" aria-label="${_esc(this._i18n.ariaToggleSidebar)}">&#9776;</button>
          <div class="vc-loading"><div class="vc-spinner"></div></div>
          <div id="${this._uid}"></div>
        </div>
      </div>`;
  }

  // ── Init ──────────────────────────────────────────────────────────────────────

  _init() {
    const container = this.querySelector(`#${this._uid}`);
    const miniCont  = this.querySelector(`#${this._uid2}`);
    const readOnly  = this.hasAttribute('read-only');
    const firstDay  = parseInt(this.getAttribute('first-day') || '1', 10);
    const locale    = this.getAttribute('locale') || 'en';
    const view      = FC_VIEW[this.getAttribute('view') || 'month'] || 'dayGridMonth';
    const weekNums  = this.hasAttribute('week-numbers');
    const timezone  = this.getAttribute('timezone') || 'local';
    const bizHours  = this._parseBusinessHours(this.getAttribute('business-hours'));

    // ── Main calendar ──────────────────────────────────────────────────────────
    try {
      this._cal = new Calendar(container, {
        plugins: [dayGridPlugin, timeGridPlugin, listPlugin, interactionPlugin],
        initialView: view,
        firstDay,
        locale,
        timeZone: timezone,
        height: '100%',
        editable:     !readOnly,
        selectable:   !readOnly,
        selectMirror: true,
        dayMaxEvents: true,
        nowIndicator: true,
        weekNumbers:  weekNums,
        businessHours: bizHours,
        headerToolbar: {
          left:   'prev,next today',
          center: 'title',
          right:  readOnly
            ? 'dayGridMonth,timeGridWeek,timeGridDay,listWeek'
            : 'customAdd dayGridMonth,timeGridWeek,timeGridDay,listWeek',
        },
        customButtons: readOnly ? {} : {
          customAdd: { text: this._i18n.addEvent, click: () => this.openAddDialog() },
        },
        buttonText: { today: this._i18n.today, month: this._i18n.month, week: this._i18n.week, day: this._i18n.day, list: this._i18n.agenda },

        select: info => {
          this._showDialog(
            { start: _localISO(info.start), end: _localISO(info.end), allDay: info.allDay, title: '' },
            false, null
          );
          this._cal.unselect();
        },

        eventClick: info => {
          const ev = _fromFC(info.event);
          this._fire('calendar-event-click', { event: ev });
          // Show popover preview; edit/delete actions are inside the popover
          this._showPopover(ev, info.event, info.jsEvent);
        },

        eventDrop: info => {
          const ev = _fromFC(info.event);
          this._events[ev.id] = ev;
          this._fire('calendar-event-updated', { event: ev });
        },

        eventResize: info => {
          const ev = _fromFC(info.event);
          this._events[ev.id] = ev;
          this._fire('calendar-event-updated', { event: ev });
        },

        datesSet: info => {
          this._fire('calendar-date-changed', { date: _localISO(info.view.currentStart).slice(0, 19) });
          try { this._miniCal?.gotoDate(info.view.currentStart); } catch (_) {}
          // Lazy-load: request events for the newly visible date range
          if (this.hasAttribute('lazy-load')) {
            this._showLoading(true);
            this._fire('calendar-fetch-events', {
              start: _localISO(info.view.activeStart).slice(0, 19),
              end:   _localISO(info.view.activeEnd).slice(0, 19),
            });
          }
        },
      });

      this._cal.render();
      this._ready = true;
      this._flush();
      this._applyFilters();
      this._fire('calendar-ready', {});
    } catch (err) {
      console.error('[vaadin-calendar] init-failed:', err);
      this._fire('calendar-error', { code: 'init-failed', message: err.message });
      return;
    }

    // ── Mini-month calendar ────────────────────────────────────────────────────
    try {
      this._miniCal = new Calendar(miniCont, {
        plugins: [dayGridPlugin, interactionPlugin],
        initialView: 'dayGridMonth',
        firstDay,
        locale,
        height: 'auto',
        contentHeight: 'auto',
        fixedWeekCount: false,
        showNonCurrentDates: false,
        headerToolbar: { left: 'prev', center: 'title', right: 'next' },
        dateClick: info => {
          this._cal.gotoDate(info.date);
          // Switch to day view on click for quick navigation
          const curView = this._cal.view.type;
          if (curView === 'dayGridMonth') this._cal.changeView('timeGridDay');
        },
      });
      this._miniCal.render();
      // Force a size recalc after render so FC measures the actual container width
      requestAnimationFrame(() => { try { this._miniCal?.updateSize(); } catch (_) {} });
    } catch (err) {
      console.warn('[vaadin-calendar] mini-cal init failed:', err.message);
    }

    // ── Sidebar toggle (mobile) ────────────────────────────────────────────────
    const toggleBtn = this.querySelector('.vc-sidebar-toggle');
    const sidebar   = this.querySelector('.vc-sidebar');
    const backdrop  = this.querySelector('.vc-sidebar-backdrop');
    if (toggleBtn && sidebar && backdrop) {
      toggleBtn.addEventListener('click', () => {
        const open = sidebar.classList.toggle('vc-sidebar-open');
        backdrop.classList.toggle('vc-sidebar-backdrop-open', open);
      });
      backdrop.addEventListener('click', () => {
        sidebar.classList.remove('vc-sidebar-open');
        backdrop.classList.remove('vc-sidebar-backdrop-open');
      });
    }

    // ── Search input ──────────────────────────────────────────────────────────
    const searchInput = this.querySelector('.vc-search');
    if (searchInput) {
      searchInput.addEventListener('input', e => {
        this._search = e.target.value.trim().toLowerCase();
        this._applyFilters();
      });
    }

    // ── Render groups already set before init ──────────────────────────────────
    this._renderGroups();
  }

  _parseBusinessHours(attr) {
    if (!attr) return false;
    const parts = attr.split(':');
    if (parts.length === 2) {
      const start = parseInt(parts[0], 10);
      const end   = parseInt(parts[1], 10);
      if (!isNaN(start) && !isNaN(end)) {
        return {
          startTime: `${String(start).padStart(2, '0')}:00`,
          endTime:   `${String(end).padStart(2, '0')}:00`,
        };
      }
    }
    return false;
  }

  _flush() {
    for (const op of this._pending) {
      try {
        if (op.op === 'add')    op.list.filter(ev => this._isVisible(ev)).forEach(ev => this._cal.addEvent(_toFC(ev)));
        if (op.op === 'setAll') { this._cal.getEvents().forEach(e => e.remove()); Object.values(this._events).filter(ev => this._isVisible(ev)).forEach(ev => this._cal.addEvent(_toFC(ev))); }
        if (op.op === 'update' && this._events[op.id]) { const fcEv = this._cal.getEventById(op.id); const ev = this._events[op.id]; if (fcEv && this._isVisible(ev)) { fcEv.setProp('title', ev.title || '(no title)'); fcEv.setDates(ev.start, ev.end, { allDay: ev.allDay || false }); } }
        if (op.op === 'delete') { const fcEv = this._cal.getEventById(op.id); if (fcEv) fcEv.remove(); }
      } catch (_) {}
    }
    this._pending = [];
  }

  _reinit() {
    if (!this._cal) return;
    this._closePopover();
    this._closeDialog();
    try { this._miniCal?.destroy(); } catch (_) {}
    try { this._cal.destroy();      } catch (_) {}
    this._cal     = null;
    this._miniCal = null;
    this._ready   = false;
    this._pending = [{ op: 'setAll' }];
    this.innerHTML = '';
    this._render();
    this._init();
  }

  // ── Group rendering & event filtering ────────────────────────────────────────

  _renderGroups() {
    const list = this.querySelector('.vc-groups-list');
    if (!list) return;
    const groups = Object.values(this._groups);
    if (groups.length === 0) { list.innerHTML = ''; return; }
    list.innerHTML = groups.map(g => {
      const hidden = g.visible === false;
      return `<div class="vc-group-item" data-id="${_esc(g.id)}" ${hidden ? 'data-hidden' : ''}>
        <span class="vc-group-dot" style="background:${_esc(g.color)}"></span>
        <span class="vc-group-name" title="${_esc(g.name)}">${_esc(g.name)}</span>
        <span class="vc-group-check"
              style="background:${hidden ? 'transparent' : _esc(g.color)};border-color:${_esc(g.color)}">
          ${hidden ? '' : '&#10003;'}
        </span>
      </div>`;
    }).join('');
    list.querySelectorAll('.vc-group-item').forEach(item => {
      item.addEventListener('click', () => {
        const id     = item.dataset.id;
        const g      = this._groups[id];
        if (!g) return;
        const newVis = g.visible === false;
        this._groups[id] = { ...g, visible: newVis };
        this._renderGroups();
        this._applyFilters();
        this._fire('calendar-group-changed', { id, visible: newVis });
      });
    });
  }

  /** Returns true if the event should currently be shown (passes group + search filters). */
  _isVisible(ev) {
    if (ev.group && this._groups[ev.group] && this._groups[ev.group].visible === false) return false;
    if (this._search) {
      const q = this._search;
      return (ev.title || '').toLowerCase().includes(q)
          || (ev.description || '').toLowerCase().includes(q)
          || (ev.location || '').toLowerCase().includes(q);
    }
    return true;
  }

  /** Re-apply group visibility + search to all events in the FullCalendar instance. */
  _applyFilters() {
    if (!this._ready) return;
    this._cal.getEvents().forEach(e => e.remove());
    Object.values(this._events).filter(ev => this._isVisible(ev)).forEach(ev => this._cal.addEvent(_toFC(ev)));
  }

  // ── Event Popover (click-to-preview) ─────────────────────────────────────────

  _showPopover(ev, fcEvent, jsEvent) {
    this._closePopover();
    this._closeDialog();

    const readOnly = this.hasAttribute('read-only');

    // ── Format date/time display ──────────────────────────────────────────────
    let timeStr = '';
    try {
      const start = new Date(ev.start);
      const end   = new Date(ev.end);
      const dateOpts = { weekday: 'short', month: 'long', day: 'numeric' };
      const timeOpts = { hour: '2-digit', minute: '2-digit' };
      if (ev.allDay) {
        timeStr = start.toLocaleDateString(undefined, dateOpts);
      } else {
        const t1 = start.toLocaleTimeString(undefined, timeOpts);
        const t2 = end.toLocaleTimeString(undefined, timeOpts);
        // Same day → "Wed, April 16 · 10:00 AM – 11:00 AM"
        // Cross-day → show both dates
        const sameDay = start.toDateString() === end.toDateString();
        if (sameDay) {
          timeStr = `${start.toLocaleDateString(undefined, dateOpts)} &middot; ${t1} &ndash; ${t2}`;
        } else {
          timeStr = `${start.toLocaleDateString(undefined, dateOpts)} ${t1} &ndash; ${end.toLocaleDateString(undefined, dateOpts)} ${t2}`;
        }
      }
    } catch (_) { timeStr = ev.start || ''; }

    // ── Group row ─────────────────────────────────────────────────────────────
    const group = ev.group ? this._groups[ev.group] : null;
    const groupHtml = group
      ? `<div class="vc-popover-row">
           <span class="vc-popover-dot" style="background:${_esc(group.color)}"></span>
           <span>${_esc(group.name)}</span>
         </div>` : '';

    const locHtml  = ev.location    ? `<div class="vc-popover-row"><span class="vc-popover-icon">&#128205;</span><span>${_esc(ev.location)}</span></div>` : '';
    const descHtml = ev.description ? `<div class="vc-popover-desc">${_esc(ev.description.slice(0, 140))}${ev.description.length > 140 ? '&hellip;' : ''}</div>` : '';
    const urlHtml  = ev.url
      ? `<div class="vc-popover-row"><span class="vc-popover-icon">&#128279;</span><a href="${_esc(ev.url)}" target="_blank" rel="noopener">${_esc(ev.url.replace(/^https?:\/\//, '').slice(0, 45))}</a></div>`
      : '';

    const repeatHtml = (ev.repeatUnit && ev.repeatUnit !== 'NONE')
      ? `<div class="vc-popover-row"><span class="vc-popover-icon">&#128260;</span><span>${_esc(this._i18n.labelEvery)} ${ev.repeatEvery > 1 ? ev.repeatEvery + ' ' : ''}${_esc(this._i18n.repeatLabels[ev.repeatUnit] || ev.repeatUnit.toLowerCase())}</span></div>`
      : '';

    const editBtn   = readOnly ? '' : `<button class="vc-popover-btn" id="vcp-edit" title="${_esc(this._i18n.tooltipEdit)}">&#9998;</button>`;
    const deleteBtn = readOnly ? '' : `<button class="vc-popover-btn vc-popover-btn-del" id="vcp-del" title="${_esc(this._i18n.tooltipDelete)}">&#128465;</button>`;

    const pop = document.createElement('div');
    pop.className = 'vc-popover';
    pop.innerHTML = `
      <div class="vc-popover-header" style="border-left:4px solid ${_esc(ev.color || '#1a73e8')}">
        <div class="vc-popover-title">${_esc(ev.title)}</div>
        <div class="vc-popover-actions">
          ${editBtn}${deleteBtn}
          <button class="vc-popover-btn" id="vcp-close" title="${_esc(this._i18n.tooltipClose)}">&#10005;</button>
        </div>
      </div>
      <div class="vc-popover-body">
        <div class="vc-popover-time"><span class="vc-popover-icon">&#128336;</span>${timeStr}</div>
        ${locHtml}${urlHtml}${descHtml}${repeatHtml}${groupHtml}
      </div>`;

    document.body.appendChild(pop);
    this._popoverEl = pop;

    // ── Positioning ───────────────────────────────────────────────────────────
    this._positionPopover(pop, jsEvent);

    // ── Close on outside click ────────────────────────────────────────────────
    const onOutside = e => {
      if (!pop.contains(e.target)) { this._closePopover(); document.removeEventListener('mousedown', onOutside, true); }
    };
    setTimeout(() => document.addEventListener('mousedown', onOutside, true), 0);
    pop._removeOutside = () => document.removeEventListener('mousedown', onOutside, true);

    // ── Escape key ────────────────────────────────────────────────────────────
    const onKey = e => { if (e.key === 'Escape') { this._closePopover(); document.removeEventListener('keydown', onKey); } };
    document.addEventListener('keydown', onKey);
    pop._removeKey = () => document.removeEventListener('keydown', onKey);

    pop.querySelector('#vcp-close').addEventListener('click', () => this._closePopover());

    if (!readOnly) {
      pop.querySelector('#vcp-edit').addEventListener('click', () => {
        this._closePopover();
        this._showDialog({ ...ev }, true, fcEvent);
      });
      pop.querySelector('#vcp-del').addEventListener('click', () => {
        if (fcEvent) fcEvent.remove();
        delete this._events[ev.id];
        this._fire('calendar-event-deleted', { event: ev, id: ev.id });
        this._closePopover();
      });
    }
  }

  _positionPopover(pop, jsEvent) {
    // Temporarily make visible to measure
    pop.style.visibility = 'hidden';
    pop.style.position   = 'fixed';
    pop.style.left = '0px';
    pop.style.top  = '0px';

    requestAnimationFrame(() => {
      const pw = pop.offsetWidth  || 300;
      const ph = pop.offsetHeight || 220;
      const vw = window.innerWidth;
      const vh = window.innerHeight;
      const cx = jsEvent?.clientX ?? vw / 2;
      const cy = jsEvent?.clientY ?? vh / 2;
      const gap = 12;

      let left = cx + gap;
      let top  = cy - 10;

      if (left + pw > vw - gap) left = cx - pw - gap;
      if (top  + ph > vh - gap) top  = vh - ph - gap;
      if (left < gap) left = gap;
      if (top  < gap) top  = gap;

      pop.style.left       = `${left}px`;
      pop.style.top        = `${top}px`;
      pop.style.visibility = 'visible';
    });
  }

  _closePopover() {
    if (this._popoverEl) {
      try { this._popoverEl._removeOutside?.(); } catch (_) {}
      try { this._popoverEl._removeKey?.();     } catch (_) {}
      this._popoverEl.remove();
      this._popoverEl = null;
    }
  }

  // ── Add / Edit / Delete dialog ────────────────────────────────────────────────

  _showDialog(ev, isEdit, fcEvent) {
    this._closeDialog();

    const bg = document.createElement('div');
    bg.className = 'vc-dialog-bg';
    bg.addEventListener('click', e => { if (e.target === bg) this._closeDialog(); });

    const dlg = document.createElement('div');
    dlg.className = 'vc-dialog';

    const selColor = ev.color || '#1a73e8';
    const swatches = EVENT_COLORS.map(c =>
      `<span class="vc-color-swatch${c.hex === selColor ? ' selected' : ''}"
             data-hex="${c.hex}" data-text="${c.textHex}"
             style="background:${c.hex}" title="${c.label}"></span>`
    ).join('');

    const repeatUnits = ['NONE','DAILY','WEEKLY','MONTHLY','YEARLY']
      .map(u => `<option value="${u}"${(ev.repeatUnit || 'NONE') === u ? ' selected' : ''}>${_esc(this._i18n.repeatLabels[u] || u.charAt(0) + u.slice(1).toLowerCase())}</option>`)
      .join('');

    const groupKeys  = Object.values(this._groups);
    const groupSelect = groupKeys.length > 0
      ? `<label>${_esc(this._i18n.labelCalendar)}</label>
         <select id="vc-group">
           <option value="">${_esc(this._i18n.noneCalendar)}</option>
           ${groupKeys.map(g => `<option value="${_esc(g.id)}"${ev.group === g.id ? ' selected' : ''}>${_esc(g.name)}</option>`).join('')}
         </select>`
      : '';

    const isAllDay = ev.allDay || false;

    dlg.innerHTML = `
      <h3>${isEdit ? _esc(this._i18n.editEvent) : _esc(this._i18n.newEvent)}</h3>

      <label>${_esc(this._i18n.labelTitle)}</label>
      <input type="text" id="vc-title" value="${_esc(ev.title || '')}" placeholder="${_esc(this._i18n.placeholderTitle)}" />

      <div class="vc-allday-row">
        <input type="checkbox" class="vc-toggle" id="vc-allday" ${isAllDay ? 'checked' : ''} />
        <label for="vc-allday">${_esc(this._i18n.allDay)}</label>
      </div>

      <div class="vc-row">
        <div>
          <label>${_esc(this._i18n.labelStart)}</label>
          <input type="${isAllDay ? 'date' : 'datetime-local'}" id="vc-start"
                 value="${isAllDay ? _toLocalDate(ev.start) : _toLocal(ev.start)}" />
        </div>
        <div>
          <label>${_esc(this._i18n.labelEnd)}</label>
          <input type="${isAllDay ? 'date' : 'datetime-local'}" id="vc-end"
                 value="${isAllDay ? _toLocalDate(ev.end) : _toLocal(ev.end)}" />
        </div>
      </div>

      <label>${_esc(this._i18n.labelDescription)}</label>
      <input type="text" id="vc-desc" value="${_esc(ev.description || '')}" placeholder="${_esc(this._i18n.placeholderDescription)}" />

      <label>${_esc(this._i18n.labelLocation)}</label>
      <input type="text" id="vc-location" value="${_esc(ev.location || '')}" placeholder="${_esc(this._i18n.placeholderLocation)}" />

      <label>${_esc(this._i18n.labelUrl)}</label>
      <input type="url" id="vc-url" value="${_esc(ev.url || '')}" placeholder="https://" />

      ${groupSelect}

      <div class="vc-section-title">${_esc(this._i18n.labelColor)}</div>
      <div class="vc-colors" id="vc-colors">${swatches}</div>
      <input type="hidden" id="vc-color"      value="${selColor}" />
      <input type="hidden" id="vc-color-text" value="${ev.colorText || '#ffffff'}" />

      <div class="vc-section-title">${_esc(this._i18n.labelRepeat)}</div>
      <div class="vc-repeat-row">
        <div>
          <label>${_esc(this._i18n.labelEvery)}</label>
          <input type="number" id="vc-repeat-every" min="1" max="999"
                 value="${ev.repeatEvery > 0 ? ev.repeatEvery : 1}"
                 style="opacity:${(ev.repeatUnit || 'NONE') === 'NONE' ? '.4' : '1'}" />
        </div>
        <div>
          <label>${_esc(this._i18n.labelUnit)}</label>
          <select id="vc-repeat-unit">${repeatUnits}</select>
        </div>
      </div>

      <div class="vc-dialog-actions">
        ${isEdit ? `<button class="vc-btn vc-btn-danger" id="vc-del">${_esc(this._i18n.delete)}</button>` : ''}
        <button class="vc-btn vc-btn-ghost" id="vc-cancel">${_esc(this._i18n.cancel)}</button>
        <button class="vc-btn vc-btn-primary" id="vc-save">${isEdit ? _esc(this._i18n.saveChanges) : _esc(this._i18n.addEventBtn)}</button>
      </div>`;

    bg.appendChild(dlg);
    document.body.appendChild(bg);
    this._dialogBg = bg;
    dlg.querySelector('#vc-title').focus();

    dlg.querySelector('#vc-allday').addEventListener('change', e => {
      const allDay  = e.target.checked;
      const startIn = dlg.querySelector('#vc-start');
      const endIn   = dlg.querySelector('#vc-end');
      const sVal = startIn.value, eVal = endIn.value;
      startIn.type = allDay ? 'date' : 'datetime-local';
      endIn.type   = allDay ? 'date' : 'datetime-local';
      startIn.value = allDay ? sVal.slice(0, 10) : (sVal + 'T00:00');
      endIn.value   = allDay ? eVal.slice(0, 10) : (eVal + 'T00:00');
    });

    dlg.querySelector('#vc-colors').addEventListener('click', e => {
      const sw = e.target.closest('.vc-color-swatch');
      if (!sw) return;
      dlg.querySelectorAll('.vc-color-swatch').forEach(s => s.classList.remove('selected'));
      sw.classList.add('selected');
      dlg.querySelector('#vc-color').value      = sw.dataset.hex;
      dlg.querySelector('#vc-color-text').value = sw.dataset.text;
    });

    dlg.querySelector('#vc-repeat-unit').addEventListener('change', e => {
      dlg.querySelector('#vc-repeat-every').style.opacity = e.target.value === 'NONE' ? '.4' : '1';
    });

    dlg.querySelector('#vc-cancel').addEventListener('click', () => this._closeDialog());

    dlg.querySelector('#vc-save').addEventListener('click', () => {
      const allDay      = dlg.querySelector('#vc-allday').checked;
      const title       = dlg.querySelector('#vc-title').value.trim() || '(no title)';
      const startRaw    = dlg.querySelector('#vc-start').value;
      const endRaw      = dlg.querySelector('#vc-end').value;
      const start       = allDay ? _fromLocalDate(startRaw) : _fromLocal(startRaw);
      const end         = allDay ? _fromLocalDate(endRaw)   : (_fromLocal(endRaw) || start);
      const desc        = dlg.querySelector('#vc-desc').value.trim();
      const location    = dlg.querySelector('#vc-location').value.trim();
      const url         = dlg.querySelector('#vc-url').value.trim();
      const color       = dlg.querySelector('#vc-color').value;
      const colorText   = dlg.querySelector('#vc-color-text').value;
      const repeatUnit  = dlg.querySelector('#vc-repeat-unit').value;
      const repeatEvery = repeatUnit === 'NONE' ? 0 : Math.max(1, parseInt(dlg.querySelector('#vc-repeat-every').value, 10) || 1);
      const group       = dlg.querySelector('#vc-group')?.value || ev.group || '';
      if (!start) return;

      if (isEdit && fcEvent) {
        fcEvent.setProp('title', title);
        fcEvent.setDates(start, end, { allDay });
        fcEvent.setProp('backgroundColor', color);
        fcEvent.setProp('borderColor',     color);
        fcEvent.setProp('textColor',       colorText);
        const updated = _fromFC(fcEvent);
        Object.assign(updated, { description: desc, location, url, color, colorText, repeatUnit, repeatEvery, group });
        updated._color = color; updated._colorText = colorText;
        this._events[updated.id] = updated;
        if (!this._isVisible(updated)) fcEvent.remove();
        this._fire('calendar-event-updated', { event: updated });
      } else {
        const newEv = {
          id: _genId(), title, start, end, allDay, description: desc,
          location, url, color, colorText, repeatUnit, repeatEvery, group,
          organizerName: '', organizerEmailAddress: '', _meta: {},
        };
        this._events[newEv.id] = newEv;
        if (this._isVisible(newEv)) this._cal.addEvent(_toFC(newEv));
        this._fire('calendar-event-created', { event: newEv });
      }
      this._closeDialog();
    });

    if (isEdit) {
      dlg.querySelector('#vc-del').addEventListener('click', () => {
        const evId = ev.id;
        if (fcEvent) fcEvent.remove();
        delete this._events[evId];
        this._fire('calendar-event-deleted', { event: ev, id: evId });
        this._closeDialog();
      });
    }
  }

  _showLoading(active) {
    const el = this.querySelector('.vc-loading');
    if (el) el.classList.toggle('vc-loading-active', active);
  }

  _closeDialog() {
    if (this._dialogBg) { this._dialogBg.remove(); this._dialogBg = null; }
  }

  _fire(name, detail) {
    this.dispatchEvent(new CustomEvent(name, { detail, bubbles: true, composed: true }));
  }
}

if (!customElements.get('vaadin-calendar')) {
  customElements.define('vaadin-calendar', VaadinCalendarElement);
}

export default VaadinCalendarElement;

