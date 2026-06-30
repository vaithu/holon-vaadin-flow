package com.holonplatform.vaadin.flow.chat.internal;

import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.beans.BeanDatastoreHelper;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.vaadin.flow.chat.ChatInvitation;
import com.holonplatform.vaadin.flow.chat.ChatMessage;
import com.holonplatform.vaadin.flow.chat.ChatReadReceipt;
import com.holonplatform.vaadin.flow.chat.ChatRoom;
import com.holonplatform.vaadin.flow.chat.ChatRoomMember;
import com.holonplatform.vaadin.flow.chat.api.ChatService;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Default {@link ChatService} implementation backed by the Holon Platform
 * {@link com.holonplatform.core.datastore.beans.BeanDatastore} via
 * {@link BeanDatastoreHelper} — no manual {@code PropertyBox} conversions.
 *
 * <p>Works with any Holon Datastore (JPA or JDBC). For Spring Boot, use
 * {@code holon-datastore-jpa-spring-boot} to auto-configure the JPA Datastore.
 */
public class DefaultChatService implements ChatService {

    // ── PathProperty constants (used for QueryFilter expressions) ─────────────

    // ChatRoom
    static final PathProperty<String>  ROOM_ID         = PathProperty.create("id",        String.class);
    static final PathProperty<String>  ROOM_NAME       = PathProperty.create("name",       String.class);
    static final PathProperty<String>  ROOM_TYPE       = PathProperty.create("type",       String.class);
    static final PathProperty<Boolean> ROOM_IS_PRIVATE = PathProperty.create("privateRoom",  Boolean.class);

    // ChatRoomMember
    static final PathProperty<String>  MEMBER_USER_ID  = PathProperty.create("userId",    String.class);
    static final PathProperty<String>  MEMBER_ROOM_ID  = PathProperty.create("roomId",    String.class);
    static final PathProperty<Instant> MEMBER_JOINED_AT= PathProperty.create("joinedAt",  Instant.class);

    // ChatMessage
    static final PathProperty<String>  MSG_ID          = PathProperty.create("id",         String.class);
    static final PathProperty<String>  MSG_ROOM_ID     = PathProperty.create("roomId",     String.class);
    static final PathProperty<String>  MSG_TEXT        = PathProperty.create("text",        String.class);
    static final PathProperty<Instant> MSG_CREATED_AT  = PathProperty.create("createdAt",  Instant.class);
    static final PathProperty<Instant> MSG_EDITED_AT   = PathProperty.create("editedAt",   Instant.class);
    static final PathProperty<Boolean> MSG_DELETED     = PathProperty.create("deleted",    Boolean.class);

    // ChatReadReceipt
    static final PathProperty<String>  RECEIPT_USER_ID = PathProperty.create("userId",    String.class);
    static final PathProperty<String>  RECEIPT_ROOM_ID = PathProperty.create("roomId",    String.class);

    // ChatInvitation
    static final PathProperty<String>  INV_ID          = PathProperty.create("id",         String.class);
    static final PathProperty<String>  INV_ROOM_ID     = PathProperty.create("roomId",     String.class);
    static final PathProperty<String>  INV_INVITEE_ID  = PathProperty.create("inviteeId",  String.class);
    static final PathProperty<String>  INV_STATUS      = PathProperty.create("status",     String.class);
    static final PathProperty<Instant> INV_CREATED_AT  = PathProperty.create("createdAt",  Instant.class);

    // ── BeanDatastoreHelper fields ────────────────────────────────────────────

    private final BeanDatastoreHelper<ChatRoom>        rooms;
    private final BeanDatastoreHelper<ChatMessage>     messages;
    private final BeanDatastoreHelper<ChatRoomMember>  members;
    private final BeanDatastoreHelper<ChatReadReceipt> receipts;
    private final BeanDatastoreHelper<ChatInvitation>  invitations;

    public DefaultChatService(Datastore datastore) {
        this.rooms       = BeanDatastoreHelper.of(datastore, ChatRoom.class);
        this.messages    = BeanDatastoreHelper.of(datastore, ChatMessage.class);
        this.members     = BeanDatastoreHelper.of(datastore, ChatRoomMember.class);
        this.receipts    = BeanDatastoreHelper.of(datastore, ChatReadReceipt.class);
        this.invitations = BeanDatastoreHelper.of(datastore, ChatInvitation.class);
    }

    // ================================================================== //
    // Room operations
    // ================================================================== //

    @Override
    public List<ChatRoom> findAllChannels() {
        try (Stream<ChatRoom> s = rooms.findAll(ROOM_TYPE.eq(ChatRoom.Type.CHANNEL.name()))) {
            return s.sorted(Comparator.comparing(ChatRoom::getName)).toList();
        }
    }

    @Override
    public List<ChatRoom> findPublicRooms() {
        try (Stream<ChatRoom> s = rooms.findAll(ROOM_IS_PRIVATE.eq(false))) {
            return s.sorted(Comparator.comparing(ChatRoom::getName)).toList();
        }
    }

    @Override
    public List<ChatRoom> findJoinedRooms(String userId) {
        List<String> roomIds;
        try (Stream<ChatRoomMember> s = members.findAll(MEMBER_USER_ID.eq(userId))) {
            roomIds = s.map(ChatRoomMember::getRoomId).toList();
        }
        if (roomIds.isEmpty()) return List.of();
        try (Stream<ChatRoom> s = rooms.findAll(ROOM_ID.in(roomIds))) {
            return s.sorted(Comparator.comparing(ChatRoom::getName)).toList();
        }
    }

    @Override
    public Optional<ChatRoom> findRoom(String roomId) {
        return rooms.findOne(ROOM_ID.eq(roomId));
    }

    @Override
    @Transactional
    public ChatRoom saveRoom(ChatRoom room) {
        return rooms.save(room).getResult().orElse(room);
    }

    // ================================================================== //
    // Membership operations
    // ================================================================== //

    @Override
    public List<ChatRoomMember> findRoomMembers(String roomId) {
        try (Stream<ChatRoomMember> s = members.findAll(MEMBER_ROOM_ID.eq(roomId))) {
            return s.sorted(Comparator.comparing(ChatRoomMember::getJoinedAt)).toList();
        }
    }

    @Override
    public Optional<ChatRoomMember> findMembership(String userId, String roomId) {
        return members.findOne(MEMBER_USER_ID.eq(userId).and(MEMBER_ROOM_ID.eq(roomId)));
    }

    @Override
    @Transactional
    public void joinRoom(String userId, String roomId) {
        if (isMember(userId, roomId)) return;  // idempotent
        members.insert(ChatRoomMember.member(userId, roomId));
    }

    @Override
    @Transactional
    public void leaveRoom(String userId, String roomId) {
        members.bulkDelete(MEMBER_USER_ID.eq(userId).and(MEMBER_ROOM_ID.eq(roomId)));
    }

    @Override
    public boolean isMember(String userId, String roomId) {
        return members.exists(MEMBER_USER_ID.eq(userId).and(MEMBER_ROOM_ID.eq(roomId)));
    }

    @Override
    public int countMembers(String roomId) {
        return (int) members.count(MEMBER_ROOM_ID.eq(roomId));
    }

    // ================================================================== //
    // Message operations
    // ================================================================== //

    @Override
    public List<ChatMessage> findMessagesSince(String roomId, Instant since) {
        var filter = MSG_ROOM_ID.eq(roomId).and(MSG_CREATED_AT.goe(since)).and(MSG_DELETED.eq(false));
        try (Stream<ChatMessage> s = messages.findAll(filter)) {
            return s.sorted(Comparator.comparing(ChatMessage::getCreatedAt)).toList();
        }
    }

    @Override
    public List<ChatMessage> findRecentMessages(String roomId, int limit) {
        if (limit <= 0) throw new IllegalArgumentException("limit must be > 0");
        var filter = MSG_ROOM_ID.eq(roomId).and(MSG_DELETED.eq(false));
        // Fetch newest N from DB, then re-sort chronologically for display
        return messages.findSlice(filter, MSG_CREATED_AT.desc(), limit, 0)
                .sorted(Comparator.comparing(ChatMessage::getCreatedAt)).toList();
    }

    @Override
    public List<ChatMessage> findMessagesBefore(String roomId, Instant before, int limit) {
        if (limit <= 0) throw new IllegalArgumentException("limit must be > 0");
        var filter = MSG_ROOM_ID.eq(roomId).and(MSG_CREATED_AT.lt(before)).and(MSG_DELETED.eq(false));
        // Returns newest-first (DESC) as per ChatService contract.
        // LiveChat.loadOlderMessages() reverses the result to display oldest-first.
        return messages.findSlice(filter, MSG_CREATED_AT.desc(), limit, 0).toList();
    }

    @Override
    public long countMessagesSince(String roomId, Instant since) {
        return messages.count(MSG_ROOM_ID.eq(roomId).and(MSG_CREATED_AT.gt(since)).and(MSG_DELETED.eq(false)));
    }

    @Override
    @Transactional
    public ChatMessage saveMessage(ChatMessage message) {
        return messages.save(message).getResult().orElse(message);
    }

    @Override
    @Transactional
    public boolean deleteMessage(String messageId) {
        return messages.findOne(MSG_ID.eq(messageId))
                .map(msg -> {
                    msg.setDeleted(true);
                    messages.update(msg);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public Optional<ChatMessage> editMessage(String messageId, String newText) {
        return messages.findOne(MSG_ID.eq(messageId))
                .map(msg -> {
                    msg.setText(newText);
                    msg.setEditedAt(Instant.now());
                    return messages.update(msg).getResult().orElse(msg);
                });
    }

    // ================================================================== //
    // Read receipts
    // ================================================================== //

    @Override
    public Optional<ChatReadReceipt> findReadReceipt(String userId, String roomId) {
        return receipts.findOne(RECEIPT_USER_ID.eq(userId).and(RECEIPT_ROOM_ID.eq(roomId)));
    }

    @Override
    @Transactional
    public void markAsRead(String userId, String roomId) {
        ChatReadReceipt receipt = findReadReceipt(userId, roomId).orElseGet(() -> {
            ChatReadReceipt r = new ChatReadReceipt();
            r.setId(UUID.randomUUID().toString());
            r.setUserId(userId);
            r.setRoomId(roomId);
            return r;
        });
        receipt.setLastReadAt(Instant.now());
        receipts.save(receipt);
    }

    // ================================================================== //
    // Invitations
    // ================================================================== //

    @Override
    @Transactional
    public ChatInvitation inviteToRoom(String roomId, String roomName,
                                       String inviterId, String inviterName,
                                       String inviteeId) {
        var existingFilter = INV_ROOM_ID.eq(roomId)
                .and(INV_INVITEE_ID.eq(inviteeId))
                .and(INV_STATUS.eq(ChatInvitation.Status.PENDING.name()));
        Optional<ChatInvitation> existing = invitations.findOne(existingFilter);
        if (existing.isPresent()) return existing.get();

        ChatInvitation inv = ChatInvitation.create(roomId, roomName, inviterId, inviterName, inviteeId);
        return invitations.insert(inv).getResult().orElse(inv);
    }

    @Override
    public List<ChatInvitation> findPendingInvitations(String inviteeId) {
        var filter = INV_INVITEE_ID.eq(inviteeId).and(INV_STATUS.eq(ChatInvitation.Status.PENDING.name()));
        try (Stream<ChatInvitation> s = invitations.findAll(filter)) {
            return s.sorted(Comparator.comparing(ChatInvitation::getCreatedAt).reversed()).toList();
        }
    }

    @Override
    public List<ChatInvitation> findRoomInvitations(String roomId) {
        try (Stream<ChatInvitation> s = invitations.findAll(INV_ROOM_ID.eq(roomId))) {
            return s.sorted(Comparator.comparing(ChatInvitation::getCreatedAt).reversed()).toList();
        }
    }

    @Override
    @Transactional
    public Optional<ChatInvitation> acceptInvitation(String invitationId) {
        return invitations.findOne(INV_ID.eq(invitationId))
                .map(inv -> {
                    inv.setStatus(ChatInvitation.Status.ACCEPTED.name());
                    inv.setRespondedAt(Instant.now());
                    ChatInvitation updated = invitations.update(inv).getResult().orElse(inv);
                    joinRoom(updated.getInviteeId(), updated.getRoomId());
                    return updated;
                });
    }

    @Override
    @Transactional
    public Optional<ChatInvitation> declineInvitation(String invitationId) {
        return invitations.findOne(INV_ID.eq(invitationId))
                .map(inv -> {
                    inv.setStatus(ChatInvitation.Status.DECLINED.name());
                    inv.setRespondedAt(Instant.now());
                    return invitations.update(inv).getResult().orElse(inv);
                });
    }
}

