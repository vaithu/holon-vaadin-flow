package com.holonplatform.vaadin.flow.vaadinplus.components;

import java.io.Serial;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLink;

/**
 * A single crumb in a {@link Breadcrumb} trail  wraps a navigation link.
 *
 * <p>Renders as:</p>
 * <pre>
 * &lt;li class="breadcrumb__item"&gt;
 *   &lt;a class="breadcrumb__link" href="â€¦"&gt;Label&lt;/a&gt;
 * &lt;/li&gt;
 * </pre>
 *
 * <p>Implements {@link AfterNavigationObserver} so the embedded {@link RouterLink} receives
 * {@code aria-current="page"} automatically when the current navigation matches its route
 * (full path comparison, not just the first segment).</p>
 *
 * <p>For the <em>last</em>, non-linked item (the current page) use {@link BreadcrumbPage} instead.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * new BreadcrumbItem("Home", HomeView.class)
 * new BreadcrumbItem("Components", ComponentsView.class)
 * }</pre>
 *
 * @see Breadcrumb
 * @see BreadcrumbPage
 * @see BreadcrumbSeparator
 */
public class BreadcrumbItem extends ListItem implements AfterNavigationObserver {

    @Serial
    private static final long serialVersionUID = 1L;

    /** The embedded router link; {@code null} for arbitrary-content items. */
    private RouterLink link;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates a breadcrumb item wrapping the given {@link RouterLink}.
     * The link receives the {@code breadcrumb__link} CSS class.
     *
     * @param link the router link (not null)
     */
    public BreadcrumbItem(RouterLink link) {
        addClassName("breadcrumb__item");
        this.link = link;
        this.link.addClassName("breadcrumb__link");
        add(this.link);
    }

    /**
     * Creates a breadcrumb item with a text label navigating to the given target view.
     *
     * @param text             the visible label text (not null)
     * @param navigationTarget the route target class (not null)
     */
    public BreadcrumbItem(String text, Class<? extends Component> navigationTarget) {
        this(new RouterLink(text, navigationTarget));
    }

    /**
     * Creates a breadcrumb item with arbitrary child content (no link).
     * Use this for custom renderings  e.g. an icon-only crumb.
     * This constructor does NOT register an {@link AfterNavigationObserver};
     * {@link #afterNavigation(AfterNavigationEvent)} is a no-op.
     *
     * @param content the component to display inside the crumb (may be null)
     */
    public BreadcrumbItem(Component content) {
        addClassName("breadcrumb__item");
        this.link = null;
        if (content != null) {
            add(content);
        }
    }

    // -----------------------------------------------------------------------
    // AfterNavigationObserver
    // -----------------------------------------------------------------------

    /**
     * Marks the embedded link with {@code aria-current="page"} when the navigated location
     * matches the link's href (full path comparison).
     *
     * <p>This is a no-op for items created with the arbitrary-content constructor.</p>
     */
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        if (this.link == null) {
            return;
        }

        // event.getLocation().getPath() returns the full path without leading slash
        // RouterLink.getHref() also returns the path without leading slash
        String currentPath = event.getLocation().getPath();
        String href = this.link.getHref();

        if (href != null && href.equals(currentPath)) {
            this.link.getElement().setAttribute("aria-current", "page");
        } else {
            this.link.getElement().removeAttribute("aria-current");
        }
    }
}
