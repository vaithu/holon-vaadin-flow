package com.holonplatform.vaadin.flow.chat.internal;

import com.holonplatform.vaadin.flow.chat.builders.LiveChatConfigurator;
import com.holonplatform.vaadin.flow.chat.components.LiveChat;

import java.util.Objects;

/**
 * Default {@link LiveChatConfigurator.BaseLiveChatConfigurator} implementation
 * that configures an existing {@link LiveChat} component.
 *
 * <p>Size, style, and component attributes are applied directly to the
 * component's content {@code Div}.</p>
 */
public class DefaultLiveChatConfigurator
        extends AbstractLiveChatConfigurator<LiveChatConfigurator.BaseLiveChatConfigurator>
        implements LiveChatConfigurator.BaseLiveChatConfigurator {

    private final LiveChat existingChat;

    public DefaultLiveChatConfigurator(LiveChat chat) {
        this.existingChat = Objects.requireNonNull(chat, "chat must not be null");
    }

    @Override
    protected LiveChatConfigurator.BaseLiveChatConfigurator getConfigurator() {
        return this;
    }

    // Override size/style methods to apply immediately to the existing component
    @Override
    public LiveChatConfigurator.BaseLiveChatConfigurator width(String width) {
        existingChat.getContent().setWidth(width);
        return getConfigurator();
    }

    @Override
    public LiveChatConfigurator.BaseLiveChatConfigurator height(String height) {
        existingChat.getContent().setHeight(height);
        return getConfigurator();
    }

    @Override
    public LiveChatConfigurator.BaseLiveChatConfigurator minWidth(String minWidth) {
        existingChat.getContent().setMinWidth(minWidth);
        return getConfigurator();
    }

    @Override
    public LiveChatConfigurator.BaseLiveChatConfigurator maxWidth(String maxWidth) {
        existingChat.getContent().setMaxWidth(maxWidth);
        return getConfigurator();
    }

    @Override
    public LiveChatConfigurator.BaseLiveChatConfigurator minHeight(String minHeight) {
        existingChat.getContent().setMinHeight(minHeight);
        return getConfigurator();
    }

    @Override
    public LiveChatConfigurator.BaseLiveChatConfigurator maxHeight(String maxHeight) {
        existingChat.getContent().setMaxHeight(maxHeight);
        return getConfigurator();
    }

    @Override
    public LiveChatConfigurator.BaseLiveChatConfigurator styleName(String name) {
        if (name != null) existingChat.getContent().addClassName(name);
        return getConfigurator();
    }

    @Override
    public LiveChatConfigurator.BaseLiveChatConfigurator styleNames(String... names) {
        if (names != null) {
            for (String name : names) {
                if (name != null) existingChat.getContent().addClassName(name);
            }
        }
        return getConfigurator();
    }

    @Override
    public LiveChatConfigurator.BaseLiveChatConfigurator id(String id) {
        existingChat.setId(id);
        return getConfigurator();
    }

    @Override
    public LiveChatConfigurator.BaseLiveChatConfigurator visible(boolean visible) {
        existingChat.setVisible(visible);
        return getConfigurator();
    }
}
