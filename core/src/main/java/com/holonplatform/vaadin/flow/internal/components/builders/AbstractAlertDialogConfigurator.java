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
import com.holonplatform.vaadin.flow.components.builders.AlertDialogConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;
import java.util.function.BooleanSupplier;

/**
 * Base {@link AlertDialogConfigurator} implementation for the shadcn/ui + Tailwind UI confirmation dialog.
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractAlertDialogConfigurator<C extends AlertDialogConfigurator<C>>
        extends AbstractComponentConfigurator<AlertDialog, C>
        implements AlertDialogConfigurator<C> {

    public AbstractAlertDialogConfigurator(AlertDialog component) {
        super(component);
    }

    // -- content --
    @Override public C title(String text)             { getComponent().setDialogTitle(text); return getConfigurator(); }
    @Override public C title(Localizable l)           { getComponent().setDialogTitle(l); return getConfigurator(); }
    @Override public C description(String text)       { getComponent().setDialogDescription(text); return getConfigurator(); }
    @Override public C description(Localizable l)     { getComponent().setDialogDescription(l); return getConfigurator(); }

    // -- header icon --
    @Override public C headerIcon(Component icon)                         { getComponent().setHeaderIcon(icon); return getConfigurator(); }
    @Override public C headerIcon(Component icon, Alert.Variant variant)  { getComponent().setHeaderIcon(icon, variant); return getConfigurator(); }

    // -- size --
    @Override public C size(AlertDialog.Size s)       { getComponent().setSize(s); return getConfigurator(); }

    // -- alignment --
    @Override public C alignment(AlertDialog.Alignment a) { getComponent().setAlignment(a); return getConfigurator(); }

    // -- body content --
    @Override public C bodyContent(Component... components) { getComponent().addBodyContent(components); return getConfigurator(); }

    // -- buttons --
    @Override public C cancelText(String text)        { getComponent().setCancelText(text); return getConfigurator(); }
    @Override public C cancelText(Localizable l)      { getComponent().setCancelText(l); return getConfigurator(); }
    @Override public C confirmText(String text)       { getComponent().setConfirmText(text); return getConfigurator(); }
    @Override public C confirmText(Localizable l)     { getComponent().setConfirmText(l); return getConfigurator(); }
    @Override public C onConfirm(Runnable action)     { getComponent().setOnConfirm(action); return getConfigurator(); }
    @Override public C onConfirm(BooleanSupplier cond){ getComponent().setOnConfirm(cond); return getConfigurator(); }
    @Override public C onCancel(Runnable action)      { getComponent().setOnCancel(action); return getConfigurator(); }
    @Override public C variant(Alert.Variant v)       { getComponent().setVariant(v); return getConfigurator(); }
    @Override public C withCancelButton(boolean v)    { getComponent().setCancelButtonVisible(v); return getConfigurator(); }
    @Override public C secondaryAction(String text, Runnable onAction)            { getComponent().setSecondaryAction(text, onAction); return getConfigurator(); }
    @Override public C secondaryAction(Localizable localizable, Runnable onAction){ getComponent().setSecondaryAction(localizable, onAction); return getConfigurator(); }

    // -- dialog behaviour --
    @Override public C closeOnEsc(boolean v)          { getComponent().setCloseOnEsc(v); return getConfigurator(); }
    @Override public C closeOnOutsideClick(boolean v) { getComponent().setCloseOnOutsideClick(v); return getConfigurator(); }
    @Override public C draggable(boolean v)           { getComponent().setDraggable(v); return getConfigurator(); }
    @Override public C resizable(boolean v)           { getComponent().setResizable(v); return getConfigurator(); }
    @Override public C withOpenedChangeListener(ComponentEventListener<Dialog.OpenedChangeEvent> l) { getComponent().addOpenedChangeListener(l); return getConfigurator(); }

    // -- loading --
    @Override public C loading(boolean loading)       { getComponent().setLoading(loading); return getConfigurator(); }

    // -- layout modifiers --
    @Override public C stackedButtons(boolean s)      { getComponent().setStackedButtons(s); return getConfigurator(); }
    @Override public C fullScreenOnMobile(boolean fs) { getComponent().setFullScreenOnMobile(fs); return getConfigurator(); }
    @Override public C footerBackground(boolean e)    { getComponent().setFooterBackground(e); return getConfigurator(); }

    // -- aria role --
    @Override public C alertRole(boolean alertRole)   { getComponent().setAlertRole(alertRole); return getConfigurator(); }

    // -- close button --
    @Override public C withCloseButton(boolean visible) { getComponent().setCloseButtonVisible(visible); return getConfigurator(); }
    @Override public C closeIcon(Component icon)        { getComponent().setCloseButtonIcon(icon); return getConfigurator(); }

    @Override protected Optional<HasSize>    hasSize()    { return Optional.of(getComponent()); }
    @Override protected Optional<HasStyle>   hasStyle()   { return Optional.of(getComponent()); }
    @Override protected Optional<HasEnabled> hasEnabled() { return Optional.of(getComponent()); }
    @Override protected Optional<HasTooltip> hasTooltip() { return Optional.empty(); }
}

