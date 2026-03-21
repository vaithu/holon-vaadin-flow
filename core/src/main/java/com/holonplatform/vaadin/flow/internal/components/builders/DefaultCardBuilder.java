package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.CardGridConfigurator;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

public class DefaultCardBuilder<B extends  CardGridConfigurator<B>>
        implements CardGridConfigurator.CardBuilder<B> {

    private final B parentBuilder;
    private final Div card;
    private final Div content;

    final List<String> cardStyles = List.of(LumoUtility.Border.ALL,
            LumoUtility.BorderColor.CONTRAST_10,
            LumoUtility.BorderRadius.SMALL,
            LumoUtility.BoxShadow.SMALL,LumoUtility.Margin.SMALL);
    private  H4 title;

    public DefaultCardBuilder(B parentBuilder, Div content) {
        this.parentBuilder = parentBuilder;
        this.content = content;
        card = new Div(this.content);
        initialize();

    }

    public <C extends CardGridConfigurator<C>> DefaultCardBuilder(B parentBuilder, String titleText, Component... components) {
        this.parentBuilder = parentBuilder;
        title = new H4(titleText);
        title.addClassNames(UIUtils.getTitleStyles());
        this.content = new Div();
        this.content.add(components);
        card = new Div();
        card.addComponentAsFirst(title);
        card.add(this.content);
        initialize();
    }

    private void initialize() {
        parentBuilder._addToParent(card);

        card.addClassNames(LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN);

        this.content.addClassNames(LumoUtility.Flex.GROW,
                LumoUtility.Padding.SMALL);

        cardStyle(true);

    }

    public DefaultCardBuilder(B parentBuilder, Component... components) {
        this.parentBuilder = parentBuilder;
        this.content = new Div(components);
        card = new Div(this.content);
        initialize();
    }

    @Override
    public CardGridConfigurator.CardBuilder<B> cardStyle(boolean enabled) {
        if (enabled) {
            cardStyles.forEach(s -> card.addClassName(s));
        } else {
            cardStyles.forEach(s -> card.removeClassName(s));
        }
        return this;
    }

    @Override
    public CardGridConfigurator.CardBuilder<B> largeBorder() {
        card.addClassName(LumoUtility.BorderRadius.LARGE);
        return this;
    }

    @Override
    public CardGridConfigurator.CardBuilder<B> titleColor(String... styles) {
        ObjectUtils.argumentNotNull(title, "Title must not be null. You need to set already before trying to set color of it");
        title.removeClassName(LumoUtility.TextColor.PRIMARY);
        title.addClassNames(styles);
        return this;
    }

    @Override
    public B add() {
        return parentBuilder;
    }

    @Override
    public Div build() {
        return card;
    }

    @Override
    public CardGridConfigurator.CardBuilder<B> colSpan(int colSpan) {
        card.addClassName("col-span-" + colSpan);
        return this;
    }

    @Override
    public CardGridConfigurator.CardBuilder<B> rowSpan(int rowSpan) {
        card.addClassName("row-span-" + rowSpan);
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
    public CardGridConfigurator.CardBuilder<B> styleName(String styleName) {
        card.addClassName(styleName);
        return this;
    }

    /**
     * Adds one or more CSS style class names to this component.
     *
     * @param styleNames The CSS style class names to be added to the component
     * @return this
     */
    @Override
    public CardGridConfigurator.CardBuilder<B> styleNames(String... styleNames) {
        card.addClassNames(styleNames);
        return this;
    }
}
