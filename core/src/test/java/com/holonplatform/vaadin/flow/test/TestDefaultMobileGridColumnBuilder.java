package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Badge;
import com.holonplatform.vaadin.flow.internal.lumo.FlexDirection;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultMobileGridColumnBuilder;
import com.iyensoft.vaadin.flow.internal.components.builders.MobileGridColumnBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TestDefaultMobileGridColumnBuilder {

    private DefaultMobileGridColumnBuilder builder;
    private Layout root;

    @BeforeEach
    void setUp() {
        builder = new DefaultMobileGridColumnBuilder();
        root = builder.build();
    }

    /* -------------------------------------------------
     * Helpers (root layout structure)
     * ------------------------------------------------- */

    private FlexLayout primary() {
        return (FlexLayout) root.getComponentAt(0);
    }

    private FlexLayout secondary() {
        return (FlexLayout) root.getComponentAt(1);
    }

    private FlexLayout tertiary() {
        return (FlexLayout) root.getComponentAt(2);
    }

    /* -------------------------------------------------
     * Constructor invariants
     * ------------------------------------------------- */

    @Test
    void sections_areAttachedAndInitiallyHidden() {
        assertThat(root.getComponentCount()).isEqualTo(3);

        assertThat(primary().isVisible()).isFalse();
        assertThat(secondary().isVisible()).isFalse();
        assertThat(tertiary().isVisible()).isFalse();

        assertThat(primary().getClassNames()).contains("mobile-grid-primary");
        assertThat(secondary().getClassNames()).contains("mobile-grid-secondary");
        assertThat(tertiary().getClassNames()).contains("mobile-grid-tertiary");
    }

    /* -------------------------------------------------
     * flexDirection
     * ------------------------------------------------- */

    @Test
    void flexDirection_row_updatesRootDirection() {
        builder.flexDirection(FlexDirection.ROW);

        assertThat(root.getFlexDirection()).isEqualTo(FlexDirection.ROW);
    }

    /* -------------------------------------------------
     * withImageAsPrimary
     * ------------------------------------------------- */

    @Test
    void withImageAsPrimary_addsImageAsFirstComponent() {
        builder.withImageAsPrimary("img.png", "alt");

        Component first = root.getComponentAt(0);
        assertThat(first).isInstanceOf(Image.class);

        Image image = (Image) first;
        assertThat(image.getAlt().orElse(null)).isEqualTo("alt");
        assertThat(image.getSrc()).contains("img.png");
        assertThat(image.getClassNames()).contains("mobile-grid-image");
    }

    /* -------------------------------------------------
     * Primary text
     * ------------------------------------------------- */

    @Test
    void withPrimaryText_makesPrimaryVisible_andAddsSpan() {
        builder.withPrimaryText("Primary");

        assertThat(primary().isVisible()).isTrue();
        assertThat(primary().getComponentCount()).isEqualTo(1);

        Component label = primary().getComponentAt(0);
        assertThat(label).isInstanceOf(Span.class);
        assertThat(label.getElement().getText()).contains("Primary");
        assertThat(label.getClassNames()).contains("mobile-grid-primary-text");
    }

    /* -------------------------------------------------
     * Secondary text
     * ------------------------------------------------- */

    @Test
    void withSecondaryText_makesSecondaryVisible_andAddsSpan() {
        builder.withSecondaryText("Secondary");

        assertThat(secondary().isVisible()).isTrue();
        assertThat(secondary().getComponentCount()).isEqualTo(1);

        Component label = secondary().getComponentAt(0);
        assertThat(label).isInstanceOf(Span.class);
        assertThat(label.getElement().getText()).contains("Secondary");
        assertThat(label.getClassNames()).contains("mobile-grid-secondary-text");
    }

    /* -------------------------------------------------
     * Tertiary text (non-numeric)
     * ------------------------------------------------- */

    @Test
    void withTertiaryText_addsSpanAndShowsTertiary() {
        builder.withTertiaryText("Info");

        assertThat(tertiary().isVisible()).isTrue();
        assertThat(tertiary().getComponentCount()).isEqualTo(1);

        Component label = tertiary().getComponentAt(0);
        assertThat(label).isInstanceOf(Span.class);
        assertThat(label.getElement().getText()).contains("Info");
        assertThat(label.getClassNames()).contains("mobile-grid-tertiary-value");
    }

    /* -------------------------------------------------
     * Tertiary text (numeric)
     * ------------------------------------------------- */

    @Test
    void withTertiaryText_numericValue_formatsAsCurrency() {
        builder.withTertiaryText("100");

        Component label = tertiary().getComponentAt(0);

        assertThat(label.getElement().getText()).contains("100");
        assertThat(label.getClassNames()).contains("mobile-grid-currency");
        assertThat(label.getClassNames())
                .containsAnyOf("mobile-grid-positive", "mobile-grid-negative");
    }

    /* -------------------------------------------------
     * Tertiary currency + date
     * ------------------------------------------------- */

    @Test
    void withTertiaryCurrencyValueAndDate_addsValueAndDate() {
        builder.withTertiaryCurrencyValueAndDate("250", LocalDate.of(2024, 1, 1));

        List<Component> children = tertiary().getChildren().toList();
        assertThat(children).hasSize(2);

        assertThat(children.get(0).getElement().getText()).contains("250");
        assertThat(children.get(1).getElement().getText())
                .isEqualTo("2024-01-01");

        assertThat(children.get(1).getClassNames())
                .contains("mobile-grid-tertiary-date");
    }

    /* -------------------------------------------------
     * Badges
     * ------------------------------------------------- */

    @Test
    void withBadgeAsPrimary_addsBadgeAndShowsPrimary() {
        builder.withBadgeAsPrimary("ACTIVE");

        assertThat(primary().isVisible()).isTrue();
        assertThat(primary().getChildren()
                .anyMatch(c -> c instanceof Badge))
                .isTrue();
    }

    /* -------------------------------------------------
     * Context menu
     * ------------------------------------------------- */

    @Test
    void withContextMenuAsPrimary_addsActionButton() {
        ContextMenu menu = new ContextMenu();

        builder.withContextMenuAsPrimary(menu, cfg -> {});

        assertThat(primary().isVisible()).isTrue();
        assertThat(primary().getChildren()
                .anyMatch(c -> c.getClass().getSimpleName().contains("Button")))
                .isTrue();
    }

    /* -------------------------------------------------
     * MenuBar
     * ------------------------------------------------- */

    @Test
    void withMenuBarAsPrimary_addsMenuBar() {
        MenuBar menuBar = new MenuBar();

        builder.withMenuBarAsPrimary(menuBar);

        assertThat(primary().getChildren()
                .anyMatch(c -> c instanceof MenuBar))
                .isTrue();
    }

    /* -------------------------------------------------
     * withPrimaryComponents / Secondary / Tertiary
     * ------------------------------------------------- */

    @Test
    void withPrimaryComponents_addsComponents_andJustifiesBetween() {
        Component a = new Image();
        Component b = new Image();

        builder.withPrimaryComponents(a, b);

        assertThat(primary().isVisible()).isTrue();
        assertThat(primary().getComponentCount()).isEqualTo(2);
        assertThat(primary().getJustifyContentMode())
                .isEqualTo(FlexComponent.JustifyContentMode.BETWEEN);
    }

    @Test
    void withSecondaryComponents_addsComponents() {
        builder.withSecondaryComponents(new Image());

        assertThat(secondary().isVisible()).isTrue();
        assertThat(secondary().getComponentCount()).isEqualTo(1);
    }

    @Test
    void withTertiaryComponents_addsComponents() {
        builder.withTertiaryComponents(new Image());

        assertThat(tertiary().isVisible()).isTrue();
        assertThat(tertiary().getComponentCount()).isEqualTo(1);
    }

    /* -------------------------------------------------
     * Avatar
     * ------------------------------------------------- */

    @Test
    void withAvatarAsPrimary_addsAvatar() {
        builder.withAvatarAsPrimary("John Doe");

        assertThat(primary().getChildren()
                .anyMatch(c -> c instanceof Avatar))
                .isTrue();
    }

    /* -------------------------------------------------
     * configurePrimary / Secondary / Tertiary
     * ------------------------------------------------- */

    @Test
    void configurePrimary_appliesConfigurator() {
        builder.configurePrimary(cfg -> cfg.styleName("primary-custom"));

        assertThat(primary().getClassNames()).contains("primary-custom");
    }

    @Test
    void configureSecondary_appliesConfigurator() {
        builder.configureSecondary(cfg -> cfg.styleName("secondary-custom"));

        assertThat(secondary().getClassNames()).contains("secondary-custom");
    }

    @Test
    void configureTertiary_appliesConfigurator() {
        builder.configureTertiary(cfg -> cfg.styleName("tertiary-custom"));

        assertThat(tertiary().getClassNames()).contains("tertiary-custom");
    }

    /* -------------------------------------------------
     * Convenience
     * ------------------------------------------------- */

    @Test
    void withPrimaryTextAndBadge_addsTextAndBadge() {
        builder.withPrimaryTextAndBadge("Title", new Badge("NEW"));

        assertThat(primary().getComponentCount()).isGreaterThanOrEqualTo(2);
    }

    /* -------------------------------------------------
     * Fluent API
     * ------------------------------------------------- */

    @Test
    void fluentMethods_returnSameBuilderInstance() {
        MobileGridColumnBuilder result =
                builder.withPrimaryText("A")
                        .withSecondaryText("B")
                        .withBadgeAsPrimary("C");

        assertThat(result).isSameAs(builder);
    }
}