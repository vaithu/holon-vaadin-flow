-- ============================================================
-- Holon Vaadin Flow Chat module – database schema
-- Compatible with: PostgreSQL, MySQL 8+, H2 (dev/test)
-- ============================================================

-- ------------------------------------------------------------
-- chat_room
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS chat_room (
    id          VARCHAR(256)  NOT NULL,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(1000),
    type        VARCHAR(20)   NOT NULL DEFAULT 'CHANNEL',
    is_private  BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_chat_room PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_chat_room_type ON chat_room (type);

-- Seed default public channel so the app works out-of-the-box
INSERT INTO chat_room (id, name, description, type, is_private)
VALUES ('general', 'general', 'General discussion', 'CHANNEL', FALSE)
    ON CONFLICT (id) DO NOTHING;

-- ------------------------------------------------------------
-- chat_room_member  (join/leave tracking)
-- ------------------------------------------------------------
-- Membership table: tracks who joined which room and their role.
-- Public channels: anyone can browse; joining creates a row here.
-- Groups / private channels: row required to access.
-- Not used for DIRECT rooms (access is always implicit for both participants).
CREATE TABLE IF NOT EXISTS chat_room_member (
    user_id   VARCHAR(255) NOT NULL,
    room_id   VARCHAR(256) NOT NULL,
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    role      VARCHAR(20)  NOT NULL DEFAULT 'MEMBER',
    CONSTRAINT pk_chat_room_member PRIMARY KEY (user_id, room_id),
    CONSTRAINT fk_member_room FOREIGN KEY (room_id) REFERENCES chat_room (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_chat_room_member_room ON chat_room_member (room_id);
CREATE INDEX IF NOT EXISTS idx_chat_room_member_user ON chat_room_member (user_id);

-- ------------------------------------------------------------
-- chat_message
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS chat_message (
    id               VARCHAR(36)   NOT NULL,
    room_id          VARCHAR(256)  NOT NULL,
    author_id        VARCHAR(255)  NOT NULL,
    author_name      VARCHAR(255)  NOT NULL,
    author_image_url VARCHAR(2048),
    text             TEXT          NOT NULL DEFAULT '',
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    edited_at        TIMESTAMP WITH TIME ZONE,
    deleted          BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_chat_message PRIMARY KEY (id),
    CONSTRAINT fk_chat_message_room FOREIGN KEY (room_id) REFERENCES chat_room (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_chat_message_room_created
    ON chat_message (room_id, created_at);

-- ------------------------------------------------------------
-- chat_read_receipt
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS chat_read_receipt (
    user_id      VARCHAR(255) NOT NULL,
    room_id      VARCHAR(256) NOT NULL,
    last_read_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_chat_read_receipt PRIMARY KEY (user_id, room_id),
    CONSTRAINT fk_chat_receipt_room FOREIGN KEY (room_id) REFERENCES chat_room (id) ON DELETE CASCADE
);

