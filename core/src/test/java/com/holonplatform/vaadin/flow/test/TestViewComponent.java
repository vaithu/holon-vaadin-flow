/*
 * Copyright 2016-2018 Axioma srl.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ViewComponent;
import com.holonplatform.vaadin.flow.components.builders.ViewComponentBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.ComponentUtil;

public class TestViewComponent {

	@Test
	public void testBuilders() {

		ViewComponentBuilder<String> builder1 = ViewComponentBuilder.create(String.class);
		assertNotNull(builder1);
		ViewComponent<String> vc = builder1.build();
		assertNotNull(vc);

		ViewComponentBuilder<Integer> builder2 = ViewComponentBuilder.create(v -> Objects.toString(v));
		assertNotNull(builder2);
		ViewComponent<Integer> vc2 = builder2.build();
		assertNotNull(vc2);

		ViewComponentBuilder<Double> builder3 = ViewComponentBuilder.<Double>create(v -> Objects.toString(v));
		assertNotNull(builder3);
		ViewComponent<Double> vc3 = builder3.build();
		assertNotNull(vc3);

		ViewComponentBuilder<String> builder4 = ViewComponentBuilder.create(StringProperty.create("test"));
		assertNotNull(builder4);
		ViewComponent<String> vc4 = builder4.build();
		assertNotNull(vc4);

		ViewComponentBuilder<String> builder5 = ViewComponent.builder(String.class);
		assertNotNull(builder5);
		ViewComponent<String> vc5 = builder1.build();
		assertNotNull(vc5);

		ViewComponentBuilder<Integer> builder6 = ViewComponent.builder(v -> Objects.toString(v));
		assertNotNull(builder6);
		ViewComponent<Integer> vc6 = builder6.build();
		assertNotNull(vc6);

		ViewComponentBuilder<Integer> builder7 = ViewComponent.builder(NumericProperty.integerType("test"));
		assertNotNull(builder7);
		ViewComponent<Integer> vc7 = builder7.build();
		assertNotNull(vc7);

		ViewComponent<Integer> vc8 = ViewComponent.create(NumericProperty.integerType("test"));
		assertNotNull(vc8);

		ViewComponentBuilder<String> builder9 = Components.view.component(String.class);
		assertNotNull(builder9);
		ViewComponent<String> vc9 = builder9.build();
		assertNotNull(vc9);

		ViewComponentBuilder<Integer> builder10 = Components.view.component(v -> Objects.toString(v));
		assertNotNull(builder10);
		ViewComponent<Integer> vc10 = builder10.build();
		assertNotNull(vc10);

		ViewComponentBuilder<Integer> builder11 = Components.view.component(NumericProperty.integerType("test"));
		assertNotNull(builder11);
		ViewComponent<Integer> vc11 = builder11.build();
		assertNotNull(vc11);

		ViewComponent<Integer> vc12 = Components.view.property(NumericProperty.integerType("test"));
		assertNotNull(vc12);
	}

	@Test
	public void testComponent() {

		ViewComponent<String> vc = ViewComponent.builder(String.class).id("testid").build();
		assertNotNull(vc.getComponent());
		assertTrue(vc.getComponent().getId().isPresent());
		assertEquals("testid", vc.getComponent().getId().get());

		vc = ViewComponent.builder(String.class).visible(true).build();
		assertTrue(vc.isVisible());

		vc = ViewComponent.builder(String.class).visible(false).build();
		assertFalse(vc.isVisible());

		vc = ViewComponent.builder(String.class).hidden().build();
		assertFalse(vc.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		vc = ViewComponent.builder(String.class).withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(vc.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		vc = ViewComponent.builder(String.class).withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(vc.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		ViewComponent<String> vc = ViewComponent.builder(String.class).styleName("test").build();
		assertNotNull(vc);

		assertTrue(vc.getClassNames().contains("test"));

		vc = ViewComponent.builder(String.class).styleName("test1").styleName("test2").build();
		assertTrue(vc.getClassNames().contains("test1"));
		assertTrue(vc.getClassNames().contains("test2"));

		vc = ViewComponent.builder(String.class).styleNames("test").build();
		assertTrue(vc.getClassNames().contains("test"));

		vc = ViewComponent.builder(String.class).styleNames("test1", "test2").build();
		assertTrue(vc.getClassNames().contains("test1"));
		assertTrue(vc.getClassNames().contains("test2"));

		vc = ViewComponent.builder(String.class).styleNames("test1", "test2").removeStyleName("test2")
				.replaceStyleName("test3").build();
		assertFalse(vc.getClassNames().contains("test1"));
		assertFalse(vc.getClassNames().contains("test2"));
		assertTrue(vc.getClassNames().contains("test3"));

	}

	@Test
	public void testSize() {

		ViewComponent<String> vc = ViewComponent.builder(String.class).build();
		assertNull(vc.getWidth());
		assertNull(vc.getHeight());

		vc = ViewComponent.builder(String.class).width("50em").build();
		assertEquals("50em", vc.getWidth());

		vc = ViewComponent.builder(String.class).width(50, Unit.EM).build();
		assertEquals("50em", vc.getWidth());

		vc = ViewComponent.builder(String.class).width(50.7f, Unit.EM).build();
		assertEquals("50.7em", vc.getWidth());

		vc = ViewComponent.builder(String.class).height("50em").build();
		assertEquals("50em", vc.getHeight());

		vc = ViewComponent.builder(String.class).height(50, Unit.EM).build();
		assertEquals("50em", vc.getHeight());

		vc = ViewComponent.builder(String.class).height(50.7f, Unit.EM).build();
		assertEquals("50.7em", vc.getHeight());

		vc = ViewComponent.builder(String.class).width("50%").height("100%").build();
		assertEquals("50%", vc.getWidth());
		assertEquals("100%", vc.getHeight());

		vc = ViewComponent.builder(String.class).width("100px").widthUndefined().build();
		assertNull(vc.getWidth());

		vc = ViewComponent.builder(String.class).height("100px").heightUndefined().build();
		assertNull(vc.getHeight());

		vc = ViewComponent.builder(String.class).width("100px").height("100px").sizeUndefined().build();
		assertNull(vc.getWidth());
		assertNull(vc.getHeight());

		vc = ViewComponent.builder(String.class).fullWidth().build();
		assertEquals("100%", vc.getWidth());
		assertNull(vc.getHeight());

		vc = ViewComponent.builder(String.class).fullHeight().build();
		assertEquals("100%", vc.getHeight());
		assertNull(vc.getWidth());

		vc = ViewComponent.builder(String.class).fullWidth().fullHeight().build();
		assertEquals("100%", vc.getWidth());
		assertEquals("100%", vc.getHeight());

		vc = ViewComponent.builder(String.class).fullSize().build();
		assertEquals("100%", vc.getWidth());
		assertEquals("100%", vc.getHeight());

	}

	@Test
	public void testEnabled() {

		ViewComponent<String> vc = ViewComponent.builder(String.class).build();
		assertTrue(vc.isEnabled());

		vc = ViewComponent.builder(String.class).enabled(true).build();
		assertTrue(vc.isEnabled());

		vc = ViewComponent.builder(String.class).enabled(false).build();
		assertFalse(vc.isEnabled());

		vc = ViewComponent.builder(String.class).disabled().build();
		assertFalse(vc.isEnabled());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testLabel() {

		ViewComponent<String> vc = ViewComponent.builder(String.class)
				.label(Localizable.builder().message("test").build()).build();
		assertEquals("test", vc.getLabel());

		vc = ViewComponent.builder(String.class).label("test").build();
		assertEquals("test", vc.getLabel());

		vc = ViewComponent.builder(String.class).label("test", "test.code").build();
		assertEquals("test", vc.getLabel());

		vc = ViewComponent.builder(String.class).label("test", "test.code", "arg").build();
		assertEquals("test", vc.getLabel());

		vc = ViewComponent.builder(String.class).caption(Localizable.builder().message("test").build()).build();
		assertEquals("test", vc.getLabel());

		vc = ViewComponent.builder(String.class).caption("test").build();
		assertEquals("test", vc.getLabel());

		vc = ViewComponent.builder(String.class).caption("test", "test.code").build();
		assertEquals("test", vc.getLabel());

		vc = ViewComponent.builder(String.class).caption("test", "test.code", "arg").build();
		assertEquals("test", vc.getLabel());

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			ViewComponent<String> vc2 = ViewComponent.builder(String.class)
					.label(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", vc2.getLabel());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			ViewComponent<String> vc2 = ViewComponent.builder(String.class).label("test", "test.code").build();
			assertEquals("TestUS", vc2.getLabel());
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			ViewComponent<String> vc2 = ViewComponent.builder(String.class).deferLocalization()
					.label("test", "test.code").build();
			assertEquals("test", vc2.getLabel());
			ComponentUtil.onComponentAttach(vc2.getComponent(), true);
			assertEquals("TestUS", vc2.getLabel());
		});

	}

	@Test
	public void testDescription() {

		ViewComponent<String> vc = ViewComponent.builder(String.class)
				.title(Localizable.builder().message("test").build()).build();
		assertEquals("test", vc.getElement().getAttribute("title"));

		vc = ViewComponent.builder(String.class).title("test").build();
		assertEquals("test", vc.getElement().getAttribute("title"));

		vc = ViewComponent.builder(String.class).title("test", "test.code").build();
		assertEquals("test", vc.getElement().getAttribute("title"));

		vc = ViewComponent.builder(String.class).title("test", "test.code", "arg").build();
		assertEquals("test", vc.getElement().getAttribute("title"));

		vc = ViewComponent.builder(String.class).description(Localizable.builder().message("test").build()).build();
		assertEquals("test", vc.getElement().getAttribute("title"));

		vc = ViewComponent.builder(String.class).description("test").build();
		assertEquals("test", vc.getElement().getAttribute("title"));

		vc = ViewComponent.builder(String.class).description("test", "test.code").build();
		assertEquals("test", vc.getElement().getAttribute("title"));

		vc = ViewComponent.builder(String.class).description("test", "test.code", "arg").build();
		assertEquals("test", vc.getElement().getAttribute("title"));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			ViewComponent<String> vc2 = ViewComponent.builder(String.class)
					.title(Localizable.builder().message("test").messageCode("test.code").build()).build();
			assertEquals("TestUS", vc2.getElement().getAttribute("title"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			ViewComponent<String> vc2 = ViewComponent.builder(String.class).title("test", "test.code").build();
			assertEquals("TestUS", vc2.getElement().getAttribute("title"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			ViewComponent<String> vc2 = ViewComponent.builder(String.class).deferLocalization()
					.title("test", "test.code").build();
			assertEquals("test", vc2.getElement().getAttribute("title"));
			ComponentUtil.onComponentAttach(vc2.getComponent(), true);
			assertEquals("TestUS", vc2.getElement().getAttribute("title"));
		});

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			ViewComponent<String> vc2 = ViewComponent.builder(String.class).withDeferredLocalization(true)
					.label("test", "test.code").title("test", "test.code").build();
			assertEquals("test", vc2.getLabel());
			assertEquals("test", vc2.getElement().getAttribute("title"));
			ComponentUtil.onComponentAttach(vc2.getComponent(), true);
			assertEquals("TestUS", vc2.getLabel());
			assertEquals("TestUS", vc2.getElement().getAttribute("title"));
		});

	}
	
	@Test
	public void testValueHolder() {
		// TODO
	}

}
