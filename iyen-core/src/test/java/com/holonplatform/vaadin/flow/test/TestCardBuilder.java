package com.holonplatform.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.builders.CardBuilder;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CardBuilder}.
 */
class TestCardBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(CardBuilder.create());
    }

    @Test
    void create_existingCard() {
        Card card = new Card();
        CardBuilder builder = CardBuilder.create(card);
        assertNotNull(builder);
        assertSame(card, builder.build());
    }

    @Test
    void build_default_returnsCard() {
        Card card = CardBuilder.create().build();
        assertNotNull(card);
    }

    @Test
    void add_components() {
        Card card = CardBuilder.create()
                .add(new Span("Content"))
                .build();
        assertTrue(card.getComponentCount() > 0);
    }

    @Test
    void withFooter() {
        Card card = CardBuilder.create()
                .withFooter(new Span("Footer"))
                .build();
        assertNotNull(card);
    }

    @Test
    void header_setsHeader() {
        Card card = CardBuilder.create()
                .header(new Span("Header"))
                .build();
        assertNotNull(card.getHeader());
    }

    @Test
    void subtitle_setsSubtitle() {
        Card card = CardBuilder.create()
                .subtitle(new Span("Sub"))
                .build();
        assertNotNull(card.getSubtitle());
    }

    @Test
    void media_setsMedia() {
        Card card = CardBuilder.create()
                .media(new Span("Image"))
                .build();
        assertNotNull(card.getMedia());
    }

    @Test
    void styleName_addsClassName() {
        Card card = CardBuilder.create()
                .styleName("my-card")
                .build();
        assertTrue(card.getClassNames().contains("my-card"));
    }

    @Test
    void fluent_chain() {
        Card card = CardBuilder.create()
                .header(new Span("Title"))
                .subtitle(new Span("Subtitle"))
                .add(new Span("Body"))
                .withFooter(new Span("Footer"))
                .styleName("custom-card")
                .build();
        assertNotNull(card);
        assertTrue(card.getClassNames().contains("custom-card"));
    }
}
