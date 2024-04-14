package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.support.BreakPoint;
import com.vaadin.flow.component.formlayout.FormLayout;

import java.util.List;

public interface ResponsiveStepConfigurator<C> {

    C breakpoint(BreakPoint breakPoint, int columns);
    C breakpoint(String size, int columns);


    C label(FormLayout.ResponsiveStep.LabelsPosition labelsPosition);

    default C small(int columns) {
        return breakpoint(BreakPoint.BREAKPOINT_SM, columns);
    }

    default C extraSmall(int columns) {
        return breakpoint(BreakPoint.BREAKPOINT_XS, columns);
    }

    default C medium(int columns) {
        return breakpoint(BreakPoint.BREAKPOINT_MD, columns);
    }

    default C large(int columns) {
        return breakpoint(BreakPoint.BREAKPOINT_LG, columns);
    }

    default C xLarge(int columns) {
        return breakpoint(BreakPoint.BREAKPOINT_XL, columns);
    }

    List<FormLayout.ResponsiveStep> build();
}
