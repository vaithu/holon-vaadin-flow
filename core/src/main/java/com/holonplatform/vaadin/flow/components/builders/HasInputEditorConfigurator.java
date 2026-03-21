package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultInputEditorBuilder;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.function.Consumer;

public interface HasInputEditorConfigurator<C extends HasInputEditorConfigurator<C>> extends ComponentConfigurator<C> {

    C saveBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    C editBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    C cancelBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    interface InputEditorBuilder extends HasInputEditorConfigurator<InputEditorBuilder> {
        HorizontalLayout build();
    }

    static InputEditorBuilder create() {
        return new DefaultInputEditorBuilder(new HorizontalLayout());
    }

}
