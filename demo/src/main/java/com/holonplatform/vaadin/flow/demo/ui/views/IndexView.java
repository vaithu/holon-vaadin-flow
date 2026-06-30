package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import java.util.List;

/**
 * Landing page – shows a card grid of all available demo components,
 * alphabetically sorted.
 */
@PageTitle("Holon Component Demo")
@Route(value = "", layout = DemoMainLayout.class)
public class IndexView extends Div {

    public IndexView() {
        addClassName("app-view");

        var title = new H1("Holon Vaadin Flow Components");

        var desc = new Paragraph(
                "A live showcase of all vaadinplus components available in the core module. " +
                        "Select a component from the sidebar — or pick a card below — to explore its " +
                        "variants and copy-ready usage examples.");

        var grid = new Div();

        record Entry(String name, String description, Class<? extends Component> view) {
        }

        List<Entry> entries = List.of(
                new Entry("Alert",
                        "Contextual feedback messages with five severity variants: Default, Destructive, Warning, Success, and Info.",
                        AlertDemoView.class),
                new Entry("BeanListing",
                        "Server-side listing for bean types with auto-columns, FilterInput / FilterInputGroup / FilterInputForm live filtering, and Pagination support.",
                        BeanListingDemoView.class),
                new Entry("ButtonGroup",
                        "Merges adjacent buttons into a single cohesive control — collapsed shared borders, outer-only corner radius, horizontal and vertical orientations.",
                        ButtonGroupDemoView.class),
                new Entry("AppBar",
                        "Responsive 3-slot page header (start / middle / end) that extends <header>.",
                        AppBarDemoView.class),
                new Entry("Breadcrumb",
                        "Accessible navigation trail with automatic separator insertion and optional ellipsis.",
                        BreadcrumbDemoView.class),
                new Entry("Calendar",
                        "Full-featured calendar powered by Calendar.js — month/week/day/agenda views, drag-and-drop event editing, dark theme, and Java listeners for all CRUD actions.",
                        CalendarDemoView.class),
                new Entry("ComponentView",
                        "Semantic <main> page section with addH2() and addPreview() helpers for documentation layouts.",
                        ComponentViewDemoView.class),
                new Entry("Empty",
                        "Empty-state placeholder with icon/illustration, title, description, and action slots.",
                        EmptyDemoView.class),
                new Entry("FilterPanel",
                        "Row-based dynamic filter builder: property selector, type-aware operator (String/Number/Date/Enum/Boolean), value input, and Apply/Clear actions.",
                        FilterPanelDemoView.class),
                new Entry("FlowStepper",
                        "Multi-step progress indicator (Shadow DOM web component) — horizontal/vertical, three variants, server-driven navigation.",
                        FlowStepperDemoView.class),
                new Entry("GridHeader",
                        "Header with selection-aware context-action toggling wired directly to a Vaadin Grid.",
                        GridHeaderDemoView.class),
                new Entry("Highlight",
                        "KPI / metric card with prefix, heading, value, details, and suffix slots.",
                        HighlightDemoView.class),
                new Entry("KanbanBoard",
                        "Drag-and-drop Kanban board with configurable columns, card renderer, move handler/validation, card actions, column actions, comments, and move audit trail.",
                        KanbanBoardDemoView.class),
                new Entry("LineItemGrid",
                        "Keyboard-centric inline spreadsheet for document line items (invoices, POs, quotes). Enter moves down same column like Excel. Responsive: card list on mobile with Sheet edit panel.",
                        LineItemGridDemoView.class),
                new Entry("InputGroup",
                        "Horizontal flex container that merges inputs, buttons, and text addons into a single visually unified control with seamless border handling.",
                        InputGroupDemoView.class),
                new Entry("InputOTP",
                        "One-Time Password input — auto-advancing slots, digit/alphanumeric patterns, separator, onComplete callback.",
                        InputOTPDemoView.class),
                new Entry("KeyValuePairs",
                        "Semantic <dl> list for structured property–value display with CSS grid columns, column spans, key position (SIDE / TOP), background colours, and striped rows.",
                        KeyValuePairsDemoView.class),
                new Entry("LazyComponent",
                        "Defers child component rendering until first attachment to the DOM — ideal for tabs, dialogs, and off-screen panels to minimise initial render cost.",
                        LazyComponentDemoView.class),
                new Entry("Pagination",
                        "Accessible pagination bar composed from PaginationContent, PaginationLink, PaginationPrevious/Next, and PaginationEllipsis.",
                        PaginationDemoView.class),
                new Entry("Preview",
                        "Column-flex content wrapper for showcasing live components inside documentation and demo layouts.",
                        PreviewDemoView.class),
                new Entry("Separator",
                        "Horizontal or vertical visual divider — meaningful (role=separator) or decorative (aria-hidden).",
                        SeparatorDemoView.class),
                new Entry("Sheet",
                        "Mobile-first slide-in panel (BOTTOM/LEFT/RIGHT) with title, description, lazy-content loading, and History API back-button.",
                        SheetDemoView.class),
                new Entry("Tag",
                        "Compact label/badge chip with optional icon or avatar prefix and Color.Text colour variants.",
                        TagDemoView.class),
                new Entry("StatusBadge",
                        "Non-interactive status pill with a coloured dot prefix and semantic tinted background. Use for document states, process stages, and metadata labels.",
                        StatusBadgeDemoView.class),
                new Entry("Chip",
                        "Interactive pill-shaped filter chip with optional count badge. ChipGroup manages mutually exclusive toggle chips and fires SelectionEvent on change.",
                        ChipDemoView.class),
                new Entry("ResponsiveDiv",
                        "CSS-class-based responsive container (flex/grid) for layout adjustments.",
                        ResponsiveDivDemoView.class),
                new Entry("ResponsiveDiv slotOnce",
                        "Lazy viewport-aware DOM population: only the component matching the current viewport is built and added — the other supplier is never called. Zero hidden nodes.",
                        ResponsiveDivSlotDemoView.class),
                new Entry("TimelineStepper",
                        "Vertical audit-log / event-history timeline with static pre-loading, infinite-scroll lazy loading, real-time prepend, entry click events, and per-entry severity levels.",
                        TimelineStepperDemoView.class),
                new Entry("BulkItemPickerDialog",
                        "Two-panel bulk-content dialog: left panel for search/scan with live item list, right panel for selected items with per-item quantity steppers and running total. Confirm callback receives BulkPickerEntry list.",
                        BulkItemPickerDemoView.class),
                new Entry("TransferList",
                        "Dual-panel shuttle component for moving items between Available and Selected panels. Single and all-items transfer in both directions. CSS-grid layout with mobile stack.",
                        TransferListDemoView.class)
        );

        for (var entry : entries) {
            var card = buildCard(entry.name(), entry.description(), entry.view());
            grid.add(card);
        }

        add(title, desc, grid);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private static Div buildCard(String name, String description, Class<? extends Component> view) {
        // RouterLink with empty text — children are added below and become the visible content
        var link = new RouterLink("", view);

        var cardTitle = new H3(name);

        var cardDesc = new Paragraph(description);

        link.add(cardTitle, cardDesc);

        var card = new Div(link);
        card.addClassName("app-card");
        return card;
    }
}


