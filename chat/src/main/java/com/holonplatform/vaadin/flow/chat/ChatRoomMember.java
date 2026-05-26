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
 * Membership record — which users belong to which room.
 *
 * <p>Maps to the {@code chat_room_member} table.
 * Every channel or group room can have members.
 * Public channels are visible to everyone but only <em>joined</em> members see them in the
 * sidebar and receive unread-count tracking.
 *
 * <p>For direct rooms, membership is implicit (the two DM participants)
 * and no entries are stored in this table.
 */
@Entity(name = "chatroommember")
@Table(name = "chat_room_member")
public class ChatRoomMember implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Membership role within a room. */
    public enum Role {
        /** Room creator — can delete the room, rename it, or add/remove members. */
        OWNER,
        /** Regular participant — can send messages and leave. */
        MEMBER
    }

    /** Surrogate primary key — UUID assigned on construction. */
    @Id
    @Column(name = "id", length = 255)
    private String id;

    @Column(name = "user_id", length = 255)
    private String userId;

    @Column(name = "room_id", length = 255)
    private String roomId;

    @Column(name = "joined_at")
    private Instant joinedAt;

    @Column(name = "role", length = 20)
    private String role;   // stored as Role.name() string

    /** No-arg constructor required by Holon BeanIntrospector. */
    public ChatRoomMember() {}

    // ── Factories ──────────────────────────────────────────────────────────────

    public static ChatRoomMember owner(String userId, String roomId) {
        ChatRoomMember m = new ChatRoomMember();
        m.id      = UUID.randomUUID().toString();
        m.userId  = userId;
        m.roomId  = roomId;
        m.joinedAt = Instant.now();
        m.role    = Role.OWNER.name();
        return m;
    }

    public static ChatRoomMember member(String userId, String roomId) {
        ChatRoomMember m = new ChatRoomMember();
        m.id      = UUID.randomUUID().toString();
        m.userId  = userId;
        m.roomId  = roomId;
        m.joinedAt = Instant.now();
        m.role    = Role.MEMBER.name();
        return m;
    }

    // ── Getters / Setters ──────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId()  { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRoomId()  { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public Instant getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Instant joinedAt) { this.joinedAt = joinedAt; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Role toRoleEnum() {
        return role != null ? Role.valueOf(role) : Role.MEMBER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatRoomMember that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(roomId, that.roomId);
    }

    @Override
    public int hashCode() { return Objects.hash(userId, roomId); }
}
