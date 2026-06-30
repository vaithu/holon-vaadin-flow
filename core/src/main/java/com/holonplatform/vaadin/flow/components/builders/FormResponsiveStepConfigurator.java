package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.support.BreakPoint;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.formlayout.FormLayout;

import java.util.List;
import java.util.Objects;

public interface FormResponsiveStepConfigurator<C extends FormResponsiveStepConfigurator<C>> {

    C breakpoint(BreakPoint breakPoint, int columns,FormLayout.ResponsiveStep.LabelsPosition labelsPosition);
    C breakpoint(String minWidth, int columns,FormLayout.ResponsiveStep.LabelsPosition labelsPosition);

    default C breakpoint(ViewMode viewMode, int columns, FormLayout.ResponsiveStep.LabelsPosition labelsPosition) {
        Objects.requireNonNull(viewMode, "ViewMode must be not null");
        return breakpoint(toBreakPoint(viewMode), columns, labelsPosition);
    }

    default C breakpoint(ViewMode viewMode, int columns) {
        return breakpoint(viewMode, columns, FormLayout.ResponsiveStep.LabelsPosition.TOP);
    }

    default C breakpoint(BreakPoint breakPoint, int columns) {
        return breakpoint(breakPoint, columns, FormLayout.ResponsiveStep.LabelsPosition.TOP);
    }

    default C breakpoint(String minWidth, int columns) {
        return breakpoint(minWidth, columns, FormLayout.ResponsiveStep.LabelsPosition.TOP);
    }

    default C mobile(int columns, FormLayout.ResponsiveStep.LabelsPosition labelsPosition) {
        return breakpoint(ViewMode.MOBILE, columns, labelsPosition);
    }

    default C mobile(int columns) {
        return mobile(columns, FormLayout.ResponsiveStep.LabelsPosition.TOP);
    }

    default C tablet(int columns, FormLayout.ResponsiveStep.LabelsPosition labelsPosition) {
        return breakpoint(ViewMode.TABLET, columns, labelsPosition);
    }

    default C tablet(int columns) {
        return tablet(columns, FormLayout.ResponsiveStep.LabelsPosition.TOP);
    }

    default C desktop(int columns, FormLayout.ResponsiveStep.LabelsPosition labelsPosition) {
        return breakpoint(ViewMode.DESKTOP, columns, labelsPosition);
    }

    default C desktop(int columns) {
        return desktop(columns, FormLayout.ResponsiveStep.LabelsPosition.TOP);
    }

    default C largeDesktop(int columns, FormLayout.ResponsiveStep.LabelsPosition labelsPosition) {
        return breakpoint(ViewMode.LARGE_DESKTOP, columns, labelsPosition);
    }

    default C largeDesktop(int columns) {
        return largeDesktop(columns, FormLayout.ResponsiveStep.LabelsPosition.TOP);
    }

    default C ultraWide(int columns, FormLayout.ResponsiveStep.LabelsPosition labelsPosition) {
        return breakpoint(ViewMode.ULTRA_WIDE, columns, labelsPosition);
    }

    default C ultraWide(int columns) {
        return ultraWide(columns, FormLayout.ResponsiveStep.LabelsPosition.TOP);
    }

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

    default C extraSmall(int columns) {
        return breakpoint(BreakPoint.BREAKPOINT_XS, columns);
    }

    default C medium(int columns,FormLayout.ResponsiveStep.LabelsPosition labelsPosition) {
        return breakpoint(BreakPoint.BREAKPOINT_MD, columns,labelsPosition);
    }

    default C large(int columns,FormLayout.ResponsiveStep.LabelsPosition labelsPosition) {
        return breakpoint(BreakPoint.BREAKPOINT_LG, columns,labelsPosition);
    }

    default C xLarge(int columns,FormLayout.ResponsiveStep.LabelsPosition labelsPosition) {
        return breakpoint(BreakPoint.BREAKPOINT_XL, columns,labelsPosition);
    }

    private static BreakPoint toBreakPoint(ViewMode viewMode) {
        return switch (viewMode) {
            case MOBILE, MOBILE_PORTRAIT, MOBILE_LANDSCAPE -> BreakPoint.BREAKPOINT_XS;
            case TABLET -> BreakPoint.BREAKPOINT_MD;
            case DESKTOP -> BreakPoint.BREAKPOINT_LG;
            case LARGE_DESKTOP -> BreakPoint.BREAKPOINT_XL;
            case ULTRA_WIDE -> BreakPoint.BREAKPOINT_XXL;
        };
    }



    List<FormLayout.ResponsiveStep> build();
}
