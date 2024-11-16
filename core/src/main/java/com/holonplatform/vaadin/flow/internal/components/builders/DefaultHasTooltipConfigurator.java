package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization;
import com.holonplatform.vaadin.flow.components.builders.HasTooltipConfigurator;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.function.Consumer;

/**
 * Default {@link HasTooltipConfigurator} implementation.
 *
 * @since 5.2.0
 */
public class DefaultHasTooltipConfigurator<C extends HasTooltip> extends AbstractLocalizationSupportConfigurator<C>
        implements HasTooltipConfigurator<DefaultHasTooltipConfigurator<C>> {

    private static final Logger LOGGER = VaadinLogger.create();

    private final C component;

    /**
     * Constructor.
     * @param component The component to configure (not null)
     * @param setTooltip Actual operation to set the tooltip (not null)
     */
    public DefaultHasTooltipConfigurator(C component, Consumer<String> setTooltip) {
        this(component, setTooltip, null);
    }

    /**
     * Constructor.
     * @param component The component to configure (not null)
     * @param setTooltip Actual operation to set the tooltip (not null)
     * @param deferrableLocalization Optional {@link HasDeferrableLocalization} reference
     */
    public DefaultHasTooltipConfigurator(C component, Consumer<String> setTooltip,
                                       HasDeferrableLocalization deferrableLocalization) {
        super(text -> setTooltip.accept(text), deferrableLocalization);
        ObjectUtils.argumentNotNull(component, "The component to configure must be not null");
        this.component = component;
    }

    /*
     * (non-Javadoc)
     * @see com.holonplatform.vaadin.flow.components.builders.HasTitleConfigurator#tooltip(com.holonplatform.core.i18n.
     * Localizable)
     */
    @Override
    public DefaultHasTooltipConfigurator<C> tooltip(Localizable tooltip) {
        if (localize(component, tooltip)) {
            LOGGER.debug(() -> "Component [" + component + "] tooltip localization was deferred");
        }
        return this;
    }

    @Override
    public DefaultHasTooltipConfigurator<C> tooltipText(String text) {
        this.component.setTooltipText(text);
        return this;
    }
}
