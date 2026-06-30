package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.IconBadge;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestIconBadge {

    @Test
    void constructor_defaultVariant_doesNotAddModifierClass() {
        IconBadge badge = new IconBadge(VaadinIcon.INFO_CIRCLE, Alert.Variant.DEFAULT);

        assertNull(badge.getVariant());
        assertTrue(badge.getClassNames().contains("icon-badge"));
        assertFalse(badge.getClassNames().contains("icon-badge--default"));
        assertFalse(badge.getClassNames().contains("icon-badge--info"));
    }

    @Test
    void setVariant_replacesPreviousModifierClass() {
        IconBadge badge = new IconBadge(VaadinIcon.INFO_CIRCLE, Alert.Variant.WARNING);

        badge.setVariant(Alert.Variant.SUCCESS);

        assertEquals(Alert.Variant.SUCCESS, badge.getVariant());
        assertFalse(badge.getClassNames().contains("icon-badge--warning"));
        assertTrue(badge.getClassNames().contains("icon-badge--success"));
    }

    @Test
    void setVariant_null_clearsModifierClass() {
        IconBadge badge = new IconBadge(VaadinIcon.INFO_CIRCLE, Alert.Variant.INFO);

        badge.setVariant(null);

        assertNull(badge.getVariant());
        assertFalse(badge.getClassNames().contains("icon-badge--info"));
    }

    @Test
    void setText_addsTextSlotAndKeepsIconBadgeVisible() {
        IconBadge badge = IconBadge.builder(VaadinIcon.INFO_CIRCLE.create(), Alert.Variant.INFO).build();

        badge.setText("Draft");

        assertEquals("Draft", badge.getText());
        assertTrue(badge.getClassNames().contains("icon-badge--has-text"));
        assertFalse(badge.getElement().hasAttribute("aria-hidden"));
    }

    @Test
    void setText_updatesText_andClearTextRestoresIconOnlyMode() {
        IconBadge badge = IconBadge.builder(VaadinIcon.INFO_CIRCLE.create(), Alert.Variant.INFO).build();

        badge.setText("Draft");
        badge.setText("Published");
        badge.clearText();

        assertEquals("", badge.getText());
        assertFalse(badge.getClassNames().contains("icon-badge--has-text"));
        assertTrue(badge.getElement().hasAttribute("aria-hidden"));
    }

    @Test
    void builder_and_fluent_text_api_work_together() {
        IconBadge badge = IconBadge.builder(VaadinIcon.INFO_CIRCLE.create(), Alert.Variant.INFO, IconBadge.Size.SM, "Draft").build();

        assertEquals("Draft", badge.getText());
        assertTrue(badge.hasText());
        assertTrue(badge.getClassNames().contains("icon-badge--has-text"));

        badge.text("Published");

        assertEquals("Published", badge.getText());
        assertTrue(badge.hasText());
    }

    @Test
    void pill_text_mode_keeps_compact_and_spacious_size_presets() {
        IconBadge compact = IconBadge.builder(VaadinIcon.INFO_CIRCLE.create(), Alert.Variant.INFO, IconBadge.Size.XS, "Draft").build();
        IconBadge spacious = IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS, IconBadge.Size.XL, "Published").build();

        assertEquals(IconBadge.Size.XS, compact.getBadgeSize());
        assertEquals(IconBadge.Size.XL, spacious.getBadgeSize());
        assertTrue(compact.getClassNames().contains("icon-badge--has-text"));
        assertTrue(spacious.getClassNames().contains("icon-badge--has-text"));
        assertTrue(compact.getClassNames().contains("icon-badge--xs"));
        assertTrue(spacious.getClassNames().contains("icon-badge--xl"));

        compact.clearText();
        spacious.text("Archived");

        assertFalse(compact.hasText());
        assertEquals("Archived", spacious.getText());
        assertTrue(spacious.hasText());
        assertEquals(IconBadge.Size.XL, spacious.getBadgeSize());
    }

    @Test
    void size_preset_roundTrips_for_new_variants() {
        IconBadge xs = IconBadge.builder(VaadinIcon.INFO_CIRCLE.create(), Alert.Variant.INFO, IconBadge.Size.XS).build();
        IconBadge xl = IconBadge.builder(VaadinIcon.INFO_CIRCLE.create(), Alert.Variant.INFO, IconBadge.Size.XL).build();

        assertEquals(IconBadge.Size.XS, xs.getBadgeSize());
        assertTrue(xs.getClassNames().contains("icon-badge--xs"));

        assertEquals(IconBadge.Size.XL, xl.getBadgeSize());
        assertTrue(xl.getClassNames().contains("icon-badge--xl"));
    }
}



