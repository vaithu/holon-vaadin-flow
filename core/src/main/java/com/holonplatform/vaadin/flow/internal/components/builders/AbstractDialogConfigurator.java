/*
 * Copyright 2016-2018 Axioma srl.
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
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.*;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.internal.components.DefaultDialog;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog.DialogResizeEvent;
import com.vaadin.flow.component.dialog.Dialog.OpenedChangeEvent;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Abstract {@link DialogConfigurator}.
 *
 * @param <C> Concrete configurator type
 * @since 5.2.0
 */
public abstract class AbstractDialogConfigurator<C extends DialogConfigurator<C>>
        extends AbstractLocalizableComponentConfigurator<DefaultDialog, C> implements DialogConfigurator<C> {

    private final MessageLocalizationSupportConfigurator<DefaultDialog> messageConfigurator;

    public AbstractDialogConfigurator() {
        super(new DefaultDialog());

        this.messageConfigurator = new MessageLocalizationSupportConfigurator<>(getComponent(),
                text -> getComponent().setMessage(text), this);
        getComponent().addThemeVariants(DialogVariant.LUMO_NO_PADDING);
    }

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

    /*
     * (non-Javadoc)
     *
     * @see
     * com.holonplatform.vaadin.flow.components.builders.HasTextConfigurator#text(
     * com.holonplatform.core.i18n. Localizable)
     */
    @Override
    public C text(Localizable text) {
        messageConfigurator.setMessage(text);
        return getConfigurator();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.DialogConfigurator#
     * withComponent(com.vaadin.flow.component. Component)
     */
    @Override
    public C withComponent(Component component) {
        getComponent().addContentComponent(component);
        return getConfigurator();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.DialogConfigurator#
     * withToolbarComponent(com.vaadin.flow. component.Component)
     */
    @Override
    public C withToolbarComponent(Component component) {
        getComponent().addFooterComponent(component);
        return getConfigurator();
    }

    @Override
    public C makeDialogResponsive() {
        getComponent().makeDialogResponsive();
        return getConfigurator();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.DialogConfigurator#
     * withOpenedChangeListener(com.vaadin.flow. component.ComponentEventListener)
     */
    @Override
    public C withOpenedChangeListener(ComponentEventListener<OpenedChangeEvent> listener) {
        ObjectUtils.argumentNotNull(listener, "Event listener must be not null");
        getComponent().addOpenedChangeListener(listener);
        return getConfigurator();
    }

    @Override
    public C withResizeListener(ComponentEventListener<DialogResizeEvent> listener) {
        ObjectUtils.argumentNotNull(listener, "Event listener must be not null");
        getComponent().addResizeListener(listener);
        return getConfigurator();
    }

    @Override
    public C resizable(boolean resizable) {
        getComponent().setResizable(resizable);
        return getConfigurator();
    }

    @Override
    public C draggable(boolean draggable) {
        getComponent().setDraggable(draggable);
        return getConfigurator();
    }

    @Override
    public C modal(boolean modal) {
        getComponent().setModal(modal);
        return getConfigurator();
    }

    @Override
    public C withHeader(String title, Component component) {
        getComponent().setHeaderTitle(title);
        getComponent().add(component);
        return getConfigurator();
    }

    @Override
    public C withHeader(String title) {
        getComponent().setHeaderTitle(title);
        return getConfigurator();
    }

    @Override
    public C withFooter(boolean showCancelBtn, Component... components) {
        if (showCancelBtn) {
            getComponent().addFooterComponent(createCancelBtn());
        }
        return withFooter(components);
    }

    private Button createCancelBtn() {
        Button cancelButton = new Button("Cancel", (e) -> getComponent().close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        addFooterRightComponent(cancelButton);
        return cancelButton;
    }


    private void addFooterRightComponent(Component component) {
        component.getStyle().set("margin-right", "auto");
        getComponent().getFooter().add(component);
    }

    @Override
    public C withFooter(String btnText, ClickEventListener<Button, ClickEvent<Button>> buttonClickEvent) {
        Button button = ButtonBuilder.create()
                .text(btnText)
//				.withThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY)
                .styleNames(LumoUtility.AlignSelf.END)
                .withClickListener(buttonClickEvent)
                .withClickListener(event -> getComponent().close())
                .build();
        return withFooter(true, button);
    }

    @Override
    public C withFooter(String btnText, String btnTextCode, ClickEventListener<Button,
            ClickEvent<Button>> buttonClickEvent) {
        Button button = ButtonBuilder.create()
                .text(Localizable.of(btnText, btnTextCode))
                .withClickListener(buttonClickEvent)
                .withClickListener(event -> getComponent().close())
//				.withThemeVariants(ButtonVariant.LUMO_ERROR,ButtonVariant.LUMO_PRIMARY)
                .styleNames(LumoUtility.AlignSelf.END)
                .build();
        return withFooter(true, button);
    }

    @Override
    public C withFooter(boolean showCancelBtn, Consumer<ButtonConfigurator.BaseButtonConfigurator> deleteConfigurator) {
        ObjectUtils.argumentNotNull(deleteConfigurator, "Configurator must be not null");
        if (showCancelBtn) {
            deleteConfigurator.accept(ButtonConfigurator.configure(createCancelBtn()));
            return withFooter(deleteConfigurator);
        }
        return getConfigurator();
    }

    private Button createDeleteBtn() {
        return ButtonBuilder.create()
                .text(Localizable.of("Delete", DialogBuilder.DEFAULT_DELETE_BUTTON_MESSAGE_CODE))
                .withThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY)
                .styleNames(LumoUtility.AlignSelf.END)
                .build();
    }

    @Override
    public C withFooter(Consumer<ButtonConfigurator.BaseButtonConfigurator> cancelConfigurator) {
        ObjectUtils.argumentNotNull(cancelConfigurator, "Configurator must be not null");
        Button button = ButtonBuilder.create()
                .withClickListener(event -> getComponent().close())
                .build();
        cancelConfigurator.accept(ButtonConfigurator.configure(button));
        return withFooter(button);
    }

    @Override
    public C withFooter(Consumer<ButtonConfigurator.BaseButtonConfigurator> cancelConfigurator, Component... components) {
        ObjectUtils.argumentNotNull(cancelConfigurator, "Configurator must be not null");
        Button cancelButton = ButtonBuilder.create()
                .text(Localizable.of("Cancel", DialogBuilder.DEFAULT_DENY_BUTTON_MESSAGE_CODE)).withClickListener(e -> {
                    getComponent().close();
                })
                .withThemeVariants(ButtonVariant.LUMO_TERTIARY)
                .styleNames(LumoUtility.AlignSelf.START)
                .build();

        cancelConfigurator.accept(ButtonConfigurator.configure(cancelButton));
        addFooterRightComponent(cancelButton);
        return withFooter(components);
    }

    @Override
    public C withContent(Component... components) {
        getComponent().addContentComponent(components);
        return getConfigurator();
    }

    @Override
    public C withContent(String message) {
        Paragraph paragraph = LabelBuilder.paragraph()
                .text(message)
                .styleNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Padding.Top.NONE,
                        LumoUtility.Padding.Bottom.NONE)
                .build();
        return withContent(paragraph);
    }

    @Override
    public C withContent(String message, String messageCode) {
        Paragraph paragraph = LabelBuilder.paragraph()
                .text(message, messageCode)
                .styleNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Padding.Top.NONE,
                        LumoUtility.Padding.Bottom.NONE)
                .build();
        return withContent(paragraph);
    }


    @Override
    public C withContent(Localizable message) {
        Paragraph paragraph = LabelBuilder.paragraph()
                .text(message)
                .styleNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Padding.Top.NONE,
                        LumoUtility.Padding.Bottom.NONE)
                .build();
        return withContent(paragraph);
    }

    @Override
    public C withFooter(Component... components) {
        getComponent().getFooter().add(components);
        return getConfigurator();
    }
}
