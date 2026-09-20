package com.holonplatform.vaadin.flow.ai.components;

import com.holonplatform.vaadin.flow.ai.builders.AIChatClientBuilder;
import com.holonplatform.vaadin.flow.ai.i18n.AIAssistantI18N;
import com.holonplatform.vaadin.flow.ai.upload.ClipboardAwareFileReceiver;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Composite;

import com.vaadin.flow.component.ai.common.AIAttachment;
import com.vaadin.flow.component.ai.orchestrator.AIOrchestrator;
import com.vaadin.flow.component.ai.orchestrator.AttachmentClickListener;
import com.vaadin.flow.component.ai.orchestrator.RequestListener;
import com.vaadin.flow.component.ai.orchestrator.ResponseListener;
import com.vaadin.flow.component.ai.provider.LLMProvider;
import com.vaadin.flow.component.ai.provider.SpringAILLMProvider;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageInputI18n;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.function.SerializableSupplier;

import java.io.Serial;
import java.util.List;

/**
 * A generic, domain-agnostic AI chat component: it accepts free-text messages, dropped/browsed
 * files, and images pasted directly from the clipboard, sends everything to an LLM through the
 * Vaadin AI {@link AIOrchestrator}, and renders the (optionally streamed) response.
 *
 * <p>Unlike {@link AIAssistant} — which is designed to be paired with a Holon-Datastore-backed
 * {@code AIController} exposing domain-specific tools (query a Grid, fill a Form, ...) — this
 * component has <b>no</b> notion of a controller or domain tools: it is the "just chat, with any
 * kind of input" building block, useful for general assistance, document Q&amp;A, image
 * description, etc. Combine it with an {@code AIController} yourself (via
 * {@link com.holonplatform.vaadin.flow.ai.builders.AIChatClientConfigurator#withController(com.vaadin.flow.component.ai.orchestrator.AIController)})
 * if you also need tool calling.
 *
 * <h3>Multi-modal input</h3>
 * <ul>
 *   <li><b>Text</b> — a {@link MessageInput}, submitted with Enter.</li>
 *   <li><b>Images / files (drag &amp; drop or click-to-browse)</b> — a visible {@link Upload}
 *       area, rendered above the message input.</li>
 *   <li><b>Clipboard content</b> — paste an image (e.g. a screenshot) anywhere in the component
 *       and it is automatically queued as an attachment for the next message, exactly like a
 *       dropped file — see {@link ClipboardAwareFileReceiver}.</li>
 * </ul>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * AIChatClient chat = AIChatClient.builder(llmProvider,
 *         "You are a helpful general-purpose assistant. Be concise.")
 *     .fullSize()
 *     .build();
 * add(chat);
 * }</pre>
 *
 * @see AIChatClientBuilder
 * @see ClipboardAwareFileReceiver
 */
public class AIChatClient extends Composite<Div> {

    @Serial
    private static final long serialVersionUID = 1L;

    private final MessageList messageList = new MessageList();
    private final MessageInput messageInput = new MessageInput();
    private final ClipboardAwareFileReceiver fileReceiver = new ClipboardAwareFileReceiver();

    private AIOrchestrator orchestrator;

    /**
     * Package-internal constructor — use {@link #builder(LLMProvider, String)} to create
     * instances.
     */
    public AIChatClient() {
        getContent().addClassName("ai-chat-client");
        messageList.setMarkdown(true);
        messageList.getElement().setAttribute("aria-label", LocalizationProvider.localize(
                "Conversation with the assistant", AIAssistantI18N.CHAT_CLIENT_MESSAGES_ARIA_LABEL));
        messageInput.setI18n(new MessageInputI18n().setMessage(LocalizationProvider.localize(
                "Ask anything — you can also drop or paste a file...",
                AIAssistantI18N.CHAT_CLIENT_INPUT_PLACEHOLDER)));
    }

    /**
     * Assembles the component. Called exactly once by {@code AIChatClientBuilder.build()}.
     *
     * @param provider     the LLM provider (not null)
     * @param systemPrompt the mandatory system prompt (not null, not blank)
     * @param userName     display name for user messages
     * @param assistantName display name for assistant messages
     * @param streaming     whether streaming responses are enabled (requires {@code @Push})
     * @param metadata      an optional supplier of additional per-turn context
     * @param requestListener  an optional listener invoked right before each request is sent
     * @param responseListener an optional listener invoked once the (streamed) response completes
     * @param attachmentClickListener an optional listener invoked when a rendered attachment is
     *                                clicked in the message list
     * @param controller       an optional {@link com.vaadin.flow.component.ai.orchestrator.AIController}
     *                         contributing domain tools, for callers that need tool calling on top
     *                         of plain multi-modal chat
     * @param clipboardPasteEnabled whether clipboard-paste capture is enabled
     * @param maxAttachments        maximum number of pending attachments held at once
     * @param maxFileSize           maximum size, in bytes, accepted for a single attachment
     * @param acceptedMimeTypePrefixes accepted MIME type prefixes, or {@code null} for any type
     * @param history            a previously persisted conversation to restore, or an empty list
     * @param historyAttachments attachments for the restored conversation, keyed by message id
     */
    public void assemble(LLMProvider provider, String systemPrompt, String userName, String assistantName,
            boolean streaming, SerializableSupplier<String> metadata, RequestListener requestListener,
            ResponseListener responseListener, AttachmentClickListener attachmentClickListener,
            com.vaadin.flow.component.ai.orchestrator.AIController controller, boolean clipboardPasteEnabled,
            int maxAttachments, long maxFileSize, java.util.Set<String> acceptedMimeTypePrefixes,
            List<com.vaadin.flow.component.ai.common.ChatMessage> history,
            java.util.Map<String, List<AIAttachment>> historyAttachments) {
        if (provider == null) {
            throw new IllegalArgumentException("An LLMProvider is required");
        }
        if (systemPrompt == null || systemPrompt.isBlank()) {
            throw new IllegalArgumentException(
                    "A system prompt is required — never build an AIOrchestrator without one");
        }

        if (provider instanceof SpringAILLMProvider springProvider) {
            springProvider.setStreaming(streaming);
        }

        fileReceiver.setClipboardPasteEnabled(clipboardPasteEnabled);
        fileReceiver.setMaxAttachments(maxAttachments);
        fileReceiver.setMaxFileSize(maxFileSize);
        fileReceiver.setAcceptedMimeTypePrefixes(acceptedMimeTypePrefixes);

        AIOrchestrator.Builder builder = AIOrchestrator.builder(provider, systemPrompt)
                .withMessageList(messageList)
                .withInput(messageInput)
                .withFileReceiver(fileReceiver)
                .withUserName(userName != null ? userName
                        : LocalizationProvider.localize("You", AIAssistantI18N.CHAT_CLIENT_USER_NAME))
                .withAssistantName(assistantName != null ? assistantName
                        : LocalizationProvider.localize("Assistant", AIAssistantI18N.CHAT_CLIENT_ASSISTANT_NAME));

        if (metadata != null) {
            builder = builder.withMetadata(metadata);
        }
        if (requestListener != null) {
            builder = builder.withRequestListener(requestListener);
        }
        if (responseListener != null) {
            builder = builder.withResponseListener(responseListener);
        }
        if (attachmentClickListener != null) {
            builder = builder.withAttachmentClickListener(attachmentClickListener);
        }
        if (controller != null) {
            builder = builder.withController(controller);
        }
        if (history != null && !history.isEmpty()) {
            builder = builder.withHistory(history,
                    historyAttachments != null ? historyAttachments : java.util.Collections.emptyMap());
        }

        this.orchestrator = builder.build();

        // The orchestrator is intentionally NOT added to the layout — it is non-visual.
        getContent().add(fileReceiver, messageList, messageInput);
    }

    /**
     * Returns a fluent builder for an {@link AIChatClient} component.
     *
     * @param provider     the LLM provider (not null)
     * @param systemPrompt the mandatory system prompt describing the assistant's role, tone and
     *                     constraints (not null, not blank)
     * @return a new {@link AIChatClientBuilder}
     */
    public static AIChatClientBuilder builder(LLMProvider provider, String systemPrompt) {
        return AIChatClientBuilder.create(provider, systemPrompt);
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
     * Returns the underlying {@link Upload} component used for drag &amp; drop / click-to-browse
     * file attachments.
     *
     * @return the upload component
     */
    public Upload getUpload() {
        return fileReceiver.getUpload();
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
     * Returns the current conversation history, as tracked by the {@link AIOrchestrator} — every
     * user and assistant message exchanged so far in this component instance (plus anything
     * restored via {@link com.holonplatform.vaadin.flow.ai.builders.AIChatClientConfigurator#withHistory}).
     *
     * <p>The history lives only as long as this component instance does (i.e. for the lifetime of
     * the UI/session it belongs to). To retain a conversation across page reloads or server
     * restarts, persist the returned list (e.g. through a Holon {@code Datastore}) and pass it
     * back in via {@code withHistory(...)} the next time the component is built.
     *
     * @return the ordered list of exchanged messages (never {@code null})
     */
    public List<com.vaadin.flow.component.ai.common.ChatMessage> getHistory() {
        return requireOrchestrator().getHistory();
    }

    /**
     * Programmatically sends a prompt to the assistant, bypassing the chat UI.
     *
     * @param message the prompt text (not null, not blank)
     */
    public void prompt(String message) {
        requireOrchestrator().prompt(message);
    }

    /**
     * Programmatically sends a prompt together with file attachments.
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
                    "The AIChatClient has not been assembled yet — build it through builder(...) first");
        }
        return orchestrator;
    }

}
