package com.holonplatform.vaadin.flow.ai.internal;

import com.holonplatform.vaadin.flow.ai.builders.AIAssistantBuilder;
import com.holonplatform.vaadin.flow.ai.components.AIAssistant;
import com.vaadin.flow.component.ai.provider.LLMProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Default implementation of {@link AIAssistantBuilder}.
 *
 * <p>Collects all configuration then calls {@link AIAssistant#assemble} on {@link #build()}.
 */
public final class DefaultAIAssistantBuilder extends AbstractAIAssistantConfigurator<AIAssistantBuilder>
        implements AIAssistantBuilder {

    private final LLMProvider provider;
    private final String systemPrompt;
    private final List<Consumer<AIAssistant>> postProcessors = new ArrayList<>();

    /**
     * Creates a builder for the given provider and mandatory system prompt.
     *
     * @param provider     the LLM provider (not null)
     * @param systemPrompt the system prompt (not null, not blank)
     */
    public DefaultAIAssistantBuilder(LLMProvider provider, String systemPrompt) {
        Objects.requireNonNull(provider, "provider must not be null");
        if (systemPrompt == null || systemPrompt.isBlank()) {
            throw new IllegalArgumentException(
                    "A system prompt is required — never build an AIOrchestrator without one");
        }
        this.provider = provider;
        this.systemPrompt = systemPrompt;
    }

    @Override
    protected AIAssistantBuilder getConfigurator() {
        return this;
    }

    @Override
    public AIAssistantBuilder withBuildPostProcessor(Consumer<AIAssistant> postProcessor) {
        Objects.requireNonNull(postProcessor, "Post-processor must not be null");
        postProcessors.add(postProcessor);
        return this;
    }

    @Override
    public AIAssistant build() {
        AIAssistant assistant = new AIAssistant();
        assistant.assemble(provider, systemPrompt, controller, userName, assistantName, fileReceiver, streaming,
                metadata);
        applyComponentConfig(assistant);
        postProcessors.forEach(pp -> pp.accept(assistant));
        return assistant;
    }

}
