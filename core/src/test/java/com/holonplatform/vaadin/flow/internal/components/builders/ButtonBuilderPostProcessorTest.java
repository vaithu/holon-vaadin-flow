package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.vaadin.flow.component.button.Button;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Unit tests for the {@code withPostProcessor} support on {@link ButtonBuilder}.
 */
class ButtonBuilderPostProcessorTest {

    // -----------------------------------------------------------------------
    // Positive tests
    // -----------------------------------------------------------------------

    @Test
    void singlePostProcessor_isAppliedOnBuild() {
        Button btn = ButtonBuilder.create()
                .text("Save")
                .withBuildPostProcessor(b -> b.getElement().setAttribute("data-testid", "save-btn"))
                .build();

        assertThat(btn.getElement().getAttribute("data-testid")).isEqualTo("save-btn");
    }

    @Test
    void multiplePostProcessors_areAppliedInRegistrationOrder() {
        List<String> callOrder = new ArrayList<>();

        Button btn = ButtonBuilder.create()
                .text("Test")
                .withBuildPostProcessor(b -> {
                    callOrder.add("first");
                    b.addClassName("class-a");
                })
                .withBuildPostProcessor(b -> {
                    callOrder.add("second");
                    b.addClassName("class-b");
                })
                .withBuildPostProcessor(b -> callOrder.add("third"))
                .build();

        // Order preserved
        assertThat(callOrder).containsExactly("first", "second", "third");

        // All side-effects applied
        assertThat(btn.hasClassName("class-a")).isTrue();
        assertThat(btn.hasClassName("class-b")).isTrue();
    }

    @Test
    void postProcessor_canModifyComponentProperties() {
        Button btn = ButtonBuilder.create()
                .withBuildPostProcessor(b -> b.setId("my-btn-id"))
                .withBuildPostProcessor(b -> b.setEnabled(false))
                .build();

        assertThat(btn.getId()).isPresent().hasValue("my-btn-id");
        assertThat(btn.isEnabled()).isFalse();
    }

    @Test
    void postProcessor_canStackWithBuilderConfiguration() {
        Button btn = ButtonBuilder.create()
                .text("Save")
                .primary()
                .withBuildPostProcessor(b -> b.addClassName("audit-tracked"))
                .build();

        // Builder-set text still intact
        assertThat(btn.getText()).isEqualTo("Save");
        // Post-processor class applied
        assertThat(btn.hasClassName("audit-tracked")).isTrue();
        // Primary variant added by builder is present (theme attribute contains 'primary')
        assertThat(btn.getElement().getAttribute("theme")).contains("primary");
    }

    @Test
    void buildWithoutPostProcessors_worksUnchanged() {
        Button btn = ButtonBuilder.create()
                .text("No Processors")
                .primary()
                .build();

        assertThat(btn.getText()).isEqualTo("No Processors");
        assertThat(btn.getElement().getAttribute("theme")).contains("primary");
    }

    @Test
    void postProcessor_isOnlyAppliedOnce_perBuildCall() {
        List<Integer> callCount = new ArrayList<>();

        ButtonBuilder builder = ButtonBuilder.create()
                .text("Once")
                .withBuildPostProcessor(b -> callCount.add(1));

        builder.build();
        // Calling build() a second time should apply post-processors again (builder is stateful)
        builder.build();

        assertThat(callCount).hasSize(2);
    }

    // -----------------------------------------------------------------------
    // Negative tests
    // -----------------------------------------------------------------------

    @Test
    void withPostProcessor_nullArgument_throwsNPE() {
        assertThatNullPointerException()
                .isThrownBy(() -> ButtonBuilder.create().withBuildPostProcessor(null));
    }
}

