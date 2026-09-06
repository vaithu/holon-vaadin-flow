package com.holonplatform.vaadin.flow.vaadinplus.components;

import java.io.Serial;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;

/**
 * The current / active page crumb in a {@link Breadcrumb}  intentionally <strong>not</strong> a link.
 *
 * <p>Renders as:</p>
 * <pre>
 * &lt;li class="breadcrumb__item"&gt;
 *   &lt;span class="breadcrumb__page"
 *         aria-disabled="true"
 *         aria-current="page"&gt;â€¦&lt;/span&gt;
 * &lt;/li&gt;
 * </pre>
 *
 * <p>This is the shadcn/ui {@code BreadcrumbPage} equivalent. It marks the last item in the trail
 * that represents the current page. Unlike {@link BreadcrumbItem}, it carries no href and receives
 * stronger text styling.</p>
 *
 * <p>Supports plain text, Holon {@link Localizable} (resolved on attach and on explicit set),
 * and arbitrary child components.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * breadcrumb.content(
 *     new BreadcrumbItem("Home", HomeView.class),
 *     new BreadcrumbSeparator(),
 *     new BreadcrumbItem("Components", ComponentsView.class),
 *     new BreadcrumbSeparator(),
 *     new BreadcrumbPage("Breadcrumb")       // â† current page, no link
 * );
 * }</pre>
 */
public class BreadcrumbPage extends ListItem {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Span span;
    private Localizable localizable;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates a current-page crumb with a plain text label.
     *
     * @param text the page label (not null)
     */
    public BreadcrumbPage(String text) {
        addClassName("breadcrumb__item");

        this.span = Components.span().text(text).build();
        applyAriaAttributes();
        add(this.span);
    }

    /**
     * Creates a current-page crumb backed by a {@link Localizable} message.
     * The text is resolved using the current locale on each attach.
     *
     * @param localizable the localizable message (not null)
     */
    public BreadcrumbPage(Localizable localizable) {
        addClassName("breadcrumb__item");

        this.span = Components.span().build();
        this.localizable = localizable;
        LocalizationProvider.localize(localizable).ifPresent(span::setText);
        applyAriaAttributes();
        add(this.span);
    }

    /**
     * Creates a current-page crumb with arbitrary child components as label.
     *
     * @param components child components (e.g. an icon + text)
     */
    public BreadcrumbPage(Component... components) {
        addClassName("breadcrumb__item");

        this.span = Components.span().build();
        this.localizable = null;
        if (components != null) {
            span.add(components);
        }
        applyAriaAttributes();
        add(this.span);
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Updates the page label from a plain string.
     *
     * @param text the new label text (not null)
     */
    public void setText(String text) {
        this.localizable = null;
        this.span.setText(text);
    }

    /**
     * Updates the page label from a {@link Localizable} message.
     * Resolves immediately if a locale is available; always re-resolves on the next attach.
     *
     * @param localizable the localizable message (not null)
     */
    public void setLocalizableText(Localizable localizable) {
        this.localizable = localizable;
        LocalizationProvider.localize(localizable).ifPresent(span::setText);
    }

    // -----------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (this.localizable != null) {
            LocalizationProvider.localize(this.localizable).ifPresent(span::setText);
        }
    }

    // -----------------------------------------------------------------------
    // Internal
    // -----------------------------------------------------------------------

    private void applyAriaAttributes() {
        this.span.addClassName("breadcrumb__page");
        this.span.getElement().setAttribute("aria-disabled", "true");
        this.span.getElement().setAttribute("aria-current", "page");
        this.span.getElement().setAttribute("role", "link");
    }
}
