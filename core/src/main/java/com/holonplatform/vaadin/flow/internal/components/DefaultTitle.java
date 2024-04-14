package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.vaadin.flow.components.HasTitle;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class DefaultTitle implements HasTitle {

    private LabelBuilder<?> title;

    public DefaultTitle() {

    }

    public DefaultTitle(LabelBuilder<?> title) {
        this.title = title;
        this.title.styleNames(LumoUtility.TextColor.PRIMARY, LumoUtility.Padding.SMALL);
    }

    public DefaultTitle(H4 component) {
        this(LabelBuilder.h4().text(component.getText()));
    }


    /**
     * Get the component title.
     *
     * @return The title text (may be null)
     */
    @Override
    public String getTitle() {
        return title.build().getText();
    }

    /**
     * Set the component title.
     *
     * @param title The title text to set
     */
    @Override
    public void setTitle(String title) {
        this.title.text(title);
    }
}
