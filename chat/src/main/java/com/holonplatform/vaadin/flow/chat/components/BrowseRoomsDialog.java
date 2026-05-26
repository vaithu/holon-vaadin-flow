package com.holonplatform.vaadin.flow.chat.components;

import com.holonplatform.vaadin.flow.chat.ChatRoom;
import com.holonplatform.vaadin.flow.chat.api.ChatService;
import com.holonplatform.vaadin.flow.chat.i18n.ChatI18N;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.io.Serial;
import java.util.List;
import java.util.function.Consumer;

/**
 * Dialog for browsing public rooms and joining or leaving them.
 *
 * <p>Displays all non-private rooms returned by {@link ChatService#findPublicRooms()}.
 * Each row shows the room name, description, member count, and a contextual
 * <em>Join</em> or <em>Leave</em> button depending on current membership.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * BrowseRoomsDialog dialog = new BrowseRoomsDialog(chatService, localUserId)
 *     .onMembershipChanged(roomId -> channelList.refresh());
 * dialog.open();
 * }</pre>
 *
 * <p>All visual styling is in {@code chat-room-dialog.css}.
 */
@StyleSheet("context://chat-room-dialog.css")
public class BrowseRoomsDialog extends Dialog {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ChatService chatService;
    private final String localUserId;
    private final Div roomList = new Div();
    private final TextField searchField = new TextField();

    /** Called after any join or leave action so the caller can refresh the sidebar. */
    private Consumer<String> onMembershipChanged;

    public BrowseRoomsDialog(ChatService chatService, String localUserId) {
        if (chatService == null) throw new IllegalArgumentException("chatService must not be null");
        if (localUserId == null) throw new IllegalArgumentException("localUserId must not be null");
        this.chatService = chatService;
        this.localUserId = localUserId;

        addClassName("browse-rooms-dialog");
        setWidth("520px");
        setHeight("500px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        add(buildHeader(), buildSearch(), roomList, buildFooter());
        loadRooms("");
    }

    /** Registers a callback invoked after join or leave, receiving the affected roomId. */
    public BrowseRoomsDialog onMembershipChanged(Consumer<String> callback) {
        this.onMembershipChanged = callback;
        return this;
    }

    // ------------------------------------------------------------------ //
    // Build
    // ------------------------------------------------------------------ //

    private H3 buildHeader() {
        H3 title = new H3(LocalizationProvider.localize("Browse rooms", ChatI18N.BROWSE_ROOMS_TITLE));
        title.addClassName("browse-rooms-dialog__title");
        return title;
    }

    private TextField buildSearch() {
        searchField.addClassName("browse-rooms-dialog__search");
        searchField.setPlaceholder(LocalizationProvider.localize("Search rooms\u2026", ChatI18N.BROWSE_ROOMS_SEARCH));
        searchField.setWidthFull();
        searchField.addValueChangeListener(e -> loadRooms(e.getValue()));
        searchField.setClearButtonVisible(true);
        return searchField;
    }

    private HorizontalLayout buildFooter() {
        Button close = new Button(LocalizationProvider.localize("Close", ChatI18N.BROWSE_ROOMS_CLOSE_BTN),
                e -> close());
        HorizontalLayout footer = new HorizontalLayout(close);
        footer.addClassName("browse-rooms-dialog__footer");
        return footer;
    }

    // ------------------------------------------------------------------ //
    // Logic
    // ------------------------------------------------------------------ //

    private void loadRooms(String filter) {
        roomList.removeAll();
        roomList.addClassName("browse-rooms-dialog__list");

        List<ChatRoom> rooms = chatService.findPublicRooms();
        if (rooms.isEmpty()) {
            roomList.add(new Paragraph(
                    LocalizationProvider.localize("No public rooms available.", ChatI18N.BROWSE_ROOMS_EMPTY)));
            return;
        }

        String lowerFilter = filter != null ? filter.toLowerCase() : "";
        rooms.stream()
                .filter(r -> lowerFilter.isBlank()
                        || r.getName().toLowerCase().contains(lowerFilter)
                        || (r.getDescription() != null
                                && r.getDescription().toLowerCase().contains(lowerFilter)))
                .forEach(room -> roomList.add(buildRoomRow(room)));
    }

    private Div buildRoomRow(ChatRoom room) {
        Div row = new Div();
        row.addClassName("browse-rooms-dialog__row");

        Span icon = new Span(room.getType() == ChatRoom.Type.GROUP ? "@" : "#");
        icon.addClassName("browse-rooms-dialog__row-icon");

        Span name = new Span(room.getName());
        name.addClassName("browse-rooms-dialog__row-name");

        Div info = new Div(name);
        info.addClassName("browse-rooms-dialog__row-info");
        if (room.getDescription() != null && !room.getDescription().isBlank()) {
            Span desc = new Span(room.getDescription());
            desc.addClassName("browse-rooms-dialog__row-desc");
            info.add(desc);
        }

        // Member count badge — singular/plural
        int memberCount = chatService.countMembers(room.getId());
        String memberLabel = memberCount == 1
                ? LocalizationProvider.localize("{0} member",  ChatI18N.BROWSE_ROOMS_MEMBER_ONE,  memberCount)
                : LocalizationProvider.localize("{0} members", ChatI18N.BROWSE_ROOMS_MEMBERS_MANY, memberCount);
        Span memberBadge = new Span(memberLabel);
        memberBadge.addClassName("browse-rooms-dialog__row-members");

        boolean joined = chatService.isMember(localUserId, room.getId());
        Button actionBtn = joined ? buildLeaveButton(room, row) : buildJoinButton(room, row);

        row.add(icon, info, memberBadge, actionBtn);
        return row;
    }

    private Button buildJoinButton(ChatRoom room, Div row) {
        Button btn = new Button(LocalizationProvider.localize("Join", ChatI18N.BROWSE_ROOMS_JOIN_BTN));
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        btn.addClassName("browse-rooms-dialog__join-btn");
        btn.addClickListener(e -> {
            try {
                chatService.joinRoom(localUserId, room.getId());
                Notification n = Notification.show(
                        LocalizationProvider.localize("Joined #{0}", ChatI18N.BROWSE_ROOMS_JOINED, room.getName()));
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                n.setDuration(2500);
                row.replace(btn, buildLeaveButton(room, row));
                if (onMembershipChanged != null) onMembershipChanged.accept(room.getId());
            } catch (Exception ex) {
                Notification.show(LocalizationProvider.localize(
                        "Could not join: {0}", ChatI18N.BROWSE_ROOMS_JOIN_ERROR, ex.getMessage()))
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        return btn;
    }

    private Button buildLeaveButton(ChatRoom room, Div row) {
        Button btn = new Button(LocalizationProvider.localize("Leave", ChatI18N.BROWSE_ROOMS_LEAVE_BTN));
        btn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        btn.addClassName("browse-rooms-dialog__leave-btn");
        btn.addClickListener(e -> {
            try {
                chatService.leaveRoom(localUserId, room.getId());
                Notification n = Notification.show(
                        LocalizationProvider.localize("Left #{0}", ChatI18N.BROWSE_ROOMS_LEFT, room.getName()));
                n.setDuration(2500);
                row.replace(btn, buildJoinButton(room, row));
                if (onMembershipChanged != null) onMembershipChanged.accept(room.getId());
            } catch (Exception ex) {
                Notification.show(LocalizationProvider.localize(
                        "Could not leave: {0}", ChatI18N.BROWSE_ROOMS_LEAVE_ERROR, ex.getMessage()))
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        return btn;
    }
}
