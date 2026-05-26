package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Badge;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.FlexLayoutConfigurator;
import com.holonplatform.vaadin.flow.components.css.BadgeColor;
import com.holonplatform.vaadin.flow.components.css.BadgeShape;
import com.holonplatform.vaadin.flow.components.css.BadgeSize;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.holonplatform.vaadin.flow.internal.lumo.FlexDirection;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.shared.HasTooltip;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractMobileGridColumnConfigurator<
        C extends MobileGridColumnConfigurator<C>>
        extends AbstractComponentConfigurator<Layout, C>
        implements MobileGridColumnConfigurator<C> {

    private final FlexLayout primary = new FlexLayout();
    private final FlexLayout secondary = new FlexLayout();
    private final FlexLayout tertiary = new FlexLayout();

    protected AbstractMobileGridColumnConfigurator(Layout layout) {
        super(layout);

        // Root container
        getComponent().addClassName("mobile-grid-column");
        getComponent().setFlexDirection(FlexDirection.COLUMN);

        // Sections
        primary.addClassName("mobile-grid-primary");
        secondary.addClassName("mobile-grid-secondary");
        tertiary.addClassName("mobile-grid-tertiary");

        // Initially hidden
        primary.setVisible(false);
        secondary.setVisible(false);
        tertiary.setVisible(false);

        // Attach eagerly
        getComponent().add(primary, secondary, tertiary);
    }

    /* -------------------------------------------------
     * Helpers
     * ------------------------------------------------- */

    private void show(FlexLayout layout) {
        if (!layout.isVisible()) {
            layout.setVisible(true);
        }
    }

    private Span createText(String text) {
        Span span = Components.span().text(text).build();
        span.getElement().setAttribute("title", text);
        return span;
    }

    /* -------------------------------------------------
     * Component capabilities
     * ------------------------------------------------- */

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }

    /* -------------------------------------------------
     * Layout direction
     * ------------------------------------------------- */

    @Override
    public C flexDirection(FlexDirection direction) {
        getComponent().setFlexDirection(direction);

        if (direction == FlexDirection.ROW) {
            primary.addClassName("mobile-grid-column-layout");
            secondary.addClassName("mobile-grid-column-layout");
            tertiary.addClassName("mobile-grid-column-layout");

            secondary.addClassName("mobile-grid-grow");
        }

        return getConfigurator();
    }

    /* -------------------------------------------------
     * Media
     * ------------------------------------------------- */

    @Override
    public C withImageAsPrimary(String imagePath, String altText) {
        Image image = new Image(imagePath, altText);
        image.addClassName("mobile-grid-image");
        getComponent().addComponentAsFirst(image);
        return getConfigurator();
    }

    /* -------------------------------------------------
     * Actions
     * ------------------------------------------------- */

    @Override
    public C withContextMenuAsPrimary(
            ContextMenu contextMenu,
            Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {

        ObjectUtils.argumentNotNull(configurator, "Configurator must not be null");

        Button actionButton = Components.button()
                .tertiaryInline()
                .iconConfigurator(VaadinIcon.ELLIPSIS_DOTS_V)
                .add()
                .tooltipText("Action")
                .build();

        contextMenu.setOpenOnClick(true);
        contextMenu.setTarget(actionButton);
        configurator.accept(ButtonConfigurator.configure(actionButton));

        primary.add(actionButton);
        show(primary);
        return getConfigurator();
    }

    @Override
    public C withMenuBarAsPrimary(MenuBar menuBar) {
        ObjectUtils.argumentNotNull(menuBar, "MenuBar must not be null");
        primary.add(menuBar);
        show(primary);
        return getConfigurator();
    }

    /* -------------------------------------------------
     * Badges
     * ------------------------------------------------- */

    @Override
    public C withBadgeAsPrimary(String text) {
        return withBadgeAsPrimary(
                new Badge(text, BadgeColor.NORMAL, BadgeSize.S, BadgeShape.PILL)
        );
    }

    @Override
    public C withBadgeAsPrimary(Badge badge) {
        primary.add(badge);
        primary.setFlexShrink(0, badge);
        show(primary);
        return getConfigurator();
    }

    /* -------------------------------------------------
     * Text sections (Span-based)
     * ------------------------------------------------- */

    @Override
    public C withPrimaryText(String text) {
        Span label = createText(text);
        label.addClassName("mobile-grid-primary-text");

        primary.add(label);
        if (primary.getComponentCount() == 1) {
            primary.setFlexGrow(1, label);
        }
        show(primary);
        return getConfigurator();
    }

    @Override
    public C withSecondaryText(String text) {
        Span label = createText(text);
        label.addClassName("mobile-grid-secondary-text");

        secondary.add(label);
        show(secondary);
        return getConfigurator();
    }

    @Override
    public C withTertiaryText(String text) {
        Span label = createText(text);
        label.setId("tertiary-text");
        label.addClassName("mobile-grid-tertiary-value");

        if (NumberUtils.isCreatable(text)) {
            label.setText(UIUtils.getCurrencySymbol() + text);
            label.addClassName("mobile-grid-currency");

            double value = NumberUtils.createNumber(text).doubleValue();
            label.addClassName(value > 0
                    ? "mobile-grid-positive"
                    : "mobile-grid-negative");
        }

        tertiary.add(label);
        tertiary.setFlexGrow(1, label);
        show(tertiary);
        return getConfigurator();
    }

    @Override
    public C withTertiaryCurrencyValueAndDate(String currencyValue, LocalDate date) {
        withTertiaryText(currencyValue);

        Span dateLabel = createText(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        dateLabel.addClassName("mobile-grid-tertiary-date");

        tertiary.add(dateLabel);
        show(tertiary);
        return getConfigurator();
    }

    /* -------------------------------------------------
     * Generic component helpers
     * ------------------------------------------------- */

    private void add(FlexLayout layout, Component... components) {
        layout.add(components);
        if (components.length > 0) {
            layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        }
        show(layout);
    }

    @Override
    public C withPrimaryComponents(Component... components) {
        add(primary, components);
        return getConfigurator();
    }

    @Override
    public C withSecondaryComponents(Component... components) {
        add(secondary, components);
        return getConfigurator();
    }

    @Override
    public C withTertiaryComponents(Component... components) {
        add(tertiary, components);
        return getConfigurator();
    }

    @Override
    public C withAvatarAsPrimary(String name) {
        add(primary, new Avatar(name));
        return getConfigurator();
    }

    /* -------------------------------------------------
     * Custom configuration
     * ------------------------------------------------- */

    @Override
    public C configurePrimary(
            Consumer<FlexLayoutConfigurator.BaseFlexLayoutConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must not be null");
        configurator.accept(FlexLayoutConfigurator.configure(primary));
        show(primary);
        return getConfigurator();
    }

    @Override
    public C configureSecondary(
            Consumer<FlexLayoutConfigurator.BaseFlexLayoutConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must not be null");
        configurator.accept(FlexLayoutConfigurator.configure(secondary));
        show(secondary);
        return getConfigurator();
    }

    @Override
    public C configureTertiary(
            Consumer<FlexLayoutConfigurator.BaseFlexLayoutConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must not be null");
        configurator.accept(FlexLayoutConfigurator.configure(tertiary));
        show(tertiary);
        return getConfigurator();
    }

    /* -------------------------------------------------
     * Convenience
     * ------------------------------------------------- */

    @Override
    public C withPrimaryTextAndBadge(String text, Badge badge) {
        return withPrimaryText(text).withBadgeAsPrimary(badge);
    }
}
