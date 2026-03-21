package com.holonplatform.vaadin.flow.components.builders;

import com.vaadin.flow.component.contextmenu.ContextMenu;

import java.util.function.Consumer;

public interface HasAccordionHeaderConfigurator<C extends HasAccordionHeaderConfigurator<C>> extends
        HorizontalLayoutBuilder {

    C title(String title);

    C menuBar(ContextMenu contextMenu);

    C addNewButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);
    C statusButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);


}
