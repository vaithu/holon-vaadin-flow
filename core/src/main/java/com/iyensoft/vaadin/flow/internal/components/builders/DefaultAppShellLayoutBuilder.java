package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.vaadinplus.components.AppShellLayout;
import com.iyensoft.vaadin.flow.components.builders.AppShellLayoutBuilder;
import com.vaadin.flow.component.applayout.AppLayout;

import java.util.Objects;

/**
 * Default implementation of {@link AppShellLayoutBuilder}.
 */
public final class DefaultAppShellLayoutBuilder
        extends AbstractAppShellLayoutConfigurator<AppShellLayoutBuilder>
        implements AppShellLayoutBuilder {

    public DefaultAppShellLayoutBuilder() {}

    @Override
    protected AppShellLayoutBuilder getConfigurator() {
        return this;
    }

    @Override
    public AppShellLayout build() {
        var layout = new AppShellLayout();
        applyTo(layout);
        return layout;
    }

    @Override
    public void configure(AppLayout target) {
        applyTo(Objects.requireNonNull(target, "target"));
    }
}
