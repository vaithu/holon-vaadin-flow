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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.MenuBarConfigurator;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.menubar.MenuBar;

import java.util.function.Function;

/**
 * Default {@link MenuBarConfigurator.MenuItemBuilder}.
 *
 * @param <M> Concrete MenuBar component type
 * @param <I> Menu item type
 * @param <S> Sub menu type
 * @param <B> Parent configurator type
 *
 * @since 5.5.6
 */
public class DefaultMenuBarItemBuilder<M extends MenuBar, I extends MenuItem, S extends SubMenu
		, B extends MenuBarConfigurator<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B>>
		implements MenuBarConfigurator.MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B> {

	private final B parentBuilder;
	private final MenuItem menuItem;
	private final SubMenu subMenu;
	private MenuItem newMenuItem;
	private final Function<com.vaadin.flow.component.ClickEvent<MenuItem>, ClickEvent<MenuItem>> clickEventConverter;

	public DefaultMenuBarItemBuilder(B parentBuilder, MenuItem menuItem,
                                     Function<com.vaadin.flow.component.ClickEvent<MenuItem>, ClickEvent<MenuItem>> clickEventConverter) {
		super();
		ObjectUtils.argumentNotNull(parentBuilder, "Parent builder must be not null");
		ObjectUtils.argumentNotNull(menuItem, "Menu item must be not null");
		ObjectUtils.argumentNotNull(clickEventConverter, "Click event converter must be not null");
		this.parentBuilder = parentBuilder;
		this.menuItem = menuItem;
		this.subMenu = menuItem.getSubMenu();
		this.clickEventConverter = clickEventConverter;
	}

	/**
	 * Sets the id of the root element of the menu item.
	 *
	 * @param id the id to set
	 * @return this
	 */
	@Override
	public MenuBarConfigurator.MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B> id(String id) {
		menuItem.setId(id);
		return this;
	}

	/**
	 * Set whether the menu item is checkable.
	 * <p>
	 * A checkable item toggles a checkmark icon when clicked.
	 * </p>
	 * <p>
	 * Changes in the checked state can be handled in the item's click handler with <code>isChecked()</code>.
	 * <p>
	 *
	 * @param checkable Whether the menu item is checkable
	 * @return this
	 * @since 5.2.3
	 */
	@Override
	public MenuBarConfigurator.MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B> checkable(boolean checkable) {
		menuItem.setCheckable(checkable);
		return this;
	}

	/**
	 * Set whether a checkable menu item is checked.
	 *
	 * @param checked Whether the menu item is checked
	 * @return this
	 * @see #checkable(boolean)
	 * @since 5.2.3
	 */
	@Override
	public MenuBarConfigurator.MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B> checked(boolean checked) {
		if (checked && !menuItem.isCheckable()) {
			menuItem.setCheckable(true);
		}
		menuItem.setChecked(checked);
		return this;
	}

	/**
	 * Register a menu item click event listener.
	 *
	 * @param menuItemClickListener The listener to add (not null)
	 * @return this
	 */
	@Override
	public MenuBarConfigurator.MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B> withClickListener(ClickEventListener<MenuItem, ClickEvent<MenuItem>> menuItemClickListener) {
		ObjectUtils.argumentNotNull(menuItemClickListener, "Click listener must be not null");
		menuItem.addClickListener(e -> menuItemClickListener.onClickEvent(clickEventConverter.apply(e)));
		return this;
	}

	/**
	 * Set whether the component is enabled.
	 *
	 * @param enabled if <code>false</code> then explicitly disables the object, if <code>true</code> then enables the
	 *                object so that its state depends on parent
	 * @return this
	 */
	@Override
	public MenuBarConfigurator.MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B> enabled(boolean enabled) {
		menuItem.setEnabled(enabled);
		return this;
	}

	/**
	 * Adds one or more CSS style class names to this component.
	 *
	 * @param styleNames The CSS style class names to be added to the component
	 * @return this
	 */
	@Override
	public MenuBarConfigurator.MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B> styleNames(String... styleNames) {
		menuItem.addClassNames(styleNames);
		return this;
	}

	/**
	 * Adds a CSS style class names to this component.
	 * <p>
	 * Multiple styles can be specified as a space-separated list of style names.
	 * </p>
	 *
	 * @param styleName The CSS style class name to be added to the component
	 * @return this
	 */
	@Override
	public MenuBarConfigurator.MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B> styleName(String styleName) {
		menuItem.addClassName(styleName);
		return this;
	}

	/**
	 * Sets the text content using a {@link Localizable} message.
	 * <p>
	 * The text value is interpred as <em>plain text</em> and the HTML markup is not supported.
	 * </p>
	 * <p>
	 * A <code>null</code> value is interpreted as an empty text.
	 * </p>
	 *
	 * @param text Localizable text content message (may be null)
	 * @return this
	 * @see LocalizationProvider
	 */
	@Override
	public MenuBarConfigurator.MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B> text(Localizable text) {
		menuItem.setText(LocalizationProvider.localize(text).orElse(""));
		return this;
	}

	/**
	 * Sets the keep open state of this menu item. An item that marked as keep open prevents menu from closing when clicked.
	 *
	 * @param keepOpen whether clicking this item keeps the menu open
	 * @return this
	 * @since 5.5.6
	 */
	@Override
	public MenuBarConfigurator.MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, B> keepOpen(boolean keepOpen) {
		menuItem.setKeepOpen(keepOpen);
		return this;
	}


	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator.MenuItemBuilder#add()
	 */
	@Override
	public B add() {
		return parentBuilder;
	}

}
