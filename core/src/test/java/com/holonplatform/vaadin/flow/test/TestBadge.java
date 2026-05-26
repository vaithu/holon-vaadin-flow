package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Badge;
import com.holonplatform.vaadin.flow.components.css.BadgeColor;
import com.holonplatform.vaadin.flow.components.css.BadgeShape;
import com.holonplatform.vaadin.flow.components.css.BadgeSize;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestBadge {

    @Test
    void testDefaultBadge() {
        var badge = new Badge("Info");
        assertTrue(badge.getClassNames().contains("badge"));
        assertEquals("badge", badge.getElement().getAttribute("theme"));
    }

    @Test
    void testBadgeWithColor() {
        var badge = new Badge("Success", BadgeColor.SUCCESS);
        assertTrue(badge.getClassNames().contains("badge"));
        assertEquals("badge success", badge.getElement().getAttribute("theme"));
    }

    @Test
    void testBadgeWithColorSizeShape() {
        var badge = new Badge("Error", BadgeColor.ERROR, BadgeSize.S, BadgeShape.PILL);
        assertTrue(badge.getClassNames().contains("badge"));
        var theme = badge.getElement().getAttribute("theme");
        assertTrue(theme.contains("badge error"));
        assertTrue(theme.contains("pill"));
        assertTrue(theme.contains("small"));
    }

    @Test
    void testBadgeNormalShapeNotAdded() {
        var badge = new Badge("Normal", BadgeColor.NORMAL, BadgeSize.M, BadgeShape.NORMAL);
        var theme = badge.getElement().getAttribute("theme");
        assertEquals("badge", theme);
    }

    @Test
    void testBadgePillOnly() {
        var badge = new Badge("Pill", BadgeColor.NORMAL, BadgeSize.M, BadgeShape.PILL);
        var theme = badge.getElement().getAttribute("theme");
        assertTrue(theme.contains("pill"));
        assertFalse(theme.contains("small"));
    }

    @Test
    void testBadgeSmallOnly() {
        var badge = new Badge("Small", BadgeColor.NORMAL, BadgeSize.S, BadgeShape.NORMAL);
        var theme = badge.getElement().getAttribute("theme");
        assertTrue(theme.contains("small"));
        assertFalse(theme.contains("pill"));
    }

    @Test
    void testBadgeAllColors() {
        for (BadgeColor color : BadgeColor.values()) {
            var badge = new Badge("test", color);
            assertNotNull(badge.getElement().getAttribute("theme"));
        }
    }
}
