package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.AvatarConfigurator;
import com.vaadin.flow.component.avatar.Avatar;

/**
 * Default {@link AvatarConfigurator} implementation (for {@code AvatarConfigurator.configure(avatar)}).
 *
 * @since 10.0.0
 */
public class DefaultAvatarConfigurator extends AbstractAvatarConfigurator<AvatarConfigurator.BaseAvatarConfigurator>
        implements AvatarConfigurator.BaseAvatarConfigurator {

    public DefaultAvatarConfigurator(Avatar avatar) {
        super(avatar);
    }

    @Override
    protected AvatarConfigurator.BaseAvatarConfigurator getConfigurator() {
        return this;
    }
}

