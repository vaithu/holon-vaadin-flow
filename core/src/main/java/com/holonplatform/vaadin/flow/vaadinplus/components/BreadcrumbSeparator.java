package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.components.Components;
import com.iyensoft.vaadin.flow.enums.MaterialSymbol;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;

/**
 * Visual separator placed <em>between</em> breadcrumb items in a {@link Breadcrumb}.
 *
 * <p>Renders as:</p>
 * <pre>
 * &lt;li class="breadcrumb__separator" aria-hidden="true" role="presentation"&gt;
 *   &lt;span class="breadcrumb__separator-icon"&gt;/&lt;/span&gt;  &lt;!-- default --&gt;
 * &lt;/li&gt;
 * </pre>
 *
 * <p>This is the shadcn/ui {@code BreadcrumbSeparator} equivalent. It is intentionally a separate
 * {@code <li>} element (not a CSS {@code ::before} trick) so you can:</p>
 * <ul>
 *   <li>Swap it with any {@link Component} or {@link MaterialSymbol} icon.</li>
 *   <li>Conditionally omit the last separator (before {@link BreadcrumbPage}).</li>
 * </ul>
 *
 * <p>Usage (manual):</p>
 * <pre>{@code
 * breadcrumb.content(
 *     new BreadcrumbItem("Home",       HomeView.class),
 *     new BreadcrumbSeparator(),
 *     new BreadcrumbItem("Components", ComponentsView.class),
 *     new BreadcrumbSeparator(MaterialSymbol.CHEVRON_RIGHT),
 *     new BreadcrumbPage("Breadcrumb")
 * );
 * }</pre>
 *
 * <p>Usage (automatic via {@code Breadcrumb.addWithSeparators}):</p>
 * <pre>{@code
 * breadcrumb.addWithSeparators(
 *     new BreadcrumbItem("Home",       HomeView.class),
 *     new BreadcrumbItem("Components", ComponentsView.class),
 *     new BreadcrumbPage("Breadcrumb")
 * );
 * }</pre>
 *
 * @see Breadcrumb
 * @see BreadcrumbItem
 * @see BreadcrumbPage
 */
public class BreadcrumbSeparator extends ListItem {

    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates a separator with the default "/" glyph.
     */
    public BreadcrumbSeparator() {
        this((Component) null);
    }

    /**
     * Creates a separator using a {@link MaterialSymbol} icon.
     *
     * @param symbol the icon to render as the separator (not null)
     */
    public BreadcrumbSeparator(MaterialSymbol symbol) {
        this(symbol != null ? symbol.create("breadcrumb__separator-icon") : null);
    }

    /**
     * Creates a separator with a custom component as content (e.g. any Vaadin icon).
     * Pass {@code null} to use the default "/" glyph.
     *
     * @param customContent the separator content, or {@code null} for the default
     */
    public BreadcrumbSeparator(Component customContent) {
        addClassName("breadcrumb__separator");
        getElement().setAttribute("aria-hidden", "true");
        getElement().setAttribute("role", "presentation");

        if (customContent != null) {
            add(customContent);
        } else {
            Span slash = Components.span().text("/").styleName("breadcrumb__separator-icon").build();
            add(slash);
        }
    }
}


