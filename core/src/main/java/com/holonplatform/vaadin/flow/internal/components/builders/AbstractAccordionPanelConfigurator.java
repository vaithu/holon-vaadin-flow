package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.AccordionConfigurator;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.dom.DomEventListener;

public abstract class AbstractAccordionPanelConfigurator<B extends AccordionConfigurator<B>>

        implements AccordionConfigurator.AccordionPanelBuilder<B> {

    private final B parentBuilder;
    private final AccordionPanel accordionPanel;

    public AbstractAccordionPanelConfigurator(B parentBuilder, AccordionPanel accordionPanel) {
        super();
        ObjectUtils.argumentNotNull(parentBuilder, "Parent builder must be not null");
        ObjectUtils.argumentNotNull(accordionPanel, "AccordionPanel item must be not null");
        this.accordionPanel = accordionPanel;
        this.parentBuilder = parentBuilder;
    }

    @Override
    public B add() {
        parentBuilder.withAccordionPanel(accordionPanel);
        return parentBuilder;
    }

    /**
     * Adds the given components as children of this component.
     *
     * @param components The components to add
     * @return this
     */
    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> add(Component... components) {
        accordionPanel.add(components);
        return this;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> addComponentAsFirst(Component component) {
        accordionPanel.addComponentAsFirst(component);
        return this;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> addComponentAtIndex(int index, Component component) {
        accordionPanel.addComponentAtIndex(index, component);
        return this;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> add(String text) {
        accordionPanel.add(text);
        return this;
    }

    /**
     * Add given theme variants to the component.
     *
     * @param variants The theme variants to add
     * @return this
     */
    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> withThemeVariants(DetailsVariant... variants) {
        accordionPanel.addThemeVariants(variants);
        return this;
    }

    /**
     * Sets the tooltip text using a {@link Localizable} message.
     * <p>
     * The tooltip is set using the <vaadin-tooltip slot="tooltip"> attribute. Browsers typically use the title to show a tooltip
     * when hovering an element
     * <p>
     *
     * @param tooltip Localizable tooltip message (may be null)
     * @return this
     * @see LocalizationProvider
     */
    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> tooltip(Localizable tooltip) {
        DefaultHasTooltipConfigurator<AccordionPanel> tooltipConfigurator = new DefaultHasTooltipConfigurator<>(accordionPanel, tooltip1 -> {
            accordionPanel.setTooltipText(tooltip1);
        });
        tooltipConfigurator.tooltip(tooltip);
        return this;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> tooltipText(String text) {
        accordionPanel.setTooltipText(text);
        return this;
    }

    /**
     * Adds a theme name to this component.
     *
     * @param themeName the theme name to add, not <code>null</code>
     * @return this
     */
    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> withThemeName(String themeName) {
        accordionPanel.addThemeName(themeName);
        return this;
    }

    /**
     * Adds an event listener for the given event type.
     * <p>
     * Event listeners are triggered in the order they are registered.
     * </p>
     *
     * @param eventType the type of event to listen to, not <code>null</code>
     * @param listener  the listener to add, not <code>null</code>
     * @return this
     */
    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> withEventListener(String eventType, DomEventListener listener) {
        accordionPanel.getElement().addEventListener(eventType, listener);
        return this;
    }

    /**
     * Adds an event listener for the given event type and configure a filter.
     * <p>
     * A filter is JavaScript expression that is used for filtering events to this listener. When an event is fired in
     * the browser, the expression is evaluated and an event is sent to the server only if the expression value is
     * <code>true</code>-ish according to JavaScript type coercion rules. The expression is evaluated in a context where
     * <code>element</code> refers to this element and <code>event</code> refers to the fired event. An expression might
     * be e.g. <code>event.button === 0</code> to only forward events triggered by the primary mouse button.
     * </p>
     * <p>
     * Event listeners are triggered in the order they are registered.
     * </p>
     *
     * @param eventType the type of event to listen to, not <code>null</code>
     * @param listener  the listener to add, not <code>null</code>
     * @param filter    the JavaScript filter expression
     * @return this
     */
    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> withEventListener(String eventType, DomEventListener listener, String filter) {
        parentBuilder.withEventListener(eventType, listener, filter);
        return this;
    }

    /**
     * Set whether the component is enabled.
     *
     * @param enabled if <code>false</code> then explicitly disables the object, if <code>true</code> then enables the
     *                object so that its state depends on parent
     * @return this
     */
    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> enabled(boolean enabled) {
        accordionPanel.setEnabled(enabled);
        return this;
    }

    /**
     * Adds a CSS style class names to this component.
     * <p>
     * Multiple styles can be specified as a space-separated list of style names.
     * </p>
     *
     * @param styleName The CSS style class name to be added to the component
     * @return this
     */
    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> styleName(String styleName) {
        accordionPanel.addClassName(styleName);
        return this;
    }

    /**
     * Adds one or more CSS style class names to this component.
     *
     * @param styleNames The CSS style class names to be added to the component
     * @return this
     */
    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> styleNames(String... styleNames) {
        accordionPanel.addClassNames(styleNames);
        return this;
    }
}
