package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.components.builders.BulkActionBuilder;
import com.holonplatform.vaadin.flow.components.builders.FormHeaderBuilder;
import com.holonplatform.vaadin.flow.components.builders.SearchBarBuilder;
import com.holonplatform.vaadin.flow.components.builders.ZohoConfigurator;
import com.holonplatform.vaadin.flow.components.css.CSSConstants;
import com.holonplatform.vaadin.flow.internal.lumo.Background;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Objects;
import java.util.Optional;

public abstract class AbstractZohoConfigurator<C extends ZohoConfigurator<C>> extends AbstractComponentConfigurator<HorizontalLayout, C>
        implements ZohoConfigurator<C> {

    private final VerticalLayout masterLayout;
    private VerticalLayout detailLayout;
    private boolean mobile;
    private boolean desktop;

    /**
     *
     * Constructor.
     *
     * @param content The content instance (not null)
     */
    public AbstractZohoConfigurator(HorizontalLayout content) {
        this(content, false);
    }

    public AbstractZohoConfigurator(HorizontalLayout content, boolean mobile) {
        super(content);
        mobile(mobile);

        masterLayout = Components.vl()
                .id("master")
                .spacing()
                .withoutPadding()
                .styleNames(LumoUtility.Padding.Top.LARGE)
                .build();

        if (desktop) {
            masterLayout.setWidth(30, Unit.PERCENTAGE);
        } else {
            masterLayout.setWidthFull();
        }

        Components.configure(content)
                .styleNames(LumoUtility.Flex.GROW)
                .fullHeight()
                .withoutSpacing()
                .addAndAlign(masterLayout, FlexComponent.Alignment.STRETCH);

        if (desktop) {

            detailLayout = Components.vl()
                    .id("detail")
                    .withoutSpacing()
                    .withoutPadding()
                    .fullWidth()
                    .build();

            Components.configure(content)
                    .addAndAlign(detailLayout, FlexComponent.Alignment.STRETCH);
        }
    }
    private void mobile(boolean mobile) {
        this.mobile = mobile;
        desktop = !this.mobile ;
    }

    @Override
    public VerticalLayout getDetailLayout() {
        Objects.requireNonNull(detailLayout, "This is initialized only in desktop view and so null for mobile");
        return detailLayout;
    }

    @Override
    public VerticalLayout getMasterLayout() {
        return masterLayout;
    }

    @Override
    public C masterContent(Component component) {
        masterLayout.add(component);
        return getConfigurator();
    }

    @Override
    public C detailContent(Component component) {
        detailLayout.add(component);
        return getConfigurator();
    }

    @Override
    public C searchBar(Component component) {
        masterLayout.addComponentAsFirst(component);
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
        masterLayout.add(component);
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
    public C grid(BeanListing<?> listing) {
        listing.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);
        return masterContent(listing.getComponent());
    }

    @Override
    public C grid(PropertyListing listing) {
        listing.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);
        return masterContent(listing.getComponent());
    }

    @Override
    public C gridHeader(GridHeader gridHeader) {
        return masterContent(gridHeader);
    }

    @Override
    public C detailsHeader(Header header) {
        return detailHeader(header);
    }

    @Override
    public C masterHeader(Header header) {
        return masterContent(header);
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
            separator(Background.CONTRAST_10);
        }

        return getConfigurator();
    }

    @Override
    public C separator(Background background) {
        getComponent().addComponentAtIndex(1,Components.utils.divider()
                .verticalSeparator()
                .styleNames(background.getClassName())
                .build());
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
        detailLayout.addComponentAsFirst(component);
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
        return new DefaultBulkActionBarBuilder<>(getConfigurator(), new HorizontalLayout());
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
