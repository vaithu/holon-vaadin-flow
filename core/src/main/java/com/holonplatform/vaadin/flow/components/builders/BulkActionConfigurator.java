package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;

import java.util.function.Consumer;

public interface BulkActionConfigurator<C extends BulkActionConfigurator<C>> extends HasOptionsMenuBarConfigurator<C>,
        HasCloseButtonConfigurator<C>,HasStyleConfigurator<C>,ComponentConfigurator<C>, HasComponentsConfigurator<C> {

    C bulkAction(MenuBar menuBar);

    C selected(LabelBuilder<?> label);

    C selected(Span span);
    C selected(int selectedCount);
    C selected(String selectedText);

    /**
     * Sets the selected-items label from a {@link Localizable} descriptor.
     *
     * @param selectedText localizable selected-count label (not null)
     * @return this configurator
     */
    C selected(Localizable selectedText);

    C selectAll(BooleanInputBuilder builder);
    C selectAll(Checkbox checkbox);

    C bulkAction(ContextMenuBuilder contextMenuBuilder, Component target);

    C withPostProcessor(Consumer<BulkActionConfigurator<C>> postProcessor);
}
