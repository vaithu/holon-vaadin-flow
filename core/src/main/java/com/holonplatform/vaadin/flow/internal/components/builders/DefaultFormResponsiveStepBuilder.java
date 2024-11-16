package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.FormResponsiveStepBuilder;
import com.holonplatform.vaadin.flow.internal.components.support.BreakPoint;
import com.vaadin.flow.component.formlayout.FormLayout;
import org.apache.commons.lang3.StringUtils;

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

    // Helper method to parse minWidth string to integer for sorting
    private int parseMinWidth(FormLayout.ResponsiveStep step) {
        String minWidth = StringUtils.substringAfter(step.toJson().toString(),":");
        minWidth = StringUtils.substringBetween(minWidth, "\"", "\"");
        if (minWidth.endsWith("px")) {
            return Integer.parseInt(minWidth.replace("px", ""));
        }
        return 0; // Default to 0 if not a pixel value
    }

    @Override
    public List<FormLayout.ResponsiveStep> build() {
        // Sort the array based on the minWidth in ascending order
        responsiveSteps.sort(Comparator.comparingInt(this::parseMinWidth));
        return responsiveSteps;
    }
}
