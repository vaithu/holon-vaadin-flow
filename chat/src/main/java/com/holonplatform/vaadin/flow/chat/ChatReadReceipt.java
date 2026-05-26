package com.holonplatform.vaadin.flow.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks the last-read timestamp for a user in a chat room.
 *
 * <p>Maps to the {@code chat_read_receipt} table (composite PK: userId + roomId).
 * Used to compute unread counts displayed on {@link com.holonplatform.vaadin.flow.chat.components.ChatChannelList}.
 */
@Entity(name = "chatreadreceipt")
@Table(name = "chat_read_receipt")
public class ChatReadReceipt implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Surrogate primary key — UUID assigned on construction. */
    @Id
    @Column(name = "id", length = 255)
    private String id;

    @Column(name = "user_id", length = 255)
    private String userId;

    @Column(name = "room_id", length = 255)
    private String roomId;

    @Column(name = "last_read_at")
    private Instant lastReadAt;

    /** No-arg constructor required by Holon BeanIntrospector. */
    public ChatReadReceipt() {
    }

    // ── Factory ────────────────────────────────────────────────────────────────

    /**
     * Creates or refreshes a read receipt with a new UUID primary key.
     */
    public static ChatReadReceipt markNow(String userId, String roomId) {
        ChatReadReceipt r = new ChatReadReceipt();
        r.id = UUID.randomUUID().toString();
        r.userId = userId;
        r.roomId = roomId;
        r.lastReadAt = Instant.now();
        return r;
    }

    // ── Getters / Setters ──────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public Instant getLastReadAt() { return lastReadAt; }
    public void setLastReadAt(Instant lastReadAt) { this.lastReadAt = lastReadAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatReadReceipt that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(roomId, that.roomId);
    }

    @Override
    public int hashCode() { return Objects.hash(userId, roomId); }
}
