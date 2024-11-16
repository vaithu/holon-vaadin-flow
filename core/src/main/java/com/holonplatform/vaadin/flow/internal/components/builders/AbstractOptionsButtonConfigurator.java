package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasOptionsButtonConfigurator;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractOptionsButtonConfigurator<C extends HasOptionsButtonConfigurator<C>>
        extends AbstractComponentConfigurator<Button, C >
        implements HasOptionsButtonConfigurator<C> {

    private Button optionsBtn;

    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public AbstractOptionsButtonConfigurator(Button component) {
        super(component);
        this.optionsBtn = component;
    }

    public AbstractOptionsButtonConfigurator() {
        super(getOptionsBtn());

    }

    @Override
    public C optionsButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        optionsBtn = getOptionsBtn();
        configurator.accept(ButtonConfigurator.configure(optionsBtn));
        return getConfigurator();
    }

    private static Button getOptionsBtn() {
        return Components.button()
                .tertiaryInline()
                .icon(VaadinIcon.ELLIPSIS_DOTS_V)
                .tooltipText("Options")
                .withFocusShortcutKey(Key.KEY_O, KeyModifier.ALT)
                .build();
    }

    /**
     * If the component supports {@link HasSize}, return the component as {@link HasSize}.
     *
     * @return Optional component as {@link HasSize}, if supported
     */
    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    /**
     * If the component supports {@link HasStyle}, return the component as {@link HasStyle}.
     *
     * @return Optional component as {@link HasStyle}, if supported
     */
    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    /**
     * If the component supports {@link HasEnabled}, return the component as {@link HasEnabled}.
     *
     * @return Optional component as {@link HasEnabled}, if supported
     */
    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
    }

    /**
     * If the component supports {@link HasTooltip}, return the component as {@link HasTooltip}.
     *
     * @return Optional component as {@link HasTooltip}, if supported
     */
    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.of(getComponent());
    }
}
