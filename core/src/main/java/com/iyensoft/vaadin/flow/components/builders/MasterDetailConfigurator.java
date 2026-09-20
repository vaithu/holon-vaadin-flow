package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.iyensoft.vaadin.flow.components.ListingBundleConfigurer;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.iyensoft.vaadin.flow.components.builders.FooterConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.iyensoft.vaadin.flow.components.builders.HeaderConfigurator;
import com.iyensoft.vaadin.flow.components.builders.MaterialHeaderConfigurator;
import com.iyensoft.vaadin.flow.components.Sheet;
import com.iyensoft.vaadin.flow.components.MasterDetailAccent;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.holonplatform.vaadin.flow.components.support.ViewMode;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultMasterDetailConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.ItemIndexProvider;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.SerializableSupplier;

/**
 * Fluent builder-phase interface for {@link MasterDetailLayout}.
 *
 * <p>Uses Consumer-based nesting — lambda scope naturally provides indentation
 * that mirrors the component hierarchy, with no {@code add()} escape-hatches:</p>
 * <pre>{@code
 * MasterDetailLayout<Order> mdl = MasterDetailBuilder.create(Order.class)
 *     .master(m -> m
 *         .listing(l -> l
 *             .columns("id", "customer", "status")
 *             .fetch((q, t, s) -> service.fetch(q)))
 *         .selectionKey(Order::getId)
 *         .card())
 *     .detail(d -> d
 *         .header(h -> h.heading("Order Details"))
 *         .withDetailSync(o -> populate(o))
 *         .content(formPanel))
 *     .build();
 * }</pre>
 *
 * @param <T> item type of the master listing
 * @param <C> self type for fluent chaining
 */
public interface MasterDetailConfigurator<T, C extends MasterDetailConfigurator<T, C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    // ── Simple modes ──────────────────────────────────────────────────────────

    /** Adds {@code master} as the single child — mobile / single-panel mode. */
    C mobile(Div master);

    /** Adds pre-built {@code master} and {@code detail} children — desktop side-by-side mode. */
    C desktop(Div master, Div detail);

    // ── Consumer-based nested config ──────────────────────────────────────────

    /**
     * Configures the master panel via a Consumer.
     * Lambda indentation mirrors the component hierarchy; no {@code add()} needed.
     *
     * <pre>{@code
     * .master(m -> m
     *     .listing(l -> l.columns("id", "name").fetch((q, t, s) -> stream))
     *     .selectionKey(Order::getId)
     *     .card())
     * }</pre>
     */
    C master(Consumer<MasterOptions<T>> configure);

    /**
     * Configures the detail panel via a Consumer.
     *
     * <pre>{@code
     * .detail(d -> d
     *     .header(h -> h.heading("Order Details"))
     *     .withDetailSync(o -> label.setText(o.getName()))
     *     .content(formPanel))
     * }</pre>
     */
    C detail(Consumer<DetailOptions<T>> configure);

    // ── Top-level convenience ─────────────────────────────────────────────────

    /**
     * Overrides the CSS part name added to the selected grid row.
     * Defaults to {@code "mdl-selected"}.
     *
     * <p>Use this when a view needs a different accent colour or indicator style.
     * The supplied name must match the {@code ::part(row &lt;name&gt;)} rule in your CSS file.</p>
     *
     * <pre>{@code
     * Components.masterDetail(Order.class)
     *     .withHighlightPartName("my-selected")   // CSS: ::part(row my-selected) { … }
     *     .master(…)
     * }</pre>
     */
    C withHighlightPartName(String partName);

    /**
     * Sets the current viewport mode. When {@link ViewMode#isMobile()} is {@code true} the
     * detail panel is not added to the DOM — only sync handlers are registered so that any
     * mobile overlay (e.g. a {@link Sheet}) populated via {@link #withDetailSync} still works.
     */
    C viewMode(ViewMode viewMode);

    /**
     * Registers a top-level detail-sync handler — no {@code detail()} needed.
     * Useful on mobile where the overlay is a {@link Sheet} rather than an inline panel.
     *
     * <pre>{@code
     * MasterDetailBuilder.create(Order.class)
     *     .viewMode(ViewMode.MOBILE)
     *     .master(m -> m.listing(l -> l.fetch(...)).selectionKey(Order::getId))
     *     .withDetailSync(o -> sheet.populate(o))
     *     .build();
     * }</pre>
     */
    C withDetailSync(SerializableConsumer<T> handler);

    /**
     * On mobile, wraps the detail panel inside the supplied {@link Sheet} and auto-opens it
     * on every row click. The Sheet should be pre-configured (title, side) but have no content
     * yet — the builder sets its content at build / first-tap time.
     */
    C withMobileSheet(Sheet sheet);

    /** Convenience — auto-creates a {@link Sheet} with the given {@code side}. */
    C withMobileSheet(Sheet.Side side);

    /**
     * Declares a lazily-built detail panel. The setup Consumer executes only on the first
     * row click; subsequent clicks update the already-built panel in-place and re-open the Sheet.
     *
     * <pre>{@code
     * .lazyDetail(d -> d
     *     .header(h -> h.heading("Order"))
     *     .content(new OrderSummaryTab())    // built ONLY on first tap
     *     .withDetailSync(o -> { }))
     * }</pre>
     *
     * <p>Without a mobile {@link ViewMode} + {@link #withMobileSheet}, the setup runs eagerly
     * at build time — identical to {@code detail(...)}.
     */
    C lazyDetail(Consumer<DetailOptions<T>> setup);

    /**
     * Wires URL {@code ?id=} deep-link synchronisation. The extractor converts an item to its
     * URL string; the loader finds an item by that string.
     */
    C withUrlSync(SerializableFunction<T, String> idExtractor, SerializableFunction<String, Optional<T>> itemLoader);

    /**
     * Wires URL deep-link synchronisation on a custom query-parameter name.
     *
     * @param idExtractor converts an item to its URL string
     * @param itemLoader  finds an item by that string
     * @param paramName   the query-parameter name to read and write (e.g. {@code "customer"});
     *                    {@code null} or blank falls back to {@code id}
     */
    C withUrlSync(SerializableFunction<T, String> idExtractor, SerializableFunction<String, Optional<T>> itemLoader,
                  String paramName);

    /**
     * Lets the layout select its own initial row on desktop: the {@code ?id=} item when the
     * URL carries one, otherwise the first row (see {@link #withInitialItem(SerializableSupplier)}).
     *
     * <p>Prefer this over wiring the initial selection from the view with an attach listener
     * and/or a navigation callback such as {@code @OnShow}. The layout resolves the target
     * from the browser {@code Location} itself, so it does not depend on navigator-injected
     * fields being populated yet — a deep link selects the requested item directly instead
     * of first selecting row one and then replacing it, which costs an extra backend query.
     * Re-navigation to a different {@code ?id=} is picked up too, and the same target is
     * never applied twice.</p>
     *
     * <pre>{@code
     * Components.masterDetail(Product.class)
     *     .viewMode(mode)
     *     .withUrlSync(p -> String.valueOf(p.getId()), id -> service.findById(Long.parseLong(id)))
     *     .withInitialItem(service::findFirst)
     *     .withAutoSelect()
     *     .master(...)
     *     .build();
     * }</pre>
     */
    C withAutoSelect();

    /**
     * Supplies a direct backend loader used to auto-select the first master row on desktop
     * (see {@code MasterDetailLayout#selectFirst}), instead of pulling it from the master
     * listing's lazy data view.
     *
     * <p>Without this, the auto-select-first-row feature calls
     * {@code ItemListing.getFirstItem()}, which — on a lazy {@code CallbackDataProvider} —
     * issues its <strong>own</strong> backend query (via {@code GridLazyDataView.getItem(0)})
     * whenever index 0 is not yet cached. Since this typically runs before the master grid's
     * own rendering fetch completes, every initial page load ends up firing two separate
     * backend queries for what is effectively the same data set. Passing a direct loader
     * (e.g. {@code productService::findFirst}) avoids that redundant fetch entirely.</p>
     *
     * <pre>{@code
     * Components.masterDetail(Product.class)
     *     .withInitialItem(productService::findFirst)
     *     .master(m -> m.listing(l -> l.fetch((q, t, f, s) -> productService.fetch(...))))
     *     .build();
     * }</pre>
     *
     * @param initialItemLoader supplier resolving the item to auto-select first (not null)
     */
    C withInitialItem(SerializableSupplier<Optional<T>> initialItemLoader);

    /**
     * Registers the callback that resolves an item's row index in the master listing, so a
     * programmatically selected row — the deep-linked {@code ?id=} item, or the first row —
     * is scrolled into view.
     *
     * <p>A Grid renders only a window of rows. Without this callback a lazily loaded listing
     * cannot locate a row it has not fetched, so a deep link to an item further down the list
     * populates the detail panel while the master row stays off-screen and apparently
     * unselected.</p>
     *
     * <p>The callback runs at most once per programmatic selection and never on a row click,
     * so a single lightweight "how many rows sort before this one" query is enough. Return
     * {@code null} when the item is not part of the current data set.</p>
     *
     * <pre>{@code
     * Components.masterDetail(Product.class)
     *     .withInitialItem(productService::findFirst)
     *     .withItemIndexProvider((item, query) -> productService.indexOf(item).orElse(null))
     *     .build();
     * }</pre>
     *
     * @param itemIndexProvider maps an item to its zero-based row index (not null)
     */
    C withItemIndexProvider(ItemIndexProvider<T, ?> itemIndexProvider);


    /**
     * Dynamically changes the accent colour of the left-bar selection indicator per item.
     *
     * <p>The provider receives the clicked item and returns a CSS class name that will be
     * applied to the {@code .master-detail-container} element.  Each class must define
     * {@code --mdl-selected-accent} and {@code --mdl-selected-bg} — the pre-built
     * {@link MasterDetailAccent} enum covers the most common variants:</p>
     *
     * <pre>{@code
     * Components.masterDetail(Order.class)
     *     .withAccentColorProvider(order -> switch (order.getStatus()) {
     *         case ACTIVE   -> MasterDetailAccent.SUCCESS.cssClass();
     *         case OVERDUE  -> MasterDetailAccent.DANGER.cssClass();
     *         case PENDING  -> MasterDetailAccent.WARNING.cssClass();
     *         default       -> MasterDetailAccent.DEFAULT.cssClass();
     *     })
     *     .master(m -> m.listing(l -> l.fetch(...)))
     *     .detail(d -> d.withDetailSync(o -> populate(o)))
     *     .build();
     * }</pre>
     *
     * <p>Custom colours: define your own CSS class and return its name directly —
     * no {@link MasterDetailAccent} value needed:</p>
     * <pre>{@code
     * // CSS:  .my-teal { --mdl-selected-accent: #0d9488; --mdl-selected-bg: rgba(13,148,136,.08); }
     * .withAccentColorProvider(item -> "my-teal")
     * }</pre>
     *
     * <p>Java code assigns only the class name; all colour values live in CSS.</p>
     *
     * @param cssClassProvider function that maps a selected item to a CSS class name;
     *                         may return {@code null} to revert to the default blue
     * @return this configurator
     */
    C withAccentColorProvider(SerializableFunction<T, String> cssClassProvider);

    /**
     * Adds a per-item, always-on CSS part name to every cell of the master row —
     * independent of grid selection — so a business/status condition can tint the
     * <em>whole</em> row (including columns rendered outside the item's own content,
     * e.g. an auto-added multi-select checkbox column) rather than just the content
     * cell itself.
     *
     * <p>The returned part name (or {@code null} for no part) is combined with the
     * grid-selection part name ({@link #withHighlightPartName(String)}) when both are
     * present on the same row, so the two indicators never conflict.</p>
     *
     * <pre>{@code
     * Components.masterDetail(Product.class)
     *     .master(m -> m.listing(l -> l
     *             .mobileViewColumn(mobileColumn(product -> product.isActive()
     *                     ? RowVariant.SELECTED : RowVariant.EXCEPTION))))
     *     .withRowPartNameGenerator(product -> product.isActive() ? "mdl-active" : null)
     *     .build();
     *
     * // CSS: .mdl-master-grid::part(mdl-active) { background-color: var(--primary-soft); }
     * }</pre>
     *
     * @param partNameGenerator function that maps an item to a CSS part name, or
     *                          {@code null} for no extra part; may itself return
     *                          {@code null} for individual items
     * @return this configurator
     */
    C withRowPartNameGenerator(SerializableFunction<T, String> partNameGenerator);

    // ── Inner options (no add(), no C back-reference) ─────────────────────────

    /**
     * Master panel options. Configure the listing, selection key, header, footer, and content.
     * All methods return {@code this} for chaining within the Consumer lambda.
     */
    interface MasterOptions<T> {
        /** Embeds a listing bundle. The full {@link ListingBundleConfigurer} API is available inside. */
        MasterOptions<T> listing(Consumer<ListingOptions<T>> configure);
        /** Stable selection key — enables row highlighting after data refresh. */
        MasterOptions<T> selectionKey(SerializableFunction<T, ?> keyExtractor);
        /** Configures the master panel header. */
        MasterOptions<T> header(Consumer<HeaderConfigurator<?>> configure);

        /** Configures the master panel header using a Material 3 header instead. */
        MasterOptions<T> materialHeader(Consumer<MaterialHeaderConfigurator<?>> configure);
        /** Configures the master panel footer. */
        MasterOptions<T> footer(Consumer<FooterConfigurator<?>> configure);
        /** Appends raw components to the master panel body. */
        MasterOptions<T> content(Component... components);
        /** Wraps the master panel in a card-styled container. */
        MasterOptions<T> card();
        /** Adds one or more CSS class names to the master panel. */
        MasterOptions<T> styleName(String... styleNames);
    }

    /**
     * Detail panel options. Configure the header, footer, content, and sync handlers.
     * All methods return {@code this} for chaining within the Consumer lambda.
     */
    interface DetailOptions<T> {
        /** Configures the detail panel header. */
        DetailOptions<T> header(Consumer<HeaderConfigurator<?>> configure);

        /** Configures the detail panel header using a Material 3 header instead. */
        DetailOptions<T> materialHeader(Consumer<MaterialHeaderConfigurator<?>> configure);
        /** Configures the detail panel footer. */
        DetailOptions<T> footer(Consumer<FooterConfigurator<?>> configure);
        /** Appends raw components to the detail panel body. */
        DetailOptions<T> content(Component... components);
        /** Registers a handler called on every row selection in the master grid. */
        DetailOptions<T> withDetailSync(SerializableConsumer<T> handler);
        /** Adds one or more CSS class names to the detail panel. */
        DetailOptions<T> styleName(String... styleNames);
    }

    /**
     * Listing options — the full {@link ListingBundleConfigurer} API.
     * Consumer scope terminates the listing configuration; no {@code add()} needed.
     */
    interface ListingOptions<T> extends ListingBundleConfigurer<T, ListingOptions<T>> {
    }

    // ── Factories ─────────────────────────────────────────────────────────────

    /** Configures an existing {@link MasterDetailLayout} with a known bean type. */
    static <T> BaseMasterDetailConfigurator<T> configure(MasterDetailLayout<T> layout, Class<T> beanType) {
        return new DefaultMasterDetailConfigurator<>(layout, beanType);
    }

    /**
     * Configures an existing {@link MasterDetailLayout} backed by a Holon {@link PropertySet}.
     * The item type is fixed to {@link PropertyBox}.
     */
    static BaseMasterDetailConfigurator<PropertyBox> configure(MasterDetailLayout<PropertyBox> layout,
                                                               PropertySet<?> propertySet) {
        return new DefaultMasterDetailConfigurator<>(layout, propertySet);
    }

    interface BaseMasterDetailConfigurator<T>
            extends MasterDetailConfigurator<T, BaseMasterDetailConfigurator<T>> {
    }
}
