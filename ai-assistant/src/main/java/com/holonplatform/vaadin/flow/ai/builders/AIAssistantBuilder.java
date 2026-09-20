package com.holonplatform.vaadin.flow.ai.builders;

import com.holonplatform.vaadin.flow.ai.components.AIAssistant;
import com.holonplatform.vaadin.flow.ai.internal.DefaultAIAssistantBuilder;
import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.vaadin.flow.component.ai.provider.LLMProvider;

/**
 * Fluent builder for {@link AIAssistant} components.
 *
 * <p>Obtained via {@link AIAssistant#builder(LLMProvider, String)}.
 *
 * <h3>Datastore-backed tools</h3>
 * <pre>{@code
 * AIAssistant assistant = AIAssistant.builder(provider, "You are a helpful assistant...")
 *     .withController(DatastoreAIController.builder()
 *         .withTool(new CustomerFindByTierTool(customerService))
 *         .build())
 *     .fullSize()
 *     .build();
 * }</pre>
 *
 * <h3>File attachments</h3>
 * <pre>{@code
 * Upload upload = new Upload();
 * AIAssistant assistant = AIAssistant.builder(provider, systemPrompt)
 *     .withController(controller)
 *     .withFileReceiver(upload)
 *     .build();
 * }</pre>
 */
public interface AIAssistantBuilder
        extends AIAssistantConfigurator<AIAssistantBuilder>, ComponentBuilder<AIAssistant, AIAssistantBuilder> {

    /**
     * Creates a new builder for the given LLM provider and mandatory system prompt.
     *
     * @param provider     the LLM provider (not null)
     * @param systemPrompt the system prompt — set the assistant's role, tone, constraints and
     *                     domain rules; never build without one (not null, not blank)
     * @return a new {@link AIAssistantBuilder}
     */
    static AIAssistantBuilder create(LLMProvider provider, String systemPrompt) {
        return new DefaultAIAssistantBuilder(provider, systemPrompt);
    }

}
