package com.iyensoft.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.iyensoft.vaadin.flow.components.Layout;
import com.iyensoft.vaadin.flow.components.ResponsiveDiv;
import com.holonplatform.vaadin.flow.components.support.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;

@StyleSheet("context://app-bar.css")
public class AppBar extends Layout implements HasTheme {

    /**
     * Always-present slot containers, created eagerly so the shell has a stable
     * three-slot structure.
     */
    private final Div startSlot;
    private final Div middleSlot;
    private final Div endSlot;
    private Div bottomSlot;

    public AppBar(Component... components) {

        startSlot = new Div();
        startSlot.addClassName("app-bar__start");
        middleSlot = new Div();
        middleSlot.addClassName("app-bar__middle");
        endSlot = new Div();
        endSlot.addClassName("app-bar__end");

        Components.configure(this)
                .fullWidth()
                .add(startSlot)
                .styleName("app-bar")
                .elementConfiguration(element -> element.setAttribute("role", "banner"));

        if (components != null && components.length > 0) {
            addToStart(components);
        }

        ResponsiveDiv.configure(this)
                .slotOnce(ViewMode.DESKTOP, unused -> add(middleSlot, endSlot))
                .slotOnce(ViewMode.MOBILE, unused -> createMobileView())
                .build();
    }

    private void createMobileView() {
        Button button = Components.button()
                .icon(VaadinIcon.ELLIPSIS_DOTS_H)
                .withThemeVariants(ButtonVariant.LUMO_ICON)
                .tertiary()
                .styleName("app-bar__action-btn")
                .ariaLabel(LocalizationProvider.localize("More actions", "app_bar.more_actions_aria"))
                .elementConfiguration(element -> element.getStyle().set("margin-inline-start", "auto"))
                .build();

        Popover popover = Components.popover().add(endSlot)
                .target(button)
                .fullWidth()
                .heightUndefined()
                .withThemeVariants(PopoverVariant.ARROW,
                                   PopoverVariant.NO_PADDING)
                .position(PopoverPosition.BOTTOM_END)
                .modal(true)
                .build();

        add(button, popover);
    }

    /**
     * Appends {@code components} to the start slot (left edge).
     */
    public void addToStart(Component... components) {
        startSlot.add(components);
    }

    /**
     * Appends {@code components} to the middle slot (flex-grow centre).
     */
    public void addToMiddle(Component... components) {
        middleSlot.add(components);
    }

    /**
     * Appends {@code components} to the end slot (right edge).
     */
    public void addToEnd(Component... components) {
        endSlot.add(components);
    }

    /**
     * Inserts {@code component} at {@code index} within the end slot.
     * Index 0 = leftmost position inside the end section.
     */
    public void addToEnd(int index, Component component) {
        endSlot.addComponentAtIndex(index, component);
    }

    /**
     * Appends {@code components} to the full-width bottom row (below
     * start/middle/end).
     */
    public void addToBottom(Component... components) {
        if (bottomSlot == null) {
            bottomSlot = new Div();
            add(bottomSlot);
        }
        if (components != null) {
            bottomSlot.setClassName("app-bar__bottom");
            bottomSlot.add(components);
        }

    }

}
