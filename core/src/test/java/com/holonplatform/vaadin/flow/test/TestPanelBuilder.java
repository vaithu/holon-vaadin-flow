package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.vaadinplus.components.Footer;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.iyensoft.vaadin.flow.components.Panel;
import com.iyensoft.vaadin.flow.components.builders.PanelBuilder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PanelBuilder}, {@link Components#panel(Panel)}, and the
 * minimal widget-style {@link Panel} API.
 */
class TestPanelBuilder {

    @Test
    void create_empty_returnsNonNull() {
        assertNotNull(PanelBuilder.create());
    }

    @Test
    void create_withPanel_returnsSameInstance() {
        Panel panel = new Panel();

        assertSame(panel, PanelBuilder.create(panel).build());
    }

    @Test
    void build_default_returnsPanel() {
        Panel panel = PanelBuilder.create().build();

        assertNotNull(panel);
        assertTrue(panel.getClassNames().contains("iyen-panel"));
        assertEquals("group", panel.getElement().getAttribute("role"));
    }

    @Test
    void styleName_addsClassName() {
        Panel panel = PanelBuilder.create()
                .styleName("my-panel")
                .build();

        assertTrue(panel.getClassNames().contains("my-panel"));
    }

    @Test
    void builder_header_content_footer_composesPanel() {
        Button primary = new Button("Primary");
        primary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button secondary = new Button("Secondary");

        Footer footer = new Footer();
        footer.setMeta(new Span("Footer"));
        footer.setLegal(new Span("Legal"));

        Panel panel = PanelBuilder.create()
                .styleName("card-panel")
                .header()
                    .heading("Header")
                    .details(new Span("Live details"))
                    .actions(primary, secondary)
                    .add()
                .content(new Div(new Span("Body")))
                .footer(footer)
                .build();

        List<com.vaadin.flow.component.Component> children = panel.getChildren().toList();
        assertEquals(3, children.size());
        assertTrue(panel.getClassNames().contains("card-panel"));

    Header header = (Header) children.get(0);
        assertEquals(2, header.getActionsComponents().length);
        assertSame(primary, header.getActionsComponents()[0]);
        assertSame(secondary, header.getActionsComponents()[1]);
        assertEquals(1, footer.getMetaComponents().length);
        assertEquals(1, footer.getLegalComponents().length);
    }

    @Test
    void components_factory_configures_panel() {
        Panel panel = new Panel();

        Components.panel(panel)
                .styleName("factory-panel")
                .header()
                    .heading("Factory header")
                    .add()
                .content(new Div(new Span("Body")))
                .footer()
                    .meta(new Span("Footer"))
                    .add();

        assertEquals(3, panel.getChildren().count());
        assertTrue(panel.getClassNames().contains("factory-panel"));
    }

    @Test
    void direct_setters_attachComponents() {
        Panel panel = new Panel();
        Header header = new Header("Direct header");
        Footer footer = new Footer();
        footer.setMeta(new Span("Direct footer"));

        panel.setHeader(header);
        panel.setContent(new Div(new Span("Body")));
        panel.setFooter(footer);

        List<com.vaadin.flow.component.Component> children = panel.getChildren().toList();
        assertEquals(3, children.size());
        assertSame(header, children.get(0));
        assertSame(footer, children.get(2));
    }
}
