package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ItemListingPageSizeSelector;
import com.holonplatform.vaadin.flow.components.ItemListingPaginationBar;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for {@link ItemListingPageSizeSelector}.
 *
 * <p>Covers:
 * <ol>
 *   <li>Standalone selector — lazy loading, no pagination bar</li>
 *   <li>Combined — page size selector + {@link ItemListingPaginationBar} (true pagination)</li>
 *   <li>With DynamicFilterPanel — filter resets to page 1 automatically</li>
 * </ol>
 */
@PageTitle("PageSizeSelector – Holon Demo")
@Route(value = "page-size-selector", layout = DemoMainLayout.class)
public class PageSizeSelectorDemoView extends Div {

    // ── Demo bean ─────────────────────────────────────────────────────────────

    public static final class Employee {
        private final int    id;
        private final String name;
        private final String department;
        private final String role;
        private final int    salary;

        public Employee(int id, String name, String department, String role, int salary) {
            this.id = id; this.name = name; this.department = department;
            this.role = role; this.salary = salary;
        }

        public int    getId()         { return id; }
        public String getName()       { return name; }
        public String getDepartment() { return department; }
        public String getRole()       { return role; }
        public int    getSalary()     { return salary; }
    }

    // ── In-memory dataset (60 rows, enough to see page-size switching) ─────────

    private static final List<Employee> EMPLOYEES = buildEmployees();

    private static List<Employee> buildEmployees() {
        String[][] depts = {
            {"Engineering", "Software Engineer"}, {"Engineering", "Tech Lead"},
            {"Product",     "Product Manager"},   {"Product",     "UX Designer"},
            {"Finance",     "Analyst"},            {"Finance",     "Controller"},
            {"HR",          "Recruiter"},          {"HR",          "People Partner"},
            {"Sales",       "Account Executive"},  {"Sales",       "Sales Manager"},
        };
        String[] firstNames = {"Alice", "Bob", "Carol", "Dave", "Eve",
                               "Frank", "Grace", "Hank", "Iris", "Jack"};
        String[] lastNames  = {"Smith", "Jones", "Taylor", "Brown", "Wilson",
                               "Davis", "Miller", "Moore", "Thomas", "White"};
        var list = new ArrayList<Employee>(60);
        int id = 1;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 10; j++) {
                String[] dept = depts[j % depts.length];
                list.add(new Employee(
                    id++,
                    firstNames[(i * 3 + j) % firstNames.length] + " " +
                    lastNames[(i * 7 + j) % lastNames.length],
                    dept[0], dept[1],
                    40_000 + (id * 500)
                ));
            }
        }
        return List.copyOf(list);
    }

    // ── Constructor ───────────────────────────────────────────────────────────

    public PageSizeSelectorDemoView() {
        addClassName("app-view");

        var title = new H1("PageSizeSelector");

        var desc = new Paragraph(
                "ItemListingPageSizeSelector renders a 'Show N entries' row that lets the user pick " +
                "how many rows appear per page. For lazy-loading grids it calls Grid.setPageSize() " +
                "directly, adjusting the backend fetch batch size immediately. " +
                "When paired with ItemListingPaginationBar the page numbers are automatically " +
                "recomputed whenever the page size changes.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(standaloneExample());
        examples.add(combinedWithBarExample());
        examples.add(withFilterPanelExample());

        add(title, desc, examples);
    }

    // ── Example builders ──────────────────────────────────────────────────────

    /**
     * 1. Standalone — page-size selector wired to a lazy-loading BeanListing.
     *    No pagination bar: changing the size simply re-fetches the first N rows.
     */
    private DemoExample standaloneExample() {
        var listing = BeanListing.builder(Employee.class, true)
                .header("id",         "#")
                .header("name",       "Name")
                .header("department", "Department")
                .header("role",       "Role")
                .header("salary",     "Salary (€)")
                .build();

        // Lazy callback: the grid passes offset + limit via the Query object.
        listing.setItems(q -> EMPLOYEES.stream()
                .skip(q.getOffset())
                .limit(q.getLimit()));
        // Note: do NOT call setItemCountUnknown() here — the selector sets
        // setItemCountCallback(q -> pageSize) internally to limit visible rows.

        var selector = Components.<Employee, String>pageSizeSelector(listing)
                .withOptions(5, 10, 20)
                .withDefaultSize(5)
                .build();

        var wrapper = new Div(selector, listing.getComponent());

        return new DemoExample("Standalone page-size selector (lazy loading)", wrapper, """
                BeanListing<Employee> listing = BeanListing.builder(Employee.class, true).build();

                // Lazy fetch callback — Grid supplies offset + limit automatically.
                listing.setItems(q -> employees.stream()
                    .skip(q.getOffset())
                    .limit(q.getLimit()));
                // Do NOT call setItemCountUnknown() — the selector calls
                // getLazyDataView().setItemCountCallback(q -> size) internally,
                // which tells the grid to render exactly N rows and stop fetching.

                ItemListingPageSizeSelector<Employee, String> selector =
                        Components.<Employee, String>pageSizeSelector(listing)
                            .withOptions(5, 10, 20)   // dropdown presets
                            .withDefaultSize(5)
                            .build();               // user can also type a custom value

                layout.add(selector, listing.getComponent());
                """);
    }

    /**
     * 2. Combined — page-size selector + ItemListingPaginationBar working together.
     *    Changing the size resets to page 1 and recomputes the total page count.
     */
    private DemoExample combinedWithBarExample() {
        var listing = BeanListing.builder(Employee.class, true)
                .header("id",         "#")
                .header("name",       "Name")
                .header("department", "Department")
                .header("role",       "Role")
                .header("salary",     "Salary (€)")
                .build();

        // Pagination bar — declared first; the selector registers a page-change listener on it.
        var bar = new ItemListingPaginationBar<>(listing)
                .withAutoRefreshOnDataChange(true);

        // withLazyFetch lets the selector OWN the data binding.
        // It wraps the callback with a mutable page offset so navigating pages
        // actually replaces the visible data (no virtual-scroll bleed-through).
        var selector = Components.<Employee, String>pageSizeSelector(listing)
                .withOptions(5, 10, 20, 50)
                .withDefaultSize(10)
                .withPaginationBar(bar)
                .withLazyFetch(
                        q -> EMPLOYEES.stream().skip(q.getOffset()).limit(q.getLimit()),
                        EMPLOYEES::size)   // gives the bar accurate total pages
                .build();

        var wrapper = new Div(selector, listing.getComponent(), bar);

        return new DemoExample("Combined: page-size selector + pagination bar (true pagination)", wrapper, """
                BeanListing<Employee> listing = BeanListing.builder(Employee.class, true).build();

                // Declare the bar BEFORE the selector.
                ItemListingPaginationBar<Employee, String> bar =
                        new ItemListingPaginationBar<>(listing)
                            .withAutoRefreshOnDataChange(true);

                // withLazyFetch takes ownership of listing.setItems().
                // DO NOT call listing.setItems() separately — the selector does it.
                // The selector wraps the callback with a mutable page offset:
                //   wrappedFetch = q -> originalFetch(currentOffset + q.getOffset(), q.getLimit())
                // On page change the bar fires a listener → offset updates → listing.refresh()
                // → grid re-fetches from the new offset → shows exactly N rows, no scrolling past.
                ItemListingPageSizeSelector<Employee, String> selector =
                        Components.<Employee, String>pageSizeSelector(listing)
                            .withOptions(5, 10, 20, 50)
                            .withDefaultSize(10)
                            .withPaginationBar(bar)
                            .withLazyFetch(
                                q -> employeeService.fetch(q.getOffset(), q.getLimit()),
                                () -> employeeService.count())
                            .build();

                layout.add(selector);
                layout.add(listing.getComponent());
                layout.add(bar);
                """);
    }

    /**
     * 3. DynamicFilterPanel + selector + pagination bar.
     *
     * <p>Demonstrates {@code withFilterReset}: when the user applies a filter the
     * selector automatically resets to page 1 and re-queries the count so the bar
     * shows the correct total pages for the filtered result set.</p>
     */
    private DemoExample withFilterPanelExample() {
        var listing = BeanListing.builder(Employee.class, true)
                .header("id",         "#")
                .header("name",       "Name")
                .header("department", "Department")
                .header("role",       "Role")
                .header("salary",     "Salary (€)")
                .build();

        // The filter panel introspects Employee at construction time.
        var filterPanel = DynamicFilterPanel.of(Employee.class);

        var bar = new ItemListingPaginationBar<>(listing)
                .withAutoRefreshOnDataChange(true);

        // countSupplier is a closure that reads filterPanel.toPredicate() at call time,
        // so it always returns the count that matches the CURRENT filter.
        var selector = Components.<Employee, String>pageSizeSelector(listing)
                .withOptions(5, 10, 20)
                .withDefaultSize(5)
                .withPaginationBar(bar)
                .withLazyFetch(
                        // fetch: apply current filter + page offset supplied by the selector
                        q -> EMPLOYEES.stream()
                                .filter(filterPanel.toPredicate())
                                .skip(q.getOffset())
                                .limit(q.getLimit()),
                        // count: re-called on every filter change via withFilterReset
                        () -> (int) EMPLOYEES.stream()
                                .filter(filterPanel.toPredicate())
                                .count())
                .withFilterReset(filterPanel)   // ← key: filter change → page 1 + recount
                .build();

        var wrapper = new Div(filterPanel, selector, listing.getComponent(), bar);

        return new DemoExample(
                "With DynamicFilterPanel: filter resets to page 1 automatically", wrapper, """
                DynamicFilterPanel<Employee> filterPanel = DynamicFilterPanel.of(Employee.class);

                ItemListingPaginationBar<Employee, String> bar =
                        new ItemListingPaginationBar<>(listing);

                // countSupplier must be a CLOSURE that reads the panel's current filter
                // at call time — NOT a value captured once at build time.
                ItemListingPageSizeSelector<Employee, String> selector =
                        Components.<Employee, String>pageSizeSelector(listing)
                            .withOptions(5, 10, 20)
                            .withDefaultSize(5)
                            .withPaginationBar(bar)
                            .withLazyFetch(
                                q -> service.fetch(
                                        q.getOffset(), q.getLimit(),
                                        filterPanel.getQueryFilter().orElse(null)),
                                () -> service.count(
                                        filterPanel.getQueryFilter().orElse(null)))
                            .withFilterReset(filterPanel)  // ← one line wires everything
                            .build();

                // What withFilterReset does on each filter-change event:
                //   1. currentPageOffset = 0          (back to page 1)
                //   2. countSupplier.get()             (re-queries count with new filter)
                //   3. bar.withItemCountEstimate(n)    (bar shows correct total pages)
                //   4. listing.refresh()               (grid re-fetches page 1 filtered)
                //   5. bar.refreshState()              (pagination UI updates)

                layout.add(filterPanel);
                layout.add(selector);
                layout.add(listing.getComponent());
                layout.add(bar);
                """);
    }
}











