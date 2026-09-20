package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.WizardFrameConfigurator;

/**
 * Default {@link WizardFrameConfigurator.BaseWizardFrameConfigurator} implementation.
 *
 * <p>Returned by {@link WizardFrameConfigurator#configure(com.iyensoft.vaadin.flow.components.WizardFrame)}.
 * Accumulates configuration state; call {@code buildWizardFrame()} to produce the assembled frame.</p>
 */
public class DefaultWizardFrameConfigurator
        extends AbstractWizardFrameConfigurator<WizardFrameConfigurator.BaseWizardFrameConfigurator>
        implements WizardFrameConfigurator.BaseWizardFrameConfigurator {

    @Override
    protected WizardFrameConfigurator.BaseWizardFrameConfigurator getConfigurator() {
        return this;
    }
}
