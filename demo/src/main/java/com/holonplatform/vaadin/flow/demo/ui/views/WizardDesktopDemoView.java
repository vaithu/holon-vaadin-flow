package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Wizard — Desktop")
@Route(value = "wizard-desktop", layout = DemoMainLayout.class)
public class WizardDesktopDemoView extends Div {

    public WizardDesktopDemoView() {
        addClassName("app-view");
        add(WizardMockupFactory.create());
    }
}
