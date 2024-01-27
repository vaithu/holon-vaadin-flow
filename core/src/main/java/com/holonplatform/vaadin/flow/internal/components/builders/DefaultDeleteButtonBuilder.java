package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

public class DefaultDeleteButtonBuilder extends AbstractButtonConfigurator<ButtonBuilder> implements ButtonBuilder{
    public DefaultDeleteButtonBuilder() {
        super(new Button());
        withThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        title("Delete", "delete.code");
    }

    /*
     * (non-Javadoc)
     * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentBuilder#getBuilder()
     */
    @Override
    protected ButtonBuilder getConfigurator() {
        return this;
    }

    /*
     * (non-Javadoc)
     * @see com.holonplatform.vaadin.flow.components.builders.ComponentBuilder#build()
     */
    @Override
    public Button build() {
        return getComponent();
    }
}
