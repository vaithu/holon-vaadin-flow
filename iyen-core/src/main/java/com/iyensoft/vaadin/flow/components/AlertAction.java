package com.iyensoft.vaadin.flow.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.dependency.StyleSheet;

/**
 * Action slot of an {@link Alert}.
 *
 * <p>Holds buttons, links, or any interactive components displayed below the description.</p>
 */
@StyleSheet("context://alert.css")
public class AlertAction extends Div {

    /**
     * Creates an action slot pre-populated with the given components.
     *
     * @param components action components (buttons, anchors, etc.)
     */
    public AlertAction(Component... components) {
        addClassName("alert__action");
        if (components != null) {
            add(components);
        }
    }
}

