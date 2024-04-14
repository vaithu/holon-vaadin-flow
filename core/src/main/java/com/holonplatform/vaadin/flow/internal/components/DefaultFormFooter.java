package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.HasFormFooter;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHorizontalLayoutBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;

import java.util.function.Consumer;

public class DefaultFormFooter extends DefaultHorizontalLayoutBuilder implements HasFormFooter {

    private Button saveBtn;
    private Button discardBtn;
    private Button updateBtn;

    public DefaultFormFooter() {

        getComponent().setId("FormFooter");

        saveBtn = Components.button()
                .text("Save")
                .primary()
//                .onClick(event -> saveBtnAction())
                .visible(true)
                .build();

        discardBtn = Components.button()
                .text("Cancel")
                .borderPrimary()
//                .onClick(event -> discardBtnAction())
                .visible(true)
                .build();

        updateBtn = Components.button()
                .text("Update")
                .primary()
//                .onClick(event -> updateBtnAction())
                .visible(false)
                .build();

        getComponent().setSpacing(true);
        getComponent().addClassName("footer");
        getComponent().setWidthFull();
        getComponent().add(saveBtn, updateBtn, discardBtn);
    }

    @Override
    public void addAdditionalComponent(Component component) {
        component.getStyle().set("margin-left", "auto");
        getComponent().addComponentAtIndex(getComponent().getComponentCount() + 1, component);
    }

    @Override
    public void saveBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        updateBtn.setVisible(false);
        saveBtn.setVisible(true);
        configurator.accept(ButtonConfigurator.configure(saveBtn));
    }

    @Override
    public void discardBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        configurator.accept(ButtonConfigurator.configure(discardBtn));
    }

    @Override
    public void updateBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        saveBtn.setVisible(false);
        updateBtn.setVisible(true);
        configurator.accept(ButtonConfigurator.configure(updateBtn));

    }


}
