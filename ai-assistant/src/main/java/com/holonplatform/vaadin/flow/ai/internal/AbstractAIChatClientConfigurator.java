package com.holonplatform.vaadin.flow.ai.internal;

import com.holonplatform.vaadin.flow.ai.builders.AIChatClientConfigurator;
import com.holonplatform.vaadin.flow.ai.components.AIChatClient;
import com.holonplatform.vaadin.flow.ai.upload.ClipboardAwareFileReceiver;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.ai.common.AIAttachment;
import com.vaadin.flow.component.ai.common.ChatMessage;
import com.vaadin.flow.component.ai.orchestrator.AIController;
import com.vaadin.flow.component.ai.orchestrator.AttachmentClickListener;
import com.vaadin.flow.component.ai.orchestrator.RequestListener;
import com.vaadin.flow.component.ai.orchestrator.ResponseListener;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.SerializableSupplier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Base {@link AIChatClientConfigurator} implementation that stores configuration state for
 * deferred application.
 *
 * <p>Because {@link AIChatClient} is assembled via {@code assemble()} after construction, all
 * configuration is stored in fields and applied when {@code build()} is called (or, in the
 * configurator variant, applied directly to an existing component).
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractAIChatClientConfigurator<C extends AIChatClientConfigurator<C>>
        implements AIChatClientConfigurator<C> {

    // ── Assembly-time state ─────────────────────────────────────────────────────

    protected AIController controller;
    protected String userName;
    protected String assistantName;
    protected boolean streaming = true;
    protected SerializableSupplier<String> metadata;
    protected RequestListener requestListener;
    protected ResponseListener responseListener;
    protected AttachmentClickListener attachmentClickListener;
    protected boolean clipboardPasteEnabled = true;
    protected Set<String> acceptedMimeTypePrefixes;
    protected int maxAttachments = ClipboardAwareFileReceiver.DEFAULT_MAX_ATTACHMENTS;
    protected long maxFileSize = ClipboardAwareFileReceiver.DEFAULT_MAX_FILE_SIZE;
    protected List<ChatMessage> history = Collections.emptyList();
    protected Map<String, List<AIAttachment>> historyAttachments = Collections.emptyMap();

    // ── Deferred component configuration ────────────────────────────────────────

    private String id;
    private boolean visible = true;
    private String width, height, minWidth, maxWidth, minHeight, maxHeight;
    private final List<String> classNames = new ArrayList<>();
    private final List<ComponentEventListener<AttachEvent>> attachListeners = new ArrayList<>();
    private final List<ComponentEventListener<DetachEvent>> detachListeners = new ArrayList<>();

    protected abstract C getConfigurator();

    // ── AIChatClientConfigurator methods ─────────────────────────────────────────

    @Override
    public C withController(AIController controller) {
        Objects.requireNonNull(controller, "controller must not be null");
        this.controller = controller;
        return getConfigurator();
    }

    @Override
    public C withMetadata(SerializableSupplier<String> metadata) {
        Objects.requireNonNull(metadata, "metadata must not be null");
        this.metadata = metadata;
        return getConfigurator();
    }

    @Override
    public C withRequestListener(RequestListener listener) {
        Objects.requireNonNull(listener, "listener must not be null");
        this.requestListener = listener;
        return getConfigurator();
    }

    @Override
    public C withResponseListener(ResponseListener listener) {
        Objects.requireNonNull(listener, "listener must not be null");
        this.responseListener = listener;
        return getConfigurator();
    }

    @Override
    public C withAttachmentClickListener(AttachmentClickListener listener) {
        Objects.requireNonNull(listener, "listener must not be null");
        this.attachmentClickListener = listener;
        return getConfigurator();
    }

    @Override
    public C withHistory(List<ChatMessage> history, Map<String, List<AIAttachment>> historyAttachments) {
        Objects.requireNonNull(history, "history must not be null");
        Objects.requireNonNull(historyAttachments, "historyAttachments must not be null");
        this.history = history;
        this.historyAttachments = historyAttachments;
        return getConfigurator();
    }

    @Override
    public C withUserName(String userName) {
        this.userName = userName;
        return getConfigurator();
    }

    @Override
    public C withAssistantName(String assistantName) {
        this.assistantName = assistantName;
        return getConfigurator();
    }

    @Override
    public C streaming(boolean streaming) {
        this.streaming = streaming;
        return getConfigurator();
    }

    @Override
    public C clipboardPasteEnabled(boolean clipboardPasteEnabled) {
        this.clipboardPasteEnabled = clipboardPasteEnabled;
        return getConfigurator();
    }

    @Override
    public C acceptedMimeTypePrefixes(Set<String> mimeTypePrefixes) {
        this.acceptedMimeTypePrefixes = mimeTypePrefixes;
        return getConfigurator();
    }

    @Override
    public C maxAttachments(int maxAttachments) {
        if (maxAttachments <= 0) {
            throw new IllegalArgumentException("maxAttachments must be > 0");
        }
        this.maxAttachments = maxAttachments;
        return getConfigurator();
    }

    @Override
    public C maxFileSize(long maxFileSize) {
        if (maxFileSize <= 0) {
            throw new IllegalArgumentException("maxFileSize must be > 0");
        }
        this.maxFileSize = maxFileSize;
        return getConfigurator();
    }

    // ── ComponentConfigurator methods ────────────────────────────────────────────

    @Override
    public C id(String id) {
        this.id = id;
        return getConfigurator();
    }

    @Override
    public C visible(boolean visible) {
        this.visible = visible;
        return getConfigurator();
    }

    @Override
    public C elementConfiguration(Consumer<Element> element) {
        // Cannot apply before component is assembled; stored state takes precedence
        return getConfigurator();
    }

    @Override
    public C withAttachListener(ComponentEventListener<AttachEvent> listener) {
        Objects.requireNonNull(listener, "listener must not be null");
        attachListeners.add(listener);
        return getConfigurator();
    }

    @Override
    public C withDetachListener(ComponentEventListener<DetachEvent> listener) {
        Objects.requireNonNull(listener, "listener must not be null");
        detachListeners.add(listener);
        return getConfigurator();
    }

    @Override
    public C withThemeName(String themeName) {
        return getConfigurator();
    }

    @Override
    public C withEventListener(String eventType, DomEventListener listener) {
        return getConfigurator();
    }

    @Override
    public C withEventListener(String eventType, DomEventListener listener, String filter) {
        return getConfigurator();
    }

    // ── HasSizeConfigurator methods ──────────────────────────────────────────────

    @Override
    public C width(String width) {
        this.width = width;
        return getConfigurator();
    }

    @Override
    public C height(String height) {
        this.height = height;
        return getConfigurator();
    }

    @Override
    public C minWidth(String minWidth) {
        this.minWidth = minWidth;
        return getConfigurator();
    }

    @Override
    public C maxWidth(String maxWidth) {
        this.maxWidth = maxWidth;
        return getConfigurator();
    }

    @Override
    public C minHeight(String minHeight) {
        this.minHeight = minHeight;
        return getConfigurator();
    }

    @Override
    public C maxHeight(String maxHeight) {
        this.maxHeight = maxHeight;
        return getConfigurator();
    }

    // ── HasStyleConfigurator methods ─────────────────────────────────────────────

    @Override
    public C styleNames(String... names) {
        if (names != null) {
            for (String name : names) {
                if (name != null) classNames.add(name);
            }
        }
        return getConfigurator();
    }

    @Override
    public C styleName(String name) {
        if (name != null) classNames.add(name);
        return getConfigurator();
    }

    // ── Apply stored configuration to an assembled AIChatClient ──────────────────

    /**
     * Applies all stored configuration to the given (already assembled) {@link AIChatClient}.
     *
     * @param chatClient the assembled component (not null)
     */
    protected void applyComponentConfig(AIChatClient chatClient) {
        if (id != null) chatClient.setId(id);
        chatClient.setVisible(visible);
        var content = chatClient.getContent();
        if (width != null) content.setWidth(width);
        if (height != null) content.setHeight(height);
        if (minWidth != null) content.setMinWidth(minWidth);
        if (maxWidth != null) content.setMaxWidth(maxWidth);
        if (minHeight != null) content.setMinHeight(minHeight);
        if (maxHeight != null) content.setMaxHeight(maxHeight);
        classNames.forEach(content::addClassName);
        attachListeners.forEach(chatClient::addAttachListener);
        detachListeners.forEach(chatClient::addDetachListener);
    }

}
