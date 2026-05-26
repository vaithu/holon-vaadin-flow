package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.iyensoft.vaadin.flow.components.builders.SideNavConfigurator;
import com.iyensoft.vaadin.flow.components.builders.SideNavItemBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.RouteParameters;

import java.util.List;
import java.util.Optional;

public abstract class AbstractSideNavConfigurator<C extends SideNavConfigurator<C>>
        extends AbstractComponentConfigurator<SideNav, C>
        implements SideNavConfigurator<C> {
    protected final SideNav sideNav;
    protected boolean searchEnabled     = false;
    protected String  searchPlaceholder = "Search...";
    protected boolean collapseEnabled   = false;

    public AbstractSideNavConfigurator(SideNav sideNav) {
        super(sideNav);
        this.sideNav = sideNav;
    }

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.empty();
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.empty();
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.empty();
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }

    @Override
    public C withItem(SideNavItem... items) {
        sideNav.addItem(items);
        return getConfigurator();
    }

    @Override
    public C withItemAsFirst(SideNavItem item) {
        sideNav.addItemAsFirst(item);
        return getConfigurator();
    }

    @Override
    public C withItemAtIndex(int index, SideNavItem item) {
        sideNav.addItemAtIndex(index, item);
        return getConfigurator();
    }

    @Override
    public C collapsible(boolean collapsible) {
        sideNav.setCollapsible(collapsible);
        return getConfigurator();
    }

    @Override
    public C expanded(boolean expanded) {
        sideNav.setExpanded(expanded);
        return getConfigurator();
    }

    @Override
    public C label(String label) {
        sideNav.setLabel(label);
        // Vaadin makes SideNav collapsible automatically when a label is set.
        // Default to expanded so items are visible without an extra .expanded(true) call.
        sideNav.setExpanded(true);
        return getConfigurator();
    }

    @Override
    public C label(Localizable label) {
        return label(resolve(label));
    }

    @Override
    public List<SideNavItem> getItems() {
        return sideNav.getItems();
    }

    @Override
    public C withSearch() {
        this.searchEnabled = true;
        return getConfigurator();
    }

    @Override
    public C withSearch(String placeholder) {
        this.searchEnabled = true;
        this.searchPlaceholder = (placeholder != null && !placeholder.isBlank()) ? placeholder : "Search...";
        return getConfigurator();
    }

    @Override
    public C withSearch(Localizable placeholder) {
        return withSearch(resolve(placeholder));
    }

    @Override
    public C withCollapse() {
        this.collapseEnabled = true;
        return getConfigurator();
    }

    @Override
    public void filter(String filter) {
        String f = (filter == null) ? "" : filter.toLowerCase();
        for (SideNavItem item : getItems()) {
            boolean visible = item.getLabel() != null && item.getLabel().toLowerCase().contains(f);
            item.setVisible(visible);
        }
    }

    public void filterRecursive(String filter) {
        String f = (filter == null) ? "" : filter.toLowerCase();
        for (SideNavItem item : getItems()) {
            filterItemRecursive(item, f);
        }
    }

    protected boolean filterItemRecursive(SideNavItem item, String filter) {
        boolean labelMatches = item.getLabel() != null && item.getLabel().toLowerCase().contains(filter);
        boolean childMatches = false;
        for (SideNavItem child : item.getItems()) {
            childMatches |= filterItemRecursive(child, filter);
        }
        boolean visible = filter.isEmpty() || labelMatches || childMatches;
        item.setVisible(visible);
        if (childMatches) item.setExpanded(true);
        return visible;
    }

    public Div buildWrapper() {
        var host = Components.div().styleName("sidenav-host").build();
        host.addAttachListener(e -> e.getUI().getPage().addStyleSheet("context://menu.css"));
        if (searchEnabled) {
            var searchField = new TextField();
            searchField.setPlaceholder(searchPlaceholder);
            searchField.addClassName("sidenav-search");
            searchField.setClearButtonVisible(true);
            searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
            searchField.setValueChangeMode(ValueChangeMode.LAZY);
            searchField.addValueChangeListener(e -> filterRecursive(e.getValue()));
            host.add(searchField);
        }
        host.add(sideNav);
        if (collapseEnabled) {
            Button toggle = Components.button()
                    .styleName("sidenav-collapse-toggle")
                    .icon(VaadinIcon.CHEVRON_LEFT)
                    .withClickListener(e -> {
                        if (host.hasClassName("sidenav-host--collapsed")) {
                            host.removeClassName("sidenav-host--collapsed");
                        } else {
                            host.addClassName("sidenav-host--collapsed");
                        }
                    })
                    .build();
            host.add(toggle);
        }
        return host;
    }

    @Override
    public SideNavItemBuilder withNavItem(String label) {
        return new DefaultSideNavItemBuilder(this, new SideNavItem(label));
    }

    @Override
    public SideNavItemBuilder withNavItem(String label, Class<? extends Component> view) {
        return new DefaultSideNavItemBuilder(this, new SideNavItem(label, view));
    }

    @Override
    public SideNavItemBuilder withNavItem(String label, Class<? extends Component> view, Component prefixComponent) {
        return new DefaultSideNavItemBuilder(this, new SideNavItem(label, view, prefixComponent));
    }

    @Override
    public SideNavItemBuilder withNavItem(String label, Class<? extends Component> view, RouteParameters params) {
        return new DefaultSideNavItemBuilder(this, new SideNavItem(label, view, params));
    }

    @Override
    public SideNavItemBuilder withNavItem(String label, Class<? extends Component> view, RouteParameters params, Component prefixComponent) {
        return new DefaultSideNavItemBuilder(this, new SideNavItem(label, view, params, prefixComponent));
    }

    @Override
    public SideNavItemBuilder withNavItem(String label, String path) {
        return new DefaultSideNavItemBuilder(this, new SideNavItem(label, path));
    }

    @Override
    public SideNavItemBuilder withNavItem(String label, String path, Component prefixComponent) {
        return new DefaultSideNavItemBuilder(this, new SideNavItem(label, path, prefixComponent));
    }

    // ── Localizable withNavItem overloads ─────────────────────────────────────

    @Override
    public SideNavItemBuilder withNavItem(Localizable label) {
        return withNavItem(resolve(label));
    }

    @Override
    public SideNavItemBuilder withNavItem(Localizable label, Class<? extends Component> view) {
        return withNavItem(resolve(label), view);
    }

    @Override
    public SideNavItemBuilder withNavItem(Localizable label, Class<? extends Component> view, Component prefixComponent) {
        return withNavItem(resolve(label), view, prefixComponent);
    }

    @Override
    public SideNavItemBuilder withNavItem(Localizable label, Class<? extends Component> view, RouteParameters params) {
        return withNavItem(resolve(label), view, params);
    }

    @Override
    public SideNavItemBuilder withNavItem(Localizable label, Class<? extends Component> view, RouteParameters params, Component prefixComponent) {
        return withNavItem(resolve(label), view, params, prefixComponent);
    }

    @Override
    public SideNavItemBuilder withNavItem(Localizable label, String path) {
        return withNavItem(resolve(label), path);
    }

    @Override
    public SideNavItemBuilder withNavItem(Localizable label, String path, Component prefixComponent) {
        return withNavItem(resolve(label), path, prefixComponent);
    }

    protected static String resolve(Localizable l) {
        return LocalizationProvider.localize(l)
                .orElseGet(() -> l.getMessage() != null ? l.getMessage() : "");
    }
}
