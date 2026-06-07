/**
 * <timeline-stepper> — Vertical audit log / event history timeline
 * with infinite-scroll lazy loading for Vaadin Java Flow.
 *
 * Place this file at: src/main/frontend/timeline-stepper.js
 *
 * ── Attributes ──────────────────────────────────────────────────────────
 *   items      JSON array of AuditEntry objects (see schema)
 *   has-more   "true" | "false"  — whether more pages exist
 *   loading    "true" | "false"  — shows skeleton rows while fetching
 *   page-size  number            — items per page (default 20)
 *
 * ── AuditEntry schema ───────────────────────────────────────────────────
 *   {
 *     id:        string,
 *     timestamp: string,          // ISO-8601 or any display string
 *     actor:     string,          // "Jane Smith"
 *     actorRole: string,          // optional  e.g. "Admin"
 *     action:    string,          // "Updated payment method"
 *     detail:    string,          // optional — extra context shown inline
 *     severity:  "info" | "success" | "warning" | "error",
 *     category:  string           // optional badge e.g. "Auth" | "Billing"
 *   }
 *
 * ── Events (bubbles + composed) ─────────────────────────────────────────
 *   tl-load-more    detail: { page, pageSize }   — sentinel entered viewport
 *   tl-entry-click  detail: { id, entry }        — row clicked / activated
 *
 * ── Public JS API (callable via element.callJsFunction from Java) ────────
 *   appendEntries(jsonArray)   — push next page; clears loading state
 *   prependEntries(jsonArray)  — prepend new live events at top
 *   updateEntry(jsonObject)    — patch a single entry by id
 *   removeEntry(id)            — remove entry by id
 *   setLoading(bool)           — toggle loading skeleton manually
 *   setHasMore(bool)           — update pagination flag
 *   clearAll()                 — reset to empty state
 */

class TimelineStepper extends HTMLElement {

  static get observedAttributes() {
    return ['items', 'has-more', 'loading', 'page-size', 'clickable'];
  }

  constructor() {
    super();
    this.attachShadow({ mode: 'open' });
    this._entries  = [];
    this._hasMore  = false;
    this._loading  = false;
    this._pageSize = 20;
    this._page     = 0;
    this._observer = null;
    this._firing   = false;
  }

  connectedCallback()    { this._parse(); this._render(); this._bindSentinel(); }
  disconnectedCallback() { if (this._observer) this._observer.disconnect(); }

  attributeChangedCallback(name, oldVal, newVal) {
    if (oldVal === newVal) return;
    this._parse();
    this._render();
    this._bindSentinel();
  }

  /* ── Public API ─────────────────────────────────────────────────────── */

  appendEntries(jsonArray) {
    const arr = this._coerce(jsonArray);
    this._entries  = [...this._entries, ...arr];
    this._page    += 1;
    this._loading  = false;
    this._firing   = false;
    this._render();
    this._bindSentinel();
  }

  prependEntries(jsonArray) {
    this._entries = [...this._coerce(jsonArray), ...this._entries];
    this._render();
  }

  updateEntry(jsonObject) {
    const obj = this._coerce(jsonObject);
    this._entries = this._entries.map(e => e.id === obj.id ? { ...e, ...obj } : e);
    this._render();
  }

  removeEntry(id) {
    this._entries = this._entries.filter(e => e.id !== id);
    this._render();
  }

  setLoading(bool) { this._loading = bool; this._render(); }
  setHasMore(bool) { this._hasMore = bool; this._render(); this._bindSentinel(); }

  clearAll() {
    this._entries = [];
    this._page    = 0;
    this._firing  = false;
    this._render();
    this._bindSentinel();
  }

  /* ── Internals ──────────────────────────────────────────────────────── */

  _coerce(val) {
    if (typeof val === 'string') { try { return JSON.parse(val); } catch { return []; } }
    return Array.isArray(val) ? val : [val];
  }

  _parse() {
    try   { this._entries = JSON.parse(this.getAttribute('items') || '[]'); }
    catch { this._entries = []; }
    this._hasMore  = this.getAttribute('has-more')  === 'true';
    this._loading  = this.getAttribute('loading')   === 'true';
    this._pageSize = parseInt(this.getAttribute('page-size') || '20', 10);
    this._clickable = this.hasAttribute('clickable');
  }

  _bindSentinel() {
    if (this._observer) { this._observer.disconnect(); this._observer = null; }
    const s = this.shadowRoot.getElementById('tl-sentinel');
    if (!s || !this._hasMore) return;
    this._observer = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && !this._loading && !this._firing) {
        this._firing  = true;
        this._loading = true;
        this._appendSkeletons();
        this._emit('tl-load-more', { page: this._page + 1, pageSize: this._pageSize });
      }
    }, { rootMargin: '120px', threshold: 0 });
    this._observer.observe(s);
  }

  _emit(name, detail) {
    this.dispatchEvent(new CustomEvent(name, { detail, bubbles: true, composed: true }));
  }

  /* ── Rendering helpers ──────────────────────────────────────────────── */

  _severityMeta(s) {
    // Tailwind-200 tints — more saturated than 100 for small 28px dots to be clearly legible
    const m = {
      success: { dot: '#a7f3d0', fg: '#065f46', label: '#065f46', icon: '✓' },
      warning: { dot: '#fde68a', fg: '#92400e', label: '#92400e', icon: '!'  },
      error:   { dot: '#fecaca', fg: '#991b1b', label: '#991b1b', icon: '✕' },
      info:    { dot: '#ddd6fe', fg: '#4c1d95', label: '#4c1d95', icon: 'i'  },
    };
    return m[s] || m.info;
  }

  _formatTs(ts) {
    try {
      const d = new Date(ts);
      if (isNaN(d)) return ts;
      return d.toLocaleString(undefined, {
        month:'short', day:'numeric', year:'numeric',
        hour:'2-digit', minute:'2-digit'
      });
    } catch { return ts; }
  }

  _initials(name) {
    if (!name) return '?';
    return name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
  }

  _avatarPalette(name) {
    // Tailwind-200 tints — small 20px avatar circles need more saturation than 100-level
    const palettes = [
      ['#bfdbfe','#1e40af'], ['#a7f3d0','#065f46'], ['#ddd6fe','#4c1d95'],
      ['#fde68a','#92400e'], ['#fbcfe8','#9d174d'], ['#bbf7d0','#166534'],
    ];
    let h = 0;
    for (let i = 0; i < (name || '').length; i++) h = (h * 31 + name.charCodeAt(i)) & 0xffff;
    return palettes[h % palettes.length];
  }

  _entryHtml(entry, idx, total) {
    const m   = this._severityMeta(entry.severity);
    const [aBg, aFg] = this._avatarPalette(entry.actor);
    const ts  = this._formatTs(entry.timestamp);
    const isLast = idx === total - 1;

    const badge = entry.category
      ? `<span class="tl-badge" style="background:${m.dot};color:${m.label};">${entry.category}</span>`
      : '';

    return `
      <div class="tl-entry" data-id="${entry.id}"
           role="listitem" tabindex="${this._clickable ? '0' : '-1'}"
           aria-label="${entry.action} — ${entry.actor || 'System'} — ${ts}">
        <div class="tl-rail">
          <div class="tl-dot" style="background:${m.dot};">
            <span class="tl-icon" style="color:${m.fg};">${m.icon}</span>
          </div>
          ${!isLast ? '<div class="tl-line"></div>' : ''}
        </div>
        <div class="tl-content">
          <div class="tl-top">
            <span class="tl-action">${entry.action}</span>${badge}
          </div>
          ${entry.detail ? `<div class="tl-detail">${entry.detail}</div>` : ''}
          <div class="tl-meta-row">
            <span class="tl-avatar" style="background:${aBg};color:${aFg};">${this._initials(entry.actor)}</span>
            <span class="tl-actor">${entry.actor || 'System'}${entry.actorRole ? `<em class="tl-role"> · ${entry.actorRole}</em>` : ''}</span>
            <span class="tl-dot-sep"></span>
            <time class="tl-time">${ts}</time>
          </div>
        </div>
      </div>`;
  }

  _skeletonRows(n = 3) {
    return Array.from({ length: n }, (_, i) => `
      <div class="tl-entry tl-skel" aria-hidden="true">
        <div class="tl-rail">
          <div class="tl-dot" style="background:#e5e7eb;box-shadow:none;"></div>
          ${i < n - 1 ? '<div class="tl-line"></div>' : ''}
        </div>
        <div class="tl-content" style="flex:1;padding-bottom:${i < n-1 ? '0':'0'}">
          <div class="skel" style="width:${55+i*10}%;height:13px;margin-bottom:7px;"></div>
          <div class="skel" style="width:${30+i*5}%;height:11px;margin-bottom:10px;"></div>
          <div class="skel" style="width:25%;height:10px;"></div>
        </div>
      </div>`).join('');
  }

  _appendSkeletons() {
    const list = this.shadowRoot.getElementById('tl-list');
    if (!list || list.querySelector('.skel-group')) return;
    const wrap = document.createElement('div');
    wrap.className = 'skel-group';
    wrap.innerHTML = this._skeletonRows();
    list.appendChild(wrap);
  }

  _render() {
    const entries  = this._entries;
    const total    = entries.length;

    const entriesHtml = entries.map((e, i) => this._entryHtml(e, i, total)).join('');

    const emptyHtml = !total && !this._loading ? `
      <div class="tl-empty" role="status" aria-live="polite">
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#d1d5db" stroke-width="1.5" stroke-linecap="round">
          <circle cx="12" cy="12" r="10"/><path d="M12 8v4l3 3"/>
        </svg>
        <span>No audit events yet</span>
      </div>` : '';

    const loadingHtml = this._loading && !total ? this._skeletonRows() : '';

    const sentinelHtml = this._hasMore
      ? `<div id="tl-sentinel" style="height:1px;"></div>` : '';

    const endHtml = !this._hasMore && total > 0 && !this._loading
      ? `<div class="tl-end" role="status">— End of audit log —</div>` : '';

    this.shadowRoot.innerHTML = `
      <style>
        :host {
          display: block;
          font-family: 'Segoe UI', system-ui, sans-serif;
          color-scheme: light dark;
        }
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        #tl-list { display: flex; flex-direction: column; width: 100%; }

        /* ── Entry row ── */
        .tl-entry {
          display: flex; gap: 0; outline: none;
          border-radius: 8px; cursor: default;
          transition: background 0.15s;
        }
        /* Hover and pointer effects only when a Java click listener is registered */
        :host([clickable]) .tl-entry:not(.tl-skel) { cursor: pointer; }
        :host([clickable]) .tl-entry:not(.tl-skel):hover .tl-content { background: light-dark(#f9fafb, #1f2937); }
        :host([clickable]) .tl-entry:not(.tl-skel):hover .tl-dot { transform: scale(1.1); }
        .tl-entry:focus-visible { box-shadow: 0 0 0 2px #378add; border-radius: 8px; }
        .tl-skel { cursor: default; pointer-events: none; }

        /* ── Left rail ── */
        .tl-rail {
          display: flex; flex-direction: column; align-items: center;
          width: 44px; flex-shrink: 0; padding-top: 3px;
        }
        .tl-dot {
          width: 28px; height: 28px; border-radius: 50%; flex-shrink: 0;
          display: flex; align-items: center; justify-content: center;
          position: relative; z-index: 1;
          transition: transform 0.15s;
        }
        .tl-icon { font-size: 11px; font-weight: 700; font-style: normal; line-height: 1; }
        .tl-line {
          flex: 1; width: 2px; min-height: 24px; margin: 5px 0;
          background: light-dark(#e5e7eb, #374151); border-radius: 1px;
        }

        /* ── Content ── */
        .tl-content {
          flex: 1; padding: 5px 10px 16px 8px;
          border-radius: 8px; transition: background 0.15s;
        }
        .tl-top {
          display: flex; align-items: center; flex-wrap: wrap;
          gap: 6px; margin-bottom: 3px;
        }
        .tl-action {
          font-size: 13.5px; font-weight: 600;
          color: light-dark(#111827, #f9fafb); line-height: 1.4;
        }
        .tl-badge {
          font-size: 10px; font-weight: 600; letter-spacing: .05em;
          text-transform: uppercase; padding: 2px 7px;
          border-radius: 20px; flex-shrink: 0;
        }
        .tl-detail {
          font-size: 12.5px; line-height: 1.55; margin-bottom: 8px;
          color: light-dark(#4b5563, #9ca3af);
        }
        .tl-meta-row {
          display: flex; align-items: center; gap: 6px; flex-wrap: wrap;
        }
        .tl-avatar {
          width: 20px; height: 20px; border-radius: 50%; flex-shrink: 0;
          display: flex; align-items: center; justify-content: center;
          font-size: 9px; font-weight: 700;
        }
        .tl-actor {
          font-size: 12px; font-weight: 500;
          color: light-dark(#6b7280, #9ca3af);
        }
        .tl-role { font-weight: 400; font-style: normal; color: light-dark(#9ca3af, #6b7280); }
        .tl-dot-sep {
          width: 3px; height: 3px; border-radius: 50%;
          background: light-dark(#d1d5db, #4b5563); flex-shrink: 0;
        }
        .tl-time { font-size: 11.5px; color: light-dark(#9ca3af, #6b7280); }

        /* ── Skeleton shimmer ── */
        @keyframes shimmer {
          0%   { background-position: 200% 0; }
          100% { background-position: -200% 0; }
        }
        .skel {
          border-radius: 4px;
          background: linear-gradient(90deg, #f3f4f6 25%, #e9eaec 50%, #f3f4f6 75%);
          background-size: 200% 100%;
          animation: shimmer 1.4s infinite;
        }

        /* ── Entry appear animation ── */
        @keyframes fadeUp {
          from { opacity: 0; transform: translateY(5px); }
          to   { opacity: 1; transform: translateY(0); }
        }
        .tl-entry:not(.tl-skel) { animation: fadeUp 0.2s ease both; }

        /* ── Empty state ── */
        .tl-empty {
          display: flex; flex-direction: column; align-items: center;
          gap: 10px; padding: 3rem 1rem;
          color: light-dark(#9ca3af, #6b7280); font-size: 13px;
        }

        /* ── End marker ── */
        .tl-end {
          text-align: center; font-size: 11.5px; letter-spacing: .05em;
          color: light-dark(#d1d5db, #374151);
          padding: 1rem 0 0.25rem;
        }
      </style>

      <div id="tl-list" role="list" aria-label="Audit event log" aria-live="polite">
        ${emptyHtml}
        ${entriesHtml}
        ${loadingHtml}
        ${sentinelHtml}
        ${endHtml}
      </div>`;

    const list = this.shadowRoot.getElementById('tl-list');

    list.addEventListener('click', e => {
      const row = e.target.closest('.tl-entry:not(.tl-skel)');
      if (!row) return;
      const entry = this._entries.find(en => en.id === row.dataset.id);
      this._emit('tl-entry-click', { id: row.dataset.id, entry });
    });

    list.addEventListener('keydown', e => {
      if (e.key !== 'Enter' && e.key !== ' ') return;
      const row = e.target.closest('.tl-entry:not(.tl-skel)');
      if (!row) return;
      e.preventDefault();
      const entry = this._entries.find(en => en.id === row.dataset.id);
      this._emit('tl-entry-click', { id: row.dataset.id, entry });
    });
  }
}

customElements.define('timeline-stepper', TimelineStepper);

