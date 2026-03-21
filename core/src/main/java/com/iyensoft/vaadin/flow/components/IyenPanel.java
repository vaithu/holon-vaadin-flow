package com.iyensoft.vaadin.flow.components;

import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.vaadin.flow.component.Component;

public class IyenPanel extends Layout {

    public IyenPanel() {
        super();
        addClassName("iyen-panel");
    }

    public IyenPanel(Component... components) {
        super(components);
        addClassName("iyen-panel");
    }
}
