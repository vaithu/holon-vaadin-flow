package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.FormResponsiveStepBuilder;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.internal.components.support.BreakPoint;
import com.vaadin.flow.component.formlayout.FormLayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DefaultFormResponsiveStepBuilder
        implements FormResponsiveStepBuilder {

    private final List<FormLayout.ResponsiveStep> responsiveSteps;

    public DefaultFormResponsiveStepBuilder() {
        responsiveSteps = new ArrayList<>();
    }

    @Override
    public DefaultFormResponsiveStepBuilder breakpoint(String size, int columns,FormLayout.ResponsiveStep.LabelsPosition labelsPosition) {
        responsiveSteps.add(new FormLayout.ResponsiveStep(size, columns,labelsPosition));
        return this;
    }

    @Override
    public DefaultFormResponsiveStepBuilder breakpoint(BreakPoint breakPoint, int columns,FormLayout.ResponsiveStep.LabelsPosition labelsPosition) {
        responsiveSteps.add(new FormLayout.ResponsiveStep(breakPoint.getSize(), columns,labelsPosition));
        return this;
    }

    @SuppressWarnings("unused") // debug helper
    private void printResponsiveStepsValue() {
        responsiveSteps.forEach(responsiveStep -> System.out.println(responsiveStep.toJson()));
    }

    @Override
    public List<FormLayout.ResponsiveStep> build() {
//        printResponsiveStepsValue();
        // Sort the array based on the minWidth in ascending order
        responsiveSteps.sort(Comparator.comparingInt(UIUtils::parseMinWidth));
//        printResponsiveStepsValue();
        return responsiveSteps;
    }
}
