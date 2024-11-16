package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.FlexBoxLayout;
import com.holonplatform.vaadin.flow.components.builders.FlexBoxLayoutConfigurator;
import com.holonplatform.vaadin.flow.components.css.BorderRadius;
import com.holonplatform.vaadin.flow.components.css.BoxSizing;
import com.holonplatform.vaadin.flow.components.css.Size;
import com.holonplatform.vaadin.flow.internal.lumo.Display;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

public abstract class AbstractFlexBoxLayoutConfigurator<C extends FlexBoxLayoutConfigurator<C>>
        extends AbstractComponentConfigurator<FlexBoxLayout, C> implements FlexBoxLayoutConfigurator<C> {

    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public AbstractFlexBoxLayoutConfigurator(FlexBoxLayout component) {
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
    public C backgroundColor(String value) {
        getComponent().setBackgroundColor(value);
        return getConfigurator();
    }

    @Override
    public C backgroundColor(String value, String theme) {
        getComponent().setBackgroundColor(value, theme);
        return getConfigurator();
    }

    @Override
    public C borderRadius(BorderRadius radius) {
        getComponent().setBorderRadius(radius);
        return getConfigurator();
    }

    @Override
    public C boxSizing(BoxSizing sizing) {
        getComponent().setBoxSizing(sizing);
        return getConfigurator();
    }

    @Override
    public C display(Display display) {
        getComponent().setDisplay(display);
        return getConfigurator();
    }

    @Override
    public C flex(String value, Component... components) {
        getComponent().setFlex(value, components);
        return getConfigurator();
    }

    @Override
    public C flexBasis(String value, Component... components) {
        getComponent().setFlexBasis(value, components);
        return getConfigurator();
    }

    @Override
    public C flexShrink(String value, Component... components) {
        getComponent().setFlexShrink(value, components);
        return getConfigurator();
    }

    @Override
    public C margin(Size... sizes) {
        getComponent().setMargin(sizes);
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
