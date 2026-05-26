package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.components.builders.ZohoViewConfigurator;
import com.holonplatform.vaadin.flow.internal.lumo.AlignItems;
import com.holonplatform.vaadin.flow.internal.lumo.FlexDirection;
import com.holonplatform.vaadin.flow.internal.lumo.Gap;
import com.holonplatform.vaadin.flow.internal.lumo.Overflow;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

public  abstract class AbstractZohoCommonBuilder< B extends ZohoViewConfigurator< B>>
        implements ZohoViewConfigurator.ZohoCommonBuilder< B> {

    private final B configurator;
    private final Layout layout;

    /**
     * Constructor.
     *
     */
    public AbstractZohoCommonBuilder(B configurator,Layout layout) {
        this.configurator = configurator;
        this.layout = layout;
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> header(Header header) {
        header.setGap(Gap.MEDIUM);
        header.getRowLayout().removeClassName(LumoUtility.Padding.MEDIUM);

        header.getRowLayout().removeClassName("iyen-header__row--padded");
        header.getTabs().ifPresent(tabs -> tabs.addClassName(LumoUtility.Margin.Minus.Horizontal.LARGE));

        header.getTabs().ifPresent(tabs -> tabs.addClassName("iyen-header__tabs--edge-aligned"));
        header.getTabs().ifPresent(tabs -> tabs.addClassName(LumoUtility.Margin.Minus.Horizontal.LARGE));
        layout.add(header);
        return this;
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> toolbar(TextField textField, Button... buttons) {
        Layout buttonLayout = new Layout(buttons);
//        applyStylesToToolbarLayout(buttonLayout);
        buttonLayout.setGap(Gap.SMALL);

        Layout toolbar = new Layout(textField, buttonLayout);
        applyStylesToToolbarLayout(toolbar);

        this.layout.add(toolbar);
        return this;
    }
    private void applyStylesToToolbarLayout(Layout toolbar) {
//        toolbar.setFlexDirection(FlexDirection.COLUMN);
//        toolbar.setFlexDirection(Breakpoint.SMALL, FlexDirection.ROW);
        toolbar.setGap(Gap.MEDIUM);
        toolbar.setAlignItems(AlignItems.BASELINE);
//        toolbar.setJustifyContent(JustifyContent.BETWEEN);

        /*toolbar.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.FlexDirection.Breakpoint.Medium.ROW, LumoUtility.JustifyContent.BETWEEN,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.AlignItems.STRETCH);*/
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> toolbar(Input<String> input, Button... buttons) {
        Layout buttonLayout = new Layout(buttons);
        buttonLayout.setGap(Gap.SMALL);

        Layout toolbar = new Layout(input.getComponent(), buttonLayout);
        applyStylesToToolbarLayout(toolbar);
        this.layout.add(toolbar);
        return this;
    }

    private void applyStylesToContentLayout(Layout content) {
        content.addClassNames("border-all", "border-radius-large");
        content.setFlexDirection(FlexDirection.COLUMN);
        content.setAlignItems(AlignItems.BASELINE);
        content.setOverflow(Overflow.HIDDEN);
        content.setHeightFull();
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> content(GridHeader gridHeader, Grid<?> grid) {
        return addContent(gridHeader, grid);
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> content(GridHeader gridHeader, BeanListing<?> listing) {
        return addContent(gridHeader, listing.getComponent());
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> content(GridHeader gridHeader, PropertyListing listing) {
        return addContent(gridHeader, listing.getComponent());
    }

    private ZohoViewConfigurator.ZohoCommonBuilder<B> addContent(GridHeader gridHeader, Component contentComponent) {
        gridHeader.getRowLayout().addClassName("grid-header__row--end-padded");
        gridHeader.getRowLayout().setAlignItems(AlignItems.BASELINE);
        gridHeader.setHeadingFontSize(Font.Size.LARGE);
        Layout content = new Layout(gridHeader, contentComponent);
        applyStylesToContentLayout(content);
        this.layout.add(content);
        return this;
    }

    private ZohoViewConfigurator.ZohoCommonBuilder<B>  addContent(Component contentComponent) {
        Layout content = new Layout( contentComponent);
        applyStylesToContentLayout(content);
        this.layout.add(content);
        return this;
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> content(Grid<?> grid) {
        return addContent(grid);
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> content(BeanListing<?> listing) {
        return addContent(listing.getComponent());
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> content(PropertyListing listing) {
        return addContent(listing.getComponent());
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> content(Component component) {
        return addContent(component);
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> width(String width) {
        layout.setMinWidth(width);
        return this;
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> height(String height) {
        layout.setHeight(height);
        return this;
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> minWidth(String minWidth) {
        layout.setMinWidth(minWidth);
        return this;
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> maxWidth(String maxWidth) {
        layout.setMaxWidth(maxWidth);
        return this;
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> minHeight(String minHeight) {
        layout.setMinHeight(minHeight);
        return this;
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> maxHeight(String maxHeight) {
        layout.setMaxHeight(maxHeight);
        return this;
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> styleNames(String... styleNames) {
        layout.addClassNames(styleNames);
        return this;
    }

    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> styleName(String styleName) {
        layout.addClassName(styleName);
        return this;
    }


    @Override
    public ZohoViewConfigurator.ZohoCommonBuilder<B> id(String id) {
        layout.setId(id);
        return this;
    }

    @Override
    public ZohoViewConfigurator.BuiltView<B> build() {
        return new ZohoViewConfigurator.BuiltView<>(layout,configurator);
    }
}
