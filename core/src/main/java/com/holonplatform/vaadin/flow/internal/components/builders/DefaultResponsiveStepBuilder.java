package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ResponsiveStepBuilder;
import com.holonplatform.vaadin.flow.internal.components.support.BreakPoint;
import com.vaadin.flow.component.formlayout.FormLayout;

import java.util.ArrayList;
import java.util.List;

public class DefaultResponsiveStepBuilder
        implements ResponsiveStepBuilder {

    private final List<FormLayout.ResponsiveStep> steps;
    private FormLayout.ResponsiveStep.LabelsPosition labelsPosition;

    public DefaultResponsiveStepBuilder() {
        this.labelsPosition = FormLayout.ResponsiveStep.LabelsPosition.TOP;
        steps = new ArrayList<>();
    }

    @Override
    public DefaultResponsiveStepBuilder breakpoint(String size, int columns) {
        steps.add(new FormLayout.ResponsiveStep(size, columns,this.labelsPosition));
        return this;
    }

    @Override
    public DefaultResponsiveStepBuilder breakpoint(BreakPoint breakPoint, int columns) {
        steps.add(new FormLayout.ResponsiveStep(breakPoint.getSize(), columns,this.labelsPosition));
        return this;
    }



    @Override
    public DefaultResponsiveStepBuilder label(FormLayout.ResponsiveStep.LabelsPosition labelsPosition) {
        this.labelsPosition = labelsPosition;
        return this;
    }

    @Override
    public List<FormLayout.ResponsiveStep> build() {
        return steps;
    }
}
