package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.vaadin.flow.component.shared.HasTooltip;

public class DefaultHasTooltipConfigurator implements HasTooltipConfigurator.BaseHasTooltipConfigurator {

    private final HasTooltip component;

    /**
     * Constructor.
     * @param component Component to configure (not null)
     */
    public DefaultHasTooltipConfigurator(HasTooltip component) {
        super();
        ObjectUtils.argumentNotNull(component, "The component to configure must be not null");
        this.component = component;
    }

    @Override
    public BaseHasTooltipConfigurator getTooltip() {
        component.getTooltip();
        return this;
    }

    @Override
    public BaseHasTooltipConfigurator setTooltipText(String text) {
        component.setTooltipText(text);
        return this;
    }
}
