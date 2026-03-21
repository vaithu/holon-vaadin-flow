package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasCloseButtonConfigurator;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.theme.lumo.LumoIcon;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractCloseButtonConfigurator<C extends HasCloseButtonConfigurator<C> & ButtonConfigurator<C>>
    extends AbstractButtonConfigurator<C>
        implements HasCloseButtonConfigurator<C> {

    private Button closeBtn;

    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public AbstractCloseButtonConfigurator(Button component) {
        super(component);
        this.closeBtn = component;
    }

    public AbstractCloseButtonConfigurator() {
        super(getCloseBtn());

    }

    private static Button getCloseBtn() {
        return Components.button()
                .tertiaryInline()
                .icon(LumoIcon.CROSS.create())
                .tooltipText("Close")
                .withFocusShortcutKey(Key.KEY_C, KeyModifier.ALT)
                .build();
    }

    @Override
    public C closeButton(Consumer<BaseButtonConfigurator> configurator) {
        configurator.accept(ButtonConfigurator.configure(closeBtn));
        return getConfigurator();
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
