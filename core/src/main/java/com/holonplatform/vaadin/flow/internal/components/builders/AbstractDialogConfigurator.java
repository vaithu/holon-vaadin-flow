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
import com.vaadin.flow.component.dialog.Dialog.DialogResizeEvent;
import com.vaadin.flow.component.dialog.Dialog.OpenedChangeEvent;
import com.vaadin.flow.component.html.Paragraph;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Abstract {@link DialogConfigurator}.
 *
 * @param <C> Concrete configurator type
 * @since 5.2.0
 */
public abstract class AbstractDialogConfigurator<C extends DialogConfigurator<C>>
        extends AbstractLocalizableComponentConfigurator<DefaultDialog, C> implements DialogConfigurator<C> {

    /** Registered trigger component — opens the dialog when clicked. */
    private Component dialogTrigger;

    /** Guards against wiring the same trigger DOM listener more than once. */
    private boolean triggerWired = false;

    public AbstractDialogConfigurator() {
        super(new DefaultDialog());
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

    // -----------------------------------------------------------------------
    // Legacy text() convenience — delegates to withContent() for any call site
    // that still uses the old text(Localizable) API entry point.
    // -----------------------------------------------------------------------

    @Override
    public C text(Localizable text) {
        return withContent(text);
    }

    @Override
    public C text(String text) {
        return withContent(text);
    }

    // -----------------------------------------------------------------------
    // Header
    // -----------------------------------------------------------------------

    @Override
    public C withTitle(String text) {
        getComponent().setDialogTitle(text);
        return getConfigurator();
    }

    @Override
    public C withTitle(Localizable localizable) {
        getComponent().setDialogTitle(localizable);
        return getConfigurator();
    }

    @Override
    public C withDescription(String text) {
        getComponent().setDialogDescription(text);
        return getConfigurator();
    }

    @Override
    public C withDescription(Localizable localizable) {
        getComponent().setDialogDescription(localizable);
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Lifecycle callbacks
    // -----------------------------------------------------------------------

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
    public C onClose(BooleanSupplier onClose) {
        getComponent().setOnClose(onClose);
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Body content
    // -----------------------------------------------------------------------

    @Override
    public C withComponent(Component component) {
        getComponent().addContentComponent(component);
        return getConfigurator();
    }

    @Override
    public C withContent(Component... components) {
        getComponent().addContentComponent(components);
        return getConfigurator();
    }

    @Override
    public C withContent(String message) {
        Paragraph p = new Paragraph(message);
        p.addClassName("h-dialog__text");
        return withContent(p);
    }

    /**
     * Adds a localised text paragraph to the dialog body.
     * {@code message} is the fallback text; {@code messageCode} is the i18n key resolved
     * via {@link com.holonplatform.vaadin.flow.i18n.LocalizationProvider}.
     */
    @Override
    public C withContent(String message, String messageCode) {
        return withContent(Localizable.of(message, messageCode));
    }

    @Override
    public C withContent(Localizable message) {
        String resolved = com.holonplatform.vaadin.flow.i18n.LocalizationProvider
                .localize(message).orElse(message.getMessage());
        Paragraph p = new Paragraph(resolved);
        p.addClassName("h-dialog__text");
        return withContent(p);
    }

    // -----------------------------------------------------------------------
    // Footer — clean DSL
    // -----------------------------------------------------------------------

    /**
     * Adds a primary action button (label + click handler) to the footer.
     * Cancel button must be added separately via {@link #withCancelButton()}.
     */
    @Override
    public C withActionButton(String text,
                               ClickEventListener<Button, ClickEvent<Button>> onClick) {
        ObjectUtils.argumentNotNull(onClick, "Click listener must be not null");
        Button btn = ButtonBuilder.create()
                .text(text)
                .styleName("h-dialog__action-btn")
                .withClickListener(onClick)
                .withClickListener(e -> getComponent().attemptClose())
                .build();
        return withFooter(btn);
    }

    @Override
    public C withActionButton(String text, String textCode,
                               ClickEventListener<Button, ClickEvent<Button>> onClick) {
        ObjectUtils.argumentNotNull(onClick, "Click listener must be not null");
        Button btn = ButtonBuilder.create()
                .text(Localizable.of(text, textCode))
                .styleName("h-dialog__action-btn")
                .withClickListener(onClick)
                .withClickListener(e -> getComponent().attemptClose())
                .build();
        return withFooter(btn);
    }

    @Override
    public C withActionButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        Button btn = ButtonBuilder.create()
                .styleName("h-dialog__action-btn")
                .withClickListener(e -> getComponent().attemptClose())
                .build();
        configurator.accept(ButtonConfigurator.configure(btn));
        return withFooter(btn);
    }

    @Override
    public C withCancelButton() {
        return withFooter(createCancelButton());
    }

    @Override
    public C withCancelButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        Button cancelBtn = createCancelButton();
        configurator.accept(ButtonConfigurator.configure(cancelBtn));
        return withFooter(cancelBtn);
    }

    /** Low-level footer escape hatch — adds components directly to the footer slot. */
    @Override
    public C withFooter(Component... components) {
        getComponent().getFooter().add(components);
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Behaviour
    // -----------------------------------------------------------------------

    @Override
    public C makeDialogResponsive() {
        getComponent().makeDialogResponsive();
        return getConfigurator();
    }

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
        getComponent().setModality(modal ? ModalityMode.STRICT : ModalityMode.MODELESS);
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Variant-aware action buttons
    // -----------------------------------------------------------------------

    @Override
    public C withActionButton(String text, DialogConfigurator.ActionVariant variant,
                               ClickEventListener<Button, ClickEvent<Button>> onClick) {
        ObjectUtils.argumentNotNull(onClick, "Click listener must be not null");
        Button btn = ButtonBuilder.create()
                .text(text)
                .styleName(actionButtonStyle(variant))
                .withClickListener(onClick)
                .withClickListener(e -> getComponent().attemptClose())
                .build();
        return withFooter(btn);
    }

    @Override
    public C withActionButton(String text, String textCode,
                               DialogConfigurator.ActionVariant variant,
                               ClickEventListener<Button, ClickEvent<Button>> onClick) {
        ObjectUtils.argumentNotNull(onClick, "Click listener must be not null");
        Button btn = ButtonBuilder.create()
                .text(Localizable.of(text, textCode))
                .styleName(actionButtonStyle(variant))
                .withClickListener(onClick)
                .withClickListener(e -> getComponent().attemptClose())
                .build();
        return withFooter(btn);
    }

    @Override
    public C withActionButton(DialogConfigurator.ActionVariant variant,
                               Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        Button btn = ButtonBuilder.create()
                .styleName(actionButtonStyle(variant))
                .withClickListener(e -> getComponent().attemptClose())
                .build();
        configurator.accept(ButtonConfigurator.configure(btn));
        return withFooter(btn);
    }

    // -----------------------------------------------------------------------
    // Close button control  (shadcn/ui DialogClose)
    // -----------------------------------------------------------------------

    @Override
    public C withCloseButton(boolean visible) {
        getComponent().setCloseButtonVisible(visible);
        return getConfigurator();
    }

    @Override
    public C withCloseButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        getComponent().configureCloseButton(btn ->
                configurator.accept(ButtonConfigurator.configure(btn)));
        return getConfigurator();
    }

    @Override
    public C closeIcon(Component icon) {
        ObjectUtils.argumentNotNull(icon, "Icon must be not null");
        getComponent().setCloseButtonIcon(icon);
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Size variants  (shadcn/ui size prop + Tailwind Plus max-w-*)
    // -----------------------------------------------------------------------

    @Override
    public C withSize(DialogConfigurator.DialogSize size) {
        ObjectUtils.argumentNotNull(size, "DialogSize must be not null");
        getComponent().setDialogSize(size);
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Icon header  (Tailwind Plus "with icon" modal pattern)
    // -----------------------------------------------------------------------

    @Override
    public C withHeaderIcon(Component icon, DialogConfigurator.IconVariant variant) {
        ObjectUtils.argumentNotNull(icon, "Icon must be not null");
        ObjectUtils.argumentNotNull(variant, "IconVariant must be not null");
        getComponent().addHeaderIcon(icon, variant.getCssClass());
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Centered variant  (Tailwind Plus "centred modal" pattern)
    // -----------------------------------------------------------------------

    @Override
    public C centered() {
        getComponent().setCentered(true);
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // Trigger (DialogTrigger equivalent)
    // -----------------------------------------------------------------------

    @Override
    public C withTrigger(Component trigger) {
        ObjectUtils.argumentNotNull(trigger, "Trigger must be not null");
        this.dialogTrigger = trigger;
        this.triggerWired = false; // reset so the new trigger gets wired on buildTriggered()
        return getConfigurator();
    }

    @Override
    public C withTrigger(String text) {
        ObjectUtils.argumentNotNull(text, "Trigger text must be not null");
        return withTrigger(ButtonBuilder.create()
                .text(text)
                .styleName("h-dialog__trigger")
                .build());
    }

    @Override
    public C withTrigger(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        Button btn = ButtonBuilder.create()
                .styleName("h-dialog__trigger")
                .build();
        configurator.accept(ButtonConfigurator.configure(btn));
        return withTrigger(btn);
    }

    /**
     * Builds the dialog, wires the click listener on the registered trigger and returns it.
     * Satisfies the {@link com.holonplatform.vaadin.flow.components.builders.DialogBuilder#buildTriggered()}
     * contract — concrete builders inherit this via the class hierarchy.
     *
     * @return the trigger component with the dialog wired to it
     * @throws IllegalStateException if no trigger has been registered
     */
    public Component buildTriggered() {
        if (dialogTrigger == null) {
            throw new IllegalStateException(
                    "No trigger configured. Call withTrigger() before buildTriggered().");
        }
        if (!triggerWired) {
            dialogTrigger.getElement().addEventListener("click", e -> getComponent().open());
            triggerWired = true;
        }
        return dialogTrigger;
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    /**
     * Builds the CSS class string for an action button given its variant.
     * Always includes the base {@code h-dialog__action-btn} class.
     */
    private static String actionButtonStyle(DialogConfigurator.ActionVariant variant) {
        if (variant == null || variant == DialogConfigurator.ActionVariant.DEFAULT) {
            return "h-dialog__action-btn";
        }
        return "h-dialog__action-btn " + variant.getCssModifier();
    }

    /**
     * Creates a pre-styled "Cancel" button.
     * The button closes the dialog via {@link DefaultDialog#attemptClose()} so that any
     * registered close guard is respected.
     */
    private Button createCancelButton() {
        return ButtonBuilder.create()
                .text(Localizable.of("Cancel", DialogBuilder.DEFAULT_DENY_BUTTON_MESSAGE_CODE))
                .styleName("h-dialog__cancel-btn")
                .withClickListener(e -> getComponent().attemptClose())
                .build();
    }
}
