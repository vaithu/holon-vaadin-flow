/*
 * Copyright 2016-2026 Axioma srl.
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
import com.holonplatform.vaadin.flow.components.builders.HighlightConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Highlight;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.iyensoft.vaadin.flow.enums.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

/**
 * Base {@link HighlightConfigurator} implementation.
 *
 * <p>Extends {@link AbstractComponentConfigurator} to inherit the standard Holon Platform
 * component lifecycle hooks ({@code id}, {@code visible}, {@code className}, {@code width},
 * etc.) and provides {@link Highlight}-specific configuration logic.</p>
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractHighlightConfigurator<C extends HighlightConfigurator<C>>
        extends AbstractComponentConfigurator<Highlight, C>
        implements HighlightConfigurator<C> {

    public AbstractHighlightConfigurator(Highlight component) {
        super(component);
    }

    // ── HighlightConfigurator implementation ──────────────────────────────────

    @Override
    public C accentColor(Highlight.AccentColor color) {
        getComponent().setAccentColor(color);
        return getConfigurator();
    }

    @Override
    public C valueFirst(boolean valueFirst) {
        getComponent().setValueFirst(valueFirst);
        return getConfigurator();
    }

    @Override
    public C heading(String text) {
        getComponent().setHeading(text);
        return getConfigurator();
    }

    @Override
    public C heading(Localizable localizable) {
        getComponent().setHeading(localizable);
        return getConfigurator();
    }

    @Override
    public C headingLevel(HeadingLevel level) {
        getComponent().setHeadingLevel(level);
        return getConfigurator();
    }

    @Override
    public C subheading(String text) {
        getComponent().setSubheading(text);
        return getConfigurator();
    }

    @Override
    public C subheading(Localizable localizable) {
        getComponent().setSubheading(localizable);
        return getConfigurator();
    }

    @Override
    public C value(String text) {
        getComponent().setValue(text);
        return getConfigurator();
    }

    @Override
    public C value(Localizable localizable) {
        getComponent().setValue(localizable);
        return getConfigurator();
    }

    @Override
    public C valueFontSize(Font.Size fontSize) {
        getComponent().setValueFontSize(fontSize);
        return getConfigurator();
    }

    @Override
    public C inlineMetric(Component... components) {
        getComponent().setInlineMetric(components);
        return getConfigurator();
    }

    @Override
    public C details(Component... components) {
        getComponent().setDetails(components);
        return getConfigurator();
    }

    @Override
    public C prefix(Component... components) {
        getComponent().setPrefix(components);
        return getConfigurator();
    }

    @Override
    public C suffix(Component... components) {
        getComponent().setSuffix(components);
        return getConfigurator();
    }

    @Override
    public C cardHeader(Component icon, Component action) {
        getComponent().setCardHeader(icon, action);
        return getConfigurator();
    }

    @Override
    public C progress(double percent) {
        getComponent().setProgress(percent);
        return getConfigurator();
    }

    @Override
    public C progressIndeterminate(boolean indeterminate) {
        getComponent().setProgressIndeterminate(indeterminate);
        return getConfigurator();
    }

    @Override
    public C progressLabel(String label) {
        getComponent().setProgressLabel(label);
        return getConfigurator();
    }

    @Override
    public C progressLabel(Localizable localizable) {
        getComponent().setProgressLabel(localizable);
        return getConfigurator();
    }

    @Override
    public C sparkline(Component chart) {
        getComponent().setSparkline(chart);
        return getConfigurator();
    }

    @Override
    public C ariaLabel(String label) {
        getComponent().setAriaLabel(label);
        return getConfigurator();
    }

    @Override
    public C ariaLabel(Localizable localizable) {
        // String overload from HasAriaLabel resolved immediately; Localizable re-resolves on attach
        getComponent().setAriaLabel(localizable);
        return getConfigurator();
    }

    // ── AbstractComponentConfigurator hooks ───────────────────────────────────

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




