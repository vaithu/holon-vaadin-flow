package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.support.BreakPoint;
import com.vaadin.flow.component.formlayout.FormLayout;

import java.util.List;

public interface FormResponsiveStepConfigurator<C extends FormResponsiveStepConfigurator<C>> {

    C breakpoint(BreakPoint breakPoint, int columns,FormLayout.ResponsiveStep.LabelsPosition labelsPosition);
    C breakpoint(String minWidth, int columns,FormLayout.ResponsiveStep.LabelsPosition labelsPosition);


    default C small(int columns,FormLayout.ResponsiveStep.LabelsPosition labelsPosition) {
        return breakpoint(BreakPoint.BREAKPOINT_XS, columns,labelsPosition);
    }

    default C small(int columns) {
        return small(columns, FormLayout.ResponsiveStep.LabelsPosition.TOP);
    }

    default C medium(int columns) {
        return medium(columns, FormLayout.ResponsiveStep.LabelsPosition.TOP);
    }

    default C large(int columns) {
        return large(columns, FormLayout.ResponsiveStep.LabelsPosition.TOP);
    }

    default C xLarge(int columns) {
        return xLarge(columns, FormLayout.ResponsiveStep.LabelsPosition.TOP);
    }

    /*default C extraSmall(int columns) {
        return breakpoint(BreakPoint.BREAKPOINT_XS, columns);
    }*/

    default C medium(int columns,FormLayout.ResponsiveStep.LabelsPosition labelsPosition) {
        return breakpoint(BreakPoint.BREAKPOINT_MD, columns,labelsPosition);
    }

    default C large(int columns,FormLayout.ResponsiveStep.LabelsPosition labelsPosition) {
        return breakpoint(BreakPoint.BREAKPOINT_LG, columns,labelsPosition);
    }

    default C xLarge(int columns,FormLayout.ResponsiveStep.LabelsPosition labelsPosition) {
        return breakpoint(BreakPoint.BREAKPOINT_XL, columns,labelsPosition);
    }



    List<FormLayout.ResponsiveStep> build();
}
