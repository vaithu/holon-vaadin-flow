package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.internal.lumo.SeparatorColor;
import com.iyensoft.vaadin.flow.components.builders.IyenDetailBuilder;
import com.iyensoft.vaadin.flow.components.builders.IyenMasterBuilder;
import com.iyensoft.vaadin.flow.utils.responsive.IyenResponsiveLayout;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.signals.Signal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests IyenResponsiveLayout in isolation — no Vaadin UI session required.
 * Covers: construction, forceMode, CSS classes, viewModeSignal and separator.
 */
class TestAbstractIyenView {

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private IyenResponsiveLayout layout() {
        return new IyenResponsiveLayout(IyenMasterBuilder.create(), IyenDetailBuilder.create());
    }

    private IyenResponsiveLayout layout(ViewMode initialMode) {
        return new IyenResponsiveLayout(IyenMasterBuilder.create(), IyenDetailBuilder.create(), initialMode);
    }

    // ------------------------------------------------------------------
    // Construction
    // ------------------------------------------------------------------

    @Test
    void noArgConstructorLeavesCurrentModeNull() {
        assertNull(layout().getCurrentMode());
    }

    @Test
    void initialModeConstructorSetsModeCorrectly() {
        assertEquals(ViewMode.DESKTOP, layout(ViewMode.DESKTOP).getCurrentMode());
    }

    @Test
    void initialModeConstructorPublishesToSignal() {
        // signal must reflect the initial mode, not the field default
        IyenResponsiveLayout l = layout(ViewMode.DESKTOP);
        assertEquals(ViewMode.DESKTOP, l.viewModeSignal().peek());
    }

    // ------------------------------------------------------------------
    // forceMode — currentMode
    // ------------------------------------------------------------------

    @Test
    void forceModeUpdatesCurrentMode() {
        IyenResponsiveLayout l = layout();
        l.forceMode(ViewMode.TABLET);
        assertEquals(ViewMode.TABLET, l.getCurrentMode());
    }

    @Test
    void forceModeNullIsIgnored() {
        IyenResponsiveLayout l = layout(ViewMode.MOBILE);
        l.forceMode(null);
        assertEquals(ViewMode.MOBILE, l.getCurrentMode());
    }

    // ------------------------------------------------------------------
    // forceMode — CSS classes
    // ------------------------------------------------------------------

    @Test
    void forceMobileSetsIyenMobileClass() {
        IyenResponsiveLayout l = layout();
        l.forceMode(ViewMode.MOBILE);
        assertTrue(l.getClassNames().contains("iyen-mobile"), "Expected iyen-mobile class");
        assertFalse(l.getClassNames().contains("iyen-desktop"), "Did not expect iyen-desktop class");
    }

    @Test
    void forceDesktopSetsIyenDesktopClass() {
        IyenResponsiveLayout l = layout();
        l.forceMode(ViewMode.DESKTOP);
        assertTrue(l.getClassNames().contains("iyen-desktop"), "Expected iyen-desktop class");
        assertFalse(l.getClassNames().contains("iyen-mobile"), "Did not expect iyen-mobile class");
    }

    @Test
    void forceModeSwapsClassesOnDesktopToMobileTransition() {
        IyenResponsiveLayout l = layout(ViewMode.DESKTOP);
        assertTrue(l.getClassNames().contains("iyen-desktop"));
        l.forceMode(ViewMode.MOBILE);
        assertTrue(l.getClassNames().contains("iyen-mobile"));
        assertFalse(l.getClassNames().contains("iyen-desktop"));
    }

    @Test
    void forceMobilePortraitTreatedAsMobile() {
        IyenResponsiveLayout l = layout();
        l.forceMode(ViewMode.MOBILE_PORTRAIT);
        assertTrue(l.getClassNames().contains("iyen-mobile"));
    }

    @Test
    void forceUltraWideTreatedAsDesktop() {
        IyenResponsiveLayout l = layout();
        l.forceMode(ViewMode.ULTRA_WIDE);
        assertTrue(l.getClassNames().contains("iyen-desktop"));
    }

    // ------------------------------------------------------------------
    // Signal
    // ------------------------------------------------------------------

    @Test
    void viewModeSignalIsNotNull() {
        assertNotNull(layout().viewModeSignal());
    }

    @Test
    void viewModeSignalIsReadOnly() {
        // asReadonly() must not be an instance of ValueSignal (writable)
        Signal<ViewMode> sig = layout().viewModeSignal();
        // the signal must not expose set() — verified structurally
        assertFalse(sig.getClass().getSimpleName().equals("ValueSignal"),
                "viewModeSignal() must return a readonly signal, not a ValueSignal");
    }

    @Test
    void viewModeSignalUpdatesOnForceMode() {
        IyenResponsiveLayout l = layout(ViewMode.MOBILE);
        l.forceMode(ViewMode.LARGE_DESKTOP);
        assertEquals(ViewMode.LARGE_DESKTOP, l.viewModeSignal().peek());
    }

    @Test
    void viewModeSignalDoesNotChangeOnSameMode() {
        IyenResponsiveLayout l = layout(ViewMode.DESKTOP);
        l.forceMode(ViewMode.DESKTOP);
        assertEquals(ViewMode.DESKTOP, l.viewModeSignal().peek());
    }

    // ------------------------------------------------------------------
    // Separator
    // ------------------------------------------------------------------

    @Test
    void withSeparatorAddsSeparatorElementInDesktopMode() {
        IyenResponsiveLayout l = layout(ViewMode.DESKTOP);
        l.withSeparator();
        boolean hasSeparator = findChildWithClass(l, "iyen-md-separator");
        assertTrue(hasSeparator, "Expected separator element with class iyen-md-separator");
    }

    @Test
    void withSeparatorColorAddsColorCssClass() {
        IyenResponsiveLayout l = layout(ViewMode.DESKTOP);
        l.withSeparator(SeparatorColor.PRIMARY);
        // SeparatorColor.PRIMARY.getClassName() == "color-bg-primary" (utilities.css pattern)
        boolean hasColorClass = findChildWithClass(l, SeparatorColor.PRIMARY.getClassName());
        assertTrue(hasColorClass, "Expected " + SeparatorColor.PRIMARY.getClassName() + " class on separator");
    }

    @Test
    void separatorNotShownInMobileMode() {
        IyenResponsiveLayout l = layout(ViewMode.MOBILE);
        l.withSeparator();
        boolean hasSeparator = findChildWithClass(l, "iyen-md-separator");
        assertFalse(hasSeparator, "Separator must not appear in mobile mode");
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /** Recursively search children for a CSS class match. */
    private boolean findChildWithClass(Component root, String cssClass) {
        return root.getChildren().anyMatch(child ->
                child.getClassNames().contains(cssClass) || findChildWithClass(child, cssClass)
        );
    }
}
