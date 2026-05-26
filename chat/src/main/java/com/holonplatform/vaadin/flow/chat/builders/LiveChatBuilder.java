package com.holonplatform.vaadin.flow.chat.builders;

import com.holonplatform.vaadin.flow.chat.api.ChatService;
import com.holonplatform.vaadin.flow.chat.components.LiveChat;
import com.holonplatform.vaadin.flow.chat.internal.DefaultLiveChatBuilder;
import com.vaadin.collaborationengine.UserInfo;

/**
 * Fluent builder for {@link LiveChat} components.
 *
 * <p>Obtained via {@link LiveChat#builder(UserInfo)}.
 *
 * <h3>Single-channel (in-memory)</h3>
 * <pre>{@code
 * LiveChat chat = LiveChat.builder(userInfo)
 *     .room("general")
 *     .withTypingIndicator()
 *     .build();
 * }</pre>
 *
 * <h3>Single-channel (DB-backed)</h3>
 * <pre>{@code
 * LiveChat chat = LiveChat.builder(userInfo)
 *     .room("general")
 *     .withPersistence(chatService)
 *     .withTypingIndicator()
 *     .build();
 * }</pre>
 *
 * <h3>Multi-channel with sidebar</h3>
 * <pre>{@code
 * LiveChat chat = LiveChat.builder(userInfo)
 *     .room("general")         // initial room shown on load
 *     .withChannels(chatService)   // activates the channel sidebar
 *     .withPersistence(chatService)
 *     .withTypingIndicator()
 *     .build();
 * }</pre>
 */
public interface LiveChatBuilder {

    // ------------------------------------------------------------------ //
    // Room configuration
    // ------------------------------------------------------------------ //

    /**
     * Sets the initial chat room / Collaboration Kit topic ID.
     *
     * <p>For channel rooms, use a simple name (e.g., {@code "general"}).
     * For direct messages, use {@link com.holonplatform.vaadin.flow.chat.ChatRoom#direct(String, String)}
     * to construct the canonical DM topic key.
     *
     * @param roomId the room identifier (not blank)
     * @return this builder
     */
    LiveChatBuilder room(String roomId);

    // ------------------------------------------------------------------ //
    // Persistence
    // ------------------------------------------------------------------ //

    /**
     * Enables DB-backed message persistence via the supplied {@link ChatService}.
     * Without this, messages are stored in-memory only and lost on server restart.
     *
     * @param chatService the service instance (not null)
     * @return this builder
     */
    LiveChatBuilder withPersistence(ChatService chatService);

    // ------------------------------------------------------------------ //
    // Features
    // ------------------------------------------------------------------ //

    /**
     * Enables the real-time "is typing…" indicator below the message list.
     *
     * @return this builder
     */
    LiveChatBuilder withTypingIndicator();

    /**
     * Enables the multi-channel sidebar.  Loads all channel rooms from the given
     * {@link ChatService} and renders them with live unread-count badges.
     *
     * <p>Automatically enables persistence for the sidebar's read receipts.
     *
     * @param chatService the service used to load rooms and unread counts (not null)
     * @return this builder
     */
    LiveChatBuilder withChannels(ChatService chatService);

    /**
     * Sets the number of messages loaded on initial room open (lazy loading page size).
     * Defaults to {@link com.holonplatform.vaadin.flow.chat.internal.HolonChatPersister#DEFAULT_PAGE_SIZE}.
     *
     * <p>Only older messages beyond this limit require an explicit "Load older" click.
     * New messages always arrive in real time regardless of page size.
     *
     * @param pageSize messages per page (must be &gt; 0)
     * @return this builder
     */
    LiveChatBuilder withPageSize(int pageSize);

    /**
     * Marks this chat as a one-to-one direct message conversation.
     *
     * <p>In DM mode the sender avatar and name are hidden for all messages —
     * both parties already know who they are talking to.
     * In group/channel mode (default) avatars and names are shown for other
     * participants' messages.
     *
     * @return this builder
     */
    LiveChatBuilder directMessage();

    /**
     * Enables Create and Browse room management buttons in the channel sidebar header.
     * Requires {@link #withChannels(ChatService)} to be set.
     *
     * @return this builder
     */
    LiveChatBuilder withRoomManagement();

    // ------------------------------------------------------------------ //
    // Terminal
    // ------------------------------------------------------------------ //

    /**
     * Builds and returns the fully assembled {@link LiveChat} component.
     *
     * @return a new, ready-to-use {@link LiveChat}
     * @throws IllegalStateException if {@link #room(String)} was not called
     */
    LiveChat build();

    // ------------------------------------------------------------------ //
    // Factory
    // ------------------------------------------------------------------ //

    /**
     * Creates a new builder for the given logged-in user.
     *
     * @param userInfo information about the user owning this chat session (not null)
     * @return a new {@link LiveChatBuilder}
     */
    static LiveChatBuilder create(UserInfo userInfo) {
        return new DefaultLiveChatBuilder(userInfo);
    }
}


