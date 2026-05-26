/**
 * <flow-stepper> — A custom stepper web component for Vaadin Java Flow
 *
 * Usage in Java:
 *   @JsModule("./stepper-component.js")
 *   @Tag("flow-stepper")
 *   public class StepperComponent extends Component { ... }
 *
 * HTML attributes:
 *   steps         — JSON array of step labels, e.g. '["Account","Details","Confirm"]'
 *   current-step  — 0-based index of the active step (default: 0)
 *   orientation   — "horizontal" | "vertical" (default: "horizontal")
 *   variant       — "default" | "numbered" | "dot" (default: "default")
 *   error-steps   — JSON array of 0-based step indices that are in error, e.g. '[0,2]'
 *
 * Events fired on the element:
 *   step-changed  — detail: { step: Number, label: String }
 *   step-complete — detail: { step: Number }
 *   step-error    — detail: { step: Number, label: String }
 */

class FlowStepper extends HTMLElement {

  static get observedAttributes() {
    return ['steps', 'current-step', 'orientation', 'variant', 'error-steps', 'click-nav'];
  }

  constructor() {
    super();
    this.attachShadow({ mode: 'open' });
    this._steps = [];
    this._current = 0;
    this._orientation = 'horizontal';
    this._variant = 'default';
    this._errorSteps = new Set();
  }

  connectedCallback() {
    this._parse();
    this._render();
  }

  attributeChangedCallback(name, oldVal, newVal) {
    if (oldVal === newVal) return;
    this._parse();
    this._render();
  }

  /* ── Public API (callable from Java via Element.callJsFunction) ── */

  /** Move to a specific step index */
  goToStep(index) {
    const i = Math.max(0, Math.min(index, this._steps.length - 1));
    this._current = i;
    this.setAttribute('current-step', i);
    this._render();
    this._emit('step-changed', { step: i, label: this._steps[i] });
  }

  /** Advance one step */
  nextStep() { this.goToStep(this._current + 1); }

  /** Go back one step */
  prevStep() { this.goToStep(this._current - 1); }

  /** Mark current step complete and advance */
  completeStep() {
    this._errorSteps.delete(this._current);
    this._emit('step-complete', { step: this._current });
    this.nextStep();
  }

  /** Mark a step as failed (shows red ✕ icon) */
  markStepError(index) {
    this._errorSteps.add(index);
    this._syncErrorAttr();
    this._render();
    this._emit('step-error', { step: index, label: this._steps[index] });
  }

  /** Clear the error state on a specific step */
  clearStepError(index) {
    this._errorSteps.delete(index);
    this._syncErrorAttr();
    this._render();
  }

  /** Clear all error states */
  clearAllErrors() {
    this._errorSteps.clear();
    this._syncErrorAttr();
    this._render();
  }

  /** Current step index */
  get currentStep() { return this._current; }

  /* ── Internals ── */

  _parse() {
    try { this._steps = JSON.parse(this.getAttribute('steps') || '[]'); }
    catch { this._steps = []; }
    this._current = parseInt(this.getAttribute('current-step') || '0', 10);
    this._orientation = this.getAttribute('orientation') || 'horizontal';
    this._variant = this.getAttribute('variant') || 'default';
    try {
      const errArr = JSON.parse(this.getAttribute('error-steps') || '[]');
      this._errorSteps = new Set(errArr);
    } catch { this._errorSteps = new Set(); }
    // 'completed' (default) | 'all' | 'none'
    this._clickNav = this.getAttribute('click-nav') || 'completed';
  }

  _syncErrorAttr() {
    this.setAttribute('error-steps', JSON.stringify([...this._errorSteps]));
  }

  _emit(name, detail) {
    this.dispatchEvent(new CustomEvent(name, { detail, bubbles: true, composed: true }));
  }

  _stepState(i) {
    if (this._errorSteps.has(i)) return 'error';
    if (i < this._current) return 'completed';
    if (i === this._current) return 'active';
    return 'pending';
  }

  /**
   * Returns true when clicking step i should trigger navigation.
   *   'none'      — never clickable (pure display)
   *   'all'       — every step is directly jumpable
   *   'completed' — only completed (back-nav) and error (retry) steps
   */
  _isClickable(i, state) {
    if (this._clickNav === 'none') return false;
    if (this._clickNav === 'all') return true;
    return state === 'completed' || state === 'error';
  }

  _render() {
    const isVertical = this._orientation === 'vertical';

    const stepsHtml = this._steps.map((label, i) => {
      const state = this._stepState(i);
      const clickable = this._isClickable(i, state);
      const icon = state === 'completed'
        ? `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
              stroke-linecap="round" stroke-linejoin="round">
              <polyline points="20 6 9 17 4 12"/>
            </svg>`
        : state === 'error'
          ? `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                stroke-linecap="round" stroke-linejoin="round">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>`
          : this._variant === 'dot'
            ? ''
            : `<span class="step-num">${i + 1}</span>`;

      const connClass = state === 'error' || (i + 1 < this._steps.length && this._stepState(i + 1) === 'error')
        ? 'errored'
        : i < this._current && !this._errorSteps.has(i) ? 'filled' : '';

      const connector = i < this._steps.length - 1
        ? `<div class="connector ${connClass}">
             <div class="connector-line"></div>
           </div>`
        : '';

      return `
        <div class="step-wrapper ${isVertical ? 'vertical' : ''}">
          <div class="step-item ${state}${clickable ? ' clickable' : ''}" data-index="${i}"
               role="${clickable ? 'button' : 'listitem'}"
               tabindex="${clickable ? '0' : '-1'}"
               aria-current="${state === 'active' ? 'step' : 'false'}"
               aria-invalid="${state === 'error'}"
               aria-label="Step ${i + 1}: ${label} — ${state}">
            <div class="step-node ${this._variant}">
              ${icon}
            </div>
            <div class="step-label-group">
              <span class="step-index-label">Step ${i + 1}</span>
              <span class="step-label">${label}</span>
            </div>
          </div>
          ${connector}
        </div>`;
    }).join('');

    this.shadowRoot.innerHTML = `
      <style>
        :host {
          display: block;
          font-family: 'Segoe UI', system-ui, sans-serif;
          /* connector / label palette */
          --color-border:    #e2e8f0;
          --color-text:      #111827;
          --color-text-sub:  #94a3b8;
          /* tinted badge palette — mirrors icon-badge / avatar--badge variants */
          --color-pending-bg:  #f1f5f9;
          --color-pending-fg:  #64748b;
          --color-active-bg:   #dbeafe;
          --color-active:      #1e40af;
          --color-done-bg:     #d1fae5;
          --color-done:        #065f46;
          --color-error-bg:    #fee2e2;
          --color-error:       #991b1b;
          --color-error-con:   #fca5a5;
          --radius:          50%;
          --node-size:       2.4rem;
          --conn-thickness:  2px;
          --transition:      0.28s cubic-bezier(.4,0,.2,1);
        }

        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        .stepper {
          display: flex;
          flex-direction: ${isVertical ? 'column' : 'row'};
          align-items: ${isVertical ? 'flex-start' : 'center'};
          gap: 0;
          width: 100%;
          padding: ${isVertical ? '0.5rem 0' : '1.25rem 0'};
        }

        .step-wrapper {
          display: flex;
          flex-direction: ${isVertical ? 'column' : 'row'};
          align-items: ${isVertical ? 'flex-start' : 'center'};
          flex: 1;
        }

        /* Connector */
        .connector {
          flex: 1;
          display: flex;
          align-items: center;
          justify-content: ${isVertical ? 'flex-start' : 'center'};
          padding: ${isVertical ? '0 0 0 calc(var(--node-size) / 2 - var(--conn-thickness) / 2)' : '0 0.5rem'};
          min-height: ${isVertical ? '2rem' : 'auto'};
          min-width: ${isVertical ? 'auto' : '2rem'};
          position: relative;
        }
        .connector-line {
          background: var(--color-border);
          transition: background var(--transition);
          ${isVertical
            ? `width: var(--conn-thickness); height: 100%; min-height: 2rem;`
            : `height: var(--conn-thickness); width: 100%;`
          }
        }
        .connector.filled .connector-line {
          background: var(--color-done);
        }
        .connector.errored .connector-line {
          background: var(--color-error-con);
        }

        /* Step Item — base: not interactive */
        .step-item {
          display: flex;
          flex-direction: ${isVertical ? 'row' : 'column'};
          align-items: center;
          gap: ${isVertical ? '0.75rem' : '0.5rem'};
          cursor: default;
          padding: 0.25rem;
          border-radius: 0.5rem;
          outline: none;
          transition: background var(--transition);
        }

        /* Only clickable steps get pointer cursor, focus ring and hover scale */
        .step-item.clickable {
          cursor: pointer;
        }
        .step-item.clickable:focus-visible {
          box-shadow: 0 0 0 3px var(--color-active);
        }
        .step-item.clickable:hover .step-node {
          transform: scale(1.08);
        }

        /* Node — tinted badge style: filled background, no visible border */
        .step-node {
          width: var(--node-size);
          height: var(--node-size);
          border-radius: var(--radius);
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
          border: 2px solid transparent;
          background: var(--color-pending-bg);
          color: var(--color-pending-fg);
          font-weight: 600;
          font-size: 0.875rem;
          transition: all var(--transition);
          position: relative;
          z-index: 1;
        }
        .step-node svg {
          width: 1rem; height: 1rem;
        }
        .step-num {
          line-height: 1;
        }

        /* Dot variant */
        .step-node.dot {
          width: 1rem;
          height: 1rem;
          border-radius: 50%;
        }

        /* States — tinted fill, no border, strong icon colour */
        .step-item.active .step-node {
          background: var(--color-active-bg);
          color: var(--color-active);
          border-color: transparent;
          box-shadow: 0 0 0 3px color-mix(in srgb, var(--color-active) 20%, transparent);
        }
        .step-item.completed .step-node {
          background: var(--color-done-bg);
          color: var(--color-done);
          border-color: transparent;
        }
        .step-item.error .step-node {
          background: var(--color-error-bg);
          color: var(--color-error);
          border-color: transparent;
          box-shadow: 0 0 0 3px color-mix(in srgb, var(--color-error) 20%, transparent);
        }

        /* Labels */
        .step-label-group {
          display: flex;
          flex-direction: column;
          align-items: ${isVertical ? 'flex-start' : 'center'};
          gap: 0.05rem;
        }
        .step-index-label {
          font-size: 0.68rem;
          font-weight: 500;
          letter-spacing: 0.04em;
          text-transform: uppercase;
          color: var(--color-text-sub);
          transition: color var(--transition);
        }
        .step-label {
          font-size: 0.85rem;
          font-weight: 500;
          color: var(--color-text-sub);
          transition: color var(--transition);
          white-space: nowrap;
        }
        .step-item.active .step-label {
          color: var(--color-active);
          font-weight: 600;
        }
        .step-item.active .step-index-label {
          color: var(--color-active);
        }
        .step-item.completed .step-label,
        .step-item.completed .step-index-label {
          color: var(--color-done);
        }
        .step-item.error .step-label,
        .step-item.error .step-index-label {
          color: var(--color-error);
        }
      </style>
      <div class="stepper" role="list" aria-label="Progress steps">
        ${stepsHtml}
      </div>`;

    /* Click / keyboard handling — only fires when step is actually clickable */
    this.shadowRoot.querySelectorAll('.step-item').forEach(el => {
      el.addEventListener('click', () => {
        const idx   = parseInt(el.dataset.index, 10);
        const state = this._stepState(idx);
        if (this._isClickable(idx, state)) {
          this.goToStep(idx);
        }
      });
      el.addEventListener('keydown', e => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault();
          const idx   = parseInt(el.dataset.index, 10);
          const state = this._stepState(idx);
          if (this._isClickable(idx, state)) {
            this.goToStep(idx);
          }
        }
      });
    });
  }
}

customElements.define('flow-stepper', FlowStepper);

