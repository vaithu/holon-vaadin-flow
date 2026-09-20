package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultPopoverBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.popover.Popover;

/** Fluent builder for {@link Popover} components. */
public interface PopoverBuilder
        extends PopoverConfigurator<PopoverBuilder>, ComponentBuilder<Popover, PopoverBuilder> {

    /** Create a new empty {@link PopoverBuilder}. */
    static PopoverBuilder create() {
        return new DefaultPopoverBuilder();
    }

    /** Create a new {@link PopoverBuilder} containing the given components. */
    static PopoverBuilder create(Component... components) {
        return new DefaultPopoverBuilder(components);
    }
}