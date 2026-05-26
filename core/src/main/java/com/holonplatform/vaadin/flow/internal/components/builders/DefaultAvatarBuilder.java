package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.AvatarBuilder;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.server.streams.DownloadHandler;

/**
 * Default {@link AvatarBuilder} implementation.
 *
 * @since 10.0.0
 */
public class DefaultAvatarBuilder extends AbstractAvatarConfigurator<AvatarBuilder>
        implements AvatarBuilder {

    public DefaultAvatarBuilder(String name) {
        super(name != null ? new Avatar(name) : new Avatar());
    }

    public DefaultAvatarBuilder(String name, String imageUrl) {
        super(name != null ? new Avatar(name) : new Avatar());
        if (imageUrl != null) {
            getComponent().setImage(imageUrl);
        }
    }

    public DefaultAvatarBuilder(String name, DownloadHandler handler) {
        super(name != null ? new Avatar(name) : new Avatar());
        if (handler != null) {
            getComponent().setImageHandler(handler);
        }
    }

    @Override
    protected AvatarBuilder getConfigurator() {
        return this;
    }

    @Override
    public Avatar build() {
        return getComponent();
    }
}



