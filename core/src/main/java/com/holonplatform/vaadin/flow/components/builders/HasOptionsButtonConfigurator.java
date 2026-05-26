package com.holonplatform.vaadin.flow.components.builders;

import java.util.function.Consumer;

public interface HasOptionsButtonConfigurator<C extends HasOptionsButtonConfigurator<C>> extends ComponentConfigurator<C>{

    C optionsButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);



    interface BaseOptionsButtonConfigurator extends HasOptionsButtonConfigurator<BaseOptionsButtonConfigurator> {

    }
}
