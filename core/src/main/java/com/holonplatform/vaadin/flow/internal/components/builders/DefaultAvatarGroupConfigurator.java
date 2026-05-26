package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.AvatarGroupConfigurator;
import com.vaadin.flow.component.avatar.AvatarGroup;

/**
 * Default {@link AvatarGroupConfigurator} implementation (for {@code AvatarGroupConfigurator.configure(group)}).
 *
 * @since 10.0.0
 */
public class DefaultAvatarGroupConfigurator
        extends AbstractAvatarGroupConfigurator<AvatarGroupConfigurator.BaseAvatarGroupConfigurator>
        implements AvatarGroupConfigurator.BaseAvatarGroupConfigurator {

    public DefaultAvatarGroupConfigurator(AvatarGroup group) {
        super(group);
    }

    @Override
    protected AvatarGroupConfigurator.BaseAvatarGroupConfigurator getConfigurator() {
        return this;
    }
}

