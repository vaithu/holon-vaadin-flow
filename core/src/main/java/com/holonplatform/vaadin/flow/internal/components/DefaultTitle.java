package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.vaadin.flow.components.HasTitle;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.internal.lumo.Text;
import com.vaadin.flow.component.html.H4;

public class DefaultTitle implements HasTitle {

    private final LabelBuilder<?> title;

    public DefaultTitle(LabelBuilder<?> title) {
        this.title = title;
        this.title.styleNames(Text.PRIMARY.getClassName(), "padding-small");
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
