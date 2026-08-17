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

import com.holonplatform.vaadin.flow.components.chartjs.ChartJsComponent;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsConfigurator;

/**
 * Default {@link ChartJsConfigurator.BaseChartJsConfigurator} implementation that configures
 * an existing {@link ChartJsComponent}.
 */
public class DefaultChartJsConfigurator
        extends AbstractChartJsConfigurator<ChartJsConfigurator.BaseChartJsConfigurator>
        implements ChartJsConfigurator.BaseChartJsConfigurator {

    public DefaultChartJsConfigurator(ChartJsComponent chart) {
        super(chart);
    }

    @Override
    protected ChartJsConfigurator.BaseChartJsConfigurator getConfigurator() {
        return this;
    }
}
