package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.BulkItemPickerDialogConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.BulkItemPickerDialog;

/**
 * Default {@link BulkItemPickerDialogConfigurator.BaseBulkItemPickerDialogConfigurator}
 * implementation — returned by
 * {@link BulkItemPickerDialogConfigurator#configure(BulkItemPickerDialog)}.
 */
public class DefaultBulkItemPickerDialogConfigurator
        extends AbstractBulkItemPickerDialogConfigurator<BulkItemPickerDialogConfigurator.BaseBulkItemPickerDialogConfigurator>
        implements BulkItemPickerDialogConfigurator.BaseBulkItemPickerDialogConfigurator {

    /**
     * Constructor.
     *
     * @param dialog the existing dialog to configure (not null)
     */
    public DefaultBulkItemPickerDialogConfigurator(BulkItemPickerDialog dialog) {
        super(dialog);
    }

    @Override
    protected BulkItemPickerDialogConfigurator.BaseBulkItemPickerDialogConfigurator getConfigurator() {
        return this;
    }
}

