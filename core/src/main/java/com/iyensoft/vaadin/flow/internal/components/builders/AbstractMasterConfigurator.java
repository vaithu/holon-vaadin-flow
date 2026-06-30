package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractFooterConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractHeaderConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractListingBundleConfigurer;
import com.holonplatform.vaadin.flow.vaadinplus.components.Footer;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.iyensoft.vaadin.flow.components.builders.MasterConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractMasterConfigurator<C extends MasterConfigurator<C>>
        extends AbstractComponentConfigurator<Div, C>
        implements MasterConfigurator<C> {

    private Header masterHeader;
    private Footer masterFooter;

    /**
     * Called when a {@link MasterConfigurator.ListingBundleNode} is finalised via {@code add()}.
     * Default is a no-op; overridden by {@code DefaultMasterNode} inside a master-detail
     * context to wire the click-to-sync listener.
     */
    private Consumer<ListingBundle<?>> listingAddedCallback = bundle -> {};

    /** Package-private — used by AbstractMasterDetailConfigurator.DefaultMasterNode only. */
    void setListingAddedCallback(Consumer<ListingBundle<?>> callback) {
        this.listingAddedCallback = callback;
    }

    public AbstractMasterConfigurator(Div component) {
        super(component);

        Components.configure(component)
                .styleNames("master-view", "master-view-content");
    }

    @Override
    public HeaderBuilder<C> header() {
        return new DefaultMasterHeaderBuilder<>(new Header(""),getComponent(),getConfigurator());
    }

    @Override
    public FooterBuilder<C> footer() {
        return new DefaultMasterFooterBuilder<>(new Footer(), getComponent(), getConfigurator());
    }

    @Override
    public C content(Component... components) {
        if (components != null) {
            for (var c : components) {
                if (c != null) getComponent().add(c);
            }
        }
        return getConfigurator();
    }

    @Override
    public C header(Header header) {
        replaceChild(masterHeader);
        masterHeader = header;
        if (header != null) getComponent().addComponentAsFirst(header);
        return getConfigurator();
    }

    @Override
    public C footer(Footer footer) {
        replaceChild(masterFooter);
        masterFooter = footer;
        if (footer != null) getComponent().add(footer);
        return getConfigurator();
    }

    /** Removes {@code old} from the component tree if it is currently attached. */
    private void replaceChild(Component old) {
        if (old != null) getComponent().remove(old);
    }

    @Override
    public C card() {
        getComponent().addClassName("card");
        return getConfigurator();
    }

    @Override
    public Optional<Header> getMasterHeader() { return Optional.ofNullable(masterHeader); }

    @Override
    public Optional<Footer> getMasterFooter() { return Optional.ofNullable(masterFooter); }

    @Override
    public <T> MasterConfigurator.ListingBundleNode<T, C> listing(Class<T> beanType) {
        return new DefaultListingBundleNode<>(beanType, getComponent(), getConfigurator(), listingAddedCallback);
    }

    @Override
    public MasterConfigurator.ListingBundleNode<PropertyBox, C> listing(PropertySet<?> propertySet) {
        return new DefaultPropertyBoxListingBundleNode<>(propertySet, getComponent(), getConfigurator(), listingAddedCallback);
    }

    @Override protected Optional<HasSize>    hasSize()    { return Optional.of(getComponent()); }
    @Override protected Optional<HasStyle>   hasStyle()   { return Optional.of(getComponent()); }
    @Override protected Optional<HasEnabled> hasEnabled() { return Optional.of(getComponent()); }
    @Override protected Optional<HasTooltip> hasTooltip() { return Optional.empty(); }

    // ── Inner: DefaultListingBundleNode ───────────────────────────────────────

    /**
     * Implements {@link MasterConfigurator.ListingBundleNode}.
     * Inherits the full build-time listing API from {@link AbstractListingBundleConfigurer}.
     * On {@link #add()}: builds the bundle, appends its parts to the master Div, then
     * invokes the parent's {@code listingAddedCallback} so the click-to-sync listener
     * can be wired when inside a master-detail context.
     */
    private static final class DefaultListingBundleNode<T, C extends MasterConfigurator<C>>
            extends AbstractListingBundleConfigurer<T, MasterConfigurator.ListingBundleNode<T, C>>
            implements MasterConfigurator.ListingBundleNode<T, C> {

        private final Div masterDiv;
        private final C parent;
        private final Consumer<ListingBundle<?>> listingAddedCallback;

        DefaultListingBundleNode(Class<T> beanType, Div masterDiv, C parent,
                                  Consumer<ListingBundle<?>> listingAddedCallback) {
            super(beanType);
            this.masterDiv = masterDiv;
            this.parent = parent;
            this.listingAddedCallback = listingAddedCallback;
        }

        @Override
        protected MasterConfigurator.ListingBundleNode<T, C> getConfigurator() { return this; }

        @Override
        public C add() {
            ListingBundle<T> bundle = buildBundle();
            Objects.requireNonNull(bundle, "Listing Bundle cannot be null");
//            masterDiv.add(bundle.header(), bundle.toolbar(), bundle.grid(), bundle.footer());
            masterDiv.add(bundle);
            listingAddedCallback.accept(bundle);  // no-op outside master-detail; wires sync inside it
            return parent;
        }
    }

    // ── Inner: DefaultPropertyBoxListingBundleNode ────────────────────────────

    /**
     * Slim adapter that extends {@link AbstractPropertyBoxListingAdapter} to expose a
     * {@link PropertySet}-backed listing inside a master panel. All fluent configuration
     * is handled by the base class; only the three class-specific operations are defined here.
     */
    private static final class DefaultPropertyBoxListingBundleNode<C extends MasterConfigurator<C>>
            extends AbstractPropertyBoxListingAdapter<MasterConfigurator.ListingBundleNode<PropertyBox, C>>
            implements MasterConfigurator.ListingBundleNode<PropertyBox, C> {

        private final Div masterDiv;
        private final C parent;
        private final Consumer<ListingBundle<?>> listingAddedCallback;

        DefaultPropertyBoxListingBundleNode(PropertySet<?> propertySet, Div masterDiv, C parent,
                                             Consumer<ListingBundle<?>> listingAddedCallback) {
            super(propertySet);
            this.masterDiv            = masterDiv;
            this.parent               = parent;
            this.listingAddedCallback = listingAddedCallback;
        }

        @Override
        protected MasterConfigurator.ListingBundleNode<PropertyBox, C> self() { return this; }

        @Override
        public C add() {
            ListingBundle<PropertyBox> bundle = buildBundle();
            masterDiv.add(bundle);
            listingAddedCallback.accept(bundle);
            return parent;
        }
    }

    // ── Inner builder: HeaderBuilder ──────────────────────────────────────────

    private static final class DefaultMasterHeaderBuilder<B extends MasterConfigurator<B>>
            extends AbstractHeaderConfigurator<MasterConfigurator.HeaderBuilder<B>>
            implements MasterConfigurator.HeaderBuilder<B> {

        private final Div div;
        private final B parent;

        public DefaultMasterHeaderBuilder(Header component, Div div, B parent) {
            super(component);
            this.div = div;
            this.parent = parent;
        }

        @Override
        public B add() {
            div.addComponentAsFirst(getComponent());
            return parent;
        }

        @Override
        protected MasterConfigurator.HeaderBuilder<B> getConfigurator() { return this; }
    }

    // ── Inner builder: FooterBuilder ──────────────────────────────────────────

    private static final class DefaultMasterFooterBuilder<B extends MasterConfigurator<B>>
            extends AbstractFooterConfigurator<MasterConfigurator.FooterBuilder<B>>
            implements MasterConfigurator.FooterBuilder<B> {

        private final Div div;
        private final B parent;

        public DefaultMasterFooterBuilder(Footer component, Div div, B parent) {
            super(component);
            this.div = div;
            this.parent = parent;
        }

        @Override
        protected MasterConfigurator.FooterBuilder<B> getConfigurator() { return this; }

        @Override
        public B add() {
            div.addComponentAsFirst(getComponent());
            return parent;
        }
    }
}


