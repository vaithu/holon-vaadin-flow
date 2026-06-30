package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbItem;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.iyensoft.vaadin.flow.enums.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TestHeader {

    @Test
    void constructor_initialState_hasOnlyRowWithHeading() {
        Header header = new Header("Customer");

        Layout row = header.getRowLayout();
        Layout topRow = header.getTopRowLayout();
        Layout column = header.getColumnLayout();

        assertEquals(1, header.getChildren().count(), "Header should initially contain only the middle row");
        assertEquals(1, row.getComponentCount(), "Row should initially contain only the top row");
        assertSame(topRow, row.getComponentAt(0));

        assertEquals(1, topRow.getComponentCount(), "Top row should initially contain only the column");
        assertSame(column, topRow.getComponentAt(0));

        assertEquals(1, column.getComponentCount(), "Column should initially contain only the line layout");
        Layout line = (Layout) column.getComponentAt(0);
        assertEquals(1, line.getComponentCount(), "Line layout should initially contain only the heading");
        assertEquals("Customer", line.getComponentAt(0).getElement().getText());
        assertTrue(header.getClassNames().contains("iyen-header--bordered"));
    }

    @Test
    void constructor_withLabelBuilder_setsHeadingFromBuilder() {
        LabelBuilder<?> labelBuilder = LabelBuilder.h3().text("FromBuilder");
        Header header = new Header(labelBuilder);

        Layout column = header.getColumnLayout();
        Layout line = (Layout) column.getComponentAt(0);
        assertEquals(1, line.getComponentCount());
        assertEquals("FromBuilder", line.getComponentAt(0).getElement().getText());
    }

    @Test
    void setPrefix_addsAndRemovesPrefixFromRow() {
        Header header = new Header("Title");
        Layout row = header.getRowLayout();
        Layout topRow = header.getTopRowLayout();

        assertEquals(1, row.getComponentCount());

        Span icon = new Span("Icon");
        header.setPrefix(icon);

        assertEquals(1, row.getComponentCount());
        assertSame(topRow, row.getComponentAt(0));
        assertEquals(2, topRow.getComponentCount());
        assertInstanceOf(Layout.class, topRow.getComponentAt(0));
        Layout prefixLayout = (Layout) topRow.getComponentAt(0);
        assertEquals(1, prefixLayout.getComponentCount());
        assertSame(icon, prefixLayout.getComponentAt(0));
        assertSame(header.getColumnLayout(), topRow.getComponentAt(1));

        header.setPrefix();

        assertEquals(1, row.getComponentCount(), "Prefix layout should be removed when empty");
        assertSame(topRow, row.getComponentAt(0));
        assertEquals(1, topRow.getComponentCount());
        assertSame(header.getColumnLayout(), topRow.getComponentAt(0));
    }

    @Test
    void setBreadcrumb_attachesAboveRowAndDetachesCleanly() {
        Header header = new Header("Title");

        BreadcrumbItem home = new BreadcrumbItem(new Span("Home"));
        BreadcrumbItem products = new BreadcrumbItem(new Span("Products"));

        header.setBreadcrumb(home, products);

        assertEquals(2, header.getChildren().count(), "Breadcrumb should sit above the row");
        assertInstanceOf(Breadcrumb.class, header.getComponentAt(0));
        assertSame(header.getRowLayout(), header.getComponentAt(1));

        Breadcrumb breadcrumb = (Breadcrumb) header.getComponentAt(0);
        assertEquals(1, breadcrumb.getComponentCount());
        assertInstanceOf(OrderedList.class, breadcrumb.getComponentAt(0));
        assertEquals(2, ((OrderedList) breadcrumb.getComponentAt(0)).getComponentCount());
        assertTrue(breadcrumb.isVisible());

        header.setBreadcrumb();

        assertEquals(1, header.getChildren().count(), "Breadcrumb should be removed when cleared");
        assertFalse(breadcrumb.isVisible());
    }

    @Test
    void setDetails_attachesBelowHeadingAndDetachesCleanly() {
        Header header = new Header("Title");
        Layout column = header.getColumnLayout();
        Layout line = (Layout) column.getComponentAt(0);

        Span detail1 = new Span("d1");
        Span detail2 = new Span("d2");
        header.setDetails(detail1, detail2);

        assertEquals(2, column.getComponentCount(), "Heading + details");
        assertSame(line, column.getComponentAt(0));
        assertEquals("Title", line.getComponentAt(0).getElement().getText());
        Layout details = (Layout) column.getComponentAt(1);
        assertEquals(2, details.getComponentCount());
        assertSame(detail1, details.getComponentAt(0));
        assertSame(detail2, details.getComponentAt(1));

        header.setDetails();

        assertEquals(1, column.getComponentCount(), "Details layout should be removed when empty");
    }

    @Test
    void setActions_attachesAndDetachesActions() {
        Header header = new Header("Title");
        Layout column = header.getColumnLayout();
        Layout line = (Layout) column.getComponentAt(0);

        Span action = new Span("Action");
        header.setActions(action);

        assertEquals(1, column.getComponentCount(), "Column should keep the line layout");
        assertEquals(2, line.getComponentCount(), "Heading + actions");
        Layout actionsLayout = (Layout) line.getComponentAt(1);
        assertEquals(1, actionsLayout.getComponentCount());
        assertSame(action, actionsLayout.getComponentAt(0));
        assertTrue(actionsLayout.isVisible());

        header.setActions();

        assertEquals(1, line.getComponentCount(), "Actions layout should be removed when empty");
    }

    @Test
    void addActions_accumulatesAndKeepsExistingActions() {
        Header header = new Header("Title");
        Layout column = header.getColumnLayout();
        Layout line = (Layout) column.getComponentAt(0);

        Span action1 = new Span("A1");
        Span action2 = new Span("A2");

        header.addActions(action1);
        header.addActions(action2);

        Layout actionsLayout = (Layout) line.getComponentAt(1);
        assertEquals(2, actionsLayout.getComponentCount());
        assertSame(action1, actionsLayout.getComponentAt(0));
        assertSame(action2, actionsLayout.getComponentAt(1));
    }

    @Test
    void setHeading_levelAndText_replacesComponent() {
        Header header = new Header("Title");
        Layout column = header.getColumnLayout();
        Layout line = (Layout) column.getComponentAt(0);

        Component originalHeading = line.getComponentAt(0);

        header.setHeading("NewTitle", HeadingLevel.H1);

        Component newHeading = line.getComponentAt(0);
        assertNotSame(originalHeading, newHeading);
        assertEquals("NewTitle", newHeading.getElement().getText());
    }

    @Test
    void setHeading_onlyText_updatesExistingHeadingText() {
        Header header = new Header("Title");
        Layout column = header.getColumnLayout();
        Layout line = (Layout) column.getComponentAt(0);

        Component originalHeading = line.getComponentAt(0);

        header.setHeading("Updated");
        Component currentHeading = line.getComponentAt(0);

        assertSame(originalHeading, currentHeading);
        assertEquals("Updated", currentHeading.getElement().getText());
    }

    @Test
    void setTabs_withTabs_attachesTabsBelowTheRow() {
        Header header = new Header("Title");

        Tab t1 = new Tab("Tab1");
        Tab t2 = new Tab("Tab2");
        header.setTabs(t1, t2);

        Optional<Tabs> tabsOpt = header.getTabs();
        assertTrue(tabsOpt.isPresent());
        Tabs tabs = tabsOpt.get();

        assertTrue(tabs.isVisible());
        assertEquals(2, tabs.getChildren().count());
        assertSame(header.getRowLayout(), header.getComponentAt(0));
        assertSame(tabs, header.getComponentAt(1));
    }

    @Test
    void setTabs_empty_removesTabsAndKeepsBorderStateManaged() {
        Header header = new Header("Title");

        header.setTabs();
        Optional<Tabs> tabsOpt = header.getTabs();
        assertTrue(tabsOpt.isPresent(), "Tabs instance should still exist");

        Tabs tabs = tabsOpt.get();
        assertFalse(tabs.isVisible(), "Tabs should be invisible when empty");
        assertTrue(header.getChildren().noneMatch(c -> c == tabs), "Tabs should not be attached to header");
    }

    @Test
    void withoutBorder_addsNoBorderClass() {
        Header header = new Header("Title");

        header.withoutBorder();

        assertTrue(header.getClassNames().contains("header--no-border"));
    }
}
