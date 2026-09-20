package com.holonplatform.vaadin.flow.ai.internal;

import com.holonplatform.vaadin.flow.ai.builders.AIAssistantConfigurator;
import com.holonplatform.vaadin.flow.ai.components.AIAssistant;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.ai.orchestrator.AIController;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.SerializableSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Base {@link AIAssistantConfigurator} implementation that stores configuration state for
 * deferred application.
 *
 * <p>Because {@link AIAssistant} is assembled via {@code assemble()} after construction, all
 * configuration is stored in fields and applied when {@code build()} is called (or, in the
 * configurator variant, applied directly to an existing component).
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractAIAssistantConfigurator<C extends AIAssistantConfigurator<C>>
        implements AIAssistantConfigurator<C> {

    // ── Assembly-time state ─────────────────────────────────────────────────────

    protected AIController controller;
    protected Upload fileReceiver;
    protected String userName;
    protected String assistantName;
    protected boolean streaming = true;
    protected SerializableSupplier<String> metadata;

    // ── Deferred component configuration ────────────────────────────────────────

    private String id;
    private boolean visible = true;
    private String width, height, minWidth, maxWidth, minHeight, maxHeight;
    private final List<String> classNames = new ArrayList<>();
    private final List<ComponentEventListener<AttachEvent>> attachListeners = new ArrayList<>();
    private final List<ComponentEventListener<DetachEvent>> detachListeners = new ArrayList<>();

    protected abstract C getConfigurator();

    // ── AIAssistantConfigurator methods ─────────────────────────────────────────

    @Override
    public C withController(AIController controller) {
        Objects.requireNonNull(controller, "controller must not be null");
        this.controller = controller;
        return getConfigurator();
    }

    @Override
    public C withFileReceiver(Upload fileReceiver) {
        Objects.requireNonNull(fileReceiver, "fileReceiver must not be null");
        this.fileReceiver = fileReceiver;
        return getConfigurator();
    }

    @Override
    public C withMetadata(SerializableSupplier<String> metadata) {
        Objects.requireNonNull(metadata, "metadata must not be null");
        this.metadata = metadata;
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

    // ── Apply stored configuration to an assembled AIAssistant ───────────────────

    /**
     * Applies all stored configuration to the given (already assembled) {@link AIAssistant}.
     *
     * @param assistant the assembled component (not null)
     */
    protected void applyComponentConfig(AIAssistant assistant) {
        if (id != null) assistant.setId(id);
        assistant.setVisible(visible);
        var content = assistant.getContent();
        if (width != null) content.setWidth(width);
        if (height != null) content.setHeight(height);
        if (minWidth != null) content.setMinWidth(minWidth);
        if (maxWidth != null) content.setMaxWidth(maxWidth);
        if (minHeight != null) content.setMinHeight(minHeight);
        if (maxHeight != null) content.setMaxHeight(maxHeight);
        classNames.forEach(content::addClassName);
        attachListeners.forEach(assistant::addAttachListener);
        detachListeners.forEach(assistant::addDetachListener);
    }

}
