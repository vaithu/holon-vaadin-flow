package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HeaderConfigurator;
import com.holonplatform.vaadin.flow.components.css.CSSUtility;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbItem;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.theme.lumo.LumoIcon;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractHeaderConfigurator<C extends HeaderConfigurator<C>>
        extends AbstractComponentConfigurator<Header,C>
        implements HeaderConfigurator<C> {


    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public AbstractHeaderConfigurator(Header component) {
        super(component);
    }

    @Override
    public C prefix(Component... components) {
        getComponent().setPrefix(components);
        return getConfigurator();
    }

    @Override
    public C hidePrefixOnDesktop(boolean hidePrefixOnDesktop) {
        if (hidePrefixOnDesktop && getComponent().getPrefixComponents() != null) {
            Arrays.stream(getComponent().getPrefixComponents()).filter(Objects::nonNull)
                    .forEach(component -> {
                        component.addClassName(CSSUtility.Bootstrap.D_MD_NONE);
                    });
        }
        return getConfigurator();
    }

    @Override
    public C breadcrumb(BreadcrumbItem... items) {
        getComponent().setBreadcrumb(items);
        return getConfigurator();
    }

    @Override
    public C details(Component... components) {
        getComponent().setDetails(components);
        return getConfigurator();
    }

    @Override
    public C actions(Component... components) {
        getComponent().setActions(components);
        return getConfigurator();
    }

    @Override
    public C edit(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        Button editBtn = Components.button()
                .icon(LumoIcon.EDIT.create())
                .title("Edit")
                .text("Edit")
                .build();

        return addActions(configurator,editBtn);
    }

    private C addActions(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator,Button button) {
        configurator.accept(ButtonConfigurator.configure(button));
        getComponent().addActions(button);
        return getConfigurator();
    }

    @Override
    public C options(MenuBar menuBar) {
        getComponent().addActions(menuBar);
        return getConfigurator();
    }

    @Override
    public C options(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        Button moreBtn = Components.button()
                .icon(VaadinIcon.ELLIPSIS_DOTS_V)
//                .withThemeVariants(ButtonVariant.LUMO_TERTIARY)
                .title("Options")
                .build();
        return addActions(configurator, moreBtn);
    }

    @Override
    public C newBtn(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        Button moreBtn = Components.button()
                .icon(LumoIcon.PLUS.create())
                .withThemeVariants(ButtonVariant.LUMO_PRIMARY)
                .title("New")
                .build();
        return addActions(configurator, moreBtn);

    }

    @Override
    public C refresh(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        Button moreBtn = Components.button()
                .icon(VaadinIcon.REFRESH)
//                .withThemeVariants(ButtonVariant.LUMO_TERTIARY)
                .title("Refresh")
                .build();
        return addActions(configurator, moreBtn);
    }

    @Override
    public C close(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        Button discardBtn = Components.button()
                .icon(LumoIcon.CROSS.create())
                .withThemeVariants(ButtonVariant.LUMO_TERTIARY)
                .title("Close")
                .build();
        return addActions(configurator, discardBtn);
    }

    @Override
    public Layout getRowLayout() {
        return getComponent().getRowLayout();
    }

    @Override
    public Layout getColumnLayout() {
        return getComponent().getColumnLayout();
    }

    @Override
    public Optional<Tabs> getTabs() {
        return getComponent().getTabs();
    }

    @Override
    public C tabs(Tab... tabs) {
        getComponent().setTabs(tabs);
        return

                getConfigurator();
    }

    @Override
    public C tabs(Tabs tabs) {
        getComponent().setTabs(tabs);
        return getConfigurator();
    }

    @Override
    public C withoutBorder() {
        getComponent().getStyle().set("border-bottom", "none !important");
        return getConfigurator();
    }

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }
}
