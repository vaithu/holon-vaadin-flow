package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.builders.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.function.Consumer;

public interface FormHeaderConfigurator<C extends FormHeaderConfigurator<C>> extends ComponentConfigurator<C>
, HasStyleConfigurator<C>, HasSizeConfigurator<C> {

    LabelBuilder<?> getTitle();

    C title(LabelBuilder<?> title);

    C title(String title);

    C editBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);
    C closeBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);
    C optionsBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    C additionalItems(Component... components);

    C horizontalRule();

    C title(Component component);

    interface BaseHasFormHeaderConfigurator extends FormHeaderConfigurator<BaseHasFormHeaderConfigurator> {

    }

    HorizontalLayout build();

}
