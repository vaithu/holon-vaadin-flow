package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.CardGridBuilder;
import com.holonplatform.vaadin.flow.components.builders.CardGridConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultCardGridBuilder;
import com.vaadin.flow.component.html.Div;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestCardBuilder {

    @Test
    public void testCard() {
        CardGridBuilder gridBuilder = new DefaultCardGridBuilder(new Div());
        assertNotNull(gridBuilder);
        Div parent = gridBuilder.build();
        assertNotNull(parent);

        assertEquals(3, parent.getClassNames().stream().count());
        gridBuilder.gap(true).large(2);
        assertEquals(5, parent.getClassNames().stream().count());

        CardGridConfigurator.CardBuilder<CardGridBuilder> cardBuilder = gridBuilder.withCard(new Div());
        Div card = cardBuilder.build();
        assertNotNull(card);

        cardBuilder.rowSpan(1);
        assertEquals(8, card.getClassNames().stream().count());
        assertEquals(1, parent.getChildren().count());
        assertEquals(1, card.getChildren().count());
        card.getChildren().findFirst().ifPresent(component -> {
            System.out.println(component.getClassNames());
            assertEquals(2,component.getClassNames().stream().count());
        });



    }
}
