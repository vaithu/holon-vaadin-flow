package com.holonplatform.vaadin.flow.ai.internal;

import com.holonplatform.vaadin.flow.ai.builders.AIChatClientConfigurator;
import com.holonplatform.vaadin.flow.ai.components.AIChatClient;

import java.util.Objects;

/**
 * Default {@link AIChatClientConfigurator.BaseAIChatClientConfigurator} implementation that
 * configures an existing, already-assembled {@link AIChatClient} component.
 *
 * <p>Size, style, and component attributes are applied directly to the component's content
 * {@code Div} as each setter is called. AI-specific setters (controller, listeners, streaming,
 * ...) only make sense before {@link AIChatClient#assemble} has run, so this configurator only
 * covers the {@link com.vaadin.flow.component.ComponentConfigurator} /
 * {@link com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator} /
 * {@link com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator} surface.
 */
public class DefaultAIChatClientConfigurator
        extends AbstractAIChatClientConfigurator<AIChatClientConfigurator.BaseAIChatClientConfigurator>
        implements AIChatClientConfigurator.BaseAIChatClientConfigurator {

    private final AIChatClient existingChatClient;

    public DefaultAIChatClientConfigurator(AIChatClient chatClient) {
        this.existingChatClient = Objects.requireNonNull(chatClient, "chatClient must not be null");
    }

    @Override
    protected AIChatClientConfigurator.BaseAIChatClientConfigurator getConfigurator() {
        return this;
    }

    // Override size/style/id/visible methods to apply immediately to the existing component

    @Override
    public AIChatClientConfigurator.BaseAIChatClientConfigurator width(String width) {
        existingChatClient.getContent().setWidth(width);
        return getConfigurator();
    }

    @Override
    public AIChatClientConfigurator.BaseAIChatClientConfigurator height(String height) {
        existingChatClient.getContent().setHeight(height);
        return getConfigurator();
    }

    @Override
    public AIChatClientConfigurator.BaseAIChatClientConfigurator minWidth(String minWidth) {
        existingChatClient.getContent().setMinWidth(minWidth);
        return getConfigurator();
    }

    @Override
    public AIChatClientConfigurator.BaseAIChatClientConfigurator maxWidth(String maxWidth) {
        existingChatClient.getContent().setMaxWidth(maxWidth);
        return getConfigurator();
    }

    @Override
    public AIChatClientConfigurator.BaseAIChatClientConfigurator minHeight(String minHeight) {
        existingChatClient.getContent().setMinHeight(minHeight);
        return getConfigurator();
    }

    @Override
    public AIChatClientConfigurator.BaseAIChatClientConfigurator maxHeight(String maxHeight) {
        existingChatClient.getContent().setMaxHeight(maxHeight);
        return getConfigurator();
    }

    @Override
    public AIChatClientConfigurator.BaseAIChatClientConfigurator styleName(String name) {
        if (name != null) existingChatClient.getContent().addClassName(name);
        return getConfigurator();
    }

    @Override
    public AIChatClientConfigurator.BaseAIChatClientConfigurator styleNames(String... names) {
        if (names != null) {
            for (String name : names) {
                if (name != null) existingChatClient.getContent().addClassName(name);
            }
        }
        return getConfigurator();
    }

    @Override
    public AIChatClientConfigurator.BaseAIChatClientConfigurator id(String id) {
        existingChatClient.setId(id);
        return getConfigurator();
    }

    @Override
    public AIChatClientConfigurator.BaseAIChatClientConfigurator visible(boolean visible) {
        existingChatClient.setVisible(visible);
        return getConfigurator();
    }

}
