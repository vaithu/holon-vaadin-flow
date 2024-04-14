package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.CardBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DefaultCardBuilder extends Div
        implements HasComponents, HasSize, CardBuilder {
    //    Div card = new Div();
    Div content = new Div();

    H4 title = new H4();

    private boolean cardStyleEnabled = false;

    final List<String> cardStyles = List.of(LumoUtility.Border.ALL,
            LumoUtility.BorderColor.CONTRAST_10,
            LumoUtility.BorderRadius.SMALL,
            LumoUtility.BoxShadow.SMALL);

    public DefaultCardBuilder(String titleText) {

        this();
        setTitle(titleText);
    }

    public DefaultCardBuilder() {
        initContent();
        setId("card-builder");
    }

    public DefaultCardBuilder(LabelBuilder<H4> title) {
        this();
        this.title = title.build();
    }

    public void setTitle(String titleText) {
        title.setText(titleText);
    }


    public Optional<String> getTitle() {
        return Optional.ofNullable(title.getText());
    }

    public void initContent() {

        title.addClassNames(
                LumoUtility.TextColor.PRIMARY, LumoUtility.Padding.SMALL,
                LumoUtility.Border.BOTTOM,
                LumoUtility.BorderColor.CONTRAST_30);
        addClassNames(LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN);
        content.addClassNames(LumoUtility.Flex.GROW,
                LumoUtility.Padding.Top.SMALL, LumoUtility.Padding.MEDIUM);
        add(title, content);
    }

    @Override
    public CardBuilder cardStyle(boolean enabled) {
        this.cardStyleEnabled = enabled;
        if (this.cardStyleEnabled) {
            cardStyles.forEach(s -> addClassName(s));
        } else {
            cardStyles.forEach(s -> removeClassName(s));
        }
        return this;
    }

    @Override
    public CardBuilder gap() {
        content.addClassName(LumoUtility.Gap.MEDIUM);
        return this;
    }

    @Override
    public CardBuilder withoutGap() {
        content.removeClassName(LumoUtility.Gap.MEDIUM);
        return this;
    }

    @Override
    public CardBuilder title(String title) {
        setTitle(title);
        return this;
    }




    @Override
    public Div build() {
        return this;
    }

    /**
     * Adds one or more CSS style class names to this component.
     *
     * @param styleNames The CSS style class names to be added to the component
     * @return this
     */
    @Override
    public CardBuilder styleNames(String... styleNames) {
        addClassNames(styleNames);
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
    public CardBuilder styleName(String styleName) {
        addClassName(styleName);
        return this;
    }

    @Override
    public CardBuilder content(Component... components) {
        if (this.cardStyleEnabled) {
            Arrays.stream(components).forEach(component -> {
                cardStyles.forEach(s -> component.addClassName(s));
            });
        } else {
            Arrays.stream(components).forEach(component -> {
                cardStyles.forEach(s -> component.removeClassName(s));
            });
        }

        content.add(components);
        return this;
    }

}