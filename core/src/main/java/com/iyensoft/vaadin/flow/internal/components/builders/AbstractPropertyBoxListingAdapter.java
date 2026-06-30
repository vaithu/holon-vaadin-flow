package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.components.ListingBundleConfigurer;
import com.holonplatform.vaadin.flow.components.PropertyListingBundleBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Shared base for PropertyBox listing adapters used by both the standalone
 * {@link com.iyensoft.vaadin.flow.components.builders.MasterConfigurator.ListingBundleNode}
 * path and the
 * {@link com.iyensoft.vaadin.flow.components.builders.MasterDetailConfigurator.ListingOptions}
 * path.
 *
 * <p>Wraps a {@link PropertyListingBundleBuilder} and delegates all
 * {@link ListingBundleConfigurer} methods to it. Concrete subclasses supply
 * {@link #self()} (returning {@code this} as the interface type) and the terminal
 * {@code add()} or {@code build()} operation.
 *
 * @param <SELF> concrete self-type returned by each fluent method
 */
abstract class AbstractPropertyBoxListingAdapter<SELF> {

    protected final PropertySet<?>              propertySet;
    protected final PropertyListingBundleBuilder delegate;

    AbstractPropertyBoxListingAdapter(PropertySet<?> propertySet) {
        this.propertySet = Objects.requireNonNull(propertySet, "propertySet must not be null");
        this.delegate    = Components.listing(propertySet);
    }

    /** Subclasses return {@code this} cast to the concrete interface type. */
    protected abstract SELF self();

    /** Builds the bundle from the delegate. Used by concrete terminal operations. */
    protected ListingBundle<PropertyBox> buildBundle() {
        ListingBundle<PropertyBox> bundle = delegate.build();
        Objects.requireNonNull(bundle, "Listing Bundle cannot be null");
        return bundle;
    }

    // ── Fetch ─────────────────────────────────────────────────────────────────

    public SELF fetch(ListingBundleConfigurer.FetchCallback<PropertyBox> cb) {
        delegate.fetch(cb::fetch); return self();
    }

    public SELF fetch(ListingBundleConfigurer.FilteredFetchCallback<PropertyBox> cb) {
        delegate.fetch(cb::fetch); return self();
    }

    /** {@code PropertyListingBundleBuilder} has no column-aware path — the columns arg is dropped. */
    public SELF fetch(ListingBundleConfigurer.ColumnAwareFilteredFetchCallback<PropertyBox> cb) {
        delegate.fetch((q, text, filter, sort) -> cb.fetch(q, text, filter, sort, List.of()));
        return self();
    }

    // ── Columns / headers ─────────────────────────────────────────────────────

    /** No-op: PropertyListing columns are fixed by the PropertySet. */
    public SELF columns(String... cols) { return self(); }

    /** No-op. */
    public SELF hidden(String... cols) { return self(); }

    public SELF header(String column, String label) {
        propertySet.forEach(p -> {
            if (p instanceof PathProperty<?> pp && column.equals(pp.relativeName())) {
                delegate.header(p, label);
            }
        });
        return self();
    }

    public SELF header(String column, Localizable localizable) {
        String resolved = LocalizationProvider.localize(localizable)
                .orElse(localizable.getMessage() != null ? localizable.getMessage() : column);
        return header(column, resolved);
    }

    public SELF header(String column, String defaultLabel, String messageCode) {
        return header(column, Localizable.builder().message(defaultLabel).messageCode(messageCode).build());
    }

    // ── Grid header ───────────────────────────────────────────────────────────

    public SELF gridHeader(Component... components)                   { delegate.gridHeader(components);          return self(); }
    public SELF gridHeader(String title, Component... contextActions) { delegate.gridHeader(title, contextActions); return self(); }
    public SELF gridHeader(String title)                              { delegate.gridHeader(title);               return self(); }

    // ── Pagination ────────────────────────────────────────────────────────────

    public SELF pageSizes(Integer... sizes)    { delegate.pageSizes(sizes);        return self(); }
    public SELF defaultPageSize(int size)      { delegate.defaultPageSize(size);   return self(); }
    public SELF paginated()                    { delegate.paginated();             return self(); }
    public SELF virtualScroll()                { delegate.virtualScroll();         return self(); }
    public SELF paginated(boolean paginated)   { delegate.paginated(paginated);    return self(); }

    // ── Search ────────────────────────────────────────────────────────────────

    public SELF search(String placeholder) {
        delegate.search(placeholder); return self();
    }

    public SELF search(Localizable localizable) {
        String p = LocalizationProvider.localize(localizable)
                .orElse(localizable.getMessage() != null ? localizable.getMessage() : "");
        delegate.search(p);
        return self();
    }

    public SELF search(String defaultPlaceholder, String messageCode) {
        return search(Localizable.builder().message(defaultPlaceholder).messageCode(messageCode).build());
    }

    // ── Filter panel ──────────────────────────────────────────────────────────

    public SELF withFilterPanel()             { delegate.withFilterPanel();  return self(); }

    /**
     * Enables the filter panel. Note: {@code PropertyListingBundleBuilder} does not expose
     * a per-panel advanced-mode toggle, so the {@code advancedMode} flag is ignored here.
     * Use {@link #advancedSearchLabel(String)} to customise the toggle label instead.
     */
    public SELF withFilterPanel(boolean advancedMode) { delegate.withFilterPanel(); return self(); }

    public SELF advancedSearchLabel(String label)     { delegate.advancedSearchLabel(label); return self(); }
    public SELF retainFilterValues(boolean retain)    { delegate.retainFilterValues(retain); return self(); }

    // ── Menu actions ──────────────────────────────────────────────────────────

    public SELF withMenuAction(String label, Runnable action)                  { delegate.withMenuAction(label, action);      return self(); }
    public SELF withMenuAction(VaadinIcon icon, String label, Runnable action) { delegate.withMenuAction(icon, label, action); return self(); }
    public SELF importAction(Runnable action)                                  { delegate.importAction(action);               return self(); }
    public SELF exportAction(Runnable action)                                  { delegate.exportAction(action);               return self(); }

    // ── No-ops (not applicable to PropertyListing) ────────────────────────────

    public SELF multiSelect()                                                                                { return self(); }
    public SELF autoCreateColumns(boolean autoCreate)                                                        { return self(); }
    public SELF onItemClick(ComponentEventListener<ItemClickEvent<PropertyBox>> listener)                    { return self(); }
    public SELF onItemClickListener(ViewMode mode, ComponentEventListener<ItemClickEvent<PropertyBox>> l)    { return self(); }
    public SELF viewModeSupplier(Supplier<ViewMode> supplier)                                                { return self(); }
    public SELF mobileViewColumn(Renderer<PropertyBox> renderer)                                             { return self(); }
    public SELF mobileViewHeader(String text)                                                                { return self(); }
    public SELF mobileViewHeader(Component component)                                                        { return self(); }

    // ── Size no-ops ───────────────────────────────────────────────────────────

    public SELF width(String width)         { return self(); }
    public SELF height(String height)       { return self(); }
    public SELF minWidth(String minWidth)   { return self(); }
    public SELF maxWidth(String maxWidth)   { return self(); }
    public SELF minHeight(String minHeight) { return self(); }
    public SELF maxHeight(String maxHeight) { return self(); }

    // ── Style no-ops ──────────────────────────────────────────────────────────

    public SELF styleNames(String... styleNames) { return self(); }
    public SELF styleName(String styleName)      { return self(); }

    // ── Component / element no-ops ────────────────────────────────────────────

    public SELF id(String id)                                                                 { return self(); }
    public SELF visible(boolean visible)                                                      { return self(); }
    public SELF elementConfiguration(Consumer<Element> element)                               { return self(); }
    public SELF withThemeName(String themeName)                                               { return self(); }
    public SELF withEventListener(String eventType, DomEventListener listener)                { return self(); }
    public SELF withEventListener(String eventType, DomEventListener listener, String filter) { return self(); }
    public SELF withAttachListener(ComponentEventListener<AttachEvent> listener)              { return self(); }
    public SELF withDetachListener(ComponentEventListener<DetachEvent> listener)              { return self(); }
}
