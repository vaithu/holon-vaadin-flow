package com.iyensoft.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.iyensoft.vaadin.flow.components.MaterialHeader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.iyensoft.vaadin.flow.components.BreadcrumbItem;
import com.iyensoft.vaadin.flow.components.BreadcrumbPage;
import com.holonplatform.vaadin.flow.components.support.ViewMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestMaterialHeaderBuilder {

    @Test
    void build_configuresIndependentMaterialHeaderSlots() {
        MaterialHeader header = Components.materialHeader()
                .variant(MaterialHeader.Variant.MEDIUM)
                .headline("Orders")
                .subtitle(new Span("12 open orders"))
                .leading(new Button("Back"))
                .actions(new Button("Sign out"))
                .build();

        assertEquals(MaterialHeader.Variant.MEDIUM, header.getVariant());
        assertEquals("region", header.getElement().getAttribute("role"));
        assertEquals(1, header.getLeadingSlot().getComponentCount());
        assertEquals(2, header.getContentSlot().getComponentCount());
        assertEquals(1, header.getTrailingSlot().getComponentCount());
    }

    @Test
    void responsiveAction_isInlineBeforeMobileLayoutIsDetected() {
        Button themeButton = new Button("Theme");
        MaterialHeader header = Components.materialHeader()
                .responsiveAction(themeButton, "Change theme", () -> { })
                .build();

        assertSame(header.getTrailingSlot(), themeButton.getParent().orElseThrow());
    }

    @Test
    void viewMode_isControlledByTheDeveloper() {
        MaterialHeader header = Components.materialHeader()
                .viewMode(ViewMode.MOBILE)
                .build();

        assertEquals(ViewMode.MOBILE, header.getViewMode());
        header.setViewMode(ViewMode.TABLET);
        assertEquals(ViewMode.TABLET, header.getViewMode());
    }

    @Test
    void overflowAction_addsAccessibleOverflowTrigger() {
        MaterialHeader header = Components.materialHeader()
                .overflowAction("Change language", () -> { })
                .build();

        Component overflowButton = header.getTrailingSlot().getChildren().findFirst().orElseThrow();
        assertTrue(overflowButton.getClassNames().contains("material-header__overflow"));
        assertEquals(LocalizationProvider.localize("More actions", "material_header.more_actions_aria"),
                overflowButton.getElement().getAttribute("aria-label"));
    }

    @Test
    void detailRegions_areAvailableForCrmHeaders() {
        MaterialHeader header = Components.materialHeader()
                .breadcrumb(new Span("CRM > Customers > Helix Robotics"))
                .headline("Helix Robotics SE")
                .details(new Span("C-2026-0023 · owner Elena Lindqvist"))
                .tags(new Span("Active"), new Span("VIP"))
                .primaryAction(new Button("Re-send dunning"))
                .secondaryAction(new Button("Print"))
                .build();

        assertEquals(1, header.getBreadcrumbSlot().getComponentCount());
        assertEquals(1, header.getDetailsSlot().getComponentCount());
        assertEquals(2, header.getTagsSlot().getComponentCount());
        assertEquals(1, header.getTrailingSlot().getChildren()
                .filter(component -> component.getClassNames().contains("material-header__primary-action"))
                .count());
    }

    @Test
    void genericMediaAndStructuredBreadcrumb_areSupported() {
        MaterialHeader header = Components.materialHeader()
                .media(new Span("Media"))
                .breadcrumb(new BreadcrumbItem(new Span("Home")), new BreadcrumbPage("Current"))
                .headline("Details")
                .build();

        assertEquals(1, header.getMediaSlot().getComponentCount());
        assertEquals(1, header.getBreadcrumbSlot().getComponentCount());
        assertEquals(header.getLeadingSlot(), header.getMediaSlot().getParent().orElseThrow());
    }
}
