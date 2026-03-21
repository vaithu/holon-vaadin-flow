package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.SplitLayoutConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;

import java.util.Optional;

public abstract class AbstractSplitLayoutConfigurator<C extends SplitLayoutConfigurator<C>>
        extends AbstractComponentConfigurator<SplitLayout, C> implements SplitLayoutConfigurator<C> {
    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public AbstractSplitLayoutConfigurator(SplitLayout component) {
        super(component);
    }

    /**
     * Add given theme variants to the component.
     *
     * @param variants The theme variants to add
     * @return this
     */
    @Override
    public C withThemeVariants(SplitLayoutVariant... variants) {
        getComponent().addThemeVariants(variants);
        return getConfigurator();
    }

    @Override
    public C primaryComponent(Component... components) {
        getComponent().addToPrimary(components);
        return getConfigurator();
    }

    @Override
    public C secondaryComponent(Component... components) {
        getComponent().addToSecondary(components);
        return getConfigurator();
    }

    @Override
    public void toggle(ToggleMode toggleMode) {

        switch (toggleMode) {
            case PRIMARY_VISIBLE_ONLY :
                getComponent().getSecondaryComponent().setVisible(false);
                break;
            case SECONDARY_VISIBLE_ONlY :
                getComponent().getPrimaryComponent().setVisible(false);
                break;
            case BOTH_VISIBLE :
                getComponent().getPrimaryComponent().setVisible(true);
                getComponent().getSecondaryComponent().setVisible(true);
        }
    }

    @Override
    public void remove(Component... components) {
        getComponent().remove(components);
    }

    @Override
    public void removeAll() {
        getComponent().removeAll();
    }

    @Override
    public C orientation(SplitLayout.Orientation orientation) {
        getComponent().setOrientation(orientation);
        return getConfigurator();
    }

    @Override
    public C splitterPosition(double position) {
        getComponent().setSplitterPosition(position);
        return getConfigurator();
    }

    @Override
    public C primaryStyle(String styleName, String value) {
        getComponent().setPrimaryStyle(styleName, value);
        return getConfigurator();
    }

    @Override
    public C secondaryStyle(String styleName, String value) {
        getComponent().setSecondaryStyle(styleName, value);
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
        return Optional.empty();
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
