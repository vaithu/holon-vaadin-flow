package com.holonplatform.vaadin.flow.chat.internal;

import com.holonplatform.vaadin.flow.chat.api.ChatService;
import com.holonplatform.vaadin.flow.chat.builders.LiveChatBuilder;
import com.holonplatform.vaadin.flow.chat.components.LiveChat;
import com.vaadin.collaborationengine.UserInfo;

/**
 * Default implementation of {@link LiveChatBuilder}.
 *
 * <p>Collects all configuration then calls {@link LiveChat#assemble} on {@link #build()}.
 */
public final class DefaultLiveChatBuilder implements LiveChatBuilder {

    private final UserInfo userInfo;

    private String roomId;
    private ChatService chatService;
    private boolean typingEnabled;
    private boolean channelsEnabled;
    private boolean roomManagementEnabled;
    private boolean directMessage;
    private int pageSize = HolonChatPersister.DEFAULT_PAGE_SIZE;

    /**
     * Creates a builder for the given user.
     *
     * @param userInfo the logged-in user (not null)
     */
    public DefaultLiveChatBuilder(UserInfo userInfo) {
        if (userInfo == null) throw new IllegalArgumentException("userInfo must not be null");
        this.userInfo = userInfo;
    }

    @Override
    public LiveChatBuilder room(String roomId) {
        if (roomId == null || roomId.isBlank()) throw new IllegalArgumentException("roomId must not be blank");
        this.roomId = roomId;
        return this;
    }

    @Override
    public LiveChatBuilder withPersistence(ChatService chatService) {
        if (chatService == null) throw new IllegalArgumentException("chatService must not be null");
        this.chatService = chatService;
        return this;
    }

    @Override
    public LiveChatBuilder withTypingIndicator() {
        this.typingEnabled = true;
        return this;
    }

    @Override
    public LiveChatBuilder withChannels(ChatService chatService) {
        if (chatService == null) throw new IllegalArgumentException("chatService must not be null");
        this.channelsEnabled = true;
        if (this.chatService == null) this.chatService = chatService;
        return this;
    }

    @Override
    public LiveChatBuilder withPageSize(int pageSize) {
        if (pageSize <= 0) throw new IllegalArgumentException("pageSize must be > 0");
        this.pageSize = pageSize;
        return this;
    }

    @Override
    public LiveChatBuilder withRoomManagement() {
        this.roomManagementEnabled = true;
        return this;
    }

    @Override
    public LiveChatBuilder directMessage() {
        this.directMessage = true;
        return this;
    }

    @Override
    public LiveChat build() {
        if (roomId == null) {
            throw new IllegalStateException(
                    "A room ID is required. Call .room(\"my-room\") before .build().");
        }
        LiveChat chat = new LiveChat(userInfo);
        chat.assemble(roomId, chatService, typingEnabled, channelsEnabled,
                roomManagementEnabled, pageSize, directMessage);
        return chat;
    }
}



