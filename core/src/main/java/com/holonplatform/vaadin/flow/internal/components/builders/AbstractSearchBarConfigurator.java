package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.Validator;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.SearchBarConfigurator;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractSearchBarConfigurator<C extends SearchBarConfigurator<C>>
        extends AbstractComponentConfigurator<HorizontalLayout,C>
        implements SearchBarConfigurator<C> {

    private final ValidatableInput<String> searchField;
    private final Button newButton;

    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public AbstractSearchBarConfigurator(HorizontalLayout component) {
        super(component);

        searchField = Components.input
                .string()
                .prefixComponent(new Icon("lumo", "search"))
                .clearButtonVisible(true)
                .blankValuesAsNull(true)
                .emptyValuesAsNull(true)
                .withFocusShortcutKey(Key.KEY_F, KeyModifier.ALT)
                .validatable()
                .withValidator(Validator.notNull())
                .withValidator(Validator.notBlank())
                .withValidator(Validator.min(2))
                .build();

        newButton = Components.button()
                .primary()
                .icon("lumo", "plus")
                .iconAfterText(false)
                .text("New")
                .tooltip("New")
                .withThemeVariants(ButtonVariant.LUMO_SMALL)
                .withClickShortcutKey(Key.KEY_N,KeyModifier.ALT)
                .build();

        Components.configure(component)
                .addAndExpand(searchField, 1)
                .alignItems(FlexComponent.Alignment.BASELINE)

                ;
    }

    @Override
    public C search(ValueHolder.ValueChangeListener<String, ValueHolder.ValueChangeEvent<String>> listener) {
        searchField.addValueChangeListener(listener);
        return getConfigurator();
    }

    @Override
    public C newButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        configurator.accept(ButtonConfigurator.configure(newButton));
        getComponent().add(newButton);
        return getConfigurator();
    }

    @Override
    public C optionsMenuBar(MenuBar menuBar) {
        menuBar.addThemeVariants(MenuBarVariant.LUMO_END_ALIGNED,MenuBarVariant.LUMO_TERTIARY_INLINE);
        getComponent().add(menuBar);
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
        return Optional.empty();
    }


}
