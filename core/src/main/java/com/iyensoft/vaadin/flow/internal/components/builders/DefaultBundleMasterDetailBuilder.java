package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.components.builders.BundleMasterDetailBuilder;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.tabs.Tabs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Default implementation of {@link BundleMasterDetailBuilder}.
 *
 * <p>Delegates to {@link MasterDetailBuilder} internally; assembles the detail
 * header from {@code detailTitle} + {@code detailActions} automatically when no
 * fully custom header is provided.</p>
 *
 * @param <T> the item type
 */
public class DefaultBundleMasterDetailBuilder<T> implements BundleMasterDetailBuilder<T> {

    // ── Master ─────────────────────────────────────────────────────────────────
    private final ListingBundle<T> bundle;

    // ── Detail header ──────────────────────────────────────────────────────────
    /** Plain-text title — used to build a Header component when no customHeader is given. */
    private String detailTitleText;
    /** Action buttons placed in the Header's trailing slot. */
    private Component[] detailActionComponents = new Component[0];
    private Header customDetailHeader;

    // ── Detail structure ───────────────────────────────────────────────────────
    private Breadcrumb detailBreadcrumbsComponent;
    private Tabs detailTabsComponent;
    private Function<T, Component[]> contentProvider;
    private Component[] footerComponents = new Component[0];

    // ── Reactive sync ──────────────────────────────────────────────────────────
    private final List<Map.Entry<Component, Consumer<T>>> syncHandlers = new ArrayList<>();

    // ── URL sync ───────────────────────────────────────────────────────────────
    private Function<T, String> idExtractor;
    private Function<String, Optional<T>> itemLoader;

    // ── Behaviour ──────────────────────────────────────────────────────────────
    private String mobileSheetTitle;
    private boolean autoSelectFirst = false;
    private final List<Runnable> dataChangedListeners = new ArrayList<>();
    private MenuBar detailMenuBar;

    // ── Constructor ────────────────────────────────────────────────────────────

    public DefaultBundleMasterDetailBuilder(ListingBundle<T> bundle) {
        this.bundle = Objects.requireNonNull(bundle, "bundle must not be null");
    }

    // ── Fluent API ─────────────────────────────────────────────────────────────

    @Override
    public BundleMasterDetailBuilder<T> detailTitle(String title) {
        this.detailTitleText = title;
        return this;
    }

    @Override
    public BundleMasterDetailBuilder<T> detailActions(Component... actions) {
        this.detailActionComponents = actions != null ? actions : new Component[0];
        return this;
    }

    @Override
    public BundleMasterDetailBuilder<T> detailHeader(Header header) {
        this.customDetailHeader = header;
        return this;
    }

    @Override
    public BundleMasterDetailBuilder<T> detailBreadcrumbs(Breadcrumb breadcrumbs) {
        this.detailBreadcrumbsComponent = breadcrumbs;
        return this;
    }

    @Override
    public BundleMasterDetailBuilder<T> detailTabs(Tabs tabsOrMenu) {
        this.detailTabsComponent = tabsOrMenu;
        return this;
    }

    @Override
    public BundleMasterDetailBuilder<T> detailMenuBar(MenuBar menuBar) {
        this.detailMenuBar = menuBar;
        return this;
    }

    @Override
    public BundleMasterDetailBuilder<T> detailContent(Function<T, Component[]> provider) {
        this.contentProvider = provider;
        return this;
    }

    @Override
    public BundleMasterDetailBuilder<T> detailFooter(Component... footer) {
        this.footerComponents = footer != null ? footer : new Component[0];
        return this;
    }

    @Override
    public BundleMasterDetailBuilder<T> withDetailSync(Component owner, Consumer<T> handler) {
        Objects.requireNonNull(owner,   "withDetailSync: owner must not be null");
        Objects.requireNonNull(handler, "withDetailSync: handler must not be null");
        syncHandlers.add(Map.entry(owner, handler));
        return this;
    }

    @Override
    public BundleMasterDetailBuilder<T> itemId(Function<T, String> extractor,
                                                 Function<String, Optional<T>> loader) {
        this.idExtractor = extractor;
        this.itemLoader  = loader;
        return this;
    }

    @Override
    public BundleMasterDetailBuilder<T> mobileSheetTitle(String title) {
        this.mobileSheetTitle = title;
        return this;
    }

    @Override
    public BundleMasterDetailBuilder<T> autoSelectFirst(boolean auto) {
        this.autoSelectFirst = auto;
        return this;
    }

    @Override
    public BundleMasterDetailBuilder<T> onDataChanged(Runnable listener) {
        if (listener != null) dataChangedListeners.add(listener);
        return this;
    }

    // ── Build ──────────────────────────────────────────────────────────────────

    @Override
    public MasterDetailLayout<T> build() {
        Objects.requireNonNull(contentProvider, "detailContent() is mandatory");

        MasterDetailBuilder<T> mb = MasterDetailBuilder.create();

        // ── Left panel: full bundle (header + search + grid) ──────────────────
        mb.masterGrid(bundle);

        // ── Right panel: header ───────────────────────────────────────────────
        Header headerComponent = resolveDetailHeader();
        if (headerComponent != null) mb.detailHeader(headerComponent);

        // ── Right panel: breadcrumbs ──────────────────────────────────────────
        if (detailBreadcrumbsComponent != null) mb.detailBreadcrumbs(detailBreadcrumbsComponent);

        // ── Right panel: tabs / menu bar ──────────────────────────────────────
        if (detailTabsComponent != null) mb.detailMenu(detailTabsComponent);

        // ── Right panel: tabs / menu bar ──────────────────────────────────────
        if (detailMenuBar != null) mb.detailMenu(detailMenuBar);

        // ── Right panel: dynamic content ──────────────────────────────────────
        mb.detailContent(contentProvider);

        // ── Right panel: footer ───────────────────────────────────────────────
        if (footerComponents.length > 0) mb.detailFooter(footerComponents);

        // ── URL sync ──────────────────────────────────────────────────────────
        if (idExtractor != null) mb.itemId(idExtractor, itemLoader);

        // ── Mobile sheet ──────────────────────────────────────────────────────
        if (mobileSheetTitle != null) mb.mobileSheetTitle(mobileSheetTitle);

        // ── Behaviour ─────────────────────────────────────────────────────────
        mb.autoSelectFirst(autoSelectFirst);
        dataChangedListeners.forEach(mb::onDataChanged);
        syncHandlers.forEach(e -> mb.withDetailSync(e.getKey(), e.getValue()));

        return mb.build();
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    /**
     * Returns the resolved detail header component:
     * <ul>
     *   <li>If a fully custom header was set via {@link #detailHeader(Header)}, returns it.</li>
     *   <li>If a title string was set via {@link #detailTitle(String)}, builds a
     *       {@link Header} with that title and the configured action buttons.</li>
     *   <li>Otherwise returns {@code null} (no detail header).</li>
     * </ul>
     */
    private Header resolveDetailHeader() {
        if (customDetailHeader != null) {
            return customDetailHeader;
        }
        if (detailTitleText != null) {
            Header header = new Header(detailTitleText);
            if (detailActionComponents.length > 0) {
                header.setActions(detailActionComponents);
            }
            return header;
        }
        return null;
    }
}

