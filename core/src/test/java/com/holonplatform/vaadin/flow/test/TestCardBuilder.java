package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.CardBuilder;
import com.holonplatform.vaadin.flow.components.builders.CardGridBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultCardGridBuilder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestCardBuilder {

    @Test
    public void testBuilders() {
        CardGridBuilder builder = new DefaultCardGridBuilder();
        assertNotNull(builder);

        CardGridBuilder add = builder.add("dfsdfk", new Span("dsfsdf"));
        assertNotNull(add);

        Div div = Components.cardGrid()
                .small(2)
                .large(4)
                .add("Test1",new Span("Text"))
                .add("Test2",new Span("Text"))
                .add("Test3",new Span("Text"))
                .add("Test4",new Span("Text"))
                .addCard(
                        Components.card()
                                .title("Hello")
                                .content(new Span("Tekdjsdkf"))

                )
                .build();

        div.getChildren()
                        .forEach(component -> {
                            if (component instanceof CardBuilder) {
                                component.getChildren().forEach(card -> {
                                    if (card instanceof H4) {
                                        assertNotNull(((H4) card).getText());
                                    }
                                });
                            }
                        });

        assertNotNull(div);
    }

    @Test
    public void testStyles() {
        CardGridBuilder div = CardGridBuilder.create().styleName("test");
        assertNotNull(div);


    }

    @Test
    public void testTitle() {
        CardBuilder cardBuilder = CardBuilder.create("Test");
        cardBuilder.getTitle().ifPresentOrElse(s -> {
            assertEquals("Test",s);
        },() -> {
            throw new IllegalArgumentException("Title doesn't match");});
        assertNotNull(cardBuilder.getTitle().get());

        cardBuilder = cardBuilder.title("Test");
        cardBuilder.getTitle().ifPresentOrElse(s -> {
            assertEquals("Test",s);
        },() -> {
            throw new IllegalArgumentException("Title doesn't match");});
        assertNotNull(cardBuilder.getTitle().get());
    }
}
