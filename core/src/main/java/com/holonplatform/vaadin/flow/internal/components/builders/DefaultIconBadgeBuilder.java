package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.IconBadgeBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.IconBadge;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Default {@link IconBadgeBuilder} implementation.
 */
public class DefaultIconBadgeBuilder
        extends AbstractIconBadgeConfigurator<IconBadgeBuilder>
        implements IconBadgeBuilder {

    public DefaultIconBadgeBuilder(VaadinIcon icon, Alert.Variant variant, IconBadge.Size size) {
        super(new IconBadge(
                icon != null ? icon.create() : VaadinIcon.CIRCLE.create(),
                variant,
                size != null ? size : IconBadge.Size.DEFAULT));
    }

    @Override
    protected IconBadgeBuilder getConfigurator() {
        return this;
    }

    @Override
    public IconBadge build() {
        return getComponent();
    }
}



