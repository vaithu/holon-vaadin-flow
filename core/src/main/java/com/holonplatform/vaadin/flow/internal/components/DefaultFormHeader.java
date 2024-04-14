package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.HasFormHeader;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HorizontalLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHorizontalLayoutBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.function.Consumer;

public class DefaultFormHeader extends DefaultHorizontalLayoutBuilder implements HasFormHeader {

    private LabelBuilder<?> title;
    private final FlexLayout rightSide = new FlexLayout();

    public DefaultFormHeader() {
        rightSide.addClassName(LumoUtility.FlexDirection.ROW);

        Components.configure(getComponent())
                .spacing()
                .fullWidth()
                .styleNames(LumoUtility.Border.BOTTOM, LumoUtility.BorderColor.CONTRAST_30)
                .add(rightSide)
                .justifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

    }

    public DefaultFormHeader(String title) {
        this();
        setTitle(title);
    }

    @Override
    public void editBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        if (configurator != null) {
            Button button = Components.button()
                    .icon("lumo", "edit")
                    .tooltip("Edit")
                    .tertiaryInline()
                    .build();
            configurator.accept(ButtonConfigurator.configure(button));
            rightSide.add(button);
        }
    }

    @Override
    public void closeBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {


        if (configurator != null) {
            Button button = Components.button()
                    .icon("lumo", "cross")
                    .tooltip("Close")
                    .tertiaryInline()
                    .build();
            configurator.accept(ButtonConfigurator.configure(button));
            rightSide.add(button);
        }
    }

    @Override
    public void optionsBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {

        if (configurator != null) {
            Button button = Components.button()
                    .icon("lumo", "cross")
                    .tooltip("Options")
                    .tertiaryInline()
                    .build();
            configurator.accept(ButtonConfigurator.configure(button));
            rightSide.add(button);
        }
    }

    @Override
    public HorizontalLayoutBuilder add(Component... components) {
        rightSide.add(components);
        return this;
    }

    @Override
    public LabelBuilder<?> getTitle() {
        ObjectUtils.argumentNotNull(title, "Title is null here because it is not already set");
        return title;
    }

    @Override
    public void setTitle(LabelBuilder<?> title) {
        this.title = title;
        this.title.styleNames(LumoUtility.TextColor.PRIMARY, LumoUtility.Padding.SMALL);
        getComponent().addComponentAsFirst(this.title.build());
    }

    @Override
    public void setTitle(String title) {
        setTitle(LabelBuilder.h4().text(title));
    }
}
