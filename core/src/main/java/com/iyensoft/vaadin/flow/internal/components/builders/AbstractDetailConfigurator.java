package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractFooterConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractHeaderConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Footer;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.iyensoft.vaadin.flow.components.builders.DetailConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractDetailConfigurator<C extends DetailConfigurator<C>>
        extends AbstractComponentConfigurator<Div, C>
        implements DetailConfigurator<C> {

    /** Currently attached Header component, if any. */
    private Header detailHeader;

    /** Currently attached Footer component, if any. */
    private Footer detailFooter;

    /**
     * Explicit sync handlers registered via {@link #withDetailSync(Consumer)}.
     * Typed as {@code Consumer<Object>} to avoid a {@code <T>} parameter on this class;
     * the correct type is captured at the lambda boundary in {@link #withDetailSync}.
     * Exposed to {@code DefaultDetailNode.add()} via {@link #getSyncHandlers()}.
     */
    private final List<Consumer<Object>> syncHandlers = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public AbstractDetailConfigurator(Div component) {
        super(component);
    }

    // ── DetailConfigurator API ──────────────────��─────────────────────────────

    /**
     * Returns a fluent {@link HeaderBuilder} that configures a new {@link Header}.
     * Call {@link HeaderBuilder#add()} to attach it and return to this configurator.
     */
    @Override
    public HeaderBuilder<C> header() {
        return new DefaultDetailHeaderBuilder<>(this);
    }

    /**
     * Returns a fluent {@link FooterBuilder} that configures a new {@link Footer}.
     * Call {@link FooterBuilder#add()} to attach it and return to this configurator.
     */
    @Override
    public FooterBuilder<C> footer() {
        return new DefaultDetailFooterBuilder<>(this);
    }

    /**
     * Appends components to the content area of the underlying {@link Div}.
     */
    @Override
    public C content(Component... components) {
        if (components != null) {
            getComponent().add(components);
        }
        return getConfigurator();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> C withDetailSync(Consumer<T> handler) {
        if (handler != null) {
            syncHandlers.add(item -> handler.accept((T) item));
        }
        return getConfigurator();
    }

    /** Exposes registered explicit sync handlers to {@code DefaultDetailNode.add()}. */
    protected List<Consumer<Object>> getSyncHandlers() {
        return syncHandlers;
    }

    /**
     * Returns the underlying {@link Div} component.
     * Provided as a named accessor so that private nested subclasses (e.g., {@code DefaultDetailNode})
     * can call it without triggering ECJ's stricter cross-package protected-access checks.
     */
    protected Div getDiv() {
        return getComponent();
    }

    /**
     * Replaces the current header with the given one, inserting it as the
     * first child so it renders above any content.
     */
    @Override
    public C header(Header header) {
        if (this.detailHeader != null) {
            getComponent().remove(this.detailHeader);
        }
        this.detailHeader = header;
        if (header != null) {
            getComponent().addComponentAsFirst(header);
        }
        return getConfigurator();
    }

    /**
     * Replaces the current footer with the given one, appending it as the
     * last child so it renders below any content.
     */
    @Override
    public C footer(Footer footer) {
        if (this.detailFooter != null) {
            getComponent().remove(this.detailFooter);
        }
        this.detailFooter = footer;
        if (footer != null) {
            getComponent().add(footer);
        }
        return getConfigurator();
    }

    @Override
    public Optional<Header> getDetailHeader() {
        return Optional.ofNullable(detailHeader);
    }

    @Override
    public Optional<Footer> getDetailFooter() {
        return Optional.ofNullable(detailFooter);
    }

    // ── HasSize / HasStyle / HasEnabled / HasTooltip ──────────────────────────

    /** {@link Div} implements {@link HasSize}. */
    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    /** {@link Div} implements {@link HasStyle}. */
    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    /** {@link Div} implements {@link HasEnabled}. */
    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
    }

    /** {@link Div} does not implement {@link HasTooltip}. */
    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }

    // ── Inner builder: HeaderBuilder ──────────────────────────────────────────

    /**
     * Fluent header builder bound to a parent {@link AbstractDetailConfigurator}.
     *
     * <p>Extends {@link AbstractHeaderConfigurator} so all header-configuration methods
     * ({@code heading()}, {@code actions()}, {@code breadcrumb()}, etc.) are available.
     * {@link #add()} attaches the built header to the parent and returns the parent
     * configurator.
     */
    private static final class DefaultDetailHeaderBuilder<C extends DetailConfigurator<C>>
            extends AbstractHeaderConfigurator<HeaderBuilder<C>>
            implements HeaderBuilder<C> {

        private final AbstractDetailConfigurator<C> parent;

        DefaultDetailHeaderBuilder(AbstractDetailConfigurator<C> parent) {
            // A fresh Header with an empty title; caller sets it via heading(...)
            super(new Header(""));
            this.parent = parent;
        }

        /** Attaches the configured header to the parent and returns the parent configurator. */
        @Override
        public C add() {
            parent.header(getComponent());
            return parent.getConfigurator();
        }

        @Override
        protected HeaderBuilder<C> getConfigurator() {
            return this;
        }
    }

    // ── Inner builder: FooterBuilder ──────────────────────────────────────────

    /**
     * Fluent footer builder bound to a parent {@link AbstractDetailConfigurator}.
     *
     * <p>Extends {@link AbstractFooterConfigurator} so all footer-configuration methods
     * ({@code prefix()}, {@code actions()}, {@code legal()}, etc.) are available.
     * {@link #add()} attaches the built footer to the parent and returns the parent
     * configurator.
     */
    private static final class DefaultDetailFooterBuilder<C extends DetailConfigurator<C>>
            extends AbstractFooterConfigurator<FooterBuilder<C>>
            implements FooterBuilder<C> {

        private final AbstractDetailConfigurator<C> parent;

        DefaultDetailFooterBuilder(AbstractDetailConfigurator<C> parent) {
            super(new Footer());
            this.parent = parent;
        }

        /** Attaches the configured footer to the parent and returns the parent configurator. */
        @Override
        public C add() {
            parent.footer(footer());
            return parent.getConfigurator();
        }

        @Override
        protected FooterBuilder<C> getConfigurator() {
            return this;
        }
    }
}
