package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.internal.components.support.BreakPoint;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

public class TestFormLayoutResponsiveSteps {



    @Test
    public void testFormLayoutResponsiveSteps() {
        final FormLayout build = Components.formLayout()
                .responsiveSteps(
                        Components.utils.responsiveSteps()
                                .breakpoint(BreakPoint.BREAKPOINT_MD, 4, FormLayout.ResponsiveStep.LabelsPosition.TOP)
                                .breakpoint(BreakPoint.BREAKPOINT_SM, 1, FormLayout.ResponsiveStep.LabelsPosition.TOP)
                                .build()
                )
                .build();

        final List<String> steps1 = Stream.of("""
                      {"minWidth":"576px","columns":1,"labelsPosition":"top"}
                      """,
                        """
                                                     {"minWidth":"768px","columns":4,"labelsPosition":"top"}
                                """)
                .map(String::trim)
                .map(this::normalizeJson).toList();

        final List<String> sortedSteps = build.getResponsiveSteps().stream().map(responsiveStep -> responsiveStep.toJson().toString().trim())
                .map(this::normalizeJson)
                .toList();

        /*for (int i = 0; i < sortedSteps.size(); i++) {
            Assertions.assertThat(sortedSteps.get(i)).isEqualTo(steps1.get(i));
        }*/

        Assertions.assertThat(sortedSteps).isEqualTo(steps1);

//        final List<FormLayout.ResponsiveStep> steps2 = List.of(new FormLayout.ResponsiveStep("576px", 1), new FormLayout.ResponsiveStep("768px", 4));
    }

    private String normalizeJson(String json) {
        return json.replaceAll("\\s+", "").replaceAll("\\r?\\n", "");
    }

    @Test
    public void testResponsiveAutoUpdateColumnSize() {
        final FormLayout build = Components.formLayout()
                .responsiveSteps(
                        Components.utils.responsiveSteps()
                                .breakpoint(BreakPoint.BREAKPOINT_MD, 4, FormLayout.ResponsiveStep.LabelsPosition.TOP)
                                .breakpoint(BreakPoint.BREAKPOINT_SM, 1, FormLayout.ResponsiveStep.LabelsPosition.TOP)
                                .build()
                )
                .add(2,new Button())
                .add(1,new Button())
                .autoUpdateResponsiveStepColumnSizeEnabled(true)
                .build();

        build.getResponsiveSteps().forEach(responsiveStep -> System.out.println(responsiveStep.toJson().toString()));
    }
}
