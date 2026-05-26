package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.BulkItemPickerDialogBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.BulkItemPickerDialog;

/**
 * Default {@link BulkItemPickerDialogBuilder} implementation.
 *
 * <p>Instantiates an empty {@link BulkItemPickerDialog} and delegates all
 * configuration to {@link AbstractBulkItemPickerDialogConfigurator}.
 * Returned by {@link BulkItemPickerDialogBuilder#create()} and
 * {@link BulkItemPickerDialog#builder()}.</p>
 */
public class DefaultBulkItemPickerDialogBuilder
        extends AbstractBulkItemPickerDialogConfigurator<BulkItemPickerDialogBuilder>
        implements BulkItemPickerDialogBuilder {

    /**
     * Constructor — creates a default-configured dialog.
     */
    public DefaultBulkItemPickerDialogBuilder() {
        super(new BulkItemPickerDialog());
    }

    @Override
    protected BulkItemPickerDialogBuilder getConfigurator() {
        return this;
    }

    @Override
    public BulkItemPickerDialog build() {
        BulkItemPickerDialog dialog = getComponent();
        if (isShouldAutoOpen()) {
            dialog.open();
        }
        return dialog;
    }
}

