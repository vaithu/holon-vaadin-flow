package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.vaadin.flow.component.Component;

import java.util.function.Consumer;

public interface FormFooter<C extends FormFooter<C>> extends ComponentConfigurator<C> {
    /*void saveBtnAction();

    void discardBtnAction();

    void updateBtnAction();*/

    C saveBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);
    C saveAndNewBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);
    C discardBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);
    C updateBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    C addAdditionalComponent(Component component);

    Layout build();

}
