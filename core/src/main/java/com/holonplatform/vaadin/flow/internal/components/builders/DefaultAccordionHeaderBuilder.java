package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasAccordionHeaderConfigurator;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.function.Consumer;

public class DefaultAccordionHeaderBuilder
        extends DefaultHorizontalLayoutBuilder
        implements HasAccordionHeaderConfigurator<DefaultAccordionHeaderBuilder> {


    private final Button addNewButton;
    private final Button statusButton;
    private final FlexLayout rightSide;

    public DefaultAccordionHeaderBuilder() {

        addNewButton = Components.button()
                .iconConfigurator(VaadinIcon.PLUS)
                .styleNames(LumoUtility.IconSize.SMALL)
                .add()
                .text("Add New ")
                .iconAfterText(false)

                .build();

        statusButton = Components.button()
                .iconAfterText(true)
                .icon(VaadinIcon.CHEVRON_DOWN)
                .text("Status: All")
                .build();



        getComponent().setWidthFull();
        getComponent().addClassNames( LumoUtility.Background.CONTRAST_5);

        rightSide = Components.flexLayout()
                .styleNames(LumoUtility.JustifyContent.END, LumoUtility.Flex.GROW, LumoUtility.Gap.MEDIUM)
                .add(statusButton,addNewButton)
                .flexDirection(FlexLayout.FlexDirection.ROW)
                .fullWidth()
                .build();

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
