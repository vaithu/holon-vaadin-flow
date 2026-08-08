package com.holonplatform.vaadin.flow.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Persistent chat message entity.
 *
 * <p>Used with {@link com.holonplatform.core.datastore.beans.BeanDatastoreHelper} for DB-backed persistence.
 * Maps to the {@code chat_message} table via {@link com.holonplatform.core.datastore.DataTarget}.
 * Implements {@link Serializable} for safe transfer between layers.
 *
 * <p>Instances are typically created through {@link ChatMessage#create(String, String, String, String, String)}.
 */
@Entity(name = "chatmessage")
@Table(name = "chat_message")
public class ChatMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", length = 255)
    private String id;

    @Column(name = "room_id", length = 255)
    private String roomId;

    @Column(name = "author_id", length = 255)
    private String authorId;

    @Column(name = "author_name", length = 120)
    private String authorName;

    @Column(name = "author_image_url", length = 512)
    private String authorImageUrl;

    @Column(name = "text", length = 4000)
    private String text;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "edited_at")
    private Instant editedAt;

    @Column(name = "deleted")
    private boolean deleted;

    // ------------------------------------------------------------------ //
    // Constructors
    // ------------------------------------------------------------------ //

    /** No-arg constructor required by Holon BeanIntrospector. */
    public ChatMessage() {
    }

    // ------------------------------------------------------------------ //
    // Factory
    // ------------------------------------------------------------------ //

    /**
     * Creates a new unsaved message.
     *
     * @param id             UUID string (caller is responsible for uniqueness)
     * @param roomId         target room/channel ID
     * @param authorId       user ID of the author
     * @param authorName     display name of the author
     * @param text           message body
     * @return a populated, unsaved instance with {@code createdAt = now}
     */
    public static ChatMessage create(String id, String roomId,
                                     String authorId, String authorName,
                                     String text) {
        ChatMessage m = new ChatMessage();
        m.id = id;
        m.roomId = roomId;
        m.authorId = authorId;
        m.authorName = authorName;
        m.text = text;
        m.createdAt = Instant.now();
        m.deleted = false;
        return m;
    }

    // ------------------------------------------------------------------ //
    // Getters / Setters
    // ------------------------------------------------------------------ //

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorImageUrl() { return authorImageUrl; }
    public void setAuthorImageUrl(String authorImageUrl) { this.authorImageUrl = authorImageUrl; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getEditedAt() { return editedAt; }
    public void setEditedAt(Instant editedAt) { this.editedAt = editedAt; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatMessage that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }

    @Override
    public String toString() {
        return "ChatMessage{id='" + id + "', roomId='" + roomId + "', author='" + authorName + "'}";
    }
}