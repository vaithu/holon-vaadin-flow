package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.BulkItemPickerDialogConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.BulkItemPickerDialog;
import com.holonplatform.vaadin.flow.vaadinplus.components.BulkPickerEntry;
import com.holonplatform.vaadin.flow.vaadinplus.components.BulkPickerFetchQuery;
import com.holonplatform.vaadin.flow.vaadinplus.components.BulkPickerItem;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Base {@link BulkItemPickerDialogConfigurator} implementation.
 *
 * <p>Extends {@link AbstractComponentConfigurator} to inherit the standard Holon Platform
 * component lifecycle hooks and provides BulkItemPickerDialog-specific configuration logic.</p>
 *
 * @param <C> concrete configurator type
 */
public abstract class AbstractBulkItemPickerDialogConfigurator<C extends BulkItemPickerDialogConfigurator<C>>
        extends AbstractComponentConfigurator<BulkItemPickerDialog, C>
        implements BulkItemPickerDialogConfigurator<C> {

    private boolean shouldAutoOpen = false;

    /**
     * Constructor.
     *
     * @param dialog the {@link BulkItemPickerDialog} instance to configure (not null)
     */
    public AbstractBulkItemPickerDialogConfigurator(BulkItemPickerDialog dialog) {
        super(dialog);
    }

    // ── BulkItemPickerDialogConfigurator implementation ───────────────────────

    @Override
    public C items(Collection<BulkPickerItem> items) {
        getComponent().setItems(items);
        return getConfigurator();
    }

    @Override
    public C item(BulkPickerItem item) {
        getComponent().addItem(item);
        return getConfigurator();
    }

    @Override
    public C itemProvider(Function<String, List<BulkPickerItem>> provider) {
        getComponent().setItemProvider(provider);
        return getConfigurator();
    }

    @Override
    public C pagedItemProvider(Function<BulkPickerFetchQuery, List<BulkPickerItem>> fetchProvider,
                               Function<String, Long> countProvider) {
        getComponent().setPagedProvider(fetchProvider, countProvider);
        return getConfigurator();
    }

    @Override
    public C pagedItemProvider(Function<BulkPickerFetchQuery, List<BulkPickerItem>> fetchProvider) {
        getComponent().setPagedProvider(fetchProvider);
        return getConfigurator();
    }

    @Override
    public C pageSize(int pageSize) {
        getComponent().setPageSize(pageSize);
        return getConfigurator();
    }

    @Override
    public C title(String title) {
        getComponent().setTitle(title);
        return getConfigurator();
    }

    @Override
    public C searchPlaceholder(String placeholder) {
        getComponent().setSearchPlaceholder(placeholder);
        return getConfigurator();
    }

    @Override
    public C addButtonText(String text) {
        getComponent().setAddButtonText(text);
        return getConfigurator();
    }

    @Override
    public C cancelButtonText(String text) {
        getComponent().setCancelButtonText(text);
        return getConfigurator();
    }

    @Override
    public C onConfirm(Consumer<List<BulkPickerEntry>> callback) {
        getComponent().setConfirmCallback(callback);
        return getConfigurator();
    }

    @Override
    public C onCancel(Runnable callback) {
        getComponent().setCancelCallback(callback);
        return getConfigurator();
    }

    @Override
    public C autoOpen() {
        this.shouldAutoOpen = true;
        return getConfigurator();
    }

    /**
     * Returns {@code true} if {@link #autoOpen()} was called.
     * Used by the concrete builder to open the dialog after {@code build()}.
     *
     * @return whether to open immediately
     */
    protected boolean isShouldAutoOpen() {
        return shouldAutoOpen;
    }

    // ── AbstractComponentConfigurator hooks ───────────────────────────────────

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.empty(); // Dialog sizing handled by CSS custom properties
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.empty();
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.empty();
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }
}





