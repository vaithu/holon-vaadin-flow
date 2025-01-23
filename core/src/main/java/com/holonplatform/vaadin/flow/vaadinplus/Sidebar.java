package com.holonplatform.vaadin.flow.vaadinplus;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import org.jetbrains.annotations.NotNull;

public class Sidebar extends Section implements HasEnabled, HasTheme {

    private Header header;

    private Layout content;

    public Sidebar(String title, Component... components) {
        this(title, null, components);
    }

    public Sidebar(String title, String description, Component... components) {

        initialize();
        createHeader(title, description);
        createContent(components);

        close();
    }

    public Sidebar(com.holonplatform.vaadin.flow.vaadinplus.components.Header header, Component... components) {
        initialize();
        header.setPrefix(createLeftArrowBtn());
        header.addActions(createCloseBtn());
        add(header);
        createContent(components);
        close();
    }

    private void initialize() {
        addClassNames(Background.BASE, BoxShadow.MEDIUM, Display.FLEX, FlexDirection.COLUMN, Overflow.HIDDEN,
                Position.FIXED, "bottom-0", "top-0", "transition-all", "z-10");
        setMaxWidth(100, Unit.PERCENTAGE);
        setWidth(480, Unit.PIXELS);
    }


    private void createHeader(String title, String description) {
        H2 title1 = new H2(title);
        title1.addClassNames(FontSize.XLARGE);

        final Button leftArrow = createLeftArrowBtn();

        Layout titleLayout = new Layout(leftArrow, title1);
        titleLayout.setDisplay(com.holonplatform.vaadin.flow.internal.lumo.Display.FLEX);
        titleLayout.setAlignItems(com.holonplatform.vaadin.flow.internal.lumo.AlignItems.CENTER);
        titleLayout.setFlexDirection(com.holonplatform.vaadin.flow.internal.lumo.FlexDirection.ROW);
        titleLayout.setGap(com.holonplatform.vaadin.flow.internal.lumo.Gap.SMALL);

        if (description != null) {
            Span description1 = new Span(description);
            description1.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
            titleLayout.add(description1);
        }

        final Button close = createCloseBtn();

        this.header = new Header(titleLayout, close);
        styleHeader(header);

        if (description == null) {
            this.header.addClassNames(AlignItems.CENTER);
        }

        add(this.header);
    }

    private void styleHeader(Header header) {
        header.addClassNames(Border.BOTTOM, Display.FLEX, JustifyContent.BETWEEN,
                Padding.End.MEDIUM, Padding.Start.LARGE, Padding.Vertical.MEDIUM);
    }

    private @NotNull Button createCloseBtn() {
        Button close = new Button(VaadinIcon.CLOSE.create(), e -> close());
        close.addClassNames(Margin.Vertical.NONE, Display.HIDDEN, Display.Breakpoint.Small.INLINE_BLOCK);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.setAriaLabel("Close sidebar");
        close.setTooltipText("Close sidebar");
        return close;
    }

    private @NotNull Button createLeftArrowBtn() {
        Button leftArrow = new Button(VaadinIcon.ARROW_LEFT.create(), e -> close());
        leftArrow.addClassNames(Margin.Vertical.NONE, Display.INLINE_BLOCK, Display.Breakpoint.Small.HIDDEN);
        leftArrow.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        leftArrow.setAriaLabel("Go back");
        leftArrow.setTooltipText("Go back");
        return leftArrow;
    }

    private void createContent(Component... components) {
        if (components != null) {
            this.content = new Layout(components);
            this.content.addClassNames(Flex.GROW, Padding.Bottom.MEDIUM, Padding.Horizontal.LARGE, Padding.Top.SMALL);
            this.content.setFlexDirection(com.holonplatform.vaadin.flow.internal.lumo.FlexDirection.COLUMN);
            add(this.content);
        }
    }

    public void createFooter(Component component) {
        add(component);
    }

    // TODO: Refocus the component that opened the sidebar after closing
    public void close() {
        addClassNames("-end-full");
        removeClassName("end-0");
        setEnabled(false);
    }

    public void open() {
        addClassNames("end-0");
        removeClassNames("-end-full");
        setEnabled(true);
    }

    public void addHeaderThemeName(String theme) {
        this.header.getElement().getThemeList().add(theme);
    }

    public void removeHeaderThemeName(String theme) {
        this.header.getElement().getThemeList().remove(theme);
    }

    public Layout getContent() {
        return this.content;
    }

}