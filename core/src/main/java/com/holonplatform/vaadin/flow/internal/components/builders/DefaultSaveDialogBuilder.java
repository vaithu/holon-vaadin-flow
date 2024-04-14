package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.DialogBuilder;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;

import java.util.function.Consumer;

public class DefaultSaveDialogBuilder extends AbstractDialogConfigurator<DialogBuilder.SaveDialogBuilder>
        implements DialogBuilder.SaveDialogBuilder {

    private final Button saveButton;
    private final Button denyButton;

    public DefaultSaveDialogBuilder(SaveDialogCallback saveDialogCallback) {

        super();
        ObjectUtils.argumentNotNull(saveDialogCallback, "Save dialog callback must be not null");

        this.saveButton = ButtonBuilder.create()
                .text(Localizable.of("Save", DialogBuilder.DEFAULT_SAVE_BUTTON_MESSAGE_CODE))
                .withClickListener(e -> {
                    saveDialogCallback.onUserAnswer(true, isOkToClose -> {
                        if (isOkToClose) {
                            getComponent().close();
                        } /*else {
                            getComponent().open();
                        }*/

                        System.out.println("Can close the dialog ? " + isOkToClose);
                    } );
                })
                .withThemeVariants(ButtonVariant.LUMO_PRIMARY)
                .withClickShortcutKey(Key.ENTER)
                .build();

        this.denyButton = ButtonBuilder.create()
                .text(Localizable.of("Cancel", DialogBuilder.DEFAULT_DENY_BUTTON_MESSAGE_CODE))
                .withClickListener(e -> {
                    getComponent().close();
//                    saveDialogCallback.onUserAnswer(false,isOkToClose -> getComponent().close());
                })
                .withThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE)
                .build();

        this.denyButton.getStyle().set("margin-right", "auto");

        getComponent().addFooterComponent(this.denyButton);
        getComponent().addFooterComponent(this.saveButton);

        getComponent().setCloseOnEsc(false);
        getComponent().setCloseOnOutsideClick(false);

        // since 5.5.0: set modal by default
        getComponent().setModal(true);
        getComponent().setDraggable(true);

    }


    @Override
    public SaveDialogBuilder saveButtonConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        configurator.accept(ButtonConfigurator.configure(saveButton));
        return getConfigurator();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.holonplatform.vaadin.flow.components.builders.DialogBuilder.
     * QuestionDialogBuilder#denialButtonConfigurator( java.util.function.Consumer)
     */
    @Override
    public SaveDialogBuilder denialButtonConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
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
    protected SaveDialogBuilder getConfigurator() {
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
