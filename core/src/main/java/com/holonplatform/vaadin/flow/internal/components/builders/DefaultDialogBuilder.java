package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.DialogBuilder.PlainDialogBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;

/** Default builder implementation for a plain {@link Dialog}. */
public class DefaultDialogBuilder extends AbstractDialogConfigurator<PlainDialogBuilder>
    implements PlainDialogBuilder {

    public DefaultDialogBuilder(Component... components) {
        super();
        withContent(components);
    }

    @Override
    protected PlainDialogBuilder getConfigurator() {
        return this;
    }

    @Override
    public Dialog build() {
        return getComponent();
    }
}