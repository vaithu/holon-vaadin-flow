package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link Pagination} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Basic pagination (pages 1–5, page 3 active)</li>
 *   <li>With ellipsis for large datasets</li>
 *   <li>First page active (disabled Previous)</li>
 *   <li>Last page active (disabled Next)</li>
 *   <li>Interactive stateful pagination bar</li>
 * </ol>
 */
@PageTitle("Pagination – Holon Demo")
@Route(value = "pagination", layout = DemoMainLayout.class)
public class PaginationDemoView extends Div {

    public PaginationDemoView() {
        addClassName("app-view");

        var title = new H1("Pagination");

        var desc = new Paragraph(
                "Accessible pagination control rendered as a <nav> landmark. " +
                "Composed from PaginationContent (<ul>), PaginationItem (<li>), " +
                "PaginationLink (numbered page button), PaginationPrevious, PaginationNext, " +
                "and PaginationEllipsis (gap indicator). " +
                "All click handling is done via Vaadin's ClickNotifier — no routing is enforced.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(withEllipsisExample());
        examples.add(firstPageExample());
        examples.add(lastPageExample());
        examples.add(interactiveExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample basicExample() {
        return new DemoExample("Basic (5 pages, page 3 active)",
                buildBar(5, 3, false, false),
                """
                Pagination pg = new Pagination();
                PaginationContent content = pg.getContent();

                PaginationPrevious prev = new PaginationPrevious();
                content.add(new PaginationItem(prev));

                // Number links — pass (page, isActive)
                for (int p = 1; p <= 5; p++) {
                    PaginationLink link = new PaginationLink(p, p == 3);
                    link.addClickListener(e -> loadPage(link.getPage()));
                    content.add(new PaginationItem(link));
                }

                PaginationNext next = new PaginationNext();
                content.add(new PaginationItem(next));
                """);
    }

    private DemoExample withEllipsisExample() {
        var pg = new Pagination();
        var c = pg.getContent();

        var prev = new PaginationPrevious();
        c.add(new PaginationItem(prev));
        c.add(new PaginationItem(new PaginationLink(1, false)));
        c.add(new PaginationItem(new PaginationLink(2, false)));
        c.add(new PaginationItem(new PaginationEllipsis()));
        c.add(new PaginationItem(new PaginationLink(7, true)));
        c.add(new PaginationItem(new PaginationLink(8, false)));
        c.add(new PaginationItem(new PaginationLink(9, false)));
        c.add(new PaginationItem(new PaginationEllipsis()));
        c.add(new PaginationItem(new PaginationLink(15, false)));
        var next = new PaginationNext();
        c.add(new PaginationItem(next));

        return new DemoExample("With Ellipsis (large dataset)",
                pg,
                """
                // Use PaginationEllipsis to hide middle pages.
                content.add(new PaginationItem(new PaginationLink(1, false)));
                content.add(new PaginationItem(new PaginationLink(2, false)));
                content.add(new PaginationItem(new PaginationEllipsis()));    // "…"
                content.add(new PaginationItem(new PaginationLink(7, true))); // current
                content.add(new PaginationItem(new PaginationLink(8, false)));
                content.add(new PaginationItem(new PaginationEllipsis()));
                content.add(new PaginationItem(new PaginationLink(15, false)));
                """);
    }

    private DemoExample firstPageExample() {
        return new DemoExample("First Page Active (Previous disabled)",
                buildBar(5, 1, true, false),
                """
                // Disable the Previous control when on page 1.
                PaginationPrevious prev = new PaginationPrevious();
                prev.setDisabled(true);
                content.add(new PaginationItem(prev));
                """);
    }

    private DemoExample lastPageExample() {
        return new DemoExample("Last Page Active (Next disabled)",
                buildBar(5, 5, false, true),
                """
                // Disable the Next control when on the last page.
                PaginationNext next = new PaginationNext();
                next.setDisabled(true);
                content.add(new PaginationItem(next));
                """);
    }

    private DemoExample interactiveExample() {
        final int totalPages = 7;
        final int[] current = {3};

        var pageLabel = new Span("Page " + current[0] + " of " + totalPages);

        var holder = new Div();

        Runnable rebuild = () -> {
            holder.removeAll();
            holder.add(buildInteractiveBar(totalPages, current[0], current, pageLabel));
            pageLabel.setText("Page " + current[0] + " of " + totalPages);
        };

        rebuild.run();
        holder.add(pageLabel);

        return new DemoExample("Interactive Stateful Bar", holder,
                """
                // Rebuild the bar on each page change — the simplest stateful approach.
                private Pagination buildBar(int total, int current,
                                            int[] currentRef, Span label) {
                    Pagination pg = new Pagination();
                    PaginationContent c = pg.getContent();

                    PaginationPrevious prev = new PaginationPrevious();
                    prev.setDisabled(current == 1);
                    prev.addClickListener(e -> {
                        if (currentRef[0] > 1) { currentRef[0]--; rebuild.run(); }
                    });
                    c.add(new PaginationItem(prev));

                    for (int p = 1; p <= total; p++) {
                        final int page = p;
                        PaginationLink link = new PaginationLink(page, page == current);
                        link.addClickListener(e -> { currentRef[0] = page; rebuild.run(); });
                        c.add(new PaginationItem(link));
                    }

                    PaginationNext next = new PaginationNext();
                    next.setDisabled(current == total);
                    next.addClickListener(e -> {
                        if (currentRef[0] < total) { currentRef[0]++; rebuild.run(); }
                    });
                    c.add(new PaginationItem(next));
                    return pg;
                }
                """);
    }

    // ── Utilities ────────────────────────────────────────────────────────────

    /** Builds a simple static bar. */
    private static Pagination buildBar(int total, int activePage,
                                       boolean prevDisabled, boolean nextDisabled) {
        var pg = new Pagination();
        var c  = pg.getContent();

        var prev = new PaginationPrevious();
        prev.setDisabled(prevDisabled);
        c.add(new PaginationItem(prev));

        for (int p = 1; p <= total; p++) {
            c.add(new PaginationItem(new PaginationLink(p, p == activePage)));
        }

        var next = new PaginationNext();
        next.setDisabled(nextDisabled);
        c.add(new PaginationItem(next));

        return pg;
    }

    /** Builds a click-wired bar for the interactive demo. */
    private Pagination buildInteractiveBar(int total, int current,
                                           int[] currentRef, Span label) {
        var pg = new Pagination();
        var c  = pg.getContent();

        var prev = new PaginationPrevious();
        prev.setDisabled(current == 1);
        prev.addClickListener(e -> {
            if (currentRef[0] > 1) {
                currentRef[0]--;
                label.setText("Page " + currentRef[0] + " of " + total);
                rebuild(pg.getContent(), total, currentRef, label);
            }
        });
        c.add(new PaginationItem(prev));

        for (int p = 1; p <= total; p++) {
            final int page = p;
            var link = new PaginationLink(page, page == current);
            link.addClickListener(e -> {
                currentRef[0] = page;
                label.setText("Page " + page + " of " + total);
                rebuild(c, total, currentRef, label);
            });
            c.add(new PaginationItem(link));
        }

        var next = new PaginationNext();
        next.setDisabled(current == total);
        next.addClickListener(e -> {
            if (currentRef[0] < total) {
                currentRef[0]++;
                label.setText("Page " + currentRef[0] + " of " + total);
                rebuild(c, total, currentRef, label);
            }
        });
        c.add(new PaginationItem(next));

        return pg;
    }

    private void rebuild(PaginationContent c, int total, int[] ref, Span label) {
        c.removeAll();

        var prev = new PaginationPrevious();
        prev.setDisabled(ref[0] == 1);
        prev.addClickListener(e -> {
            if (ref[0] > 1) { ref[0]--; label.setText("Page " + ref[0] + " of " + total); rebuild(c, total, ref, label); }
        });
        c.add(new PaginationItem(prev));

        for (int p = 1; p <= total; p++) {
            final int page = p;
            var link = new PaginationLink(page, page == ref[0]);
            link.addClickListener(e -> { ref[0] = page; label.setText("Page " + page + " of " + total); rebuild(c, total, ref, label); });
            c.add(new PaginationItem(link));
        }

        var next = new PaginationNext();
        next.setDisabled(ref[0] == total);
        next.addClickListener(e -> {
            if (ref[0] < total) { ref[0]++; label.setText("Page " + ref[0] + " of " + total); rebuild(c, total, ref, label); }
        });
        c.add(new PaginationItem(next));
    }
}

