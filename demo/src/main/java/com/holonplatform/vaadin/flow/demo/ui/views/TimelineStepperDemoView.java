package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.TimelineStepper;
import com.holonplatform.vaadin.flow.vaadinplus.components.TimelineStepper.AuditEntry;
import com.holonplatform.vaadin.flow.vaadinplus.components.TimelineStepper.Severity;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link TimelineStepper} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Static pre-loaded entries (all Severity variants)</li>
 *   <li>Builder pattern with typed AuditEntry</li>
 *   <li>Entry click listener</li>
 *   <li>Infinite-scroll loading (mock: first page seeded, load-more appends second batch)</li>
 *   <li>Real-time prepend (push new events to the top)</li>
 *   <li>Category badges</li>
 * </ol>
 */
@PageTitle("TimelineStepper – Holon Demo")
@Route(value = "timeline-stepper", layout = DemoMainLayout.class)
public class TimelineStepperDemoView extends Div {

    public TimelineStepperDemoView() {
        addClassName("app-view");

        var title = new H1("TimelineStepper");

        var desc = new Paragraph(
                "A vertical audit-log / event-history timeline backed by the " +
                "<timeline-stepper> web component. Supports static pre-loading, " +
                "infinite-scroll lazy loading, real-time prepend, entry click events, " +
                "and per-entry severity levels and category badges.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(staticEntriesExample());
        examples.add(builderPatternExample());
        examples.add(entryClickExample());
        examples.add(infiniteScrollExample());
        examples.add(realtimePrependExample());
        examples.add(categoryBadgesExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample staticEntriesExample() {
        var timeline = new TimelineStepper();
        timeline.setWidth("100%");
        timeline.setItems(List.of(
                new AuditEntry("1", "2026-04-13 09:00", "System",      "Application started"),
                new AuditEntry("2", "2026-04-13 09:05", "Jane Smith",  "User logged in")
                        .actorRole("Admin"),
                new AuditEntry("3", "2026-04-13 09:12", "Jane Smith",  "Record updated")
                        .detail("Changed status from Draft to Active")
                        .severity(Severity.INFO),
                new AuditEntry("4", "2026-04-13 09:45", "Bob Brown",   "Permission denied")
                        .actorRole("Viewer")
                        .severity(Severity.WARNING),
                new AuditEntry("5", "2026-04-13 10:02", "System",      "Database backup failed")
                        .detail("Connection timeout after 30 s")
                        .severity(Severity.ERROR),
                new AuditEntry("6", "2026-04-13 10:10", "Carol White", "Report exported")
                        .detail("Generated 2,048 rows as CSV")
                        .severity(Severity.SUCCESS)
        ));

        return new DemoExample("Static Pre-loaded Entries", timeline, """
                // Pre-load the first (and only) page of entries at construction.
                var timeline = new TimelineStepper();
                timeline.setItems(List.of(
                    new AuditEntry("1", "2026-04-13 09:00", "System",     "App started"),
                    new AuditEntry("2", "2026-04-13 09:05", "Jane Smith", "Logged in")
                            .actorRole("Admin"),
                    new AuditEntry("3", "2026-04-13 09:12", "Jane Smith", "Record updated")
                            .detail("Changed status to Active")
                            .severity(Severity.INFO),
                    new AuditEntry("4", "2026-04-13 09:45", "Bob Brown",  "Permission denied")
                            .severity(Severity.WARNING),
                    new AuditEntry("5", "2026-04-13 10:02", "System",     "Backup failed")
                            .severity(Severity.ERROR),
                    new AuditEntry("6", "2026-04-13 10:10", "Carol White","Report exported")
                            .severity(Severity.SUCCESS)
                ));
                """);
    }

    private DemoExample builderPatternExample() {
        var entries = List.of(
                new AuditEntry("b1", "2026-04-13 08:00", "Alice", "Sprint started")
                        .severity(Severity.INFO),
                new AuditEntry("b2", "2026-04-13 08:30", "Alice", "PR #42 merged")
                        .detail("feat: add pagination component")
                        .severity(Severity.SUCCESS),
                new AuditEntry("b3", "2026-04-13 09:00", "CI/CD", "Build failed")
                        .detail("Unit tests: 3 failures")
                        .severity(Severity.ERROR)
        );

        var timeline = TimelineStepper.builder()
                .items(entries)
                .hasMore(false)
                .width("100%")
                .build();

        return new DemoExample("Fluent Builder", timeline, """
                // Use TimelineStepper.builder() for a fluent creation pattern.
                var timeline = TimelineStepper.builder()
                    .items(auditService.getFirstPage(20))
                    .hasMore(auditService.hasMore(0))
                    .pageSize(20)
                    .width("100%")
                    .build();
                """);
    }

    private DemoExample entryClickExample() {
        var selectedSpan = new Span("Click an entry…");

        var timeline = TimelineStepper.builder()
                .items(List.of(
                        new AuditEntry("c1", "2026-04-13 10:00", "Jane",   "Invoice #1001 created"),
                        new AuditEntry("c2", "2026-04-13 10:05", "Jane",   "Invoice #1001 sent"),
                        new AuditEntry("c3", "2026-04-13 10:30", "System", "Payment received")
                                .severity(Severity.SUCCESS),
                        new AuditEntry("c4", "2026-04-13 11:00", "Jane",   "Receipt issued")
                                .severity(Severity.SUCCESS)
                ))
                .hasMore(false)
                .width("100%")
                .onEntryClick(e -> selectedSpan.setText("Selected entry id: " + e.getEntryId()))
                .build();

        var wrapper = new Div(timeline, selectedSpan);

        return new DemoExample("Entry Click Listener", wrapper, """
                // onEntryClick() / addEntryClickListener() fires when the user
                // clicks (or keyboard-activates) a row.
                TimelineStepper timeline = TimelineStepper.builder()
                    .items(entries)
                    .onEntryClick(e -> {
                        String id = e.getEntryId();
                        // navigate, open detail panel, etc.
                        detailView.load(auditService.findById(id));
                    })
                    .build();
                """);
    }

    private DemoExample infiniteScrollExample() {
        // Simulated two-page dataset
        List<AuditEntry> page1 = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            page1.add(new AuditEntry("p" + i,
                    "2026-04-13 0" + i + ":00",
                    "User" + i,
                    "Action " + i + " (page 1)"));
        }

        List<AuditEntry> page2 = new ArrayList<>();
        for (int i = 6; i <= 10; i++) {
            page2.add(new AuditEntry("p" + i,
                    "2026-04-12 0" + (i - 5) + ":00",
                    "User" + i,
                    "Action " + i + " (page 2)")
                    .severity(Severity.INFO));
        }

        // Mutable flag to avoid double-loading in the demo
        final boolean[] page2Loaded = {false};

        var timeline = new TimelineStepper();
        timeline.setWidth("100%");
        timeline.setPageSize(5);
        timeline.setItems(page1);
        timeline.setHasMore(true);

        timeline.addLoadMoreListener(e -> {
            if (!page2Loaded[0]) {
                timeline.appendEntries(page2);
                timeline.setHasMore(false);
                page2Loaded[0] = true;
            }
        });

        return new DemoExample("Infinite-Scroll Loading", timeline, """
                // The tl-load-more event fires when the scroll sentinel enters the viewport.
                // Append the next page and update hasMore to control the sentinel.
                TimelineStepper timeline = new TimelineStepper();
                timeline.setPageSize(20);
                timeline.setItems(auditService.getPage(1, 20));
                timeline.setHasMore(auditService.hasMore(1));

                timeline.addLoadMoreListener(e -> {
                    int next = e.getPage();
                    timeline.appendEntries(auditService.getPage(next, e.getPageSize()));
                    timeline.setHasMore(auditService.hasMore(next));
                });
                """);
    }

    private DemoExample realtimePrependExample() {
        var timeline = new TimelineStepper();
        timeline.setWidth("100%");
        timeline.setItems(List.of(
                new AuditEntry("r1", "2026-04-13 12:00", "System", "Monitoring started"),
                new AuditEntry("r2", "2026-04-13 12:01", "System", "Health check OK")
                        .severity(Severity.SUCCESS)
        ));
        timeline.setHasMore(false);

        // Counter for new real-time events
        final int[] counter = {3};

        var pushBtn = new Button("Push new event", VaadinIcon.REFRESH.create(), e -> {
            int n = counter[0]++;
            timeline.prependEntries(List.of(
                    new AuditEntry("r" + n,
                            "2026-04-13 12:0" + n,
                            "System",
                            "Real-time event #" + n)
                            .severity(n % 2 == 0 ? Severity.INFO : Severity.SUCCESS)
            ));
        });

        var wrapper = new Div(pushBtn, timeline);

        return new DemoExample("Real-time Prepend", wrapper, """
                // prependEntries() pushes new events to the TOP of the timeline —
                // ideal for real-time feeds (WebSocket, Server-Sent Events, Polling).
                timeline.prependEntries(List.of(
                    new AuditEntry(UUID.randomUUID().toString(),
                        Instant.now().toString(), "System", "New alert")
                        .severity(Severity.WARNING)
                ));
                """);
    }

    private DemoExample categoryBadgesExample() {
        var timeline = TimelineStepper.builder()
                .items(List.of(
                        new AuditEntry("cat1", "2026-04-13 08:00", "Jane",   "Login")
                                .category("Auth")
                                .severity(Severity.INFO),
                        new AuditEntry("cat2", "2026-04-13 08:10", "Jane",   "Subscription upgraded")
                                .category("Billing")
                                .severity(Severity.SUCCESS),
                        new AuditEntry("cat3", "2026-04-13 08:20", "System", "Storage limit reached")
                                .category("Storage")
                                .severity(Severity.WARNING),
                        new AuditEntry("cat4", "2026-04-13 08:30", "Jane",   "Password changed")
                                .category("Auth")
                                .severity(Severity.INFO),
                        new AuditEntry("cat5", "2026-04-13 08:40", "System", "Backup restored")
                                .category("System")
                                .severity(Severity.SUCCESS)
                ))
                .hasMore(false)
                .width("100%")
                .build();

        return new DemoExample("Category Badges", timeline, """
                // .category() adds a badge label to the entry row.
                // Useful for filtering display and visual grouping in audit logs.
                new AuditEntry("1", "2026-04-13 08:00", "Jane", "Login")
                    .category("Auth")
                    .severity(Severity.INFO);

                new AuditEntry("2", "2026-04-13 08:10", "Jane", "Subscription upgraded")
                    .category("Billing")
                    .severity(Severity.SUCCESS);
                """);
    }
}


