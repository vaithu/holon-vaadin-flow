package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultBulkItemPickerDialogBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.BulkItemPickerDialog;

/**
 * Holon Fluent Builder for {@link BulkItemPickerDialog}.
 *
 * <p>Extends {@link BulkItemPickerDialogConfigurator} (all configuration methods)
 * and {@link ComponentBuilder} (terminal {@link #build()} method).</p>
 *
 * <p>Obtain via {@link BulkItemPickerDialog#builder()} or {@link #create()}:</p>
 * <pre>{@code
 * BulkItemPickerDialog dialog = BulkItemPickerDialog.builder()
 *     .title("Add Products")
 *     .items(catalogue)
 *     .onConfirm(entries -> order.addLines(entries))
 *     .build();
 * dialog.open();
 * }</pre>
 *
 * @see BulkItemPickerDialogConfigurator
 * @see BulkItemPickerDialog
 */
public interface BulkItemPickerDialogBuilder
        extends BulkItemPickerDialogConfigurator<BulkItemPickerDialogBuilder>,
                ComponentBuilder<BulkItemPickerDialog, BulkItemPickerDialogBuilder> {

    /**
     * Creates a new {@link BulkItemPickerDialogBuilder}.
     *
     * @return a new builder
     */
    static BulkItemPickerDialogBuilder create() {
        return new DefaultBulkItemPickerDialogBuilder();
    }
}

