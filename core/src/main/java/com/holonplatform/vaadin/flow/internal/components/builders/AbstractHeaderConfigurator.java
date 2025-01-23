package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HeaderConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbItem;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.theme.lumo.LumoIcon;

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
    public C breadcrumb(BreadcrumbItem... items) {
        getComponent().setBreadcrumb(items);
        return getConfigurator();
    }

    @Override
    public C heading(String title, HeadingLevel level) {
        getComponent().setHeading(title, level);
        return getConfigurator();
    }

    @Override
    public C heading(String title) {
        getComponent().setHeading(title);
        return getConfigurator();
    }

    @Override
    public C headingFontSize(Font.Size fontSize) {
        getComponent().setHeadingFontSize(fontSize);
        return getConfigurator();
    }

    @Override
    public C headingFontWeight(Font.Weight fontWeight) {
        getComponent().setHeadingFontWeight(fontWeight);
        return getConfigurator();
    }

    @Override
    public C headingId(String id) {
        getComponent().setHeadingId(id);
        return getConfigurator();
    }

    @Override
    public C headingLineHeight(Font.LineHeight lineHeight) {
        getComponent().setHeadingLineHeight(lineHeight);
        return getConfigurator();
    }

    @Override
    public C headingTextColor(Color.Text textColor) {
        getComponent().setHeadingTextColor(textColor);
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
    public C close(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        if (configurator != null) {
            Button discardBtn = Components.button()
                    .icon(LumoIcon.CROSS.create())
                    .withThemeVariants(ButtonVariant.LUMO_TERTIARY)
                    .build();
            configurator.accept(ButtonConfigurator.configure(discardBtn));
            getComponent().addActions(discardBtn);
        }
        return getConfigurator();
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
    public Tabs getTabs() {
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
    public C add(Component... components) {
        getComponent().add(components);
        return getConfigurator();
    }

    @Override
    public C add(String text) {
        getComponent().add(text);
        return getConfigurator();
    }

    @Override
    public C addComponentAsFirst(Component component) {
        getComponent().addComponentAsFirst(component);
        return getConfigurator();
    }

    @Override
    public C addComponentAtIndex(int index, Component component) {
        getComponent().addComponentAtIndex(index, component);
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
