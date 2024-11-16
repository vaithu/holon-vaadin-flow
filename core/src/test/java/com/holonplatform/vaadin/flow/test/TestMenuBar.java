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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.MenuBarBuilder;
import com.holonplatform.vaadin.flow.components.builders.MenuItemBuilder;
import com.holonplatform.vaadin.flow.test.util.LocalizationTestUtils;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestMenuBar {

	@Test
	public void testBuilder() {

		MenuBarBuilder builder = MenuBarBuilder.create();
		assertNotNull(builder);

		MenuBar menuBar = builder.build();
		assertNotNull(menuBar);

		/*builder = Components.menuBar();
		assertNotNull(builder);*/

	}

	@Test
	public void testComponent() {

		MenuBar menu = MenuBarBuilder.create().id("testid").build();
		assertTrue(menu.getId().isPresent());
		assertEquals("testid", menu.getId().get());

		menu = MenuBarBuilder.create().build();
		assertTrue(menu.isVisible());

		menu = MenuBarBuilder.create().visible(true).build();
		assertTrue(menu.isVisible());

		menu = MenuBarBuilder.create().visible(false).build();
		assertFalse(menu.isVisible());

		menu = MenuBarBuilder.create().hidden().build();
		assertFalse(menu.isVisible());
	}

	@Test
	public void testStyles() {

		MenuBar menu = MenuBarBuilder.create().styleName("test").build();
		assertNotNull(menu);

		assertTrue(menu.getClassNames().contains("test"));

		menu = MenuBarBuilder.create().styleName("test1").styleName("test2").build();
		assertTrue(menu.getClassNames().contains("test1"));
		assertTrue(menu.getClassNames().contains("test2"));

		menu = MenuBarBuilder.create().styleNames("test").build();
		assertTrue(menu.getClassNames().contains("test"));

		menu = MenuBarBuilder.create().styleNames("test1", "test2").build();
		assertTrue(menu.getClassNames().contains("test1"));
		assertTrue(menu.getClassNames().contains("test2"));

	}

	@Test
	public void testMenuItems() {

		MenuBar menu = MenuBarBuilder.create().withMenuItem("test1").add().withMenuItem("test2").add().build();
		assertEquals(2L, menu.getItems().stream().count());

		menu = MenuBarBuilder.create().withMenuItem("test1").disabled().add().build();
		assertFalse(menu.getItems().stream().findFirst().map(i -> i.isEnabled()).orElse(true));

		menu = MenuBarBuilder.create().withMenuItem("test1").text("test2").add().build();
		assertNotNull(menu.getItems().stream().filter(i -> "test2".equals(i.getText())).findFirst().orElse(null));

	}

	@Test
	public void testTextMenuItems() {

		MenuBar menu = MenuBarBuilder.create().withMenuItem("test").add().build();
		assertNotNull(menu.getItems().stream().filter(i -> "test".equals(i.getText())).findFirst().orElse(null));

		menu = MenuBarBuilder.create().withMenuItem(Localizable.builder().message("test").build()).add().build();
		assertNotNull(menu.getItems().stream().filter(i -> "test".equals(i.getText())).findFirst().orElse(null));

		menu = MenuBarBuilder.create().withMenuItem("test", "test.code").add().build();
		assertNotNull(menu.getItems().stream().filter(i -> "test".equals(i.getText())).findFirst().orElse(null));

		LocalizationTestUtils.withTestLocalizationContext(() -> {
			MenuBar menu2 = MenuBarBuilder.create()
					.withMenuItem(Localizable.builder().message("test").messageCode("test.code").build()).add().build();
			assertNotNull(menu2.getItems().stream().filter(i -> "TestUS".equals(i.getText())).findFirst().orElse(null));
		});

		menu = MenuBarBuilder.create().withMenuItem("test", e -> {
		}).build();
		assertNotNull(menu.getItems().stream().filter(i -> "test".equals(i.getText())).findFirst().orElse(null));

	}

	@Test
	public void testComponentMenuItems() {

		final Span cmp = new Span("test");

		MenuBar menu = MenuBarBuilder.create().withMenuItem(cmp).add().build();
		assertNotNull(menu.getItems().stream().filter(i -> cmp.equals(i.getChildren().findFirst().orElse(null)))
				.findFirst().orElse(null));

		menu = MenuBarBuilder.create().withMenuItem(cmp, e -> {
		}).build();
		assertNotNull(menu.getItems().stream().filter(i -> cmp.equals(i.getChildren().findFirst().orElse(null)))
				.findFirst().orElse(null));

	}

	@Test
	public void testSubMenu() {

		final Span cmp = new Span("test");

		MenuBar menu = MenuBarBuilder.create().withMenuItem(cmp)
				.add()
				.withMenuItem("Test")
				.add()
				.build();

		assertNotNull(menu);

		final MenuItemBuilder menuItemBuilder = MenuItemBuilder.create().withMenuItem("test").withMenuItem("test");

		final MenuItem menuItem1 = menuItemBuilder.withSubMenu("dsfsdf").withMenuItem("sdskdfjsdf").getMenuItem();
		assertNotNull(menuItem1);

		final MenuItem menuItem2 = menuItemBuilder.withSubMenu("dsfsdf").withSubMenuItem("sdskdfasdasdjsdf").getMenuItem();
		assertNotEquals(menuItem1,menuItem2);



		/*final SubMenu subMenu = menu.getItems().get(0).getSubMenu();

		assertNotNull(subMenu);
		assertNotNull(menu.getItems().get(1).getSubMenu());

		assertNotNull(subMenu.getItems());

		Element element = subMenu.getParentMenuItem().getElement();

		System.out.println(element);

		for (MenuItem item : subMenu.getItems()) {
			element = item.getElement();
			System.out.println(element);
		}

		System.out.println(subMenu.getItems().get(0).getElement());*/

	}

}
