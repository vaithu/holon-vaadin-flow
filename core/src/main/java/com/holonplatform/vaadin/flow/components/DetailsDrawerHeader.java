package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.css.BoxShadowBorders;
import com.holonplatform.vaadin.flow.components.css.Right;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class DetailsDrawerHeader extends FlexBoxLayout {

    private Button close;
    private NativeLabel title;

    public DetailsDrawerHeader(String title) {
        addClassName(BoxShadowBorders.BOTTOM);
        setFlexDirection(FlexDirection.COLUMN);
        setWidthFull();

        this.close = UIUtils.createTertiaryInlineButton(VaadinIcon.CLOSE);
        UIUtils.setLineHeight("1", this.close);

        this.title = new NativeLabel(title);

        FlexBoxLayout wrapper = new FlexBoxLayout(this.close, this.title);
        wrapper.setAlignItems(Alignment.CENTER);
//        wrapper.setPadding(Horizontal.RESPONSIVE_L, Vertical.M);
        wrapper.addClassNames(LumoUtility.Padding.Vertical.MEDIUM,
                LumoUtility.Padding.Horizontal.LARGE);
        wrapper.setSpacing(Right.L);
        add(wrapper);
    }

    public DetailsDrawerHeader(String title, Tabs tabs) {
        this(title);
        add(tabs);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void addCloseListener(ComponentEventListener<ClickEvent<Button>> listener) {
        this.close.addClickListener(listener);
    }

}
