package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.HasEditButton;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

public class DefaultEditButton extends Button implements HasEditButton {

    public DefaultEditButton() {
        this.editBtn = Components.button()
                .tertiaryInline()
                .icon(VaadinIcon.EDIT)
                .visible(false)
                .build();
    }

    public DefaultEditButton(Button editBtn) {
        this.editBtn = editBtn;
    }

    private final Button editBtn;

    @Override
    public DefaultEditButton editButton(Runnable runnable) {
        editBtn.addClickListener(buttonClickEvent -> runnable.run());
        return this;
    }

    @Override
    public DefaultEditButton visible(boolean visible) {
        editBtn.setVisible(visible);
        return this;
    }
}
