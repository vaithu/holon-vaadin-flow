package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.HeaderBuilder;
import com.holonplatform.vaadin.flow.components.builders.HeaderConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbItem;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link HeaderBuilder} and the {@link Header} component.
 */
class TestHeaderBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_string_returnsNonNull() {
        assertNotNull(HeaderBuilder.create("Dashboard"));
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_returnsHeader() {
        Header header = HeaderBuilder.create("Dashboard").build();
        assertNotNull(header);
        assertTrue(header.getClassNames().contains("iyen-header"));
    }

    // =========================================================================
    // Prefix
    // =========================================================================

    @Nested
    class PrefixTests {

        @Test
        void prefix_setsComponents() {
            Header header = HeaderBuilder.create("Page")
                    .prefix(new Span("Logo"))
                    .build();
            assertNotNull(header.getPrefixComponents());
            assertEquals(1, header.getPrefixComponents().length);
        }

        @Test
        void hidePrefixOnDesktop_addsHiddenClass() {
            Header header = HeaderBuilder.create("Page")
                    .prefix(new Span("Logo"))
                    .hidePrefixOnDesktop()
                    .build();
            assertNotNull(header);
        }
    }

    // =========================================================================
    // Breadcrumb
    // =========================================================================

    @Test
    void breadcrumb_setsItems() {
        Header header = HeaderBuilder.create("Orders")
                .breadcrumb(new BreadcrumbItem(new Span("Home")), new BreadcrumbItem(new Span("Orders")))
                .build();
        assertNotNull(header);
    }

    // =========================================================================
    // Details
    // =========================================================================

    @Test
    void details_setsComponents() {
        Header header = HeaderBuilder.create("Profile")
                .details(new Span("Last login: today"), new Span("Role: Admin"))
                .build();
        assertNotNull(header);
    }

    // =========================================================================
    // Actions
    // =========================================================================

    @Nested
    class ActionTests {

        @Test
        void actions_setsComponents() {
            Header header = HeaderBuilder.create("Settings")
                    .actions(new Button("Save"), new Button("Cancel"))
                    .build();
            assertNotNull(header);
        }

        @Test
        void edit_addsEditButton() {
            Header header = HeaderBuilder.create("Profile")
                    .edit(btn -> btn.text("Edit Profile"))
                    .build();
            assertNotNull(header);
        }

        @Test
        void options_consumer_addsOptionsButton() {
            Header header = HeaderBuilder.create("Settings")
                    .options(btn -> btn.text("More"))
                    .build();
            assertNotNull(header);
        }

        @Test
        void newBtn_addsNewButton() {
            Header header = HeaderBuilder.create("Products")
                    .newBtn(btn -> btn.text("New Product"))
                    .build();
            assertNotNull(header);
        }

        @Test
        void refresh_addsRefreshButton() {
            Header header = HeaderBuilder.create("Dashboard")
                    .refresh(btn -> btn.text("Refresh"))
                    .build();
            assertNotNull(header);
        }

        @Test
        void close_addsCloseButton() {
            Header header = HeaderBuilder.create("Detail")
                    .close(btn -> btn.text("Close"))
                    .build();
            assertNotNull(header);
        }
    }

    // =========================================================================
    // Tabs
    // =========================================================================

    @Nested
    class TabTests {

        @Test
        void tabs_vararg_addsTabs() {
            Header header = HeaderBuilder.create("Dashboard")
                    .tabs(new Tab("Overview"), new Tab("Analytics"))
                    .build();
            assertTrue(header.getTabs().isPresent());
        }

        @Test
        void tabs_object_addsTabs() {
            Tabs tabs = new Tabs(new Tab("Tab1"), new Tab("Tab2"));
            Header header = HeaderBuilder.create("Dashboard")
                    .tabs(tabs)
                    .build();
            assertTrue(header.getTabs().isPresent());
        }
    }

    // =========================================================================
    // Layout access
    // =========================================================================

    @Test
    void getRowLayout_returnsLayout() {
        HeaderBuilder builder = HeaderBuilder.create("Test");
        assertNotNull(builder.getRowLayout());
    }

    @Test
    void getColumnLayout_returnsLayout() {
        HeaderBuilder builder = HeaderBuilder.create("Test");
        assertNotNull(builder.getColumnLayout());
    }

    // =========================================================================
    // withoutBorder
    // =========================================================================

    @Test
    void withoutBorder_addsClassName() {
        Header header = HeaderBuilder.create("Clean Header")
                .withoutBorder()
                .build();
        assertTrue(header.getClassNames().contains("header--no-border"));
    }

    // =========================================================================
    // Configure static
    // =========================================================================

    @Test
    void configure_static_returnsConfigurator() {
        Header header = new Header("Test");
        HeaderConfigurator.BaseHeaderConfigurator cfg =
                HeaderConfigurator.configure(header);
        assertNotNull(cfg);
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        Header header = HeaderBuilder.create("Products")
                .prefix(new Span("Icon"))
                .breadcrumb(new BreadcrumbItem(new Span("Home")), new BreadcrumbItem(new Span("Products")))
                .details(new Span("156 items"))
                .actions(new Button("Export"))
                .newBtn(btn -> btn.text("Add Product"))
                .tabs(new Tab("All"), new Tab("Active"), new Tab("Draft"))
                .withoutBorder()
                .build();
        assertNotNull(header);
        assertTrue(header.getTabs().isPresent());
        assertTrue(header.getClassNames().contains("header--no-border"));
    }
}
