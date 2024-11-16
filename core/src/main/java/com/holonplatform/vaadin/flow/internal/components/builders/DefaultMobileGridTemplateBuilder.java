package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Badge;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.DivConfigurator;
import com.holonplatform.vaadin.flow.components.builders.MobileGridTemplateConfigurator;
import com.holonplatform.vaadin.flow.components.css.BadgeColor;
import com.holonplatform.vaadin.flow.components.css.BadgeShape;
import com.holonplatform.vaadin.flow.components.css.BadgeSize;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class DefaultMobileGridTemplateBuilder extends DefaultDivBuilder implements MobileGridTemplateConfigurator<DefaultMobileGridTemplateBuilder> {

    private final Div columnDiv;
    private  Div primaryDiv, secondaryDiv, tertiaryDiv;
    private final FlexLayout primary, secondary, tertiary;
    private Button actionButton;

    public DefaultMobileGridTemplateBuilder() {

        primary = new FlexLayout();
        secondary = new FlexLayout();
        tertiary = new FlexLayout();

        columnDiv = new Div();
        columnDiv.addClassNames(LumoUtility.FlexDirection.COLUMN, LumoUtility.Overflow.HIDDEN
                , LumoUtility.Display.FLEX
                , LumoUtility.Flex.GROW);

        getComponent().addClassNames(LumoUtility.Padding.Top.SMALL, LumoUtility.Display.FLEX,
                LumoUtility.Padding.Bottom.SMALL, LumoUtility.LineHeight.MEDIUM);

        getComponent().add(columnDiv);

    }



    @Override
    public DefaultMobileGridTemplateBuilder image(String imagePath, String altText) {
        Image image = new Image(imagePath, altText);
        image.addClassNames(LumoUtility.Flex.SHRINK_NONE, LumoUtility.Width.MEDIUM, LumoUtility.Height.MEDIUM);
        getComponent().addComponentAsFirst(image);
        return this;
    }

    @Override
    public DefaultMobileGridTemplateBuilder badge(String text) {
        Badge id = new Badge(text, BadgeColor.NORMAL, BadgeSize.S, BadgeShape.PILL);
        badge(id);

        return this;
    }

    @Override
    public DefaultMobileGridTemplateBuilder action(ContextMenu contextMenu, Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        actionButton = Components.button()
                .tertiaryInline()
                .iconConfigurator(VaadinIcon.ELLIPSIS_DOTS_V)
                .add()
                .tooltipText("Action")
                .build();
        contextMenu.setOpenOnClick(true);
        contextMenu.setTarget(actionButton);
        configurator.accept(ButtonConfigurator.configure(actionButton));
        primary.add(actionButton);
        return this;
    }

    @Override
    public DefaultMobileGridTemplateBuilder badge(Badge badge) {
        primary.add(badge);
        primary.setFlexShrink(0, badge);
        return this;
    }

    @Override
    public DefaultMobileGridTemplateBuilder primaryText(String txt) {
        Div text = Components.label()
                .text(txt)
                .build();
        text.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.TextColor.BODY, LumoUtility.Overflow.HIDDEN,
                LumoUtility.TextOverflow.ELLIPSIS, LumoUtility.Flex.GROW);

        primary.addComponentAsFirst(text);
        primary.setAlignItems(FlexComponent.Alignment.CENTER);
        primary.setFlexGrow(1, text);

        primary.addClassNames(LumoUtility.Gap.MEDIUM);
        columnDiv.addComponentAsFirst(primary);
        return this;
    }

    @Override
    public DefaultMobileGridTemplateBuilder secondaryText(String txt) {
        Div text = Components.label()
                .text(txt)
                .build();
        text.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.TERTIARY, LumoUtility.Overflow.HIDDEN,
                LumoUtility.TextOverflow.ELLIPSIS, LumoUtility.Margin.Bottom.SMALL);
        secondary.add(text);
        columnDiv.add(secondary);
        return this;
    }

    @Override
    public DefaultMobileGridTemplateBuilder tertiaryText(String txt, String... styles) {
        Div text = Components.label()
                .text(txt)
                .title(txt)
                .id("tertiary text")
                .build();
        text.addClassNames(LumoUtility.FontWeight.BOLD);
        if (NumberUtils.isCreatable(txt)) {
            text.setText(String.format("%s%s", UIUtils.getCurrencySymbol(), txt));
            text.getElement().setAttribute("font-family", "monospace");
            if (NumberUtils.createNumber(txt).doubleValue() > 0D) {
                text.addClassNames(LumoUtility.TextColor.SUCCESS);
            } else {
                text.addClassNames(LumoUtility.TextColor.ERROR);
            }
        } else if (styles.length == 0){
            text.addClassNames(LumoUtility.TextColor.PRIMARY);
        }

        tertiary.add(text);
        tertiary.setFlexGrow(1, text);
        tertiary.addClassNames(styles);
        columnDiv.add(tertiary);
        return this;
    }

    @Override
    public DefaultMobileGridTemplateBuilder tertiaryText(String text, LocalDate date, String... styles) {
        tertiaryText(text,styles);
        Div dateText = Components.label()
                .text(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .build();
        dateText.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.TERTIARY);

        tertiary.add(dateText);
        return this;
    }

    private DefaultMobileGridTemplateBuilder createDiv(Div div,Consumer<BaseDivConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        configurator.accept(DivConfigurator.configure(div));

        return this;
    }

    @Override
    public DefaultMobileGridTemplateBuilder primary(Consumer<BaseDivConfigurator> configurator) {
        primaryDiv = new Div();
        columnDiv.addComponentAsFirst(primaryDiv);
        return createDiv(primaryDiv, configurator);
    }

    @Override
    public DefaultMobileGridTemplateBuilder secondary(Consumer<BaseDivConfigurator> configurator) {
        secondaryDiv = new Div();
        columnDiv.add(secondaryDiv);
        return createDiv(secondaryDiv, configurator);
    }

    @Override
    public DefaultMobileGridTemplateBuilder tertiary(Consumer<BaseDivConfigurator> configurator) {
        tertiaryDiv = new Div();
        columnDiv.add(tertiaryDiv);
        return createDiv(tertiaryDiv, configurator);
    }

    @Override
    public DefaultMobileGridTemplateBuilder primary(String text, Badge badge) {

        return primaryText(text).badge(badge);
    }
}
