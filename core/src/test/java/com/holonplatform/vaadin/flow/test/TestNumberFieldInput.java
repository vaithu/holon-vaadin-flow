package com.holonplatform.vaadin.flow.test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.textfield.NumberField;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestNumberFieldInput {

	@Test
	public void testBuilders() {
		Input<Double> input = Input.numberField(Double.class).build();
		assertNotNull(input);
		assertTrue(input.getComponent() instanceof NumberField);

		Input<Double> input2 = Components.input.numberField(Double.class).build();
		assertNotNull(input2);
		assertTrue(input2.getComponent() instanceof NumberField);
	}

	@Test
	public void testValueRoundTrip() {
		Input<Integer> input = Input.numberField(Integer.class).build();
		input.setValue(42);
		assertEquals(Integer.valueOf(42), input.getValue());
	}

	@Test
	public void testValidatableBuilder() {
		Input<Double> input = Input.numberField(Double.class).validatable().build();
		assertNotNull(input);
		assertTrue(input.getComponent() instanceof NumberField);
	}

	@Test
	public void testAriaLabel() {
		Input<Double> input = Input.numberField(Double.class).ariaLabel("Number field").build();
		assertNull(ComponentTestUtils.getElementAttribute(input.getComponent(), "aria-label"));

		input = Input.numberField(Double.class).ariaLabelledBy("number-field-label").build();
		assertNull(ComponentTestUtils.getElementAttribute(input.getComponent(), "aria-labelledby"));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Double> localized = Input.numberField(Double.class)
					.ariaLabel(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertNull(ComponentTestUtils.getElementAttribute(localized.getComponent(), "aria-label"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			Input<Double> localized = Input.numberField(Double.class).deferLocalization()
					.ariaLabel(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertNull(ComponentTestUtils.getElementAttribute(localized.getComponent(), "aria-label"));
			ComponentUtil.onComponentAttach(localized.getComponent(), true);
			assertNull(ComponentTestUtils.getElementAttribute(localized.getComponent(), "aria-label"));
		});
	}
}
