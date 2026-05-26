package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.TransferItem;
import com.holonplatform.vaadin.flow.vaadinplus.components.TransferList;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * Demo page for the {@link TransferList} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Basic transfer list — all items start available</li>
 *   <li>Pre-selected items — some items start in the selected panel</li>
 *   <li>Custom panel titles</li>
 * </ol>
 */
@PageTitle("TransferList – Holon Demo")
@Route(value = "transfer-list", layout = DemoMainLayout.class)
public class TransferListDemoView extends Div {

    public TransferListDemoView() {
        addClassName("app-view");

        var title = new H1("TransferList");
        var desc = new Paragraph(
                "Dual-panel shuttle component for moving items between an Available source list " +
                "and a Selected target list. Single-item and all-items transfer buttons are provided " +
                "in both directions. Click items to highlight (row-select) them before transferring. " +
                "Styled exclusively via transfer-list.css — no inline styles.");

        var examples = ResponsiveDiv.flex().column().gapL().build();
        examples.add(basicExample());
        examples.add(preSelectedExample());
        examples.add(customTitlesExample());

        add(title, desc, examples);
    }

    // ── Examples ──────────────────────────────────────────────────────────────

    private DemoExample basicExample() {
        var items = furnitureItems();
        Span status = new Span("0 items selected");

        var list = TransferList.builder()
                .availableItems(items)
                .onTransfer(e -> {
                    int count = e.getSelectedItems().size();
                    status.setText(count + " item(s) in Selected panel");
                })
                .build();

        Div preview = new Div(list, status);

        return new DemoExample("Basic — all items start available", preview, """
                TransferList list = TransferList.builder()
                    .availableItems(List.of(
                        TransferItem.of("1", "Coffee Table"),
                        TransferItem.of("2", "Sofa"),
                        TransferItem.of("3", "Area Rug")
                    ))
                    .onTransfer(e -> save(e.getSelectedItems()))
                    .build();
                """);
    }

    private DemoExample preSelectedExample() {
        var available = List.of(
                TransferItem.of("1", "Coffee Table"),
                TransferItem.of("2", "Storage Cabinet"),
                TransferItem.of("3", "Area Rug"),
                TransferItem.of("4", "Floor Lamp"),
                TransferItem.of("5", "Bookshelf")
        );
        var preSelected = List.of(
                TransferItem.of("6", "Sofa"),
                TransferItem.of("7", "Queen Size Bed")
        );

        var list = TransferList.builder()
                .availableItems(available)
                .selectedItems(preSelected)
                .build();

        return new DemoExample("Pre-selected items", new Div(list), """
                TransferList list = TransferList.builder()
                    .availableItems(available)
                    .selectedItems(preSelected)    // shown in right panel from start
                    .build();
                """);
    }

    private DemoExample customTitlesExample() {
        var list = TransferList.builder()
                .availableItems(furnitureItems())
                .availableTitle("Product Catalogue")
                .selectedTitle("Purchase Order Items")
                .onTransfer(e -> {
                    // items available for downstream wiring, e.g. updating a grid
                    // e.getSelectedItems() → List<TransferItem> ready for use
                })
                .build();

        return new DemoExample("Custom panel titles", new Div(list), """
                TransferList list = TransferList.builder()
                    .availableItems(catalogue)
                    .availableTitle("Product Catalogue")
                    .selectedTitle("Purchase Order Items")
                    .onTransfer(e -> lineItems.setItems(e.getSelectedItems()))
                    .build();
                """);
    }

    // ── Sample data ───────────────────────────────────────────────────────────

    private static List<TransferItem> furnitureItems() {
        return List.of(
                TransferItem.of("1",  "Storage Cabinet"),
                TransferItem.of("2",  "Dining Table and Chairs Set"),
                TransferItem.of("3",  "Coffee Table"),
                TransferItem.of("4",  "Area Rug"),
                TransferItem.of("5",  "Sofa"),
                TransferItem.of("6",  "Queen Size Bed"),
                TransferItem.of("7",  "Bookshelf"),
                TransferItem.of("8",  "Office Chair"),
                TransferItem.of("9",  "Floor Lamp"),
                TransferItem.of("10", "Side Table")
        );
    }
}



