package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("Popover – Holon Demo")
@Route(value = "popover", layout = DemoMainLayout.class)
public class PopoverDemoView extends Div {

    public PopoverDemoView() {
        addClassName("app-view");

        var title = new H1("Popover");

        var desc = new Paragraph(
                "Popover is a Vaadin component for creating overlays positioned next to a target component. "
                + "It supports click, hover, and focus triggers, modal mode, arrow variants, and configurable positioning.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(positionExample());
        examples.add(hoverExample());
        examples.add(arrowExample());
        examples.add(modalExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var target = ButtonBuilder.create().text("Click me").primary().build();

        var popover = new Popover();
        popover.setTarget(target);
        popover.add(new Span("Hello from the popover!"));

        var wrapper = new Div(target, popover);
        return new DemoExample("Basic Popover", wrapper, """
                var target = ButtonBuilder.create().text("Click me").primary().build();

                var popover = new Popover();
                popover.setTarget(target);
                popover.add(new Span("Hello from the popover!"));""");
    }

    private DemoExample positionExample() {
        var topBtn = ButtonBuilder.create().text("Top").secondary().build();
        var topPopover = new Popover();
        topPopover.setTarget(topBtn);
        topPopover.setPosition(PopoverPosition.TOP);
        topPopover.add(new Span("Positioned on top"));

        var bottomBtn = ButtonBuilder.create().text("Bottom").secondary().build();
        var bottomPopover = new Popover();
        bottomPopover.setTarget(bottomBtn);
        bottomPopover.setPosition(PopoverPosition.BOTTOM);
        bottomPopover.add(new Span("Positioned on bottom"));

        var startBtn = ButtonBuilder.create().text("Start").secondary().build();
        var startPopover = new Popover();
        startPopover.setTarget(startBtn);
        startPopover.setPosition(PopoverPosition.START);
        startPopover.add(new Span("Positioned at start"));

        var endBtn = ButtonBuilder.create().text("End").secondary().build();
        var endPopover = new Popover();
        endPopover.setTarget(endBtn);
        endPopover.setPosition(PopoverPosition.END);
        endPopover.add(new Span("Positioned at end"));

        var wrapper = new Div(topBtn, topPopover, bottomBtn, bottomPopover,
                startBtn, startPopover, endBtn, endPopover);

        return new DemoExample("Positions", wrapper, """
                var topBtn = ButtonBuilder.create().text("Top").secondary().build();
                var topPopover = new Popover();
                topPopover.setTarget(topBtn);
                topPopover.setPosition(PopoverPosition.TOP);
                topPopover.add(new Span("Positioned on top"));

                var bottomBtn = ButtonBuilder.create().text("Bottom").secondary().build();
                var bottomPopover = new Popover();
                bottomPopover.setTarget(bottomBtn);
                bottomPopover.setPosition(PopoverPosition.BOTTOM);
                bottomPopover.add(new Span("Positioned on bottom"));""");
    }

    private DemoExample hoverExample() {
        var target = ButtonBuilder.create().text("Hover me").secondary().build();

        var popover = new Popover();
        popover.setTarget(target);
        popover.setOpenOnHover(true);
        popover.setOpenOnClick(false);
        popover.add(new Span("Appears on hover"));

        var wrapper = new Div(target, popover);
        return new DemoExample("Hover Trigger", wrapper, """
                var target = ButtonBuilder.create().text("Hover me").secondary().build();

                var popover = new Popover();
                popover.setTarget(target);
                popover.setOpenOnHover(true);
                popover.setOpenOnClick(false);
                popover.add(new Span("Appears on hover"));""");
    }

    private DemoExample arrowExample() {
        var target = ButtonBuilder.create().text("With Arrow").secondary().build();

        var popover = new Popover();
        popover.setTarget(target);
        popover.addThemeVariants(PopoverVariant.ARROW);
        popover.setPosition(PopoverPosition.BOTTOM);
        popover.add(new Span("Arrow variant popover"));

        var wrapper = new Div(target, popover);
        return new DemoExample("Arrow Variant", wrapper, """
                var target = ButtonBuilder.create().text("With Arrow").secondary().build();

                var popover = new Popover();
                popover.setTarget(target);
                popover.addThemeVariants(PopoverVariant.ARROW);
                popover.setPosition(PopoverPosition.BOTTOM);
                popover.add(new Span("Arrow variant popover"));""");
    }

    private DemoExample modalExample() {
        var target = ButtonBuilder.create().text("Open Modal Popover").error().build();

        var nameField = new TextField("Name");
        var emailField = new TextField("Email");
        var closeBtn = ButtonBuilder.create().text("Close").secondary().build();

        var popover = new Popover();
        popover.setTarget(target);
        popover.setModal(true);
        popover.setCloseOnOutsideClick(false);
        popover.setWidth("300px");
        popover.add(nameField, emailField, closeBtn);

        closeBtn.addClickListener(e -> popover.close());

        var wrapper = new Div(target, popover);
        return new DemoExample("Modal Popover", wrapper, """
                var target = ButtonBuilder.create().text("Open Modal").error().build();
                var nameField = new TextField("Name");
                var closeBtn = ButtonBuilder.create().text("Close").secondary().build();

                var popover = new Popover();
                popover.setTarget(target);
                popover.setModal(true);
                popover.setCloseOnOutsideClick(false);
                popover.setWidth("300px");
                popover.add(nameField, closeBtn);
                closeBtn.addClickListener(e -> popover.close());""");
    }
}
