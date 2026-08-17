package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.vaadinplus.components.AppShellLayout;
import com.iyensoft.vaadin.flow.components.builders.AppShellLayoutConfigurator;
import com.vaadin.flow.component.applayout.AppLayout;

import java.util.Objects;

/**
 * Default {@link AppShellLayoutConfigurator.BaseAppShellLayoutConfigurator} implementation
 * that configures an existing {@link AppShellLayout}.
 *
 * <p>Call {@link #apply()} to apply the stored configuration to the wrapped layout.</p>
 */
public class DefaultAppShellLayoutConfigurator
        extends AbstractAppShellLayoutConfigurator<AppShellLayoutConfigurator.BaseAppShellLayoutConfigurator>
        implements AppShellLayoutConfigurator.BaseAppShellLayoutConfigurator {

    private final AppLayout existingLayout;

    public DefaultAppShellLayoutConfigurator(AppShellLayout layout) {
        this.existingLayout = Objects.requireNonNull(layout, "layout must not be null");
    }

    @Override
    protected AppShellLayoutConfigurator.BaseAppShellLayoutConfigurator getConfigurator() {
        return this;
    }

    /**
     * Applies all stored configuration to the wrapped {@link AppLayout}.
     */
    public void apply() {
        applyTo(existingLayout);
    }
}
