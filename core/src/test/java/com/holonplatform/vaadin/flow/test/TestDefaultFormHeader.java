package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.internal.components.DefaultFormHeader;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestDefaultFormHeader {

    @Test
    public void testTitle() {

        DefaultFormHeader header = new DefaultFormHeader("Hello");
        assertThat(header).isNotNull();

        LabelBuilder<H4> title = LabelBuilder.h4().text("Hello");
        header.setTitle(title);

        assertThat(header.getTitle()).isEqualTo(title);

        assertThat(header.build()).isInstanceOf( HorizontalLayout.class);
    }
}
