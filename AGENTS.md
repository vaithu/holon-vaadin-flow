# Holon Platform — Coding Rules for LLMs

> **Read this file before writing any Java code that touches Vaadin, Spring, or data access in this project. These rules are non-negotiable.**

This project runs on the **Holon Platform** (https://holon-platform.com). Holon is a Java ecosystem built *on top of* Vaadin, Spring and JPA that adds a Property model, a Datastore API, an Auth API, a Navigator API, and a fluent builder layer over Vaadin Flow components. **In this project we always go through Holon — never around it.**

---

## 1. The single most important rule

> **Use Holon Platform modules. Do not use raw Vaadin core components, raw Spring Security boilerplate, raw JPA EntityManager, or raw BeanPropertyRetriever-style code when a Holon equivalent exists.**

If you catch yourself writing `new com.vaadin.flow.component.button.Button(...)` for ordinary UI work, or `new com.vaadin.flow.component.textfield.TextField()`, or assembling a `SecurityFilterChain` from scratch, or hand-rolling an `EntityManagerFactory`, **stop and use the Holon builder/equivalent instead**.

You may only drop down to a raw Vaadin/Spring/JPA API when:
1. The user explicitly asks for it, OR
2. Holon genuinely has no abstraction for that one concern (rare — ask first).

---

## 2. Scope — when these rules apply

Apply these rules whenever the task involves **any** of:
- Building a Vaadin-based UI
- Wiring up authentication / authorization / login flows
- Defining or querying persistent data
- Navigating between views / pages
- Configuring a Spring Boot application in this repo
- Adding or modifying a Maven dependency that has a Holon equivalent

If the task is unrelated (e.g. a CLI util, a test harness with no UI, an unrelated microservice), you may ignore this file.

---

## 3. Module map — pick the right artifact

Group ids and artifact ids below are exact. Versions shown are the **latest stable releases in the local Maven repo as of 2026-07-10**; always cross-check against the local Maven repo (see §10) and prefer the highest version already cached locally.

**Current baseline versions:**
- `holon-core` modules: **10.0.0** (`com.holon-platform.core`)
- `holon-vaadin-flow` modules: **10.0.0** (`com.holon-platform.vaadin`) — dev snapshot is `10.0.3-SNAPSHOT`
- `holon-datastore-jdbc` / `holon-datastore-jpa`: **10.0.0**
- Vaadin: **25.2.1** · Spring Boot: **4.1.0** · Java: **25**

### 3.1 Core (`com.holon-platform.core`)

| Concern | Artifact id |
|---|---|
| Property model, PropertySet, PropertyBox, Validator, Context, converters | `holon-core` |
| HTTP / REST client (`RestClient`, `HttpRequest`, `HttpResponse`) | `holon-http` |
| Async HTTP | `holon-async-http` |
| Async Datastore | `holon-async-datastore` |
| Auth API (Realm, Account, Authenticator, Authorizer, AuthContext) | `holon-auth` |
| JWT auth | `holon-auth-jwt` |
| Spring integration for core | `holon-spring` |
| Spring Security integration | `holon-spring-security` |
| Spring Boot auto-config | `holon-spring-boot` |
| Base Spring Boot starter | `holon-starter` |
| Spring Security starter | `holon-starter-security` |
| Test starter | `holon-starter-test` |

Core BOM (`com.holon-platform.core:holon-bom:10.0.0` — covers all core artifacts above):
```xml
<dependency>
  <groupId>com.holon-platform.core</groupId>
  <artifactId>holon-bom</artifactId>
  <version>10.0.0</version>
  <type>pom</type>
  <scope>import</scope>
</dependency>
```

Platform BOM (adds Spring / Spring Boot / Spring Security version management on top of core):
```xml
<dependency>
  <groupId>com.holon-platform.core</groupId>
  <artifactId>holon-bom-platform</artifactId>
  <version>10.0.0</version>
  <type>pom</type>
  <scope>import</scope>
</dependency>
```

### 3.2 Vaadin Flow (`com.holon-platform.vaadin`)

| Concern | Artifact id |
|---|---|
| Holon + Vaadin Flow integration, Components API, input/listing builders | `holon-vaadin-flow` |
| `Navigator` API (`@View`, `@QueryParameter`, `Navigator.get().navigateTo(...)`) | `holon-vaadin-flow-navigator` |
| Spring integration for Vaadin Flow | `holon-vaadin-flow-spring` |
| Spring Boot auto-config + servlet container | `holon-vaadin-flow-spring-boot` |
| Spring Boot starter (recommended for new apps) | `holon-starter-vaadin-flow` |
| Chart.js charts (`ChartJs` fluent builder) | `holon-vaadin-flow-chartjs` |
| FullCalendar 6 (`VaadinCalendar` wrapper) | `holon-vaadin-flow-calendar` |
| Chat UI components | `holon-vaadin-flow-chat` |
| Customer domain components | `holon-vaadin-flow-customer` |
| Test utilities | `holon-vaadin-flow-test` |
| Vaadin Flow BOM | `holon-vaadin-flow-bom` |

Vaadin Flow BOM (covers all `holon-vaadin-flow-*` artifacts above):
```xml
<dependency>
  <groupId>com.holon-platform.vaadin</groupId>
  <artifactId>holon-vaadin-flow-bom</artifactId>
  <version>10.0.0</version>
  <type>pom</type>
  <scope>import</scope>
</dependency>
```

### 3.3 Datastore backends (`com.holon-platform.jdbc` / `com.holon-platform.jpa`)

| Concern | Artifact id | Group id |
|---|---|---|
| JDBC Datastore, SQL filters, transactions | `holon-datastore-jdbc` | `com.holon-platform.jdbc` |
| JDBC starter with HikariCP | `holon-starter-jdbc-datastore-hikaricp` | `com.holon-platform.jdbc` |
| JPA Datastore (`Datastore` over JPA) | `holon-datastore-jpa` | `com.holon-platform.jpa` |
| JPA Datastore Spring Boot auto-config | `holon-datastore-jpa-spring-boot` | `com.holon-platform.jpa` |
| JPA starter with Hibernate | `holon-starter-jpa-hibernate` | `com.holon-platform.jpa` |
| JPA starter with EclipseLink | `holon-starter-jpa-eclipselink` | `com.holon-platform.jpa` |
| QueryDSL over JPA Datastore | `holon-datastore-jpa-querydsl` | `com.holon-platform.jpa` |
| MongoDB Datastore | `holon-datastore-mongo` | `com.holon-platform.mongo` |

All JDBC/JPA modules are at version **10.0.0**.

### 3.4 SaaS / Multi-tenant (`com.holon-platform.saas`)

The tenant/SaaS framework modules have been extracted into a separate project (`holon-saas`). Use `com.holon-platform.saas:holon-saas-bom:1.0.0` for the SaaS BOM. Key modules:

| Concern | Artifact id |
|---|---|
| SaaS starter for Vaadin Flow | `holon-starter-vaadin-flow-saas` |
| Tenant core | `holon-vaadin-flow-tenant-core` |
| Tenant data | `holon-vaadin-flow-tenant-data` |
| Tenant security | `holon-vaadin-flow-tenant-security` |
| Tenant users | `holon-vaadin-flow-tenant-users` |
| Tenant admin | `holon-vaadin-flow-tenant-admin` |
| Tenant audit | `holon-vaadin-flow-tenant-audit` |
| Tenant billing | `holon-vaadin-flow-tenant-billing` |
| Tenant onboarding | `holon-vaadin-flow-tenant-onboarding` |
| Tenant settings | `holon-vaadin-flow-tenant-settings` |

### 3.5 Recommended BOM import stack for new apps

Import these three BOMs in order inside `<dependencyManagement>`:
```xml
<!-- 1. Spring Boot versions (Spring Framework, Spring Security, etc.) -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-dependencies</artifactId>
  <version>4.1.0</version>
  <type>pom</type>
  <scope>import</scope>
</dependency>
<!-- 2. Vaadin component versions -->
<dependency>
  <groupId>com.vaadin</groupId>
  <artifactId>vaadin-bom</artifactId>
  <version>25.2.1</version>
  <type>pom</type>
  <scope>import</scope>
</dependency>
<!-- 3. Holon core + vaadin modules -->
<dependency>
  <groupId>com.holon-platform.core</groupId>
  <artifactId>holon-bom</artifactId>
  <version>10.0.0</version>
  <type>pom</type>
  <scope>import</scope>
</dependency>
<dependency>
  <groupId>com.holon-platform.vaadin</groupId>
  <artifactId>holon-vaadin-flow-bom</artifactId>
  <version>10.0.0</version>
  <type>pom</type>
  <scope>import</scope>
</dependency>
```
With these imported you do not pin versions for any Holon or Vaadin artifact.

---

## 4. The import cheat-sheet

Use these package roots. Never invent your own — if a class you're about to write isn't covered below, grep the local Maven repo or ask the user.

```
com.holonplatform.core.property.*         // StringProperty, NumericProperty, TemporalProperty, PropertySet, PropertyBox
com.holonplatform.core.datastore.*        // Datastore, ConfigurableDatastore, DataTarget, QueryFilter
com.holonplatform.core.validator.*        // Validator, ValidationException
com.holonplatform.core.context.*          // Context, ContextScope
com.holonplatform.core.i18n.*             // LocalizationContext
com.holonplatform.core.auth.*             // AuthContext, Account, Authentication, Authorization
com.holonplatform.core.http.*             // RestClient, HttpRequest, HttpResponse

com.holonplatform.vaadin.flow.components.* // Components.* (the main fluent builder factory)
com.holonplatform.vaadin.flow.components.layout.*  // Holon layout builders
com.holonplatform.vaadin.flow.components.input.*   // Input builders
com.holonplatform.vaadin.flow.components.listing.* // Listing/Grid builders
com.holonplatform.vaadin.flow.components.PropertyRenderer, ValueChangeEvent
com.holonplatform.vaadin.flow.navigator.*  // Navigator, @View, @QueryParameter, ViewConfiguration
com.holonplatform.vaadin.flow.data.*       // DataProvider wiring to Datastore

com.holonplatform.spring.*                // @PropertyPath, SpringPropertyBoxDatastoreBinder, etc.
com.holonplatform.spring.security.*       // Holon Spring Security auto-config classes
```

---

## 5. Decision trees for common tasks

### 5.1 "I need a form for entity X"

```
Don't:   bind Vaadin TextField/ComboBox/DatePicker by hand
Do:      Components.input.form(Entity.PROPERTY_SET)
            .hide(Entity.ID)               // exclude auto-generated / read-only fields
            .bind(datastore, Entity.TARGET) // optional: bind to a Datastore PropertyBox
            .build();
         → use Components.input.* fluent builders for any one-off field
```

### 5.2 "I need a grid/listing of X"

```
Don't:   Vaadin Grid<PropertyBox> + DataProvider manually
Do:      Components.listing.properties(Entity.PROPERTY_SET)
            .dataSource(datastore, Entity.TARGET)   // Holon wires the DataProvider
            .fullSize()
            .selectionMode(SelectionMode.SINGLE)
            .build();
```

### 5.3 "I need to add a navigation between views"

```
Don't:   com.vaadin.flow.router.Route + Router.navigate(...)
Do:      @View("path")           on the view class
         @QueryParameter("id")   on a field for typed query params
         Navigator.get().navigateTo("path")
         Navigator.get().navigation(View.class).withQueryParameter("id", value).navigate()
```

### 5.4 "I need login / auth"

```
Don't:   hand-rolled SecurityFilterChain, UserDetailsService, DaoAuthenticationProvider
Do:      define an AccountProvider, build a Realm with Authenticator + Authorizer,
         expose AuthContext as a Spring bean (@SessionScope),
         use holon-starter-security for auto-config,
         use Account.authenticator(accountProvider) for credential validation,
         Credentials.builder().secret(...).hashAlgorithm(...).build() for password handling.
```

### 5.5 "I need to persist data"

```
Don't:   EntityManager + JPQL strings + manual transaction management
Do:      model entities as Holon Property sets (StringProperty/NumericProperty/...),
         call datastore.<T>builder(Target).filter(...).build(...).insert()/update()/delete(),
         let Holon's transaction support handle the boundary, or expose the JPA Datastore as
         a Spring bean via holon-datastore-jpa + holon-spring-boot starter.
```

### 5.6 "I need to do a REST call"

```
Don't:   RestTemplate / new HttpURLConnection / raw WebClient boilerplate
Do:      com.holonplatform.core.http.RestClient (or its async sibling in holon-async-http).
```

---

## 6. Don't / Do reference card

| ❌ Never (in this project) | ✅ Always (in this project) |
|---|---|
| `new Button("Save")` (raw Vaadin) | `Components.button().text("Save").onClick(...)` |
| `new TextField()`, `new EmailField()` for ordinary input | `Components.input.string(...)`, `Components.input.email(...)`, etc. |
| `Grid<MyEntity>`, `DataProvider.fromCallbacks` | `Components.listing.properties(MyEntity.PROPERTY_SET).dataSource(...)` |
| `new HorizontalLayout()`, `new FormLayout()` when a fluent builder fits | `Components.layout.horizontal().append(...)` / `Components.layout.form(...)` |
| `@Route("foo")` + `UI.navigate(...)` | `@View("foo")` + `Navigator.get().navigateTo("foo")` |
| `@RequestParam String id` on routes | `@QueryParameter("id")` on a typed field in the view |
| `SecurityFilterChain` + `UserDetailsService` from scratch | Holon `Realm` + `AccountProvider` + `AuthContext` bean (`holon-starter-security`) |
| `EntityManager` JPQL with strings | `Datastore` API with `DataTarget` + `QueryFilter` |
| `RestTemplate` / raw `URLConnection` | `com.holonplatform.core.http.RestClient` |
| Manual i18n `ResourceBundle.getBundle(...)` | `LocalizationContext` (`com.holonplatform.core.i18n`) wired into the UI |
| Manual `Validator` framework (Jakarta Bean Validation alone) | Holon `Validator` + `Property.validator(...)` chains, composable with Bean Validation |

---

## 7. Maven configuration template

Use this as the starting point for any new module in the project; replace starters only as the task requires.

```xml
<properties>
  <java.version>25</java.version>
  <maven.compiler.release>25</maven.compiler.release>
  <vaadin.version>25.2.1</vaadin.version>
  <spring-boot.version>4.1.0</spring-boot.version>
  <holon.core.version>10.0.0</holon.core.version>
  <holon.vaadin.version>10.0.0</holon.vaadin.version>
  <holon.jdbc.datastore.version>10.0.0</holon.jdbc.datastore.version>
  <holon.jpa.datastore.version>10.0.0</holon.jpa.datastore.version>
</properties>

<dependencyManagement>
  <dependencies>
    <!-- Spring Boot versions (Spring Framework, Security, etc.) -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-dependencies</artifactId>
      <version>${spring-boot.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
    <!-- Vaadin component versions -->
    <dependency>
      <groupId>com.vaadin</groupId>
      <artifactId>vaadin-bom</artifactId>
      <version>${vaadin.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
    <!-- Holon core modules -->
    <dependency>
      <groupId>com.holon-platform.core</groupId>
      <artifactId>holon-bom</artifactId>
      <version>${holon.core.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
    <!-- Holon Vaadin Flow modules -->
    <dependency>
      <groupId>com.holon-platform.vaadin</groupId>
      <artifactId>holon-vaadin-flow-bom</artifactId>
      <version>${holon.vaadin.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <!-- Core (Property/Datastore/Auth/etc.) — version comes from holon-bom -->
  <dependency>
    <groupId>com.holon-platform.core</groupId>
    <artifactId>holon-core</artifactId>
  </dependency>

  <!-- Vaadin Flow + components + navigator — versions come from holon-vaadin-flow-bom -->
  <dependency>
    <groupId>com.holon-platform.vaadin</groupId>
    <artifactId>holon-vaadin-flow</artifactId>
  </dependency>
  <dependency>
    <groupId>com.holon-platform.vaadin</groupId>
    <artifactId>holon-vaadin-flow-navigator</artifactId>
  </dependency>

  <!-- Spring Boot starter for Vaadin Flow (replaces raw vaadin-spring-boot starter) -->
  <dependency>
    <groupId>com.holon-platform.vaadin</groupId>
    <artifactId>holon-starter-vaadin-flow</artifactId>
  </dependency>

  <!-- Security (if auth needed) -->
  <dependency>
    <groupId>com.holon-platform.core</groupId>
    <artifactId>holon-starter-security</artifactId>
  </dependency>

  <!-- Datastore: pick exactly one of jdbc/jpa/mongo -->
  <!--
  <dependency>
    <groupId>com.holon-platform.jdbc</groupId>
    <artifactId>holon-starter-jdbc-datastore-hikaricp</artifactId>
    <version>${holon.jdbc.datastore.version}</version>
  </dependency>
  OR for JPA with Hibernate:
  <dependency>
    <groupId>com.holon-platform.jpa</groupId>
    <artifactId>holon-starter-jpa-hibernate</artifactId>
    <version>${holon.jpa.datastore.version}</version>
  </dependency>
  -->
</dependencies>
```

**Rules for the `pom.xml`:**
- Import `holon-bom` (core) and `holon-vaadin-flow-bom` (vaadin) — do **not** use the old combined `com.holon-platform:bom` (no 10.x release). See §3.5 for the complete import stack.
- Do NOT add `com.vaadin:vaadin-core` or any Vaadin `*-flow` component artifact (`vaadin-button-flow`, `vaadin-textfield-flow`, …) when the Holon side covers it. The Holon starter brings Vaadin Flow transitively with the integration already wired.
- Pin JDBC/JPA Datastore versions explicitly via properties (`${holon.jdbc.datastore.version}`, `${holon.jpa.datastore.version}`) — they are **not** part of `holon-bom`.
- One Datastore starter per app.

---

## 8. Java baseline

This project targets **Java 25** (`maven.compiler.release=25`). Use Java 25 language features freely (records, pattern matching, sealed classes, virtual threads, etc.). Do **not** pull in older Java syntax or libraries targeting Java 8. The minimum Holon Platform 10.x requires Java 17+; this project runs at 25.

---

## 9. Self-check before you respond

Before you send code to the user, scan your draft and answer these out loud (in your thinking):

1. Did I import `com.vaadin.flow.component.*` for a component Holon provides via `Components.*`? → **rewrite it**.
2. Did I write `@Route(...)` instead of `@View(...)`? → **rewrite it**.
3. Did I instantiate `Datastore` via `EntityManagerFactoryBuilder`? → rewrite using Holon's `Datastore` bean (auto-configured by the starter).
4. Did I hand-code a `SecurityFilterChain`? → rewrite using `Realm` + `AuthContext`.
5. Did I write a `Validator` from scratch? → use `Property.validator(...)` chains.
6. Is any Holon dependency declared without the correct BOM import (`holon-bom:10.0.0` + `holon-vaadin-flow-bom:10.0.0`)? → fix the `pom.xml`. See §3.5 and §7.
7. Did I forget `@QueryParameter` typed binding and use `@RequestParam` instead? → rewrite.
8. Did I name a class using Vaadin conventions instead of Holon ones (e.g. `MyView` annotated `@Route` vs `@View`)?
9. Did I leave any TODO like "use Holon here later"? → fix it now.
10. If I'm not sure a class exists in Holon, did I check the local Maven repo (§10) or the linked reference docs (§11) before answering?

**Performance (this app runs at ~10k concurrent users — see §14):**

11. Did I call `.findMany()` on a UI-bound `Datastore` query without `.limit(...)` / paging? → page it.
12. Did I add a `.filter(Property.eq(...))` without a backing DB index? → add the index.
13. Did I put `@SessionScope` on a bean that holds a big/unsized collection? → scope it tighter or move to request scope.
14. Did I block a Vaadin event handler on a slow I/O call (DB / HTTP / third-party)? → make it async (`CompletableFuture.supplyAsync(...)` + `UI.access(...)`), or use `holon-async-datastore` / `holon-async-http`.
15. Did I add a new endpoint / hot-path bean without a Micrometer counter or timer? → instrument it.
16. Did I touch the connection-pool, cache, or Vaadin push/prod-mode config without re-checking it? → re-check.
17. Is this change on a "hot path" (main listing, form submit, auth, any Datastore query)? → load-test before declaring done (k6 / Gatling at the target concurrency).

If any answer is "yes / I didn't check / I guessed", fix it before responding.

---

## 10. How to read the local Maven repo (the user said latest `holon-core` is already there)

When the user mentions a specific Holon version, or asks "what's available", inspect:

```
~/.m2/repository/com/holon-platform/core/holon-core/
~/.m2/repository/com/holon-platform/vaadin/holon-vaadin-flow/
~/.m2/repository/com/holon-platform/vaadin/holon-vaadin-flow-navigator/
~/.m2/repository/com/holon-platform/core/holon-spring-security/
~/.m2/repository/com/holon-platform/core/holon-auth/
```

Use `ls` or `find ~/.m2/repository/com/holon-platform -maxdepth 4 -type d | sort`. The highest numeric directory under each artifact is the version actually installed locally. Prefer that version in the `pom.xml` to avoid network calls. If the user told you a specific version, use that.

---

## 11. Reference docs (use them, don't guess)

If you are not 100% sure a class/method exists, check before writing:

- Core: https://docs.holon-platform.com/current/reference/holon-core.html
- Vaadin Flow module: https://docs.holon-platform.com/current/reference/holon-vaadin-flow.html
- Module index: https://holon-platform.com/modules/
- Tutorials: https://holon-platform.com/tutorials/
- Source (canonical Java types): https://github.com/holon-platform (per-module repos under the `holon-platform` org)
- The Bakery demo end-to-end (Property model → JPA Datastore → Vaadin UI → Spring Security Auth): https://holon-platform.com/blog/the-bakery-project-a-vaadin-flow-full-stack-demo-application/

When in doubt, browse the source on GitHub for the exact class signature; that's authoritative. Never invent a method name.

---

## 12. When to ask the user

Stop and ask (instead of guessing) when:
- Two Holon modules could fit and the trade-off matters (e.g. JDBC Datastore vs JPA Datastore — different dev model).
- The user asks for "real" Vaadin (the Holon abstractions get in their way) — confirm before bypassing Holon.
- The user gave no version info and the local Maven repo has multiple Holon versions cached — pick the newest or ask.
- A specific Holon class/API is not in your training data and you cannot verify it from the local jar or the docs in §11 — ask or read the source.

Don't ask when:
- The choice is obvious (e.g. "use `Components.input.form(...)` for a form").
- The version is pinned in the repo's existing `pom.xml` — match it.
- The Holon-side solution is the only one in this file.

---

## 13. Performance — designed for 10k concurrent users

This app runs at **~10,000 concurrent users**. Every line of code you write must assume it runs 10,000× in parallel. **Default to the cheap option.** When a fast path and a convenient path are equally clear, pick the fast one. Be especially careful in the auth path, the main listing, and any Datastore-backed screen — those are the real hot paths.

### 13.1 Database / Datastore
- **Always paginate.** `Components.listing.properties(...)` defaults to paging — keep it that way. Never pass an unbounded `Datastore.query().target(...).findMany()` to a UI listing. Use `.limit(n)` with paging.
- **No N+1.** If a loop calls `datastore.findOne(...)` while iterating another result, fix it (join, batch, or `Property` pre-fetch).
- **Indexes.** Every `Property` referenced in `.filter(...)` or `.sort(...)` must have a backing DB index. Don't ship SQL/JPQL without them.
- **Pool sizing.** With 10k users, configure HikariCP `maximum-pool-size` ≈ `users × 0.1` (≈1000 max, subject to the DB hard cap), and set `connectionTimeout` < the HTTP request timeout so the pool can't deadlock under burst.
- **Async I/O.** Any call likely >50ms (DB query, HTTP, third-party API) → use `holon-async-datastore` / `holon-async-http`, or off-thread it. Never block the request thread.
- **Cache hot reads.** Wrap read-heavy paths (e.g. `AccountProvider`, lookup-by-id) in Spring `@Cacheable` with Caffeine or a distributed cache. Invalidate deliberately on writes.

### 13.2 Vaadin sessions (1 per user → ~10k sessions in memory)
- Vaadin creates **one HTTP session per user**. 10k sessions × ~50KB ≈ **500MB minimum**. Budget session-scoped beans accordingly.
- `@SessionScope` is **only** for auth context, locale, and small per-user prefs. **No** big collections, no caches, no non-serializable state.
- **No blocking in event handlers.** UI handlers run on the Vaadin session lock. Slow ops use `CompletableFuture.supplyAsync(...)` and re-enter with `UI.access(...)`.
- **`@Push`** must be enabled (websocket, not long-polling) so the server can deliver updates without per-user polling overhead.
- **Production mode.** Always `vaadin.productionMode=true` / the prod bundle. Never ship dev-mode bundles to 10k users.

### 13.3 Allocation, logging, concurrency
- `Property` / `PropertySet` / `DataTarget` definitions are **`static final` constants** — reuse them, don't allocate per request.
- Don't `toString()` a large object graph in a log. Log the **business event** at INFO; keep DEBUG behind a runtime guard.
- `ConcurrentHashMap` over `Collections.synchronizedMap`. `AtomicInteger` / `AtomicLong` over `synchronized` counters. **Never lock across I/O.**
- Use `volatile` correctly for double-checked init; reach for `j.u.concurrent` types instead of hand-rolled waiting.

### 13.4 Auth at scale
- Hash passwords with **bcrypt** or **Argon2** (Holon `Credentials.Encoder.HASH_BCRYPT` / `HASH_ARGON2`), never SHA-anything from 2008.
- If using JWT (`holon-auth-jwt`), **cache the signer** — don't re-resolve keys per request.
- **Rate-limit failed logins** via the `Authenticator`/`Authorizer` extension. This is your DOS shield.
- Session-fixation protection and CSRF on by default — verify `holon-starter-security` doesn't strip them.

### 13.5 Observability — you can't fix what you can't see
- Every bean on a hot path must emit a Micrometer metric (`@Timed`, `MeterRegistry.counter(...)`). Datastore query duration, `AuthContext` access, cache hit ratio — instrument them.
- `/actuator/prometheus` must exist and be scraped. If you add an endpoint, don't skip instrumentation.

### 13.6 Definition of done (perf)
A change touching the main listing, the form submission, the Datastore query path, the auth flow, or anything tagged "hot path" is **not done** until it's been load-tested at the target concurrency (Gatling / k6 / JMeter). Suggested ramp before merge:
```
k6 run --vus 1000 --duration 5m ...   # ramp and watch p95 latency + error rate
```
If you cannot run a load test locally, write a quick k6/Gatling script in the PR and call it out — do **not** declare done silently.

---

## 14. TL;DR for the inattentive

```
Holon module?  Yes → use it.
Vaadin core?   No  → use Components.* via holon-vaadin-flow.
Spring Sec?    No  → use Realm/AuthContext via holon-spring-security + holon-starter-security.
JPA EMF?       No  → use Datastore via holon-datastore-jpa + holon-spring-boot.
Routing?       No  → use @View + Navigator via holon-vaadin-flow-navigator.
Tests start?   Yes → holon-starter-test (core) or holon-vaadin-flow-test (UI).
BOM?           Yes → holon-bom:10.0.0 (core) + holon-vaadin-flow-bom:10.0.0 (vaadin). See §3.5.
Hot path?      Page, index, cache, async; keep @SessionScope small; load-test before merge.
```

When in doubt: read §5 (decision trees) and §6 (don't/do table). When still in doubt: §11. Perf concerns start at §13.
