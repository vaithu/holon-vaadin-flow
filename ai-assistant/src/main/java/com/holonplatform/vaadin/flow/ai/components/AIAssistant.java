package com.holonplatform.vaadin.flow.ai.components;

import com.holonplatform.vaadin.flow.ai.builders.AIAssistantBuilder;
import com.holonplatform.vaadin.flow.ai.i18n.AIAssistantI18N;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Composite;

import com.vaadin.flow.component.ai.common.AIAttachment;
import com.vaadin.flow.component.ai.orchestrator.AIController;
import com.vaadin.flow.component.ai.orchestrator.AIOrchestrator;
import com.vaadin.flow.component.ai.provider.LLMProvider;
import com.vaadin.flow.component.ai.provider.SpringAILLMProvider;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageInputI18n;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.function.SerializableSupplier;

import java.io.Serial;
import java.util.List;

/**
 * A ready-to-use AI chat assistant component, wrapping a Vaadin AI {@link AIOrchestrator} around
 * a {@link MessageList} + {@link MessageInput} pair.
 *
 * <p>{@code MessageList} / {@code MessageInput} are the only raw Vaadin components used here —
 * Holon Vaadin Flow has no chat equivalent. Everything else (route guarding, data access, i18n)
 * follows the usual Holon Platform conventions.
 *
 * <p>The underlying {@link AIOrchestrator} is a non-visual coordination object: it is created and
 * held internally by this component, but never added to the layout itself.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * AIAssistant assistant = AIAssistant.builder(llmProvider,
 *         "You are a helpful assistant for the customer module. Answer concisely.")
 *     .withController(datastoreAIController)
 *     .fullSize()
 *     .build();
 * add(assistant);
 * }</pre>
 *
 * @see AIAssistantBuilder
 */
public class AIAssistant extends Composite<Div> {

    @Serial
    private static final long serialVersionUID = 1L;

    private final MessageList messageList = new MessageList();
    private final MessageInput messageInput = new MessageInput();

    private AIOrchestrator orchestrator;

    /**
     * Package-internal constructor — use {@link #builder(LLMProvider, String)} to create
     * instances.
     */
    public AIAssistant() {
        getContent().addClassName("ai-assistant");
        messageList.setMarkdown(true);
        messageList.getElement().setAttribute("aria-label",
                LocalizationProvider.localize("Conversation with the assistant", AIAssistantI18N.MESSAGES_ARIA_LABEL));
        messageInput.setI18n(new MessageInputI18n().setMessage(
                LocalizationProvider.localize("Ask a question...", AIAssistantI18N.INPUT_PLACEHOLDER)));
    }

    /**
     * Assembles the component. Called exactly once by {@code AIAssistantBuilder.build()}.
     *
     * @param provider     the LLM provider (not null)
     * @param systemPrompt the mandatory system prompt (not null, not blank)
     * @param controller   an optional Holon-Datastore-backed {@link AIController}
     * @param userName     display name for user messages
     * @param assistantName display name for assistant messages
     * @param fileReceiver  an optional {@link Upload} component enabling chat file attachments
     * @param streaming     whether streaming responses are enabled (requires {@code @Push})
     * @param metadata      an optional supplier of additional per-turn context (tenant, locale,
     *                      current user, ...), see {@code withMetadata}
     */
    public void assemble(LLMProvider provider, String systemPrompt, AIController controller,
                          String userName, String assistantName, Upload fileReceiver, boolean streaming,
                          SerializableSupplier<String> metadata) {
        if (provider == null) {
            throw new IllegalArgumentException("An LLMProvider is required");
        }
        if (systemPrompt == null || systemPrompt.isBlank()) {
            throw new IllegalArgumentException(
                    "A system prompt is required — never build an AIOrchestrator without one");
        }

        // setStreaming() is not part of the LLMProvider contract itself — only SpringAILLMProvider
        // currently exposes it (streaming is enabled by default for every provider).
        if (provider instanceof SpringAILLMProvider springProvider) {
            springProvider.setStreaming(streaming);
        }

        AIOrchestrator.Builder builder = AIOrchestrator.builder(provider, systemPrompt)
                .withMessageList(messageList)
                .withInput(messageInput)
                .withUserName(userName != null ? userName
                        : LocalizationProvider.localize("You", AIAssistantI18N.USER_NAME))
                .withAssistantName(assistantName != null ? assistantName
                        : LocalizationProvider.localize("Assistant", AIAssistantI18N.ASSISTANT_NAME));

        if (controller != null) {
            builder = builder.withController(controller);
        }
        if (fileReceiver != null) {
            builder = builder.withFileReceiver(fileReceiver);
        }
        if (metadata != null) {
            builder = builder.withMetadata(metadata);
        }

        this.orchestrator = builder.build();

        // The orchestrator is intentionally NOT added to the layout — it is non-visual.
        getContent().add(messageList, messageInput);
        if (fileReceiver != null) {
            getContent().addComponentAsFirst(fileReceiver);
        }
    }

    /**
     * Returns a fluent builder for an {@link AIAssistant} component.
     *
     * @param provider     the LLM provider (not null)
     * @param systemPrompt the mandatory system prompt describing the assistant's role, tone and
     *                     constraints (not null, not blank)
     * @return a new {@link AIAssistantBuilder}
     */
    public static AIAssistantBuilder builder(LLMProvider provider, String systemPrompt) {
        return AIAssistantBuilder.create(provider, systemPrompt);
    }

    /**
     * Returns the underlying {@link MessageList}.
     *
     * @return the message list
     */
    public MessageList getMessageList() {
        return messageList;
    }

    /**
     * Returns the underlying {@link MessageInput}.
     *
     * @return the message input
     */
    public MessageInput getMessageInput() {
        return messageInput;
    }

    /**
     * Returns the underlying {@link AIOrchestrator}, once {@link #assemble} has been called.
     *
     * @return the orchestrator, or {@code null} if not yet assembled
     */
    public AIOrchestrator getOrchestrator() {
        return orchestrator;
    }

    /**
     * Programmatically sends a prompt to the assistant, bypassing the chat UI — useful for
     * "fill this form from the receipt the user just uploaded elsewhere in the page" flows where
     * no {@link MessageInput} submission is involved.
     *
     * @param message the prompt text (not null, not blank)
     * @throws IllegalStateException if the component has not been assembled yet (i.e. built via
     *                                {@link #builder(LLMProvider, String)})
     */
    public void prompt(String message) {
        requireOrchestrator().prompt(message);
    }

    /**
     * Programmatically sends a prompt together with file attachments — e.g. a document the
     * application generated, or a file captured through a standalone {@link Upload} elsewhere in
     * the view (not the one configured via {@code withFileReceiver}, whose attachments are
     * queued automatically for the next chat submission instead).
     *
     * @param message     the prompt text (not null, not blank)
     * @param attachments the attachments to send with this prompt (not null, may be empty)
     */
    public void prompt(String message, List<AIAttachment> attachments) {
        requireOrchestrator().prompt(message, attachments);
    }

    private AIOrchestrator requireOrchestrator() {
        if (orchestrator == null) {
            throw new IllegalStateException(
                    "The AIAssistant has not been assembled yet — build it through builder(...) first");
        }
        return orchestrator;
    }

}
