package com.iyensoft.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.PopoverBuilder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestPopoverBuilder {

    @Test
    void createWithContentAddsInitialComponents() {
        Span content = new Span("Details");

        Popover popover = PopoverBuilder.create(content).build();

        assertEquals(1, popover.getElement().getChildCount());
        assertEquals("Details", popover.getElement().getChild(0).getText());
    }

    @Test
    void configurationMapsToPopoverApi() {
        Button target = new Button("Open");

        Popover popover = PopoverBuilder.create()
                .target(target)
                .position(PopoverPosition.BOTTOM_END)
                .modal(true, true)
                .autofocus(true)
                .closeOnEsc(false)
                .closeOnOutsideClick(false)
                .openOnClick(false)
                .width("20rem")
                .height("10rem")
                .ariaLabel("Details")
                .arrow()
                .build();

        assertEquals(target, popover.getTarget());
        assertEquals(PopoverPosition.BOTTOM_END, popover.getPosition());
        assertTrue(popover.isModal());
        assertTrue(popover.isBackdropVisible());
        assertTrue(popover.isAutofocus());
        assertTrue(popover.getElement().getThemeList().contains(PopoverVariant.ARROW.getVariantName()));
        assertEquals("Details", popover.getElement().getAttribute("aria-label"));
    }

    @Test
    void componentsFactoryCreatesPopoverBuilder() {
        Popover popover = Components.popover(new Span("Factory")).build();

        assertEquals("Factory", popover.getElement().getChild(0).getText());
    }

    @Test
    void buildPostProcessorRunsBeforeReturn() {
        Popover popover = PopoverBuilder.create()
                .withBuildPostProcessor(component -> component.getElement().setAttribute("data-test", "popover"))
                .build();

        assertEquals("popover", popover.getElement().getAttribute("data-test"));
    }
}