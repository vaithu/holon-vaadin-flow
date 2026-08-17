package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.WizardFrameBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.WizardFrame;

/**
 * Default {@link WizardFrameBuilder} implementation.
 *
 * <p>Extends {@link AbstractWizardFrameConfigurator} for all configuration state and logic.
 * Returned by {@link WizardFrameBuilder#create()}.</p>
 */
public final class DefaultWizardFrameBuilder
        extends AbstractWizardFrameConfigurator<WizardFrameBuilder>
        implements WizardFrameBuilder {

    @Override
    protected WizardFrameBuilder getConfigurator() {
        return this;
    }

    @Override
    public WizardFrame build() {
        return buildWizardFrame();
    }
}

