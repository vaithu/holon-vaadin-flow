package com.holonplatform.vaadin.flow.chat.builders;

import com.holonplatform.vaadin.flow.chat.api.ChatService;
import com.holonplatform.vaadin.flow.chat.components.LiveChat;
import com.holonplatform.vaadin.flow.chat.internal.DefaultLiveChatConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;

/**
 * Configurator for {@link LiveChat} components.
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 */
public interface LiveChatConfigurator<C extends LiveChatConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    C room(String roomId);

    C withPersistence(ChatService chatService);

    C withTypingIndicator();

    C withChannels(ChatService chatService);

    C withPageSize(int pageSize);

    C directMessage();

    C withRoomManagement();

    /**
     * Configure an existing {@link LiveChat} component.
     *
     * @param chat the component to configure (not null)
     * @return a new {@link BaseLiveChatConfigurator}
     */
    static BaseLiveChatConfigurator configure(LiveChat chat) {
        return new DefaultLiveChatConfigurator(chat);
    }

    interface BaseLiveChatConfigurator extends LiveChatConfigurator<BaseLiveChatConfigurator> {}
}
