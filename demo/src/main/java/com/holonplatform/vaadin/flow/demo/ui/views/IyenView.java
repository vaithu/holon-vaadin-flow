package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Main;

public class IyenView extends Main {

    public IyenView() {

        ResponsiveDiv responsiveDiv = ResponsiveDiv.flex().column().gapM()
                .slotOnce(ViewMode.DESKTOP, this::createDesktopGrid)
                .slotOnce(ViewMode.MOBILE, this::createMobileGrid)
                .build();

        add(responsiveDiv);


    }

    private Component createMobileGrid() {
        return null;
    }

    private Component createDesktopGrid() {
        return null;
    }
}
