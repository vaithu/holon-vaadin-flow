package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.TabSheetConfigurator;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;

import java.util.Optional;

public abstract class AbstractTabSheetConfigurator<C extends TabSheetConfigurator<C>>
        extends AbstractLocalizableComponentConfigurator<TabSheet, C> implements TabSheetConfigurator<C> {

    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public AbstractTabSheetConfigurator(TabSheet component) {
        super(component);
    }

    @Override
    public C withSelectedChangeListener(ComponentEventListener<TabSheet.SelectedChangeEvent> listener) {
        getComponent().addSelectedChangeListener(listener);
        return getConfigurator();
    }

    @Override
    public C withTab(Component tabContent, Component content) {
        getComponent().add(tabContent, content);
        return getConfigurator();
    }

    @Override
    public C withTab(Tab tab, Component content) {
        getComponent().add(tab, content);
        return getConfigurator();
    }

    @Override
    public C withTab(Tab tab, Component content, int position) {
        getComponent().add(tab, content, position);
        return getConfigurator();
    }

    @Override
    public C withTab(String tabText, Component content) {
        getComponent().add(tabText, content);
        return getConfigurator();
    }

    @Override
    public C prefixComponent(Component component) {
        getComponent().setPrefixComponent(component);
        return getConfigurator();
    }

    @Override
    public C suffixComponent(Component component) {
        getComponent().setSuffixComponent(component);
        return getConfigurator();
    }

    @Override
    public C bordered() {
        getComponent().addThemeVariants(TabSheetVariant.LUMO_BORDERED);
        return getConfigurator();
    }

    /**
     * Add given theme variants to the component.
     *
     * @param variants The theme variants to add
     * @return this
     */
    @Override
    public C withThemeVariants(TabSheetVariant... variants) {
        return getConfigurator();
    }

    /**
     * If the component supports {@link HasSize}, return the component as {@link HasSize}.
     *
     * @return Optional component as {@link HasSize}, if supported
     */
    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.empty();
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
