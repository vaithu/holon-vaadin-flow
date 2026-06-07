package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultBulkItemPickerDialogConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.BulkItemPickerDialog;
import com.holonplatform.vaadin.flow.vaadinplus.components.BulkPickerEntry;
import com.holonplatform.vaadin.flow.vaadinplus.components.BulkPickerFetchQuery;
import com.holonplatform.vaadin.flow.vaadinplus.components.BulkPickerItem;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Fluent configurator for {@link BulkItemPickerDialog}.
 *
 * <p>Provides methods to configure the item catalogue, labels, and callbacks
 * before the dialog is built and opened.</p>
 *
 * @param <C> the concrete configurator type for fluent chaining
 *
 * @see BulkItemPickerDialogBuilder
 * @see BulkItemPickerDialog
 */
public interface BulkItemPickerDialogConfigurator<C extends BulkItemPickerDialogConfigurator<C>>
        extends ComponentConfigurator<C> {

    // ── Item catalogue ────────────────────────────────────────────────────────

    /**
     * Sets the full item catalogue shown in the left (search) panel (eager mode).
     * Replaces any previously added items or lazy provider.
     *
     * <p>For large catalogues backed by a database, prefer
     * {@link #itemProvider(Function)} to avoid loading all rows upfront.</p>
     *
     * @param items the catalogue (not null; may be empty)
     * @return this configurator (for chaining)
     */
    C items(Collection<BulkPickerItem> items);

    /**
     * Appends a single item to the item catalogue.
     *
     * @param item the item to content (not null)
     * @return this configurator (for chaining)
     */
    C item(BulkPickerItem item);

    /**
     * Sets a lazy item provider that is called on every debounced search query
     * instead of filtering an in-memory list.
     *
     * <p>The provider receives the verbatim search text entered by the user and
     * should return the matching items (limit the result, e.g. top-50).</p>
     *
     * <pre>{@code
     * BulkItemPickerDialog.builder()
     *     .itemProvider(query -> productService.search(query, 50))
     *     .onConfirm(entries -> ...)
     *     .build().open();
     * }</pre>
     *
     * @param provider {@code (query) → List<BulkPickerItem>} (not null)
     * @return this configurator (for chaining)
     */
    C itemProvider(Function<String, List<BulkPickerItem>> provider);

    // ── Labels ────────────────────────────────────────────────────────────────

    /**
     * Sets the dialog header title text.
     * Default: {@code "Add Items in Bulk"}.
     *
     * @param title the title text (not null)
     * @return this configurator (for chaining)
     */
    C title(String title);

    /**
     * Sets the search field placeholder text.
     * Default: {@code "Type to search or scan the barcode of the item"}.
     *
     * @param placeholder the placeholder text (not null)
     * @return this configurator (for chaining)
     */
    C searchPlaceholder(String placeholder);

    /**
     * Sets the label of the primary confirm button.
     * Default: {@code "Add Items"}.
     *
     * @param text the label text (not null)
     * @return this configurator (for chaining)
     */
    C addButtonText(String text);

    /**
     * Sets the label of the secondary cancel button.
     * Default: {@code "Cancel"}.
     *
     * @param text the label text (not null)
     * @return this configurator (for chaining)
     */
    C cancelButtonText(String text);

    /**
     * Sets a paginated item provider using Spring Data-style offset/limit semantics.
     * Preferred over {@link #itemProvider(Function)} for large catalogues.
     *
     * <p>The {@code fetchProvider} is called on every debounced search with a
     * {@link BulkPickerFetchQuery} carrying {@code (query, offset, limit)}.
     * The {@code countProvider} is called once per query change to enable
     * "Page X of Y · N items" display.</p>
     *
     * <pre>{@code
     * BulkItemPickerDialog.builder()
     *     .pagedItemProvider(
     *         q -> repo.findByNameOrSku(q.query(), q.offset(), q.limit()),
     *         q -> repo.countByNameOrSku(q)
     *     )
     *     .build().open();
     * }</pre>
     *
     * @param fetchProvider paginated fetch callback (not null)
     * @param countProvider total-count callback (not null)
     * @return this configurator (for chaining)
     */
    C pagedItemProvider(Function<BulkPickerFetchQuery, List<BulkPickerItem>> fetchProvider,
                        Function<String, Long> countProvider);

    /**
     * Sets a paginated item provider <em>without</em> a count function.
     * The pagination bar shows "Page X" only; Next is disabled on the last page.
     *
     * @param fetchProvider paginated fetch callback (not null)
     * @return this configurator (for chaining)
     */
    C pagedItemProvider(Function<BulkPickerFetchQuery, List<BulkPickerItem>> fetchProvider);

    /**
     * Sets the number of items fetched per page.
     * Default: 20.
     *
     * @param pageSize items per page (≥ 1)
     * @return this configurator (for chaining)
     */
    C pageSize(int pageSize);

    // ── Callbacks ─────────────────────────────────────────────────────────────

    /**
     * Registers the confirm callback invoked when the user clicks the content button.
     * Receives the full list of {@link BulkPickerEntry} objects (item + quantity).
     *
     * @param callback the confirm handler (not null)
     * @return this configurator (for chaining)
     */
    C onConfirm(Consumer<List<BulkPickerEntry>> callback);

    /**
     * Registers the cancel callback invoked when the user cancels or closes the dialog.
     *
     * @param callback the cancel handler (not null)
     * @return this configurator (for chaining)
     */
    C onCancel(Runnable callback);

    /**
     * Immediately opens the dialog after {@link #build()} returns.
     *
     * @return this configurator (for chaining)
     */
    C autoOpen();

    // ── Configure factory ─────────────────────────────────────────────────────

    /**
     * Returns a fluent configurator for an existing {@link BulkItemPickerDialog}.
     *
     * @param dialog the dialog to configure (not null)
     * @return a new {@link BaseBulkItemPickerDialogConfigurator}
     */
    static BaseBulkItemPickerDialogConfigurator configure(BulkItemPickerDialog dialog) {
        return new DefaultBulkItemPickerDialogConfigurator(dialog);
    }

    // ── Base configurator ─────────────────────────────────────────────────────

    /**
     * Non-generic base configurator.
     */
    interface BaseBulkItemPickerDialogConfigurator
            extends BulkItemPickerDialogConfigurator<BaseBulkItemPickerDialogConfigurator> {
    }
}





