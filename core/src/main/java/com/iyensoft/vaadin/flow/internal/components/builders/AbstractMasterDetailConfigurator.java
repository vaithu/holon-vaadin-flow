package com.iyensoft.vaadin.flow.internal.components.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.components.builders.FooterConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HeaderConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractListingBundleConfigurer;
import com.holonplatform.vaadin.flow.vaadinplus.components.Footer;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.iyensoft.vaadin.flow.components.DetailSyncAware;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailConfigurator;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.iyensoft.vaadin.flow.internal.components.masterdetail.SelectionHighlighter;
import com.iyensoft.vaadin.flow.internal.components.masterdetail.UrlSelectionSync;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.shared.HasTooltip;

/**
 * Abstract base for all {@link MasterDetailConfigurator} implementations.
 *
 * <p>Wraps a {@link MasterDetailLayout} directly. Concrete subclasses return
 * {@link #getComponent()} from {@code build()} (the Panel pattern).</p>
 *
 * @param <T> item type
 * @param <C> self type
 */
public abstract class AbstractMasterDetailConfigurator<T, C extends MasterDetailConfigurator<T, C>>
        extends AbstractComponentConfigurator<MasterDetailLayout<T>, C>
        implements MasterDetailConfigurator<T, C> {

    /** Bean type supplied at construction time; {@code null} for PropertySet builders. */
    private final Class<T> beanType;

    /** PropertySet supplied at construction time; {@code null} for bean-typed builders. */
    private final PropertySet<?> propertySet;

    /** Viewport mode; when mobile the detail panel is not added to the DOM at build time. */
    private ViewMode viewMode;

    /** Optional Sheet to wrap the detail panel in on mobile. */
    private Sheet mobileSheet;

    /** Optional override for the CSS part name used by SelectionHighlighter. */
    private String highlightPartName;

    /** Optional per-item accent CSS class provider. */
    private Function<T, String> accentColorProvider;

    /** Bean-typed constructor. */
    protected AbstractMasterDetailConfigurator(MasterDetailLayout<T> component, Class<T> beanType) {
        super(component);
        this.beanType = beanType;
        this.propertySet = null;
    }

    /** PropertySet-typed constructor (item type is {@code PropertyBox}). */
    protected AbstractMasterDetailConfigurator(MasterDetailLayout<T> component, PropertySet<?> propertySet) {
        super(component);
        this.beanType = null;
        this.propertySet = propertySet;
    }

    // ── MasterDetailConfigurator API ──────────────────────────────────────────

    @Override
    public C withHighlightPartName(String partName) {
        this.highlightPartName = partName;
        return getConfigurator();
    }

    @Override
    public C withAccentColorProvider(Function<T, String> cssClassProvider) {
        this.accentColorProvider = cssClassProvider;
        return getConfigurator();
    }

    @Override
    public C viewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
        return getConfigurator();
    }

    @Override
    public C withMobileSheet(Sheet sheet) {
        this.mobileSheet = sheet;
        return getConfigurator();
    }

    @Override
    public C withMobileSheet(Sheet.Side side) {
        this.mobileSheet = Sheet.builder(side).fullscreenOnMobile(true).build();
        return getConfigurator();
    }

    /** Returns the configured mobile Sheet, or {@code null} if none. */
    private Sheet getMobileSheet() {
        return mobileSheet;
    }

    @Override
    public C withDetailSync(Consumer<T> handler) {
        getComponent().addSyncDispatcher(handler);
        return getConfigurator();
    }

    /** Returns {@code true} when a mobile ViewMode has been set. */
    boolean isMobile() {
        return viewMode != null && viewMode.isMobile();
    }

    @Override
    public C lazyDetail(Consumer<MasterDetailConfigurator.DetailOptions<T>> setup) {
        if (!isMobile() || mobileSheet == null) {
            // Non-mobile or no Sheet: execute setup eagerly at build time.
            DefaultDetailOptions<T> opts = new DefaultDetailOptions<>(new Div());
            setup.accept(opts);
            wireDetailDiv(opts.getDiv(), opts.getSyncHandlers(), getComponent());
            return getConfigurator();
        }

        // Mobile + Sheet: truly lazy — nothing allocated until first tap.
        //
        // IMPORTANT: extract all captures into locals so the lambda holds NO implicit
        // reference to this configurator. Without this, the configurator would be pinned
        // in heap for the entire session lifetime — a significant leak at scale.
        // After first tap, setupRef and nodeRef are nulled so the consumer lambda and the
        // DefaultDetailOptions (and any view-level captures) are immediately eligible for GC.
        final Sheet localSheet = mobileSheet;
        final MasterDetailLayout<T> layout = getComponent();
        final List<Consumer<Object>> dispatchers = new ArrayList<>();
        final boolean[] initialized = { false };

        @SuppressWarnings("unchecked")
        final Consumer<MasterDetailConfigurator.DetailOptions<T>>[] setupRef = new Consumer[]{ setup };
        @SuppressWarnings("unchecked")
        final DefaultDetailOptions<T>[] nodeRef = new DefaultDetailOptions[]{ new DefaultDetailOptions<>(new Div()) };

        layout.addSyncDispatcher(item -> {
            if (!initialized[0]) {
                initialized[0] = true;
                // ▶ Only here are the detail components constructed (first tap).
                setupRef[0].accept(nodeRef[0]);
                collectSyncAware(nodeRef[0].getDiv(), dispatchers);
                dispatchers.addAll(nodeRef[0].getSyncHandlers());

                // Distribute to Sheet slots (same logic as wireMobileSheet).
                Header dh   = nodeRef[0].getDetailHeader();
                Footer df   = nodeRef[0].getDetailFooter();
                List<Component> body = nodeRef[0].getBodyComponents();
                if (dh != null) {
                    dh.addActions(localSheet.getCloseButton());
                    localSheet.setHeader(dh);
                }
                if (!body.isEmpty()) {
                    localSheet.setContent(body.toArray(new Component[0]));
                }
                if (df != null) {
                    localSheet.setFooter(df);
                }

                layout.add(localSheet);
                // Release one-shot state so GC can collect early.
                nodeRef[0] = null;
                setupRef[0] = null;
            }
            dispatchers.forEach(h -> h.accept(item));
            localSheet.open();
        });
        return getConfigurator();
    }

    /**
     * Collects all {@link DetailSyncAware} dispatchers from {@code div} and merges the
     * explicit {@code syncHandlers} list, then registers them all on {@code layout} and
     * appends {@code div} as a direct child.
     */
    private static <T> void wireDetailDiv(Div div,
                                          List<Consumer<Object>> syncHandlers,
                                          MasterDetailLayout<T> layout) {
        List<Consumer<Object>> dispatchers = new ArrayList<>();
        collectSyncAware(div, dispatchers);
        dispatchers.addAll(syncHandlers);
        dispatchers.forEach(h -> layout.addSyncDispatcher(item -> h.accept(item)));
        layout.add(div);
    }

    @Override
    public C withUrlSync(Function<T, String> idExtractor,
                         Function<String, Optional<T>> itemLoader) {
        getComponent().setUrlSync(UrlSelectionSync.<T>builder()
                .idExtractor(idExtractor)
                .itemLoader(itemLoader)
                .build());
        return getConfigurator();
    }

    @Override
    public C mobile(Div master) {
        getComponent().add(master);
        return getConfigurator();
    }

    @Override
    public C desktop(Div master, Div detail) {
        getComponent().add(master, detail);
        return getConfigurator();
    }

    @Override
    public C master(Consumer<MasterDetailConfigurator.MasterOptions<T>> configure) {
        DefaultMasterOptions<T> opts = new DefaultMasterOptions<>(new Div(), getComponent(), beanType, propertySet, highlightPartName, accentColorProvider);
        configure.accept(opts);
        opts.wire();
        return getConfigurator();
    }

    @Override
    public C detail(Consumer<MasterDetailConfigurator.DetailOptions<T>> configure) {
        DefaultDetailOptions<T> opts = new DefaultDetailOptions<>(new Div());
        configure.accept(opts);
        if (isMobile()) {
            Sheet sheet = getMobileSheet();
            if (sheet != null) {
                wireMobileSheet(opts, sheet);
            } else {
                // No Sheet: register explicit handlers only; nothing is attached to DOM.
                opts.getSyncHandlers().forEach(h ->
                        getComponent().addSyncDispatcher(item -> h.accept(item)));
            }
        } else {
            wireDetailDiv(opts.getDiv(), opts.getSyncHandlers(), getComponent());
        }
        return getConfigurator();
    }

    /**
     * Wires a fully-configured detail panel into a mobile {@link Sheet}.
     *
     * <p>Maps the three logical slots of the detail panel to the three Sheet slots:</p>
     * <ul>
     *   <li>detail {@code Header}  → {@code sheet.setHeader(...)} + injects the Sheet's own
     *       close button so the user can dismiss the Sheet</li>
     *   <li>body components (non-Header, non-Footer children) → {@code sheet.setContent(...)}</li>
     *   <li>detail {@code Footer}  → {@code sheet.setFooter(...)}</li>
     * </ul>
     *
     * <p>If no detail header was configured the Sheet keeps its built-in back/close header.</p>
     */
    private void wireMobileSheet(DefaultDetailOptions<T> opts, Sheet sheet) {
        List<Consumer<Object>> dispatchers = new ArrayList<>();
        collectSyncAware(opts.getDiv(), dispatchers);
        dispatchers.addAll(opts.getSyncHandlers());

        Header detailHeader = opts.getDetailHeader();
        Footer detailFooter = opts.getDetailFooter();
        List<Component> body = opts.getBodyComponents();

        if (detailHeader != null) {
            // Preserve close UX: inject the Sheet's own close button into the custom header.
            detailHeader.addActions(sheet.getCloseButton());
            sheet.setHeader(detailHeader);
        }
        if (!body.isEmpty()) {
            sheet.setContent(body.toArray(new Component[0]));
        }
        if (detailFooter != null) {
            sheet.setFooter(detailFooter);
        }

        dispatchers.forEach(h -> getComponent().addSyncDispatcher(item -> h.accept(item)));
        getComponent().addSyncDispatcher(__ -> sheet.open());
        getComponent().add(sheet);
    }

    // ── AbstractComponentConfigurator plumbing ────────────────────────────────

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.ofNullable(getComponent());
    }

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.ofNullable(getComponent());
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.ofNullable(getComponent());
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }

    // ── Inner: AbstractPanelOptions ───────────────────────────────────────────

    /**
     * Shared base for {@link DefaultMasterOptions} and {@link DefaultDetailOptions}.
     * <p>Holds the panel {@link Div} and the explicit sync-handler list, and provides
     * the Consumer-based {@code header/footer/content/styleName} methods so neither
     * subclass duplicates that logic.
     */
    private static abstract class AbstractPanelOptions<SELF> {

        protected final Div div;
        protected final List<Consumer<Object>> syncHandlers = new ArrayList<>();

        AbstractPanelOptions(Div div) {
            this.div = div;
        }

        protected abstract SELF self();

        public SELF header(Consumer<HeaderConfigurator<?>> configure) {
            if (configure != null) {
                Header h = new Header("");
                configure.accept(HeaderConfigurator.configure(h));
                div.addComponentAsFirst(h);
            }
            return self();
        }

        public SELF footer(Consumer<FooterConfigurator<?>> configure) {
            if (configure != null) {
                Footer f = new Footer();
                configure.accept(FooterConfigurator.configure(f));
                div.add(f);
            }
            return self();
        }

        public SELF content(Component... components) {
            if (components != null) div.add(components);
            return self();
        }

        public SELF styleName(String... styleNames) {
            if (styleNames != null) div.addClassNames(styleNames);
            return self();
        }

        Div getDiv() { return div; }
        List<Consumer<Object>> getSyncHandlers() { return syncHandlers; }
    }

    // ── Inner: DefaultMasterOptions ───────────────────────────────────────────

    /**
     * Consumer-receivable master panel options. Extends {@link AbstractPanelOptions}
     * for shared header/footer/content/styleName logic. Wires the listing + selection
     * machinery after the consumer has fully configured it.
     */
    private static final class DefaultMasterOptions<T>
            extends AbstractPanelOptions<MasterDetailConfigurator.MasterOptions<T>>
            implements MasterDetailConfigurator.MasterOptions<T> {

        private final MasterDetailLayout<T> layout;
        private final Class<T> beanType;
        private final PropertySet<?> propertySet;
        private final String highlightPartName;
        private final Function<T, String> accentColorProvider;
        private Function<T, ?> selectionKeyExtractor;
        private ListingBundle<?> builtBundle;

        DefaultMasterOptions(Div div, MasterDetailLayout<T> layout,
                             Class<T> beanType, PropertySet<?> propertySet,
                             String highlightPartName, Function<T, String> accentColorProvider) {
            super(div);
            this.layout = layout;
            this.beanType = beanType;
            this.propertySet = propertySet;
            this.highlightPartName = highlightPartName;
            this.accentColorProvider = accentColorProvider;
            div.addClassNames("master-view", "master-view-content");
        }

        @Override
        protected MasterDetailConfigurator.MasterOptions<T> self() { return this; }

        @Override
        public MasterDetailConfigurator.MasterOptions<T> listing(
                Consumer<MasterDetailConfigurator.ListingOptions<T>> configure) {
            MasterDetailConfigurator.ListingOptions<T> opts = createListingOptions();
            configure.accept(opts);
            builtBundle = extractBundle(opts);
            div.add(builtBundle);
            return this;
        }

        @SuppressWarnings("unchecked")
        private MasterDetailConfigurator.ListingOptions<T> createListingOptions() {
            if (propertySet != null) {
                return (MasterDetailConfigurator.ListingOptions<T>)
                        new DefaultPropertyBoxListingOptions(propertySet);
            }
            if (beanType == null) {
                throw new IllegalStateException(
                        "No bean type or PropertySet configured. " +
                        "Use MasterDetailBuilder.create(BeanClass.class) or " +
                        "MasterDetailBuilder.create(PropertySet) to enable listing().");
            }
            return new DefaultBeanListingOptions<>(beanType);
        }

        @SuppressWarnings("unchecked")
        private ListingBundle<T> extractBundle(MasterDetailConfigurator.ListingOptions<T> opts) {
            if (opts instanceof DefaultBeanListingOptions<?> bean) {
                return ((DefaultBeanListingOptions<T>) bean).build();
            }
            if (opts instanceof DefaultPropertyBoxListingOptions pb) {
                return (ListingBundle<T>) pb.build();
            }
            throw new IllegalStateException("Unknown ListingOptions: " + opts.getClass());
        }

        @Override
        public MasterDetailConfigurator.MasterOptions<T> selectionKey(Function<T, ?> keyExtractor) {
            this.selectionKeyExtractor = keyExtractor;
            return this;
        }

        @Override
        public MasterDetailConfigurator.MasterOptions<T> card() {
            div.addClassName("card");
            return this;
        }

        /**
         * Called by {@code master(Consumer)} after the consumer exits.
         * At this point {@code selectionKeyExtractor} is fully set, so the highlighter
         * is wired correctly regardless of call order within the lambda.
         *
         * <p>Key-extractor resolution order (first wins):</p>
         * <ol>
         *   <li>Explicit {@link #selectionKey} configured by the caller.</li>
         *   <li>Auto-detected bean identifier field ({@code @Id}, {@code @Identifier},
         *       or field named {@code id}) — ensures value-based comparison when the
         *       managed fetch callback re-fetches and returns new object instances.</li>
         *   <li>Object identity ({@code Function.identity()}) as last resort.</li>
         * </ol>
         */
        @SuppressWarnings("unchecked")
        void wire() {
            if (builtBundle != null) {
                ListingBundle<T> typedBundle = (ListingBundle<T>) builtBundle;
                // Required: CSS rules in master-detail-v2.css target
                // .mdl-master-grid::part(first-column-cell mdl-selected) — without this class on
                // the grid element the SelectionHighlighter's part-name generator has no effect.
                typedBundle.listing().getComponent().addClassName("mdl-master-grid");

                // Resolve effective key extractor.
                Function<T, ?> effectiveKey = selectionKeyExtractor;
                if (effectiveKey == null && beanType != null) {
                    effectiveKey = detectIdentifierExtractor(beanType);
                }

                SelectionHighlighter<T> highlighter =
                        new SelectionHighlighter<>(typedBundle.listing(), effectiveKey, highlightPartName);
                layout.setMasterBundle(typedBundle);
                layout.setMasterHighlighter(highlighter);

                typedBundle.listing().addItemClickListener(event -> {
                    highlighter.setHighlighted(event.getItem());
                    if (accentColorProvider != null) {
                        layout.setAccentClass(accentColorProvider.apply(event.getItem()));
                    }
                    layout.dispatchSync(event.getItem());
                });
            }
            layout.add(div);
        }

        /**
         * Detects the identifier field of {@code beanType} by checking, in order:
         * <ol>
         *   <li>Fields annotated with {@code @Identifier} (Holon Platform).</li>
         *   <li>Fields annotated with {@code @Id} (JPA).</li>
         *   <li>Fields named {@code id} (convention).</li>
         * </ol>
         * Walks the class hierarchy.  Returns {@code null} when nothing is found, so
         * {@link SelectionHighlighter} falls back to {@code Function.identity()}.
         */
        private static <B> Function<B, ?> detectIdentifierExtractor(Class<B> beanType) {
            Class<?> cls = beanType;
            while (cls != null && cls != Object.class) {
                for (java.lang.reflect.Field field : cls.getDeclaredFields()) {
                    if (isIdentifierField(field)) {
                        field.setAccessible(true);
                        java.lang.reflect.Field captured = field;
                        return item -> {
                            try { return captured.get(item); }
                            catch (IllegalAccessException e) { return item; }
                        };
                    }
                }
                cls = cls.getSuperclass();
            }
            return null;
        }

        private static boolean isIdentifierField(java.lang.reflect.Field field) {
            for (java.lang.annotation.Annotation a : field.getAnnotations()) {
                String name = a.annotationType().getSimpleName();
                if ("Id".equals(name) || "Identifier".equals(name)) return true;
            }
            return "id".equalsIgnoreCase(field.getName());
        }
    }

    // ── Inner: DefaultDetailOptions ───────────────────────────────────────────

    /**
     * Consumer-receivable detail panel options. Extends {@link AbstractPanelOptions}
     * for shared header/footer/content/styleName logic.
     *
     * <p>Overrides {@link #header} and {@link #footer} to capture references so that
     * {@link AbstractMasterDetailConfigurator#wireMobileSheet} can distribute them
     * to the correct {@link Sheet} slots (header / content / footer).</p>
     */
    private static final class DefaultDetailOptions<T>
            extends AbstractPanelOptions<MasterDetailConfigurator.DetailOptions<T>>
            implements MasterDetailConfigurator.DetailOptions<T> {

        private Header detailHeader;
        private Footer detailFooter;

        DefaultDetailOptions(Div div) {
            super(div);
            div.addClassNames("detail-view","detail-view-content");
        }

        @Override
        protected MasterDetailConfigurator.DetailOptions<T> self() { return this; }

        /** Overrides base to also capture the {@link Header} reference. */
        @Override
        public MasterDetailConfigurator.DetailOptions<T> header(Consumer<HeaderConfigurator<?>> configure) {
            if (configure != null) {
                Header h = new Header("");
                configure.accept(HeaderConfigurator.configure(h));
                div.addComponentAsFirst(h);
                this.detailHeader = h;
            }
            return self();
        }

        /** Overrides base to also capture the {@link Footer} reference. */
        @Override
        public MasterDetailConfigurator.DetailOptions<T> footer(Consumer<FooterConfigurator<?>> configure) {
            if (configure != null) {
                Footer f = new Footer();
                configure.accept(FooterConfigurator.configure(f));
                div.add(f);
                this.detailFooter = f;
            }
            return self();
        }

        @Override
        @SuppressWarnings("unchecked")
        public MasterDetailConfigurator.DetailOptions<T> withDetailSync(Consumer<T> handler) {
            if (handler != null) {
                syncHandlers.add(item -> handler.accept((T) item));
            }
            return this;
        }

        Header getDetailHeader() { return detailHeader; }
        Footer getDetailFooter() { return detailFooter; }

        /**
         * Returns children of the wrapper div that are neither the detail {@link Header}
         * nor the detail {@link Footer} — i.e. the "body" content components.
         */
        List<Component> getBodyComponents() {
            return div.getChildren()
                    .filter(c -> !(c instanceof Header) && !(c instanceof Footer))
                    .toList();
        }
    }

    // ── Inner: DefaultBeanListingOptions ─────────────────────────────────────

    private static final class DefaultBeanListingOptions<T>
            extends AbstractListingBundleConfigurer<T, MasterDetailConfigurator.ListingOptions<T>>
            implements MasterDetailConfigurator.ListingOptions<T> {

        DefaultBeanListingOptions(Class<T> beanType) {
            super(beanType);
        }

        @Override
        protected MasterDetailConfigurator.ListingOptions<T> getConfigurator() { return this; }

        ListingBundle<T> build() { return buildBundle(); }
    }

    // ── Inner: DefaultPropertyBoxListingOptions ───────────────────────────────

    /**
     * Slim adapter for PropertySet-backed master listings inside a master-detail context.
     * All fluent configuration is handled by {@link AbstractPropertyBoxListingAdapter};
     * only the class-specific {@link #build()} terminal is defined here.
     */
    private static final class DefaultPropertyBoxListingOptions
            extends AbstractPropertyBoxListingAdapter<MasterDetailConfigurator.ListingOptions<PropertyBox>>
            implements MasterDetailConfigurator.ListingOptions<PropertyBox> {

        DefaultPropertyBoxListingOptions(PropertySet<?> propertySet) {
            super(propertySet);
        }

        @Override
        protected MasterDetailConfigurator.ListingOptions<PropertyBox> self() { return this; }

        ListingBundle<PropertyBox> build() { return buildBundle(); }
    }

    // ── collectSyncAware ──────────────────────────────────────────────────────

    /**
     * Scans {@code root} and its descendants for {@link DetailSyncAware} instances,
     * appending a typed dispatch {@link Consumer} for each one to {@code out}.
     */
    @SuppressWarnings("unchecked")
    private static void collectSyncAware(Component root, List<Consumer<Object>> out) {
        if (root instanceof DetailSyncAware<?> aware) {
            out.add(item -> ((DetailSyncAware<Object>) aware).onItemSelected(item));
        }
        root.getChildren().forEach(child -> collectSyncAware(child, out));
    }
}
