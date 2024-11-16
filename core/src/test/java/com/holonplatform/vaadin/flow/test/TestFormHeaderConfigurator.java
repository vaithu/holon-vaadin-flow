package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.FormHeaderBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFormHeader;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestFormHeaderConfigurator {


    @Test
    public void testHeader() {
        FormHeaderBuilder header = new DefaultFormHeader()
                .title("New Item")
                .styleNames("dsdfsdf")
                .horizontalRule();

        assertThat(header)
                .isNotNull();

        header = Components.formHeader();


        assertThat(header)
                .isNotNull();

        assertThat(header.build()).isInstanceOf(HorizontalLayout.class);


        HorizontalLayout layout = Components.formHeader().build();
        assertThat(layout).isInstanceOf(HorizontalLayout.class);
        layout.getClassNames().forEach(s -> System.out.println(s));
        assertThat(layout.getClassNames().contains("dsdfsdf"))
                .isFalse();

        layout = Components.formHeader()
                .title("New Item")
                .styleNames("header")
                .closeBtnConfigurator(closeButton -> {
                })
                .horizontalRule()
                .build();

        assertThat(layout).isNotNull();

    }
}
