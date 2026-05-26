package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.AvatarGroupBuilder;
import com.vaadin.flow.component.avatar.AvatarGroup;

/**
 * Default {@link AvatarGroupBuilder} implementation.
 *
 * @since 10.0.0
 */
public class DefaultAvatarGroupBuilder extends AbstractAvatarGroupConfigurator<AvatarGroupBuilder>
        implements AvatarGroupBuilder {

    public DefaultAvatarGroupBuilder() {
        super(new AvatarGroup());
    }

    @Override
    protected AvatarGroupBuilder getConfigurator() {
        return this;
    }

    @Override
    public AvatarGroup build() {
        return getComponent();
    }
}

