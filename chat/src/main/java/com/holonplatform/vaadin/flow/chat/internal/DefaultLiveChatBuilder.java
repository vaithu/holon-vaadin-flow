package com.holonplatform.vaadin.flow.chat.internal;

import com.holonplatform.vaadin.flow.chat.builders.LiveChatBuilder;
import com.holonplatform.vaadin.flow.chat.components.LiveChat;
import com.vaadin.collaborationengine.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Default implementation of {@link LiveChatBuilder}.
 *
 * <p>Collects all configuration then calls {@link LiveChat#assemble} on {@link #build()}.
 */
public final class DefaultLiveChatBuilder extends AbstractLiveChatConfigurator<LiveChatBuilder>
        implements LiveChatBuilder {

    private final UserInfo userInfo;
    private final List<Consumer<LiveChat>> postProcessors = new ArrayList<>();

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
    protected LiveChatBuilder getConfigurator() {
        return this;
    }

    @Override
    public LiveChatBuilder withBuildPostProcessor(Consumer<LiveChat> postProcessor) {
        Objects.requireNonNull(postProcessor, "Post-processor must not be null");
        postProcessors.add(postProcessor);
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
        applyComponentConfig(chat);
        postProcessors.forEach(pp -> pp.accept(chat));
        return chat;
    }
}





