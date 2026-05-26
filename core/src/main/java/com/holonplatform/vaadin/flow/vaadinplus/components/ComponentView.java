package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;

@StyleSheet("context://component-view.css")
public class ComponentView extends Main {

    public ComponentView() {
        addClassName("component-view");
    }

    public void addH2(String text) {
        H2 h2 = new H2(text);
        h2.addClassName("component-view__heading");
        add(h2);
    }

    public void addPreview(Component... components) {
        add(new Preview(components));
    }

}