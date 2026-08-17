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
package com.holonplatform.vaadin.flow.components.chartjs;

import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.holonplatform.vaadin.flow.components.chartjs.internal.DefaultChartJsConfigurator;

import java.util.function.Consumer;

/**
 * Configurator for {@link ChartJsComponent} components.
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 *
 * @since 10.0.0
 */
public interface ChartJsConfigurator<C extends ChartJsConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    C type(ChartType chartType);

    C data(String dataJson);

    C data(ChartJsData data);

    C categories(String... categories);

    C series(ChartJsDataset series);

    C series(String name, Number... values);

    C addSeries(ChartJsDataset series);

    C addSeries(String name, Number... values);

    C options(String optionsJson);

    C options(ChartJsOptions options);

    C title(String title);

    C chartTitle(String title);

    C subtitle(String subtitle);

    C chartSubTitle(String subtitle);

    C responsive(boolean responsive);

    C maintainAspectRatio(boolean maintainAspectRatio);

    C legendEnabled(boolean enabled);

    C tooltipShared(boolean shared);

    C xAxisTitle(String title);

    C yAxisTitle(String title);

    C xAxis(Consumer<AxisBuilder> configurator);

    C yAxis(Consumer<AxisBuilder> configurator);

    C config(ChartJsConfig config);

    /**
     * Axis mini-builder for fluent axis configuration.
     */
    interface AxisBuilder {

        AxisBuilder title(String title);

        AxisBuilder categories(String... categories);
    }

    /**
     * Configure an existing {@link ChartJsComponent}.
     *
     * @param chart the component to configure (not null)
     * @return a new {@link BaseChartJsConfigurator}
     */
    static BaseChartJsConfigurator configure(ChartJsComponent chart) {
        return new DefaultChartJsConfigurator(chart);
    }

    interface BaseChartJsConfigurator extends ChartJsConfigurator<BaseChartJsConfigurator> {}
}
