package com.iyensoft.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.Components;
import com.iyensoft.vaadin.flow.components.Panel;
import com.iyensoft.vaadin.flow.components.PanelVariant;
import com.iyensoft.vaadin.flow.components.builders.DetailPanelBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestDetailPanelBuilder {

    @Test
    void create_buildsCompactDetailPanel() {
        Span details = new Span("Supporting details");
        Button action = new Button("Edit");
        Span content = new Span("Body");

        Panel panel = Components.detailPanel("Account")
                .details(details)
                .actions(action)
                .content(content)
                .background(PanelVariant.Background.SURFACE_2)
                .build();

        List<Component> children = panel.getChildren().toList();
        Component header = children.get(0);
        List<Component> headerChildren = header.getChildren().toList();
        Component title = headerChildren.get(0);

        assertTrue(header.getElement().getClassList().contains("panel-header"));
        assertEquals(List.of(title, action), headerChildren);
        assertEquals("h4", title.getElement().getTag());
        assertTrue(title.getElement().getClassList().contains("panel-title"));
        assertEquals("Account", title.getElement().getChild(0).getTextRecursively());
        assertSame(details, title.getChildren().filter(Span.class::isInstance).findFirst().orElseThrow());
        assertEquals(PanelVariant.Background.SURFACE_2, panel.getBackground());
        assertSame(content, children.get(1).getChildren().findFirst().orElseThrow());
    }

    @Test
    void detailsAndActions_replacePreviousComponents() {
        Button first = new Button("First");
        Button second = new Button("Second");

        Panel panel = Components.detailPanel("Account")
                .details(new Span("old"))
                .details(new Span("new"))
                .actions(first)
                .actions(second)
                .build();

        Component header = panel.getChildren().findFirst().orElseThrow();
        Component title = header.getChildren().findFirst().orElseThrow();
        assertEquals(2, header.getChildren().count());
        assertSame(second, header.getChildren().toList().get(1));
        assertEquals("Accountnew", title.getElement().getTextRecursively());
    }

    @Test
    void create_rejectsNullTitle() {
        assertThrows(NullPointerException.class, () -> DetailPanelBuilder.create(null));
    }
}
