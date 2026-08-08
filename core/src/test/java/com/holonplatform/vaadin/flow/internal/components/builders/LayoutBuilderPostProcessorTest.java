package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.HorizontalLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.VerticalLayoutBuilder;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Unit tests for the {@code withPostProcessor} support on
 * {@link HorizontalLayoutBuilder} and {@link VerticalLayoutBuilder}.
 */
class LayoutBuilderPostProcessorTest {

    // -----------------------------------------------------------------------
    // HorizontalLayoutBuilder — positive tests
    // -----------------------------------------------------------------------

    @Test
    void horizontalLayout_singlePostProcessor_isApplied() {
        HorizontalLayout layout = HorizontalLayoutBuilder.create()
                .withBuildPostProcessor(l -> l.getElement().setAttribute("data-testid", "h-toolbar"))
                .build();

        assertThat(layout.getElement().getAttribute("data-testid")).isEqualTo("h-toolbar");
    }

    @Test
    void horizontalLayout_multiplePostProcessors_appliedInOrder() {
        List<String> order = new ArrayList<>();

        HorizontalLayout layout = HorizontalLayoutBuilder.create()
                .withBuildPostProcessor(l -> { order.add("first");  l.addClassName("class-a"); })
                .withBuildPostProcessor(l -> { order.add("second"); l.addClassName("class-b"); })
                .build();

        assertThat(order).containsExactly("first", "second");
        assertThat(layout.hasClassName("class-a")).isTrue();
        assertThat(layout.hasClassName("class-b")).isTrue();
    }

    @Test
    void horizontalLayout_postProcessor_stacksWithBuilderConfig() {
        HorizontalLayout layout = HorizontalLayoutBuilder.create()
                .spacing()
                .padding()
                .withBuildPostProcessor(l -> l.addClassName("audit-tracked"))
                .build();

        // Builder-configured properties intact
        assertThat(layout.isSpacing()).isTrue();
        assertThat(layout.isPadding()).isTrue();
        // Post-processor applied
        assertThat(layout.hasClassName("audit-tracked")).isTrue();
    }

    @Test
    void horizontalLayout_buildWithoutPostProcessors_worksUnchanged() {
        HorizontalLayout layout = HorizontalLayoutBuilder.create()
                .spacing()
                .build();

        assertThat(layout.isSpacing()).isTrue();
        assertThat(layout.isPadding()).isFalse();
    }

    // -----------------------------------------------------------------------
    // VerticalLayoutBuilder — positive tests
    // -----------------------------------------------------------------------

    @Test
    void verticalLayout_singlePostProcessor_isApplied() {
        VerticalLayout layout = VerticalLayoutBuilder.create()
                .withBuildPostProcessor(l -> l.getElement().setAttribute("data-testid", "v-panel"))
                .build();

        assertThat(layout.getElement().getAttribute("data-testid")).isEqualTo("v-panel");
    }

    @Test
    void verticalLayout_multiplePostProcessors_appliedInOrder() {
        List<String> order = new ArrayList<>();

        VerticalLayout layout = VerticalLayoutBuilder.create()
                .withBuildPostProcessor(l -> { order.add("first");  l.addClassName("class-x"); })
                .withBuildPostProcessor(l -> { order.add("second"); l.addClassName("class-y"); })
                .build();

        assertThat(order).containsExactly("first", "second");
        assertThat(layout.hasClassName("class-x")).isTrue();
        assertThat(layout.hasClassName("class-y")).isTrue();
    }

    @Test
    void verticalLayout_postProcessor_stacksWithBuilderConfig() {
        VerticalLayout layout = VerticalLayoutBuilder.create()
                .padding()
                .withBuildPostProcessor(l -> l.setId("main-panel"))
                .build();

        assertThat(layout.isPadding()).isTrue();
        assertThat(layout.getId()).isPresent().hasValue("main-panel");
    }

    @Test
    void verticalLayout_buildWithoutPostProcessors_worksUnchanged() {
        VerticalLayout layout = VerticalLayoutBuilder.create()
                .padding()
                .build();

        assertThat(layout.isPadding()).isTrue();
        assertThat(layout.isSpacing()).isFalse();
    }

    // -----------------------------------------------------------------------
    // Negative tests — both builders
    // -----------------------------------------------------------------------

    @Test
    void horizontalLayout_withPostProcessor_null_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> HorizontalLayoutBuilder.create().withBuildPostProcessor(null));
    }

    @Test
    void verticalLayout_withPostProcessor_null_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> VerticalLayoutBuilder.create().withBuildPostProcessor(null));
    }
}

