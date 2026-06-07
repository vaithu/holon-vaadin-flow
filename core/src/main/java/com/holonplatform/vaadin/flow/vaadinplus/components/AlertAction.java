package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

/**
 * Action slot of an {@link Alert}.
 *
 * <p>Holds buttons, links, or any interactive components displayed below the description.</p>
 */
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

