package com.holonplatform.vaadin.flow.chat.components;

import com.holonplatform.vaadin.flow.chat.ChatRoom;
import com.holonplatform.vaadin.flow.chat.api.ChatService;
import com.holonplatform.vaadin.flow.chat.i18n.ChatI18N;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.collaborationengine.MessageManager;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;

import java.io.Serial;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sidebar channel list for the Live Chat.
 *
 * <h3>Lazy loading strategy</h3>
 * <ul>
 *   <li><strong>Initial unread counts</strong> are computed once from the DB
 *       (snapshot via {@link ChatService#countMessagesSince}) when the sidebar loads.</li>
 *   <li><strong>Active room</strong> — one {@link MessageManager} is open only for
 *       the currently visible room.</li>
 *   <li>On {@link #setActiveRoom} the unread count for that room is reset and
 *       {@link ChatService#markAsRead} is persisted.</li>
 * </ul>
 *
 * <h3>Signal architecture</h3>
 * <p>Each {@link RoomRow} owns a {@code ValueSignal<Integer> unreadSignal}. A shared
 * {@code ValueSignal<String> activeRoomSignal} tracks which room is selected. Both are
 * read inside {@code Signal.effect()} calls so DOM updates are reactive and batched.
 * <ul>
 *   <li>Badge writes are <em>batched</em>: rapid increments in the same UI cycle produce
 *       a single {@code badge.setText()} call.</li>
 *   <li>Active-state CSS is <em>atomic</em>: one {@code activeRoomSignal.set()} drives
 *       all rows — no O(N) imperative loop needed on room switch.</li>
 *   <li>No separate {@code ConcurrentHashMap<String,Integer>} — the signal IS the count.</li>
 * </ul>
 *
 * <p><strong>peek() vs get()</strong>: {@code signal.get()} is only called inside
 * {@code Signal.effect()} lambdas (where dependency tracking is required). Every other
 * read uses {@code signal.peek()} to avoid the "outside reactive context" exception.
 *
 * <p>All visual styling is in {@code chat-channel-list.css}.
 */
@StyleSheet("context://chat-channel-list.css")
public class ChatChannelList extends Composite<Div> {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UserInfo localUser;
    private final ChatService chatService;

    /** Single active-room MessageManager (at most one open at any time). */
    private MessageManager activeRoomManager;

    /**
     * Which room is currently active. Read via {@link ValueSignal#peek()} outside effects;
     * read via {@link ValueSignal#get()} only inside {@code Signal.effect()} lambdas.
     */
    private final ValueSignal<String> activeRoomSignal = new ValueSignal<>(null);

    /** Row components indexed by roomId. */
    private final Map<String, RoomRow> roomRows = new ConcurrentHashMap<>();

    /**
     * When {@code true}, the sidebar header shows Create and Browse buttons.
     * Enable via {@link #enableRoomManagement()}.
     */
    private boolean managementEnabled;

    public ChatChannelList(UserInfo localUser, ChatService chatService) {
        if (localUser == null) throw new IllegalArgumentException("localUser must not be null");
        if (chatService == null) throw new IllegalArgumentException("chatService must not be null");
        this.localUser = localUser;
        this.chatService = chatService;
        getContent().addClassName("chat-channel-list");
    }

    // ------------------------------------------------------------------ //
    // Lifecycle
    // ------------------------------------------------------------------ //

    @Override
    protected void onAttach(com.vaadin.flow.component.AttachEvent event) {
        super.onAttach(event);
        loadRooms();
    }

    @Override
    protected void onDetach(com.vaadin.flow.component.DetachEvent event) {
        closeActiveManager();
        super.onDetach(event);
    }

    // ------------------------------------------------------------------ //
    // Public API
    // ------------------------------------------------------------------ //

    /** Reloads the room list from the DB and rebuilds rows. */
    public void refresh() {
        loadRooms();
    }

    /**
     * Enables the Create + Browse management buttons in the sidebar header.
     * Must be called before the component is attached.
     */
    public void enableRoomManagement() {
        this.managementEnabled = true;
    }

    /**
     * Marks the given room as active: clears its unread badge, persists a read
     * receipt, and opens a live {@link MessageManager} for that room.
     */
    public void setActiveRoom(String roomId) {
        // One signal write drives all row active-CSS effects atomically.
        activeRoomSignal.set(roomId);

        // Clear unread badge for the newly active room.
        RoomRow row = roomRows.get(roomId);
        if (row != null) {
            row.unreadSignal.set(0);
        }

        chatService.markAsRead(localUser.getId(), roomId);

        closeActiveManager();
        openActiveRoomManager(roomId);
    }

    @SuppressWarnings("UnusedReturnValue")
    public Registration addRoomSelectedListener(ComponentEventListener<RoomSelectedEvent> listener) {
        return addListener(RoomSelectedEvent.class, listener);
    }

    // ------------------------------------------------------------------ //
    // Internal
    // ------------------------------------------------------------------ //

    @SuppressWarnings("deprecation") // findAllChannels fallback for backwards-compatibility
    private void loadRooms() {
        closeActiveManager();
        roomRows.clear();
        getContent().removeAll();

        // ---- header row with optional management buttons ----
        getContent().add(buildListHeader());

        // Use findJoinedRooms when room-management is enabled (membership model);
        // fall back to findAllChannels for backwards-compatibility with implementations
        // that haven't added membership support yet.
        List<ChatRoom> channels;
        try {
            channels = chatService.findJoinedRooms(localUser.getId());
        } catch (UnsupportedOperationException ignored) {
            //noinspection deprecation
            channels = chatService.findAllChannels();
        }

        for (ChatRoom room : channels) {
            int unread = computeSnapshotUnread(room.getId());
            RoomRow roomRow = new RoomRow(room, unread, activeRoomSignal);
            roomRow.addClickListener(e -> onRoomClicked(room));
            roomRows.put(room.getId(), roomRow);
            getContent().add(roomRow);
        }

        // Re-subscribe active room if one was already selected.
        String currentActive = activeRoomSignal.peek();
        if (currentActive != null) {
            RoomRow active = roomRows.get(currentActive);
            if (active != null) {
                active.unreadSignal.set(0);
            }
            openActiveRoomManager(currentActive);
        }
    }

    /**
     * Builds the "Channels" header row.
     * When {@link #managementEnabled} is {@code true}, adds "+" (create) and "⊕" (browse) buttons.
     */
    private Div buildListHeader() {
        Span label = new Span(LocalizationProvider.localize("Channels", ChatI18N.CHANNEL_LIST_HEADER));
        label.addClassName("chat-channel-list__header-label");

        Div header = new Div(label);
        header.addClassName("chat-channel-list__header");

        if (managementEnabled) {
            // Create room button
            Span createBtn = new Span("+");
            createBtn.addClassName("chat-channel-list__header-btn");
            createBtn.addClassName("chat-channel-list__header-btn--create");
            createBtn.getElement().setAttribute("title",
                    LocalizationProvider.localize("Create channel or group", ChatI18N.CHANNEL_LIST_CREATE_TOOLTIP));
            createBtn.addClickListener(e ->
                    new CreateRoomDialog(chatService, localUser.getId())
                            .onCreated(room -> {
                                loadRooms();
                                setActiveRoom(room.getId());
                            })
                            .open());

            // Browse rooms button
            Span browseBtn = new Span("⊕");
            browseBtn.addClassName("chat-channel-list__header-btn");
            browseBtn.addClassName("chat-channel-list__header-btn--browse");
            browseBtn.getElement().setAttribute("title",
                    LocalizationProvider.localize("Browse & join rooms", ChatI18N.CHANNEL_LIST_BROWSE_TOOLTIP));
            browseBtn.addClickListener(e ->
                    new BrowseRoomsDialog(chatService, localUser.getId())
                            .onMembershipChanged(roomId -> loadRooms())
                            .open());

            header.add(createBtn, browseBtn);
        }
        return header;
    }

    private int computeSnapshotUnread(String roomId) {
        return chatService.findReadReceipt(localUser.getId(), roomId)
                .map(r -> (int) chatService.countMessagesSince(roomId, r.getLastReadAt()))
                .orElseGet(() -> (int) chatService.countMessagesSince(roomId, Instant.EPOCH));
    }

    /**
     * Opens a single {@link MessageManager} for the active room.
     * Badge increments use {@code signal.set()} — thread-safe, batched by Vaadin.
     */
    private void openActiveRoomManager(String roomId) {
        activeRoomManager = new MessageManager(this, localUser, roomId);
        activeRoomManager.setMessageHandler(ctx -> {
            String senderId = ctx.getMessage().getUser().getId();
            if (localUser.getId().equals(senderId)) return;

            // peek() — message handler runs outside Signal.effect(); no tracking context.
            String active = activeRoomSignal.peek();
            roomRows.forEach((rid, row) -> {
                if (!rid.equals(active)) {
                    row.incrementUnread();
                }
            });
        });
    }

    private void closeActiveManager() {
        if (activeRoomManager != null) {
            activeRoomManager.close();
            activeRoomManager = null;
        }
    }

    private void onRoomClicked(ChatRoom room) {
        setActiveRoom(room.getId());
        fireEvent(new RoomSelectedEvent(this, false, room));
    }

    // ================================================================== //
    // Inner: RoomRow
    // ================================================================== //

    /**
     * A single selectable row in the channel sidebar.
     *
     * <h3>Signal design</h3>
     * <ul>
     *   <li>{@link #unreadSignal} — authoritative unread counter; {@code Signal.effect()}
     *       in the constructor renders the badge reactively.</li>
     *   <li>{@code activeRoomSignal} (shared) — {@code Signal.effect()} applies/removes
     *       the active CSS class without any external coordination.</li>
     * </ul>
     * Both effects are lifecycle-bound to {@code this}: they auto-dispose when the row is
     * removed from the DOM (no memory leaks on {@code loadRooms()} rebuild).
     */
    static class RoomRow extends Div {

        @Serial
        private static final long serialVersionUID = 1L;

        /** Per-row authoritative unread count. Written by {@link #incrementUnread()}. */
        final ValueSignal<Integer> unreadSignal;

        private final Span badge;

        RoomRow(ChatRoom room, int initialUnread, ValueSignal<String> activeRoomSignal) {
            addClassName("chat-channel-list__item");

            Span icon = new Span(room.getType() == ChatRoom.Type.DIRECT ? "@" : "#");
            icon.addClassName("chat-channel-list__icon");

            Span name = new Span(room.getName());
            name.addClassName("chat-channel-list__name");

            badge = new Span();
            badge.addClassName("chat-channel-list__badge");
            badge.getElement().setAttribute("theme", "badge contrast pill small");
            add(icon, name, badge);

            unreadSignal = new ValueSignal<>(initialUnread);

            // Effect 1 — badge. get() inside effect = dependency tracking. ✓
            Signal.effect(this, () -> {
                int count = unreadSignal.get();
                badge.setVisible(count > 0);
                badge.setText(count > 99 ? "99+" : String.valueOf(count));
            });

            // Effect 2 — active CSS. get() inside effect = dependency tracking. ✓
            Signal.effect(this, () -> {
                boolean active = room.getId().equals(activeRoomSignal.get());
                if (active) addClassName("chat-channel-list__item--active");
                else removeClassName("chat-channel-list__item--active");
            });
        }

        /**
         * Increments the unread count by 1.
         * peek() used because this is called outside any Signal.effect().
         */
        void incrementUnread() {
            unreadSignal.set(unreadSignal.peek() + 1);
        }
    }

    // ================================================================== //
    // Event: RoomSelectedEvent
    // ================================================================== //

    public static class RoomSelectedEvent extends ComponentEvent<ChatChannelList> {

        @Serial
        private static final long serialVersionUID = 1L;

        private final ChatRoom room;

        RoomSelectedEvent(ChatChannelList source, boolean fromClient, ChatRoom room) {
            super(source, fromClient);
            this.room = room;
        }

        public ChatRoom getRoom() { return room; }
    }
}
