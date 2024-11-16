package com.holonplatform.vaadin.flow.components;

import com.vaadin.flow.component.Component;

public interface HasResponsiveView {

    default void enableResponsiveView(Component layout) {
        ResizeObserver.get().observe(layout, observation -> {
            if (observation.width() < 450) {
                mobileView();
            } else {
                desktopView();
            }
        });
    }

    void mobileView();

    void desktopView();

}
