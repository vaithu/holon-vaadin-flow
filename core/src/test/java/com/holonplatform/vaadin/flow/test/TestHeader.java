package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbItem;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TestHeader {

    // ---------------------------------------------------------------------
    // Constructor / initial state
    // ---------------------------------------------------------------------

    @Test
    void constructor_initialState_hasOnlyHeadingInColumn() {
        Header header = new Header("Customer");

        Layout row = header.getRowLayout();
        Layout column = header.getColumnLayout();

        // Row should contain only column initially
        assertEquals(1, row.getComponentCount(), "Row should initially contain only the column");
        assertSame(column, row.getComponentAt(0), "Column should be the only child of row");

        // Column should contain only heading initially
        assertEquals(1, column.getComponentCount(), "Column should initially contain only heading");
        Component heading = column.getComponentAt(0);
        assertEquals("Customer", heading.getElement().getText());
    }

    @Test
    void constructor_withLabelBuilder_setsHeadingFromBuilder() {
        LabelBuilder<?> labelBuilder = LabelBuilder.h3().text("FromBuilder");
        Header header = new Header(labelBuilder);

        Layout column = header.getColumnLayout();
        assertEquals(1, column.getComponentCount());
        Component heading = column.getComponentAt(0);
        assertEquals("FromBuilder", heading.getElement().getText());
    }

    // ---------------------------------------------------------------------
    // Prefix
    // ---------------------------------------------------------------------

    @Test
    void setPrefix_addsAndRemovesPrefixFromRow() {
        Header header = new Header("Title");

        Layout row = header.getRowLayout();

        // Initially no prefix
        assertEquals(1, row.getComponentCount(), "Row only has column at start");

        Span icon = new Span("Icon");
        header.setPrefix(icon);

        // Now row should have prefix and column (and possibly actions later)
        assertEquals(2, row.getComponentCount(), "Row should now have prefix + column");
        Component first = row.getComponentAt(0);
        assertInstanceOf(Layout.class, first, "First component should be prefix layout");
        Layout prefixLayout = (Layout) first;
        assertEquals(1, prefixLayout.getComponentCount());
        assertSame(icon, prefixLayout.getComponentAt(0));

        // Clear prefix
        header.setPrefix();
        // Row should go back to just column
        assertEquals(1, row.getComponentCount(), "Prefix layout should be removed when empty");
        assertSame(header.getColumnLayout(), row.getComponentAt(0));
    }

    // ---------------------------------------------------------------------
    // Breadcrumb
    // ---------------------------------------------------------------------

    @Test
    void setBreadcrumb_attachesAndDetachesBreadcrumb() {
        Header header = new Header("Title");
        Layout column = header.getColumnLayout();

        // Initially only heading
        assertEquals(1, column.getComponentCount(), "Only heading initially");

        // Use no-arg RouterLink to avoid touching VaadinService / Router
        RouterLink homeLink = new RouterLink();
        homeLink.setText("Home");

        // BreadcrumbItem expects a RouterLink
        BreadcrumbItem homeItem = new BreadcrumbItem(homeLink);
        // If your API is different, use whatever factory/ctor you actually have:
        // BreadcrumbItem homeItem = BreadcrumbItem.of(homeLink);
        // or BreadcrumbItem homeItem = BreadcrumbItem.builder(homeLink).build();

        header.setBreadcrumb(homeItem);

        // Breadcrumb should now be at index 0, heading at index 1
        assertEquals(2, column.getComponentCount(), "Column should have breadcrumb + heading");
        assertInstanceOf(Breadcrumb.class, column.getComponentAt(0), "First component should be Breadcrumb");

        Breadcrumb breadcrumb = (Breadcrumb) column.getComponentAt(0);
        assertEquals(1, breadcrumb.getComponentCount(), "Breadcrumb should contain one item");
        assertTrue(breadcrumb.isVisible(), "Breadcrumb should be visible when it has items");

        // Clearing breadcrumb should remove it from the column
        header.setBreadcrumb();
        assertEquals(1, column.getComponentCount(), "Breadcrumb removed when empty");
        assertFalse(breadcrumb.isVisible(), "Breadcrumb should be invisible when empty");
        assertNotSame(breadcrumb, column.getComponentAt(0), "First component should now be the heading again");
    }

    // ---------------------------------------------------------------------
    // Details
    // ---------------------------------------------------------------------

    @Test
    void setDetails_attachesAndDetachesDetails() {
        Header header = new Header("Title");
        Layout column = header.getColumnLayout();

        assertEquals(1, column.getComponentCount(), "Only heading initially");

        Span detail1 = new Span("d1");
        Span detail2 = new Span("d2");
        header.setDetails(detail1, detail2);

        // Column should have heading + details
        assertEquals(2, column.getComponentCount(), "Heading + details");
        Layout details = (Layout) column.getComponentAt(1);
        assertEquals(2, details.getComponentCount());
        assertSame(detail1, details.getComponentAt(0));
        assertSame(detail2, details.getComponentAt(1));

        // Clearing details removes it
        header.setDetails();
        assertEquals(1, column.getComponentCount(), "Details layout removed when empty");
    }

    // ---------------------------------------------------------------------
    // Actions
    // ---------------------------------------------------------------------

    @Test
    void setActions_attachesAndDetachesActions() {
        Header header = new Header("Title");
        Layout row = header.getRowLayout();

        assertEquals(1, row.getComponentCount(), "Only column initially");

        Span action = new Span("Action");
        header.setActions(action);

        assertEquals(2, row.getComponentCount(), "Column + actions");
        Layout actionsLayout = (Layout) row.getComponentAt(1);
        assertEquals(1, actionsLayout.getComponentCount());
        assertSame(action, actionsLayout.getComponentAt(0));
        assertTrue(actionsLayout.isVisible());

        // Clear
        header.setActions();
        assertEquals(1, row.getComponentCount(), "Actions layout removed when empty");
    }

    @Test
    void addActions_accumulatesAndAttachesOnFirstNonEmpty() {
        Header header = new Header("Title");
        Layout row = header.getRowLayout();

        assertEquals(1, row.getComponentCount());

        Span action1 = new Span("A1");
        Span action2 = new Span("A2");

        header.addActions(action1);
        header.addActions(action2);

        assertEquals(2, row.getComponentCount(), "Column + actions");
        Layout actionsLayout = (Layout) row.getComponentAt(1);
        assertEquals(2, actionsLayout.getComponentCount());
        assertSame(action1, actionsLayout.getComponentAt(0));
        assertSame(action2, actionsLayout.getComponentAt(1));
    }

    // ---------------------------------------------------------------------
    // Heading
    // ---------------------------------------------------------------------

    @Test
    void setHeading_levelAndText_replacesComponent() {
        Header header = new Header("Title");
        Layout column = header.getColumnLayout();

        Component originalHeading = column.getComponentAt(0);

        header.setHeading("NewTitle", HeadingLevel.H1);

        Component newHeading = column.getComponentAt(0);
        assertNotSame(originalHeading, newHeading);
        assertEquals("NewTitle", newHeading.getElement().getText());
    }

    @Test
    void setHeading_onlyText_updatesExistingHeadingText() {
        Header header = new Header("Title");
        Layout column = header.getColumnLayout();

        Component originalHeading = column.getComponentAt(0);

        header.setHeading("Updated");
        Component currentHeading = column.getComponentAt(0);

        assertSame(originalHeading, currentHeading, "Heading component instance should stay same");
        assertEquals("Updated", currentHeading.getElement().getText());
    }

    // ---------------------------------------------------------------------
    // Tabs
    // ---------------------------------------------------------------------

    @Test
    void setTabs_withTabs_attachesTabsAndRemovesBorder() {
        Header header = new Header("Title");

        Tab t1 = new Tab("Tab1");
        Tab t2 = new Tab("Tab2");
        header.setTabs(t1, t2);

        Optional<Tabs> tabsOpt = header.getTabs();
        assertTrue(tabsOpt.isPresent());
        Tabs tabs = tabsOpt.get();

        assertTrue(tabs.isVisible());
        assertEquals(2, tabs.getComponentCount());
        assertSame(t1, tabs.getComponentAt(0));
        assertSame(t2, tabs.getComponentAt(1));

        // Header should contain tabs as a child
        assertTrue(header.getChildren().anyMatch(c -> c == tabs));
    }

    @Test
    void setTabs_empty_removesTabsAndRestoresBorder() {
        Header header = new Header("Title");

        header.setTabs(); // empty
        Optional<Tabs> tabsOpt = header.getTabs();
        assertTrue(tabsOpt.isPresent(), "Tabs instance is present but should not be attached");

        Tabs tabs = tabsOpt.get();
        assertFalse(tabs.isVisible(), "Tabs should be invisible when no tab items");
        assertFalse(header.getChildren().anyMatch(c -> c == tabs), "Tabs should not be attached to header");
    }


    // Simple dummy target view for RouterLink
    @Route("dummy")
    public static class DummyView extends Div {
    }
}
