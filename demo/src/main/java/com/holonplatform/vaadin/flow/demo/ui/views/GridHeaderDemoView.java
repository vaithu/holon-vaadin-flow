package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link Header} and {@link GridHeader} components.
 *
 * <p>Covers:
 * <ol>
 *   <li>Header – title only</li>
 *   <li>Header – with action buttons</li>
 *   <li>Header – with details chips and heading style options</li>
 *   <li>Header – with tabs</li>
 *   <li>GridHeader – selection-aware context actions</li>
 * </ol>
 */
@PageTitle("GridHeader – Holon Demo")
@Route(value = "grid-header", layout = DemoMainLayout.class)
public class GridHeaderDemoView extends Div {

    public GridHeaderDemoView() {
        addClassName("app-view");

        var title = new H1("GridHeader / Header");

        var desc = new Paragraph(
                "Header is a composite layout component that combines a breadcrumb, " +
                "a heading element (H1–H6 or Span), detail chips, and an action toolbar " +
                "into a cohesive page/section header. " +
                "GridHeader extends Header with selection-aware action toggling — " +
                "default actions are shown when no Grid rows are selected; " +
                "context actions replace them when one or more rows are selected.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(titleOnlyExample());
        examples.add(withActionsExample());
        examples.add(headingStylesExample());
        examples.add(withTabsExample());
        examples.add(gridHeaderExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample titleOnlyExample() {
        var header = new Header("Page Title");

        return new DemoExample("Title Only", header, """
                // Header defaults to HeadingLevel.H2.
                Header header = new Header("Page Title");

                // Explicit heading level:
                Header header = new Header("Dashboard", HeadingLevel.H1);
                """);
    }

    private DemoExample withActionsExample() {
        var header = new Header("Products");
        header.setActions(
                new Button("Import", VaadinIcon.UPLOAD.create()),
                new Button("Export", VaadinIcon.DOWNLOAD.create()),
                new Button("Add Product", VaadinIcon.PLUS.create())
        );

        return new DemoExample("With Action Toolbar", header, """
                Header header = new Header("Products");

                // setActions() replaces all current actions.
                header.setActions(
                    new Button("Import",     VaadinIcon.UPLOAD.create()),
                    new Button("Export",     VaadinIcon.DOWNLOAD.create()),
                    new Button("Add Product", VaadinIcon.PLUS.create())
                );
                """);
    }

    private DemoExample headingStylesExample() {
        var container = new Div();

        // Font size variants
        for (Font.Size size : new Font.Size[]{Font.Size.MEDIUM, Font.Size.LARGE, Font.Size.XLARGE, Font.Size.XXLARGE}) {
            var h = new Header("Heading – Font.Size." + size.name(), HeadingLevel.H3);
            h.setHeadingFontSize(size);
            container.add(h);
        }

        // Color variant
        var colorHeader = new Header("Heading – Color.Text.PRIMARY", HeadingLevel.H3);
        colorHeader.setHeadingTextColor(Color.Text.PRIMARY);
        container.add(colorHeader);

        return new DemoExample("Heading Font Size & Color", container, """
                // Change the heading element's font-size:
                header.setHeadingFontSize(Font.Size.XXLARGE);

                // Change the heading element's text colour:
                header.setHeadingTextColor(Color.Text.PRIMARY);

                // Change the heading element level at runtime:
                header.setHeading("New Title", HeadingLevel.H1);
                """);
    }

    private DemoExample withTabsExample() {
        var header = new Header("Reports");
        header.setTabs(
                new Tab("Daily")
        );
        header.setActions(new Button("Export", VaadinIcon.DOWNLOAD.create()));

        return new DemoExample("With Tabs", header, """
                Header header = new Header("Reports");

                // setTabs() adds a Tabs bar below the heading row.
                header.setTabs(
                    new Tab("Daily"),
                    new Tab("Weekly"),
                    new Tab("Monthly"),
                    new Tab("Yearly")
                );
                header.setActions(new Button("Export"));
                """);
    }

    private DemoExample gridHeaderExample() {
        record Product(String name, String category, double price) {}

        var grid = new Grid<Product>();
        grid.addColumn(Product::name).setHeader("Name");
        grid.addColumn(Product::category).setHeader("Category");
        grid.addColumn(p -> "€ " + String.format("%.2f", p.price())).setHeader("Price");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setItems(List.of(
                new Product("Laptop Pro", "Electronics", 1299.99),
                new Product("Wireless Mouse", "Accessories", 49.99),
                new Product("USB-C Hub", "Accessories", 89.99),
                new Product("Mechanical Keyboard", "Accessories", 159.99)
        ));

        // Context actions — shown when rows are selected
        var deleteBtn = new Button("Delete selected", VaadinIcon.TRASH.create());
        deleteBtn.addClickListener(e -> {
            int count = grid.getSelectedItems().size();
            grid.deselectAll();
            com.vaadin.flow.component.notification.Notification
                    .show(count + " item(s) deleted");
        });

        var exportBtn = new Button("Export selected", VaadinIcon.DOWNLOAD.create());

        // Default actions — shown when nothing is selected
        var addBtn = new Button("Add Product", VaadinIcon.PLUS.create());
        var filterBtn = new Button("Filter", VaadinIcon.FILTER.create());

        var selectionLabel = new Span();
        selectionLabel.getElement().setText("Select rows to see context actions appear");

        var gridHeader = new GridHeader("Products", grid);
        gridHeader.setDefaultActions(addBtn, filterBtn);
        gridHeader.setContextActions(deleteBtn, exportBtn);

        // Keep the selection hint updated
        grid.addSelectionListener(e -> {
            int size = e.getAllSelectedItems().size();
            selectionLabel.getElement().setText(
                    size == 0
                    ? "Select rows to see context actions appear"
                    : size + " row(s) selected — context actions shown"
            );
        });

        var container = new Div(gridHeader, selectionLabel, grid);

        return new DemoExample("GridHeader – Selection-Aware Context Actions", container, """
                Grid<Product> grid = new Grid<>();
                grid.setSelectionMode(Grid.SelectionMode.MULTI);

                GridHeader header = new GridHeader("Products", grid);

                // Default actions — visible when selection is empty
                header.setDefaultActions(
                    new Button("Add Product"),
                    new Button("Filter")
                );

                // Context actions — replace default actions when rows are selected
                header.setContextActions(
                    new Button("Delete selected"),
                    new Button("Export selected")
                );
                // The GridHeader listens to the grid's selection events automatically.
                """);
    }
}

