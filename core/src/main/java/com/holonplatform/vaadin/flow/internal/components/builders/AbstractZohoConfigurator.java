package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.components.builders.BulkActionBuilder;
import com.holonplatform.vaadin.flow.components.builders.FormHeaderBuilder;
import com.holonplatform.vaadin.flow.components.builders.SearchBarBuilder;
import com.holonplatform.vaadin.flow.components.builders.ZohoConfigurator;
import com.holonplatform.vaadin.flow.components.css.CSSConstants;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Optional;

public abstract class AbstractZohoConfigurator<C extends ZohoConfigurator<C>> extends AbstractComponentConfigurator<HorizontalLayout, C>
        implements ZohoConfigurator<C> {

    private final VerticalLayout leftVL;
    private final VerticalLayout rightVL;

    /**
     * Constructor.
     *
     * @param content The content instance (not null)
     */
    public AbstractZohoConfigurator(HorizontalLayout content) {
        super(content);

        leftVL = Components.vl()
                .id("master")
                .styleNames(LumoUtility.Gap.SMALL,LumoUtility.Display.FLEX)
                .width(30, Unit.PERCENTAGE)
                .withoutPadding()
                .build();

        rightVL = Components.vl()
                .id("detail")
                .withoutSpacing()
                .withoutPadding()
                .styleNames(LumoUtility.Display.FLEX)
                .build();

        Components.configure(content)
                .styleNames(LumoUtility.Gap.MEDIUM, CSSConstants.CARD,LumoUtility.Display.FLEX, LumoUtility.Flex.GROW)
                .addAndExpand(leftVL, 1)
                .align(leftVL, FlexComponent.Alignment.STRETCH)
                .addAndExpand(rightVL, 1)
                .align(rightVL, FlexComponent.Alignment.STRETCH);


    }

    @Override
    public C masterContent(Component component) {
        leftVL.add(component);
        return getConfigurator();
    }

    @Override
    public C detailContent(Component component) {
        rightVL.addAndExpand(component);
        return getConfigurator();
    }

    @Override
    public C searchBar(Component component) {
        leftVL.addComponentAsFirst(component);
        return getConfigurator();
    }

    @Override
    public C searchBar(SearchBarBuilder searchBarBuilder) {
        return searchBar(searchBarBuilder.build());
    }

    @Override
    public C bulkAction(BulkActionBuilder bulkActionBuilder) {
        return bulkAction(bulkActionBuilder.build());
    }

    @Override
    public C bulkAction(Component component) {
        leftVL.add(component);
        return getConfigurator();
    }

    @Override
    public C grid(Grid<?> grid) {

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);
        return masterContent(grid);
    }

    @Override
    public C grid(Component grid) {

        if (grid instanceof Grid<?>) {
            ((Grid<?>) grid).addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);
        }
        return masterContent(grid);
    }

    @Override
    public C listing(PropertyListing listing) {
        listing.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);
        return masterContent(listing.getComponent());
    }

    @Override
    public C listing(BeanListing<?> listing) {
        listing.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);
        return masterContent(listing.getComponent());
    }

    @Override
    public C separator(boolean separator) {
        if (separator) {
            getComponent().addComponentAtIndex(1, Components.utils.divider()
                    .verticalSeparator()
                    .styleNames(LumoUtility.Background.CONTRAST_30)
                    .build());
        }

        return getConfigurator();
    }

    @Override
    public C detailHeader(FormHeaderBuilder formHeader) {
        return detailHeader(formHeader
                .styleNames(CSSConstants.FORM_HEADER)
                .build());
    }

    @Override
    public C detailHeader(Component component) {
        rightVL.addComponentAsFirst(component);
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
    public BulkActionBarBuilder<C> bulkActionBar() {
        return new DeafultBulkActionBarBuilder<>(getConfigurator(), new HorizontalLayout());
    }

    @Override
    public FormHeaderBarBuilder<C> formHeader() {
        return new DefaultFormHeaderBarBuilder<>(getConfigurator(), new HorizontalLayout());
    }

    @Override
    public SearchFormBarBuilder<C> searchBar() {
        return new DefaultSearchFormBarBuilder<>(getConfigurator(), new HorizontalLayout());
    }
}
