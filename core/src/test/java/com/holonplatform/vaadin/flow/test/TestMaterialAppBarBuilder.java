package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.MaterialAppBarBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.iyensoft.vaadin.flow.components.MaterialAppBar;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.holonplatform.vaadin.flow.components.support.ViewMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestMaterialAppBarBuilder {

    @Test
    void create_buildsSmallAppBarWithSemanticSlots() {
        MaterialAppBar appBar = MaterialAppBarBuilder.create()
                .headline("Dashboard")
                .subtitle(new Span("Overview"))
                .leading(new Button("Menu"))
                .actions(new Button("Settings"))
                .build();

        assertEquals(MaterialAppBar.Variant.SMALL, appBar.getVariant());
        assertEquals("banner", appBar.getElement().getAttribute("role"));
        assertEquals(1, appBar.getLeadingSlot().getComponentCount());
        assertEquals(2, appBar.getContentSlot().getComponentCount());
        assertEquals(1, appBar.getTrailingSlot().getComponentCount());
    }

    @Test
    void configuration_setsM3VariantAndStates() {
        MaterialAppBar appBar = MaterialAppBarBuilder.create()
                .variant(MaterialAppBar.Variant.LARGE_FLEXIBLE)
                .centered()
                .search()
                .scrolled()
                .build();

        assertEquals(MaterialAppBar.Variant.LARGE_FLEXIBLE, appBar.getVariant());
        assertTrue(appBar.getClassNames().contains("material-app-bar--large-flexible"));
        assertTrue(appBar.getClassNames().contains("material-app-bar--centered"));
        assertTrue(appBar.getClassNames().contains("material-app-bar--search-active"));
        assertTrue(appBar.getClassNames().contains("material-app-bar--scrolled"));
    }

    @Test
    void createWithComponents_placesComponentsInLeadingSlot() {
        MaterialAppBar appBar = MaterialAppBarBuilder.create(new Span("Brand")).build();

        assertEquals(1, appBar.getLeadingSlot().getComponentCount());
    }

    @Test
    void overflowAction_addsAccessibleOverflowTrigger() {
        MaterialAppBar appBar = MaterialAppBarBuilder.create()
                .overflowAction("Filter", () -> { })
                .build();

        assertEquals(1, appBar.getTrailingSlot().getComponentCount());
        Component overflowButton = appBar.getTrailingSlot().getChildren().findFirst().orElseThrow();
        assertTrue(overflowButton.getClassNames().contains("material-app-bar__overflow"));
        assertEquals(LocalizationProvider.localize("More actions", "material_app_bar.more_actions_aria"),
                overflowButton.getElement().getAttribute("aria-label"));
    }

    @Test
    void responsiveAction_isInlineBeforeMobileLayoutIsDetected() {
        Button filterButton = new Button("Filter");
        MaterialAppBar appBar = MaterialAppBarBuilder.create()
                .responsiveAction(filterButton, "Filter", () -> { })
                .build();

        assertSame(appBar.getTrailingSlot(), filterButton.getParent().orElseThrow());
    }

    @Test
    void viewMode_isControlledByTheDeveloper() {
        MaterialAppBar appBar = MaterialAppBarBuilder.create()
                .viewMode(ViewMode.MOBILE)
                .build();

        assertEquals(ViewMode.MOBILE, appBar.getViewMode());
        appBar.setViewMode(ViewMode.TABLET);
        assertEquals(ViewMode.TABLET, appBar.getViewMode());
    }

    @Test
    void responsiveAction_acceptsInitialViewMode() {
        Button filterButton = new Button("Filter");
        MaterialAppBar appBar = MaterialAppBarBuilder.create()
                .responsiveAction(ViewMode.MOBILE, filterButton, "Filter", () -> { })
                .build();

        assertEquals(ViewMode.MOBILE, appBar.getViewMode());
        assertNotSame(appBar.getTrailingSlot(), filterButton.getParent().orElse(null));
    }
}