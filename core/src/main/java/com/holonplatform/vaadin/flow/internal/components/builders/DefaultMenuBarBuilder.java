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
package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.MenuBarBuilder;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultClickEvent;
import com.holonplatform.vaadin.flow.internal.components.support.ComponentClickListenerAdapter;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.menubar.MenuBar;

/**
 * Default {@link MenuBarBuilder} implementation.
 *
 * @since 5.5.6
 */
public class DefaultMenuBarBuilder
		extends AbstractMenuBarConfigurator<MenuBar, MenuItem, SubMenu, MenuBarBuilder>
		implements MenuBarBuilder {

	public DefaultMenuBarBuilder() {
		super(new MenuBar(), (menu, text) -> menu.addItem(text),
				(menu, component) -> menu.addItem(component), e -> ComponentClickListenerAdapter
						.configure(new DefaultClickEvent<>(e.getSource(), e.isFromClient()), e));
	}

    /*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected MenuBarBuilder getConfigurator() {
		return this;
	}

	@Override
	public MenuBar build() {
		return getInstance();
	}
}
