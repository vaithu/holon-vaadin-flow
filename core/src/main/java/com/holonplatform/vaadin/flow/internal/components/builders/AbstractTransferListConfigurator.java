package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.TransferListConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.TransferItem;
import com.holonplatform.vaadin.flow.vaadinplus.components.TransferList;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Collection;
import java.util.Optional;

/**
 * Base {@link TransferListConfigurator} implementation.
 *
 * @param <C> concrete configurator type
 */
public abstract class AbstractTransferListConfigurator<C extends TransferListConfigurator<C>>
        extends AbstractComponentConfigurator<TransferList, C>
        implements TransferListConfigurator<C> {

    /**
     * Constructor.
     *
     * @param component the {@link TransferList} instance to configure (not null)
     */
    public AbstractTransferListConfigurator(TransferList component) {
        super(component);
    }

    // ── TransferListConfigurator implementation ───────────────────────────────

    @Override
    public C availableItems(Collection<TransferItem> items) {
        getComponent().setAvailableItems(items);
        return getConfigurator();
    }

    @Override
    public C selectedItems(Collection<TransferItem> items) {
        getComponent().setSelectedItems(items);
        return getConfigurator();
    }

    @Override
    public C availableTitle(String title) {
        getComponent().setAvailableTitle(title);
        return getConfigurator();
    }

    @Override
    public C selectedTitle(String title) {
        getComponent().setSelectedTitle(title);
        return getConfigurator();
    }

    @Override
    public C onTransfer(ComponentEventListener<TransferList.TransferEvent> listener) {
        getComponent().addTransferListener(listener);
        return getConfigurator();
    }

    // ── AbstractComponentConfigurator hooks ───────────────────────────────────

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

