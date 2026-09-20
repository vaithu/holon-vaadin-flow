package com.holonplatform.vaadin.flow.ai.i18n;

/**
 * Message key constants for the AI assistant module.
 *
 * <p>All keys follow the convention {@code ai.<component>.<element>}.
 *
 * <h3>Registering translations</h3>
 * <p>Bundle: {@code com.holonplatform.vaadin.flow.ai.i18n.AIAssistantMessages}
 * <pre>{@code
 * LocalizationContext.builder()
 *     .withMessageProvider(
 *         MessageProvider.fromResourceBundle("com.holonplatform.vaadin.flow.ai.i18n.AIAssistantMessages"))
 *     .build();
 * }</pre>
 *
 * <p>The module ships an English default bundle. Add
 * {@code AIAssistantMessages_it.properties}, {@code AIAssistantMessages_fr.properties}, etc. to
 * override.
 *
 * @see com.holonplatform.vaadin.flow.i18n.LocalizationProvider
 */
public final class AIAssistantI18N {

    private AIAssistantI18N() {
    }

    /** Default display name used for user-authored messages. */
    public static final String USER_NAME = "ai.assistant.user_name";

    /** Default display name used for assistant-authored messages. */
    public static final String ASSISTANT_NAME = "ai.assistant.assistant_name";

    /** Placeholder text for the message input field. */
    public static final String INPUT_PLACEHOLDER = "ai.assistant.input_placeholder";

    /** Accessible label for the message list region. */
    public static final String MESSAGES_ARIA_LABEL = "ai.assistant.messages_aria_label";

    /**
     * Error notification shown when the LLM provider fails — argument {0} is the error message.
     */
    public static final String ERROR_NOTIFICATION = "ai.assistant.error_notification";

    /** Default display name used for user-authored messages, in {@code AIChatClient}. */
    public static final String CHAT_CLIENT_USER_NAME = "ai.chatclient.user_name";

    /** Default display name used for assistant-authored messages, in {@code AIChatClient}. */
    public static final String CHAT_CLIENT_ASSISTANT_NAME = "ai.chatclient.assistant_name";

    /** Placeholder text for {@code AIChatClient}'s message input field. */
    public static final String CHAT_CLIENT_INPUT_PLACEHOLDER = "ai.chatclient.input_placeholder";

    /** Accessible label for {@code AIChatClient}'s message list region. */
    public static final String CHAT_CLIENT_MESSAGES_ARIA_LABEL = "ai.chatclient.messages_aria_label";

}
