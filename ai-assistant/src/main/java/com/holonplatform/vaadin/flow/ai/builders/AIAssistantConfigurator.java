package com.holonplatform.vaadin.flow.ai.builders;

import com.holonplatform.vaadin.flow.ai.components.AIAssistant;
import com.holonplatform.vaadin.flow.ai.internal.DefaultAIAssistantConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.vaadin.flow.component.ai.orchestrator.AIController;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.function.SerializableSupplier;

/**
 * Configurator for {@link AIAssistant} components.
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 */
public interface AIAssistantConfigurator<C extends AIAssistantConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    /**
     * Registers the (single) {@link AIController} contributing tools to this assistant. Typically
     * a {@code DatastoreAIController} delegating to a Holon {@code Datastore} /
     * {@code BeanDatastoreHelper} / {@code *Service}.
     *
     * <p>Only one controller may be attached per assistant instance — attempting to reuse the
     * same controller across two assistants throws {@code IllegalStateException} at build time.
     *
     * @param controller the AI controller (not null)
     * @return this configurator
     */
    C withController(AIController controller);

    /**
     * Enables file attachment support: the given {@link Upload} component is wired to the
     * assistant so users can attach files (images, PDFs, documents, ...) to their chat messages.
     *
     * <p>Per Vaadin AI semantics, the orchestrator installs its own in-memory upload handler on
     * the given component — it must not already have one configured — and renders it above the
     * message input.
     *
     * @param fileUpload the upload component to wire to the assistant (not null)
     * @return this configurator
     */
    C withFileReceiver(Upload fileUpload);

    /**
     * Registers a supplier of additional contextual information (tenant, locale, current user,
     * open work items, ...) that is appended to every LLM request. The supplier is invoked once
     * per conversation turn, right before the request is sent; returning {@code null} or a blank
     * string skips the context for that turn. Setting a supplier replaces the orchestrator's
     * default current-date-and-time line — include it yourself (e.g. via
     * {@code ZonedDateTime.now()}) if you still want it.
     *
     * @param metadata a supplier of additional context text, evaluated once per turn (not null)
     * @return this configurator
     */
    C withMetadata(SerializableSupplier<String> metadata);

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
     * Configure an existing {@link AIAssistant} component.
     *
     * @param assistant the component to configure (not null)
     * @return a new {@link BaseAIAssistantConfigurator}
     */
    static BaseAIAssistantConfigurator configure(AIAssistant assistant) {
        return new DefaultAIAssistantConfigurator(assistant);
    }

    interface BaseAIAssistantConfigurator extends AIAssistantConfigurator<BaseAIAssistantConfigurator> {
    }

}
