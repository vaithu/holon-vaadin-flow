package com.holonplatform.vaadin.flow.ai.internal;

import com.holonplatform.vaadin.flow.ai.builders.AIChatClientBuilder;
import com.holonplatform.vaadin.flow.ai.components.AIChatClient;
import com.vaadin.flow.component.ai.provider.LLMProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Default implementation of {@link AIChatClientBuilder}.
 *
 * <p>Collects all configuration then calls {@link AIChatClient#assemble} on {@link #build()}.
 */
public final class DefaultAIChatClientBuilder extends AbstractAIChatClientConfigurator<AIChatClientBuilder>
        implements AIChatClientBuilder {

    private final LLMProvider provider;
    private final String systemPrompt;
    private final List<Consumer<AIChatClient>> postProcessors = new ArrayList<>();

    /**
     * Creates a builder for the given provider and mandatory system prompt.
     *
     * @param provider     the LLM provider (not null)
     * @param systemPrompt the system prompt (not null, not blank)
     */
    public DefaultAIChatClientBuilder(LLMProvider provider, String systemPrompt) {
        Objects.requireNonNull(provider, "provider must not be null");
        if (systemPrompt == null || systemPrompt.isBlank()) {
            throw new IllegalArgumentException(
                    "A system prompt is required — never build an AIOrchestrator without one");
        }
        this.provider = provider;
        this.systemPrompt = systemPrompt;
    }

    @Override
    protected AIChatClientBuilder getConfigurator() {
        return this;
    }

    @Override
    public AIChatClientBuilder withBuildPostProcessor(Consumer<AIChatClient> postProcessor) {
        Objects.requireNonNull(postProcessor, "Post-processor must not be null");
        postProcessors.add(postProcessor);
        return this;
    }

    @Override
    public AIChatClient build() {
        AIChatClient chatClient = new AIChatClient();
        chatClient.assemble(provider, systemPrompt, userName, assistantName, streaming, metadata, requestListener,
                responseListener, attachmentClickListener, controller, clipboardPasteEnabled, maxAttachments,
                maxFileSize, acceptedMimeTypePrefixes, history, historyAttachments);
        applyComponentConfig(chatClient);
        postProcessors.forEach(pp -> pp.accept(chatClient));
        return chatClient;
    }

}
