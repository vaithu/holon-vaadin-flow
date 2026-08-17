package com.holonplatform.vaadin.flow.chat.internal;

import com.holonplatform.vaadin.flow.chat.api.ChatService;
import com.holonplatform.vaadin.flow.chat.builders.LiveChatConfigurator;
import com.holonplatform.vaadin.flow.chat.components.LiveChat;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Base {@link LiveChatConfigurator} implementation that stores configuration state
 * for deferred application.
 *
 * <p>Because {@link LiveChat} is assembled via {@code assemble()} after construction,
 * all configuration is stored in fields and applied when {@code build()} is called
 * (or in the configurator variant, applied directly to an existing component).</p>
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractLiveChatConfigurator<C extends LiveChatConfigurator<C>>
        implements LiveChatConfigurator<C> {

    // ── Assembly-time state ─────────────────────────────────────────────────────

    protected String roomId;
    protected ChatService chatService;
    protected boolean typingEnabled;
    protected boolean channelsEnabled;
    protected boolean roomManagementEnabled;
    protected boolean directMessage;
    protected int pageSize = HolonChatPersister.DEFAULT_PAGE_SIZE;

    // ── Deferred component configuration ────────────────────────────────────────

    private String id;
    private boolean visible = true;
    private String width, height, minWidth, maxWidth, minHeight, maxHeight;
    private final List<String> classNames = new ArrayList<>();
    private final List<ComponentEventListener<AttachEvent>> attachListeners = new ArrayList<>();
    private final List<ComponentEventListener<DetachEvent>> detachListeners = new ArrayList<>();

    protected abstract C getConfigurator();

    // ── LiveChatConfigurator methods ────────────────────────────────────────────

    @Override
    public C room(String roomId) {
        if (roomId == null || roomId.isBlank()) throw new IllegalArgumentException("roomId must not be blank");
        this.roomId = roomId;
        return getConfigurator();
    }

    @Override
    public C withPersistence(ChatService chatService) {
        Objects.requireNonNull(chatService, "chatService must not be null");
        this.chatService = chatService;
        return getConfigurator();
    }

    @Override
    public C withTypingIndicator() {
        this.typingEnabled = true;
        return getConfigurator();
    }

    @Override
    public C withChannels(ChatService chatService) {
        Objects.requireNonNull(chatService, "chatService must not be null");
        this.channelsEnabled = true;
        if (this.chatService == null) this.chatService = chatService;
        return getConfigurator();
    }

    @Override
    public C withPageSize(int pageSize) {
        if (pageSize <= 0) throw new IllegalArgumentException("pageSize must be > 0");
        this.pageSize = pageSize;
        return getConfigurator();
    }

    @Override
    public C directMessage() {
        this.directMessage = true;
        return getConfigurator();
    }

    @Override
    public C withRoomManagement() {
        this.roomManagementEnabled = true;
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

    // ── Apply stored configuration to an assembled LiveChat ──────────────────────

    /**
     * Applies all stored configuration to the given (already assembled) {@link LiveChat}.
     *
     * @param chat the assembled component (not null)
     */
    protected void applyComponentConfig(LiveChat chat) {
        if (id != null) chat.setId(id);
        chat.setVisible(visible);
        var content = chat.getContent();
        if (width != null) content.setWidth(width);
        if (height != null) content.setHeight(height);
        if (minWidth != null) content.setMinWidth(minWidth);
        if (maxWidth != null) content.setMaxWidth(maxWidth);
        if (minHeight != null) content.setMinHeight(minHeight);
        if (maxHeight != null) content.setMaxHeight(maxHeight);
        classNames.forEach(content::addClassName);
        attachListeners.forEach(chat::addAttachListener);
        detachListeners.forEach(chat::addDetachListener);
    }
}
