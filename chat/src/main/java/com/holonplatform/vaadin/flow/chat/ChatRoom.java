package com.holonplatform.vaadin.flow.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Persistent chat room / channel entity.
 *
 * <p>Maps to the {@code chat_room} table.  Two room types are supported:
 * <ul>
 *   <li>{@link Type#CHANNEL} â€“ named rooms visible to all members (e.g., #general)</li>
 *   <li>{@link Type#DIRECT}  â€“ private conversation between exactly two users</li>
 * </ul>
 *
 * <p>For {@link Type#DIRECT} rooms the convention is that {@code id} is composed as
 * {@code "dm/" + lexicographically-smallest-userId + ":" + other-userId} so the
 * same DM room is always reachable by both participants with the same topic key.
 */
@Entity(name = "chatroom")
@Table(name = "chat_room")
public class ChatRoom implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Room type discriminator. */
    public enum Type {
        /** Named channel visible/shared across multiple users. */
        CHANNEL,
        /** Private direct-message conversation between exactly two users. */
        DIRECT,
        /**
         * Private group chat â€” like a multi-person DM.
         * Always {@code isPrivate = true}; only visible to explicit members.
         */
        GROUP
    }

    @Id
    @Column(name = "id", length = 255)
    private String id;

    @Column(name = "name", length = 120)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    /**
     * Stored as {@link Type#name()} string in the DB column.
     * Use {@link #getType()} / {@link #setType(Type)} for typed access.
     */
    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "is_private")
    private boolean privateRoom;

    /** No-arg constructor required by Holon BeanIntrospector. */
    public ChatRoom() {
    }

    // ------------------------------------------------------------------ //
    // Factory
    // ------------------------------------------------------------------ //

    /**
     * Creates a channel-type room.
     *
     * @param id          unique identifier (e.g., UUID)
     * @param name        human-readable name (e.g., "general")
     * @param description optional description
     * @return a new, unsaved {@link ChatRoom}
     */
    public static ChatRoom channel(String id, String name, String description) {
        ChatRoom r = new ChatRoom();
        r.id = id;
        r.name = name;
        r.description = description;
        r.type = Type.CHANNEL.name();
        return r;
    }

    /**
     * Creates a direct-message room deterministically from two user IDs.
     * The ID is always {@code "dm/" + sorted(user1,user2).join(":")} so that
     * both participants compute the same room ID independently.
     *
     * @param userId1 first participant
     * @param userId2 second participant
     * @return a new, unsaved {@link ChatRoom}
     */
    public static ChatRoom direct(String userId1, String userId2) {
        String[] sorted = userId1.compareTo(userId2) <= 0
                ? new String[]{userId1, userId2}
                : new String[]{userId2, userId1};
        ChatRoom r = new ChatRoom();
        r.id = "dm/" + sorted[0] + ":" + sorted[1];
        r.name = userId1 + " & " + userId2;
        r.type = Type.DIRECT.name();
        return r;
    }

    /**
     * Creates a group-type room (private multi-person chat).
     *
     * @param id          unique identifier (e.g., UUID)
     * @param name        human-readable name (e.g., "Project Alpha")
     * @param description optional description
     * @return a new, unsaved {@link ChatRoom}
     */
    public static ChatRoom group(String id, String name, String description) {
        ChatRoom r = new ChatRoom();
        r.id = id;
        r.name = name;
        r.description = description;
        r.type = Type.GROUP.name();
        r.privateRoom = true;
        return r;
    }

    // ------------------------------------------------------------------ //
    // Getters / Setters
    // ------------------------------------------------------------------ //

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    /** Returns the room type enum, or {@code null} if the type column is null. */
    public Type getType() { return type != null ? Type.valueOf(type) : null; }

    /** Stores the enum name in the DB column (e.g. "CHANNEL"). */
    public void setType(Type type) { this.type = type != null ? type.name() : null; }

    public boolean isPrivateRoom() { return privateRoom; }
    public void setPrivateRoom(boolean privateRoom) { this.privateRoom = privateRoom; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatRoom that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }

    @Override
    public String toString() {
        return "ChatRoom{id='" + id + "', name='" + name + "', type=" + type + "}";
    }
}