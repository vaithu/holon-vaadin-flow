package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.AlertModalConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertAction;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertDescription;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertModal;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertTitle;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

/**
 * Base {@link AlertModalConfigurator} implementation.
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractAlertModalConfigurator<C extends AlertModalConfigurator<C>>
        extends AbstractComponentConfigurator<AlertModal, C>
        implements AlertModalConfigurator<C> {

    public AbstractAlertModalConfigurator(AlertModal component) {
        super(component);
    }

    @Override public C variant(Alert.Variant variant) { getComponent().setVariant(variant); return getConfigurator(); }
    @Override public C icon(Icon icon) { getComponent().setIcon(icon); return getConfigurator(); }
    @Override public C clearIcon() { getComponent().clearIcon(); return getConfigurator(); }
    @Override public C title(AlertTitle title) { getComponent().setTitle(title); return getConfigurator(); }
    @Override public C title(String text) { getComponent().setTitle(text); return getConfigurator(); }
    @Override public C title(Localizable localizable) { getComponent().setTitle(localizable); return getConfigurator(); }
    @Override public C description(AlertDescription description) { getComponent().setDescription(description); return getConfigurator(); }
    @Override public C description(String text) { getComponent().setDescription(text); return getConfigurator(); }
    @Override public C description(Localizable localizable) { getComponent().setDescription(localizable); return getConfigurator(); }
    @Override public C action(AlertAction action) { getComponent().setAction(action); return getConfigurator(); }
    @Override public C action(Component... actions) { getComponent().setAction(actions); return getConfigurator(); }
    @Override public C closeOnEsc(boolean v) { getComponent().setCloseOnEsc(v); return getConfigurator(); }
    @Override public C closeOnOutsideClick(boolean v) { getComponent().setCloseOnOutsideClick(v); return getConfigurator(); }
    @Override public C draggable(boolean v) { getComponent().setDraggable(v); return getConfigurator(); }
    @Override public C resizable(boolean v) { getComponent().setResizable(v); return getConfigurator(); }
    @Override public C withOpenedChangeListener(ComponentEventListener<Dialog.OpenedChangeEvent> l) { getComponent().addOpenedChangeListener(l); return getConfigurator(); }

    @Override protected Optional<HasSize> hasSize() { return Optional.of(getComponent()); }
    @Override protected Optional<HasStyle> hasStyle() { return Optional.of(getComponent()); }
    @Override protected Optional<HasEnabled> hasEnabled() { return Optional.of(getComponent()); }
    @Override protected Optional<HasTooltip> hasTooltip() { return Optional.empty(); }
}

