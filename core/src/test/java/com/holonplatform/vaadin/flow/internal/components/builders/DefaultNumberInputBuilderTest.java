package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Input;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultNumberInputBuilderTest {

    @Test
    void textFieldBackedNumberInput_usesRightAlignedThemeVariant() {
        Input<Integer> input = new DefaultNumberInputBuilder<>(Integer.class).build();

        assertThat(input.getComponent().getElement().getAttribute("theme")).contains("align-right");
    }

    @Test
    void numberFieldBackedNumberInput_usesRightAlignedThemeVariant() {
        Input<Integer> input = new DefaultNumberFieldInputBuilder<>(Integer.class).build();

        assertThat(input.getComponent().getElement().getAttribute("theme")).contains("align-right");
    }
}