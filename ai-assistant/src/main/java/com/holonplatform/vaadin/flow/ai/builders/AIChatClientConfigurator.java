package com.holonplatform.vaadin.flow.ai.builders;

import com.holonplatform.vaadin.flow.ai.components.AIChatClient;
import com.holonplatform.vaadin.flow.ai.internal.DefaultAIChatClientConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.vaadin.flow.component.ai.common.AIAttachment;
import com.vaadin.flow.component.ai.common.ChatMessage;
import com.vaadin.flow.component.ai.orchestrator.AIController;
import com.vaadin.flow.component.ai.orchestrator.AttachmentClickListener;
import com.vaadin.flow.component.ai.orchestrator.RequestListener;
import com.vaadin.flow.component.ai.orchestrator.ResponseListener;
import com.vaadin.flow.function.SerializableSupplier;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Configurator for {@link AIChatClient} components.
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 */
public interface AIChatClientConfigurator<C extends AIChatClientConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    /**
     * Attaches an optional {@link AIController} contributing domain tools, for callers that need
     * tool calling on top of plain multi-modal chat (e.g. a {@code DatastoreAIController}).
     * Unlike {@link com.holonplatform.vaadin.flow.ai.components.AIAssistant}, this is entirely
     * optional — {@link AIChatClient} works perfectly well as a plain chat window with no
     * controller at all.
     *
     * @param controller the AI controller (not null)
     * @return this configurator
     */
    C withController(AIController controller);

    /**
     * Registers a supplier of additional contextual information, evaluated once per conversation
     * turn. See {@code AIOrchestrator.Builder#withMetadata(SerializableSupplier)}.
     *
     * @param metadata a supplier of additional context text (not null)
     * @return this configurator
     */
    C withMetadata(SerializableSupplier<String> metadata);

    /**
     * Registers a listener invoked right before each request is sent to the LLM provider.
     *
     * @param listener the listener (not null)
     * @return this configurator
     */
    C withRequestListener(RequestListener listener);

    /**
     * Registers a listener invoked once the (possibly streamed) response completes, or fails.
     *
     * @param listener the listener (not null)
     * @return this configurator
     */
    C withResponseListener(ResponseListener listener);

    /**
     * Registers a listener invoked when the user clicks a rendered attachment in the message
     * list.
     *
     * @param listener the listener (not null)
     * @return this configurator
     */
    C withAttachmentClickListener(AttachmentClickListener listener);

    /**
     * Restores a previous conversation, both for the LLM context (so the model remembers earlier
     * turns) and for the rendered {@code MessageList} (so the user sees the past messages again).
     * Typical use: reload a conversation persisted across page reloads / server restarts —
     * e.g. fetched from a Holon {@code Datastore} — before the {@link AIChatClient} is built.
     *
     * @param history            the ordered conversation messages (not null, may be empty)
     * @param historyAttachments attachments keyed by {@link ChatMessage#messageId()}, for user
     *                           messages that included files/images (not null, may be empty)
     * @return this configurator
     * @see AIChatClient#getHistory()
     */
    C withHistory(List<ChatMessage> history, Map<String, List<AIAttachment>> historyAttachments);

    /**
     * Overrides the default display name used for user-authored messages (default: localized
     * "You").
     *
     * @param userName the display name
     * @return this configurator
     */
    C withUserName(String userName);

    /**
     * Overrides the default display name used for assistant-authored messages (default:
     * localized "Assistant").
     *
     * @param assistantName the display name
     * @return this configurator
     */
    C withAssistantName(String assistantName);

    /**
     * Enables or disables streaming responses (enabled by default). Streaming requires
     * {@code @Push} on the {@code AppShellConfigurator}; disable it if push is unavailable.
     *
     * @param streaming {@code true} to stream partial tokens as they arrive
     * @return this configurator
     */
    C streaming(boolean streaming);

    /**
     * Enables or disables clipboard-paste capture of images (enabled by default). See
     * {@link com.holonplatform.vaadin.flow.ai.upload.ClipboardAwareFileReceiver}.
     *
     * @param clipboardPasteEnabled {@code true} to enable capturing pasted files
     * @return this configurator
     */
    C clipboardPasteEnabled(boolean clipboardPasteEnabled);

    /**
     * Restricts accepted MIME types by prefix (e.g. {@code "image/"}) for both drag/drop-uploaded
     * and clipboard-pasted content. Defaults to accepting any type.
     *
     * @param mimeTypePrefixes accepted MIME type prefixes
     * @return this configurator
     */
    C acceptedMimeTypePrefixes(Set<String> mimeTypePrefixes);

    /**
     * Sets the maximum number of pending attachments held at once (default
     * {@value com.holonplatform.vaadin.flow.ai.upload.ClipboardAwareFileReceiver#DEFAULT_MAX_ATTACHMENTS}).
     *
     * @param maxAttachments the maximum attachment count, must be &gt; 0
     * @return this configurator
     */
    C maxAttachments(int maxAttachments);

    /**
     * Sets the maximum size, in bytes, accepted for a single attachment (default
     * {@value com.holonplatform.vaadin.flow.ai.upload.ClipboardAwareFileReceiver#DEFAULT_MAX_FILE_SIZE}).
     *
     * @param maxFileSize the maximum file size in bytes, must be &gt; 0
     * @return this configurator
     */
    C maxFileSize(long maxFileSize);

    /**
     * Configure an existing {@link AIChatClient} component.
     *
     * @param chatClient the component to configure (not null)
     * @return a new {@link BaseAIChatClientConfigurator}
     */
    static BaseAIChatClientConfigurator configure(AIChatClient chatClient) {
        return new DefaultAIChatClientConfigurator(chatClient);
    }

    interface BaseAIChatClientConfigurator extends AIChatClientConfigurator<BaseAIChatClientConfigurator> {
    }

}
