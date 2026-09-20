# Per-user time zones with Holon Platform + Vaadin Flow

How to render `java.time.Instant` (and any other absolute point on the time-line) in **each user's own
time zone** in a multi-tenant / SaaS Vaadin application.

> Requires `holon-core` **12.0.0+** (time-zone aware `LocalizationContext`) and
> `holon-vaadin-flow` **12.0.0+**.

---

## 1. Why you need this

`Instant`, `OffsetDateTime` and `java.util.Date` are *absolute* points on the time-line. They have **no
calendar fields of their own** — a month, a day or a wall-clock time only exists once you pick a time
zone.

Two consequences:

1. **Formatting an `Instant` fails without a zone.** Before 12.0.0 a Holon grid column bound to an
   `Instant` property blew up with:

   ```
   java.time.temporal.UnsupportedTemporalTypeException: Unsupported field: MonthOfYear
       at java.base/java.time.Instant.getLong(Instant.java:603)
       ...
       at com.holonplatform.core.internal.presentation.DefaultStringValuePresenter.convertTemporal(...)
       at com.holonplatform.vaadin.flow.internal.components.DefaultBeanListing.lambda$generateDefaultGridColumn$0(...)
   ```

2. **Using the server zone is wrong for SaaS.** If your service runs in `UTC` on AWS `eu-west-1`, an
   order created at `2026-03-18T12:00:00Z` must be shown as:

   | User        | Zone               | Rendered                |
   |-------------|--------------------|-------------------------|
   | Mumbai      | `Asia/Kolkata`     | `18/03/2026 17:30` (IST) |
   | New York    | `America/New_York` | `03/18/2026 08:00` (EDT) |
   | London      | `Europe/London`    | `18/03/2026 12:00` (GMT) |

   Note New York is `-04:00` in March because of DST — which is exactly why you must store an **IANA
   zone id** (`America/New_York`) and *not* a fixed offset (`-05:00`).

Holon 12.0.0 adds a time zone to the localization API and normalizes instant-like temporals
(`Instant` → `ZonedDateTime`) before formatting them.

---

## 2. The API

### `Localization` — the per-request/per-user localization

```java
Localization localization = Localization.builder(Locale.of("en", "IN"))
        .zone(ZoneId.of("Asia/Kolkata"))          // <-- new in 12.0.0
        .build();
```

### `LocalizationContext` — the (session scoped) context

```java
LocalizationContext ctx = LocalizationContext.builder()
        .withMessageProvider(MessageProvider.fromProperties("messages").build())
        .withDefaultZone(ZoneId.of("UTC"))        // <-- new in 12.0.0, optional fallback
        .build();

ctx.localize(localization);                        // locale + zone applied together

Optional<ZoneId> zone = ctx.getZone();             // <-- new in 12.0.0
```

### Resolution order

```
Localization.getZone()  (walking the Localization parent chain)
      ↓ (absent)
LocalizationContext default zone  (Builder.withDefaultZone)
      ↓ (absent)
ZoneId.systemDefault()            (server zone — last resort)
```

Everything is `default`-implemented on the interfaces, so **existing custom implementations of
`Localization` / `LocalizationContext` keep compiling**.

---

## 3. Step-by-step: wire the browser time zone into the session

### Step 1 — model the timestamp as an `Instant`

Store absolute timestamps as `Instant` (SQL `TIMESTAMP WITH TIME ZONE` / `timestamptz`). Never store a
`LocalDateTime` "in server time" — you lose the information needed to re-render it per user.

```java
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;          // absolute point in time

    // getters / setters ...
}
```

### Step 2 — add the session localization initializer

Create a Vaadin `VaadinServiceInitListener` Spring bean. It does two things:

* **on session init** — creates a `LocalizationContext` and binds it to the Vaadin session, so Holon's
  `VaadinSessionScope` publishes it as the *current* context resource (every Holon component then picks
  it up automatically through `LocalizationProvider`);
* **on UI init** — asks the browser for its zone and re-localizes the context with it.

```java
package com.example.app;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.holonplatform.core.i18n.Localization;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.i18n.MessageProvider;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.ExtendedClientDetails;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;

@Component
public class SessionLocalizationInitializer implements VaadinServiceInitListener {

    private static final long serialVersionUID = 1L;

    /** Session attribute holding the resolved browser time zone. */
    private static final String ZONE_ATTRIBUTE = SessionLocalizationInitializer.class.getName() + ".zone";

    /** Resource bundle base name (same one used by your I18NProvider, if any). */
    private static final String BUNDLE_BASE = "messages";

    @Override
    public void serviceInit(ServiceInitEvent event) {
        final VaadinService service = event.getSource();
        service.addSessionInitListener(e -> localize(e.getSession(), e.getSession().getLocale(), null));
        service.addUIInitListener(e -> initBrowserTimeZone(e.getUI()));
    }

    private static void initBrowserTimeZone(UI ui) {
        final VaadinSession session = ui.getSession();
        final ZoneId known = (session != null) ? (ZoneId) session.getAttribute(ZONE_ATTRIBUTE) : null;
        if (known != null) {
            // already resolved for this session: apply it *before* the view is rendered
            localize(session, ui.getLocale(), known);
            return;
        }
        // the browser zone requires a client round trip: the first view renders before it is available
        ui.getPage().retrieveExtendedClientDetails(details -> {
            final ZoneId zone = resolveZone(details);
            final VaadinSession current = ui.getSession();
            if (current != null) {
                current.setAttribute(ZONE_ATTRIBUTE, zone);
                localize(current, ui.getLocale(), zone);
            }
            // re-render the already displayed route so temporal values use the browser zone
            ui.refreshCurrentRoute(true);
        });
    }

    private static void localize(VaadinSession session, Locale locale, ZoneId zone) {
        if (session == null) {
            return;
        }
        LocalizationContext localizationContext = session.getAttribute(LocalizationContext.class);
        if (localizationContext == null) {
            localizationContext = LocalizationContext.builder()
                    .withMessageProvider(MessageProvider.fromProperties(BUNDLE_BASE).build())
                    .build();
            session.setAttribute(LocalizationContext.class, localizationContext);
        }
        final Localization.Builder localization =
                Localization.builder((locale != null) ? locale : Locale.getDefault());
        if (zone != null) {
            localization.zone(zone);
        }
        localizationContext.localize(localization.build());
    }

    /** Prefer the IANA zone id (DST aware) over the raw offset. */
    private static ZoneId resolveZone(ExtendedClientDetails details) {
        final String zoneId = details.getTimeZoneId();
        if (zoneId != null && !zoneId.trim().isEmpty()) {
            try {
                return ZoneId.of(zoneId.trim());
            } catch (DateTimeException e) {
                // unknown zone id: fall back to the raw offset below
            }
        }
        return ZoneOffset.ofTotalSeconds(details.getTimezoneOffset() / 1000);
    }
}
```

**Why `refreshCurrentRoute(true)`?** `retrieveExtendedClientDetails` needs a client round trip, so the
first view of a brand-new session is already on screen when the zone arrives. Refreshing re-renders it
with the correct zone. The zone is cached in the session, so every subsequent UI (navigation, new tab,
reload) applies it *synchronously* in the UI init listener — there is no refresh loop.

### Step 3 — that's it, components just work

No change is needed in your views. `PropertyListing`, `BeanListing`, `PropertyInputForm`,
`ViewComponent`, `EntityFormPanel`… all format through `Property.present(...)` →
`LocalizationContext`, which now applies the session zone:

```java
BeanListing<Product> listing = Components.listing.items(Product.class)
        .dataSource(productService::findAll)
        .visibleColumns("name", "category", "price", "active", "createdDate")
        .columnHeader("createdDate", "Created")
        .build();
```

For an explicit format:

```java
String created = LocalizationContext.require()
        .format(product.getCreatedDate(), TemporalFormat.MEDIUM, TemporalFormat.SHORT);
```

---

## 4. Alternative: a Spring session-scoped `LocalizationContext` bean

If you prefer Spring scopes, declare the bean as session scoped and `holon-vaadin-flow-spring-boot`
auto-registers `LocalizationContextInitializer`, which localizes it with the session `Locale`:

```java
@Bean
@VaadinSessionScope
public LocalizationContext localizationContext() {
    return LocalizationContext.builder()
            .withMessageProvider(MessageProvider.fromProperties("messages").build())
            .withDefaultZone(ZoneId.of("UTC"))
            .build();
}
```

You still need the UI init listener of *Step 2* to push the **browser zone** into it — Spring only knows
the `Locale`.

> Choose **one** approach. The session-attribute approach of Step 2 avoids scoped-proxy issues and is
> what the `demo` module uses.

---

## 5. Per-tenant / per-user-profile zone instead of the browser zone

In many SaaS products the zone is a *user preference* stored in the profile, and the browser zone is only
the initial default. Just replace the source of the `ZoneId`:

```java
ZoneId zone = currentUser.getPreferredZone()          // profile setting wins
        .orElseGet(() -> browserZone(details));       // browser as fallback

LocalizationContext.require().localize(
        Localization.builder(currentUser.getLocale()).zone(zone).build());
```

Whenever the user changes the preference, call `localize(...)` again and
`UI.getCurrent().refreshCurrentRoute(true)` — registered
`LocalizationChangeListener`s fire too, so localized components re-render.

---

## 6. Rules of thumb

| Do | Don't |
|----|-------|
| Store timestamps as `Instant` / `timestamptz` | Store `LocalDateTime` "in server time" |
| Persist the user's **IANA zone id** (`Asia/Kolkata`) | Persist a fixed offset (`+05:30`) — breaks on DST |
| Run application servers and databases in **UTC** | Rely on `ZoneId.systemDefault()` for presentation |
| Use `LocalDate` for zone-less business dates (invoice date, birth date) | Convert business dates through a zone |
| Re-localize + refresh when the zone changes | Cache formatted strings across sessions |

---

## 7. Testing

`holon-core` ships a regression test showing the behaviour
(`com.holonplatform.core.test.TestI18n#testInstantZonedFormat`):

```java
// 2024-06-01T12:00:00Z
final Instant instant = ZonedDateTime.of(LocalDateTime.of(2024, Month.JUNE, 1, 12, 0, 0), ZoneOffset.UTC)
        .toInstant();

final LocalizationContext india = LocalizationContext.builder().build();
india.localize(Localization.builder(Locale.ITALY).zone(ZoneId.of("Asia/Kolkata"))
        .defaultDateTemporalFormat(TemporalFormat.SHORT)
        .defaultTimeTemporalFormat(TemporalFormat.SHORT)
        .build());

final LocalizationContext newYork = LocalizationContext.builder().build();
newYork.localize(Localization.builder(Locale.ITALY).zone(ZoneId.of("America/New_York"))
        .defaultDateTemporalFormat(TemporalFormat.SHORT)
        .defaultTimeTemporalFormat(TemporalFormat.SHORT)
        .build());

// same instant, different wall-clock time per user zone (New York is in DST: UTC-4)
assertEquals("01/06/24, 17:30", india.format(instant));
assertEquals("01/06/24, 08:00", newYork.format(instant));
```

For a Vaadin Browserless / TestBench test, set the zone up-front before building the view:

```java
VaadinSession.getCurrent().setAttribute(LocalizationContext.class, ctx);
ctx.localize(Localization.builder(Locale.US).zone(ZoneId.of("America/New_York")).build());
```

---

## 8. Troubleshooting

| Symptom | Cause / fix |
|---------|-------------|
| `UnsupportedTemporalTypeException: Unsupported field: MonthOfYear` | Running against `holon-core` < 12.0.0 — upgrade, or convert the `Instant` yourself with `instant.atZone(zone)` |
| Dates render in server zone | No `LocalizationContext` is *current*: make sure it is bound to the Vaadin session (Step 2) and that a `VaadinSession` exists when it is read |
| Zone applied only after a manual page reload | `ui.refreshCurrentRoute(true)` missing in the `retrieveExtendedClientDetails` callback |
| Translations disappeared after adding the context | A *localized* `LocalizationContext` takes precedence over the Vaadin `I18NProvider`: add the same bundle via `withMessageProvider(MessageProvider.fromProperties("messages").build())` |
| `details.getTimeZoneId()` is empty | Exotic/legacy browser — the code falls back to `ZoneOffset.ofTotalSeconds(details.getTimezoneOffset() / 1000)` (no DST awareness) |
