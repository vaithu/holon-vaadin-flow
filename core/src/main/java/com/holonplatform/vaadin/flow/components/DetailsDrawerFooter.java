package com.holonplatform.vaadin.flow.components;

import com.holonplatform.core.Registration;
import com.holonplatform.vaadin.flow.components.css.Right;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class DetailsDrawerFooter extends FlexBoxLayout {

    private Button save;
    private Button cancel;

    public DetailsDrawerFooter() {
//        setBackgroundColor(LumoStyles.Color.Contrast._5);
//        setPadding(Horizontal.RESPONSIVE_L, Vertical.S);
        setSpacing(Right.S);
        addClassNames(LumoUtility.Background.CONTRAST_5,LumoUtility.Padding.Vertical.SMALL
        ,LumoUtility.Padding.Horizontal.LARGE);
        setWidthFull();

        save = UIUtils.createPrimaryButton("Save");
        cancel = UIUtils.createTertiaryButton("Cancel");
        add(save, cancel);
    }

    public Registration addSaveListener(
            ComponentEventListener<ClickEvent<Button>> listener) {
        return (Registration) save.addClickListener(listener);
    }

    public Registration addCancelListener(
            ComponentEventListener<ClickEvent<Button>> listener) {
        return (Registration) cancel.addClickListener(listener);
    }

}
