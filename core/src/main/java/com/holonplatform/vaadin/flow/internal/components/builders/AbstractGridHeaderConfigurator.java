package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.components.builders.GridHeaderConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

public abstract class AbstractGridHeaderConfigurator<C extends GridHeaderConfigurator<C>>
        extends AbstractComponentConfigurator<GridHeader, C>
        implements GridHeaderConfigurator<C> {

    /**
     * Constructor.
     *
     * @param gridHeader The GridHeader instance (not getConfigurator())
     */
    public AbstractGridHeaderConfigurator(GridHeader gridHeader) {
        super(gridHeader);
    }

    @Override
    public C grid(Grid<?> grid) {
        // Delegate grid wiring to the GridHeader itself
        getComponent().setGrid(grid);
        return getConfigurator();
    }

    @Override
    public C listing(PropertyListing propertyListing) {
        Component component = propertyListing.getComponent();
        if (component instanceof Grid<?>) {
            return grid((Grid<?>) component);
        }
        throw new IllegalArgumentException("Provided PropertyListing is not an instance of Grid<?>");
    }

    @Override
    public C listing(BeanListing<?> beanListing) {
        Component component = beanListing.getComponent();
        if (component instanceof Grid<?>) {
            return grid((Grid<?>) component);
        }
        throw new IllegalArgumentException("Provided BeanListing is not an instance of Grid<?>");
    }

    @Override
    public C defaultActions(Component... components) {
        // Delegate state and logic to GridHeader
        getComponent().setDefaultActions(components);
        return getConfigurator();
    }

    @Override
    public C contextActions(Component... components) {
        // Delegate state and logic to GridHeader
        getComponent().setContextActions(components);
        return getConfigurator();
    }

    // ---- AbstractComponentConfigurator hooks ----

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }

}
