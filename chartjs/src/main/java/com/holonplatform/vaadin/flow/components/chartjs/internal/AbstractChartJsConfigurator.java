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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsComponent;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsConfig;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsConfigurator;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsData;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsDataset;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsOptions;
import com.holonplatform.vaadin.flow.components.chartjs.ChartType;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Base {@link ChartJsConfigurator} implementation.
 *
 * @param <C> Concrete configurator type
 *
 * @since 10.0.0
 */
public abstract class AbstractChartJsConfigurator<C extends ChartJsConfigurator<C>>
        extends AbstractComponentConfigurator<ChartJsComponent, C>
        implements ChartJsConfigurator<C> {

    private final ChartJsData.Builder fluentDataBuilder = ChartJsData.create();
    private final ChartJsOptions.Builder fluentOptionsBuilder = ChartJsOptions.create();

    private long orderCounter;
    private long explicitDataOrder = -1;
    private long explicitOptionsOrder = -1;
    private long fluentDataOrder = -1;
    private long fluentOptionsOrder = -1;

    public AbstractChartJsConfigurator(ChartJsComponent component) {
        super(component);
    }

    /**
     * Apply accumulated fluent data and options builders to the component.
     * Must be called by {@code build()} before returning the component.
     */
    protected void applyFluentBuilders() {
        if (fluentDataOrder > explicitDataOrder) {
            getComponent().setData(fluentDataBuilder.build());
        }
        if (fluentOptionsOrder > explicitOptionsOrder) {
            getComponent().setOptions(fluentOptionsBuilder.build());
        }
    }

    private long nextOrder() {
        return ++orderCounter;
    }

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.empty();
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.empty();
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }

    // Override style methods to act directly on the element since ChartJsComponent
    // doesn't implement HasStyle but Component exposes class list via its element.
    @Override
    public C styleNames(String... styleNames) {
        if (styleNames != null) {
            for (String name : styleNames) {
                if (name != null) getComponent().getElement().getClassList().add(name);
            }
        }
        return getConfigurator();
    }

    @Override
    public C styleName(String styleName) {
        if (styleName != null) getComponent().getElement().getClassList().add(styleName);
        return getConfigurator();
    }

    // ── ChartJsConfigurator methods ───────────────────────────────────────────

    @Override
    public C type(ChartType chartType) {
        getComponent().setType(chartType);
        return getConfigurator();
    }

    @Override
    public C data(String dataJson) {
        getComponent().setDataJson(dataJson);
        explicitDataOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C data(ChartJsData data) {
        getComponent().setData(data);
        explicitDataOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C categories(String... categories) {
        fluentDataBuilder.categories(categories);
        fluentDataOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C series(ChartJsDataset series) {
        fluentDataBuilder.series(series);
        fluentDataOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C series(String name, Number... values) {
        fluentDataBuilder.series(ChartJsDataset.create().name(name).values(values).build());
        fluentDataOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C addSeries(ChartJsDataset series) {
        return series(series);
    }

    @Override
    public C addSeries(String name, Number... values) {
        return series(name, values);
    }

    @Override
    public C options(String optionsJson) {
        getComponent().setOptionsJson(optionsJson);
        explicitOptionsOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C options(ChartJsOptions options) {
        getComponent().setOptions(options);
        explicitOptionsOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C title(String title) {
        fluentOptionsBuilder.title(title);
        fluentOptionsOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C chartTitle(String title) {
        return title(title);
    }

    @Override
    public C subtitle(String subtitle) {
        fluentOptionsBuilder.subtitle(subtitle);
        fluentOptionsOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C chartSubTitle(String subtitle) {
        return subtitle(subtitle);
    }

    @Override
    public C responsive(boolean responsive) {
        fluentOptionsBuilder.responsive(responsive);
        fluentOptionsOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C maintainAspectRatio(boolean maintainAspectRatio) {
        fluentOptionsBuilder.maintainAspectRatio(maintainAspectRatio);
        fluentOptionsOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C legendEnabled(boolean enabled) {
        fluentOptionsBuilder.legendEnabled(enabled);
        fluentOptionsOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C tooltipShared(boolean shared) {
        fluentOptionsBuilder.tooltipShared(shared);
        fluentOptionsOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C xAxisTitle(String title) {
        fluentOptionsBuilder.xAxisTitle(title);
        fluentOptionsOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C yAxisTitle(String title) {
        fluentOptionsBuilder.yAxisTitle(title);
        fluentOptionsOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C xAxis(Consumer<ChartJsConfigurator.AxisBuilder> configurator) {
        ObjectUtils.argumentNotNull(configurator, "X axis configurator must be not null");
        configurator.accept(new DefaultAxisBuilder("x"));
        fluentOptionsOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C yAxis(Consumer<ChartJsConfigurator.AxisBuilder> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Y axis configurator must be not null");
        configurator.accept(new DefaultAxisBuilder("y"));
        fluentOptionsOrder = nextOrder();
        return getConfigurator();
    }

    @Override
    public C config(ChartJsConfig config) {
        getComponent().setConfig(config);
        explicitDataOrder = nextOrder();
        explicitOptionsOrder = explicitDataOrder;
        return getConfigurator();
    }

    private class DefaultAxisBuilder implements ChartJsConfigurator.AxisBuilder {

        private final String axis;

        private DefaultAxisBuilder(String axis) {
            this.axis = axis;
        }

        @Override
        public ChartJsConfigurator.AxisBuilder title(String title) {
            if ("x".equals(axis)) {
                xAxisTitle(title);
            } else {
                yAxisTitle(title);
            }
            return this;
        }

        @Override
        public ChartJsConfigurator.AxisBuilder categories(String... categories) {
            if ("x".equals(axis)) {
                AbstractChartJsConfigurator.this.categories(categories);
            } else {
                fluentOptionsBuilder.nestedProperty("scales.y.labels", List.of(categories));
                fluentOptionsOrder = nextOrder();
            }
            return this;
        }
    }
}
