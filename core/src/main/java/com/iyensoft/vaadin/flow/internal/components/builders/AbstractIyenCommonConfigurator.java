package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.holonplatform.vaadin.flow.internal.lumo.Display;
import com.holonplatform.vaadin.flow.internal.lumo.FlexDirection;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.iyensoft.vaadin.flow.components.builders.IyenCommonConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.textfield.TextField;

import java.util.Optional;

public abstract class AbstractIyenCommonConfigurator<C extends IyenCommonConfigurator<C>>
        extends AbstractComponentConfigurator<Layout,C>
        implements IyenCommonConfigurator<C> {

    private final Layout container;
    private Layout content;

    public AbstractIyenCommonConfigurator(Layout container) {
        super(container);
        this.container = container;
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
    public C header(Header header) {
        header.setHeadingFontSize(Font.Size.XXLARGE);

        // Remove the default row padding when used inside a master-view layout
        header.getRowLayout().removeClassName("iyen-header__row--padded");

        // Position the tabs flush with the container edges (negative margin)
        header.getTabs().ifPresent(tabs -> tabs.addClassName("iyen-header__tabs--edge-aligned"));
        container.add(header);
        return getConfigurator();
    }

    @Override
    public C toolbar(TextField textField, Button... buttons) {
        Component component = textField;
        this.container.add(toolbar(component,buttons));
        return getConfigurator();
    }

    @Override
    public C toolbar(MenuBar menuBar) {
        Layout toolbar = new Layout(menuBar);
        toolbar.setDisplay(Display.FLEX);
        toolbar.setFlexDirection(FlexDirection.ROW);
        toolbar.addClassName("toolbar--filled");
        this.container.add(toolbar);
        return getConfigurator();
    }

    @Override
    public C toolbar(Input<String> input, Button... buttons) {
        Layout toolbar = toolbar(input.getComponent(), buttons);
        this.container.add(toolbar);
        return getConfigurator();
    }

    private Layout toolbar(Component component, Button... buttons) {
        Layout toolbar = new Layout(component);
        toolbar.addClassName("toolbar");
        if (buttons != null && buttons.length > 0) {

            for (Button button : buttons) {
                button.addClassName("toolbar__btn--constrained");
            }

            Layout buttonLayout = new Layout(buttons);
            buttonLayout.addClassName("toolbar");
            toolbar.add(buttonLayout);
        }


        return toolbar;
    }

    @Override
    public C content(GridHeader gridHeader, Grid<?> grid) {
        return addContent(gridHeader, grid);
    }

    @Override
    public C content(GridHeader gridHeader, BeanListing<?> listing) {
        return addContent(gridHeader, listing.getComponent());
    }

    @Override
    public C content(GridHeader gridHeader, PropertyListing listing) {
        return addContent(gridHeader, listing.getComponent());
    }

    @Override
    public C content(Component component) {
        return addContent(component);
    }

    @Override
    public C content(ListingBundle<?> bundle) {
        ResponsiveDiv div = ResponsiveDiv.flex().column()
                .fullHeight()
                .gapS()
                .build();
        GridHeader header = bundle.header();
        Div toolbar = bundle.toolbar();
        Component grid = bundle.grid();
        Div footer = bundle.footer();
        if (header != null) {
            div.add(header);
        }
        div.add(toolbar);
        if (grid != null) {
            div.add(grid);
        }if (footer != null) {
            div.add(footer);
        }
        return addContent(div);
    }

    @Override
    public Layout getContent() {
        return this.content;
    }

    private C addContent(GridHeader gridHeader, Component contentComponent) {
        gridHeader.getRowLayout().addClassName("grid-header__row--end-padded");
//        gridHeader.getRowLayout().setAlignItems(AlignItems.BASELINE);
        gridHeader.setHeadingFontSize(Font.Size.LARGE);
        content = new Layout(gridHeader, contentComponent);
        content.addClassName("master-content");
        content.addClassName("master-content--card");
        content.setFlexGrow(contentComponent);
        this.container.add(content);
        return getConfigurator();
    }

    private C addContent(Component contentComponent) {
        content = new Layout(contentComponent);
        this.container.add(content);
        content.setFlexGrow(contentComponent);
        return getConfigurator();
    }



}
