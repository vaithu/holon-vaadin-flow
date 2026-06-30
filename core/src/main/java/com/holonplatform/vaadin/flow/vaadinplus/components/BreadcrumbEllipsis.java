package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.iyensoft.vaadin.flow.enums.MaterialSymbol;
import com.vaadin.flow.component.ClickEvent;
import com.holonplatform.vaadin.flow.components.Components;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;

/**
 * Ellipsis placeholder used to collapse the middle section of a long {@link Breadcrumb} trail.
 *
 * <p>Renders as:</p>
 * <pre>
 * &lt;li class="breadcrumb__item"&gt;
 *   &lt;span class="breadcrumb__ellipsis"
 *         aria-hidden="true"
 *         aria-label="More"
 *         role="presentation"&gt;
 *     &lt;span class="breadcrumb__ellipsis-icon"&gt;•••&lt;/span&gt;  &lt;!-- default --&gt;
 *   &lt;/span&gt;
 * &lt;/li&gt;
 * </pre>
 *
 * <p>This is the shadcn/ui {@code BreadcrumbEllipsis} equivalent. Use it when the breadcrumb trail
 * is too long for the available space. Typically you show the first and last items and collapse
 * the middle ones behind this component, which can reveal them via a dropdown or tooltip on click.</p>
 *
 * <p>Usage (static "…" indicator):</p>
 * <pre>{@code
 * breadcrumb.addWithSeparators(
 *     new BreadcrumbItem("Home",         HomeView.class),
 *     new BreadcrumbEllipsis(),
 *     new BreadcrumbItem("Components",   ComponentsView.class),
 *     new BreadcrumbPage("Breadcrumb")
 * );
 * }</pre>
 *
 * <p>Usage (clickable to reveal hidden items):</p>
 * <pre>{@code
 * BreadcrumbEllipsis ellipsis = new BreadcrumbEllipsis();
 * ellipsis.addClickListener(e -> showHiddenItemsContextMenu(e));
 * }</pre>
 *
 * @see Breadcrumb
 * @see BreadcrumbItem
 * @see BreadcrumbPage
 * @see BreadcrumbSeparator
 */
public class BreadcrumbEllipsis extends ListItem {

    private static final long serialVersionUID = 1L;

    private final Span ellipsis;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates an ellipsis with the default "•••" indicator.
     */
    public BreadcrumbEllipsis() {
        this((Component) null);
    }

    /**
     * Creates an ellipsis using a {@link MaterialSymbol} icon.
     *
     * @param symbol the icon to use as the ellipsis indicator (not null)
     */
    public BreadcrumbEllipsis(MaterialSymbol symbol) {
        this(symbol != null ? symbol.create("breadcrumb__ellipsis-icon") : null);
    }

    /**
     * Creates an ellipsis with a custom component as the indicator.
     * Pass {@code null} to use the default "•••" glyph.
     *
     * @param customIcon the ellipsis indicator component, or {@code null} for the default
     */
    public BreadcrumbEllipsis(Component customIcon) {
        addClassName("breadcrumb__item");

        this.ellipsis = Components.span().styleName("breadcrumb__ellipsis").build();
        this.ellipsis.getElement().setAttribute("aria-hidden", "true");
        this.ellipsis.getElement().setAttribute("aria-label", "More");
        this.ellipsis.getElement().setAttribute("role", "presentation");

        if (customIcon != null) {
            this.ellipsis.add(customIcon);
        } else {
            Span dots = Components.span().text("•••").styleName("breadcrumb__ellipsis-icon").build();
            this.ellipsis.add(dots);
        }

        add(this.ellipsis);
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Adds a click listener on the ellipsis indicator span.
     * Use this to show a dropdown / popover that reveals the hidden breadcrumb items.
     *
     * <p>The method is named {@code addEllipsisClickListener} (not {@code addClickListener}) to
     * avoid an erasure clash with the inherited {@code ClickNotifier.addClickListener}.</p>
     *
     * @param listener the click listener (not null)
     */
    public void addEllipsisClickListener(ComponentEventListener<ClickEvent<Span>> listener) {
        // breadcrumb__ellipsis--clickable sets cursor:pointer in breadcrumb.css
        this.ellipsis.addClassName("breadcrumb__ellipsis--clickable");
        this.ellipsis.addClickListener(listener);
    }
}



