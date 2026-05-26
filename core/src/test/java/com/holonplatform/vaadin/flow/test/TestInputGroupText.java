package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.vaadinplus.components.InputGroupText;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestInputGroupText {

    @Test
    void testDefaultClassName() {
        var text = new InputGroupText();
        assertTrue(text.getClassNames().contains("input-group__text"));
    }

    @Test
    void testWithText() {
        var text = new InputGroupText("@");
        assertTrue(text.getClassNames().contains("input-group__text"));
    }

    @Test
    void testWithNullText() {
        var text = new InputGroupText((String) null);
        assertTrue(text.getClassNames().contains("input-group__text"));
    }

    @Test
    void testWithComponents() {
        var text = new InputGroupText(new Span("$"), new Span("USD"));
        assertTrue(text.getClassNames().contains("input-group__text"));
    }
}
