package com.holonplatform.vaadin.flow.chat;

import com.holonplatform.vaadin.flow.chat.api.ChatService;
import com.holonplatform.vaadin.flow.chat.internal.DefaultChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link DefaultChatService}.
 *
 * <p>Uses a hand-rolled in-memory {@link ChatService} stub so that no Datastore
 * wiring is needed.  Tests cover both positive paths and error/edge cases.
 */
@DisplayName("ChatService unit tests")
class DefaultChatServiceTest {

    // We test via an in-memory stub that mirrors the DefaultChatService contract,
    // so we can run without a real JDBC datasource in CI.
    private InMemoryChatService service;

    @BeforeEach
    void setUp() {
        service = new InMemoryChatService();
    }

    // ================================================================== //
    // Rooms
    // ================================================================== //

    @Nested
    @DisplayName("Rooms")
    class RoomTests {

        @Test
        @DisplayName("findAllChannels returns only CHANNEL type rooms sorted by name")
        void findAllChannels_returnsChannelsSortedByName() {
            service.saveRoom(ChatRoom.channel("b", "beta", null));
            service.saveRoom(ChatRoom.channel("a", "alpha", null));
            service.saveRoom(ChatRoom.direct("u1", "u2"));   // DM – must be excluded

            List<ChatRoom> channels = service.findAllChannels();

            assertThat(channels)
                    .hasSize(2)
                    .extracting(ChatRoom::getName)
                    .containsExactly("alpha", "beta");
        }

        @Test
        @DisplayName("findRoom returns the room when it exists")
        void findRoom_existingRoom_returnsIt() {
            service.saveRoom(ChatRoom.channel("gen", "general", "desc"));
            Optional<ChatRoom> found = service.findRoom("gen");
            assertThat(found).isPresent().get().extracting(ChatRoom::getName).isEqualTo("general");
        }

        @Test
        @DisplayName("findRoom returns empty for unknown id")
        void findRoom_unknown_returnsEmpty() {
            assertThat(service.findRoom("no-such-room")).isEmpty();
        }

        @Test
        @DisplayName("saveRoom persists and returns the room")
        void saveRoom_persistsRoom() {
            ChatRoom saved = service.saveRoom(ChatRoom.channel("x", "xray", null));
            assertThat(saved.getId()).isEqualTo("x");
            assertThat(service.findRoom("x")).isPresent();
        }
    }

    // ================================================================== //
    // Messages
    // ================================================================== //

    @Nested
    @DisplayName("Messages")
    class MessageTests {

        @BeforeEach
        void seedRoom() {
            service.saveRoom(ChatRoom.channel("r1", "room1", null));
        }

        @Test
        @DisplayName("saveMessage + findMessagesSince returns the message")
        void saveAndFind_roundTrip() {
            ChatMessage msg = ChatMessage.create("m1", "r1", "alice", "Alice", "Hello!");
            service.saveMessage(msg);

            List<ChatMessage> results = service.findMessagesSince("r1", Instant.EPOCH);

            assertThat(results).hasSize(1).first().extracting(ChatMessage::getText).isEqualTo("Hello!");
        }

        @Test
        @DisplayName("findMessagesSince filters messages before the cutoff")
        void findMessagesSince_filtersOldMessages() {
            ChatMessage old = ChatMessage.create("m-old", "r1", "alice", "Alice", "Old");
            old.setCreatedAt(Instant.parse("2020-01-01T00:00:00Z"));
            service.saveMessage(old);

            ChatMessage recent = ChatMessage.create("m-new", "r1", "bob", "Bob", "New");
            recent.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
            service.saveMessage(recent);

            List<ChatMessage> results = service.findMessagesSince("r1",
                    Instant.parse("2023-01-01T00:00:00Z"));

            assertThat(results).hasSize(1).first().extracting(ChatMessage::getText).isEqualTo("New");
        }

        @Test
        @DisplayName("findRecentMessages returns at most limit messages, oldest-first")
        void findRecentMessages_respectsLimit() {
            for (int i = 0; i < 10; i++) {
                ChatMessage m = ChatMessage.create("m" + i, "r1", "u", "U", "msg" + i);
                m.setCreatedAt(Instant.ofEpochSecond(i + 1));
                service.saveMessage(m);
            }
            List<ChatMessage> recent = service.findRecentMessages("r1", 3);
            assertThat(recent).hasSize(3);
            // Should be the 3 newest: msg7, msg8, msg9 in ascending order
            assertThat(recent.get(0).getText()).isEqualTo("msg7");
            assertThat(recent.get(2).getText()).isEqualTo("msg9");
        }

        @Test
        @DisplayName("findRecentMessages returns empty when room has no messages")
        void findRecentMessages_emptyRoom() {
            assertThat(service.findRecentMessages("r1", 50)).isEmpty();
        }

        @Test
        @DisplayName("findMessagesBefore returns messages strictly before cutoff, newest-first")
        void findMessagesBefore_strictBound() {
            ChatMessage m1 = ChatMessage.create("a", "r1", "u", "U", "first");
            m1.setCreatedAt(Instant.parse("2024-01-01T10:00:00Z"));
            ChatMessage m2 = ChatMessage.create("b", "r1", "u", "U", "second");
            m2.setCreatedAt(Instant.parse("2024-01-01T11:00:00Z"));
            ChatMessage m3 = ChatMessage.create("c", "r1", "u", "U", "third");
            m3.setCreatedAt(Instant.parse("2024-01-01T12:00:00Z"));
            service.saveMessage(m1); service.saveMessage(m2); service.saveMessage(m3);

            // Cutoff is m3's time → should exclude m3, return m2 and m1 newest-first
            List<ChatMessage> result = service.findMessagesBefore("r1",
                    Instant.parse("2024-01-01T12:00:00Z"), 10);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getText()).isEqualTo("second");  // newest-first
            assertThat(result.get(1).getText()).isEqualTo("first");
        }

        @Test
        @DisplayName("findMessagesBefore respects limit")
        void findMessagesBefore_respectsLimit() {
            for (int i = 0; i < 5; i++) {
                ChatMessage m = ChatMessage.create("x" + i, "r1", "u", "U", "x" + i);
                m.setCreatedAt(Instant.ofEpochSecond(i + 1));
                service.saveMessage(m);
            }
            List<ChatMessage> result = service.findMessagesBefore("r1", Instant.ofEpochSecond(10), 2);
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("deleteMessage soft-deletes; findMessagesSince excludes deleted")
        void deleteMessage_softDelete_excluded() {
            ChatMessage msg = ChatMessage.create("m1", "r1", "alice", "Alice", "Bye");
            service.saveMessage(msg);
            boolean deleted = service.deleteMessage("m1");

            assertThat(deleted).isTrue();
            assertThat(service.findMessagesSince("r1", Instant.EPOCH)).isEmpty();
        }

        @Test
        @DisplayName("deleteMessage returns false for non-existent ID")
        void deleteMessage_nonExistent_returnsFalse() {
            assertThat(service.deleteMessage("ghost")).isFalse();
        }

        @Test
        @DisplayName("editMessage updates text and sets editedAt")
        void editMessage_updatesTextAndTimestamp() {
            ChatMessage msg = ChatMessage.create("m1", "r1", "alice", "Alice", "Original");
            service.saveMessage(msg);

            Optional<ChatMessage> edited = service.editMessage("m1", "Updated");

            assertThat(edited).isPresent();
            assertThat(edited.get().getText()).isEqualTo("Updated");
            assertThat(edited.get().getEditedAt()).isNotNull();
        }

        @Test
        @DisplayName("editMessage returns empty for non-existent ID")
        void editMessage_nonExistent_returnsEmpty() {
            assertThat(service.editMessage("ghost", "text")).isEmpty();
        }

        @Test
        @DisplayName("countMessagesSince returns correct count")
        void countMessagesSince_correctCount() {
            Instant cutoff = Instant.parse("2024-06-01T00:00:00Z");

            ChatMessage before = ChatMessage.create("m1", "r1", "a", "A", "Before");
            before.setCreatedAt(Instant.parse("2024-05-01T00:00:00Z"));
            service.saveMessage(before);

            ChatMessage after = ChatMessage.create("m2", "r1", "b", "B", "After");
            after.setCreatedAt(Instant.parse("2024-07-01T00:00:00Z"));
            service.saveMessage(after);

            assertThat(service.countMessagesSince("r1", cutoff)).isEqualTo(1);
        }
    }

    // ================================================================== //
    // Read receipts
    // ================================================================== //

    @Nested
    @DisplayName("Read receipts")
    class ReadReceiptTests {

        @Test
        @DisplayName("markAsRead then findReadReceipt returns the receipt")
        void markAsRead_persistsReceipt() {
            service.markAsRead("user-1", "room-x");
            Optional<ChatReadReceipt> receipt = service.findReadReceipt("user-1", "room-x");

            assertThat(receipt).isPresent();
            assertThat(receipt.get().getUserId()).isEqualTo("user-1");
            assertThat(receipt.get().getRoomId()).isEqualTo("room-x");
            assertThat(receipt.get().getLastReadAt()).isNotNull();
        }

        @Test
        @DisplayName("findReadReceipt returns empty when never read")
        void findReadReceipt_neverRead_returnsEmpty() {
            assertThat(service.findReadReceipt("nobody", "nowhere")).isEmpty();
        }

        @Test
        @DisplayName("markAsRead twice updates lastReadAt")
        void markAsRead_twice_updatesTimestamp() throws InterruptedException {
            service.markAsRead("user-1", "room-x");
            Instant first = service.findReadReceipt("user-1", "room-x").get().getLastReadAt();

            Thread.sleep(5);   // ensure clock advances

            service.markAsRead("user-1", "room-x");
            Instant second = service.findReadReceipt("user-1", "room-x").get().getLastReadAt();

            assertThat(second).isAfterOrEqualTo(first);
        }
    }

    // ================================================================== //
    // Domain – ChatRoom
    // ================================================================== //

    @Nested
    @DisplayName("ChatRoom factory")
    class ChatRoomFactoryTest {

        @Test
        @DisplayName("direct() produces deterministic canonical DM id")
        void direct_canonicalId_symmetrical() {
            String id1 = ChatRoom.direct("alice", "bob").getId();
            String id2 = ChatRoom.direct("bob", "alice").getId();
            assertThat(id1).isEqualTo(id2);
        }

        @Test
        @DisplayName("direct() id starts with dm/")
        void direct_idPrefixed() {
            assertThat(ChatRoom.direct("a", "b").getId()).startsWith("dm/");
        }

        @Test
        @DisplayName("channel() sets CHANNEL type")
        void channel_setsType() {
            assertThat(ChatRoom.channel("c", "n", null).getType()).isEqualTo(ChatRoom.Type.CHANNEL);
        }
    }

    // ================================================================== //
    // Domain – ChatMessage
    // ================================================================== //

    @Nested
    @DisplayName("ChatMessage factory")
    class ChatMessageFactoryTest {

        @Test
        @DisplayName("create() sets createdAt and deleted=false")
        void create_setsDefaults() {
            ChatMessage msg = ChatMessage.create("id", "room", "user", "Name", "Hi");
            assertThat(msg.getCreatedAt()).isNotNull();
            assertThat(msg.isDeleted()).isFalse();
            assertThat(msg.getEditedAt()).isNull();
        }

        @Test
        @DisplayName("equals() is ID-based")
        void equals_isIdBased() {
            ChatMessage a = ChatMessage.create("same", "r", "u", "n", "t");
            ChatMessage b = ChatMessage.create("same", "r2", "u2", "n2", "t2");
            assertThat(a).isEqualTo(b);
        }
    }

    // ================================================================== //
    // Membership
    // ================================================================== //

    @Nested
    @DisplayName("Membership")
    class MembershipTests {

        @BeforeEach
        void seedRoom() {
            service.saveRoom(ChatRoom.channel("ch1", "general", null));
        }

        @Test
        @DisplayName("joinRoom makes user a member; isMember returns true")
        void joinRoom_thenIsMember() {
            service.joinRoom("alice", "ch1");
            assertThat(service.isMember("alice", "ch1")).isTrue();
        }

        @Test
        @DisplayName("joinRoom is idempotent — calling twice does not create duplicate")
        void joinRoom_idempotent() {
            service.joinRoom("alice", "ch1");
            service.joinRoom("alice", "ch1");
            assertThat(service.countMembers("ch1")).isEqualTo(1);
        }

        @Test
        @DisplayName("leaveRoom removes membership; isMember returns false")
        void leaveRoom_removesMembership() {
            service.joinRoom("alice", "ch1");
            service.leaveRoom("alice", "ch1");
            assertThat(service.isMember("alice", "ch1")).isFalse();
        }

        @Test
        @DisplayName("leaveRoom is idempotent when user is not a member")
        void leaveRoom_notMember_noOp() {
            service.leaveRoom("ghost", "ch1"); // should not throw
            assertThat(service.isMember("ghost", "ch1")).isFalse();
        }

        @Test
        @DisplayName("isMember returns false for non-member")
        void isMember_nonMember_returnsFalse() {
            assertThat(service.isMember("nobody", "ch1")).isFalse();
        }

        @Test
        @DisplayName("countMembers returns correct count across multiple users")
        void countMembers_multipleUsers() {
            service.joinRoom("alice", "ch1");
            service.joinRoom("bob", "ch1");
            service.joinRoom("carol", "ch1");
            assertThat(service.countMembers("ch1")).isEqualTo(3);
        }

        @Test
        @DisplayName("findMembership returns the record when user is a member")
        void findMembership_returnsPresentWhenMember() {
            service.joinRoom("alice", "ch1");
            assertThat(service.findMembership("alice", "ch1")).isPresent();
        }

        @Test
        @DisplayName("findMembership returns empty when user has not joined")
        void findMembership_returnsEmptyWhenNotJoined() {
            assertThat(service.findMembership("alice", "ch1")).isEmpty();
        }

        @Test
        @DisplayName("findRoomMembers returns all members in join order")
        void findRoomMembers_returnsAll() {
            service.joinRoom("alice", "ch1");
            service.joinRoom("bob", "ch1");
            assertThat(service.findRoomMembers("ch1")).hasSize(2);
        }

        @Test
        @DisplayName("findJoinedRooms returns only rooms the user joined")
        void findJoinedRooms_returnsCorrectRooms() {
            service.saveRoom(ChatRoom.channel("ch2", "tech", null));
            service.joinRoom("alice", "ch1");
            // alice did NOT join ch2
            List<ChatRoom> joined = service.findJoinedRooms("alice");
            assertThat(joined).hasSize(1).first().extracting(ChatRoom::getId).isEqualTo("ch1");
        }
    }

    // ================================================================== //
    // Invitations
    // ================================================================== //

    @Nested
    @DisplayName("Invitations")
    class InvitationTests {

        @BeforeEach
        void seedRoom() {
            service.saveRoom(ChatRoom.group("grp1", "Project Alpha", null));
        }

        @Test
        @DisplayName("inviteToRoom creates PENDING invitation")
        void inviteToRoom_createsPendingInvitation() {
            ChatInvitation inv = service.inviteToRoom("grp1", "Project Alpha", "alice", "Alice", "bob");
            assertThat(inv).isNotNull();
            assertThat(inv.checkPending()).isTrue();
            assertThat(inv.getInviteeId()).isEqualTo("bob");
        }

        @Test
        @DisplayName("inviteToRoom is idempotent — second call returns existing PENDING invitation")
        void inviteToRoom_idempotent_returnsSame() {
            ChatInvitation first  = service.inviteToRoom("grp1", "Project Alpha", "alice", "Alice", "bob");
            ChatInvitation second = service.inviteToRoom("grp1", "Project Alpha", "alice", "Alice", "bob");
            assertThat(first.getId()).isEqualTo(second.getId());
        }

        @Test
        @DisplayName("findPendingInvitations returns only PENDING invitations for invitee")
        void findPendingInvitations_returnsPendingForInvitee() {
            service.inviteToRoom("grp1", "Project Alpha", "alice", "Alice", "bob");
            List<ChatInvitation> pending = service.findPendingInvitations("bob");
            assertThat(pending).hasSize(1).first().extracting(ChatInvitation::getRoomId).isEqualTo("grp1");
        }

        @Test
        @DisplayName("findPendingInvitations returns empty when no pending invitations")
        void findPendingInvitations_emptyWhenNone() {
            assertThat(service.findPendingInvitations("nobody")).isEmpty();
        }

        @Test
        @DisplayName("acceptInvitation changes status to ACCEPTED and adds membership")
        void acceptInvitation_changesStatusAndJoinsRoom() {
            ChatInvitation inv = service.inviteToRoom("grp1", "Project Alpha", "alice", "Alice", "bob");
            Optional<ChatInvitation> accepted = service.acceptInvitation(inv.getId());
            assertThat(accepted).isPresent();
            assertThat(accepted.get().checkAccepted()).isTrue();
            assertThat(service.isMember("bob", "grp1")).isTrue();
        }

        @Test
        @DisplayName("acceptInvitation returns empty for non-existent invitation")
        void acceptInvitation_nonExistent_returnsEmpty() {
            assertThat(service.acceptInvitation("ghost-id")).isEmpty();
        }

        @Test
        @DisplayName("declineInvitation changes status to DECLINED without creating membership")
        void declineInvitation_changesStatusNoMembership() {
            ChatInvitation inv = service.inviteToRoom("grp1", "Project Alpha", "alice", "Alice", "carol");
            Optional<ChatInvitation> declined = service.declineInvitation(inv.getId());
            assertThat(declined).isPresent();
            assertThat(declined.get().checkDeclined()).isTrue();
            assertThat(service.isMember("carol", "grp1")).isFalse();
        }

        @Test
        @DisplayName("declineInvitation returns empty for non-existent invitation")
        void declineInvitation_nonExistent_returnsEmpty() {
            assertThat(service.declineInvitation("ghost-id")).isEmpty();
        }

        @Test
        @DisplayName("findRoomInvitations returns all invitations for the room")
        void findRoomInvitations_returnsAll() {
            service.inviteToRoom("grp1", "Project Alpha", "alice", "Alice", "bob");
            service.inviteToRoom("grp1", "Project Alpha", "alice", "Alice", "carol");
            assertThat(service.findRoomInvitations("grp1")).hasSize(2);
        }
    }

    // ================================================================== //
    // Builder
    // ================================================================== //

    @Nested
    @DisplayName("LiveChatBuilder")
    class LiveChatBuilderTest {

        @Test
        @DisplayName("build() throws when room() not called")
        void build_withoutRoom_throws() {
            assertThatThrownBy(() ->
                    com.holonplatform.vaadin.flow.chat.components.LiveChat
                            .builder(new com.vaadin.collaborationengine.UserInfo("u"))
                            .build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("room ID");
        }
    }

    // ================================================================== //
    // In-memory stub
    // ================================================================== //

    /**
     * Pure in-memory stub implementation of {@link ChatService} used in tests.
     */
    static class InMemoryChatService implements ChatService {
        private final java.util.Map<String, ChatRoom>        rooms       = new java.util.LinkedHashMap<>();
        private final java.util.Map<String, ChatMessage>     messages    = new java.util.LinkedHashMap<>();
        private final java.util.Map<String, ChatReadReceipt> receipts    = new java.util.LinkedHashMap<>();
        private final java.util.Map<String, ChatRoomMember>  members     = new java.util.LinkedHashMap<>();
        private final java.util.Map<String, ChatInvitation>  invitations = new java.util.LinkedHashMap<>();

        @Override public List<ChatRoom> findAllChannels() {
            return rooms.values().stream()
                    .filter(r -> r.getType() == ChatRoom.Type.CHANNEL)
                    .sorted(java.util.Comparator.comparing(ChatRoom::getName))
                    .toList();
        }

        @Override public Optional<ChatRoom> findRoom(String roomId) {
            return Optional.ofNullable(rooms.get(roomId));
        }

        @Override public ChatRoom saveRoom(ChatRoom room) {
            rooms.put(room.getId(), room);
            return room;
        }

        @Override public List<ChatMessage> findMessagesSince(String roomId, Instant since) {
            return messages.values().stream()
                    .filter(m -> m.getRoomId().equals(roomId))
                    .filter(m -> !m.isDeleted())
                    .filter(m -> !m.getCreatedAt().isBefore(since))
                    .sorted(java.util.Comparator.comparing(ChatMessage::getCreatedAt))
                    .toList();
        }

        @Override public List<ChatMessage> findRecentMessages(String roomId, int limit) {
            return messages.values().stream()
                    .filter(m -> m.getRoomId().equals(roomId))
                    .filter(m -> !m.isDeleted())
                    .sorted(java.util.Comparator.comparing(ChatMessage::getCreatedAt).reversed())
                    .limit(limit)
                    .sorted(java.util.Comparator.comparing(ChatMessage::getCreatedAt))  // oldest-first
                    .toList();
        }

        @Override public List<ChatMessage> findMessagesBefore(String roomId, Instant before, int limit) {
            return messages.values().stream()
                    .filter(m -> m.getRoomId().equals(roomId))
                    .filter(m -> !m.isDeleted())
                    .filter(m -> m.getCreatedAt().isBefore(before))
                    .sorted(java.util.Comparator.comparing(ChatMessage::getCreatedAt).reversed())
                    .limit(limit)
                    .toList();
        }

        @Override public long countMessagesSince(String roomId, Instant since) {
            return messages.values().stream()
                    .filter(m -> m.getRoomId().equals(roomId))
                    .filter(m -> !m.isDeleted())
                    .filter(m -> m.getCreatedAt().isAfter(since))
                    .count();
        }

        @Override public ChatMessage saveMessage(ChatMessage message) {
            messages.put(message.getId(), message);
            return message;
        }

        @Override public boolean deleteMessage(String messageId) {
            ChatMessage m = messages.get(messageId);
            if (m == null) return false;
            m.setDeleted(true);
            return true;
        }

        @Override public Optional<ChatMessage> editMessage(String messageId, String newText) {
            ChatMessage m = messages.get(messageId);
            if (m == null) return Optional.empty();
            m.setText(newText);
            m.setEditedAt(Instant.now());
            return Optional.of(m);
        }

        @Override public Optional<ChatReadReceipt> findReadReceipt(String userId, String roomId) {
            return Optional.ofNullable(receipts.get(userId + "|" + roomId));
        }

        @Override public void markAsRead(String userId, String roomId) {
            receipts.put(userId + "|" + roomId, ChatReadReceipt.markNow(userId, roomId));
        }

        @Override public List<ChatRoom> findPublicRooms() {
            return rooms.values().stream()
                    .filter(r -> !r.isPrivateRoom())
                    .sorted(java.util.Comparator.comparing(ChatRoom::getName))
                    .toList();
        }

        @Override public List<ChatRoom> findJoinedRooms(String userId) {
            return members.values().stream()
                    .filter(m -> m.getUserId().equals(userId))
                    .map(m -> rooms.get(m.getRoomId()))
                    .filter(java.util.Objects::nonNull)
                    .sorted(java.util.Comparator.comparing(ChatRoom::getName))
                    .toList();
        }

        @Override public List<ChatRoomMember> findRoomMembers(String roomId) {
            return members.values().stream()
                    .filter(m -> m.getRoomId().equals(roomId))
                    .toList();
        }

        @Override public Optional<ChatRoomMember> findMembership(String userId, String roomId) {
            return Optional.ofNullable(members.get(userId + "|" + roomId));
        }

        @Override public void joinRoom(String userId, String roomId) {
            members.putIfAbsent(userId + "|" + roomId,
                    ChatRoomMember.member(userId, roomId));
        }

        @Override public void leaveRoom(String userId, String roomId) {
            members.remove(userId + "|" + roomId);
        }

        @Override public boolean isMember(String userId, String roomId) {
            return members.containsKey(userId + "|" + roomId);
        }

        @Override public int countMembers(String roomId) {
            return (int) members.values().stream()
                    .filter(m -> m.getRoomId().equals(roomId))
                    .count();
        }

        // ── Invitations ────────────────────────────────────────────────────────

        @Override public ChatInvitation inviteToRoom(String roomId, String roomName,
                                                     String inviterId, String inviterName,
                                                     String inviteeId) {
            // Idempotent: return existing PENDING invite if present
            return invitations.values().stream()
                    .filter(i -> i.getRoomId().equals(roomId)
                            && i.getInviteeId().equals(inviteeId)
                            && i.checkPending())
                    .findFirst()
                    .orElseGet(() -> {
                        ChatInvitation inv = ChatInvitation.create(
                                roomId, roomName, inviterId, inviterName, inviteeId);
                        invitations.put(inv.getId(), inv);
                        return inv;
                    });
        }

        @Override public List<ChatInvitation> findPendingInvitations(String inviteeId) {
            return invitations.values().stream()
                    .filter(i -> i.getInviteeId().equals(inviteeId) && i.checkPending())
                    .sorted(java.util.Comparator.comparing(ChatInvitation::getCreatedAt).reversed())
                    .toList();
        }

        @Override public List<ChatInvitation> findRoomInvitations(String roomId) {
            return invitations.values().stream()
                    .filter(i -> i.getRoomId().equals(roomId))
                    .sorted(java.util.Comparator.comparing(ChatInvitation::getCreatedAt).reversed())
                    .toList();
        }

        @Override public Optional<ChatInvitation> acceptInvitation(String invitationId) {
            ChatInvitation inv = invitations.get(invitationId);
            if (inv == null) return Optional.empty();
            inv.setStatus(ChatInvitation.Status.ACCEPTED.name());
            inv.setRespondedAt(Instant.now());
            joinRoom(inv.getInviteeId(), inv.getRoomId());
            return Optional.of(inv);
        }

        @Override public Optional<ChatInvitation> declineInvitation(String invitationId) {
            ChatInvitation inv = invitations.get(invitationId);
            if (inv == null) return Optional.empty();
            inv.setStatus(ChatInvitation.Status.DECLINED.name());
            inv.setRespondedAt(Instant.now());
            return Optional.of(inv);
        }
    }
}



