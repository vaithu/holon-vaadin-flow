package com.holonplatform.vaadin.flow.ai.builders;

import com.holonplatform.vaadin.flow.ai.components.AIChatClient;
import com.holonplatform.vaadin.flow.ai.internal.DefaultAIChatClientBuilder;
import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.vaadin.flow.component.ai.provider.LLMProvider;

/**
 * Fluent builder for {@link AIChatClient} components.
 *
 * <p>Obtained via {@link AIChatClient#builder(LLMProvider, String)}.
 *
 * <h3>Plain multi-modal chat</h3>
 * <pre>{@code
 * AIChatClient chat = AIChatClient.builder(provider, "You are a helpful assistant.")
 *     .fullSize()
 *     .build();
 * }</pre>
 *
 * <h3>Restrict attachments to images only</h3>
 * <pre>{@code
 * AIChatClient chat = AIChatClient.builder(provider, systemPrompt)
 *     .acceptedMimeTypePrefixes(Set.of("image/"))
 *     .maxFileSize(5 * 1024 * 1024)
 *     .build();
 * }</pre>
 */
public interface AIChatClientBuilder
        extends AIChatClientConfigurator<AIChatClientBuilder>, ComponentBuilder<AIChatClient, AIChatClientBuilder> {

    /**
     * Creates a new builder for the given LLM provider and mandatory system prompt.
     *
     * @param provider     the LLM provider (not null)
     * @param systemPrompt the system prompt (not null, not blank)
     * @return a new {@link AIChatClientBuilder}
     */
    static AIChatClientBuilder create(LLMProvider provider, String systemPrompt) {
        return new DefaultAIChatClientBuilder(provider, systemPrompt);
    }

}
