package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.Badge;
import com.vaadin.flow.component.contextmenu.ContextMenu;

import java.time.LocalDate;
import java.util.function.Consumer;

public interface MobileGridTemplateConfigurator<C extends MobileGridTemplateConfigurator<C>> extends DivBuilder {

    C image(String imagePath, String altText);

    C action(ContextMenu contextMenu,
            Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);
    C badge(String text);

    C badge(Badge badge);

    C primaryText(String text);

    C secondaryText(String text);

    C tertiaryText(String text, String... styles);
    C tertiaryText(String text, LocalDate date, String... styles);

    C primary(Consumer<DivConfigurator.BaseDivConfigurator> configurator);
    C secondary(Consumer<DivConfigurator.BaseDivConfigurator> configurator);
    C tertiary(Consumer<DivConfigurator.BaseDivConfigurator> configurator);

    C primary(String text, Badge badge);

}
