package com.iyensoft.vaadin.flow.internal.components.builders;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.components.ListingBundleBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractHeaderConfigurator;
import com.holonplatform.vaadin.flow.internal.lumo.SeparatorColor;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.Empty;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.components.Separator;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailConfigurator;
import com.iyensoft.vaadin.flow.internal.components.masterdetail.SelectionHighlighter;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.Renderer;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
/**
 * Abstract implementation of {@link MasterDetailConfigurator}.
 *
 * <p>This class extends {@link Layout} — it contains the shared builder logic and also acts
 * as the resulting component. Call {@link #build()} to wire the internal structure, then content
 * this instance to a parent layout.
 * All operational methods ({@link #notifyDataChanged()}, {@link #clearSelection()}, etc.) are
 * available directly on this instance both before and after {@link #build()}.</p>
 *
 * <p>No CSS classes or stylesheets are applied; styling is left entirely to the caller.</p>
 *
 * @param <T> item type
 * @param <C> parent configurator type
 */
@StyleSheet("context://master-detail-v2.css")
public abstract class AbstractMasterDetailConfigurator<T, C extends MasterDetailConfigurator<T, C>> extends Layout implements MasterDetailConfigurator<T, C> {

    protected abstract C getConfigurator();

    private final ListingBundleBuilder<T> listingBundleBuilder;

    private Function<T, Optional<T>> detailLoader;
    private Function<T, String> idExtractor;
    private Function<String, Optional<T>> itemLoader;
    private boolean deepLinkRequested;
    private boolean autoShowFirst;
    private int selectionDebounceMillis;
    private int detailCacheSize = 1;
    private boolean scrollToSelected;
    private boolean clearDetailOnPageChange;
    private boolean prefetchAdjacent;
    private final List<Runnable> dataChangedListeners = new ArrayList<>();
    private final List<Consumer<T>> itemChangedListeners = new ArrayList<>();
    private final List<Map.Entry<Component, Consumer<T>>> syncHandlers = new ArrayList<>();

    private Header masterHeader;
    private boolean separatorVisible;
    private SeparatorColor separatorColor; // stored; applied by caller via CSS
    private Header detailHeader;
    private DefaultDetailTabsNode detailTabs;
    private final List<Component> detailContentComponents = new ArrayList<>();
    private final List<Component> detailFooterComponents = new ArrayList<>();
    private boolean mobileMasterOnly;
    private String mobileSheetTitle;
    private Separator separatorComponent;
    private ListingBundle<T> configuredListingBundle;

    private final Map<Object, T> detailCache = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Object, T> eldest) {
            return size() > Math.max(1, detailCacheSize);
        }
    };

    private Grid<T> builtGrid;
    private SelectionHighlighter<T> selectionHighlighter;
    private T currentItem;
    private Object currentItemKey;
    private boolean autoSelectFired;
    private long lastSyncAt;

    // Exposed via getListingBundle / getMasterDiv / getDetailDiv
    private ListingBundle<T> builtBundle;
    private Layout masterPanel;
    private Layout detailPanel;

    // â��€â”€ Inner node singletons â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private final DefaultMasterViewNode masterNode = new DefaultMasterViewNode();
    private final DefaultDefaultViewNode defaultViewNode = new DefaultDefaultViewNode();
    private final DefaultSeparatorNode separatorNode = new DefaultSeparatorNode();
    private final DefaultDetailNode detailNode = new DefaultDetailNode();

    // â”€â”€ Constructor â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public AbstractMasterDetailConfigurator(Class<T> beanType) {
        this.listingBundleBuilder = Components.listing(Objects.requireNonNull(beanType, "beanType must not be null"));
    }

    @Override
    public MasterNode<T, C> masterView() {
        return masterNode;
    }

    @Override
    public MasterDetailConfigurator.DefaultViewNode<T, C> defaultView() {
        separatorVisible = true;
        return defaultViewNode;
    }

    @Override
    public MasterDetailConfigurator.DefaultViewNode<T, C> defaultView(MasterNode<T, ?> master) {
        if (master != null) {
            master.add();
        }
        return defaultView();
    }

    @Override
    public C detailLoader(Function<T, Optional<T>> detailLoader) {
        this.detailLoader = detailLoader;
        return getConfigurator();
    }

    @Override
    public C deepLink() {
        this.deepLinkRequested = true;
        return getConfigurator();
    }

    @Override
    public C deepLink(Function<T, String> idExtractor, Function<String, Optional<T>> itemLoader) {
        return itemId(idExtractor, itemLoader).deepLink();
    }

    @Override
    public C itemId(Function<T, String> idExtractor, Function<String, Optional<T>> itemLoader) {
        this.idExtractor = idExtractor;
        this.itemLoader = itemLoader;
        return getConfigurator();
    }

    @Override
    public C autoShowFirst() {
        return autoShowFirst(true);
    }

    @Override
    public C autoShowFirst(boolean auto) {
        this.autoShowFirst = auto;
        return getConfigurator();
    }

    @Override
    public C selectionDebounce(int millis) {
        this.selectionDebounceMillis = Math.max(0, millis);
        return getConfigurator();
    }

    @Override
    public C detailCacheSize(int size) {
        this.detailCacheSize = Math.max(1, size);
        return getConfigurator();
    }

    @Override
    public C scrollToSelected(boolean scrollToSelected) {
        this.scrollToSelected = scrollToSelected;
        return getConfigurator();
    }

    @Override
    public C clearDetailOnPageChange(boolean clearDetailOnPageChange) {
        this.clearDetailOnPageChange = clearDetailOnPageChange;
        return getConfigurator();
    }

    @Override
    public C prefetchAdjacent(boolean prefetchAdjacent) {
        this.prefetchAdjacent = prefetchAdjacent;
        return getConfigurator();
    }

    @Override
    public C onDataChanged(Runnable listener) {
        if (listener != null) {
            dataChangedListeners.add(listener);
        }
        return getConfigurator();
    }

    @Override
    public C onItemChanged(Consumer<T> listener) {
        if (listener != null) {
            itemChangedListeners.add(listener);
        }
        return getConfigurator();
    }

    @Override
    public C withDetailSync(Component owner, Consumer<T> handler) {
        Objects.requireNonNull(owner, "withDetailSync: owner must not be null");
        Objects.requireNonNull(handler, "withDetailSync: handler must not be null");
        syncHandlers.add(Map.entry(owner, handler));
        return getConfigurator();
    }

    @Override
    public C mobileSheetTitle(String title) {
        this.mobileSheetTitle = title;
        return getConfigurator();
    }

    @Override
    public C mobileMasterOnly(boolean mobileMasterOnly) {
        setMobileMasterOnly(mobileMasterOnly);
        return getConfigurator();
    }

    protected void setMobileMasterOnly(boolean mobileMasterOnly) {
        this.mobileMasterOnly = mobileMasterOnly;
    }

    public C build() {
        ListingBundle<T> bundle = configuredListingBundle != null ? configuredListingBundle : listingBundleBuilder.build();
        this.builtBundle = bundle;
        this.builtGrid = extractGrid(bundle);
        this.builtGrid.addClassName("mdl-master-grid");
        this.separatorComponent = null;
        this.selectionHighlighter = new SelectionHighlighter<>(builtGrid,
                idExtractor != null ? idExtractor : Function.identity());

        // Resolve tabs content container into the detail content list
        if (detailTabs != null) {
            Component tabsContent = detailTabs.getContentContainer();
            if (tabsContent != null && !detailContentComponents.contains(tabsContent)) {
                detailContentComponents.addFirst(tabsContent);
            }
        }

        // Fallback empty state when no detail content was registered
        if (detailContentComponents.isEmpty()) {
            detailContentComponents.add(Empty.builder()
                    .title("Select an item")
                    .description("Choose a row in the master list to view details.")
                    .build());
        }

        this.masterPanel = Components.layout()
                .styleName("master-view")
                .build();
        masterPanel.setWidthFull();
        Layout masterContent = Components.layout()
                .styleName("master-view-content")
                .build();
        masterContent.setWidthFull();

        if (masterHeader != null) masterPanel.add(masterHeader);

        GridHeader bundleHeader = bundle.header();
        if (bundleHeader != null) {
            masterPanel.add(bundleHeader);
        }

        Component masterToolbar = bundle.toolbar();
        if (masterToolbar.isVisible()) {
            masterContent.add(masterToolbar);
        }

        builtGrid.setWidthFull();
        masterContent.add(builtGrid);
        masterContent.setFlexGrow(builtGrid);

        Component bundleFooter = bundle.footer();
        if (bundleFooter != null) {
            masterContent.add(bundleFooter);
        }

        masterPanel.add(masterContent);
        masterPanel.setFlexGrow(masterContent);

        this.detailPanel = Components.layout()
                .styleName("detail-view")
                .build();
        detailPanel.setWidthFull();
        Layout detailContent = Components.layout()
                .styleName("detail-view-content")
                .build();
        detailContent.setWidthFull();

        if (detailHeader != null) {
            detailContent.add(detailHeader);
            if (detailTabs != null) detailHeader.setTabs(detailTabs.getTabs());
        }

        detailContent.add(detailContentComponents.toArray(Component[]::new));
        if (!detailFooterComponents.isEmpty()) {
            detailContent.add(detailFooterComponents.toArray(Component[]::new));
        }

        detailPanel.add(detailContent);
        detailPanel.setFlexGrow(detailContent);

        if (mobileMasterOnly) {
            addClassName("mdl-mobile-master-only");
        }

        if (separatorVisible) {
            separatorComponent = Separator.builder()
                    .orientation(Separator.Orientation.VERTICAL)
                    .styleName("separator")
                    .color(Color.Background.CONTRAST_90)
                    .decorative(true)
                    .build();
            if (separatorColor != null) {
                separatorComponent.addClassName(separatorColor.getClassName());
            }
            this.add(masterPanel, separatorComponent, detailPanel);
        } else {
            this.add(masterPanel, detailPanel);
        }

        if (mobileSheetTitle != null && !mobileSheetTitle.isBlank()) {
            getElement().setAttribute("data-mobile-sheet-title", mobileSheetTitle);
        }

        builtGrid.addItemClickListener(event -> handleItemClick(event.getItem()));

        if (deepLinkRequested || autoShowFirst) {
            addAttachListener(event -> event.getUI().getElement()
                    .executeJs("return window.location.search")
                    .then(String.class, search -> {
                        String id = deepLinkRequested ? extractIdQueryParameter(search) : null;
                        if (id != null && !id.isBlank() && itemLoader != null) {
                            autoSelectFired = true;
                            restoreSelection(id);
                            return;
                        }
                        if (autoShowFirst && !autoSelectFired) {
                            autoSelectFired = true;
                            selectFirst();
                        }
                    }));
        }


        if (clearDetailOnPageChange || prefetchAdjacent) {
            var bar = bundle.bar();
            if (bar != null) {
                bar.addPageChangeListener(page -> {
                    if (clearDetailOnPageChange) clearSelection();
                    if (prefetchAdjacent) prefetchPage(page, bar.getPageSize());
                });
            }
        }

        // Register the default data refresh policy.
        dataChangedListeners.add(this::refreshData);

        ResponsiveDiv.configure(this)
                .styleName("master-detail-container")
                .elevated();

        return getConfigurator();
    }

    @Override
    public void notifyDataChanged() {
        dataChangedListeners.forEach(Runnable::run);
    }

    private void refreshData() {
        if (builtGrid == null) {
            return;
        }
        if (currentItem != null) {
            builtGrid.getDataProvider().refreshItem(currentItem);
            return;
        }
        builtGrid.getDataProvider().refreshAll();
    }

    @Override
    public void refreshItem(T item) {
        if (builtGrid == null || item == null) {
            return;
        }
        builtGrid.getDataProvider().refreshItem(item);
    }

    @Override
    public void refreshCurrentItem() {
        refreshItem(currentItem);
    }

    @Override
    public void clearSelection() {
        if (builtGrid != null) builtGrid.deselectAll();
        if (selectionHighlighter != null) selectionHighlighter.setHighlighted(null);
        currentItem = null;
        currentItemKey = null;
        if (deepLinkRequested && idExtractor != null) {
            clearBrowserId();
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void selectFirst() {
        if (builtGrid == null) return;
        builtGrid.getDataProvider()
                .fetch(new Query(0, 1, null, null, null))
                .findFirst()
                .ifPresent(item -> activateItem((T) item, true, false, false));
    }

    @Override
    public void restoreSelection(String idStr) {
        if (itemLoader == null || idStr == null || idStr.isBlank()) return;
        autoSelectFired = true;
        itemLoader.apply(idStr).ifPresent(item -> activateItem(item, true, true, true));
    }

    @Override
    public void clickItem(T item) {
        activateItem(item, scrollToSelected, false, false);
    }

    @Override
    public java.util.Optional<ListingBundle<T>> getListingBundle() {
        return java.util.Optional.ofNullable(builtBundle);
    }

    @Override
    public java.util.Optional<Div> getMasterDiv() {
        return java.util.Optional.ofNullable(masterPanel);
    }

    @Override
    public java.util.Optional<Div> getDetailDiv() {
        return java.util.Optional.ofNullable(detailPanel);
    }

    @Override
    public Optional<Header> getMasterHeader() {
        return Optional.ofNullable(masterHeader);
    }

    @Override
    public Optional<Separator> getSeparatorComponent() {
        return Optional.ofNullable(separatorComponent);
    }

    @Override
    public Optional<Header> getDetailHeader() {
        return Optional.ofNullable(detailHeader);
    }

    @Override
    public Optional<Tabs> getDetailTabs() {
        return detailTabs != null ? Optional.of(detailTabs.getTabs()) : Optional.empty();
    }

    @Override
    public List<Component> getDetailContentComponents() {
        return List.copyOf(detailContentComponents);
    }

    @Override
    public List<Component> getDetailFooterComponents() {
        return List.copyOf(detailFooterComponents);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    private void handleItemClick(T item) {
        activateItem(item, scrollToSelected, false, false);
    }

    private void activateItem(T item, boolean ensureVisible, boolean skipDetailLoader, boolean bypassCache) {
        if (item == null) return;

        long now = System.currentTimeMillis();
        if (selectionDebounceMillis > 0 && currentItem != null && now - lastSyncAt < selectionDebounceMillis) {
            return;
        }
        lastSyncAt = now;

        T resolved = resolveDetailItem(item, skipDetailLoader, bypassCache);
        currentItem = resolved;
        currentItemKey = itemKey(resolved);

        // Apply the left-accent highlight to the clicked row
        selectionHighlighter.setHighlighted(resolved);

        if (ensureVisible) {
            scrollResolvedItemIntoView(resolved);
        }

        // Always fire sync handlers â€” they populate the detail panel content
        syncHandlers.forEach(entry -> entry.getValue().accept(resolved));
        itemChangedListeners.forEach(listener -> listener.accept(resolved));

        if (deepLinkRequested && idExtractor != null) {
            updateBrowserId(resolved);
        }
    }

    private void scrollResolvedItemIntoView(T item) {
        if (builtGrid == null || item == null) {
            return;
        }

        try {
            builtGrid.scrollToItem(item);
        } catch (Exception ignored) {
            // Grid item scrolling is best-effort only.
        }

        Object targetKey = idExtractor != null ? idExtractor.apply(item) : item;
        if (targetKey == null) {
            return;
        }

        int total = -1;
        try {
            total = builtGrid.getDataProvider().size(new Query<>());
        } catch (Exception ignored) {
            // Some lazy providers may not support count queries reliably.
        }

        final int batchSize = 200;
        int offset = 0;
        while (total < 0 || offset < total) {
            List<T> batch;
            try (Stream<T> stream = builtGrid.getDataProvider().fetch(new Query<>(offset, batchSize, null, null, null))) {
                batch = stream.toList();
            }

            if (batch.isEmpty()) {
                break;
            }

                for (T candidate : batch) {
                Object candidateKey = idExtractor != null ? idExtractor.apply(candidate) : candidate;
                if (Objects.equals(targetKey, candidateKey)) {
                    return;
                }
            }

            offset += batch.size();
            if (batch.size() < batchSize) {
                break;
            }
        }
    }

    private T resolveDetailItem(T item, boolean skipDetailLoader, boolean bypassCache) {
        Object cacheKey = itemKey(item);
        if (!bypassCache && currentItem != null && Objects.equals(cacheKey, currentItemKey)) {
            return currentItem;
        }
        if (!bypassCache && cacheKey != null && detailCache.containsKey(cacheKey)) {
            return detailCache.get(cacheKey);
        }

        T resolved = item;
        if (!skipDetailLoader && detailLoader != null) {
            resolved = detailLoader.apply(item).orElse(item);
        }

        if (cacheKey != null) {
            detailCache.put(cacheKey, resolved);
        }
        return resolved;
    }

    private void prefetchPage(int page, int pageSize) {
        if (builtGrid == null || pageSize <= 0) return;
        int offset = Math.max(0, (page - 1) * pageSize);
        int limit = Math.clamp(pageSize, 1, 4);
        try (Stream<T> stream = builtGrid.getDataProvider().fetch(
                new Query<>(offset, pageSize, null, null, null))) {
            stream.limit(limit).forEach(this::prefetchResolvedItem);
        }
    }

    private void prefetchResolvedItem(T item) {
        Object cacheKey = itemKey(item);
        if (cacheKey != null && detailCache.containsKey(cacheKey)) {
            return;
        }
        T resolved = detailLoader != null ? detailLoader.apply(item).orElse(item) : item;
        if (cacheKey != null) {
            detailCache.put(cacheKey, resolved);
        }
    }

    private Object itemKey(T item) {
        if (item == null) {
            return null;
        }
        return idExtractor != null ? idExtractor.apply(item) : item;
    }

    @SuppressWarnings("unchecked")
    private Grid<T> extractGrid(ListingBundle<T> bundle) {
        return (Grid<T>) bundle.listing().getComponent();
    }

    private static String extractIdQueryParameter(String search) {
        if (search == null || search.isBlank()) return null;
        String query = search.startsWith("?") ? search.substring(1) : search;
        for (String pair : query.split("&")) {
            int index = pair.indexOf('=');
            if (index < 0) {
                if ("id".equals(pair)) return "";
                continue;
            }
            if ("id".equals(pair.substring(0, index))) {
                return URLDecoder.decode(pair.substring(index + 1), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private void updateBrowserId(T item) {
        if (item == null || idExtractor == null) return;
        String id = idExtractor.apply(item);
        if (id == null || id.isBlank()) return;
        getUI().ifPresent(ui -> ui.getPage().executeJs(
                "const url = new URL(window.location.href);" +
                        "url.searchParams.set('id', $0);" +
                        "history.replaceState({}, '', url.toString());",
                id));
    }

    private void clearBrowserId() {
        getUI().ifPresent(ui -> ui.getPage().executeJs(
                "const url = new URL(window.location.href);" +
                        "url.searchParams.delete('id');" +
                        "history.replaceState({}, '', url.toString());"));
    }

    private final class DefaultMasterViewNode implements MasterNode<T, C> {

        @Override
        public MasterDetailConfigurator.HeaderNode<T, C> header() {
            if (masterHeader == null) {
                masterHeader = new Header("");
            }
            return new DefaultHeaderNode(masterHeader);
        }

        @Override
        public C header(Header header) {
            masterHeader = Objects.requireNonNull(header, "header must not be null");
            return AbstractMasterDetailConfigurator.this.getConfigurator();
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> listingBundle() {
            return new DefaultListingBundleNode(listingBundleBuilder);
        }

        @Override
        public C listingBundle(ListingBundle<T> listingBundle) {
            configuredListingBundle = Objects.requireNonNull(listingBundle, "listingBundle must not be null");
            return AbstractMasterDetailConfigurator.this.getConfigurator();
        }



        @Override
        public C add() {
            return AbstractMasterDetailConfigurator.this.getConfigurator();
        }
    }

    private final class DefaultDefaultViewNode implements MasterDetailConfigurator.DefaultViewNode<T, C> {

        @Override
        public SeparatorNode<C> separator() {
            separatorVisible = true;
            return separatorNode;
        }

        @Override
        public DetailNode<T, C> detail() {
            return detailNode;
        }

        @Override
        public C master(Header header, ListingBundle<T> listingBundle) {
            masterHeader = Objects.requireNonNull(header, "header must not be null");
            configuredListingBundle = Objects.requireNonNull(listingBundle, "listingBundle must not be null");
            separatorVisible = true;
            return AbstractMasterDetailConfigurator.this.getConfigurator();
        }

        @Override
        public C add() {
            return AbstractMasterDetailConfigurator.this.getConfigurator();
        }
    }

    private final class DefaultHeaderNode extends AbstractHeaderConfigurator<HeaderNode<T, C>>
            implements MasterDetailConfigurator.HeaderNode<T, C> {

        private DefaultHeaderNode(Header header) {
            super(header);
        }

        @Override
        protected DefaultHeaderNode getConfigurator() {
            return this;
        }

        @Override
        public MasterDetailConfigurator.HeaderNode<T, C> withoutBorder() {
            getComponent().withoutBorder();
            return this;
        }

        @Override
        public MasterNode<T, C> add() {
            return masterNode;
        }
    }

    private final class DefaultListingBundleNode implements MasterDetailConfigurator.ListingBundleNode<T, C> {

        private final ListingBundleBuilder<T> delegate;

        private DefaultListingBundleNode(ListingBundleBuilder<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> columns(String... cols) {
            delegate.columns(cols);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> mobileViewColumn(Renderer<T> renderer) {
            delegate.mobileViewColumn(renderer);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> mobileViewHeader(String text) {
            delegate.mobileViewHeader(text);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> mobileViewHeader(Component component) {
            delegate.mobileViewHeader(component);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> hidden(String... cols) {
            delegate.hidden(cols);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> header(String column, String label) {
            delegate.header(column, label);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> header(String column, Localizable localizable) {
            delegate.header(column, localizable);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> header(String column, String defaultLabel, String messageCode) {
            delegate.header(column, defaultLabel, messageCode);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> pageSizes(Integer... sizes) {
            delegate.pageSizes(sizes);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> defaultPageSize(int size) {
            delegate.defaultPageSize(size);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> search(String placeholder) {
            delegate.search(placeholder);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> search(Localizable localizable) {
            delegate.search(localizable);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> search(String defaultPlaceholder, String messageCode) {
            delegate.search(defaultPlaceholder, messageCode);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> withFilterPanel() {
            delegate.withFilterPanel();
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> advancedSearchLabel(String label) {
            delegate.advancedSearchLabel(label);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> retainFilterValues(boolean retain) {
            delegate.retainFilterValues(retain);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> multiSelect() {
            delegate.multiSelect();
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> onItemClickListener(ViewMode viewMode, ComponentEventListener<ItemClickEvent<T>> listener) {
            delegate.onItemClickListener(viewMode,listener);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> gridHeader(String title) {
            delegate.gridHeader(title);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> gridHeader(Component... components) {
            delegate.gridHeader(components);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> paginated() {
            delegate.paginated();
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> virtualScroll() {
            delegate.virtualScroll();
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> paginated(boolean paginated) {
            delegate.paginated(paginated);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> autoCreateColumns(boolean autoCreate) {
            delegate.autoCreateColumns(autoCreate);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> importAction(Runnable action) {
            delegate.importAction(action);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> exportAction(Runnable action) {
            delegate.exportAction(action);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> withMenuAction(String label, Runnable action) {
            delegate.withMenuAction(label, action);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> withMenuAction(VaadinIcon icon, String label, Runnable action) {
            delegate.withMenuAction(icon, label, action);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> fetch(ListingBundleBuilder.FetchCallback<T> callback) {
            delegate.fetch(callback);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> fetch(ListingBundleBuilder.FilteredFetchCallback<T> callback) {
            delegate.fetch(callback);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> fetch(ListingBundleBuilder.ColumnAwareFilteredFetchCallback<T> cb) {
            delegate.fetch(cb);
            return this;
        }

        @Override
        public MasterDetailConfigurator.ListingBundleNode<T, C> viewModeSupplier(Supplier<ViewMode> supplier) {
            delegate.viewModeSupplier(supplier);
            return this;
        }

        @Override
        public MasterNode<T, C> add() {
            return masterNode;
        }
    }

    private final class DefaultSeparatorNode implements SeparatorNode<C> {

        @Override
        public SeparatorNode<C> visible(boolean visible) {
            separatorVisible = visible;
            return this;
        }

        @Override
        public SeparatorNode<C> color(SeparatorColor color) {
            separatorColor = color;
            return this;
        }

        @Override
        public C add() {
            return AbstractMasterDetailConfigurator.this.getConfigurator();
        }
    }

    private final class DefaultDetailNode implements DetailNode<T, C> {

        @Override
        public MasterDetailConfigurator.DetailHeaderNode<T, C> header() {
            if (detailHeader == null) {
                detailHeader = new Header("");
            }
            return new DefaultDetailHeaderNode(detailHeader);
        }

        @Override
        public MasterDetailConfigurator.DetailTabsNode<T, C> tabs() {
            if (detailTabs == null) {
                detailTabs = new DefaultDetailTabsNode();
            }
            return detailTabs;
        }

        @Override
        public MasterDetailConfigurator.DetailContentNode<T, C> content() {
            return new DefaultDetailContentNode();
        }

        @Override
        public MasterDetailConfigurator.DetailFooterNode<T, C> footer() {
            return new DefaultDetailFooterNode();
        }

        @Override
        public DetailNode<T, C> withDetailSync(Component owner, Consumer<T> handler) {
            AbstractMasterDetailConfigurator.this.withDetailSync(owner, handler);
            return this;
        }

        @Override
        public C add() {
            return AbstractMasterDetailConfigurator.this.getConfigurator();
        }
    }

    private final class DefaultDetailHeaderNode extends AbstractHeaderConfigurator<MasterDetailConfigurator.DetailHeaderNode<T, C>>
            implements MasterDetailConfigurator.DetailHeaderNode<T, C> {

        private DefaultDetailHeaderNode(Header header) {
            super(header);
        }

        @Override
        protected DefaultDetailHeaderNode getConfigurator() {
            return this;
        }

        @Override
        public MasterDetailConfigurator.DetailHeaderNode<T, C> withoutBorder() {
            getComponent().withoutBorder();
            return this;
        }

        @Override
        public DetailNode<T, C> add() {
            return detailNode;
        }
    }

    private final class DefaultDetailTabsNode extends AbstractLazyTabsConfigurator<MasterDetailConfigurator.DetailTabsNode<T, C>>
            implements MasterDetailConfigurator.DetailTabsNode<T, C> {

        private DefaultDetailTabsNode() {
            super(new Tabs());
        }

        @Override
        protected DefaultDetailTabsNode getConfigurator() {
            return this;
        }

        @Override
        public DetailNode<T, C> add() {
            return detailNode;
        }
    }

    private final class DefaultDetailContentNode implements MasterDetailConfigurator.DetailContentNode<T, C> {

        @Override
        public MasterDetailConfigurator.DetailContentNode<T, C> content(Component... components) {
            if (components != null) {
                for (Component component : components) {
                    if (component != null) {
                        detailContentComponents.add(component);
                    }
                }
            }
            return this;
        }

        @Override
        public DetailNode<T, C> add() {
            return detailNode;
        }
    }

    private final class DefaultDetailFooterNode implements MasterDetailConfigurator.DetailFooterNode<T, C> {

        @Override
        public MasterDetailConfigurator.DetailFooterNode<T, C> content(Component... components) {
            if (components != null) {
                for (Component component : components) {
                    if (component != null) {
                        detailFooterComponents.add(component);
                    }
                }
            }
            return this;
        }

        @Override
        public DetailNode<T, C> add() {
            return detailNode;
        }
    }
}
