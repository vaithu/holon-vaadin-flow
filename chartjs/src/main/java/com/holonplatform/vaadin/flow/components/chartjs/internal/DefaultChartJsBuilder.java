/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin.flow.components.chartjs.internal;

import com.holonplatform.vaadin.flow.components.chartjs.ChartJsBuilder;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsComponent;

/**
 * Default {@link ChartJsBuilder} implementation.
 */
public class DefaultChartJsBuilder extends AbstractChartJsConfigurator<ChartJsBuilder> implements ChartJsBuilder {

	public DefaultChartJsBuilder() {
		super(new ChartJsComponent());
	}

	@Override
	protected ChartJsBuilder getConfigurator() {
		return this;
	}

	@Override
	public ChartJsComponent build() {
		applyFluentBuilders();
		applyPostProcessors();
		return getComponent();
	}
}







