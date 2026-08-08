// ─── CDN loader (singleton) ───────────────────────────────────────────────────
// Chart.js is loaded once from CDN and cached; subsequent calls reuse the same
// promise so the <script> tag is never injected more than once.
const CHART_JS_CDN = 'https://cdn.jsdelivr.net/npm/chart.js@4/dist/chart.umd.min.js';
let _chartJsReady = null;

function ensureChartJs() {
  if (_chartJsReady) {
    return _chartJsReady;
  }
  _chartJsReady = new Promise((resolve, reject) => {
    if (window.Chart) {
      resolve(window.Chart);
      return;
    }
    const script = document.createElement('script');
    script.src = CHART_JS_CDN;
    script.onload = () => resolve(window.Chart);
    script.onerror = (err) => reject(new Error(`Failed to load Chart.js from CDN: ${err}`));
    document.head.appendChild(script);
  });
  return _chartJsReady;
}

const VAADIN_SERIES_PALETTE = [
  '#2f7ed8',
  '#0d233a',
  '#8bbc21',
  '#910000',
  '#1aadce',
  '#492970',
  '#f28f43',
  '#77a1e5',
  '#c42525',
  '#a6c96a'
];

// ─── Shadow-DOM CSS ──────────────────────────────────────────────────────────
// :host must be display:block so that percentage heights on children resolve
// against the explicit width/height Vaadin places on the host element via
// inline style (e.g. style="width:600px;height:600px").
// Without this the container div has height:0 → canvas height:0 → invisible.
//
// max-width:100% ensures the host never overflows its layout parent.  When the
// viewport is narrower than any explicit pixel width (e.g. on mobile), the host
// shrinks to fit, which shrinks the inner div (width:100%), which triggers
// Chart.js's ResizeObserver so the canvas is re-drawn at the correct size.
// Without this, responsive:true in Chart.js has no effect on mobile because
// the container never changes size and the ResizeObserver never fires.
const SHADOW_STYLES = `
  :host { display: block; overflow: hidden; max-width: 100%; }
  div   { width: 100%; height: 100%; position: relative; overflow: hidden; }
`;

class HolonChartJs extends HTMLElement {
  constructor() {
    super();

    // ── instance state ──────────────────────────────────────────────────────
    this._chart = null;
    this._renderScheduled = false;

    // Data held as plain instance fields – NOT as HTML attributes.
    // Vaadin Flow's setProperty() calls the JS property setter which stores
    // the value here and schedules a microtask render (Lit-style batching).
    this._chartType = null;
    this._dataJson = null;
    this._optionsJson = null;

    // ── shadow DOM ──────────────────────────────────────────────────────────
    const root = this.attachShadow({ mode: 'open' });

    const style = document.createElement('style');
    style.textContent = SHADOW_STYLES;

    this._container = document.createElement('div');
    this._canvas = document.createElement('canvas');
    this._container.appendChild(this._canvas);

    root.appendChild(style);
    root.appendChild(this._container);
  }

  // ── lifecycle ──────────────────────────────────────────────────────────────

  connectedCallback() {
    // Handle the case where Vaadin set properties before the custom-element
    // class was registered (the "upgrade" timing problem).
    this._upgradePredefinedProperties();
    // Schedule a render; if properties are already present (upgrade case or
    // re-attach) the chart is built in the next microtask.
    this._scheduleRender();
  }

  disconnectedCallback() {
    this._destroyChart();
  }

  // ── property accessors ────────────────────────────────────────────────────
  // Vaadin Flow calls `element.setProperty("chartType", …)` which invokes
  // these setters.  We store values locally and schedule a batched render so
  // that all three properties are applied before the chart is (re-)created.

  set chartType(value) {
    this._chartType = value;
    this._scheduleRender();
  }

  get chartType() {
    return this._chartType;
  }

  set dataJson(value) {
    this._dataJson = value;
    this._scheduleRender();
  }

  get dataJson() {
    return this._dataJson;
  }

  set optionsJson(value) {
    this._optionsJson = (value == null || value === '') ? null : value;
    this._scheduleRender();
  }

  get optionsJson() {
    return this._optionsJson;
  }

  // ── render scheduling (microtask batching) ────────────────────────────────

  /**
   * Schedule a single refreshChart() call at end of the current microtask
   * queue.  Multiple property writes in the same synchronous JS task therefore
   * trigger only ONE chart build.
   */
  _scheduleRender() {
    if (!this.isConnected || this._renderScheduled) {
      return;
    }
    this._renderScheduled = true;
    Promise.resolve().then(() => {
      if (this._renderScheduled) {
        this._renderScheduled = false;
        this.refreshChart();
      }
    });
  }

  // ── property-upgrade helper ─────────────────────────────────────────��─────

  _upgradePredefinedProperties() {
    this._upgradeProperty('chartType');
    this._upgradeProperty('dataJson');
    this._upgradeProperty('optionsJson');
  }

  /**
   * If Vaadin set a property as an own property on the element before the
   * custom-element class was registered, that own property shadows the
   * prototype setter.  Delete + re-assign funnels the value through the setter.
   */
  _upgradeProperty(propertyName) {
    if (!Object.prototype.hasOwnProperty.call(this, propertyName)) {
      return;
    }
    const value = this[propertyName];
    delete this[propertyName];
    this[propertyName] = value;
  }

  // ── public render API (also called via callJsFunction from Java) ──────────

  async refreshChart() {
    // Cancel any microtask render that is already queued – this call IS the
    // render (prevents a double-render when callJsFunction + microtask race).
    this._renderScheduled = false;

    const chartType = this._chartType;
    const dataJson = this._dataJson;
    const optionsJson = this._optionsJson;

    if (!chartType || !dataJson) {
      return;
    }

    let Chart;
    try {
      Chart = await ensureChartJs();
    } catch (err) {
      // eslint-disable-next-line no-console
      console.error('holon-chartjs: could not load Chart.js from CDN', err);
      return;
    }

    const parsedData = this._tryParseJson(dataJson, 'data');
    if (parsedData === null) {
      return;
    }

    const parsedOptions = optionsJson ? this._tryParseJson(optionsJson, 'options') : undefined;
    if (optionsJson && parsedOptions === null) {
      return;
    }

    this._applyDefaultPalette(parsedData, chartType);
    const effectiveOptions = this._mergeVaadinLikeDefaults(parsedOptions || {});

    this._destroyChart();

    const context = this._canvas.getContext('2d');
    this._chart = new Chart(context, {
      type: chartType,
      data: parsedData,
      options: effectiveOptions
    });
  }

  // ── palette / defaults helpers (unchanged) ────────────────────────────────

  _applyDefaultPalette(data, chartType) {
    if (!data || !Array.isArray(data.datasets)) {
      return;
    }
    const pieLike = chartType === 'pie' || chartType === 'doughnut' || chartType === 'polarArea';
    data.datasets.forEach((dataset, datasetIndex) => {
      if (!dataset) {
        return;
      }
      const baseColor = VAADIN_SERIES_PALETTE[datasetIndex % VAADIN_SERIES_PALETTE.length];
      const points = Array.isArray(dataset.data) && dataset.data.length > 0 ? dataset.data.length : 1;
      if (dataset.backgroundColor === undefined) {
        dataset.backgroundColor = pieLike
          ? this._paletteSeries(points, 0.65)
          : this._toRgba(baseColor, 0.65);
      }
      if (dataset.borderColor === undefined) {
        dataset.borderColor = pieLike
          ? this._paletteSeries(points, 1)
          : baseColor;
      }
      if (dataset.borderWidth === undefined) {
        dataset.borderWidth = 1;
      }
    });
  }

  _mergeVaadinLikeDefaults(options) {
    const target = options || {};
    this._setDefaultNested(target, ['responsive'], true);
    this._setDefaultNested(target, ['maintainAspectRatio'], false);
    this._setDefaultNested(target, ['plugins', 'legend', 'display'], true);
    this._setDefaultNested(target, ['plugins', 'tooltip', 'mode'], 'index');
    this._setDefaultNested(target, ['plugins', 'tooltip', 'intersect'], false);
    this._setDefaultNested(target, ['interaction', 'mode'], 'index');
    this._setDefaultNested(target, ['interaction', 'intersect'], false);
    return target;
  }

  _setDefaultNested(target, path, value) {
    let current = target;
    for (let i = 0; i < path.length - 1; i += 1) {
      const segment = path[i];
      if (typeof current[segment] !== 'object' || current[segment] === null || Array.isArray(current[segment])) {
        current[segment] = {};
      }
      current = current[segment];
    }
    const leaf = path[path.length - 1];
    if (current[leaf] === undefined) {
      current[leaf] = value;
    }
  }

  _paletteSeries(size, alpha) {
    const result = [];
    for (let i = 0; i < size; i += 1) {
      const color = VAADIN_SERIES_PALETTE[i % VAADIN_SERIES_PALETTE.length];
      result.push(this._toRgba(color, alpha));
    }
    return result;
  }

  _toRgba(hex, alpha) {
    const normalized = (hex || '').replace('#', '');
    const expanded = normalized.length === 3
      ? normalized.split('').map((segment) => segment + segment).join('')
      : normalized;
    if (!/^[\da-fA-F]{6}$/.test(expanded)) {
      return hex;
    }
    const red = Number.parseInt(expanded.substring(0, 2), 16);
    const green = Number.parseInt(expanded.substring(2, 4), 16);
    const blue = Number.parseInt(expanded.substring(4, 6), 16);
    return `rgba(${red}, ${green}, ${blue}, ${alpha})`;
  }

  _tryParseJson(value, name) {
    try {
      return JSON.parse(value);
    } catch (error) {
      // eslint-disable-next-line no-console
      console.error(`Invalid Chart.js ${name} JSON for holon-chartjs`, error);
      return null;
    }
  }

  _destroyChart() {
    if (this._chart) {
      this._chart.destroy();
      this._chart = null;
    }
  }
}

if (!customElements.get('holon-chartjs')) {
  customElements.define('holon-chartjs', HolonChartJs);
}

