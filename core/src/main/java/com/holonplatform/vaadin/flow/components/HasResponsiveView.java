package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.iyensoft.vaadin.flow.utils.responsive.ViewMode;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface HasResponsiveView {

    Logger log = LoggerFactory.getLogger(HasResponsiveView.class);

    /*default void enableResponsiveView(Div parentLayout) {
        log.info("Reaached enableResponsiveView");
        parentLayout.removeAll();
        ResizeObserver.get().observe(parentLayout, observation -> {
            BreakPoint breakPoint = UIUtils.getBreakPoint(observation.width());
            switch (breakPoint) {
                case BREAKPOINT_XS:
                case BREAKPOINT_SM:
                    mobileView(parentLayout);
                    break;
                case BREAKPOINT_MD:
                    tabletView(parentLayout);
                    break;
                case BREAKPOINT_LG:
                case BREAKPOINT_XL:
                case BREAKPOINT_XXL:
                default:
                    desktopView(parentLayout);
                    break;
            }
        });
    }*/

    default void enableResponsiveView(Div parentLayout, UI ui, int width) {
        final ViewMode[] viewModes = new ViewMode[1];

        ui.getPage().addBrowserWindowResizeListener(browserWindowResizeEvent -> {

            if (UIUtils.getViewMode(browserWindowResizeEvent.getWidth()) == ViewMode.MOBILE) {
                mobileView(parentLayout);
                viewModes[0] = ViewMode.MOBILE;
                log.info("This is mobile from Browser resize");
            } else if (UIUtils.getViewMode(browserWindowResizeEvent.getWidth()) == ViewMode.DESKTOP) {
                desktopView(parentLayout);
                viewModes[0] = ViewMode.DESKTOP;
                log.info("This is desktop from Browser resize");
            }

        });


        ui.getPage().retrieveExtendedClientDetails(extendedClientDetails -> {
            if ((UIUtils.getViewMode(extendedClientDetails.getWindowInnerWidth()) == ViewMode.MOBILE && width != 0)
                    || extendedClientDetails.isIOS()) {

                mobileView(parentLayout);
                viewModes[0] = ViewMode.MOBILE;
                log.info("This is mobile retrieveExtendedClientDetails");
            } else if (UIUtils.getViewMode(extendedClientDetails.getWindowInnerWidth()) == ViewMode.DESKTOP || width == 0) {
                desktopView(parentLayout);
                viewModes[0] = ViewMode.DESKTOP;
                log.info("This is desktop retrieveExtendedClientDetails {}", extendedClientDetails.getWindowInnerWidth());
            }
        });

        if (viewModes[0] == null) {
           /* if (width != 0 && UIUtils.getScreenSize(width) == ScreenSize.MOBILE) {
                mobileView(parentLayout);
                log.info("This is mobile");
            } else if (width == 0 || UIUtils.getScreenSize(width) == ScreenSize.DESKTOP) {
                desktopView(parentLayout);
                log.info("This is desktop");
            }*/
        } else {
            log.info("This is {}}", viewModes[0]);
        }

    }

    void mobileView(Div parentLayout);

    void desktopView(Div parentLayout);

    /*default void enableResponsiveView(Div parentLayout, int mobileThreshold) {
        ResizeObserver.get().observe(parentLayout, observation -> {
            if (observation.width() < mobileThreshold) {
                mobileView(parentLayout);
            } else {
                desktopView(parentLayout);
            }
        });
    }

    default void enableResponsiveView(Div parentLayout, int mobileThreshold, int tabletThreshold) {
        ResizeObserver.get().observe(parentLayout, observation -> {
            if (observation.width() < mobileThreshold) {
                mobileView(parentLayout);
            } else if (observation.width() < tabletThreshold) {
                tabletView(parentLayout);
            } else {
                desktopView(parentLayout);
            }
        });
    }*/

    default void tabletView(Div parentLayout) {
        // Default implementation for tablet view, can be overridden
    }
}