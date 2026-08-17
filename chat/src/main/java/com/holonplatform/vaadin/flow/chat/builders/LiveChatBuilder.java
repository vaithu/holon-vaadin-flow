package com.holonplatform.vaadin.flow.chat.builders;

import com.holonplatform.vaadin.flow.chat.components.LiveChat;
import com.holonplatform.vaadin.flow.chat.internal.DefaultLiveChatBuilder;
import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
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
public interface LiveChatBuilder extends LiveChatConfigurator<LiveChatBuilder>,
        ComponentBuilder<LiveChat, LiveChatBuilder> {

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



