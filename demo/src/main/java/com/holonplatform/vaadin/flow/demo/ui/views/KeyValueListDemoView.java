package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValueItem;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValueList;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo page for {@link KeyValueList} and {@link KeyValueItem}.
 *
 * <p>Covers:
 * <ol>
 *   <li>Basic text rows (constructor + factory)</li>
 *   <li>Component values (Tag badge, HorizontalLayout icon+text)</li>
 *   <li>Separator toggle</li>
 *   <li>Required indicator (CSS asterisk)</li>
 *   <li>Copyable values (monospace, user-select)</li>
 *   <li>Per-row dividers (CSS ::after)</li>
 *   <li>Mixed — user profile card</li>
 *   <li>Super text (above key) and sub text (below value)</li>
 *   <li>Bank statement pattern (super + sub + valueEnd on mobile)</li>
 *   <li>Value wrapping</li>
 *   <li>Click to navigate (setClickable + addClickListener)</li>
 * </ol>
 */
@PageTitle("KeyValueList – Holon Demo")
@Route(value = "key-value-list", layout = DemoMainLayout.class)
public class KeyValueListDemoView extends Div {

    public KeyValueListDemoView() {
        addClassName("app-view");

        var title = new H1("KeyValueList");

        var desc = new Paragraph(
                "A 3-column CSS grid container (key | : | value) where each KeyValueItem " +
                "uses display:contents, giving perfect column alignment across all rows " +
                "without any JavaScript. Rich item API: copyable values, required " +
                "indicators, component values, dividers, and a fluent builder.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(factoryMethodExample());
        examples.add(componentValueExample());
        examples.add(separatorToggleExample());
        examples.add(requiredIndicatorExample());
        examples.add(copyableValueExample());
        examples.add(dividersExample());
        examples.add(mixedExample());
        examples.add(superSubTextExample());
        examples.add(bankStatementExample());
        examples.add(wrapExample());
        examples.add(clickNavigationExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample basicExample() {
        var list = new KeyValueList()
                .addItem(new KeyValueItem("First name", "Jane"))
                .addItem(new KeyValueItem("Last name",  "Smith"))
                .addItem(new KeyValueItem("Email",      "jane.smith@example.com"))
                .addItem(new KeyValueItem("Department", "Engineering"))
                .addItem(new KeyValueItem("Location",   "Berlin, Germany"));

        return new DemoExample("Basic Text Rows", list, """
                new KeyValueList()
                    .addItem(new KeyValueItem("First name", "Jane"))
                    .addItem(new KeyValueItem("Last name",  "Smith"))
                    .addItem(new KeyValueItem("Email",      "jane.smith@example.com"))
                    .addItem(new KeyValueItem("Department", "Engineering"))
                    .addItem(new KeyValueItem("Location",   "Berlin, Germany"));
                """);
    }

    private DemoExample factoryMethodExample() {
        // Using Components.keyValueList() and KeyValueItem.of() factories
        var list = Components.keyValueList(
                KeyValueItem.of("Company",   "Acme Corp"),
                KeyValueItem.of("Industry",  "Technology"),
                KeyValueItem.of("Founded",   "2005"),
                KeyValueItem.of("Employees", "1,200"),
                KeyValueItem.of("Website",   "acme.example.com")
        );

        return new DemoExample("Factory Methods", list, """
                // Components.keyValueList() pre-populates from varargs
                Components.keyValueList(
                    KeyValueItem.of("Company",   "Acme Corp"),
                    KeyValueItem.of("Industry",  "Technology"),
                    KeyValueItem.of("Founded",   "2005"),
                    KeyValueItem.of("Employees", "1,200"),
                    KeyValueItem.of("Website",   "acme.example.com")
                );
                """);
    }

    private DemoExample componentValueExample() {
        var statusItem = new KeyValueItem("Status", new Tag("Active"));

        // HorizontalLayout is an idiomatic flex-row component — no inline styles needed
        var roleRow = new HorizontalLayout(VaadinIcon.USER.create(), new Span("Senior Engineer"));
        roleRow.setAlignItems(FlexComponent.Alignment.CENTER);
        roleRow.setPadding(false);
        var roleItem = new KeyValueItem("Role", roleRow);

        var planItem  = new KeyValueItem("Plan",         new Tag("Enterprise"));
        var sinceItem = new KeyValueItem("Member since", "March 2022");

        var list = new KeyValueList()
                .addItem(statusItem)
                .addItem(roleItem)
                .addItem(planItem)
                .addItem(sinceItem);

        return new DemoExample("Component Values", list, """
                // Pass any Vaadin Component as the value.
                var statusItem = new KeyValueItem("Status", new Tag("Active"));

                var roleRow = new HorizontalLayout(VaadinIcon.USER.create(), new Span("Senior Engineer"));
                roleRow.setAlignItems(FlexComponent.Alignment.CENTER);
                roleRow.setPadding(false);
                var roleItem = new KeyValueItem("Role", roleRow);
                """);
    }

    private DemoExample separatorToggleExample() {
        var withColon    = new KeyValueItem("With separator",    "Visible colon between key and value");
        var withoutColon = new KeyValueItem("Without separator", "Colon hidden");
        withoutColon.setShowSeparator(false);

        var list = new KeyValueList()
                .addItem(withColon)
                .addItem(withoutColon);

        return new DemoExample("Separator Toggle", list, """
                // Default: separator ":" is visible.
                // Call setShowSeparator(false) to hide it per row.
                var withColon    = new KeyValueItem("With separator",    "Visible");
                var withoutColon = new KeyValueItem("Without separator", "Hidden");
                withoutColon.setShowSeparator(false);
                """);
    }

    private DemoExample requiredIndicatorExample() {
        var emailItem = KeyValueItem.builder()
                .key("Email")
                .value("jane.smith@example.com")
                .required(true)
                .build();

        var phoneItem = KeyValueItem.builder()
                .key("Phone")
                .value("Not provided")
                .required(false)
                .build();

        var notesItem = KeyValueItem.builder()
                .key("Notes")
                .value("Optional field — no asterisk shown")
                .build();

        var list = new KeyValueList()
                .addItem(emailItem)
                .addItem(phoneItem)
                .addItem(notesItem);

        return new DemoExample("Required Indicator", list, """
                // required(true) adds data-required="true" on the key span.
                // The CSS ::after inserts a red asterisk — purely visual, no validation.
                KeyValueItem.builder()
                    .key("Email")
                    .value("jane.smith@example.com")
                    .required(true)
                    .build();
                """);
    }

    private DemoExample copyableValueExample() {
        var tokenItem = KeyValueItem.builder()
                .key("API Token")
                .value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
                .copyable(true)
                .tooltip("Click to select and copy")
                .build();

        var secretItem = KeyValueItem.builder()
                .key("Client Secret")
                .value("sk_live_abc123xyz789")
                .copyable(true)
                .build();

        var normalItem = KeyValueItem.of("Version", "10.0.0");

        var list = new KeyValueList()
                .addItem(tokenItem)
                .addItem(secretItem)
                .addItem(normalItem);

        return new DemoExample("Copyable Values", list, """
                // copyable(true) adds kv-copyable class → monospace, user-select:text.
                // Pair with tooltip() for discoverability.
                KeyValueItem.builder()
                    .key("API Token")
                    .value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
                    .copyable(true)
                    .tooltip("Click to select and copy")
                    .build();
                """);
    }

    private DemoExample dividersExample() {
        // divider() adds a full-width ::after line spanning all 3 grid columns
        var list = new KeyValueList()
                .addItem(KeyValueItem.builder().key("Monday").value("Team standup").divider(true).build())
                .addItem(KeyValueItem.builder().key("Tuesday").value("Design review").divider(true).build())
                .addItem(KeyValueItem.builder().key("Wednesday").value("Sprint planning").divider(true).build())
                .addItem(KeyValueItem.builder().key("Thursday").value("Code review").divider(true).build())
                .addItem(KeyValueItem.of("Friday", "Retrospective"));   // last row — no divider

        return new DemoExample("Per-Row Dividers", list, """
                // divider(true) appends a full-width hr-like line below the row.
                // Implemented via CSS ::after spanning grid-column: 1 / -1
                // — no extra DOM element required.
                KeyValueItem.builder().key("Monday").value("Team standup").divider(true).build()
                // ...
                KeyValueItem.of("Friday", "Retrospective")  // last row — no divider
                """);
    }

    private DemoExample mixedExample() {
        var nameItem = KeyValueItem.builder()
                .key("Name")
                .value("Jane Smith")
                .divider(true)
                .build();

        var emailItem = KeyValueItem.builder()
                .key("Email")
                .value("jane.smith@example.com")
                .required(true)
                .divider(true)
                .build();

        var tokenItem = KeyValueItem.builder()
                .key("Token")
                .value("abc-123-xyz-789")
                .copyable(true)
                .tooltip("Select to copy")
                .divider(true)
                .build();

        var statusItem = new KeyValueItem("Status", new Tag("Active"));
        statusItem.setShowSeparator(false);

        var list = new KeyValueList()
                .addItem(nameItem)
                .addItem(emailItem)
                .addItem(tokenItem)
                .addItem(statusItem);

        return new DemoExample("Mixed — User Profile Card", list, """
                new KeyValueList()
                    .addItem(KeyValueItem.builder()
                        .key("Name").value("Jane Smith").divider(true).build())
                    .addItem(KeyValueItem.builder()
                        .key("Email").value("jane.smith@example.com")
                        .required(true).divider(true).build())
                    .addItem(KeyValueItem.builder()
                        .key("Token").value("abc-123-xyz-789")
                        .copyable(true).tooltip("Select to copy").divider(true).build())
                    .addItem(new KeyValueItem("Status", new Tag("Active")));
                """);
    }

    private DemoExample superSubTextExample() {
        var list = new KeyValueList()
                .addItem(KeyValueItem.builder()
                        .superText("Account holder")
                        .key("Jane Smith")
                        .value("EUR current account")
                        .subText("Opened March 2022")
                        .divider(true)
                        .build())
                .addItem(KeyValueItem.builder()
                        .superText("IBAN")
                        .key("DE89 3704 0044 0532 0130 00")
                        .value("€ 12,450.00")
                        .subText("Available balance")
                        .build());

        return new DemoExample("Super Text (above key) & Sub Text (below value)", list, """
                // superText() renders small ALL-CAPS tertiary label above the key.
                // subText()   renders small tertiary label below the value.
                // Both are hidden when null / blank.
                KeyValueItem.builder()
                    .superText("Account holder")
                    .key("Jane Smith")
                    .value("EUR current account")
                    .subText("Opened March 2022")
                    .divider(true)
                    .build()
                """);
    }

    private DemoExample bankStatementExample() {
        // Realistic bank-statement rows:
        // - superText:  uppercase category label
        // - key:        description / merchant
        // - value:      amount (right-aligned on mobile via valueEnd)
        // - subText:    running balance
        // - divider:    row separator
        var list = new KeyValueList()
                .addItem(KeyValueItem.builder()
                        .superText("income")
                        .key("Salary — Acme Corp")
                        .value("+€ 5,000.00")
                        .subText("Balance: € 12,450.00")
                        .valueEnd(true)
                        .divider(true)
                        .build())
                .addItem(KeyValueItem.builder()
                        .superText("rent")
                        .key("Monthly rent")
                        .value("−€ 1,200.00")
                        .subText("Balance: € 11,250.00")
                        .valueEnd(true)
                        .divider(true)
                        .build())
                .addItem(KeyValueItem.builder()
                        .superText("utilities")
                        .key("Electricity & Gas — E.ON")
                        .value("−€ 87.50")
                        .subText("Balance: € 11,162.50")
                        .valueEnd(true)
                        .divider(true)
                        .build())
                .addItem(KeyValueItem.builder()
                        .superText("subscription")
                        .key("Streaming service")
                        .value("−€ 14.99")
                        .subText("Balance: € 11,147.51")
                        .valueEnd(true)
                        .build());

        return new DemoExample("Bank Statement (super + sub + valueEnd)", list, """
                // valueEnd(true) right-aligns the value column on MOBILE ONLY.
                // On desktop the 3-column grid layout is unchanged.
                KeyValueItem.builder()
                    .superText("income")          // ALL-CAPS tertiary above key
                    .key("Salary — Acme Corp")    // primary description
                    .value("+€ 5,000.00")         // amount
                    .subText("Balance: € 12,450") // running balance below
                    .valueEnd(true)               // right-align amount on mobile
                    .divider(true)
                    .build()
                """);
    }

    private DemoExample wrapExample() {
        var list = new KeyValueList()
                .addItem(KeyValueItem.builder()
                        .key("Notes")
                        .value("This is a very long note that contains many words and should " +
                               "wrap gracefully across multiple lines without overflowing its " +
                               "container or breaking the grid layout.")
                        .wrap(true)
                        .divider(true)
                        .build())
                .addItem(KeyValueItem.builder()
                        .key("No wrap (default)")
                        .value("Short text fits on one line")
                        .build());

        return new DemoExample("Value Wrapping", list, """
                // wrap(true) adds kv-wrap modifier — removes white-space:nowrap
                // from key and adds overflow-wrap:break-word to value.
                KeyValueItem.builder()
                    .key("Notes")
                    .value("Long text that wraps...")
                    .wrap(true)
                    .build()
                """);
    }

    private DemoExample clickNavigationExample() {
        // What's needed for click-to-navigate:
        //  1. setClickable(true)     — adds pointer cursor + hover/active CSS feedback
        //  2. addClickListener(...)  — inherited from Div (ClickNotifier<Div>)
        //  3. UI.getCurrent().navigate(TargetView.class)  — or Navigator API

        var list = new KeyValueList()
                .addItem(buildClickableRow("Dashboard",   "Overview of all metrics",       "dashboard"))
                .addItem(buildClickableRow("Transactions","Recent payment history",         "transactions"))
                .addItem(buildClickableRow("Settings",    "Profile and account preferences","settings"));

        return new DemoExample("Click to Navigate", list, """
                // ① setClickable(true) — pointer cursor + hover/active highlight
                // ② addClickListener() — inherited from Div via ClickNotifier<Div>
                // ③ navigate inside the listener

                // Plain Vaadin navigation:
                item.setClickable(true);
                item.addClickListener(e ->
                    UI.getCurrent().navigate(DetailView.class));

                // Navigate with a route parameter:
                item.addClickListener(e ->
                    UI.getCurrent().navigate("orders/" + order.getId()));

                // Holon Navigator API (supports @QueryParameter injection):
                item.addClickListener(e ->
                    Navigator.get()
                        .navigation(DetailView.class)
                        .withQueryParameter("id", entity.getId())
                        .navigate());
                """);
    }

    /** Helper: builds a clickable row that shows a notification simulating navigation. */
    private KeyValueItem buildClickableRow(String title, String description, String route) {
        var item = KeyValueItem.builder()
                .key(title)
                .value(description)
                .clickable(true)
                .divider(!route.equals("settings")) // no divider on last row
                .build();

        // In a real view, replace with: UI.getCurrent().navigate(TargetView.class)
        item.addClickListener(e ->
                Notification.show("→ navigating to /" + route, 1500,
                        Notification.Position.BOTTOM_CENTER));
        return item;
    }
}














