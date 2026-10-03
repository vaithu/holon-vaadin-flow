package com.iyensoft.vaadin.flow.internal.components.masterdetail;

import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.components.Sheet;
import com.iyensoft.vaadin.flow.components.Components;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailBuilder;
import com.holonplatform.vaadin.flow.components.support.ViewMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.NotFoundException;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Reusable base for entity master-detail screens.
 *
 * <p>The framework owns the common master-detail lifecycle:
 * selection, responsive placement, URL synchronization, lazy detail creation,
 * auto-selection, and empty-state behavior.</p>
 *
 * <p>Applications implement the entity-specific ID and loading hooks, then configure
 * the master-detail builder after their dependencies have been assigned. The default
 * URL parameter is {@code id}.</p>
 *
 * @param <T> master entity type
 */
public abstract class EntityMasterDetailView<T> extends Div implements BeforeEnterObserver {

    private final Class<T> entityType;
    private MasterDetailLayout<T> layout;
    private final String itemName;

    protected EntityMasterDetailView(Class<T> entityType, String itemName) {
        this.entityType = Objects.requireNonNull(entityType, "Entity type must not be null");
        this.itemName = Objects.requireNonNull(itemName, "Item name must not be null");
        setSizeFull();
    }

    /**
     * Creates and installs the master-detail layout with the shared defaults.
     *
     * <p>Call this from the subclass constructor after assigning any dependencies
     * used by the configuration.</p>
     */
    protected final void configureMasterDetail(Consumer<MasterDetailBuilder<T>> configuration) {
        Objects.requireNonNull(configuration, "Configuration must not be null");
        if (layout != null) {
            throw new IllegalStateException("Master-detail layout is already configured");
        }
        MasterDetailBuilder<T> builder = Components.masterDetail(entityType)
                .viewMode(ViewMode.DESKTOP)
                .withMobileSheet(Sheet.Side.RIGHT)
                .withUrlSync(this::itemId, this::findItemById)
                .withInitialItem(this::findFirstItem)
                .withAutoSelect();

        configuration.accept(builder);
        layout = builder.build();
        add(layout);
    }

    /**
     * Returns the identifier written to the URL when an item is selected.
     */
    protected abstract String itemId(T item);

    /**
     * Finds the item named by a URL identifier, or returns empty when it does not exist.
     */
    protected abstract Optional<T> findItemById(String id);

    /**
     * Finds the first item to select when there is no deep link.
     */
    protected abstract Optional<T> findFirstItem();

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String id = event.getLocation().getQueryParameters()
                .getSingleParameter("id").orElse(null);
        if (id == null || id.isBlank()) {
            return;
        }
        try {
            if (getMasterDetailLayout().preloadFromUrl(id).isEmpty()) {
                event.rerouteToError(NotFoundException.class, "No " + itemName + " with id " + id);
            }
        } catch (NumberFormatException exception) {
            event.rerouteToError(NotFoundException.class, "Invalid " + itemName + " id " + id);
        }
    }

    /**
     * Returns the built master-detail layout.
     */
    protected final MasterDetailLayout<T> getMasterDetailLayout() {
        if (layout == null) {
            throw new IllegalStateException("Master-detail layout has not been configured");
        }
        return layout;
    }
}
