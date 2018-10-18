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
import com.holonplatform.vaadin.flow.components.builders.LabelConfigurator;
import com.holonplatform.vaadin.flow.components.support.ContentMode;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HtmlContainer;

/**
 * Base {@link LabelConfigurator} implementation.
 * 
 * @param <L> Concrete label type
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractLabelConfigurator<L extends HtmlContainer & ClickNotifier, C extends LabelConfigurator<L, C>>
		extends AbstractLocalizableComponentConfigurator<L, C> implements LabelConfigurator<L, C> {

	protected final DefaultHasSizeConfigurator sizeConfigurator;
	protected final DefaultHasStyleConfigurator styleConfigurator;
	protected final DefaultHasEnabledConfigurator enabledConfigurator;
	protected final DefaultHasTextConfigurator textConfigurator;
	protected final DefaultHasHtmlTextConfigurator htmlTextConfigurator;
	protected final DefaultHasTitleConfigurator<L> titleConfigurator;

	private Localizable text;
	private ContentMode contentMode = ContentMode.TEXT;

	public AbstractLabelConfigurator(L component) {
		super(component);
		this.sizeConfigurator = new DefaultHasSizeConfigurator(component);
		this.styleConfigurator = new DefaultHasStyleConfigurator(component);
		this.enabledConfigurator = new DefaultHasEnabledConfigurator(component);
		this.textConfigurator = new DefaultHasTextConfigurator(component, this);
		this.htmlTextConfigurator = new DefaultHasHtmlTextConfigurator(component, this);
		this.titleConfigurator = new DefaultHasTitleConfigurator<>(component, title -> {
			component.setTitle(title);
		}, this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	@Override
	public C width(String width) {
		sizeConfigurator.width(width);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	@Override
	public C height(String height) {
		sizeConfigurator.height(height);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public C styleNames(String... styleNames) {
		styleConfigurator.styleNames(styleNames);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public C styleName(String styleName) {
		styleConfigurator.styleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#removeStyleName(java.lang.String)
	 */
	@Override
	public C removeStyleName(String styleName) {
		styleConfigurator.removeStyleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#replaceStyleName(java.lang.String)
	 */
	@Override
	public C replaceStyleName(String styleName) {
		styleConfigurator.replaceStyleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTitleConfigurator#title(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public C title(Localizable title) {
		titleConfigurator.title(title);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public C enabled(boolean enabled) {
		enabledConfigurator.enabled(enabled);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ClickNotifierConfigurator#withClickListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public C withClickListener(ComponentEventListener<ClickEvent<L>> listener) {
		getComponent().addClickListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasHtmlTextConfigurator#htmlText(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public C htmlText(Localizable text) {
		this.text = text;
		htmlTextConfigurator.htmlText(text);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTextConfigurator#text(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public C text(Localizable text) {
		this.text = text;
		textConfigurator.text(text);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.LabelConfigurator#content(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public C content(Localizable content) {
		this.text = content;
		switch (contentMode) {
		case HTML:
			return htmlText(content);
		case TEXT:
		default:
			return text(content);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.LabelConfigurator#contentMode(com.holonplatform.vaadin.flow.
	 * components.support.ContentMode)
	 */
	@Override
	public C contentMode(ContentMode contentMode) {
		this.contentMode = (contentMode != null) ? contentMode : ContentMode.TEXT;
		if (text != null) {
			return content(text);
		}
		return getConfigurator();
	}

}
