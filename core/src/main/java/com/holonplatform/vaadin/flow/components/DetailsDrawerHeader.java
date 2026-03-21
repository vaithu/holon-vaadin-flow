package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.css.BoxShadowBorders;
import com.holonplatform.vaadin.flow.components.css.Right;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class DetailsDrawerHeader extends FlexBoxLayout {

    private final FlexBoxLayout wrapper;
    private Button close;
    private H4 title;
    private final HorizontalLayout rightSide;

    public DetailsDrawerHeader(String title) {
        addClassName(BoxShadowBorders.BOTTOM);
        setFlexDirection(FlexDirection.COLUMN);
        setWidthFull();

        this.close = UIUtils.createTertiaryInlineButton(VaadinIcon.CLOSE);
        this.close.addClassNames(LumoUtility.Margin.Left.AUTO);
        UIUtils.setLineHeight("1", this.close);

        this.title = new H4(title);
        this.title.addClassName(LumoUtility.FontWeight.BOLD);

        rightSide = new HorizontalLayout();
        rightSide.setSpacing(true);
        rightSide.setAlignItems(Alignment.BASELINE);
        rightSide.getStyle().set("margin-left", "auto");

        wrapper = new FlexBoxLayout(this.title,rightSide);
        wrapper.setAlignItems(Alignment.CENTER);
//        wrapper.setPadding(Horizontal.RESPONSIVE_L, Vertical.M);
        wrapper.addClassNames(LumoUtility.Padding.Vertical.MEDIUM,
                LumoUtility.Padding.Horizontal.LARGE);
        wrapper.setSpacing(Right.L);
        add(wrapper);
    }

    public void addHeaderComponents(Component... components) {
        rightSide.add(components);
    }

    public void addCloseBtn() {
        rightSide.add(this.close);
    }

    public DetailsDrawerHeader(String title, Component... components) {
        this(title);
        rightSide.add(components);
    }

    public DetailsDrawerHeader(String title, Tabs tabs) {
        this(title);
        add(tabs);
    }

    public DetailsDrawerHeader(String title, TabSheet tabSheet) {
        this(title);
        add(tabSheet);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void addCloseListener(ComponentEventListener<ClickEvent<Button>> listener) {
        this.close.addClickListener(listener);
    }

}
