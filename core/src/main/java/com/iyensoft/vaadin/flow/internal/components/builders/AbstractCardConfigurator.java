package com.iyensoft.vaadin.flow.internal.components.builders;


import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.iyensoft.vaadin.flow.components.builders.CardConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.card.CardVariant;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractCardConfigurator<C extends CardConfigurator<C>>
        extends AbstractComponentConfigurator<Card, C>
        implements CardConfigurator<C> {

    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public AbstractCardConfigurator(Card component) {
        super(component);
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
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

    /* -------------------------------------------------
     * Content / children
     * ------------------------------------------------- */

    @Override
    public C add(Collection<Component> components) {
        components.forEach(getComponent()::add);
        return getConfigurator();
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

    /* -------------------------------------------------
     * Footer
     * ------------------------------------------------- */

    @Override
    public C addToFooter(Component... footerComponent) {
        getComponent().addToFooter(footerComponent);
        return getConfigurator();
    }

    @Override
    public Component[] getFooterComponents() {
        return getComponent().getFooterComponents();
    }

    /* -------------------------------------------------
     * Read-only accessors
     * ------------------------------------------------- */

    @Override
    public Optional<String> getAriaRole() {
        return getComponent().getAriaRole();
    }

    @Override
    public Stream<Component> getChildren() {
        return getComponent().getChildren();
    }

    @Override
    public Component getHeader() {
        return getComponent().getHeader();
    }

    @Override
    public Component getHeaderPrefix() {
        return getComponent().getHeaderPrefix();
    }

    @Override
    public Component getHeaderSuffix() {
        return getComponent().getHeaderSuffix();
    }

    @Override
    public Component getMedia() {
        return getComponent().getMedia();
    }

    @Override
    public Component getSubtitle() {
        return getComponent().getSubtitle();
    }

    @Override
    public Component getTitle() {
        return getComponent().getTitle();
    }

    @Override
    public String getTitleAsText() {
        return getComponent().getTitleAsText();
    }

    /* -------------------------------------------------
     * Fluent mutation methods (return C)
     * ------------------------------------------------- */

    @Override
    public C ariaRole(String role) {
        getComponent().setAriaRole(role);
        return getConfigurator();
    }

    @Override
    public C header(Component header) {
        getComponent().setHeader(header);
        return getConfigurator();
    }

    @Override
    public C headerPrefix(Component headerPrefix) {
        getComponent().setHeaderPrefix(headerPrefix);
        return getConfigurator();
    }

    @Override
    public C headerSuffix(Component headerSuffix) {
        getComponent().setHeaderSuffix(headerSuffix);
        return getConfigurator();
    }

    @Override
    public C media(Component media) {
        getComponent().setMedia(media);
        return getConfigurator();
    }

    @Override
    public C subtitle(Component subtitle) {
        getComponent().setSubtitle(subtitle);
        return getConfigurator();
    }

    @Override
    public C title(LabelBuilder<?> labelBuilder) {
        getComponent().setTitle(labelBuilder.build());
        return getConfigurator();
    }

    /* -------------------------------------------------
     * Remove
     * ------------------------------------------------- */

    @Override
    public void remove(Collection<Component> components) {
        components.forEach(getComponent()::remove);
    }

    @Override
    public void removeAll() {
        getComponent().removeAll();
    }

    /* -------------------------------------------------
     * Theme variants
     * ------------------------------------------------- */

    @Override
    public C withThemeVariants(CardVariant... variants) {
        getComponent().addThemeVariants(variants);
        return getConfigurator();
    }


}