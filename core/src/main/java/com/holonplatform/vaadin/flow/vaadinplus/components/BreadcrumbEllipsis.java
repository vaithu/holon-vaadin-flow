package com.holonplatform.vaadin.flow.vaadinplus.components;

import java.io.Serial;
import com.vaadin.flow.component.ClickEvent;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;

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
 *     &lt;span class="breadcrumb__ellipsis-icon"&gt;â€¢â€¢â€¢&lt;/span&gt;  &lt;!-- default --&gt;
 *   &lt;/span&gt;
 * &lt;/li&gt;
 * </pre>
 *
 * <p>This is the shadcn/ui {@code BreadcrumbEllipsis} equivalent. Use it when the breadcrumb trail
 * is too long for the available space. Typically you show the first and last items and collapse
 * the middle ones behind this component, which can reveal them via a dropdown or tooltip on click.</p>
 *
 * <p>Usage (static "â€¦" indicator):</p>
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

    @Serial
    private static final long serialVersionUID = 1L;

    private final Span ellipsis;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates an ellipsis with the default "â€¢â€¢â€¢" indicator.
     */
    public BreadcrumbEllipsis() {
        this((Component) null);
    }

    /**
     * Creates an ellipsis using a {@link VaadinIcon} icon.
     *
     * @param icon the icon to use as the ellipsis indicator (not null)
     */
    public BreadcrumbEllipsis(VaadinIcon icon) {
        addClassName("breadcrumb__item");
        this.ellipsis = Components.span().styleName("breadcrumb__ellipsis").build();
        this.ellipsis.getElement().setAttribute("aria-hidden", "true");
        this.ellipsis.getElement().setAttribute("aria-label",
                LocalizationProvider.localize("More", "breadcrumb.ellipsis.aria_label"));
        this.ellipsis.getElement().setAttribute("role", "presentation");
        if (icon != null) {
            var ic = icon.create();
            ic.addClassName("breadcrumb__ellipsis-icon");
            this.ellipsis.add(ic);
        } else {
            this.ellipsis.add(Components.span().text("â€¢â€¢â€¢").styleName("breadcrumb__ellipsis-icon").build());
        }
        add(this.ellipsis);
    }

    /**
     * Creates an ellipsis with a custom component as the indicator.
     * Pass {@code null} to use the default "â€¢â€¢â€¢" glyph.
     *
     * @param customIcon the ellipsis indicator component, or {@code null} for the default
     */
    public BreadcrumbEllipsis(Component customIcon) {
        addClassName("breadcrumb__item");

        this.ellipsis = Components.span().styleName("breadcrumb__ellipsis").build();
        this.ellipsis.getElement().setAttribute("aria-hidden", "true");
        this.ellipsis.getElement().setAttribute("aria-label",
                LocalizationProvider.localize("More", "breadcrumb.ellipsis.aria_label"));
        this.ellipsis.getElement().setAttribute("role", "presentation");

        if (customIcon != null) {
            this.ellipsis.add(customIcon);
        } else {
            Span dots = Components.span().text("â€¢â€¢â€¢").styleName("breadcrumb__ellipsis-icon").build();
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


