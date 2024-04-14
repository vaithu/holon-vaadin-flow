package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.builders.CardBuilder;
import com.holonplatform.vaadin.flow.components.builders.CardGridBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class DefaultCardGridBuilder implements CardGridBuilder {

    private String title;
    private Component content;

    private final Div cardGrid;


    public DefaultCardGridBuilder() {
        cardGrid = new Div();
        cardGrid.addClassNames(LumoUtility.Display.GRID, LumoUtility.Grid.FLOW_ROW, LumoUtility.AlignItems.START
        );//,
//
        cardGrid.setId("card-grid-builder");


    }

    @Override
    public CardGridBuilder gap() {
        cardGrid.addClassName(LumoUtility.Gap.MEDIUM);
        return this;
    }

    @Override
    public CardGridBuilder withoutGap() {
        cardGrid.removeClassName(LumoUtility.Gap.MEDIUM);
        return this;
    }

    private void addCard(String title, Component component) {
        ObjectUtils.argumentNotNull(title, "Title cannot be null");
        ObjectUtils.argumentNotNull(component, "Content cannot be null");
        CardBuilder cardBuilder = CardBuilder.create()
                .title(title)
                .content(component);

        cardGrid.add(cardBuilder.build());

    }

    @Override
    public CardGridBuilder addCard(CardBuilder card) {
        cardGrid.add(card.build());
        return this;
    }

    @Override
    public CardGridBuilder add() {
        addCard();
        return this;
    }

    private void addCard() {
        ObjectUtils.argumentNotNull(title, "Title cannot be null");
        ObjectUtils.argumentNotNull(content, "Content cannot be null");
        CardBuilder cardBuilder = CardBuilder.create()
                .title(title)
                .content(content);

        cardGrid.add(cardBuilder.build());
    }

    @Override
    public CardGridBuilder add(String title, Component component) {

        addCard(title, component);
        return this;
    }

    @Override
    public String title(String titleText) {
        return this.title = titleText;
    }

    @Override
    public Component content(Component component) {
        return this.content = component;
    }

    @Override
    public CardGridBuilder column(int column) {
        cardGrid.addClassName("grid-cols-" + column);
        return this;
    }

    @Override
    public CardGridBuilder row(int row) {
        cardGrid.addClassName("grid-rows-" + row);
        return this;
    }

    @Override
    public CardGridBuilder spanColumn(int column) {
        cardGrid.addClassName("col-span-" + column);
        return this;
    }

    @Override
    public CardGridBuilder spanRow(int row) {
        cardGrid.addClassName("row-span-" + row);
        return this;
    }

    @Override
    public CardGridBuilder small(int column) {
        cardGrid.addClassName("sm:grid-cols-" + column);
        return this;
    }

    @Override
    public CardGridBuilder medium(int column) {
        cardGrid.addClassName("md:grid-cols-" + column);
        return this;
    }

    @Override
    public CardGridBuilder large(int column) {
        cardGrid.addClassName("lg:grid-cols-" + column);
        return this;
    }

    @Override
    public CardGridBuilder xLarge(int column) {
        cardGrid.addClassName("xl:grid-cols-" + column);
        return this;
    }

    @Override
    public CardGridBuilder xxLarge(int column) {
        cardGrid.addClassName("2xl:grid-cols-" + column);
        return this;
    }

    @Override
    public Div build() {
        return cardGrid;
    }


    /**
     * Adds one or more CSS style class names to this component.
     *
     * @param styleNames The CSS style class names to be added to the component
     * @return this
     */
    @Override
    public CardGridBuilder styleNames(String... styleNames) {
        cardGrid.addClassNames(styleNames);
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
    public CardGridBuilder styleName(String styleName) {
        cardGrid.addClassName(styleName);
        return this;
    }

    /**
     * Sets the width of the component.
     * <p>
     * The width should be in a format understood by the browser, e.g. "100px" or "2.5em".
     * </p>
     * <p>
     * If the provided <code>width</code> value is <code>null</code> then width is removed.
     * </p>
     *
     * @param width the width to set, may be <code>null</code>
     * @return this
     */
    @Override
    public CardGridBuilder width(String width) {
        cardGrid.setWidth(width);
        return this;
    }

    /**
     * Sets the height of the component.
     * <p>
     * The height should be in a format understood by the browser, e.g. "100px" or "2.5em".
     * </p>
     * <p>
     * If the provided <code>height</code> value is <code>null</code> then height is removed.
     * </p>
     *
     * @param height the height to set, may be <code>null</code>
     * @return this
     */
    @Override
    public CardGridBuilder height(String height) {
        cardGrid.setHeight(height);
        return this;
    }

    /**
     * Set the min-width of the component.
     * <p>
     * The width should be in a format understood by the browser, e.g. "100px" or "2.5em".
     * <p>
     *
     * @param minWidth the min width to set, may be <code>null</code>
     * @return this
     * @since 5.2.3
     */
    @Override
    public CardGridBuilder minWidth(String minWidth) {
        cardGrid.setMinWidth(minWidth);
        return this;
    }

    /**
     * Set the max-width of the component.
     * <p>
     * The width should be in a format understood by the browser, e.g. "100px" or "2.5em".
     * <p>
     *
     * @param maxWidth the max width to set, may be <code>null</code>
     * @return this
     * @since 5.2.3
     */
    @Override
    public CardGridBuilder maxWidth(String maxWidth) {
        cardGrid.setMaxWidth(maxWidth);
        return this;
    }

    /**
     * Set the min-height of the component.
     * <p>
     * The height should be in a format understood by the browser, e.g. "100px" or "2.5em".
     * <p>
     *
     * @param minHeight the min height to set, may be <code>null</code>
     * @return this
     * @since 5.2.3
     */
    @Override
    public CardGridBuilder minHeight(String minHeight) {
        cardGrid.setMinHeight(minHeight);
        return this;
    }

    /**
     * Set the max-height of the component.
     * <p>
     * The height should be in a format understood by the browser, e.g. "100px" or "2.5em".
     * <p>
     *
     * @param maxHeight the max height to set, may be <code>null</code>
     * @return this
     * @since 5.2.3
     */
    @Override
    public CardGridBuilder maxHeight(String maxHeight) {
        cardGrid.setMaxHeight(maxHeight);
        return this;
    }

    /**
     * Adds a theme name to this component.
     *
     * @param themeName the theme name to add, not <code>null</code>
     * @return this
     */
    @Override
    public CardGridBuilder withThemeName(String themeName) {
        return null;
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
    public CardGridBuilder withEventListener(String eventType, DomEventListener listener) {
        return null;
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
    public CardGridBuilder withEventListener(String eventType, DomEventListener listener, String filter) {
        return null;
    }

    /**
     * Set whether the component is enabled.
     *
     * @param enabled if <code>false</code> then explicitly disables the object, if <code>true</code> then enables the
     *                object so that its state depends on parent
     * @return this
     */
    @Override
    public CardGridBuilder enabled(boolean enabled) {
        cardGrid.setEnabled(enabled);
        return this;
    }

    /**
     * Adds the given components as children of this component.
     *
     * @param components The components to add
     * @return this
     */
    @Override
    public CardGridBuilder add(Component... components) {
        cardGrid.add(components);
        return this;
    }

    /**
     * Adds the components of each {@link HasComponent} as children of this component.
     *
     * @param components The {@link HasComponent}s to add
     * @return this
     */
    @Override
    public CardGridBuilder add(HasComponent... components) {

        return CardGridBuilder.super.add(components);
    }
}
