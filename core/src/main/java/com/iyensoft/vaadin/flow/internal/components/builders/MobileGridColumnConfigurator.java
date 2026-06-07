package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Badge;
import com.holonplatform.vaadin.flow.components.builders.*;
import com.holonplatform.vaadin.flow.internal.lumo.FlexDirection;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.menubar.MenuBar;

import java.time.LocalDate;
import java.util.function.Consumer;

public interface MobileGridColumnConfigurator<
        C extends MobileGridColumnConfigurator<C>>
        extends ComponentConfigurator<C>,
        HasStyleConfigurator<C>,
        HasSizeConfigurator<C> {

    /* =================================================
     * Primary row – media & actions
     * ================================================= */

    /** Adds a leading image to the column (before text). */
    C withImageAsPrimary(String imagePath, String altText);

    /** Adds an action menu button to the primary row. */
    C withContextMenuAsPrimary(
            ContextMenu contextMenu,
            Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    /** Adds a MenuBar to the primary row. */
    C withMenuBarAsPrimary(MenuBar menuBar);

    /** Adds a badge to the primary row. */
    C withBadgeAsPrimary(String text);

    /** Adds a badge component to the primary row. */
    C withBadgeAsPrimary(Badge badge);

    C withPrimaryComponents(Component... components);

    C withAvatarAsPrimary(String name);

    /* =================================================
     * Text sections
     * ================================================= */

    /** Main text (title / label) of the column. */
    C withPrimaryText(String text);

    /** Secondary descriptive text below the primary row. */
    C withSecondaryText(String text);

    C withSecondaryComponents(Component... components);

    /** Tertiary supporting text (e.g. amount, status). */
    C withTertiaryText(String text);

        /** Tertiary supporting text with an explicit semantic text class. */
        C withTertiaryText(String text, String textClassName);

    /** Tertiary currencyValue with an associated date line. */
    C withTertiaryCurrencyValueAndDate(
            String currencyValue,
            LocalDate date);

    C withTertiaryComponents(Component... components);

    C flexDirection(FlexDirection flexDirection);

    /* =================================================
     * Layout escape hatches (advanced use)
     * ================================================= */

    /** Directly configure the primary container. */
    C configurePrimary(
            Consumer<FlexLayoutConfigurator.BaseFlexLayoutConfigurator> configurator);

    /** Directly configure the secondary container. */
    C configureSecondary(
            Consumer<FlexLayoutConfigurator.BaseFlexLayoutConfigurator> configurator);

    /** Directly configure the tertiary container. */
    C configureTertiary(
            Consumer<FlexLayoutConfigurator.BaseFlexLayoutConfigurator> configurator);

    /* =================================================
     * Convenience methods
     * ================================================= */

    /** Adds primary text followed by a badge. */
    C withPrimaryTextAndBadge(String text, Badge badge);
}