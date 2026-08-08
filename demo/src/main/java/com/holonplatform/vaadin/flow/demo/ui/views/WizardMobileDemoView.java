package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Wizard — Mobile")
@Route(value = "wizard-mobile", layout = DemoMainLayout.class)
public class WizardMobileDemoView extends Div {

    public WizardMobileDemoView() {
        addClassName("app-view");
        add(WizardMockupFactory.create());
    }
}
