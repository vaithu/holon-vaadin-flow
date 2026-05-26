package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.internal.lumo.Background;
import com.holonplatform.vaadin.flow.internal.lumo.Breakpoint;
import com.holonplatform.vaadin.flow.internal.lumo.ColumnSpan;
import com.holonplatform.vaadin.flow.internal.lumo.GridColumns;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValuePair;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValuePairs;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for {@link KeyValuePairs} and {@link KeyValuePair}.
 *
 * <p>Covers:
 * <ol>
 *   <li>Simple text key–value pairs (default side layout)</li>
 *   <li>Key position: TOP</li>
 *   <li>Two-column grid</li>
 *   <li>Column span across multiple grid columns</li>
 *   <li>Background colour variants</li>
 *   <li>Striped rows</li>
 *   <li>Custom key widths and responsive breakpoint</li>
 * </ol>
 */
@PageTitle("KeyValuePairs – Holon Demo")
@Route(value = "key-value-pairs", layout = DemoMainLayout.class)
public class KeyValuePairsDemoView extends Div {

    public KeyValuePairsDemoView() {
        addClassName("app-view");

        var title = new H1("KeyValuePairs");

        var desc = new Paragraph(
                "A semantic <dl> list for displaying structured property–value data. " +
                "Supports CSS grid columns, column spans, key position (SIDE / TOP), " +
                "key width, background colours, and optional striped rows.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(simpleExample());
        examples.add(keyPositionTopExample());
        examples.add(twoColumnExample());
        examples.add(columnSpanExample());
        examples.add(backgroundsExample());
        examples.add(stripesExample());
        examples.add(keyWidthAndBreakpointExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample simpleExample() {
        var kvp = new KeyValuePairs(
                new KeyValuePair("First name",  "Jane"),
                new KeyValuePair("Last name",   "Smith"),
                new KeyValuePair("Email",       "jane.smith@example.com"),
                new KeyValuePair("Department",  "Engineering"),
                new KeyValuePair("Location",    "Berlin, Germany")
        );

        return new DemoExample("Simple Text Pairs", kvp, """
                // Default: key on the left (KeyPosition.SIDE), 25% key width,
                // breakpoint MEDIUM (key wraps above value on small screens).
                new KeyValuePairs(
                    new KeyValuePair("First name", "Jane"),
                    new KeyValuePair("Last name",  "Smith"),
                    new KeyValuePair("Email",      "jane.smith@example.com"),
                    new KeyValuePair("Department", "Engineering")
                );
                """);
    }

    private DemoExample keyPositionTopExample() {
        var kvp = new KeyValuePairs(
                new KeyValuePair("Status",  "Active"),
                new KeyValuePair("Plan",    "Enterprise"),
                new KeyValuePair("Joined",  "March 2022"),
                new KeyValuePair("Region",  "EMEA")
        );
        kvp.setKeyPosition(KeyValuePair.KeyPosition.TOP);

        return new DemoExample("Key Position: TOP", kvp, """
                // Force the key label above the value on every screen width.
                var kvp = new KeyValuePairs(
                    new KeyValuePair("Status", "Active"),
                    new KeyValuePair("Plan",   "Enterprise")
                );
                kvp.setKeyPosition(KeyValuePair.KeyPosition.TOP);
                """);
    }

    private DemoExample twoColumnExample() {
        var kvp = new KeyValuePairs(
                new KeyValuePair("Company",   "Acme Corp"),
                new KeyValuePair("Industry",  "Technology"),
                new KeyValuePair("Country",   "Germany"),
                new KeyValuePair("City",      "Berlin"),
                new KeyValuePair("Employees", "1,200"),
                new KeyValuePair("Founded",   "2005")
        );
        kvp.setColumns(GridColumns.COLUMNS_2);
        kvp.setKeyPosition(KeyValuePair.KeyPosition.TOP);

        return new DemoExample("Two-Column Grid", kvp, """
                // Lay out pairs in a CSS grid with two columns.
                var kvp = new KeyValuePairs(
                    new KeyValuePair("Company",  "Acme Corp"),
                    new KeyValuePair("Industry", "Technology"),
                    new KeyValuePair("Country",  "Germany"),
                    new KeyValuePair("City",     "Berlin"),
                    new KeyValuePair("Employees","1,200"),
                    new KeyValuePair("Founded",  "2005")
                );
                kvp.setColumns(GridColumns.COLUMNS_2);
                kvp.setKeyPosition(KeyValuePair.KeyPosition.TOP);
                """);
    }

    private DemoExample columnSpanExample() {
        var descPair = new KeyValuePair("Description",
                "Acme Corp is a multinational technology company headquartered in Berlin, " +
                "providing enterprise software solutions since 2005.");

        var kvp = new KeyValuePairs(
                new KeyValuePair("Company",  "Acme Corp"),
                new KeyValuePair("Industry", "Technology"),
                new KeyValuePair("Country",  "Germany"),
                new KeyValuePair("City",     "Berlin"),
                descPair
        );
        kvp.setColumns(GridColumns.COLUMNS_2);
        kvp.setKeyPosition(KeyValuePair.KeyPosition.TOP);
        kvp.setColumnSpan(ColumnSpan.COLUMN_SPAN_2, descPair);

        return new DemoExample("Column Span", kvp, """
                // setColumnSpan() stretches a single pair across multiple grid columns.
                KeyValuePair descPair = new KeyValuePair("Description", "Long text...");

                var kvp = new KeyValuePairs(
                    new KeyValuePair("Company", "Acme Corp"),
                    new KeyValuePair("Country", "Germany"),
                    descPair    // spans both columns
                );
                kvp.setColumns(GridColumns.COLUMNS_2);
                kvp.setColumnSpan(ColumnSpan.COLUMN_SPAN_2, descPair);
                """);
    }

    private DemoExample backgroundsExample() {
        var stack = new Div();

        for (Background bg : new Background[]{
                Background.BASE, Background.CONTRAST_5, Background.PRIMARY_10,
                Background.SUCCESS_10, Background.ERROR_10}) {

            var kvp = new KeyValuePairs(
                    new KeyValuePair("Background",  bg.name()),
                    new KeyValuePair("CSS class",   bg.getClassName())
            );
            kvp.setBackground(bg);
            stack.add(kvp);
        }

        return new DemoExample("Background Variants", stack, """
                // setBackground() swaps the CSS colour class on the <dl>.
                var kvp = new KeyValuePairs(
                    new KeyValuePair("Type", "Primary tint")
                );
                kvp.setBackground(Background.PRIMARY_10);

                // Available values (subset):
                // BASE, CONTRAST_5, PRIMARY_10, SUCCESS_10, ERROR_10 …
                """);
    }

    private DemoExample stripesExample() {
        var pairs = new KeyValuePair[]{
                new KeyValuePair("Monday",    "Team standup"),
                new KeyValuePair("Tuesday",   "Design review"),
                new KeyValuePair("Wednesday", "Sprint planning"),
                new KeyValuePair("Thursday",  "Code review"),
                new KeyValuePair("Friday",    "Retrospective")
        };
        var kvp = new KeyValuePairs(pairs);
        kvp.setStripes(true);

        return new DemoExample("Striped Rows", kvp, """
                // setStripes(true) adds theme="stripes" — styled by key-value-pair.css.
                var kvp = new KeyValuePairs(
                    new KeyValuePair("Monday",    "Team standup"),
                    new KeyValuePair("Tuesday",   "Design review"),
                    new KeyValuePair("Wednesday", "Sprint planning")
                );
                kvp.setStripes(true);
                """);
    }

    private DemoExample keyWidthAndBreakpointExample() {
        var stack = new Div();

        // Narrow key (15%)
        var narrow = new KeyValuePairs(
                new KeyValuePair("Role",    "Senior Engineer"),
                new KeyValuePair("Team",    "Platform"),
                new KeyValuePair("Manager", "Alex Jones")
        );
        narrow.setKeyWidth(15, Unit.PERCENTAGE);

        // Wide key (40%)
        var wide = new KeyValuePairs(
                new KeyValuePair("Subscription tier", "Professional"),
                new KeyValuePair("Billing cycle",     "Monthly"),
                new KeyValuePair("Next renewal",      "2026-05-01")
        );
        wide.setKeyWidth(40, Unit.PERCENTAGE);

        // Full-width keys (KeyPosition.TOP equivalent via setKeyWidthFull)
        var full = new KeyValuePairs(
                new KeyValuePair("Notes", "Always full-width regardless of screen size")
        );
        full.setKeyWidthFull();
        full.setBreakpoint(Breakpoint.LARGE);

        stack.add(narrow, wide, full);

        return new DemoExample("Key Width & Breakpoint", stack, """
                // Control key column width — defaults to 25%.
                narrow.setKeyWidth(15, Unit.PERCENTAGE);
                wide.setKeyWidth(40, Unit.PERCENTAGE);

                // setKeyWidthFull() grows the key to fill available width.
                full.setKeyWidthFull();

                // setBreakpoint() sets the screen-size threshold below which
                // the key moves above the value (SIDE position only).
                kvp.setBreakpoint(Breakpoint.LARGE);   // stack below lg breakpoint
                """);
    }
}

