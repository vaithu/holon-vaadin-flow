package com.holonplatform.vaadin.flow.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a pending or resolved invitation to join a group chat room.
 *
 * <p>Invitations are sent by a room {@link ChatRoomMember.Role#OWNER} or any member
 * (depending on application policy) and must be explicitly accepted or declined by
 * the recipient.
 *
 * <h3>Lifecycle</h3>
 * <pre>
 *   inviteToRoom() → PENDING
 *       ├── acceptInvitation() → ACCEPTED  (also calls joinRoom)
 *       └── declineInvitation() → DECLINED
 * </pre>
 *
 * <p>Maps to the {@code chat_invitation} table when using
 * {@link com.holonplatform.vaadin.flow.chat.internal.DefaultChatService}.
 */
@Entity(name = "chatinvitation")
@Table(name = "chat_invitation")
public class ChatInvitation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Current state of the invitation. */
    public enum Status {
        /** Sent but not yet answered. */
        PENDING,
        /** Recipient accepted — they have been added to the room. */
        ACCEPTED,
        /** Recipient declined — no membership was created. */
        DECLINED
    }

    @Id
    @Column(name = "id", length = 255)
    private String  id;           // UUID string

    @Column(name = "room_id", length = 255)
    private String  roomId;

    @Column(name = "room_name", length = 120)
    private String  roomName;

    @Column(name = "inviter_id", length = 255)
    private String  inviterId;

    @Column(name = "inviter_name", length = 120)
    private String  inviterName;

    @Column(name = "invitee_id", length = 255)
    private String  inviteeId;

    @Column(name = "status", length = 20)
    private String  status;       // Status.name()

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "responded_at")
    private Instant respondedAt;

    /** No-arg constructor required by Holon BeanIntrospector. */
    public ChatInvitation() {}

    // ------------------------------------------------------------------ //
    // Factory
    // ------------------------------------------------------------------ //

    /**
     * Creates a new PENDING invitation.
     *
     * @param roomId      target room
     * @param roomName    display name of the room (denormalized)
     * @param inviterId   user ID of the person sending the invite
     * @param inviterName display name of the inviter (denormalized)
     * @param inviteeId   user ID of the person being invited
     * @return a new, unsaved invitation
     */
    public static ChatInvitation create(String roomId, String roomName,
                                        String inviterId, String inviterName,
                                        String inviteeId) {
        ChatInvitation inv = new ChatInvitation();
        inv.id          = UUID.randomUUID().toString();
        inv.roomId      = roomId;
        inv.roomName    = roomName;
        inv.inviterId   = inviterId;
        inv.inviterName = inviterName;
        inv.inviteeId   = inviteeId;
        inv.status      = Status.PENDING.name();
        inv.createdAt   = Instant.now();
        return inv;
    }

    // ------------------------------------------------------------------ //
    // Getters / Setters
    // ------------------------------------------------------------------ //

    public String  getId()           { return id; }
    public void    setId(String id)  { this.id = id; }

    public String  getRoomId()            { return roomId; }
    public void    setRoomId(String v)    { this.roomId = v; }

    public String  getRoomName()          { return roomName; }
    public void    setRoomName(String v)  { this.roomName = v; }

    public String  getInviterId()         { return inviterId; }
    public void    setInviterId(String v) { this.inviterId = v; }

    public String  getInviterName()       { return inviterName; }
    public void    setInviterName(String v){ this.inviterName = v; }

    public String  getInviteeId()         { return inviteeId; }
    public void    setInviteeId(String v) { this.inviteeId = v; }

    public String  getStatus()            { return status; }
    public void    setStatus(String v)    { this.status = v; }

    public Status toStatusEnum() {
        return status != null ? Status.valueOf(status) : Status.PENDING;
    }

    public Instant getCreatedAt()         { return createdAt; }
    public void    setCreatedAt(Instant v){ this.createdAt = v; }

    public Instant getRespondedAt()           { return respondedAt; }
    public void    setRespondedAt(Instant v)  { this.respondedAt = v; }

    public boolean checkPending()  { return Status.PENDING.name().equals(status); }
    public boolean checkAccepted() { return Status.ACCEPTED.name().equals(status); }
    public boolean checkDeclined() { return Status.DECLINED.name().equals(status); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatInvitation that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }

    @Override
    public String toString() {
        return "ChatInvitation{id='" + id + "', room='" + roomId
                + "', invitee='" + inviteeId + "', status=" + status + '}';
    }
}
