package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.PopoverBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.popover.Popover;

/** Default {@link PopoverBuilder} implementation. */
public class DefaultPopoverBuilder extends AbstractPopoverConfigurator<PopoverBuilder>
        implements PopoverBuilder {

    public DefaultPopoverBuilder(Component... components) {
        super(new Popover(components));
    }

    @Override
    protected PopoverBuilder getConfigurator() {
        return this;
    }

    @Override
    public Popover build() {
        applyPostProcessors();
        return getComponent();
    }
}