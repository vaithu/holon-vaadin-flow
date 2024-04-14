package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HorizontalLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;

import java.util.function.Consumer;

public interface HasFormHeader extends HorizontalLayoutBuilder {

    LabelBuilder<?> getTitle();

    void setTitle(LabelBuilder<?> title);

    void setTitle(String title);

    void editBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);
    void closeBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);
    void optionsBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);


}
