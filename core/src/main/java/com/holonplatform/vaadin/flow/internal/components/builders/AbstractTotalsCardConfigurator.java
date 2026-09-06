/*
 * Copyright 2016-2024 Axioma srl.
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
import com.holonplatform.vaadin.flow.components.builders.TotalsCardConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsCard;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsGauge;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsRow;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Base {@link TotalsCardConfigurator} implementation.
 *
 * <p>Extends {@link AbstractComponentConfigurator} to inherit the standard Holon Platform
 * component lifecycle hooks ({@code id}, {@code visible}, {@code styleName}, {@code width},
 * etc.) and provides TotalsCard-specific configuration logic.</p>
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractTotalsCardConfigurator<C extends TotalsCardConfigurator<C>>
        extends AbstractComponentConfigurator<TotalsCard, C>
        implements TotalsCardConfigurator<C> {

    /**
     * Constructor.
     *
     * @param component the {@link TotalsCard} component instance to configure (not null)
     */
    public AbstractTotalsCardConfigurator(TotalsCard component) {
        super(component);
    }

    // -----------------------------------------------------------------------
    // TotalsCardConfigurator implementation
    // -----------------------------------------------------------------------

    @Override
    public C row(TotalsRow row) {
        getComponent().addRow(row);
        return getConfigurator();
    }

    @Override
    public C row(String label, String value) {
        getComponent().addRow(label, value);
        return getConfigurator();
    }

    @Override
    public C row(String label, String value, TotalsRow.Variant variant) {
        getComponent().addRow(label, value, variant);
        return getConfigurator();
    }

    @Override
    public C row(Localizable label, Localizable value, TotalsRow.Variant variant) {
        getComponent().addRow(label, value, variant);
        return getConfigurator();
    }

    @Override
    public C clearRows() {
        getComponent().clearRows();
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Variant
    // -----------------------------------------------------------------------

    @Override
    public C variant(TotalsCard.Variant variant) {
        getComponent().setVariant(variant);
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Header (eyebrow + editable highlight field)
    // -----------------------------------------------------------------------

    @Override
    public C eyebrow(String text) {
        getComponent().setEyebrow(text);
        return getConfigurator();
    }

    @Override
    public C eyebrow(Localizable text) {
        getComponent().setEyebrow(text);
        return getConfigurator();
    }

    @Override
    public C highlight(String caption, String initialValue, String suffix, Consumer<String> onValueChange) {
        getComponent().setHighlight(caption, initialValue, suffix, onValueChange);
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Gauge
    // -----------------------------------------------------------------------

    @Override
    public C gauge(TotalsGauge gauge) {
        getComponent().setGauge(gauge);
        return getConfigurator();
    }

    @Override
    public C gauge(String amount, String label, double percent) {
        getComponent().setGauge(amount, label, percent);
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Terms (slider, term toggle, note)
    // -----------------------------------------------------------------------

    @Override
    public C slider(String label, int min, int max, int value, IntConsumer onChange) {
        getComponent().setSlider(label, min, max, value, onChange);
        return getConfigurator();
    }

    @Override
    public C toggleGroup(List<String> options, int selectedIndex, IntConsumer onSelectionChange) {
        getComponent().setToggleGroup(options, selectedIndex, onSelectionChange);
        return getConfigurator();
    }

    @Override
    public C termsNote(String text) {
        getComponent().setTermsNote(text);
        return getConfigurator();
    }

    @Override
    public C termsNote(Localizable text) {
        getComponent().setTermsNote(text);
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Actions
    // -----------------------------------------------------------------------

    @Override
    public C primaryAction(String label, Runnable onClick) {
        getComponent().setPrimaryAction(label, onClick);
        return getConfigurator();
    }

    @Override
    public C secondaryAction(String label, Runnable onClick) {
        getComponent().setSecondaryAction(label, onClick);
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // AbstractComponentConfigurator hooks
    // -----------------------------------------------------------------------

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }
}

