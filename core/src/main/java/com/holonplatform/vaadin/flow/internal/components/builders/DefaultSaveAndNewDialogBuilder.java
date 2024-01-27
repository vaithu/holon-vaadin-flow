package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.DialogBuilder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

import java.util.function.Consumer;

public class DefaultSaveAndNewDialogBuilder extends AbstractDialogConfigurator<DialogBuilder.SaveAndNewDialogBuilder> implements DialogBuilder.SaveAndNewDialogBuilder {

    private final Button saveAndNewButton;
    private final Button denyButton;

    public DefaultSaveAndNewDialogBuilder(QuestionDialogCallback questionDialogCallback) {
        super();
        ObjectUtils.argumentNotNull(questionDialogCallback, "SaveAndNew dialog callback must be not null");

        this.saveAndNewButton = ButtonBuilder.create()
                .text(Localizable.of("Save&New", DialogBuilder.DEFAULT_SAVE_NEW_BUTTON_MESSAGE_CODE)).withClickListener(e -> {
                    getComponent().open();
                    questionDialogCallback.onUserAnswer(true);
                })
                .build();

        this.denyButton = ButtonBuilder.create()
                .text(Localizable.of("Cancel", DialogBuilder.DEFAULT_DENY_BUTTON_MESSAGE_CODE)).withClickListener(e -> {
                    getComponent().close();
                    questionDialogCallback.onUserAnswer(false);
                }).build();

        this.denyButton.getStyle().set("margin-right", "auto");

        getComponent().addFooterComponent(this.denyButton);
        getComponent().addFooterComponent(this.saveAndNewButton);

        getComponent().setCloseOnEsc(false);
        getComponent().setCloseOnOutsideClick(false);

        // since 5.5.0: set modal by default
        getComponent().setModal(true);
    }



    @Override
    public SaveAndNewDialogBuilder saveAndNewButtonConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        configurator.accept(ButtonConfigurator.configure(saveAndNewButton));
        return getConfigurator();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.DialogBuilder.
     * QuestionDialogBuilder#denialButtonConfigurator( java.util.function.Consumer)
     */
    @Override
    public SaveAndNewDialogBuilder denialButtonConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        configurator.accept(ButtonConfigurator.configure(denyButton));
        return getConfigurator();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.internal.components.builders.
     * AbstractComponentConfigurator#getConfigurator()
     */
    @Override
    protected SaveAndNewDialogBuilder getConfigurator() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.DialogBuilder#build()
     */
    @Override
    public Dialog build() {
        return getComponent();
    }
}
