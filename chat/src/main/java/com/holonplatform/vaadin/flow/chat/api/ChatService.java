package com.holonplatform.vaadin.flow.chat.api;

import com.holonplatform.vaadin.flow.chat.ChatInvitation;
import com.holonplatform.vaadin.flow.chat.ChatMessage;
import com.holonplatform.vaadin.flow.chat.ChatReadReceipt;
import com.holonplatform.vaadin.flow.chat.ChatRoom;
import com.holonplatform.vaadin.flow.chat.ChatRoomMember;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service layer interface for the chat module.
 *
 * <p>All writes are transactional. Implementations back all operations via
 * the Holon Platform {@code Datastore} API – no JPA repositories are used directly.
 *
 * <p>Pass an implementation to
 * {@link com.holonplatform.vaadin.flow.chat.builders.LiveChatBuilder#withPersistence(ChatService)}
 * to enable DB-backed message history and unread-count tracking.
 */
public interface ChatService {

    // ------------------------------------------------------------------ //
    // Rooms
    // ------------------------------------------------------------------ //

    /**
     * Returns all rooms of type {@link ChatRoom.Type#CHANNEL} ordered by name.
     * Includes both public and private channels.
     * Use {@link #findPublicRooms()} for the browse dialog (public only).
     *
     * @return live list of channels; never null
     * @deprecated Prefer {@link #findJoinedRooms(String)} for the sidebar and
     *             {@link #findPublicRooms()} for the browse dialog.
     */
    @Deprecated
    List<ChatRoom> findAllChannels();

    /**
     * Returns all non-private rooms (public channels) in name order.
     * Used by the browse dialog to show rooms the user can join.
     *
     * @return public rooms sorted by name; never null
     */
    List<ChatRoom> findPublicRooms();

    /**
     * Returns all rooms the given user is a member of, ordered by name.
     * These are the rooms shown in the user's channel sidebar.
     *
     * @param userId user identifier
     * @return rooms the user has joined; never null
     */
    List<ChatRoom> findJoinedRooms(String userId);

    /**
     * Returns the room with the given id, if it exists.
     *
     * @param roomId room identifier
     * @return optional room
     */
    Optional<ChatRoom> findRoom(String roomId);

    /**
     * Persists a new or updated room.
     *
     * @param room the room to save (not null)
     * @return the saved room (with any generated fields populated)
     */
    ChatRoom saveRoom(ChatRoom room);

    // ------------------------------------------------------------------ //
    // Messages
    // ------------------------------------------------------------------ //

    /**
     * Fetches non-deleted messages for a room created at or after {@code since}, in
     * ascending order. Used by the Collaboration Kit persister for incremental updates
     * (when {@code since} is not {@link Instant#EPOCH}).
     *
     * @param roomId room identifier
     * @param since  lower bound (inclusive)
     * @return ordered list (oldest first); never null
     */
    List<ChatMessage> findMessagesSince(String roomId, Instant since);

    /**
     * Returns the {@code limit} most recent non-deleted messages for a room,
     * ordered oldest-first. Used for lazy initial load so only the last page
     * of messages is fetched when a chat room first opens.
     *
     * @param roomId room identifier
     * @param limit  maximum number of messages to return (must be &gt; 0)
     * @return ordered list (oldest first, ≤ limit rows); never null
     */
    List<ChatMessage> findRecentMessages(String roomId, int limit);

    /**
     * Returns up to {@code limit} non-deleted messages that were created
     * <em>strictly before</em> {@code before}, ordered newest-first.
     * Used for "Load older messages" pagination (infinite scroll upward).
     *
     * <p>Callers should reverse the result before displaying it so that
     * older messages are prepended in chronological order.
     *
     * @param roomId room identifier
     * @param before exclusive upper bound timestamp
     * @param limit  maximum number of messages to return
     * @return ordered list (newest-first); never null
     */
    List<ChatMessage> findMessagesBefore(String roomId, Instant before, int limit);

    /**
     * Counts messages in a room that were created after {@code since}.
     * Used for unread-badge computation.
     *
     * @param roomId room identifier
     * @param since  exclusive lower bound
     * @return count of newer messages
     */
    long countMessagesSince(String roomId, Instant since);

    /**
     * Persists a new message.  The {@code id} and {@code createdAt} fields must
     * already be set by the caller (typically the {@code HolonChatPersister}).
     *
     * @param message the fully-populated message (not null)
     * @return the saved message
     */
    ChatMessage saveMessage(ChatMessage message);

    /**
     * Soft-deletes a message.  The record remains in the DB but
     * {@link ChatMessage#isDeleted()} is set to {@code true}.
     *
     * @param messageId id of the message to delete
     * @return {@code true} if a row was affected
     */
    boolean deleteMessage(String messageId);

    /**
     * Updates the text of an existing message and sets {@code editedAt = now}.
     *
     * @param messageId id of the message to edit
     * @param newText   replacement body
     * @return the updated message, if found
     */
    Optional<ChatMessage> editMessage(String messageId, String newText);

    // ------------------------------------------------------------------ //
    // Membership (join / leave / list members)
    // ------------------------------------------------------------------ //

    /**
     * Returns all members of a room, ordered by join date.
     *
     * @param roomId room identifier
     * @return member list; never null
     */
    List<ChatRoomMember> findRoomMembers(String roomId);

    /**
     * Returns the membership record for a specific user/room pair, if it exists.
     *
     * @param userId user identifier
     * @param roomId room identifier
     * @return optional membership; empty if the user has not joined this room
     */
    Optional<ChatRoomMember> findMembership(String userId, String roomId);

    /**
     * Adds the user to the given room as a {@link ChatRoomMember.Role#MEMBER}.
     * If the user is already a member this is a no-op.
     *
     * @param userId user identifier
     * @param roomId room identifier
     */
    void joinRoom(String userId, String roomId);

    /**
     * Removes the user from the given room.
     * If the user is not a member this is a no-op.
     *
     * @param userId user identifier
     * @param roomId room identifier
     */
    void leaveRoom(String userId, String roomId);

    /**
     * Returns {@code true} if the user is currently a member of the room.
     *
     * @param userId user identifier
     * @param roomId room identifier
     * @return {@code true} if the user is a member
     */
    boolean isMember(String userId, String roomId);

    /**
     * Returns the number of members in a room. Useful for the browse dialog.
     *
     * @param roomId room identifier
     * @return member count (≥ 0)
     */
    int countMembers(String roomId);

    // ------------------------------------------------------------------ //
    // Read receipts (unread counts)
    // ------------------------------------------------------------------ //

    /**
     * Returns the last-read receipt for a specific user/room pair.
     *
     * @param userId user identifier
     * @param roomId room identifier
     * @return optional receipt; empty if the user has never opened this room
     */
    Optional<ChatReadReceipt> findReadReceipt(String userId, String roomId);

    /**
     * Upserts a read receipt for the given user/room to the current moment.
     * Should be called whenever the user opens or activates a chat room.
     *
     * @param userId user identifier
     * @param roomId room identifier
     */
    void markAsRead(String userId, String roomId);

    // ------------------------------------------------------------------ //
    // Invitations
    // ------------------------------------------------------------------ //

    /**
     * Creates and persists a PENDING invitation for {@code inviteeId} to join {@code roomId}.
     * If a PENDING invitation already exists for the same invitee + room this is a no-op and
     * the existing invitation is returned.
     *
     * @param roomId      the room to invite the user into (must be {@link ChatRoom.Type#GROUP})
     * @param roomName    display name of the room (denormalised — avoids an extra DB read)
     * @param inviterId   user ID of the person sending the invitation
     * @param inviterName display name of the inviter (denormalised)
     * @param inviteeId   user ID of the person being invited
     * @return the new or existing PENDING invitation
     */
    default ChatInvitation inviteToRoom(String roomId, String roomName,
                                        String inviterId, String inviterName,
                                        String inviteeId) {
        throw new UnsupportedOperationException("Invitations not supported by this ChatService implementation");
    }

    /**
     * Returns all PENDING invitations addressed to {@code inviteeId}, ordered newest first.
     *
     * @param inviteeId the user whose pending invites to fetch
     * @return pending invitations; never null
     */
    default List<ChatInvitation> findPendingInvitations(String inviteeId) {
        return List.of();
    }

    /**
     * Returns all invitations (any status) sent to a particular room, ordered newest first.
     * Useful for the invite-management panel shown to room owners.
     *
     * @param roomId the room
     * @return all invitations for the room; never null
     */
    default List<ChatInvitation> findRoomInvitations(String roomId) {
        return List.of();
    }

    /**
     * Accepts a PENDING invitation.
     * Automatically calls {@link #joinRoom(String, String)} so the invitee becomes a member.
     *
     * @param invitationId the invitation to accept
     * @return the updated invitation, or empty if not found
     */
    default Optional<ChatInvitation> acceptInvitation(String invitationId) {
        return Optional.empty();
    }

    /**
     * Declines a PENDING invitation.  The invitation status is set to DECLINED;
     * no membership is created.
     *
     * @param invitationId the invitation to decline
     * @return the updated invitation, or empty if not found
     */
    default Optional<ChatInvitation> declineInvitation(String invitationId) {
        return Optional.empty();
    }
}


