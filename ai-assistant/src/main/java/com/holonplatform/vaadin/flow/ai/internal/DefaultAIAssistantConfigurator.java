package com.holonplatform.vaadin.flow.ai.internal;

import com.holonplatform.vaadin.flow.ai.builders.AIAssistantConfigurator;
import com.holonplatform.vaadin.flow.ai.components.AIAssistant;

import java.util.Objects;

/**
 * Default {@link AIAssistantConfigurator.BaseAIAssistantConfigurator} implementation that
 * configures an existing, already-assembled {@link AIAssistant} component.
 *
 * <p>Size, style, and component attributes are applied directly to the component's content
 * {@code Div} as each setter is called.
 */
public class DefaultAIAssistantConfigurator
        extends AbstractAIAssistantConfigurator<AIAssistantConfigurator.BaseAIAssistantConfigurator>
        implements AIAssistantConfigurator.BaseAIAssistantConfigurator {

    private final AIAssistant existingAssistant;

    public DefaultAIAssistantConfigurator(AIAssistant assistant) {
        this.existingAssistant = Objects.requireNonNull(assistant, "assistant must not be null");
    }

    @Override
    protected AIAssistantConfigurator.BaseAIAssistantConfigurator getConfigurator() {
        return this;
    }

    // Override size/style/id/visible methods to apply immediately to the existing component

    @Override
    public AIAssistantConfigurator.BaseAIAssistantConfigurator width(String width) {
        existingAssistant.getContent().setWidth(width);
        return getConfigurator();
    }

    @Override
    public AIAssistantConfigurator.BaseAIAssistantConfigurator height(String height) {
        existingAssistant.getContent().setHeight(height);
        return getConfigurator();
    }

    @Override
    public AIAssistantConfigurator.BaseAIAssistantConfigurator minWidth(String minWidth) {
        existingAssistant.getContent().setMinWidth(minWidth);
        return getConfigurator();
    }

    @Override
    public AIAssistantConfigurator.BaseAIAssistantConfigurator maxWidth(String maxWidth) {
        existingAssistant.getContent().setMaxWidth(maxWidth);
        return getConfigurator();
    }

    @Override
    public AIAssistantConfigurator.BaseAIAssistantConfigurator minHeight(String minHeight) {
        existingAssistant.getContent().setMinHeight(minHeight);
        return getConfigurator();
    }

    @Override
    public AIAssistantConfigurator.BaseAIAssistantConfigurator maxHeight(String maxHeight) {
        existingAssistant.getContent().setMaxHeight(maxHeight);
        return getConfigurator();
    }

    @Override
    public AIAssistantConfigurator.BaseAIAssistantConfigurator styleName(String name) {
        if (name != null) existingAssistant.getContent().addClassName(name);
        return getConfigurator();
    }

    @Override
    public AIAssistantConfigurator.BaseAIAssistantConfigurator styleNames(String... names) {
        if (names != null) {
            for (String name : names) {
                if (name != null) existingAssistant.getContent().addClassName(name);
            }
        }
        return getConfigurator();
    }

    @Override
    public AIAssistantConfigurator.BaseAIAssistantConfigurator id(String id) {
        existingAssistant.setId(id);
        return getConfigurator();
    }

    @Override
    public AIAssistantConfigurator.BaseAIAssistantConfigurator visible(boolean visible) {
        existingAssistant.setVisible(visible);
        return getConfigurator();
    }

}
