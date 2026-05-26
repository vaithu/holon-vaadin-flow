package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultAlertModalConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertAction;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertDescription;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertModal;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertTitle;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;

/**
 * Configurator for {@link AlertModal} components.
 *
 * @param <C> Concrete configurator type
 * @see AlertModalBuilder
 */
public interface AlertModalConfigurator<C extends AlertModalConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    C variant(Alert.Variant variant);

    C icon(Icon icon);

    C clearIcon();

    C title(AlertTitle title);

    C title(String text);

    C title(Localizable localizable);

    C description(AlertDescription description);

    C description(String text);

    C description(Localizable localizable);

    C action(AlertAction action);

    C action(Component... actions);

    C closeOnEsc(boolean closeOnEsc);

    C closeOnOutsideClick(boolean closeOnOutsideClick);

    C draggable(boolean draggable);

    C resizable(boolean resizable);

    C withOpenedChangeListener(ComponentEventListener<Dialog.OpenedChangeEvent> listener);

    static BaseAlertModalConfigurator configure(AlertModal modal) {
        return new DefaultAlertModalConfigurator(modal);
    }

    interface BaseAlertModalConfigurator extends AlertModalConfigurator<BaseAlertModalConfigurator> {
    }
}

