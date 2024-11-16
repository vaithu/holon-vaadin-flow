package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.DividerBuilder;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class DefaultDividerBuilder
        extends DefaultLabelBuilder<Span>
        implements DividerBuilder {

    private int size;


    public DefaultDividerBuilder(Span component) {
        super(component);
        getComponent().addClassNames(
                LumoUtility.Display.FLEX);
    }



    @Override
    public DividerBuilder verticalSeparator() {
        getComponent().setWidth(size == 0 ? "2px" : UIUtils.formatSize(size));
        return this;
    }

    @Override
    public DividerBuilder horizontalSeparator() {
        getComponent().setHeight(size == 0 ? "2px" : UIUtils.formatSize(size));
        return this;
    }

    @Override
    public DividerBuilder iconSeparator() {

        getComponent().getStyle().set("height", "40px");
        getComponent().addClassName(LumoUtility.Padding.Vertical.MEDIUM);
        return this;
    }

    @Override
    public DividerBuilder size(int size) {
        this.size = size;
        return this;
    }
}
