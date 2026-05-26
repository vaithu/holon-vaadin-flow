package com.holonplatform.vaadin.flow.chat.internal;

import com.holonplatform.vaadin.flow.chat.ChatMessage;
import com.holonplatform.vaadin.flow.chat.api.ChatService;
import com.vaadin.collaborationengine.CollaborationMessage;
import com.vaadin.collaborationengine.CollaborationMessagePersister;
import com.vaadin.collaborationengine.UserInfo;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Bridges Collaboration Kit's {@link CollaborationMessagePersister} to the Holon-backed
 * {@link ChatService}, with <strong>lazy / paginated history loading</strong>.
 *
 * <h3>Lazy loading strategy</h3>
 * <p>Collaboration Kit calls {@link #fetchMessages} in two situations:
 * <ol>
 *   <li><strong>Initial hydration</strong> — {@code getSince() == Instant.EPOCH}.
 *       Only the last {@link #pageSize} messages are returned, keeping startup fast
 *       even for rooms with thousands of messages.</li>
 *   <li><strong>Incremental update</strong> — {@code getSince()} is a recent timestamp
 *       after a new message was submitted. All messages since that point are returned
 *       (typically just 1–2 rows), preserving real-time ordering.</li>
 * </ol>
 *
 * <p>Older messages (beyond the initial page) are loaded on demand by
 * {@link com.holonplatform.vaadin.flow.chat.components.LiveChat} when the user clicks
 * "Load older messages".
 *
 * <h3>Default page size</h3>
 * <p>{@value #DEFAULT_PAGE_SIZE} messages. Override via
 * {@link #HolonChatPersister(ChatService, int)} or
 * {@link com.holonplatform.vaadin.flow.chat.builders.LiveChatBuilder#withPageSize(int)}.
 */
public class HolonChatPersister implements CollaborationMessagePersister {

    /** Default number of messages loaded on first connect. */
    public static final int DEFAULT_PAGE_SIZE = 50;

    private final ChatService chatService;
    private final int pageSize;

    /**
     * Creates a persister with the {@link #DEFAULT_PAGE_SIZE default page size}.
     *
     * @param chatService the service that owns the DB persistence (not null)
     */
    public HolonChatPersister(ChatService chatService) {
        this(chatService, DEFAULT_PAGE_SIZE);
    }

    /**
     * Creates a persister with an explicit page size.
     *
     * @param chatService the service that owns the DB persistence (not null)
     * @param pageSize    number of messages loaded on initial hydration (must be &gt; 0)
     */
    public HolonChatPersister(ChatService chatService, int pageSize) {
        if (chatService == null) throw new IllegalArgumentException("chatService must not be null");
        if (pageSize <= 0) throw new IllegalArgumentException("pageSize must be > 0");
        this.chatService = chatService;
        this.pageSize = pageSize;
    }

    // ------------------------------------------------------------------ //
    // CollaborationMessagePersister
    // ------------------------------------------------------------------ //

    /**
     * Returns messages for the given topic/room.
     *
     * <ul>
     *   <li>Initial load ({@code since == EPOCH}): returns only the last {@link #pageSize}
     *       messages — <em>lazy/paginated</em>.</li>
     *   <li>Incremental ({@code since > EPOCH}): returns all messages at or after
     *       {@code since} so new messages propagate in real time.</li>
     * </ul>
     */
    @Override
    public Stream<CollaborationMessage> fetchMessages(FetchQuery query) {
        String roomId = query.getTopicId();
        Instant since = query.getSince();

        // Discriminate: initial hydration vs. incremental update
        boolean isInitialLoad = since.equals(Instant.EPOCH);

        java.util.List<ChatMessage> messages = isInitialLoad
                ? chatService.findRecentMessages(roomId, pageSize)          // lazy: last N only
                : chatService.findMessagesSince(roomId, since);             // incremental: all new

        return messages.stream().map(this::toCollaborationMessage);
    }

    /**
     * Persists a new message to the DB via {@link ChatService#saveMessage}.
     */
    @Override
    public void persistMessage(PersistRequest request) {
        CollaborationMessage cm = request.getMessage();
        UserInfo user = cm.getUser();

        ChatMessage entity = ChatMessage.create(
                UUID.randomUUID().toString(),
                request.getTopicId(),
                user.getId(),
                user.getName(),
                cm.getText());
        entity.setAuthorImageUrl(user.getImage());
        entity.setCreatedAt(cm.getTime());

        chatService.saveMessage(entity);
    }

    // ------------------------------------------------------------------ //
    // Helpers
    // ------------------------------------------------------------------ //

    public int getPageSize() { return pageSize; }

    private CollaborationMessage toCollaborationMessage(ChatMessage entity) {
        UserInfo userInfo = new UserInfo(
                entity.getAuthorId(),
                entity.getAuthorName(),
                entity.getAuthorImageUrl());
        return new CollaborationMessage(userInfo, entity.getText(), entity.getCreatedAt());
    }
}
