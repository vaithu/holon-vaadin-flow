package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.holonplatform.vaadin.flow.vaadinplus.components.SheetStack;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import com.vaadin.flow.theme.lumo.LumoIcon;


/**
 * Demo page for the {@link Sheet} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Bottom sheet (default mobile pattern)</li>
 *   <li>Right sheet (detail / filter panel)</li>
 *   <li>Left sheet (navigation drawer)</li>
 *   <li>With title, description, and content</li>
 *   <li>Lazy content (loaded once on first open)</li>
 *   <li>Full-screen on mobile</li>
 * </ol>
 *
 * <p>Each example renders a trigger button. The Sheet opens as a full-screen overlay.
 */
@PageTitle("Sheet – Holon Demo")
@Route(value = "sheet", layout = DemoMainLayout.class)
public class SheetDemoView extends Div {

    public SheetDemoView() {
        addClassName("app-view");

        var title = new H1("Sheet");

        var desc = new Paragraph(
                "Mobile-first slide-in panel inspired by shadcn/ui Sheet. " +
                "Unlike Drawer (which adds touch gestures and drag-to-dismiss), " +
                "Sheet focuses on a clean CSS slide transition with an optional close/back button in the header. " +
                "Supports BOTTOM, LEFT, and RIGHT sides; " +
                "browser History API integration; lazy content loading; and full-screen mobile expansion.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(bottomExample());
        examples.add(rightExample());
        examples.add(leftExample());
        examples.add(fullSlotsExample());
        examples.add(lazyContentExample());
        examples.add(stackedSheetsExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample bottomExample() {
        var sheet = Sheet.builder(Sheet.Side.BOTTOM)
                .title("Quick Actions")
                .description("Choose an action to perform.")
                .content(new Paragraph("Add content here — buttons, lists, forms, etc."))
                .build();

        var triggerBtn = new Button("Open Bottom Sheet", VaadinIcon.ARROW_UP.create());
        triggerBtn.addClickListener(e -> sheet.open());

        return new DemoExample("Bottom (default — action sheet pattern)", triggerBtn, """
                Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM)
                    .title("Quick Actions")
                    .description("Choose an action to perform.")
                    .content(new Paragraph("…"))
                    .build();

                openButton.addClickListener(e -> sheet.open());
                """);
    }

    private DemoExample rightExample() {
        var filterBox = new ListBox<String>();
        filterBox.setItems("Last 7 days", "Last 30 days", "Last 90 days", "Custom range");
        filterBox.setValue("Last 30 days");

        var sheet = Sheet.builder(Sheet.Side.RIGHT)
                .title("Filter Results")
                .description("Select a date range to narrow the report.")
                .content(filterBox)
                .onClose(() -> Notification.show("Filter: " + filterBox.getValue()))
                .build();

        var triggerBtn = new Button("Open Filter Panel", VaadinIcon.FILTER.create());
        triggerBtn.addClickListener(e -> sheet.open());

        return new DemoExample("Right (filter / detail panel)", triggerBtn, """
                Sheet sheet = Sheet.builder(Sheet.Side.RIGHT)
                    .title("Filter Results")
                    .description("Select a date range.")
                    .content(filterForm)
                    .onClose(() -> applyFilters())
                    .build();
                """);
    }

    private DemoExample leftExample() {
        var navItems = new Div();
        for (String item : new String[]{"Home", "Orders", "Products", "Reports", "Settings", "Help"}) {
            var navItem = new Paragraph(item);
            navItems.add(navItem);
        }

        var sheet = Sheet.builder(Sheet.Side.LEFT)
                .title("Navigation")
                .content(navItems)
                .build();

        var menuBtn = new Button("Open Nav Sheet", VaadinIcon.MENU.create());
        menuBtn.addClickListener(e -> sheet.open());

        return new DemoExample("Left (navigation drawer)", menuBtn, """
                Sheet sheet = Sheet.builder(Sheet.Side.LEFT)
                    .title("Navigation")
                    .content(navigationList)
                    .build();

                menuButton.addClickListener(e -> sheet.open());
                """);
    }

    private DemoExample fullSlotsExample() {
        var nameField    = new TextField("Full Name");
        var emailField   = new TextField("Email");
        var notesField   = new TextArea("Notes");
        notesField.setMaxHeight("120px");

        var formDiv = new Div(nameField, emailField, notesField);

        var sheet = Sheet.builder(Sheet.Side.RIGHT)
                .title("Edit Contact")
                .description("Update the contact's information below. Changes are saved on close.")
                .content(formDiv)
                .onOpen(nameField::focus)
                .onClose(() -> Notification.show("Saved: " + nameField.getValue()))
                .build();

        var triggerBtn = new Button("Open Edit Sheet", LumoIcon.EDIT.create());
        triggerBtn.addClickListener(e -> sheet.open());

        return new DemoExample("Title + Description + onOpen/onClose callbacks", triggerBtn, """
                Sheet sheet = Sheet.builder(Sheet.Side.RIGHT)
                    .title("Edit Contact")
                    .description("Update the contact's information below.")
                    .content(nameField, emailField, notesField)
                    .onOpen(() -> nameField.focus())
                    .onClose(() -> saveChanges())
                    .build();
                """);
    }

    private DemoExample lazyContentExample() {
        var sheet = Sheet.builder(Sheet.Side.BOTTOM)
                .title("Lazy-Loaded Content")
                .description("Content is fetched exactly once on first open; subsequent re-opens reuse the DOM.")
                .lazyContent(() -> {
                    var msg = new Paragraph("Content loaded at " + java.time.LocalTime.now()
                            + "  (this timestamp won't change on re-open).");
                    return new com.vaadin.flow.component.Component[]{msg};
                })
                .build();

        var triggerBtn = new Button("Open Lazy Sheet (open twice to see)", VaadinIcon.CLOCK.create());
        triggerBtn.addClickListener(e -> sheet.open());

        return new DemoExample("Lazy Content (loaded once on first open)", triggerBtn, """
                // lazyContent() supplier is called exactly once on the first open().
                // Ideal for heavy DB-backed content; pair with @Cacheable on the service.
                Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM)
                    .title("Order Details")
                    .lazyContent(() -> new Component[]{ orderDetailView.build(orderId) })
                    .onOpen(() -> orderDetailView.refresh())   // lightweight refresh only if stale
                    .build();
                """);
    }

    private DemoExample stackedSheetsExample() {

        // ── SheetStack ────────────────────────────────────────────────────────
        // Manages a SizedStack<Sheet> internally. Each push():
        //   • root (depth 0) → backdropVisible(true)  – dims the app content
        //   • child (depth > 0) → backdropVisible(false) – parent not visible through overlay
        //   • all → fullscreenOnMobile(true) – 100 % viewport on mobile
        //   • onClose → removes from stack + detaches element after the 300 ms animation
        var sheetStack = new SheetStack(Sheet.Side.RIGHT);

        // ── Signals (2 only) — feed the navTracker breadcrumb ─────────────────
        // SheetStack handles all the open/backdrop/fullscreen logic; signals are
        // only needed to keep the always-attached navTracker in sync.
        var categorySignal = new ValueSignal<>("");
        var productSignal  = new ValueSignal<>("");

        var navTracker = new Span("—");
        Signal.effect(navTracker, () -> {
            var cat  = categorySignal.get();
            var prod = productSignal.get();
            navTracker.setText(cat.isBlank() ? "—"
                    : prod.isBlank() ? "📂  " + cat
                    : "📂  " + cat + "  ›  " + prod);
        });

        // ── Detail content (single instance, updated before each push) ────────
        var detailName  = new Paragraph();
        var detailPrice = new Paragraph();
        var detailDesc  = new Paragraph(
                "Premium quality. 2-year manufacturer warranty. " +
                "Free delivery on orders over €50. 30-day hassle-free returns.");
        var detailContent = new Div(detailName, detailPrice, detailDesc);

        // ── Data ──────────────────────────────────────────────────────────────
        record Product(String name, String price, String emoji) {}
        record Category(String name, String emoji, Product[] products) {}

        var categories = new Category[]{
            new Category("Electronics", "💻", new Product[]{
                new Product("Laptop Pro 15",        "€1,299", "💻"),
                new Product("Wireless Headphones",  "€249",   "🎧"),
                new Product("Smart Watch Series 9", "€399",   "⌚")
            }),
            new Category("Clothing", "👕", new Product[]{
                new Product("Premium Leather Jacket", "€189", "🧥"),
                new Product("Classic Denim Jeans",    "€89",  "👖"),
                new Product("Merino Wool Scarf",      "€59",  "🧣")
            }),
            new Category("Furniture", "🛋️", new Product[]{
                new Product("Solid Oak Dining Table", "€699",   "🪑"),
                new Product("Ergonomic Office Chair", "€449",   "🪑"),
                new Product("Linen Corner Sofa",      "€1,199", "🛋️")
            })
        };

        // ── Product list container (single instance, rebuilt per category) ────
        var productListDiv = new Div();

        // ── Category list ─────────────────────────────────────────────────────
        var categoryDiv = new Div();

        for (var cat : categories) {
            var catRow     = new Div();
            var catLabel   = new Span(cat.emoji() + "  " + cat.name());
            var catChevron = new Span("›");
            catRow.add(catLabel, catChevron);

            catRow.addClickListener(e -> {
                categorySignal.set(cat.name());
                productSignal.set("");

                productListDiv.removeAll();
                for (var p : cat.products()) {
                    var row       = new Div();
                    var info      = new Div();
                    var nameSpan  = new Span(p.emoji() + "  " + p.name());
                    var priceSpan = new Span(p.price());
                    info.add(nameSpan, priceSpan);

                    var viewBtn = new Button("View");
                    viewBtn.addClickListener(ev -> {
                        // Set content imperatively — SheetStack handles the rest
                        detailName.setText(p.emoji() + "  " + p.name());
                        detailPrice.setText(p.price());
                        productSignal.set(p.name()); // navTracker only
                        sheetStack.push("Product Detail", detailContent);
                    });

                    row.add(info, viewBtn);
                    productListDiv.add(row);
                }
                // SheetStack.push() applies backdropVisible(false) + fullscreenOnMobile automatically
                sheetStack.push(cat.name(), productListDiv);
            });

            categoryDiv.add(catRow);
        }

        var triggerBtn = new Button("Browse Catalogue", VaadinIcon.LIST.create());
        triggerBtn.addClickListener(e -> {
            categorySignal.set("");
            productSignal.set("");
            sheetStack.push("Shop by Category", categoryDiv); // root → backdropVisible(true)
        });

        var wrapper = new Div(triggerBtn, navTracker);

        return new DemoExample(
                "Stacked RIGHT Sheets — SheetStack + 2 Signals for breadcrumb",
                wrapper, """
                // SheetStack wraps SizedStack<Sheet> and applies stacking rules automatically.
                var sheetStack = new SheetStack(Sheet.Side.RIGHT);   // or Components.sheetStack(Side.RIGHT)

                // 2 signals — only for the navTracker breadcrumb outside the sheets.
                // SheetStack handles backdropVisible / fullscreenOnMobile / DOM cleanup.
                var categorySignal = new ValueSignal<>("");
                var productSignal  = new ValueSignal<>("");
                Signal.effect(navTracker, () -> navTracker.setText(buildBreadcrumb(...)));

                // Level 1 — root: backdropVisible(true) applied by SheetStack
                openBtn.addClickListener(e -> sheetStack.push("Shop by Category", categoryList));

                // Level 2 — child: backdropVisible(false) + fullscreenOnMobile(true) applied automatically
                categoryRow.addClickListener(e -> {
                    categorySignal.set(cat.name());  // → navTracker updates
                    rebuildProductList(productListDiv, cat);
                    sheetStack.push(cat.name(), productListDiv);
                });

                // Level 3 — child: same rules
                viewBtn.addClickListener(e -> {
                    detailName.setText(p.name());    // imperative — no signal needed here
                    detailPrice.setText(p.price());
                    productSignal.set(p.name());     // → navTracker only
                    sheetStack.push("Product Detail", detailView);
                });

                // On close (back button / close icon):
                //   • Sheet removed from SizedStack
                //   • Element removed from DOM after 350 ms (post animation)
                //   • Previous sheet becomes visible (it was never closed)
                """);
    }
}



