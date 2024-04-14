package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.TitleConfigurator;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.components.DefaultTitle;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

public abstract class AbstractTitleConfigurator<C extends TitleConfigurator<C>>
        extends AbstractComponentConfigurator<HorizontalLayout, C>
        implements TitleConfigurator<C> {

    private final H4 h4;
    private DefaultTitle defaultTitle;
    private DefaultHasTextConfigurator textConfigurator;




    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public AbstractTitleConfigurator(HorizontalLayout component) {
        super(component);
        h4 = new H4();
        defaultTitle = new DefaultTitle(h4);
        textConfigurator = new DefaultHasTextConfigurator(h4);

        getComponent().setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);


        getComponent().add(
                h4

        );

    }

    /**
     * Adds the given components as children of this component.
     *
     * @param components The components to add
     * @return this
     */
    @Override
    public C add(Component... components) {
        getComponent().add(components);
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


    @Override
    public C border() {
        border(UIUtils.borderStyles());
        return getConfigurator();
    }

    @Override
    public C border(String... styles) {
        getComponent().addClassNames(styles);
        return getConfigurator();
    }

    @Override
    public void removeBorder() {
        getComponent().removeClassNames(UIUtils.borderStyles());
    }

    @Override
    public C withoutBorder() {
        removeBorder();
        return getConfigurator();
    }

    /**
     * Sets the text content using a {@link Localizable} message.
     * <p>
     * The text value is interpred as <em>plain text</em> and the HTML markup is not supported.
     * </p>
     * <p>
     * A <code>null</code> value is interpreted as an empty text.
     * </p>
     *
     * @param text Localizable text content message (may be null)
     * @return this
     * @see LocalizationProvider
     */
    @Override
    public C text(Localizable text) {
        textConfigurator.text(text);
        return getConfigurator();
    }
}
