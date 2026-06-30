package com.iyensoft.vaadin.flow.components;

import com.holonplatform.vaadin.flow.vaadinplus.components.Footer;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;

@StyleSheet("context://panel.css")
public class Panel extends Div {

    private static final String CLASS_PANEL = "iyen-panel";

    public Panel() {
        addClassName(CLASS_PANEL);
        getElement().setAttribute("role", "group");

    }

    public void setHeader(Header header) {
        addComponentAsFirst(header);
    }

    public void setContent(Component... components) {
        add(components);
    }

    public void setFooter(Footer footer) {
        add(footer);
    }

}
