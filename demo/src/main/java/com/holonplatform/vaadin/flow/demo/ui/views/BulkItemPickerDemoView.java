package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.BulkItemPickerDialog;
import com.holonplatform.vaadin.flow.vaadinplus.components.BulkPickerItem;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Locale;

/**
 * Demo page for the {@link BulkItemPickerDialog} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Default dialog with furniture item catalogue</li>
 *   <li>Custom title and button labels</li>
 *   <li>Pre-opened dialog using {@code autoOpen()}</li>
 * </ol>
 */
@PageTitle("BulkItemPickerDialog – Holon Demo")
@Route(value = "bulk-item-picker-dialog", layout = DemoMainLayout.class)
public class BulkItemPickerDemoView extends Div {

    // ── Sample catalogue ──────────────────────────────────────────────────────

    private static final List<BulkPickerItem> CATALOGUE = List.of(
            BulkPickerItem.of("1",  "Storage Cabinet",          "Item 3 sku", 345.00),
            BulkPickerItem.of("2",  "Dining Table and Chairs Set", "Item 4 sku", 126.00),
            BulkPickerItem.of("3",  "Coffee Table",             "Item 5 sku", 331.00),
            BulkPickerItem.of("4",  "Area Rug",                 "Item 6 sku", 579.00),
            BulkPickerItem.of("5",  "Sofa",                     "Item 7 sku", 307.00),
            BulkPickerItem.of("6",  "Dining Table and Chairs Set", "Item 8 sku", 499.00),
            BulkPickerItem.of("7",  "Queen Size Bed",           "Item 9 sku", 489.00),
            BulkPickerItem.of("8",  "Bookshelf",                "Item 10 sku", 219.00),
            BulkPickerItem.of("9",  "Office Chair",             "Item 11 sku", 189.00),
            BulkPickerItem.of("10", "Floor Lamp",               "Item 12 sku", 79.00)
    );

    // ── View ──────────────────────────────────────────────────────────────────

    public BulkItemPickerDemoView() {
        addClassName("app-view");

        var title = new H1("BulkItemPickerDialog");
        var desc = new Paragraph(
                "Two-panel dialog for adding multiple items in bulk with adjustable quantities. " +
                "Supports three provider modes: eager (in-memory filter), simple lazy (Function<String,List>), " +
                "and paginated (offset + limit + optional count — Spring Data style). " +
                "The search field debounces 300 ms before triggering any provider. " +
                "In paged mode a Previous / Next bar and 'Page X of Y · N items' counter appear at the bottom of the list.");

        var examples = ResponsiveDiv.flex().column().gapL().build();
        examples.add(eagerExample());
        examples.add(pagedWithCountExample());
        examples.add(pagedWithoutCountExample());
        examples.add(customLabelsExample());

        add(title, desc, examples);
    }

    // ── Examples ──────────────────────────────────────────────────────────────

    private DemoExample eagerExample() {
        Span resultLabel = new Span("No items added yet");

        Button openBtn = new Button("Open — eager (in-memory)");
        openBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        openBtn.addClickListener(e -> BulkItemPickerDialog.builder()
                .items(CATALOGUE)
                .onConfirm(entries -> {
                    String summary = entries.stream()
                            .map(en -> en.item().name() + " × " + en.quantity())
                            .reduce((a, b) -> a + ", " + b).orElse("(empty)");
                    resultLabel.setText("Added: " + summary);
                })
                .build().open());

        return new DemoExample("Eager mode — full catalogue in-memory, 300 ms debounced filter",
                new Div(openBtn, resultLabel), """
                // All items loaded upfront; filtered in-memory on each debounced keystroke.
                BulkItemPickerDialog.builder()
                    .items(catalogue)
                    .onConfirm(entries -> order.addLines(entries))
                    .build().open();
                """);
    }

    private DemoExample pagedWithCountExample() {
        Button openBtn = new Button("Open — paged with count (offset/limit/total)");
        openBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        openBtn.addClickListener(e -> BulkItemPickerDialog.builder()
                .pageSize(4)  // small page to make pagination visible in the demo
                .pagedItemProvider(
                    // fetch: receives (query, offset, limit) — call your repository here
                    q -> CATALOGUE.stream()
                            .filter(item -> q.query().isBlank()
                                    || item.name().toLowerCase(Locale.ROOT).contains(q.query().toLowerCase(Locale.ROOT))
                                    || item.sku().toLowerCase(Locale.ROOT).contains(q.query().toLowerCase(Locale.ROOT)))
                            .skip(q.offset())
                            .limit(q.limit())
                            .toList(),
                    // count: receives query — returns total matching rows
                    query -> CATALOGUE.stream()
                            .filter(item -> query.isBlank()
                                    || item.name().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))
                                    || item.sku().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))
                            .count()
                )
                .onConfirm(entries -> Notification.show(
                        entries.size() + " item(s) added", 3000, Notification.Position.BOTTOM_END))
                .build().open());

        return new DemoExample(
                "Paged mode with count — Page X of Y · N items  (pageSize=4 for demo visibility)",
                new Div(openBtn), """
                // Production pattern — replace with your repository call:
                BulkItemPickerDialog.builder()
                    .pageSize(20)
                    .pagedItemProvider(
                        q -> productRepo.findByNameOrSku(q.query(), q.offset(), q.limit()),
                        q -> productRepo.countByNameOrSku(q)
                    )
                    .onConfirm(entries -> order.addLines(entries))
                    .build().open();
                """);
    }

    private DemoExample pagedWithoutCountExample() {
        Button openBtn = new Button("Open — paged without count (no total)");
        openBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        openBtn.addClickListener(e -> BulkItemPickerDialog.builder()
                .pageSize(3)
                .pagedItemProvider(q -> CATALOGUE.stream()
                        .filter(item -> q.query().isBlank()
                                || item.name().toLowerCase(Locale.ROOT).contains(q.query().toLowerCase(Locale.ROOT)))
                        .skip(q.offset())
                        .limit(q.limit())
                        .toList())
                .onConfirm(entries -> Notification.show(
                        entries.size() + " item(s) added", 3000, Notification.Position.BOTTOM_END))
                .build().open());

        return new DemoExample(
                "Paged mode without count — shows 'Page X' only; Next disabled on last page (pageSize=3)",
                new Div(openBtn), """
                // Without count provider — Next is disabled when returned slice < pageSize.
                BulkItemPickerDialog.builder()
                    .pageSize(20)
                    .pagedItemProvider(q -> productRepo.findByNameOrSku(q.query(), q.offset(), q.limit()))
                    .onConfirm(entries -> order.addLines(entries))
                    .build().open();
                """);
    }

    private DemoExample customLabelsExample() {
        Button openBtn = new Button("Add Products to Purchase Order");
        openBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        openBtn.addClickListener(e -> BulkItemPickerDialog.builder()
                .title("Select Products")
                .searchPlaceholder("Search by name or SKU…")
                .addButtonText("Add to Order")
                .cancelButtonText("Discard")
                .items(CATALOGUE)
                .onConfirm(entries -> Notification.show(
                        entries.size() + " product(s) added to the order",
                        3000, Notification.Position.BOTTOM_END))
                .build()
                .open());

        return new DemoExample("Custom title and button labels", new Div(openBtn), """
                BulkItemPickerDialog.builder()
                    .title("Select Products")
                    .searchPlaceholder("Search by name or SKU…")
                    .addButtonText("Add to Order")
                    .cancelButtonText("Discard")
                    .items(catalogue)
                    .onConfirm(entries -> order.addLines(entries))
                    .build()
                    .open();
                """);
    }
}






