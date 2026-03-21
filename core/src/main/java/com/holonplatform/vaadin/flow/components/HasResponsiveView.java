package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.enums.ScreenSize;
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
        final ScreenSize[] screenSizes = new ScreenSize[1];

        ui.getPage().addBrowserWindowResizeListener(browserWindowResizeEvent -> {

            if (UIUtils.getScreenSize(browserWindowResizeEvent.getWidth()) == ScreenSize.MOBILE) {
                mobileView(parentLayout);
                screenSizes[0] = ScreenSize.MOBILE;
                log.info("This is mobile from Browser resize");
            } else if (UIUtils.getScreenSize(browserWindowResizeEvent.getWidth()) == ScreenSize.DESKTOP) {
                desktopView(parentLayout);
                screenSizes[0] = ScreenSize.DESKTOP;
                log.info("This is desktop from Browser resize");
            }

        });


        ui.getPage().retrieveExtendedClientDetails(extendedClientDetails -> {
            if ((UIUtils.getScreenSize(extendedClientDetails.getWindowInnerWidth()) == ScreenSize.MOBILE && width != 0)
                    || extendedClientDetails.isIOS()) {

                mobileView(parentLayout);
                screenSizes[0] = ScreenSize.MOBILE;
                log.info("This is mobile retrieveExtendedClientDetails");
            } else if (UIUtils.getScreenSize(extendedClientDetails.getWindowInnerWidth()) == ScreenSize.DESKTOP || width == 0) {
                desktopView(parentLayout);
                screenSizes[0] = ScreenSize.DESKTOP;
                log.info("This is desktop retrieveExtendedClientDetails {}", extendedClientDetails.getWindowInnerWidth());
            }
        });

        if (screenSizes[0] == null) {
           /* if (width != 0 && UIUtils.getScreenSize(width) == ScreenSize.MOBILE) {
                mobileView(parentLayout);
                log.info("This is mobile");
            } else if (width == 0 || UIUtils.getScreenSize(width) == ScreenSize.DESKTOP) {
                desktopView(parentLayout);
                log.info("This is desktop");
            }*/
        } else {
            log.info("This is {}}", screenSizes[0]);
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