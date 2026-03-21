package com.holonplatform.vaadin.flow.components.builders;

import com.vaadin.flow.component.menubar.MenuBar;

public interface HasOptionsMenuBarConfigurator<C extends HasOptionsMenuBarConfigurator<C>> extends ComponentConfigurator<C>{

    default C optionsMenuBar(MenuBarBuilder menuBarBuilder) {
        return optionsMenuBar(menuBarBuilder.build());
    }
    C optionsMenuBar(MenuBar menuBar);

    interface BaseOptionsMenuBarConfigurator extends HasOptionsMenuBarConfigurator<BaseOptionsMenuBarConfigurator> {

    }
}
