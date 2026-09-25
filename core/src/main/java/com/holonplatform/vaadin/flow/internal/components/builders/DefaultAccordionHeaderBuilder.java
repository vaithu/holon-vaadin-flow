package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasAccordionHeaderConfigurator;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.components.utils.StyleSheetSupport;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

import java.util.function.Consumer;

public class DefaultAccordionHeaderBuilder
        extends DefaultHorizontalLayoutBuilder
        implements HasAccordionHeaderConfigurator<DefaultAccordionHeaderBuilder> {


    private final Button addNewButton;
    private final Button statusButton;
    private final FlexLayout rightSide;

    public DefaultAccordionHeaderBuilder() {

        addNewButton = ButtonBuilder.create()
                .iconConfigurator(VaadinIcon.PLUS)
                .styleNames("icon-size-small")
                .add()
                .text(LocalizationProvider.localize("Add New ", "accordion.add_new"))
                .iconAfterText(false)
                .build();

        statusButton = ButtonBuilder.create()
                .iconAfterText(true)
                .icon(VaadinIcon.CHEVRON_DOWN)
                .text(LocalizationProvider.localize("Status: All", "accordion.status_all"))
                .build();

        getComponent().setWidthFull();
        StyleSheetSupport.require(getComponent(), "utilities.css", "layout.css");
        getComponent().addClassName("color-bg-contrast-5");

        rightSide = new FlexLayout(statusButton, addNewButton);
        rightSide.addClassNames("justify-end", "flex-grow-1", "gap-m");
        rightSide.setFlexDirection(FlexLayout.FlexDirection.ROW);
        rightSide.setWidthFull();

        getComponent().add(rightSide);
        alignItems(FlexComponent.Alignment.CENTER);

    }

    @Override
    public DefaultAccordionHeaderBuilder title(String title) {
        getComponent().addComponentAsFirst(
                LabelBuilder.h4().text(title).title(title).build()
        );
        return this;
    }

    @Override
    public DefaultAccordionHeaderBuilder menuBar(ContextMenu contextMenu) {
        contextMenu.setOpenOnClick(true);
        contextMenu.setTarget(statusButton);
        return this;
    }

    @Override
    public DefaultAccordionHeaderBuilder addNewButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        configurator.accept(ButtonConfigurator.configure(addNewButton));
        return this;
    }

    @Override
    public DefaultAccordionHeaderBuilder statusButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        configurator.accept(ButtonConfigurator.configure(statusButton));
        return this;
    }
}
