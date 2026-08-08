package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultWizardFrameBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.WizardFrame;

/**
 * Fluent builder for {@link WizardFrame} components.
 *
 * <h3>Usage example</h3>
 * <pre>{@code
 * WizardFrame wizard = WizardFrame.builder()
 *     .title("New Customer")
 *     .step("Contact",
 *         EntityFormPanel.bean(ContactBean.class).noFooter().build())
 *     .step("Company",
 *         EntityFormPanel.bean(CompanyBean.class).noFooter().build())
 *     .step("Review", "Create Customer",
 *         reviewPanel)
 *     .onFinish(wf -> service.save(collected))
 *     .build();
 * }</pre>
 *
 * @see WizardFrameConfigurator
 * @see WizardFrame
 */
public interface WizardFrameBuilder extends WizardFrameConfigurator<WizardFrameBuilder> {

    /**
     * Builds and returns the configured {@link WizardFrame}.
     *
     * @return a new {@link WizardFrame} instance
     * @throws IllegalStateException if no steps have been configured
     */
    WizardFrame build();

    /**
     * Creates a new {@link WizardFrameBuilder}.
     *
     * @return a new builder instance
     */
    static WizardFrameBuilder create() {
        return new DefaultWizardFrameBuilder();
    }
}
