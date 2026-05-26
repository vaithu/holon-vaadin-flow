package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.HasDetailsConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

public abstract class AbstractDetailsConfigurator<C extends HasDetailsConfigurator<C>>
        extends AbstractLocalizableComponentConfigurator<Details, C> implements HasDetailsConfigurator<C> {

    public AbstractDetailsConfigurator(Details component) {
        super(component);
    }

    @Override
    public C add(Component... components) {
        getComponent().add(components);
        return getConfigurator();
    }

    @Override
    public C addComponentAsFirst(Component component) {
        getComponent().addComponentAsFirst(component);
        return getConfigurator();
    }

    @Override
    public C addComponentAtIndex(int index, Component component) {
        getComponent().addComponentAtIndex(index, component);
        return getConfigurator();
    }

    @Override
    public C add(String text) {
        getComponent().add(text);
        return getConfigurator();
    }

    @Override
    public C opened(boolean opened) {
        getComponent().setOpened(opened);
        return getConfigurator();
    }

    @Override
    public C summary(Component summary) {
        getComponent().setSummary(summary);
        return getConfigurator();
    }

    @Override
    public C summaryText(String summary) {
        getComponent().setSummaryText(summary);
        return getConfigurator();
    }

    @Override
    public C withThemeVariants(DetailsVariant... variants) {
        getComponent().addThemeVariants(variants);
        return getConfigurator();
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
        return Optional.of(getComponent());
    }
}
