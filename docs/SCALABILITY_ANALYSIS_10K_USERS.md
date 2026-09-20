# Core module — scalability analysis for a 10k-concurrent-user SaaS

Scope: `core/src/main/java` (~1,250 classes). The question asked was two-fold — where is there room for
improvement in the `core` components, and how do those components behave when ~10,000 users are on
them at once.

## Why Vaadin changes the scaling maths

Vaadin Flow keeps the entire component tree **server-side, in the `VaadinSession`**. That gives three
properties that dominate everything else in this analysis:

1. **Per-session heap is the binding constraint.** Anything allocated per component is multiplied by
   10,000. Waste that is invisible at 10 users is decisive at 10,000.
2. **Sessions get serialized.** Once a single node can no longer hold every session, the deployment
   turns on session replication or disk passivation. At that point every object reachable from a
   component must be `Serializable`, or the session simply fails to persist.
3. **Static state is shared by every tenant.** A field that is `static` is shared across all 10,000
   users. If it caches anything locale-, tenant-, or user-specific, it is a correctness bug, not just
   a performance one.

The findings below are grouped by which of those three properties they violate.

---

## Findings and fixes

### 1. `FormSection` minted duplicate DOM ids under concurrency — CRITICAL

```java
private static int idCounter = 0;
...
String titleId = "form-section-" + (++idCounter) + "-title";
```

`++idCounter` is an unsynchronized read-modify-write on shared static state, executed by every request
thread that builds a form. Lost updates hand the same id to several sections, and since that id is
wired into `aria-labelledby`, the form layout ends up pointing at the wrong heading — a real
accessibility defect, silently, only under load.

This is not theoretical. The regression test added with this change measures it directly: with the old
code, 16 threads building 250 sections each produced **3,499 distinct ids out of 4,000** — 501
collisions.

**Fix:** an `AtomicLong`, seeded from `SecureRandom` at class-load so that ids minted on different
cluster nodes cannot collide either after a session fails over. Rendered base-36 to keep the id short.

### 2. Session state was not serializable — CRITICAL

`MasterDetailLayout` is a `Div`, so it lives in the `VaadinSession`. It held its handlers as raw
`java.util.function` types:

```java
private final List<Consumer<T>> syncDispatchers = new ArrayList<>();
private Supplier<Optional<T>> initialItemSupplier;
private Function<T, String> accentColorProvider;
```

A `java.util.function.Consumer` lambda is **not** `Serializable`. Neither were the two collaborators
the layout retains, `SelectionHighlighter` and `UrlSelectionSync`, which are plain final classes
holding more raw `Function` fields. Any deployment that enables replication or passivation — which is
exactly what a 10k-user deployment does when it scales past one node — would fail to write out every
session containing a master-detail view.

**Fix:** converted the *retained* handlers to Vaadin's `SerializableConsumer` / `SerializableSupplier`
/ `SerializableFunction`, and made `SelectionHighlighter` and `UrlSelectionSync` `Serializable`. The
signature change propagates through `MasterDetailConfigurator` and `AbstractMasterDetailConfigurator`,
including the intermediate `List<Consumer<Object>>` dispatcher lists — those matter because a
serializable lambda that *captures* a non-serializable one is still not serializable.

Deliberately left alone: the `Consumer<MasterOptions<T>>`-style **configuration** callbacks. Those run
once at build time and are never stored, so they do not need to be serializable and constraining them
would be API churn for no benefit.

### 3. `ResponsiveDiv` funnelled every user through one global lock — HIGH

```java
private static final Map<Div, Map> SLOT_REGISTRY =
        Collections.synchronizedMap(new WeakHashMap<>());
```

Two process-wide registries, keyed by the `Div` being built, touched on every `build()` and every first
attach. Two costs, both of which only appear under load:

- **One monitor for all users.** Thousands of concurrent users rendering responsive containers all
  serialize through a single mutex, for work that is entirely per-component.
- **An unbounded leak for containers that are never attached.** Entries were removed only by the attach
  listener, so a `build()` whose result is discarded before attach left its suppliers pinned — and the
  weak key could not help at all while the div was still reachable from the session.

The registries existed for one reason: to keep non-serializable `Supplier` lambdas *out* of the session
(see the file's original comment). Note the side effect — slot state silently vanished across
deserialization, which the old code documented as a "graceful no-op".

**Fix:** declaring the slots as `SerializableSupplier`/`SerializableConsumer` removes the original
reason entirely. The suppliers are now captured directly by the attach listener (itself serializable)
and released after first attach. No shared lock, no cross-component map, and slot state that actually
survives replication instead of disappearing. `ModeSwitchBuilder` got the same treatment, since
`WindowSizeTracker.enable(div, this::applyMode)` pins that builder for the div's whole lifetime.

### 4. Formatting ignored the user's locale — HIGH (correctness)

Three separate places formatted numbers and dates with a locale that had nothing to do with the user:

| Location | Problem |
| --- | --- |
| `UIUtils` | `ThreadLocal<DecimalFormat>` pinned to `Locale.US`; `ThreadLocal<DateTimeFormatter>`; bare `NumberFormat.getIntegerInstance()` / `getCurrencyInstance()` / `getInstance()` |
| `AbstractItemListing.numberRenderer` | `Locale.getDefault()` |
| `LineItemGrid.fmt` | hardcoded `Locale.US` |

In a multi-tenant SaaS, `Locale.getDefault()` is an arbitrary *server* property. Every tenant got the
server's grouping and decimal conventions, and in `UIUtils` every tenant got US conventions for
monetary amounts.

The `ThreadLocal` was wrong in a second, subtler way: it pins a formatter for the lifetime of a *pooled
request thread*, so the instance both persists indefinitely and drifts across the different users that
thread serves over time. And `DateTimeFormatter` is immutable and thread-safe, so wrapping it in a
`ThreadLocal` bought nothing at all.

**Fix:** a `currentLocale()` helper resolving `UI.getCurrent().getLocale()` then
`LocalizationProvider.getCurrentLocale()`, falling back to the JVM default only outside a UI. Formatters
are built per call — `DecimalFormat` is not thread-safe, so a correct implementation cannot cache
instances anyway, and the allocation is negligible next to the render it feeds.

### 5. Bean annotation reflection repeated per user — HIGH

`EntityFormPanel.applyAutoRequiredFromAnnotations` walked the full class hierarchy with
`getDeclaredFields()`, then `getAnnotations()` per field, then a reflective
`annotationType().getMethod("message").invoke(ann)` per annotated field — on **every form build**, i.e.
once per user opening the form. The result is a pure function of the bean `Class`.

**Fix:** extracted the derived metadata into a `RequiredFieldMeta` record cached in a
`ConcurrentHashMap<Class<?>, List<RequiredFieldMeta>>`. Growth is bounded by the number of bean classes
the application declares — not by users or sessions — and the keys are `Class` objects already held
alive by their own class loader, so this is a bounded cache rather than a leak. The per-call
`explicitFields` filter stays outside the cache, since it varies per builder.

---

## Verified as *not* problems

Worth recording so they are not "fixed" again:

- `DefaultEnumCaptionRegistry` — already a `ConcurrentHashMap`, bounded by enum constants.
- `BeanToMap.FIELD_CACHE`, `DefaultStringToTimeConverter.CACHE` — keyed by `Class` / `Locale`, bounded.
- `ViewMode` / `Breakpoint` static maps — built once at class-init, read-only.
- `ViewModeContext` — correctly per-session via `VaadinSessionScope`.
- `UIUtils.FIXED_COLUMNS_*` / `FLEXIBLE_COLUMNS` — `static final` but immutable `Map.of`/`List.of`.
- The Holon listing builders expose lazy `setItems(FetchCallback…)` / `BackEndDataProvider` paths; the
  in-memory `ListDataProvider` overloads are opt-in convenience, not the default.

## Known remaining items (not addressed here)

- `ItemLineEditor` uses `setAllRowsVisible(true)` with an in-memory `setItems(rows)`. Fine for short
  editable line-item lists, risky if the row count is ever unbounded.
- Some builder inner classes in `EntityFormPanel` and `DynamicFilterPanel` eagerly allocate collections
  for optional features that most forms never use. Low individual cost, but multiplied by session count.
- A few `addAttachListener` call sites mutate `event.getUI().getPage()` (stylesheet/JS registration)
  without guarding against re-attach.

## Verification

- `mvn -o compile` — all 13 modules build, including `demo`, the main consumer of the changed
  master-detail API.
- `mvn -o -pl core test` — new `TestConcurrencyAndSerialization` passes (3 tests): concurrent
  `FormSection` id uniqueness, and serialization round-trips of `ResponsiveDiv` with lazy slots and of
  `MasterDetailLayout` with handlers attached.
- The id-uniqueness test was confirmed to *fail* against the original implementation (3,499/4,000), so
  it is a genuine regression guard rather than a tautology.

Four test classes (`TestAppBar`, `TestGridToolbarBuilder`, `TestListingBundleGridHeader`,
`TestWindowSizeTracker`) were failing when this work started — pre-existing breakage from unrelated
in-flight refactors. All four have now been fixed:

- **`TestWindowSizeTracker`** — `WindowSizeTracker.track()` now reads
  `ScreenOrientation.orientationSignal(ui)`, which dereferences `UI.getInternals()`; a bare
  `mock(UI.class)` returned `null`. The test now stubs `UIInternals` and extends `AbstractSessionTest`,
  because `Signal.effect(Component, …)` only runs inside a real Vaadin session context. Two
  orientation-aware cases were added. 9/9.
- **`TestAppBar`** — the old assertions counted eager children on a never-attached `AppBar`, but
  `AppBar` now builds its middle/end slots through `ResponsiveDiv.slotOnce(...)`, so they materialise
  on attach and only on desktop (on mobile the end slot collapses into a `Popover`). Rewritten against
  the real responsive contract, using a new test-only `PageTestSupport` shim to set a viewport. 11/11.
- **`TestGridToolbarBuilder`** — the "Clear selection" control is an icon button with no text, so
  looking it up by the label `"Clear"` could never match. The tests now use the public
  `GridToolbar.getClearButton()` accessor. 10/10.
- **`TestListingBundleGridHeader`** — this one was a genuine production bug, not a stale test.
  `ListingBundle.setGridHeader(Component)` added the header as a child but never assigned
  `headerTitle`, so `header()` always returned `null` on the bean-listing path. In addition, the
  bean-listing builder had no way to express context actions: `gridHeader(Component)` overwrote the
  title instead of wiring the component into the toolbar, diverging from `PropertyListingBundleBuilder`,
  which already treated `gridHeader(Component...)` as context actions. Fixed by recording
  `headerTitle` in `setGridHeader`, widening `ListingBundleConfigurer.gridHeader(Component)` to
  `gridHeader(Component...)` with context-action semantics, and routing the title through the
  `ListingBundle` constructor so both builder paths produce the same plain heading. 3/3.

Final state: `mvn -o test` — all 13 modules BUILD SUCCESS, `core` at 2,713 tests with 0 failures and
0 errors.

### Session-serializable `Input` adapters

The `Input` adapter chain retained plain `java.util.function` handlers, so every lambda bound to them
was compiled as a non-serializable hidden class. The demo's `SerializationDiagnosticTest` reported 20
such lambdas reachable from a view — `focusOperation`, `hasEnabledSupplier`, and the
required/label/placeholder `CallbackPropertyHandler` getters and setters. Because these are *retained*
by the `Input` instance, they live in the `VaadinSession` and would break session replication or
passivation, which is exactly what a 10k-user deployment relies on to spread load and survive node
recycling.

A lambda is serializable only if its **target type** is, so the fix had to change the declared types
rather than add `Serializable` at the storage site:

- `Input.PropertyHandler` now extends `Serializable` and declares two serializable functional
  interfaces, `PropertyGetter` and `PropertySetter`. `PropertyHandler.create(...)` and
  `CallbackPropertyHandler` take those types instead of raw `BiFunction`/`TriConsumer` (holon-core's
  `TriConsumer` is not serializable and is outside this repository, hence the local sub-interface).
- `InputAdapterBuilder`, `DefaultHasValueInputBuilder`, and `InputAdapter` use
  `SerializableFunction`/`SerializableConsumer` for every retained operation: the empty-value,
  is-empty, value, size, style, enabled, value-change-mode, validation, and invalid-change-notifier
  suppliers, plus the focus operation.
- The adapter registry (`InputAdaptersContainer`, `DefaultInputAdaptersContainer`, and the
  `withAdapter`/`withAdapters` methods across all input builders) now uses
  `SerializableFunction<Input<T>, A>`.
- `StringInputValueSupplier` and `StringInputIsEmptySupplier` implement `SerializableFunction`; they
  are instantiated and retained as the value/is-empty suppliers of every string input.

All call sites are lambdas, so this is source-compatible for normal builder usage.

`SerializationDiagnosticTest` was also changed from print-only to assertive: it now collects findings
and fails with the offending field paths, so a reintroduced non-serializable handler breaks the build
instead of scrolling past in the log. After the fix it reports 0 findings.
