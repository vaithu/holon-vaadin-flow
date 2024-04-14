package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.HasSearchBar;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.function.Consumer;

public class DefaultSearchBar extends HorizontalLayout implements HasSearchBar {


    private final Input<String> searchField;
    private final Button newButton;
    private final HorizontalLayout rightSide;

    public DefaultSearchBar() {

        searchField = Components.input
                .string()
                .prefixComponent(new Icon("lumo", "search"))
                .clearButtonVisible(true)
                .withFocusShortcut(Key.ENTER)
                .add()
                .build();

        newButton = Components.button()
                .primary()
                .icon("lumo", "plus")
                .iconAfterText(true)
                .text("New")
                .tooltip("New")
                .build();

        rightSide = new HorizontalLayout(newButton);
        rightSide.getStyle().set("margin-right", "auto");

        Components.configure(this)
                .addAndExpand(searchField, 1)
                .add(rightSide);


    }

    @Override
    public void search(Consumer<String> consumer) {
        searchField.addValueChangeListener(event -> consumer.accept(event.getValue()));
    }

    @Override
    public void newBtnConfigurator(Consumer<ButtonConfigurator> configurator) {
        configurator.accept(ButtonConfigurator.configure(newButton));
    }

    @Override
    public void add(Component... components) {
        rightSide.add(components);
    }
}
