package com.holonplatform.vaadin.flow.chat.components;

import com.holonplatform.vaadin.flow.chat.ChatMessage;
import com.holonplatform.vaadin.flow.chat.api.ChatService;
import com.holonplatform.vaadin.flow.chat.builders.LiveChatBuilder;
import com.holonplatform.vaadin.flow.chat.i18n.ChatI18N;
import com.holonplatform.vaadin.flow.chat.internal.HolonChatPersister;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.collaborationengine.CollaborationAvatarGroup;
import com.vaadin.collaborationengine.CollaborationMessageInput;
import com.vaadin.collaborationengine.CollaborationMessageList;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;

import java.io.Serial;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Full-featured live chat component with lazy message loading.
 *
 * <p>Composes all chat sub-components into a single, ready-to-use widget:
 * <pre>
 * LiveChat
 * ├── [sidebar]   ChatChannelList     (optional; visible when multi-channel)
 * └── chat__panel
 *     ├── chat__header
 *     │   ├── room title (H3)
 *     │   └── CollaborationAvatarGroup (who's in the room)
 *     ├── chat__messages
 *     │   ├── chat__history           (lazy older-page MessageList, hidden initially)
 *     │   ├── chat__load-more         ("Load older messages" button, hidden when no more)
 *     │   └── CollaborationMessageList (live messages — last N only on initial load)
 *     ├── chat__typing
 *     │   └── TypingIndicator
 *     └── chat__input
 *         └── CollaborationMessageInput
 * </pre>
 *
 * <h3>Lazy loading</h3>
 * <p>On first open only the last {@code pageSize} (default 50) messages are loaded.
 * A "Load older messages" button appears above them; each click fetches the previous
 * {@code pageSize} rows from the {@link ChatService} and prepends them without any
 * live-sync overhead — they are static history, not Collaboration Kit topics.
 *
 * <h3>Single-channel usage</h3>
 * <pre>{@code
 * LiveChat chat = LiveChat.builder(userInfo)
 *     .room("general")
 *     .withPersistence(chatService)
 *     .withTypingIndicator()
 *     .build();
 * add(chat);
 * }</pre>
 *
 * <h3>Custom page size</h3>
 * <pre>{@code
 * LiveChat chat = LiveChat.builder(userInfo)
 *     .room("general")
 *     .withPersistence(chatService)
 *     .withPageSize(25)      // load last 25 messages; each "Load older" fetches 25 more
 *     .build();
 * }</pre>
 *
 * @see LiveChatBuilder
 */
@StyleSheet("context://live-chat.css")
public class LiveChat extends Composite<Div> {

    @Serial
    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------------ //
    // Sub-components
    // ------------------------------------------------------------------ //

    private final UserInfo localUser;

    private ChatChannelList channelList;
    private CollaborationMessageList messageList;
    private CollaborationMessageInput messageInput;
    private TypingIndicator typingIndicator;
    private CollaborationAvatarGroup avatarGroup;

    /** Non-live history panel: older messages loaded by "Load older" clicks. */
    private MessageList historyList;
    /** Button that triggers the previous-page fetch. */
    private Div loadMoreButton;
    /** All history items accumulated across multiple "Load older" clicks (oldest first). */
    private final List<MessageListItem> historyItems = new ArrayList<>();

    private final H3 roomTitle = new H3();
    private String currentRoomId;

    /** The service used for paginated history fetches and persistence. */
    private ChatService chatService;
    private int pageSize = HolonChatPersister.DEFAULT_PAGE_SIZE;

    /**
     * Timestamp of the oldest message currently shown.
     * Initially {@code null} — set after {@link #assemble} from the first
     * "Load older" click or when we know the oldest live-message timestamp.
     */
    private Instant oldestLoadedTimestamp;

    /**
     * Guards against concurrent "Load older messages" fetches.
     *
     * <p>A {@code Signal.effect()} in {@link #onAttach} binds this to the
     * button's enabled state: while a DB fetch is in progress the button is
     * disabled; it re-enables automatically when the fetch completes.
     * No explicit {@code loadMoreButton.setEnabled()} calls are needed anywhere else.
     */
    private final ValueSignal<Boolean> isLoadingHistory = new ValueSignal<>(false);

    // ------------------------------------------------------------------ //
    // Constructor (package-private — use builder)
    // ------------------------------------------------------------------ //

    /**
     * Package-internal constructor — use {@link LiveChatBuilder} to create instances.
     *
     * @param localUser information about the currently logged-in user (not null)
     */
    public LiveChat(UserInfo localUser) {
        if (localUser == null) throw new IllegalArgumentException("localUser must not be null");
        this.localUser = localUser;
        getContent().addClassName("live-chat");
    }

    // ------------------------------------------------------------------ //
    // Lifecycle
    // ------------------------------------------------------------------ //

    @Override
    protected void onAttach(AttachEvent event) {
        super.onAttach(event);
        if (loadMoreButton != null) {
            // Reactive guard: button disables itself while a fetch is in flight.
            // Vaadin batches the enabled/disabled DOM write with the rest of the update.
            Signal.effect(loadMoreButton, () ->
                    loadMoreButton.getElement().setEnabled(!isLoadingHistory.get()));
        }
    }

    // ------------------------------------------------------------------ //
    // Static factory
    // ------------------------------------------------------------------ //

    /**
     * Returns a fluent builder for a {@link LiveChat} component.
     *
     * @param userInfo information about the currently logged-in user (not null)
     * @return a new {@link LiveChatBuilder}
     */
    public static LiveChatBuilder builder(UserInfo userInfo) {
        return LiveChatBuilder.create(userInfo);
    }

    // ------------------------------------------------------------------ //
    // Package-private assembly (called by the builder)
    // ------------------------------------------------------------------ //

    /**
     * Backward-compatible overload — defaults to group/channel mode (not DM).
     */
    public void assemble(String roomId, ChatService chatService, boolean enableTyping,
                         boolean showChannels, boolean enableRoomManagement, int pageSize) {
        assemble(roomId, chatService, enableTyping, showChannels, enableRoomManagement, pageSize, false);
    }

    /**
     * Assembles the component. Called exactly once by {@link LiveChatBuilder#build()}.
     */
    public void assemble(String roomId,
                         ChatService chatService,
                         boolean enableTyping,
                         boolean showChannels,
                         boolean enableRoomManagement,
                         int pageSize,
                         boolean directMessage) {

        this.currentRoomId    = roomId;
        this.chatService      = chatService;
        this.pageSize         = pageSize;

        if (directMessage) {
            getContent().addClassName("live-chat--direct");
        }

        HolonChatPersister persister = chatService != null
                ? new HolonChatPersister(chatService, pageSize)
                : null;

        messageList = persister != null
                ? new CollaborationMessageList(localUser, roomId, persister)
                : new CollaborationMessageList(localUser, roomId);

        messageList.setMessageConfigurator((message, user) -> {
            if (user.getId().equals(localUser.getId())) {
                message.addThemeNames("live-chat-own-message");
            }
        });

        avatarGroup = new CollaborationAvatarGroup(localUser, roomId);
        avatarGroup.addClassName("live-chat__avatars");

        messageInput = new CollaborationMessageInput(messageList);
        messageInput.addClassName("live-chat__input");

        if (enableTyping) {
            typingIndicator = new TypingIndicator(localUser, roomId);
            messageInput.getElement().addEventListener("keydown", e -> typingIndicator.startTyping())
                    .addEventData("event.key");
            messageInput.getElement().addEventListener("blur", e -> typingIndicator.stopTyping());
        }

        historyList = new MessageList();
        historyList.addClassName("live-chat__history");
        historyList.setVisible(false);

        loadMoreButton = new Div();
        loadMoreButton.addClassName("live-chat__load-more");
        loadMoreButton.setText(LocalizationProvider.localize("Load older messages", ChatI18N.LIVE_CHAT_LOAD_OLDER));
        loadMoreButton.setVisible(chatService != null);
        loadMoreButton.addClickListener(e -> loadOlderMessages());

        if (showChannels && chatService != null) {
            channelList = new ChatChannelList(localUser, chatService);
            channelList.addClassName("live-chat__channels");
            if (enableRoomManagement) {
                channelList.enableRoomManagement();
            }
            channelList.addRoomSelectedListener(e -> switchRoom(e.getRoom().getId()));
            channelList.setActiveRoom(roomId);
        }

        buildLayout(enableTyping, showChannels && chatService != null);
    }

    // ------------------------------------------------------------------ //
    // Layout assembly
    // ------------------------------------------------------------------ //

    private void buildLayout(boolean hasTyping, boolean hasChannels) {
        Div header = buildHeader();

        Div messagesWrapper = new Div(historyList, loadMoreButton, messageList);
        messagesWrapper.addClassName("live-chat__messages");

        Div panel = new Div();
        panel.addClassName("live-chat__panel");
        panel.add(header, messagesWrapper);

        if (hasTyping) {
            Div typingWrapper = new Div(typingIndicator);
            typingWrapper.addClassName("live-chat__typing");
            panel.add(typingWrapper);
        }

        // Input bar
        Div inputBar = new Div();
        inputBar.addClassName("live-chat__input-bar");
        inputBar.add(messageInput);
        panel.add(inputBar);

        if (hasChannels) {
            getContent().addClassName("live-chat--with-channels");
            getContent().add(channelList, panel);
        } else {
            getContent().add(panel);
        }
    }

    private Div buildHeader() {
        roomTitle.addClassName("live-chat__room-title");
        String prefix = LocalizationProvider.localize("# ", ChatI18N.LIVE_CHAT_ROOM_PREFIX);
        roomTitle.setText(prefix + currentRoomId);

        Div header = new Div(roomTitle, avatarGroup);
        header.addClassName("live-chat__header");
        return header;
    }

    // ------------------------------------------------------------------ //
    // Lazy "Load older" pagination
    // ------------------------------------------------------------------ //

    /**
     * Fetches the previous page of messages from {@link ChatService} and prepends
     * them to the history list. Each call fetches {@link #pageSize} messages
     * that are older than the oldest currently displayed.
     *
     * <p>This method is called when the user clicks the "Load older messages" button.
     * It does <em>not</em> use the Collaboration Kit topic — historical messages are
     * rendered as static {@link MessageListItem}s in a separate {@link MessageList}.
     */
    private void loadOlderMessages() {
        if (chatService == null) return;
        // Guard: reject re-entrant calls (double-click, rapid keyboard activation).
        // isLoadingHistory.set(true) disables the button via Signal.effect() in onAttach().
        // Guard: peek() — called outside Signal.effect(), no tracking context.
        if (Boolean.TRUE.equals(isLoadingHistory.peek())) return;
        isLoadingHistory.set(true);

        try {
            Instant before = oldestLoadedTimestamp != null
                    ? oldestLoadedTimestamp
                    : Instant.now();  // first click: fetch before now (pre-live messages)

            List<ChatMessage> page = chatService.findMessagesBefore(currentRoomId, before, pageSize);

            if (page.isEmpty()) {
                loadMoreButton.setVisible(false);
                return;
            }

            // page is newest-first (DESC order from DB) — reverse to oldest-first for display
            List<ChatMessage> chronological = new ArrayList<>(page);
            Collections.reverse(chronological);

            oldestLoadedTimestamp = chronological.getFirst().getCreatedAt();

            List<MessageListItem> newItems = chronological.stream()
                    .map(this::toMessageListItem)
                    .toList();

            historyItems.addAll(0, newItems);  // insert at front
            historyList.setItems(historyItems.toArray(new MessageListItem[0]));
            historyList.setVisible(true);

            if (page.size() < pageSize) {
                loadMoreButton.setVisible(false);
            }
        } finally {
            // Always re-enable — even if the fetch throws, the button must not stay locked.
            isLoadingHistory.set(false);
        }
    }

    private MessageListItem toMessageListItem(ChatMessage msg) {
        MessageListItem item = new MessageListItem(
                msg.getText(),
                msg.getCreatedAt(),
                msg.getAuthorName(),
                msg.getAuthorImageUrl());
        if (msg.getEditedAt() != null) {
            item.setUserAbbreviation("(edited)");
        }
        // Mark own messages so CSS can right-align them (same as CollaborationMessageList)
        if (localUser.getId().equals(msg.getAuthorId())) {
            item.addThemeNames("live-chat-own-message");
        }
        return item;
    }

    // ------------------------------------------------------------------ //
    // Room switching
    // ------------------------------------------------------------------ //

    /**
     * Switches the visible room to the given ID, resets history state,
     * and updates all sub-components to the new topic.
     *
     * @param newRoomId the target room ID (not null)
     */
    public void switchRoom(String newRoomId) {
        if (newRoomId.equals(currentRoomId)) return;

        currentRoomId = newRoomId;
        String prefix = LocalizationProvider.localize("# ", ChatI18N.LIVE_CHAT_ROOM_PREFIX);
        roomTitle.setText(prefix + newRoomId);

        // Reset lazy-load state for the new room
        historyItems.clear();
        historyList.setItems();
        historyList.setVisible(false);
        oldestLoadedTimestamp = null;
        loadMoreButton.setVisible(chatService != null);

        // Move Collaboration Kit topics
        avatarGroup.setTopic(newRoomId);
        messageList.setTopic(newRoomId);
        if (typingIndicator != null) {
            typingIndicator.stopTyping();
        }

        if (channelList != null) {
            channelList.setActiveRoom(newRoomId);
        }
    }

    // ------------------------------------------------------------------ //
    // Accessors
    // ------------------------------------------------------------------ //

    /** Returns the current room/topic ID. */
    public String getCurrentRoomId() { return currentRoomId; }

    /** Returns the underlying {@link CollaborationMessageList}. */
    public CollaborationMessageList getMessageList() { return messageList; }

    /** Returns the underlying {@link CollaborationMessageInput}. */
    public CollaborationMessageInput getMessageInput() { return messageInput; }

    /** Returns the avatar group. */
    public CollaborationAvatarGroup getAvatarGroup() { return avatarGroup; }

    /** Returns the typing indicator, or {@code null} if disabled. */
    public TypingIndicator getTypingIndicator() { return typingIndicator; }

    /** Returns the channel sidebar, or {@code null} in single-channel mode. */
    public ChatChannelList getChannelList() { return channelList; }

    /** Returns the static history list (non-live older messages). */
    public MessageList getHistoryList() { return historyList; }

    /** Returns the configured lazy page size. */
    public int getPageSize() { return pageSize; }
}


