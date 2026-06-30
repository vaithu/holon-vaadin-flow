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
import com.holonplatform.vaadin.flow.components.builders.SheetConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.holonplatform.vaadin.flow.vaadinplus.components.SheetDescription;
import com.holonplatform.vaadin.flow.vaadinplus.components.SheetTitle;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Base {@link SheetConfigurator} implementation.
 *
 * <p>Delegates every configuration call to the wrapped {@link Sheet} component and exposes
 * the standard Holon Platform component hooks ({@code id}, {@code visible}, {@code styleName},
 * {@code width}, etc.) via {@link AbstractComponentConfigurator}.</p>
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractSheetConfigurator<C extends SheetConfigurator<C>>
        extends AbstractComponentConfigurator<Sheet, C>
        implements SheetConfigurator<C> {

    public AbstractSheetConfigurator(Sheet component) {
        super(component);
    }

    // -----------------------------------------------------------------------
    // SheetConfigurator implementation
    // -----------------------------------------------------------------------

    @Override
    public C side(Sheet.Side side) {
        getComponent().setSide(side);
        return getConfigurator();
    }

    @Override
    public C title(SheetTitle title) {
        getComponent().setTitle(title);
        return getConfigurator();
    }

    @Override
    public C title(String text) {
        getComponent().setTitle(text);
        return getConfigurator();
    }

    @Override
    public C title(Localizable localizable) {
        getComponent().setTitle(localizable);
        return getConfigurator();
    }

    @Override
    public C header(com.holonplatform.vaadin.flow.vaadinplus.components.Header header) {
        getComponent().setHeader(header);
        return getConfigurator();
    }

    @Override
    public C description(SheetDescription description) {
        getComponent().setDescription(description);
        return getConfigurator();
    }

    @Override
    public C description(String text) {
        getComponent().setDescription(text);
        return getConfigurator();
    }

    @Override
    public C description(Localizable localizable) {
        getComponent().setDescription(localizable);
        return getConfigurator();
    }

    @Override
    public C content(Component... components) {
        getComponent().setContent(components);
        return getConfigurator();
    }

    @Override
    public C backdropVisible(boolean backdropVisible) {
        getComponent().setBackdropVisible(backdropVisible);
        return getConfigurator();
    }

    @Override
    public C closeOnBackdropClick(boolean closeOnBackdropClick) {
        getComponent().setCloseOnBackdropClick(closeOnBackdropClick);
        return getConfigurator();
    }

    @Override
    public C historyEnabled(boolean historyEnabled) {
        getComponent().setHistoryEnabled(historyEnabled);
        return getConfigurator();
    }

    @Override
    public C fullscreenOnMobile(boolean fullscreen) {
        getComponent().setFullscreenOnMobile(fullscreen);
        return getConfigurator();
    }

    @Override
    public C belowHeader(boolean belowHeader) {
        getComponent().setBelowHeader(belowHeader);
        return getConfigurator();
    }

    @Override
    public C backButton(boolean show) {
        getComponent().setShowBackButton(show);
        return getConfigurator();
    }

    @Override
    public C closeButton(boolean show) {
        getComponent().setShowCloseButton(show);
        return getConfigurator();
    }

    @Override
    public C lazyContent(Supplier<Component[]> supplier) {
        getComponent().setLazyContent(supplier);
        return getConfigurator();
    }

    @Override
    public C onOpen(Runnable onOpen) {
        getComponent().setOnOpen(onOpen);
        return getConfigurator();
    }

    @Override
    public C onClose(Runnable onClose) {
        getComponent().setOnClose(onClose);
        return getConfigurator();
    }

    @Override
    public C footer(com.holonplatform.vaadin.flow.vaadinplus.components.Footer footer) {
        getComponent().setFooter(footer);
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

