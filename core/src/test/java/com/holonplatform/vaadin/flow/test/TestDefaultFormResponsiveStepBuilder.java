package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFormResponsiveStepBuilder;
import com.holonplatform.vaadin.flow.internal.components.support.BreakPoint;
import com.vaadin.flow.component.formlayout.FormLayout;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestDefaultFormResponsiveStepBuilder {

    @Test
    void breakpoint() {
        DefaultFormResponsiveStepBuilder builder = new DefaultFormResponsiveStepBuilder();
        builder.breakpoint(BreakPoint.BREAKPOINT_LG, 2,FormLayout.ResponsiveStep.LabelsPosition.TOP);
        builder.breakpoint(BreakPoint.BREAKPOINT_SM, 1,FormLayout.ResponsiveStep.LabelsPosition.ASIDE);
        builder.medium(3, FormLayout.ResponsiveStep.LabelsPosition.ASIDE);
        List<FormLayout.ResponsiveStep> responsiveSteps = builder.build();
//        responsiveSteps.forEach(responsiveStep -> System.out.println(responsiveStep.toJson()));
        assertEquals(3,responsiveSteps.size());
    }

}