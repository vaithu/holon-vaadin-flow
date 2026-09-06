package com.holonplatform.vaadin.flow.vaadinplus.components;

import java.io.Serial;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.OrderedList;

import java.util.function.Supplier;

/**
 * Accessible breadcrumb navigation component inspired by shadcn/ui {@code Breadcrumb}.
 *
 * <p>Renders as:</p>
 * <pre>
 * &lt;nav class="breadcrumb" aria-label="Breadcrumb"&gt;
 *   &lt;ol class="breadcrumb__list"&gt;
 *     &lt;li class="breadcrumb__item"&gt;&lt;a class="breadcrumb__link" href="/"&gt;Home&lt;/a&gt;&lt;/li&gt;
 *     &lt;li class="breadcrumb__separator" aria-hidden="true"&gt;/&lt;/li&gt;
 *     &lt;li class="breadcrumb__item"&gt;&lt;a class="breadcrumb__link" href="/components"&gt;Components&lt;/a&gt;&lt;/li&gt;
 *     &lt;li class="breadcrumb__separator" aria-hidden="true"&gt;/&lt;/li&gt;
 *     &lt;li class="breadcrumb__item"&gt;&lt;span class="breadcrumb__page" aria-current="page"&gt;Breadcrumb&lt;/span&gt;&lt;/li&gt;
 *   &lt;/ol&gt;
 * &lt;/nav&gt;
 * </pre>
 *
 * <p>You can compose items manually (full control over separators) or use the convenience method
 * {@link #addWithSeparators(ListItem...)} which inserts {@link BreadcrumbSeparator}s automatically
 * between items.</p>
 *
 * <p><strong>Manual usage:</strong></p>
 * <pre>{@code
 * Breadcrumb bc = new Breadcrumb(
 *     new BreadcrumbItem("Home",       HomeView.class),
 *     new BreadcrumbSeparator(),
 *     new BreadcrumbItem("Components", ComponentsView.class),
 *     new BreadcrumbSeparator(),
 *     new BreadcrumbPage("Breadcrumb")
 * );
 * }</pre>
 *
 * <p><strong>Automatic separators:</strong></p>
 * <pre>{@code
 * Breadcrumb bc = new Breadcrumb();
 * bc.addWithSeparators(
 *     new BreadcrumbItem("Home",       HomeView.class),
 *     new BreadcrumbItem("Components", ComponentsView.class),
 *     new BreadcrumbPage("Breadcrumb")
 * );
 * }</pre>
 *
 * <p><strong>Custom separator icon:</strong></p>
 * <pre>{@code
 * Breadcrumb bc = new Breadcrumb();
 * bc.setSeparatorSupplier(() -> new BreadcrumbSeparator(VaadinIcon.CHEVRON_RIGHT));
 * bc.addWithSeparators(
 *     new BreadcrumbItem("Home",       HomeView.class),
 *     new BreadcrumbPage("Dashboard")
 * );
 * }</pre>
 *
 * <p><strong>With ellipsis for long trails:</strong></p>
 * <pre>{@code
 * Breadcrumb bc = new Breadcrumb();
 * bc.addWithSeparators(
 *     new BreadcrumbItem("Home",         HomeView.class),
 *     new BreadcrumbEllipsis(),
 *     new BreadcrumbItem("Components",   ComponentsView.class),
 *     new BreadcrumbPage("Breadcrumb")
 * );
 * }</pre>
 *
 * @see BreadcrumbItem
 * @see BreadcrumbPage
 * @see BreadcrumbSeparator
 * @see BreadcrumbEllipsis
 */
@StyleSheet("context://breadcrumb.css")
public class Breadcrumb extends Nav {

    @Serial
    private static final long serialVersionUID = 1L;

    private final OrderedList list;

    /** Factory used by {@link #addWithSeparators}; defaults to {@link BreadcrumbSeparator#BreadcrumbSeparator()}. */
    private Supplier<BreadcrumbSeparator> separatorSupplier = BreadcrumbSeparator::new;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates an empty breadcrumb.
     */
    public Breadcrumb() {
        addClassName("breadcrumb");
        getElement().setAttribute("aria-label",
                LocalizationProvider.localize("Breadcrumb", "breadcrumb.aria_label"));

        this.list = new OrderedList();
        this.list.addClassName("breadcrumb__list");
        add(this.list);
    }

    /**
     * Creates a breadcrumb pre-populated with the given items.
     * Items are added as-is  separators must be included explicitly.
     *
     * @param items the items to content (may be {@link BreadcrumbItem}, {@link BreadcrumbSeparator},
     *              {@link BreadcrumbPage}, {@link BreadcrumbEllipsis}, or any {@link ListItem})
     */
    public Breadcrumb(ListItem... items) {
        this();
        this.list.add(items);
    }

    // -----------------------------------------------------------------------
    // Manual content / remove
    // -----------------------------------------------------------------------

    /**
     * Adds one or more items to the end of the breadcrumb list.
     * Accepts any {@link ListItem} sub-type:
     * {@link BreadcrumbItem}, {@link BreadcrumbPage}, {@link BreadcrumbSeparator},
     * {@link BreadcrumbEllipsis}.
     *
     * @param items the items to content (not null)
     */
    public void add(ListItem... items) {
        this.list.add(items);
    }

    /**
     * Removes the given items from the breadcrumb list.
     *
     * @param items the items to remove (not null)
     */
    public void remove(ListItem... items) {
        this.list.remove(items);
    }

    /**
     * Removes all items from the breadcrumb list.
     */
    @Override
    public void removeAll() {
        this.list.removeAll();
    }

    // -----------------------------------------------------------------------
    // Convenience  automatic separators
    // -----------------------------------------------------------------------

    /**
     * Configures the separator factory used by {@link #addWithSeparators(ListItem...)}.
     *
     * <p>Default: {@code () -> new BreadcrumbSeparator()} (renders "/").</p>
     *
     * <p>Example  chevron separator:</p>
     * <pre>{@code
     * breadcrumb.setSeparatorSupplier(() -> new BreadcrumbSeparator(VaadinIcon.CHEVRON_RIGHT));
     * }</pre>
     *
     * @param separatorSupplier the factory that creates each separator (not null)
     */
    public void setSeparatorSupplier(Supplier<BreadcrumbSeparator> separatorSupplier) {
        if (separatorSupplier == null) throw new IllegalArgumentException("separatorSupplier must not be null");
        this.separatorSupplier = separatorSupplier;
    }

    /**
     * Adds all given items, automatically inserting a {@link BreadcrumbSeparator} between consecutive items.
     *
     * <p>A separator is inserted between <em>every</em> pair of adjacent items, including before
     * and after a {@link BreadcrumbEllipsis}. The separator factory configured via
     * {@link #setSeparatorSupplier(Supplier)} is used to create each separator instance.</p>
     *
     * <p>Items are appended <em>after</em> any items already in the list. If the list already
     * contains items a leading separator is prepended so the new items connect cleanly.</p>
     *
     * @param items the items to content (not null, at least one)
     */
    public void addWithSeparators(ListItem... items) {
        if (items == null || items.length == 0) return;

        boolean listHasExistingItems = this.list.getElement().getChildCount() > 0;

        for (int i = 0; i < items.length; i++) {
            if (i > 0 || listHasExistingItems) {
                this.list.add(separatorSupplier.get());
            }
            this.list.add(items[i]);
        }
    }

    /**
     * Clears the breadcrumb list and repopulates it from the given items,
     * automatically inserting separators between consecutive items.
     *
     * @param items the items to set (not null, at least one)
     */
    public void setWithSeparators(ListItem... items) {
        this.list.removeAll();
        addWithSeparators(items);
    }
}
