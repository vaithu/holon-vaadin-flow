# holon-vaadin-flow-ai-assistant

AI chat assistant components for Vaadin Flow, built on top of the **free, Apache-2.0-licensed**
`com.vaadin:vaadin-ai-core-flow` module (`AIOrchestrator` / `LLMProvider` / `AIController`),
with data access delegated to the **Holon Datastore API** — never raw JDBC handed to the LLM.

> **Preview feature.** `vaadin-ai-core-flow` is a Vaadin 25.1+ preview feature (named
> `vaadin-ai-components-flow` before Vaadin 25.3). Enable it in
> `src/main/resources/vaadin-featureflags.properties`:
> ```properties
> com.vaadin.experimental.aiComponents=true
> ```

## What it provides

| Class | Purpose |
| --- | --- |
| `AIAssistant` | Ready-to-use chat component paired with a Holon `Datastore`-backed `AIController` — the "assistant with domain tools" building block. |
| `AIChatClient` | Generic, domain-agnostic multi-modal chat component — text, dropped/browsed files, and clipboard-pasted images. No controller required. |
| `ClipboardAwareFileReceiver` | Custom `AIFileReceiver` merging `Upload`-based and clipboard-paste-based attachments. |
| `DatastoreAIController` | Whitelisted, tool-based `AIController` — tools delegate to a Holon `Datastore` / `BeanDatastoreHelper` / `*Service`. |
| `DatastoreDatabaseProvider` | `DatabaseProvider` for open-ended NL-to-SQL querying, guarded to single read-only `SELECT` statements. |
| `BeanListingAIController` | Lets the LLM filter/sort a Holon `BeanListing` (Grid) through a whitelisted set of bean properties — no SQL/table names ever reach the model. |
| `ChartJsAIController` | Lets the LLM render charts on a Holon `ChartJsComponent` through a whitelisted grouping/aggregation tool. |
| `BeanPropertyInputFormAIController` | Lets the LLM fill a Holon `BeanPropertyInputForm` from unstructured text/attachments, through a whitelisted, type-safe, validator-respecting tool. |

Each `*AIController` above is a **Holon-native, non-commercial equivalent** of Vaadin's built-in
`GridAIController` / `ChartAIController` / `FormAIController` (which *are* present in the same free
jar, and can also be used directly — see [Using Vaadin's built-in controllers](#using-vaadins-built-in-controllers)).
The Holon versions trade some flexibility for an explicit whitelist of what the LLM is allowed to
touch, and route every write through the existing Datastore/validator/authorization layer.

## Prerequisites

1. Add the module dependency:
   ```xml
   <dependency>
       <groupId>com.holon-platform.vaadin</groupId>
       <artifactId>holon-vaadin-flow-ai-assistant</artifactId>
   </dependency>
   ```
2. Enable the preview feature flag (see above).
3. Add `@Push` to your `AppShellConfigurator` — streaming responses require server push:
   ```java
   @Push
   public class Application implements AppShellConfigurator {
   }
   ```
4. Provide an `LLMProvider` bean — either `SpringAILLMProvider` (wraps a Spring AI `ChatModel`) or
   `LangChain4JLLMProvider` (wraps a LangChain4j `ChatModel`), both shipped in
   `vaadin-ai-core-flow`. Read the API key from configuration — never hard-code it:
   ```java
   @Bean
   LLMProvider llmProvider(@Value("${app.ai.api-key}") String apiKey) {
       ChatModel chatModel = OpenAiChatModel.builder()
           .openAiApi(OpenAiApi.builder().apiKey(apiKey).build())
           .build();
       return new SpringAILLMProvider(chatModel);
   }
   ```

---

## Step 1 — A plain multi-modal chat window (`AIChatClient`)

Use this when you just need a general-purpose chat surface — no domain tools required. Supports:

- **Text** — typed or pasted directly into the message box.
- **Files/images (drag & drop or click-to-browse)** — a visible `Upload` area.
- **Clipboard-pasted images** (e.g. a screenshot) — captured automatically anywhere in the
  component, queued exactly like a dropped file.

```java
@Route("assistant")
@RolesAllowed("USER")
public class AssistantView extends VerticalLayout {

    public AssistantView(LLMProvider provider) {
        AIChatClient chat = AIChatClient.builder(provider,
                "You are a helpful general-purpose assistant. Be concise.")
            .fullSize()
            .build();
        add(chat);
        setSizeFull();
    }
}
```

Restrict accepted attachments and tune guardrails:

```java
AIChatClient chat = AIChatClient.builder(provider, systemPrompt)
    .acceptedMimeTypePrefixes(Set.of("image/", "application/pdf"))
    .maxFileSize(5 * 1024 * 1024)   // 5 MB
    .maxAttachments(3)
    .clipboardPasteEnabled(true)    // default
    .streaming(true)                // default, requires @Push
    .build();
```

Retain/restore conversation history (e.g. across page reloads — see the module's chat-history
section below):

```java
List<ChatMessage> history = chat.getHistory();
// persist `history` (and any needed attachments) via your Datastore ...

AIChatClient restored = AIChatClient.builder(provider, systemPrompt)
    .withHistory(savedHistory, savedAttachments)
    .build();
```

---

## Step 2 — An assistant with domain tools (`AIAssistant` + `DatastoreAIController`)

Use this when the assistant must **answer questions using your data** — e.g. "how many customers
are in the Gold tier?". Define one `AITool` per operation you want to expose, each delegating to
an existing Holon-backed service:

```java
public class CustomerFindByTierTool implements AITool {

    private final CustomerService customerService; // your Holon Datastore-backed service

    public CustomerFindByTierTool(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public String getName() {
        return "Customer_findByTier";
    }

    @Override
    public String getDescription() {
        return "Finds customers belonging to the given tier (GOLD, SILVER, BRONZE).";
    }

    @Override
    public String getParametersSchema() {
        return """
                { "type": "object",
                  "properties": { "tier": { "type": "string", "enum": ["GOLD","SILVER","BRONZE"] } },
                  "required": ["tier"] }""";
    }

    @Override
    public String execute(JsonNode arguments) {
        String tier = arguments.get("tier").asText();
        List<Customer> customers = customerService.findByTier(tier); // Datastore-backed query
        return customers.stream().map(Customer::getName).collect(Collectors.joining(", "));
    }
}
```

Wire it into an `AIAssistant`:

```java
DatastoreAIController controller = DatastoreAIController.builder()
    .withTool(new CustomerFindByTierTool(customerService))
    .build();

AIAssistant assistant = AIAssistant.builder(provider,
        "You are a helpful assistant for the customer module. Answer concisely, "
      + "using the Customer_findByTier tool when the user asks about tiers.")
    .withController(controller)
    .fullSize()
    .build();
add(assistant);
```

> **One controller per orchestrator.** Register every tool for a given assistant on a single
> `DatastoreAIController` instance rather than attaching multiple controllers.

### Open-ended natural-language querying (`DatastoreDatabaseProvider`)

For "ask anything about the data" scenarios, attach a `DatabaseProvider` instead of/alongside a
tool-based controller. **Always** back it with a **read-only** database account:

```java
DatastoreDatabaseProvider dbProvider = new DatastoreDatabaseProvider(
    readOnlyDataSource,
    "TABLE customers(id, name, tier, region) -- tier: GOLD/SILVER/BRONZE",
    /* maxRows */ 200);
```

Guardrails already enforced:

- Only single `SELECT` / `WITH ... SELECT` statements are executed (`ReadOnlySqlGuard`) —
  anything else throws `IllegalArgumentException`.
- A row cap (`maxRows`, default 200) avoids unbounded result sets.
- Query **rows** are returned to your UI code, never fed back into the model — only the
  plain-text schema from `getSchema()` ever reaches the LLM.

---

## Step 3 — Let the AI drive a Grid (`BeanListingAIController`)

```java
BeanListing<Customer> customerListing = BeanListing.builder(Customer.class)
    .dataSource(datastore, CUSTOMER_TARGET, filterConverter)
    .withQueryConfigurationProvider(gridController) // registered below
    .build();

BeanListingAIController<Customer> gridController =
        BeanListingAIController.attach(customerListing, Customer.class, "name", "email", "tier");

AIAssistant assistant = AIAssistant.builder(provider,
        "You can filter and sort the customers grid using the Grid_query tool.")
    .withController(gridController)
    .build();
```

The LLM only ever sees the whitelisted bean property names (`name`, `email`, `tier`) — never SQL
or table names. The resulting `QueryFilter`/`QuerySort` is applied through the listing's existing
Datastore-backed data provider, so query-time authorization/scoping still applies.

---

## Step 4 — Let the AI render charts (`ChartJsAIController`)

```java
ChartJsAIController chartController = ChartJsAIController.builder(chartComponent)
    .withGroupableProperty("tier")
    .withGroupableProperty("region")
    .withAggregateProperty("amount")
    .withAllowedChartTypes(ChartType.BAR, ChartType.PIE, ChartType.LINE)
    .withDataResolver(spec -> customerService.aggregate(spec)) // Datastore-backed aggregate query
    .build();

AIAssistant assistant = AIAssistant.builder(provider,
        "You can render charts of the customers data using the Chart_render tool.")
    .withController(chartController)
    .build();
```

`ChartDataResolver` is application-supplied and is expected to delegate to a Holon
`Datastore`/`BeanDatastoreHelper` group-by aggregate query — never raw SQL handed to the LLM.

---

## Step 5 — Let the AI fill a form from pasted text (`BeanPropertyInputFormAIController`)

Use this when the user pastes unstructured text (an email, a receipt, an invoice) and you want the
AI to populate a Holon form — with **existing field validators still enforced** and **no direct
database write**.

```java
BeanPropertyInputForm<Invoice> form = BeanPropertyInputForm.formLayout(Invoice.class).build();

BeanPropertyInputFormAIController<Invoice> formController = BeanPropertyInputFormAIController
    .attach(form, Invoice.class, "supplierName", "invoiceNumber", "amount", "dueDate");

AIChatClient chat = AIChatClient.builder(provider,
        "Extract invoice fields from the text or attachment the user provides, then call "
      + "Form_fillFromText to populate the form. Never guess values not clearly present in "
      + "the source. Always let the user review and confirm before saving.")
    .withController(formController)
    .build();

// (optional) observe/audit what the AI changed
formController.addFieldValueChangedListener(changes ->
        changes.forEach(c -> log.info("{} : {} -> {}", c.property(), c.oldValue(), c.newValue())));

add(chat, form, saveButton); // saveButton explicitly persists form.getBean() — the AI never does
```

**How it works:**

1. `Form_fillFromText` is the *only* tool exposed, and its JSON-Schema restricts the `property`
   field to an `enum` of exactly the names you whitelist (`supplierName`, `invoiceNumber`, ...) —
   the LLM cannot set anything else.
2. Extracted values are converted to the bean property's real type and written into a **fresh
   bean instance** via `BeanPropertySet`, then applied with `form.setBean(...)` — so all existing
   `Validator`s/converters configured on the form run exactly as if the user had typed the values.
3. Changed fields are diffed (old vs. new value), an `"ai-filled"` CSS class is added so you can
   visually flag them, and `FieldValueChange` events are fired to any registered listener.
4. **Nothing is persisted** by this controller — saving remains an explicit, separate user action.

Style the highlight in your theme, e.g.:

```css
.ai-filled {
    background-color: var(--lumo-primary-color-10pct);
    border-color: var(--lumo-primary-color);
}
```

### Using Vaadin's built-in controllers

`GridAIController` / `ChartAIController` / `FormAIController` ship in the **commercial**
`vaadin-ai-extensions-flow` jar (since Vaadin 25.3; they were part of the free
`vaadin-ai-components-flow` jar before) and can be used directly for quick, generic
scenarios — e.g. `FormAIController` auto-scans *any* `HasValue` field inside a `FormLayout` (or
other `Component & HasComponents` container) with no whitelist. This module does not depend on
them; add `com.vaadin:vaadin-ai-extensions-flow` yourself if you want to use them:

```java
FormAIController formController = new FormAIController(formLayout);
formController.describeField((HasValue<?, ?>) firstNameField, "Customer's given name");
```

Since Holon's `Input<T>.getComponent()` returns the real underlying Vaadin field, this works
transparently with Holon forms too — but without the whitelist/validator-first guarantees the
Holon-native controllers above provide. Choose the Holon-native controller when you want a
locked-down, auditable AI-write surface; choose the raw Vaadin controller for fast prototyping on
a simple form.

---

## Conversation history

The `AIOrchestrator` behind `AIAssistant`/`AIChatClient` tracks the full conversation for the
lifetime of the component/UI session automatically — no action needed for normal use within a
session.

To persist a conversation across page reloads or server restarts:

```java
// save (e.g. on detach, or periodically)
List<ChatMessage> history = chat.getHistory();
// persist `history` through your Datastore ...

// restore (next time the view/component is built)
AIChatClient chat = AIChatClient.builder(provider, systemPrompt)
    .withHistory(savedHistory, savedAttachments)
    .build();
```

`withHistory(...)` repopulates both the LLM's context and the visible `MessageList`.

---

## Security guardrails checklist

- [ ] Data exposed to the LLM goes through a Holon-backed custom `AIController`/`DatabaseProvider`
      that delegates to the injected `Datastore`/`BeanDatastoreHelper` — never raw JDBC.
- [ ] `DatastoreDatabaseProvider`/query tools are backed by a **read-only** database account.
- [ ] `getSchema()` returns a plain-text schema description only; `executeQuery` results are
      rendered in the UI and **never** returned to the model.
- [ ] `AIOrchestrator`/`AIAssistant.builder(...)`/`AIChatClient.builder(...)` are always given a
      **system prompt**.
- [ ] Each `LLMProvider`/`MessageList`/`MessageInput`/controller belongs to exactly **one**
      orchestrator (fresh instances per view/component).
- [ ] `@Push` is present on the `AppShellConfigurator` class (streaming requires server push).
- [ ] The `com.vaadin.experimental.aiComponents` feature flag is enabled.
- [ ] The LLM API key is read from configuration — no hard-coded secrets in Java.
- [ ] The chat route is guarded with `@Authenticate`/`@RolesAllowed`, like every other view.
- [ ] Form-filling/grid-filtering/chart-rendering controllers are given an **explicit whitelist**
      of properties — never bound to arbitrary bean fields.
- [ ] AI-driven form fills are reviewed by a human before any explicit "Save" action persists them.

## Build

```bash
mvn -pl ai-assistant -am test
```
